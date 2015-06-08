package com.edwin.shakazookeeper.serializer;

/**
 * @author jinming.wu
 * @date 2015-6-4
 */
public interface ZkSerializer {

    /**
     * 序列化
     * 
     * @param data
     * @return
     * @throws ZkSerialException
     */
    public byte[] serialize(Object data) throws ZkSerialException;

    /**
     * 反序列化
     * 
     * @param bytes
     * @return
     * @throws ZkSerialException
     */
    public Object deserialize(byte[] bytes) throws ZkSerialException;
}
