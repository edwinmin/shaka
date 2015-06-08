package com.edwin.shakacore.component.thread;

import java.util.List;
import java.util.concurrent.ThreadFactory;

import com.google.common.collect.Lists;

/**
 * 线程管理器
 * 
 * @author jinming.wu
 * @date 2015-5-28
 */
public class ShakaThreadManager {

    private List<AbstractWorker> workers;

    private static ThreadFactory threadFactory;

    static {
        threadFactory = new ShakaThreadFactory();
    }

    public ShakaThreadManager() {
        workers = Lists.newCopyOnWriteArrayList();
    }

    public void runWorker(AbstractWorker worker) {

        workers.add(worker);

        Thread thread = threadFactory.newThread(worker);
        thread.start();
    }

    public void shutdown(AbstractWorker worker) {
        workers.remove(worker);
        worker.shutdown();
    }

    public void shutdown() {

        for (AbstractWorker worker : workers) {
            worker.shutdown();
        }

        workers.clear();
    }
}
