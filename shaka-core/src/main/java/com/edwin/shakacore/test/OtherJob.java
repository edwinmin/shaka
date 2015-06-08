package com.edwin.shakacore.test;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author jinming.wu
 * @date 2015-5-25
 */
public class OtherJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("OtherJob");
    }
}
