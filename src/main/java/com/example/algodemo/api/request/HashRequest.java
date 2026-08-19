package com.example.algodemo.api.request;

/**
 * 哈希算法请求。
 */
public class HashRequest {

    private String algorithm;
    private String content;

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
}
