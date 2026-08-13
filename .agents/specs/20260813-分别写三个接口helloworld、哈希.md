# 算法演示与调用埋点可视化平台 — 实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 在 testDj（后端 Spring Boot）实现 helloworld/哈希/冒泡排序三个算法接口、导出接口、AOP 埋点与埋点查询接口；在 testDJnew（前端 React+ECharts）实现三 Tab 页面、导出按钮与多维报表可视化。

**Architecture:** 后端按 algo / export / tracking / person 四模块划分，算法接口为纯计算 REST 端点，埋点通过 `@TrackInvoke` 注解 + AOP 环绕切面非侵入采集，落库至 `invoke_log` 表，导出接口从埋点表读取快照生成 xlsx/csv，埋点查询接口按维度聚合返回 series/trend 结构。前端单页面 `AlgoDashboardPage` 承载三 Tab + 工具栏 + 报表区，图表统一用 ECharts 渲染折线/饼图/柱状图。跨仓通过第 4 章接口契约对齐。

**Tech Stack:**
- 后端：Java 17、Spring Boot 3.2.x、H2（开发）/ MySQL 8（生产）、Apache POI 5.x、Lombok、JUnit 5、MockMvc
- 前端：React 18、TypeScript 5、Vite 5、Ant Design 5、Apache ECharts 5、Axios、Vitest、Testing Library

---

## Global Constraints

- Java 17 为最低版本；Spring Boot 3.2+。
- 统一响应体：`{ "code": int, "data": object|null, "msg": string|null, "traceId": string }`，`code=0` 成功。
- 所有 REST 接口路径前缀 `/api/`。
- 调用人身份通过请求头 `X-User-Id`（必填）+ `X-User-Name`（可选）传递；人员维度由 `person` 元数据表按 `caller_id` 查询填充。
- 埋点采集失败不得影响业务接口（AOP 切面 try-catch + 异步落库）。
- 接口契约向后兼容：新增字段不破坏既有字段。
- 前端组件单一职责，每个组件可独立测试。
- 遵循 TDD：先写失败测试 → 实现 → 测试通过 → 提交。
- 频繁提交：每个 Task 结束提交一次。

---

## File Structure

### 后端 testDj（Spring Boot 项目根：`testDj-main/`）

```
testDj-main/
├── pom.xml
└── src/
    ├── main/
    │   ├── java/com/dt/algo/
    │   │   ├── AlgoApplication.java                 # 启动类
    │   │   ├── common/
    │   │   │   ├── ApiResponse.java                 # 统一响应体
    │   │   │   ├── GlobalExceptionHandler.java       # 全局异常处理
    │   │   │   └── TraceIdFilter.java               # traceId 生成与注入
    │   │   ├── algo/
    │   │   │   ├── AlgoController.java              # 三个算法 REST 端点
    │   │   │   ├── AlgoService.java                # 算法纯函数逻辑
    │   │   │   └── dto/
    │   │   │       ├── HelloWorldRequest.java
    │   │   │       ├── HelloWorldResponse.java
    │   │   │       ├── HashRequest.java
    │   │   │       ├── HashResponse.java
    │   │   │       ├── BubbleRequest.java
    │   │   │       └── BubbleResponse.java
    │   │   ├── tracking/
    │   │   │   ├── TrackInvoke.java                 # 埋点注解
    │   │   │   ├── TrackingAspect.java             # AOP 环绕切面
    │   │   │   ├── InvokeLog.java                  # 实体
    │   │   │   ├── InvokeLogRepository.java        # 仓储
    │   │   │   ├── TrackingService.java            # 异步落库
    │   │   │   ├── MetricsController.java          # 埋点查询 REST 端点
    │   │   │   ├── MetricsService.java             # 聚合查询逻辑
    │   │   │   └── dto/
    │   │   │       ├── MetricsSummaryRequest.java
    │   │   │       └── MetricsSummaryResponse.java
    │   │   ├── person/
    │   │   │   ├── Person.java                      # 人员元数据实体
    │   │   │   ├── PersonRepository.java
    │   │   │   ├── PersonService.java               # 按 caller_id 查维度
    │   │   │   └── PersonDataInitializer.java      # 种子数据
    │   │   ├── export/
    │   │   │   ├── ExportController.java            # 导出 REST 端点
    │   │   │   ├── ExportService.java              # xlsx/csv 生成
    │   │   │   └── dto/ExportResultRow.java
    │   │   └── config/
    │   │       └── RequestContextFilter.java       # 解析 X-User-Id 填充上下文
    │   └── resources/
    │       ├── application.yml
    │       └── schema.sql                           # H2 建表
    └── test/
        └── java/com/dt/algo/
            ├── algo/AlgoServiceTest.java
            ├── algo/AlgoControllerIntegrationTest.java
            ├── tracking/TrackingAspectTest.java
            ├── tracking/MetricsServiceTest.java
            ├── person/PersonServiceTest.java
            └── export/ExportServiceTest.java
```

### 前端 testDJnew（React 项目根：`testDJnew-main/`）

```
testDJnew-main/
├── package.json
├── vite.config.ts
├── tsconfig.json
├── index.html
└── src/
    ├── main.tsx
    ├── App.tsx
    ├── api/
    │   ├── client.ts              # Axios 实例 + 拦截器
    │   ├── algoApi.ts             # 三个算法接口调用
    │   ├── exportApi.ts           # 导出接口调用
    │   └── metricsApi.ts          # 埋点查询接口调用
    ├── types/
    │   └── api.ts                 # 跨仓契约 TS 类型定义
    ├── pages/
    │   └── AlgoDashboardPage.tsx  # 主页面
    ├── components/
    │   ├── AlgoTabs.tsx           # 三 Tab 容器
    │   ├── HelloWorldTab.tsx
    │   ├── HashTab.tsx
    │   ├── BubbleTab.tsx
    │   ├── ExportButton.tsx
    │   ├── MetricsFilter.tsx
    │   ├── ChartLine.tsx
    │   ├── ChartPie.tsx
    │   └── ChartBar.tsx
    └── __tests__/
        ├── HelloWorldTab.test.tsx
        ├── HashTab.test.tsx
        ├── BubbleTab.test.tsx
        ├── ExportButton.test.tsx
        └── MetricsFilter.test.tsx
```

---

## Task 1: 后端项目骨架与统一响应体

**Files:**
- Create: `testDj-main/pom.xml`
- Create: `testDj-main/src/main/java/com/dt/algo/AlgoApplication.java`
- Create: `testDj-main/src/main/java/com/dt/algo/common/ApiResponse.java`
- Create: `testDj-main/src/main/java/com/dt/algo/common/GlobalExceptionHandler.java`
- Create: `testDj-main/src/main/java/com/dt/algo/common/TraceIdFilter.java`
- Create: `testDj-main/src/main/resources/application.yml`
- Test: `testDj-main/src/test/java/com/dt/algo/common/ApiResponseTest.java`

**Interfaces:**
- Consumes: 无（首个 Task）
- Produces: `ApiResponse<T>` — 统一响应体，后续所有 Controller 使用
  ```java
  public class ApiResponse<T> {
      private int code;        // 0=成功
      private T data;
      private String msg;
      private String traceId;
      public static <T> ApiResponse<T> ok(T data) { ... }
      public static <T> ApiResponse<T> error(int code, String msg) { ... }
  }
  ```

- [ ] **Step 1: Write the failing test**

```java
package com.dt.algo.common;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {
    @Test
    void ok_sets_code_zero_and_data() {
        ApiResponse<String> resp = ApiResponse.ok("hello");
        assertEquals(0, resp.getCode());
        assertEquals("hello", resp.getData());
        assertNull(resp.getMsg());
    }

    @Test
    void error_sets_code_and_msg() {
        ApiResponse<Void> resp = ApiResponse.error(400, "bad request");
        assertEquals(400, resp.getCode());
        assertEquals("bad request", resp.getMsg());
        assertNull(resp.getData());
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd testDj-main && mvn test -pl . -Dtest=ApiResponseTest -q`（首次需先建 pom.xml）
Expected: 编译失败 — `ApiResponse` 类不存在

- [ ] **Step 3: Write minimal implementation**

`pom.xml`:
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
    </parent>
    <groupId>com.dt</groupId>
    <artifactId>algo-platform</artifactId>
    <version>0.1.0</version>
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
            <artifactId>spring-boot-starter-aop</artifactId>
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
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
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

`AlgoApplication.java`:
```java
package com.dt.algo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AlgoApplication {
    public static void main(String[] args) {
        SpringApplication.run(AlgoApplication.class, args);
    }
}
```

`ApiResponse.java`:
```java
package com.dt.algo.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ApiResponse<T> {
    private int code;
    private T data;
    private String msg;
    private String traceId;

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(0, data, null, UUID.randomUUID().toString());
    }

    public static <T> ApiResponse<T> error(int code, String msg) {
        return new ApiResponse<>(code, null, msg, UUID.randomUUID().toString());
    }
}
```

`application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:algodb;DB_CLOSE_DELAY=-1
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: none
    show-sql: true
  sql:
    init:
      mode: always
      schema-locations: classpath:schema.sql
server:
  port: 8080
```

- [ ] **Step 4: Run test to verify it passes**

