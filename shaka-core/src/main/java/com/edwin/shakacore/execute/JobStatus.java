package com.edwin.shakacore.execute;

/**
 * @author jinming.wu
 * @date 2015-5-27
 */
public enum JobStatus {

    SCHEDULING(0),

    SUSPEND(1),

    DELETED(2);

    public int status;

    JobStatus(int status) {
        this.status = status;
    }
}
