package com.edwin.shakacore;

import java.text.ParseException;

import org.quartz.CronExpression;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SchedulerListener;
import org.quartz.Trigger;
import org.quartz.TriggerListener;
import org.quartz.impl.StdSchedulerFactory;

import com.edwin.shakacore.test.MyTriggerListener;
import com.edwin.shakacore.test.OtherJob;

/**
 * @author jinming.wu
 * @date 2015-5-25
 */
public class QuartzTest {

    public static void main(String args[]) throws ParseException {

        SchedulerFactory schedulerfactory = new StdSchedulerFactory();

        QuartzTest test = new QuartzTest();

        JobListener jobListener = test.new MyJobListener();

        SchedulerListener schedulerListener = test.new MySchedulerListener();

        TriggerListener triggerListener = new MyTriggerListener();

        Scheduler scheduler = null;
        try {

            scheduler = schedulerfactory.getScheduler();
            JobDetail jobDetail = new JobDetail("job1", "group1", QuartzJob.class);
            CronTrigger cornTrigger = new CronTrigger("trigger1", "group1"); // "0/13 * * * * ?"
            cornTrigger.setCronExpression("0/5 * * * * ?");
            
            scheduler.scheduleJob(jobDetail, cornTrigger);
            
            scheduler.start();
            
            JobDetail jobDetail2 = new JobDetail("job2", "group1", OtherJob.class);
            CronTrigger cornTrigger2 = new CronTrigger("trigger2", "group"); // "0/13 * * * * ?"
            cornTrigger2.setCronExpression("0/10 * * * * ?");
            
            scheduler.scheduleJob(jobDetail2, cornTrigger2);
            // scheduler.shutdown();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    class MySchedulerListener implements SchedulerListener {

        @Override
        public void jobScheduled(Trigger trigger) {
            System.out.println("jobScheduled");
        }

        @Override
        public void jobUnscheduled(String triggerName, String triggerGroup) {
            System.out.println("jobUnscheduled");
        }

        @Override
        public void triggerFinalized(Trigger trigger) {
            System.out.println("triggerFinalized");
        }

        @Override
        public void triggersPaused(String triggerName, String triggerGroup) {
            System.out.println("triggersPaused");
        }

        @Override
        public void triggersResumed(String triggerName, String triggerGroup) {
            System.out.println("triggersResumed");
        }

        @Override
        public void jobAdded(JobDetail jobDetail) {
            System.out.println("jobAdded");
        }

        @Override
        public void jobDeleted(String jobName, String groupName) {
            System.out.println("jobDeleted");
        }

        @Override
        public void jobsPaused(String jobName, String jobGroup) {
            System.out.println("jobsPaused");
        }

        @Override
        public void jobsResumed(String jobName, String jobGroup) {
            System.out.println("jobsResumed");
        }

        @Override
        public void schedulerError(String msg, SchedulerException cause) {
            System.out.println("schedulerError");
        }

        @Override
        public void schedulerInStandbyMode() {
            System.out.println("schedulerInStandbyMode");
        }

        @Override
        public void schedulerStarted() {
            System.out.println("schedulerStarted");
        }

        @Override
        public void schedulerShutdown() {
            System.out.println("schedulerShutdown");
        }

        @Override
        public void schedulerShuttingdown() {
            System.out.println("schedulerShuttingdown");
        }

    }

    class MyJobListener implements JobListener {

        @Override
        public String getName() {
            return "MyJobListener";
        }

        @Override
        public void jobToBeExecuted(JobExecutionContext context) {
            System.out.println("jobToBeExecuted");
        }

        @Override
        public void jobExecutionVetoed(JobExecutionContext context) {
            System.out.println("jobExecutionVetoed");
        }

        @Override
        public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
            System.out.println("jobWasExecuted");
        }
    }
}
