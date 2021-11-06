package com.katus.demo;

import com.katus.common.io.FsManipulator;
import com.katus.common.io.FsManipulatorFactory;
import com.katus.common.io.LineIterator;
import com.katus.data.HaiNingDataSet;
import com.katus.data.HaiNingRecord;
import com.katus.data.HaiNingResultDataSet;
import com.katus.data.HaiNingResultRecord;
import com.katus.exception.DataException;
import lombok.extern.slf4j.Slf4j;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.factory.Nd4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author SUN Katus
 * @version 1.0, 2021-11-06
 */
@Slf4j
public class BasicFunctions {

    public static void firstConfig() {
        Nd4j.setDefaultDataTypes(DataType.DOUBLE, DataType.DOUBLE);
    }

    public static HaiNingDataSet readDataSet(String filename) {
        return new HaiNingDataSet(() -> {
            FsManipulator fsManipulator = FsManipulatorFactory.create();
            List<HaiNingRecord> list = new ArrayList<>();
            try {
                LineIterator lineIterator = fsManipulator.getLineIterator(filename);
                while (lineIterator.hasNext()) {
                    String line = lineIterator.next();
                    list.add(new HaiNingRecord(line, ","));
                }
            } catch (IOException e) {
                log.error("read dataset error!", e);
                throw new DataException();
            }
            return list;
        });
    }

    public static void writeResultDataSet(String filename, HaiNingResultDataSet resultDataSet) {
        resultDataSet.output(list -> {
            List<String> lines = list.stream().map(HaiNingResultRecord::put).collect(Collectors.toList());
            FsManipulator fsManipulator = FsManipulatorFactory.create();
            try {
                fsManipulator.writeTextToFile(filename, lines);
            } catch (IOException e) {
                log.error("write result dataset error!", e);
                throw new DataException();
            }
        });
    }
}
