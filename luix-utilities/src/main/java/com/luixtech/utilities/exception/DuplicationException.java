package com.luixtech.utilities.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Iterator;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class DuplicationException extends RuntimeException {

    private static final long                serialVersionUID = 4161299998151198599L;
    private final        Map<String, Object> fieldMap;

    public DuplicationException(Map<String, Object> fieldMap) {
        this.fieldMap = fieldMap;
    }
}