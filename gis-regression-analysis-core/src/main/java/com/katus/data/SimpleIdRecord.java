package com.katus.data;

import com.katus.exception.InvalidParamException;

import java.util.Objects;

/**
 * @author SUN Katus
 * @version 1.0, 2021-10-08
 */
public class SimpleIdRecord extends SimpleRecord implements Recognizable<Long>, Comparable<SimpleIdRecord> {
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

    @Override
    public int compareTo(SimpleIdRecord o) {
        return id.compareTo(o.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleIdRecord that = (SimpleIdRecord) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
