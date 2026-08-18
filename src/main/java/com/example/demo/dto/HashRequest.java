package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 哈希计算请求 DTO。
 */
public class HashRequest {

    @NotBlank(message = "input 不能为空")
    private String input;

    @NotBlank(message = "algorithm 不能为空，请指定 MD5 或 SHA256")
    private String algorithm;

    public String getInput() { return input; }
    public void setInput(String input) { this.input = input; }
    public String getAlgorithm() { return algorithm; }
    public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }
}