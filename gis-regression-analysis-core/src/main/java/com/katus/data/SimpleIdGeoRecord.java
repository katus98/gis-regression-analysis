package com.katus.data;

import com.katus.exception.InvalidParamException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author SUN Katus
 * @version 1.0, 2021-10-08
 */
public class SimpleIdGeoRecord extends SimpleIdRecord {
    private static final Logger logger = LoggerFactory.getLogger(SimpleIdGeoRecord.class);

    protected double lonX;
    protected double latY;

    public SimpleIdGeoRecord(String str, String sep) {
        super(str, sep);
    }

    public double getLonX() {
        return lonX;
    }

    public void setLonX(double lonX) {
        this.lonX = lonX;
    }

    public double getLatY() {
        return latY;
    }

    public void setLatY(double latY) {
        this.latY = latY;
    }

    @Override
    public double[] load(String s) {
        String[] items = s.split(SEPARATOR);
        if (items.length < 4) {
            logger.error("item length is too short");
            throw new InvalidParamException();
        }
        setId(Long.valueOf(items[0]));
        double[] data = new double[items.length - 3];
        for (int i = 0; i < data.length; i++) {
            data[i] = Double.parseDouble(items[i+1]);
        }
        this.lonX = Double.parseDouble(items[items.length-2]);
        this.latY = Double.parseDouble(items[items.length-1]);
        return data;
    }

    @Override
    public String put() {
        return super.put() + SEPARATOR + lonX + SEPARATOR + latY;
    }
}
