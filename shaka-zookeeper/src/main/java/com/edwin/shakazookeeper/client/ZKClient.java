package com.edwin.shakazookeeper.client;

import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.data.Stat;

import com.edwin.shakazookeeper.listener.ZKDataListener;

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
     * 判断是否连接
     * 
     * @return
     */
    public boolean isConnected();

    /**
     * 销毁
     */
    public void destroy();

    /**
     * 添加watch
     * 
     * @param path
     * @throws Exception
     */
    public void watch(String path, CuratorWatcher watcher) throws Exception;

    /**
     * 节点创建
     * 
     * @param path
     * @param data
     * @return
     * @throws Exception
     */
    public String create(final String path, final byte[] data) throws Exception;

    /**
     * 节点创建
     * 
     * @param path
     * @param data
     * @return
     * @throws Exception
     */
    public String create(final String path, final Object data) throws Exception;

    /**
     * 删除节点
     * 
     * @param path
     * @throws Exception
     */
    public void remove(final String path) throws Exception;

    /**
     * 更新数据by object
     * 
     * @param path
     * @param stat
     * @param data
     * @throws Exception
     */
    public void update(final String path, final Stat stat, final Object data) throws Exception;

    /**
     * 更新数据
     * 
     * @param path
     */
    public void update(final String path, final Stat stat, byte[] data) throws Exception;

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
     * 获取对象
     * 
     * @param path
     * @param watched
     * @return
     * @throws Exception
     */
    public Object getObject(String path, boolean watched) throws Exception;

    /**
     * 判断是否存在
     * 
     * @param path
     * @param watched
     * @return
     * @throws Exception
     */
    public boolean exists(final String path, final boolean watched) throws Exception;

    /**
     * 添加监控listener（非动态）
     * 
     * @param dataListener
     */
    public void addListener(ZKDataListener dataListener);
}
