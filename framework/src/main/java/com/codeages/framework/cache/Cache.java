package com.codeages.framework.cache;

import org.springframework.cache.annotation.Cacheable;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Cacheable(cacheNames = {"default"}, unless="#result == null")
public @interface Cache {
    String value() default "default";
}
