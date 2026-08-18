package com.example.demo.dto;

public class HashResponse {
    private String input;
    private String algorithm;
    private String hash;

    public HashResponse(String input, String algorithm, String hash) {
        this.input = input;
        this.algorithm = algorithm;
        this.hash = hash;
    }

    public String getInput() { return input; }
    public String getAlgorithm() { return algorithm; }
    public String getHash() { return hash; }
}