package com.gupaoedu.service.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author fred-zhang
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface TimeOut {

    /**
     * 超时时间
     * @return
     */
    long value();
    /**
     * 时间单位
     */
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

    String fallback() default "";
}
