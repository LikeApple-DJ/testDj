package com.example.demo.controller;
import com.example.demo.service.HashService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController
@RequestMapping("/api")
public class HashController {
    private final HashService hashService;
    public HashController(HashService hashService) { this.hashService = hashService; }
    @PostMapping("/hash")
    public ResponseEntity<?> hash(@RequestBody Map<String, String> body) {
        String input = body.get("input");
        if (input == null || input.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("code", "BIZ_001", "message", "输入参数不能为空"));
        }
        String hash = hashService.computeHash(input);
        return ResponseEntity.ok(Map.of("algorithm", "SHA-256", "input", input, "hash", hash));
    }
}