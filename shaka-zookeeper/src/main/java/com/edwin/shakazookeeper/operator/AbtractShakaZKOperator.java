package com.edwin.shakazookeeper.operator;

import com.edwin.shakazookeeper.ZKOperator;
import com.edwin.shakazookeeper.client.ZKClient;

/**
 * @author jinming.wu
 * @date 2015-5-21
 */
public abstract class AbtractShakaZKOperator implements ZKOperator {

    protected static volatile boolean isInit = false;

    protected ZKClient                zkClient;

    @Override
    public void setZKClient(ZKClient zkClient) {
        this.zkClient = zkClient;
    }
}
