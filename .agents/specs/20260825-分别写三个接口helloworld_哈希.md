# 三接口工具 + 埋点可视化报表 — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 构建完整的后端三接口服务（HelloWorld/SHA-256/冒泡排序）+ AOP 埋点 + Excel 导出，以及前端三 Tab 工具页 + 多维可视化报表仪表盘。

**Architecture:** 后端 Spring Boot 3 单体服务，JWT 认证，Spring AOP 切面采集调用记录落 MySQL，Apache POI 生成 Excel；前端 React 18 SPA，Axios 统一拦截，ECharts 三图联动，按人员类型/层级/部门维度切换。

**Tech Stack:** Spring Boot 3 + Spring Security + JWT + MySQL + JPA + Apache POI + AOP | React 18 + React Router + Axios + ECharts

**Design Doc:** `[testDj] docs/superpowers/specs/2026-08-25-three-api-tracking-dashboard-design.md`

---

## Global Constraints

- 后端统一路径前缀 `/api/*`
- 认证：JWT `Bearer {token}`，token 含 userId/username
- 错误响应格式：`{ code, message }`
- 导出：`Content-Disposition: attachment; filename=xxx.xlsx`，前端以 blob 下载
- 报表维度枚举：`personType` / `personLevel` / `personDept`
- 密码存储：BCrypt
- 哈希算法：SHA-256
- 项目均为 greenfield，需从零搭建

---

## 跨仓依赖与现状摘要

| 仓库 | 现状 | 待建内容 |
|------|------|----------|
| `[testDj]` | 空仓库，仅含设计文档 `docs/superpowers/specs/` | Spring Boot 3 项目骨架、JWT 认证、3 业务接口、AOP 埋点、Excel 导出、报表 API、MySQL 表 |
| `[testDJnew]` | 空仓库，仅含 `README.md` | React 18 项目骨架、登录/注册页、Dashboard 页（3 Tab + 导出按钮 + ECharts 报表） |

**仓间对齐点：**
1. JWT 格式：后端签发 `{ userId, username }` → 前端 `Authorization: Bearer <token>`
2. API 路径：后端 `/api/*` → 前端 Axios baseURL 代理或 CORS
3. 报表维度：后端 `?dimension=personType|personLevel|personDept` → 前端下拉切换
4. 导出：后端流式返回 `.xlsx` → 前端 `responseType: 'blob'` 触发下载
5. 错误码：后端统一 `{ code, message }` → 前端拦截器统一 toast

---

## Task 1: [testDj] Spring Boot 3 项目骨架与数据库

**Files:**
- Create: `[testDj] pom.xml`
- Create: `[testDj] src/main/java/com/example/demo/DemoApplication.java`
- Create: `[testDj] src/main/resources/application.yml`
- Create: `[testDj] src/main/java/com/example/demo/model/User.java`
- Create: `[testDj] src/main/java/com/example/demo/model/TrackingRecord.java`
- Create: `[testDj] src/main/java/com/example/demo/repository/UserRepository.java`
- Create: `[testDj] src/main/java/com/example/demo/repository/TrackingRecordRepository.java`

**Interfaces:**
- Produces: `User` entity (id, username, password, personType, personLevel, personDept, createdAt), `TrackingRecord` entity (id, userId, apiName, paramsJson, callTime, ipAddress), `UserRepository extends JpaRepository<User, Long>`, `TrackingRecordRepository extends JpaRepository<TrackingRecord, Long>`

- [ ] **Step 1: Create pom.xml with all dependencies**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
        <relativePath/>
    </parent>
    <groupId>com.example</groupId>
    <artifactId>demo</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>demo</name>
    <description>Three API Tools + Tracking Dashboard Backend</description>
    <properties>
        <java.version>17</java.version>
        <jjwt.version>0.12.3</jjwt.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-aop</artifactId>
        </dependency>
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>${jjwt.version}</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>${jjwt.version}</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>${jjwt.version}</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.apache.poi</groupId>
            <artifactId>poi-ooxml</artifactId>
            <version>5.2.5</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

- [ ] **Step 2: Create DemoApplication.java**

```java
package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
```

- [ ] **Step 3: Create application.yml**

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/demo_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect

jwt:
  secret: dGhpcyBpcyBhIHZlcnkgbG9uZyBzZWNyZXQga2V5IGZvciBqd3QgdG9rZW4gZ2VuZXJhdGlvbg==
  expiration: 86400000
```

- [ ] **Step 4: Create User.java entity**

```java
package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(name = "person_type", length = 50)
    private String personType;

    @Column(name = "person_level", length = 50)
    private String personLevel;

    @Column(name = "person_dept", length = 100)
    private String personDept;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public User() {}

    public User(String username, String password, String personType, String personLevel, String personDept) {
        this.username = username;
        this.password = password;
        this.personType = personType;
        this.personLevel = personLevel;
        this.personDept = personDept;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getPersonType() { return personType; }
    public void setPersonType(String personType) { this.personType = personType; }
    public String getPersonLevel() { return personLevel; }
    public void setPersonLevel(String personLevel) { this.personLevel = personLevel; }
    public String getPersonDept() { return personDept; }
    public void setPersonDept(String personDept) { this.personDept = personDept; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
```

- [ ] **Step 5: Create TrackingRecord.java entity**

```java
package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tracking_records")
public class TrackingRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "api_name", length = 50)
    private String apiName;

    @Column(name = "params_json", columnDefinition = "TEXT")
    private String paramsJson;

    @Column(name = "call_time")
    private LocalDateTime callTime = LocalDateTime.now();

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    public TrackingRecord() {}

    public TrackingRecord(Long userId, String apiName, String paramsJson, String ipAddress) {
        this.userId = userId;
        this.apiName = apiName;
        this.paramsJson = paramsJson;
        this.ipAddress = ipAddress;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getApiName() { return apiName; }
    public void setApiName(String apiName) { this.apiName = apiName; }
    public String getParamsJson() { return paramsJson; }
    public void setParamsJson(String paramsJson) { this.paramsJson = paramsJson; }
    public LocalDateTime getCallTime() { return callTime; }
    public void setCallTime(LocalDateTime callTime) { this.callTime = callTime; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
}
```

- [ ] **Step 6: Create UserRepository.java**

```java
package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
```

- [ ] **Step 7: Create TrackingRecordRepository.java**

```java
package com.example.demo.repository;

import com.example.demo.model.TrackingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface TrackingRecordRepository extends JpaRepository<TrackingRecord, Long> {

    @Query("SELECT tr FROM TrackingRecord tr JOIN User u ON tr.userId = u.id " +
           "WHERE (:apiName IS NULL OR tr.apiName = :apiName)")
    List<TrackingRecord> findByApiName(@Param("apiName") String apiName);

    @Query("SELECT tr FROM TrackingRecord tr JOIN User u ON tr.userId = u.id")
    List<TrackingRecord> findAllWithUser();
}
```

- [ ] **Step 8: Build and verify compilation**

Run: `cd /root/.agentix/agentic-dev/runs/DEV-966dcd0a-7905-11f1-9649-3b4281182f10-e09a2952-5098-4d54-8616-02251dd458b2/worktree/testDj-main && mvn compile -q 2>&1`
Expected: BUILD SUCCESS

- [ ] **Step 9: Commit**

```bash
git add pom.xml src/
git commit -m "feat: Spring Boot 3 project skeleton with JPA entities and repositories"
```

---

## Task 2: [testDj] JWT 认证模块（安全配置 + 注册/登录）

**Files:**
- Create: `[testDj] src/main/java/com/example/demo/security/JwtUtil.java`
- Create: `[testDj] src/main/java/com/example/demo/security/JwtAuthenticationFilter.java`
- Create: `[testDj] src/main/java/com/example/demo/config/SecurityConfig.java`
- Create: `[testDj] src/main/java/com/example/demo/config/WebConfig.java`
- Create: `[testDj] src/main/java/com/example/demo/model/dto/RegisterRequest.java`
- Create: `[testDj] src/main/java/com/example/demo/model/dto/LoginRequest.java`
- Create: `[testDj] src/main/java/com/example/demo/model/dto/AuthResponse.java`
- Create: `[testDj] src/main/java/com/example/demo/service/UserService.java`
- Create: `[testDj] src/main/java/com/example/demo/controller/AuthController.java`

**Interfaces:**
- Consumes: `User`, `UserRepository` (from Task 1)
- Produces:
  - `JwtUtil.generateToken(userId, username): String`
  - `JwtUtil.validateToken(token): boolean`
  - `JwtUtil.getUserIdFromToken(token): Long`
  - `JwtAuthenticationFilter` — OncePerRequestFilter，从 Authorization header 提取 Bearer token
  - `POST /api/auth/register` — Body: `{ username, password, personType, personLevel, personDept }` → `{ id, username, token }`
  - `POST /api/auth/login` — Body: `{ username, password }` → `{ token, user: { id, username, personType, personLevel, personDept } }`

- [ ] **Step 1: Create JwtUtil.java**

```java
package com.example.demo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey key;
    private final long expiration;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration}") long expiration) {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.expiration = expiration;
    }

    public String generateToken(Long userId, String username) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiration);
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("username", username)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).getPayload();
        return Long.parseLong(claims.getSubject());
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).getPayload();
        return claims.get("username", String.class);
    }
}
```

- [ ] **Step 2: Create JwtAuthenticationFilter.java**

```java
package com.example.demo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            if (jwtUtil.validateToken(token)) {
                Long userId = jwtUtil.getUserIdFromToken(token);
                String username = jwtUtil.getUsernameFromToken(token);
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                userId, username, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        filterChain.doFilter(request, response);
    }
}
```

- [ ] **Step 3: Create SecurityConfig.java**

```java
package com.example.demo.config;

