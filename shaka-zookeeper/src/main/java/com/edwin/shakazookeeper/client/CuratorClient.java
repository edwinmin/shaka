package com.edwin.shakazookeeper.client;

import java.util.concurrent.atomic.AtomicBoolean;

import lombok.Setter;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.api.SetDataBuilder;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edwin.shakazookeeper.Constants;
import com.edwin.shakazookeeper.exe.ExecuteException;
import com.edwin.shakazookeeper.listener.ZKDataListener;
import com.edwin.shakazookeeper.serializer.SerializableSerializer;
import com.edwin.shakazookeeper.serializer.ZkSerializer;

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

    @Setter
    private ZkSerializer           zkSerializer      = new SerializableSerializer();

    private CuratorListener        curatorListener   = new ShakaCuratorListener(this);

    private CuratorFramework       curatorClient;

    @Setter
    private int                    sessionTimeout    = Constants.DEFAULT_SESSION_TIMEOUT;

    @Setter
    private int                    connectionTimeout = Constants.DEFAULT_CONNECTION_TIMEOUT;

    public CuratorClient(String connectionString) {
        this.connectionString = connectionString;
    }

    @Override
    public void init() {

        if (!isConnected.get()) {
            synchronized (isConnected) {
                if (!isConnected.get()) {
                    retryPolicy = new ExponentialBackoffRetry(Constants.BASE_SLEEP_MS, Constants.MAX_TRY_TIMES);

                    curatorClient = CuratorFrameworkFactory.builder().connectString(connectionString).sessionTimeoutMs(sessionTimeout).connectionTimeoutMs(connectionTimeout).retryPolicy(retryPolicy).build();

                    curatorClient.getConnectionStateListenable().addListener(new ConnectionStateListener() {

                        @Override
                        public void stateChanged(CuratorFramework client, ConnectionState newState) {
                            if (newState == ConnectionState.CONNECTED || newState == ConnectionState.RECONNECTED) {
                                System.out.println(newState);
                                isConnected.set(true);
                            } else {
                                isConnected.set(false);
                                logger.error("Lost connection to zookeeper. ");
                            }
                        }
                    });

                    curatorClient.getCuratorListenable().addListener(curatorListener);

                    curatorClient.start();

                    isConnected.set(true);
                }
            }
        }
    }

    public void remove(final String path) throws Exception {
        execute(new Operation() {

            @Override
            public Object execute() throws Exception {
                curatorClient.delete().guaranteed().deletingChildrenIfNeeded().forPath(path);
                return null;
            }
        });
    }

    public String create(final String path, final byte[] data) throws Exception {

        return (String) execute(new Operation() {

            @Override
            public Object execute() throws Exception {
                if (data == null) {
                    return curatorClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
                }
                return curatorClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path,
                                                                                                                data);
            }
        });
    }

    @Override
    public String create(final String path, final Object data) throws Exception {

        byte[] bytes = null;
        if (data != null) {
            bytes = zkSerializer.serialize(data);
        }

        return this.create(path, bytes);
    }

    @Override
    public void update(final String path, final Stat stat, final Object data) throws Exception {

        byte[] bytes = null;
        if (data != null) {
            bytes = zkSerializer.serialize(data);
        }
        this.update(path, stat, bytes);
    }

    @Override
    public void update(final String path, final Stat stat, final byte[] data) throws Exception {

        execute(new Operation() {

            @Override
            public Object execute() throws Exception {
                SetDataBuilder setBuilder = curatorClient.setData();
                if (stat != null && data != null) {
                    setBuilder.withVersion(stat.getVersion()).forPath(path, data);
                } else if (stat != null && data == null) {
                    setBuilder.withVersion(stat.getVersion()).forPath(path);
                } else if (stat == null && data != null) {
                    setBuilder.forPath(path, data);
                } else {
                    setBuilder.forPath(path);
                }
                return null;
            }
        });
    }

    @Override
    public void watch(final String path, final CuratorWatcher watcher) throws Exception {

        execute(new Operation() {

            @Override
            public Object execute() throws Exception {
                curatorClient.checkExists().usingWatcher(watcher).forPath(path);
                return null;
            }
        });
    }

    @Override
    public byte[] getData(final String path, final boolean watched) throws Exception {

        return (byte[]) execute(new Operation() {

            @Override
            public Object execute() throws Exception {
                try {
                    if (watched) {
                        return curatorClient.getData().watched().forPath(path);
                    }

                    return curatorClient.getData().forPath(path);
                } catch (NoNodeException e) {
                    return null;
                }
            }
        });
    }

    @Override
    public boolean exists(final String path, final boolean watched) throws Exception {

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

    @Override
    public Object getObject(String path, boolean watched) throws Exception {

        byte[] bytes = this.getData(path, watched);
        if (bytes == null || bytes.length == 0) {
            return null;
        }

        return zkSerializer.deserialize(bytes);
    }

    private Object execute(Operation operation) throws Exception {

        // 失败后等待重连
        if (!isConnected.get()) {
            throw new ExecuteException("Lost connection to zookeeper. wait to auto reconnecting...");
        }

        Object result = null;
        try {
            result = operation.execute();
        } catch (Exception e) {
            throw e;
        }

        return result;
    }

    interface Operation {

        Object execute() throws Exception;
    }

    @Override
    public boolean isConnected() {
        return isConnected.get();
    }

    @Override
    public void destroy() {
        this.curatorClient.close();
    }

    @Override
    public void addListener(ZKDataListener dataListener) {
    }
}
