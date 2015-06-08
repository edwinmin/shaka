package com.edwin.shakapersist.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.edwin.shakapersist.entity.ShakaHost;

/**
 * Created by shichao.liao on 15/5/22.
 */
public interface ShakaHostDao {

    public int addShakaHost(ShakaHost shakaHost);

    public ShakaHost loadShakaHost(int id);

    public List<ShakaHost> findHostsByGroup(@Param("groupId")
    int groupId);
}
