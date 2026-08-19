package com.example.algodemo.service;

import com.example.algodemo.service.model.HashResult;

/**
 * 哈希服务。
 */
public interface HashService {

    /**
     * 对 content 使用指定算法生成摘要。
     *
     * @param algorithm 算法名：MD5 / SHA256
     * @param content   待摘要原文
     * @return 哈希结果
     */
    HashResult hash(String algorithm, String content);
}
