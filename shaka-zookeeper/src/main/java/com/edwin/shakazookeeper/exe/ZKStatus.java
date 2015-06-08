package com.edwin.shakazookeeper.exe;

/**
 * @author jinming.wu
 * @date 2015-6-4
 */
public enum ZKStatus {

    DEFAULT(0),

    SCHEDULED(1),

    RUNNING(2),

    SUCCESS(3),

    FAIL(4),

    DELETED(5);

    public int code;

    ZKStatus(int code) {
        this.code = code;
    }
}
