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
import java.lang.reflect.InvocationTargetException;
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


    private ExecutorService executorService = Executors.newFixedThreadPool(2);

    /**
     * Aop 实现 超时
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around("@annotation(com.gupaoedu.service.annotation.TimeOut)")
    public Object beforeTimeoutMehtodInvocation(ProceedingJoinPoint pjp) throws Throwable {

        Object returnValue = null;
        Signature signature = pjp.getSignature();
        if (signature instanceof MethodSignature) {
            MethodSignature methodSignature = (MethodSignature) signature;
            Method method = methodSignature.getMethod();
            Object[] args = pjp.getArgs();
            //3 通过 Method 获取 注解
            TimeOut timeOut = method.getAnnotation(TimeOut.class);
            if (timeOut != null) {
                //4 获取 注解 的属性
                long value = timeOut.value();
                TimeUnit timeUnit = timeOut.timeUnit();
                String fallback = timeOut.fallback();

                //5 构造 Future 超时时间
                Future<Object> future = executorService.submit(new Callable<Object>() {

                    @Override
                    public Object call() throws Exception {
                        try {
                            return pjp.proceed();
                        } catch (Throwable throwable) {
                            throw new RuntimeException();
                        }
                    }
                });

                try {
                    //6 执行 调用方法
                    returnValue = future.get(value, timeUnit);
                } catch (TimeoutException e) {
                    //7 失败，fallback
                    returnValue = invokeFallbackMethod(method, pjp.getTarget(), fallback, args);
                }
            }
        }
        return returnValue;
    }


    /**
     *
     * @param method
     * @param bean
     * @param fallback
     * @param args 参数 透传
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
        private Object invokeFallbackMethod (Method method, Object bean, String fallback, Object[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

            //7。1 查找  fallback 方法
            Method fallbackMethod = findFallbackMethod(method, bean, fallback);

            return fallbackMethod.invoke(bean, args);

        }


        /**
         * 查找对应的 fallback method
         * @param method
         * @param bean
         * @param fallback
         * @return
         * @throws NoSuchMethodException
         */
        private Method findFallbackMethod(Method method, Object bean, String fallback) throws NoSuchMethodException {


            Class<?> beanClass = bean.getClass();

            Method fallbackMethod = beanClass.getMethod(fallback, method.getParameterTypes());
            return fallbackMethod;

        }
}
