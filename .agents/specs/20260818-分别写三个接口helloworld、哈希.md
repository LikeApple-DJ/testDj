# 算法演示与埋点报表系统 — 实施计划

> **文档版本**: v1.0
> **生成日期**: 2025-08-18
> **关联仓库**: testDj (后端) / testDJnew (前端)
> **上游规格**: `.agents/specs/${system.dima}.md` (需求澄清)
> **技能**: writing-plans
> **对于 agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

---

**Goal:** 构建一个全栈算法演示系统：后端提供 HelloWorld、哈希算法、冒泡排序三个接口及埋点导出；前端通过三 Tab 页面展示结果，提供 Excel 导出和 ECharts 多维可视化报表。

**Architecture:** Spring Boot 3 后端 + H2 内存数据库 + React 18 前端，通过 REST API 通信。埋点通过 HandlerInterceptor 自动记录每次 API 调用，前端在 Dashboard 页面底部渲染调用统计图表。

**Tech Stack:** Java 17 / Spring Boot 3.2.x / Maven 3.9.x / H2 / Spring Data JPA / Apache POI 5.x / React 18 / TypeScript 5.x / Vite 5.x / Ant Design 5.x / ECharts 5.x / pnpm 8.x

---

## Global Constraints

- 所有接口统一响应格式 `{code, message, data}`
- 埋点记录字段：callerName, callerType, callerLevel, callerDept, apiPath, apiMethod, callTime, clientIp, userAgent
- 调用人信息从请求头 `X-Caller-Name / X-Caller-Type / X-Caller-Level / X-Caller-Dept` 提取，缺失使用默认值
- 导出格式为 Excel (.xlsx)，Content-Disposition 包含动态文件名
- 冒泡排序最大数组长度 100
- 后端端口 8080，前端端口 5173 (Vite dev server)，通过 Vite proxy 代理 `/api` 到后端
- 禁止使用 grep / glob 工具
- 禁止 Git 写操作

---

## File Structure

### testDj (后端) — 新建文件清单

```
testDj-main/
├── pom.xml                                          # Maven 项目配置
├── src/main/java/com/example/demo/
│   ├── DemoApplication.java                         # Spring Boot 启动类
│   ├── common/
│   │   └── ApiResult.java                           # 统一响应封装
│   ├── config/
│   │   └── WebConfig.java                           # 拦截器注册
│   ├── interceptor/
│   │   └── MetricsInterceptor.java                  # 埋点拦截器
│   ├── entity/
│   │   └── MetricsRecord.java                       # 埋点 JPA 实体
│   ├── repository/
│   │   └── MetricsRecordRepository.java             # 埋点 JPA Repository
│   ├── service/
│   │   ├── HashService.java                         # 哈希算法服务
│   │   ├── BubbleSortService.java                   # 冒泡排序服务
│   │   ├── ExportService.java                       # Excel 导出服务
│   │   └── MetricsService.java                      # 埋点查询服务
│   ├── controller/
│   │   ├── HelloWorldController.java                # HelloWorld 接口
│   │   ├── HashController.java                      # 哈希接口
│   │   ├── BubbleSortController.java                # 冒泡排序接口
│   │   ├── ExportController.java                    # 导出接口
│   │   └── MetricsController.java                   # 报表查询接口
│   └── dto/
│       ├── HashRequest.java                         # 哈希请求 DTO
│       ├── HashResponse.java                        # 哈希响应 DTO
│       ├── BubbleSortRequest.java                   # 冒泡排序请求 DTO
│       ├── BubbleSortResponse.java                  # 冒泡排序响应 DTO
│       ├── ExportRequest.java                       # 导出请求 DTO
│       ├── MetricsResponse.java                     # 报表响应 DTO
│       └── MetricsItem.java                         # 报表单项 DTO
├── src/main/resources/
│   └── application.yml                              # Spring Boot 配置
└── src/test/java/com/example/demo/
    ├── controller/
    │   ├── HelloWorldControllerTest.java            # HelloWorld 测试
    │   ├── HashControllerTest.java                  # Hash 测试
    │   └── BubbleSortControllerTest.java            # BubbleSort 测试
    └── service/
        ├── HashServiceTest.java                     # Hash 服务测试
        └── BubbleSortServiceTest.java               # BubbleSort 服务测试
```

### testDJnew (前端) — 新建文件清单

```
testDJnew-main/
├── package.json                                     # 项目依赖配置
├── tsconfig.json                                    # TypeScript 配置
├── tsconfig.node.json                               # Node TypeScript 配置
├── vite.config.ts                                   # Vite 配置 (含 proxy)
├── index.html                                       # 入口 HTML
├── src/
│   ├── main.tsx                                     # React 入口
│   ├── App.tsx                                      # 根组件 (路由)
│   ├── api/
│   │   └── client.ts                                # API 封装
│   ├── types/
│   │   └── index.ts                                 # 类型定义
│   ├── pages/
│   │   └── DashboardPage.tsx                        # Dashboard 主页面
│   ├── components/
│   │   ├── AlgorithmTabs.tsx                        # 三 Tab 容器
│   │   ├── HelloWorldTab.tsx                        # HelloWorld Tab
│   │   ├── HashTab.tsx                              # 哈希算法 Tab
│   │   ├── BubbleSortTab.tsx                        # 冒泡排序 Tab
│   │   ├── ExportButton.tsx                         # 导出按钮
│   │   ├── MetricsPanel.tsx                         # 报表面板
│   │   ├── DimensionSelector.tsx                    # 维度选择器
│   │   ├── ChartTypeSelector.tsx                    # 图表类型选择器
│   │   └── MetricsChart.tsx                         # ECharts 图表
│   └── styles/
│       └── dashboard.css                            # 页面样式
```

---

## Task 1: testDj — Maven 项目骨架与启动配置

**Files:**
- Create: `testDj-main/pom.xml`
- Create: `testDj-main/src/main/java/com/example/demo/DemoApplication.java`
- Create: `testDj-main/src/main/resources/application.yml`

**Interfaces:**
- Produces: Spring Boot 应用启动，端口 8080，H2 内存数据库自动建表，JPA 开启 DDL auto

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
    <description>Algorithm Demo with Metrics</description>
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
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
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
  application:
    name: demo
  h2:
    console:
      enabled: true
      path: /h2-console
  datasource:
    url: jdbc:h2:mem:metricsdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: update
    show-sql: false
```

- [ ] **Step 4: 编译验证**

Run: `cd /home/admin/.agentic-dev/runs/DEV-9d10e310-7901-11f1-8a9f-59ecae612580-773ed3b1-aebd-43a1-8efc-449f0978cd97/worktree/testDj-main && mvn compile -q 2>&1`

Expected: BUILD SUCCESS

---

## Task 2: testDj — 统一响应封装 (ApiResult)

**Files:**
- Create: `testDj-main/src/main/java/com/example/demo/common/ApiResult.java`

**Interfaces:**
- Produces: `ApiResult<T>` — `code` (int), `message` (String), `data` (T)
- Static factory: `ApiResult.success(T data)`, `ApiResult.error(int code, String message)`

- [ ] **Step 1: 创建 ApiResult.java**

```java
package com.example.demo.common;

public class ApiResult<T> {
    private int code;
    private String message;
    private T data;

    private ApiResult() {}

    public static <T> ApiResult<T> success(T data) {
        ApiResult<T> r = new ApiResult<>();
        r.code = 0;
        r.message = "success";
        r.data = data;
        return r;
    }

