package com.katus.demo;

import com.katus.data.HaiNingDataSet;
import com.katus.data.HaiNingRecord;
import com.katus.data.HaiNingResultRecord;
import com.katus.exception.InvalidParamException;
import com.katus.regression.linear.WeightedRegression;
import com.katus.regression.weight.BandwidthType;
import com.katus.regression.weight.NetworkLengthWeightCalculator;
import com.katus.test.aic.AIC;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.JedisPool;

import java.util.Map;

/**
 * @author SUN Katus
 * @version 1.0, 2021-11-06
 */
@Slf4j
public class AicForLength {

    public static void main(String[] args) {
        BasicFunctions.firstConfig();
        if (args.length < 2) {
            log.error("args less than 2");
            throw new InvalidParamException();
        }
        String trainFilename = args[0];
        int numThread = Integer.parseInt(args[1]);

        HaiNingDataSet trainingDataSet = BasicFunctions.readDataSet(trainFilename);

        double[] bandwidths = new double[50];
        for (int i = 0; i < bandwidths.length; i++) {
            bandwidths[i] = i + 1;
        }
        AIC<HaiNingRecord, HaiNingResultRecord> aic = new AIC.AICBuilder<HaiNingRecord, HaiNingResultRecord>()
                .testDataSet(trainingDataSet)
                .linearRegressionBuilder((td, rd, b) -> {
                    GenericObjectPoolConfig<?> poolConfig = new GenericObjectPoolConfig<>();
                    poolConfig.setMaxTotal(numThread);
                    poolConfig.setMaxIdle(numThread);
                    poolConfig.setMinIdle(0);
                    poolConfig.setMaxWaitMillis(-1);
                    JedisPool jedisPool = new JedisPool(poolConfig, "localhost", 6379, 3000, "skrv587");
                    NetworkLengthWeightCalculator weightCalculator = new NetworkLengthWeightCalculator.NetworkLengthWeightCalculatorBuilder()
                            .jedisPool(jedisPool)
                            .bandwidthType(BandwidthType.ADAPTIVE)
                            .trainingDataSet(td)
                            .bandwidth(b)
                            .build();
                    return new WeightedRegression.WeightedRegressionBuilder<HaiNingRecord, HaiNingResultRecord>()
                            .resultDataSet(rd)
                            .weightCalculator(weightCalculator)
                            .numThread(numThread)
                            .build();
                })
                .bandwidths(bandwidths)
                .clazz(HaiNingResultRecord.class)
                .build();

        if (aic.pass()) {
            Map<Double, Double> resultMap = aic.getAicResults();
            for (Map.Entry<Double, Double> entry : resultMap.entrySet()) {
                log.info("{} : {}", entry.getKey(), entry.getValue());
            }
        }
    }
}
