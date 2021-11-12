package com.katus;

import com.katus.common.io.FsManipulator;
import com.katus.common.io.FsManipulatorFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author SUN Katus
 * @version 1.0, 2021-11-12
 */
public class ExtractTrainingDataSet {

    public static void main(String[] args) throws IOException {
        FsManipulator fsManipulator = FsManipulatorFactory.create();
        List<String> lines = fsManipulator.readToLines("F:\\data\\gis\\traffic\\tables\\train\\all.csv");
        List<String> trainingLines = new ArrayList<>();
        for (String line : lines) {
            String[] items = line.split(",");
            if (items[1].equals("0.000000")) {
                continue;
            }
            int count = 0;
            for (int i = 2; i < 11; i++) {
                if (!items[i].equals("0.000000")) {
                    count++;
                }
            }
            if (count >= 2) {
                trainingLines.add(line);
            }
        }
        fsManipulator.writeTextToFile("F:\\data\\gis\\traffic\\tables\\train\\train2.csv", trainingLines);
    }
}