    public static <T> ApiResult<T> error(int code, String message) {
        ApiResult<T> r = new ApiResult<>();
        r.code = code;
        r.message = message;
        r.data = null;
        return r;
    }

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}
```

- [ ] **Step 2: 编译验证**

Run: `cd /home/admin/.agentic-dev/runs/DEV-9d10e310-7901-11f1-8a9f-59ecae612580-773ed3b1-aebd-43a1-8efc-449f0978cd97/worktree/testDj-main && mvn compile -q 2>&1`

Expected: BUILD SUCCESS

---

## Task 3: testDj — HelloWorld 接口

**Files:**
- Create: `testDj-main/src/main/java/com/example/demo/controller/HelloWorldController.java`
- Create: `testDj-main/src/test/java/com/example/demo/controller/HelloWorldControllerTest.java`

**Interfaces:**
- Consumes: `ApiResult` from Task 2
- Produces: `GET /api/helloworld` → `ApiResult<Map>` with `message` and `timestamp`

- [ ] **Step 1: 编写测试 (TDD)**

```java
package com.example.demo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class HelloWorldControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnHelloWorld() throws Exception {
        mockMvc.perform(get("/api/helloworld"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.message").value("Hello World"))
                .andExpect(jsonPath("$.data.timestamp").exists());
    }
}
```

- [ ] **Step 2: 运行测试确认失败**

Run: `cd /home/admin/.agentic-dev/runs/DEV-9d10e310-7901-11f1-8a9f-59ecae612580-773ed3b1-aebd-43a1-8efc-449f0978cd97/worktree/testDj-main && mvn test -Dtest=HelloWorldControllerTest -q 2>&1`

Expected: FAIL (404 or similar)

- [ ] **Step 3: 实现 HelloWorldController**

```java
package com.example.demo.controller;

import com.example.demo.common.ApiResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HelloWorldController {

    @GetMapping("/helloworld")
    public ApiResult<Map<String, Object>> helloWorld() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("message", "Hello World");
        data.put("timestamp", Instant.now().toString());
        return ApiResult.success(data);
    }
}
```

- [ ] **Step 4: 运行测试确认通过**

Run: `cd /home/admin/.agentic-dev/runs/DEV-9d10e310-7901-11f1-8a9f-59ecae612580-773ed3b1-aebd-43a1-8efc-449f0978cd97/worktree/testDj-main && mvn test -Dtest=HelloWorldControllerTest -q 2>&1`

Expected: PASS (Tests run: 1, Failures: 0)

---

## Task 4: testDj — 哈希算法 (HashService + HashController + DTOs)

**Files:**
- Create: `testDj-main/src/main/java/com/example/demo/dto/HashRequest.java`
- Create: `testDj-main/src/main/java/com/example/demo/dto/HashResponse.java`
- Create: `testDj-main/src/main/java/com/example/demo/service/HashService.java`
- Create: `testDj-main/src/main/java/com/example/demo/controller/HashController.java`
- Create: `testDj-main/src/test/java/com/example/demo/service/HashServiceTest.java`
- Create: `testDj-main/src/test/java/com/example/demo/controller/HashControllerTest.java`

**Interfaces:**
- Consumes: `ApiResult` from Task 2
- Produces: `POST /api/hash` body `{input, algorithm}` → `ApiResult<HashResponse>`
- `HashService.compute(String input, String algorithm)` → `String hash`

- [ ] **Step 1: 创建 DTO**

```java
// HashRequest.java
package com.example.demo.dto;

public class HashRequest {
    private String input;
    private String algorithm; // MD5 | SHA256

    public String getInput() { return input; }
    public void setInput(String input) { this.input = input; }
    public String getAlgorithm() { return algorithm; }
    public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }
}
```

```java
// HashResponse.java
package com.example.demo.dto;

public class HashResponse {
    private String input;
    private String algorithm;
    private String hash;

    public HashResponse(String input, String algorithm, String hash) {
        this.input = input;
        this.algorithm = algorithm;
        this.hash = hash;
    }

    public String getInput() { return input; }
    public String getAlgorithm() { return algorithm; }
    public String getHash() { return hash; }
}
```

- [ ] **Step 2: 编写 HashService 测试**

```java
package com.example.demo.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HashServiceTest {

    private final HashService service = new HashService();

    @Test
    void shouldComputeMD5() {
        String hash = service.compute("hello", "MD5");
        assertEquals("5d41402abc4b2a76b9719d911017c592", hash);
    }

    @Test
    void shouldComputeSHA256() {
        String hash = service.compute("hello", "SHA256");
        assertEquals(64, hash.length());
    }

    @Test
    void shouldThrowOnUnknownAlgorithm() {
        assertThrows(IllegalArgumentException.class, () ->
                service.compute("hello", "RIPEMD"));
    }

    @Test
    void shouldHandleEmptyInput() {
        String hash = service.compute("", "MD5");
        assertEquals("d41d8cd98f00b204e9800998ecf8427e", hash);
    }
}
```

- [ ] **Step 3: 实现 HashService**

```java
package com.example.demo.service;

