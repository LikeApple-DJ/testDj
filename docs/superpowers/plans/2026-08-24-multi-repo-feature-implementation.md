# Multi-Repo Feature Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement three backend API interfaces (HelloWorld, Hash, BubbleSort), a frontend SPA with three tabs, CSV export, and tracking dashboard with visualizations.

**Architecture:** Two-repo architecture: testDj (Spring Boot 3 backend, Java 17, H2 in-memory DB) provides REST APIs; ykstest (Vue 3 + Element Plus + ECharts) provides the frontend SPA. Cross-repo communication via HTTP REST with unified `{code, message, data}` response format.

**Tech Stack:**
- testDj: Spring Boot 3.2+, Java 17, Maven, H2 Database, Spring Data JPA
- ykstest: Vue 3.4+, Element Plus 2.9+, ECharts 5.5+, Axios 1.7+, Vite 5+

## Global Constraints

- API base path: `http://localhost:8080`
- Unified response: `{ code: 200, message: "success", data: {} }`
- Error codes: 200=success, 400=bad request, 500=server error
- Date format: ISO 8601 `yyyy-MM-dd HH:mm:ss`
- Caller info passed via headers: `X-Caller-Name`, `X-Caller-Type`, `X-Caller-Level`, `X-Caller-Dept`
- Export format: CSV UTF-8 with BOM, filename `export_{tab}_{yyyyMMddHHmmss}.csv`
- Frontend proxy: vite.config.js proxy `/api` → `http://localhost:8080`
- No Git write operations allowed (read-only: status/log/diff/show)

---

````markdown
# Task 1: Initialize Spring Boot Project + H2 Configuration (testDj)

**Files:**
- Create: `testDj-main/pom.xml`
- Create: `testDj-main/src/main/java/com/example/demo/DemoApplication.java`
- Create: `testDj-main/src/main/resources/application.yml`
- Create: `testDj-main/src/main/resources/schema.sql`

**Interfaces:**
- Consumes: (none — first task)
- Produces: Runnable Spring Boot application with H2 console at `/h2-console`

- [ ] **Step 1: Create pom.xml**

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
    <description>Multi-repo demo backend</description>
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
      ddl-auto: none
    show-sql: true
    database-platform: org.hibernate.dialect.H2Dialect
  sql:
    init:
      mode: always
      schema-locations: classpath:schema.sql
```

- [ ] **Step 4: Create schema.sql**

```sql
CREATE TABLE IF NOT EXISTS tracking_record (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    api_name    VARCHAR(50)   NOT NULL,
    caller_name VARCHAR(100)  NOT NULL,
    caller_type VARCHAR(50),
    caller_level VARCHAR(50),
    caller_dept  VARCHAR(100),
    extra_info  VARCHAR(500),
    call_time   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_api_name ON tracking_record(api_name);
CREATE INDEX IF NOT EXISTS idx_call_time ON tracking_record(call_time);
CREATE INDEX IF NOT EXISTS idx_caller_type ON tracking_record(caller_type);
CREATE INDEX IF NOT EXISTS idx_caller_dept ON tracking_record(caller_dept);
```

- [ ] **Step 5: Verify build**

Run: `cd /workspace/testDj-main && mvn clean compile -q`
Expected: BUILD SUCCESS
````

---

````markdown
# Task 2: Implement HelloController + HelloService (testDj)

**Files:**
- Create: `testDj-main/src/main/java/com/example/demo/controller/HelloController.java`
- Create: `testDj-main/src/main/java/com/example/demo/service/HelloService.java`

**Interfaces:**
- Consumes: Task 1 (Spring Boot base)
- Produces: `GET /api/hello?name=World` → `{ code: 200, data: { greeting: "Hello, World!" } }`

- [ ] **Step 1: Create HelloService.java**

```java
package com.example.demo.service;

import org.springframework.stereotype.Service;

@Service
public class HelloService {

    public String greet(String name) {
        if (name == null || name.isBlank()) {
            name = "World";
        }
        return "Hello, " + name + "!";
    }
}
```

- [ ] **Step 2: Create HelloController.java**

```java
package com.example.demo.controller;

import com.example.demo.service.HelloService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class HelloController {

    private final HelloService helloService;

    public HelloController(HelloService helloService) {
        this.helloService = helloService;
    }

    @GetMapping("/hello")
    public Map<String, Object> hello(
            @RequestParam(defaultValue = "World") String name,
            @RequestHeader(value = "X-Caller-Name", required = false, defaultValue = "anonymous") String callerName,
            @RequestHeader(value = "X-Caller-Type", required = false) String callerType,
            @RequestHeader(value = "X-Caller-Level", required = false) String callerLevel,
            @RequestHeader(value = "X-Caller-Dept", required = false) String callerDept) {

        String greeting = helloService.greet(name);
        return Map.of("code", 200, "message", "success",
                "data", Map.of("greeting", greeting));
    }
}
```

- [ ] **Step 3: Verify compilation**

Run: `cd /workspace/testDj-main && mvn compile -q`
Expected: BUILD SUCCESS
````

---

````markdown
# Task 3: Implement HashController + HashService (testDj)

**Files:**
- Create: `testDj-main/src/main/java/com/example/demo/controller/HashController.java`
- Create: `testDj-main/src/main/java/com/example/demo/service/HashService.java`

**Interfaces:**
- Consumes: Task 1 (Spring Boot base)
- Produces: `POST /api/hash` → `{ code: 200, data: { algorithm, input, output } }`

- [ ] **Step 1: Create HashService.java**

```java
package com.example.demo.service;

import org.springframework.stereotype.Service;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Service
public class HashService {

    public String hash(String input, String algorithm) {
        if (algorithm == null || algorithm.isBlank()) {
            algorithm = "SHA-256";
        }
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm.toUpperCase());
            byte[] digest = md.digest(input.getBytes());
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("Unsupported algorithm: " + algorithm);
        }
    }
}
```

- [ ] **Step 2: Create HashController.java**

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
    public Map<String, Object> hash(
            @RequestBody Map<String, String> request,
            @RequestHeader(value = "X-Caller-Name", required = false, defaultValue = "anonymous") String callerName,
            @RequestHeader(value = "X-Caller-Type", required = false) String callerType,
            @RequestHeader(value = "X-Caller-Level", required = false) String callerLevel,
            @RequestHeader(value = "X-Caller-Dept", required = false) String callerDept) {

        String input = request.getOrDefault("input", "");
        String algorithm = request.getOrDefault("algorithm", "SHA-256");
        String output = hashService.hash(input, algorithm);
        return Map.of("code", 200, "message", "success",
                "data", Map.of("algorithm", algorithm.toUpperCase(), "input", input, "output", output));
    }
}
```

