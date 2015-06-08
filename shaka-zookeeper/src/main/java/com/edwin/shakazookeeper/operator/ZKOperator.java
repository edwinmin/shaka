package com.edwin.shakazookeeper.operator;

import java.util.List;

import org.apache.curator.framework.api.CuratorWatcher;

import com.edwin.shakazookeeper.MachineType;
import com.edwin.shakazookeeper.client.ZKClient;

/**
 * @author jinming.wu
 * @date 2015-5-22
 */
public interface ZKOperator {

    /**
     * 机器注册
     * 
     * @param machineType
     * @param machineIP
     */
    public void register(MachineType machineType, String machineIP) throws Exception;

    /**
     * 设置zkclient
     * 
     * @param zkClient
     */
    public void setZKClient(ZKClient zkClient);

    /**
     * 获取心跳节点
     * 
     * @param machineType
     */
    public List<String> getChildHBNodes(MachineType machineType, CuratorWatcher watcher) throws Exception;
}
