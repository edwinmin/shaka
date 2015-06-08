package com.edwin.shakapersist.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.edwin.shakapersist.entity.ShakaJob;

/**
 * Created by shichao.liao on 15/5/21.
 */
public interface ShakaJobDao {

    public int addShakaJob(ShakaJob jobConfig);

    public void updateShakaJob(ShakaJob job);

    public void updateJobStatus(@Param("jobId")
    int jobId, @Param("status")
    int status);

    public ShakaJob loadShakaJob(int jobId);

    public List<ShakaJob> findShakaJobsByStatus(@Param("statusList")
    List<Integer> statusList, @Param("hostIP")
    String hostIP);
}