- [ ] **Step 3: Verify compilation**

Run: `cd /workspace/testDj-main && mvn compile -q`
Expected: BUILD SUCCESS
````

---

````markdown
# Task 4: Implement SortController + SortService (testDj)

**Files:**
- Create: `testDj-main/src/main/java/com/example/demo/controller/SortController.java`
- Create: `testDj-main/src/main/java/com/example/demo/service/SortService.java`

**Interfaces:**
- Consumes: Task 1 (Spring Boot base)
- Produces: `POST /api/sort` → `{ code: 200, data: { original, sorted, swapCount } }`

- [ ] **Step 1: Create SortService.java**

```java
package com.example.demo.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;

@Service
public class SortService {

    public SortResult bubbleSort(List<Integer> numbers, String order) {
        if (numbers == null) {
            numbers = List.of();
        }
        boolean ascending = !"desc".equalsIgnoreCase(order);
        List<Integer> arr = new ArrayList<>(numbers);
        int n = arr.size();
        int swapCount = 0;

        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1 - i; j++) {
                boolean needSwap = ascending
                        ? arr.get(j) > arr.get(j + 1)
                        : arr.get(j) < arr.get(j + 1);
                if (needSwap) {
                    int temp = arr.get(j);
                    arr.set(j, arr.get(j + 1));
                    arr.set(j + 1, temp);
                    swapCount++;
                }
            }
        }
        return new SortResult(numbers, arr, swapCount);
    }

    public record SortResult(List<Integer> original, List<Integer> sorted, int swapCount) {}
}
```

- [ ] **Step 2: Create SortController.java**

```java
package com.example.demo.controller;

import com.example.demo.service.SortService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SortController {

    private final SortService sortService;

    public SortController(SortService sortService) {
        this.sortService = sortService;
    }

    @PostMapping("/sort")
    public Map<String, Object> sort(
            @RequestBody Map<String, Object> request,
            @RequestHeader(value = "X-Caller-Name", required = false, defaultValue = "anonymous") String callerName,
            @RequestHeader(value = "X-Caller-Type", required = false) String callerType,
            @RequestHeader(value = "X-Caller-Level", required = false) String callerLevel,
            @RequestHeader(value = "X-Caller-Dept", required = false) String callerDept) {

        @SuppressWarnings("unchecked")
        List<Integer> numbers = (List<Integer>) request.getOrDefault("numbers", List.of());
        String order = (String) request.getOrDefault("order", "asc");
        SortService.SortResult result = sortService.bubbleSort(numbers, order);
        return Map.of("code", 200, "message", "success",
                "data", Map.of("original", result.original(), "sorted", result.sorted(), "swapCount", result.swapCount()));
    }
}
```

