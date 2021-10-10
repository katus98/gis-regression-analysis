package com.katus.data;

/**
 * @author SUN Katus
 * @version 1.0, 2021-10-10
 */
public interface Prediction {

    double prediction();

    default double getPrediction() {
        return prediction();
    }
}
