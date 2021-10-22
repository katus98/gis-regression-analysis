package com.katus;

import com.katus.common.io.FsManipulator;
import com.katus.common.io.FsManipulatorFactory;
import com.katus.common.util.Strings;
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
            List<String> itemList = Strings.splitToList(line, ",");
            String kind = itemList.get(2).split(";")[0];
            itemList.add(2, kind);
            builder.delete(0, builder.length());
            builder.append(itemList.get(0));
            for (int i = 1; i < itemList.size(); i++) {
                builder.append(',').append(itemList.get(i));
            }
            strList.add(builder.toString());
        }
        fsManipulator.writeTextToFile(outputFilename, strList);
    }
}
