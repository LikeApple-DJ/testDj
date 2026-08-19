package com.dtcode.demo.demo.model.dto;

/**
 * 哈希算法请求参数
 *
 * @author DTCoder
 */
public class HashRequest {

    private String input;

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    @Override
    public String toString() {
        return "HashRequest{input='" + input + "'}";
    }
}
