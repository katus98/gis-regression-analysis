package com.katus.demo;

import com.katus.data.HaiNingDataSet;
import com.katus.data.HaiNingRecord;
import com.katus.data.HaiNingResultDataSet;
import com.katus.data.HaiNingResultRecord;
import com.katus.exception.DataSetConvertException;
import com.katus.exception.InvalidParamException;
import com.katus.regression.linear.MultipleLinearRegression;
import com.katus.test.r.GlobalRSquare;
import lombok.extern.slf4j.Slf4j;

/**
 * @author SUN Katus
 * @version 1.0, 2021-11-06
 */
@Slf4j
public class StraightOls {

    public static void main(String[] args) throws DataSetConvertException {
        BasicFunctions.firstConfig();
        if (args.length < 4) {
            log.error("args less than 4");
            throw new InvalidParamException();
        }
        String trainingFilename = args[0];
        String predictFilename = args[1];
        String resultFilename = args[2];
        int numThread = Integer.parseInt(args[3]);

        HaiNingDataSet trainingDataSet = BasicFunctions.readHaiNingDataSet(trainingFilename);
        HaiNingDataSet tempDataSet = BasicFunctions.readHaiNingDataSet(predictFilename);
        HaiNingResultDataSet resultDataSet = tempDataSet.convertToResultDataSet(HaiNingResultRecord.class, HaiNingResultDataSet.class);

        MultipleLinearRegression<HaiNingRecord, HaiNingResultRecord> regression = new MultipleLinearRegression.MultipleLinearRegressionBuilder<HaiNingRecord, HaiNingResultRecord>()
                .trainingDataSet(trainingDataSet)
                .resultDataSet(resultDataSet)
                .numThread(numThread)
                .build();
        regression.train();
        regression.predict();

        GlobalRSquare rSquare = new GlobalRSquare(resultDataSet.yMatrix(), resultDataSet.predictions());
        log.info("Global R Square: {}", rSquare.getR2());

        BasicFunctions.writeHaiNingResultDataSet(resultFilename, resultDataSet);
    }
}
