package com.katus.demo;

import com.katus.common.io.FsManipulator;
import com.katus.common.io.FsManipulatorFactory;
import com.katus.common.io.LineIterator;
import com.katus.data.HaiNingDataSet;
import com.katus.data.HaiNingRecord;
import com.katus.data.HaiNingResultDataSet;
import com.katus.data.HaiNingResultRecord;
import com.katus.exception.DataSetConvertException;
import com.katus.regression.linear.WeightedRegression;
import com.katus.regression.weight.BandwidthType;
import com.katus.regression.weight.StraightWeightCalculator;
import com.katus.regression.weight.WeightCalculator;
import com.katus.test.aic.AIC;
import lombok.extern.slf4j.Slf4j;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.factory.Nd4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author SUN Katus
 * @version 1.0, 2021-11-04
 */
@Slf4j
public class HaiNingRegression {

    public static void main(String[] args) throws DataSetConvertException {
        Nd4j.setDefaultDataTypes(DataType.DOUBLE, DataType.DOUBLE);
        HaiNingDataSet trainDataSet = new HaiNingDataSet(() -> {
            FsManipulator fsManipulator = FsManipulatorFactory.create();
            List<HaiNingRecord> list = new ArrayList<>();
            try {
                LineIterator lineIterator = fsManipulator.getLineIterator("F:\\data\\gis\\traffic\\tables\\train\\train.csv");
                while (lineIterator.hasNext()) {
                    String line = lineIterator.next();
                    list.add(new HaiNingRecord(line, ","));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return list;
        });
        HaiNingDataSet tempDataSet = new HaiNingDataSet(() -> {
            FsManipulator fsManipulator = FsManipulatorFactory.create();
            List<HaiNingRecord> list = new ArrayList<>();
            try {
                LineIterator lineIterator = fsManipulator.getLineIterator("F:\\data\\gis\\traffic\\tables\\train\\all.csv");
                while (lineIterator.hasNext()) {
                    String line = lineIterator.next();
                    list.add(new HaiNingRecord(line, ","));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return list;
        });
        HaiNingResultDataSet resultDataSet = tempDataSet.convertToResultDataSet(HaiNingResultRecord.class, HaiNingResultDataSet.class);
        AIC<HaiNingRecord, HaiNingResultRecord> aic = new AIC.AICBuilder<HaiNingRecord, HaiNingResultRecord>()
                .testDataSet(trainDataSet)
                .linearRegressionBuilder((td, pd, b) -> {
                    WeightCalculator<HaiNingRecord> weightCalculator = new StraightWeightCalculator.StraightWeightCalculatorBuilder()
                            .bandwidthType(BandwidthType.ADAPTIVE)
                            .trainingDataSet(td)
                            .bandwidth(b)
                            .build();
                    return new WeightedRegression.WeightedRegressionBuilder<HaiNingRecord, HaiNingResultRecord>()
                            .trainingDataSet(td)
                            .predictDataSet(pd)
                            .weightCalculator(weightCalculator)
                            .numThread(16)
                            .build();
                })
                .bandwidths(5, 10, 15, 20, 25, 30, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 50)
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
