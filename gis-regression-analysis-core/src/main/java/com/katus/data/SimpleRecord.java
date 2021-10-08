package com.katus.data;

import com.katus.exception.InvalidParamException;

/**
 * @author SUN Katus
 * @version 1.0, 2021-10-07
 */
public class SimpleRecord extends AbstractRecord<String> {
    protected final String SEPARATOR;
    protected double[] x;
    protected double y;

    public SimpleRecord(String str, String sep) {
        super(str);
        this.SEPARATOR = sep;
    }

    @Override
    public void setX(double[] x) {
        this.x = x;
    }

    @Override
    public int xSize() {
        return x.length;
    }

    @Override
    public void setX(int index, double x) {
        if (index < 0 || index >= xSize()) {
            throw new InvalidParamException();
        }
        this.x[index] = x;
    }

    @Override
    public void setY(double y) {
        this.y = y;
    }

    @Override
    public double y() {
        return y;
    }

    @Override
    public double x(int index) {
        if (index < 0 || index >= xSize()) {
            throw new InvalidParamException();
        }
        return x[index];
    }

    @Override
    public double[] x() {
        return x;
    }

    @Override
    public double[] load(String s) {
        String[] items = s.split(SEPARATOR);
        if (items.length < 1) {
            throw new InvalidParamException();
        }
        double[] data = new double[items.length];
        for (int i = 0; i < data.length; i++) {
            data[i] = Double.parseDouble(items[i]);
        }
        return data;
    }
}
