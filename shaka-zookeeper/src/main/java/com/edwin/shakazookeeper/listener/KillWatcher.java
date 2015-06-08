package com.edwin.shakazookeeper.listener;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event.EventType;

/**
 * @author jinming.wu
 * @date 2015-6-7
 */
public class KillWatcher implements CuratorWatcher {

    private Condition condition;

    private Lock      lock;

    public KillWatcher(Condition condition, Lock lock) {
        this.condition = condition;
        this.lock = lock;
    }

    @Override
    public void process(WatchedEvent event) throws Exception {

        // 只处理特点节点变化事件
        if (event.getType() == EventType.NodeDataChanged) {
            lock.lock();
            try {
                condition.signal();
            } finally {
                lock.unlock();
            }
        }
    }
}
