package com.katus;

import com.katus.common.io.FsManipulator;
import com.katus.common.io.FsManipulatorFactory;
import com.katus.common.util.Strings;
import com.katus.data.HaiNingOriginalRecord;
import com.katus.exception.InvalidParamException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author SUN Katus
 * @version 1.0, 2021-10-05
 */
public class PrepareData {

    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            throw new InvalidParamException();
        }
        String basePath = args[0];
        String targetFilename = args[1];
        String outputFilename = args[2];
        FsManipulator manipulator = FsManipulatorFactory.create();
        List<String> stringList = manipulator.readToLines(basePath + targetFilename);
        List<HaiNingOriginalRecord> dataList = new ArrayList<>();
        for (String line : stringList) {
            List<String> items = Strings.splitToList(line, ",");
            HaiNingOriginalRecord record = new HaiNingOriginalRecord();
            record.setRoadId(Long.parseLong(items.get(16)));
            record.setFactor(Double.parseDouble(items.get(17)));
            record.setLonX(Double.parseDouble(items.get(19)));
            record.setLatY(Double.parseDouble(items.get(20)));
            dataList.add(Integer.parseInt(items.get(16)) - 1, record);
        }
        for (int i = 3; i < args.length; i++) {
            String filename = args[i].substring(0, args[i].length() - 4);
            stringList = manipulator.readToLines(basePath + args[i]);
            for (String line : stringList) {
                int id = Integer.parseInt(Strings.splitToList(line, ",").get(39)) - 1;
                dataList.get(id).increase(filename);
            }
        }
        stringList.clear();
        for (HaiNingOriginalRecord record : dataList) {
            record.update();
            stringList.add(record.toString());
        }
        manipulator.writeTextToFile(basePath + outputFilename, stringList);
    }
}
