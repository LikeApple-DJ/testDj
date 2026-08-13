# 多接口演示与分析系统 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 构建一个多接口演示 + 数据可视化分析系统，后端提供 HelloWorld、哈希算法、冒泡排序三个接口，前端以三 Tab 页面展示执行结果，支持导出和埋点统计报表。

**Architecture:** 后端采用 Java 17 + Spring Boot 3.x + H2 内嵌数据库 + Spring AOP 实现埋点；前端采用 React 18 + TypeScript + Ant Design 5 + ECharts 5 + Vite 5。前后端通过 RESTful JSON 接口通信，用户维度信息通过 HTTP Header 传递。

**Tech Stack:**
- 后端: Java 17, Spring Boot 3.x, Maven, H2, Spring Data JPA, Spring AOP
- 前端: React 18, TypeScript 5, Vite 5, Ant Design 5, ECharts 5 (echarts-for-react), Axios

**Global Constraints:**
- 后端仓库路径: `testDj-main/`
- 前端仓库路径: `testDJnew-main/`
- 所有请求通过 Header 传递用户信息: `X-User-Id`, `X-User-Type`, `X-User-Level`, `X-User-Dept`
- 导出格式: CSV
- 数据库: H2 内嵌模式（MVP 阶段）
- 后端配置 CORS 允许前端跨域访问

---

## File Structure

### testDj（后端）

| 文件路径 | 职责 |
|----------|------|
| `pom.xml` | Maven 构建配置，声明所有依赖 |
| `src/main/java/com/example/demo/DemoApplication.java` | Spring Boot 启动类 |
| `src/main/resources/application.yml` | 应用配置（端口、H2、JPA、CORS） |
| `src/main/resources/schema.sql` | 建表 SQL |
| `src/main/java/com/example/demo/dto/HelloRequest.java` | HelloWorld 请求 DTO |
| `src/main/java/com/example/demo/dto/HelloResponse.java` | HelloWorld 响应 DTO |
| `src/main/java/com/example/demo/dto/HashRequest.java` | 哈希请求 DTO |
| `src/main/java/com/example/demo/dto/HashResponse.java` | 哈希响应 DTO |
| `src/main/java/com/example/demo/dto/SortRequest.java` | 排序请求 DTO |
| `src/main/java/com/example/demo/dto/SortResponse.java` | 排序响应 DTO |
| `src/main/java/com/example/demo/dto/StatisticsResponse.java` | 统计响应 DTO |
| `src/main/java/com/example/demo/entity/ApiCallLog.java` | 调用记录 JPA Entity |
| `src/main/java/com/example/demo/repository/ApiCallLogRepository.java` | 调用记录 Repository |
| `src/main/java/com/example/demo/service/HelloService.java` | HelloWorld 业务逻辑 |
| `src/main/java/com/example/demo/service/HashService.java` | 哈希算法业务逻辑 |
| `src/main/java/com/example/demo/service/BubbleSortService.java` | 冒泡排序业务逻辑 |
| `src/main/java/com/example/demo/service/ExportService.java` | 导出业务逻辑 |
| `src/main/java/com/example/demo/service/StatisticsService.java` | 统计查询业务逻辑 |
| `src/main/java/com/example/demo/controller/HelloController.java` | HelloWorld REST 控制器 |
| `src/main/java/com/example/demo/controller/HashController.java` | 哈希 REST 控制器 |
| `src/main/java/com/example/demo/controller/BubbleSortController.java` | 排序 REST 控制器 |
| `src/main/java/com/example/demo/controller/ExportController.java` | 导出 REST 控制器 |
| `src/main/java/com/example/demo/controller/StatisticsController.java` | 统计 REST 控制器 |
| `src/main/java/com/example/demo/aspect/ApiCallLogAspect.java` | AOP 埋点切面 |
| `src/main/java/com/example/demo/config/CorsConfig.java` | CORS 跨域配置 |
| `src/test/java/com/example/demo/controller/HelloControllerTest.java` | HelloWorld 接口测试 |
| `src/test/java/com/example/demo/controller/HashControllerTest.java` | 哈希接口测试 |
| `src/test/java/com/example/demo/controller/BubbleSortControllerTest.java` | 排序接口测试 |
| `src/test/java/com/example/demo/service/BubbleSortServiceTest.java` | 冒泡排序算法测试 |

### testDJnew（前端）

| 文件路径 | 职责 |
|----------|------|
| `package.json` | 依赖声明 |
| `vite.config.ts` | Vite 构建配置 + API 代理 |
| `tsconfig.json` | TypeScript 配置 |
| `tsconfig.node.json` | Node 端 TypeScript 配置 |
| `index.html` | HTML 入口 |
| `src/main.tsx` | React 入口 |
| `src/App.tsx` | 路由配置 |
| `src/types/index.ts` | TypeScript 类型定义 |
| `src/services/api.ts` | 统一 API 调用层 |
| `src/pages/DemoPage.tsx` | 主页面（三 Tab） |
| `src/pages/ReportPage.tsx` | 报表页面 |
| `src/components/tabs/HelloTab.tsx` | HelloWorld Tab 组件 |
| `src/components/tabs/HashTab.tsx` | 哈希 Tab 组件 |
| `src/components/tabs/BubbleSortTab.tsx` | 冒泡排序 Tab 组件 |
| `src/components/ExportButton.tsx` | 导出按钮组件 |
| `src/components/charts/LineChart.tsx` | 折线图组件 |
| `src/components/charts/PieChart.tsx` | 饼图组件 |
| `src/components/charts/BarChart.tsx` | 柱状图组件 |

---

## Task 1: 初始化 Spring Boot 后端项目

**Files:**
- Create: `testDj-main/pom.xml`
- Create: `testDj-main/src/main/java/com/example/demo/DemoApplication.java`
- Create: `testDj-main/src/main/resources/application.yml`
- Create: `testDj-main/src/main/resources/schema.sql`
- Create: `testDj-main/src/main/java/com/example/demo/config/CorsConfig.java`

**Interfaces:**
- Produces: 可启动的 Spring Boot 空项目，监听 8080 端口，H2 控制台可访问

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
    <description>Multi-API Demo with Analytics</description>

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
            <artifactId>spring-boot-starter-aop</artifactId>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
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

- [ ] **Step 2: 创建启动类 DemoApplication.java**

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
    url: jdbc:h2:mem:demodb
    driver-class-name: org.h2.Driver
    username: sa
    password:
  h2:
    console:
      enabled: true
      path: /h2-console
  jpa:
    hibernate:
      ddl-auto: none
    show-sql: false
  sql:
    init:
      mode: always
      schema-locations: classpath:schema.sql
