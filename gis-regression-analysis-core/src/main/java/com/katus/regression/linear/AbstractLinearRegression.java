package com.katus.regression.linear;

import com.katus.data.AbstractDataSet;
import com.katus.data.AbstractResultDataSet;
import com.katus.data.AbstractResultRecordWithInfo;
import com.katus.data.Record;
import com.katus.regression.Regression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author SUN Katus
 * @version 1.0, 2021-10-09
 */
public abstract class AbstractLinearRegression<R extends Record, RR extends AbstractResultRecordWithInfo<R>> implements Regression {
    private static final Logger logger = LoggerFactory.getLogger(AbstractLinearRegression.class);

    protected final AbstractDataSet<R> trainingDataSet;
    protected final AbstractResultDataSet<R, RR> resultDataSet;

    protected AbstractLinearRegression(AbstractDataSet<R> trainingDataSet, AbstractResultDataSet<R, RR> resultDataSet) {
        this.trainingDataSet = trainingDataSet;
        this.resultDataSet = resultDataSet;
    }

    public AbstractDataSet<R> getTrainingDataSet() {
        return trainingDataSet;
    }

    public AbstractResultDataSet<R, RR> getResultDataSet() {
        predict();
        return resultDataSet;
    }
}
