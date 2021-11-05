package com.katus.regression.linear;

import com.katus.data.AbstractDataSet;
import com.katus.data.AbstractResultDataSet;
import com.katus.data.AbstractResultRecordWithInfo;
import com.katus.data.Record;
import com.katus.regression.Regression;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author SUN Katus
 * @version 1.0, 2021-10-09
 */
public abstract class AbstractLinearRegression<R extends Record, RR extends AbstractResultRecordWithInfo<R>> implements Regression {
    protected final AbstractDataSet<R> trainingDataSet;
    protected final AbstractResultDataSet<R, RR> predictDataSet;

    protected AbstractLinearRegression(AbstractDataSet<R> trainingDataSet, AbstractResultDataSet<R, RR> predictDataSet) {
        this.trainingDataSet = trainingDataSet;
        this.predictDataSet = predictDataSet;
    }

    public AbstractResultDataSet<R, RR> getResultDataSet() {
        train();
        predict();
        return predictDataSet;
    }

    protected static void waitingForFinish(ExecutorService es, String doing) {
        es.shutdown();
        try {
            while (!es.awaitTermination(60, TimeUnit.SECONDS)) {
                System.out.println(doing + "...");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(doing + " is over!");
    }
}
