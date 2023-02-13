package com.katus.data;

import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author SUN Katus
 * @version 1.0, 2023-02-12
 */
@Slf4j
public class JinHuaRecord extends AbstractRecord<ResultSet> {
    protected long id;
    protected double lonX, latY;

    public JinHuaRecord(ResultSet resultSet) {
        super(resultSet);
    }

    @Override
    public double[] load(ResultSet resultSet) {
        double[] data = new double[13];
        try {
            this.id = resultSet.getLong("id");
            data[0] = resultSet.getDouble("death_index");
            data[1] = resultSet.getDouble("velocity");
            data[2] = resultSet.getDouble("flow");
            data[3] = resultSet.getDouble("ill_scramble_count");
            data[4] = resultSet.getDouble("ill_behavior_count");
            data[5] = resultSet.getDouble("ill_reverse_count");
            data[6] = resultSet.getDouble("ill_overspeed_count");
            data[7] = resultSet.getDouble("ill_signals_count");
            data[8] = resultSet.getDouble("ill_others_count");
            data[9] = resultSet.getDouble("poi_car_count");
            data[10] = resultSet.getDouble("poi_entertainment_count");
            data[11] = resultSet.getDouble("poi_food_count");
            data[12] = resultSet.getDouble("poi_traffic_count");
            this.lonX = resultSet.getDouble("x");
            this.latY = resultSet.getDouble("y");
        } catch (SQLException e) {
            log.info("INVALID INPUT", e);
            throw new RuntimeException(e);
        }
        return data;
    }

    @Override
    public String put() {
        StringBuilder builder = new StringBuilder();
        builder.append(id).append(",").append(y());
        for (int i = 0; i < xSize(); i++) {
            builder.append(",").append(x(i));
        }
        return builder.toString();
    }

    public long id() {
        return this.id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
}
