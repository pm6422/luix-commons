package com.luixtech.springbootframework.controller.advice;

import com.luixtech.springbootframework.controller.response.ConvertResponse;
import com.luixtech.springbootframework.controller.response.Result;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * Controller advice to translate the server side normal http response to client-friendly json structures.
 * <p>
 * Refer to <a href="https://github.com/feiniaojin/graceful-response">Graceful Response</a>
 */
@ControllerAdvice
@Slf4j
public class ResponseConverterAdvice implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter methodParameter, @NonNull Class<? extends HttpMessageConverter<?>> clazz) {
        return (methodParameter.getMethod().getDeclaringClass().isAnnotationPresent(ConvertResponse.class)
                || methodParameter.getMethod().isAnnotationPresent(ConvertResponse.class))
                && MappingJackson2HttpMessageConverter.class.isAssignableFrom(clazz);
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  @NonNull MethodParameter methodParameter,
                                  @NonNull MediaType selectedContentType,
                                  @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  @NonNull ServerHttpRequest request,
                                  @NonNull ServerHttpResponse response) {
        if (body instanceof Result) {
            return body;
        } else {
            return Result.ok(body);
        }
    }
}