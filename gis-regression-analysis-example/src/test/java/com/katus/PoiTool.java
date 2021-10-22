package com.katus;

import com.katus.common.io.FsManipulator;
import com.katus.common.io.FsManipulatorFactory;
import com.katus.exception.InvalidParamException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author SUN Katus
 * @version 1.0, 2021-10-22
 */
public class PoiTool {

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            throw new InvalidParamException();
        }
        String inputFilename = args[0];
        String outputFilename = args[1];
        FsManipulator fsManipulator = FsManipulatorFactory.create();
        List<String> lineList = fsManipulator.readToLines(inputFilename);
        List<String> strList = new ArrayList<>(lineList.size());
        StringBuilder builder = new StringBuilder();
        for (String line : lineList) {
            String[] items = line.split(",");
            String kind = items[2].split(";")[0];
            builder.delete(0, builder.length());
            builder.append(items[0]).append(',').append(items[1]).append(',').append(kind);
            for (int i = 2; i < items.length; i++) {
                builder.append(',').append(items[i]);
            }
            strList.add(builder.toString());
        }
        fsManipulator.writeTextToFile(outputFilename, strList);
    }
}
