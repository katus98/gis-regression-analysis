package com.katus.data;

/**
 * @author SUN Katus
 * @version 1.0, 2021-10-07
 */
public class HaiNingRecord extends SimpleIdGeoRecord {

    public HaiNingRecord(String str, String sep) {
        super(str, sep);
    }

    @Override
    public double[] load(String s) {
        String[] items = s.split(SEPARATOR);
        setId(Long.valueOf(items[0]));
        double[] data = new double[]{
                Double.parseDouble(items[1]),
                Double.parseDouble(items[2]),
                Double.parseDouble(items[3]),
                Double.parseDouble(items[5]),
                Double.parseDouble(items[6]),
                Double.parseDouble(items[7]),
                Double.parseDouble(items[8]),
                Double.parseDouble(items[9]),
                Double.parseDouble(items[10])
        };
        setLonX(Double.parseDouble(items[items.length-3]));
        setLatY(Double.parseDouble(items[items.length-2]));
        return data;
    }
}
