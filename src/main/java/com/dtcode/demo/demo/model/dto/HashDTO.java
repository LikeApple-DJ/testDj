package com.dtcode.demo.demo.model.dto;

/**
 * 哈希算法响应数据
 *
 * @author DTCoder
 */
public class HashDTO {

    private String input;
    private String algorithm;
    private String hashValue;
    private String timestamp;

    public HashDTO() {
    }

    public HashDTO(String input, String algorithm, String hashValue, String timestamp) {
        this.input = input;
        this.algorithm = algorithm;
        this.hashValue = hashValue;
        this.timestamp = timestamp;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "HashDTO{"
                + "input='" + input + '\''
                + ", algorithm='" + algorithm + '\''
                + ", hashValue='" + hashValue + '\''
                + ", timestamp='" + timestamp + '\''
                + '}';
    }
}
