package com.katus.demo;

import com.katus.data.HaiNingDataSet;
import com.katus.data.HaiNingRecord;
import com.katus.data.HaiNingResultDataSet;
import com.katus.data.HaiNingResultRecord;
import com.katus.exception.DataSetConvertException;
import com.katus.exception.InvalidParamException;
import com.katus.regression.linear.WeightedRegression;
import com.katus.regression.weight.BandwidthType;
import com.katus.regression.weight.NetworkCostWeightCalculator;
import com.katus.test.r.GlobalRSquare;
import com.katus.test.r.LocalRSquare;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.time.Duration;

/**
 * @author SUN Katus
 * @version 1.0, 2021-11-06
 */
@Slf4j
public class CostGwr {

    public static void main(String[] args) throws DataSetConvertException {
        BasicFunctions.firstConfig();
        if (args.length < 5) {
            log.error("args less than 5");
            throw new InvalidParamException();
        }
        String trainingFilename = args[0];
        String predictFilename = args[1];
        String resultFilename = args[2];
        BandwidthType type = BandwidthType.valueOf(args[3]);
        double bandwidth = Double.parseDouble(args[4]);
        int numThread = Integer.parseInt(args[5]);

        log.info("--------GWR FOR COST--------");
        log.info("Training Data: {}", trainingFilename);
        log.info("Predict Data: {}", predictFilename);
        log.info("Result Data: {}", resultFilename);
        log.info("Bandwidth: {} {}", type, bandwidth);

        HaiNingDataSet trainingDataSet = BasicFunctions.readDataSet(trainingFilename);
        HaiNingDataSet tempDataSet = BasicFunctions.readDataSet(predictFilename);
        HaiNingResultDataSet resultDataSet = tempDataSet.convertToResultDataSet(HaiNingResultRecord.class, HaiNingResultDataSet.class);

        GenericObjectPoolConfig<Jedis> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxTotal(numThread);
        poolConfig.setMaxIdle(numThread);
        poolConfig.setMinIdle(0);
        poolConfig.setMaxWait(Duration.ofMinutes(1L));
        JedisPool jedisPool = new JedisPool(poolConfig, "localhost", 6379, 3000, "skrv587");

        NetworkCostWeightCalculator weightCalculator = new NetworkCostWeightCalculator.NetworkCostWeightCalculatorBuilder()
                .jedisPool(jedisPool)
                .bandwidthType(type)
                .bandwidth(bandwidth)
                .trainingDataSet(trainingDataSet)
                .build();

        WeightedRegression<HaiNingRecord, HaiNingResultRecord> regression = new WeightedRegression.WeightedRegressionBuilder<HaiNingRecord, HaiNingResultRecord>()
                .resultDataSet(resultDataSet)
                .weightCalculator(weightCalculator)
                .numThread(numThread)
                .build();
        regression.train();
        regression.predict();

        GlobalRSquare rSquare = new GlobalRSquare(resultDataSet.yMatrix(), resultDataSet.predictions());
        log.info("Global R Square: {}", rSquare.getR2());

        LocalRSquare<HaiNingRecord, HaiNingResultRecord> localRSquare = new LocalRSquare.LocalRSquareBuilder<HaiNingRecord, HaiNingResultRecord>()
                .weightedRegression(regression)
                .numThread(numThread)
                .build();
        localRSquare.test();

        BasicFunctions.writeResultDataSet(resultFilename, resultDataSet);
    }
}
