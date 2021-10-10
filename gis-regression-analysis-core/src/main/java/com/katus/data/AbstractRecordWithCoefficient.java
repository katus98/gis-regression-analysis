package com.katus.data;

import com.katus.exception.InvalidParamException;

/**
 * @author SUN Katus
 * @version 1.0, 2021-10-09
 */
public abstract class AbstractRecordWithCoefficient<R extends Record> implements Record, Coefficient, Prediction {
    protected R record;
    protected double[] beta;
    protected double prediction;

    public void predict() {
        double pre = beta(0);
        for (int i = 0; i < xSize(); i++) {
            pre += beta(i+1) * x(i);
        }
        this.prediction = pre;
    }

    public void predict(double[] beta) {
        setBeta(beta);
        predict();
    }

    @Override
    public double[] beta() {
        return beta;
    }

    @Override
    public double beta(int index) {
        if (index < 0 || index >= betaSize()) {
            throw new InvalidParamException();
        }
        return beta[index];
    }

    @Override
    public int betaSize() {
        return beta.length;
    }

    @Override
    public void setBeta(int index, double beta) {
        if (index < 0 || index >= betaSize()) {
            throw new InvalidParamException();
        }
        this.beta[index] = beta;
    }

    @Override
    public void setBeta(double[] beta) {
        if (beta.length != xSize() + 1) {
            throw new InvalidParamException();
        }
        this.beta = beta;
    }

    @Override
    public double y() {
        return record.y();
    }

    @Override
    public double x(int index) {
        return record.x(index);
    }

    @Override
    public double[] x() {
        return record.x();
    }

    @Override
    public double prediction() {
        return prediction;
    }
}
