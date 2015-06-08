package com.edwin.shakacore.quartz;

import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import org.quartz.Trigger;

import com.edwin.shakacore.ShakaCoreContext;
import com.edwin.shakacore.ShakaScheduler;
import com.edwin.shakacore.component.thread.RefreshWorker;
import com.edwin.shakazookeeper.MachineType;
import com.edwin.shakazookeeper.operator.ScheduleZKOperator;
import com.edwin.shakazookeeper.operator.ShakaScheduleZKOperator;

/**
 * @author jinming.wu
 * @date 2015-6-1
 */
public class ShakaScheduleListener implements SchedulerListener {

    private ShakaScheduler     shakaScheduler;

    private ScheduleZKOperator scheduleZKOperator;

    public ShakaScheduleListener(ShakaScheduler shakaScheduler) {
        this.shakaScheduler = shakaScheduler;
        this.scheduleZKOperator = (ScheduleZKOperator) ShakaCoreContext.getInstance().getObjectByClazz(ShakaScheduleZKOperator.class);
    }

    @Override
    public void jobScheduled(Trigger trigger) {

    }

    @Override
    public void jobUnscheduled(String triggerName, String triggerGroup) {

    }

    @Override
    public void triggerFinalized(Trigger trigger) {

    }

    @Override
    public void triggersPaused(String triggerName, String triggerGroup) {

    }

    @Override
    public void triggersResumed(String triggerName, String triggerGroup) {

    }

    @Override
    public void jobAdded(JobDetail jobDetail) {

    }

    @Override
    public void jobDeleted(String jobName, String groupName) {

    }

    @Override
    public void jobsPaused(String jobName, String jobGroup) {
    }

    @Override
    public void jobsResumed(String jobName, String jobGroup) {

    }

    @Override
    public void schedulerError(String msg, SchedulerException cause) {
        System.out.println(msg);
        System.out.println(cause.getCause());
        System.out.println(cause.getErrorCode());
    }

    @Override
    public void schedulerInStandbyMode() {

    }

    @Override
    public void schedulerStarted() {

        // register server to zookeeper
        try {
            scheduleZKOperator.register(MachineType.SERVER, ShakaCoreContext.getInstance().getHostIP());
        } catch (NodeExistsException e) {
        } catch (Exception e) {
            shakaScheduler.shutdown();
        }

        // start workers
        shakaScheduler.getThreadManager().runWorker(new RefreshWorker());
    }

    @Override
    public void schedulerShutdown() {

    }

    @Override
    public void schedulerShuttingdown() {

    }

}
