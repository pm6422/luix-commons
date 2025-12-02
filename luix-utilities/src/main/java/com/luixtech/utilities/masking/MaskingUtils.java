package com.luixtech.utilities.masking;

import org.apache.commons.lang3.StringUtils;

public abstract class MaskingUtils {

    /**
     * [中文姓名] 只显示第一个汉字，其他隐藏为2个星号<例子：李**>
     */
    public static String maskChineseName(final String lastName, final String firstName) {
        if (StringUtils.isBlank(lastName) || StringUtils.isBlank(firstName)) {
            return StringUtils.EMPTY;
        }
        return maskChineseName(lastName + firstName);
    }

    /**
     * [中文姓名] 只显示第一个汉字，其他隐藏为2个星号<例子：李**>
     */
    public static String maskChineseName(final String fullName) {
        if (StringUtils.isBlank(fullName)) {
            return StringUtils.EMPTY;
        }
        final String name = StringUtils.left(fullName, 1);
        return StringUtils.rightPad(name, StringUtils.length(fullName), "*");
    }

    /**
     * [身份证号] 显示最后四位，其他隐藏。共计18位或者15位。<例子：420**********5762>
     */
    public static String maskIdCard(final String id) {
        if (StringUtils.isBlank(id)) {
            return StringUtils.EMPTY;
        }
        String paddedLast4 = StringUtils.leftPad(StringUtils.right(id, 4), StringUtils.length(id), '*');
        String starPrefix = "*".repeat(3);
        String tail = paddedLast4.startsWith(starPrefix) ? paddedLast4.substring(starPrefix.length()) : paddedLast4;
        return StringUtils.left(id, 3).concat(tail);
    }

    /**
     * [固定电话] 后四位，其他隐藏<例子：****1234>
     */
    public static String maskLandlinePhone(final String num) {
        if (StringUtils.isBlank(num)) {
            return StringUtils.EMPTY;
        }
        return StringUtils.leftPad(StringUtils.right(num, 4), StringUtils.length(num), "*");
    }

    /**
     * [手机号码] 前三位，后四位，其他隐藏<例子:138******1234>
     */
    public static String maskMobilePhone(final String num) {
        if (StringUtils.isBlank(num)) {
            return StringUtils.EMPTY;
        }
        String paddedLast4 = StringUtils.leftPad(StringUtils.right(num, 4), StringUtils.length(num), '*');
        String starPrefix = "*".repeat(3);
        String tail = paddedLast4.startsWith(starPrefix) ? paddedLast4.substring(starPrefix.length()) : paddedLast4;
        return StringUtils.left(num, 3).concat(tail);

    }

    /**
     * [地址] 只显示到地区，不显示详细地址；我们要对个人信息增强保护<例子：北京市海淀区****>
     *
     * @param address                  address
     * @param sensitiveCharacterLength sensitive character length
     */
    public static String maskAddress(final String address, final int sensitiveCharacterLength) {
        if (StringUtils.isBlank(address)) {
            return StringUtils.EMPTY;
        }
        final int length = StringUtils.length(address);
        return StringUtils.rightPad(StringUtils.left(address, length - sensitiveCharacterLength), length, "*");
    }

    /**
     * [电子邮箱] 邮箱前缀仅显示第一个字母，前缀其他隐藏，用星号代替，@及后面的地址显示<例子:g**@163.com>
     */
    public static String maskEmail(final String email) {
        if (StringUtils.isBlank(email)) {
            return StringUtils.EMPTY;
        }
        final int index = email.indexOf('@');
        if (index <= 1) {
            return email;
        } else {
            return StringUtils.rightPad(StringUtils.left(email, 1), index, "*")
                    .concat(StringUtils.mid(email, index, StringUtils.length(email)));
        }
    }

    /**
     * [银行卡号] 前六位，后四位，其他用星号隐藏每位1个星号<例子:6222600**********1234>
     */
    public static String maskBankCard(final String cardNum) {
        if (StringUtils.isBlank(cardNum)) {
            return StringUtils.EMPTY;
        }
        String paddedLast4 = StringUtils.leftPad(StringUtils.right(cardNum, 4), StringUtils.length(cardNum), '*');
        String starPrefix = "*".repeat(6);
        String tail = paddedLast4.startsWith(starPrefix) ? paddedLast4.substring(starPrefix.length()) : paddedLast4;
        return StringUtils.left(cardNum, 6).concat(tail);
    }

    /**
     * [公司开户银行联号] 公司开户银行联行号，显示前两位，其他用星号隐藏，每位1个星号<例子:12********>
     */
    public static String maskCnapsCode(final String code) {
        if (StringUtils.isBlank(code)) {
            return StringUtils.EMPTY;
        }
        return StringUtils.rightPad(StringUtils.left(code, 2), StringUtils.length(code), "*");
    }
}
