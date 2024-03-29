package com.katus;

import com.katus.common.io.FsManipulator;
import com.katus.common.io.FsManipulatorFactory;
import com.katus.data.HaiNingProcessRecord;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author SUN Katus
 * @version 1.0, 2021-10-31
 */
public class ProcessData {

    public static void main(String[] args) throws IOException {
        prepareAllData(args);
    }

    /*
    F:\data\gis\traffic\tables\var_join\join_battle.txt
    F:\data\gis\traffic\tables\var_join\join_drinks.txt
    F:\data\gis\traffic\tables\var_join\join_overspeed.txt
    F:\data\gis\traffic\tables\var_join\join_reverse.txt
    F:\data\gis\traffic\tables\var_join\join_signal.txt
    F:\data\gis\traffic\tables\var_join\join_car.txt
    F:\data\gis\traffic\tables\var_join\join_entertainment.txt
    F:\data\gis\traffic\tables\var_join\join_food.txt
    F:\data\gis\traffic\tables\var_join\join_traffic.txt
     */
    public static void prepareAllData(String[] paths) throws IOException {
        FsManipulator fsManipulator = FsManipulatorFactory.create();
        List<String> lines = fsManipulator.readToLines("F:\\data\\gis\\traffic\\tables\\var_join\\base.txt");
        List<HaiNingProcessRecord> records = new ArrayList<>(15274);
        for (String line : lines) {
            String[] items = line.split(",");
            records.add(new HaiNingProcessRecord(Long.parseLong(items[2]), Double.parseDouble(items[3]), Double.parseDouble(items[4]), 250.0 / Double.parseDouble(items[1])));
        }
        lines = fsManipulator.readToLines("F:\\data\\gis\\traffic\\tables\\var_join\\join_accident_use50.txt");
        for (String line : lines) {
            String[] items = line.split(",");
            HaiNingProcessRecord record = records.get(Integer.parseInt(items[3]) - 1);
            record.setCi(record.getCi() + Double.parseDouble(items[1]));
        }
        for (String path : paths) {
            lines = fsManipulator.readToLines(path);
            for (String line : lines) {
                String[] items = line.split(",");
                HaiNingProcessRecord record = records.get(Integer.parseInt(items[1]) - 1);
                record.increase(path, 1.0);
            }
        }
        for (HaiNingProcessRecord record : records) {
            record.update();
        }
        fsManipulator.writeTextToFile("F:\\data\\gis\\traffic\\tables\\var_join\\all_data50.csv", records.stream().map(Objects::toString).collect(Collectors.toList()));
    }
}
