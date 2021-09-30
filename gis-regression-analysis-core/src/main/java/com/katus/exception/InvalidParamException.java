package com.katus.exception;

/**
 * @author SUN Katus
 * @version 1.0, 2021-09-30
 */
public class InvalidParamException extends BaseRuntimeException {

    public InvalidParamException() {
        this("Parameter is invalid!");
    }

    public InvalidParamException(String message) {
        super(message);
    }
}
