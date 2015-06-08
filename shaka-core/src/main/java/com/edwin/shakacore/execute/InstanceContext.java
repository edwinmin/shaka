package com.edwin.shakacore.execute;

import java.util.HashMap;

import lombok.Data;

import com.edwin.shakacore.quartz.QuartzShakaJob;
import com.edwin.shakapersist.entity.ShakaTask;
import com.edwin.shakazookeeper.exe.ExeContext;

/**
 * @author jinming.wu
 * @date 2015-6-2
 */
@Data
public class InstanceContext {

    private QuartzShakaJob qtzJob;

    private ShakaTask      task;

    public InstanceContext() {
    }

    public InstanceContext(QuartzShakaJob qtzJob, ShakaTask task) {
        this.qtzJob = qtzJob;
        this.task = task;
    }

    public ExeContext getExeContext() {
        ExeContext context = new ExeContext();
        context.setAgentIP(task.getExeHostIP());
        context.setCommand(qtzJob.getShakaJob().getCommand());
        context.setExeIdentity(qtzJob.getShakaJob().getExeIdentity());
        context.setInstanceId(task.getInstanceId());
        context.setTaskId(task.getTaskId());
        context.setJobId(qtzJob.getShakaJob().getJobId());
        context.setType(qtzJob.getShakaJob().getType());
        context.setPairs(new HashMap<String, Object>());
        return context;
    }
}
