package com.edwin.shakazookeeper.operator;

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
     * 更新心跳
     * 
     * @param machineType
     * @param machineIP
     */
    public void updateHeartBeat(MachineType machineType, String machineIP) throws Exception;
}
