package com.example.hash;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 哈希工具类，提供常见哈希算法的计算功能。
 * <p>
 * 支持的算法包括 MD5、SHA-1、SHA-256。
 * 所有方法返回小写十六进制字符串。
 *
 * @author AiWork
 * @date 2026/10/21
 */
public final class HashUtils {

    /** MD5 算法名称 */
    public static final String ALGORITHM_MD5 = "MD5";

    /** SHA-1 算法名称 */
    public static final String ALGORITHM_SHA1 = "SHA-1";

    /** SHA-256 算法名称 */
    public static final String ALGORITHM_SHA256 = "SHA-256";

    private HashUtils() {
        // 工具类禁止实例化
    }

    /**
     * 计算输入字符串的 MD5 哈希值。
     *
     * @param input 待哈希的字符串，不能为 null
     * @return MD5 哈希值（小写十六进制，32位）
     * @throws IllegalArgumentException 如果 input 为 null
     */
    public static String md5(String input) {
        return hash(input, ALGORITHM_MD5);
    }

    /**
     * 计算输入字符串的 SHA-1 哈希值。
     *
     * @param input 待哈希的字符串，不能为 null
     * @return SHA-1 哈希值（小写十六进制，40位）
     * @throws IllegalArgumentException 如果 input 为 null
     */
    public static String sha1(String input) {
        return hash(input, ALGORITHM_SHA1);
    }

    /**
     * 计算输入字符串的 SHA-256 哈希值。
     *
     * @param input 待哈希的字符串，不能为 null
     * @return SHA-256 哈希值（小写十六进制，64位）
     * @throws IllegalArgumentException 如果 input 为 null
     */
    public static String sha256(String input) {
        return hash(input, ALGORITHM_SHA256);
    }

    /**
     * 使用指定算法计算输入字符串的哈希值。
     *
     * @param input     待哈希的字符串，不能为 null
     * @param algorithm 哈希算法名称，不能为 null 或空字符串
     * @return 哈希值的十六进制字符串表示
     * @throws IllegalArgumentException 如果 input 为 null，或 algorithm 为 null/空/不支持
     */
    public static String hash(String input, String algorithm) {
        if (input == null) {
            throw new IllegalArgumentException("input must not be null");
        }
        if (algorithm == null || algorithm.isEmpty()) {
            throw new IllegalArgumentException("algorithm must not be null or empty");
        }

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("Unsupported algorithm: " + algorithm, e);
        }

        byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hashBytes);
    }

    /**
     * 将字节数组转换为小写十六进制字符串。
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}