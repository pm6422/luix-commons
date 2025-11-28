package com.luixtech.springbootframework.utils;

import java.security.SecureRandom;

public abstract class RandomUtils {

    private static final int LENGTH = 20;

    /**
     * Generates a password.
     *
     * @return the generated password
     */
    private static final String ALPHANUM = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS   = "0123456789";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public static String generatePassword() {
        return randomFromAlphabet(LENGTH, ALPHANUM);
    }

    /**
     * Generates an activation key.
     *
     * @return the generated activation key
     */
    public static String generateActivationKey() {
        return randomFromAlphabet(LENGTH, DIGITS);
    }

    /**
     * Generates a reset key.
     *
     * @return the generated reset key
     */
    public static String generateResetKey() {
        return randomFromAlphabet(LENGTH, DIGITS);
    }

    private static String randomFromAlphabet(int length, String alphabet) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int idx = SECURE_RANDOM.nextInt(alphabet.length());
            sb.append(alphabet.charAt(idx));
        }
        return sb.toString();
    }
}
