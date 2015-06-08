package com.edwin.shakacore;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author jinming.wu
 * @date 2015-5-25
 */
public class QuartzJob implements Job {

    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
       // System.out.println(arg0.getJobDetail().getJobListenerNames()[0]);
        System.out.println(arg0.getJobDetail().getKey());
    }
}
