package com.example.demo.service;

import com.example.demo.model.UserInfo;
import com.example.demo.repository.UserInfoRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration}")
    private long jwtExpiration;

    @Autowired
    private UserInfoRepository userInfoRepository;

    public Map<String, Object> login(String username, String password) {
        Optional<UserInfo> userOpt = userInfoRepository.findByUsername(username);
        if (userOpt.isEmpty() || !userOpt.get().getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        UserInfo user = userOpt.get();
        String token = generateToken(user.getCallerId());

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", user.getCallerId());
        userMap.put("name", user.getDisplayName());
        userMap.put("type", user.getCallerType());
        userMap.put("level", user.getCallerLevel());
        userMap.put("dept", user.getCallerDept());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", userMap);
        return result;
    }

    public String getCallerIdFromToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    public UserInfo getUserByCallerId(String callerId) {
        return userInfoRepository.findByCallerId(callerId).orElse(null);
    }

    private String generateToken(String callerId) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .subject(callerId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(key)
                .compact();
    }
}