package com.katus.data;

/**
 * @author SUN Katus
 * @version 1.0, 2021-10-05
 */
public interface Record {

    double y();

    double x(int index);

    double[] x();

    default int xSize() {
        return x().length;
    }
}
