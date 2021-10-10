package com.katus.test.r;

import com.katus.test.Test;
import org.nd4j.linalg.api.ndarray.INDArray;

/**
 * @author SUN Katus
 * @version 1.0, 2021-10-10
 */
public class GlobalR implements Test {
    private final INDArray trueValueMatrix;
    private final double[] predictValues;
    private volatile double r2 = -1.0;

    public GlobalR(INDArray trueValueMatrix, double[] predictValues) {
        this.trueValueMatrix = trueValueMatrix;
        this.predictValues = predictValues;
    }

    public double getR2() {
        test();
        return r2;
    }

    @Override
    public void test() {
        if (r2 < 0) {
            synchronized (this) {
                if (r2 < 0) {
                    double avg = trueValueMatrix.meanNumber().doubleValue();
                    double s2 = trueValueMatrix.varNumber().doubleValue();
                    double result = 0.0;
                    for (double predictValue : predictValues) {
                        result += Math.pow(predictValue - avg, 2);
                    }
                    this.r2 = result / s2;
                }
            }
        }
    }

    @Override
    public boolean pass() {
        return false;
    }
}
