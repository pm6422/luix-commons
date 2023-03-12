package com.luixtech.utilities.lang;

public abstract class ReflectionUtils {

    public static boolean hasZeroArgConstructor(Class<?> clazz) {
        try {
            clazz.getDeclaredConstructor();
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    public static boolean isJdkClass(Class<?> clazz) {
        return clazz.getName().startsWith("java.") || clazz.getName().startsWith("javax.");
    }
}