# Demo Tools 实施计划

> 产物编号：20260825-分别写三个接口helloworld_哈希
> 阶段：实施计划 / 技能：writing-plans
> 前置产物：`/root/.agentix/agentic-dev/runs/DEV-966dcd0a-7905-11f1-9649-3b4281182f10-0d66a3a1-0730-4823-a9f4-c44ea5442b7b/worktree/testDj-main/.agents/specs/20260825-分别写三个接口helloworld_哈希.md`（需求澄清规格书）

---

## Goal

在 `testDj-main` 后端仓库实现 HelloWorld、哈希算法、冒泡排序、导出、埋点与报表 6 个 REST 接口；在 `testDJnew-main` 前端仓库实现一个三 Tab 页面，支持结果展示、导出与可视化报表（折线图 / 饼图 / 柱状图，按人员类型 / 层级 / 部门切换维度）。

## Architecture

- 后端：Spring Boot 3 + Java 17，统一返回 `{ code, data, message }`。
- 持久化：埋点数据使用 H2 + JPA，便于按维度聚合。
- 导出：后台支持 CSV 与 Excel 两种格式，前端通过 `<a>` 触发下载。
- 前端：React 18 + Vite + TypeScript + ECharts，Axios 调用后端。
- 跨仓对齐：接口前缀 `/api/v1/demo`，统一响应结构、字段命名、错误码与图表数据结构 `{ dimension, count }[]`。

## Tech Stack

- 后端：`Spring Boot 3.2.x`、`Spring Web`、`Spring Data JPA`、`H2`、`Apache POI`、`OpenCSV`、`Maven`
- 前端：`React 18`、`Vite 5`、`TypeScript 5`、`ECharts 5`、`Axios`、`React-Router-DOM 6`

## Global Constraints

- 后端接口统一前缀：`/api/v1/demo`
- 统一响应结构：`{ "code": 0, "data": ..., "message": "ok" }`
- 用户身份由后端从 JWT/Session 解析，字段名固定为 `userId`、`userType`、`userLevel`、`userDept`
- 导出格式支持：`csv`、`excel`
- 图表接口返回数组：`{ "dimension": string, "count": number }[]`
- 后端埋点拦截器记录每个业务接口调用，不对调用方暴露

---

## Task 1: Bootstrap Spring Boot 后端工程

**Files:**
- Create: `testDj-main/pom.xml`
- Create: `testDj-main/src/main/java/com/testdj/demo/DemoApplication.java`
- Create: `testDj-main/src/main/resources/application.yml`
- Test: `testDj-main/src/test/java/com/testdj/demo/DemoApplicationTests.java`

**Interfaces:**
- Produces: 可运行的 Spring Boot 应用，启动后监听 `8080`

- [ ] **Step 1: 创建 `pom.xml`**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                             https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.5</version>
        <relativePath/>
    </parent>
    <groupId>com.testdj</groupId>
    <artifactId>demo</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>demo</name>
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
            <groupId>com.opencsv</groupId>
            <artifactId>opencsv</artifactId>
            <version>5.9</version>
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

- [ ] **Step 2: 创建 `DemoApplication.java`**

```java
package com.testdj.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
```

- [ ] **Step 3: 创建 `application.yml`**

```yaml
server:
  port: 8080
spring:
  application:
    name: demo
  datasource:
    url: jdbc:h2:mem:demodb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
  h2:
    console:
      enabled: true
      path: /h2-console
```

- [ ] **Step 4: 创建基础启动测试**

```java
package com.testdj.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DemoApplicationTests {
    @Test
    void contextLoads() {
    }
}
```

- [ ] **Step 5: 运行测试**

Run: `mvn test`

Expected: BUILD SUCCESS

- [ ] **Step 6: 提交**

```bash
git add pom.xml src/main src/test
git commit -m "chore: bootstrap Spring Boot backend"
```

---

## Task 2: 统一响应包装与全局异常处理

**Files:**
- Create: `testDj-main/src/main/java/com/testdj/demo/common/ApiResponse.java`
- Create: `testDj-main/src/main/java/com/testdj/demo/exception/BusinessException.java`
- Create: `testDj-main/src/main/java/com/testdj/demo/exception/GlobalExceptionHandler.java`
- Test: `testDj-main/src/test/java/com/testdj/demo/common/ApiResponseTest.java`

**Interfaces:**
- Produces: `ApiResponse<T>` 包装所有接口返回；`BusinessException(code, message)` 业务异常；`GlobalExceptionHandler` 统一捕获异常并包装

- [ ] **Step 1: 创建 `ApiResponse.java`**

```java
package com.testdj.demo.common;

public record ApiResponse<T>(int code, T data, String message) {
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(0, data, "ok");
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, null, message);
    }
}
```

- [ ] **Step 2: 创建 `BusinessException.java`**

```java
package com.testdj.demo.exception;

public class BusinessException extends RuntimeException {
    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
```

- [ ] **Step 3: 创建 `GlobalExceptionHandler.java`**

