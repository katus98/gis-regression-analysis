package com.katus.data;

import com.katus.exception.InvalidParamException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author SUN Katus
 * @version 1.0, 2021-10-07
 */
public abstract class AbstractRecord<R> implements Record {
    private static final Logger logger = LoggerFactory.getLogger(AbstractRecord.class);

    protected double[] x;
    protected double y = Constants.NO_DATA;

    AbstractRecord() {
    }

    protected AbstractRecord(R r) {
        init(r);
    }

    public void setX(double[] x) {
        this.x = x;
    }

    public void setX(int index, double x) {
        if (index < 0 || index >= xSize()) {
            logger.error("index of x is out of range");
            throw new InvalidParamException();
        }
        this.x[index] = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public abstract double[] load(R r);

    public abstract String put();

    @Override
    public double y() {
        return y;
    }

    @Override
    public double x(int index) {
        if (index < 0 || index >= xSize()) {
            logger.error("index of x is out of range");
            throw new InvalidParamException();
        }
        return x[index];
    }

    @Override
    public double[] x() {
        return x;
    }

    void init(R r) {
        double[] data = load(r);
        setY(data[0]);
        double[] xs = new double[data.length - 1];
        System.arraycopy(data, 1, xs, 0, xs.length);
        setX(xs);
    }
}