```

- [ ] **Step 4: 创建 schema.sql**

```sql
CREATE TABLE IF NOT EXISTS api_call_log (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    api_name      VARCHAR(50)   NOT NULL,
    user_id       VARCHAR(100)  NOT NULL,
    user_type     VARCHAR(50),
    user_level    VARCHAR(50),
    user_dept     VARCHAR(100),
    call_time     TIMESTAMP     NOT NULL,
    request_body  TEXT,
    response_body TEXT
);
```

- [ ] **Step 5: 创建 CorsConfig.java**

```java
package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
```

- [ ] **Step 6: 验证项目可启动**

Run: `cd testDj-main && mvn spring-boot:run`
Expected: 应用启动成功，监听 8080 端口，无报错

- [ ] **Step 7: Commit**

```bash
git add pom.xml src/
git commit -m "feat: initialize Spring Boot project with H2 and CORS config"
```

---

## Task 2: 实现 HelloWorld 接口

**Files:**
- Create: `testDj-main/src/main/java/com/example/demo/dto/HelloRequest.java`
- Create: `testDj-main/src/main/java/com/example/demo/dto/HelloResponse.java`
- Create: `testDj-main/src/main/java/com/example/demo/service/HelloService.java`
- Create: `testDj-main/src/main/java/com/example/demo/controller/HelloController.java`
- Create: `testDj-main/src/test/java/com/example/demo/controller/HelloControllerTest.java`

**Interfaces:**
- Consumes: Spring Boot 项目基础结构（Task 1）
- Produces: `POST /api/demo/hello` → `{ "message": "Hello, {name}!", "timestamp": "..." }`

- [ ] **Step 1: 创建 HelloRequest.java**

```java
package com.example.demo.dto;

public class HelloRequest {
    private String name;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
```

- [ ] **Step 2: 创建 HelloResponse.java**

```java
package com.example.demo.dto;

import java.time.LocalDateTime;

public class HelloResponse {
    private String message;
    private String timestamp;

    public HelloResponse(String message) {
        this.message = message;
        this.timestamp = LocalDateTime.now().toString();
    }

    public String getMessage() { return message; }
    public String getTimestamp() { return timestamp; }
}
```

- [ ] **Step 3: 创建 HelloService.java**

```java
package com.example.demo.service;

import com.example.demo.dto.HelloResponse;
import org.springframework.stereotype.Service;

@Service
public class HelloService {
    public HelloResponse sayHello(String name) {
        String greeting = "Hello, " + name + "!";
        return new HelloResponse(greeting);
    }
}
```

- [ ] **Step 4: 创建 HelloController.java**

```java
package com.example.demo.controller;

import com.example.demo.dto.HelloRequest;
import com.example.demo.dto.HelloResponse;
import com.example.demo.service.HelloService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/demo")
public class HelloController {

    private final HelloService helloService;

    public HelloController(HelloService helloService) {
        this.helloService = helloService;
    }

    @PostMapping("/hello")
    public HelloResponse hello(@RequestBody HelloRequest request) {
        return helloService.sayHello(request.getName());
    }
}
```

- [ ] **Step 5: 创建测试 HelloControllerTest.java**

```java
package com.example.demo.controller;

import com.example.demo.service.HelloService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HelloController.class)
class HelloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HelloService helloService;

    @Test
    void testHello() throws Exception {
        when(helloService.sayHello("World"))
            .thenReturn(new com.example.demo.dto.HelloResponse("Hello, World!"));

        mockMvc.perform(post("/api/demo/hello")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"World\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Hello, World!"))
            .andExpect(jsonPath("$.timestamp").exists());
    }
}
```

- [ ] **Step 6: 运行测试验证**

Run: `cd testDj-main && mvn test -Dtest=HelloControllerTest`
Expected: PASS

- [ ] **Step 7: Commit**

```bash
git add src/
git commit -m "feat: implement HelloWorld API endpoint"
```

---

## Task 3: 实现哈希算法接口

**Files:**
- Create: `testDj-main/src/main/java/com/example/demo/dto/HashRequest.java`
- Create: `testDj-main/src/main/java/com/example/demo/dto/HashResponse.java`
- Create: `testDj-main/src/main/java/com/example/demo/service/HashService.java`
- Create: `testDj-main/src/main/java/com/example/demo/controller/HashController.java`
- Create: `testDj-main/src/test/java/com/example/demo/controller/HashControllerTest.java`

**Interfaces:**
- Consumes: Spring Boot 项目基础结构（Task 1）
- Produces: `POST /api/demo/hash` → `{ "input": "...", "algorithm": "SHA-256", "hash": "..." }`

- [ ] **Step 1: 创建 HashRequest.java**

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

- [ ] **Step 2: 创建 HashResponse.java**

```java
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

- [ ] **Step 3: 创建 HashService.java**

```java
package com.example.demo.service;

import com.example.demo.dto.HashResponse;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class HashService {

    public HashResponse computeHash(String input, String algorithm) {
        try {
            String normalizedAlgorithm = normalizeAlgorithm(algorithm);
            MessageDigest digest = MessageDigest.getInstance(normalizedAlgorithm);
            byte[] hashBytes = digest.digest(input.getBytes());
            String hash = bytesToHex(hashBytes);
            return new HashResponse(input, algorithm, hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("Unsupported algorithm: " + algorithm);
        }
    }

    private String normalizeAlgorithm(String algorithm) {
        return switch (algorithm.toUpperCase()) {
            case "MD5" -> "MD5";
            case "SHA-1" -> "SHA-1";
            case "SHA-256" -> "SHA-256";
            default -> algorithm;
        };
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
```

- [ ] **Step 4: 创建 HashController.java**

```java
package com.example.demo.controller;

import com.example.demo.dto.HashRequest;
import com.example.demo.dto.HashResponse;
import com.example.demo.service.HashService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/demo")
public class HashController {

    private final HashService hashService;

    public HashController(HashService hashService) {
        this.hashService = hashService;
    }

    @PostMapping("/hash")
    public HashResponse hash(@RequestBody HashRequest request) {
        return hashService.computeHash(request.getInput(), request.getAlgorithm());
    }
}
```

- [ ] **Step 5: 创建测试 HashControllerTest.java**

```java
package com.example.demo.controller;

import com.example.demo.dto.HashResponse;
import com.example.demo.service.HashService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HashController.class)
class HashControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HashService hashService;

    @Test
    void testHash() throws Exception {
        when(hashService.computeHash("hello", "SHA-256"))
            .thenReturn(new HashResponse("hello", "SHA-256",
                "2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824"));

        mockMvc.perform(post("/api/demo/hash")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"input\": \"hello\", \"algorithm\": \"SHA-256\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.input").value("hello"))
            .andExpect(jsonPath("$.algorithm").value("SHA-256"))
            .andExpect(jsonPath("$.hash").value("2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824"));
    }
}
```

