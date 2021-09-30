package com.katus.data;

/**
 * @author SUN Katus
 * @version 1.0, 2021-09-30
 */
public interface OriginalDataSet {
    double[] getYArray();
    double[] getXArray(int index);
    int getXNumber();
}
