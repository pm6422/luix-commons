package com.luixtech.utilities.response;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * If the controller method is annotated with this annotation, the result of method will be converted by
 * {@link com.luixtech.springbootframework.controller.advice.ResponseConverterAdvice}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ConvertResponse {

}