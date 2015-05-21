package com.edwin.shakazookeeper;

import com.edwin.shakazookeeper.client.ZKClient;

/**
 * zk操作器
 * 
 * @author jinming.wu
 * @date 2015-5-21
 */
public interface ZKOperator {

    /**
     * 设置zkClient
     * 
     * @param zkClient
     */
    public void setZKClient(ZKClient zkClient);

}
