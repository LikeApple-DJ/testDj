package com.example.algodemo.service.model;

/**
 * 哈希计算结果。
 */
public class HashResult {

    private String algorithm;
    private String content;
    private String digest;

    public HashResult() {
    }

    public HashResult(String algorithm, String content, String digest) {
        this.algorithm = algorithm;
        this.content = content;
        this.digest = digest;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }
}
