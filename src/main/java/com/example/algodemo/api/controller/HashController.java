package com.example.algodemo.api.controller;

import com.example.algodemo.api.request.HashRequest;
import com.example.algodemo.common.response.ApiResponse;
import com.example.algodemo.service.HashService;
import com.example.algodemo.service.model.HashResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 哈希算法接口。
 */
@RestController
public class HashController {

    private final HashService hashService;

    public HashController(HashService hashService) {
        this.hashService = hashService;
    }

    @PostMapping({"/api/hash", "/openapi/hash"})
    public ApiResponse<HashResult> hash(@RequestBody HashRequest request) {
        HashResult result = hashService.hash(request.getAlgorithm(), request.getContent());
        return ApiResponse.success(result);
    }
}
