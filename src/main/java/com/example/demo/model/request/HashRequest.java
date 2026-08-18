package com.example.demo.model.request;

/**
 * 哈希计算请求参数。
 */
public class HashRequest {

    /** 待哈希的原始字符串，必填 */
    private String input;

    /** 哈希算法，默认 SHA-256，可选 MD5 */
    private String algorithm;

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }
}