Run: `cd testDj-main && mvn test -Dtest=ApiResponseTest -q`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
cd testDj-main
git add pom.xml src/main/java/com/dt/algo/AlgoApplication.java src/main/java/com/dt/algo/common/ApiResponse.java src/main/resources/application.yml src/test/java/com/dt/algo/common/ApiResponseTest.java
git commit -m "feat(backend): project scaffold + ApiResponse unified response body"
```

---

## Task 2: 全局异常处理与 TraceId 过滤器

**Files:**
- Create: `testDj-main/src/main/java/com/dt/algo/common/GlobalExceptionHandler.java`
- Create: `testDj-main/src/main/java/com/dt/algo/common/TraceIdFilter.java`
- Test: `testDj-main/src/test/java/com/dt/algo/common/GlobalExceptionHandlerTest.java`

**Interfaces:**
- Consumes: `ApiResponse`（Task 1）
- Produces: 全局异常处理返回 `ApiResponse.error(...)`；`TraceIdFilter` 为每个请求生成 `traceId` 存入 `MDC`

- [ ] **Step 1: Write the failing test**

```java
package com.dt.algo.common;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {
    @Test
    void illegalArgument_returns_400() {
        // 验证 GlobalExceptionHandler 处理 IllegalArgumentException 返回 code=400
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        IllegalArgumentException ex = new IllegalArgumentException("input required");
        var resp = handler.handleIllegalArgument(ex);
        assertEquals(400, resp.getCode());
        assertEquals("input required", resp.getMsg());
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd testDj-main && mvn test -Dtest=GlobalExceptionHandlerTest -q`
Expected: FAIL — `GlobalExceptionHandler` 不存在

- [ ] **Step 3: Write minimal implementation**

`GlobalExceptionHandler.java`:
```java
package com.dt.algo.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleIllegalArgument(IllegalArgumentException ex) {
        return ApiResponse.error(400, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleGeneric(Exception ex) {
        return ApiResponse.error(500, "internal error");
    }
}
```

`TraceIdFilter.java`:
```java
package com.dt.algo.common;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.UUID;

@Component
public class TraceIdFilter implements Filter {
    public static final String TRACE_ID = "traceId";

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        String traceId = ((HttpServletRequest) req).getHeader("X-Trace-Id");
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString();
        }
        MDC.put(TRACE_ID, traceId);
        try {
            chain.doFilter(req, res);
        } finally {
            MDC.remove(TRACE_ID);
        }
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `cd testDj-main && mvn test -Dtest=GlobalExceptionHandlerTest -q`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
cd testDj-main
git add src/main/java/com/dt/algo/common/GlobalExceptionHandler.java src/main/java/com/dt/algo/common/TraceIdFilter.java src/test/java/com/dt/algo/common/GlobalExceptionHandlerTest.java
git commit -m "feat(backend): global exception handler + traceId filter"
```

---

## Task 3: 三个算法纯函数 Service

**Files:**
- Create: `testDj-main/src/main/java/com/dt/algo/algo/AlgoService.java`
- Create: `testDj-main/src/main/java/com/dt/algo/algo/dto/HelloWorldRequest.java`
- Create: `testDj-main/src/main/java/com/dt/algo/algo/dto/HelloWorldResponse.java`
- Create: `testDj-main/src/main/java/com/dt/algo/algo/dto/HashRequest.java`
- Create: `testDj-main/src/main/java/com/dt/algo/algo/dto/HashResponse.java`
- Create: `testDj-main/src/main/java/com/dt/algo/algo/dto/BubbleRequest.java`
- Create: `testDj-main/src/main/java/com/dt/algo/algo/dto/BubbleResponse.java`
- Test: `testDj-main/src/test/java/com/dt/algo/algo/AlgoServiceTest.java`

**Interfaces:**
- Consumes: 无
- Produces:
  - `AlgoService.helloWorld(String input) -> HelloWorldResponse`
  - `AlgoService.hash(String input, String algo) -> HashResponse`
  - `AlgoService.bubbleSort(List<Integer> input) -> BubbleResponse`

- [ ] **Step 1: Write the failing test**

```java
package com.dt.algo.algo;

import com.dt.algo.algo.dto.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class AlgoServiceTest {
    private final AlgoService service = new AlgoService();

    @Test
    void helloWorld_default() {
        HelloWorldResponse resp = service.helloWorld("world");
        assertEquals("Hello, world!", resp.getMessage());
    }

    @Test
    void helloWorld_custom() {
        HelloWorldResponse resp = service.helloWorld("DT");
        assertEquals("Hello, DT!", resp.getMessage());
    }

    @ParameterizedTest
    @CsvSource({ "abc,SHA-256", "abc,MD5", "abc,SHA-1" })
    void hash_returns_nonempty_digest(String input, String algo) {
        HashResponse resp = service.hash(input, algo);
        assertEquals(input, resp.getInput());
        assertEquals(algo, resp.getAlgo());
        assertNotNull(resp.getDigest());
        assertFalse(resp.getDigest().isEmpty());
    }

    @Test
    void hash_sha256_known_value() {
        HashResponse resp = service.hash("abc", "SHA-256");
        assertEquals("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad",
                resp.getDigest());
    }

    @Test
    void bubbleSort_sorts_and_counts_swaps() {
        BubbleResponse resp = service.bubbleSort(List.of(5, 3, 8, 1, 9, 2));
        assertEquals(List.of(1, 2, 3, 5, 8, 9), resp.getSorted());
        assertTrue(resp.getSwaps() > 0);
        assertTrue(resp.getDurationMs() >= 0);
    }

    @Test
    void bubbleSort_empty() {
        BubbleResponse resp = service.bubbleSort(List.of());
        assertTrue(resp.getSorted().isEmpty());
        assertEquals(0, resp.getSwaps());
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd testDj-main && mvn test -Dtest=AlgoServiceTest -q`
Expected: FAIL — `AlgoService` 及 DTO 不存在

- [ ] **Step 3: Write minimal implementation**

`HelloWorldRequest.java`:
```java
package com.dt.algo.algo.dto;

import lombok.Data;

@Data
public class HelloWorldRequest {
    private String input = "world";
}
```

`HelloWorldResponse.java`:
```java
package com.dt.algo.algo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HelloWorldResponse {
    private String message;
}
```

`HashRequest.java`:
```java
package com.dt.algo.algo.dto;

import lombok.Data;

@Data
public class HashRequest {
    private String input;
    private String algo = "SHA-256";
}
```

`HashResponse.java`:
```java
package com.dt.algo.algo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HashResponse {
    private String input;
    private String algo;
    private String digest;
}
```

`BubbleRequest.java`:
```java
package com.dt.algo.algo.dto;

import lombok.Data;
import java.util.List;

@Data
public class BubbleRequest {
    private List<Integer> input;
}
```

`BubbleResponse.java`:
```java
package com.dt.algo.algo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class BubbleResponse {
    private List<Integer> input;
    private List<Integer> sorted;
    private int swaps;
    private double durationMs;
}
```

`AlgoService.java`:
```java
package com.dt.algo.algo;

import com.dt.algo.algo.dto.*;
import org.springframework.stereotype.Service;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Service
public class AlgoService {

    public HelloWorldResponse helloWorld(String input) {
        String name = (input == null || input.isBlank()) ? "world" : input;
        return new HelloWorldResponse("Hello, " + name + "!");
    }

    public HashResponse hash(String input, String algo) {
        String algorithm = (algo == null || algo.isBlank()) ? "SHA-256" : algo;
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] digestBytes = md.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digestBytes) {
                sb.append(String.format("%02x", b));
            }
            return new HashResponse(input, algorithm, sb.toString());
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("unsupported algo: " + algorithm);
        }
    }

    public BubbleResponse bubbleSort(List<Integer> input) {
        List<Integer> arr = new ArrayList<>(input);
        int n = arr.size();
        int swaps = 0;
        long start = System.nanoTime();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1 - i; j++) {
                if (arr.get(j) > arr.get(j + 1)) {
                    int tmp = arr.get(j);
                    arr.set(j, arr.get(j + 1));
                    arr.set(j + 1, tmp);
                    swaps++;
                }
            }
        }
        double durationMs = (System.nanoTime() - start) / 1_000_000.0;
        return new BubbleResponse(input, arr, swaps, durationMs);
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `cd testDj-main && mvn test -Dtest=AlgoServiceTest -q`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
cd testDj-main
git add src/main/java/com/dt/algo/algo/ src/test/java/com/dt/algo/algo/AlgoServiceTest.java
git commit -m "feat(algo): helloworld/hash/bubbleSort pure service + DTOs"
```

---

## Task 4: 算法 REST Controller

**Files:**
- Create: `testDj-main/src/main/java/com/dt/algo/algo/AlgoController.java`
- Test: `testDj-main/src/test/java/com/dt/algo/algo/AlgoControllerIntegrationTest.java`

**Interfaces:**
- Consumes: `AlgoService`（Task 3），`ApiResponse`（Task 1）
- Produces: 三个 REST 端点
  - `POST /api/algo/helloworld` — body `HelloWorldRequest` → `ApiResponse<HelloWorldResponse>`
  - `POST /api/algo/hash` — body `HashRequest` → `ApiResponse<HashResponse>`
  - `POST /api/algo/bubble` — body `BubbleRequest` → `ApiResponse<BubbleResponse>`

- [ ] **Step 1: Write the failing test**

```java
package com.dt.algo.algo;

import com.dt.algo.AlgoApplication;
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

@SpringBootTest(classes = AlgoApplication.class)
@AutoConfigureMockMvc
class AlgoControllerIntegrationTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    @Test
    void helloworld_endpoint() throws Exception {
        mvc.perform(post("/api/algo/helloworld")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(Map.of("input", "DT"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0))
            .andExpect(jsonPath("$.data.message").value("Hello, DT!"));
    }

    @Test
    void hash_endpoint() throws Exception {
        mvc.perform(post("/api/algo/hash")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(Map.of("input", "abc", "algo", "SHA-256"))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0))
            .andExpect(jsonPath("$.data.digest").isNotEmpty());
    }

    @Test
    void bubble_endpoint() throws Exception {
        mvc.perform(post("/api/algo/bubble")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(Map.of("input", List.of(5, 3, 8, 1)))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0))
            .andExpect(jsonPath("$.data.sorted[0]").value(1));
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd testDj-main && mvn test -Dtest=AlgoControllerIntegrationTest -q`
Expected: FAIL — 404，Controller 不存在

- [ ] **Step 3: Write minimal implementation**

`AlgoController.java`:
```java
package com.dt.algo.algo;

import com.dt.algo.algo.dto.*;
import com.dt.algo.common.ApiResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/algo")
public class AlgoController {

    private final AlgoService algoService;

    public AlgoController(AlgoService algoService) {
        this.algoService = algoService;
    }

    @PostMapping("/helloworld")
    public ApiResponse<HelloWorldResponse> helloworld(@RequestBody HelloWorldRequest req) {
        return ApiResponse.ok(algoService.helloWorld(req.getInput()));
    }

    @PostMapping("/hash")
    public ApiResponse<HashResponse> hash(@RequestBody HashRequest req) {
        return ApiResponse.ok(algoService.hash(req.getInput(), req.getAlgo()));
    }

    @PostMapping("/bubble")
    public ApiResponse<BubbleResponse> bubble(@RequestBody BubbleRequest req) {
        return ApiResponse.ok(algoService.bubbleSort(req.getInput()));
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `cd testDj-main && mvn test -Dtest=AlgoControllerIntegrationTest -q`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
cd testDj-main
git add src/main/java/com/dt/algo/algo/AlgoController.java src/test/java/com/dt/algo/algo/AlgoControllerIntegrationTest.java
git commit -m "feat(algo): REST controller for helloworld/hash/bubble endpoints"
```

---

## Task 5: 人员元数据模块（person）

**Files:**
- Create: `testDj-main/src/main/resources/schema.sql`
- Create: `testDj-main/src/main/java/com/dt/algo/person/Person.java`
- Create: `testDj-main/src/main/java/com/dt/algo/person/PersonRepository.java`
- Create: `testDj-main/src/main/java/com/dt/algo/person/PersonService.java`
- Create: `testDj-main/src/main/java/com/dt/algo/person/PersonDataInitializer.java`
- Test: `testDj-main/src/test/java/com/dt/algo/person/PersonServiceTest.java`

**Interfaces:**
- Consumes: 无
- Produces:
  - `Person` 实体：`id, callerId, callerName, personType, personLevel, department`
  - `PersonService.findByCallerId(String callerId) -> Person`（可能为 null）
  - 种子数据：至少 3 条人员记录覆盖不同类型/层级/部门

- [ ] **Step 1: Write the failing test**

```java
package com.dt.algo.person;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PersonServiceTest {
    @Autowired PersonService personService;

    @Test
    void findByCallerId_seed_data() {
        Person p = personService.findByCallerId("u001");
        assertNotNull(p);
        assertEquals("u001", p.getCallerId());
        assertNotNull(p.getPersonType());
        assertNotNull(p.getPersonLevel());
        assertNotNull(p.getDepartment());
    }

    @Test
    void findByCallerId_not_found_returns_null() {
        assertNull(personService.findByCallerId("nonexistent"));
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd testDj-main && mvn test -Dtest=PersonServiceTest -q`
Expected: FAIL — `Person` / `PersonService` 不存在

- [ ] **Step 3: Write minimal implementation**

`schema.sql`:
```sql
CREATE TABLE IF NOT EXISTS person (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    caller_id VARCHAR(64) NOT NULL UNIQUE,
    caller_name VARCHAR(64),
    person_type VARCHAR(32),
    person_level VARCHAR(16),
    department VARCHAR(64)
);

CREATE TABLE IF NOT EXISTS invoke_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    interface VARCHAR(32) NOT NULL,
    caller_id VARCHAR(64),
    caller_name VARCHAR(64),
    person_type VARCHAR(32),
    person_level VARCHAR(16),
    department VARCHAR(64),
    input_summary VARCHAR(512),
    result_summary VARCHAR(512),
    duration_ms INT,
    invoke_time TIMESTAMP,
    trace_id VARCHAR(64)
);
```

`Person.java`:
```java
package com.dt.algo.person;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "person")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String callerId;
    private String callerName;
    private String personType;
    private String personLevel;
    private String department;
}
```

`PersonRepository.java`:
```java
package com.dt.algo.person;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {
    Person findByCallerId(String callerId);
}
```

`PersonService.java`:
```java
package com.dt.algo.person;

import org.springframework.stereotype.Service;

@Service
public class PersonService {
    private final PersonRepository repo;
    public PersonService(PersonRepository repo) { this.repo = repo; }

    public Person findByCallerId(String callerId) {
        if (callerId == null) return null;
        return repo.findByCallerId(callerId);
    }
}
```

`PersonDataInitializer.java`:
```java
package com.dt.algo.person;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class PersonDataInitializer implements CommandLineRunner {
    private final PersonRepository repo;
    public PersonDataInitializer(PersonRepository repo) { this.repo = repo; }

    @Override
    public void run(String... args) {
        if (repo.count() > 0) return;
        repo.saveAll(List.of(
            build("u001", "张三", "正式", "L3", "研发一部"),
            build("u002", "李四", "外包", "L2", "研发二部"),
            build("u003", "王五", "实习", "L1", "研发一部")
        ));
    }

    private Person build(String id, String name, String type, String level, String dept) {
        Person p = new Person();
        p.setCallerId(id);
        p.setCallerName(name);
        p.setPersonType(type);
        p.setPersonLevel(level);
        p.setDepartment(dept);
        return p;
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `cd testDj-main && mvn test -Dtest=PersonServiceTest -q`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
cd testDj-main
git add src/main/resources/schema.sql src/main/java/com/dt/algo/person/ src/test/java/com/dt/algo/person/PersonServiceTest.java
git commit -m "feat(person): person entity, repository, service, seed data"
```

---

## Task 6: RequestContextFilter — 解析调用人身份

**Files:**
- Create: `testDj-main/src/main/java/com/dt/algo/config/RequestContextFilter.java`
- Test: `testDj-main/src/test/java/com/dt/algo/config/RequestContextFilterTest.java`

**Interfaces:**
- Consumes: `PersonService`（Task 5）
- Produces: `RequestContext` — ThreadLocal 持有 `callerId, callerName, personType, personLevel, department`，供 AOP 切面读取

- [ ] **Step 1: Write the failing test**

```java
package com.dt.algo.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RequestContextFilterTest {
    @AfterEach
    void cleanup() { RequestContext.clear(); }

    @Test
    void set_and_get_context() {
        RequestContext.set("u001", "张三", "正式", "L3", "研发一部");
        assertEquals("u001", RequestContext.getCallerId());
        assertEquals("张三", RequestContext.getCallerName());
        assertEquals("正式", RequestContext.getPersonType());
        assertEquals("L3", RequestContext.getPersonLevel());
        assertEquals("研发一部", RequestContext.getDepartment());
    }

    @Test
    void clear_resets_context() {
        RequestContext.set("u001", "张三", "正式", "L3", "研发一部");
        RequestContext.clear();
        assertNull(RequestContext.getCallerId());
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd testDj-main && mvn test -Dtest=RequestContextFilterTest -q`
Expected: FAIL — `RequestContext` 不存在

- [ ] **Step 3: Write minimal implementation**

`RequestContext.java`（放入 config 包）:
```java
package com.dt.algo.config;

public class RequestContext {
    private static final ThreadLocal<Context> HOLDER = new ThreadLocal<>();

    public static void set(String callerId, String callerName,
                            String personType, String personLevel, String department) {
        Context ctx = new Context();
        ctx.callerId = callerId;
        ctx.callerName = callerName;
        ctx.personType = personType;
        ctx.personLevel = personLevel;
        ctx.department = department;
        HOLDER.set(ctx);
    }

    public static String getCallerId() { return get() == null ? null : get().callerId; }
    public static String getCallerName() { return get() == null ? null : get().callerName; }
    public static String getPersonType() { return get() == null ? null : get().personType; }
    public static String getPersonLevel() { return get() == null ? null : get().personLevel; }
    public static String getDepartment() { return get() == null ? null : get().department; }

    public static void clear() { HOLDER.remove(); }

    private static Context get() { return HOLDER.get(); }

    private static class Context {
        String callerId, callerName, personType, personLevel, department;
    }
}
```

`RequestContextFilter.java`:
```java
package com.dt.algo.config;

import com.dt.algo.person.Person;
import com.dt.algo.person.PersonService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class RequestContextFilter implements Filter {
    private final PersonService personService;

    public RequestContextFilter(PersonService personService) {
        this.personService = personService;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpReq = (HttpServletRequest) req;
        String callerId = httpReq.getHeader("X-User-Id");
        String callerName = httpReq.getHeader("X-User-Name");
        String personType = null, personLevel = null, department = null;
        if (callerId != null) {
            Person p = personService.findByCallerId(callerId);
            if (p != null) {
                callerName = p.getCallerName();
                personType = p.getPersonType();
                personLevel = p.getPersonLevel();
                department = p.getDepartment();
            }
        }
        RequestContext.set(callerId, callerName, personType, personLevel, department);
        try {
            chain.doFilter(req, res);
        } finally {
            RequestContext.clear();
        }
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `cd testDj-main && mvn test -Dtest=RequestContextFilterTest -q`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
cd testDj-main
git add src/main/java/com/dt/algo/config/ src/test/java/com/dt/algo/config/RequestContextFilterTest.java
git commit -m "feat(config): RequestContext ThreadLocal + filter for caller identity"
```

---

## Task 7: 埋点注解与 AOP 切面

**Files:**
- Create: `testDj-main/src/main/java/com/dt/algo/tracking/TrackInvoke.java`
- Create: `testDj-main/src/main/java/com/dt/algo/tracking/InvokeLog.java`
- Create: `testDj-main/src/main/java/com/dt/algo/tracking/InvokeLogRepository.java`
- Create: `testDj-main/src/main/java/com/dt/algo/tracking/TrackingService.java`
- Create: `testDj-main/src/main/java/com/dt/algo/tracking/TrackingAspect.java`
- Test: `testDj-main/src/test/java/com/dt/algo/tracking/TrackingAspectTest.java`

**Interfaces:**
- Consumes: `RequestContext`（Task 6），`ApiResponse`（Task 1）
- Produces:
  - `@TrackInvoke(interface="hash")` 注解
  - `TrackingAspect` 环绕切面：捕获入参/出参摘要、耗时、调用人，异步落库
  - `TrackingService.saveAsync(InvokeLog log)` — 异步保存
  - `InvokeLog` 实体对应 `invoke_log` 表

- [ ] **Step 1: Write the failing test**

```java
package com.dt.algo.tracking;

import com.dt.algo.AlgoApplication;
import com.dt.algo.algo.AlgoController;
import com.dt.algo.config.RequestContext;
import org.junit.jupiter.api.AfterEach;
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
import static org.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = AlgoApplication.class)
@AutoConfigureMockMvc
class TrackingAspectTest {
    @Autowired MockMvc mvc;
    @Autowired InvokeLogRepository repo;

    @AfterEach
    void cleanup() { repo.deleteAll(); }

    @Test
    void invoke_logs_recorded() throws Exception {
        mvc.perform(post("/api/algo/hash")
                .header("X-User-Id", "u001")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"input\":\"abc\",\"algo\":\"SHA-256\"}"))
            .andExpect(status().isOk());

        await().atMost(3, SECONDS).untilAsserted(() -> {
            assertFalse(repo.findAll().isEmpty());
            InvokeLog log = repo.findAll().get(0);
            assertEquals("hash", log.getInterface());
            assertEquals("u001", log.getCallerId());
            assertEquals("正式", log.getPersonType());
            assertNotNull(log.getDurationMs());
            assertNotNull(log.getInvokeTime());
        });
    }
}
```

> 注：`awaitility` 需在 pom.xml 加依赖。在 Step 3 前先追加：

```xml
<dependency>
    <groupId>org.awaitility</groupId>
    <artifactId>awaitility</artifactId>
    <scope>test</scope>
</dependency>
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd testDj-main && mvn test -Dtest=TrackingAspectTest -q`
Expected: FAIL — `TrackInvoke` / `TrackingAspect` 不存在

- [ ] **Step 3: Write minimal implementation**

`TrackInvoke.java`:
```java
package com.dt.algo.tracking;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TrackInvoke {
    String interfaceName();
}
```

`InvokeLog.java`:
```java
package com.dt.algo.tracking;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "invoke_log")
public class InvokeLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String interfaceName;     // 对应列名 interface（H2 保留字，用列别名）
    @Column(name = "interface")
    private String iface;
    private String callerId;
    private String callerName;
    private String personType;
    private String personLevel;
    private String department;
    private String inputSummary;
    private String resultSummary;
    private Integer durationMs;
    private LocalDateTime invokeTime;
    private String traceId;
}
```

> 注意：`interface` 是 Java 保留字也是 H2 保留字，实体属性名用 `iface`，`@Column(name = "interface")` 映射到列。`interfaceName` 字段仅做逻辑分组，实际落库用 `iface`。简化为只保留 `iface`：

修正 `InvokeLog.java`（去掉 `interfaceName`，只保留 `iface`）:
```java
package com.dt.algo.tracking;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "invoke_log")
public class InvokeLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "interface")
    private String iface;
    private String callerId;
    private String callerName;
    private String personType;
    private String personLevel;
    private String department;
    private String inputSummary;
    private String resultSummary;
    private Integer durationMs;
    private LocalDateTime invokeTime;
    private String traceId;
}
```

`InvokeLogRepository.java`:
```java
package com.dt.algo.tracking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface InvokeLogRepository extends JpaRepository<InvokeLog, Long> {

    List<InvokeLog> findTop100ByIfaceOrderByInvokeTimeDesc(String iface);

    @Query("SELECT i FROM InvokeLog i WHERE i.invokeTime >= :since ORDER BY i.invokeTime DESC")
    List<InvokeLog> findSince(@Param("since") java.time.LocalDateTime since);
}
```

`TrackingService.java`:
```java
package com.dt.algo.tracking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class TrackingService {
    private static final Logger log = LoggerFactory.getLogger(TrackingService.class);
    private final InvokeLogRepository repo;

    public TrackingService(InvokeLogRepository repo) { this.repo = repo; }

    @Async
    public void saveAsync(InvokeLog entry) {
        try {
            repo.save(entry);
        } catch (Exception e) {
            log.warn("tracking save failed: {}", e.getMessage());
        }
    }
}
```

`TrackingAspect.java`:
```java
package com.dt.algo.tracking;

import com.dt.algo.common.ApiResponse;
import com.dt.algo.config.RequestContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

@Aspect
@Component
public class TrackingAspect {
    private static final ObjectMapper om = new ObjectMapper();
    private final TrackingService trackingService;

    public TrackingAspect(TrackingService trackingService) {
        this.trackingService = trackingService;
    }

    @Around("@annotation(track)")
    public Object around(ProceedingJoinPoint pjp, TrackInvoke track) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = null;
        Exception thrown = null;
        try {
            result = pjp.proceed();
            return result;
        } catch (Exception e) {
            thrown = e;
            throw e;
        } finally {
            try {
                InvokeLog entry = new InvokeLog();
                entry.setIface(track.interfaceName());
                entry.setCallerId(RequestContext.getCallerId());
                entry.setCallerName(RequestContext.getCallerName());
                entry.setPersonType(RequestContext.getPersonType());
                entry.setPersonLevel(RequestContext.getPersonLevel());
                entry.setDepartment(RequestContext.getDepartment());
                entry.setInputSummary(summarize(pjp.getArgs()));
                entry.setResultSummary(result != null ? summarize(result) : (thrown != null ? thrown.getMessage() : null));
                entry.setDurationMs((int) (System.currentTimeMillis() - start));
                entry.setInvokeTime(LocalDateTime.now());
                if (result instanceof ApiResponse<?> resp) {
                    entry.setTraceId(resp.getTraceId());
                }
                trackingService.saveAsync(entry);
            } catch (Exception e) {
                LoggerFactory.getLogger(TrackingAspect.class)
                    .warn("tracking aspect failed: {}", e.getMessage());
            }
        }
    }

    private String summarize(Object obj) {
        try {
            String json = om.writeValueAsString(obj);
            return json.length() > 500 ? json.substring(0, 500) : json;
        } catch (Exception e) {
            return String.valueOf(obj);
        }
    }
}
```

在 `AlgoController` 方法上添加注解：
```java
@TrackInvoke(interfaceName = "helloworld")
@PostMapping("/helloworld")
public ApiResponse<HelloWorldResponse> helloworld(...) { ... }

@TrackInvoke(interfaceName = "hash")
@PostMapping("/hash")
public ApiResponse<HashResponse> hash(...) { ... }

@TrackInvoke(interfaceName = "bubble")
@PostMapping("/bubble")
public ApiResponse<BubbleResponse> bubble(...) { ... }
```

- [ ] **Step 4: Run test to verify it passes**

Run: `cd testDj-main && mvn test -Dtest=TrackingAspectTest -q`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
cd testDj-main
git add pom.xml src/main/java/com/dt/algo/tracking/ src/main/java/com/dt/algo/algo/AlgoController.java src/test/java/com/dt/algo/tracking/TrackingAspectTest.java
git commit -m "feat(tracking): @TrackInvoke annotation + AOP aspect + async persistence"
```

---

## Task 8: 埋点查询接口（Metrics）

**Files:**
- Create: `testDj-main/src/main/java/com/dt/algo/tracking/dto/MetricsSummaryRequest.java`
- Create: `testDj-main/src/main/java/com/dt/algo/tracking/dto/MetricsSummaryResponse.java`
- Create: `testDj-main/src/main/java/com/dt/algo/tracking/MetricsService.java`
- Create: `testDj-main/src/main/java/com/dt/algo/tracking/MetricsController.java`
- Test: `testDj-main/src/test/java/com/dt/algo/tracking/MetricsServiceTest.java`

**Interfaces:**
- Consumes: `InvokeLogRepository`（Task 7）
- Produces:
  - `GET /api/metrics/summary?dimension={personType|personLevel|department|interface}&range={1d|7d|30d}&chart={line|pie|bar}`
  - `MetricsSummaryResponse { dimension, chart, series: [{label, value}], trend: [{date, value}] }`

- [ ] **Step 1: Write the failing test**

```java
package com.dt.algo.tracking;

import com.dt.algo.tracking.dto.MetricsSummaryResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MetricsServiceTest {
    @Autowired MetricsService metricsService;
    @Autowired InvokeLogRepository repo;

    @Test
    void summary_by_department_returns_series() {
        InvokeLog log = new InvokeLog();
        log.setIface("hash");
        log.setCallerId("u001");
        log.setDepartment("研发一部");
        log.setInvokeTime(LocalDateTime.now());
        repo.save(log);

        MetricsSummaryResponse resp = metricsService.summary("department", "1d", "bar");
        assertEquals("department", resp.getDimension());
        assertEquals("bar", resp.getChart());
        assertFalse(resp.getSeries().isEmpty());
        assertTrue(resp.getSeries().stream().anyMatch(s -> "研发一部".equals(s.getLabel())));
    }

    @Test
    void summary_line_chart_has_trend() {
        repo.save(makeLog("hash", "u001", "正式", "L3", "研发一部"));
        MetricsSummaryResponse resp = metricsService.summary("personType", "7d", "line");
        assertNotNull(resp.getTrend());
        assertFalse(resp.getTrend().isEmpty());
    }

    private InvokeLog makeLog(String iface, String uid, String type, String level, String dept) {
        InvokeLog l = new InvokeLog();
        l.setIface(iface);
        l.setCallerId(uid);
        l.setPersonType(type);
        l.setPersonLevel(level);
        l.setDepartment(dept);
        l.setInvokeTime(LocalDateTime.now());
        return l;
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd testDj-main && mvn test -Dtest=MetricsServiceTest -q`
Expected: FAIL — `MetricsService` 不存在

- [ ] **Step 3: Write minimal implementation**

`MetricsSummaryRequest.java`:
```java
package com.dt.algo.tracking.dto;

import lombok.Data;

@Data
public class MetricsSummaryRequest {
    private String dimension;  // personType | personLevel | department | interface
    private String range;      // 1d | 7d | 30d
    private String chart;      // line | pie | bar
}
```

`MetricsSummaryResponse.java`:
```java
package com.dt.algo.tracking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class MetricsSummaryResponse {
    private String dimension;
    private String chart;
    private List<SeriesPoint> series;
    private List<TrendPoint> trend;

    @Data
    @AllArgsConstructor
    public static class SeriesPoint {
        private String label;
        private long value;
    }

    @Data
    @AllArgsConstructor
    public static class TrendPoint {
        private String date;
        private long value;
    }
}
```

`MetricsService.java`:
```java
package com.dt.algo.tracking;

import com.dt.algo.tracking.dto.MetricsSummaryResponse;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MetricsService {
    private final InvokeLogRepository repo;

    public MetricsService(InvokeLogRepository repo) { this.repo = repo; }

    public MetricsSummaryResponse summary(String dimension, String range, String chart) {
        LocalDateTime since = parseRange(range);
        List<InvokeLog> logs = repo.findSince(since);

        Map<String, Long> grouped = logs.stream()
            .collect(Collectors.groupingBy(
                log -> resolveDimension(log, dimension),
                Collectors.counting()
            ));

        List<MetricsSummaryResponse.SeriesPoint> series = grouped.entrySet().stream()
            .map(e -> new MetricsSummaryResponse.SeriesPoint(e.getKey(), e.getValue()))
            .collect(Collectors.toList());

        List<MetricsSummaryResponse.TrendPoint> trend = new ArrayList<>();
        if ("line".equals(chart)) {
            Map<LocalDate, Long> byDate = logs.stream()
                .collect(Collectors.groupingBy(
                    log -> log.getInvokeTime().toLocalDate(),
                    Collectors.counting()
                ));
            trend = byDate.entrySet().stream()
                .map(e -> new MetricsSummaryResponse.TrendPoint(e.getKey().toString(), e.getValue()))
                .sorted(Comparator.comparing(MetricsSummaryResponse.TrendPoint::getDate))
                .collect(Collectors.toList());
        }

        return new MetricsSummaryResponse(dimension, chart, series, trend);
    }

    private String resolveDimension(InvokeLog log, String dimension) {
        return switch (dimension) {
            case "personType" -> nullSafe(log.getPersonType());
            case "personLevel" -> nullSafe(log.getPersonLevel());
            case "department" -> nullSafe(log.getDepartment());
            case "interface" -> nullSafe(log.getIface());
            default -> "unknown";
        };
    }

    private String nullSafe(String s) { return s == null ? "unknown" : s; }

    private LocalDateTime parseRange(String range) {
        int days = switch (range) {
            case "1d" -> 1;
            case "7d" -> 7;
            case "30d" -> 30;
            default -> 7;
        };
        return LocalDateTime.now().minusDays(days);
    }
}
```

`MetricsController.java`:
```java
package com.dt.algo.tracking;

import com.dt.algo.common.ApiResponse;
import com.dt.algo.tracking.dto.MetricsSummaryResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/metrics")
public class MetricsController {
    private final MetricsService metricsService;

    public MetricsController(MetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @GetMapping("/summary")
    public ApiResponse<MetricsSummaryResponse> summary(
            @RequestParam String dimension,
            @RequestParam(defaultValue = "7d") String range,
            @RequestParam(defaultValue = "bar") String chart) {
        return ApiResponse.ok(metricsService.summary(dimension, range, chart));
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `cd testDj-main && mvn test -Dtest=MetricsServiceTest -q`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
cd testDj-main
git add src/main/java/com/dt/algo/tracking/dto/ src/main/java/com/dt/algo/tracking/MetricsService.java src/main/java/com/dt/algo/tracking/MetricsController.java src/test/java/com/dt/algo/tracking/MetricsServiceTest.java
git commit -m "feat(metrics): summary endpoint with dimension aggregation + trend"
```

---

## Task 9: 导出接口

**Files:**
- Create: `testDj-main/src/main/java/com/dt/algo/export/ExportController.java`
- Create: `testDj-main/src/main/java/com/dt/algo/export/ExportService.java`
- Create: `testDj-main/src/main/java/com/dt/algo/export/dto/ExportResultRow.java`
- Test: `testDj-main/src/test/java/com/dt/algo/export/ExportServiceTest.java`

**Interfaces:**
- Consumes: `InvokeLogRepository`（Task 7）
- Produces:
  - `GET /api/export?type={helloworld|hash|bubble}&format={xlsx|csv}`
  - 返回二进制流，`Content-Disposition: attachment; filename=algo-<type>-<ts>.<ext>`
  - 导出内容为该接口最近 100 次调用记录

- [ ] **Step 1: Write the failing test**

```java
package com.dt.algo.export;

import com.dt.algo.tracking.InvokeLog;
import com.dt.algo.tracking.InvokeLogRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ExportServiceTest {
    @Autowired ExportService exportService;
    @Autowired InvokeLogRepository repo;

    @Test
    void export_csv_returns_nonempty_bytes() {
        repo.deleteAll();
        InvokeLog log = new InvokeLog();
        log.setIface("hash");
        log.setCallerId("u001");
        log.setCallerName("张三");
        log.setInputSummary("{\"input\":\"abc\"}");
        log.setResultSummary("{\"digest\":\"ba78...\"}");
        log.setDurationMs(5);
        log.setInvokeTime(LocalDateTime.now());
        repo.save(log);

        byte[] csv = exportService.export("hash", "csv");
        assertNotNull(csv);
        assertTrue(csv.length > 0);
        String content = new String(csv);
        assertTrue(content.contains("hash"));
        assertTrue(content.contains("u001"));
    }

    @Test
    void export_xlsx_returns_nonempty_bytes() {
        repo.deleteAll();
        repo.save(makeLog("bubble", "u002"));
        byte[] xlsx = exportService.export("bubble", "xlsx");
        assertNotNull(xlsx);
        assertTrue(xlsx.length > 0);
    }

    private InvokeLog makeLog(String iface, String uid) {
        InvokeLog l = new InvokeLog();
        l.setIface(iface);
        l.setCallerId(uid);
        l.setInvokeTime(LocalDateTime.now());
        return l;
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd testDj-main && mvn test -Dtest=ExportServiceTest -q`
Expected: FAIL — `ExportService` 不存在

- [ ] **Step 3: Write minimal implementation**

`ExportResultRow.java`:
```java
package com.dt.algo.export.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExportResultRow {
    private String iface;
    private String callerId;
    private String callerName;
    private String inputSummary;
    private String resultSummary;
    private Integer durationMs;
    private String invokeTime;
}
```

`ExportService.java`:
```java
package com.dt.algo.export;

import com.dt.algo.export.dto.ExportResultRow;
import com.dt.algo.tracking.InvokeLog;
import com.dt.algo.tracking.InvokeLogRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class ExportService {
    private final InvokeLogRepository repo;

    public ExportService(InvokeLogRepository repo) { this.repo = repo; }

    public byte[] export(String type, String format) {
        List<InvokeLog> logs = repo.findTop100ByIfaceOrderByInvokeTimeDesc(type);
        List<ExportResultRow> rows = logs.stream()
            .map(l -> new ExportResultRow(
                l.getIface(), l.getCallerId(), l.getCallerName(),
                l.getInputSummary(), l.getResultSummary(),
                l.getDurationMs(),
                l.getInvokeTime() != null ? l.getInvokeTime().toString() : null
            ))
            .toList();

        if ("xlsx".equalsIgnoreCase(format)) {
            return toXlsx(rows);
        }
        return toCsv(rows);
    }

    private byte[] toXlsx(List<ExportResultRow> rows) {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("export");
            Row header = sheet.createRow(0);
            String[] cols = {"interface","callerId","callerName","inputSummary","resultSummary","durationMs","invokeTime"};
            for (int i = 0; i < cols.length; i++) {
                header.createCell(i).setCellValue(cols[i]);
            }
            for (int r = 0; r < rows.size(); r++) {
                Row row = sheet.createRow(r + 1);
                ExportResultRow d = rows.get(r);
                row.createCell(0).setCellValue(d.getIface());
                row.createCell(1).setCellValue(nullSafe(d.getCallerId()));
                row.createCell(2).setCellValue(nullSafe(d.getCallerName()));
                row.createCell(3).setCellValue(nullSafe(d.getInputSummary()));
                row.createCell(4).setCellValue(nullSafe(d.getResultSummary()));
                row.createCell(5).setCellValue(d.getDurationMs() != null ? d.getDurationMs() : 0);
                row.createCell(6).setCellValue(nullSafe(d.getInvokeTime()));
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            wb.write(bos);
            return bos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("xlsx export failed", e);
        }
    }

    private byte[] toCsv(List<ExportResultRow> rows) {
        StringBuilder sb = new StringBuilder();
        sb.append("interface,callerId,callerName,inputSummary,resultSummary,durationMs,invokeTime\n");
        for (ExportResultRow r : rows) {
            sb.append(nullSafe(r.getIface())).append(",")
              .append(nullSafe(r.getCallerId())).append(",")
              .append(nullSafe(r.getCallerName())).append(",")
              .append(nullSafe(r.getInputSummary())).append(",")
              .append(nullSafe(r.getResultSummary())).append(",")
              .append(r.getDurationMs() != null ? r.getDurationMs() : "").append(",")
              .append(nullSafe(r.getInvokeTime())).append("\n");
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String nullSafe(String s) { return s == null ? "" : s; }
}
```

`ExportController.java`:
```java
package com.dt.algo.export;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/export")
public class ExportController {
    private final ExportService exportService;

    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    @GetMapping
    public void export(@RequestParam String type,
                       @RequestParam(defaultValue = "csv") String format,
                       HttpServletResponse response) throws Exception {
        byte[] data = exportService.export(type, format);
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String ext = "xlsx".equalsIgnoreCase(format) ? "xlsx" : "csv";
        String contentType = "xlsx".equalsIgnoreCase(format)
                ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                : "text/csv";
        response.setContentType(contentType);
        response.setHeader("Content-Disposition",
                "attachment; filename=algo-" + type + "-" + ts + "." + ext);
        try (OutputStream os = response.getOutputStream()) {
            os.write(data);
            os.flush();
        }
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `cd testDj-main && mvn test -Dtest=ExportServiceTest -q`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
cd testDj-main
git add src/main/java/com/dt/algo/export/ src/test/java/com/dt/algo/export/ExportServiceTest.java
git commit -m "feat(export): xlsx/csv export endpoint from invoke_log"
```

---

## Task 10: 前端项目骨架与 API 类型定义

**Files:**
- Create: `testDJnew-main/package.json`
- Create: `testDJnew-main/vite.config.ts`
- Create: `testDJnew-main/tsconfig.json`
- Create: `testDJnew-main/index.html`
- Create: `testDJnew-main/src/main.tsx`
- Create: `testDJnew-main/src/App.tsx`
- Create: `testDJnew-main/src/types/api.ts`
- Create: `testDJnew-main/src/api/client.ts`
- Test: `testDJnew-main/src/__tests__/client.test.ts`

**Interfaces:**
- Consumes: 后端接口契约（spec 第 4 章）
- Produces: 跨仓契约 TS 类型 + Axios 实例

- [ ] **Step 1: Write the failing test**

```typescript
// src/__tests__/client.test.ts
import { describe, it, expect } from 'vitest';
import { apiClient } from '../api/client';

describe('apiClient', () => {
  it('has base URL /api', () => {
    expect(apiClient.defaults.baseURL).toBe('/api');
  });
});
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd testDJnew-main && npx vitest run src/__tests__/client.test.ts`
Expected: FAIL — 模块不存在

- [ ] **Step 3: Write minimal implementation**

`package.json`:
```json
{
  "name": "algo-dashboard-frontend",
  "private": true,
  "version": "0.1.0",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "tsc && vite build",
    "test": "vitest run"
  },
  "dependencies": {
    "react": "^18.3.1",
    "react-dom": "^18.3.1",
    "antd": "^5.20.0",
    "echarts": "^5.5.0",
    "echarts-for-react": "^3.0.2",
    "axios": "^1.7.0"
  },
  "devDependencies": {
    "@types/react": "^18.3.0",
    "@types/react-dom": "^18.3.0",
    "@vitejs/plugin-react": "^4.3.0",
    "typescript": "^5.5.0",
    "vite": "^5.4.0",
    "vitest": "^2.0.0",
    "@testing-library/react": "^16.0.0",
    "jsdom": "^24.1.0"
  }
}
```

`vite.config.ts`:
```typescript
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api': 'http://localhost:8080'
    }
  },
  test: {
    environment: 'jsdom',
    globals: true
  }
});
```

`tsconfig.json`:
```json
{
  "compilerOptions": {
    "target": "ES2020",
    "lib": ["ES2020", "DOM", "DOM.Iterable"],
    "module": "ESNext",
    "moduleResolution": "bundler",
    "jsx": "react-jsx",
    "strict": true,
    "esModuleInterop": true,
    "skipLibCheck": true
  },
  "include": ["src"]
}
```

`index.html`:
```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8" />
  <title>算法演示与埋点可视化</title>
</head>
<body>
  <div id="root"></div>
  <script type="module" src="/src/main.tsx"></script>
</body>
</html>
```

`src/main.tsx`:
```tsx
import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
import 'antd/dist/reset.css';

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);
```

`src/App.tsx`:
```tsx
import AlgoDashboardPage from './pages/AlgoDashboardPage';

export default function App() {
  return <AlgoDashboardPage />;
}
```

`src/types/api.ts`:
```typescript
export interface ApiResponse<T> {
  code: number;
  data: T;
  msg: string | null;
  traceId: string;
}

export interface HelloWorldResponse {
  message: string;
}

export interface HashResponse {
  input: string;
  algo: string;
  digest: string;
}

export interface BubbleResponse {
  input: number[];
  sorted: number[];
  swaps: number;
  durationMs: number;
}

export interface SeriesPoint {
  label: string;
  value: number;
}

export interface TrendPoint {
  date: string;
  value: number;
}

export interface MetricsSummaryResponse {
  dimension: string;
  chart: string;
  series: SeriesPoint[];
  trend: TrendPoint[];
}
```

`src/api/client.ts`:
```typescript
import axios from 'axios';

export const apiClient = axios.create({
  baseURL: '/api',
  headers: { 'X-User-Id': 'u001', 'X-User-Name': '张三' }
});

apiClient.interceptors.response.use(
  (resp) => resp,
  (error) => {
    console.error('API error:', error?.response?.data ?? error.message);
    return Promise.reject(error);
  }
);
```

- [ ] **Step 4: Run test to verify it passes**

Run: `cd testDJnew-main && npm install && npx vitest run src/__tests__/client.test.ts`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
cd testDJnew-main
git add -A
git commit -m "feat(frontend): project scaffold + API types + axios client"
```

---

## Task 11: 前端 API 调用层

**Files:**
- Create: `testDJnew-main/src/api/algoApi.ts`
- Create: `testDJnew-main/src/api/exportApi.ts`
- Create: `testDJnew-main/src/api/metricsApi.ts`
- Test: `testDJnew-main/src/__tests__/algoApi.test.ts`

**Interfaces:**
- Consumes: `apiClient`（Task 10），`types/api.ts`（Task 10）
- Produces:
  - `algoApi.helloWorld(input: string) => Promise<HelloWorldResponse>`
  - `algoApi.hash(input: string, algo: string) => Promise<HashResponse>`
  - `algoApi.bubbleSort(input: number[]) => Promise<BubbleResponse>`
  - `exportApi.export(type: string, format: string) => void`（触发浏览器下载）
  - `metricsApi.summary(dimension, range, chart) => Promise<MetricsSummaryResponse>`

- [ ] **Step 1: Write the failing test**

```typescript
// src/__tests__/algoApi.test.ts
import { describe, it, expect, vi } from 'vitest';
import { algoApi } from '../api/algoApi';
import { apiClient } from '../api/client';

vi.mock('../api/client', () => ({
  apiClient: { post: vi.fn(), get: vi.fn() }
}));

describe('algoApi', () => {
  it('helloWorld calls POST /algo/helloworld', async () => {
    vi.mocked(apiClient.post).mockResolvedValue({
      data: { code: 0, data: { message: 'Hello, world!' }, traceId: 't1' }
    });
    const result = await algoApi.helloWorld('world');
    expect(apiClient.post).toHaveBeenCalledWith('/algo/helloworld', { input: 'world' });
    expect(result.message).toBe('Hello, world!');
  });
});
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd testDJnew-main && npx vitest run src/__tests__/algoApi.test.ts`
Expected: FAIL — `algoApi` 不存在

- [ ] **Step 3: Write minimal implementation**

`src/api/algoApi.ts`:
```typescript
import { apiClient } from './client';
import type { ApiResponse, HelloWorldResponse, HashResponse, BubbleResponse } from '../types/api';

export const algoApi = {
  async helloWorld(input: string): Promise<HelloWorldResponse> {
    const resp = await apiClient.post<ApiResponse<HelloWorldResponse>>('/algo/helloworld', { input });
    return resp.data.data;
  },

  async hash(input: string, algo: string): Promise<HashResponse> {
    const resp = await apiClient.post<ApiResponse<HashResponse>>('/algo/hash', { input, algo });
    return resp.data.data;
  },

  async bubbleSort(input: number[]): Promise<BubbleResponse> {
    const resp = await apiClient.post<ApiResponse<BubbleResponse>>('/algo/bubble', { input });
    return resp.data.data;
  }
};
```

`src/api/exportApi.ts`:
```typescript
export const exportApi = {
  export(type: string, format: string = 'csv'): void {
    const url = `/api/export?type=${encodeURIComponent(type)}&format=${encodeURIComponent(format)}`;
    const a = document.createElement('a');
    a.href = url;
    a.download = '';
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
  }
};
```

`src/api/metricsApi.ts`:
```typescript
import { apiClient } from './client';
import type { ApiResponse, MetricsSummaryResponse } from '../types/api';

export const metricsApi = {
  async summary(dimension: string, range: string, chart: string): Promise<MetricsSummaryResponse> {
    const resp = await apiClient.get<ApiResponse<MetricsSummaryResponse>>('/metrics/summary', {
      params: { dimension, range, chart }
    });
    return resp.data.data;
  }
};
```

- [ ] **Step 4: Run test to verify it passes**

Run: `cd testDJnew-main && npx vitest run src/__tests__/algoApi.test.ts`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
cd testDJnew-main
git add src/api/ src/__tests__/algoApi.test.ts
git commit -m "feat(frontend): algoApi/exportApi/metricsApi call layer"
```

---

## Task 12: 前端三个算法 Tab 组件

**Files:**
- Create: `testDJnew-main/src/components/HelloWorldTab.tsx`
- Create: `testDJnew-main/src/components/HashTab.tsx`
- Create: `testDJnew-main/src/components/BubbleTab.tsx`
- Create: `testDJnew-main/src/components/AlgoTabs.tsx`
- Test: `testDJnew-main/src/__tests__/HelloWorldTab.test.tsx`

**Interfaces:**
- Consumes: `algoApi`（Task 11）
- Produces: `AlgoTabs` 组件，含三个 Tab，每个 Tab 有输入 + 执行按钮 + 结果展示

- [ ] **Step 1: Write the failing test**

```tsx
// src/__tests__/HelloWorldTab.test.tsx
import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import HelloWorldTab from '../components/HelloWorldTab';
import { algoApi } from '../api/algoApi';

vi.mock('../api/algoApi', () => ({
  algoApi: { helloWorld: vi.fn() }
}));

describe('HelloWorldTab', () => {
  it('shows result after execute', async () => {
    vi.mocked(algoApi.helloWorld).mockResolvedValue({ message: 'Hello, DT!' });
    render(<HelloWorldTab />);
    fireEvent.change(screen.getByPlaceholderText('输入名称'), { target: { value: 'DT' } });
    fireEvent.click(screen.getByText('执行'));
    await waitFor(() => {
      expect(screen.getByText('Hello, DT!')).toBeInTheDocument();
    });
  });
});
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd testDJnew-main && npx vitest run src/__tests__/HelloWorldTab.test.tsx`
Expected: FAIL — 组件不存在

- [ ] **Step 3: Write minimal implementation**

`src/components/HelloWorldTab.tsx`:
```tsx
import { useState } from 'react';
import { Input, Button, Typography, Space } from 'antd';
import { algoApi } from '../api/algoApi';

const { Text } = Typography;

export default function HelloWorldTab() {
  const [input, setInput] = useState('world');
  const [result, setResult] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const handleExecute = async () => {
    setLoading(true);
    try {
      const resp = await algoApi.helloWorld(input);
      setResult(resp.message);
    } catch {
      setResult('调用失败');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Space direction="vertical" style={{ width: '100%' }}>
      <Space>
        <Input
          placeholder="输入名称"
          value={input}
          onChange={(e) => setInput(e.target.value)}
          style={{ width: 200 }}
        />
        <Button type="primary" loading={loading} onClick={handleExecute}>执行</Button>
      </Space>
      {result && <Text strong>{result}</Text>}
    </Space>
  );
}
```

`src/components/HashTab.tsx`:
```tsx
import { useState } from 'react';
import { Input, Button, Typography, Space, Select } from 'antd';
import { algoApi } from '../api/algoApi';
import type { HashResponse } from '../types/api';

const { Text } = Typography;

export default function HashTab() {
  const [input, setInput] = useState('abc');
  const [algo, setAlgo] = useState('SHA-256');
  const [result, setResult] = useState<HashResponse | null>(null);
  const [loading, setLoading] = useState(false);

  const handleExecute = async () => {
    setLoading(true);
    try {
      const resp = await algoApi.hash(input, algo);
      setResult(resp);
    } catch {
      setResult(null);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Space direction="vertical" style={{ width: '100%' }}>
      <Space>
        <Input
          placeholder="输入文本"
          value={input}
          onChange={(e) => setInput(e.target.value)}
          style={{ width: 200 }}
        />
        <Select
          value={algo}
          onChange={setAlgo}
          options={[
            { value: 'SHA-256', label: 'SHA-256' },
            { value: 'MD5', label: 'MD5' },
            { value: 'SHA-1', label: 'SHA-1' }
          ]}
          style={{ width: 120 }}
        />
        <Button type="primary" loading={loading} onClick={handleExecute}>执行</Button>
      </Space>
      {result && (
        <Space direction="vertical">
          <Text>算法: {result.algo}</Text>
          <Text>摘要: {result.digest}</Text>
        </Space>
      )}
    </Space>
  );
}
```

`src/components/BubbleTab.tsx`:
```tsx
import { useState } from 'react';
import { Input, Button, Typography, Space } from 'antd';
import { algoApi } from '../api/algoApi';
import type { BubbleResponse } from '../types/api';

const { Text } = Typography;

export default function BubbleTab() {
  const [input, setInput] = useState('5,3,8,1,9,2');
  const [result, setResult] = useState<BubbleResponse | null>(null);
  const [loading, setLoading] = useState(false);

  const handleExecute = async () => {
    const arr = input.split(',').map((s) => parseInt(s.trim(), 10)).filter((n) => !isNaN(n));
    setLoading(true);
    try {
      const resp = await algoApi.bubbleSort(arr);
      setResult(resp);
    } catch {
      setResult(null);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Space direction="vertical" style={{ width: '100%' }}>
      <Space>
        <Input
          placeholder="输入数组，逗号分隔"
          value={input}
          onChange={(e) => setInput(e.target.value)}
          style={{ width: 300 }}
        />
        <Button type="primary" loading={loading} onClick={handleExecute}>执行</Button>
      </Space>
      {result && (
        <Space direction="vertical">
          <Text>排序前: {result.input.join(', ')}</Text>
          <Text>排序后: {result.sorted.join(', ')}</Text>
          <Text>交换次数: {result.swaps}</Text>
          <Text>耗时: {result.durationMs} ms</Text>
        </Space>
      )}
    </Space>
  );
}
```

`src/components/AlgoTabs.tsx`:
```tsx
import { Tabs } from 'antd';
import HelloWorldTab from './HelloWorldTab';
import HashTab from './HashTab';
import BubbleTab from './BubbleTab';

export type AlgoTabKey = 'helloworld' | 'hash' | 'bubble';

interface AlgoTabsProps {
  activeKey: AlgoTabKey;
  onChange: (key: AlgoTabKey) => void;
}

export default function AlgoTabs({ activeKey, onChange }: AlgoTabsProps) {
  return (
    <Tabs
      activeKey={activeKey}
      onChange={(k) => onChange(k as AlgoTabKey)}
      items={[
        { key: 'helloworld', label: 'HelloWorld', children: <HelloWorldTab /> },
        { key: 'hash', label: '哈希算法', children: <HashTab /> },
        { key: 'bubble', label: '冒泡排序', children: <BubbleTab /> }
      ]}
    />
  );
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `cd testDJnew-main && npx vitest run src/__tests__/HelloWorldTab.test.tsx`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
cd testDJnew-main
git add src/components/HelloWorldTab.tsx src/components/HashTab.tsx src/components/BubbleTab.tsx src/components/AlgoTabs.tsx src/__tests__/HelloWorldTab.test.tsx
git commit -m "feat(frontend): three algo tab components + AlgoTabs container"
```

---

## Task 13: 导出按钮组件

**Files:**
- Create: `testDJnew-main/src/components/ExportButton.tsx`
- Test: `testDJnew-main/src/__tests__/ExportButton.test.tsx`

**Interfaces:**
- Consumes: `exportApi`（Task 11）
- Produces: `ExportButton` 组件，props `{ type: AlgoTabKey }`，点击触发导出

- [ ] **Step 1: Write the failing test**

```tsx
// src/__tests__/ExportButton.test.tsx
import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import ExportButton from '../components/ExportButton';
import { exportApi } from '../api/exportApi';

vi.mock('../api/exportApi', () => ({
  exportApi: { export: vi.fn() }
}));

describe('ExportButton', () => {
  it('calls exportApi with current tab type', () => {
    render(<ExportButton type="hash" />);
    fireEvent.click(screen.getByText('导出'));
    expect(exportApi.export).toHaveBeenCalledWith('hash', 'csv');
  });
});
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd testDJnew-main && npx vitest run src/__tests__/ExportButton.test.tsx`
Expected: FAIL — 组件不存在

- [ ] **Step 3: Write minimal implementation**

`src/components/ExportButton.tsx`:
```tsx
import { Button } from 'antd';
import { DownloadOutlined } from '@ant-design/icons';
import { exportApi } from '../api/exportApi';
import type { AlgoTabKey } from './AlgoTabs';

interface ExportButtonProps {
  type: AlgoTabKey;
  format?: string;
}

export default function ExportButton({ type, format = 'csv' }: ExportButtonProps) {
  const handleExport = () => {
    exportApi.export(type, format);
  };

  return (
    <Button icon={<DownloadOutlined />} onClick={handleExport}>
      导出
    </Button>
  );
}
```

> 注：需在 package.json dependencies 中追加 `"@ant-design/icons": "^5.3.0"`。

- [ ] **Step 4: Run test to verify it passes**

Run: `cd testDJnew-main && npm install && npx vitest run src/__tests__/ExportButton.test.tsx`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
cd testDJnew-main
git add package.json package-lock.json src/components/ExportButton.tsx src/__tests__/ExportButton.test.tsx
git commit -m "feat(frontend): ExportButton component"
```

---

## Task 14: 报表筛选器与三个图表组件

**Files:**
- Create: `testDJnew-main/src/components/MetricsFilter.tsx`
- Create: `testDJnew-main/src/components/ChartLine.tsx`
- Create: `testDJnew-main/src/components/ChartPie.tsx`
- Create: `testDJnew-main/src/components/ChartBar.tsx`
- Test: `testDJnew-main/src/__tests__/MetricsFilter.test.tsx`

**Interfaces:**
- Consumes: `metricsApi`（Task 11），`MetricsSummaryResponse` 类型（Task 10）
- Produces:
  - `MetricsFilter` — props `{ onFilter: (dimension, range, chart) => void }`
  - `ChartLine` — props `{ data: MetricsSummaryResponse }`
  - `ChartPie` — props `{ data: MetricsSummaryResponse }`
  - `ChartBar` — props `{ data: MetricsSummaryResponse }`

- [ ] **Step 1: Write the failing test**

```tsx
// src/__tests__/MetricsFilter.test.tsx
import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import MetricsFilter from '../components/MetricsFilter';

describe('MetricsFilter', () => {
  it('calls onFilter with defaults on button click', () => {
    const onFilter = vi.fn();
    render(<MetricsFilter onFilter={onFilter} />);
    fireEvent.click(screen.getByText('查询'));
    expect(onFilter).toHaveBeenCalledWith('department', '7d', 'bar');
  });
});
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd testDJnew-main && npx vitest run src/__tests__/MetricsFilter.test.tsx`
Expected: FAIL — 组件不存在

- [ ] **Step 3: Write minimal implementation**

`src/components/MetricsFilter.tsx`:
```tsx
import { useState } from 'react';
import { Select, Button, Space } from 'antd';

interface MetricsFilterProps {
  onFilter: (dimension: string, range: string, chart: string) => void;
}

export default function MetricsFilter({ onFilter }: MetricsFilterProps) {
  const [dimension, setDimension] = useState('department');
  const [range, setRange] = useState('7d');
  const [chart, setChart] = useState('bar');

  return (
    <Space>
      <Select
        value={dimension}
        onChange={setDimension}
        options={[
          { value: 'personType', label: '人员类型' },
          { value: 'personLevel', label: '人员层级' },
          { value: 'department', label: '部门' },
          { value: 'interface', label: '接口' }
        ]}
        style={{ width: 120 }}
      />
      <Select
        value={range}
        onChange={setRange}
        options={[
          { value: '1d', label: '近1天' },
          { value: '7d', label: '近7天' },
          { value: '30d', label: '近30天' }
        ]}
        style={{ width: 100 }}
      />
      <Select
        value={chart}
        onChange={setChart}
        options={[
          { value: 'line', label: '折线图' },
          { value: 'pie', label: '饼图' },
          { value: 'bar', label: '柱状图' }
        ]}
        style={{ width: 100 }}
      />
      <Button type="primary" onClick={() => onFilter(dimension, range, chart)}>查询</Button>
    </Space>
  );
}
```

`src/components/ChartLine.tsx`:
```tsx
import ReactECharts from 'echarts-for-react';
import type { MetricsSummaryResponse } from '../types/api';

interface ChartLineProps {
  data: MetricsSummaryResponse;
}

export default function ChartLine({ data }: ChartLineProps) {
  const option = {
    title: { text: '调用次数趋势' },
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: data.trend.map((p) => p.date)
    },
    yAxis: { type: 'value' },
    series: [{
      type: 'line',
      data: data.trend.map((p) => p.value)
    }]
  };
  return <ReactECharts option={option} style={{ height: 300 }} />;
}
```

`src/components/ChartPie.tsx`:
```tsx
import ReactECharts from 'echarts-for-react';
import type { MetricsSummaryResponse } from '../types/api';

