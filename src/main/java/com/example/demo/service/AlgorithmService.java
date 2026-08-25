package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

@Service
public class AlgorithmService {

    public String hello() {
        return "Hello World!";
    }

    public String hash(String input, String algorithm) {
        if (input == null) {
            throw new IllegalArgumentException("input cannot be null");
        }
        String algo = (algorithm == null || algorithm.isBlank()) ? "SHA-256" : algorithm.toUpperCase();
        try {
            MessageDigest md = MessageDigest.getInstance(algo);
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("Unsupported algorithm: " + algorithm);
        }
    }

    public int[] bubbleSort(int[] array) {
        if (array == null) {
            throw new IllegalArgumentException("array cannot be null");
        }
        int[] arr = Arrays.copyOf(array, array.length);
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
        return arr;
    }
}