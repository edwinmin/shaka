package com.edwin.shakapersist.dao;

import org.apache.ibatis.annotations.Param;

import com.edwin.shakapersist.entity.ShakaTask;

/**
 * Created by shichao.liao on 15/5/22.
 */
public interface ShakaTaskDao {

    public int addShakaTask(ShakaTask shakaTask);
    
    public int updateTask(ShakaTask shakaTask);

    public ShakaTask loadShakaTask(String instanceId);

    public ShakaTask loadLastShakaTask(@Param("jobId")
    int jobId);
}
