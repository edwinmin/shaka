package com.edwin.shakazookeeper.listener;

/**
 * @author jinming.wu
 * @date 2015-6-5
 */
public abstract class DataChanageListener implements ZKDataListener {

    @Override
    public void dataDelete(String path) throws Exception {

    }

    @Override
    public void childrenChanage(String path) throws Exception {

    }
}
