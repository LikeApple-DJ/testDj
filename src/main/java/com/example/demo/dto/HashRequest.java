package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class HashRequest {

    @NotBlank(message = "input 不能为空")
    private String input;

    @NotBlank(message = "algorithm 不能为空")
    @Pattern(regexp = "(?i)(MD5|SHA-1|SHA-256)", message = "algorithm 必须为 MD5/SHA-1/SHA-256 之一")
    private String algorithm;

    public String getInput() { return input; }
    public void setInput(String input) { this.input = input; }
    public String getAlgorithm() { return algorithm; }
    public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }
}
