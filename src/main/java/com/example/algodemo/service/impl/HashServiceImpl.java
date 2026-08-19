package com.example.algodemo.service.impl;

import com.example.algodemo.common.constant.HashAlgorithmEnum;
import com.example.algodemo.common.exception.AlgorithmErrorCode;
import com.example.algodemo.common.exception.BusinessException;
import com.example.algodemo.service.HashService;
import com.example.algodemo.service.model.HashResult;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 哈希服务实现。
 */
@Service
public class HashServiceImpl implements HashService {

    @Override
    public HashResult hash(String algorithm, String content) {
        if (content == null || content.isEmpty()) {
            throw new BusinessException(AlgorithmErrorCode.ALG_001);
        }
        HashAlgorithmEnum algorithmEnum = HashAlgorithmEnum.of(algorithm);
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithmEnum.getDigestName());
            byte[] bytes = digest.digest(content.getBytes(StandardCharsets.UTF_8));
            String digestHex = String.format("%0" + (bytes.length * 2) + "x", new BigInteger(1, bytes));
            HashResult result = new HashResult();
            result.setAlgorithm(algorithmEnum.name());
            result.setContent(content);
            result.setDigest(digestHex);
            return result;
        } catch (NoSuchAlgorithmException e) {
            throw new BusinessException(AlgorithmErrorCode.ALG_002);
        }
    }
}