- [ ] **Step 3: Verify compilation**

Run: `cd /workspace/testDj-main && mvn compile -q`
Expected: BUILD SUCCESS
````

---

````markdown
# Task 5: Implement Tracking Entity, Repository, Service, and Interceptor (testDj)

**Files:**
- Create: `testDj-main/src/main/java/com/example/demo/entity/TrackingRecord.java`
- Create: `testDj-main/src/main/java/com/example/demo/repository/TrackingRepository.java`
- Create: `testDj-main/src/main/java/com/example/demo/service/TrackingService.java`
- Create: `testDj-main/src/main/java/com/example/demo/config/WebConfig.java`
- Modify: `testDj-main/src/main/java/com/example/demo/controller/HelloController.java` (add tracking call)
- Modify: `testDj-main/src/main/java/com/example/demo/controller/HashController.java` (add tracking call)
- Modify: `testDj-main/src/main/java/com/example/demo/controller/SortController.java` (add tracking call)

**Interfaces:**
- Consumes: Task 1 (schema.sql tracking_record table), Task 2-4 (controllers)
- Produces: `TrackingService.record()` method consumed by all controllers; CORS config for frontend

- [ ] **Step 1: Create TrackingRecord entity**

```java
package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tracking_record")
public class TrackingRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "api_name", nullable = false, length = 50)
    private String apiName;

    @Column(name = "caller_name", nullable = false, length = 100)
    private String callerName;

    @Column(name = "caller_type", length = 50)
    private String callerType;

    @Column(name = "caller_level", length = 50)
    private String callerLevel;

    @Column(name = "caller_dept", length = 100)
    private String callerDept;

    @Column(name = "extra_info", length = 500)
    private String extraInfo;

    @Column(name = "call_time")
    private LocalDateTime callTime;

    public TrackingRecord() {}

    public TrackingRecord(String apiName, String callerName, String callerType,
                          String callerLevel, String callerDept, String extraInfo) {
        this.apiName = apiName;
        this.callerName = callerName;
        this.callerType = callerType;
        this.callerLevel = callerLevel;
        this.callerDept = callerDept;
        this.extraInfo = extraInfo;
        this.callTime = LocalDateTime.now();
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getApiName() { return apiName; }
    public void setApiName(String apiName) { this.apiName = apiName; }
    public String getCallerName() { return callerName; }
    public void setCallerName(String callerName) { this.callerName = callerName; }
    public String getCallerType() { return callerType; }
    public void setCallerType(String callerType) { this.callerType = callerType; }
    public String getCallerLevel() { return callerLevel; }
    public void setCallerLevel(String callerLevel) { this.callerLevel = callerLevel; }
    public String getCallerDept() { return callerDept; }
    public void setCallerDept(String callerDept) { this.callerDept = callerDept; }
    public String getExtraInfo() { return extraInfo; }
    public void setExtraInfo(String extraInfo) { this.extraInfo = extraInfo; }
    public LocalDateTime getCallTime() { return callTime; }
    public void setCallTime(LocalDateTime callTime) { this.callTime = callTime; }
}
```

- [ ] **Step 2: Create TrackingRepository**

```java
package com.example.demo.repository;

import com.example.demo.entity.TrackingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TrackingRepository extends JpaRepository<TrackingRecord, Long> {

    @Query("SELECT t.callerType AS label, COUNT(t) AS value FROM TrackingRecord t GROUP BY t.callerType")
    List<Object[]> countByCallerType();

    @Query("SELECT t.callerLevel AS label, COUNT(t) AS value FROM TrackingRecord t GROUP BY t.callerLevel")
    List<Object[]> countByCallerLevel();

    @Query("SELECT t.callerDept AS label, COUNT(t) AS value FROM TrackingRecord t GROUP BY t.callerDept")
    List<Object[]> countByCallerDept();

    @Query("SELECT FUNCTION('FORMATDATETIME', t.callTime, 'yyyy-MM-dd') AS label, COUNT(t) AS value " +
           "FROM TrackingRecord t WHERE t.callTime BETWEEN :start AND :end GROUP BY label ORDER BY label")
    List<Object[]> countByTimeRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
```

- [ ] **Step 3: Create TrackingService**

