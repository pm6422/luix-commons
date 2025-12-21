package com.luixtech.utilities.lang;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class MathUtils {
    /**
     * Parse integer string to integer type value, return defaultValue if parse failure
     *
     * @param intStr       integer string
     * @param defaultValue default value
     * @return integer value
     */
    public static int parseInt(String intStr, int defaultValue) {
        try {
            return Integer.parseInt(intStr);
        } catch (NumberFormatException e) {
            log.warn("Failed to parse integer!", e);
            return defaultValue;
        }
    }

    /**
     * Parse long string to long type value, return defaultValue if parse failure
     *
     * @param longStr      long string
     * @param defaultValue default value
     * @return long value
     */
    public static long parseLong(String longStr, long defaultValue) {
        try {
            return Long.parseLong(longStr);
        } catch (NumberFormatException e) {
            log.warn("Failed to parse long!", e);
            return defaultValue;
        }
    }

    /**
     * Convert an integer to a non-negative integer using bitwise operations.
     * <p>
     * If the input is 0 or positive, returns itself.
     * If the input is negative, clears the sign bit to convert it into a positive value or 0
     * (Integer.MIN_VALUE will be converted to 0).
     *
     * @param val integer
     * @return positive integer or zero
     */
    public static int getNonNegativeVal(int val) {
        return 0x7fffffff & val;
    }

    /**
     * Convert an integer to a non-negative integer within a fixed range using bitwise operations.
     *
     * @param val integer
     * @return positive integer or zero with range [0-16777215]
     */
    public static int getRangedNonNegativeVal(int val) {
        return 0x00ffffff & val;
    }
}
