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
     * 通过二进制位操作将原先的整数转化为非负整数
     * 当原先的整数是0或正数时返回本身
     * 当原先的整数是负数时通过二进制首位取反转化为正数或0(Integer.MIN_VALUE将转换为0)
     *
     * @param val integer
     * @return positive integer or zero
     */
    public static int getNonNegativeVal(int val) {
        return 0x7fffffff & val;
    }

    /**
     * 通过二进制位操作将原先的整数转化为一定值范围内的非负整数
     *
     * @param val integer
     * @return positive integer or zero with range [0-16777215]
     */
    public static int getRangedNonNegativeVal(int val) {
        return 0x00ffffff & val;
    }
}
