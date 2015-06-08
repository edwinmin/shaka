package com.edwin.shakazookeeper.client;

import com.edwin.shakazookeeper.Environment;

/**
 * @author jinming.wu
 * @date 2015-5-22
 */
public class CuratorClientFactory implements ZKClientFactory {

    @Override
    public ZKClient createZKClient(Environment env) {

        CuratorClient curatorClient = new CuratorClient(env.getConnectionString());
        
        curatorClient.setSessionTimeout(env.getSessionTimeOut());

        curatorClient.init();

        return curatorClient;
    }
}
