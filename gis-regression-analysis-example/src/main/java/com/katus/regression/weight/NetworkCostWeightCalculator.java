package com.katus.regression.weight;

import com.katus.data.AbstractDataSet;
import com.katus.data.HaiNingRecord;
import com.katus.exception.InvalidParamException;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.JedisPool;

/**
 * @author SUN Katus
 * @version 1.0, 2021-11-05
 */
public class NetworkCostWeightCalculator extends WeightCalculator<HaiNingRecord> {
    private final JedisPool jedisPool;

    protected NetworkCostWeightCalculator(BandwidthType bandwidthType, WeightType weightType, double bandwidth, AbstractDataSet<HaiNingRecord> trainingDataSet, WeightFunction weightFunction, JedisPool jedisPool) {
        super(bandwidthType, weightType, bandwidth, trainingDataSet, weightFunction);
        this.jedisPool = jedisPool;
    }

    @Override
    public double calDistance(HaiNingRecord r1, HaiNingRecord r2) {
        // todo
        return 0;
    }

    @Slf4j
    public static class NetworkCostWeightCalculatorBuilder extends WeightCalculatorBuilder<HaiNingRecord> {
        private JedisPool jedisPool;

        @Override
        public NetworkCostWeightCalculator build() {
            if (!check()) {
                log.error("network cost weight calculator params are invalid");
                throw new InvalidParamException();
            }
            return new NetworkCostWeightCalculator(bandwidthType, weightType, bandwidth, trainingDataSet, weightFunction, jedisPool);
        }

        public NetworkCostWeightCalculator.NetworkCostWeightCalculatorBuilder jedisPool(JedisPool jedisPool) {
            this.jedisPool = jedisPool;
            return this;
        }

        @Override
        public boolean check() {
            return super.check() && jedisPool != null;
        }

        @Override
        public NetworkCostWeightCalculator.NetworkCostWeightCalculatorBuilder bandwidthType(BandwidthType bandwidthType) {
            this.bandwidthType = bandwidthType;
            return this;
        }

        @Override
        public NetworkCostWeightCalculator.NetworkCostWeightCalculatorBuilder weightType(WeightType weightType) {
            this.weightType = weightType;
            return this;
        }

        @Override
        public NetworkCostWeightCalculator.NetworkCostWeightCalculatorBuilder bandwidth(double bandwidth) {
            this.bandwidth = bandwidth;
            return this;
        }

        @Override
        public NetworkCostWeightCalculator.NetworkCostWeightCalculatorBuilder trainingDataSet(AbstractDataSet<HaiNingRecord> trainingDataSet) {
            this.trainingDataSet = trainingDataSet;
            return this;
        }

        @Override
        public NetworkCostWeightCalculator.NetworkCostWeightCalculatorBuilder weightFunction(WeightFunction weightFunction) {
            this.weightFunction = weightFunction;
            return this;
        }
    }
}