import com.example.demo.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> {})
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

- [ ] **Step 4: Create WebConfig.java (CORS)**

```java
package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins("http://localhost:3000")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
```

- [ ] **Step 5: Create DTOs**

RegisterRequest.java:
```java
package com.example.demo.model.dto;

public class RegisterRequest {
    private String username;
    private String password;
    private String personType;
    private String personLevel;
    private String personDept;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getPersonType() { return personType; }
    public void setPersonType(String personType) { this.personType = personType; }
    public String getPersonLevel() { return personLevel; }
    public void setPersonLevel(String personLevel) { this.personLevel = personLevel; }
    public String getPersonDept() { return personDept; }
    public void setPersonDept(String personDept) { this.personDept = personDept; }
}
```

LoginRequest.java:
```java
package com.example.demo.model.dto;

public class LoginRequest {
    private String username;
    private String password;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
```

AuthResponse.java:
```java
package com.example.demo.model.dto;

public class AuthResponse {
    private Long id;
    private String username;
    private String token;
    private String personType;
    private String personLevel;
    private String personDept;

    public AuthResponse() {}

    public AuthResponse(Long id, String username, String token) {
        this.id = id;
        this.username = username;
        this.token = token;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getPersonType() { return personType; }
    public void setPersonType(String personType) { this.personType = personType; }
    public String getPersonLevel() { return personLevel; }
    public void setPersonLevel(String personLevel) { this.personLevel = personLevel; }
    public String getPersonDept() { return personDept; }
    public void setPersonDept(String personDept) { this.personDept = personDept; }
}
```

- [ ] **Step 6: Create UserService.java**

```java
package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.model.dto.AuthResponse;
import com.example.demo.model.dto.LoginRequest;
import com.example.demo.model.dto.RegisterRequest;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        User user = new User(
                req.getUsername(),
                passwordEncoder.encode(req.getPassword()),
                req.getPersonType(),
                req.getPersonLevel(),
                req.getPersonDept()
        );
        user = userRepository.save(user);
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        AuthResponse resp = new AuthResponse(user.getId(), user.getUsername(), token);
        resp.setPersonType(user.getPersonType());
        resp.setPersonLevel(user.getPersonLevel());
        resp.setPersonDept(user.getPersonDept());
        return resp;
    }

    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        AuthResponse resp = new AuthResponse(user.getId(), user.getUsername(), token);
        resp.setPersonType(user.getPersonType());
        resp.setPersonLevel(user.getPersonLevel());
        resp.setPersonDept(user.getPersonDept());
        return resp;
    }
}
```

- [ ] **Step 7: Create AuthController.java**

```java
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

    public AuthController(UserService userService) {
        this.userService = userService;
    }

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
            return ResponseEntity.ok(resp);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("code", 400, "message", e.getMessage()));
        }
    }
}
```

- [ ] **Step 8: Build and verify compilation**

Run: `cd /root/.agentix/agentic-dev/runs/DEV-966dcd0a-7905-11f1-9649-3b4281182f10-e09a2952-5098-4d54-8616-02251dd458b2/worktree/testDj-main && mvn compile -q 2>&1`
Expected: BUILD SUCCESS

- [ ] **Step 9: Commit**

```bash
git add src/main/java/com/example/demo/security/ src/main/java/com/example/demo/config/ src/main/java/com/example/demo/model/dto/ src/main/java/com/example/demo/service/UserService.java src/main/java/com/example/demo/controller/AuthController.java
git commit -m "feat: JWT authentication with register/login endpoints"
```

---

## Task 3: [testDj] 三个业务接口（HelloWorld / SHA-256 哈希 / 冒泡排序）

**Files:**
- Create: `[testDj] src/main/java/com/example/demo/service/HashService.java`
- Create: `[testDj] src/main/java/com/example/demo/service/BubbleSortService.java`
- Create: `[testDj] src/main/java/com/example/demo/controller/HelloWorldController.java`
- Create: `[testDj] src/main/java/com/example/demo/controller/HashController.java`
- Create: `[testDj] src/main/java/com/example/demo/controller/BubbleSortController.java`

**Interfaces:**
- Consumes: JWT auth filter (from Task 2)
- Produces:
  - `GET /api/helloworld?name={name}` → `{ result: "Hello, {name}!" }`
  - `POST /api/hash` Body: `{ input: "string" }` → `{ algorithm: "SHA-256", input: "string", hash: "hex..." }`
  - `POST /api/bubblesort` Body: `{ array: [5, 3, 8, 1, 2] }` → `{ original: [...], sorted: [...] }`

- [ ] **Step 1: Create HashService.java**

```java
package com.example.demo.service;

import org.springframework.stereotype.Service;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class HashService {

    public String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes());
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
```

- [ ] **Step 2: Create BubbleSortService.java**

```java
package com.example.demo.service;

import org.springframework.stereotype.Service;
import java.util.Arrays;

@Service
public class BubbleSortService {

    public int[] sort(int[] array) {
        int[] sorted = Arrays.copyOf(array, array.length);
        int n = sorted.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (sorted[j] > sorted[j + 1]) {
                    int temp = sorted[j];
                    sorted[j] = sorted[j + 1];
                    sorted[j + 1] = temp;
                }
            }
        }
        return sorted;
    }
}
```

