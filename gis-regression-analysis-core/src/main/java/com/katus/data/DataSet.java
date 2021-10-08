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
        return Nd4j.create(yArray());
    }

    default INDArray xMatrix(int index) {
        return Nd4j.create(xArray(index));
    }

    default INDArray xMatrix() {
        double[][] xM = new double[xSize()][];
        for (int i = 0; i < xSize(); i++) {
            xM[i] = xArray(i);
        }
        return Nd4j.create(xM);
    }
}
