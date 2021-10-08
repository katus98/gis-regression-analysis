package com.katus.data;

/**
 * @author SUN Katus
 * @version 1.0, 2021-10-07
 */
public abstract class AbstractRecord<R> implements Record {

    AbstractRecord() {
    }

    protected AbstractRecord(R r) {
        init(r);
    }

    public abstract void setX(double[] xs);

    public abstract void setX(int index, double x);

    public abstract void setY(double y);

    protected abstract double[] load(R r);

    void init(R r) {
        double[] data = load(r);
        setY(data[0]);
        double[] xs = new double[data.length - 1];
        System.arraycopy(data, 1, xs, 0, xs.length);
        setX(xs);
    }
}