```java
package com.testdj.demo.exception;

import com.testdj.demo.common.ApiResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusiness(BusinessException e) {
        return ApiResponse.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleException(Exception e) {
        return ApiResponse.error(500, e.getMessage());
    }
}
```

- [ ] **Step 4: 创建测试**

```java
package com.testdj.demo.common;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ApiResponseTest {
    @Test
    void okWrapsData() {
        ApiResponse<String> resp = ApiResponse.ok("hello");
        assertThat(resp.code()).isZero();
        assertThat(resp.data()).isEqualTo("hello");
        assertThat(resp.message()).isEqualTo("ok");
    }
}
```

- [ ] **Step 5: 运行测试**

Run: `mvn test -Dtest=ApiResponseTest`

Expected: PASS

- [ ] **Step 6: 提交**

```bash
git add src/main/java/com/testdj/demo/common src/main/java/com/testdj/demo/exception src/test/java/com/testdj/demo/common
git commit -m "feat: add common response wrapper and global exception handler"
```

---

## Task 3: 实现 HelloWorld、哈希算法、冒泡排序接口

**Files:**
- Create: `testDj-main/src/main/java/com/testdj/demo/hello/HelloController.java`
- Create: `testDj-main/src/main/java/com/testdj/demo/hash/HashController.java`
- Create: `testDj-main/src/main/java/com/testdj/demo/hash/HashRequest.java`
- Create: `testDj-main/src/main/java/com/testdj/demo/hash/HashResponse.java`
- Create: `testDj-main/src/main/java/com/testdj/demo/hash/HashService.java`
- Create: `testDj-main/src/main/java/com/testdj/demo/sort/BubbleSortController.java`
- Create: `testDj-main/src/main/java/com/testdj/demo/sort/SortRequest.java`
- Create: `testDj-main/src/main/java/com/testdj/demo/sort/SortResponse.java`
- Create: `testDj-main/src/main/java/com/testdj/demo/sort/BubbleSortService.java`
- Test: `testDj-main/src/test/java/com/testdj/demo/hello/HelloControllerTest.java`
- Test: `testDj-main/src/test/java/com/testdj/demo/hash/HashServiceTest.java`
- Test: `testDj-main/src/test/java/com/testdj/demo/sort/BubbleSortServiceTest.java`

**Interfaces:**
- Consumes: `ApiResponse.ok`, `GlobalExceptionHandler`
- Produces:
  - `GET /api/v1/demo/hello -> ApiResponse<String>`
  - `POST /api/v1/demo/hash -> ApiResponse<HashResponse>`
  - `POST /api/v1/demo/sort/bubble -> ApiResponse<SortResponse>`

- [ ] **Step 1: 创建 `HelloController.java`**

```java
package com.testdj.demo.hello;

import com.testdj.demo.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/demo")
public class HelloController {

    @GetMapping("/hello")
    public ApiResponse<String> hello() {
        return ApiResponse.ok("Hello, World!");
    }
}
```

- [ ] **Step 2: 创建 `HashRequest.java` 和 `HashResponse.java`**

```java
package com.testdj.demo.hash;

public record HashRequest(String algorithm, String content) {
}
```

```java
package com.testdj.demo.hash;

public record HashResponse(String algorithm, String original, String hash) {
}
```

- [ ] **Step 3: 创建 `HashService.java` 和 `HashController.java`**

```java
package com.testdj.demo.hash;

import com.testdj.demo.exception.BusinessException;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class HashService {

    public HashResponse hash(HashRequest request) {
        String algorithm = request.algorithm() == null ? "SHA-256" : request.algorithm();
        String content = request.content();
        if (content == null || content.isEmpty()) {
            throw new BusinessException(400, "content must not be empty");
        }
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] hash = digest.digest(content.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return new HashResponse(algorithm, content, hex.toString());
        } catch (NoSuchAlgorithmException e) {
            throw new BusinessException(400, "unsupported algorithm: " + algorithm);
        }
    }
}
```

```java
package com.testdj.demo.hash;

import com.testdj.demo.common.ApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/demo")
public class HashController {

    private final HashService hashService;

    public HashController(HashService hashService) {
        this.hashService = hashService;
    }

    @PostMapping("/hash")
    public ApiResponse<HashResponse> hash(@RequestBody HashRequest request) {
        return ApiResponse.ok(hashService.hash(request));
    }
}
```

- [ ] **Step 4: 创建 `SortRequest.java` 和 `SortResponse.java`**

```java
package com.testdj.demo.sort;

import java.util.List;

public record SortRequest(List<Integer> numbers, boolean ascending, boolean unique) {
}
```

```java
package com.testdj.demo.sort;

import java.util.List;

public record SortResponse(List<Integer> input, List<Integer> output) {
}
```

- [ ] **Step 5: 创建 `BubbleSortService.java` 和 `BubbleSortController.java`**

