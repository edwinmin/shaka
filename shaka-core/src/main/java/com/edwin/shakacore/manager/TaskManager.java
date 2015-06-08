package com.edwin.shakacore.manager;

import org.quartz.JobExecutionContext;

import com.edwin.shakacore.execute.InstanceContext;
import com.edwin.shakapersist.entity.ShakaJob;
import com.edwin.shakapersist.entity.ShakaTask;

/**
 * @author jinming.wu
 * @date 2015-6-2
 */
public interface TaskManager {

    /**
     * 初始化生成一次任务
     * 
     * @param job
     * @return
     */
    public ShakaTask initTask(ShakaJob job, JobExecutionContext context);

    /**
     * 执行任务
     * 
     * @param context
     * @throws Exception
     */
    public void execute(InstanceContext context) throws Exception;
}