```java
package com.example.demo.service;

import com.example.demo.entity.TrackingRecord;
import com.example.demo.repository.TrackingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class TrackingService {

    private final TrackingRepository repository;

    public TrackingService(TrackingRepository repository) {
        this.repository = repository;
    }

    public void record(String apiName, String callerName, String callerType,
                       String callerLevel, String callerDept, String extraInfo) {
        TrackingRecord record = new TrackingRecord(apiName, callerName, callerType,
                callerLevel, callerDept, extraInfo);
        repository.save(record);
    }

    public Map<String, Object> getStats(String dimension, String startDate, String endDate) {
        List<Object[]> rawData;
        switch (dimension) {
            case "callerType" -> rawData = repository.countByCallerType();
            case "callerLevel" -> rawData = repository.countByCallerLevel();
            case "callerDept" -> rawData = repository.countByCallerDept();
            case "time" -> {
                LocalDateTime start = startDate != null ? LocalDateTime.parse(startDate + " 00:00:00",
                        java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : LocalDateTime.now().minusDays(7);
                LocalDateTime end = endDate != null ? LocalDateTime.parse(endDate + " 23:59:59",
                        java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : LocalDateTime.now();
                rawData = repository.countByTimeRange(start, end);
            }
            default -> throw new IllegalArgumentException("Unsupported dimension: " + dimension);
        }
        List<String> labels = new ArrayList<>();
        List<Long> values = new ArrayList<>();
        for (Object[] row : rawData) {
            labels.add((String) row[0]);
            values.add((Long) row[1]);
        }
        return Map.of("labels", labels, "values", values);
    }

    public Map<String, Object> getRecords(int page, int size) {
        org.springframework.data.domain.Pageable pageable =
                org.springframework.data.domain.PageRequest.of(page, size,
                        org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "callTime"));
        var pagedResult = repository.findAll(pageable);
        return Map.of("records", pagedResult.getContent(), "total", pagedResult.getTotalElements());
    }
}
```

