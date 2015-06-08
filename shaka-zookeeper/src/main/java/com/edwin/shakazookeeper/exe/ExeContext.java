package com.edwin.shakazookeeper.exe;

import java.io.Serializable;
import java.util.Map;

import lombok.Data;

/**
 * zk执行期间上下文
 * 
 * @author jinming.wu
 * @date 2015-6-2
 */
@Data
public class ExeContext implements Serializable {

    private static final long   serialVersionUID = 1L;

    private String              instanceId;

    private String              taskId;

    private int                 jobId;

    private String              agentIP;

    private String              exeIdentity;

    private String              command;

    private int                 type;

    private Map<String, Object> pairs;

    private Integer             zkStatus         = ZKStatus.DEFAULT.code;
}
