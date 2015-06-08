package com.edwin.shakacore.component.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jinming.wu
 * @date 2015-5-30
 */
public class ShakaThreadFactory implements ThreadFactory {

    static final AtomicInteger poolNumber = new AtomicInteger(1);
    final AtomicInteger        threadNumber;
    final ThreadGroup          group;
    final String               namePrefix;
    final boolean              isDaemon;

    public ShakaThreadFactory() {
        this("Shaka");
    }

    public ShakaThreadFactory(String name) {
        this(name, true);
    }

    public ShakaThreadFactory(String preffix, boolean daemon) {
        this.threadNumber = new AtomicInteger(1);
        int identity = poolNumber.getAndIncrement();
        this.group = new ThreadGroup(preffix + "-" + identity + "-threadGroup");
        this.namePrefix = preffix + "-" + identity + "-thread-";
        this.isDaemon = daemon;
    }

    @Override
    public Thread newThread(Runnable worker) {

        Thread t = new Thread(this.group, worker, this.namePrefix + worker.getClass().getSimpleName()
                                                  + threadNumber.getAndDecrement(), 0);

        t.setDaemon(this.isDaemon);
        if (t.getPriority() != 5) {
            t.setPriority(5);
        }

        return t;
    }

    public ThreadGroup getGroup() {
        return group;
    }
}
