package com.gupaoedu.service.annotation;

import java.lang.annotation.*;

/**
 * @author fred-zhang
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Limited {

    /**
     * 限流 大小
     * @return
     */
    int value();
}
