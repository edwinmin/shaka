package com.edwin.shakacore;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.Getter;
import lombok.Setter;

import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.edwin.shakacore.component.thread.ShakaThreadManager;
import com.edwin.shakacore.exception.ShakaException;
import com.edwin.shakacore.manager.JobManager;
import com.edwin.shakacore.manager.ShakaJobManager;
import com.edwin.shakacore.quartz.ShakaJobListener;
import com.edwin.shakacore.quartz.ShakaScheduleListener;
import com.google.common.collect.Lists;

/**
 * 调度器
 * 
 * @author jinming.wu
 * @date 2015-5-25
 */
public class ShakaScheduler {

    private static final Logger     logger             = LoggerFactory.getLogger(ShakaScheduler.class);

    /** 调度器 */
    @Getter
    private Scheduler               scheduler;

    /** job执行监听器 */
    @Setter
    @Getter
    private List<JobListener>       jobListeners       = Lists.newCopyOnWriteArrayList();

    /** 调度监听器 */
    @Setter
    @Getter
    private List<SchedulerListener> schedulerListeners = Lists.newCopyOnWriteArrayList();

    /** 全局调度上下文 */
    @Getter
    private ShakaCoreContext        schduleContext;

    /** 作业管理 */
    @Setter
    @Getter
    private JobManager              jobManager;

    /** 线程管理 */
    @Setter
    @Getter
    private ShakaThreadManager      threadManager;

    private AtomicBoolean           isStart            = new AtomicBoolean(false);

    public ShakaScheduler() {
        schduleContext = ShakaCoreContext.getInstance();
    }

    public void init() {

        // init scheduler
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            schduleContext.put(ShakaCoreContext.SCHEDULER, scheduler);
        } catch (SchedulerException e) {
            throw new ShakaException("Get ShakaScheduler error. ", e);
        }

        // init jobManager
        if (jobManager == null) {
            jobManager = (JobManager) schduleContext.getObjectByClazz(ShakaJobManager.class);
        } else {
            schduleContext.putToMap(jobManager);
        }

        // init threadManager
        if (threadManager == null) {
            threadManager = (ShakaThreadManager) schduleContext.getObjectByClazz(ShakaThreadManager.class);
        } else {
            schduleContext.putToMap(threadManager);
        }
    }

    public void start() throws Exception {

        if (!isStart.get()) {
            synchronized (isStart) {
                if (!isStart.get()) {

                    // 初始化核心类
                    init();

                    // 初始化listeners
                    initListeners();

                    // 初始化可调度job到内存
                    jobManager.initJobs();
                    
                    // 启动quartz调度器
                    scheduler.start();

                    isStart.set(true);
                }
            }
        }
    }

    private void initListeners() throws SchedulerException {

        for (SchedulerListener schedulerListener : schedulerListeners) {
            scheduler.addSchedulerListener(schedulerListener);
        }

        for (JobListener jobListener : jobListeners) {
            scheduler.addGlobalJobListener(jobListener);
        }
    }

    public void addJobListener(JobListener jobListener) {
        jobListeners.add(jobListener);
    }

    public void addSchedulerListener(SchedulerListener schedulerListener) {
        schedulerListeners.add(schedulerListener);
    }

    public void shutdown() {

        try {
            scheduler.shutdown(true);
        } catch (Exception e) {
            logger.error("Shutdown scheduler exception. ", e);
        }

        threadManager.shutdown();

        isStart.set(false);
    }

    public static void main(String args[]) throws Exception {
        String[] path = new String[] { "classpath*:/config/spring/common/appcontext-*.xml",
                "classpath*:appcontext-*.xml", "classpath*:shakacontext-*" };

        ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext(path);
        ShakaScheduler shakaScheduler = new ShakaScheduler();
        shakaScheduler.addSchedulerListener(new ShakaScheduleListener(shakaScheduler));
        shakaScheduler.addJobListener(new ShakaJobListener());
        shakaScheduler.start();
    }
}
