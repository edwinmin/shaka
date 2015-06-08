package com.edwin.shakacore.exception;

import lombok.Getter;

import org.quartz.JobExecutionException;

/**
 * @author jinming.wu
 * @date 2015-6-2
 */
public class ShakaJobExecuteException extends JobExecutionException {

    private static final long serialVersionUID = 1L;

    @Getter
    private int               code;
    
    public ShakaJobExecuteException(Throwable cause) {
        super(cause);
    }

    public ShakaJobExecuteException(int code, String msg) {
        super(msg);
        this.code = code;
    }

    public ShakaJobExecuteException(int code, String msg, Throwable cause) {
        super(msg, cause);
        this.code = code;
    }

    public ShakaJobExecuteException(int code, Throwable cause) {
        super(cause);
        this.code = code;
    }
}
