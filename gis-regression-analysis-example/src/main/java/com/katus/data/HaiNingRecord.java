package com.katus.data;

/**
 * @author SUN Katus
 * @version 1.0, 2021-10-07
 */
public class HaiNingRecord extends SimpleRecord {
    private double lon_x;
    private double lat_y;

    public HaiNingRecord(String str, String sep) {
        super(str, sep);
    }

    @Override
    public double[] load(String s) {
        // todo
        return super.load(s);
    }
}
