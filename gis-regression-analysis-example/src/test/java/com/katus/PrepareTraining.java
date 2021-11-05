package com.katus;

import com.katus.common.io.FsManipulator;
import com.katus.common.io.FsManipulatorFactory;
import com.katus.common.io.LineIterator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author SUN Katus
 * @version 1.0, 2021-11-04
 */
public class PrepareTraining {

    public static void main(String[] args) throws IOException {
        FsManipulator fsManipulator = FsManipulatorFactory.create();
        LineIterator lineIterator = fsManipulator.getLineIterator("F:\\data\\gis\\traffic\\tables\\train\\all.csv");
        int count = 0;
        List<String> list = new ArrayList<>();
        while (lineIterator.hasNext()) {
            String line = lineIterator.next();
            if ((count++) % 3 == 0) {
                list.add(line);
            }
        }
        fsManipulator.writeTextToFile("F:\\data\\gis\\traffic\\tables\\train\\train.csv", list);
    }
}
