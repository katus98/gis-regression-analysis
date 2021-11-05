package com.katus.data;

import com.katus.exception.DataException;
import com.katus.exception.DataSetConvertException;
import com.katus.exception.InvalidParamException;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author SUN Katus
 * @version 1.0, 2021-10-08
 */
public abstract class AbstractDataSet<R extends Record> implements DataSet, Cloneable {
    private static final Logger logger = LoggerFactory.getLogger(AbstractDataSet.class);

    protected List<R> records;
    protected volatile boolean latest = false;
    private volatile INDArray xMatrix, yMatrix, xMatrixT, yMatrixT;

    protected AbstractDataSet(DataSetInput<R> loader) {
        input(loader);
    }

    public void input(DataSetInput<R> input) {
        this.records = input.input();
        update();
    }

    public void output(DataSetOutput<R> output) {
        output.output(records);
    }

    public R getRecord(int index) {
        if (index < 0 || index >= size()) {
            logger.error("index of record is out of range");
            throw new InvalidParamException();
        }
        return records.get(index);
    }

    public synchronized void addRecord(R r) {
        this.latest = false;
        records.add(r);
    }

    public R removeRecord(int index) {
        if (index < 0 || index >= size()) {
            logger.error("index of record is out of range");
            throw new InvalidParamException();
        }
        synchronized (this) {
            this.latest = false;
            return records.remove(index);
        }
    }

    public synchronized void clear() {
        records.clear();
        this.latest = false;
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

    @Override
    @SuppressWarnings("unchecked")
    public AbstractDataSet<R> clone() throws CloneNotSupportedException {
        AbstractDataSet<R> clone = (AbstractDataSet<R>) super.clone();
        clone.records = new ArrayList<>(records);
        clone.latest = false;
        return clone;
    }

    @SuppressWarnings("unchecked")
    public <RR extends AbstractResultRecordWithInfo<R>> AbstractResultDataSet<R, RR> convertToResultDataSet(Class<RR> clazz) throws DataSetConvertException {
        RR resultRecord;
        try {
            resultRecord = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error("can not construct the result record class", e);
            throw new DataSetConvertException(e);
        }
        return new AbstractResultDataSet<R, RR>(() -> {
            List<RR> list = new ArrayList<>();
            for (R record : records) {
                try {
                    RR rRecord = (RR) resultRecord.clone();
                    rRecord.setBaseRecord(record);
                    list.add(rRecord);
                } catch (CloneNotSupportedException e) {
                    logger.error("can not clone result records", e);
                    throw new DataException();
                }
            }
            return list;
        }) {};
    }

    @SuppressWarnings("unchecked")
    public <RR extends AbstractResultRecordWithInfo<R>, RDS extends AbstractResultDataSet<R, RR>> RDS convertToResultDataSet(Class<RR> rrClass, Class<RDS> rdsClass) throws DataSetConvertException {
        try {
            RR resultRecord = rrClass.newInstance();
            Constructor<RDS> constructor = rdsClass.getConstructor(DataSetInput.class);
            return constructor.newInstance((DataSetInput<RR>) () -> {
                List<RR> list = new ArrayList<>();
                for (R record : records) {
                    try {
                        RR rRecord = (RR) resultRecord.clone();
                        rRecord.setBaseRecord(record);
                        list.add(rRecord);
                    } catch (CloneNotSupportedException e) {
                        logger.error("can not clone result records", e);
                        throw new DataException();
                    }
                }
                return list;
            });
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            logger.error("can not construct the result record class", e);
            throw new DataSetConvertException(e);
        }
    }
}
