package com.edwin.shakazookeeper.listener;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.WatchedEvent;

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

        lock.lock();
        try {
            condition.signal();
        } finally {
            lock.unlock();
        }
    }
}
