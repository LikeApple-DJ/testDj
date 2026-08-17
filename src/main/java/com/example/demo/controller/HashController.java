package com.example.demo.controller;

import com.example.demo.dto.HashRequest;
import com.example.demo.dto.HashResponse;
import org.springframework.web.bind.annotation.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@RestController
@RequestMapping("/api/hash")
public class HashController {

    @PostMapping("/sha256")
    public HashResponse sha256(@RequestBody HashRequest request) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(request.getInput().getBytes());
        String hashHex = HexFormat.of().formatHex(hashBytes);
        return new HashResponse(request.getInput(), "SHA-256", hashHex);
    }
}