- [ ] **Step 6: 运行测试验证**

Run: `cd testDj-main && mvn test -Dtest=HashControllerTest`
Expected: PASS

- [ ] **Step 7: Commit**

```bash
git add src/
git commit -m "feat: implement hash algorithm API endpoint"
```

---

## Task 4: 实现冒泡排序接口

**Files:**
- Create: `testDj-main/src/main/java/com/example/demo/dto/SortRequest.java`
- Create: `testDj-main/src/main/java/com/example/demo/dto/SortResponse.java`
- Create: `testDj-main/src/main/java/com/example/demo/service/BubbleSortService.java`
- Create: `testDj-main/src/main/java/com/example/demo/controller/BubbleSortController.java`
- Create: `testDj-main/src/test/java/com/example/demo/service/BubbleSortServiceTest.java`
- Create: `testDj-main/src/test/java/com/example/demo/controller/BubbleSortControllerTest.java`

**Interfaces:**
- Consumes: Spring Boot 项目基础结构（Task 1）
- Produces: `POST /api/demo/bubble-sort` → `{ "original": [...], "sorted": [...], "steps": N }`

- [ ] **Step 1: 创建 SortRequest.java**

```java
package com.example.demo.dto;

import java.util.List;

public class SortRequest {
    private List<Integer> array;

    public List<Integer> getArray() { return array; }
    public void setArray(List<Integer> array) { this.array = array; }
}
```

- [ ] **Step 2: 创建 SortResponse.java**

```java
package com.example.demo.dto;

import java.util.List;

public class SortResponse {
    private List<Integer> original;
    private List<Integer> sorted;
    private int steps;

    public SortResponse(List<Integer> original, List<Integer> sorted, int steps) {
        this.original = original;
        this.sorted = sorted;
        this.steps = steps;
    }

    public List<Integer> getOriginal() { return original; }
    public List<Integer> getSorted() { return sorted; }
    public int getSteps() { return steps; }
}
```

- [ ] **Step 3: 创建 BubbleSortService.java**

```java
package com.example.demo.service;

import com.example.demo.dto.SortResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BubbleSortService {

    public SortResponse bubbleSort(List<Integer> input) {
        List<Integer> arr = new ArrayList<>(input);
        int n = arr.size();
        int steps = 0;

        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - 1 - i; j++) {
                steps++;
                if (arr.get(j) > arr.get(j + 1)) {
                    int temp = arr.get(j);
                    arr.set(j, arr.get(j + 1));
                    arr.set(j + 1, temp);
                    swapped = true;
                }
            }
            if (!swapped) break;
        }

        return new SortResponse(input, arr, steps);
    }
}
```

- [ ] **Step 4: 创建 BubbleSortController.java**

```java
package com.example.demo.controller;

import com.example.demo.dto.SortRequest;
import com.example.demo.dto.SortResponse;
import com.example.demo.service.BubbleSortService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/demo")
public class BubbleSortController {

    private final BubbleSortService bubbleSortService;

    public BubbleSortController(BubbleSortService bubbleSortService) {
        this.bubbleSortService = bubbleSortService;
    }

    @PostMapping("/bubble-sort")
    public SortResponse sort(@RequestBody SortRequest request) {
        return bubbleSortService.bubbleSort(request.getArray());
    }
}
```

- [ ] **Step 5: 创建 BubbleSortServiceTest.java**

```java
package com.example.demo.service;

import com.example.demo.dto.SortResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BubbleSortServiceTest {

    private final BubbleSortService service = new BubbleSortService();

    @Test
    void testBubbleSort() {
        SortResponse result = service.bubbleSort(List.of(5, 3, 8, 1, 9));
        assertEquals(List.of(5, 3, 8, 1, 9), result.getOriginal());
        assertEquals(List.of(1, 3, 5, 8, 9), result.getSorted());
        assertTrue(result.getSteps() > 0);
    }

    @Test
    void testAlreadySorted() {
        SortResponse result = service.bubbleSort(List.of(1, 2, 3));
        assertEquals(List.of(1, 2, 3), result.getSorted());
    }

    @Test
    void testEmptyArray() {
        SortResponse result = service.bubbleSort(List.of());
        assertEquals(List.of(), result.getSorted());
        assertEquals(0, result.getSteps());
    }
}
```

- [ ] **Step 6: 创建 BubbleSortControllerTest.java**

```java
package com.example.demo.controller;

import com.example.demo.dto.SortResponse;
import com.example.demo.service.BubbleSortService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BubbleSortController.class)
class BubbleSortControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BubbleSortService bubbleSortService;

    @Test
    void testSort() throws Exception {
        when(bubbleSortService.bubbleSort(List.of(5, 3, 8, 1, 9)))
            .thenReturn(new SortResponse(List.of(5, 3, 8, 1, 9), List.of(1, 3, 5, 8, 9), 6));

        mockMvc.perform(post("/api/demo/bubble-sort")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"array\": [5, 3, 8, 1, 9]}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.original").isArray())
            .andExpect(jsonPath("$.sorted").value(List.of(1, 3, 5, 8, 9)))
            .andExpect(jsonPath("$.steps").value(6));
    }
}
```

- [ ] **Step 7: 运行测试验证**

Run: `cd testDj-main && mvn test -Dtest=BubbleSortServiceTest,BubbleSortControllerTest`
Expected: PASS

- [ ] **Step 8: Commit**

```bash
git add src/
git commit -m "feat: implement bubble sort API endpoint"
```

---

## Task 5: 实现数据模型与 AOP 埋点

**Files:**
- Create: `testDj-main/src/main/java/com/example/demo/entity/ApiCallLog.java`
- Create: `testDj-main/src/main/java/com/example/demo/repository/ApiCallLogRepository.java`
- Create: `testDj-main/src/main/java/com/example/demo/aspect/ApiCallLogAspect.java`

**Interfaces:**
- Consumes: schema.sql 中的 `api_call_log` 表（Task 1）
- Produces: `ApiCallLog` Entity + `ApiCallLogRepository` + AOP 切面自动记录三个业务接口的调用

- [ ] **Step 1: 创建 ApiCallLog.java**

