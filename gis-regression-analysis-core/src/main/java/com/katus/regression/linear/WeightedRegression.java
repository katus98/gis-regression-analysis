package com.katus.regression.linear;

import com.katus.data.*;
import com.katus.exception.DataException;
import com.katus.exception.InvalidParamException;
import com.katus.regression.weight.WeightCalculator;
import com.katus.util.ExecutorManager;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.inverse.InvertMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author SUN Katus
 * @version 1.0, 2021-10-08
 */
public class WeightedRegression<R extends Record, RR extends AbstractResultRecordWithInfo<R>> extends AbstractLinearRegression<R, RR> {
    private static final Logger logger = LoggerFactory.getLogger(WeightedRegression.class);

    protected final WeightCalculator<R> weightCalculator;
    protected final int numThread;
    private volatile boolean trained = false, predicted = false;

    protected WeightedRegression(AbstractDataSet<R> trainingDataSet, AbstractResultDataSet<R, RR> predictDataSet, WeightCalculator<R> weightCalculator, int numThread) {
        super(trainingDataSet, predictDataSet);
        this.weightCalculator = weightCalculator;
        this.numThread = Math.min(predictDataSet.size(), numThread);
    }

    @Override
    public void train() {
        if (!trained) {
            synchronized (this) {
                if (!trained) {
                    ExecutorService executorService = Executors.newFixedThreadPool(numThread);
                    int interval = resultDataSet.size() / numThread, start, end = 0;
                    for (int i = 0; i < numThread; i++) {
                        start = end;
                        if (i == numThread - 1) {
                            end = resultDataSet.size();
                        } else {
                            end += interval;
                        }
                        executorService.submit(this.new WeightedRegressionTrainer(start, end, weightCalculator));
                    }
                    ExecutorManager.waitingForFinish(executorService, "Training");
                    this.trained = true;
                }
            }
        }
    }

    @Override
    public void predict() {
        train();
        if (!predicted) {
            synchronized (this) {
                if (!predicted) {
                    ExecutorService executorService = Executors.newFixedThreadPool(numThread);
                    int interval = resultDataSet.size() / numThread, start, end = 0;
                    for (int i = 0; i < numThread; i++) {
                        start = end;
                        if (i == numThread - 1) {
                            end = resultDataSet.size();
                        } else {
                            end += interval;
                        }
                        executorService.submit(this.new WeightedRegressionPredictor(start, end));
                    }
                    ExecutorManager.waitingForFinish(executorService, "Predicting");
                    this.predicted = true;
                }
            }
        }
    }

    public WeightCalculator<R> getWeightCalculator() {
        return weightCalculator;
    }

    public class WeightedRegressionTrainer implements Runnable {
        private final int start, end;
        private final WeightCalculator<R> weightCalculator;

        public WeightedRegressionTrainer(int start, int end, WeightCalculator<R> weightCalculator) {
            this.start = start;
            this.end = end;
            try {
                this.weightCalculator = weightCalculator.clone();
            } catch (CloneNotSupportedException e) {
                logger.error("failed to clone weight calculator", e);
                throw new DataException();
            }
        }

        @Override
        public void run() {
            for (int i = start; i < end; i++) {
                INDArray weightMatrix = weightCalculator.calWeightMatrix(resultDataSet.getRecord(i).getBaseRecord());
                INDArray temp = trainingDataSet.xMatrixT().mmul(weightMatrix);
                INDArray temp1 = temp.mmul(trainingDataSet.xMatrix());
                for (int j = 0; j < temp1.shape()[0]; j++) {
                    temp1.putScalar(new int[]{j, j}, temp1.getDouble(j, j) + Constants.ALLOW_ERROR);
                }
                INDArray p1 = InvertMatrix.invert(temp1, true);
                INDArray p2 = temp.mmul(trainingDataSet.yMatrix());
                INDArray betaMatrix = p1.mmul(p2);
                resultDataSet.setBetaMatrix(i, betaMatrix);
                logger.trace("Training Thread {}-{}: index {} is over", start, end, i);
            }
            logger.debug("Training Thread {}-{} is over", start, end);
        }
    }

    public class WeightedRegressionPredictor implements Runnable {
        private final int start, end;

        public WeightedRegressionPredictor(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public void run() {
            for (int i = start; i < end; i++) {
                resultDataSet.getRecord(i).predict();
                logger.trace("Predicting Thread {}-{}: index {} is over", start, end, i);
            }
            logger.debug("Predicting Thread {}-{} is over", start, end);
        }
    }

    public static class WeightedRegressionBuilder<R extends Record, RR extends AbstractResultRecordWithInfo<R>> {
        private static final Logger logger = LoggerFactory.getLogger(WeightedRegressionBuilder.class);

        private AbstractResultDataSet<R, RR> resultDataSet;
        private WeightCalculator<R> weightCalculator;
        private int numThread = Runtime.getRuntime().availableProcessors() / 2 + 1;

        public WeightedRegression<R, RR> build() {
            AbstractDataSet<R> trainingDataSet = weightCalculator.getTrainingDataSet();
            if (resultDataSet == null || weightCalculator == null || numThread < 1 || trainingDataSet == null) {
                logger.error("weighted regression params are invalid");
                throw new InvalidParamException();
            }
            return new WeightedRegression<>(trainingDataSet, resultDataSet, weightCalculator, numThread);
        }

        public WeightedRegressionBuilder<R, RR> resultDataSet(AbstractResultDataSet<R, RR> predictDataSet) {
            this.resultDataSet = predictDataSet;
            return this;
        }

        public WeightedRegressionBuilder<R, RR> weightCalculator(WeightCalculator<R> weightCalculator) {
            this.weightCalculator = weightCalculator;
            return this;
        }

        public WeightedRegressionBuilder<R, RR> numThread(int numThread) {
            this.numThread = numThread;
            return this;
        }
    }
}
