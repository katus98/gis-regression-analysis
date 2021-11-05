package com.katus.data;

import com.katus.exception.InvalidParamException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author SUN Katus
 * @version 1.0, 2021-10-09
 */
public class SimpleCoefficient implements Coefficient {
    private static final Logger logger = LoggerFactory.getLogger(SimpleCoefficient.class);

    protected double[] beta;

    @Override
    public double[] beta() {
        return beta;
    }

    @Override
    public double beta(int index) {
        if (index < 0 || index >= betaSize()) {
            logger.error("index of beta is out of range");
            throw new InvalidParamException();
        }
        return beta[index];
    }

    @Override
    public int betaSize() {
        return beta.length;
    }

    @Override
    public void setBeta(int index, double beta) {
        if (index < 0 || index >= betaSize()) {
            logger.error("index of beta is out of range");
            throw new InvalidParamException();
        }
        this.beta[index] = beta;
    }

    @Override
    public void setBeta(double[] beta) {
        this.beta = beta;
    }
}
