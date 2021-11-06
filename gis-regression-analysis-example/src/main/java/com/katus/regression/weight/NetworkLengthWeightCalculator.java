package com.katus.regression.weight;

import com.katus.data.AbstractDataSet;
import com.katus.data.HaiNingRecord;
import com.katus.exception.InvalidParamException;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author SUN Katus
 * @version 1.0, 2021-11-05
 */
public class NetworkLengthWeightCalculator extends WeightCalculator<HaiNingRecord> {
    private final JedisPool jedisPool;

    protected NetworkLengthWeightCalculator(BandwidthType bandwidthType, WeightType weightType, double bandwidth, AbstractDataSet<HaiNingRecord> trainingDataSet, WeightFunction weightFunction, JedisPool jedisPool) {
        super(bandwidthType, weightType, bandwidth, trainingDataSet, weightFunction);
        this.jedisPool = jedisPool;
    }

    @Override
    public double calDistance(HaiNingRecord r1, HaiNingRecord r2) {
        Jedis jedis = jedisPool.getResource();
        jedis.select(getPartition(r1.getId()));
        String length = jedis.lrange("length-" + r1.getId(), r2.getId() - 1, r2.getId() - 1).get(0);
        jedis.close();
        return Double.parseDouble(length);
    }

    @Override
    protected void initDistances(HaiNingRecord r1) {
        this.distances = new HashMap<>();
        Jedis jedis = jedisPool.getResource();
        jedis.select(getPartition(r1.getId()));
        List<String> costs = jedis.lrange("length-" + r1.getId(), 0, -1);
        jedis.close();
        for (int i = 0; i < trainingDataSet.size(); i++) {
            HaiNingRecord record = trainingDataSet.getRecord(i);
            distances.put(record, Double.valueOf(costs.get((int) (record.getId() - 1))));
        }
        this.distances = distances.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
    }

    private int getPartition(long i) {
        return Math.min(((int) i - 1) / 954, 15);
    }

    @Slf4j
    public static class NetworkLengthWeightCalculatorBuilder extends WeightCalculatorBuilder<HaiNingRecord> {
        private JedisPool jedisPool;

        @Override
        public NetworkLengthWeightCalculator build() {
            if (!check()) {
                log.error("network length weight calculator params are invalid");
                throw new InvalidParamException();
            }
            return new NetworkLengthWeightCalculator(bandwidthType, weightType, bandwidth, trainingDataSet, weightFunction, jedisPool);
        }

        public NetworkLengthWeightCalculatorBuilder jedisPool(JedisPool jedisPool) {
            this.jedisPool = jedisPool;
            return this;
        }

        @Override
        public boolean check() {
            return super.check() && jedisPool != null;
        }

        @Override
        public NetworkLengthWeightCalculatorBuilder bandwidthType(BandwidthType bandwidthType) {
            this.bandwidthType = bandwidthType;
            return this;
        }

        @Override
        public NetworkLengthWeightCalculatorBuilder weightType(WeightType weightType) {
            this.weightType = weightType;
            return this;
        }

        @Override
        public NetworkLengthWeightCalculatorBuilder bandwidth(double bandwidth) {
            this.bandwidth = bandwidth;
            return this;
        }

        @Override
        public NetworkLengthWeightCalculatorBuilder trainingDataSet(AbstractDataSet<HaiNingRecord> trainingDataSet) {
            this.trainingDataSet = trainingDataSet;
            return this;
        }

        @Override
        public NetworkLengthWeightCalculatorBuilder weightFunction(WeightFunction weightFunction) {
            this.weightFunction = weightFunction;
            return this;
        }
    }
}