```java
package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "api_call_log")
public class ApiCallLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "api_name", nullable = false, length = 50)
    private String apiName;

    @Column(name = "user_id", nullable = false, length = 100)
    private String userId;

    @Column(name = "user_type", length = 50)
    private String userType;

    @Column(name = "user_level", length = 50)
    private String userLevel;

    @Column(name = "user_dept", length = 100)
    private String userDept;

    @Column(name = "call_time", nullable = false)
    private LocalDateTime callTime;

    @Column(name = "request_body", columnDefinition = "TEXT")
    private String requestBody;

    @Column(name = "response_body", columnDefinition = "TEXT")
    private String responseBody;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getApiName() { return apiName; }
    public void setApiName(String apiName) { this.apiName = apiName; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }
    public String getUserLevel() { return userLevel; }
    public void setUserLevel(String userLevel) { this.userLevel = userLevel; }
    public String getUserDept() { return userDept; }
    public void setUserDept(String userDept) { this.userDept = userDept; }
    public LocalDateTime getCallTime() { return callTime; }
    public void setCallTime(LocalDateTime callTime) { this.callTime = callTime; }
    public String getRequestBody() { return requestBody; }
    public void setRequestBody(String requestBody) { this.requestBody = requestBody; }
    public String getResponseBody() { return responseBody; }
    public void setResponseBody(String responseBody) { this.responseBody = responseBody; }
}
```

- [ ] **Step 2: 创建 ApiCallLogRepository.java**

```java
package com.example.demo.repository;

import com.example.demo.entity.ApiCallLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ApiCallLogRepository extends JpaRepository<ApiCallLog, Long> {

    List<ApiCallLog> findByApiName(String apiName);

    @Query("SELECT a.userType, COUNT(a) FROM ApiCallLog a WHERE a.callTime >= :since GROUP BY a.userType")
    List<Object[]> countByUserTypeSince(@Param("since") LocalDateTime since);

    @Query("SELECT a.userLevel, COUNT(a) FROM ApiCallLog a WHERE a.callTime >= :since GROUP BY a.userLevel")
    List<Object[]> countByUserLevelSince(@Param("since") LocalDateTime since);

    @Query("SELECT a.userDept, COUNT(a) FROM ApiCallLog a WHERE a.callTime >= :since GROUP BY a.userDept")
    List<Object[]> countByUserDeptSince(@Param("since") LocalDateTime since);

    @Query("SELECT a FROM ApiCallLog a WHERE a.apiName = :apiName ORDER BY a.callTime DESC")
    List<ApiCallLog> findByApiNameOrderByCallTimeDesc(@Param("apiName") String apiName);
}
```

- [ ] **Step 3: 创建 ApiCallLogAspect.java**

```java
package com.example.demo.aspect;

import com.example.demo.entity.ApiCallLog;
import com.example.demo.repository.ApiCallLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Aspect
@Component
public class ApiCallLogAspect {

    private final ApiCallLogRepository repository;
    private final ObjectMapper objectMapper;

    public ApiCallLogAspect(ApiCallLogRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Around("execution(* com.example.demo.controller.HelloController.hello(..)) || " +
            "execution(* com.example.demo.controller.HashController.hash(..)) || " +
            "execution(* com.example.demo.controller.BubbleSortController.sort(..))")
    public Object logApiCall(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();

        try {
            ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attrs.getRequest();

            ApiCallLog log = new ApiCallLog();
            log.setApiName(resolveApiName(joinPoint));
            log.setUserId(getHeader(request, "X-User-Id", "anonymous"));
            log.setUserType(getHeader(request, "X-User-Type", null));
            log.setUserLevel(getHeader(request, "X-User-Level", null));
            log.setUserDept(getHeader(request, "X-User-Dept", null));
            log.setCallTime(LocalDateTime.now());
            log.setRequestBody(objectMapper.writeValueAsString(joinPoint.getArgs()[0]));
            log.setResponseBody(objectMapper.writeValueAsString(result));

            repository.save(log);
        } catch (Exception e) {
            // 埋点失败不影响业务
        }

        return result;
    }

    private String resolveApiName(ProceedingJoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        return switch (methodName) {
            case "hello" -> "hello";
            case "hash" -> "hash";
            case "sort" -> "bubble-sort";
            default -> methodName;
        };
    }

    private String getHeader(HttpServletRequest request, String name, String defaultValue) {
        String value = request.getHeader(name);
        return (value != null && !value.isBlank()) ? value : defaultValue;
    }
}
```

- [ ] **Step 4: 运行全部测试验证**

Run: `cd testDj-main && mvn test`
Expected: 所有测试 PASS

- [ ] **Step 5: Commit**

```bash
git add src/
git commit -m "feat: add api_call_log entity, repository, and AOP tracking aspect"
```

---

## Task 6: 实现统计查询接口

**Files:**
- Create: `testDj-main/src/main/java/com/example/demo/dto/StatisticsResponse.java`
- Create: `testDj-main/src/main/java/com/example/demo/service/StatisticsService.java`
- Create: `testDj-main/src/main/java/com/example/demo/controller/StatisticsController.java`

**Interfaces:**
- Consumes: `ApiCallLogRepository`（Task 5）
- Produces: `GET /api/demo/statistics?dimension=userType|userLevel|userDept&period=7d|30d|all` → 分组统计数据

- [ ] **Step 1: 创建 StatisticsResponse.java**

```java
package com.example.demo.dto;

import java.util.List;

public class StatisticsResponse {
    private String dimension;
    private List<DimensionItem> data;
    private long total;

    public StatisticsResponse(String dimension, List<DimensionItem> data, long total) {
        this.dimension = dimension;
        this.data = data;
        this.total = total;
    }

    public String getDimension() { return dimension; }
    public List<DimensionItem> getData() { return data; }
    public long getTotal() { return total; }

    public static class DimensionItem {
        private String label;
        private long count;

        public DimensionItem(String label, long count) {
            this.label = label;
            this.count = count;
        }

        public String getLabel() { return label; }
        public long getCount() { return count; }
    }
}
```

- [ ] **Step 2: 创建 StatisticsService.java**

```java
package com.example.demo.service;

import com.example.demo.dto.StatisticsResponse;
import com.example.demo.dto.StatisticsResponse.DimensionItem;
import com.example.demo.repository.ApiCallLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

    private final ApiCallLogRepository repository;

    public StatisticsService(ApiCallLogRepository repository) {
        this.repository = repository;
    }

    public StatisticsResponse getStatistics(String dimension, String period) {
        LocalDateTime since = resolvePeriod(period);
        List<Object[]> rows = switch (dimension) {
            case "userType" -> repository.countByUserTypeSince(since);
            case "userLevel" -> repository.countByUserLevelSince(since);
            case "userDept" -> repository.countByUserDeptSince(since);
            default -> throw new IllegalArgumentException("Unknown dimension: " + dimension);
        };

        List<DimensionItem> items = rows.stream()
            .map(row -> new DimensionItem(
                row[0] != null ? row[0].toString() : "unknown",
                (Long) row[1]))
            .collect(Collectors.toList());

        long total = items.stream().mapToLong(DimensionItem::getCount).sum();
        return new StatisticsResponse(dimension, items, total);
    }

    private LocalDateTime resolvePeriod(String period) {
        return switch (period) {
            case "7d" -> LocalDateTime.now().minusDays(7);
            case "30d" -> LocalDateTime.now().minusDays(30);
            default -> LocalDateTime.of(2000, 1, 1, 0, 0);
        };
    }
}
```

