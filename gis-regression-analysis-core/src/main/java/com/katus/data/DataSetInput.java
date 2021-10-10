package com.katus.data;

import java.util.List;

/**
 * @author SUN Katus
 * @version 1.0, 2021-10-08
 */
@FunctionalInterface
public interface DataSetInput<R extends Record> {

    List<R> input();
}
