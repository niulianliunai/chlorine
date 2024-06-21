package com.chlorine.base.auth.aspect;

import com.chlorine.redis.annotation.RedisException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class RedisAspect {
    private static Logger LOGGER = LoggerFactory.getLogger(RedisAspect.class);

    @Pointcut("execution(public * com.cl.chlorine.service.*RedisService.*(..))")
    public void redisAspect() {}

    @Around("redisAspect()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method =  methodSignature.getMethod();
        Object result = null;
        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            //有RedisException注解的方法需要抛出异常
            if (method.isAnnotationPresent(RedisException.class)) {
                throw throwable;
            } else {
                LOGGER.error(throwable.getMessage());
            }
        }
        return result;
    }
}
