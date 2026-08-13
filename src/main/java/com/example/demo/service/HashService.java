package com.example.demo.service;

import com.example.demo.dto.HashResponse;
import com.example.demo.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Set;

@Service
public class HashService {

    private static final Logger log = LoggerFactory.getLogger(HashService.class);
    private static final Set<String> SUPPORTED_ALGORITHMS = Set.of("MD5", "SHA-1", "SHA-256");

    public HashResponse computeHash(String input, String algorithm) {
        if (input == null || input.isEmpty()) {
            throw new BusinessException("BIZ_002", "input 不能为空");
        }
        if (algorithm == null || algorithm.isBlank()) {
            throw new BusinessException("BIZ_003", "algorithm 不能为空");
        }

        String normalizedAlgorithm = normalizeAlgorithm(algorithm);
        try {
            MessageDigest digest = MessageDigest.getInstance(normalizedAlgorithm);
            byte[] hashBytes = digest.digest(input.getBytes());
            String hash = bytesToHex(hashBytes);
            return new HashResponse(input, algorithm, hash);
        } catch (NoSuchAlgorithmException e) {
            log.warn("Unsupported algorithm: {}", algorithm, e);
            throw new BusinessException("BIZ_003", "不支持的算法: " + algorithm);
        }
    }

    private String normalizeAlgorithm(String algorithm) {
        String upper = algorithm.toUpperCase();
        if (!SUPPORTED_ALGORITHMS.contains(upper)) {
            throw new BusinessException("BIZ_003", "algorithm 必须为 MD5/SHA-1/SHA-256 之一");
        }
        return upper;
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