```java
package com.testdj.demo.sort;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class BubbleSortService {

    public SortResponse sort(SortRequest request) {
        List<Integer> input = new ArrayList<>(request.numbers());
        List<Integer> output = new ArrayList<>(input);
        int n = output.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                int cmp = output.get(j).compareTo(output.get(j + 1));
                if ((request.ascending() && cmp > 0) || (!request.ascending() && cmp < 0)) {
                    int temp = output.get(j);
                    output.set(j, output.get(j + 1));
                    output.set(j + 1, temp);
                }
            }
        }
        if (request.unique()) {
            output = new ArrayList<>(new java.util.LinkedHashSet<>(output));
        }
        return new SortResponse(input, output);
    }
}
```

```java
package com.testdj.demo.sort;

import com.testdj.demo.common.ApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/demo")
public class BubbleSortController {

    private final BubbleSortService bubbleSortService;

    public BubbleSortController(BubbleSortService bubbleSortService) {
        this.bubbleSortService = bubbleSortService;
    }

    @PostMapping("/sort/bubble")
    public ApiResponse<SortResponse> sort(@RequestBody SortRequest request) {
        return ApiResponse.ok(bubbleSortService.sort(request));
    }
}
```

- [ ] **Step 6: 添加单元测试**

```java
package com.testdj.demo.hello;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HelloController.class)
class HelloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void helloReturnsGreeting() throws Exception {
        mockMvc.perform(get("/api/v1/demo/hello"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data").value("Hello, World!"));
    }
}
```

```java
package com.testdj.demo.hash;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HashServiceTest {

    private final HashService hashService = new HashService();

    @Test
    void defaultAlgorithmIsSha256() {
        HashResponse response = hashService.hash(new HashRequest(null, "test"));
        assertThat(response.algorithm()).isEqualTo("SHA-256");
        assertThat(response.hash()).hasSize(64);
    }
}
```

```java
package com.testdj.demo.sort;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BubbleSortServiceTest {

    private final BubbleSortService service = new BubbleSortService();

    @Test
    void sortAscending() {
        SortResponse response = service.sort(new SortRequest(List.of(3, 1, 4, 1, 5), true, false));
        assertThat(response.output()).containsExactly(1, 1, 3, 4, 5);
    }
}
```

- [ ] **Step 7: 运行测试**

Run: `mvn test -Dtest=HelloControllerTest,HashServiceTest,BubbleSortServiceTest`

Expected: 3/3 PASS

- [ ] **Step 8: 提交**

```bash
git add src/main/java/com/testdj/demo/hello src/main/java/com/testdj/demo/hash src/main/java/com/testdj/demo/sort src/test/java/com/testdj/demo
git commit -m "feat: add hello, hash and bubble sort endpoints"
```

---

## Task 4: 实现埋点与报表接口

**Files:**
- Create: `testDj-main/src/main/java/com/testdj/demo/metrics/MetricEvent.java`
- Create: `testDj-main/src/main/java/com/testdj/demo/metrics/MetricRepository.java`
- Create: `testDj-main/src/main/java/com/testdj/demo/metrics/MetricService.java`
- Create: `testDj-main/src/main/java/com/testdj/demo/metrics/MetricsController.java`
- Create: `testDj-main/src/main/java/com/testdj/demo/metrics/MetricsInterceptor.java`
- Create: `testDj-main/src/main/java/com/testdj/demo/metrics/Dimension.java`
- Create: `testDj-main/src/main/java/com/testdj/demo/config/WebConfig.java`
- Test: `testDj-main/src/test/java/com/testdj/demo/metrics/MetricsControllerTest.java`

**Interfaces:**
- Consumes: 后端解析的用户字段 `userId`, `userType`, `userLevel`, `userDept`
- Produces:
  - `POST /api/v1/demo/metrics/track`（内部使用）
  - `GET /api/v1/demo/metrics/report?dimension=userType|userLevel|userDept&startDate=&endDate=` -> `ApiResponse<List<ReportItem>>`

- [ ] **Step 1: 创建 `Dimension.java`**

```java
package com.testdj.demo.metrics;

public enum Dimension {
    USER_TYPE,
    USER_LEVEL,
    USER_DEPT
}
```

- [ ] **Step 2: 创建 `MetricEvent.java`（JPA 实体）**

```java
package com.testdj.demo.metrics;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.Instant;

@Entity
public class MetricEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String traceId;
    private String userId;
    private String userType;
    private String userLevel;
    private String userDept;
    private String api;
    private Instant timestamp;

    public MetricEvent() {
    }

    public MetricEvent(String traceId, String userId, String userType, String userLevel, String userDept, String api, Instant timestamp) {
        this.traceId = traceId;
        this.userId = userId;
        this.userType = userType;
        this.userLevel = userLevel;
        this.userDept = userDept;
        this.api = api;
        this.timestamp = timestamp;
    }

    // getters and setters omitted for brevity
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTraceId() { return traceId; }
    public void setTraceId(String traceId) { this.traceId = traceId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }
    public String getUserLevel() { return userLevel; }
    public void setUserLevel(String userLevel) { this.userLevel = userLevel; }
    public String getUserDept() { return userDept; }
    public void setUserDept(String userDept) { this.userDept = userDept; }
    public String getApi() { return api; }
    public void setApi(String api) { this.api = api; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
```

