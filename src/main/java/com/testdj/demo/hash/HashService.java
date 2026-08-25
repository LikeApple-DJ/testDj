package com.testdj.demo.hash;

import com.testdj.demo.exception.BusinessException;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class HashService {

    public HashResponse hash(HashRequest request) {
        String algorithm = request.algorithm() == null ? "SHA-256" : request.algorithm();
        String content = request.content();
        if (content == null || content.isEmpty()) {
            throw new BusinessException(400, "content must not be empty");
        }
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] hash = digest.digest(content.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return new HashResponse(algorithm, content, hex.toString());
        } catch (NoSuchAlgorithmException e) {
            throw new BusinessException(400, "unsupported algorithm: " + algorithm);
        }
    }
}