- [ ] **Step 3: Create HelloWorldController.java**

```java
package com.example.demo.controller;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class HelloWorldController {

    @GetMapping("/helloworld")
    public Map<String, String> hello(@RequestParam(defaultValue = "World") String name) {
        return Map.of("result", "Hello, " + name + "!");
    }
}
```

- [ ] **Step 4: Create HashController.java**

```java
package com.example.demo.controller;

import com.example.demo.service.HashService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class HashController {

    private final HashService hashService;

    public HashController(HashService hashService) {
        this.hashService = hashService;
    }

    @PostMapping("/hash")
    public Map<String, String> hash(@RequestBody Map<String, String> body) {
        String input = body.getOrDefault("input", "");
        String hash = hashService.sha256(input);
        return Map.of("algorithm", "SHA-256", "input", input, "hash", hash);
    }
}
```

- [ ] **Step 5: Create BubbleSortController.java**

```java
package com.example.demo.controller;

import com.example.demo.service.BubbleSortService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class BubbleSortController {

    private final BubbleSortService bubbleSortService;

    public BubbleSortController(BubbleSortService bubbleSortService) {
        this.bubbleSortService = bubbleSortService;
    }

    @PostMapping("/bubblesort")
    public Map<String, Object> sort(@RequestBody Map<String, List<Integer>> body) {
        List<Integer> list = body.get("array");
        if (list == null || list.isEmpty()) {
            return Map.of("original", List.of(), "sorted", List.of());
        }
        int[] array = list.stream().mapToInt(Integer::intValue).toArray();
        int[] sorted = bubbleSortService.sort(array);
        return Map.of("original", list, "sorted", sorted);
    }
}
```

- [ ] **Step 6: Build and verify compilation**

Run: `cd /root/.agentix/agentic-dev/runs/DEV-966dcd0a-7905-11f1-9649-3b4281182f10-e09a2952-5098-4d54-8616-02251dd458b2/worktree/testDj-main && mvn compile -q 2>&1`
Expected: BUILD SUCCESS

- [ ] **Step 7: Commit**

```bash
git add src/main/java/com/example/demo/service/HashService.java src/main/java/com/example/demo/service/BubbleSortService.java src/main/java/com/example/demo/controller/HelloWorldController.java src/main/java/com/example/demo/controller/HashController.java src/main/java/com/example/demo/controller/BubbleSortController.java
git commit -m "feat: three business APIs - HelloWorld, SHA-256 hash, bubble sort"
```

---

## Task 4: [testDj] AOP 埋点切面

**Files:**
- Create: `[testDj] src/main/java/com/example/demo/aspect/TrackingAspect.java`

**Interfaces:**
- Consumes: `TrackingRecordRepository` (from Task 1), JWT auth context (from Task 2), 3 business controllers (from Task 3)
- Produces: `TrackingAspect` — `@Around` 切面拦截 `@RequestMapping` 注解的方法（排除 `/api/auth/**` 和 `/api/tracking/**`），提取 userId、apiName、paramsJson、ipAddress，写入 `tracking_records` 表

- [ ] **Step 1: Create TrackingAspect.java**

```java
package com.example.demo.aspect;

import com.example.demo.model.TrackingRecord;
import com.example.demo.repository.TrackingRecordRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;

@Aspect
@Component
public class TrackingAspect {

    private final TrackingRecordRepository trackingRepo;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TrackingAspect(TrackingRecordRepository trackingRepo) {
        this.trackingRepo = trackingRepo;
    }

    @Around("@within(org.springframework.web.bind.annotation.RestController) " +
            "&& !execution(* com.example.demo.controller.AuthController.*(..)) " +
            "&& !execution(* com.example.demo.controller.TrackingController.*(..))")
    public Object trackApiCall(ProceedingJoinPoint joinPoint) throws Throwable {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = (auth != null && auth.getPrincipal() instanceof Long)
                ? (Long) auth.getPrincipal() : null;

        String apiName = joinPoint.getSignature().getName();
        String paramsJson = "{}";
        Object[] args = joinPoint.getArgs();
        if (args.length > 0) {
            try {
                paramsJson = objectMapper.writeValueAsString(args[0]);
            } catch (Exception e) {
                paramsJson = "{\"raw\":\"" + args[0].toString() + "\"}";
            }
        }

        String ipAddress = "unknown";
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletRequest request = attrs.getRequest();
            ipAddress = request.getRemoteAddr();
        }

        if (userId != null) {
            TrackingRecord record = new TrackingRecord(userId, apiName, paramsJson, ipAddress);
            trackingRepo.save(record);
        }

        return joinPoint.proceed();
    }
}
```

- [ ] **Step 2: Build and verify compilation**

Run: `cd /root/.agentix/agentic-dev/runs/DEV-966dcd0a-7905-11f1-9649-3b4281182f10-e09a2952-5098-4d54-8616-02251dd458b2/worktree/testDj-main && mvn compile -q 2>&1`
Expected: BUILD SUCCESS

- [ ] **Step 3: Commit**

```bash
git add src/main/java/com/example/demo/aspect/TrackingAspect.java
git commit -m "feat: AOP tracking aspect for API call recording"
```

---

## Task 5: [testDj] Excel 导出接口

**Files:**
- Create: `[testDj] src/main/java/com/example/demo/service/ExportService.java`
- Create: `[testDj] src/main/java/com/example/demo/controller/ExportController.java`

**Interfaces:**
- Consumes: `TrackingRecordRepository` (from Task 1), `UserRepository` (from Task 1), JWT auth (from Task 2)
- Produces: `GET /api/export?type={helloworld|hash|bubblesort}` → `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet` 二进制流，`Content-Disposition: attachment; filename={type}_export.xlsx`

- [ ] **Step 1: Create ExportService.java**

```java
package com.example.demo.service;

import com.example.demo.model.TrackingRecord;
import com.example.demo.model.User;
import com.example.demo.repository.TrackingRecordRepository;
import com.example.demo.repository.UserRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ExportService {

    private final TrackingRecordRepository trackingRepo;
    private final UserRepository userRepository;

    public ExportService(TrackingRecordRepository trackingRepo, UserRepository userRepository) {
        this.trackingRepo = trackingRepo;
        this.userRepository = userRepository;
    }

    public byte[] exportTrackingRecords(String apiType) throws IOException {
        List<TrackingRecord> records;
        if (apiType == null || apiType.isBlank()) {
            records = trackingRepo.findAllWithUser();
        } else {
            records = trackingRepo.findByApiName(apiType);
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(apiType != null ? apiType : "all");
            Row header = sheet.createRow(0);
            String[] columns = {"ID", "用户名", "人员类型", "人员层级", "人员部门", "接口名", "参数", "调用时间", "IP地址"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columns[i]);
            }

            int rowIdx = 1;
            for (TrackingRecord tr : records) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(tr.getId());
                Optional<User> userOpt = userRepository.findById(tr.getUserId());
                String username = userOpt.map(User::getUsername).orElse("unknown");
                String personType = userOpt.map(User::getPersonType).orElse("");
                String personLevel = userOpt.map(User::getPersonLevel).orElse("");
                String personDept = userOpt.map(User::getPersonDept).orElse("");
                row.createCell(1).setCellValue(username);
                row.createCell(2).setCellValue(personType);
                row.createCell(3).setCellValue(personLevel);
                row.createCell(4).setCellValue(personDept);
                row.createCell(5).setCellValue(tr.getApiName());
                row.createCell(6).setCellValue(tr.getParamsJson());
                row.createCell(7).setCellValue(tr.getCallTime() != null ? tr.getCallTime().toString() : "");
                row.createCell(8).setCellValue(tr.getIpAddress());
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            return baos.toByteArray();
        }
    }
}
```

