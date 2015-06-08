package com.edwin.shakacore.quartz;

import java.text.ParseException;

import org.quartz.CronExpression;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;

import com.edwin.shakacore.exception.ShakaException;
import com.edwin.shakapersist.entity.ShakaJob;

/**
 * @author jinming.wu
 * @date 2015-5-29
 */
public class QuartzShakaJobBuilder {

    // private static JavassistClassGenerator classGenerator;

    private QuartzShakaJob      quartzShakaJob;

    private static final String DEFAULT_CLASS = "com.edwin.shakacore.quartz.DefaultQuartzJob";

    static {
        // classGenerator = (JavassistClassGenerator)
        // ShakaCoreContext.getInstance().getObjectByClazz(JavassistClassGenerator.class);
    }

    private QuartzShakaJobBuilder(ShakaJob shakaJob) {
        quartzShakaJob = new QuartzShakaJob(shakaJob);
    }

    public static QuartzShakaJobBuilder newBuilder(ShakaJob shakaJob) {
        return new QuartzShakaJobBuilder(shakaJob);
    }

    public QuartzShakaJobBuilder withCronTrigger() throws ParseException {

        CronTrigger cronTrigger = new CronTrigger("Trigger" + quartzShakaJob.getShakaJob().getJobId(),
                                                  Scheduler.DEFAULT_GROUP); // "0/13 * * * * ?"
        CronExpression cronExpression = new CronExpression(quartzShakaJob.getShakaJob().getCorn());
        cronTrigger.setCronExpression(cronExpression);
        quartzShakaJob.setCronTrigger(cronTrigger);
        return this;
    }

    public QuartzShakaJobBuilder withJobDetail() {

        if (quartzShakaJob.getClazz() == null) {
            throw new ShakaException("Must invoke withJobClazz before withJobDetail. ");
        }
        JobDetail jobDetail = new JobDetail("Job" + quartzShakaJob.getShakaJob().getJobId(), Scheduler.DEFAULT_GROUP,
                                            quartzShakaJob.getClazz());
        quartzShakaJob.setJobDetail(jobDetail);
        return this;
    }

    public QuartzShakaJobBuilder withJobClazz(Class<?> clazz) throws Exception {
        // Class<Job> jobClazz = (Class<Job>) classGenerator.generateClass(quartzShakaJob.getShakaJob());
        if (clazz == null) {
            clazz = Class.forName(DEFAULT_CLASS);
        }
        quartzShakaJob.setClazz(clazz);
        return this;
    }

    public QuartzShakaJob build() {
        return quartzShakaJob;
    }
}
