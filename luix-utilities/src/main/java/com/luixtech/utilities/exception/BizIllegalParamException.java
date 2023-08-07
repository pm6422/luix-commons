package com.luixtech.utilities.exception;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class BizIllegalParamException extends RuntimeException {
    public BizIllegalParamException(String message) {
        super(message);
    }
}
