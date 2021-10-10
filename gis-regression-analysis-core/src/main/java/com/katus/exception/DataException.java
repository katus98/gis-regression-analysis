package com.katus.exception;

/**
 * @author SUN Katus
 * @version 1.0, 2021-10-10
 */
public class DataException extends BaseRuntimeException {

    public DataException() {
        this("Data Error!");
    }

    public DataException(String message) {
        super(message);
    }
}