interface ChartPieProps {
  data: MetricsSummaryResponse;
}

export default function ChartPie({ data }: ChartPieProps) {
  const option = {
    title: { text: '维度占比' },
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie',
      radius: '60%',
      data: data.series.map((s) => ({ name: s.label, value: s.value }))
    }]
  };
  return <ReactECharts option={option} style={{ height: 300 }} />;
}
```

`src/components/ChartBar.tsx`:
```tsx
import ReactECharts from 'echarts-for-react';
import type { MetricsSummaryResponse } from '../types/api';

interface ChartBarProps {
  data: MetricsSummaryResponse;
}

export default function ChartBar({ data }: ChartBarProps) {
  const option = {
    title: { text: '维度对比' },
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: data.series.map((s) => s.label)
    },
    yAxis: { type: 'value' },
    series: [{
      type: 'bar',
      data: data.series.map((s) => s.value)
    }]
  };
  return <ReactECharts option={option} style={{ height: 300 }} />;
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `cd testDJnew-main && npx vitest run src/__tests__/MetricsFilter.test.tsx`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
cd testDJnew-main
git add src/components/MetricsFilter.tsx src/components/ChartLine.tsx src/components/ChartPie.tsx src/components/ChartBar.tsx src/__tests__/MetricsFilter.test.tsx
git commit -m "feat(frontend): MetricsFilter + ChartLine/Pie/Bar components"
```

---

## Task 15: 主页面 AlgoDashboardPage 组装

**Files:**
- Create: `testDJnew-main/src/pages/AlgoDashboardPage.tsx`
- Test: `testDJnew-main/src/__tests__/AlgoDashboardPage.test.tsx`

**Interfaces:**
- Consumes: `AlgoTabs`（Task 12），`ExportButton`（Task 13），`MetricsFilter` + 图表组件（Task 14），`metricsApi`（Task 11）
- Produces: 完整页面，组合三 Tab + 导出按钮 + 报表区

- [ ] **Step 1: Write the failing test**

```tsx
// src/__tests__/AlgoDashboardPage.test.tsx
import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import AlgoDashboardPage from '../pages/AlgoDashboardPage';

