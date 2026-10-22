package com.dtazziboot.tools.model.dto;

import java.io.Serializable;

/**
 * 哈希算法出参。
 */
public class HashResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 使用的算法。 */
    private String algorithm;

    /** 十六进制摘要。 */
    private String hash;

    public HashResult() {
    }

    public HashResult(String algorithm, String hash) {
        this.algorithm = algorithm;
        this.hash = hash;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}