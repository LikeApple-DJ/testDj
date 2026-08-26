package com.example.demo.service;

import com.example.demo.dto.HashRequest;
import com.example.demo.dto.SortRequest;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class AlgorithmService {

    private static final Set<String> ALLOWED_ALGORITHMS = Set.of("MD5", "SHA-1", "SHA-256");

    public Map<String, String> helloWorld() {
        Map<String, String> result = new LinkedHashMap<>();
        result.put("message", "Hello World");
        result.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return result;
    }

    public Map<String, Object> computeHash(HashRequest request) {
        String algorithm = request.getAlgorithm() != null ? request.getAlgorithm().toUpperCase() : "SHA-256";
        if (!ALLOWED_ALGORITHMS.contains(algorithm)) {
            throw new IllegalArgumentException("Unsupported algorithm: " + algorithm + ". Allowed: MD5, SHA-1, SHA-256");
        }

        String input = request.getInput() != null ? request.getInput() : "";
        String hash = "";
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] digest = md.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            hash = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hash algorithm not available: " + algorithm, e);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("algorithm", algorithm);
        result.put("input", input);
        result.put("hash", hash);
        return result;
    }

    public Map<String, Object> bubbleSort(SortRequest request) {
        List<Integer> original = request.getArray() != null
                ? new ArrayList<>(request.getArray())
                : Collections.emptyList();

        List<Integer> arr = new ArrayList<>(original);
        List<List<Integer>> steps = new ArrayList<>();
        steps.add(new ArrayList<>(arr));

        int n = arr.size();
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                if (arr.get(j) > arr.get(j + 1)) {
                    int temp = arr.get(j);
                    arr.set(j, arr.get(j + 1));
                    arr.set(j + 1, temp);
                    swapped = true;
                    steps.add(new ArrayList<>(arr));
                }
            }
            if (!swapped) break;
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("original", original);
        result.put("sorted", arr);
        result.put("steps", steps);
        return result;
    }
}