- [ ] **Step 3: 创建 StatisticsController.java**

```java
package com.example.demo.controller;

import com.example.demo.dto.StatisticsResponse;
import com.example.demo.service.StatisticsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/demo")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/statistics")
    public StatisticsResponse getStatistics(
            @RequestParam(defaultValue = "userDept") String dimension,
            @RequestParam(defaultValue = "all") String period) {
        return statisticsService.getStatistics(dimension, period);
    }
}
```

- [ ] **Step 4: 运行测试验证**

Run: `cd testDj-main && mvn test`
Expected: 所有测试 PASS

- [ ] **Step 5: Commit**

```bash
git add src/
git commit -m "feat: implement statistics query API with dimension and period support"
```

---

## Task 7: 实现 CSV 导出接口

**Files:**
- Create: `testDj-main/src/main/java/com/example/demo/service/ExportService.java`
- Create: `testDj-main/src/main/java/com/example/demo/controller/ExportController.java`

**Interfaces:**
- Consumes: `ApiCallLogRepository`（Task 5）
- Produces: `GET /api/demo/export?type=hello|hash|bubble-sort&format=csv` → CSV 文件下载

- [ ] **Step 1: 创建 ExportService.java**

```java
package com.example.demo.service;

import com.example.demo.entity.ApiCallLog;
import com.example.demo.repository.ApiCallLogRepository;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.util.List;

@Service
public class ExportService {

    private final ApiCallLogRepository repository;

    public ExportService(ApiCallLogRepository repository) {
        this.repository = repository;
    }

    public String exportToCsv(String type) {
        List<ApiCallLog> logs = repository.findByApiNameOrderByCallTimeDesc(type);

        StringWriter writer = new StringWriter();
        // CSV header
        writer.write("ID,API Name,User ID,User Type,User Level,User Dept,Call Time,Request Body,Response Body\n");
        // CSV rows
        for (ApiCallLog log : logs) {
            writer.write(String.format("%d,%s,%s,%s,%s,%s,%s,%s,%s\n",
                escapeCsv(log.getId()),
                escapeCsv(log.getApiName()),
                escapeCsv(log.getUserId()),
                escapeCsv(log.getUserType()),
                escapeCsv(log.getUserLevel()),
                escapeCsv(log.getUserDept()),
                escapeCsv(log.getCallTime() != null ? log.getCallTime().toString() : ""),
                escapeCsv(log.getRequestBody()),
                escapeCsv(log.getResponseBody())
            ));
        }
        return writer.toString();
    }

    private String escapeCsv(Object value) {
        if (value == null) return "";
        String str = value.toString();
        if (str.contains(",") || str.contains("\"") || str.contains("\n")) {
            return "\"" + str.replace("\"", "\"\"") + "\"";
        }
        return str;
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
@RequestMapping("/api/demo")
public class ExportController {

    private final ExportService exportService;

    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> export(
            @RequestParam(defaultValue = "hello") String type,
            @RequestParam(defaultValue = "csv") String format) {

        String csvContent = exportService.exportToCsv(type);
        String filename = type + "-export.csv";

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
            .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
            .body(csvContent.getBytes());
    }
}
```

- [ ] **Step 3: 运行全部测试验证**

Run: `cd testDj-main && mvn test`
Expected: 所有测试 PASS

- [ ] **Step 4: Commit**

```bash
git add src/
git commit -m "feat: implement CSV export API for call log data"
```

---

## Task 8: 初始化 React 前端项目

**Files:**
- Create: `testDJnew-main/package.json`
- Create: `testDJnew-main/vite.config.ts`
- Create: `testDJnew-main/tsconfig.json`
- Create: `testDJnew-main/tsconfig.node.json`
- Create: `testDJnew-main/index.html`
- Create: `testDJnew-main/src/main.tsx`
- Create: `testDJnew-main/src/App.tsx`
- Create: `testDJnew-main/src/types/index.ts`
- Create: `testDJnew-main/src/services/api.ts`

**Interfaces:**
- Produces: 可运行的 React + Vite + TypeScript 空项目，含 API 服务层和类型定义

- [ ] **Step 1: 创建 package.json**

```json
{
  "name": "test-dj-new",
  "private": true,
  "version": "0.1.0",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "tsc && vite build",
    "preview": "vite preview"
  },
  "dependencies": {
    "react": "^18.2.0",
    "react-dom": "^18.2.0",
    "react-router-dom": "^6.22.0",
    "antd": "^5.15.0",
    "echarts": "^5.5.0",
    "echarts-for-react": "^3.0.2",
    "axios": "^1.6.7"
  },
  "devDependencies": {
    "@types/react": "^18.2.55",
    "@types/react-dom": "^18.2.19",
    "@vitejs/plugin-react": "^4.2.1",
    "typescript": "^5.3.3",
    "vite": "^5.1.0"
  }
}
```

- [ ] **Step 2: 创建 vite.config.ts**

```typescript
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
});
```

- [ ] **Step 3: 创建 tsconfig.json**

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
    "noUnusedLocals": true,
    "noUnusedParameters": true,
    "noFallthroughCasesInSwitch": true
  },
  "include": ["src"],
  "references": [{ "path": "./tsconfig.node.json" }]
}
```

- [ ] **Step 4: 创建 tsconfig.node.json**

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

- [ ] **Step 5: 创建 index.html**

```html
<!DOCTYPE html>
<html lang="zh-CN">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>多接口演示系统</title>
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
import { BrowserRouter } from 'react-router-dom';
import App from './App';

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <BrowserRouter>
      <App />
    </BrowserRouter>
  </React.StrictMode>
);
```

- [ ] **Step 7: 创建 src/App.tsx**

```tsx
import { Routes, Route } from 'react-router-dom';
import DemoPage from './pages/DemoPage';
import ReportPage from './pages/ReportPage';

function App() {
  return (
    <Routes>
      <Route path="/" element={<DemoPage />} />
      <Route path="/report" element={<ReportPage />} />
    </Routes>
  );
}

export default App;
```

- [ ] **Step 8: 创建 src/types/index.ts**

```typescript
export interface HelloRequest {
  name: string;
}

export interface HelloResponse {
  message: string;
  timestamp: string;
}

export interface HashRequest {
  input: string;
  algorithm: 'MD5' | 'SHA-1' | 'SHA-256';
}

export interface HashResponse {
  input: string;
  algorithm: string;
  hash: string;
}

export interface SortRequest {
  array: number[];
}

