package com.luixtech.utilities.encryption;

import org.apache.commons.lang3.StringUtils;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentStringPBEConfig;

/**
 * Refer to <a href="https://www.toutiao.com/article/7100736419382968868/?log_from=befad6de6e9a9_1676367278980">SpringBoot集成Jasypt加密敏感信息</a>
 */
public abstract class JasyptEncryptUtils {

    private static final String DEFAULT_PUBLIC_KEY = "PEB232@2HJ89";
    private static final String DEFAULT_ALGORITHM  = "PBEWithMD5AndDES";

    public static void main(String[] args) {
        String username = encrypt("root");
        String password = encrypt("123456");
        System.out.println(decrypt(username));
        System.out.println(decrypt(password));
    }

    /**
     * Encrypt the plain text
     *
     * @param plainText plain text to be encrypted
     * @return encrypted text
     */
    public static String encrypt(String plainText) {
        return encrypt(plainText, DEFAULT_ALGORITHM, DEFAULT_PUBLIC_KEY);
    }

    /**
     * Encrypt the plain text
     *
     * @param plainText plain text to be encrypted
     * @param algorithm encryption algorithm
     * @param publicKey public key
     * @return encrypted text
     */
    public static String encrypt(String plainText, String algorithm, String publicKey) {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        EnvironmentStringPBEConfig config = new EnvironmentStringPBEConfig();
        config.setAlgorithm(StringUtils.defaultIfEmpty(algorithm, DEFAULT_ALGORITHM));
        // Set public key
        config.setPassword(StringUtils.defaultIfEmpty(publicKey, DEFAULT_PUBLIC_KEY));
        encryptor.setConfig(config);
        return encryptor.encrypt(plainText);
    }

    /**
     * Decrypt the encrypted text
     *
     * @param cipherText encrypted text to be decrypted
     * @return decrypted text
     */
    public static String decrypt(String cipherText) {
        return decrypt(cipherText, DEFAULT_ALGORITHM, DEFAULT_PUBLIC_KEY);
    }

    /**
     * Decrypt the encrypted text
     *
     * @param cipherText encrypted text to be decrypted
     * @param algorithm  encryption algorithm
     * @param publicKey  public key
     * @return decrypted text
     */
    public static String decrypt(String cipherText, String algorithm, String publicKey) {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        EnvironmentStringPBEConfig config = new EnvironmentStringPBEConfig();
        config.setAlgorithm(StringUtils.defaultIfEmpty(algorithm, DEFAULT_ALGORITHM));
        // Set public key
        config.setPassword(StringUtils.defaultIfEmpty(publicKey, DEFAULT_PUBLIC_KEY));
        encryptor.setConfig(config);
        return encryptor.decrypt(cipherText);
    }
}
