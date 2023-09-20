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

    @Override
    public String getMessage() {
        StringBuffer sb = new StringBuffer("Found duplicated data for { ");
        Iterator<Map.Entry<String, Object>> iterator = fieldMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            sb.append(entry.getKey()).append(": ").append(entry.getValue());
            if (iterator.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append(" }");
        return sb.toString();
    }
}