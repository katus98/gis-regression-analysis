package com.katus.data;

import java.io.Serializable;

/**
 * @author SUN Katus
 * @version 1.0, 2021-10-05
 */
public interface Record<T extends Serializable> {
    T getId();
    double getY();
    double getX(int index);
    double[] getX();
}
