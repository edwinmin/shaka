package com.edwin.shakacore.quartz;

import java.util.Date;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edwin.shakacore.ShakaCoreContext;
import com.edwin.shakacore.exception.ShakaJobExecuteException;
import com.edwin.shakacore.execute.ErrorCode;
import com.edwin.shakacore.execute.InstanceContext;
import com.edwin.shakacore.manager.JobManager;
import com.edwin.shakacore.manager.ShakaJobManager;
import com.edwin.shakacore.manager.ShakaTaskManager;
import com.edwin.shakacore.manager.TaskManager;
import com.edwin.shakapersist.entity.ShakaJob;
import com.edwin.shakapersist.entity.ShakaTask;

/**
 * 字节码操作覆盖该类
 * 
 * @author jinming.wu
 * @date 2015-5-29
 */
public class DefaultQuartzJob implements Job {

    private static Logger                logger = LoggerFactory.getLogger(DefaultQuartzJob.class);

    private JobManager                   jobManager;

    private TaskManager                  taskManager;

    private Map<Integer, QuartzShakaJob> qtzShakaJobMap;

    public DefaultQuartzJob() throws ClassNotFoundException {
        init();
    }

    public void init() throws ClassNotFoundException {

        qtzShakaJobMap = ShakaCoreContext.getInstance().getQtzShakaJobMap();

        jobManager = (JobManager) ShakaCoreContext.getInstance().getObjectByClazz(ShakaJobManager.class);

        taskManager = (TaskManager) ShakaCoreContext.getInstance().getObjectByClazz(ShakaTaskManager.class);
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        JobDetail jobDetail = context.getJobDetail();
        
        int jobId = 0;
        try {
            jobId = Integer.valueOf(jobDetail.getName().replaceAll("Job", ""));
        } catch (Exception e) {
            throw new ShakaJobExecuteException(ErrorCode.ERR_ID_FORMAT, "Job " + jobDetail.getName()
                                                                        + " is not not a standard format. ", e);
        }

        QuartzShakaJob qtzShakaJob = qtzShakaJobMap.get(jobId);
        if (qtzShakaJob == null) {
            throw new ShakaJobExecuteException(ErrorCode.ERR_NOT_FOUND, "Job " + jobId + " has not been found. ");
        }

        System.out.println(new Date() + "   " + jobId);

        ShakaJob job = qtzShakaJob.getShakaJob();

        ShakaTask task = taskManager.initTask(job, context);
        
        try {
            taskManager.execute(new InstanceContext(qtzShakaJob,task));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
