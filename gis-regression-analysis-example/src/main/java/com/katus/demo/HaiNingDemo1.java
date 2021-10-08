package com.katus.demo;

import com.katus.common.io.FsManipulator;
import com.katus.common.io.FsManipulatorFactory;
import com.katus.data.HaiNingDataSet;
import com.katus.data.HaiNingRecord;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author SUN Katus
 * @version 1.0, 2021-10-08
 */
public class HaiNingDemo1 {

    public static void main(String[] args) {
        HaiNingDataSet dataSet = new HaiNingDataSet(() -> {
            List<HaiNingRecord> list = new ArrayList<>();
            FsManipulator manipulator = FsManipulatorFactory.create();
            try {
                List<String> strList = manipulator.readToLines("F:\\data\\form\\traffic\\result.csv");
                for (String line : strList) {
                    list.add(new HaiNingRecord(line, ","));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return list;
        });
        INDArray xMatrix = dataSet.xMatrix();
        INDArray yMatrix = dataSet.yMatrix();
        System.out.println(xMatrix.getRows() + " " + xMatrix.getColumns());
    }
}
