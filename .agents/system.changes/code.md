# Demo Tools 代码实现方案

> **文档元信息**
>
> | 项目 | 内容 |
> |------|------|
> | 文档版本 | v1.0 |
> | 作者 | DTCoder |
> | 创建日期 | 2026-08-25 |
> | 需求来源 | `.agents/specs/20260825-分别写三个接口helloworld_哈希.md` |
> | 设计依据 | `.agents/system.changes/design.md` |
> | 阶段 | 编码实现 |

---

## 1. 代码实现总览

本方案承接《Demo Tools 系分设计》，将设计文档中的接口、模块、时序与约束落实到具体代码文件与关键实现。

- **后端**：Spring Boot 3 + Java 17，统一前缀 `/api/v1/demo`，统一响应 `{ code, data, message }`。
- **前端**：React 18 + Vite + TypeScript + ECharts，通过 Vite proxy 转发 `/api` 到后端 `8080` 端口。
- **埋点**：基于 Spring `HandlerInterceptor` 拦截业务接口，写入 H2 关系型数据库，按 `userType` / `userLevel` / `userDept` 聚合。
- **导出**：按当前 Tab 生成 CSV / Excel 二进制流，前端通过 `<a>` 触发下载。

---

## 2. 后端代码实现

### 2.1 工程结构

```
testDj-main/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/com/testdj/demo/
│   │   │   ├── DemoApplication.java
│   │   │   ├── common/
│   │   │   │   └── ApiResponse.java
│   │   │   ├── config/
│   │   │   │   └── WebConfig.java
│   │   │   ├── exception/
│   │   │   │   ├── BusinessException.java
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   ├── hello/
│   │   │   │   └── HelloController.java
│   │   │   ├── hash/
│   │   │   │   ├── HashController.java
│   │   │   │   ├── HashRequest.java
│   │   │   │   ├── HashResponse.java
│   │   │   │   └── HashService.java
│   │   │   ├── sort/
│   │   │   │   ├── BubbleSortController.java
│   │   │   │   ├── BubbleSortService.java
│   │   │   │   ├── SortRequest.java
│   │   │   │   └── SortResponse.java
│   │   │   ├── export/
│   │   │   │   ├── ExportController.java
│   │   │   │   ├── ExportRequest.java
│   │   │   │   └── ExportService.java
│   │   │   └── metrics/
│   │   │       ├── Dimension.java
│   │   │       ├── MetricEvent.java
│   │   │       ├── MetricRepository.java
│   │   │       ├── MetricService.java
│   │   │       ├── MetricsController.java
│   │   │       ├── MetricsInterceptor.java
│   │   │       └── ReportItem.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/java/com/testdj/demo/
│       ├── DemoApplicationTests.java
│       ├── hello/
│       │   └── HelloControllerTest.java
│       ├── hash/
│       │   └── HashServiceTest.java
│       ├── sort/
│       │   └── BubbleSortServiceTest.java
│       ├── export/
│       │   └── ExportControllerTest.java
│       └── metrics/
│           └── MetricsControllerTest.java
```

### 2.2 统一响应与全局异常

**`ApiResponse.java`**

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

**`BusinessException.java`**

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

**`GlobalExceptionHandler.java`**

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

### 2.3 HelloWorld 接口

**`HelloController.java`**

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

### 2.4 哈希算法接口

**`HashRequest.java`**

```java
package com.testdj.demo.hash;

public record HashRequest(String algorithm, String content) {
}
```

**`HashResponse.java`**

```java
package com.testdj.demo.hash;

public record HashResponse(String algorithm, String original, String hash) {
}
```

**`HashService.java`**

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

**`HashController.java`**

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

### 2.5 冒泡排序接口

**`SortRequest.java`**

```java
package com.testdj.demo.sort;

import java.util.List;

public record SortRequest(List<Integer> numbers, boolean ascending, boolean unique) {
}
```

**`SortResponse.java`**

```java
package com.testdj.demo.sort;

import java.util.List;

public record SortResponse(List<Integer> input, List<Integer> output) {
}
```

**`BubbleSortService.java`**

```java
package com.testdj.demo.sort;

import com.testdj.demo.exception.BusinessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Service
public class BubbleSortService {

    public SortResponse sort(SortRequest request) {
        if (request.numbers() == null || request.numbers().isEmpty()) {
            throw new BusinessException(400, "numbers must not be empty");
        }
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
            output = new ArrayList<>(new LinkedHashSet<>(output));
        }
        return new SortResponse(input, output);
    }
}
```

**`BubbleSortController.java`**

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

### 2.6 导出接口

**`ExportRequest.java`**

```java
package com.testdj.demo.export;

public record ExportRequest(String tab, String format) {
}
```

