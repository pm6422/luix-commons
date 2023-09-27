package com.luixtech.utilities.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class InvocationTimeoutException extends RuntimeException {

    private long timeoutInMs;

    public InvocationTimeoutException(long timeoutInMs, String message) {
        super(message);
    }
}