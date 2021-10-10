package com.katus.test.aic;

import com.katus.data.AbstractDataSet;
import com.katus.data.AbstractRecordWithCoefficient;
import com.katus.data.AbstractResultDataSet;
import com.katus.data.Record;
import com.katus.exception.DataException;
import com.katus.exception.InvalidParamException;
import com.katus.regression.linear.AbstractLinearRegression;
import com.katus.regression.linear.MultipleLinearRegression;
import com.katus.test.Test;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author SUN Katus
 * @version 1.0, 2021-10-09
 */
public class AIC<R extends Record> implements Test {
    private final AbstractDataSet<R> testDataSet;
    private final LinearRegressionBuilder<R> linearRegressionBuilder;
    private final double trainingRatio;
    private final double[] bandwidths;
    private final Map<Double, Double> resultMap;
    private volatile boolean test = false;

    private AIC(AbstractDataSet<R> testDataSet, LinearRegressionBuilder<R> linearRegressionBuilder, double trainingRatio, double[] bandwidths) {
        this.testDataSet = testDataSet;
        this.linearRegressionBuilder = linearRegressionBuilder;
        this.trainingRatio = trainingRatio;
        this.bandwidths = bandwidths;
        this.resultMap = new LinkedHashMap<>();
    }

    public Map<Double, Double> getAicResults() {
        test();
        return resultMap;
    }

    private double aic(AbstractResultDataSet<R> resultDataSet) {
        double squareSum = 0.0;
        for (int i = 0; i < resultDataSet.size(); i++) {
            AbstractRecordWithCoefficient<R> record = resultDataSet.getRecord(i);
            double predictValue = record.prediction();
            double trueValue = record.y();
            squareSum += Math.pow(predictValue - trueValue, 2);
        }
        int trainingSize = testDataSet.size() - resultDataSet.size();
        return Math.log(squareSum / trainingSize) + 2.0 * (resultDataSet.xSize() + 1) / trainingSize;
    }

    @Override
    public void test() {
        if (!test) {
            synchronized (this) {
                if (!test) {
                    AbstractDataSet<R> trainingDataSet, predictDataSet;
                    try {
                        trainingDataSet = testDataSet.clone();
                        predictDataSet = testDataSet.clone();
                    } catch (CloneNotSupportedException e) {
                        throw new DataException();
                    }
                    int moveSize = (int) (Math.min(trainingRatio, 1 - trainingRatio) * testDataSet.size());
                    Random random = new Random();
                    if (trainingRatio >= 0.5) {
                        predictDataSet.clear();
                        for (int i = 0; i < moveSize; i++) {
                            predictDataSet.addRecord(trainingDataSet.removeRecord(random.nextInt(trainingDataSet.size())));
                        }
                    } else {
                        trainingDataSet.clear();
                        for (int i = 0; i < moveSize; i++) {
                            trainingDataSet.addRecord(predictDataSet.removeRecord(random.nextInt(predictDataSet.size())));
                        }
                    }
                    for (double bandwidth : bandwidths) {
                        AbstractLinearRegression<R> regression = linearRegressionBuilder.build(trainingDataSet, predictDataSet.convertToResultDataSet(), bandwidth);
                        AbstractResultDataSet<R> resultDataSet = regression.getResultDataSet();
                        resultMap.put(bandwidth, aic(resultDataSet));
                        if (regression instanceof MultipleLinearRegression) {
                            break;
                        }
                    }
                    this.test = true;
                }
            }
        }
    }

    @Override
    public boolean pass() {
        test();
        return true;
    }

    public static class AICBuilder<R extends Record> {
        private AbstractDataSet<R> testDataSet;
        private LinearRegressionBuilder<R> linearRegressionBuilder;
        private double trainingRatio = 0.7;
        private double[] bandwidths = new double[0];

        public AIC<R> build() {
            if (!check()) {
                throw new InvalidParamException();
            }
            return new AIC<>(testDataSet, linearRegressionBuilder, trainingRatio, bandwidths);
        }

        public boolean check() {
            return testDataSet != null && linearRegressionBuilder != null && trainingRatio > 0 && trainingRatio < 1 && bandwidths.length > 0;
        }

        public AICBuilder<R> testDataSet(AbstractDataSet<R> testDataSet) {
            this.testDataSet = testDataSet;
            return this;
        }

        public AICBuilder<R> linearRegressionBuilder(LinearRegressionBuilder<R> linearRegressionBuilder) {
            this.linearRegressionBuilder = linearRegressionBuilder;
            return this;
        }

        public AICBuilder<R> trainingRatio(double trainingRatio) {
            this.trainingRatio = trainingRatio;
            return this;
        }

        public AICBuilder<R> bandwidths(double... bandwidths) {
            this.bandwidths = bandwidths;
            return this;
        }
    }
}
