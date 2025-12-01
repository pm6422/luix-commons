package com.luixtech.utilities.thread;

import org.apache.commons.lang3.StringUtils;

public abstract class TenantThreadContextHolder {
    private static final ThreadLocal<String> HOLDER = new InheritableThreadLocal<>() {
        @Override
        protected String initialValue() {
            return StringUtils.EMPTY;
        }
    };

    public static String getTenant() {
        return HOLDER.get();
    }

    public static void setTenant(String tenant) {
        HOLDER.set(tenant);
    }

    public static void destroy() {
        HOLDER.remove();
    }
}