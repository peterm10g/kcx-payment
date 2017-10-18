package com.lsh.payment.core.service.RedisService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Project Name: lsh-payment
 * Created by miaozhuang
 * Date: 16/9/18
 * 北京链商电子商务有限公司
 * Package com.lsh.payment.core.service.hold.
 * desc:预占接口业务层
 */
@Service
public class RedisBaseService<T> {
	
	@Resource
	private RedisTemplate<String, T> redisTemplate;
	
	private Log log = LogFactory.getLog(this.getClass());
	
	public int set(T Object,String rediskey){
		
		BoundValueOperations<String,T> redis = redisTemplate.boundValueOps(rediskey);
		
		redis.set(Object);
		
		log.info("---------> add redis data");
		
		return 1;
	}
	
	public T get(String redisKey){
		
		BoundValueOperations<String,T> redis = redisTemplate.boundValueOps(redisKey);
		
		//log.info("---------> get redis data");
		
		return redis.get();
	}

	public int delete(String rediskey){

		redisTemplate.delete(rediskey);

		log.info("---------> delete redis data key " + rediskey);

		return 1;
	}

}
