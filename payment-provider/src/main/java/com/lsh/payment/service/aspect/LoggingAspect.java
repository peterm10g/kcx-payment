package com.lsh.payment.service.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * lsh-payment
 * Created by peter on 16/9/7.
 */
@Order(1)
@Aspect
@Component
public class LoggingAspect {

    private static Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    /**
     * 切入点表达式.
     */
    @Pointcut("execution(* com.lsh.payment.service.*.*Service*.*(..))")
    public void declareJointPointExpression(){}

    /**
     * 前置通知
     * @param joinPoint 连接点
     */
    @Before("declareJointPointExpression()")
    public void beforeMethod(JoinPoint joinPoint){
        String methodName = joinPoint.getSignature().getName();
        String thisclass = joinPoint.getTarget().getClass().getName();

        logger.info("The class is: [{}] ; the method is : [{}] start", thisclass, methodName);
    }

    /**
     * 后置通知
     * @param joinPoint 连接点
     */
    @After("declareJointPointExpression()")
    public void afterMethod(JoinPoint joinPoint){
        String methodName = joinPoint.getSignature().getName();
        String thisclass = joinPoint.getTarget().getClass().getName();
        logger.info("The class is: [{}] ; the method is : [{}] ends", thisclass, methodName);
    }

    /**
     * 环绕通知
     * @param joinPoint 连接点
     * @throws Throwable 异常
     */
    @Around("declareJointPointExpression()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {

        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().getName();
        Object result = joinPoint.proceed();
        logger.info( "{} runtime is : [{}] 毫秒", methodName, (System.currentTimeMillis() - startTime));

        return result;

    }

}
