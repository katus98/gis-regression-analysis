package com.katus.data;

import com.katus.exception.InvalidParamException;
import lombok.Getter;
import lombok.Setter;

/**
 * @author SUN Katus
 * @version 1.0, 2021-10-05
 */
@Getter
@Setter
public class HaiNingOriginalRecord implements Record {
    private long roadId;

    private double di;
    private double big;
    private double gc;
    private double gd;
    private double middle;
    private double none;
    private double small;
    private double special;
    private double xz;

    private double lonX;
    private double latY;

    private double factor;

    public void increase(String filename) {
        switch (filename) {
            case "di":
                di++;
                break;
            case "big":
                big++;
                break;
            case "gc":
                gc++;
                break;
            case "gd":
                gd++;
                break;
            case "middle":
                middle++;
                break;
            case "none":
                none++;
                break;
            case "small":
                small++;
                break;
            case "special":
                special++;
                break;
            case "xz":
                xz++;
                break;
            default:
                throw new InvalidParamException("No such X!");
        }
    }

    @Override
    public double getY() {
        return di;
    }

    @Override
    public double getX(int index) {
        return getX()[index];
    }

    @Override
    public double[] getX() {
        return new double[]{big, gc, gd, middle, none, small, special, xz};
    }

    public void update() {
        di = di * factor;
        big = big * factor;
        gc = gc * factor;
        gd = gd * factor;
        middle = middle * factor;
        none = none * factor;
        small = small * factor;
        special = special * factor;
        xz = xz * factor;
    }

    @Override
    public String toString() {
        return String.format("%d,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f", roadId, di, big, gc, gd, middle, none, small, special, xz, lonX, latY, factor);
    }
}
