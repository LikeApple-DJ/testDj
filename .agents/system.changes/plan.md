# 跨仓算法服务 + 埋点报表 — 实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 在 testDj（后端 Spring Boot）与 testDJnew（前端 React）两仓中实现三个算法接口（helloworld/hash/bubblesort）、导出功能、埋点日志及可视化报表。

**Architecture:** 后端 Spring Boot 3.2 提供 REST API，通过 JWT 做身份识别，H2 内存数据库存储埋点日志与用户维度数据；前端 React 18 通过 axios 调用后端，ECharts 5 渲染折线图/饼图/柱状图。仓间交互边界为 5 个 API 契约。

**Tech Stack:** Java 17 + Spring Boot 3.2 + H2 + Apache POI + JWT (jjwt) | React 18 + axios + ECharts 5 + react-tabs

**Repos:**
- `[testDj]` = `/root/.agentix/agentic-dev/runs/DEV-966dcd0a-7905-11f1-9649-3b4281182f10-7aa717a8-9632-4f0d-b727-a17c31bc687f/worktree/testDj-main` — 后端
- `[testDJnew]` = `/root/.agentix/agentic-dev/runs/DEV-966dcd0a-7905-11f1-9649-3b4281182f10-7aa717a8-9632-4f0d-b727-a17c31bc687f/worktree/testDJnew-main` — 前端

---

## Global Constraints

