package com.luixtech.utilities.thread;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.apache.commons.lang3.StringUtils;

public abstract class TenantThreadContextHolder {
    private static final TransmittableThreadLocal<String> HOLDER = TransmittableThreadLocal.withInitial(() -> StringUtils.EMPTY);

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