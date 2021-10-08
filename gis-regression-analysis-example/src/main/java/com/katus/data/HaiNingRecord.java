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
        double[] data = new double[items.length - 4];
        for (int i = 0; i < data.length; i++) {
            data[i] = Double.parseDouble(items[i+1]);
        }
        this.lonX = Double.parseDouble(items[items.length-3]);
        this.latY = Double.parseDouble(items[items.length-2]);
        return data;
    }
}
