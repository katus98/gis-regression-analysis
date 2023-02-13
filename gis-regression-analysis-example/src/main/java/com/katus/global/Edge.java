package com.katus.global;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author SUN Katus
 * @version 1.0, 2022-12-07
 */
@Getter
@Setter
@ToString
@Slf4j
public class Edge {
    private long id;
    private long startId;
    private long endId;
    private double length;
    private double time;
    private double velocity;
    private double flow;

    public Edge() {
    }

    public Edge(ResultSet rs) throws SQLException {
        this.id = rs.getLong("id");
        this.startId = rs.getLong("start_id");
        this.endId = rs.getLong("end_id");
        this.length = rs.getDouble("length");
        try {
            this.time = rs.getDouble("time");
            this.velocity = rs.getDouble("velocity");
            this.flow = rs.getDouble("flow");
        } catch (SQLException e) {
            log.debug("{}", "Not has some columns.");
        }
    }

    public double cost() {
        return time;
    }
}