export interface SortResponse {
  original: number[];
  sorted: number[];
  steps: number;
}

export interface StatisticsResponse {
  dimension: string;
  data: DimensionItem[];
  total: number;
}

export interface DimensionItem {
  label: string;
  count: number;
}

export interface UserHeaders {
  'X-User-Id': string;
  'X-User-Type': string;
  'X-User-Level': string;
  'X-User-Dept': string;
}
```

- [ ] **Step 9: 创建 src/services/api.ts**

```typescript
import axios from 'axios';
import type {
  HelloRequest, HelloResponse,
  HashRequest, HashResponse,
  SortRequest, SortResponse,
  StatisticsResponse,
} from '../types';

const api = axios.create({
  baseURL: '/api/demo',
  headers: {
    'X-User-Id': 'user001',
    'X-User-Type': '正式',
    'X-User-Level': 'P6',
    'X-User-Dept': '技术部',
  },
});

export const callHello = (data: HelloRequest) =>
  api.post<HelloResponse>('/hello', data).then((res) => res.data);

export const callHash = (data: HashRequest) =>
  api.post<HashResponse>('/hash', data).then((res) => res.data);

export const callBubbleSort = (data: SortRequest) =>
  api.post<SortResponse>('/bubble-sort', data).then((res) => res.data);