- [ ] **Step 4: Create WebConfig (CORS)**

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
                        .allowedOrigins("http://localhost:5173")
                        .allowedMethods("GET", "POST", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
```

- [ ] **Step 5: Add tracking calls to HelloController**

Insert after `String greeting = helloService.greet(name);` line:
```java
        trackingService.record("hello", callerName, callerType, callerLevel, callerDept,
                "name=" + name);
```
Add field: `private final TrackingService trackingService;`
Add constructor parameter: `TrackingService trackingService`
Add initializer: `this.trackingService = trackingService;`

- [ ] **Step 6: Add tracking calls to HashController**

Insert after `String output = hashService.hash(input, algorithm);` line:
```java
        trackingService.record("hash", callerName, callerType, callerLevel, callerDept,
                "algorithm=" + algorithm + ",input=" + input);
```
Add field: `private final TrackingService trackingService;`
Add constructor parameter: `TrackingService trackingService`
Add initializer: `this.trackingService = trackingService;`

- [ ] **Step 7: Add tracking calls to SortController**

Insert after `SortService.SortResult result = sortService.bubbleSort(numbers, order);` line:
```java
        trackingService.record("sort", callerName, callerType, callerLevel, callerDept,
                "order=" + order + ",length=" + numbers.size());
```
Add field: `private final TrackingService trackingService;`
Add constructor parameter: `TrackingService trackingService`
Add initializer: `this.trackingService = trackingService;`

- [ ] **Step 8: Verify compilation**

Run: `cd /workspace/testDj-main && mvn compile -q`
Expected: BUILD SUCCESS
````

---

````markdown
# Task 6: Implement ExportController (testDj)

**Files:**
- Create: `testDj-main/src/main/java/com/example/demo/controller/ExportController.java`
- Create: `testDj-main/src/main/java/com/example/demo/service/ExportService.java`

**Interfaces:**
- Consumes: Task 1-5 (Spring Boot, HelloService, HashService, SortService, TrackingService)
- Produces: `GET /api/export?tab=hello&callerName=xxx` → CSV file download

- [ ] **Step 1: Create ExportService.java**

```java
package com.example.demo.service;

import com.example.demo.entity.TrackingRecord;
import com.example.demo.repository.TrackingRepository;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExportService {

    private final TrackingRepository trackingRepository;

    public ExportService(TrackingRepository trackingRepository) {
        this.trackingRepository = trackingRepository;
    }

    public byte[] exportCsv(String tab) {
        List<TrackingRecord> records = trackingRepository.findAll();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // Write UTF-8 BOM
        baos.write(0xEF);
        baos.write(0xBB);
        baos.write(0xBF);
        try (OutputStreamWriter writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8)) {
            // Filter by tab if needed
            String targetApi = switch (tab) {
                case "hello" -> "hello";
                case "hash" -> "hash";
                case "sort" -> "sort";
                default -> null;
            };

            writer.write("ID,API,Caller,Type,Level,Dept,Extra,Time\n");
            for (TrackingRecord record : records) {
                if (targetApi != null && !targetApi.equals(record.getApiName())) {
                    continue;
                }
                String line = String.format("%d,%s,%s,%s,%s,%s,%s,%s\n",
                        record.getId(),
                        safeCsv(record.getApiName()),
                        safeCsv(record.getCallerName()),
                        safeCsv(record.getCallerType()),
                        safeCsv(record.getCallerLevel()),
                        safeCsv(record.getCallerDept()),
                        safeCsv(record.getExtraInfo()),
                        record.getCallTime() != null
                                ? record.getCallTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                                : "");
                writer.write(line);
            }
            writer.flush();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate CSV", e);
        }
        return baos.toByteArray();
    }

    private String safeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api")
public class ExportController {

    private final ExportService exportService;

    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> export(
            @RequestParam String tab,
            @RequestParam String callerName) {

        byte[] csvData = exportService.exportCsv(tab);
        String filename = String.format("export_%s_%s.csv", tab,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csvData);
    }
}
```

- [ ] **Step 3: Verify compilation**

Run: `cd /workspace/testDj-main && mvn compile -q`
Expected: BUILD SUCCESS
````

---

````markdown
# Task 7: Implement TrackingController (testDj)

**Files:**
- Create: `testDj-main/src/main/java/com/example/demo/controller/TrackingController.java`

**Interfaces:**
- Consumes: Task 5 (TrackingService)
- Produces: `GET /api/tracking/stats?dimension=callerType` and `GET /api/tracking/records?page=0&size=10`

- [ ] **Step 1: Create TrackingController.java**

```java
package com.example.demo.controller;

import com.example.demo.service.TrackingService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/tracking")
public class TrackingController {

    private final TrackingService trackingService;

    public TrackingController(TrackingService trackingService) {
        this.trackingService = trackingService;
    }

    @GetMapping("/stats")
    public Map<String, Object> stats(
            @RequestParam String dimension,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        Map<String, Object> statsData = trackingService.getStats(dimension, startDate, endDate);
        return Map.of("code", 200, "message", "success", "data", statsData);
    }

    @GetMapping("/records")
    public Map<String, Object> records(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Map<String, Object> recordData = trackingService.getRecords(page, size);
        return Map.of("code", 200, "message", "success", "data", recordData);
    }
}
```

- [ ] **Step 2: Verify compilation**

Run: `cd /workspace/testDj-main && mvn compile -q`
Expected: BUILD SUCCESS
````

---

````markdown
# Task 8: Initialize Vue 3 Project + Element Plus (ykstest)

**Files:**
- Create: `ykstest-main/package.json`
- Create: `ykstest-main/vite.config.js`
- Create: `ykstest-main/index.html`
- Create: `ykstest-main/src/main.js`
- Create: `ykstest-main/src/App.vue`
- Create: `ykstest-main/src/styles/main.css`

**Interfaces:**
- Consumes: (none — first frontend task)
- Produces: Runnable Vue 3 SPA with Element Plus, ECharts, Axios, and Vite dev server

- [ ] **Step 1: Create package.json**

```json
{
  "name": "ykstest",
  "private": true,
  "version": "1.0.0",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "vite build",
    "preview": "vite preview"
  },
  "dependencies": {
    "vue": "^3.4.0",
    "element-plus": "^2.9.0",
    "axios": "^1.7.0",
    "echarts": "^5.5.0",
    "vue-echarts": "^7.0.0"
  },
  "devDependencies": {
    "@vitejs/plugin-vue": "^5.0.0",
    "vite": "^5.4.0"
  }
}
```

- [ ] **Step 2: Create vite.config.js**

```javascript
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

- [ ] **Step 3: Create index.html**

```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Multi-Repo Demo</title>
</head>
<body>
  <div id="app"></div>
  <script type="module" src="/src/main.js"></script>
</body>
</html>
```

- [ ] **Step 4: Create src/main.js**

```javascript
import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import './styles/main.css'

const app = createApp(App)
app.use(ElementPlus)
app.mount('#app')
```

- [ ] **Step 5: Create src/App.vue**

```vue
<template>
  <div id="app-container">
    <MainPage />
  </div>
</template>

<script setup>
import MainPage from './views/MainPage.vue'
</script>
```

- [ ] **Step 6: Create src/styles/main.css**

```css
body {
  margin: 0;
  padding: 20px;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Microsoft YaHei', sans-serif;
  background-color: #f5f7fa;
}
#app-container {
  max-width: 1200px;
  margin: 0 auto;
}
```

- [ ] **Step 7: Install dependencies**

Run: `cd /workspace/ykstest-main && npm install`
Expected: success (node_modules created)
````

---

````markdown
# Task 9: Implement API Client + Three Tab Components (ykstest)

**Files:**
- Create: `ykstest-main/src/api/index.js`
- Create: `ykstest-main/src/views/MainPage.vue`
- Create: `ykstest-main/src/components/tabs/HelloTab.vue`
- Create: `ykstest-main/src/components/tabs/HashTab.vue`
- Create: `ykstest-main/src/components/tabs/SortTab.vue`

