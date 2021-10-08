package com.katus.data;

/**
 * @author SUN Katus
 * @version 1.0, 2021-10-08
 */
public interface DataSetLoader<R extends Record> {

    R[] load();
}
