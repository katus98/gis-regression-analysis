package com.katus.demo;

import com.katus.common.io.FsManipulator;
import com.katus.common.io.FsManipulatorFactory;
import com.katus.common.io.LineIterator;
import com.katus.data.*;
import com.katus.exception.DataException;
import com.katus.global.QueryUtil;
import lombok.extern.slf4j.Slf4j;
import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.factory.Nd4j;

import java.io.IOException;
import java.sql.SQLException;
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
        try {
            QueryUtil.loadBothIds();
        } catch (SQLException e) {
            log.error("LOAD IDS ERROR", e);
            throw new RuntimeException(e);
        }
    }

    public static HaiNingDataSet readHaiNingDataSet(String filename) {
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

    public static void writeHaiNingResultDataSet(String filename, HaiNingResultDataSet resultDataSet) {
        resultDataSet.output(list -> {
            List<String> lines = list.stream().map(HaiNingResultRecord::put).collect(Collectors.toList());
            FsManipulator fsManipulator = FsManipulatorFactory.create();
            lines.add(0, "id,ci,battle,drinks,reverse,signal,car,entertainment,food,traffic,ci_pre," +
                    "beta_0,beta_battle,beta_drinks,beta_reverse,beta_signal,beta_car,beta_entertainment,beta_food," +
                    "beta_traffic,r_square,lon_x,lat_y");
            try {
                fsManipulator.writeTextToFile(filename, lines);
            } catch (IOException e) {
                log.error("write result dataset error!", e);
                throw new DataException();
            }
        });
    }

    public static void writeJinHuaResultDataSet(String filename, JinHuaResultDataSet resultDataSet) {
        resultDataSet.output(list -> {
            List<String> contents = new ArrayList<>();
            contents.add("id,death_index,velocity,flow,ill_scramble_count,ill_behavior_count,ill_reverse_count," +
                    "ill_overspeed_count,ill_signals_count,ill_others_count,poi_car_count,poi_entertainment_count," +
                    "poi_food_count,poi_traffic_count,death_index_pre,beta_0,beta_velocity,beta_flow,beta_ill_scramble_count," +
                    "beta_ill_behavior_count,beta_ill_reverse_count,beta_ill_overspeed_count,beta_ill_signals_count,beta_ill_others_count," +
                    "beta_poi_car_count,beta_poi_entertainment_count,beta_poi_food_count,beta_poi_traffic_count,r_square,lon_x,lat_y");
            contents.addAll(list.stream().map(JinHuaResultRecord::put).collect(Collectors.toList()));
            FsManipulator fsManipulator = FsManipulatorFactory.create();
            try {
                fsManipulator.writeTextToFile(filename, contents);
            } catch (IOException e) {
                log.error("write result dataset error!", e);
                throw new DataException();
            }
        });
    }
}
