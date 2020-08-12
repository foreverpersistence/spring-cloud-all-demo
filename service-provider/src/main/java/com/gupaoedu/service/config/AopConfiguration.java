package com.gupaoedu.service.config;

import com.gupaoedu.service.annotation.Limited;
import com.gupaoedu.service.annotation.TimeOut;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Stream;

/**
 * @author fred
 * @date 2020/8/12 5:57 下午
 * @description aop
 */
@Aspect
@Configuration
public class AopConfiguration {


    private Semaphore semaphore = null;

    //一个 method 一个 Semaphore
    private Map<Method, Semaphore> semaphoreCache = new ConcurrentHashMap<>();

    @Around("@annotation(com.gupaoedu.service.annotation.Limited)")
    public Object beforeLimitedMehtodInvocation(ProceedingJoinPoint pjp) throws Throwable {

        Signature signature = pjp.getSignature();
        if (signature instanceof MethodSignature) {
            MethodSignature methodSignature = (MethodSignature) signature;
            Method method = methodSignature.getMethod();

//            //得到 注解
//            Limited limited = method.getAnnotation(Limited.class);
//            //得到 @Limited 的属性
//            int value = limited.value();
            // 根据属性构造 Semaphore
            Semaphore semaphore = getSemaphore(method);//initSemaphore(value);
            // 在方法主动调用之前， 调用 Semaphore@acquire()
            try {

                semaphore.acquire();
                //执行 拦截方法
                return pjp.proceed();
            } finally {
                //调用 Semaphore@release
                semaphore.release();
            }
        }
        return pjp.proceed();

    }

    public Semaphore getSemaphore(Method method) {

        return semaphoreCache.computeIfAbsent(method, key -> {
            //得到 注解
            Limited limited = method.getAnnotation(Limited.class);
            //得到 @Limited 的属性
            int value = limited.value();
            return new Semaphore(value);
        });
    }

    public Semaphore initSemaphore(int permits) {
        if (semaphore == null) {
            semaphore = new Semaphore(permits);
        }
        return semaphore;
    }

}
