package com.edwin.shakazookeeper.listener;

/**
 * @author jinming.wu
 * @date 2015-6-5
 */
public abstract class ChildrenChanageListener implements ZKDataListener {

    @Override
    public void dataChanage(String path, Object data) throws Exception {
        // TODO Auto-generated method stub
    }

    @Override
    public void dataDelete(String path) throws Exception {
        // TODO Auto-generated method stub
    }
}
