package com.example.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

@Service
public class HashService {

    private static final Logger log = LoggerFactory.getLogger(HashService.class);

    private static final char[] HEX = "0123456789abcdef".toCharArray();

    public String compute(String input, String algorithm) {
        if (!"MD5".equalsIgnoreCase(algorithm) && !"SHA256".equalsIgnoreCase(algorithm)) {
            throw new IllegalArgumentException(
                    "不支持的算法: " + algorithm + "，仅支持 MD5 / SHA256");
        }
        try {
            String algo = algorithm.equalsIgnoreCase("SHA256") ? "SHA-256" : "MD5";
            MessageDigest md = MessageDigest.getInstance(algo);
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(digest);
        } catch (NoSuchAlgorithmException e) {
            log.error("哈希算法不可用: algorithm={}", algorithm, e);
            throw new RuntimeException("算法不可用", e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        char[] chars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            chars[i * 2] = HEX[v >>> 4];
            chars[i * 2 + 1] = HEX[v & 0x0F];
        }
        return new String(chars);
    }
}