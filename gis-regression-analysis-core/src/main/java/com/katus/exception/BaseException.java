package com.katus.exception;

/**
 * @author SUN Katus
 * @version 1.0, 2021-09-30
 */
public abstract class BaseException extends Exception {

    public BaseException(String message) {
        super(message);
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseException(Throwable cause) {
        super(cause);
    }
}
