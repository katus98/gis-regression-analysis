package com.katus.data;

/**
 * @author SUN Katus
 * @version 1.0, 2021-10-12
 */
public interface RSquare {

    double rSquare();

    void setRSquare(double rSquare);

    default double getRSquare() {
        return rSquare();
    }
}