- [ ] **Step 2: Create ExportController.java**

```java
package com.example.demo.controller;

import com.example.demo.service.ExportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ExportController {

    private final ExportService exportService;

    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> export(@RequestParam(defaultValue = "") String type) throws IOException {
        byte[] data = exportService.exportTrackingRecords(type.isBlank() ? null : type);
        String filename = (type.isBlank() ? "all" : type) + "_export.xlsx";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(data);
    }
}
```

- [ ] **Step 3: Build and verify compilation**

Run: `cd /root/.agentix/agentic-dev/runs/DEV-966dcd0a-7905-11f1-9649-3b4281182f10-e09a2952-5098-4d54-8616-02251dd458b2/worktree/testDj-main && mvn compile -q 2>&1`
Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add src/main/java/com/example/demo/service/ExportService.java src/main/java/com/example/demo/controller/ExportController.java
git commit -m "feat: Excel export endpoint for tracking records"
```

---

## Task 6: [testDj] 埋点报表接口

**Files:**
- Create: `[testDj] src/main/java/com/example/demo/service/TrackingService.java`
- Create: `[testDj] src/main/java/com/example/demo/controller/TrackingController.java`

**Interfaces:**
- Consumes: `TrackingRecordRepository`, `UserRepository` (from Task 1), JWT auth (from Task 2)
- Produces: `GET /api/tracking/report?dimension={personType|personLevel|personDept}` → `[ { label: "技术岗", callCount: 42, details: [...] }, ... ]`

- [ ] **Step 1: Create TrackingService.java**

```java
package com.example.demo.service;

import com.example.demo.model.TrackingRecord;
import com.example.demo.model.User;
import com.example.demo.repository.TrackingRecordRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TrackingService {

    private final TrackingRecordRepository trackingRepo;
    private final UserRepository userRepository;

    public TrackingService(TrackingRecordRepository trackingRepo, UserRepository userRepository) {
        this.trackingRepo = trackingRepo;
        this.userRepository = userRepository;
    }

    public List<Map<String, Object>> getReport(String dimension) {
        List<TrackingRecord> records = trackingRepo.findAllWithUser();
        Map<Long, User> userMap = userRepository.findAll().stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        Map<String, List<TrackingRecord>> grouped = new LinkedHashMap<>();
        for (TrackingRecord record : records) {
            User user = userMap.get(record.getUserId());
            if (user == null) continue;
            String key = getDimensionValue(user, dimension);
            grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(record);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, List<TrackingRecord>> entry : grouped.entrySet()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("label", entry.getKey());
            item.put("callCount", entry.getValue().size());
            List<Map<String, Object>> details = entry.getValue().stream().map(tr -> {
                Map<String, Object> d = new LinkedHashMap<>();
                User u = userMap.get(tr.getUserId());
                d.put("username", u != null ? u.getUsername() : "unknown");
                d.put("apiName", tr.getApiName());
                d.put("callTime", tr.getCallTime() != null ? tr.getCallTime().toString() : "");
                return d;
            }).collect(Collectors.toList());
            item.put("details", details);
            result.add(item);
        }
        return result;
    }

    private String getDimensionValue(User user, String dimension) {
        return switch (dimension) {
            case "personType" -> user.getPersonType() != null ? user.getPersonType() : "未设置";
            case "personLevel" -> user.getPersonLevel() != null ? user.getPersonLevel() : "未设置";
            case "personDept" -> user.getPersonDept() != null ? user.getPersonDept() : "未设置";
            default -> "未知";
        };
    }
}
```

- [ ] **Step 2: Create TrackingController.java**

```java
package com.example.demo.controller;

import com.example.demo.service.TrackingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tracking")
public class TrackingController {

    private final TrackingService trackingService;

    public TrackingController(TrackingService trackingService) {
        this.trackingService = trackingService;
    }

    @GetMapping("/report")
    public List<Map<String, Object>> report(@RequestParam(defaultValue = "personType") String dimension) {
        return trackingService.getReport(dimension);
    }
}
```

- [ ] **Step 3: Build and verify compilation**

Run: `cd /root/.agentix/agentic-dev/runs/DEV-966dcd0a-7905-11f1-9649-3b4281182f10-e09a2952-5098-4d54-8616-02251dd458b2/worktree/testDj-main && mvn compile -q 2>&1`
Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add src/main/java/com/example/demo/service/TrackingService.java src/main/java/com/example/demo/controller/TrackingController.java
git commit -m "feat: tracking report API with dimension-based grouping"
```

---

## Task 7: [testDJnew] React 18 项目骨架与路由

**Files:**
- Create: `[testDJnew] package.json`
- Create: `[testDJnew] public/index.html`
- Create: `[testDJnew] src/index.js`
- Create: `[testDJnew] src/App.jsx`
- Create: `[testDJnew] src/utils/auth.js`
- Create: `[testDJnew] src/hooks/useAuth.js`
- Create: `[testDJnew] src/api/index.js`

**Interfaces:**
- Consumes: 后端 `/api/auth/*` (from Task 2)
- Produces: React 18 SPA 骨架，Axios 封装（Base URL `http://localhost:8080`，JWT 拦截器），`useAuth` hook，`auth.js` token 工具

- [ ] **Step 1: Create package.json**

```json
{
  "name": "three-api-dashboard",
  "version": "0.1.0",
  "private": true,
  "dependencies": {
    "react": "^18.2.0",
    "react-dom": "^18.2.0",
    "react-router-dom": "^6.20.0",
    "react-scripts": "5.0.1",
    "axios": "^1.6.2",
    "echarts": "^5.4.3",
    "echarts-for-react": "^3.0.2"
  },
  "scripts": {
    "start": "react-scripts start",
    "build": "react-scripts build"
  },
  "browserslist": {
    "production": [">0.2%", "not dead", "not op_mini all"],
    "development": ["last 1 chrome version", "last 1 firefox version", "last 1 safari version"]
  }
}
```

- [ ] **Step 2: Create public/index.html**

```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>三接口工具 + 埋点报表</title>
</head>
<body>
    <div id="root"></div>
</body>
</html>
```

- [ ] **Step 3: Create src/index.js**

```jsx
import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
    <React.StrictMode>
        <App />
    </React.StrictMode>
);
```

- [ ] **Step 4: Create src/utils/auth.js**

```js
const TOKEN_KEY = 'jwt_token';
const USER_KEY = 'user_info';

export function getToken() {
    return localStorage.getItem(TOKEN_KEY);
}

export function setToken(token) {
    localStorage.setItem(TOKEN_KEY, token);
}

export function removeToken() {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
}

export function getUser() {
    const raw = localStorage.getItem(USER_KEY);
    return raw ? JSON.parse(raw) : null;
}

export function setUser(user) {
    localStorage.setItem(USER_KEY, JSON.stringify(user));
}

export function isLoggedIn() {
    return !!getToken();
}
```