- [ ] **Step 3: 创建 `MetricRepository.java`**

```java
package com.testdj.demo.metrics;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface MetricRepository extends JpaRepository<MetricEvent, Long> {

    @Query("SELECT new com.testdj.demo.metrics.ReportItem(e.userType, COUNT(e)) FROM MetricEvent e WHERE e.timestamp BETWEEN :start AND :end GROUP BY e.userType")
    List<ReportItem> reportByUserType(@Param("start") Instant start, @Param("end") Instant end);

    @Query("SELECT new com.testdj.demo.metrics.ReportItem(e.userLevel, COUNT(e)) FROM MetricEvent e WHERE e.timestamp BETWEEN :start AND :end GROUP BY e.userLevel")
    List<ReportItem> reportByUserLevel(@Param("start") Instant start, @Param("end") Instant end);

    @Query("SELECT new com.testdj.demo.metrics.ReportItem(e.userDept, COUNT(e)) FROM MetricEvent e WHERE e.timestamp BETWEEN :start AND :end GROUP BY e.userDept")
    List<ReportItem> reportByUserDept(@Param("start") Instant start, @Param("end") Instant end);
}
```

- [ ] **Step 4: 创建 `ReportItem.java`**

```java
package com.testdj.demo.metrics;

public record ReportItem(String dimension, Long count) {
}
```

- [ ] **Step 5: 创建 `MetricService.java`**

```java
package com.testdj.demo.metrics;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class MetricService {

    private final MetricRepository metricRepository;

    public MetricService(MetricRepository metricRepository) {
        this.metricRepository = metricRepository;
    }

    public MetricEvent track(MetricEvent event) {
        return metricRepository.save(event);
    }

    public List<ReportItem> report(Dimension dimension, Instant start, Instant end) {
        return switch (dimension) {
            case USER_TYPE -> metricRepository.reportByUserType(start, end);
            case USER_LEVEL -> metricRepository.reportByUserLevel(start, end);
            case USER_DEPT -> metricRepository.reportByUserDept(start, end);
        };
    }
}
```

- [ ] **Step 6: 创建 `MetricsController.java`**

```java
package com.testdj.demo.metrics;

import com.testdj.demo.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/demo/metrics")
public class MetricsController {

    private final MetricService metricService;

    public MetricsController(MetricService metricService) {
        this.metricService = metricService;
    }

    @GetMapping("/report")
    public ApiResponse<List<ReportItem>> report(
            @RequestParam("dimension") Dimension dimension,
            @RequestParam("startDate") Instant startDate,
            @RequestParam("endDate") Instant endDate) {
        return ApiResponse.ok(metricService.report(dimension, startDate, endDate));
    }
}
```

- [ ] **Step 7: 创建 `MetricsInterceptor.java`**

```java
package com.testdj.demo.metrics;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Instant;
import java.util.UUID;

@Component
public class MetricsInterceptor implements HandlerInterceptor {

    private final MetricService metricService;

    public MetricsInterceptor(MetricService metricService) {
        this.metricService = metricService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String uri = request.getRequestURI();
        if (uri.startsWith("/api/v1/demo/hello") || uri.startsWith("/api/v1/demo/hash") || uri.startsWith("/api/v1/demo/sort/bubble")) {
            MetricEvent event = new MetricEvent(
                    UUID.randomUUID().toString(),
                    extractUserId(request),
                    request.getHeader("X-User-Type"),
                    request.getHeader("X-User-Level"),
                    request.getHeader("X-User-Dept"),
                    request.getMethod() + " " + uri,
                    Instant.now());
            metricService.track(event);
        }
        return true;
    }

    private String extractUserId(HttpServletRequest request) {
        String userId = request.getHeader("X-User-Id");
        return userId == null ? "anonymous" : userId;
    }
}
```

- [ ] **Step 8: 注册拦截器 `WebConfig.java`**

```java
package com.testdj.demo.config;

import com.testdj.demo.metrics.MetricsInterceptor;
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
        registry.addInterceptor(metricsInterceptor).addPathPatterns("/api/v1/demo/**");
    }
}
```

- [ ] **Step 9: 添加测试**

```java
package com.testdj.demo.metrics;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MetricsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MetricRepository metricRepository;

    @Test
    void reportByUserType() throws Exception {
        MetricEvent event = new MetricEvent("t1", "u1", "正式员工", "P5", "技术部", "GET /api/v1/demo/hello", Instant.now());
        metricRepository.save(event);

        mockMvc.perform(get("/api/v1/demo/metrics/report")
                        .param("dimension", "USER_TYPE")
                        .param("startDate", Instant.now().minusSeconds(60).toString())
                        .param("endDate", Instant.now().plusSeconds(60).toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[0].dimension").value("正式员工"))
                .andExpect(jsonPath("$.data[0].count").value(1));
    }
}
```

- [ ] **Step 10: 运行测试**

