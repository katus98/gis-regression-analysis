package com.katus.data;

import java.util.List;

/**
 * @author SUN Katus
 * @version 1.0, 2021-10-09
 */
@FunctionalInterface
public interface DataSetOutput<R extends Record> {

    void output(List<R> records);
}
