package com.luixtech.utilities.masking;

public abstract class DataMaskingThreadContextHolder {
    private static final ThreadLocal<Boolean> HOLDER = new InheritableThreadLocal<>() {
        @Override
        protected Boolean initialValue() {
            return true;
        }
    };

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