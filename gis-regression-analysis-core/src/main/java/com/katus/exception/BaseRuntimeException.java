package com.katus.exception;

/**
 * @author SUN Katus
 * @version 1.0, 2021-09-30
 */
public abstract class BaseRuntimeException extends RuntimeException {

    public BaseRuntimeException(String message) {
        super(message);
    }

    public BaseRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseRuntimeException(Throwable cause) {
        super(cause);
    }
}
