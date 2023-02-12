package com.katus.demo;

import com.katus.common.io.FsManipulator;
import com.katus.common.io.FsManipulatorFactory;
import com.katus.common.io.LineIterator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author SUN Katus
 * @version 1.0, 2022-05-11
 */
@Slf4j
public class AdvancedProcessCost {
    private static final int NUM_THREAD = 16;
    private static final JedisPool JEDIS_POOL;
    private static final int DATA_SIZE = 15274;

    static {
        // 构建连接池配置
        GenericObjectPoolConfig<Jedis> poolConfig = new GenericObjectPoolConfig<>();
        // 最大活跃数
        poolConfig.setMaxTotal(NUM_THREAD);
        // 最大空闲数
        poolConfig.setMaxIdle(NUM_THREAD);
        // 最小空闲数
        poolConfig.setMinIdle(0);
        // 当连接池空了之后, 多久没获取到Jedis对象则超时
        poolConfig.setMaxWait(Duration.ofMinutes(1L));
        // 构建连接池时直接指定密码
        JEDIS_POOL = new JedisPool(poolConfig, "localhost", 6379, 3000, "skrv587");
    }

    public static void main(String[] args) throws IOException {
        log.info("init array...");
        String[] initArray = new String[DATA_SIZE];
        Arrays.fill(initArray, String.valueOf(Double.MAX_VALUE));
        log.info("init array is over");

        log.info("init redis...");
        ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREAD);
        int interval = DATA_SIZE / NUM_THREAD, start, end = 0;
        for (int i = 0; i < NUM_THREAD; i++) {
            start = end;
            if (i == NUM_THREAD - 1) {
                end = DATA_SIZE;
            } else {
                end += interval;
            }
            log.info("{}: {}-{}", i, start, end);
            executorService.submit(new RedisInit(JEDIS_POOL.getResource(), initArray, i, start, end));
        }
        executorService.shutdown();
        try {
            while (!executorService.awaitTermination(5, TimeUnit.MINUTES)) {
                log.info("redis inputting...");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("init redis is over");

        log.info("update redis...");
        FsManipulator fsManipulator = FsManipulatorFactory.create();
        LineIterator lineIterator = fsManipulator.getLineIterator(args[0]);
        Jedis jedis = JEDIS_POOL.getResource();
        long count = 0L, startLine = 1L;
        if (args.length > 1) {
            startLine = Long.parseLong(args[1]) + 1;
        }
        while (lineIterator.hasNext()) {
            if (count < startLine) {
                lineIterator.next();
                if (count % 10000 == 0) {
                    log.info("no updated {} items...", count);
                }
            } else {
                String line = lineIterator.next();
                String[] items = line.split(",");
                int originID = Integer.parseInt(items[1]);
                int destinationID = Integer.parseInt(items[2]);
                int db = (destinationID - 1) / interval;
                if (db > 15) db = 15;
                jedis.select(db);
                jedis.lset("cost-" + destinationID, originID - 1, items[3].trim());
                if (count % 10000 == 0) {
                    log.info("updated {} items...", count);
                }
            }
            count++;
        }
        jedis.close();
        lineIterator.close();
        log.info("update redis is over");
    }

    @Slf4j
    public static class RedisInit implements Runnable {
        private final Jedis jedis;
        private final String[] values;
        private final Integer start, end;

        public RedisInit(Jedis jedis, String[] values, Integer index, Integer start, Integer end) {
            this.jedis = jedis;
            this.values = values;
            this.start = start;
            this.end = end;
            this.jedis.select(index);
        }

        @Override
        public void run() {
            for (int i = start; i < end; i++) {
                int destinationID = i + 1;
                String key = "cost-" + destinationID;
                try {
                    if (jedis.llen(key) != 15274L) {
                        jedis.del(key);
                        jedis.rpush(key, values);
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
