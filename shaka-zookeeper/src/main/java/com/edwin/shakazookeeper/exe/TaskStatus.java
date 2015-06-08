package com.edwin.shakazookeeper.exe;

/**
 * 任务执行状态（外部显示）
 * 
 * @author jinming.wu
 * @date 2015-5-28
 */
public enum TaskStatus {

    READY(0),

    RUNNING(1),

    SUCCESS(2),

    FAIL(3),

    TIMEOUT(4),

    KILLED(5),

    UP_FAIL(6);

    public int status;

    TaskStatus(int status) {
        this.status = status;
    }
}
