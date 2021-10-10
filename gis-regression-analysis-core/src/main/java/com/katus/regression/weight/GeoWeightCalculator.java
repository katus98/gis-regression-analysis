package com.katus.regression.weight;

import com.katus.data.AbstractDataSet;
import com.katus.data.SimpleIdGeoRecord;
import com.katus.exception.InvalidParamException;

/**
 * @author SUN Katus
 * @version 1.0, 2021-10-09
 */
public class GeoWeightCalculator extends WeightCalculator<SimpleIdGeoRecord> {

    protected GeoWeightCalculator(BandwidthType bandwidthType, WeightType weightType, double bandwidth, AbstractDataSet<SimpleIdGeoRecord> trainingDataSet, WeightFunction weightFunction) {
        super(bandwidthType, weightType, bandwidth, trainingDataSet, weightFunction);
    }

    @Override
    public double calDistance(SimpleIdGeoRecord r1, SimpleIdGeoRecord r2) {
        return Math.sqrt(Math.pow(r1.getLonX() - r2.getLonX(), 2) + Math.pow(r1.getLatY() - r2.getLatY(), 2));
    }

    public static class GeoWeightCalculatorBuilder extends WeightCalculatorBuilder<SimpleIdGeoRecord> {

        @Override
        public GeoWeightCalculator build() {
            if (!check()) {
                throw new InvalidParamException();
            }
            return new GeoWeightCalculator(bandwidthType, weightType, bandwidth, trainingDataSet, weightFunction);
        }
    }
}
