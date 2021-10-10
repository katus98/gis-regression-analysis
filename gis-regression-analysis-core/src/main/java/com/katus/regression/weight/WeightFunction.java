package com.katus.regression.weight;

/**
 * @author SUN Katus
 * @version 1.0, 2021-10-10
 */
@FunctionalInterface
public interface WeightFunction {

    double weight(double distance, double bandwidth);
}
