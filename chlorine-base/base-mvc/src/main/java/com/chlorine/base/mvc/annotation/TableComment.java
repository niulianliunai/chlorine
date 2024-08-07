package com.chlorine.base.mvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE}) // 应用于类
@Retention(RetentionPolicy.RUNTIME)
public @interface TableComment {
    String value() default "";
}

