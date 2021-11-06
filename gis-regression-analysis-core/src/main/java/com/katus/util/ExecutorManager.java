package com.katus.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author SUN Katus
 * @version 1.0, 2021-11-06
 */
public final class ExecutorManager {
    private static final Logger logger = LoggerFactory.getLogger(ExecutorManager.class);

    public static void waitingForFinish(ExecutorService es, String doing) {
        es.shutdown();
        try {
            while (!es.awaitTermination(60, TimeUnit.SECONDS)) {
                logger.info(doing + "...");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info(doing + " is over!");
    }
}
