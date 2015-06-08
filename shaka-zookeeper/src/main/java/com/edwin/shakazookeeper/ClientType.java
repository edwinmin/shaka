package com.edwin.shakazookeeper;

import com.google.common.base.Strings;

/**
 * @author jinming.wu
 * @date 2015-5-24
 */
public enum ClientType {

    CURATOR,

    ZKCLIENT;

    public static ClientType getClientType(String type) {
        if (Strings.isNullOrEmpty(type)) {
            return CURATOR;
        }

        for (ClientType client : ClientType.values()) {
            if (client.toString().equalsIgnoreCase(type)) {
                return client;
            }
        }
        return CURATOR;
    }
}
