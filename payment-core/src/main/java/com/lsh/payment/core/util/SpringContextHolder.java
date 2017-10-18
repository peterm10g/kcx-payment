package com.lsh.payment.core.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Lazy(value=false)
public class SpringContextHolder implements ApplicationContextAware{

	public static ApplicationContext context;
	
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		SpringContextHolder.context = applicationContext;
	}

	public ApplicationContext getContext(){
		return context;
	}
	
	public static <T>T getBean(String beanName){
		return (T)context.getBean(beanName);
	}
	
	public static boolean containBean(String beanName) {
		return context.containsBean(beanName);
	}
}
