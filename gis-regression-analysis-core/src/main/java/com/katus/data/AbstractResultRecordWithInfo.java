package com.katus.data;

import com.katus.exception.InvalidParamException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * @author SUN Katus
 * @version 1.0, 2021-10-09
 */
public abstract class AbstractResultRecordWithInfo<R extends Record> implements Record, Coefficient, Prediction, RSquare, Cloneable {
    private static final Logger logger = LoggerFactory.getLogger(AbstractResultRecordWithInfo.class);

    protected R record;
    protected double[] beta = new double[0];
    protected double prediction = Constants.NO_DATA;
    protected double rSquare = Constants.NO_DATA;

    public abstract String put();

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

    public void setBaseRecord(R record) {
        this.record = record;
    }

    public R getBaseRecord() {
        return record;
    }

    @Override
    public double[] beta() {
        return beta;
    }

    @Override
    public double beta(int index) {
        if (index < 0 || index >= betaSize()) {
            logger.error("index of beta is out of range");
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
            logger.error("index of beta is out of range");
            throw new InvalidParamException();
        }
        this.beta[index] = beta;
    }

    @Override
    public void setBeta(double[] beta) {
        if (beta.length != xSize() + 1) {
            logger.error("beta size is wrong");
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

    @Override
    public double rSquare() {
        return rSquare;
    }

    @Override
    public void setRSquare(double rSquare) {
        this.rSquare = rSquare;
    }

    @Override
    @SuppressWarnings("unchecked")
    public AbstractResultRecordWithInfo<R> clone() throws CloneNotSupportedException {
        AbstractResultRecordWithInfo<R> clone = (AbstractResultRecordWithInfo<R>) super.clone();
        clone.beta = Arrays.copyOf(beta(), betaSize());
        return clone;
    }
}
