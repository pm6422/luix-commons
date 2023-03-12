package com.luixtech.utilities.serviceloader.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface SpiName {

    String value() default "";
}
