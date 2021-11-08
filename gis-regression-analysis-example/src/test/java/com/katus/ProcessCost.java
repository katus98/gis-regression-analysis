package com.katus;

import com.katus.common.io.FsManipulator;
import com.katus.common.io.FsManipulatorFactory;
import com.katus.common.io.LineIterator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author SUN Katus
 * @version 1.0, 2021-11-01
 */
@Slf4j
public class ProcessCost {
    private static final int NUM_THREAD = 16;
    private static final JedisPool JEDIS_POOL;
    private static final Map<Integer, double[]> costMap = new LinkedHashMap<>();

    static {
        // 构建连接池配置
        GenericObjectPoolConfig<?> poolConfig = new GenericObjectPoolConfig<>();
        // 最大活跃数
        poolConfig.setMaxTotal(NUM_THREAD);
        // 最大空闲数
        poolConfig.setMaxIdle(NUM_THREAD);
        // 最小空闲数
        poolConfig.setMinIdle(0);
        // 当连接池空了之后, 多久没获取到Jedis对象则超时
        poolConfig.setMaxWaitMillis(-1);
        // 构建连接池时直接指定密码
        JEDIS_POOL = new JedisPool(poolConfig, "10.79.231.85", 6379, 3000, "skrv587");
    }

    public static void main(String[] args) throws IOException {
        final int dataSize = 15274;
        FsManipulator fsManipulator = FsManipulatorFactory.create();
        LineIterator lineIterator = fsManipulator.getLineIterator("F:\\data\\gis\\traffic\\tables\\od\\length.txt");
        for (int i = 1; i <= dataSize; i++) {
            double[] doubles = new double[dataSize];
            Arrays.fill(doubles, Double.MAX_VALUE);
            costMap.put(i, doubles);
        }
        boolean flag = false;
        while (lineIterator.hasNext()) {
            if (!flag) {
                flag = true;
                lineIterator.next();
                continue;
            }
            String line = lineIterator.next();
            String[] items = line.split(",");
            int originID = Integer.parseInt(items[1]);
            Integer destinationID = Integer.valueOf(items[2]);
            double cost = Double.parseDouble(items[3]);
            double[] costs = costMap.get(destinationID);
            costs[originID - 1] = cost;
        }
        lineIterator.close();
        log.info("data preparation is over");

        ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREAD);
        int interval = dataSize / NUM_THREAD, start, end = 0;
        for (int i = 0; i < NUM_THREAD; i++) {
            start = end;
            if (i == NUM_THREAD - 1) {
                end = dataSize;
            } else {
                end += interval;
            }
            log.info("{}: {}-{}", i, start, end);
            executorService.submit(new RedisInput(JEDIS_POOL.getResource(), costMap, i, start, end));
        }
        executorService.shutdown();
        try {
            while (!executorService.awaitTermination(5, TimeUnit.MINUTES)) {
                log.info("redis inputting...");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("input redis is over");
    }

    @Slf4j
    public static class RedisInput implements Runnable {
        private final Jedis jedis;
        private final Map<Integer, double[]> costMap;
        private final Integer start, end;

        public RedisInput(Jedis jedis, Map<Integer, double[]> costMap, Integer index, Integer start, Integer end) {
            this.jedis = jedis;
            this.costMap = costMap;
            this.start = start;
            this.end = end;
            this.jedis.select(index);
        }

        @Override
        public void run() {
            for (int i = start; i < end; i++) {
                Integer destinationID = i + 1;
                String key = "length-" + destinationID;
                try {
                    if (jedis.llen(key) != 15274L) {
                        jedis.del(key);
                        double[] costs = costMap.get(destinationID);
                        for (double cost : costs) {
                            jedis.rpush(key, String.valueOf(cost));
                        }
                    }
                } catch (Exception e) {
                    log.error("Failed", e);
                    log.error("Thread {}-{} failed the {} item", start, end, i);
                }
                if ((i - start + 1) % 20 == 0) {
                    log.trace("Thread {}-{} finished {} items", start, end, i - start + 1);
                }
            }
            jedis.close();
            log.debug("Thread {}-{} is over", start, end);
        }
    }
}
