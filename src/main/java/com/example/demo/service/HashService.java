package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Service
public class HashService {

    public String hash(String input, String algorithm) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("input must not be empty");
        }
        if (algorithm == null || algorithm.isBlank()) {
            throw new IllegalArgumentException("algorithm must not be empty");
        }

        String normalizedAlgo = algorithm.toUpperCase().replace("-", "");
        String algoName;
        switch (normalizedAlgo) {
            case "MD5":
                algoName = "MD5";
                break;
            case "SHA256":
                algoName = "SHA-256";
                break;
            case "SHA512":
                algoName = "SHA-512";
                break;
            default:
                throw new IllegalArgumentException("Unsupported algorithm: " + algorithm
                        + ". Supported: MD5, SHA-256, SHA-512");
        }

        try {
            MessageDigest md = MessageDigest.getInstance(algoName);
            byte[] digest = md.digest(input.getBytes());
            return HexFormat.of().formatHex(digest).toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Algorithm not available: " + algoName, e);
        }
    }
}