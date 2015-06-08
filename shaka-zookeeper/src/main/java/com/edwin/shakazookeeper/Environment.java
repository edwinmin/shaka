package com.edwin.shakazookeeper;

import lombok.Data;


/**
 * 环境类
 * 
 * @author jinming.wu
 * @date 2015-5-24
 */
@Data
public class Environment {

    private int        envId;

    private String     connectionString;

    private int        sessionTimeOut;

    private ClientType clientType;
}
