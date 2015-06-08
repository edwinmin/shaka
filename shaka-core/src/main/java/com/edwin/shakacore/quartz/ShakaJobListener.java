package com.edwin.shakacore.quartz;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

/**
 * @author jinming.wu
 * @date 2015-6-1
 */
public class ShakaJobListener implements JobListener {

    private static final String DEFAULT_NAME = "ShakaJobListener";

    @Override
    public String getName() {
        return DEFAULT_NAME;
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        //System.out.println("jobExecutionVetoed");
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        //System.out.println(jobException.getMessage());
    }
}