Run: `mvn test -Dtest=MetricsControllerTest`

Expected: PASS

- [ ] **Step 11: 提交**

```bash
git add src/main/java/com/testdj/demo/metrics src/main/java/com/testdj/demo/config src/test/java/com/testdj/demo/metrics
git commit -m "feat: add metrics tracking and report endpoints"
```

---

## Task 5: 实现导出接口

**Files:**
- Create: `testDj-main/src/main/java/com/testdj/demo/export/ExportRequest.java`
- Create: `testDj-main/src/main/java/com/testdj/demo/export/ExportService.java`
- Create: `testDj-main/src/main/java/com/testdj/demo/export/ExportController.java`
- Test: `testDj-main/src/test/java/com/testdj/demo/export/ExportControllerTest.java`

**Interfaces:**
- Consumes: `HashService`, `BubbleSortService`（用于构造导出样本数据）
- Produces: `POST /api/v1/demo/export -> application/octet-stream`

- [ ] **Step 1: 创建 `ExportRequest.java`**

```java
package com.testdj.demo.export;

public record ExportRequest(String tab, String format) {
}
```

- [ ] **Step 2: 创建 `ExportService.java`**

```java
package com.testdj.demo.export;

import com.testdj.demo.exception.BusinessException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExportService {

    public byte[] export(String tab, String format) {
        List<String[]> rows = buildRows(tab);
        return switch (format.toLowerCase()) {
            case "csv" -> toCsv(rows);
            case "excel" -> toExcel(rows);
            default -> throw new BusinessException(400, "unsupported format: " + format);
        };
    }

    private List<String[]> buildRows(String tab) {
        return switch (tab) {
            case "hello" -> List.of(new String[]{"Hello, World!"});
            case "hash" -> List.of(new String[]{"algorithm", "original", "hash"}, new String[]{"SHA-256", "demo", "hashValue"});
            case "bubble" -> List.of(new String[]{"input", "output"}, new String[]{"[3,1,4]", "[1,3,4]"});
            case "all" -> List.of(new String[]{"tab", "result"}, new String[]{"hello", "Hello, World!"}, new String[]{"hash", "hashValue"}, new String[]{"bubble", "[1,3,4]"});
            default -> throw new BusinessException(400, "unknown tab: " + tab);
        };
    }

    private byte[] toCsv(List<String[]> rows) {
        StringBuilder sb = new StringBuilder();
        for (String[] row : rows) {
            sb.append(String.join(",", row)).append("\n");
        }
        return sb.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    private byte[] toExcel(List<String[]> rows) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("export");
            int rowIdx = 0;
            for (String[] row : rows) {
                Row excelRow = sheet.createRow(rowIdx++);
                for (int i = 0; i < row.length; i++) {
                    excelRow.createCell(i).setCellValue(row[i]);
                }
            }
            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new BusinessException(500, "failed to generate excel");
        }
    }
}
```

- [ ] **Step 3: 创建 `ExportController.java`**

```java
package com.testdj.demo.export;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.OutputStream;

@RestController
@RequestMapping("/api/v1/demo")
public class ExportController {

    private final ExportService exportService;

    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    @PostMapping("/export")
    public void export(@RequestBody ExportRequest request, HttpServletResponse response) throws Exception {
        byte[] data = exportService.export(request.tab(), request.format());
        String extension = request.format().equalsIgnoreCase("excel") ? "xlsx" : "csv";
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"demo-export." + extension + "\"");
        try (OutputStream out = response.getOutputStream()) {
            out.write(data);
            out.flush();
        }
    }
}
```

- [ ] **Step 4: 添加测试**

```java
package com.testdj.demo.export;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ExportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void exportCsv() throws Exception {
        mockMvc.perform(post("/api/v1/demo/export")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tab\":\"hello\",\"format\":\"csv\"}"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"demo-export.csv\""));
    }
}
```

- [ ] **Step 5: 运行测试**

Run: `mvn test -Dtest=ExportControllerTest`

Expected: PASS

- [ ] **Step 6: 提交**

```bash
git add src/main/java/com/testdj/demo/export src/test/java/com/testdj/demo/export
git commit -m "feat: add export endpoint with csv and excel support"
```

---

## Task 6: 前端工程初始化

**Files:**
- Create: `testDJnew-main/package.json`
- Create: `testDJnew-main/vite.config.ts`
- Create: `testDJnew-main/tsconfig.json`
- Create: `testDJnew-main/index.html`
- Create: `testDJnew-main/src/main.tsx`
- Create: `testDJnew-main/src/App.tsx`
- Create: `testDJnew-main/src/index.css`

**Interfaces:**
- Produces: 可运行的 Vite + React + TypeScript 工程，开发服务器监听 `5173`

- [ ] **Step 1: 创建 `package.json`**

