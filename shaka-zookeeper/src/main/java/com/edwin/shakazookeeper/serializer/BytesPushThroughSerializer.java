package com.edwin.shakazookeeper.serializer;

/**
 * @author jinming.wu
 * @date 2015-6-4
 */
public class BytesPushThroughSerializer implements ZkSerializer {

    @Override
    public byte[] serialize(Object data) throws ZkSerialException {
        return (byte[]) data;
    }

    @Override
    public Object deserialize(byte[] bytes) throws ZkSerialException {
        return bytes;
    }
}
