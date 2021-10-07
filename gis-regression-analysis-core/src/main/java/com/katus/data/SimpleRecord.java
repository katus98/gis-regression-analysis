package com.katus.data;

import com.katus.exception.InvalidParamException;

/**
 * @author SUN Katus
 * @version 1.0, 2021-10-07
 */
public class SimpleRecord extends AbstractRecord<Long, String> {
    protected final String SEPARATOR;
    protected long id;
    protected double[] xs;
    protected double y;

    public SimpleRecord(String str, String sep) {
        super(str);
        this.SEPARATOR = sep;
    }

    @Override
    public void setX(double[] xs) {
        this.xs = xs;
    }

    @Override
    public void setX(int index, double x) {
        if (index < 0 || index >= xs.length) {
            throw new InvalidParamException();
        }
        this.xs[index] = x;
    }

    @Override
    public void setY(double y) {
        this.y = y;
    }

    @Override
    public Long id() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public double getX(int index) {
        if (index < 0 || index >= xs.length) {
            throw new InvalidParamException();
        }
        return xs[index];
    }

    @Override
    public double[] getX() {
        return xs;
    }

    @Override
    public double[] load(String s) {
        String[] items = s.split(SEPARATOR);
        if (items.length < 2) {
            throw new InvalidParamException();
        }
        setId(Long.valueOf(items[0]));
        double[] data = new double[items.length - 1];
        for (int i = 0; i < data.length; i++) {
            data[i] = Double.parseDouble(items[i+1]);
        }
        return data;
    }
}
