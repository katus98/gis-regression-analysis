package com.katus;

import com.katus.common.io.FsManipulator;
import com.katus.common.io.FsManipulatorFactory;
import com.katus.common.io.LineIterator;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author SUN Katus
 * @version 1.0, 2022-05-12
 */
@Slf4j
public class ProcessDetails {
    private static final Pattern NOT_DIGIT_PATTERN = Pattern.compile("\\D");
    private static final SimpleDateFormat DATE_FORMAT;
    private static final Date START_DATE, END_DATE;

    static {
        DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            START_DATE = DATE_FORMAT.parse("2018-01-01 00:00:00");
            END_DATE = DATE_FORMAT.parse("2020-12-31 23:59:59");
        } catch (ParseException e) {
            log.error("", e);
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException, ParseException {
        appendLossToAccidents(args);
        extractUseful(args);
    }

    private static void extractUseful(String[] args) throws IOException {
        log.info("extract useful info...");
        FsManipulator fsManipulator = FsManipulatorFactory.create();
        LineIterator lineIterator = fsManipulator.getLineIterator(args[2]);
        Writer writer = fsManipulator.writeAsText(args[3]);
        String line;
        lineIterator.next();
        writer.write("id,time,day,plates,lon,lat,death,injured,losses,crisis_index\n");
        while (lineIterator.hasNext()) {
            line = lineIterator.next();
            String[] items = line.split(",");
            if (items.length > 26 && items[1].compareTo("2018-01-01 00:00:00") >= 0 && items[1].compareTo("2020-12-31 23:59:59") <= 0) {
                double death = Math.max(Double.parseDouble(items[11]) + Double.parseDouble(items[12]) + Double.parseDouble(items[13]), Double.parseDouble(items[15]));
                double injured = Math.max(Double.parseDouble(items[10]), Double.parseDouble(items[14]));
                double losses = Double.parseDouble(items[29]);
                double ci = death + 0.035 * injured + 0.024 * losses / 10000;
                writer.write(items[0] + "," + items[1] + "," + items[2] + "," + items[3] + "," + items[25] + "," + items[26]
                + "," + death + "," + injured + "," + losses + "," + ci + "\n");
            }
        }
        writer.close();
        lineIterator.close();
        log.info("extract useful info is over");
    }

    private static void appendLossToAccidents(String[] args) throws IOException {
        log.info("build losses map...");
        Map<String, Double> resMap = new HashMap<>();
        FsManipulator fsManipulator = FsManipulatorFactory.create();
        LineIterator lineIterator = fsManipulator.getLineIterator(args[0]);
        String line;
        StringBuilder lineBuilder = new StringBuilder();
        while (lineIterator.hasNext()) {
            lineBuilder.delete(0, lineBuilder.length());
            do {
                line = lineIterator.next().trim();
                lineBuilder.append(line);
            } while (!line.endsWith("xxx"));
            line = lineBuilder.toString();
            String[] items = line.split(",");
            resMap.put(items[0], extractMoney(items[1] + items[2]));
        }
        lineIterator.close();
        log.info("build losses map is over");
        log.info("append losses to table...");
        lineIterator = fsManipulator.getLineIterator(args[1]);
        Writer writer = fsManipulator.writeAsText(args[2]);
        writer.write(lineIterator.next() + ",losses\n");
        while (lineIterator.hasNext()) {
            line = lineIterator.next();
            String id = line.split(",")[0];
            writer.write(line + "," + resMap.getOrDefault(id, 0.0) + "\n");
        }
        writer.close();
        lineIterator.close();
        log.info("append losses to table is over");
    }

    private static double extractMoney(String detail) {
        double result = 500.0;
        String line;
        while (detail.contains("元")) {
            int index = detail.indexOf("元");
            line = detail.substring(Math.max(0, index - 10), index);
            Matcher matcher = NOT_DIGIT_PATTERN.matcher(line);
            String numStr = matcher.replaceAll("").trim();
            if (!numStr.isEmpty()) {
                if (line.contains("万元")) {
                    result = Math.max(result, Double.parseDouble(numStr) * 10000) + 500;
                } else {
                    result = Math.max(result, Double.parseDouble(numStr)) + 500;
                }
            }
            detail = detail.substring(index + 1);
        }
        return result;
    }
}
