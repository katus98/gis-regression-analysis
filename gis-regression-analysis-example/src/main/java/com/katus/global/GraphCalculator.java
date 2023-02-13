package com.katus.global;

import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 基于 Dijkstra 最短路径算法的图计算器
 * * 指定终点计算起点距离
 *
 * @author SUN Katus
 * @version 1.0, 2022-12-13
 */
@Slf4j
public class GraphCalculator {
    private static final double MIN_COST = 0.1;
    /**
     * 图节点计算数据结构
     */
    private Map<Long, GraphNode> nodeGraphMap;
    /**
     * 范围内的边ID集合
     */
    private Set<Long> edgeIds;
    /**
     * 起点匹配点
     */
    private long endPointId;
    /**
     * 图范围半径
     */
    private double distanceRadius;
    /**
     * 图计算是否完成
     */
    private volatile boolean isFinished;

    public GraphCalculator(long endPointId, double distanceRadius) throws SQLException {
        this.endPointId = endPointId;
        this.distanceRadius = distanceRadius;
        this.nodeGraphMap = QueryUtil.queryNodeIdsWithinRange(endPointId, distanceRadius);
        this.edgeIds = QueryUtil.queryEdgeIdsWithinRange(endPointId, distanceRadius);
        // 将起始位置的末端点加入图, 防止图结构完全缺失导致的空指针异常
        this.nodeGraphMap.put(endPointId, null);
        this.isFinished = false;
    }

    public void setEndPointIdAndDistanceRadius(long endPointId, double distanceRadius) throws SQLException {
        if (this.endPointId != endPointId || this.distanceRadius != distanceRadius) {
            this.isFinished = false;
            this.endPointId = endPointId;
            this.distanceRadius = distanceRadius;
            this.nodeGraphMap = QueryUtil.queryNodeIdsWithinRange(endPointId, distanceRadius);
            this.edgeIds = QueryUtil.queryEdgeIdsWithinRange(endPointId, distanceRadius);
            // 将起始位置的末端点加入图, 防止图结构完全缺失导致的空指针异常
            this.nodeGraphMap.put(endPointId, null);
        }
    }

    public void setEndPointId(long endPointId) throws SQLException {
        setEndPointIdAndDistanceRadius(endPointId, distanceRadius);
    }

    public void setDistanceRadius(double distanceRadius) throws SQLException {
        setEndPointIdAndDistanceRadius(endPointId, distanceRadius);
    }

    /**
     * 计算最短路径长度
     */
    public double computeCost(long startId) throws SQLException {
        return canArrived(startId) ? nodeGraphMap.get(startId).getCumulativeCost() : GlobalConfig.MAX_COST;
    }

    /**
     * 终点是否可以到达
     */
    public boolean canArrived(long startId) throws SQLException {
        buildNodeGraphMap();
        return nodeGraphMap.containsKey(startId) && nodeGraphMap.get(startId).isVisited();
    }

    private void initNodeGraphMap() {
        for (Map.Entry<Long, GraphNode> entry : nodeGraphMap.entrySet()) {
            entry.setValue(new GraphNode(entry.getKey()));
        }
    }

    private void buildNodeGraphMap() throws SQLException {
        if (!isFinished) {
            synchronized (this) {
                if (!isFinished) {
                    if (endPointId == -1L) {
                        log.error("HAVE NOT Assigned the START Point");
                        throw new RuntimeException();
                    }
                    // 初始化数据结构
                    initNodeGraphMap();
                    // 起点是上一个匹配点所在边的终点
                    long lastEndId = -1L;
                    long endId = endPointId;
                    // 设置起点信息
                    GraphNode startGraphNode = nodeGraphMap.get(endId);
                    startGraphNode.setCumulativeCost(MIN_COST);
                    startGraphNode.setVisited(true);
                    // 获取从起点开始的所有边
                    List<Edge> edges = QueryUtil.acquireAllEdges(endId);
                    boolean isRealNode = QueryUtil.isRealNode(endId);
                    // 初始化直接与起点相连的信息
                    for (Edge edge : edges) {
                        long startId = edge.getStartId();
                        // 仅处理范围内的边和节点
                        if (edgeIds.contains(edge.getId()) && nodeGraphMap.containsKey(startId)) {
                            GraphNode graphNode = nodeGraphMap.get(startId);
                            if (isRealNode || lastEndId != startId) {
                                graphNode.setCumulativeCost(edge.cost() + startGraphNode.getCumulativeCost());
                                graphNode.setPreviousNodeId(endId);
                            }
                        }
                    }
                    // Dijkstra 最短路径算法
                    int n = nodeGraphMap.size();
                    for (int i = 1; i < n; i++) {
                        double minCost = GlobalConfig.MAX_COST;
                        for (Map.Entry<Long, GraphNode> entry : nodeGraphMap.entrySet()) {
                            long nodeId = entry.getKey();
                            GraphNode graphNode = entry.getValue();
                            if (!graphNode.isVisited() && graphNode.getCumulativeCost() < minCost) {
                                minCost = graphNode.getCumulativeCost();
                                endId = nodeId;
                            }
                        }
                        // 短路, 如果未访问节点中已经不存在可到达的节点则直接中断计算
                        if (minCost == GlobalConfig.MAX_COST) {
                            break;
                        }
                        nodeGraphMap.get(endId).setVisited(true);
                        lastEndId = nodeGraphMap.get(endId).getPreviousNodeId();
                        edges = QueryUtil.acquireAllEdges(endId);
                        isRealNode = QueryUtil.isRealNode(endId);
                        for (Edge edge : edges) {
                            long startId = edge.getStartId();
                            // 仅处理范围内的边和节点
                            if (edgeIds.contains(edge.getId()) && nodeGraphMap.containsKey(startId)) {
                                GraphNode graphNode = nodeGraphMap.get(startId);
                                if (isRealNode || lastEndId != startId) {
                                    double newCost = edge.cost() + nodeGraphMap.get(endId).getCumulativeCost();
                                    if (graphNode.getCumulativeCost() > newCost) {
                                        graphNode.setCumulativeCost(newCost);
                                        graphNode.setPreviousNodeId(endId);
                                    }
                                }
                            }
                        }
                    }
                    this.isFinished = true;
                }
            }
        }
    }
}
