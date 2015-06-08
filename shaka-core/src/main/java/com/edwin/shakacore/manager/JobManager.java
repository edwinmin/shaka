package com.edwin.shakacore.manager;

import java.util.Map;

import com.edwin.shakapersist.entity.ShakaJob;

/**
 * 作业管理
 * 
 * @author jinming.wu
 * @date 2015-5-27
 */
public interface JobManager {

    /**
     * 获取要调度的作业
     * 
     * @return
     */
    public Map<Integer, ShakaJob> loadScheduleJobs();

    /**
     * 初始化作业
     */
    public void initJobs();

    /**
     * 添加作业
     */
    public void addJob(ShakaJob job) throws Exception;

    /**
     * 更新作业
     * 
     * @param job
     */
    public void updateJob(ShakaJob job) throws Exception;

    /**
     * 删除作业
     * 
     * @param job
     */
    public boolean removeJob(int jobId) throws Exception;

    /**
     * 手动运行（不通过quartz）
     * 
     * @param jobId
     * @throws Exception
     */
    public void manualRun(int jobId) throws Exception;

    /**
     * 暂停作业
     * 
     * @param jobId
     * @throws Exception
     */
    public void suspend(int jobId) throws Exception;

    /**
     * 恢复作业
     * 
     * @param jobId
     * @throws Exception
     */
    public void resume(int jobId) throws Exception;

    /**
     * 激动被删除的活动
     * 
     * @param jobId
     * @throws Exception
     */
    public void activate(int jobId) throws Exception;

    /**
     * 同步数据库中的job数据
     */
    // public void syncJobsFromDB();

    /**
     * 获取内存调度的作业
     * 
     * @return
     */
    // public Map<Integer, ShakaJob> synGetScheduleJobs();

    /**
     * 刷新内存中任务
     */
    public void refresh() throws Exception;
}
