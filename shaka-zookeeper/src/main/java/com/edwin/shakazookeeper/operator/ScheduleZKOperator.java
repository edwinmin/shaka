package com.edwin.shakazookeeper.operator;

import com.edwin.shakazookeeper.exe.ExeContext;

/**
 * 调度
 * 
 * @author jinming.wu
 * @date 2015-5-25
 */
public interface ScheduleZKOperator extends ZKOperator {

    /**
     * 执行任务
     * 
     * @param context
     * @throws Exception
     */
    public void executeTask(ExeContext context) throws Exception;

    /**
     * kill掉任务
     * 
     * @param context
     * @throws Exception
     */
    public void killTask(ExeContext context) throws Exception;
}