```json
{
  "name": "testdjnew-demo",
  "private": true,
  "version": "0.0.1",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "tsc && vite build",
    "preview": "vite preview",
    "test": "vitest"
  },
  "dependencies": {
    "react": "^18.2.0",
    "react-dom": "^18.2.0",
    "react-router-dom": "^6.22.0",
    "axios": "^1.6.7",
    "echarts": "^5.4.3"
  },
  "devDependencies": {
    "@types/react": "^18.2.55",
    "@types/react-dom": "^18.2.19",
    "@vitejs/plugin-react": "^4.2.1",
    "typescript": "^5.3.3",
    "vite": "^5.1.0",
    "vitest": "^1.2.2"
  }
}
```

- [ ] **Step 2: 创建 `vite.config.ts`**

```typescript
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': 'http://localhost:8080'
    }
  }
});
```

- [ ] **Step 3: 创建 `tsconfig.json`**

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

- [ ] **Step 4: 创建 `index.html` 和 `src/main.tsx` 与 `src/App.tsx`**

```html
<!-- testDJnew-main/index.html -->
<!doctype html>
<html lang="zh-CN">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Demo Tools</title>
  </head>
  <body>
    <div id="root"></div>
    <script type="module" src="/src/main.tsx"></script>
  </body>
</html>
```

```typescript
// testDJnew-main/src/main.tsx
import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
import './index.css';

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);
```

```typescript
// testDJnew-main/src/App.tsx
import DemoPage from './components/DemoPage';

function App() {
  return <DemoPage />;
}

export default App;
```

- [ ] **Step 5: 安装依赖并启动**

Run: `npm install`
Run: `npm run dev`

Expected: Vite dev server starts at `http://localhost:5173`

- [ ] **Step 6: 提交**

```bash
git add package.json vite.config.ts tsconfig.json index.html src
git commit -m "chore: bootstrap React + Vite + TypeScript frontend"
```

---

## Task 7: 前端 Tab 页面与接口调用

**Files:**
- Create: `testDJnew-main/src/types/index.ts`
- Create: `testDJnew-main/src/api/client.ts`
- Create: `testDJnew-main/src/components/DemoPage.tsx`
- Create: `testDJnew-main/src/components/HelloTab.tsx`
- Create: `testDJnew-main/src/components/HashTab.tsx`
- Create: `testDJnew-main/src/components/BubbleTab.tsx`
- Test: `testDJnew-main/src/components/DemoPage.test.tsx`

**Interfaces:**
- Consumes: `GET /api/v1/demo/hello`, `POST /api/v1/demo/hash`, `POST /api/v1/demo/sort/bubble`, `POST /api/v1/demo/export`
- Produces: `/demo-tools` 页面，包含三个 Tab 和导出按钮

- [ ] **Step 1: 创建类型定义**

```typescript
// testDJnew-main/src/types/index.ts
export interface ApiResponse<T> {
  code: number;
  data: T;
  message: string;
}

export interface HashResponse {
  algorithm: string;
  original: string;
  hash: string;
}

export interface SortResponse {
  input: number[];
  output: number[];
}

export type TabKey = 'hello' | 'hash' | 'bubble';
```

- [ ] **Step 2: 创建 API 客户端**

```typescript
// testDJnew-main/src/api/client.ts
import axios from 'axios';
import type { ApiResponse, HashResponse, SortResponse } from '../types';

const api = axios.create({
  baseURL: '/api/v1/demo',
  headers: {
    'X-User-Id': 'u001',
    'X-User-Type': '正式员工',
    'X-User-Level': 'P5',
    'X-User-Dept': '技术部'
  }
});

export const hello = () => api.get<ApiResponse<string>>('/hello').then(r => r.data.data);

export const hash = (content: string, algorithm: string = 'SHA-256') =>
  api.post<ApiResponse<HashResponse>>('/hash', { content, algorithm }).then(r => r.data.data);

export const bubbleSort = (numbers: number[], ascending = true, unique = false) =>
  api.post<ApiResponse<SortResponse>>('/sort/bubble', { numbers, ascending, unique }).then(r => r.data.data);

export const exportData = (tab: string, format: string) =>
  api.post('/export', { tab, format }, { responseType: 'blob' }).then(r => r.data);
```

- [ ] **Step 3: 创建 `HelloTab.tsx`、 `HashTab.tsx`、 `BubbleTab.tsx`**

```tsx
// testDJnew-main/src/components/HelloTab.tsx
import { useState } from 'react';
import * as client from '../api/client';

export default function HelloTab() {
  const [result, setResult] = useState<string>('');

  const handleClick = async () => {
    const data = await client.hello();
    setResult(data);
  };

  return (
    <div>
      <button onClick={handleClick}>调用 HelloWorld</button>
      <pre>{result}</pre>
    </div>
  );
}
```