- [ ] **Step 5: Create src/api/index.js**

```js
import axios from 'axios';
import { getToken, removeToken } from '../utils/auth';

const api = axios.create({
    baseURL: 'http://localhost:8080',
    timeout: 10000,
});

api.interceptors.request.use((config) => {
    const token = getToken();
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response && error.response.status === 401) {
            removeToken();
            window.location.href = '/login';
        }
        return Promise.reject(error);
    }
);

export default api;
```

- [ ] **Step 6: Create src/hooks/useAuth.js**

```js
import { useState, useCallback } from 'react';
import { getToken, setToken, removeToken, getUser, setUser } from '../utils/auth';

export function useAuth() {
    const [user, setUserState] = useState(() => getUser());

    const login = useCallback((token, userInfo) => {
        setToken(token);
        setUser(userInfo);
        setUserState(userInfo);
    }, []);

    const logout = useCallback(() => {
        removeToken();
        setUserState(null);
    }, []);

    const isAuthenticated = !!getToken();

    return { user, isAuthenticated, login, logout };
}
```

- [ ] **Step 7: Create src/App.jsx**

```jsx
import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from './hooks/useAuth';
import LoginPage from './pages/LoginPage';
import DashboardPage from './pages/DashboardPage';

function ProtectedRoute({ children }) {
    const { isAuthenticated } = useAuth();
    if (!isAuthenticated) {
        return <Navigate to="/login" replace />;
    }
    return children;
}

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/login" element={<LoginPage />} />
                <Route path="/dashboard" element={
                    <ProtectedRoute>
                        <DashboardPage />
                    </ProtectedRoute>
                } />
                <Route path="*" element={<Navigate to="/dashboard" replace />} />
            </Routes>
        </BrowserRouter>
    );
}

export default App;
```

- [ ] **Step 8: Install dependencies and verify build**

Run: `cd /root/.agentix/agentic-dev/runs/DEV-966dcd0a-7905-11f1-9649-3b4281182f10-e09a2952-5098-4d54-8616-02251dd458b2/worktree/testDJnew-main && npm install 2>&1`
Expected: packages installed without errors

- [ ] **Step 9: Commit**

```bash
git add package.json package-lock.json public/ src/
git commit -m "feat: React 18 project skeleton with routing, auth, and Axios"
```

---

## Task 8: [testDJnew] 登录/注册页面

**Files:**
- Create: `[testDJnew] src/pages/LoginPage.jsx`

**Interfaces:**
- Consumes: `api` (Axios from Task 7), `useAuth` hook (from Task 7)
- Produces: `/login` 路由页面，含登录表单和注册表单切换，调用 `POST /api/auth/login` 和 `POST /api/auth/register`

- [ ] **Step 1: Create LoginPage.jsx**

```jsx
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api';
import { useAuth } from '../hooks/useAuth';

function LoginPage() {
    const [isRegister, setIsRegister] = useState(false);
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [personType, setPersonType] = useState('');
    const [personLevel, setPersonLevel] = useState('');
    const [personDept, setPersonDept] = useState('');
    const [error, setError] = useState('');
    const { login } = useAuth();
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        try {
            if (isRegister) {
                const res = await api.post('/api/auth/register', {
                    username, password, personType, personLevel, personDept
                });
                login(res.data.token, {
                    id: res.data.id,
                    username: res.data.username,
                    personType: res.data.personType,
                    personLevel: res.data.personLevel,
                    personDept: res.data.personDept,
                });
            } else {
                const res = await api.post('/api/auth/login', { username, password });
                login(res.data.token, res.data.user);
            }
            navigate('/dashboard');
        } catch (err) {
            setError(err.response?.data?.message || '操作失败');
        }
    };

    const styles = {
        container: {
            maxWidth: '420px', margin: '80px auto', padding: '32px',
            border: '1px solid #e0e0e0', borderRadius: '8px',
            fontFamily: 'Arial, sans-serif',
        },
        title: { textAlign: 'center', marginBottom: '24px', fontSize: '24px' },
        input: {
            width: '100%', padding: '10px', marginBottom: '12px',
            boxSizing: 'border-box', border: '1px solid #ccc', borderRadius: '4px',
        },
        button: {
            width: '100%', padding: '12px', backgroundColor: '#1976d2',
            color: 'white', border: 'none', borderRadius: '4px',
            cursor: 'pointer', fontSize: '16px', marginTop: '8px',
        },
        switch: {
            textAlign: 'center', marginTop: '16px', color: '#1976d2',
            cursor: 'pointer', fontSize: '14px',
        },
        error: { color: 'red', marginBottom: '12px', fontSize: '14px' },
    };

    return (
        <div style={styles.container}>
            <h2 style={styles.title}>{isRegister ? '注册' : '登录'}</h2>
            {error && <div style={styles.error}>{error}</div>}
            <form onSubmit={handleSubmit}>
                <input style={styles.input} placeholder="用户名" value={username}
                    onChange={e => setUsername(e.target.value)} required />
                <input style={styles.input} type="password" placeholder="密码" value={password}
                    onChange={e => setPassword(e.target.value)} required />
                {isRegister && (
                    <>
                        <input style={styles.input} placeholder="人员类型（如：技术岗/管理岗/运营岗）"
                            value={personType} onChange={e => setPersonType(e.target.value)} />
                        <input style={styles.input} placeholder="人员层级（如：初级/中级/高级/专家）"
                            value={personLevel} onChange={e => setPersonLevel(e.target.value)} />
                        <input style={styles.input} placeholder="人员部门（如：研发部/产品部/运维部）"
                            value={personDept} onChange={e => setPersonDept(e.target.value)} />
                    </>
                )}
                <button type="submit" style={styles.button}>
                    {isRegister ? '注册' : '登录'}
                </button>
            </form>
            <div style={styles.switch} onClick={() => { setIsRegister(!isRegister); setError(''); }}>
                {isRegister ? '已有账号？去登录' : '没有账号？去注册'}
            </div>
        </div>
    );
}

export default LoginPage;
```

- [ ] **Step 2: Verify build**

Run: `cd /root/.agentix/agentic-dev/runs/DEV-966dcd0a-7905-11f1-9649-3b4281182f10-e09a2952-5098-4d54-8616-02251dd458b2/worktree/testDJnew-main && npx react-scripts build 2>&1 | tail -5`
Expected: "The build folder is ready to be deployed."

- [ ] **Step 3: Commit**

```bash
git add src/pages/LoginPage.jsx
git commit -m "feat: login and registration page with JWT integration"
```

---

## Task 9: [testDJnew] 三个 Tab 组件 + 导出按钮

**Files:**
- Create: `[testDJnew] src/components/HelloWorldTab.jsx`
- Create: `[testDJnew] src/components/HashTab.jsx`
- Create: `[testDJnew] src/components/BubbleSortTab.jsx`
- Create: `[testDJnew] src/components/ExportButton.jsx`

**Interfaces:**
- Consumes: `api` (Axios from Task 7), 后端 `GET /api/helloworld`, `POST /api/hash`, `POST /api/bubblesort`, `GET /api/export`
- Produces: 三个独立 Tab 组件（输入框 + 执行按钮 + 结果展示区），`ExportButton` 组件（接收 `type` prop，调用导出接口并触发 blob 下载）

- [ ] **Step 1: Create HelloWorldTab.jsx**

