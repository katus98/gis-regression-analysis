package com.katus.data;

import com.katus.exception.InvalidParamException;

/**
 * @author SUN Katus
 * @version 1.0, 2021-10-08
 */
public class SimpleIdRecord extends SimpleRecord implements Recognizable<Long> {
    protected Long id;

    public SimpleIdRecord(String str, String sep) {
        super(str, sep);
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