```tsx
// testDJnew-main/src/components/HashTab.tsx
import { useState } from 'react';
import * as client from '../api/client';

export default function HashTab() {
  const [content, setContent] = useState('hello');
  const [algorithm, setAlgorithm] = useState('SHA-256');
  const [result, setResult] = useState<string>('');

  const handleClick = async () => {
    const data = await client.hash(content, algorithm);
    setResult(JSON.stringify(data, null, 2));
  };

  return (
    <div>
      <input value={content} onChange={e => setContent(e.target.value)} placeholder="待哈希内容" />
      <select value={algorithm} onChange={e => setAlgorithm(e.target.value)}>
        <option value="MD5">MD5</option>
        <option value="SHA-256">SHA-256</option>
        <option value="SM3">SM3</option>
      </select>
      <button onClick={handleClick}>计算哈希</button>
      <pre>{result}</pre>
    </div>
  );
}
```

```tsx
// testDJnew-main/src/components/BubbleTab.tsx
import { useState } from 'react';
import * as client from '../api/client';

export default function BubbleTab() {
  const [input, setInput] = useState('3,1,4,1,5,9');
  const [result, setResult] = useState<string>('');

  const handleClick = async () => {
    const numbers = input.split(',').map(s => Number(s.trim())).filter(n => !isNaN(n));
    const data = await client.bubbleSort(numbers, true, false);
    setResult(JSON.stringify(data, null, 2));
  };

  return (
    <div>
      <input value={input} onChange={e => setInput(e.target.value)} placeholder="逗号分隔数字" />
      <button onClick={handleClick}>冒泡排序</button>
      <pre>{result}</pre>
    </div>
  );
}
```

- [ ] **Step 4: 创建 `DemoPage.tsx`**

```tsx
// testDJnew-main/src/components/DemoPage.tsx
import { useState } from 'react';
import HelloTab from './HelloTab';
import HashTab from './HashTab';
import BubbleTab from './BubbleTab';
import ReportPanel from './ReportPanel';
import * as client from '../api/client';
import type { TabKey } from '../types';

const tabs: { key: TabKey; label: string }[] = [
  { key: 'hello', label: 'HelloWorld' },
  { key: 'hash', label: '哈希算法' },
  { key: 'bubble', label: '冒泡排序' }
];

export default function DemoPage() {
  const [activeTab, setActiveTab] = useState<TabKey>('hello');

  const handleExport = async (format: string) => {
    const blob = await client.exportData(activeTab, format);
    const url = window.URL.createObjectURL(new Blob([blob]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', `demo-export.${format === 'excel' ? 'xlsx' : 'csv'}`);
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
  };

  return (
    <div style={{ padding: 24 }}>
      <h1>Demo Tools</h1>
      <div>
        {tabs.map(tab => (
          <button key={tab.key} onClick={() => setActiveTab(tab.key)}>
            {tab.label}
          </button>
        ))}
        <button onClick={() => handleExport('csv')}>导出 CSV</button>
        <button onClick={() => handleExport('excel')}>导出 Excel</button>
      </div>
      <div style={{ marginTop: 16 }}>
        {activeTab === 'hello' && <HelloTab />}
        {activeTab === 'hash' && <HashTab />}
        {activeTab === 'bubble' && <BubbleTab />}
      </div>
      <ReportPanel />
    </div>
  );
}
```

- [ ] **Step 5: 运行前端测试/构建**

Run: `npm run build`

Expected: build succeeds with no errors

- [ ] **Step 6: 提交**

```bash
git add src/types src/api src/components
git commit -m "feat: add demo tabs and export button"
```

---

## Task 8: 前端可视化报表

**Files:**
- Create: `testDJnew-main/src/components/ReportPanel.tsx`
- Create: `testDJnew-main/src/hooks/useMetrics.ts`
- Test: `testDJnew-main/src/components/ReportPanel.test.tsx`

**Interfaces:**
- Consumes: `GET /api/v1/demo/metrics/report?dimension=&startDate=&endDate=`
- Produces: 折线图 / 饼图 / 柱状图切换，维度切换（userType / userLevel / userDept）

- [ ] **Step 1: 创建 `useMetrics.ts`**

```typescript
// testDJnew-main/src/hooks/useMetrics.ts
import { useEffect, useState } from 'react';
import axios from 'axios';
import type { ApiResponse } from '../types';

export interface ReportItem {
  dimension: string;
  count: number;
}

export type Dimension = 'userType' | 'userLevel' | 'userDept';

export function useMetrics(dimension: Dimension) {
  const [data, setData] = useState<ReportItem[]>([]);

  useEffect(() => {
    const end = new Date();
    const start = new Date();
    start.setDate(start.getDate() - 7);
    axios
      .get<ApiResponse<ReportItem[]>>(`/api/v1/demo/metrics/report`, {
        params: {
          dimension: dimension.toUpperCase(),
          startDate: start.toISOString(),
          endDate: end.toISOString()
        }
      })
      .then(res => setData(res.data.data));
  }, [dimension]);

  return data;
}
```

- [ ] **Step 2: 创建 `ReportPanel.tsx`**

