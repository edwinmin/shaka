package com.edwin.shakazookeeper.client;

import com.edwin.shakazookeeper.Environment;

/**
 * @author jinming.wu
 * @date 2015-5-21
 */
public interface ZKClientFactory {

    /**
     * 创建zk客户端
     * 
     * @return
     */
    public ZKClient createZKClient(Environment env);
}
