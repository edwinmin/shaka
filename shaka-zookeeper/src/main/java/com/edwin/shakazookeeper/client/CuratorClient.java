package com.edwin.shakazookeeper.client;

import java.util.concurrent.atomic.AtomicBoolean;

import lombok.Setter;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edwin.shakazookeeper.Constants;

/**
 * @author jinming.wu
 * @date 2015-5-21
 */
public class CuratorClient implements ZKClient {

    private static final Logger    logger            = LoggerFactory.getLogger(CuratorClient.class);

    private volatile AtomicBoolean isConnected       = new AtomicBoolean(false);

    @Setter
    private String                 connectionString;

    @Setter
    private RetryPolicy            retryPolicy;

    private CuratorFramework       curatorClient;

    private static int             sessionTimeout    = Constants.DEFAULT_SESSION_TIMEOUT;

    private static int             connectionTimeout = Constants.DEFAULT_CONNECTION_TIMEOUT;

    public CuratorClient(String connectionString) {
        this.connectionString = connectionString;
    }

    @Override
    public void init() {

        retryPolicy = new ExponentialBackoffRetry(Constants.BASE_SLEEP_MS, Constants.MAX_TRY_TIMES);

        curatorClient = CuratorFrameworkFactory.builder().connectString(connectionString).sessionTimeoutMs(sessionTimeout).connectionTimeoutMs(connectionTimeout).retryPolicy(retryPolicy).build();

        curatorClient.getConnectionStateListenable().addListener(new ConnectionStateListener() {

            @Override
            public void stateChanged(CuratorFramework client, ConnectionState newState) {
                if (newState == ConnectionState.CONNECTED || newState == ConnectionState.RECONNECTED) {
                    isConnected.set(true);
                } else {
                    isConnected.set(false);
                    logger.error("Lost connection to zookeeper. ");
                }
            }
        });

        curatorClient.getCuratorListenable().addListener(new ShakaCuratorListener());

        curatorClient.start();
    }

    @Override
    public void watch(final String path) throws Exception {

        execute(new Operation() {

            @Override
            public Object execute() throws Exception {
                curatorClient.checkExists().watched().forPath(path);
                return null;
            }
        });
    }

    @Override
    public byte[] getData(final String path, final boolean watched) throws Exception {

        return (byte[]) execute(new Operation() {

            @Override
            public Object execute() throws Exception {
                if (watched) {
                    return curatorClient.getData().watched().forPath(path);
                }

                return curatorClient.getData().forPath(path);
            }
        });
    }

    @Override
    public boolean exists(final String path, final boolean watched) {

        return (Boolean) execute(new Operation() {

            @Override
            public Object execute() throws Exception {
                Stat stat = null;
                if (watched) {
                    stat = curatorClient.checkExists().watched().forPath(path);
                } else {
                    stat = curatorClient.checkExists().forPath(path);
                }
                return stat != null;
            }
        });
    }

    private Object execute(Operation operation) {

        // 失败后等待重连
        if (!isConnected.get()) {
            logger.error("Lost connection to zookeeper. wait to auto reconnecting...");
            return null;
        }

        Object result = null;
        try {
            result = operation.execute();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return result;
    }

    interface Operation {

        Object execute() throws Exception;
    }
}
