package com.luixtech.utilities.serviceloader.annotation;

import java.lang.annotation.*;

/**
 * Service provider interface
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Spi {

    SpiScope scope() default SpiScope.SINGLETON;

}
