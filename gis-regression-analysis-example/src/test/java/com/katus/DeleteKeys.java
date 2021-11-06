package com.katus;

import redis.clients.jedis.Jedis;

import java.util.Set;

/**
 * @author SUN Katus
 * @version 1.0, 2021-11-06
 */
public class DeleteKeys {

    public static void main(String[] args) {
        Jedis jedis = new Jedis("10.79.231.86", 6379);
        jedis.auth("skrv587");
        for (int i = 0; i < 16; i++) {
            jedis.select(i);
            Set<String> keys = jedis.keys("length-*");
            for (String key : keys) {
                jedis.del(key);
            }
        }
        jedis.close();
    }
}