**Interfaces:**
- Consumes: Task 8 (Vue 3 + Element Plus base), testDj Tasks 2-4 (API endpoints)
- Produces: MainPage with three tabs calling backend APIs

- [ ] **Step 1: Create src/api/index.js**

```javascript
import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// Default caller info headers
const defaultCaller = {
  'X-Caller-Name': 'demo-user',
  'X-Caller-Type': '正式',
  'X-Caller-Level': '中级',
  'X-Caller-Dept': '研发'
}

function getCallerHeaders() {
  const stored = localStorage.getItem('callerInfo')
  if (stored) {
    try {
      return JSON.parse(stored)
    } catch { /* ignore */ }
  }
  return defaultCaller
}

export function setCallerInfo(info) {
  localStorage.setItem('callerInfo', JSON.stringify(info))
}

export function getHello(name) {
  return api.get('/hello', {
    params: { name },
    headers: getCallerHeaders()
  })
}

export function postHash(input, algorithm) {
  return api.post('/hash', { input, algorithm }, {
    headers: getCallerHeaders()
  })
}

export function postSort(numbers, order) {
  return api.post('/sort', { numbers, order }, {
    headers: getCallerHeaders()
  })
}

export function getExport(tab, callerName) {
  return api.get('/export', {
    params: { tab, callerName },
    responseType: 'blob'
  })
}

export function getTrackingStats(dimension, startDate, endDate) {
  return api.get('/tracking/stats', {
    params: { dimension, startDate, endDate }
  })
}

export function getTrackingRecords(page, size) {
  return api.get('/tracking/records', {
    params: { page, size }
  })
}

export default api
```

- [ ] **Step 2: Create MainPage.vue**

```vue
<template>
  <div class="main-page">
    <h1>Multi-Repo Demo</h1>
    <el-tabs v-model="activeTab" type="border-card">
      <el-tab-pane label="HelloWorld" name="hello">
        <HelloTab />
      </el-tab-pane>
      <el-tab-pane label="Hash Algorithm" name="hash">
        <HashTab />
      </el-tab-pane>
      <el-tab-pane label="Bubble Sort" name="sort">
        <SortTab />
      </el-tab-pane>
    </el-tabs>

    <div class="action-bar">
      <ExportButton />
    </div>

    <el-divider />

    <Dashboard />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import HelloTab from '../components/tabs/HelloTab.vue'
import HashTab from '../components/tabs/HashTab.vue'
import SortTab from '../components/tabs/SortTab.vue'
import ExportButton from '../components/ExportButton.vue'
import Dashboard from '../components/dashboard/Dashboard.vue'

const activeTab = ref('hello')
</script>

<style scoped>
.main-page {
  padding: 20px;
}
.action-bar {
  margin: 20px 0;
  text-align: right;
}
</style>
```

- [ ] **Step 3: Create HelloTab.vue**

```vue
<template>
  <div class="hello-tab">
    <el-input v-model="name" placeholder="Enter name" style="width: 300px; margin-right: 10px" />
    <el-button type="primary" @click="callHello">Greet</el-button>
    <el-divider />
    <el-card v-if="result">
      <pre>{{ result }}</pre>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { getHello } from '../../api/index.js'

const name = ref('World')
const result = ref(null)

async function callHello() {
  try {
    const res = await getHello(name.value)
    result.value = JSON.stringify(res.data, null, 2)
  } catch (e) {
    result.value = 'Error: ' + e.message
  }
}
</script>
```

- [ ] **Step 4: Create HashTab.vue**

```vue
<template>
  <div class="hash-tab">
    <el-input v-model="input" placeholder="Enter text to hash" style="width: 400px; margin-right: 10px" />
    <el-select v-model="algorithm" style="width: 150px; margin-right: 10px">
      <el-option label="MD5" value="MD5" />
      <el-option label="SHA-256" value="SHA-256" />
      <el-option label="SHA-512" value="SHA-512" />
    </el-select>
    <el-button type="primary" @click="callHash">Hash</el-button>
    <el-divider />
    <el-card v-if="result">
      <pre>{{ result }}</pre>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { postHash } from '../../api/index.js'

const input = ref('hello')
const algorithm = ref('SHA-256')
const result = ref(null)

async function callHash() {
  try {
    const res = await postHash(input.value, algorithm.value)
    result.value = JSON.stringify(res.data, null, 2)
  } catch (e) {
    result.value = 'Error: ' + e.message
  }
}
</script>
```

- [ ] **Step 5: Create SortTab.vue**

