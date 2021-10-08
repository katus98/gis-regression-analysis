package com.katus.data;

import com.katus.exception.InvalidParamException;

/**
 * @author SUN Katus
 * @version 1.0, 2021-10-08
 */
public abstract class AbstractDataSet<R extends Record> implements DataSet {
    protected R[] records;

    public AbstractDataSet(DataSetLoader<R> loader) {
        this.records = loader.load();
    }

    public R getRecord(int index) {
        if (index < 0 || index >= size()) {
            throw new InvalidParamException();
        }
        return records[index];
    }

    @Override
    public double[] yArray() {
        double[] yArray = new double[size()];
        for (int i = 0; i < size(); i++) {
            yArray[i] = records[i].y();
        }
        return yArray;
    }

    @Override
    public double[] xArray(int index) {
        double[] xArray = new double[size()];
        for (int i = 0; i < size(); i++) {
            xArray[i] = records[i].x(index);
        }
        return xArray;
    }

    @Override
    public int xSize() {
        if (size() < 1) {
            return 0;
        }
        return records[0].xSize();
    }

    @Override
    public int size() {
        return records.length;
    }
}
