package com.edwin.shakacore.component.thread;

import java.util.concurrent.atomic.AtomicBoolean;

import com.edwin.shakacore.ShakaCoreContext;
import com.edwin.shakacore.manager.JobManager;
import com.edwin.shakacore.manager.ShakaJobManager;
import com.edwin.shakazookeeper.exe.DateWrapperHolder;

/**
 * @author jinming.wu
 * @date 2015-5-28
 */
public class RefreshWorker extends AbstractWorker {

    private JobManager        jobManager;

    private DateWrapperHolder dateWrapperHolder;

    private AtomicBoolean     isInterrupted;

    private ShakaCoreContext  shakaCoreContext;

    public RefreshWorker() {

        init();

        jobManager = (JobManager) shakaCoreContext.getObjectByClazz(ShakaJobManager.class);

        dateWrapperHolder = (DateWrapperHolder) shakaCoreContext.getObjectByClazz(DateWrapperHolder.class);
    }

    public RefreshWorker(JobManager jobManager) {

        init();

        this.jobManager = jobManager;
    }

    private void init() {
        isInterrupted = new AtomicBoolean(false);
        if (shakaCoreContext == null) {
            shakaCoreContext = ShakaCoreContext.getInstance();
        }
    }

    @Override
    public void run() {

        while (!isInterrupted.get()) {

            try {

                // sync data from db
                jobManager.refresh();

                // remove overdue data in generator
                dateWrapperHolder.removeOverdueData();

                Thread.sleep(60 * 1000);
            } catch (Exception e) {
                logger.error("Refresh thread is interrupted. ", e);
            }
        }
    }

    @Override
    public void shutdown() {
        isInterrupted.set(true);
    }

    @Override
    protected void setShakaCoreContext(ShakaCoreContext shakaCoreContext) {
        this.shakaCoreContext = shakaCoreContext;
    }
}
