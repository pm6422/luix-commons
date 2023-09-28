package com.luixtech.springbootframework.controller.response;

import com.luixtech.springbootframework.controller.advice.ResponseConverterAdvice;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * If the controller method is annotated with this annotation, the result of method will be converted by
 * {@link ResponseConverterAdvice}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ConvertResponse {

}