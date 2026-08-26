# 跨仓协同开发 — 实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 从零构建跨仓全栈应用：后端 3 个核心算法接口 + 导出 + 埋点统计，前端 3 Tab 展示 + 导出按钮 + ECharts 多维报表可视化。

**Architecture:** Java Spring Boot 后端（testDj）提供 RESTful API，React 前端（testDJnew）通过 axios 调用。后端基于 JWT 做身份识别，H2 内存数据库存储埋点日志与用户维度数据，Apache POI 生成 Excel 导出。前端使用 ECharts 渲染折线图/饼图/柱状图。

**Tech Stack:** Java 17 + Spring Boot 3.2 + Spring Security + JWT (jjwt) + H2 + Apache POI + Maven | React 18 + axios + ECharts 5 + react-router-dom 6

---

## Global Constraints

- 所有 API 路径前缀 `/api/`
- 后端端口 8080，前端端口 3000（开发模式 proxy 到 8080）
- 跨域 CORS 允许 `http://localhost:3000`
- JWT Token 通过 `Authorization: Bearer <token>` 传递
- 埋点记录每次 API 调用的 username + api_path + timestamp
- 导出格式统一为 `.xlsx`（Apache POI）
- 前端的请求/响应格式必须与 §4.3 接口契约严格对齐

---

## File Structure

### 后端 `[testDj]` — 新增文件清单

```
testDj-main/
├── pom.xml                                  # Maven 依赖管理
├── src/main/java/com/example/demo/
│   ├── DemoApplication.java                 # Spring Boot 启动类
│   ├── config/
│   │   ├── SecurityConfig.java              # Spring Security 配置 + JWT 过滤器注册
│   │   └── WebConfig.java                   # CORS 跨域配置
│   ├── controller/
│   │   ├── AlgorithmController.java         # /api/helloworld, /api/hash, /api/bubblesort
│   │   ├── ExportController.java            # /api/export
│   │   └── StatsController.java             # /api/stats
│   ├── service/
│   │   ├── AlgorithmService.java            # 哈希算法 + 冒泡排序逻辑
│   │   ├── ExportService.java               # Excel 生成
│   │   └── StatsService.java                # 埋点查询 + 维度聚合
│   ├── model/
│   │   ├── InvocationLog.java               # JPA 实体：埋点日志
│   │   └── UserProfile.java                 # JPA 实体：用户维度
│   ├── repository/
│   │   ├── InvocationLogRepository.java     # 埋点日志 DAO
│   │   └── UserProfileRepository.java       # 用户维度 DAO
│   ├── security/
│   │   ├── JwtTokenFilter.java              # OncePerRequestFilter：JWT 解析
│   │   └── JwtTokenProvider.java            # JWT 生成/验证工具
│   └── dto/
│       ├── HashRequest.java                 # 哈希请求 DTO
│       ├── SortRequest.java                 # 排序请求 DTO
│       └── StatsResponse.java               # 统计响应 DTO
└── src/main/resources/
    ├── application.yml                      # 应用配置
    └── data.sql                             # 初始化用户维度 + 测试埋点数据
```

### 前端 `[testDJnew]` — 新增文件清单

```
testDJnew-main/
├── package.json                             # 依赖声明
├── public/
│   └── index.html                           # HTML 入口
├── src/
│   ├── App.jsx                              # 路由 + 布局壳
│   ├── index.jsx                            # ReactDOM 挂载
│   ├── pages/
│   │   ├── DashboardPage.jsx                # 主页面：3 Tab + 导出
│   │   └── ReportPage.jsx                   # 报表页面：维度选择器 + 3 图表
│   ├── components/
│   │   ├── HelloWorldTab.jsx                # Tab 1：展示 HelloWorld 结果
│   │   ├── HashTab.jsx                      # Tab 2：输入文本 + 算法选择 → 展示哈希
│   │   ├── BubbleSortTab.jsx                # Tab 3：输入数组 → 展示排序步骤
│   │   ├── ExportButton.jsx                 # 通用导出按钮
│   │   ├── LineChart.jsx                    # ECharts 折线图
│   │   ├── PieChart.jsx                     # ECharts 饼图
│   │   ├── BarChart.jsx                     # ECharts 柱状图
│   │   └── DimensionSelector.jsx            # 维度 + 图表类型切换控件
│   ├── services/
│   │   └── api.js                           # axios 实例 + 5 个 API 封装函数
│   └── utils/
│       └── auth.js                          # JWT Token 存储/读取
```

---

## Tasks

### Task 1: 后端项目骨架 — pom.xml + 启动类 + 配置

**Files:**
- Create: `testDj-main/pom.xml`
- Create: `testDj-main/src/main/java/com/example/demo/DemoApplication.java`
- Create: `testDj-main/src/main/resources/application.yml`
- Create: `testDj-main/src/main/java/com/example/demo/config/WebConfig.java`
- Create: `testDj-main/src/main/java/com/example/demo/config/SecurityConfig.java`

**Interfaces:**
- Produces: `DemoApplication` 启动类（Spring Boot 入口），`WebConfig` 允许 `http://localhost:3000` CORS，`SecurityConfig` 注册 JwtTokenFilter 并放行 `/api/**`

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
    <description>Demo project for Spring Boot</description>
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
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
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
      path: /h2-console
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: false
    defer-datasource-initialization: true
  sql:
    init:
      mode: always

jwt:
  secret: c2VjdXJlLXNlY3JldC1rZXktZm9yLWRlbW8tYXBwbGljYXRpb24tMjAyNQ==
  expiration: 86400000
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

