package com.testdj.dto;

public class HashRequest {
    private String input;

    public HashRequest() {}

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