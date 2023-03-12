package com.luixtech.utilities.lang;

import org.apache.commons.lang3.EnumUtils;

public interface EnumValueHoldable<T> {

    /**
     * Get enum value
     *
     * @return enum value
     */
    T getValue();

    /**
     * Check the enum value whether is a valid one
     *
     * @param enumClazz enum class
     * @param enumValue enum value
     * @param <E>       enum type
     * @param <T>       enum value type
     * @return {@code true} if it was valid and {@code false} otherwise
     */
    static <E extends Enum<E> & EnumValueHoldable<T>, T> boolean isValidValue(Class<E> enumClazz, T enumValue) {
        return getEnumByValue(enumClazz, enumValue) != null;
    }

    /**
     * Get enum object by enum value
     *
     * @param enumClazz enum class
     * @param enumValue enum value
     * @param <E>       enum type
     * @param <T>       enum value type
     * @return enum object
     */
    static <E extends Enum<E> & EnumValueHoldable<T>, T> E getEnumByValue(Class<E> enumClazz, T enumValue) {
        return enumValue == null ? null :
                EnumUtils.getEnumList(enumClazz)
                        .stream()
                        .filter(e -> enumValue.equals(e.getValue()))
                        .findFirst()
                        .orElse(null);
    }
}