- Java 17+, Spring Boot 3.2.x, Maven 3.8+
- React 18, ECharts 5, react-tabs
- 所有接口路径以 `/api/` 为前缀
- 导出格式：Excel (.xlsx)，后端生成 `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet` 流
- 身份识别：JWT Token（Header: `Authorization: Bearer <token>`），解析 username → 关联 user_profile 表
- 埋点：每次 /api/* 调用写入 invocation_log 表（非 /api/stats 和 /api/export 自身）
- CORS 允许前端跨域（localhost:3000）
- 数据库：H2 内存数据库，启动时自动建表 + 初始化 user_profile 种子数据
- 哈希算法：MD5、SHA-1、SHA-256，通过 algorithm 参数选择
- 冒泡排序：输入 JSON 整数数组，返回排序结果 + 每步中间状态
- 报表维度：type（人员类型）、level（人员层级）、department（人员部门），chart 参数：line/pie/bar

---

## Task 1: 后端项目骨架 — pom.xml + 启动类 + 配置

**Files:**
- Create: `[testDj] pom.xml`
- Create: `[testDj] src/main/java/com/example/demo/DemoApplication.java`
- Create: `[testDj] src/main/resources/application.yml`

**Interfaces:**
- Produces: `DemoApplication` 启动类（Spring Boot 入口），`application.yml` 配置，`pom.xml` 依赖声明

- [ ] **Step 1: 创建 pom.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.5</version>
        <relativePath/>
    </parent>
    <groupId>com.example</groupId>
    <artifactId>demo</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>demo</name>
    <description>Algorithm Service with Tracking</description>
    <properties>
        <java.version>17</java.version>
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
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>0.12.5</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>0.12.5</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>0.12.5</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.apache.poi</groupId>
            <artifactId>poi-ooxml</artifactId>
            <version>5.2.5</version>
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

- [ ] **Step 2: 创建 DemoApplication.java**

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

- [ ] **Step 3: 创建 application.yml**

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password:
  h2:
    console:
      enabled: true
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: update
    show-sql: false
  sql:
    init:
      mode: always

app:
  jwt:
    secret: cross-repo-demo-secret-key-2025-min-256-bits-long!!
    expiration-ms: 3600000
```

- [ ] **Step 4: 验证编译**

Run: `cd /root/.agentix/agentic-dev/runs/DEV-966dcd0a-7905-11f1-9649-3b4281182f10-7aa717a8-9632-4f0d-b727-a17c31bc687f/worktree/testDj-main && mvn compile -q 2>&1`
Expected: BUILD SUCCESS

---

## Task 2: 后端数据模型 — Entity + Repository + 种子数据

**Files:**
- Create: `[testDj] src/main/java/com/example/demo/model/InvocationLog.java`
- Create: `[testDj] src/main/java/com/example/demo/model/UserProfile.java`
- Create: `[testDj] src/main/java/com/example/demo/repository/InvocationLogRepository.java`
- Create: `[testDj] src/main/java/com/example/demo/repository/UserProfileRepository.java`
- Create: `[testDj] src/main/resources/data.sql`

**Interfaces:**
- Consumes: JPA auto-scan（由 Task 1 的 application.yml 配置激活）
- Produces:
  - `InvocationLog` entity: `id (Long)`, `username (String)`, `api (String)`, `timestamp (LocalDateTime)`
  - `UserProfile` entity: `id (Long)`, `username (String, unique)`, `type (String)`, `level (String)`, `department (String)`
  - `InvocationLogRepository extends JpaRepository<InvocationLog, Long>` + 聚合查询方法
  - `UserProfileRepository extends JpaRepository<UserProfile, Long>` + `findByUsername(String)`

- [ ] **Step 1: 创建 InvocationLog.java**

```java
package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "invocation_log")
public class InvocationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String api;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public InvocationLog() {}

    public InvocationLog(String username, String api, LocalDateTime timestamp) {
        this.username = username;
        this.api = api;
        this.timestamp = timestamp;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getApi() { return api; }
    public void setApi(String api) { this.api = api; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
```

- [ ] **Step 2: 创建 UserProfile.java**

```java
package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "user_profile")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String level;

    @Column(nullable = false)
    private String department;

    public UserProfile() {}

    public UserProfile(String username, String type, String level, String department) {
        this.username = username;
        this.type = type;
        this.level = level;
        this.department = department;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}
```

- [ ] **Step 3: 创建 InvocationLogRepository.java**

```java
package com.example.demo.repository;

import com.example.demo.model.InvocationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvocationLogRepository extends JpaRepository<InvocationLog, Long> {

    long countByApi(String api);

    long countByUsername(String username);

    @Query("SELECT i.api, COUNT(i) FROM InvocationLog i GROUP BY i.api")
    List<Object[]> countGroupByApi();

    @Query("SELECT i.username, COUNT(i) FROM InvocationLog i GROUP BY i.username")
    List<Object[]> countGroupByUsername();

    @Query("SELECT u.type, COUNT(i) FROM InvocationLog i JOIN UserProfile u ON i.username = u.username GROUP BY u.type")
    List<Object[]> countGroupByUserType();

    @Query("SELECT u.level, COUNT(i) FROM InvocationLog i JOIN UserProfile u ON i.username = u.username GROUP BY u.level")
    List<Object[]> countGroupByUserLevel();

    @Query("SELECT u.department, COUNT(i) FROM InvocationLog i JOIN UserProfile u ON i.username = u.username GROUP BY u.department")
    List<Object[]> countGroupByUserDepartment();

    @Query("SELECT u.type, i.api, COUNT(i) FROM InvocationLog i JOIN UserProfile u ON i.username = u.username GROUP BY u.type, i.api")
    List<Object[]> countGroupByTypeAndApi();
}
```

- [ ] **Step 4: 创建 UserProfileRepository.java**

```java
package com.example.demo.repository;

import com.example.demo.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByUsername(String username);
}
```

- [ ] **Step 5: 创建 data.sql 种子数据**

```sql
INSERT INTO user_profile (username, type, level, department) VALUES ('admin', '正式员工', 'P8', '技术部');
INSERT INTO user_profile (username, type, level, department) VALUES ('zhangsan', '正式员工', 'P6', '技术部');
INSERT INTO user_profile (username, type, level, department) VALUES ('lisi', '外包', 'P5', '产品部');
INSERT INTO user_profile (username, type, level, department) VALUES ('wangwu', '实习生', 'P3', '产品部');
INSERT INTO user_profile (username, type, level, department) VALUES ('zhaoliu', '正式员工', 'P7', '数据部');
```

- [ ] **Step 6: 验证编译**

Run: `cd /root/.agentix/agentic-dev/runs/DEV-966dcd0a-7905-11f1-9649-3b4281182f10-7aa717a8-9632-4f0d-b727-a17c31bc687f/worktree/testDj-main && mvn compile -q 2>&1`
Expected: BUILD SUCCESS

---

## Task 3: 后端安全 — JWT + CORS 配置

**Files:**
- Create: `[testDj] src/main/java/com/example/demo/security/JwtTokenProvider.java`
- Create: `[testDj] src/main/java/com/example/demo/security/JwtTokenFilter.java`
- Create: `[testDj] src/main/java/com/example/demo/config/SecurityConfig.java`
- Create: `[testDj] src/main/java/com/example/demo/config/WebConfig.java`

**Interfaces:**
- Consumes: `application.yml` 中的 `app.jwt.secret` / `app.jwt.expiration-ms`
- Produces:
  - `JwtTokenProvider.generateToken(String username)` → `String`
  - `JwtTokenProvider.getUsernameFromToken(String token)` → `String`
  - `JwtTokenProvider.validateToken(String token)` → `boolean`
  - `JwtTokenFilter` — OncePerRequestFilter，从 Header 解析 Token，设置 SecurityContext
  - `SecurityConfig` — 放行 `/api/**`（仅做身份提取，不做强制认证），禁用 CSRF
  - `WebConfig` — CORS 允许 `http://localhost:3000`，所有方法/Header

- [ ] **Step 1: 创建 JwtTokenProvider.java**

```java
package com.example.demo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long expirationMs;

    public JwtTokenProvider(@Value("${app.jwt.secret}") String secret,
                            @Value("${app.jwt.expiration-ms}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public String generateToken(String username) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
```

- [ ] **Step 2: 创建 JwtTokenFilter.java**

```java
package com.example.demo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = extractToken(request);
        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
            String username = jwtTokenProvider.getUsernameFromToken(token);
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(auth);
        } else {
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken("anonymous", null, Collections.emptyList()));
        }
        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}
```

- [ ] **Step 3: 创建 SecurityConfig.java**

```java
package com.example.demo.config;

import com.example.demo.security.JwtTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenFilter jwtTokenFilter;

    public SecurityConfig(JwtTokenFilter jwtTokenFilter) {
        this.jwtTokenFilter = jwtTokenFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
```

- [ ] **Step 4: 创建 WebConfig.java**

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

- [ ] **Step 5: 验证编译**

Run: `cd /root/.agentix/agentic-dev/runs/DEV-966dcd0a-7905-11f1-9649-3b4281182f10-7aa717a8-9632-4f0d-b727-a17c31bc687f/worktree/testDj-main && mvn compile -q 2>&1`
Expected: BUILD SUCCESS

---

## Task 4: 后端 DTO + Service 层 — 算法服务

**Files:**
- Create: `[testDj] src/main/java/com/example/demo/dto/HashRequest.java`
- Create: `[testDj] src/main/java/com/example/demo/dto/SortRequest.java`
- Create: `[testDj] src/main/java/com/example/demo/service/AlgorithmService.java`

**Interfaces:**
- Consumes: —
- Produces:
  - `HashRequest` record: `String input`, `String algorithm`
  - `SortRequest` record: `List<Integer> array`
  - `AlgorithmService.helloWorld()` → `Map<String, String>` (含 message + timestamp)
  - `AlgorithmService.computeHash(HashRequest req)` → `Map<String, Object>` (含 algorithm, input, hash)
  - `AlgorithmService.bubbleSort(SortRequest req)` → `Map<String, Object>` (含 original, sorted, steps)

- [ ] **Step 1: 创建 HashRequest.java**

```java
package com.example.demo.dto;

public class HashRequest {
    private String input;
    private String algorithm;

    public HashRequest() {}
    public HashRequest(String input, String algorithm) {
        this.input = input;
        this.algorithm = algorithm;
    }

    public String getInput() { return input; }
    public void setInput(String input) { this.input = input; }
    public String getAlgorithm() { return algorithm; }
    public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }
}
```

- [ ] **Step 2: 创建 SortRequest.java**

```java
package com.example.demo.dto;

import java.util.List;

public class SortRequest {
    private List<Integer> array;

    public SortRequest() {}
    public SortRequest(List<Integer> array) { this.array = array; }

    public List<Integer> getArray() { return array; }
    public void setArray(List<Integer> array) { this.array = array; }
}
```

- [ ] **Step 3: 创建 AlgorithmService.java**

```java
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
```

- [ ] **Step 4: 验证编译**

Run: `cd /root/.agentix/agentic-dev/runs/DEV-966dcd0a-7905-11f1-9649-3b4281182f10-7aa717a8-9632-4f0d-b727-a17c31bc687f/worktree/testDj-main && mvn compile -q 2>&1`
Expected: BUILD SUCCESS

---

## Task 5: 后端 Controller 层 — 算法接口 + 埋点

**Files:**
- Create: `[testDj] src/main/java/com/example/demo/controller/AlgorithmController.java`

**Interfaces:**
- Consumes: `AlgorithmService`, `InvocationLogRepository`, `SecurityContextHolder`
- Produces:
  - `GET /api/helloworld` → `{"message":"Hello World","timestamp":"..."}`
  - `POST /api/hash` (body: `{"input":"text","algorithm":"SHA-256"}`) → `{"algorithm":"SHA-256","input":"text","hash":"..."}`
  - `POST /api/bubblesort` (body: `{"array":[3,1,4,1,5]}`) → `{"original":[...],"sorted":[...],"steps":[...]}`

- [ ] **Step 1: 创建 AlgorithmController.java**

```java
package com.example.demo.controller;

import com.example.demo.dto.HashRequest;
import com.example.demo.dto.SortRequest;
import com.example.demo.model.InvocationLog;
import com.example.demo.repository.InvocationLogRepository;
import com.example.demo.service.AlgorithmService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AlgorithmController {

    private final AlgorithmService algorithmService;
    private final InvocationLogRepository invocationLogRepository;

    public AlgorithmController(AlgorithmService algorithmService,
                               InvocationLogRepository invocationLogRepository) {
        this.algorithmService = algorithmService;
        this.invocationLogRepository = invocationLogRepository;
    }

    @GetMapping("/helloworld")
    public ResponseEntity<Map<String, String>> helloWorld() {
        logInvocation("/api/helloworld");
        return ResponseEntity.ok(algorithmService.helloWorld());
    }

    @PostMapping("/hash")
    public ResponseEntity<Map<String, Object>> hash(@RequestBody HashRequest request) {
        logInvocation("/api/hash");
        return ResponseEntity.ok(algorithmService.computeHash(request));
    }

    @PostMapping("/bubblesort")
    public ResponseEntity<Map<String, Object>> bubbleSort(@RequestBody SortRequest request) {
        logInvocation("/api/bubblesort");
        return ResponseEntity.ok(algorithmService.bubbleSort(request));
    }

    private void logInvocation(String api) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = (auth != null && auth.getName() != null) ? auth.getName() : "anonymous";
        InvocationLog log = new InvocationLog(username, api, LocalDateTime.now());
        invocationLogRepository.save(log);
    }
}
```

- [ ] **Step 2: 验证编译**

Run: `cd /root/.agentix/agentic-dev/runs/DEV-966dcd0a-7905-11f1-9649-3b4281182f10-7aa717a8-9632-4f0d-b727-a17c31bc687f/worktree/testDj-main && mvn compile -q 2>&1`
Expected: BUILD SUCCESS

---

## Task 6: 后端导出服务 — Excel 导出

**Files:**
- Create: `[testDj] src/main/java/com/example/demo/service/ExportService.java`
- Create: `[testDj] src/main/java/com/example/demo/controller/ExportController.java`

**Interfaces:**
- Consumes: `AlgorithmService`
- Produces:
  - `GET /api/export?tab=helloworld|hash|bubblesort` → `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet` 文件流

- [ ] **Step 1: 创建 ExportService.java**

```java
package com.example.demo.service;

import com.example.demo.dto.HashRequest;
import com.example.demo.dto.SortRequest;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

@Service
public class ExportService {

    private final AlgorithmService algorithmService;

    public ExportService(AlgorithmService algorithmService) {
        this.algorithmService = algorithmService;
    }

    public byte[] exportTab(String tab) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(tab);
            CellStyle headerStyle = createHeaderStyle(workbook);

            switch (tab.toLowerCase()) {
                case "helloworld" -> fillHelloWorld(sheet, headerStyle);
                case "hash" -> fillHash(sheet, headerStyle);
                case "bubblesort" -> fillBubbleSort(sheet, headerStyle);
                default -> throw new IllegalArgumentException("Unknown tab: " + tab);
            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            return bos.toByteArray();
        }
    }

    private void fillHelloWorld(Sheet sheet, CellStyle headerStyle) {
        Row header = sheet.createRow(0);
        createCell(header, 0, "Message", headerStyle);
        createCell(header, 1, "Timestamp", headerStyle);

        Map<String, String> result = algorithmService.helloWorld();
        Row row = sheet.createRow(1);
        row.createCell(0).setCellValue(result.get("message"));
        row.createCell(1).setCellValue(result.get("timestamp"));

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    private void fillHash(Sheet sheet, CellStyle headerStyle) {
        Row header = sheet.createRow(0);
        createCell(header, 0, "Algorithm", headerStyle);
        createCell(header, 1, "Input", headerStyle);
        createCell(header, 2, "Hash", headerStyle);

        String[] algorithms = {"MD5", "SHA-1", "SHA-256"};
        String sampleInput = "Hello World";
        int rowIdx = 1;
        for (String algo : algorithms) {
            Map<String, Object> result = algorithmService.computeHash(new HashRequest(sampleInput, algo));
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue((String) result.get("algorithm"));
            row.createCell(1).setCellValue((String) result.get("input"));
            row.createCell(2).setCellValue((String) result.get("hash"));
        }

        for (int i = 0; i < 3; i++) sheet.autoSizeColumn(i);
    }

    private void fillBubbleSort(Sheet sheet, CellStyle headerStyle) {
        Row header = sheet.createRow(0);
        createCell(header, 0, "Original", headerStyle);
        createCell(header, 1, "Sorted", headerStyle);
        createCell(header, 2, "Total Steps", headerStyle);

        Map<String, Object> result = algorithmService.bubbleSort(
                new SortRequest(Arrays.asList(3, 1, 4, 1, 5)));
        Row row = sheet.createRow(1);
        row.createCell(0).setCellValue(result.get("original").toString());
        row.createCell(1).setCellValue(result.get("sorted").toString());
        @SuppressWarnings("unchecked")
        List<List<Integer>> steps = (List<List<Integer>>) result.get("steps");
        row.createCell(2).setCellValue(steps.size() - 1);

        for (int i = 0; i < 3; i++) sheet.autoSizeColumn(i);
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private void createCell(Row row, int col, String value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }
}
```

- [ ] **Step 2: 创建 ExportController.java**

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
    public ResponseEntity<byte[]> export(@RequestParam(defaultValue = "helloworld") String tab)
            throws IOException {
        byte[] data = exportService.exportTab(tab);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=" + tab + "_export.xlsx")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(data);
    }
}
```

- [ ] **Step 3: 验证编译**

Run: `cd /root/.agentix/agentic-dev/runs/DEV-966dcd0a-7905-11f1-9649-3b4281182f10-7aa717a8-9632-4f0d-b727-a17c31bc687f/worktree/testDj-main && mvn compile -q 2>&1`
Expected: BUILD SUCCESS

---

## Task 7: 后端统计服务 — 埋点查询（多维度）

**Files:**
- Create: `[testDj] src/main/java/com/example/demo/service/StatsService.java`
- Create: `[testDj] src/main/java/com/example/demo/controller/StatsController.java`

**Interfaces:**
- Consumes: `InvocationLogRepository`
- Produces:
  - `GET /api/stats?dimension=type|level|department|api&chart=line|pie|bar` → `{"dimension":"type","data":[{"key":"正式员工","count":42},...]}`

- [ ] **Step 1: 创建 StatsService.java**

```java
package com.example.demo.service;

import com.example.demo.repository.InvocationLogRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatsService {

    private final InvocationLogRepository repository;

    public StatsService(InvocationLogRepository repository) {
        this.repository = repository;
    }

    public Map<String, Object> getStats(String dimension) {
        List<Object[]> raw;
        switch (dimension.toLowerCase()) {
            case "type"       -> raw = repository.countGroupByUserType();
            case "level"      -> raw = repository.countGroupByUserLevel();
            case "department" -> raw = repository.countGroupByUserDepartment();
            case "api"        -> raw = repository.countGroupByApi();
            default -> throw new IllegalArgumentException(
                    "Unknown dimension: " + dimension + ". Allowed: type, level, department, api");
        }

        List<Map<String, Object>> data = raw.stream()
                .map(row -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("key", row[0]);
                    item.put("count", row[1]);
                    return item;
                })
                .collect(Collectors.toList());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("dimension", dimension);
        result.put("data", data);
        return result;
    }
}
```

- [ ] **Step 2: 创建 StatsController.java**

```java
package com.example.demo.controller;

import com.example.demo.service.StatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats(
            @RequestParam(defaultValue = "type") String dimension,
            @RequestParam(defaultValue = "bar") String chart) {
        return ResponseEntity.ok(statsService.getStats(dimension));
    }
}
```

- [ ] **Step 3: 验证编译**

Run: `cd /root/.agentix/agentic-dev/runs/DEV-966dcd0a-7905-11f1-9649-3b4281182f10-7aa717a8-9632-4f0d-b727-a17c31bc687f/worktree/testDj-main && mvn compile -q 2>&1`
Expected: BUILD SUCCESS

---

## Task 8: 前端项目骨架 — React + 依赖 + 入口

**Files:**
- Create: `[testDJnew] package.json`
- Create: `[testDJnew] public/index.html`
- Create: `[testDJnew] src/index.jsx`
- Create: `[testDJnew] src/App.jsx`

**Interfaces:**
- Produces: React 18 SPA 入口，react-tabs 页面框架，路由占位

- [ ] **Step 1: 创建 package.json**

```json
{
  "name": "testdjnew-frontend",
  "version": "0.1.0",
  "private": true,
  "dependencies": {
    "axios": "^1.7.2",
    "echarts": "^5.5.0",
    "echarts-for-react": "^3.0.2",
    "react": "^18.3.1",
    "react-dom": "^18.3.1",
    "react-scripts": "5.0.1",
    "react-tabs": "^6.0.2"
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

- [ ] **Step 2: 创建 public/index.html**

```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>算法服务面板</title>
</head>
<body>
    <div id="root"></div>
</body>
</html>
```

- [ ] **Step 3: 创建 src/index.jsx**

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

- [ ] **Step 4: 创建 src/App.jsx**

```jsx
import React from 'react';
import DashboardPage from './pages/DashboardPage';
import ReportPage from './pages/ReportPage';

function App() {
    const [page, setPage] = React.useState('dashboard');

    return (
        <div style={{ maxWidth: 960, margin: '0 auto', padding: 24, fontFamily: 'sans-serif' }}>
            <nav style={{ marginBottom: 24, display: 'flex', gap: 16 }}>
                <button onClick={() => setPage('dashboard')}
                        style={navBtnStyle(page === 'dashboard')}>
                    算法服务
                </button>
                <button onClick={() => setPage('report')}
                        style={navBtnStyle(page === 'report')}>
                    调用报表
                </button>
            </nav>

            {page === 'dashboard' ? <DashboardPage /> : <ReportPage />}
        </div>
    );
}

function navBtnStyle(active) {
    return {
        padding: '8px 20px',
        border: 'none',
        borderRadius: 6,
        cursor: 'pointer',
        fontWeight: 'bold',
        background: active ? '#1677ff' : '#e8e8e8',
        color: active ? '#fff' : '#333',
    };
}

export default App;
```

- [ ] **Step 5: 安装依赖**

Run: `cd /root/.agentix/agentic-dev/runs/DEV-966dcd0a-7905-11f1-9649-3b4281182f10-7aa717a8-9632-4f0d-b727-a17c31bc687f/worktree/testDJnew-main && npm install 2>&1 | tail -5`
Expected: 无错误

---

## Task 9: 前端 API 服务层 + JWT 工具

**Files:**
- Create: `[testDJnew] src/services/api.js`
- Create: `[testDJnew] src/utils/auth.js`

**Interfaces:**
- Consumes: —
- Produces:
  - `auth.js`: `getToken()`, `setToken(token)`, `getUsername()`, `generateMockToken(username)`
  - `api.js`: `helloWorld()`, `computeHash(input, algorithm)`, `bubbleSort(array)`, `exportTab(tab)`, `getStats(dimension, chart)`

- [ ] **Step 1: 创建 src/utils/auth.js**

```js
const TOKEN_KEY = 'app_jwt_token';
const USERNAME_KEY = 'app_username';

export function getToken() {
    return localStorage.getItem(TOKEN_KEY);
}

export function setToken(token) {
    localStorage.setItem(TOKEN_KEY, token);
}

export function getUsername() {
    return localStorage.getItem(USERNAME_KEY) || 'anonymous';
}

export function setUsername(username) {
    localStorage.setItem(USERNAME_KEY, username);
}

export function generateMockToken(username) {
    const header = btoa(JSON.stringify({ alg: 'HS256', typ: 'JWT' }));
    const payload = btoa(JSON.stringify({ sub: username, iat: Date.now() / 1000 }));
    return `${header}.${payload}.mock-signature`;
}
```

- [ ] **Step 2: 创建 src/services/api.js**

```js
import axios from 'axios';
import { getToken, generateMockToken, setToken, setUsername } from '../utils/auth';

const BASE_URL = 'http://localhost:8080/api';

const client = axios.create({ baseURL: BASE_URL });

client.interceptors.request.use((config) => {
    let token = getToken();
    if (!token) {
        const username = 'admin';
        setUsername(username);
        token = generateMockToken(username);
        setToken(token);
    }
    config.headers.Authorization = `Bearer ${token}`;
    return config;
});

export async function helloWorld() {
    const { data } = await client.get('/helloworld');
    return data;
}

export async function computeHash(input, algorithm) {
    const { data } = await client.post('/hash', { input, algorithm });
    return data;
}

export async function bubbleSort(array) {
    const { data } = await client.post('/bubblesort', { array });
    return data;
}

export async function exportTab(tab) {
    const response = await client.get('/export', {
        params: { tab },
        responseType: 'blob',
    });
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', `${tab}_export.xlsx`);
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
}

export async function getStats(dimension = 'type') {
    const { data } = await client.get('/stats', { params: { dimension } });
    return data;
}
```

---

## Task 10: 前端 Tab 组件 — HelloWorld / Hash / BubbleSort

**Files:**
- Create: `[testDJnew] src/components/HelloWorldTab.jsx`
- Create: `[testDJnew] src/components/HashTab.jsx`
- Create: `[testDJnew] src/components/BubbleSortTab.jsx`
- Create: `[testDJnew] src/components/ExportButton.jsx`

**Interfaces:**
- Consumes: `api.js` (helloWorld, computeHash, bubbleSort, exportTab)
- Produces:
  - `HelloWorldTab` — 展示 message + timestamp
  - `HashTab` — 文本输入框 + 算法下拉选择 + 哈希结果展示
  - `BubbleSortTab` — JSON 数组输入框 + 原始/排序/步骤展示
  - `ExportButton` — prop: `tab`，点击触发 exportTab

- [ ] **Step 1: 创建 HelloWorldTab.jsx**

```jsx
import React, { useState, useEffect } from 'react';
import { helloWorld } from '../services/api';
import ExportButton from './ExportButton';

export default function HelloWorldTab() {
    const [data, setData] = useState(null);
    const [loading, setLoading] = useState(false);

    const fetchData = async () => {
        setLoading(true);
        try {
            const result = await helloWorld();
            setData(result);
        } catch (e) {
            setData({ message: 'Error', timestamp: e.message });
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => { fetchData(); }, []);

    return (
        <div style={{ padding: 16 }}>
            <button onClick={fetchData} disabled={loading}
                    style={btnStyle}>
                {loading ? '请求中...' : '调用 HelloWorld'}
            </button>
            <ExportButton tab="helloworld" />
            {data && (
                <pre style={preStyle}>
                    {JSON.stringify(data, null, 2)}
                </pre>
            )}
        </div>
    );
}

const btnStyle = {
    padding: '8px 16px', marginRight: 12, cursor: 'pointer',
    background: '#1677ff', color: '#fff', border: 'none', borderRadius: 6,
};
const preStyle = {
    marginTop: 16, padding: 16, background: '#f5f5f5',
    borderRadius: 8, overflow: 'auto',
};
```

- [ ] **Step 2: 创建 HashTab.jsx**

```jsx
import React, { useState } from 'react';
import { computeHash } from '../services/api';
import ExportButton from './ExportButton';

const ALGORITHMS = ['MD5', 'SHA-1', 'SHA-256'];

export default function HashTab() {
    const [input, setInput] = useState('Hello World');
    const [algorithm, setAlgorithm] = useState('SHA-256');
    const [result, setResult] = useState(null);
    const [loading, setLoading] = useState(false);

    const handleCompute = async () => {
        setLoading(true);
        try {
            const data = await computeHash(input, algorithm);
            setResult(data);
        } catch (e) {
            setResult({ error: e.message });
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={{ padding: 16 }}>
            <div style={{ marginBottom: 12 }}>
                <label style={{ display: 'block', marginBottom: 4, fontWeight: 'bold' }}>输入文本</label>
                <input type="text" value={input} onChange={e => setInput(e.target.value)}
                       style={inputStyle} />
            </div>
            <div style={{ marginBottom: 12 }}>
                <label style={{ display: 'block', marginBottom: 4, fontWeight: 'bold' }}>算法</label>
                <select value={algorithm} onChange={e => setAlgorithm(e.target.value)}
                        style={inputStyle}>
                    {ALGORITHMS.map(a => <option key={a} value={a}>{a}</option>)}
                </select>
            </div>
            <button onClick={handleCompute} disabled={loading} style={btnStyle}>
                {loading ? '计算中...' : '计算哈希'}
            </button>
            <ExportButton tab="hash" />
            {result && (
                <pre style={preStyle}>{JSON.stringify(result, null, 2)}</pre>
            )}
        </div>
    );
}

const btnStyle = {
    padding: '8px 16px', marginRight: 12, cursor: 'pointer',
    background: '#1677ff', color: '#fff', border: 'none', borderRadius: 6,
};
const inputStyle = {
    width: '100%', padding: '8px 12px', border: '1px solid #d9d9d9',
    borderRadius: 6, fontSize: 14,
};
const preStyle = {
    marginTop: 16, padding: 16, background: '#f5f5f5',
    borderRadius: 8, overflow: 'auto',
};
```

- [ ] **Step 3: 创建 BubbleSortTab.jsx**

```jsx
import React, { useState } from 'react';
import { bubbleSort } from '../services/api';
import ExportButton from './ExportButton';

export default function BubbleSortTab() {
    const [input, setInput] = useState('[3,1,4,1,5]');
    const [result, setResult] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    const handleSort = async () => {
        setError('');
        let array;
        try {
            array = JSON.parse(input);
            if (!Array.isArray(array) || !array.every(n => typeof n === 'number')) {
                setError('请输入合法的 JSON 整数数组，如 [3,1,4,1,5]');
                return;
            }
        } catch {
            setError('JSON 格式错误，请检查输入');
            return;
        }
        setLoading(true);
        try {
            const data = await bubbleSort(array);
            setResult(data);
        } catch (e) {
            setError(e.message);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={{ padding: 16 }}>
            <div style={{ marginBottom: 12 }}>
                <label style={{ display: 'block', marginBottom: 4, fontWeight: 'bold' }}>
                    输入整数数组（JSON 格式）
                </label>
                <input type="text" value={input} onChange={e => setInput(e.target.value)}
                       style={inputStyle} />
            </div>
            <button onClick={handleSort} disabled={loading} style={btnStyle}>
                {loading ? '排序中...' : '执行冒泡排序'}
            </button>
            <ExportButton tab="bubblesort" />
            {error && <p style={{ color: 'red', marginTop: 12 }}>{error}</p>}
            {result && (
                <pre style={preStyle}>{JSON.stringify(result, null, 2)}</pre>
            )}
        </div>
    );
}

const btnStyle = {
    padding: '8px 16px', marginRight: 12, cursor: 'pointer',
    background: '#1677ff', color: '#fff', border: 'none', borderRadius: 6,
};
const inputStyle = {
    width: '100%', padding: '8px 12px', border: '1px solid #d9d9d9',
    borderRadius: 6, fontSize: 14,
};
const preStyle = {
    marginTop: 16, padding: 16, background: '#f5f5f5',
    borderRadius: 8, overflow: 'auto', maxHeight: 400,
};
```

- [ ] **Step 4: 创建 ExportButton.jsx**

```jsx
import React, { useState } from 'react';
import { exportTab } from '../services/api';

export default function ExportButton({ tab }) {
    const [exporting, setExporting] = useState(false);

    const handleExport = async () => {
        setExporting(true);
        try {
            await exportTab(tab);
        } catch (e) {
            alert('导出失败: ' + e.message);
        } finally {
            setExporting(false);
        }
    };

    return (
        <button onClick={handleExport} disabled={exporting}
                style={{
                    padding: '8px 16px', cursor: 'pointer',
                    background: '#52c41a', color: '#fff', border: 'none', borderRadius: 6,
                }}>
            {exporting ? '导出中...' : '📥 导出 Excel'}
        </button>
    );
}
```

---

## Task 11: 前端 DashboardPage — 三 Tab 集成

**Files:**
- Create: `[testDJnew] src/pages/DashboardPage.jsx`

**Interfaces:**
- Consumes: `HelloWorldTab`, `HashTab`, `BubbleSortTab`
- Produces: `DashboardPage` — 三 Tab 布局

- [ ] **Step 1: 创建 DashboardPage.jsx**

```jsx
import React from 'react';
import { Tab, Tabs, TabList, TabPanel } from 'react-tabs';
import 'react-tabs/style/react-tabs.css';
import HelloWorldTab from '../components/HelloWorldTab';
import HashTab from '../components/HashTab';
import BubbleSortTab from '../components/BubbleSortTab';

export default function DashboardPage() {
    return (
        <div style={{ background: '#fff', borderRadius: 12, padding: 24, boxShadow: '0 2px 8px rgba(0,0,0,0.1)' }}>
            <h2 style={{ marginTop: 0 }}>算法服务面板</h2>
            <Tabs>
                <TabList>
                    <Tab>Hello World</Tab>
                    <Tab>Hash 哈希</Tab>
                    <Tab>Bubble Sort 冒泡排序</Tab>
                </TabList>
                <TabPanel>
                    <HelloWorldTab />
                </TabPanel>
                <TabPanel>
                    <HashTab />
                </TabPanel>
                <TabPanel>
                    <BubbleSortTab />
                </TabPanel>
            </Tabs>
        </div>
    );
}
```

---

## Task 12: 前端图表组件 — LineChart / PieChart / BarChart

**Files:**
- Create: `[testDJnew] src/components/LineChart.jsx`
- Create: `[testDJnew] src/components/PieChart.jsx`
- Create: `[testDJnew] src/components/BarChart.jsx`
- Create: `[testDJnew] src/components/DimensionSelector.jsx`

**Interfaces:**
- Consumes: `echarts-for-react`
- Produces:
  - `LineChart({ data })` — 折线图
  - `PieChart({ data })` — 饼图
  - `BarChart({ data })` — 柱状图
  - `DimensionSelector({ value, onChange })` — 维度下拉

- [ ] **Step 1: 创建 LineChart.jsx**

```jsx
import React from 'react';
import ReactECharts from 'echarts-for-react';

export default function LineChart({ data = [] }) {
    const option = {
        title: { text: '调用次数趋势（折线图）', left: 'center' },
        tooltip: { trigger: 'axis' },
        xAxis: {
            type: 'category',
            data: data.map(d => d.key),
            axisLabel: { rotate: 30 },
        },
        yAxis: { type: 'value', name: '调用次数' },
        series: [{
            type: 'line',
            data: data.map(d => d.count),
            smooth: true,
            itemStyle: { color: '#1677ff' },
            areaStyle: { color: 'rgba(22,119,255,0.1)' },
        }],
    };

    return <ReactECharts option={option} style={{ height: 400 }} />;
}
```

- [ ] **Step 2: 创建 PieChart.jsx**

```jsx
import React from 'react';
import ReactECharts from 'echarts-for-react';

export default function PieChart({ data = [] }) {
    const option = {
        title: { text: '调用占比（饼图）', left: 'center' },
        tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
        series: [{
            type: 'pie',
            radius: ['40%', '70%'],
            data: data.map(d => ({ name: d.key, value: d.count })),
            emphasis: {
                itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0,0,0,0.5)' },
            },
            label: { formatter: '{b}: {d}%' },
        }],
    };

    return <ReactECharts option={option} style={{ height: 400 }} />;
}
```

- [ ] **Step 3: 创建 BarChart.jsx**

```jsx
import React from 'react';
import ReactECharts from 'echarts-for-react';

export default function BarChart({ data = [] }) {
    const option = {
        title: { text: '调用次数分布（柱状图）', left: 'center' },
        tooltip: { trigger: 'axis' },
        xAxis: {
            type: 'category',
            data: data.map(d => d.key),
            axisLabel: { rotate: 30 },
        },
        yAxis: { type: 'value', name: '调用次数' },
        series: [{
            type: 'bar',
            data: data.map(d => d.count),
            itemStyle: {
                color: {
                    type: 'linear',
                    x: 0, y: 0, x2: 0, y2: 1,
                    colorStops: [
                        { offset: 0, color: '#1677ff' },
                        { offset: 1, color: '#69b1ff' },
                    ],
                },
                borderRadius: [6, 6, 0, 0],
            },
        }],
    };

    return <ReactECharts option={option} style={{ height: 400 }} />;
}
```

- [ ] **Step 4: 创建 DimensionSelector.jsx**

```jsx
import React from 'react';

const DIMENSIONS = [
    { value: 'type', label: '人员类型' },
    { value: 'level', label: '人员层级' },
    { value: 'department', label: '人员部门' },
    { value: 'api', label: '接口' },
];

export default function DimensionSelector({ value, onChange }) {
    return (
        <div style={{ marginBottom: 16, display: 'flex', alignItems: 'center', gap: 12 }}>
            <span style={{ fontWeight: 'bold' }}>分析维度：</span>
            <select value={value} onChange={e => onChange(e.target.value)}
                    style={{ padding: '8px 12px', borderRadius: 6, border: '1px solid #d9d9d9', fontSize: 14 }}>
                {DIMENSIONS.map(d => <option key={d.value} value={d.value}>{d.label}</option>)}
            </select>
        </div>
    );
}
```

---

## Task 13: 前端 ReportPage — 报表页面集成

**Files:**
- Create: `[testDJnew] src/pages/ReportPage.jsx`

**Interfaces:**
- Consumes: `LineChart`, `PieChart`, `BarChart`, `DimensionSelector`, `getStats`
- Produces: `ReportPage` — 维度选择 + 三种图表展示

- [ ] **Step 1: 创建 ReportPage.jsx**

```jsx
import React, { useState, useEffect, useCallback } from 'react';
import { getStats } from '../services/api';
import DimensionSelector from '../components/DimensionSelector';
import LineChart from '../components/LineChart';
import PieChart from '../components/PieChart';
import BarChart from '../components/BarChart';

export default function ReportPage() {
    const [dimension, setDimension] = useState('type');
    const [data, setData] = useState([]);
    const [loading, setLoading] = useState(false);

    const fetchStats = useCallback(async () => {
        setLoading(true);
        try {
            const result = await getStats(dimension);
            setData(result.data || []);
        } catch (e) {
            console.error('Failed to fetch stats:', e);
            setData([]);
        } finally {
            setLoading(false);
        }
    }, [dimension]);

    useEffect(() => { fetchStats(); }, [fetchStats]);

    return (
        <div style={{ background: '#fff', borderRadius: 12, padding: 24, boxShadow: '0 2px 8px rgba(0,0,0,0.1)' }}>
            <h2 style={{ marginTop: 0 }}>调用情况报表</h2>
            <DimensionSelector value={dimension} onChange={setDimension} />
            {loading ? (
                <p>加载中...</p>
            ) : (
                <div style={{ display: 'flex', flexDirection: 'column', gap: 24 }}>
                    <div style={{ background: '#fafafa', borderRadius: 8, padding: 16 }}>
                        <LineChart data={data} />
                    </div>
                    <div style={{ background: '#fafafa', borderRadius: 8, padding: 16 }}>
                        <PieChart data={data} />
                    </div>
                    <div style={{ background: '#fafafa', borderRadius: 8, padding: 16 }}>
                        <BarChart data={data} />
                    </div>
                </div>
            )}
        </div>
    );
}
```

---

## 仓间对齐点检查清单

| # | 对齐项 | 后端 (testDj) | 前端 (testDJnew) | 状态 |
|---|--------|---------------|------------------|------|
| 1 | `/api/helloworld` GET | `AlgorithmController.helloWorld()` → `{"message","timestamp"}` | `api.helloWorld()` → 展示 JSON | ✅ |
| 2 | `/api/hash` POST | `AlgorithmController.hash()` body: `{"input","algorithm"}` → `{"algorithm","input","hash"}` | `api.computeHash(input, algo)` | ✅ |
| 3 | `/api/bubblesort` POST | `AlgorithmController.bubbleSort()` body: `{"array":[...]}` → `{"original","sorted","steps"}` | `api.bubbleSort(array)` | ✅ |
| 4 | `/api/export?tab=` GET | `ExportController.export()` → `.xlsx` stream | `api.exportTab(tab)` → blob download | ✅ |
| 5 | `/api/stats?dimension=&chart=` GET | `StatsController.getStats()` → `{"dimension","data":[{"key","count"}]}` | `api.getStats(dimension)` → 图表数据 | ✅ |
| 6 | JWT Header | `Authorization: Bearer <token>` 解析 username | `auth.js` 自动生成 mock token | ✅ |
| 7 | CORS | `WebConfig` 允许 localhost:3000 | axios baseURL localhost:8080 | ✅ |
| 8 | 埋点 | 每次调用写入 invocation_log | N/A（前端不感知） | ✅ |

---

## 自检 Review

### 1. Spec 覆盖
- ✅ 三个接口 helloworld / hash / bubblesort → Task 4 + Task 5
- ✅ 前端三 Tab 页面 → Task 10 + Task 11
- ✅ 导出按钮 + 后台导出接口 → Task 6 + Task 10 (ExportButton)
- ✅ 埋点获取调用次数和调用人 → Task 5 (logInvocation) + Task 7
- ✅ 前端可视化报表（维度 + 三种图表）→ Task 12 + Task 13

### 2. Placeholder 扫描
- ✅ 无 TBD / TODO / "implement later" 占位符
- ✅ 所有步骤包含实际代码或命令

### 3. 类型一致性
- ✅ `HashRequest.input` / `HashRequest.algorithm` 前后端一致
- ✅ `SortRequest.array` → `List<Integer>` 前后端一致
- ✅ `StatsResponse` 结构 `{dimension, data: [{key, count}]}` 前后端一致
- ✅ 导出 tab 参数值 `helloworld` / `hash` / `bubblesort` 前后端一致