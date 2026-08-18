package com.example.demo.controller;

import com.example.demo.common.ApiResult;
import com.example.demo.dto.HashRequest;
import com.example.demo.dto.HashResponse;
import com.example.demo.service.HashService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HashController {

    private static final Logger log = LoggerFactory.getLogger(HashController.class);

    private final HashService hashService;

    public HashController(HashService hashService) {
        this.hashService = hashService;
    }

    @PostMapping("/hash")
    public ResponseEntity<ApiResult<HashResponse>> hash(@RequestBody HashRequest request) {
        try {
            if (request.getAlgorithm() == null || request.getAlgorithm().isBlank()) {
                return ResponseEntity.badRequest()
                        .body(ApiResult.error(400, "algorithm 不能为空，请指定 MD5 或 SHA256"));
            }
            String hash = hashService.compute(request.getInput(), request.getAlgorithm());
            HashResponse data = new HashResponse(
                    request.getInput(),
                    request.getAlgorithm().toUpperCase(),
                    hash);
            return ResponseEntity.ok(ApiResult.success(data));
        } catch (IllegalArgumentException e) {
            log.error("哈希算法参数非法", e);
            return ResponseEntity.badRequest()
                    .body(ApiResult.error(400, e.getMessage()));
        }
    }
}