package com.katus.demo;

import com.katus.data.HaiNingDataSet;
import com.katus.data.HaiNingRecord;
import com.katus.data.HaiNingResultRecord;
import com.katus.exception.InvalidParamException;
import com.katus.regression.linear.WeightedRegression;
import com.katus.regression.weight.BandwidthType;
import com.katus.regression.weight.NetworkCostWeightCalculator;
import com.katus.test.aic.AIC;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.time.Duration;
import java.util.Map;

/**
 * @author SUN Katus
 * @version 1.0, 2021-11-06
 */
@Slf4j
public class AicForCost {

    public static void main(String[] args) {
        BasicFunctions.firstConfig();
        if (args.length < 2) {
            log.error("args less than 2");
            throw new InvalidParamException();
        }
        String trainFilename = args[0];
        BandwidthType type = BandwidthType.valueOf(args[1]);
        double ratio = Double.parseDouble(args[2]);
        int numThread = Integer.parseInt(args[3]);

        log.info("--------AIC TEST--------");
        log.info("DATA: {}", trainFilename);
        log.info("Bandwidth Type: {}", type);
        log.info("Test Ratio: {}", ratio);

        HaiNingDataSet trainingDataSet = BasicFunctions.readHaiNingDataSet(trainFilename);

        double[] bandwidths = new double[25];
        for (int i = 0; i < bandwidths.length; i++) {
            bandwidths[i] = i + 1;
        }
        AIC<HaiNingRecord, HaiNingResultRecord> aic = new AIC.AICBuilder<HaiNingRecord, HaiNingResultRecord>()
                .testDataSet(trainingDataSet)
                .linearRegressionBuilder((td, rd, b) -> {
                    GenericObjectPoolConfig<Jedis> poolConfig = new GenericObjectPoolConfig<>();
                    poolConfig.setMaxTotal(numThread);
                    poolConfig.setMaxIdle(numThread);
                    poolConfig.setMinIdle(0);
                    poolConfig.setMaxWait(Duration.ofMinutes(1L));
                    JedisPool jedisPool = new JedisPool(poolConfig, "localhost", 6379, 3000, "skrv587");
                    NetworkCostWeightCalculator weightCalculator = new NetworkCostWeightCalculator.NetworkCostWeightCalculatorBuilder()
                            .jedisPool(jedisPool)
                            .bandwidthType(type)
                            .trainingDataSet(td)
                            .bandwidth(b)
                            .build();
                    return new WeightedRegression.WeightedRegressionBuilder<HaiNingRecord, HaiNingResultRecord>()
                            .resultDataSet(rd)
                            .weightCalculator(weightCalculator)
                            .numThread(numThread)
                            .build();
                })
                .trainingRatio(ratio)
                .bandwidths(bandwidths)
                .clazz(HaiNingResultRecord.class)
                .build();

        if (aic.pass()) {
            Map<Double, Double> resultMap = aic.getAicResults();
            for (Map.Entry<Double, Double> entry : resultMap.entrySet()) {
                log.info("{} {} : {}", type, entry.getKey(), entry.getValue());
            }
        }
    }
}
