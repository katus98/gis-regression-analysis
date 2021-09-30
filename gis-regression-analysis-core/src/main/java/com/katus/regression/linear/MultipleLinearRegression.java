package com.katus.regression.linear;

import com.katus.exception.InvalidParamException;
import com.katus.regression.Regression;

/**
 * @author SUN Katus
 * @version 1.0, 2021-09-30
 */
public class MultipleLinearRegression implements Regression {
    private final int xNumber;

    public MultipleLinearRegression(int xNumber) {
        if (xNumber < 1) {
            throw new InvalidParamException();
        }
        this.xNumber = xNumber;
    }
}