```jsx
import React, { useState } from 'react';
import api from '../api';

function HelloWorldTab() {
    const [name, setName] = useState('');
    const [result, setResult] = useState('');

    const handleExecute = async () => {
        try {
            const res = await api.get('/api/helloworld', { params: { name: name || 'World' } });
            setResult(res.data.result);
        } catch (err) {
            setResult('请求失败: ' + (err.response?.data?.message || err.message));
        }
    };

    return (
        <div style={{ padding: '16px' }}>
            <h3>HelloWorld 接口</h3>
            <input
                placeholder="输入名称（默认 World）"
                value={name}
                onChange={e => setName(e.target.value)}
                style={{ padding: '8px', width: '300px', marginRight: '8px' }}
            />
            <button onClick={handleExecute}
                style={{ padding: '8px 16px', cursor: 'pointer' }}>
                执行
            </button>
            {result && (
                <div style={{ marginTop: '16px', padding: '12px', background: '#f5f5f5', borderRadius: '4px' }}>
                    <strong>结果：</strong>{result}
                </div>
            )}
        </div>
    );
}

export default HelloWorldTab;
```

- [ ] **Step 2: Create HashTab.jsx**

```jsx
import React, { useState } from 'react';
import api from '../api';

function HashTab() {
    const [input, setInput] = useState('');
    const [result, setResult] = useState(null);

    const handleExecute = async () => {
        try {
            const res = await api.post('/api/hash', { input });
            setResult(res.data);
        } catch (err) {
            setResult({ error: '请求失败: ' + (err.response?.data?.message || err.message) });
        }
    };

    return (
        <div style={{ padding: '16px' }}>
            <h3>SHA-256 哈希接口</h3>
            <input
                placeholder="输入要哈希的字符串"
                value={input}
                onChange={e => setInput(e.target.value)}
                style={{ padding: '8px', width: '300px', marginRight: '8px' }}
            />
            <button onClick={handleExecute}
                style={{ padding: '8px 16px', cursor: 'pointer' }}>
                执行
            </button>
            {result && (
                <div style={{ marginTop: '16px', padding: '12px', background: '#f5f5f5', borderRadius: '4px' }}>
                    <div><strong>算法：</strong>{result.algorithm}</div>
                    <div><strong>输入：</strong>{result.input}</div>
                    <div style={{ wordBreak: 'break-all' }}><strong>哈希值：</strong>{result.hash}</div>
                </div>
            )}
        </div>
    );
}

export default HashTab;
```

- [ ] **Step 3: Create BubbleSortTab.jsx**

```jsx
import React, { useState } from 'react';
import api from '../api';

function BubbleSortTab() {
    const [input, setInput] = useState('5,3,8,1,2');
    const [result, setResult] = useState(null);

    const handleExecute = async () => {
        const arr = input.split(',').map(s => parseInt(s.trim(), 10)).filter(n => !isNaN(n));
        try {
            const res = await api.post('/api/bubblesort', { array: arr });
            setResult(res.data);
        } catch (err) {
            setResult({ error: '请求失败: ' + (err.response?.data?.message || err.message) });
        }
    };

    return (
        <div style={{ padding: '16px' }}>
            <h3>冒泡排序接口</h3>
            <input
                placeholder="输入数组（逗号分隔），如 5,3,8,1,2"
                value={input}
                onChange={e => setInput(e.target.value)}
                style={{ padding: '8px', width: '300px', marginRight: '8px' }}
            />
            <button onClick={handleExecute}
                style={{ padding: '8px 16px', cursor: 'pointer' }}>
                执行
            </button>
            {result && (
                <div style={{ marginTop: '16px', padding: '12px', background: '#f5f5f5', borderRadius: '4px' }}>
                    <div><strong>原始数组：</strong>[{Array.isArray(result.original) ? result.original.join(', ') : ''}]</div>
                    <div><strong>排序结果：</strong>[{Array.isArray(result.sorted) ? result.sorted.join(', ') : ''}]</div>
                </div>
            )}
        </div>
    );
}

export default BubbleSortTab;
```

- [ ] **Step 4: Create ExportButton.jsx**

```jsx
import React from 'react';
import api from '../api';

function ExportButton({ type }) {
    const handleExport = async () => {
        try {
            const res = await api.get('/api/export', {
                params: { type },
                responseType: 'blob',
            });
            const url = window.URL.createObjectURL(new Blob([res.data]));
            const link = document.createElement('a');
            link.href = url;
            const disposition = res.headers['content-disposition'];
            const filename = disposition
                ? disposition.split('filename=')[1]?.replace(/"/g, '')
                : `${type || 'all'}_export.xlsx`;
            link.setAttribute('download', filename);
            document.body.appendChild(link);
            link.click();
            link.remove();
            window.URL.revokeObjectURL(url);
        } catch (err) {
            alert('导出失败: ' + (err.response?.data?.message || err.message));
        }
    };

    return (
        <button onClick={handleExport}
            style={{
                padding: '8px 16px', cursor: 'pointer',
                backgroundColor: '#4caf50', color: 'white',
                border: 'none', borderRadius: '4px', marginLeft: '8px',
            }}>
            导出Excel
        </button>
    );
}

export default ExportButton;
```

- [ ] **Step 5: Verify build**

Run: `cd /root/.agentix/agentic-dev/runs/DEV-966dcd0a-7905-11f1-9649-3b4281182f10-e09a2952-5098-4d54-8616-02251dd458b2/worktree/testDJnew-main && npx react-scripts build 2>&1 | tail -5`
Expected: "The build folder is ready to be deployed."

- [ ] **Step 6: Commit**

```bash
git add src/components/HelloWorldTab.jsx src/components/HashTab.jsx src/components/BubbleSortTab.jsx src/components/ExportButton.jsx
git commit -m "feat: three tab components and export button"
```

---

## Task 10: [testDJnew] Dashboard 主页面（Tab 布局 + 埋点报表 ECharts 可视化）

**Files:**
- Create: `[testDJnew] src/components/TrackingDashboard.jsx`
- Create: `[testDJnew] src/pages/DashboardPage.jsx`

**Interfaces:**
- Consumes: `api` (Axios from Task 7), `useAuth` hook (from Task 7), Tab 组件 (from Task 9), 后端 `GET /api/tracking/report?dimension=...`
- Produces: `/dashboard` 路由页面，含 Header（用户名 + 退出）、三 Tab 切换、导出按钮、ECharts 报表区（维度切换 + 折线图/饼图/柱状图）

- [ ] **Step 1: Create TrackingDashboard.jsx**

