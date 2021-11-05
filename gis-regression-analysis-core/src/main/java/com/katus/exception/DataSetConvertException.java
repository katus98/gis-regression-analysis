package com.katus.exception;

/**
 * @author SUN Katus
 * @version 1.0, 2021-11-04
 */
public class DataSetConvertException extends BaseException {

    public DataSetConvertException() {
        this("DataSet Convert Error!");
    }

    public DataSetConvertException(String message) {
        super(message);
    }

    public DataSetConvertException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataSetConvertException(Throwable cause) {
        this("DataSet Convert Error!", cause);
    }
}
