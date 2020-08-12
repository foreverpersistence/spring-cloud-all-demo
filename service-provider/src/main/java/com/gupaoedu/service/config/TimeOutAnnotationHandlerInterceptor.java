package com.gupaoedu.service.config;

import com.gupaoedu.service.annotation.TimeOut;
import org.aspectj.lang.reflect.MethodSignature;
import org.omg.CORBA.TIMEOUT;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.concurrent.*;
import java.util.stream.Stream;

/**
 * @author fred
 * @date 2020/8/12 3:47 下午
 * @description 拦截器 进行 超时处理
 */
public class TimeOutAnnotationHandlerInterceptor implements HandlerInterceptor {

    private ExecutorService executorService = Executors.newFixedThreadPool(2);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1、拦截 处理防范
        //2、 等到的拦截对象  handler 在 Spring Wev MVC 注解编程中 永远都是  HandlerMethod
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            //2。1 获取 Method 对象
            Method method = handlerMethod.getMethod();
            //3 通过 Method 获取 注解
            TimeOut timeOut = method.getAnnotation(TimeOut.class);
            if (timeOut != null) {
                Object bean = handlerMethod.getBean();

                //4 获取 注解 的属性
                long value = timeOut.value();
                TimeUnit timeUnit = timeOut.timeUnit();
                String fallback = timeOut.fallback();

                //5 构造 Future 超时时间
                Future<Object> future = executorService.submit(new Callable<Object>() {

                    @Override
                    public Object call() throws Exception {
                        return method.invoke(bean);
                    }
                });
                Object returnValue = null;
                try {
                    //6 执行 调用方法
                    returnValue = future.get(value, timeUnit);
                } catch (TimeoutException e) {
                    //7 失败，fallback
                    returnValue = invokeFallbackMethod(handlerMethod, bean, fallback);
                }

                // 8 返回执行结果
                response.getWriter().write(returnValue.toString());

                return false;
            }

        }
        return true;
    }

    private Object invokeFallbackMethod(HandlerMethod handlerMethod, Object bean, String fallback) throws Exception {

        //7。1 查找  fallback 方法
        Method fallbackMethod = findFallbackMethod(handlerMethod, bean, fallback);


        return fallbackMethod.invoke(bean);

    }


    /**
     * 查找对应的 fallback method
     * @param handlerMethod
     * @param bean
     * @param fallback
     * @return
     * @throws NoSuchMethodException
     */
    private Method findFallbackMethod(HandlerMethod handlerMethod, Object bean, String fallback) throws NoSuchMethodException {


        Class<?> beanClass = bean.getClass();
        MethodParameter[] methodParameters = handlerMethod.getMethodParameters();
        Class[] parameterTypes = Stream.of(methodParameters)
                .map(MethodParameter::getParameterType)
                .toArray(Class[]::new);

        Method fallbackMethod = beanClass.getMethod(fallback, parameterTypes);
        return fallbackMethod;

    }
}