export const exportData = (type: string) =>
  api.get('/export', {
    params: { type, format: 'csv' },
    responseType: 'blob',
  }).then((res) => {
    const url = window.URL.createObjectURL(new Blob([res.data]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', `${type}-export.csv`);
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
  });

export const getStatistics = (dimension: string, period: string = 'all') =>
  api.get<StatisticsResponse>('/statistics', { params: { dimension, period } })
    .then((res) => res.data);
```

- [ ] **Step 10: 安装依赖并验证项目可启动**

Run: `cd testDJnew-main && npm install && npm run dev`
Expected: Vite 开发服务器启动在 3000 端口

- [ ] **Step 11: Commit**

```bash
git add .
git commit -m "feat: initialize React + Vite + TypeScript project with API layer"
```

---

## Task 9: 实现三 Tab 主页面与 Tab 组件

**Files:**
- Create: `testDJnew-main/src/pages/DemoPage.tsx`
- Create: `testDJnew-main/src/components/tabs/HelloTab.tsx`
- Create: `testDJnew-main/src/components/tabs/HashTab.tsx`
- Create: `testDJnew-main/src/components/tabs/BubbleSortTab.tsx`
- Create: `testDJnew-main/src/components/ExportButton.tsx`

**Interfaces:**
- Consumes: `api.ts` 服务层（Task 8）、类型定义（Task 8）
- Produces: 主页面含三 Tab，每个 Tab 有输入表单 + 执行按钮 + 结果展示 + 历史记录表格

- [ ] **Step 1: 创建 ExportButton.tsx**

```tsx
import { Button, Dropdown } from 'antd';
import { DownloadOutlined } from '@ant-design/icons';
import { exportData } from '../services/api';

const menuItems = [
  { key: 'hello', label: '导出 HelloWorld 结果' },
  { key: 'hash', label: '导出哈希算法结果' },
  { key: 'bubble-sort', label: '导出冒泡排序结果' },
];

export default function ExportButton() {
  const handleExport = async ({ key }: { key: string }) => {
    await exportData(key);
  };

  return (
    <Dropdown menu={{ items: menuItems, onClick: handleExport }}>
      <Button icon={<DownloadOutlined />}>导出 ▼</Button>
    </Dropdown>
  );
}
```

- [ ] **Step 2: 创建 HelloTab.tsx**

```tsx
import { useState } from 'react';
import { Input, Button, Card, Table, Space, Typography } from 'antd';
import { callHello } from '../../services/api';
import type { HelloResponse } from '../../types';

const { Title } = Typography;

interface HistoryRecord {
  key: number;
  input: string;
  output: string;
  time: string;
}

export default function HelloTab() {
  const [name, setName] = useState('');
  const [result, setResult] = useState<HelloResponse | null>(null);
  const [history, setHistory] = useState<HistoryRecord[]>([]);
  const [loading, setLoading] = useState(false);

  const handleExecute = async () => {
    if (!name.trim()) return;
    setLoading(true);
    try {
      const res = await callHello({ name });
      setResult(res);
      setHistory((prev) => [
        { key: Date.now(), input: name, output: res.message, time: res.timestamp },
        ...prev,
      ]);
    } finally {
      setLoading(false);
    }
  };

  const columns = [
    { title: '时间', dataIndex: 'time', key: 'time' },
    { title: '输入', dataIndex: 'input', key: 'input' },
    { title: '输出', dataIndex: 'output', key: 'output' },
  ];

  return (
    <Space direction="vertical" size="large" style={{ width: '100%' }}>
      <Card title="输入">
        <Space>
          <Input
            placeholder="请输入名字"
            value={name}
            onChange={(e) => setName(e.target.value)}
            style={{ width: 300 }}
          />
          <Button type="primary" onClick={handleExecute} loading={loading}>
            执行
          </Button>
        </Space>
      </Card>

      {result && (
        <Card title="执行结果">
          <Title level={4}>{result.message}</Title>
          <p>时间戳: {result.timestamp}</p>
        </Card>
      )}

      {history.length > 0 && (
        <Card title="历史记录">
          <Table columns={columns} dataSource={history} pagination={{ pageSize: 5 }} />
        </Card>
      )}
    </Space>
  );
}
```

- [ ] **Step 3: 创建 HashTab.tsx**

```tsx
import { useState } from 'react';
import { Input, Select, Button, Card, Table, Space, Typography } from 'antd';
import { callHash } from '../../services/api';
import type { HashResponse } from '../../types';

const { Title } = Typography;

interface HistoryRecord {
  key: number;
  input: string;
  algorithm: string;
  hash: string;
  time: string;
}

export default function HashTab() {
  const [input, setInput] = useState('');
  const [algorithm, setAlgorithm] = useState<'MD5' | 'SHA-1' | 'SHA-256'>('SHA-256');
  const [result, setResult] = useState<HashResponse | null>(null);
  const [history, setHistory] = useState<HistoryRecord[]>([]);
  const [loading, setLoading] = useState(false);

  const handleExecute = async () => {
    if (!input.trim()) return;
    setLoading(true);
    try {
      const res = await callHash({ input, algorithm });
      setResult(res);
      setHistory((prev) => [
        { key: Date.now(), input, algorithm, hash: res.hash, time: new Date().toISOString() },
        ...prev,
      ]);
    } finally {
      setLoading(false);
    }
  };

  const columns = [
    { title: '时间', dataIndex: 'time', key: 'time' },
    { title: '输入', dataIndex: 'input', key: 'input' },
    { title: '算法', dataIndex: 'algorithm', key: 'algorithm' },
    { title: '哈希值', dataIndex: 'hash', key: 'hash', ellipsis: true },
  ];

  return (
    <Space direction="vertical" size="large" style={{ width: '100%' }}>
      <Card title="输入">
        <Space>
          <Input
            placeholder="请输入待哈希文本"
            value={input}
            onChange={(e) => setInput(e.target.value)}
            style={{ width: 300 }}
          />
          <Select value={algorithm} onChange={setAlgorithm} style={{ width: 120 }}>
            <Select.Option value="MD5">MD5</Select.Option>
            <Select.Option value="SHA-1">SHA-1</Select.Option>
            <Select.Option value="SHA-256">SHA-256</Select.Option>
          </Select>
          <Button type="primary" onClick={handleExecute} loading={loading}>
            执行
          </Button>
        </Space>
      </Card>

      {result && (
        <Card title="执行结果">
          <p>输入: {result.input}</p>
          <p>算法: {result.algorithm}</p>
          <Title level={5} copyable>{result.hash}</Title>
        </Card>
      )}

      {history.length > 0 && (
        <Card title="历史记录">
          <Table columns={columns} dataSource={history} pagination={{ pageSize: 5 }} />
        </Card>
      )}
    </Space>
  );
}
```

- [ ] **Step 4: 创建 BubbleSortTab.tsx**

```tsx
import { useState } from 'react';
import { Input, Button, Card, Table, Space, Typography, Tag } from 'antd';
import { callBubbleSort } from '../../services/api';
import type { SortResponse } from '../../types';

const { Title } = Typography;

interface HistoryRecord {
  key: number;
  input: string;
  sorted: string;
  steps: number;
  time: string;
}

export default function BubbleSortTab() {
  const [arrayInput, setArrayInput] = useState('');
  const [result, setResult] = useState<SortResponse | null>(null);
  const [history, setHistory] = useState<HistoryRecord[]>([]);
  const [loading, setLoading] = useState(false);

  const handleExecute = async () => {
    const arr = arrayInput.split(',').map((s) => parseInt(s.trim(), 10)).filter((n) => !isNaN(n));
    if (arr.length === 0) return;
    setLoading(true);
    try {
      const res = await callBubbleSort({ array: arr });
      setResult(res);
      setHistory((prev) => [
        {
          key: Date.now(),
          input: arr.join(', '),
          sorted: res.sorted.join(', '),
          steps: res.steps,
          time: new Date().toISOString(),
        },
        ...prev,
      ]);
    } finally {
      setLoading(false);
    }
  };

  const columns = [
    { title: '时间', dataIndex: 'time', key: 'time' },
    { title: '原始数组', dataIndex: 'input', key: 'input' },
    { title: '排序结果', dataIndex: 'sorted', key: 'sorted' },
    { title: '比较次数', dataIndex: 'steps', key: 'steps' },
  ];

  return (
    <Space direction="vertical" size="large" style={{ width: '100%' }}>
      <Card title="输入">
        <Space>
          <Input
            placeholder="请输入数组，用逗号分隔分，如: 5, 3, 8, 1, 9"
            value={arrayInput}
            onChange={(e) => setArrayInput(e.target.value)}
            style={{ width: 400 }}
          />
          <Button type="primary" onClick={handleExecute} loading={loading}>
            执行
          </Button>
        </Space>
      </Card>

      {result && (
        <Card title="执行结果">
          <p>原始数组: {result.original.map((n) => <Tag key={n}>{n}</Tag>)}</p>
          <p>排序结果: {result.sorted.map((n) => <Tag key={n} color="green">{n}</Tag>)}</p>
          <Title level={5}>比较次数: {result.steps}</Title>
        </Card>
      )}

      {history.length > 0 && (
        <Card title="历史记录">
          <Table columns={columns} dataSource={history} pagination={{ pageSize: 5 }} />
        </Card>
      )}
    </Space>
  );
}
```

- [ ] **Step 5: 创建 DemoPage.tsx**

```tsx
import { Tabs, Layout, Space, Button } from 'antd';
import { BarChartOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import HelloTab from '../components/tabs/HelloTab';
import HashTab from '../components/tabs/HashTab';
import BubbleSortTab from '../components/tabs/BubbleSortTab';
import ExportButton from '../components/ExportButton';

const { Header, Content } = Layout;

export default function DemoPage() {
  const navigate = useNavigate();

  const tabItems = [
    { key: 'hello', label: 'HelloWorld', children: <HelloTab /> },
    { key: 'hash', label: '哈希算法', children: <HashTab /> },
    { key: 'bubble-sort', label: '冒泡排序', children: <BubbleSortTab /> },
  ];

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Header style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <span style={{ color: '#fff', fontSize: 18, fontWeight: 'bold' }}>
          多接口演示系统
        </span>
        <Space>
          <ExportButton />
          <Button
            icon={<BarChartOutlined />}
            onClick={() => navigate('/report')}
            style={{ color: '#fff', borderColor: '#fff' }}
          >
            查看报表
          </Button>
        </Space>
      </Header>
      <Content style={{ padding: 24 }}>
        <Tabs defaultActiveKey="hello" items={tabItems} size="large" />
      </Content>
    </Layout>
  );
}
```

- [ ] **Step 6: 验证前端页面可正常渲染**

Run: `cd testDJnew-main && npm run build`
Expected: 构建成功，无 TypeScript 错误

- [ ] **Step 7: Commit**

```bash
git add src/
git commit -m "feat: implement main page with 3 tabs and export button"
```

---

## Task 10: 实现报表可视化页面

**Files:**
- Create: `testDJnew-main/src/components/charts/LineChart.tsx`
- Create: `testDJnew-main/src/components/charts/PieChart.tsx`
- Create: `testDJnew-main/src/components/charts/BarChart.tsx`
- Create: `testDJnew-main/src/pages/ReportPage.tsx`

**Interfaces:**
- Consumes: `api.ts` 中的 `getStatistics`（Task 8）、类型定义（Task 8）
- Produces: 报表页面，含维度选择器 + 折线图（调用趋势）+ 饼图（维度占比）+ 柱状图（各接口调用对比）

- [ ] **Step 1: 创建 LineChart.tsx**

```tsx
import ReactECharts from 'echarts-for-react';
import type { StatisticsResponse } from '../../types';

interface Props {
  data: StatisticsResponse | null;
  title: string;
}

export default function LineChart({ data, title }: Props) {
  const option = {
    title: { text: title, left: 'center' },
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: data?.data.map((item) => item.label) ?? [],
    },
    yAxis: { type: 'value', name: '调用次数' },
    series: [
      {
        name: '调用次数',
        type: 'line',
        data: data?.data.map((item) => item.count) ?? [],
        smooth: true,
        areaStyle: { opacity: 0.3 },
      },
    ],
  };

  return <ReactECharts option={option} style={{ height: 350 }} />;
}
```

- [ ] **Step 2: 创建 PieChart.tsx**

```tsx
import ReactECharts from 'echarts-for-react';
import type { StatisticsResponse } from '../../types';

interface Props {
  data: StatisticsResponse | null;
  title: string;
}

export default function PieChart({ data, title }: Props) {
  const option = {
    title: { text: title, left: 'center' },
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { orient: 'vertical', left: 'left' },
    series: [
      {
        name: '占比',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
        label: { show: false },
        emphasis: { label: { show: true, fontSize: 16, fontWeight: 'bold' } },
        data: data?.data.map((item) => ({ name: item.label, value: item.count })) ?? [],
      },
    ],
  };

  return <ReactECharts option={option} style={{ height: 350 }} />;
}
```

- [ ] **Step 3: 创建 BarChart.tsx**

```tsx
import ReactECharts from 'echarts-for-react';
import type { StatisticsResponse } from '../../types';

interface Props {
  data: StatisticsResponse | null;
  title: string;
}

export default function BarChart({ data, title }: Props) {
  const option = {
    title: { text: title, left: 'center' },
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: data?.data.map((item) => item.label) ?? [],
      axisLabel: { rotate: 30 },
    },
    yAxis: { type: 'value', name: '调用次数' },
    series: [
      {
        name: '调用次数',
        type: 'bar',
        data: data?.data.map((item) => item.count) ?? [],
        itemStyle: {
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [
              { offset: 0, color: '#1890ff' },
              { offset: 1, color: '#69c0ff' },
            ],
          },
        },
      },
    ],
  };

  return <ReactECharts option={option} style={{ height: 350 }} />;
}
```

- [ ] **Step 4: 创建 ReportPage.tsx**

```tsx
import { useState, useEffect } from 'react';
import { Layout, Select, Space, Card, Button, Row, Col } from 'antd';
import { ArrowLeftOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { getStatistics } from '../services/api';
import type { StatisticsResponse } from '../types';
import LineChart from '../components/charts/LineChart';
import PieChart from '../components/charts/PieChart';
import BarChart from '../components/charts/BarChart';

const { Header, Content } = Layout;

const dimensionOptions = [
  { value: 'userType', label: '人员类型' },
  { value: 'userLevel', label: '人员层级' },
  { value: 'userDept', label: '人员部门' },
];

const periodOptions = [
  { value: '7d', label: '最近7天' },
  { value: '30d', label: '最近30天' },
  { value: 'all', label: '全部' },
];

export default function ReportPage() {
  const navigate = useNavigate();
  const [dimension, setDimension] = useState('userDept');
  const [period, setPeriod] = useState('all');
  const [stats, setStats] = useState<StatisticsResponse | null>(null);

  const fetchStats = async () => {
    const data = await getStatistics(dimension, period);
    setStats(data);
  };

  useEffect(() => {
    fetchStats();
  }, [dimension, period]);

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Header style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
        <Button
          icon={<ArrowLeftOutlined />}
          onClick={() => navigate('/')}
          style={{ color: '#fff', borderColor: '#fff' }}
        >
          返回主页
        </Button>
        <span style={{ color: '#fff', fontSize: 18, fontWeight: 'bold' }}>
          调用统计报表
        </span>
      </Header>
      <Content style={{ padding: 24 }}>
        <Card style={{ marginBottom: 16 }}>
          <Space size="large">
            <span>维度选择:</span>
            <Select
              value={dimension}
              onChange={setDimension}
              options={dimensionOptions}
              style={{ width: 150 }}
            />
            <span>时间范围:</span>
            <Select
              value={period}
              onChange={setPeriod}
              options={periodOptions}
              style={{ width: 150 }}
            />
            <span>总调用次数: <strong>{stats?.total ?? 0}</strong></span>
          </Space>
        </Card>

        <Row gutter={[16, 16]}>
          <Col span={12}>
            <Card>
              <LineChart data={stats} title="调用趋势（折线图）" />
            </Card>
          </Col>
          <Col span={12}>
            <Card>
              <PieChart data={stats} title="各维度占比（饼图）" />
            </Card>
          </Col>
        </Row>

        <Row gutter={[16, 16]} style={{ marginTop: 16 }}>
          <Col span={24}>
            <Card>
              <BarChart data={stats} title="各维度调用次数对比（柱状图）" />
            </Card>
          </Col>
        </Row>
      </Content>
    </Layout>
  );
}
```

- [ ] **Step 5: 验证前端构建**

Run: `cd testDJnew-main && npm run build`
Expected: 构建成功，无 TypeScript 错误

- [ ] **Step 6: Commit**

```bash
git add src/
git commit -m "feat: implement report page with line, pie, and bar charts"
```

---

## 跨仓对齐点检查

| 对齐项 | 后端 (testDj) | 前端 (testDJnew) | 状态 |
|--------|---------------|-------------------|------|
| HelloWorld 接口 | `POST /api/demo/hello` → `HelloResponse` | `callHello()` → `HelloResponse` | ✅ 契约一致 |
| 哈希接口 | `POST /api/demo/hash` → `HashResponse` | `callHash()` → `HashResponse` | ✅ 契约一致 |
| 冒泡排序接口 | `POST /api/demo/bubble-sort` → `SortResponse` | `callBubbleSort()` → `SortResponse` | ✅ 契约一致 |
| 导出接口 | `GET /api/demo/export?type=&format=csv` → CSV blob | `exportData()` → blob 下载 | ✅ 契约一致 |
| 统计接口 | `GET /api/demo/statistics?dimension=&period=` → `StatisticsResponse` | `getStatistics()` → `StatisticsResponse` | ✅ 契约一致 |
| Header 约定 | 从 `X-User-Id/Type/Level/Dept` 读取 | axios 默认 headers 携带 | ✅ 契约一致 |
| 跨域 | `CorsConfig` 允许所有来源 | Vite proxy `/api` → `localhost:8080` | ✅ 双保险 |

---

## 实施顺序总结

| Phase | 仓库 | 任务 | 产出 |
|-------|------|------|------|
| Phase 1 | testDj | Task 1-4 | Spring Boot 项目 + 三个业务接口 |
| Phase 2 | testDj | Task 5-6 | AOP 埋点 + 统计查询接口 |
| Phase 3 | testDj | Task 7 | CSV 导出接口 |
| Phase 4 | testDJnew | Task 8-9 | React 项目 + 三 Tab 主页面 + 导出按钮 |
| Phase 5 | testDJnew | Task 10 | 报表可视化页面（折线图/饼图/柱状图） |
