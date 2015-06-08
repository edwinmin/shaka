package com.edwin.shakazookeeper.serializer;

/**
 * 序列化异常
 * 
 * @author jinming.wu
 * @date 2015-6-4
 */
public class ZkSerialException extends Exception {

    private static final long serialVersionUID = 1L;

    public ZkSerialException(Throwable cause) {
        super(cause);
    }

    public ZkSerialException(String message, Throwable cause) {
        super(message, cause);
    }
}
