package com.edwin.shakazookeeper.client;

import java.util.concurrent.ConcurrentMap;

import com.edwin.shakazookeeper.Environment;
import com.google.common.collect.Maps;

/**
 * zk操作类对象仓库
 * 
 * @author jinming.wu
 * @date 2015-5-24
 */
public class ZKClientWareHouse {

    private static ConcurrentMap<Environment, ZKClient> operators = Maps.newConcurrentMap();

    private ZKClientFactory                             zkClientFactory;

    public ZKClient getZKClient(Environment env) throws Exception {

        ZKClient zkClient = operators.get(env);
        if (zkClient != null) {
            return zkClient;
        }
        switch (env.getClientType()) {
            case CURATOR:
                zkClientFactory = new CuratorClientFactory();
                break;
            default:
                break;
        }

        zkClient = zkClientFactory.createZKClient(env);

        automicPutInWareHouse(env, zkClient);

        return zkClient;
    }

    /**
     * 原子操作
     * 
     * @param env
     * @param zkClient
     * @return
     * @throws Exception
     */
    private ZKClient automicPutInWareHouse(Environment env, ZKClient zkClient) throws Exception {

        ZKClient oldZKClient = operators.putIfAbsent(env, zkClient);
        if (oldZKClient != null && oldZKClient != zkClient) {
            zkClient.destroy();
            return oldZKClient;
        }

        return zkClient;
    }
}
