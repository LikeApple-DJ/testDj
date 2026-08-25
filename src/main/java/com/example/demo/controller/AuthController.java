package com.example.demo.controller;
import com.example.demo.model.dto.AuthResponse;
import com.example.demo.model.dto.LoginRequest;
import com.example.demo.model.dto.RegisterRequest;
import com.example.demo.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    public AuthController(UserService userService) { this.userService = userService; }
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        try {
            AuthResponse resp = userService.register(req);
            return ResponseEntity.ok(resp);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("code", 400, "message", e.getMessage()));
        }
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            AuthResponse resp = userService.login(req);
            Map<String, Object> userMap = Map.of(
                "id", resp.getId(),
                "username", resp.getUsername(),
                "personType", resp.getPersonType() != null ? resp.getPersonType() : "",
                "personLevel", resp.getPersonLevel() != null ? resp.getPersonLevel() : "",
                "personDept", resp.getPersonDept() != null ? resp.getPersonDept() : ""
            );
            return ResponseEntity.ok(Map.of("token", resp.getToken(), "user", userMap));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("code", 400, "message", e.getMessage()));
        }
    }
}