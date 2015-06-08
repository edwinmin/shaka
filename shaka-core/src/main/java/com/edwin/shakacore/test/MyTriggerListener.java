package com.edwin.shakacore.test;

import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerListener;

/**
 * @author jinming.wu
 * @date 2015-5-25
 */
public class MyTriggerListener implements TriggerListener{

    @Override
    public String getName() {
        return "MyTriggerListener";
    }

    @Override
    public void triggerFired(Trigger trigger, JobExecutionContext context) {
        System.out.println("triggerFired");
    }

    @Override
    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
        System.out.println("vetoJobExecution");
        return false;
    }

    @Override
    public void triggerMisfired(Trigger trigger) {
        System.out.println("triggerMisfired");
    }

    @Override
    public void triggerComplete(Trigger trigger, JobExecutionContext context, int triggerInstructionCode) {
        System.out.println("triggerComplete");
    }

}
