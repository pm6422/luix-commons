package com.luixtech.utilities.masking;

import org.apache.commons.lang3.StringUtils;

public abstract class MaskingUtils {

    /**
     * [Chinese name] Show only the first character and mask the rest with asterisks (e.g. Li**).
     */
    public static String maskChineseName(final String lastName, final String firstName) {
        if (StringUtils.isBlank(lastName) || StringUtils.isBlank(firstName)) {
            return StringUtils.EMPTY;
        }
        return maskChineseName(lastName + firstName);
    }

    /**
     * [Chinese name] Show only the first character and mask the rest with asterisks (e.g. Li**).
     */
    public static String maskChineseName(final String fullName) {
        if (StringUtils.isBlank(fullName)) {
            return StringUtils.EMPTY;
        }
        final String name = StringUtils.left(fullName, 1);
        return StringUtils.rightPad(name, StringUtils.length(fullName), "*");
    }

    /**
     * [ID card number] Show the last 4 characters and mask the rest (15 or 18 characters). (e.g. 420**********5762).
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
     * [Landline] Show the last 4 digits and mask the rest (e.g. ****1234).
     */
    public static String maskLandlinePhone(final String num) {
        if (StringUtils.isBlank(num)) {
            return StringUtils.EMPTY;
        }
        return StringUtils.leftPad(StringUtils.right(num, 4), StringUtils.length(num), "*");
    }

    /**
     * [Mobile phone] Show the first 3 digits and last 4 digits and mask the rest (e.g. 138******1234).
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
     * [Address] Show only the area and mask the detailed address (e.g. Area****).
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
     * [Email] Show only the first character of the local-part; mask the rest, and keep the domain (e.g. g**@163.com).
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
     * [Bank card number] Show the first 6 digits and last 4 digits; mask the rest (e.g. 6222600**********1234).
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
     * [CNAPS code] Show the first 2 digits and mask the rest (e.g. 12********).
     */
    public static String maskCnapsCode(final String code) {
        if (StringUtils.isBlank(code)) {
            return StringUtils.EMPTY;
        }
        return StringUtils.rightPad(StringUtils.left(code, 2), StringUtils.length(code), "*");
    }
}
