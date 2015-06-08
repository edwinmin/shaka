package com.edwin.shakacore.id;

/**
 * @author jinming.wu
 * @date 2015-5-25
 */
public interface IDGenerator {

    /**
     * 生成taskId
     * 
     * @param jobId
     * @return
     */
    public String generateTaskID(int jobId);

    /**
     * 生成instanceIds
     * 
     * @param taskId
     * @return
     */
    public String generateInstanceID(String taskId);

}
