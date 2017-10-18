package com.lsh.payment.core.dao.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;


public abstract class JedisBaseDao {

    private static final Logger logger = LoggerFactory.getLogger(JedisBaseDao.class);

    @Resource(name = "jedisPool")
    private JedisPool jedisPool;


    public Jedis getJedis() {
        return jedisPool.getResource();
    }

    public Long deleteKey(String key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.del(key);
        } catch (Throwable e) {
            logger.error("deleteKey error", e);
            return 0L;
        } finally {
            try {
                if (null != jedis) {
                    jedis.close();
                }
            } catch (Throwable e) {
                logger.error("redis close 错误", e);
            }
        }
    }

    public Object eval(String script, int keyCount, String... params) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            return jedis.eval(script, keyCount, params);
        } catch (Throwable e) {
            logger.error("eval error", e);
            return 0L;
        } finally {
            try {
                if (null != jedis) {
                    jedis.close();
                }
            } catch (Throwable e) {
                logger.error("redis close 错误", e);
            }
        }
    }
}
