package com.lsh.payment.core.service.RedisService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Project Name: lsh-payment
 * Created by peter on 16/12/13.
 * 北京链商电子商务有限公司
 * Package
 * desc:
 */
@Service
public class RedisLockService {

    private static Logger logger = LoggerFactory.getLogger(RedisLockService.class);

    @Resource(name = "redisTemplate_w")
    private RedisTemplate<String, String> valOp_w;


    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Value("${pay.redisLockFlag}")
    private String redisLockFlag;

    @Value("${pay.redisLockRepiry}")
    private Long redisLockRepiry;

    private static String REDIS_PAY_LOCK = "lock";


    /**
     * @param key   redis key
     * @param value 值
     * @return boolean
     */
    public synchronized boolean lock(final String key, final String value) {

        //开关标记
        if (!redisLockFlag.equals(REDIS_PAY_LOCK)) {
            return true;
        }

        boolean redisLock;
        try {
            redisLock = this.setNX_EX(key, value);
        } catch (Exception e) {
            logger.info("redis lock 操作异常");
            redisLock = true;
        }

        return redisLock;
    }

    /**
     * 释放redis锁
     *
     * @param lockKey 锁主键
     */
    public synchronized void unlock(String lockKey) {

        //开关标记
        if (!redisLockFlag.equals(REDIS_PAY_LOCK)) {
            return;
        }

        try {
            if (redisTemplate.hasKey(lockKey)) {
                redisTemplate.delete(lockKey);
            }
        } catch (Exception e) {
            logger.info("redis lock 操作异常 ");
        }

    }


    /**
     * 设置redis 锁值
     *
     * @param key   redis锁key
     * @param value 锁value
     * @return 是否获得锁
     */
    private boolean setNX_EX(final String key, final String value) {
        Object obj = null;
        try {
            obj = redisTemplate.execute(new RedisCallback<Object>() {
                @Override
                public Object doInRedis(RedisConnection connection) throws DataAccessException {
                    StringRedisSerializer serializer = new StringRedisSerializer();
                    Boolean success = connection.setNX(serializer.serialize(key), serializer.serialize(value));
                    if (success) {
                        connection.expire(serializer.serialize(key), redisLockRepiry);
                    }
                    connection.close();
                    return success;
                }
            });
        } catch (Exception e) {
            logger.error("setNX redis error, key : " + key, e);
        }
        return obj != null ? (Boolean) obj : true;
    }


//    /**
//     * 锁实现逻辑
//     * @param key   redis锁
//     * @param value redis 锁value
//     * @return      是否获得锁
//     */
//    private boolean setNX(final String key, final String value) {
//        boolean flag = false;
//        try {
//            final byte[] lockKeyByte = redisTemplate.getStringSerializer().serialize(key);
//            final byte[] valueByte = new JdkSerializationRedisSerializer().serialize(value);
//            logger.info("getConnectionFactory().getConnection() start");
//            RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
//            logger.info("getConnectionFactory().getConnection() end");
////            connection.multi();
//
//            flag = connection.setNX(lockKeyByte, valueByte);
//            if (flag) {
//                flag = connection.expire(lockKeyByte, redisLockRepiry);
//            }
////            connection.exec();
//            connection.close();
//        } catch (Exception e) {
//            logger.error("setNX redis error, key : {}", key);
//        }
//        return flag;
//    }


    public void pipelineSample(final List<String> lockKeys) {

        RedisCallback<List<Object>> pipelineCallback = new RedisCallback<List<Object>>() {
            @Override
            public List<Object> doInRedis(RedisConnection connection) throws DataAccessException {

//                connection.multi();//加了事务或变慢
                for (String key : lockKeys) {
                    byte[] lockKeyByte = new StringRedisSerializer().serialize(key);
                    connection.get(lockKeyByte);
                }

//                connection.exec();
                return null;
            }
        };


        List<Object> results = valOp_w.executePipelined(pipelineCallback);
        for (Object item : results) {

            logger.info("redis value is {}", item);
        }

    }


}