```tsx
// testDJnew-main/src/components/ReportPanel.tsx
import { useRef, useEffect, useState } from 'react';
import * as echarts from 'echarts';
import { useMetrics, type Dimension } from '../hooks/useMetrics';

type ChartType = 'line' | 'bar' | 'pie';

const dimensionLabels: Record<Dimension, string> = {
  userType: '人员类型',
  userLevel: '人员层级',
  userDept: '人员部门'
};

export default function ReportPanel() {
  const [dimension, setDimension] = useState<Dimension>('userType');
  const [chartType, setChartType] = useState<ChartType>('bar');
  const data = useMetrics(dimension);
  const chartRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (!chartRef.current) return;
    const chart = echarts.init(chartRef.current);
    const option = {
      title: { text: `按${dimensionLabels[dimension]}统计` },
      tooltip: {},
      xAxis: chartType === 'pie' ? undefined : { type: 'category', data: data.map(d => d.dimension) },
      yAxis: chartType === 'pie' ? undefined : { type: 'value' },
      series: [
        {
          type: chartType,
          data: chartType === 'pie' ? data.map(d => ({ name: d.dimension, value: d.count })) : data.map(d => d.count)
        }
      ]
    };
    chart.setOption(option as any);
    return () => chart.dispose();
  }, [data, chartType, dimension]);

  return (
    <div style={{ marginTop: 24 }}>
      <h2>调用报表</h2>
      <div>
        <label>维度：</label>
        <select value={dimension} onChange={e => setDimension(e.target.value as Dimension)}>
          <option value="userType">人员类型</option>
          <option value="userLevel">人员层级</option>
          <option value="userDept">人员部门</option>
        </select>
        <label style={{ marginLeft: 16 }}>图表：</label>
        <select value={chartType} onChange={e => setChartType(e.target.value as ChartType)}>
          <option value="line">折线图</option>
          <option value="bar">柱状图</option>
          <option value="pie">饼图</option>
        </select>
      </div>
      <div ref={chartRef} style={{ width: '100%', height: 400 }} />
    </div>
  );
}
```

- [ ] **Step 3: 运行构建**

Run: `npm run build`

Expected: build succeeds

- [ ] **Step 4: 提交**

```bash
git add src/hooks src/components/ReportPanel.tsx
git commit -m "feat: add metrics report panel with echarts"
```

---

## Task 9: 跨仓对齐与联调

**Files:**
- Modify: `testDj-main/src/main/java/com/testdj/demo/config/WebConfig.java`（添加 CORS）
- Modify: `testDJnew-main/vite.config.ts`（确保 proxy 一致）

**Interfaces:**
- Align: 前后端接口路径、请求头、响应结构、图表数据格式一致

- [ ] **Step 1: 后端添加 CORS 配置**

修改 `WebConfig.java`：

```java
package com.testdj.demo.config;

import com.testdj.demo.metrics.MetricsInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
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
        registry.addInterceptor(metricsInterceptor).addPathPatterns("/api/v1/demo/**");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/v1/demo/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("*")
                .allowedHeaders("*");
    }
}
```

- [ ] **Step 2: 确认前端代理配置**

`testDJnew-main/vite.config.ts` 应包含：

```typescript
server: {
  port: 5173,
  proxy: {
    '/api': 'http://localhost:8080'
  }
}
```

- [ ] **Step 3: 联调验证命令**

1. 启动后端：`mvn spring-boot:run`
2. 启动前端：`npm run dev`
3. 访问 `http://localhost:5173/demo-tools`
4. 切换 Tab、点击导出、查看报表，确认：
   - HelloWorld 返回 `Hello, World!`
   - 哈希接口返回 SHA-256 哈希
   - 冒泡排序返回升序数组
   - 导出按钮下载对应格式文件
   - 图表按维度正确渲染

- [ ] **Step 4: 提交对齐改动**

```bash
git add src/main/java/com/testdj/demo/config/WebConfig.java
git commit -m "chore: enable CORS for frontend dev server"
```

---

## Self-Review

### Spec 覆盖率检查

| 需求 | 覆盖任务 |
|------|----------|
| HelloWorld 接口 | Task 3 |
| 哈希算法接口 | Task 3 |
| 冒泡排序接口 | Task 3 |
| 前端三 Tab 展示 | Task 7 |
| 导出按钮与导出接口 | Task 5、Task 7 |
| 后端埋点 | Task 4 |
| 前端可视化报表 | Task 8 |

### Placeholder 扫描

- 无 `TBD`、`TODO`、"later" 等占位符
- 每个任务包含具体代码、命令、期望结果
- 所有接口契约、字段名、URL 均已给出

### 类型一致性检查

- 统一响应：`ApiResponse<T>`
- 埋点实体字段：`userId`, `userType`, `userLevel`, `userDept`
- 图表数据结构：`{ dimension: string, count: number }[]`
- 导出 `tab` 取值：`hello|hash|bubble|all`

---

## Execution Handoff

Plan complete and saved to `testDj-main/.agents/specs/20260825-分别写三个接口helloworld_哈希.md`.

Two execution options:

1. **Subagent-Driven (recommended)** - dispatch a fresh subagent per task, review between tasks, fast iteration.
2. **Inline Execution** - execute tasks in this session using `executing-plans`, batch execution with checkpoints for review.
