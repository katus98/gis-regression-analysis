package com.katus.test.r;

import com.katus.data.Constants;
import com.katus.test.Test;
import org.nd4j.linalg.api.ndarray.INDArray;

/**
 * @author SUN Katus
 * @version 1.0, 2021-10-10
 */
public class GlobalRSquare implements Test {
    private final INDArray trueValueMatrix;
    private final double[] predictValues;
    private volatile double r2 = Constants.NO_DATA;

    public GlobalRSquare(INDArray trueValueMatrix, double[] predictValues) {
        this.trueValueMatrix = trueValueMatrix;
        this.predictValues = predictValues;
    }

    public double getR2() {
        test();
        return r2;
    }

    @Override
    public void test() {
        if (r2 == Constants.NO_DATA) {
            synchronized (this) {
                if (r2 == Constants.NO_DATA) {
                    double avg = trueValueMatrix.meanNumber().doubleValue();
                    double s2 = trueValueMatrix.varNumber().doubleValue();
                    double result = 0.0;
                    for (double predictValue : predictValues) {
                        result += Math.pow(predictValue - avg, 2);
                    }
                    result /= predictValues.length;
                    this.r2 = result / s2;
                }
            }
        }
    }

    @Override
    public boolean pass() {
        return r2 != Constants.NO_DATA;
    }
}
