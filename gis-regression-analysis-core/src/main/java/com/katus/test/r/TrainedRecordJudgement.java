package com.katus.test.r;

import com.katus.data.Record;

/**
 * @author SUN Katus
 * @version 1.0, 2021-11-06
 */
@FunctionalInterface
public interface TrainedRecordJudgement<R extends Record> {

    boolean isTrained(R record);
}
