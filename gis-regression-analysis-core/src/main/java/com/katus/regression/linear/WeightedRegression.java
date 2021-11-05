package com.katus.regression.linear;

import com.katus.data.AbstractDataSet;
import com.katus.data.AbstractResultDataSet;
import com.katus.data.AbstractResultRecordWithInfo;
import com.katus.data.Record;
import com.katus.exception.DataException;
import com.katus.exception.InvalidParamException;
import com.katus.regression.weight.WeightCalculator;
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
                    int interval = predictDataSet.size() / numThread, start, end = 0;
                    for (int i = 0; i < numThread; i++) {
                        start = end;
                        if (i == numThread - 1) {
                            end = predictDataSet.size();
                        } else {
                            end += interval;
                        }
                        executorService.submit(this.new WeightedRegressionTrainer(start, end, weightCalculator));
                    }
                    waitingForFinish(executorService, "Training");
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
                    int interval = predictDataSet.size() / numThread, start, end = 0;
                    for (int i = 0; i < numThread; i++) {
                        start = end;
                        if (i == numThread - 1) {
                            end = predictDataSet.size();
                        } else {
                            end += interval;
                        }
                        executorService.submit(this.new WeightedRegressionPredictor(start, end));
                    }
                    waitingForFinish(executorService, "Predicting");
                    this.predicted = true;
                }
            }
        }
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
                INDArray weightMatrix = weightCalculator.calWeightMatrix(predictDataSet.getRecord(i).getBaseRecord());
                INDArray temp = trainingDataSet.xMatrixT().mmul(weightMatrix);
                INDArray temp1 = temp.mmul(trainingDataSet.xMatrix());
                for (int j = 0; j < temp1.shape()[0]; j++) {
                    temp1.putScalar(new int[]{j, j}, temp1.getDouble(j, j) + 0.000001);
                }
                INDArray p1 = InvertMatrix.invert(temp1, true);
                INDArray p2 = temp.mmul(trainingDataSet.yMatrix());
                INDArray betaMatrix = p1.mmul(p2);
                predictDataSet.setBetaMatrix(i, betaMatrix);
            }
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
                predictDataSet.getRecord(i).predict();
            }
        }
    }

    public static class WeightedRegressionBuilder<R extends Record, RR extends AbstractResultRecordWithInfo<R>> {
        private static final Logger logger = LoggerFactory.getLogger(WeightedRegressionBuilder.class);

        private AbstractDataSet<R> trainingDataSet;
        private AbstractResultDataSet<R, RR> predictDataSet;
        private WeightCalculator<R> weightCalculator;
        private int numThread = Runtime.getRuntime().availableProcessors() / 2 + 1;

        public WeightedRegression<R, RR> build() {
            if (predictDataSet == null || weightCalculator == null || numThread < 1) {
                logger.error("weighted regression params are invalid");
                throw new InvalidParamException();
            }
            if (trainingDataSet == null) {
                this.trainingDataSet = predictDataSet.convertToSourceDataSet();
            }
            return new WeightedRegression<>(trainingDataSet, predictDataSet, weightCalculator, numThread);
        }

        public WeightedRegressionBuilder<R, RR> trainingDataSet(AbstractDataSet<R> trainingDataSet) {
            this.trainingDataSet = trainingDataSet;
            return this;
        }

        public WeightedRegressionBuilder<R, RR> predictDataSet(AbstractResultDataSet<R, RR> predictDataSet) {
            this.predictDataSet = predictDataSet;
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