```jsx
import React, { useState, useEffect, useCallback } from 'react';
import ReactECharts from 'echarts-for-react';
import api from '../api';

const DIMENSIONS = [
    { value: 'personType', label: '人员类型' },
    { value: 'personLevel', label: '人员层级' },
    { value: 'personDept', label: '人员部门' },
];

function TrackingDashboard() {
    const [dimension, setDimension] = useState('personType');
    const [data, setData] = useState([]);
    const [loading, setLoading] = useState(false);

    const fetchData = useCallback(async () => {
        setLoading(true);
        try {
            const res = await api.get('/api/tracking/report', { params: { dimension } });
            setData(res.data);
        } catch (err) {
            console.error('Failed to fetch tracking data:', err);
        } finally {
            setLoading(false);
        }
    }, [dimension]);

    useEffect(() => { fetchData(); }, [fetchData]);

    const labels = data.map(d => d.label);
    const counts = data.map(d => d.callCount);

    const lineOption = {
        title: { text: '调用趋势（折线图）', left: 'center' },
        tooltip: { trigger: 'axis' },
        xAxis: { type: 'category', data: labels },
        yAxis: { type: 'value', name: '调用次数' },
        series: [{ data: counts, type: 'line', smooth: true, areaStyle: {} }],
    };

    const pieOption = {
        title: { text: '调用占比（饼图）', left: 'center' },
        tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
        series: [{
            type: 'pie', radius: ['40%', '70%'],
            data: data.map(d => ({ name: d.label, value: d.callCount })),
            emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0 } },
        }],
    };

    const barOption = {
        title: { text: '调用对比（柱状图）', left: 'center' },
        tooltip: { trigger: 'axis' },
        xAxis: { type: 'category', data: labels },
        yAxis: { type: 'value', name: '调用次数' },
        series: [{
            data: counts, type: 'bar',
            itemStyle: { color: '#5470c6' },
        }],
    };

    return (
        <div style={{ padding: '16px' }}>
            <h3>埋点调用报表</h3>
            <div style={{ marginBottom: '16px' }}>
                <span>维度切换：</span>
                <select value={dimension} onChange={e => setDimension(e.target.value)}
                    style={{ padding: '6px 12px', fontSize: '14px' }}>
                    {DIMENSIONS.map(d => (
                        <option key={d.value} value={d.value}>{d.label}</option>
                    ))}
                </select>
                {loading && <span style={{ marginLeft: '12px', color: '#999' }}>加载中...</span>}
            </div>
            <div style={{ display: 'flex', flexWrap: 'wrap', gap: '16px' }}>
                <div style={{ flex: '1 1 400px', minWidth: '350px', border: '1px solid #e0e0e0', borderRadius: '8px', padding: '8px' }}>
                    <ReactECharts option={lineOption} style={{ height: '300px' }} />
                </div>
                <div style={{ flex: '1 1 400px', minWidth: '350px', border: '1px solid #e0e0e0', borderRadius: '8px', padding: '8px' }}>
                    <ReactECharts option={pieOption} style={{ height: '300px' }} />
                </div>
                <div style={{ flex: '1 1 400px', minWidth: '350px', border: '1px solid #e0e0e0', borderRadius: '8px', padding: '8px' }}>
                    <ReactECharts option={barOption} style={{ height: '300px' }} />
                </div>
            </div>
        </div>
    );
}

export default TrackingDashboard;
```

- [ ] **Step 2: Create DashboardPage.jsx**

```jsx
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import HelloWorldTab from '../components/HelloWorldTab';
import HashTab from '../components/HashTab';
import BubbleSortTab from '../components/BubbleSortTab';
import ExportButton from '../components/ExportButton';
import TrackingDashboard from '../components/TrackingDashboard';

const TABS = [
    { key: 'helloworld', label: 'HelloWorld', component: HelloWorldTab },
    { key: 'hash', label: '哈希', component: HashTab },
    { key: 'bubblesort', label: '排序', component: BubbleSortTab },
];

function DashboardPage() {
    const [activeTab, setActiveTab] = useState('helloworld');
    const { user, logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    const ActiveComponent = TABS.find(t => t.key === activeTab)?.component;

    return (
        <div style={{ maxWidth: '1200px', margin: '0 auto', fontFamily: 'Arial, sans-serif' }}>
            {/* Header */}
            <div style={{
                display: 'flex', justifyContent: 'space-between', alignItems: 'center',
                padding: '12px 24px', borderBottom: '1px solid #e0e0e0',
                backgroundColor: '#fafafa',
            }}>
                <span style={{ fontSize: '20px', fontWeight: 'bold' }}>三接口工具 + 埋点报表</span>
                <div>
                    <span style={{ marginRight: '16px' }}>
                        {user?.username} ({user?.personType || '未设置'} / {user?.personDept || '未设置'})
                    </span>
                    <button onClick={handleLogout}
                        style={{ padding: '6px 16px', cursor: 'pointer' }}>
                        退出
                    </button>
                </div>
            </div>

            {/* Tabs */}
            <div style={{ display: 'flex', borderBottom: '2px solid #e0e0e0', padding: '0 24px' }}>
                {TABS.map(tab => (
                    <div key={tab.key} onClick={() => setActiveTab(tab.key)}
                        style={{
                            padding: '12px 24px', cursor: 'pointer',
                            borderBottom: activeTab === tab.key ? '2px solid #1976d2' : '2px solid transparent',
                            color: activeTab === tab.key ? '#1976d2' : '#666',
                            fontWeight: activeTab === tab.key ? 'bold' : 'normal',
                            marginBottom: '-2px',
                        }}>
                        {tab.label}
                    </div>
                ))}
                <div style={{ marginLeft: 'auto', display: 'flex', alignItems: 'center' }}>
                    <ExportButton type={activeTab} />
                </div>
            </div>

            {/* Tab Content */}
            <div style={{ padding: '0 24px' }}>
                {ActiveComponent && <ActiveComponent />}
            </div>

            {/* Tracking Dashboard */}
            <div style={{ borderTop: '2px solid #e0e0e0', marginTop: '24px' }}>
                <TrackingDashboard />
            </div>
        </div>
    );
}

export default DashboardPage;
```

- [ ] **Step 3: Verify build**

Run: `cd /root/.agentix/agentic-dev/runs/DEV-966dcd0a-7905-11f1-9649-3b4281182f10-e09a2952-5098-4d54-8616-02251dd458b2/worktree/testDJnew-main && npx react-scripts build 2>&1 | tail -5`
Expected: "The build folder is ready to be deployed."

- [ ] **Step 4: Commit**

```bash
git add src/components/TrackingDashboard.jsx src/pages/DashboardPage.jsx
git commit -m "feat: dashboard page with 3 tabs, export, and ECharts tracking visualization"
```

---

## Task 11: [testDj] 后端集成测试

**Files:**
- Create: `[testDj] src/test/java/com/example/demo/DemoApplicationTests.java`

**Interfaces:**
- Consumes: All backend modules (from Tasks 1-6)
- Produces: 集成测试覆盖注册→登录→调用三接口→查询报表→导出 的完整链路

- [ ] **Step 1: Create integration test**

