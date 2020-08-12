package com.gupaoedu.service.config;

import com.gupaoedu.service.annotation.TimeOut;
import org.omg.CORBA.TIMEOUT;
import org.springframework.http.HttpMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @author fred
 * @date 2020/8/12 3:47 下午
 * @description todo
 */
public class TimeOutAnnotationHandlerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            TimeOut timeOut = method.getAnnotation(TimeOut.class);
            if (timeOut != null) {
                long value = timeOut.value();
                TimeUnit timeUnit = timeOut.timeUnit();
                String fallback = timeOut.fallback();
            }

        }
        return true;
    }
}
