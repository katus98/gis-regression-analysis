package com.katus.regression.linear;

import com.katus.data.AbstractDataSet;
import com.katus.data.AbstractResultDataSet;
import com.katus.data.Record;
import com.katus.exception.InvalidParamException;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.inverse.InvertMatrix;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author SUN Katus
 * @version 1.0, 2021-09-30
 */
public class MultipleLinearRegression<R extends Record> extends AbstractLinearRegression<R> {
    protected final int numThread;
    private volatile INDArray betaMatrix;
    private volatile boolean predicted = false;

    protected MultipleLinearRegression(AbstractDataSet<R> trainingDataSet, AbstractResultDataSet<R> predictDataSet, int numThread) {
        super(trainingDataSet, predictDataSet);
        this.numThread = Math.min(predictDataSet.size(), numThread);
    }

    @Override
    public void train() {
        if (betaMatrix == null) {
            synchronized (this) {
                if (betaMatrix == null) {
                    INDArray p1 = InvertMatrix.invert(trainingDataSet.xMatrixT().mmul(trainingDataSet.xMatrix()), true);
                    INDArray p2 = trainingDataSet.xMatrixT().mmul(trainingDataSet.yMatrix());
                    this.betaMatrix = p1.mmul(p2);
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
                    double[] beta = new double[trainingDataSet.xSize() + 1];
                    for (int i = 0; i < beta.length; i++) {
                        beta[i] = betaMatrix.getDouble(i, 0);
                    }
                    ExecutorService executorService = Executors.newFixedThreadPool(numThread);
                    int interval = predictDataSet.size() / numThread, start, end = 0;
                    for (int i = 0; i < numThread; i++) {
                        start = end;
                        if (i == numThread - 1) {
                            end = predictDataSet.size();
                        } else {
                            end += interval;
                        }
                        executorService.submit(this.new MultipleLinearRegressionPredictor(start, end, beta));
                    }
                    waitingForFinish(executorService, "Predicting");
                    this.predicted = true;
                }
            }
        }
    }

    public class MultipleLinearRegressionPredictor implements Runnable {
        private final int start, end;
        private final double[] beta;

        public MultipleLinearRegressionPredictor(int start, int end, double[] beta) {
            this.start = start;
            this.end = end;
            this.beta = beta;
        }

        @Override
        public void run() {
            for (int i = start; i < end; i++) {
                predictDataSet.getRecord(i).predict(beta);
            }
        }
    }

    public static class MultipleLinearRegressionBuilder<R extends Record> {
        private AbstractDataSet<R> trainingDataSet;
        private AbstractResultDataSet<R> predictDataSet;
        private int numThread = 1;

        public MultipleLinearRegression<R> build() {
            if (predictDataSet == null) {
                throw new InvalidParamException();
            }
            if (trainingDataSet == null) {
                this.trainingDataSet = predictDataSet.convertToSourceDataSet();
            }
            return new MultipleLinearRegression<>(trainingDataSet, predictDataSet, numThread);
        }

        public MultipleLinearRegressionBuilder<R> trainingDataSet(AbstractDataSet<R> trainingDataSet) {
            this.trainingDataSet = trainingDataSet;
            return this;
        }

        public MultipleLinearRegressionBuilder<R> predictDataSet(AbstractResultDataSet<R> predictDataSet) {
            this.predictDataSet = predictDataSet;
            return this;
        }

        public MultipleLinearRegressionBuilder<R> numThread(int numThread) {
            this.numThread = numThread;
            return this;
        }
    }
}
