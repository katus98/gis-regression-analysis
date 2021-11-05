package com.katus.test.r;

import com.katus.data.*;
import com.katus.exception.InvalidParamException;
import com.katus.regression.linear.WeightedRegression;
import com.katus.regression.weight.WeightCalculator;
import com.katus.test.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author SUN Katus
 * @version 1.0, 2021-10-10
 */
public class LocalRSquare<R extends Record, RR extends AbstractResultRecordWithInfo<R>> implements Test {
    private final WeightedRegression<R, RR> weightedRegression;
    private final AbstractDataSet<R> trainingDataSet;
    private final AbstractResultDataSet<R, RR> resultDataSet;
    private final WeightCalculator<R> weightCalculator;
    private volatile boolean test = false;

    private LocalRSquare(AbstractDataSet<R> trainingDataSet, AbstractResultDataSet<R, RR> resultDataSet, WeightCalculator<R> weightCalculator, int numThread) {
        WeightedRegression.WeightedRegressionBuilder<R, RR> builder = new WeightedRegression.WeightedRegressionBuilder<>();
        this.weightedRegression = builder
                .trainingDataSet(trainingDataSet)
                .predictDataSet(resultDataSet)
                .weightCalculator(weightCalculator)
                .numThread(numThread)
                .build();
        this.trainingDataSet = trainingDataSet;
        this.resultDataSet = resultDataSet;
        this.weightCalculator = weightCalculator;
    }

    @Override
    public void test() {
        if (!test) {
            synchronized (this) {
                if (!test) {
                    weightedRegression.predict();
                    double[] realYs = trainingDataSet.yArray();
                    double[] predictYs = new double[realYs.length];
                    for (int i = 0, j = 0; i < resultDataSet.size(); i++) {
                        AbstractResultRecordWithInfo<R> record = resultDataSet.getRecord(i);
                        if (record.y() != Constants.NO_DATA) {
                            predictYs[j++] = record.prediction();
                        }
                    }
                    for (int i = 0; i < resultDataSet.size(); i++) {
                        boolean[] booleans = weightCalculator.calBoolArray(resultDataSet.getRecord(i).getBaseRecord());
                        double sum = 0.0;
                        int count = 0;
                        for (int j = 0; j < booleans.length; j++) {
                            if (booleans[j]) {
                                sum += trainingDataSet.getRecord(j).y();
                                count++;
                            }
                        }
                        double avgLocalY = sum / count, predictRMS = 0.0, realRMS = 0.0;
                        for (double predictY : predictYs) {
                            predictRMS += Math.pow(predictY - avgLocalY, 2);
                        }
                        for (double realY : realYs) {
                            realRMS += Math.pow(realY - avgLocalY, 2);
                        }
                        double rSquare = predictRMS / realRMS;
                        resultDataSet.getRecord(i).setRSquare(rSquare);
                    }
                    this.test = true;
                }
            }
        }
    }

    @Override
    public boolean pass() {
        return test;
    }

    public static class LocalRSquareBuilder<R extends Record, RR extends AbstractResultRecordWithInfo<R>> {
        private static final Logger logger = LoggerFactory.getLogger(LocalRSquareBuilder.class);

        private AbstractDataSet<R> trainingDataSet;
        private AbstractResultDataSet<R, RR> predictDataSet;
        private WeightCalculator<R> weightCalculator;
        private int numThread = Runtime.getRuntime().availableProcessors() / 2 + 1;

        public LocalRSquare<R, RR> build() {
            if (predictDataSet == null || weightCalculator == null || numThread < 1) {
                logger.error("local r params are invalid");
                throw new InvalidParamException();
            }
            if (trainingDataSet == null) {
                this.trainingDataSet = predictDataSet.convertToSourceDataSet();
            }
            return new LocalRSquare<>(trainingDataSet, predictDataSet, weightCalculator, numThread);
        }

        public LocalRSquareBuilder<R, RR> trainingDataSet(AbstractDataSet<R> trainingDataSet) {
            this.trainingDataSet = trainingDataSet;
            return this;
        }

        public LocalRSquareBuilder<R, RR> predictDataSet(AbstractResultDataSet<R, RR> predictDataSet) {
            this.predictDataSet = predictDataSet;
            return this;
        }

        public LocalRSquareBuilder<R, RR> weightCalculator(WeightCalculator<R> weightCalculator) {
            this.weightCalculator = weightCalculator;
            return this;
        }

        public LocalRSquareBuilder<R, RR> numThread(int numThread) {
            this.numThread = numThread;
            return this;
        }
    }
}
