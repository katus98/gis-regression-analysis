package com.katus.data;

/**
 * @author SUN Katus
 * @version 1.0, 2021-10-09
 */
public interface Coefficient {

    double[] beta();

    double beta(int index);

    int betaSize();

    void setBeta(int index, double beta);

    void setBeta(double[] beta);
}
