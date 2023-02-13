package com.katus.global;

import com.katus.data.DataSetInput;
import com.katus.data.JinHuaRecord;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author SUN Katus
 * @version 1.0, 2023-02-12
 */
public final class QueryUtil {
    /**
     * 图结构缓存(懒加载)
     */
    private static final Map<Long, List<Edge>> GRAPH;
    /**
     * 既是中心点又是图节点的ID集合
     */
    private static final Set<Long> BOTH_ID_SET;

    static {
        GRAPH = new ConcurrentHashMap<>();
        BOTH_ID_SET = new HashSet<>();
    }

    public static DataSetInput<JinHuaRecord> generateDataSetLoader(boolean isAll) {
        return () -> {
            String sql = "SELECT id, death_index, velocity, flow, ill_scramble_count, ill_behavior_count, " +
                    "ill_reverse_count, ill_overspeed_count, ill_signals_count, ill_others_count, " +
                    "poi_car_count, poi_entertainment_count, poi_food_count, poi_traffic_count, " +
                    "ST_X(point_geom) AS x, ST_Y(point_geom) AS y " +
                    "FROM analysis_units";
            if (!isAll) {
                sql += " WHERE death_index > 0";
            }
            List<JinHuaRecord> list = new ArrayList<>();
            try {
                Connection conn = GlobalConfig.PG_SOURCE.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    list.add(new JinHuaRecord(rs));
                }
                rs.close();
                stmt.close();
                conn.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return list;
        };
    }

    /**
     * 获取指定ID节点为起点的所有边
     */
    public static List<Edge> acquireAllEdges(long endId) throws SQLException {
        if (!GRAPH.containsKey(endId)) {
            List<Edge> list = new ArrayList<>();
            String sql = String.format("SELECT id, start_id, end_id, length, time, velocity, flow FROM graph_edges_jinhua WHERE end_id = %d", endId);
            Connection conn = GlobalConfig.PG_SOURCE.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                list.add(new Edge(rs));
            }
            rs.close();
            stmt.close();
            conn.close();
            GRAPH.put(endId, list);
        }
        return GRAPH.get(endId);
    }

    /**
     * 获取空间范围内的所有边ID
     */
    public static Set<Long> queryEdgeIdsWithinRange(long id, double radius) throws SQLException {
        Set<Long> idSet = new HashSet<>();
        String sql = String.format("WITH ip AS (SELECT gemo AS p FROM graph_nodes_jinhua WHERE id = %d)" +
                        " SELECT id FROM graph_edges_jinhua WHERE ST_Intersects(geom, ST_Buffer(ST_SetSRID(ip.p, %d), %f))",
                id, GlobalConfig.SRID_WGS84_UTM_50N, radius);
        Connection conn = GlobalConfig.PG_SOURCE.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            idSet.add(rs.getLong("id"));
        }
        rs.close();
        stmt.close();
        conn.close();
        return idSet;
    }

    /**
     * 获取空间范围内的所有节点ID
     */
    public static Map<Long, GraphNode> queryNodeIdsWithinRange(long id, double radius) throws SQLException {
        Map<Long, GraphNode> idMap = new LinkedHashMap<>();
        String sql = String.format("WITH ip AS (SELECT gemo AS p FROM graph_nodes_jinhua WHERE id = %d)" +
                        " SELECT id FROM graph_nodes_jinhua WHERE ST_Intersects(geom, ST_Buffer(ST_SetSRID(ip.p, %d), %f))",
                id, GlobalConfig.SRID_WGS84_UTM_50N, radius);
        Connection conn = GlobalConfig.PG_SOURCE.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            idMap.put(rs.getLong("id"), null);
        }
        rs.close();
        stmt.close();
        conn.close();
        return idMap;
    }

    public static void loadBothIds() throws SQLException {
        String sql = "SELECT id FROM nodes_f WHERE is_node IS TRUE AND is_center IS TRUE";
        BOTH_ID_SET.clear();
        Connection conn = GlobalConfig.PG_SOURCE.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            BOTH_ID_SET.add(rs.getLong("id"));
        }
        rs.close();
        stmt.close();
        conn.close();
    }

    public static boolean isRealNode(long id) {
        return id > GlobalConfig.CENTER_POINT_NUMBER || BOTH_ID_SET.contains(id);
    }
}
