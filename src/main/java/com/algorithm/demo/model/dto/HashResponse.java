package com.algorithm.demo.model.dto;

/**
 * 哈希算法响应数据
 *
 * @author DTCoder
 */
public class HashResponse {

    /** 原始输入 */
    private String input;

    /** 哈希算法名称 */
    private String algorithm;

    /** 哈希结果（十六进制） */
    private String hashValue;

    public HashResponse() {
    }

    public HashResponse(String input, String algorithm, String hashValue) {
        this.input = input;
        this.algorithm = algorithm;
        this.hashValue = hashValue;
    }

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

    public String getHashValue() {
        return hashValue;
    }

    public void setHashValue(String hashValue) {
        this.hashValue = hashValue;
    }
}
