package com.luixtech.springbootframework.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;

@Slf4j
public class ExceptionHandlingAsyncUncaughtExceptionHandler extends SimpleAsyncUncaughtExceptionHandler {

    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        log.error("Unexpected exception occurred invoking async method: " + method, ex);
    }
}
