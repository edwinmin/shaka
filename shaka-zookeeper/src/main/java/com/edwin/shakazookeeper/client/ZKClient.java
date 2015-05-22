package com.edwin.shakazookeeper.client;

/**
 * 链接zk的客户端
 * 
 * @author jinming.wu
 * @date 2015-5-21
 */
public interface ZKClient {

    /**
     * 初始化
     */
    public void init() throws Exception;

    /**
     * 添加watch
     * 
     * @param path
     * @throws Exception
     */
    public void watch(final String path) throws Exception;

    /**
     * 获取数据
     * 
     * @param path
     * @param watched
     * @return
     * @throws Exception
     */
    public byte[] getData(final String path, final boolean watched) throws Exception;

    /**
     * 判断是否存在
     * 
     * @param path
     * @param watched
     * @return
     * @throws Exception
     */
    public boolean exists(final String path, final boolean watched) throws Exception;

}
