package com.katus.data;

import com.katus.exception.DataException;
import com.katus.exception.InvalidParamException;
import lombok.Getter;
import lombok.Setter;

/**
 * @author SUN Katus
 * @version 1.0, 2021-10-31
 */
@Getter
@Setter
public class HaiNingProcessRecord implements Record {
    private long roadId;

    private double ci;
    private double battle;
    private double drinks;
    private double overspeed;
    private double reverse;
    private double signal;
    private double car;
    private double entertainment;
    private double food;
    private double traffic;

    private double lonX;
    private double latY;

    private double factor;

    public HaiNingProcessRecord(long roadId, double lonX, double latY, double factor) {
        this.roadId = roadId;
        this.lonX = lonX;
        this.latY = latY;
        this.factor = factor;
    }

    public HaiNingProcessRecord(String line) {
        String[] items = line.split(",");
        this.roadId = Long.parseLong(items[0]);
        this.ci = Double.parseDouble(items[1]);
        this.battle = Double.parseDouble(items[2]);
        this.drinks = Double.parseDouble(items[3]);
        this.overspeed = Double.parseDouble(items[4]);
        this.reverse = Double.parseDouble(items[5]);
        this.signal = Double.parseDouble(items[6]);
        this.car = Double.parseDouble(items[7]);
        this.entertainment = Double.parseDouble(items[8]);
        this.food = Double.parseDouble(items[9]);
        this.traffic = Double.parseDouble(items[10]);
        this.lonX = Double.parseDouble(items[11]);
        this.latY = Double.parseDouble(items[12]);
        this.factor = Double.parseDouble(items[13]);
    }

    @Override
    public double y() {
        return ci;
    }

    @Override
    public double x(int index) {
        if (index >= x().length || index < 0) {
            throw new InvalidParamException();
        }
        return x()[index];
    }

    @Override
    public double[] x() {
        return new double[]{battle, drinks, overspeed, reverse, signal, car, entertainment, food, traffic};
    }

    public void update() {
        ci *= factor;
        battle *= factor;
        drinks *= factor;
        overspeed *= factor;
        reverse *= factor;
        signal *= factor;
    }

    public void increase(String filename, double interval) {
        filename = filename.substring(filename.lastIndexOf("_") + 1, filename.lastIndexOf("."));
        switch (filename) {
            case "battle":
                battle += interval;
                break;
            case "drinks":
                drinks += interval;
                break;
            case "overspeed":
                overspeed += interval;
                break;
            case "reverse":
                reverse += interval;
                break;
            case "signal":
                signal += interval;
                break;
            case "car":
                car += interval;
                break;
            case "entertainment":
                entertainment += interval;
                break;
            case "food":
                food += interval;
                break;
            case "traffic":
                traffic += interval;
                break;
            default:
                throw new DataException();
        }
    }

    @Override
    public String toString() {
        return String.format("%d,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f", roadId, ci, battle, drinks, overspeed, reverse, signal, car, entertainment, food, traffic, lonX, latY, factor);
    }
}
