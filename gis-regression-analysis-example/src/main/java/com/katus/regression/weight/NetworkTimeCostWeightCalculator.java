package com.katus.regression.weight;

import com.katus.data.AbstractDataSet;
import com.katus.data.JinHuaRecord;
import com.katus.exception.InvalidParamException;
import com.katus.global.GraphCalculator;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author SUN Katus
 * @version 1.0, 2023-02-12
 */
@Slf4j
public class NetworkTimeCostWeightCalculator extends WeightCalculator<JinHuaRecord> {
    protected final double distanceRadius;

    protected NetworkTimeCostWeightCalculator(BandwidthType bandwidthType, WeightType weightType, double bandwidth,
                                              AbstractDataSet<JinHuaRecord> trainingDataSet, WeightFunction weightFunction,
                                              double distanceRadius) {
        super(bandwidthType, weightType, bandwidth, trainingDataSet, weightFunction);
        this.distanceRadius = distanceRadius;
    }

    @Override
    protected void initDistances(JinHuaRecord r1) {
        this.distances = new HashMap<>();
        try {
            GraphCalculator calculator = new GraphCalculator(r1.id(), distanceRadius);
            for (int i = 0; i < trainingDataSet.size(); i++) {
                JinHuaRecord record = trainingDataSet.getRecord(i);
                distances.put(record, calculator.computeCost(record.id()));
            }
            this.distances = distances.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
        } catch (SQLException e) {
            log.error("SQL ERROR", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public double calDistance(JinHuaRecord r1, JinHuaRecord r2) {
        try {
            GraphCalculator calculator = new GraphCalculator(r1.id(), distanceRadius);
            return calculator.computeCost(r2.id());
        } catch (SQLException e) {
            log.error("SQL ERROR", e);
            throw new RuntimeException(e);
        }
    }

    public static class NetworkTimeCostWeightCalculatorBuilder extends WeightCalculatorBuilder<JinHuaRecord> {
        protected double distanceRadius;

        @Override
        public NetworkTimeCostWeightCalculator build() {
            if (!check()) {
                log.error("network time cost weight calculator params are invalid");
                throw new InvalidParamException();
            }
            return new NetworkTimeCostWeightCalculator(bandwidthType, weightType, bandwidth, trainingDataSet, weightFunction, distanceRadius);
        }

        public boolean check() {
            return bandwidth > 0.0 && trainingDataSet != null && (!WeightType.CUSTOMIZED.equals(weightType) || weightFunction != null) && distanceRadius > 0;
        }

        public NetworkTimeCostWeightCalculatorBuilder distanceRadius(double distanceRadius) {
            this.distanceRadius = distanceRadius;
            return this;
        }

        public NetworkTimeCostWeightCalculatorBuilder bandwidthType(BandwidthType bandwidthType) {
            this.bandwidthType = bandwidthType;
            return this;
        }

        public NetworkTimeCostWeightCalculatorBuilder weightType(WeightType weightType) {
            this.weightType = weightType;
            return this;
        }

        public NetworkTimeCostWeightCalculatorBuilder bandwidth(double bandwidth) {
            this.bandwidth = bandwidth;
            return this;
        }

        public NetworkTimeCostWeightCalculatorBuilder trainingDataSet(AbstractDataSet<JinHuaRecord> trainingDataSet) {
            this.trainingDataSet = trainingDataSet;
            return this;
        }

        public NetworkTimeCostWeightCalculatorBuilder weightFunction(WeightFunction weightFunction) {
            this.weightFunction = weightFunction;
            return this;
        }
    }
}