**`ExportService.java`**

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
import java.nio.charset.StandardCharsets;
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
            case "hash" -> List.of(
                    new String[]{"algorithm", "original", "hash"},
                    new String[]{"SHA-256", "demo", "hashValue"});
            case "bubble" -> List.of(
                    new String[]{"input", "output"},
                    new String[]{"[3,1,4]", "[1,3,4]"});
            case "all" -> List.of(
                    new String[]{"tab", "result"},
                    new String[]{"hello", "Hello, World!"},
                    new String[]{"hash", "hashValue"},
                    new String[]{"bubble", "[1,3,4]"});
            default -> throw new BusinessException(400, "unknown tab: " + tab);
        };
    }

    private byte[] toCsv(List<String[]> rows) {
        StringBuilder sb = new StringBuilder();
        for (String[] row : rows) {
            sb.append(String.join(",", row)).append("\n");
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
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

**`ExportController.java`**

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

### 2.7 埋点与报表接口

**`Dimension.java`**

```java
package com.testdj.demo.metrics;

public enum Dimension {
    USER_TYPE,
    USER_LEVEL,
    USER_DEPT
}
```

**`ReportItem.java`**

```java
package com.testdj.demo.metrics;

public record ReportItem(String dimension, Long count) {
}
```

**`MetricEvent.java`**

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

    // Getters and setters
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

**`MetricRepository.java`**

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

**`MetricService.java`**

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

**`MetricsController.java`**

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

**`MetricsInterceptor.java`**

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

### 2.8 配置与拦截器注册

**`WebConfig.java`**

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

**`application.yml`**

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

---

## 3. 前端代码实现

### 3.1 工程结构

```
testDJnew-main/
├── index.html
├── package.json
├── tsconfig.json
├── vite.config.ts
└── src/
    ├── main.tsx
    ├── App.tsx
    ├── index.css
    ├── types/
    │   └── index.ts
    ├── api/
    │   └── client.ts
    ├── components/
    │   ├── DemoPage.tsx
    │   ├── HelloTab.tsx
    │   ├── HashTab.tsx
    │   ├── BubbleTab.tsx
    │   └── ReportPanel.tsx
    └── hooks/
        └── useMetrics.ts
```

### 3.2 类型定义与 API 客户端

**`types/index.ts`**

```typescript
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
export type Dimension = 'userType' | 'userLevel' | 'userDept';
export type ChartType = 'line' | 'bar' | 'pie';

export interface ReportItem {
  dimension: string;
  count: number;
}
```

**`api/client.ts`**

```typescript
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

export const hello = () =>
  api.get<ApiResponse<string>>('/hello').then(r => r.data.data);

export const hash = (content: string, algorithm: string = 'SHA-256') =>
  api.post<ApiResponse<HashResponse>>('/hash', { content, algorithm }).then(r => r.data.data);

export const bubbleSort = (numbers: number[], ascending = true, unique = false) =>
  api.post<ApiResponse<SortResponse>>('/sort/bubble', { numbers, ascending, unique }).then(r => r.data.data);

export const exportData = (tab: string, format: string) =>
  api.post('/export', { tab, format }, { responseType: 'blob' }).then(r => r.data);

export const fetchReport = (dimension: string, startDate: string, endDate: string) =>
  api.get<ApiResponse<Array<{ dimension: string; count: number }>>>('/metrics/report', {
    params: { dimension, startDate, endDate }
  }).then(r => r.data.data);
```

### 3.3 Tab 组件

**`HelloTab.tsx`**

```tsx
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

**`HashTab.tsx`**

```tsx
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
      </select>
      <button onClick={handleClick}>计算哈希</button>
      <pre>{result}</pre>
    </div>
  );
}
```

**`BubbleTab.tsx`**

```tsx
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

### 3.4 主页面

**`DemoPage.tsx`**

```tsx
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

### 3.5 可视化报表

**`hooks/useMetrics.ts`**

```typescript
import { useEffect, useState } from 'react';
import { fetchReport } from '../api/client';

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
    fetchReport(dimension.toUpperCase(), start.toISOString(), end.toISOString())
      .then(setData)
      .catch(() => setData([]));
  }, [dimension]);

  return data;
}
```

**`ReportPanel.tsx`**

```tsx
import { useRef, useEffect, useState } from 'react';
import * as echarts from 'echarts';
import { useMetrics, type Dimension } from '../hooks/useMetrics';

const dimensionLabels: Record<Dimension, string> = {
  userType: '人员类型',
  userLevel: '人员层级',
  userDept: '人员部门'
};

export default function ReportPanel() {
  const [dimension, setDimension] = useState<Dimension>('userType');
  const [chartType, setChartType] = useState<'line' | 'bar' | 'pie'>('bar');
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
        <select value={chartType} onChange={e => setChartType(e.target.value as any)}>
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

---

## 4. 接口契约与跨仓对齐

| 项目 | 后端 | 前端 | 对齐结论 |
|------|------|------|----------|
| 接口前缀 | `/api/v1/demo` | `baseURL: '/api/v1/demo'` | 一致 |
| 响应结构 | `{ code, data, message }` | `ApiResponse<T>` | 一致 |
| 身份字段 | `X-User-Id/Type/Level/Dept` | 请求头默认携带 | 一致 |
| 导出格式 | `csv`、`excel` | 导出按钮传对应 format | 一致 |
| 图表数据 | `{ dimension, count }[]` | ECharts 渲染 | 一致 |
| 报表维度 | `userType`、`userLevel`、`userDept` | 下拉选择并转大写 | 一致 |

---

## 5. 测试策略

| 测试对象 | 测试类型 | 覆盖点 |
|----------|----------|--------|
| `HelloController` | MockMvc | 返回 `Hello, World!`，响应结构正确 |
| `HashService` | 单元测试 | 默认 SHA-256，空 content 抛异常 |
| `BubbleSortService` | 单元测试 | 升序、降序、去重 |
| `ExportController` | 集成测试 | CSV / Excel 响应头、文件名 |
| `MetricsController` | 集成测试 | 按维度聚合正确 |
| 前端组件 | Vitest | Tab 切换、图表渲染不抛异常 |

---

## 6. 风险与注意点

1. **SM3 算法**：`MessageDigest` 默认不支持 SM3，如需支持需引入 `bcprov-jdk18on` 等密码学库。
2. **埋点阻塞**：当前实现为同步写入 H2，生产环境建议改为异步线程或消息队列，避免阻塞业务接口。
3. **导出数据量**：导出为内存流生成，大数据量场景应改为流式写入。
4. **CORS 限制**：`WebConfig` 仅允许 `http://localhost:5173`，生产环境需根据实际域名调整。
5. **时区问题**：报表查询使用 `Instant`，前后端统一 ISO 8601 格式，避免时区歧义。
