package com.edwin.shakazookeeper.listener;

/**
 * @author jinming.wu
 * @date 2015-6-5
 */
public interface ZKDataListener {

    /**
     * 数据变动监听
     * 
     * @param path
     * @param data
     * @throws Exception
     */
    public void dataChanage(String path, Object data) throws Exception;

    /**
     * 数据删除监听
     * 
     * @param path
     * @throws Exception
     */
    public void dataDelete(String path) throws Exception;

    /**
     * 子节点变动监听
     * 
     * @param path
     * @throws Exception
     */
    public void childrenChanage(String path) throws Exception;
}