- [ ] **Step 5: 创建 SecurityConfig.java**

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
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
```

- [ ] **Step 6: 验证 — 编译项目**

Run: `cd /root/.agentix/agentic-dev/runs/DEV-966dcd0a-7905-11f1-9649-3b4281182f10-7aa717a8-9632-4f0d-b727-a17c31bc687f/worktree/testDj-main && mvn compile -q 2>&1`
Expected: BUILD SUCCESS

---

### Task 2: 后端 JWT 安全组件

**Files:**
- Create: `testDj-main/src/main/java/com/example/demo/security/JwtTokenProvider.java`
- Create: `testDj-main/src/main/java/com/example/demo/security/JwtTokenFilter.java`

**Interfaces:**
- Produces: `JwtTokenProvider` — `String generateToken(String username)`, `String getUsernameFromToken(String token)`, `boolean validateToken(String token)`
- Produces: `JwtTokenFilter` — 从 `Authorization: Bearer <token>` 解析 username，设置到 SecurityContext；若无 token 则设匿名用户 "anonymous"

- [ ] **Step 1: 创建 JwtTokenProvider.java**

```java
package com.example.demo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long expiration;

    public JwtTokenProvider(@Value("${jwt.secret}") String secret,
                            @Value("${jwt.expiration}") long expiration) {
        this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
        this.expiration = expiration;
    }

    public String generateToken(String username) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiration);
        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
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
        String username = "anonymous";

        if (token != null && jwtTokenProvider.validateToken(token)) {
            username = jwtTokenProvider.getUsernameFromToken(token);
        }

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);

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

---

### Task 3: 后端实体模型 + DTO + 数据初始化

**Files:**
- Create: `testDj-main/src/main/java/com/example/demo/model/InvocationLog.java`
- Create: `testDj-main/src/main/java/com/example/demo/model/UserProfile.java`
- Create: `testDj-main/src/main/java/com/example/demo/repository/InvocationLogRepository.java`
- Create: `testDj-main/src/main/java/com/example/demo/repository/UserProfileRepository.java`
- Create: `testDj-main/src/main/java/com/example/demo/dto/HashRequest.java`
- Create: `testDj-main/src/main/java/com/example/demo/dto/SortRequest.java`
- Create: `testDj-main/src/main/java/com/example/demo/dto/StatsResponse.java`
- Create: `testDj-main/src/main/resources/data.sql`

