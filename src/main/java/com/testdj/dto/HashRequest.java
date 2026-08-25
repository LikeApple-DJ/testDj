package com.testdj.dto;

import jakarta.validation.constraints.NotBlank;

public class HashRequest {
    @NotBlank(message = "input must not be blank")
    private String input;

    public HashRequest() {}

    public HashRequest(String input) {
        this.input = input;
    }

    public String getInput() { return input; }
    public void setInput(String input) { this.input = input; }
}