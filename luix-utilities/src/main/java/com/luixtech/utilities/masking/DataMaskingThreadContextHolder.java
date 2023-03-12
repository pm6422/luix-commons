package com.luixtech.utilities.masking;

import com.alibaba.ttl.TransmittableThreadLocal;

public abstract class DataMaskingThreadContextHolder {
    private static final TransmittableThreadLocal<Boolean> HOLDER = TransmittableThreadLocal.withInitial(() -> true);

    public static Boolean getMaskEnabled() {
        return HOLDER.get();
    }

    public static void setMaskEnabled(Boolean enabled) {
        HOLDER.set(enabled);
    }

    public static void destroy() {
        HOLDER.remove();
    }
}