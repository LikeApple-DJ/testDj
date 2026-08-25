package com.testdj.demo.hash;

import com.testdj.demo.common.ErrorCode;
import com.testdj.demo.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class HashService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HashService.class);

    public HashResponse hash(HashRequest request) {
        String algorithm = request.algorithm() == null ? "SHA-256" : request.algorithm();
        String content = request.content();
        if (content == null || content.isEmpty()) {
            throw new BusinessException(ErrorCode.HASH_CONTENT_EMPTY, ErrorCode.HASH_CONTENT_EMPTY_MSG);
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
            LOGGER.error("unsupported algorithm: {}", algorithm, e);
            throw new BusinessException(ErrorCode.HASH_UNSUPPORTED_ALGORITHM,
                    ErrorCode.HASH_UNSUPPORTED_ALGORITHM_MSG + ": " + algorithm);
        }
    }
}
