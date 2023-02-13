package com.katus.global;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

/**
 * @author SUN Katus
 * @version 1.0, 2023-02-12
 */
public final class GlobalConfig {
    public static final DataSource PG_SOURCE;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://td1:5432/graduate_katus");
        config.setDriverClassName("org.postgresql.Driver");
        config.setUsername("postgres");
        config.setPassword("tdzv587");
        config.setAutoCommit(true);
        config.setIdleTimeout(60000L);
        config.setConnectionTimeout(60000L);
        config.setMaxLifetime(0);
        config.setMinimumIdle(10);
        config.setMaximumPoolSize(10);
        PG_SOURCE = new HikariDataSource(config);
    }

    public static final double MAX_COST = Integer.MAX_VALUE;
    public static final int SRID_WGS84 = 4326, SRID_WGS84_UTM_50N = 32650;
    public static final int CENTER_POINT_NUMBER = 73715;
}
