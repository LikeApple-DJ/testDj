package com.example.demo.controller;

import com.example.demo.dto.HashRequest;
import com.example.demo.dto.HashResponse;
import org.springframework.web.bind.annotation.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@RestController
@RequestMapping("/api/hash")
public class HashController {

    @PostMapping("/sha256")
    public HashResponse sha256(@RequestBody HashRequest request) throws NoSuchAlgorithmException {
        // 校验输入参数，防止空指针异常
        if (request.getInput() == null || request.getInput().isBlank()) {
            throw new IllegalArgumentException("input 不能为空");
        }
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        // 显式指定 UTF-8 字符集，确保跨环境哈希值一致
        byte[] hashBytes = digest.digest(request.getInput().getBytes(StandardCharsets.UTF_8));
        String hashHex = HexFormat.of().formatHex(hashBytes);
        return new HashResponse(request.getInput(), "SHA-256", hashHex);
    }
}