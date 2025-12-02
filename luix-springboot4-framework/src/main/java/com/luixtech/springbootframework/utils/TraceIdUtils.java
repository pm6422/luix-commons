package com.luixtech.springbootframework.utils;

import com.github.f4b6a3.tsid.TsidCreator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import java.util.Map;
import java.util.UUID;

/**
 * Utility class for managing trace IDs in distributed systems.
 * Uses SLF4J's MDC (Mapped Diagnostic Context) to store trace IDs.
 */
public class TraceIdUtils {
    private static final String TRACE_ID_KEY = "traceId";

    /**
     * Generates a new trace ID and sets it in MDC
     *
     * @return the generated trace ID
     */
    public static String generateTraceId() {
        String traceId = "T" + TsidCreator.getTsid().toLong();
        MDC.put(TRACE_ID_KEY, traceId);
        return traceId;
    }

    /**
     * Gets the current trace ID from MDC
     *
     * @return current trace ID, or null if not set
     */
    public static String getTraceId() {
        return MDC.get(TRACE_ID_KEY);
    }

    /**
     * Sets a trace ID in MDC
     *
     * @param traceId the trace ID to set
     */
    public static void setTraceId(String traceId) {
        if (traceId != null && !traceId.isEmpty()) {
            MDC.put(TRACE_ID_KEY, traceId);
        }
    }

    /**
     * Removes the trace ID from MDC
     */
    public static void removeTraceId() {
        MDC.remove(TRACE_ID_KEY);
    }

    /**
     * Removes the trace ID from MDC (alias for removeTraceId)
     */
    public static void remove() {
        removeTraceId();
    }

    /**
     * Sets parent MDC context to child thread
     *
     * @param parentMdc the parent MDC context map
     */
    public static void setParentMdcToChild(Map<String, String> parentMdc) {
        if (parentMdc != null && !parentMdc.isEmpty()) {
            MDC.setContextMap(parentMdc);
        }
    }

    /**
     * Clears all MDC context
     */
    public static void clear() {
        MDC.clear();
    }
}
