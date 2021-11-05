package com.katus.regression.linear;

import com.katus.data.AbstractDataSet;
import com.katus.data.AbstractResultDataSet;
import com.katus.data.AbstractResultRecordWithInfo;
import com.katus.data.Record;
import com.katus.regression.Regression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author SUN Katus
 * @version 1.0, 2021-10-09
 */
public abstract class AbstractLinearRegression<R extends Record, RR extends AbstractResultRecordWithInfo<R>> implements Regression {
    private static final Logger logger = LoggerFactory.getLogger(AbstractLinearRegression.class);

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
                logger.info(doing + "...");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info(doing + " is over!");
    }
}
