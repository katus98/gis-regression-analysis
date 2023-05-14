package com.katus;

import com.katus.common.io.FsManipulator;
import com.katus.common.io.FsManipulatorFactory;
import com.katus.common.io.LineIterator;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author SUN Katus
 * @version 1.0, 2023-05-14
 */
@Slf4j
public class PreciseTest {
    public static void main(String[] args) throws IOException {
        FsManipulator fsManipulator = FsManipulatorFactory.create();
        String filename = "F:\\data\\graduation\\gwr\\ols_pre.csv";
        LineIterator it = fsManipulator.getLineIterator(filename);
        // OLS 2 15
        // GWR 1 14
        // N GWR 1 14
        int trueIndex = 2, preIndex = 15, count = 0;
        double totalError = 0.0, totalBias = 0.0, totalValue = 0.0;
        it.next();
        while (it.hasNext()) {
            String line = it.next();
            String[] items = line.split(",");
            double trueValue = Double.parseDouble(items[trueIndex]);
            double preValue = Double.parseDouble(items[preIndex]);
            if (trueValue > 0 && preValue > 0 && Math.abs(trueValue - preValue) / trueValue < 1000000) {
                count++;
                totalError += Math.abs(trueValue - preValue) / trueValue;
                totalBias += Math.pow(trueValue - preValue, 2);
                totalValue += trueValue;
                if (totalError / count > 100) {
                    log.error("{}", totalError);
                }
            }
        }
        double mre = totalError / count;
        double rmse = Math.sqrt(totalBias / count);
        double avg = totalValue / count;
        it = fsManipulator.getLineIterator(filename);
        it.next();
        count = 0;
        double totalSon = 0.0, totalMum = 0.0;
        while (it.hasNext()) {
            String line = it.next();
            String[] items = line.split(",");
            double trueValue = Double.parseDouble(items[trueIndex]);
            double preValue = Double.parseDouble(items[preIndex]);
            if (trueValue > 0 && preValue > 0 && Math.abs(trueValue - preValue) / trueValue < 1000000) {
                count++;
                totalSon += Math.pow(preValue - trueValue, 2);
                totalMum += Math.pow(trueValue - avg, 2);
            }
        }
        double r_2 = 1 - totalSon / totalMum;
        log.info("MRE: {}, RMSE: {}, AVG: {}, COUNT: {}, R^2: {}", mre, rmse, avg, count, r_2);
    }
}
