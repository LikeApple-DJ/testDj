package com.example.demo.service;

import com.example.demo.dto.HashResponse;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class HashService {

    public HashResponse computeHash(String input, String algorithm) {
        try {
            String normalizedAlgorithm = normalizeAlgorithm(algorithm);
            MessageDigest digest = MessageDigest.getInstance(normalizedAlgorithm);
            byte[] hashBytes = digest.digest(input.getBytes());
            String hash = bytesToHex(hashBytes);
            return new HashResponse(input, algorithm, hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("Unsupported algorithm: " + algorithm);
        }
    }

    private String normalizeAlgorithm(String algorithm) {
        return switch (algorithm.toUpperCase()) {
            case "MD5" -> "MD5";
            case "SHA-1" -> "SHA-1";
            case "SHA-256" -> "SHA-256";
            default -> algorithm;
        };
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
