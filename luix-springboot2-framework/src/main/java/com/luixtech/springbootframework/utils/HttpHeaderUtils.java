package com.luixtech.springbootframework.utils;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;

/**
 * Utility class for generate http header.
 */
public abstract class HttpHeaderUtils {
    private static final String HEADER_X_TOTAL_COUNT = "X-Total-Count";

    /**
     * Generate pagination headers for a Spring Data {@link org.springframework.data.domain.Page} object.
     *
     * @param page The page.
     * @param <T>  The type of object.
     * @return http header.
     */
    public static <T> HttpHeaders generatePageHeaders(Page<T> page) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HEADER_X_TOTAL_COUNT, Long.toString(page.getTotalElements()));
        return headers;
    }
}
