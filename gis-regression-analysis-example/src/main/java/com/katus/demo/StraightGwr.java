package com.katus.demo;

import com.katus.data.HaiNingDataSet;
import com.katus.data.HaiNingRecord;
import com.katus.data.HaiNingResultDataSet;
import com.katus.data.HaiNingResultRecord;
import com.katus.exception.DataSetConvertException;
import com.katus.exception.InvalidParamException;
import com.katus.regression.linear.WeightedRegression;
import com.katus.regression.weight.BandwidthType;
import com.katus.regression.weight.StraightWeightCalculator;
import com.katus.test.r.GlobalRSquare;
import com.katus.test.r.LocalRSquare;
import lombok.extern.slf4j.Slf4j;

/**
 * @author SUN Katus
 * @version 1.0, 2021-11-06
 */
@Slf4j
public class StraightGwr {

    public static void main(String[] args) throws DataSetConvertException {
        BasicFunctions.firstConfig();
        if (args.length < 5) {
            log.error("args less than 5");
            throw new InvalidParamException();
        }
        String trainingFilename = args[0];
        String predictFilename = args[1];
        String resultFilename = args[2];
        double bandwidth = Double.parseDouble(args[3]);
        int numThread = Integer.parseInt(args[4]);

        HaiNingDataSet trainingDataSet = BasicFunctions.readHaiNingDataSet(trainingFilename);
        HaiNingDataSet tempDataSet = BasicFunctions.readHaiNingDataSet(predictFilename);
        HaiNingResultDataSet resultDataSet = tempDataSet.convertToResultDataSet(HaiNingResultRecord.class, HaiNingResultDataSet.class);

        StraightWeightCalculator weightCalculator = new StraightWeightCalculator.StraightWeightCalculatorBuilder()
                .bandwidthType(BandwidthType.ADAPTIVE)
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

        BasicFunctions.writeHaiNingResultDataSet(resultFilename, resultDataSet);
    }
}
