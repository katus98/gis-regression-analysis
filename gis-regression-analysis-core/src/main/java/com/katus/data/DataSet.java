package com.katus.data;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

/**
 * @author SUN Katus
 * @version 1.0, 2021-09-30
 */
public interface DataSet {

    double[] yArray();

    double[] xArray(int index);

    int xSize();

    int size();

    default INDArray yMatrix() {
        return Nd4j.create(yArray(), new int[]{size(), 1});
    }

    default INDArray yMatrixT() {
        return Nd4j.create(yArray());
    }

    default INDArray xMatrix(int index) {
        return xMatrix().getColumn(index);
    }

    default INDArray xMatrix() {
        return xMatrixT().transpose();
    }

    default INDArray xMatrixT() {
        double[][] xM = new double[xSize() + 1][];
        xM[0] = new double[size()];
        for (int i = 0; i < size(); i++) {
            xM[0][i] = 1.0;
        }
        for (int i = 1; i < xSize() + 1; i++) {
            xM[i] = xArray(i - 1);
        }
        return Nd4j.create(xM);
    }
}
