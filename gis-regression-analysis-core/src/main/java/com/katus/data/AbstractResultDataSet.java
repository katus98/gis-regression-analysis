package com.katus.data;

import com.katus.exception.InvalidParamException;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author SUN Katus
 * @version 1.0, 2021-10-09
 */
public abstract class AbstractResultDataSet<R extends Record, RR extends AbstractResultRecordWithInfo<R>> extends AbstractDataSet<RR> {
    private static final Logger logger = LoggerFactory.getLogger(AbstractResultDataSet.class);

    protected AbstractResultDataSet(DataSetInput<RR> loader) {
        super(loader);
    }

    public INDArray betaMatrix(int index) {
        return Nd4j.create(new int[]{1, xSize() + 1}, records.get(index).beta);
    }

    public void setBetaMatrix(int index, INDArray betaMatrix) {
        if (betaMatrix.shape()[1] != 1L || betaMatrix.shape()[0] != xSize() + 1) {
            logger.error("shape of beta matrix is wrong");
            throw new InvalidParamException();
        }
        double[] beta = new double[xSize() + 1];
        for (int i = 0; i < beta.length; i++) {
            beta[i] = betaMatrix.getDouble(i, 0);
        }
        records.get(index).setBeta(beta);
    }

    @Override
    public AbstractResultDataSet<R, RR> clone() throws CloneNotSupportedException {
        return (AbstractResultDataSet<R, RR>) super.clone();
    }

    public AbstractDataSet<R> convertToSourceDataSet() {
        return new AbstractDataSet<R>(() -> {
            List<R> list = new ArrayList<>();
            for (AbstractResultRecordWithInfo<R> record : records) {
                if (record.y() != Constants.NO_DATA) {
                    list.add(record.getBaseRecord());
                }
            }
            return list;
        }) {};
    }
}
