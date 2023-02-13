package com.katus.exp;

import com.katus.data.JinHuaDataSet;
import com.katus.data.JinHuaRecord;
import com.katus.data.JinHuaResultDataSet;
import com.katus.data.JinHuaResultRecord;
import com.katus.demo.BasicFunctions;
import com.katus.exception.DataSetConvertException;
import com.katus.exception.InvalidParamException;
import com.katus.global.QueryUtil;
import com.katus.regression.linear.WeightedRegression;
import com.katus.regression.weight.BandwidthType;
import com.katus.regression.weight.NetworkTimeCostWeightCalculator;
import com.katus.test.r.GlobalRSquare;
import com.katus.test.r.LocalRSquare;
import lombok.extern.slf4j.Slf4j;

/**
 * @author SUN Katus
 * @version 1.0, 2023-02-12
 */
@Slf4j
public class TimeCostGwr {
    public static void main(String[] args) throws DataSetConvertException {
        BasicFunctions.firstConfig();
        if (args.length < 5) {
            log.error("args less than 5");
            throw new InvalidParamException();
        }
        String resultFilename = args[0];
        BandwidthType type = BandwidthType.valueOf(args[1]);
        double bandwidth = Double.parseDouble(args[2]);
        double distanceRadius = Double.parseDouble(args[3]);
        int numThread = Integer.parseInt(args[4]);

        log.info("--------GWR FOR TIME COST--------");
        log.info("Result Data: {}", resultFilename);
        log.info("Bandwidth: {} {}s", type, bandwidth);
        log.info("Distance Radius: {}m", distanceRadius);

        JinHuaDataSet trainingDataSet = new JinHuaDataSet(QueryUtil.generateDataSetLoader(false));
        JinHuaDataSet tempDataSet = new JinHuaDataSet(QueryUtil.generateDataSetLoader(true));
        JinHuaResultDataSet resultDataSet = tempDataSet.convertToResultDataSet(JinHuaResultRecord.class, JinHuaResultDataSet.class);

        NetworkTimeCostWeightCalculator weightCalculator = new NetworkTimeCostWeightCalculator.NetworkTimeCostWeightCalculatorBuilder()
                .distanceRadius(distanceRadius)
                .bandwidthType(type)
                .bandwidth(bandwidth)
                .trainingDataSet(trainingDataSet)
                .build();

        WeightedRegression<JinHuaRecord, JinHuaResultRecord> regression = new WeightedRegression.WeightedRegressionBuilder<JinHuaRecord, JinHuaResultRecord>()
                .resultDataSet(resultDataSet)
                .weightCalculator(weightCalculator)
                .numThread(numThread)
                .build();

        regression.train();
        regression.predict();

        GlobalRSquare rSquare = new GlobalRSquare(resultDataSet.yMatrix(), resultDataSet.predictions());
        log.info("Global R Square: {}", rSquare.getR2());

        LocalRSquare<JinHuaRecord, JinHuaResultRecord> localRSquare = new LocalRSquare.LocalRSquareBuilder<JinHuaRecord, JinHuaResultRecord>()
                .weightedRegression(regression)
                .numThread(numThread)
                .build();
        localRSquare.test();

        BasicFunctions.writeJinHuaResultDataSet(resultFilename, resultDataSet);
    }
}
