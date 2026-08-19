package com.algorithm.demo.model.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 哈希算法请求
 *
 * @author DTCoder
 */
public class HashRequest {

    /** 待计算的输入字符串 */
    @NotBlank(message = "输入字符串不能为空")
    private String input;

    public HashRequest() {
    }

    public HashRequest(String input) {
        this.input = input;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }
}