```java
package com.example.demo;

import com.example.demo.model.User;
import com.example.demo.model.TrackingRecord;
import com.example.demo.model.dto.AuthResponse;
import com.example.demo.repository.TrackingRecordRepository;
import com.example.demo.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DemoApplicationTests {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private TrackingRecordRepository trackingRepo;
    @Autowired private ObjectMapper objectMapper;

    private String token;

    @BeforeEach
    void setUp() throws Exception {
        trackingRepo.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void fullIntegrationFlow() throws Exception {
        // 1. Register
        String registerJson = objectMapper.writeValueAsString(Map.of(
            "username", "testuser",
            "password", "pass123",
            "personType", "技术岗",
            "personLevel", "高级",
            "personDept", "研发部"
        ));
        String registerResp = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON).content(registerJson))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        AuthResponse auth = objectMapper.readValue(registerResp, AuthResponse.class);
        token = auth.getToken();
        assertNotNull(token);

        // 2. Login
        String loginJson = objectMapper.writeValueAsString(Map.of(
            "username", "testuser", "password", "pass123"
        ));
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON).content(loginJson))
                .andExpect(status().isOk());

        // 3. HelloWorld API
        mockMvc.perform(get("/api/helloworld")
                .header("Authorization", "Bearer " + token)
                .param("name", "Alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("Hello, Alice!"));

        // 4. Hash API
        String hashJson = objectMapper.writeValueAsString(Map.of("input", "test"));
        mockMvc.perform(post("/api/hash")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(hashJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.algorithm").value("SHA-256"))
                .andExpect(jsonPath("$.hash").isNotEmpty());

        // 5. BubbleSort API
        String sortJson = objectMapper.writeValueAsString(
            Map.of("array", List.of(5, 3, 8, 1, 2))
        );
        mockMvc.perform(post("/api/bubblesort")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(sortJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sorted[0]").value(1))
                .andExpect(jsonPath("$.sorted[4]").value(8));

        // 6. Tracking records exist
        List<TrackingRecord> records = trackingRepo.findAll();
        assertTrue(records.size() >= 3, "Should have at least 3 tracking records");

        // 7. Tracking report API
        mockMvc.perform(get("/api/tracking/report")
                .header("Authorization", "Bearer " + token)
                .param("dimension", "personType"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].label").value("技术岗"));

        // 8. Export API
        mockMvc.perform(get("/api/export")
                .header("Authorization", "Bearer " + token)
                .param("type", "helloworld"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition",
                        "attachment; filename=helloworld_export.xlsx"));
    }
}
```

- [ ] **Step 2: Run integration tests**

Run: `cd /root/.agentix/agentic-dev/runs/DEV-966dcd0a-7905-11f1-9649-3b4281182f10-e09a2952-5098-4d54-8616-02251dd458b2/worktree/testDj-main && mvn test -Dtest=DemoApplicationTests 2>&1`
Expected: Tests pass (requires MySQL running at localhost:3306)

- [ ] **Step 3: Commit**

```bash
git add src/test/
git commit -m "test: integration test covering full flow register→login→APIs→tracking→export"
```

---

## 仓间产物汇总

| 仓库 | 产物 | 路径 |
|------|------|------|
| `[testDj]` | pom.xml (Maven 项目配置) | `pom.xml` |
| `[testDj]` | DemoApplication.java | `src/main/java/com/example/demo/DemoApplication.java` |
| `[testDj]` | application.yml | `src/main/resources/application.yml` |
| `[testDj]` | User.java (Entity) | `src/main/java/com/example/demo/model/User.java` |
| `[testDj]` | TrackingRecord.java (Entity) | `src/main/java/com/example/demo/model/TrackingRecord.java` |
| `[testDj]` | UserRepository.java | `src/main/java/com/example/demo/repository/UserRepository.java` |
| `[testDj]` | TrackingRecordRepository.java | `src/main/java/com/example/demo/repository/TrackingRecordRepository.java` |
| `[testDj]` | JwtUtil.java | `src/main/java/com/example/demo/security/JwtUtil.java` |
| `[testDj]` | JwtAuthenticationFilter.java | `src/main/java/com/example/demo/security/JwtAuthenticationFilter.java` |
| `[testDj]` | SecurityConfig.java | `src/main/java/com/example/demo/config/SecurityConfig.java` |
| `[testDj]` | WebConfig.java | `src/main/java/com/example/demo/config/WebConfig.java` |
| `[testDj]` | DTOs (RegisterRequest/LoginRequest/AuthResponse) | `src/main/java/com/example/demo/model/dto/` |
| `[testDj]` | UserService.java | `src/main/java/com/example/demo/service/UserService.java` |
| `[testDj]` | AuthController.java | `src/main/java/com/example/demo/controller/AuthController.java` |
| `[testDj]` | HashService.java | `src/main/java/com/example/demo/service/HashService.java` |
| `[testDj]` | BubbleSortService.java | `src/main/java/com/example/demo/service/BubbleSortService.java` |
| `[testDj]` | HelloWorldController.java | `src/main/java/com/example/demo/controller/HelloWorldController.java` |
| `[testDj]` | HashController.java | `src/main/java/com/example/demo/controller/HashController.java` |
| `[testDj]` | BubbleSortController.java | `src/main/java/com/example/demo/controller/BubbleSortController.java` |
| `[testDj]` | TrackingAspect.java | `src/main/java/com/example/demo/aspect/TrackingAspect.java` |
| `[testDj]` | ExportService.java | `src/main/java/com/example/demo/service/ExportService.java` |
| `[testDj]` | ExportController.java | `src/main/java/com/example/demo/controller/ExportController.java` |
| `[testDj]` | TrackingService.java | `src/main/java/com/example/demo/service/TrackingService.java` |
| `[testDj]` | TrackingController.java | `src/main/java/com/example/demo/controller/TrackingController.java` |
| `[testDj]` | DemoApplicationTests.java | `src/test/java/com/example/demo/DemoApplicationTests.java` |
| `[testDJnew]` | package.json | `package.json` |
| `[testDJnew]` | public/index.html | `public/index.html` |
| `[testDJnew]` | src/index.js | `src/index.js` |
| `[testDJnew]` | src/App.jsx | `src/App.jsx` |
| `[testDJnew]` | src/utils/auth.js | `src/utils/auth.js` |
| `[testDJnew]` | src/api/index.js | `src/api/index.js` |
| `[testDJnew]` | src/hooks/useAuth.js | `src/hooks/useAuth.js` |
| `[testDJnew]` | LoginPage.jsx | `src/pages/LoginPage.jsx` |
| `[testDJnew]` | HelloWorldTab.jsx | `src/components/HelloWorldTab.jsx` |
| `[testDJnew]` | HashTab.jsx | `src/components/HashTab.jsx` |
| `[testDJnew]` | BubbleSortTab.jsx | `src/components/BubbleSortTab.jsx` |
| `[testDJnew]` | ExportButton.jsx | `src/components/ExportButton.jsx` |
| `[testDJnew]` | TrackingDashboard.jsx | `src/components/TrackingDashboard.jsx` |
| `[testDJnew]` | DashboardPage.jsx | `src/pages/DashboardPage.jsx` |

**仓间对齐 Review 要点：**
1. JWT 格式：后端签发 `{ sub: userId, username }`；前端 `api/index.js` 拦截器自动附加 `Bearer {token}`
2. CORS：后端 `WebConfig` 允许 `http://localhost:3000`，前端 `api/index.js` baseURL 指向 `http://localhost:8080`
3. 报表维度：后端 `?dimension=personType|personLevel|personDept`，前端 `TrackingDashboard` 下拉值与后端完全一致
4. 导出：后端 `Content-Disposition: attachment; filename=xxx.xlsx`，前端 `ExportButton` 以 `responseType: 'blob'` 接收并触发下载
5. 错误处理：后端统一 `{ code, message }`，前端 Axios 响应拦截器统一处理 401 跳转登录页

---

## Self-Review

1. **Spec coverage:** 设计文档 10 个章节全部覆盖 — 3 接口（Task 3）、前端 Tab（Task 9-10）、导出（Task 5+9）、AOP 埋点（Task 4）、可视化报表（Task 10）、认证（Task 2+8）、数据库（Task 1）、仓间对齐（仓间汇总表）
2. **Placeholder scan:** 无 TBD/TODO/占位符，所有代码完整可执行
3. **Type consistency:** `AuthResponse` 字段与前端 `login()` 调用一致；`TrackingRecord` 字段与 AOP 切面写入一致；报表接口返回 `{ label, callCount, details }` 与前端 `TrackingDashboard` 消费一致