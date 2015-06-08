package com.edwin.shakazookeeper.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author jinming.wu
 * @date 2015-6-4
 */
public class SerializableSerializer implements ZkSerializer {

    @Override
    public byte[] serialize(Object data) throws ZkSerialException {
        try {
            ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
            ObjectOutputStream stream = new ObjectOutputStream(byteArrayOS);
            stream.writeObject(data);
            stream.close();
            return byteArrayOS.toByteArray();
        } catch (IOException e) {
            throw new ZkSerialException(e);
        }
    }

    @Override
    public Object deserialize(byte[] bytes) throws ZkSerialException {
        try {
            ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
            Object object = inputStream.readObject();
            return object;
        } catch (ClassNotFoundException e) {
            throw new ZkSerialException("Unable to find object class.", e);
        } catch (IOException e) {
            throw new ZkSerialException(e);
        }
    }
}