```vue
<template>
  <div class="sort-tab">
    <el-input v-model="numbersInput" placeholder="e.g. 3,1,4,1,5,9,2,6" style="width: 400px; margin-right: 10px" />
    <el-select v-model="order" style="width: 120px; margin-right: 10px">
      <el-option label="Ascending" value="asc" />
      <el-option label="Descending" value="desc" />
    </el-select>
    <el-button type="primary" @click="callSort">Sort</el-button>
    <el-divider />
    <el-card v-if="result">
      <pre>{{ result }}</pre>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { postSort } from '../../api/index.js'

const numbersInput = ref('3,1,4,1,5,9,2,6')
const order = ref('asc')
const result = ref(null)

async function callSort() {
  try {
    const numbers = numbersInput.value.split(',').map(s => parseInt(s.trim(), 10))
    const res = await postSort(numbers, order.value)
    result.value = JSON.stringify(res.data, null, 2)
  } catch (e) {
    result.value = 'Error: ' + e.message
  }
}
</script>
```

- [ ] **Step 6: Verify build**

Run: `cd /workspace/ykstest-main && npx vite build 2>&1 | tail -5`
Expected: Build successful
````

---

````markdown
# Task 10: Implement ExportButton Component (ykstest)

**Files:**
- Create: `ykstest-main/src/components/ExportButton.vue`

**Interfaces:**
- Consumes: Task 9 (api/index.js getExport function)
- Produces: Export button that downloads CSV from backend

- [ ] **Step 1: Create ExportButton.vue**

```vue
<template>
  <div class="export-button">
    <el-select v-model="selectedTab" style="width: 150px; margin-right: 10px">
      <el-option label="HelloWorld" value="hello" />
      <el-option label="Hash" value="hash" />
      <el-option label="Sort" value="sort" />
    </el-select>
    <el-button type="success" @click="handleExport" :loading="loading">
      Export CSV
    </el-button>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { getExport } from '../api/index.js'

const selectedTab = ref('hello')
const loading = ref(false)

async function handleExport() {
  loading.value = true
  try {
    const callerName = 'demo-user'
    const response = await getExport(selectedTab.value, callerName)
    const url = window.URL.createObjectURL(new Blob([response.data], { type: 'text/csv;charset=utf-8;' }))
    const link = document.createElement('a')
    link.href = url
    const timestamp = new Date().toISOString().replace(/[:.]/g, '').slice(0, 14)
    link.setAttribute('download', `export_${selectedTab.value}_${timestamp}.csv`)
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
  } catch (e) {
    console.error('Export failed:', e)
  } finally {
    loading.value = false
  }
}
</script>
```

- [ ] **Step 2: Verify build**

Run: `cd /workspace/ykstest-main && npx vite build 2>&1 | tail -5`
Expected: Build successful
````

---

````markdown
# Task 11: Implement Dashboard + Three Charts (ykstest)

**Files:**
- Create: `ykstest-main/src/components/dashboard/Dashboard.vue`
- Create: `ykstest-main/src/components/dashboard/LineChart.vue`
- Create: `ykstest-main/src/components/dashboard/PieChart.vue`
- Create: `ykstest-main/src/components/dashboard/BarChart.vue`

**Interfaces:**
- Consumes: Task 9 (api/index.js getTrackingStats function)
- Produces: Dashboard with three chart types displaying tracking data

- [ ] **Step 1: Create Dashboard.vue**

```vue
<template>
  <div class="dashboard">
    <h2>📊 Tracking Dashboard</h2>
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card>
          <template #header>
            <span>📈 Call Trend (by time)</span>
          </template>
          <LineChart :data="timeData" />
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>🥧 By Type / Level</span>
          </template>
          <el-radio-group v-model="pieDimension" style="margin-bottom: 10px">
            <el-radio value="callerType">By Type</el-radio>
            <el-radio value="callerLevel">By Level</el-radio>
          </el-radio-group>
          <PieChart :data="pieData" />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>📊 By Department</span>
          </template>
          <BarChart :data="deptData" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { getTrackingStats } from '../../api/index.js'
import LineChart from './LineChart.vue'
import PieChart from './PieChart.vue'
import BarChart from './BarChart.vue'

const timeData = ref({ labels: [], values: [] })
const pieData = ref({ labels: [], values: [] })
const deptData = ref({ labels: [], values: [] })
const pieDimension = ref('callerType')

async function fetchAll() {
  try {
    const timeRes = await getTrackingStats('time')
    timeData.value = timeRes.data.data || { labels: [], values: [] }

    const pieRes = await getTrackingStats(pieDimension.value)
    pieData.value = pieRes.data.data || { labels: [], values: [] }

    const deptRes = await getTrackingStats('callerDept')
    deptData.value = deptRes.data.data || { labels: [], values: [] }
  } catch (e) {
    console.error('Failed to fetch stats:', e)
  }
}

watch(pieDimension, () => {
  getTrackingStats(pieDimension.value).then(res => {
    pieData.value = res.data.data || { labels: [], values: [] }
  })
})

onMounted(fetchAll)
</script>
```

