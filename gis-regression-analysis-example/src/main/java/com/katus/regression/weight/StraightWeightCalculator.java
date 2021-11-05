package com.katus.regression.weight;

import com.katus.data.AbstractDataSet;
import com.katus.data.HaiNingRecord;
import com.katus.exception.InvalidParamException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author SUN Katus
 * @version 1.0, 2021-11-05
 */
public class StraightWeightCalculator extends WeightCalculator<HaiNingRecord> {
    protected StraightWeightCalculator(BandwidthType bandwidthType, WeightType weightType, double bandwidth, AbstractDataSet<HaiNingRecord> trainingDataSet, WeightFunction weightFunction) {
        super(bandwidthType, weightType, bandwidth, trainingDataSet, weightFunction);
    }

    @Override
    public double calDistance(HaiNingRecord r1, HaiNingRecord r2) {
        return Math.sqrt(Math.pow(r1.getLonX() - r2.getLonX(), 2) + Math.pow(r1.getLatY() - r2.getLatY(), 2));
    }

    @Override
    public StraightWeightCalculator clone() throws CloneNotSupportedException {
        return (StraightWeightCalculator) super.clone();
    }

    @Slf4j
    public static class StraightWeightCalculatorBuilder extends WeightCalculatorBuilder<HaiNingRecord> {

        @Override
        public WeightCalculator<HaiNingRecord> build() {
            if (!check()) {
                log.error("straight weight calculator params are invalid");
                throw new InvalidParamException();
            }
            return new StraightWeightCalculator(bandwidthType, weightType, bandwidth, trainingDataSet, weightFunction);
        }
    }
}
