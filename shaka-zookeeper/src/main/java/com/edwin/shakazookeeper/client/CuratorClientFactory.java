package com.edwin.shakazookeeper.client;

/**
 * @author jinming.wu
 * @date 2015-5-22
 */
public class CuratorClientFactory implements ZKClientFactory {

    @Override
    public ZKClient createZKClient(String connectionString) {

        return new CuratorClient(connectionString);
    }
}