import org.springframework.stereotype.Service;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class HashService {

    private static final char[] HEX = "0123456789abcdef".toCharArray();

    public String compute(String input, String algorithm) {
        if (!"MD5".equalsIgnoreCase(algorithm) && !"SHA256".equalsIgnoreCase(algorithm)) {
            throw new IllegalArgumentException(
                    "不支持的算法: " + algorithm + "，仅支持 MD5 / SHA256");
        }
        try {
            String algo = algorithm.equalsIgnoreCase("SHA256") ? "SHA-256" : "MD5";
            MessageDigest md = MessageDigest.getInstance(algo);
            byte[] digest = md.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return bytesToHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("算法不可用", e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        char[] chars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            chars[i * 2] = HEX[v >>> 4];
            chars[i * 2 + 1] = HEX[v & 0x0F];
        }
        return new String(chars);
    }
}
```

- [ ] **Step 4: 运行 HashService 测试**

Run: `cd /home/admin/.agentic-dev/runs/DEV-9d10e310-7901-11f1-8a9f-59ecae612580-773ed3b1-aebd-43a1-8efc-449f0978cd97/worktree/testDj-main && mvn test -Dtest=HashServiceTest -q 2>&1`

Expected: PASS

- [ ] **Step 5: 编写 HashController 测试**

```java
package com.example.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class HashControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldHashMD5() throws Exception {
        String body = objectMapper.writeValueAsString(
                Map.of("input", "hello", "algorithm", "MD5"));
        mockMvc.perform(post("/api/hash")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.hash").value("5d41402abc4b2a76b9719d911017c592"));
    }

    @Test
    void shouldRejectUnknownAlgorithm() throws Exception {
        String body = objectMapper.writeValueAsString(
                Map.of("input", "hello", "algorithm", "RIPEMD"));
        mockMvc.perform(post("/api/hash")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }
}
```

- [ ] **Step 6: 实现 HashController**

```java
package com.example.demo.controller;

import com.example.demo.common.ApiResult;
import com.example.demo.dto.HashRequest;
import com.example.demo.dto.HashResponse;
import com.example.demo.service.HashService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class HashController {

    private final HashService hashService;

    public HashController(HashService hashService) {
        this.hashService = hashService;
    }

    @PostMapping("/hash")
    public ResponseEntity<ApiResult<HashResponse>> hash(@RequestBody HashRequest request) {
        try {
            String hash = hashService.compute(request.getInput(), request.getAlgorithm());
            HashResponse data = new HashResponse(
                    request.getInput(),
                    request.getAlgorithm().toUpperCase(),
                    hash);
            return ResponseEntity.ok(ApiResult.success(data));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResult.error(400, e.getMessage()));
        }
    }
}
```

- [ ] **Step 7: 运行 HashController 测试**

Run: `cd /home/admin/.agentic-dev/runs/DEV-9d10e310-7901-11f1-8a9f-59ecae612580-773ed3b1-aebd-43a1-8efc-449f0978cd97/worktree/testDj-main && mvn test -Dtest=HashControllerTest -q 2>&1`

Expected: PASS

---

## Task 5: testDj — 冒泡排序 (BubbleSortService + Controller + DTOs)

**Files:**
- Create: `testDj-main/src/main/java/com/example/demo/dto/BubbleSortRequest.java`
- Create: `testDj-main/src/main/java/com/example/demo/dto/BubbleSortResponse.java`
- Create: `testDj-main/src/main/java/com/example/demo/service/BubbleSortService.java`
- Create: `testDj-main/src/main/java/com/example/demo/controller/BubbleSortController.java`
- Create: `testDj-main/src/test/java/com/example/demo/service/BubbleSortServiceTest.java`
- Create: `testDj-main/src/test/java/com/example/demo/controller/BubbleSortControllerTest.java`

**Interfaces:**
- Consumes: `ApiResult` from Task 2
- Produces: `POST /api/bubblesort` body `{array, order}` → `ApiResult<BubbleSortResponse>`
- `BubbleSortService.sort(int[] array, String order)` → `BubbleSortResponse` (含 original, sorted, steps, comparisons)

- [ ] **Step 1: 创建 DTO**

```java
// BubbleSortRequest.java
package com.example.demo.dto;

import java.util.List;

public class BubbleSortRequest {
    private List<Integer> array;
    private String order; // asc | desc

    public List<Integer> getArray() { return array; }
    public void setArray(List<Integer> array) { this.array = array; }
    public String getOrder() { return order; }
    public void setOrder(String order) { this.order = order; }
}
```

```java
// BubbleSortResponse.java
package com.example.demo.dto;

import java.util.List;

public class BubbleSortResponse {
    private List<Integer> original;
    private List<Integer> sorted;
    private List<SortStep> steps;
    private int comparisons;

    public BubbleSortResponse(List<Integer> original, List<Integer> sorted,
                               List<SortStep> steps, int comparisons) {
        this.original = original;
        this.sorted = sorted;
        this.steps = steps;
        this.comparisons = comparisons;
    }

    public List<Integer> getOriginal() { return original; }
    public List<Integer> getSorted() { return sorted; }
    public List<SortStep> getSteps() { return steps; }
    public int getComparisons() { return comparisons; }

    public static class SortStep {
        private int round;
        private List<Integer> array;

        public SortStep(int round, List<Integer> array) {
            this.round = round;
            this.array = array;
        }

        public int getRound() { return round; }
        public List<Integer> getArray() { return array; }
    }
}
```

- [ ] **Step 2: 编写 BubbleSortService 测试**

```java
package com.example.demo.service;

import com.example.demo.dto.BubbleSortResponse;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class BubbleSortServiceTest {

    private final BubbleSortService service = new BubbleSortService();

    @Test
    void shouldSortAscending() {
        BubbleSortResponse r = service.sort(new int[]{5, 3, 8, 1, 2}, "asc");
        assertArrayEquals(new int[]{1, 2, 3, 5, 8},
                r.getSorted().stream().mapToInt(i -> i).toArray());
        assertTrue(r.getComparisons() > 0);
        assertFalse(r.getSteps().isEmpty());
    }

    @Test
    void shouldSortDescending() {
        BubbleSortResponse r = service.sort(new int[]{5, 3, 8, 1, 2}, "desc");
        assertArrayEquals(new int[]{8, 5, 3, 2, 1},
                r.getSorted().stream().mapToInt(i -> i).toArray());
    }

    @Test
    void shouldHandleSingleElement() {
        BubbleSortResponse r = service.sort(new int[]{42}, "asc");
        assertEquals(List.of(42), r.getSorted());
        assertEquals(0, r.getComparisons());
    }

    @Test
    void shouldHandleEmptyArray() {
        BubbleSortResponse r = service.sort(new int[]{}, "asc");
        assertTrue(r.getSorted().isEmpty());
    }

    @Test
    void shouldThrowOnInvalidOrder() {
        assertThrows(IllegalArgumentException.class, () ->
                service.sort(new int[]{1, 2}, "random"));
    }
}
```

- [ ] **Step 3: 实现 BubbleSortService**

```java
package com.example.demo.service;

import com.example.demo.dto.BubbleSortResponse;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BubbleSortService {

    private static final int MAX_LENGTH = 100;

    public BubbleSortResponse sort(int[] array, String order) {
        if (!"asc".equalsIgnoreCase(order) && !"desc".equalsIgnoreCase(order)) {
            throw new IllegalArgumentException("排序方向仅支持 asc 或 desc");
        }
        if (array.length > MAX_LENGTH) {
            throw new IllegalArgumentException("数组长度不能超过 " + MAX_LENGTH);
        }

        List<Integer> original = Arrays.stream(array).boxed().collect(Collectors.toList());
        int[] working = Arrays.copyOf(array, array.length);
        boolean ascending = "asc".equalsIgnoreCase(order);
        List<BubbleSortResponse.SortStep> steps = new ArrayList<>();
        int comparisons = 0;
        int n = working.length;

        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                comparisons++;
                boolean shouldSwap = ascending ? working[j] > working[j + 1]
                                               : working[j] < working[j + 1];
                if (shouldSwap) {
                    int tmp = working[j];
                    working[j] = working[j + 1];
                    working[j + 1] = tmp;
                    swapped = true;
                }
            }
            steps.add(new BubbleSortResponse.SortStep(
                    i + 1,
                    Arrays.stream(working).boxed().collect(Collectors.toList())));
            if (!swapped) break;
        }

        List<Integer> sorted = Arrays.stream(working).boxed().collect(Collectors.toList());
        return new BubbleSortResponse(original, sorted, steps, comparisons);
    }
}
```

- [ ] **Step 4: 运行 BubbleSortService 测试**

Run: `cd /home/admin/.agentic-dev/runs/DEV-9d10e310-7901-11f1-8a9f-59ecae612580-773ed3b1-aebd-43a1-8efc-449f0978cd97/worktree/testDj-main && mvn test -Dtest=BubbleSortServiceTest -q 2>&1`

Expected: PASS

- [ ] **Step 5: 编写 BubbleSortController 测试**

```java
package com.example.demo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BubbleSortControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Test
    void shouldSortAscending() throws Exception {
        String body = objectMapper.writeValueAsString(
                Map.of("array", List.of(5, 3, 8, 1, 2), "order", "asc"));
        mockMvc.perform(post("/api/bubblesort")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.sorted[0]").value(1))
                .andExpect(jsonPath("$.data.sorted[4]").value(8))
                .andExpect(jsonPath("$.data.comparisons").isNumber())
                .andExpect(jsonPath("$.data.steps").isArray());
    }

    @Test
    void shouldRejectInvalidOrder() throws Exception {
        String body = objectMapper.writeValueAsString(
                Map.of("array", List.of(1, 2), "order", "bad"));
        mockMvc.perform(post("/api/bubblesort")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }
}
```

- [ ] **Step 6: 实现 BubbleSortController**

```java
package com.example.demo.controller;

import com.example.demo.common.ApiResult;
import com.example.demo.dto.BubbleSortRequest;
import com.example.demo.dto.BubbleSortResponse;
import com.example.demo.service.BubbleSortService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class BubbleSortController {

    private final BubbleSortService bubbleSortService;

    public BubbleSortController(BubbleSortService bubbleSortService) {
        this.bubbleSortService = bubbleSortService;
    }

    @PostMapping("/bubblesort")
    public ResponseEntity<ApiResult<BubbleSortResponse>> bubbleSort(
            @RequestBody BubbleSortRequest request) {
        try {
            int[] array = request.getArray().stream().mapToInt(i -> i).toArray();
            BubbleSortResponse data = bubbleSortService.sort(array, request.getOrder());
            return ResponseEntity.ok(ApiResult.success(data));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResult.error(400, e.getMessage()));
        }
    }
}
```

- [ ] **Step 7: 运行 BubbleSortController 测试**

Run: `cd /home/admin/.agentic-dev/runs/DEV-9d10e310-7901-11f1-8a9f-59ecae612580-773ed3b1-aebd-43a1-8efc-449f0978cd97/worktree/testDj-main && mvn test -Dtest=BubbleSortControllerTest -q 2>&1`

Expected: PASS

---

## Task 6: testDj — 埋点实体、Repository 与拦截器

**Files:**
- Create: `testDj-main/src/main/java/com/example/demo/entity/MetricsRecord.java`
- Create: `testDj-main/src/main/java/com/example/demo/repository/MetricsRecordRepository.java`
- Create: `testDj-main/src/main/java/com/example/demo/interceptor/MetricsInterceptor.java`
- Create: `testDj-main/src/main/java/com/example/demo/config/WebConfig.java`

**Interfaces:**
- Consumes: nothing from earlier tasks (standalone infrastructure)
- Produces:
  - `MetricsRecord` JPA entity — persisted to H2 on every `/api/**` request
  - `MetricsRecordRepository` — JPA Repository with `findByCallTimeBetween` and aggregation queries
  - `MetricsInterceptor` — reads `X-Caller-*` headers, async persists record
  - `WebConfig` — registers interceptor for `/api/**`

- [ ] **Step 1: 创建 MetricsRecord 实体**

```java
package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "metrics_record")
public class MetricsRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "caller_name", nullable = false, length = 100)
    private String callerName;

    @Column(name = "caller_type", nullable = false, length = 50)
    private String callerType;

    @Column(name = "caller_level", nullable = false, length = 50)
    private String callerLevel;

    @Column(name = "caller_dept", nullable = false, length = 100)
    private String callerDept;

    @Column(name = "api_path", nullable = false, length = 200)
    private String apiPath;

    @Column(name = "api_method", nullable = false, length = 10)
    private String apiMethod;

    @Column(name = "call_time", nullable = false)
    private Instant callTime;

    @Column(name = "client_ip", length = 50)
    private String clientIp;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    public MetricsRecord() {}

    public MetricsRecord(String callerName, String callerType, String callerLevel,
                         String callerDept, String apiPath, String apiMethod,
                         Instant callTime, String clientIp, String userAgent) {
        this.callerName = callerName;
        this.callerType = callerType;
        this.callerLevel = callerLevel;
        this.callerDept = callerDept;
        this.apiPath = apiPath;
        this.apiMethod = apiMethod;
        this.callTime = callTime;
        this.clientIp = clientIp;
        this.userAgent = userAgent;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCallerName() { return callerName; }
    public void setCallerName(String callerName) { this.callerName = callerName; }
    public String getCallerType() { return callerType; }
    public void setCallerType(String callerType) { this.callerType = callerType; }
    public String getCallerLevel() { return callerLevel; }
    public void setCallerLevel(String callerLevel) { this.callerLevel = callerLevel; }
    public String getCallerDept() { return callerDept; }
    public void setCallerDept(String callerDept) { this.callerDept = callerDept; }
    public String getApiPath() { return apiPath; }
    public void setApiPath(String apiPath) { this.apiPath = apiPath; }
    public String getApiMethod() { return apiMethod; }
    public void setApiMethod(String apiMethod) { this.apiMethod = apiMethod; }
    public Instant getCallTime() { return callTime; }
    public void setCallTime(Instant callTime) { this.callTime = callTime; }
    public String getClientIp() { return clientIp; }
    public void setClientIp(String clientIp) { this.clientIp = clientIp; }
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
}
```

- [ ] **Step 2: 创建 MetricsRecordRepository**

```java
package com.example.demo.repository;

import com.example.demo.entity.MetricsRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface MetricsRecordRepository extends JpaRepository<MetricsRecord, Long> {

    List<MetricsRecord> findByCallTimeBetween(Instant start, Instant end);

    @Query("SELECT m.callerType, COUNT(m) FROM MetricsRecord m " +
           "WHERE m.callTime BETWEEN :start AND :end GROUP BY m.callerType")
    List<Object[]> countByCallerType(@Param("start") Instant start, @Param("end") Instant end);

    @Query("SELECT m.callerLevel, COUNT(m) FROM MetricsRecord m " +
           "WHERE m.callTime BETWEEN :start AND :end GROUP BY m.callerLevel")
    List<Object[]> countByCallerLevel(@Param("start") Instant start, @Param("end") Instant end);

    @Query("SELECT m.callerDept, COUNT(m) FROM MetricsRecord m " +
           "WHERE m.callTime BETWEEN :start AND :end GROUP BY m.callerDept")
    List<Object[]> countByCallerDept(@Param("start") Instant start, @Param("end") Instant end);

    @Query("SELECT m.apiPath, COUNT(m) FROM MetricsRecord m " +
           "WHERE m.callerType = :type AND m.callTime BETWEEN :start AND :end " +
           "GROUP BY m.apiPath")
    List<Object[]> countByApiPathAndCallerType(@Param("type") String type,
                                                @Param("start") Instant start,
                                                @Param("end") Instant end);

    @Query("SELECT m.apiPath, COUNT(m) FROM MetricsRecord m " +
           "WHERE m.callerLevel = :level AND m.callTime BETWEEN :start AND :end " +
           "GROUP BY m.apiPath")
    List<Object[]> countByApiPathAndCallerLevel(@Param("level") String level,
                                                 @Param("start") Instant start,
                                                 @Param("end") Instant end);

    @Query("SELECT m.apiPath, COUNT(m) FROM MetricsRecord m " +
           "WHERE m.callerDept = :dept AND m.callTime BETWEEN :start AND :end " +
           "GROUP BY m.apiPath")
    List<Object[]> countByApiPathAndCallerDept(@Param("dept") String dept,
                                                @Param("start") Instant start,
                                                @Param("end") Instant end);
}
```

- [ ] **Step 3: 创建 MetricsInterceptor**

```java
package com.example.demo.interceptor;

import com.example.demo.entity.MetricsRecord;
import com.example.demo.repository.MetricsRecordRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Instant;

@Component
public class MetricsInterceptor implements HandlerInterceptor {

    private final MetricsRecordRepository repository;

    public MetricsInterceptor(MetricsRecordRepository repository) {
        this.repository = repository;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                 Object handler, Exception ex) {
        String callerName = request.getHeader("X-Caller-Name");
        String callerType = request.getHeader("X-Caller-Type");
        String callerLevel = request.getHeader("X-Caller-Level");
        String callerDept = request.getHeader("X-Caller-Dept");

        MetricsRecord record = new MetricsRecord(
                defaultIfEmpty(callerName, "anonymous"),
                defaultIfEmpty(callerType, "未知"),
                defaultIfEmpty(callerLevel, "未知"),
                defaultIfEmpty(callerDept, "未知"),
                request.getRequestURI(),
                request.getMethod(),
                Instant.now(),
                request.getRemoteAddr(),
                request.getHeader("User-Agent")
        );
        repository.save(record);
    }

    private String defaultIfEmpty(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
```

- [ ] **Step 4: 创建 WebConfig**

```java
package com.example.demo.config;

import com.example.demo.interceptor.MetricsInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final MetricsInterceptor metricsInterceptor;

    public WebConfig(MetricsInterceptor metricsInterceptor) {
        this.metricsInterceptor = metricsInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(metricsInterceptor)
                .addPathPatterns("/api/**");
    }
}
```

- [ ] **Step 5: 编译验证**

Run: `cd /home/admin/.agentic-dev/runs/DEV-9d10e310-7901-11f1-8a9f-59ecae612580-773ed3b1-aebd-43a1-8efc-449f0978cd97/worktree/testDj-main && mvn compile -q 2>&1`

Expected: BUILD SUCCESS

---

## Task 7: testDj — 埋点报表接口 (MetricsService + MetricsController + DTOs)

**Files:**
- Create: `testDj-main/src/main/java/com/example/demo/dto/MetricsResponse.java`
- Create: `testDj-main/src/main/java/com/example/demo/dto/MetricsItem.java`
- Create: `testDj-main/src/main/java/com/example/demo/service/MetricsService.java`
- Create: `testDj-main/src/main/java/com/example/demo/controller/MetricsController.java`

**Interfaces:**
- Consumes: `MetricsRecordRepository` from Task 6, `ApiResult` from Task 2
- Produces: `GET /api/metrics?dimension=&startDate=&endDate=` → `ApiResult<MetricsResponse>`
- `MetricsService.query(String dimension, String startDate, String endDate)` → `MetricsResponse`

- [ ] **Step 1: 创建 DTO**

```java
// MetricsItem.java
package com.example.demo.dto;

import java.util.List;

public class MetricsItem {
    private String label;
    private long count;
    private List<MetricsItem> subItems;

    public MetricsItem(String label, long count, List<MetricsItem> subItems) {
        this.label = label;
        this.count = count;
        this.subItems = subItems;
    }

    public String getLabel() { return label; }
    public long getCount() { return count; }
    public List<MetricsItem> getSubItems() { return subItems; }
}
```

```java
// MetricsResponse.java
package com.example.demo.dto;

import java.util.List;

public class MetricsResponse {
    private String dimension;
    private List<MetricsItem> items;
    private long totalCalls;

    public MetricsResponse(String dimension, List<MetricsItem> items, long totalCalls) {
        this.dimension = dimension;
        this.items = items;
        this.totalCalls = totalCalls;
    }

    public String getDimension() { return dimension; }
    public List<MetricsItem> getItems() { return items; }
    public long getTotalCalls() { return totalCalls; }
}
```

- [ ] **Step 2: 实现 MetricsService**

```java
package com.example.demo.service;

import com.example.demo.dto.MetricsItem;
import com.example.demo.dto.MetricsResponse;
import com.example.demo.repository.MetricsRecordRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

@Service
public class MetricsService {

    private final MetricsRecordRepository repository;

    public MetricsService(MetricsRecordRepository repository) {
        this.repository = repository;
    }

    public MetricsResponse query(String dimension, String startDate, String endDate) {
        Instant start = parseDate(startDate, LocalDate.of(2000, 1, 1));
        Instant end = parseDate(endDate, LocalDate.now().plusDays(1));

        BiFunction<String, Instant[], List<Object[]>> subQuery = switch (dimension) {
            case "personType" -> (label, range) ->
                    repository.countByApiPathAndCallerType(label, range[0], range[1]);
            case "level" -> (label, range) ->
                    repository.countByApiPathAndCallerLevel(label, range[0], range[1]);
            case "department" -> (label, range) ->
                    repository.countByApiPathAndCallerDept(label, range[0], range[1]);
            default -> throw new IllegalArgumentException(
                    "不支持的维度: " + dimension + "，仅支持 personType / level / department");
        };

        List<Object[]> agg = switch (dimension) {
            case "personType" -> repository.countByCallerType(start, end);
            case "level" -> repository.countByCallerLevel(start, end);
            case "department" -> repository.countByCallerDept(start, end);
            default -> List.of();
        };

        List<MetricsItem> items = new ArrayList<>();
        long total = 0;
        for (Object[] row : agg) {
            String label = (String) row[0];
            long count = (Long) row[1];
            total += count;

            List<MetricsItem> subItems = new ArrayList<>();
            List<Object[]> subRows = subQuery.apply(label, new Instant[]{start, end});
            for (Object[] sub : subRows) {
                subItems.add(new MetricsItem((String) sub[0], (Long) sub[1], List.of()));
            }
            items.add(new MetricsItem(label, count, subItems));
        }

        return new MetricsResponse(dimension, items, total);
    }

    private Instant parseDate(String dateStr, LocalDate fallback) {
        if (dateStr == null || dateStr.isBlank()) {
            return fallback.atStartOfDay().toInstant(ZoneOffset.UTC);
        }
        return LocalDate.parse(dateStr).atStartOfDay().toInstant(ZoneOffset.UTC);
    }
}
```

- [ ] **Step 3: 实现 MetricsController**

```java
package com.example.demo.controller;

import com.example.demo.common.ApiResult;
import com.example.demo.dto.MetricsResponse;
import com.example.demo.service.MetricsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class MetricsController {

    private final MetricsService metricsService;

    public MetricsController(MetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @GetMapping("/metrics")
    public ApiResult<MetricsResponse> metrics(
            @RequestParam String dimension,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        MetricsResponse data = metricsService.query(dimension, startDate, endDate);
        return ApiResult.success(data);
    }
}
```

- [ ] **Step 4: 编译验证**

Run: `cd /home/admin/.agentic-dev/runs/DEV-9d10e310-7901-11f1-8a9f-59ecae612580-773ed3b1-aebd-43a1-8efc-449f0978cd97/worktree/testDj-main && mvn compile -q 2>&1`

Expected: BUILD SUCCESS

---

## Task 8: testDj — Excel 导出接口 (ExportService + ExportController + DTOs)

**Files:**
- Create: `testDj-main/src/main/java/com/example/demo/dto/ExportRequest.java`
- Create: `testDj-main/src/main/java/com/example/demo/service/ExportService.java`
- Create: `testDj-main/src/main/java/com/example/demo/controller/ExportController.java`

**Interfaces:**
- Consumes: `ApiResult` from Task 2
- Produces: `POST /api/export` body `{type, data}` → binary `.xlsx` file
- `ExportService.export(String type, Map<String, Object> data)` → `byte[]`

- [ ] **Step 1: 创建 ExportRequest DTO**

```java
package com.example.demo.dto;

import java.util.Map;

public class ExportRequest {
    private String type; // helloworld | hash | bubblesort
    private Map<String, Object> data;

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Map<String, Object> getData() { return data; }
    public void setData(Map<String, Object> data) { this.data = data; }
}
```

- [ ] **Step 2: 实现 ExportService**

```java
package com.example.demo.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class ExportService {

    public byte[] export(String type, Map<String, Object> data) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(type);
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            int rowIdx = 0;
            Row headerRow = sheet.createRow(rowIdx++);
            headerRow.createCell(0).setCellValue("Key");
            headerRow.createCell(1).setCellValue("Value");
            headerRow.getCell(0).setCellStyle(headerStyle);
            headerRow.getCell(1).setCellStyle(headerStyle);

            // Flatten nested structures
            flattenAndWrite(sheet, rowIdx, data, "");

            for (int i = 0; i < 2; i++) sheet.autoSizeColumn(i);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            return bos.toByteArray();
        }
    }

    @SuppressWarnings("unchecked")
    private int flattenAndWrite(Sheet sheet, int startRow, Map<String, Object> data, String prefix) {
        int rowIdx = startRow;
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Map) {
                rowIdx = flattenAndWrite(sheet, rowIdx, (Map<String, Object>) value, key);
            } else if (value instanceof List) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(key);
                row.createCell(1).setCellValue(formatList((List<?>) value));
            } else {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(key);
                row.createCell(1).setCellValue(value != null ? value.toString() : "");
            }
        }
        return rowIdx;
    }

    private String formatList(List<?> list) {
        if (list.isEmpty()) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (Object o : list) {
            if (sb.length() > 1) sb.append(", ");
            sb.append(o);
        }
        sb.append("]");
        return sb.toString();
    }
}
```

- [ ] **Step 3: 实现 ExportController**

```java
package com.example.demo.controller;

import com.example.demo.dto.ExportRequest;
import com.example.demo.service.ExportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.Instant;

@RestController
@RequestMapping("/api")
public class ExportController {

    private final ExportService exportService;

    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    @PostMapping("/export")
    public ResponseEntity<byte[]> export(@RequestBody ExportRequest request) throws IOException {
        byte[] excelBytes = exportService.export(request.getType(), request.getData());
        String filename = "export-" + request.getType() + "-"
                + Instant.now().toEpochMilli() + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelBytes);
    }
}
```

- [ ] **Step 4: 编译验证**

Run: `cd /home/admin/.agentic-dev/runs/DEV-9d10e310-7901-11f1-8a9f-59ecae612580-773ed3b1-aebd-43a1-8efc-449f0978cd97/worktree/testDj-main && mvn compile -q 2>&1`

Expected: BUILD SUCCESS

- [ ] **Step 5: 运行全部后端测试**

Run: `cd /home/admin/.agentic-dev/runs/DEV-9d10e310-7901-11f1-8a9f-59ecae612580-773ed3b1-aebd-43a1-8efc-449f0978cd97/worktree/testDj-main && mvn test -q 2>&1`

Expected: BUILD SUCCESS (all tests pass)

---

## Task 9: testDJnew — Vite + React + TypeScript 项目骨架

**Files:**
- Create: `testDJnew-main/package.json`
- Create: `testDJnew-main/tsconfig.json`
- Create: `testDJnew-main/tsconfig.node.json`
- Create: `testDJnew-main/vite.config.ts`
- Create: `testDJnew-main/index.html`
- Create: `testDJnew-main/src/main.tsx`
- Create: `testDJnew-main/src/App.tsx`
- Create: `testDJnew-main/src/types/index.ts`

**Interfaces:**
- Produces: Vite dev server on port 5173, proxy `/api` → `http://localhost:8080`, React 18 + Ant Design + ECharts ready

- [ ] **Step 1: 创建 package.json**

```json
{
  "name": "testdjnew",
  "private": true,
  "version": "0.0.1",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "tsc && vite build",
    "preview": "vite preview"
  },
  "dependencies": {
    "react": "^18.3.1",
    "react-dom": "^18.3.1",
    "antd": "^5.20.0",
    "echarts": "^5.5.1",
    "echarts-for-react": "^3.0.2"
  },
  "devDependencies": {
    "@types/react": "^18.3.3",
    "@types/react-dom": "^18.3.0",
    "@vitejs/plugin-react": "^4.3.1",
    "typescript": "^5.5.4",
    "vite": "^5.4.0"
  }
}
```

- [ ] **Step 2: 创建 tsconfig.json**

```json
{
  "compilerOptions": {
    "target": "ES2020",
    "useDefineForClassFields": true,
    "lib": ["ES2020", "DOM", "DOM.Iterable"],
    "module": "ESNext",
    "skipLibCheck": true,
    "moduleResolution": "bundler",
    "allowImportingTsExtensions": true,
    "resolveJsonModule": true,
    "isolatedModules": true,
    "noEmit": true,
    "jsx": "react-jsx",
    "strict": true,
    "noUnusedLocals": false,
    "noUnusedParameters": false,
    "noFallthroughCasesInSwitch": true
  },
  "include": ["src"],
  "references": [{ "path": "./tsconfig.node.json" }]
}
```

- [ ] **Step 3: 创建 tsconfig.node.json**

```json
{
  "compilerOptions": {
    "composite": true,
    "skipLibCheck": true,
    "module": "ESNext",
    "moduleResolution": "bundler",
    "allowSyntheticDefaultImports": true
  },
  "include": ["vite.config.ts"]
}
```

- [ ] **Step 4: 创建 vite.config.ts**

```typescript
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
});
```

- [ ] **Step 5: 创建 index.html**

```html
<!DOCTYPE html>
<html lang="zh-CN">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>算法演示平台</title>
  </head>
  <body>
    <div id="root"></div>
    <script type="module" src="/src/main.tsx"></script>
  </body>
</html>
```

- [ ] **Step 6: 创建 src/main.tsx**

```tsx
import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);
```

- [ ] **Step 7: 创建 src/App.tsx**

```tsx
import { ConfigProvider } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import DashboardPage from './pages/DashboardPage';

function App() {
  return (
    <ConfigProvider locale={zhCN}>
      <DashboardPage />
    </ConfigProvider>
  );
}

export default App;
```

- [ ] **Step 8: 创建 src/types/index.ts**

```typescript
// 统一后端响应格式
export interface ApiResult<T> {
  code: number;
  message: string;
  data: T;
}

// HelloWorld
export interface HelloWorldData {
  message: string;
  timestamp: string;
}

// Hash
export interface HashData {
  input: string;
  algorithm: string;
  hash: string;
}

// BubbleSort
export interface SortStep {
  round: number;
  array: number[];
}

export interface BubbleSortData {
  original: number[];
  sorted: number[];
  steps: SortStep[];
  comparisons: number;
}

// Metrics
export interface MetricsItem {
  label: string;
  count: number;
  subItems: MetricsItem[];
}

export interface MetricsResponse {
  dimension: string;
  items: MetricsItem[];
  totalCalls: number;
}

// 页面状态
export type TabKey = 'helloworld' | 'hash' | 'bubblesort';
export type Dimension = 'personType' | 'level' | 'department';
export type ChartType = 'line' | 'pie' | 'bar';
```

- [ ] **Step 9: 安装依赖验证**

Run: `cd /home/admin/.agentic-dev/runs/DEV-9d10e310-7901-11f1-8a9f-59ecae612580-773ed3b1-aebd-43a1-8efc-449f0978cd97/worktree/testDJnew-main && pnpm install 2>&1`

Expected: dependencies installed successfully

---

## Task 10: testDJnew — API 客户端封装

**Files:**
- Create: `testDJnew-main/src/api/client.ts`

**Interfaces:**
- Consumes: types from Task 9
- Produces:
  - `fetchHelloWorld()` → `Promise<ApiResult<HelloWorldData>>`
  - `fetchHash(input, algorithm)` → `Promise<ApiResult<HashData>>`
  - `fetchBubbleSort(array, order)` → `Promise<ApiResult<BubbleSortData>>`
  - `exportExcel(type, data)` → `Promise<Blob>`
  - `fetchMetrics(dimension, startDate?, endDate?)` → `Promise<ApiResult<MetricsResponse>>`

- [ ] **Step 1: 创建 src/api/client.ts**

```typescript
import type {
  ApiResult,
  HelloWorldData,
  HashData,
  BubbleSortData,
  MetricsResponse,
  Dimension,
} from '../types';

const BASE_URL = '/api';

// 请求头注入调用人信息（埋点用）
function callerHeaders(): Record<string, string> {
  return {
    'X-Caller-Name': 'demo-user',
    'X-Caller-Type': '正式员工',
    'X-Caller-Level': 'P7',
    'X-Caller-Dept': '技术部',
  };
}

async function request<T>(url: string, options?: RequestInit): Promise<ApiResult<T>> {
  const res = await fetch(`${BASE_URL}${url}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...callerHeaders(),
      ...options?.headers,
    },
  });
  return res.json();
}

export async function fetchHelloWorld(): Promise<ApiResult<HelloWorldData>> {
  return request<HelloWorldData>('/helloworld');
}

export async function fetchHash(
  input: string,
  algorithm: string
): Promise<ApiResult<HashData>> {
  return request<HashData>('/hash', {
    method: 'POST',
    body: JSON.stringify({ input, algorithm }),
  });
}

export async function fetchBubbleSort(
  array: number[],
  order: string
): Promise<ApiResult<BubbleSortData>> {
  return request<BubbleSortData>('/bubblesort', {
    method: 'POST',
    body: JSON.stringify({ array, order }),
  });
}

export async function exportExcel(type: string, data: unknown): Promise<Blob> {
  const res = await fetch(`${BASE_URL}/export`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      ...callerHeaders(),
    },
    body: JSON.stringify({ type, data }),
  });
  if (!res.ok) throw new Error('导出失败');
  return res.blob();
}

export async function fetchMetrics(
  dimension: Dimension,
  startDate?: string,
  endDate?: string
): Promise<ApiResult<MetricsResponse>> {
  const params = new URLSearchParams({ dimension });
  if (startDate) params.set('startDate', startDate);
  if (endDate) params.set('endDate', endDate);
  return request<MetricsResponse>(`/metrics?${params.toString()}`);
}
```

---

## Task 11: testDJnew — Dashboard 页面 + 三 Tab 组件

**Files:**
- Create: `testDJnew-main/src/pages/DashboardPage.tsx`
- Create: `testDJnew-main/src/components/AlgorithmTabs.tsx`
- Create: `testDJnew-main/src/components/HelloWorldTab.tsx`
- Create: `testDJnew-main/src/components/HashTab.tsx`
- Create: `testDJnew-main/src/components/BubbleSortTab.tsx`
- Create: `testDJnew-main/src/components/ExportButton.tsx`
- Create: `testDJnew-main/src/styles/dashboard.css`

**Interfaces:**
- Consumes: API client from Task 10, types from Task 9
- Produces: Full Dashboard page with 3 tabs, export button, metrics panel placeholder

- [ ] **Step 1: 创建 DashboardPage.tsx**

```tsx
import { useState, useCallback } from 'react';
import { Typography } from 'antd';
import AlgorithmTabs from '../components/AlgorithmTabs';
import ExportButton from '../components/ExportButton';
import MetricsPanel from '../components/MetricsPanel';
import type { TabKey, HelloWorldData, HashData, BubbleSortData } from '../types';
import '../styles/dashboard.css';

const { Title } = Typography;

type TabResult = HelloWorldData | HashData | BubbleSortData | null;

export default function DashboardPage() {
  const [activeTab, setActiveTab] = useState<TabKey>('helloworld');
  const [tabResults, setTabResults] = useState<Record<TabKey, TabResult>>({
    helloworld: null,
    hash: null,
    bubblesort: null,
  });

  const handleResult = useCallback((tab: TabKey, data: TabResult) => {
    setTabResults((prev) => ({ ...prev, [tab]: data }));
  }, []);

  const currentResult = tabResults[activeTab];

  return (
    <div className="dashboard-container">
      <Title level={2} style={{ textAlign: 'center', marginBottom: 24 }}>
        算法演示平台
      </Title>

      <AlgorithmTabs
        activeTab={activeTab}
        onTabChange={setActiveTab}
        onResult={handleResult}
      />

      {currentResult && (
        <div className="export-area">
          <ExportButton type={activeTab} data={currentResult} />
        </div>
      )}

      <MetricsPanel />
    </div>
  );
}
```

- [ ] **Step 2: 创建 AlgorithmTabs.tsx**

```tsx
import { Tabs } from 'antd';
import HelloWorldTab from './HelloWorldTab';
import HashTab from './HashTab';
import BubbleSortTab from './BubbleSortTab';
import type { TabKey, HelloWorldData, HashData, BubbleSortData } from '../types';

interface Props {
  activeTab: TabKey;
  onTabChange: (tab: TabKey) => void;
  onResult: (tab: TabKey, data: HelloWorldData | HashData | BubbleSortData) => void;
}

export default function AlgorithmTabs({ activeTab, onTabChange, onResult }: Props) {
  const items = [
    {
      key: 'helloworld',
      label: 'HelloWorld',
      children: <HelloWorldTab onResult={(d) => onResult('helloworld', d)} />,
    },
    {
      key: 'hash',
      label: '哈希算法',
      children: <HashTab onResult={(d) => onResult('hash', d)} />,
    },
    {
      key: 'bubblesort',
      label: '冒泡排序',
      children: <BubbleSortTab onResult={(d) => onResult('bubblesort', d)} />,
    },
  ];

  return (
    <Tabs
      activeKey={activeTab}
      onChange={(k) => onTabChange(k as TabKey)}
      items={items}
      size="large"
    />
  );
}
```

- [ ] **Step 3: 创建 HelloWorldTab.tsx**

```tsx
import { useEffect, useState } from 'react';
import { Spin, Alert, Descriptions } from 'antd';
import { fetchHelloWorld } from '../api/client';
import type { HelloWorldData } from '../types';

interface Props {
  onResult: (data: HelloWorldData) => void;
}

export default function HelloWorldTab({ onResult }: Props) {
  const [loading, setLoading] = useState(true);
  const [data, setData] = useState<HelloWorldData | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    fetchHelloWorld()
      .then((res) => {
        if (res.code === 0) {
          setData(res.data);
          onResult(res.data);
        } else {
          setError(res.message);
        }
      })
      .catch((e) => setError(e.message))
      .finally(() => setLoading(false));
  }, [onResult]);

  if (loading) return <Spin tip="加载中..." />;
  if (error) return <Alert type="error" message={error} />;

  return (
    <Descriptions bordered column={1}>
      <Descriptions.Item label="消息">{data?.message}</Descriptions.Item>
      <Descriptions.Item label="时间戳">{data?.timestamp}</Descriptions.Item>
    </Descriptions>
  );
}
```

- [ ] **Step 4: 创建 HashTab.tsx**

```tsx
import { useState } from 'react';
import { Input, Select, Button, Spin, Alert, Descriptions, Space } from 'antd';
import { fetchHash } from '../api/client';
import type { HashData } from '../types';

interface Props {
  onResult: (data: HashData) => void;
}

export default function HashTab({ onResult }: Props) {
  const [input, setInput] = useState('hello');
  const [algorithm, setAlgorithm] = useState('MD5');
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<HashData | null>(null);
  const [error, setError] = useState<string | null>(null);

  const handleExecute = async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await fetchHash(input, algorithm);
      if (res.code === 0) {
        setData(res.data);
        onResult(res.data);
      } else {
        setError(res.message);
      }
    } catch (e: any) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <Space style={{ marginBottom: 16 }}>
        <Input
          placeholder="输入文本"
          value={input}
          onChange={(e) => setInput(e.target.value)}
          style={{ width: 260 }}
        />
        <Select
          value={algorithm}
          onChange={setAlgorithm}
          options={[
            { label: 'MD5', value: 'MD5' },
            { label: 'SHA256', value: 'SHA256' },
          ]}
          style={{ width: 120 }}
        />
        <Button type="primary" onClick={handleExecute} loading={loading}>
          执行
        </Button>
      </Space>

      {loading && <Spin />}
      {error && <Alert type="error" message={error} />}
      {data && (
        <Descriptions bordered column={1}>
          <Descriptions.Item label="输入">{data.input}</Descriptions.Item>
          <Descriptions.Item label="算法">{data.algorithm}</Descriptions.Item>
          <Descriptions.Item label="哈希值">
            <code style={{ wordBreak: 'break-all' }}>{data.hash}</code>
          </Descriptions.Item>
        </Descriptions>
      )}
    </div>
  );
}
```

- [ ] **Step 5: 创建 BubbleSortTab.tsx**

```tsx
import { useState } from 'react';
import { Input, Select, Button, Spin, Alert, Table, Space } from 'antd';
import { fetchBubbleSort } from '../api/client';
import type { BubbleSortData } from '../types';

interface Props {
  onResult: (data: BubbleSortData) => void;
}

export default function BubbleSortTab({ onResult }: Props) {
  const [arrayStr, setArrayStr] = useState('5,3,8,1,2');
  const [order, setOrder] = useState('asc');
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<BubbleSortData | null>(null);
  const [error, setError] = useState<string | null>(null);

  const handleExecute = async () => {
    const array = arrayStr
      .split(',')
      .map((s) => s.trim())
      .filter(Boolean)
      .map(Number);
    if (array.some(isNaN)) {
      setError('请输入有效的数字数组，以逗号分隔');
      return;
    }

    setLoading(true);
    setError(null);
    try {
      const res = await fetchBubbleSort(array, order);
      if (res.code === 0) {
        setData(res.data);
        onResult(res.data);
      } else {
        setError(res.message);
      }
    } catch (e: any) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  };

  const stepColumns = [
    { title: '轮次', dataIndex: 'round', key: 'round' },
    {
      title: '数组状态',
      dataIndex: 'array',
      key: 'array',
      render: (arr: number[]) => `[${arr.join(', ')}]`,
    },
  ];

  return (
    <div>
      <Space style={{ marginBottom: 16 }}>
        <Input
          placeholder="数组 (逗号分隔)"
          value={arrayStr}
          onChange={(e) => setArrayStr(e.target.value)}
          style={{ width: 260 }}
        />
        <Select
          value={order}
          onChange={setOrder}
          options={[
            { label: '升序 asc', value: 'asc' },
            { label: '降序 desc', value: 'desc' },
          ]}
          style={{ width: 140 }}
        />
        <Button type="primary" onClick={handleExecute} loading={loading}>
          执行
        </Button>
      </Space>

      {loading && <Spin />}
      {error && <Alert type="error" message={error} />}
      {data && (
        <div>
          <p>
            <strong>原始数组:</strong> [{data.original.join(', ')}]
          </p>
          <p>
            <strong>排序结果:</strong> [{data.sorted.join(', ')}]
          </p>
          <p>
            <strong>比较次数:</strong> {data.comparisons}
          </p>
          <Table
            dataSource={data.steps}
            columns={stepColumns}
            rowKey="round"
            pagination={false}
            size="small"
            style={{ maxWidth: 500 }}
          />
        </div>
      )}
    </div>
  );
}
```

- [ ] **Step 6: 创建 ExportButton.tsx**

```tsx
import { Button, message } from 'antd';
import { DownloadOutlined } from '@ant-design/icons';
import { exportExcel } from '../api/client';

interface Props {
  type: string;
  data: unknown;
}

export default function ExportButton({ type, data }: Props) {
  const handleExport = async () => {
    try {
      const blob = await exportExcel(type, data);
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `export-${type}-${Date.now()}.xlsx`;
      a.click();
      URL.revokeObjectURL(url);
      message.success('导出成功');
    } catch (e: any) {
      message.error('导出失败: ' + e.message);
    }
  };

  return (
    <Button
      type="primary"
      icon={<DownloadOutlined />}
      onClick={handleExport}
    >
      导出 Excel
    </Button>
  );
}
```

- [ ] **Step 7: 创建 dashboard.css**

```css
.dashboard-container {
  max-width: 960px;
  margin: 0 auto;
  padding: 24px 16px;
}

.export-area {
  margin: 16px 0;
  text-align: right;
}

.metrics-panel {
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #f0f0f0;
}
```

---

## Task 12: testDJnew — 可视化报表 (MetricsPanel + ECharts)

**Files:**
- Create: `testDJnew-main/src/components/MetricsPanel.tsx`
- Create: `testDJnew-main/src/components/DimensionSelector.tsx`
- Create: `testDJnew-main/src/components/ChartTypeSelector.tsx`
- Create: `testDJnew-main/src/components/MetricsChart.tsx`

**Interfaces:**
- Consumes: `fetchMetrics` from Task 10, types from Task 9
- Produces: Full metrics panel with dimension selector, chart type selector, and ECharts rendering (line/pie/bar)

- [ ] **Step 1: 创建 DimensionSelector.tsx**

```tsx
import { Select } from 'antd';
import type { Dimension } from '../types';

interface Props {
  value: Dimension;
  onChange: (d: Dimension) => void;
}

const OPTIONS: { label: string; value: Dimension }[] = [
  { label: '人员类型', value: 'personType' },
  { label: '人员层级', value: 'level' },
  { label: '人员部门', value: 'department' },
];

export default function DimensionSelector({ value, onChange }: Props) {
  return (
    <Select
      value={value}
      onChange={onChange}
      options={OPTIONS}
      style={{ width: 160 }}
    />
  );
}
```

- [ ] **Step 2: 创建 ChartTypeSelector.tsx**

```tsx
import { Radio } from 'antd';
import type { ChartType } from '../types';

interface Props {
  value: ChartType;
  onChange: (c: ChartType) => void;
}

export default function ChartTypeSelector({ value, onChange }: Props) {
  return (
    <Radio.Group
      value={value}
      onChange={(e) => onChange(e.target.value)}
      optionType="button"
      buttonStyle="solid"
    >
      <Radio.Button value="bar">柱状图</Radio.Button>
      <Radio.Button value="line">折线图</Radio.Button>
      <Radio.Button value="pie">饼图</Radio.Button>
    </Radio.Group>
  );
}
```

- [ ] **Step 3: 创建 MetricsChart.tsx**

```tsx
import ReactECharts from 'echarts-for-react';
import type { MetricsItem, ChartType } from '../types';

interface Props {
  chartType: ChartType;
  items: MetricsItem[];
  totalCalls: number;
}

export default function MetricsChart({ chartType, items, totalCalls }: Props) {
  const labels = items.map((i) => i.label);
  const values = items.map((i) => i.count);

  const getOption = () => {
    const base: Record<string, unknown> = {
      title: {
        text: `总调用次数: ${totalCalls}`,
        left: 'center',
        textStyle: { fontSize: 14 },
      },
      tooltip: { trigger: chartType === 'pie' ? 'item' : 'axis' },
      legend: {
        data: labels,
        bottom: 0,
      },
    };

    switch (chartType) {
      case 'bar':
        return {
          ...base,
          xAxis: { type: 'category', data: labels },
          yAxis: { type: 'value' },
          series: [{ type: 'bar', data: values, name: '调用次数' }],
        };
      case 'line':
        return {
          ...base,
          xAxis: { type: 'category', data: labels, boundaryGap: false },
          yAxis: { type: 'value' },
          series: [
            {
              type: 'line',
              data: values,
              name: '调用次数',
              smooth: true,
              areaStyle: { opacity: 0.15 },
            },
          ],
        };
      case 'pie':
        return {
          ...base,
          series: [
            {
              type: 'pie',
              radius: ['40%', '70%'],
              data: items.map((i) => ({ name: i.label, value: i.count })),
              label: { formatter: '{b}: {c} ({d}%)' },
            },
          ],
        };
      default:
        return base;
    }
  };

  return (
    <ReactECharts
      option={getOption()}
      style={{ height: 400, width: '100%' }}
      notMerge
    />
  );
}
```

- [ ] **Step 4: 创建 MetricsPanel.tsx**

```tsx
import { useState, useEffect, useCallback } from 'react';
import { Typography, Spin, Alert, Space } from 'antd';
import DimensionSelector from './DimensionSelector';
import ChartTypeSelector from './ChartTypeSelector';
import MetricsChart from './MetricsChart';
import { fetchMetrics } from '../api/client';
import type { Dimension, ChartType, MetricsResponse } from '../types';

const { Title } = Typography;

export default function MetricsPanel() {
  const [dimension, setDimension] = useState<Dimension>('personType');
  const [chartType, setChartType] = useState<ChartType>('bar');
  const [data, setData] = useState<MetricsResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const res = await fetchMetrics(dimension);
      if (res.code === 0) {
        setData(res.data);
      } else {
        setError(res.message);
      }
    } catch (e: any) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  }, [dimension]);

  useEffect(() => {
    load();
  }, [load]);

  return (
    <div className="metrics-panel">
      <Title level={3}>调用统计报表</Title>

      <Space style={{ marginBottom: 16 }}>
        <span>维度:</span>
        <DimensionSelector value={dimension} onChange={setDimension} />
        <span style={{ marginLeft: 16 }}>图表:</span>
        <ChartTypeSelector value={chartType} onChange={setChartType} />
      </Space>

      {loading && <Spin />}
      {error && <Alert type="error" message={error} />}
      {data && (
        <MetricsChart
          chartType={chartType}
          items={data.items}
          totalCalls={data.totalCalls}
        />
      )}
    </div>
  );
}
```

- [ ] **Step 5: TypeScript 编译检查**

Run: `cd /home/admin/.agentic-dev/runs/DEV-9d10e310-7901-11f1-8a9f-59ecae612580-773ed3b1-aebd-43a1-8efc-449f0978cd97/worktree/testDJnew-main && npx tsc --noEmit 2>&1`

Expected: No errors (or only pre-existing ones)

---

## 跨仓对齐点检查清单

| # | 对齐项 | 后端 (testDj) | 前端 (testDJnew) | 状态 |
|---|--------|---------------|-------------------|------|
| 1 | 统一响应 `{code, message, data}` | ApiResult 封装 | `ApiResult<T>` 泛型类型 | ✅ |
| 2 | GET `/api/helloworld` | `HelloWorldController` | `fetchHelloWorld()` | ✅ |
| 3 | POST `/api/hash` `{input, algorithm}` | `HashController` + `HashRequest` | `fetchHash(input, algorithm)` | ✅ |
| 4 | POST `/api/bubblesort` `{array, order}` | `BubbleSortController` + `BubbleSortRequest` | `fetchBubbleSort(array, order)` | ✅ |
| 5 | POST `/api/export` `{type, data}` → Blob | `ExportController` + POI | `exportExcel(type, data)` → Blob | ✅ |
| 6 | GET `/api/metrics?dimension=` | `MetricsController` + `MetricsService` | `fetchMetrics(dimension)` | ✅ |
| 7 | 埋点 `X-Caller-*` 请求头 | `MetricsInterceptor` 读取 | `callerHeaders()` 注入 | ✅ |
| 8 | 维度枚举 | `personType / level / department` | `Dimension` 类型 | ✅ |
| 9 | 图表类型 | N/A | `line / bar / pie` (ChartType) | ✅ |
| 10 | 冒泡排序最大长度 | `MAX_LENGTH = 100` | 前端无线制（后端兜底） | ✅ |

---

## 开发顺序

```
Phase 1 (Task 1-2):    testDj Maven 骨架 + ApiResult
Phase 2 (Task 3-5):    testDj 三个核心接口 (HelloWorld / Hash / BubbleSort)
Phase 3 (Task 6-7):    testDj 埋点拦截器 + 报表查询接口
Phase 4 (Task 8):      testDj Excel 导出接口
Phase 5 (Task 9):      testDJnew Vite + React 项目骨架
Phase 6 (Task 10):     testDJnew API 客户端
Phase 7 (Task 11):     testDJnew Dashboard + 三 Tab + 导出按钮
Phase 8 (Task 12):     testDJnew ECharts 可视化报表
Phase 9:               联调测试 (启动后端 → 启动前端 → 验证完整链路)
```

---

## 自审查

| 审查项 | 结果 |
|--------|------|
| 占位符扫描 | 无 TBD/TODO/待实现 |
| 类型一致性 | DTO 字段名与前端类型定义完全对齐 |
| 接口契约 | 所有 5 个接口的路径、方法、请求体、响应体均前后端一致 |
| 测试覆盖 | 后端 3 个 Controller 测试 + 2 个 Service 测试 |
| 埋点完整性 | 拦截器覆盖所有 `/api/**`，请求头提取 + 默认值兜底 |
| 导出格式 | Excel .xlsx 二进制流，Content-Disposition 含动态文件名 |
| 图表维度 | 3 维度 (personType/level/department) × 3 图表类型 (line/pie/bar) |

---

> **文档状态**: ✅ 已完成
> **下一步**: 进入 executing-plans 或 subagent-driven-development 阶段逐任务实施