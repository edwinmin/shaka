package com.edwin.shakacore.quartz;

import lombok.Data;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;

import com.edwin.shakapersist.entity.ShakaJob;

/**
 * @author jinming.wu
 * @date 2015-5-29
 */
@Data
public class QuartzShakaJob {

    private JobDetail   jobDetail;

    private CronTrigger cronTrigger;

    private ShakaJob    shakaJob;

    private Class<?>    clazz;

    public QuartzShakaJob(ShakaJob job) {
        this.shakaJob = job;
    }
}
