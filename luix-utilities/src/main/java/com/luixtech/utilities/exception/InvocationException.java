package com.luixtech.utilities.exception;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class InvocationException extends RuntimeException {

    public InvocationException(String message) {
        super(message);
    }
}
