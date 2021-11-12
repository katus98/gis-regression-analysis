package com.katus.test.r;

import com.katus.data.*;
import com.katus.exception.DataException;
import com.katus.exception.InvalidParamException;
import com.katus.regression.linear.WeightedRegression;
import com.katus.regression.weight.WeightCalculator;
import com.katus.test.Test;
import com.katus.util.ExecutorManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author SUN Katus
 * @version 1.0, 2021-10-10
 */
public class LocalRSquare<R extends Record, RR extends AbstractResultRecordWithInfo<R>> implements Test {
    private static final Logger logger = LoggerFactory.getLogger(LocalRSquare.class);

    private final WeightedRegression<R, RR> weightedRegression;
    private final AbstractDataSet<R> trainingDataSet;
    private final AbstractResultDataSet<R, RR> resultDataSet;
    private final WeightCalculator<R> weightCalculator;
    private final int numThread;
    private volatile boolean test = false;

    private LocalRSquare(WeightedRegression<R, RR> weightedRegression, int numThread) {
        this.weightedRegression = weightedRegression;
        this.trainingDataSet = weightedRegression.getTrainingDataSet();
        this.resultDataSet = weightedRegression.getResultDataSet();
        this.weightCalculator = weightedRegression.getWeightCalculator();
        this.numThread = numThread;
    }

    @Override
    public void test() {
        if (!test) {
            synchronized (this) {
                if (!test) {
                    weightedRegression.predict();
                    double[] predictYs = new double[trainingDataSet.size()];
                    for (int i = 0, j = 0; i < resultDataSet.size(); i++) {
                        RR record = resultDataSet.getRecord(i);
                        if (trainingDataSet.isTrained(record.getBaseRecord())) {
                            predictYs[j++] = record.prediction();
                        }
                    }
                    ExecutorService executorService = Executors.newFixedThreadPool(numThread);
                    int interval = resultDataSet.size() / numThread, start, end = 0;
                    for (int i = 0; i < numThread; i++) {
                        start = end;
                        if (i == numThread - 1) {
                            end = resultDataSet.size();
                        } else {
                            end += interval;
                        }
                        executorService.submit(this.new LocalRSquareTester(start, end, weightCalculator, predictYs));
                    }
                    ExecutorManager.waitingForFinish(executorService, "Local R Square Testing");
                    this.test = true;
                }
            }
        }
    }

    @Override
    public boolean pass() {
        return test;
    }

    public class LocalRSquareTester implements Runnable {
        private final int start, end;
        private final WeightCalculator<R> weightCalculator;
        private final double[] predictYs;

        public LocalRSquareTester(int start, int end, WeightCalculator<R> weightCalculator, double[] predictYs) {
            this.start = start;
            this.end = end;
            try {
                this.weightCalculator = weightCalculator.clone();
            } catch (CloneNotSupportedException e) {
                logger.error("failed to clone weight calculator", e);
                throw new DataException();
            }
            this.predictYs = predictYs;
        }

        @Override
        public void run() {
            double[] realYs = trainingDataSet.yArray();
            for (int i = start; i < end; i++) {
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
                logger.trace("Testing Thread {}-{}: index {} is over", start, end, i);
            }
            logger.debug("Testing Thread {}-{} is over", start, end);
        }
    }

    public static class LocalRSquareBuilder<R extends Record, RR extends AbstractResultRecordWithInfo<R>> {
        private static final Logger logger = LoggerFactory.getLogger(LocalRSquareBuilder.class);

        private WeightedRegression<R, RR> weightedRegression;
        private int numThread = Runtime.getRuntime().availableProcessors() / 2 + 1;

        public LocalRSquare<R, RR> build() {
            if (weightedRegression == null || weightedRegression.getTrainingDataSet() == null || weightedRegression.getResultDataSet() == null
                    || weightedRegression.getWeightCalculator() == null || numThread < 1) {
                logger.error("local r params are invalid");
                throw new InvalidParamException();
            }
            return new LocalRSquare<>(weightedRegression, numThread);
        }

        public LocalRSquareBuilder<R, RR> weightedRegression(WeightedRegression<R, RR> weightedRegression) {
            this.weightedRegression = weightedRegression;
            return this;
        }

        public LocalRSquareBuilder<R, RR> numThread(int numThread) {
            this.numThread = numThread;
            return this;
        }
    }
}
