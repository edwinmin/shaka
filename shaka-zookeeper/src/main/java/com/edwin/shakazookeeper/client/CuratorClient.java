package com.edwin.shakazookeeper.client;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.Setter;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
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
import com.google.common.collect.Maps;

/**
 * @author jinming.wu
 * @date 2015-5-21
 */
public class CuratorClient implements ZKClient {

    private static final Logger                   logger            = LoggerFactory.getLogger(CuratorClient.class);

    private volatile AtomicBoolean                isConnected       = new AtomicBoolean(false);

    @Setter
    private String                                connectionString;

    @Setter
    private RetryPolicy                           retryPolicy;

    @Setter
    private ZkSerializer                          zkSerializer      = new SerializableSerializer();

    private CuratorFramework                      curatorClient;

    private ConcurrentMap<String, CuratorWatcher> watcherMap        = Maps.newConcurrentMap();

    @Setter
    private int                                   sessionTimeout    = Constants.DEFAULT_SESSION_TIMEOUT;

    @Setter
    private int                                   connectionTimeout = Constants.DEFAULT_CONNECTION_TIMEOUT;

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
                            if (newState == ConnectionState.CONNECTED) {
                                isConnected.set(true);
                            } else if (newState == ConnectionState.RECONNECTED) {
                                isConnected.set(true);

                                // 重连时确保watcher成功添加，这里就不处理内存数据的同步了
                                Map<String, CuratorWatcher> copyWatcherMap = Collections.unmodifiableMap(watcherMap);
                                for (Entry<String, CuratorWatcher> entry : copyWatcherMap.entrySet()) {
                                    try {
                                        curatorClient.checkExists().usingWatcher(entry.getValue()).forPath(entry.getKey());
                                    } catch (Exception e) {
                                        logger.error("Rewatch the path miss error, path " + entry.getKey(), e);
                                    }
                                }
                            } else {
                                isConnected.set(false);
                                logger.error("Lost connection to zookeeper. ");
                            }
                        }
                    });

                    curatorClient.start();

                    isConnected.compareAndSet(false, true);
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
    public String createTemp(final String path) throws Exception {
        return (String) execute(new Operation() {

            @Override
            public Object execute() throws Exception {
                return curatorClient.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path,
                                                                                                               new byte[0]);
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
                if (!watcherMap.containsKey(path)) {
                    watcherMap.put(path, watcher);
                }
                curatorClient.checkExists().usingWatcher(watcher).forPath(path);
                return null;
            }
        });
    }

    @Override
    public byte[] getData(final String path, final CuratorWatcher watcher) throws Exception {

        return (byte[]) execute(new Operation() {

            @Override
            public Object execute() throws Exception {
                try {
                    if (watcher != null) {
                        if (!watcherMap.containsKey(path)) {
                            watcherMap.put(path, watcher);
                        }
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
    public byte[] getData(final String path) throws Exception {

        return (byte[]) execute(new Operation() {

            @Override
            public Object execute() throws Exception {
                    return curatorClient.getData().forPath(path);
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> getChildren(final String path, final CuratorWatcher watcher) throws Exception {

        return (List<String>) execute(new Operation() {

            @Override
            public Object execute() throws Exception {
                try {
                    if (watcher != null) {
                        if (!watcherMap.containsKey(path)) {
                            watcherMap.put(path, watcher);
                        }
                        return curatorClient.getChildren().watched().forPath(path);
                    }

                    return curatorClient.getChildren().watched().forPath(path);
                } catch (NoNodeException e) {
                    return null;
                }
            }
        });
    }

    @Override
    public boolean exists(final String path, final CuratorWatcher watcher) throws Exception {

        return (Boolean) execute(new Operation() {

            @Override
            public Object execute() throws Exception {
                Stat stat = null;
                if (watcher != null) {
                    if (!watcherMap.containsKey(path)) {
                        watcherMap.put(path, watcher);
                    }
                    stat = curatorClient.checkExists().watched().forPath(path);
                } else {
                    stat = curatorClient.checkExists().forPath(path);
                }
                return stat != null;
            }
        });
    }

    @Override
    public boolean exists(final String path) throws Exception {
        return (Boolean) execute(new Operation() {

            @Override
            public Object execute() throws Exception {
                   return curatorClient.checkExists().forPath(path);
            }
        });
    }

    @Override
    public void removeWatcher(String path) {
        watcherMap.remove(path);
    }

    @Override
    public Object getObject(String path, final CuratorWatcher watcher) throws Exception {

        byte[] bytes = this.getData(path, watcher);
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