- [ ] **Step 2: Create LineChart.vue**

```vue
<template>
  <div ref="chartRef" style="width: 100%; height: 300px"></div>
</template>

<script setup>
import { ref, onMounted, watch, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts'

const props = defineProps({
  data: { type: Object, default: () => ({ labels: [], values: [] }) }
})

const chartRef = ref(null)
let chartInstance = null

function renderChart() {
  if (!chartRef.value) return
  if (!chartInstance) {
    chartInstance = echarts.init(chartRef.value)
  }
  chartInstance.setOption({
    xAxis: { type: 'category', data: props.data.labels },
    yAxis: { type: 'value' },
    series: [{ type: 'line', data: props.data.values, smooth: true }],
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true }
  })
}

watch(() => props.data, renderChart, { deep: true })
onMounted(renderChart)
onBeforeUnmount(() => { chartInstance?.dispose() })
</script>
```

- [ ] **Step 3: Create PieChart.vue**

```vue
<template>
  <div ref="chartRef" style="width: 100%; height: 300px"></div>
</template>

<script setup>
import { ref, onMounted, watch, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts'

const props = defineProps({
  data: { type: Object, default: () => ({ labels: [], values: [] }) }
})

const chartRef = ref(null)
let chartInstance = null

function renderChart() {
  if (!chartRef.value) return
  if (!chartInstance) {
    chartInstance = echarts.init(chartRef.value)
  }
  const pieData = props.data.labels.map((label, i) => ({
    name: label,
    value: props.data.values[i]
  }))
  chartInstance.setOption({
    series: [{ type: 'pie', data: pieData, radius: ['30%', '70%'] }],
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' }
  })
}

watch(() => props.data, renderChart, { deep: true })
onMounted(renderChart)
onBeforeUnmount(() => { chartInstance?.dispose() })
</script>
```

- [ ] **Step 4: Create BarChart.vue**

```vue
<template>
  <div ref="chartRef" style="width: 100%; height: 300px"></div>
</template>

<script setup>
import { ref, onMounted, watch, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts'

const props = defineProps({
  data: { type: Object, default: () => ({ labels: [], values: [] }) }
})

const chartRef = ref(null)
let chartInstance = null

function renderChart() {
  if (!chartRef.value) return
  if (!chartInstance) {
    chartInstance = echarts.init(chartRef.value)
  }
  chartInstance.setOption({
    xAxis: { type: 'category', data: props.data.labels },
    yAxis: { type: 'value' },
    series: [{ type: 'bar', data: props.data.values }],
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true }
  })
}

watch(() => props.data, renderChart, { deep: true })
onMounted(renderChart)
onBeforeUnmount(() => { chartInstance?.dispose() })
</script>
```

- [ ] **Step 5: Verify build**

Run: `cd /workspace/ykstest-main && npx vite build 2>&1 | tail -5`
Expected: Build successful
````

---

## Self-Review Checklist

**1. Spec coverage:**
- ✅ HelloWorld API → Task 2
- ✅ Hash Algorithm API → Task 3
- ✅ Bubble Sort API → Task 4
- ✅ Tracking/Instrumentation (埋点) → Task 5
- ✅ Export CSV interface → Task 6
- ✅ Tracking stats API → Task 7
- ✅ Frontend with three tabs → Task 8, 9
- ✅ Export button → Task 10
- ✅ Dashboard with Line/Pie/Bar charts → Task 11
- ✅ Cross-repo contract (CORS, proxy, headers) → Task 5 (WebConfig), Task 8 (vite.config.js)

**2. Placeholder scan:** No TBD, TODO, or placeholder patterns found.

**3. Type consistency:**
- `TrackingService.record(apiName, callerName, callerType, callerLevel, callerDept, extraInfo)` used consistently across Tasks 5-7
- API response format `{ code, message, data }` consistent across all controllers
- Chart component props `{ data: { labels, values } }` consistent across LineChart, PieChart, BarChart
- Caller headers `X-Caller-Name/Type/Level/Dept` consistent across api/index.js and all controllers

---

**Plan complete and saved to `docs/superpowers/plans/2026-08-24-multi-repo-feature-implementation.md`.**

Two execution options:

1. **Subagent-Driven (recommended)** - dispatch a fresh subagent per task, review between tasks, fast iteration
2. **Inline Execution** - execute tasks in this session using executing-plans, batch execution with checkpoints