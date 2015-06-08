package com.edwin.shakacore.manager;

import lombok.Setter;

import org.joda.time.DateTime;
import org.quartz.JobExecutionContext;

import com.edwin.shakacore.ShakaCoreContext;
import com.edwin.shakacore.exception.ShakaJobExecuteException;
import com.edwin.shakacore.execute.ErrorCode;
import com.edwin.shakacore.execute.InstanceContext;
import com.edwin.shakacore.execute.policy.DefaultHostAssignPolicy;
import com.edwin.shakacore.execute.policy.HostAssignPolicy;
import com.edwin.shakacore.id.DefaultIDGenerator;
import com.edwin.shakacore.id.IDGenerator;
import com.edwin.shakacore.spring.RepositoryLocator;
import com.edwin.shakapersist.dao.ShakaTaskDao;
import com.edwin.shakapersist.entity.ShakaHost;
import com.edwin.shakapersist.entity.ShakaJob;
import com.edwin.shakapersist.entity.ShakaTask;
import com.edwin.shakazookeeper.MachineType;
import com.edwin.shakazookeeper.exe.TaskStatus;
import com.edwin.shakazookeeper.operator.ScheduleZKOperator;
import com.edwin.shakazookeeper.operator.ShakaScheduleZKOperator;

/**
 * @author jinming.wu
 * @date 2015-6-2
 */
public class ShakaTaskManager implements TaskManager {

    private static ShakaTaskDao shakaTaskDao = RepositoryLocator.getShakaTaskDao();

    // id生成器
    private IDGenerator         generator;

    @Setter
    private HostAssignPolicy    assignPolicy;

    // zk 操作器
    private ScheduleZKOperator  scheduleZKOperator;

    public ShakaTaskManager() throws Exception {

        scheduleZKOperator = (ScheduleZKOperator) ShakaCoreContext.getInstance().getObjectByClazz(ShakaScheduleZKOperator.class);

        assignPolicy = new DefaultHostAssignPolicy();

        generator = (IDGenerator) ShakaCoreContext.getInstance().getObjectByClazz(DefaultIDGenerator.class);
    }

    @Override
    public ShakaTask initTask(ShakaJob job, JobExecutionContext context) {

        int jobId = job.getJobId();
        String taskId = generator.generateTaskID(jobId);
        String instanceId = generator.generateInstanceID(taskId);
        ShakaTask task = new ShakaTask();
        task.setTaskId(taskId);
        task.setInstanceId(instanceId);
        task.setLogId(0);
        task.setJobId(jobId);
        task.setReturnCode(-2);
        task.setScheduleTime(context.getScheduledFireTime());
        task.setStatus(TaskStatus.READY.status);
        task.setTaskId(taskId);

        shakaTaskDao.addShakaTask(task);

        return task;
    }

    @Override
    public void execute(InstanceContext context) throws Exception {

        ShakaJob job = context.getQtzJob().getShakaJob();
        ShakaHost host = assignPolicy.assignHost(job);
        ShakaTask task = context.getTask();
        task.setExeHostIP(host.getIP());
        task.setStartTime(DateTime.now().toDate());

        try {
            scheduleZKOperator.executeTask(context.getExeContext());
        } catch (Exception e) {
            task.setStatus(TaskStatus.UP_FAIL.status);
            task.setEndTime(DateTime.now().toDate());
            shakaTaskDao.updateTask(task);
            throw new ShakaJobExecuteException(ErrorCode.ERR_ZK_EXE, "Instance " + task.getInstanceId()
                                                                     + " schedule error. ", e);
        }
        task.setStatus(TaskStatus.RUNNING.status);
        shakaTaskDao.updateTask(task);
    }
}
