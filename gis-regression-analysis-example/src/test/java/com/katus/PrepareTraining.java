package com.katus;

import com.katus.common.io.FsManipulator;
import com.katus.common.io.FsManipulatorFactory;
import com.katus.common.io.LineIterator;
import com.katus.data.HaiNingProcessRecord;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author SUN Katus
 * @version 1.0, 2021-11-04
 */
@Slf4j
public class PrepareTraining {

    public static void main(String[] args) throws IOException {
        FsManipulator fsManipulator = FsManipulatorFactory.create();
        LineIterator lineIterator = fsManipulator.getLineIterator("F:\\data\\gis\\traffic\\tables\\var_join\\all_data50.csv");
        List<String> list = new ArrayList<>();
        while (lineIterator.hasNext()) {
            HaiNingProcessRecord record = new HaiNingProcessRecord(lineIterator.next());
            if (record.getCi() > 0.0 && record.getBattle() + record.getDrinks() + record.getOverspeed() + record.getReverse() + record.getSignal() + record.getCar()
                    + record.getEntertainment() + record.getFood() + record.getTraffic() > 0.0) {
                list.add(record.toString());
            }
        }
        log.info("{} lines has been select.", list.size());
        fsManipulator.writeTextToFile("F:\\data\\gis\\traffic\\tables\\var_join\\train_data50.csv", list);
    }
}
