package com.edwin.shakazookeeper.listener;

/**
 * @author jinming.wu
 * @date 2015-6-5
 */
public abstract class DataDeleteListener implements ZKDataListener {

    @Override
    public void dataChanage(String path, Object data) throws Exception {

    }

    @Override
    public void childrenChanage(String path) throws Exception {

    }
}
