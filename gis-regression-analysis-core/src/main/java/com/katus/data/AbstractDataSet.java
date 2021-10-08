package com.katus.data;

import com.katus.exception.InvalidParamException;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.List;

/**
 * @author SUN Katus
 * @version 1.0, 2021-10-08
 */
public abstract class AbstractDataSet<R extends Record> implements DataSet {
    protected List<R> records;
    protected volatile boolean latest = false;
    private volatile INDArray xMatrix, yMatrix, xMatrixT, yMatrixT;

    public AbstractDataSet(DataSetLoader<R> loader) {
        this.records = loader.load();
        update();
    }

    public R getRecord(int index) {
        if (index < 0 || index >= size()) {
            throw new InvalidParamException();
        }
        return records.get(index);
    }

    public void addRecord(R r) {
        this.latest = false;
        records.add(r);
    }

    @Override
    public double[] yArray() {
        double[] yArray = new double[size()];
        for (int i = 0; i < size(); i++) {
            yArray[i] = records.get(i).y();
        }
        return yArray;
    }

    @Override
    public double[] xArray(int index) {
        double[] xArray = new double[size()];
        for (int i = 0; i < size(); i++) {
            xArray[i] = records.get(i).x(index);
        }
        return xArray;
    }

    @Override
    public int xSize() {
        if (size() < 1) {
            return 0;
        }
        return records.get(0).xSize();
    }

    @Override
    public int size() {
        return records.size();
    }

    @Override
    public INDArray yMatrix() {
        update();
        return yMatrix;
    }

    @Override
    public INDArray xMatrix() {
        update();
        return xMatrix;
    }

    @Override
    public INDArray yMatrixT() {
        update();
        return yMatrixT;
    }

    @Override
    public INDArray xMatrixT() {
        update();
        return xMatrixT;
    }

    private void update() {
        if (!latest) {
            synchronized (this) {
                if (!latest) {
                    this.yMatrix = DataSet.super.yMatrix();
                    this.yMatrixT = DataSet.super.yMatrixT();
                    this.xMatrixT = DataSet.super.xMatrixT();
                    this.xMatrix = xMatrixT.transpose();
                    this.latest = true;
                }
            }
        }
    }
}
