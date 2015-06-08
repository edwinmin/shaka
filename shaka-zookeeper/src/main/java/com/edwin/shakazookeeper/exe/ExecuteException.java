package com.edwin.shakazookeeper.exe;

/**
 * @author jinming.wu
 * @date 2015-6-4
 */
public class ExecuteException extends RuntimeException {

    private static final long serialVersionUID = -1109902211815368609L;

    public ExecuteException(Throwable cause) {
        super(cause);
    }

    public ExecuteException(String message) {
        super(message);
    }

    public ExecuteException(String message, Throwable cause) {
        super(message, cause);
    }
}
