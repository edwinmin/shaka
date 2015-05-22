package com.edwin.shakazookeeper;

import com.edwin.shakazookeeper.client.ZKClient;

/**
 * @author jinming.wu
 * @date 2015-5-22
 */
public interface ZKOperator {

    /**
     * 设置zkclient
     * 
     * @param zkClient
     */
    public void setZKClient(ZKClient zkClient);
}
