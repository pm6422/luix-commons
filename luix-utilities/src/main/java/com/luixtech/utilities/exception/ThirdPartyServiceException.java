package com.luixtech.utilities.exception;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class ThirdPartyServiceException extends RuntimeException {
    public ThirdPartyServiceException(String message) {
        super(message);
    }
}
