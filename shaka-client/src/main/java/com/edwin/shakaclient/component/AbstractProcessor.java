package com.edwin.shakaclient.component;

import com.edwin.shakazookeeper.Constants;
import com.edwin.shakazookeeper.Environment;
import com.edwin.shakazookeeper.ShakaZKContext;
import com.edwin.shakazookeeper.client.ZKClient;
import com.edwin.shakazookeeper.client.ZKClientWareHouse;

/**
 * Created by shichao.liao on 15/6/8.
 */
public abstract class AbstractProcessor implements Processor {
    protected ZKClient zkClient;
    protected static final String      SHAKA  = "shaka";

    protected static ZKClientWareHouse zkClientWareHouse;

    static {
        zkClientWareHouse = new ZKClientWareHouse();
    }

    AbstractProcessor() throws Exception {

        if (zkClient == null) {
            Environment env = ShakaZKContext.getInstance().getEnv();
            zkClient = zkClientWareHouse.getZKClient(env);
        }
    }

    protected String getPath(String... nodes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nodes.length; i++) {
            sb.append(Constants.SEPARATOR).append(nodes[i]);
        }
        return sb.toString();
    }
}
