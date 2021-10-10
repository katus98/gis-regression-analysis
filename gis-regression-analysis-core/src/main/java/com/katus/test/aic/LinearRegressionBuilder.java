package com.katus.test.aic;

import com.katus.data.AbstractDataSet;
import com.katus.data.AbstractResultDataSet;
import com.katus.data.Record;
import com.katus.regression.linear.AbstractLinearRegression;

/**
 * @author SUN Katus
 * @version 1.0, 2021-10-10
 */
public interface LinearRegressionBuilder<R extends Record> {

    AbstractLinearRegression<R> build(AbstractDataSet<R> trainingDataSet, AbstractResultDataSet<R> predictDataSet, double bandwidth);
}