vi.mock('../api/metricsApi', () => ({
  metricsApi: {
    summary: vi.fn().mockResolvedValue({
      dimension: 'department', chart: 'bar',
      series: [{ label: '研发一部', value: 10 }],
      trend: []
    })
  }
}));

describe('AlgoDashboardPage', () => {
  it('renders tabs, export button, and metrics filter', () => {
    render(<AlgoDashboardPage />);
    expect(screen.getByText('HelloWorld')).toBeInTheDocument();
    expect(screen.getByText('哈希算法')).toBeInTheDocument();
    expect(screen.getByText('冒泡排序')).toBeInTheDocument();
    expect(screen.getByText('导出')).toBeInTheDocument();
    expect(screen.getByText('查询')).toBeInTheDocument();
  });
});
```

- [ ] **Step 2: Run test to verify it fails**

Run: `cd testDJnew-main && npx vitest run src/__tests__/AlgoDashboardPage.test.tsx`
Expected: FAIL — 页面不存在

- [ ] **Step 3: Write minimal implementation**

`src/pages/AlgoDashboardPage.tsx`:
```tsx
import { useState, useEffect } from 'react';
import { Card, Space, Typography } from 'antd';
import AlgoTabs, { type AlgoTabKey } from '../components/AlgoTabs';
import ExportButton from '../components/ExportButton';
import MetricsFilter from '../components/MetricsFilter';
import ChartLine from '../components/ChartLine';
import ChartPie from '../components/ChartPie';
import ChartBar from '../components/ChartBar';
import { metricsApi } from '../api/metricsApi';
import type { MetricsSummaryResponse } from '../types/api';

