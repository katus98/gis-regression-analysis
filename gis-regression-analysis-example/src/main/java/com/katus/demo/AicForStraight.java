package com.katus.demo;

import com.katus.data.HaiNingDataSet;
import com.katus.data.HaiNingRecord;
import com.katus.data.HaiNingResultRecord;
import com.katus.exception.DataSetConvertException;
import com.katus.exception.InvalidParamException;
import com.katus.regression.linear.WeightedRegression;
import com.katus.regression.weight.BandwidthType;
import com.katus.regression.weight.StraightWeightCalculator;
import com.katus.test.aic.AIC;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author SUN Katus
 * @version 1.0, 2021-11-04
 */
@Slf4j
public class AicForStraight {

    public static void main(String[] args) throws DataSetConvertException {
        BasicFunctions.firstConfig();
        if (args.length < 2) {
            log.error("args less than 2");
            throw new InvalidParamException();
        }
        String trainFilename = args[0];
        int numThread = Integer.parseInt(args[1]);

        HaiNingDataSet trainingDataSet = BasicFunctions.readDataSet(trainFilename);

        double[] bandwidths = new double[50];
        for (int i = 0; i < 50; i++) {
            bandwidths[i] = i + 1;
        }
        AIC<HaiNingRecord, HaiNingResultRecord> aic = new AIC.AICBuilder<HaiNingRecord, HaiNingResultRecord>()
                .testDataSet(trainingDataSet)
                .linearRegressionBuilder((td, rd, b) -> {
                    StraightWeightCalculator weightCalculator = new StraightWeightCalculator.StraightWeightCalculatorBuilder()
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