**Interfaces:**
- Produces: `InvocationLog` entity (id, username, apiPath, timestamp), `UserProfile` entity (id, username, type, level, department)
- Produces: `InvocationLogRepository` extends JpaRepository with `countByApiPath(String)` and `findAll()`; `UserProfileRepository` extends JpaRepository with `findByUsername(String)`
- Produces: `HashRequest` (input: String, algorithm: String), `SortRequest` (array: int[]), `StatsResponse` (dimension: String, data: List<KV>)

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

    @Column(name = "api_path", nullable = false)
    private String apiPath;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public InvocationLog() {}

    public InvocationLog(String username, String apiPath, LocalDateTime timestamp) {
        this.username = username;
        this.apiPath = apiPath;
        this.timestamp = timestamp;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getApiPath() { return apiPath; }
    public void setApiPath(String apiPath) { this.apiPath = apiPath; }
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

    @Column(name = "user_type", nullable = false)
    private String userType;

    @Column(nullable = false)
    private String level;

    @Column(nullable = false)
    private String department;

    public UserProfile() {}

    public UserProfile(String username, String userType, String level, String department) {
        this.username = username;
        this.userType = userType;
        this.level = level;
        this.department = department;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }
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
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvocationLogRepository extends JpaRepository<InvocationLog, Long> {
    long countByApiPath(String apiPath);
    List<InvocationLog> findAll();
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

- [ ] **Step 5: 创建 HashRequest.java**

```java
package com.example.demo.dto;

public class HashRequest {
    private String input;
    private String algorithm;

    public String getInput() { return input; }
    public void setInput(String input) { this.input = input; }
    public String getAlgorithm() { return algorithm; }
    public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }
}
```

- [ ] **Step 6: 创建 SortRequest.java**

```java
package com.example.demo.dto;

public class SortRequest {
    private int[] array;

    public int[] getArray() { return array; }
    public void setArray(int[] array) { this.array = array; }
}
```

- [ ] **Step 7: 创建 StatsResponse.java**

```java
package com.example.demo.dto;

import java.util.List;

public class StatsResponse {
    private String dimension;
    private List<KeyValue> data;

    public StatsResponse(String dimension, List<KeyValue> data) {
        this.dimension = dimension;
        this.data = data;
    }

    public String getDimension() { return dimension; }
    public void setDimension(String dimension) { this.dimension = dimension; }
    public List<KeyValue> getData() { return data; }
    public void setData(List<KeyValue> data) { this.data = data; }

    public static class KeyValue {
        private String key;
        private long count;

        public KeyValue(String key, long count) {
            this.key = key;
            this.count = count;
        }

        public String getKey() { return key; }
        public void setKey(String key) { this.key = key; }
        public long getCount() { return count; }
        public void setCount(long count) { this.count = count; }
    }
}
```

- [ ] **Step 8: 创建 data.sql**

```sql
INSERT INTO user_profile (username, user_type, level, department) VALUES
('zhangsan', '正式员工', 'P6', '技术部'),
('lisi', '正式员工', 'P7', '技术部'),
('wangwu', '外包', 'P5', '产品部'),
('zhaoliu', '正式员工', 'P8', '技术部'),
('sunqi', '实习生', 'P4', '产品部'),
('zhouba', '外包', 'P5', '技术部'),
('wujiu', '正式员工', 'P7', '产品部'),
('zhengshi', '正式员工', 'P6', '运营部');

INSERT INTO invocation_log (username, api_path, timestamp) VALUES
('zhangsan', '/api/helloworld', '2025-07-15 10:00:00'),
('lisi', '/api/hash', '2025-07-15 10:05:00'),
('zhangsan', '/api/hash', '2025-07-15 10:10:00'),
('wangwu', '/api/bubblesort', '2025-07-15 10:15:00'),
('zhaoliu', '/api/helloworld', '2025-07-15 10:20:00'),
('zhangsan', '/api/bubblesort', '2025-07-15 10:25:00'),
('lisi', '/api/helloworld', '2025-07-15 10:30:00'),
('sunqi', '/api/hash', '2025-07-15 10:35:00'),
('zhouba', '/api/helloworld', '2025-07-15 10:40:00'),
('wujiu', '/api/bubblesort', '2025-07-15 10:45:00'),
('zhengshi', '/api/hash', '2025-07-15 10:50:00'),
('zhangsan', '/api/helloworld', '2025-07-15 11:00:00'),
('lisi', '/api/bubblesort', '2025-07-15 11:05:00'),
('wangwu', '/api/hash', '2025-07-15 11:10:00');
```

- [ ] **Step 9: 验证 — 编译**

Run: `cd /root/.agentix/agentic-dev/runs/DEV-966dcd0a-7905-11f1-9649-3b4281182f10-7aa717a8-9632-4f0d-b727-a17c31bc687f/worktree/testDj-main && mvn compile -q 2>&1`
Expected: BUILD SUCCESS

---

### Task 4: 后端 AlgorithmService + AlgorithmController

**Files:**
- Create: `testDj-main/src/main/java/com/example/demo/service/AlgorithmService.java`
- Create: `testDj-main/src/main/java/com/example/demo/controller/AlgorithmController.java`

**Interfaces:**
- Consumes: `HashRequest`, `SortRequest` DTOs; `InvocationLogRepository` (for 埋点); `SecurityContextHolder` (for username)
- Produces: `AlgorithmService` — `Map<String,Object> helloworld()`, `Map<String,Object> hash(HashRequest)`, `Map<String,Object> bubbleSort(SortRequest)`
- Produces: `AlgorithmController` — GET `/api/helloworld`, POST `/api/hash`, POST `/api/bubblesort`

- [ ] **Step 1: 创建 AlgorithmService.java**

```java
package com.example.demo.service;

import com.example.demo.dto.HashRequest;
import com.example.demo.dto.SortRequest;
import com.example.demo.model.InvocationLog;
import com.example.demo.repository.InvocationLogRepository;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class AlgorithmService {

    private final InvocationLogRepository logRepository;

    public AlgorithmService(InvocationLogRepository logRepository) {
        this.logRepository = logRepository;
    }

    public Map<String, Object> helloworld() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("message", "Hello World");
        result.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return result;
    }

    public Map<String, Object> hash(HashRequest request) {
        String algorithm = request.getAlgorithm();
        if (algorithm == null || algorithm.isBlank()) {
            algorithm = "SHA-256";
        }
        String algorithmUpper = algorithm.toUpperCase();

        Set<String> supported = Set.of("MD5", "SHA-1", "SHA-256");
        if (!supported.contains(algorithmUpper)) {
            throw new IllegalArgumentException("Unsupported algorithm: " + algorithm + ". Supported: MD5, SHA-1, SHA-256");
        }

        try {
            String javaAlgo = algorithmUpper.equals("SHA-1") ? "SHA-1" : algorithmUpper;
            MessageDigest md = MessageDigest.getInstance(javaAlgo);
            byte[] digest = md.digest(request.getInput().getBytes());
            StringBuilder hex = new StringBuilder();
            for (byte b : digest) {
                hex.append(String.format("%02x", b));
            }

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("algorithm", algorithmUpper);
            result.put("input", request.getInput());
            result.put("hash", hex.toString());
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Hash computation failed", e);
        }
    }

    public Map<String, Object> bubbleSort(SortRequest request) {
        int[] arr = Arrays.copyOf(request.getArray(), request.getArray().length);
        List<Map<String, Object>> steps = new ArrayList<>();

        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;

                    Map<String, Object> step = new LinkedHashMap<>();
                    step.put("step", steps.size() + 1);
                    step.put("swapped", new int[]{j, j + 1});
                    step.put("array", Arrays.copyOf(arr, arr.length));
                    steps.add(step);
                }
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("original", request.getArray());
        result.put("sorted", arr);
        result.put("steps", steps);
        return result;
    }

    public void logInvocation(String username, String apiPath) {
        logRepository.save(new InvocationLog(username, apiPath, LocalDateTime.now()));
    }
}
```

- [ ] **Step 2: 创建 AlgorithmController.java**

```java
package com.example.demo.controller;

import com.example.demo.dto.HashRequest;
import com.example.demo.dto.SortRequest;
import com.example.demo.service.AlgorithmService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AlgorithmController {

    private final AlgorithmService algorithmService;

    public AlgorithmController(AlgorithmService algorithmService) {
        this.algorithmService = algorithmService;
    }

    @GetMapping("/helloworld")
    public ResponseEntity<Map<String, Object>> helloWorld() {
        String username = getCurrentUsername();
        algorithmService.logInvocation(username, "/api/helloworld");
        return ResponseEntity.ok(algorithmService.helloworld());
    }

    @PostMapping("/hash")
    public ResponseEntity<Map<String, Object>> hash(@RequestBody HashRequest request) {
        String username = getCurrentUsername();
        algorithmService.logInvocation(username, "/api/hash");
        return ResponseEntity.ok(algorithmService.hash(request));
    }

    @PostMapping("/bubblesort")
    public ResponseEntity<Map<String, Object>> bubbleSort(@RequestBody SortRequest request) {
        String username = getCurrentUsername();
        algorithmService.logInvocation(username, "/api/bubblesort");
        return ResponseEntity.ok(algorithmService.bubbleSort(request));
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "anonymous";
    }
}
```

- [ ] **Step 3: 验证 — 编译**

Run: `cd /root/.agentix/agentic-dev/runs/DEV-966dcd0a-7905-11f1-9649-3b4281182f10-7aa717a8-9632-4f0d-b727-a17c31bc687f/worktree/testDj-main && mvn compile -q 2>&1`
Expected: BUILD SUCCESS

---

### Task 5: 后端 ExportService + ExportController

**Files:**
- Create: `testDj-main/src/main/java/com/example/demo/service/ExportService.java`
- Create: `testDj-main/src/main/java/com/example/demo/controller/ExportController.java`

**Interfaces:**
- Consumes: `AlgorithmService` (获取各接口结果数据); `InvocationLogRepository` (获取埋点数据)
- Produces: `ExportService` — `byte[] export(String tab)` 返回 .xlsx 字节数组
- Produces: `ExportController` — GET `/api/export?tab=helloworld|hash|bubblesort`

- [ ] **Step 1: 创建 ExportService.java**

```java
package com.example.demo.service;

import com.example.demo.model.InvocationLog;
import com.example.demo.repository.InvocationLogRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class ExportService {

    private final InvocationLogRepository logRepository;

    public ExportService(InvocationLogRepository logRepository) {
        this.logRepository = logRepository;
    }

    public byte[] export(String tab) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(tab);
            Row header = sheet.createRow(0);

            switch (tab.toLowerCase()) {
                case "helloworld" -> {
                    header.createCell(0).setCellValue("Message");
                    header.createCell(1).setCellValue("Timestamp");
                    Row row = sheet.createRow(1);
                    row.createCell(0).setCellValue("Hello World");
                    row.createCell(1).setCellValue(java.time.LocalDateTime.now().toString());
                }
                case "hash" -> {
                    header.createCell(0).setCellValue("Algorithm");
                    header.createCell(1).setCellValue("Description");
                    Row r1 = sheet.createRow(1);
                    r1.createCell(0).setCellValue("MD5");
                    r1.createCell(1).setCellValue("128-bit hash");
                    Row r2 = sheet.createRow(2);
                    r2.createCell(0).setCellValue("SHA-1");
                    r2.createCell(1).setCellValue("160-bit hash");
                    Row r3 = sheet.createRow(3);
                    r3.createCell(0).setCellValue("SHA-256");
                    r3.createCell(1).setCellValue("256-bit hash");
                }
                case "bubblesort" -> {
                    header.createCell(0).setCellValue("Example Input");
                    header.createCell(1).setCellValue("Sorted Output");
                    Row row = sheet.createRow(1);
                    row.createCell(0).setCellValue("[3, 1, 4, 1, 5]");
                    row.createCell(1).setCellValue("[1, 1, 3, 4, 5]");
                }
                default -> {
                    header.createCell(0).setCellValue("API");
                    header.createCell(1).setCellValue("Call Count");
                    List<InvocationLog> logs = logRepository.findAll();
                    long hw = logs.stream().filter(l -> l.getApiPath().equals("/api/helloworld")).count();
                    long ha = logs.stream().filter(l -> l.getApiPath().equals("/api/hash")).count();
                    long bs = logs.stream().filter(l -> l.getApiPath().equals("/api/bubblesort")).count();
                    int r = 1;
                    for (String[] entry : new String[][]{{"helloworld", String.valueOf(hw)}, {"hash", String.valueOf(ha)}, {"bubblesort", String.valueOf(bs)}}) {
                        Row row = sheet.createRow(r++);
                        row.createCell(0).setCellValue(entry[0]);
                        row.createCell(1).setCellValue(entry[1]);
                    }
                }
            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            return bos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Excel export failed", e);
        }
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

@RestController
@RequestMapping("/api")
public class ExportController {

    private final ExportService exportService;

    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> export(@RequestParam(defaultValue = "helloworld") String tab) {
        byte[] data = exportService.export(tab);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", tab + ".xlsx");
        return ResponseEntity.ok().headers(headers).body(data);
    }
}
```

- [ ] **Step 3: 验证 — 编译**

Run: `cd /root/.agentix/agentic-dev/runs/DEV-966dcd0a-7905-11f1-9649-3b4281182f10-7aa717a8-9632-4f0d-b727-a17c31bc687f/worktree/testDj-main && mvn compile -q 2>&1`
Expected: BUILD SUCCESS

---

### Task 6: 后端 StatsService + StatsController

**Files:**
- Create: `testDj-main/src/main/java/com/example/demo/service/StatsService.java`
- Create: `testDj-main/src/main/java/com/example/demo/controller/StatsController.java`

**Interfaces:**
- Consumes: `InvocationLogRepository`, `UserProfileRepository`
- Produces: `StatsService` — `StatsResponse getStats(String dimension)` 按维度聚合调用次数
- Produces: `StatsController` — GET `/api/stats?dimension=type|level|department&chart=line|pie|bar`

- [ ] **Step 1: 创建 StatsService.java**

```java
package com.example.demo.service;

import com.example.demo.dto.StatsResponse;
import com.example.demo.model.InvocationLog;
import com.example.demo.model.UserProfile;
import com.example.demo.repository.InvocationLogRepository;
import com.example.demo.repository.UserProfileRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatsService {

    private final InvocationLogRepository logRepository;
    private final UserProfileRepository profileRepository;

    public StatsService(InvocationLogRepository logRepository, UserProfileRepository profileRepository) {
        this.logRepository = logRepository;
        this.profileRepository = profileRepository;
    }

    public StatsResponse getStats(String dimension) {
        List<InvocationLog> logs = logRepository.findAll();
        List<UserProfile> profiles = profileRepository.findAll();
        Map<String, UserProfile> profileMap = profiles.stream()
                .collect(Collectors.toMap(UserProfile::getUsername, p -> p));

        Map<String, Long> aggregated = new LinkedHashMap<>();

        for (InvocationLog log : logs) {
            UserProfile profile = profileMap.get(log.getUsername());
            String key = switch (dimension != null ? dimension.toLowerCase() : "type") {
                case "level" -> profile != null ? profile.getLevel() : "unknown";
                case "department" -> profile != null ? profile.getDepartment() : "unknown";
                default -> profile != null ? profile.getUserType() : "unknown";
            };
            aggregated.merge(key, 1L, Long::sum);
        }

        List<StatsResponse.KeyValue> data = aggregated.entrySet().stream()
                .map(e -> new StatsResponse.KeyValue(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        return new StatsResponse(dimension != null ? dimension : "type", data);
    }
}
```

- [ ] **Step 2: 创建 StatsController.java**

```java
package com.example.demo.controller;

import com.example.demo.dto.StatsResponse;
import com.example.demo.service.StatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/stats")
    public ResponseEntity<StatsResponse> stats(
            @RequestParam(defaultValue = "type") String dimension,
            @RequestParam(defaultValue = "line") String chart) {
        return ResponseEntity.ok(statsService.getStats(dimension));
    }
}
```

- [ ] **Step 3: 验证 — 编译**

Run: `cd /root/.agentix/agentic-dev/runs/DEV-966dcd0a-7905-11f1-9649-3b4281182f10-7aa717a8-9632-4f0d-b727-a17c31bc687f/worktree/testDj-main && mvn compile -q 2>&1`
Expected: BUILD SUCCESS

---

### Task 7: 前端项目骨架 — package.json + 入口文件

**Files:**
- Create: `testDJnew-main/package.json`
- Create: `testDJnew-main/public/index.html`
- Create: `testDJnew-main/src/index.jsx`
- Create: `testDJnew-main/src/utils/auth.js`
- Create: `testDJnew-main/src/services/api.js`

**Interfaces:**
- Produces: `auth.js` — `getToken()`, `setToken(token)`, `getUsername()`
- Produces: `api.js` — `helloWorld()`, `hash(input, algorithm)`, `bubbleSort(array)`, `exportExcel(tab)`, `getStats(dimension, chart)`, axios 实例 baseURL `/api`

- [ ] **Step 1: 创建 package.json**

```json
{
  "name": "testdjnew",
  "version": "0.1.0",
  "private": true,
  "dependencies": {
    "react": "^18.2.0",
    "react-dom": "^18.2.0",
    "react-router-dom": "^6.23.0",
    "axios": "^1.7.0",
    "echarts": "^5.5.0",
    "echarts-for-react": "^3.0.2"
  },
  "devDependencies": {
    "react-scripts": "5.0.1"
  },
  "scripts": {
    "start": "react-scripts start",
    "build": "react-scripts build"
  },
  "proxy": "http://localhost:8080",
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
    <title>算法演示平台</title>
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

- [ ] **Step 4: 创建 src/utils/auth.js**

```js
const TOKEN_KEY = 'demo_jwt_token';

export function getToken() {
    return localStorage.getItem(TOKEN_KEY) || '';
}

export function setToken(token) {
    localStorage.setItem(TOKEN_KEY, token);
}

export function getUsername() {
    const token = getToken();
    if (!token) return 'anonymous';
    try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        return payload.sub || 'anonymous';
    } catch {
        return 'anonymous';
    }
}
```

- [ ] **Step 5: 创建 src/services/api.js**

```js
import axios from 'axios';
import { getToken } from '../utils/auth';

const client = axios.create({
    baseURL: '/api',
    timeout: 10000,
});

client.interceptors.request.use((config) => {
    const token = getToken();
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

export async function helloWorld() {
    const res = await client.get('/helloworld');
    return res.data;
}

export async function hash(input, algorithm) {
    const res = await client.post('/hash', { input, algorithm });
    return res.data;
}

export async function bubbleSort(array) {
    const res = await client.post('/bubblesort', { array });
    return res.data;
}

export async function exportExcel(tab) {
    const res = await client.get('/export', {
        params: { tab },
        responseType: 'blob',
    });
    return res.data;
}

export async function getStats(dimension, chart) {
    const res = await client.get('/stats', {
        params: { dimension, chart },
    });
    return res.data;
}
```

---

### Task 8: 前端 Tab 组件 + ExportButton

**Files:**
- Create: `testDJnew-main/src/components/HelloWorldTab.jsx`
- Create: `testDJnew-main/src/components/HashTab.jsx`
- Create: `testDJnew-main/src/components/BubbleSortTab.jsx`
- Create: `testDJnew-main/src/components/ExportButton.jsx`

**Interfaces:**
- Consumes: `api.js` functions (`helloWorld`, `hash`, `bubbleSort`, `exportExcel`)
- Produces: 3 个 Tab 组件（各自独立请求 + 展示结果），1 个通用导出按钮

- [ ] **Step 1: 创建 HelloWorldTab.jsx**

```jsx
import React, { useState } from 'react';
import { helloWorld } from '../services/api';
import ExportButton from './ExportButton';

export default function HelloWorldTab() {
    const [result, setResult] = useState(null);
    const [loading, setLoading] = useState(false);

    const fetchHello = async () => {
        setLoading(true);
        try {
            const data = await helloWorld();
            setResult(data);
        } catch (err) {
            setResult({ error: err.message });
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={{ padding: 20 }}>
            <h3>HelloWorld 接口</h3>
            <button onClick={fetchHello} disabled={loading}>
                {loading ? '请求中...' : '调用 /helloworld'}
            </button>
            <ExportButton tab="helloworld" />
            {result && (
                <pre style={{ background: '#f5f5f5', padding: 12, marginTop: 10 }}>
                    {JSON.stringify(result, null, 2)}
                </pre>
            )}
        </div>
    );
}
```

- [ ] **Step 2: 创建 HashTab.jsx**

```jsx
import React, { useState } from 'react';
import { hash } from '../services/api';
import ExportButton from './ExportButton';

export default function HashTab() {
    const [input, setInput] = useState('');
    const [algorithm, setAlgorithm] = useState('SHA-256');
    const [result, setResult] = useState(null);
    const [loading, setLoading] = useState(false);

    const fetchHash = async () => {
        if (!input) return;
        setLoading(true);
        try {
            const data = await hash(input, algorithm);
            setResult(data);
        } catch (err) {
            setResult({ error: err.message });
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={{ padding: 20 }}>
            <h3>Hash 哈希计算</h3>
            <div>
                <input
                    type="text"
                    placeholder="输入文本"
                    value={input}
                    onChange={(e) => setInput(e.target.value)}
                    style={{ marginRight: 8, padding: 4 }}
                />
                <select value={algorithm} onChange={(e) => setAlgorithm(e.target.value)} style={{ padding: 4 }}>
                    <option value="MD5">MD5</option>
                    <option value="SHA-1">SHA-1</option>
                    <option value="SHA-256">SHA-256</option>
                </select>
                <button onClick={fetchHash} disabled={loading} style={{ marginLeft: 8 }}>
                    {loading ? '计算中...' : '计算哈希'}
                </button>
            </div>
            <ExportButton tab="hash" />
            {result && (
                <pre style={{ background: '#f5f5f5', padding: 12, marginTop: 10 }}>
                    {JSON.stringify(result, null, 2)}
                </pre>
            )}
        </div>
    );
}
```

- [ ] **Step 3: 创建 BubbleSortTab.jsx**

```jsx
import React, { useState } from 'react';
import { bubbleSort } from '../services/api';
import ExportButton from './ExportButton';

export default function BubbleSortTab() {
    const [input, setInput] = useState('[3, 1, 4, 1, 5]');
    const [result, setResult] = useState(null);
    const [loading, setLoading] = useState(false);

    const fetchSort = async () => {
        let array;
        try {
            array = JSON.parse(input);
        } catch {
            setResult({ error: '请输入合法的 JSON 数组，如 [3,1,4,1,5]' });
            return;
        }
        setLoading(true);
        try {
            const data = await bubbleSort(array);
            setResult(data);
        } catch (err) {
            setResult({ error: err.message });
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={{ padding: 20 }}>
            <h3>BubbleSort 冒泡排序</h3>
            <div>
                <input
                    type="text"
                    placeholder="输入 JSON 数组"
                    value={input}
                    onChange={(e) => setInput(e.target.value)}
                    style={{ marginRight: 8, padding: 4, width: 240 }}
                />
                <button onClick={fetchSort} disabled={loading}>
                    {loading ? '排序中...' : '执行排序'}
                </button>
            </div>
            <ExportButton tab="bubblesort" />
            {result && (
                <pre style={{ background: '#f5f5f5', padding: 12, marginTop: 10, maxHeight: 400, overflow: 'auto' }}>
                    {JSON.stringify(result, null, 2)}
                </pre>
            )}
        </div>
    );
}
```

- [ ] **Step 4: 创建 ExportButton.jsx**

```jsx
import React, { useState } from 'react';
import { exportExcel } from '../services/api';

export default function ExportButton({ tab }) {
    const [exporting, setExporting] = useState(false);

    const handleExport = async () => {
        setExporting(true);
        try {
            const blob = await exportExcel(tab);
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = `${tab}.xlsx`;
            document.body.appendChild(a);
            a.click();
            a.remove();
            window.URL.revokeObjectURL(url);
        } catch (err) {
            alert('导出失败: ' + err.message);
        } finally {
            setExporting(false);
        }
    };

    return (
        <button onClick={handleExport} disabled={exporting} style={{ marginLeft: 12 }}>
            {exporting ? '导出中...' : '导出 Excel'}
        </button>
    );
}
```

---

### Task 9: 前端图表组件 + DimensionSelector

**Files:**
- Create: `testDJnew-main/src/components/LineChart.jsx`
- Create: `testDJnew-main/src/components/PieChart.jsx`
- Create: `testDJnew-main/src/components/BarChart.jsx`
- Create: `testDJnew-main/src/components/DimensionSelector.jsx`

**Interfaces:**
- Consumes: `getStats(dimension, chart)` from `api.js`
- Produces: 3 个 ECharts 图表组件（接收 `dimension` prop，内部请求数据），1 个维度选择器

- [ ] **Step 1: 创建 LineChart.jsx**

```jsx
import React, { useEffect, useState } from 'react';
import ReactECharts from 'echarts-for-react';
import { getStats } from '../services/api';

export default function LineChart({ dimension }) {
    const [option, setOption] = useState(null);

    useEffect(() => {
        (async () => {
            try {
                const stats = await getStats(dimension, 'line');
                setOption({
                    title: { text: `调用次数折线图 - ${dimension}` },
                    xAxis: { type: 'category', data: stats.data.map((d) => d.key) },
                    yAxis: { type: 'value' },
                    series: [{ data: stats.data.map((d) => d.count), type: 'line', smooth: true }],
                    tooltip: { trigger: 'axis' },
                });
            } catch (err) {
                console.error(err);
            }
        })();
    }, [dimension]);

    if (!option) return <div>加载中...</div>;
    return <ReactECharts option={option} style={{ height: 350 }} />;
}
```

- [ ] **Step 2: 创建 PieChart.jsx**

```jsx
import React, { useEffect, useState } from 'react';
import ReactECharts from 'echarts-for-react';
import { getStats } from '../services/api';

export default function PieChart({ dimension }) {
    const [option, setOption] = useState(null);

    useEffect(() => {
        (async () => {
            try {
                const stats = await getStats(dimension, 'pie');
                setOption({
                    title: { text: `调用次数饼图 - ${dimension}` },
                    tooltip: { trigger: 'item' },
                    series: [{
                        type: 'pie',
                        radius: '60%',
                        data: stats.data.map((d) => ({ name: d.key, value: d.count })),
                        emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0,0,0,0.5)' } },
                    }],
                });
            } catch (err) {
                console.error(err);
            }
        })();
    }, [dimension]);

    if (!option) return <div>加载中...</div>;
    return <ReactECharts option={option} style={{ height: 350 }} />;
}
```

- [ ] **Step 3: 创建 BarChart.jsx**

```jsx
import React, { useEffect, useState } from 'react';
import ReactECharts from 'echarts-for-react';
import { getStats } from '../services/api';

export default function BarChart({ dimension }) {
    const [option, setOption] = useState(null);

    useEffect(() => {
        (async () => {
            try {
                const stats = await getStats(dimension, 'bar');
                setOption({
                    title: { text: `调用次数柱状图 - ${dimension}` },
                    xAxis: { type: 'category', data: stats.data.map((d) => d.key) },
                    yAxis: { type: 'value' },
                    series: [{ data: stats.data.map((d) => d.count), type: 'bar' }],
                    tooltip: { trigger: 'axis' },
                });
            } catch (err) {
                console.error(err);
            }
        })();
    }, [dimension]);

    if (!option) return <div>加载中...</div>;
    return <ReactECharts option={option} style={{ height: 350 }} />;
}
```

- [ ] **Step 4: 创建 DimensionSelector.jsx**

```jsx
import React from 'react';

export default function DimensionSelector({ dimension, setDimension, chart, setChart }) {
    return (
        <div style={{ padding: '10px 20px', display: 'flex', gap: 16, alignItems: 'center' }}>
            <label>
                维度：
                <select value={dimension} onChange={(e) => setDimension(e.target.value)}>
                    <option value="type">人员类型</option>
                    <option value="level">人员层级</option>
                    <option value="department">人员部门</option>
                </select>
            </label>
            <label>
                图表类型：
                <select value={chart} onChange={(e) => setChart(e.target.value)}>
                    <option value="line">折线图</option>
                    <option value="pie">饼图</option>
                    <option value="bar">柱状图</option>
                </select>
            </label>
        </div>
    );
}
```

---

### Task 10: 前端页面组装 — DashboardPage + ReportPage + App

**Files:**
- Create: `testDJnew-main/src/pages/DashboardPage.jsx`
- Create: `testDJnew-main/src/pages/ReportPage.jsx`
- Create: `testDJnew-main/src/App.jsx`

**Interfaces:**
- Consumes: All tab components, ExportButton, chart components, DimensionSelector
- Produces: `DashboardPage` (3 Tab 切换), `ReportPage` (维度选择 + 图表), `App` (路由 / 和 /report)

- [ ] **Step 1: 创建 DashboardPage.jsx**

```jsx
import React, { useState } from 'react';
import HelloWorldTab from '../components/HelloWorldTab';
import HashTab from '../components/HashTab';
import BubbleSortTab from '../components/BubbleSortTab';

const TABS = [
    { key: 'helloworld', label: 'HelloWorld', component: HelloWorldTab },
    { key: 'hash', label: 'Hash 哈希', component: HashTab },
    { key: 'bubblesort', label: 'BubbleSort 排序', component: BubbleSortTab },
];

export default function DashboardPage() {
    const [active, setActive] = useState('helloworld');
    const ActiveComponent = TABS.find((t) => t.key === active).component;

    return (
        <div>
            <div style={{ display: 'flex', borderBottom: '2px solid #ddd', marginBottom: 0 }}>
                {TABS.map((tab) => (
                    <button
                        key={tab.key}
                        onClick={() => setActive(tab.key)}
                        style={{
                            padding: '10px 20px',
                            border: 'none',
                            background: active === tab.key ? '#1890ff' : '#f0f0f0',
                            color: active === tab.key ? '#fff' : '#333',
                            cursor: 'pointer',
                            fontWeight: active === tab.key ? 'bold' : 'normal',
                        }}
                    >
                        {tab.label}
                    </button>
                ))}
            </div>
            <ActiveComponent />
        </div>
    );
}
```

- [ ] **Step 2: 创建 ReportPage.jsx**

```jsx
import React, { useState } from 'react';
import DimensionSelector from '../components/DimensionSelector';
import LineChart from '../components/LineChart';
import PieChart from '../components/PieChart';
import BarChart from '../components/BarChart';

export default function ReportPage() {
    const [dimension, setDimension] = useState('type');
    const [chart, setChart] = useState('line');

    const renderChart = () => {
        switch (chart) {
            case 'pie': return <PieChart dimension={dimension} />;
            case 'bar': return <BarChart dimension={dimension} />;
            default: return <LineChart dimension={dimension} />;
        }
    };

    return (
        <div>
            <h2 style={{ padding: '0 20px' }}>调用情况报表</h2>
            <DimensionSelector
                dimension={dimension}
                setDimension={setDimension}
                chart={chart}
                setChart={setChart}
            />
            <div style={{ padding: '0 20px' }}>
                {renderChart()}
            </div>
        </div>
    );
}
```

- [ ] **Step 3: 创建 App.jsx**

```jsx
import React from 'react';
import { BrowserRouter, Routes, Route, Link, useLocation } from 'react-router-dom';
import DashboardPage from './pages/DashboardPage';
import ReportPage from './pages/ReportPage';

function Nav() {
    const location = useLocation();
    const isActive = (path) => location.pathname === path
        ? { background: '#1890ff', color: '#fff' }
        : { background: '#f0f0f0', color: '#333' };

    return (
        <nav style={{ display: 'flex', gap: 8, padding: '12px 20px', background: '#fff', borderBottom: '1px solid #eee' }}>
            <Link to="/" style={{ padding: '8px 16px', textDecoration: 'none', borderRadius: 4, ...isActive('/') }}>
                算法演示
            </Link>
            <Link to="/report" style={{ padding: '8px 16px', textDecoration: 'none', borderRadius: 4, ...isActive('/report') }}>
                调用报表
            </Link>
        </nav>
    );
}

export default function App() {
    return (
        <BrowserRouter>
            <Nav />
            <Routes>
                <Route path="/" element={<DashboardPage />} />
                <Route path="/report" element={<ReportPage />} />
            </Routes>
        </BrowserRouter>
    );
}
```

---

## Self-Review

### 1. Spec coverage

| 需求 | 覆盖任务 |
|------|---------|
| HelloWorld 接口 | Task 4 (AlgorithmService + AlgorithmController) |
| Hash 接口 (MD5/SHA-1/SHA-256) | Task 4 (AlgorithmService.hash) |
| BubbleSort 接口 | Task 4 (AlgorithmService.bubbleSort) |
| 前端 3 Tab 页面 | Task 8 (Tab 组件) + Task 10 (DashboardPage) |
| 导出按钮 + 导出接口 | Task 5 (ExportService/Controller) + Task 8 (ExportButton) |
| 埋点（调用人+次数） | Task 4 (AlgorithmService.logInvocation) + Task 3 (InvocationLog) |
| 前端可视化报表 | Task 9 (图表组件) + Task 10 (ReportPage) |
| 维度拆分（人员类型/层级/部门） | Task 6 (StatsService) + Task 9 (DimensionSelector) |
| 折线图/饼图/柱状图 | Task 9 (LineChart/PieChart/BarChart) |
| JWT 身份识别 | Task 2 (JwtTokenProvider + JwtTokenFilter) |
| user_profile 表 | Task 3 (UserProfile + data.sql) |

### 2. Placeholder scan

✅ 无 TBD/TODO/implement later
✅ 所有代码步骤均包含完整代码
✅ 无 "add appropriate error handling" 占位描述

### 3. Type consistency

✅ `HashRequest` (input: String, algorithm: String) — 前后一致
✅ `SortRequest` (array: int[]) — 前后一致
✅ `StatsResponse` (dimension: String, data: List<KeyValue>) — 前后一致
✅ `api.js` 函数签名与后端 Controller 对齐
✅ `DimensionSelector` props 与 ReportPage 使用一致

---

## 仓间对齐点检查

| 对齐点 | 后端 (testDj) | 前端 (testDJnew) | 状态 |
|--------|-------------|-----------------|------|
| GET /api/helloworld → `{message, timestamp}` | `AlgorithmController.helloWorld()` | `api.helloWorld()` → `HelloWorldTab` | ✅ |
| POST /api/hash ← `{input, algorithm}` → `{algorithm, input, hash}` | `AlgorithmController.hash()` | `api.hash()` → `HashTab` | ✅ |
| POST /api/bubblesort ← `{array}` → `{original, sorted, steps}` | `AlgorithmController.bubbleSort()` | `api.bubbleSort()` → `BubbleSortTab` | ✅ |
| GET /api/export?tab= → `.xlsx` blob | `ExportController.export()` | `api.exportExcel()` → `ExportButton` | ✅ |
| GET /api/stats?dimension=&chart= → `{dimension, data: [{key, count}]}` | `StatsController.stats()` | `api.getStats()` → `LineChart/PieChart/BarChart` | ✅ |
| JWT Authorization header | `JwtTokenFilter` 解析 Bearer token | `api.js` interceptor 注入 | ✅ |
| CORS origin | `WebConfig` 允许 localhost:3000 | `package.json` proxy → localhost:8080 | ✅ |