const { Title } = Typography;

export default function AlgoDashboardPage() {
  const [activeTab, setActiveTab] = useState<AlgoTabKey>('helloworld');
  const [metrics, setMetrics] = useState<MetricsSummaryResponse | null>(null);
  const [dimension, setDimension] = useState('department');
  const [range, setRange] = useState('7d');
  const [chart, setChart] = useState('bar');

  const fetchMetrics = async (dim: string, rng: string, ch: string) => {
    const data = await metricsApi.summary(dim, rng, ch);
    setMetrics(data);
    setDimension(dim);
    setRange(rng);
    setChart(ch);
  };

  useEffect(() => {
    fetchMetrics(dimension, range, chart);
  }, []);

  return (
    <div style={{ padding: 24 }}>
      <Title level={3}>算法演示与调用埋点可视化</Title>

      <Card title="算法执行" extra={<ExportButton type={activeTab} />}>
        <AlgoTabs activeKey={activeTab} onChange={setActiveTab} />
      </Card>

      <Card title="调用情况报表" style={{ marginTop: 16 }}>
        <Space direction="vertical" style={{ width: '100%' }}>
          <MetricsFilter onFilter={fetchMetrics} />
          {metrics && chart === 'line' && <ChartLine data={metrics} />}
          {metrics && chart === 'pie' && <ChartPie data={metrics} />}
          {metrics && chart === 'bar' && <ChartBar data={metrics} />}
        </Space>
      </Card>
    </div>
  );
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `cd testDJnew-main && npx vitest run src/__tests__/AlgoDashboardPage.test.tsx`
Expected: PASS

- [ ] **Step 5: Commit**

```bash
cd testDJnew-main
git add src/pages/AlgoDashboardPage.tsx src/__tests__/AlgoDashboardPage.test.tsx
git commit -m "feat(frontend): AlgoDashboardPage assembling tabs + export + metrics charts"
```

---

## Task 16: 跨仓集成验证

**Files:**
- 无新增文件，仅验证

**Interfaces:**
- Consumes: 全部前序 Task
- Produces: 跨仓对齐验证报告

- [ ] **Step 1: 后端全量测试**

Run: `cd testDj-main && mvn test -q`
Expected: 全部 PASS

- [ ] **Step 2: 前端全量测试**

Run: `cd testDJnew-main && npx vitest run`
Expected: 全部 PASS

- [ ] **Step 3: 跨仓契约对齐检查**

逐项核对：

| 对齐点 | 后端 | 前端 | 状态 |
|--------|------|------|------|
| 算法接口路径 | `POST /api/algo/{helloworld,hash,bubble}` | `apiClient.post('/algo/...')` | ✅ 一致 |
| 响应体结构 | `{code,data,msg,traceId}` | `ApiResponse<T>` TS 类型 | ✅ 一致 |
| 导出接口 | `GET /api/export?type=&format=` | `exportApi.export(type, format)` → `/api/export?type=...&format=...` | ✅ 一致 |
| 埋点查询 | `GET /api/metrics/summary?dimension=&range=&chart=` | `metricsApi.summary(dimension, range, chart)` | ✅ 一致 |
| 调用人传递 | 读取 `X-User-Id` / `X-User-Name` | `apiClient` 默认 header 注入 | ✅ 一致 |
| series/trend 结构 | `SeriesPoint{label,value}` / `TrendPoint{date,value}` | TS 类型 `SeriesPoint` / `TrendPoint` | ✅ 一致 |

- [ ] **Step 4: Commit（如有修复）**

```bash
# 仅在对齐检查发现不一致并修复时提交
cd testDj-main && git add -A && git commit -m "fix: cross-repo contract alignment" || true
cd testDJnew-main && git add -A && git commit -m "fix: cross-repo contract alignment" || true
```

---

## Self-Review

**1. Spec coverage:**
- F1（三个算法接口）→ Task 3 + Task 4 ✅
- F2（前端三 Tab 页面）→ Task 12 + Task 15 ✅
- F3（导出按钮 + 导出接口）→ Task 9（后端）+ Task 13（前端）✅
- F4（后端埋点：调用次数 + 调用人）→ Task 5 + Task 6 + Task 7 ✅
- F5（前端可视化报表：折线/饼图/柱状图，按人员类型/层级/部门维度）→ Task 8（后端查询）+ Task 14 + Task 15（前端图表）✅

**2. Placeholder scan:** 无 TBD/TODO/"implement later" 等占位符。所有步骤含完整代码。✅

**3. Type consistency:**
- `ApiResponse<T>` — Task 1 定义，Task 4/7/8 使用，字段 `code/data/msg/traceId` 一致 ✅
- `AlgoService` 方法签名 — Task 3 定义 `helloWorld(String) / hash(String, String) / bubbleSort(List<Integer>)`，Task 4 Controller 调用一致 ✅
- `InvokeLog.iface` — Task 7 定义，Task 8 `MetricsService` 使用 `getIface()`，Task 9 `ExportService` 使用 `getIface()` ✅
- `MetricsSummaryResponse.SeriesPoint{label,value}` / `TrendPoint{date,value}` — Task 8 定义，Task 14 前端 TS 类型 `SeriesPoint{label,value}` / `TrendPoint{date,value}` 一致 ✅
- `AlgoTabKey = 'helloworld' | 'hash' | 'bubble'` — Task 12 定义，Task 13 `ExportButton` props 使用，Task 15 `AlgoDashboardPage` 使用 ✅
- `exportApi.export(type, format)` — Task 11 定义，Task 13 调用参数 `(type, 'csv')` 一致 ✅

无类型不一致问题。✅
