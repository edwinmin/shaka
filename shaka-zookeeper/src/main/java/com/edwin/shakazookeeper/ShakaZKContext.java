package com.edwin.shakazookeeper;

import java.util.Properties;

import com.edwin.shakautils.io.ResourceHelper;

/**
 * @author jinming.wu
 * @date 2015-5-25
 */
public class ShakaZKContext {

    private static ShakaZKContext instance       = new ShakaZKContext();

    private static String         ZK_CONFIG_PATH = "classpath:zookeeper.properties";

    private Properties            properties;

    private Environment           env;

    private ShakaZKContext() {

        if (this.env == null) {
            this.env = new Environment();
            try {
                this.properties = ResourceHelper.getProperties(ZK_CONFIG_PATH);
                if (properties == null) {
                    setDefaultValue();
                }
                this.env.setConnectionString(properties.getProperty("connectionString"));
                this.env.setSessionTimeOut(Integer.valueOf(properties.getProperty("50000")));
                this.env.setClientType(ClientType.getClientType(properties.getProperty("client")));
            } catch (Exception e) {
                setDefaultValue();
            }
        }
    }

    private void setDefaultValue() {
        env.setConnectionString(Constants.DEFAULT_CONNECTION);
        env.setSessionTimeOut(Constants.DEFAULT_SESSION_TIMEOUT);
        env.setClientType(ClientType.CURATOR);
    }

    public static ShakaZKContext getInstance() {
        return instance;
    }

    public Environment getEnv() {
        return env;
    }
}
