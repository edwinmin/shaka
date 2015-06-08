package com.edwin.shakacore.exception;

/**
 * @author jinming.wu
 * @date 2015-5-28
 */
public class ShakaException extends RuntimeException {

    private static final long serialVersionUID = 3346367716771817329L;

    public ShakaException(String message, Throwable cause) {
        super(message, cause);
    }

    public ShakaException(String message) {
        super(message);
    }

    public ShakaException(Throwable cause) {
        super(cause);
    }
}
