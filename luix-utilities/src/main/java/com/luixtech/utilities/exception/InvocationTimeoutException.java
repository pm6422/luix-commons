package com.luixtech.utilities.exception;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class InvocationTimeoutException extends RuntimeException {

    public InvocationTimeoutException(String message) {
        super(message);
    }
}
