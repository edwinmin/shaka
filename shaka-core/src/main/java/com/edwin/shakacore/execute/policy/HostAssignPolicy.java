package com.edwin.shakacore.execute.policy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edwin.shakacore.spring.RepositoryLocator;
import com.edwin.shakapersist.dao.ShakaHostDao;
import com.edwin.shakapersist.entity.ShakaHost;
import com.edwin.shakapersist.entity.ShakaJob;

/**
 * 执行机器分配策略
 * 
 * @author jinming.wu
 * @date 2015-6-2
 */
public abstract class HostAssignPolicy {

    protected final Logger        logger  = LoggerFactory.getLogger(getClass());

    protected static ShakaHostDao hostDao = RepositoryLocator.getShakaHostDao();

    /**
     * 分配机器
     * 
     * @return
     */
    public abstract ShakaHost assignHost(ShakaJob job);
}
