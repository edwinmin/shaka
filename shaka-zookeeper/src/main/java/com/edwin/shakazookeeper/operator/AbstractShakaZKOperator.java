package com.edwin.shakazookeeper.operator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edwin.shakazookeeper.Constants;
import com.edwin.shakazookeeper.Environment;
import com.edwin.shakazookeeper.ShakaZKContext;
import com.edwin.shakazookeeper.client.ZKClient;
import com.edwin.shakazookeeper.client.ZKClientWareHouse;

/**
 * @author jinming.wu
 * @date 2015-5-21
 */
public abstract class AbstractShakaZKOperator implements ZKOperator {

    protected final Logger             logger = LoggerFactory.getLogger(getClass());

    protected static final String      SHAKA  = "shaka";

    protected ZKClient                 zkClient;

    protected static ZKClientWareHouse zkClientWareHouse;

    static {
        zkClientWareHouse = new ZKClientWareHouse();
    }

    public AbstractShakaZKOperator() throws Exception {
        if (zkClient == null) {
            Environment env = ShakaZKContext.getInstance().getEnv();
            zkClient = zkClientWareHouse.getZKClient(env);
        }
    }

    public AbstractShakaZKOperator(ZKClient zkClient) {
        this.zkClient = zkClient;
    }

    @Override
    public void setZKClient(ZKClient zkClient) {
        this.zkClient = zkClient;
    }

    protected String getPath(String... nodes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nodes.length; i++) {
            sb.append(Constants.SEPARATOR).append(nodes[i]);
        }
        return sb.toString();
    }
}
