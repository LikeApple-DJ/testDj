# 三个接口（helloworld / 哈希 / 冒泡排序）+ 导出 + 埋点报表 实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 在 testDj 后端以 Spring Boot 3 提供 helloworld / MD5 哈希 / 冒泡排序 / 导出 / 埋点报表接口，在 ranxitest 前端以 Vue3+Vite+ECharts 提供三 tab 结果展示页、导出按钮与多维可视化报表。

**Architecture:** testDj 为后端（Java 21 + Spring Boot 3 + spring-boot-starter-web + POI），提供统一响应 `Result<T>(code/message/data)`，通过 `X-Caller-Id` 请求头识别调用人，埋点进程内内存暂存，报表按「人员类型/层级/部门/时间」聚合。ranxitest 为前端（Vue3+Vite+ECharts+axios），通过 Vite 代理访问后端 `/api/**`，页面含三 tab、导出按钮、报表区（折线/饼/柱状）。

**Tech Stack:** Java 21、Spring Boot 3.2.5、Apache POI 5.2.5、Vue 3、Vite 5、ECharts 5、axios。

## Global Constraints

- testDj = 后端，ranxitest = 前端。
- 后端 Java 21 + Maven + Spring Boot 3（`spring-boot-starter-web`）；前端 Vue 3 + Vite + ECharts，从零搭建。
- 哈希算法 MD5（十六进制小写输出）；冒泡排序入参为整数数组；helloWorld 固定输出 `"Hello, World!"`，无参。
- 调用人标识由请求 `X-Caller-Id` 头部传入；人员「类型/层级/部门」由后端人员字典维护；埋点进程内内存暂存（demo 级，预留替换点）。
- 导出支持 JSON / Excel(.xlsx) / CSV 三种格式。
- 统一响应结构 `Result<T>`：`{ "code": 0, "message": "ok", "data": ... }`；成功 code=0，失败 code=400/500 且 data=null。
- 图表形态：折线图=按时间趋势；饼图=各维度占比；柱状图=各维度按接口人次对比。
- 所有接口先加测试（TDD），测试通过后提交；每个任务一个独立提交。

---

## 仓间接口契约（跨仓对齐点，实现阶段必须严格遵守）

| 契约项 | 定义 |
| --- | --- |
| 响应包结构 | `Result<T>` = `{code:int, message:String, data:T}` |
| helloWorld | `GET /api/helloWorld` → `Result<String>`，data=`"Hello, World!"` |
| 哈希 | `POST /api/hash`，入参 `{"text":"abc"}`，返回 `Result<Map>`：`data={"algorithm":"MD5","input":"abc","hash":"900150983cd24fb0d6963f7d28e17f72"}` |
| 冒泡排序 | `POST /api/sort/bubble`，入参 `{"numbers":[5,3,8,1]}`，返回 `Result<Map>`：`data={"algorithm":"bubble-sort","input":[5,3,8,1],"sorted":[1,3,5,8]}` |
| 调用人 | 请求头 `X-Caller-Id`；缺省记 `anonymous` |
| 人员字典 | `GET /api/metrics/persons` → `Result<List<Person>>`，`Person={id,name,type,level,department}` |
| 报表查询 | `GET /api/metrics/query?dimension=personType|personLevel|personDepartment&chartType=line|pie|bar` → `Result<MetricsQueryResponse>` |
| 导出 | `POST /api/export`，入参 `{"format":"json|csv|xlsx","page":"helloworld|hash|bubble","rows":[{"key":"...","value":"..."}]}` → 文件字节流（`Content-Disposition: attachment`） |

报表响应 `MetricsQueryResponse` 统一为：
```
{ "dimension": "...", "chartType": "...", "categories": [...], "series": [{"name":"...", "values":[...]}], "total": 0 }
```
- pie：`categories`=维度值，`series`=[单条 "调用次数" 系列]。
- bar：`categories`=维度值，`series`=每条一个接口（helloworld/hash/bubble）。
- line：`categories`=HH:mm 时间桶，`series`=[单条 "调用次数" 系列]。

---

## 文件结构

### [testDj] 后端
- Modify: `pom.xml`（Spring Boot parent + starter-web + starter-test + POI）
- Create: `src/main/java/com/dt/example/Application.java`
- Create: `src/main/java/com/dt/example/common/Result.java`
- Create: `src/main/java/com/dt/example/algorithm/HashService.java`
- Create: `src/main/java/com/dt/example/algorithm/BubbleSortService.java`
- Create: `src/main/java/com/dt/example/metrics/Person.java`
- Create: `src/main/java/com/dt/example/metrics/PersonDictionary.java`
- Create: `src/main/java/com/dt/example/metrics/MetricsCollector.java`
- Create: `src/main/java/com/dt/example/metrics/MetricsInterceptor.java`
- Create: `src/main/java/com/dt/example/metrics/ChartSeries.java`
- Create: `src/main/java/com/dt/example/metrics/MetricsQueryResponse.java`
- Create: `src/main/java/com/dt/example/metrics/MetricsService.java`
- Create: `src/main/java/com/dt/example/export/ExportService.java`
- Create: `src/main/java/com/dt/example/web/WebConfig.java`
- Create: `src/main/java/com/dt/example/web/BusinessController.java`
- Create: `src/main/java/com/dt/example/web/MetricsController.java`
- Create: `src/main/java/com/dt/example/web/ExportController.java`
- Create: `src/main/java/com/dt/example/web/GlobalExceptionHandler.java`
- Test: `src/test/java/com/dt/example/algorithm/HashServiceTest.java`
- Test: `src/test/java/com/dt/example/algorithm/BubbleSortServiceTest.java`
- Test: `src/test/java/com/dt/example/web/BusinessControllerTest.java`
- Test: `src/test/java/com/dt/example/metrics/MetricsServiceTest.java`
- Test: `src/test/java/com/dt/example/export/ExportServiceTest.java`

### [ranxitest] 前端
- Create: `package.json`
- Create: `vite.config.js`
- Create: `index.html`
- Create: `src/main.js`
- Create: `src/style.css`
- Create: `src/App.vue`
- Create: `src/api/index.js`
- Create: `src/views/ThreeTabs.vue`
- Create: `src/components/MetricsCharts.vue`

---

# Part A —— [testDj] 后端

## Task 1: Spring Boot 工程化 + 统一响应 Result

**Files:**
- Modify: `testDj/pom.xml`
- Create: `testDj/src/main/java/com/dt/example/Application.java`
- Create: `testDj/src/main/java/com/dt/example/common/Result.java`

**Interfaces:**
- Produces: `com.dt.example.common.Result<T>`，构造/工厂方法 `Result.ok(data)`、`Result.error(code, message)`。启动类 `com.dt.example.Application`。

- [ ] **Step 1: 替换 pom.xml 为 Spring Boot 3 结构**

`pom.xml` 完整内容：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.5</version>
        <relativePath/>
    </parent>

    <groupId>com.dt.example</groupId>
    <artifactId>hello-world</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>HelloWorld</name>
    <description>Hello World + hash + bubble sort + metrics + export demo</description>

    <properties>
        <java.version>21</java.version>
        <poi.version>5.2.5</poi.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.poi</groupId>
            <artifactId>poi-ooxml</artifactId>
            <version>${poi.version}</version>
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

- [ ] **Step 2: 创建启动类**

`src/main/java/com/dt/example/Application.java`：

```java
package com.dt.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 应用入口。
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

- [ ] **Step 3: 创建统一响应 Result**

`src/main/java/com/dt/example/common/Result.java`：

```java
package com.dt.example.common;

/**
 * 统一响应结构。
 *
 * @param code    状态码，0 表示成功
 * @param message 提示信息
 * @param data    业务数据，失败时为 null
 * @param <T>     业务数据类型
 */
public record Result<T>(int code, String message, T data) {

    public static <T> Result<T> ok(T data) {
        return new Result<>(0, "ok", data);
    }

    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message, null);
    }
}
```

- [ ] **Step 4: 验证编译通过**

Run: `cd /root/.agentix/agentic-dev/runs/DEV-d885ee0d-78e2-11f1-8f3f-75954cb1c56f-02a853e4-5eda-4c6d-b754-838828eb0c80/worktree/testDj-AI-task-DEV-9d10e310-7901-11f1-8a9f-59ecae612580-0b823fae-c0c6-45c3 && mvn -q -DskipTests compile`
Expected: exit 0（BUILD SUCCESS）。

- [ ] **Step 5: Commit**

```bash
git add pom.xml src/main/java/com/dt/example/Application.java src/main/java/com/dt/example/common/Result.java
git commit -m "chore(backend): 引入 Spring Boot 3 与统一响应 Result"
```

---

## Task 2: MD5 哈希与冒泡排序算法服务

**Files:**
- Create: `testDj/src/main/java/com/dt/example/algorithm/HashService.java`
- Create: `testDj/src/main/java/com/dt/example/algorithm/BubbleSortService.java`
- Test: `testDj/src/test/java/com/dt/example/algorithm/HashServiceTest.java`
- Test: `testDj/src/test/java/com/dt/example/algorithm/BubbleSortServiceTest.java`

**Interfaces:**
- Consumes: 无（纯算法）。
- Produces: `HashService.md5Hex(String): String`（十六进制小写）；`BubbleSortService.sort(int[]): int[]`（升序，返回新数组，不修改入参）。

- [ ] **Step 1: 写失败测试**

`src/test/java/com/dt/example/algorithm/HashServiceTest.java`：

```java
package com.dt.example.algorithm;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("HashService 测试")
class HashServiceTest {

    private final HashService hashService = new HashService();

    @Test
    @DisplayName("abc 的 MD5 应为固定十六进制值")
    void should_hashMd5_when_inputGiven() {
        assertThat(hashService.md5Hex("abc"))
            .isEqualTo("900150983cd24fb0d6963f7d28e17f72");
    }

    @Test
    @DisplayName("空串的 MD5 应为固定十六进制值")
    void should_hashEmptyString() {
        assertThat(hashService.md5Hex(""))
            .isEqualTo("d41d8cd98f00b204e9800998ecf8427e");
    }
}
```

`src/test/java/com/dt/example/algorithm/BubbleSortServiceTest.java`：

```java
package com.dt.example.algorithm;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("BubbleSortService 测试")
class BubbleSortServiceTest {

    private final BubbleSortService service = new BubbleSortService();

    @Test
    @DisplayName("乱序数组应升序排序")
    void should_sortAscending() {
        assertThat(service.sort(new int[]{5, 3, 8, 1, 9}))
            .containsExactly(1, 3, 5, 8, 9);
    }

    @Test
    @DisplayName("已排序数组应保持不变")
    void should_keepSorted() {
        assertThat(service.sort(new int[]{1, 2, 3}))
            .containsExactly(1, 2, 3);
    }

    @Test
    @DisplayName("空数组应安全返回空数组")
    void should_handleEmpty() {
        assertThat(service.sort(new int[]{})).isEmpty();
    }

    @Test
    @DisplayName("不应修改入参数组")
    void should_notMutateInput() {
        int[] input = {3, 1, 2};
        service.sort(input);
        assertThat(input).containsExactly(3, 1, 2);
    }
}
```

- [ ] **Step 2: 运行测试确认失败**

Run: `./mvnw test -Dtest=HashServiceTest,BubbleSortServiceTest`（本项目已无 wrapper，用 `mvn -q -Dtest=HashServiceTest,BubbleSortServiceTest test`）
Expected: FAIL（编译错误：类不存在）。

- [ ] **Step 3: 实现两个算法服务**

`src/main/java/com/dt/example/algorithm/HashService.java`：

```java
package com.dt.example.algorithm;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 哈希算法服务，提供 MD5 十六进制摘要。
 */
@Service
public class HashService {

    /**
     * 计算输入文本的 MD5 摘要（十六进制小写）。
     *
     * @param input 输入文本
     * @return 32 位小写十六进制摘要
     */
    public String md5Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(digest.length * 2);
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("MD5 算法不可用", e);
        }
    }
}
```

`src/main/java/com/dt/example/algorithm/BubbleSortService.java`：

```java
package com.dt.example.algorithm;

import org.springframework.stereotype.Service;

/**
 * 冒泡排序服务。
 */
@Service
public class BubbleSortService {

    /**
     * 返回升序排序后的新数组，不修改入参。
     *
     * @param numbers 整数数组
     * @return 升序新数组
     */
    public int[] sort(int[] numbers) {
        int[] result = numbers.clone();
        for (int i = 0; i < result.length - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < result.length - 1 - i; j++) {
                if (result[j] > result[j + 1]) {
                    int tmp = result[j];
                    result[j] = result[j + 1];
                    result[j + 1] = tmp;
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }
        return result;
    }
}
```

- [ ] **Step 4: 运行测试确认通过**

Run: `mvn -q -Dtest=HashServiceTest,BubbleSortServiceTest test`
Expected: PASS（Tests run 全部通过）。

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/dt/example/algorithm src/test/java/com/dt/example/algorithm
git commit -m "feat(backend): 新增 MD5 哈希与冒泡排序算法服务"
```

---

## Task 3: 业务接口（helloWorld / hash / bubble）

**Files:**
- Create: `testDj/src/main/java/com/dt/example/web/BusinessController.java`
- Test: `testDj/src/test/java/com/dt/example/web/BusinessControllerTest.java`

**Interfaces:**
- Consumes: `HashService.md5Hex`、`BubbleSortService.sort`、`Result`。
- Produces: `GET /api/helloWorld`、`POST /api/hash`、`POST /api/sort/bubble` 的响应结构（见契约表）。

- [ ] **Step 1: 写失败测试（MockMvc）**

`src/test/java/com/dt/example/web/BusinessControllerTest.java`：

```java
package com.dt.example.web;

import com.dt.example.algorithm.BubbleSortService;
import com.dt.example.algorithm.HashService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BusinessController.class)
@Import({HashService.class, BubbleSortService.class})
@DisplayName("BusinessController 测试")
class BusinessControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/helloWorld 应返回固定问候语")
    void helloWorld() throws Exception {
        mockMvc.perform(get("/api/helloWorld"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0))
            .andExpect(jsonPath("$.data").value("Hello, World!"));
    }

    @Test
    @DisplayName("POST /api/hash 应返回 MD5")
    void hash() throws Exception {
        String body = objectMapper.writeValueAsString(
            new BusinessController.HashRequest("abc"));
        mockMvc.perform(post("/api/hash")
                .contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0))
            .andExpect(jsonPath("$.data.algorithm").value("MD5"))
            .andExpect(jsonPath("$.data.hash")
                .value("900150983cd24fb0d6963f7d28e17f72"));
    }

    @Test
    @DisplayName("POST /api/sort/bubble 应返回升序数组")
    void bubbleSort() throws Exception {
        String body = "{\"numbers\":[5,3,8,1]}";
        mockMvc.perform(post("/api/sort/bubble")
                .contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0))
            .andExpect(jsonPath("$.data.sorted[0]").value(1))
            .andExpect(jsonPath("$.data.sorted[3]").value(8));
    }
}
```

- [ ] **Step 2: 运行测试确认失败**

Run: `mvn -q -Dtest=BusinessControllerTest test`
Expected: FAIL（找不到 BusinessController）。

- [ ] **Step 3: 实现 BusinessController**

`src/main/java/com/dt/example/web/BusinessController.java`：

```java
package com.dt.example.web;

import com.dt.example.algorithm.BubbleSortService;
import com.dt.example.algorithm.HashService;
import com.dt.example.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 三个演示业务接口。
 */
@RestController
@RequestMapping("/api")
public class BusinessController {

    private final HashService hashService;
    private final BubbleSortService bubbleSortService;

    public BusinessController(HashService hashService, BubbleSortService bubbleSortService) {
        this.hashService = hashService;
        this.bubbleSortService = bubbleSortService;
    }

    @GetMapping("/helloWorld")
    public Result<String> helloWorld() {
        return Result.ok("Hello, World!");
    }

    @PostMapping("/hash")
    public Result<Map<String, Object>> hash(@RequestBody HashRequest request) {
        if (request.text() == null) {
            throw new IllegalArgumentException("text 不能为空");
        }
        String hash = hashService.md5Hex(request.text());
        return Result.ok(Map.of(
            "algorithm", "MD5",
            "input", request.text(),
            "hash", hash
        ));
    }

    @PostMapping("/sort/bubble")
    public Result<Map<String, Object>> bubbleSort(@RequestBody SortRequest request) {
        if (request.numbers() == null) {
            throw new IllegalArgumentException("numbers 不能为空");
        }
        int[] sorted = bubbleSortService.sort(request.numbers());
        return Result.ok(Map.of(
            "algorithm", "bubble-sort",
            "input", request.numbers(),
            "sorted", sorted
        ));
    }

    public record HashRequest(String text) {
    }

    public record SortRequest(int[] numbers) {
    }
}
```

- [ ] **Step 4: 运行测试确认通过**

Run: `mvn -q -Dtest=BusinessControllerTest test`
Expected: PASS。

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/dt/example/web/BusinessController.java src/test/java/com/dt/example/web/BusinessControllerTest.java
git commit -m "feat(backend): 新增 helloworld/hash/bubble 业务接口"
```

---

## Task 4: 埋点基础设施（人员字典 + 收集器 + 拦截器）

**Files:**
- Create: `testDj/src/main/java/com/dt/example/metrics/Person.java`
- Create: `testDj/src/main/java/com/dt/example/metrics/PersonDictionary.java`
- Create: `testDj/src/main/java/com/dt/example/metrics/MetricsCollector.java`
- Create: `testDj/src/main/java/com/dt/example/metrics/MetricsInterceptor.java`
- Create: `testDj/src/main/java/com/dt/example/web/WebConfig.java`

**Interfaces:**
- Produces:
  - `Person(String id, String name, String type, String level, String department)`
  - `PersonDictionary.find(String id): Person`（未知 id 返回 `type/level/department="未知"`）；`PersonDictionary.all(): List<Person>`
  - `MetricsCollector.record(String callerId, String api)`；`MetricsCollector.all(): List<Event>`；`Event(String callerId, String api, Instant timestamp)`
  - 拦截器记录范围：`/api/helloWorld`、`/api/hash`、`/api/sort/bubble`；请求头 `X-Caller-Id`，缺省 `anonymous`。

- [ ] **Step 1: 创建 Person 与人员字典**

`src/main/java/com/dt/example/metrics/Person.java`：

```java
package com.dt.example.metrics;

/**
 * 人员及其组织维度。
 */
public record Person(String id, String name, String type, String level, String department) {
}
```

`src/main/java/com/dt/example/metrics/PersonDictionary.java`：

```java
package com.dt.example.metrics;

import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 进程内人员字典，维护调用人的组织维度。
 */
@Component
public class PersonDictionary {

    private final Map<String, Person> persons = new LinkedHashMap<>();

    public PersonDictionary() {
        register(new Person("u001", "张三", "正式员工", "P6", "研发部"));
        register(new Person("u002", "李四", "正式员工", "P5", "产品部"));
        register(new Person("u003", "王五", "外包", "P4", "研发部"));
        register(new Person("u004", "赵六", "实习生", "P3", "市场部"));
    }

    private void register(Person person) {
        persons.put(person.id(), person);
    }

    public Person find(String id) {
        return persons.getOrDefault(id, new Person(id, id, "未知", "未知", "未知"));
    }

    public List<Person> all() {
        return List.copyOf(persons.values());
    }
}
```

- [ ] **Step 2: 创建埋点收集器**

`src/main/java/com/dt/example/metrics/MetricsCollector.java`：

```java
package com.dt.example.metrics;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 埋点收集器，进程内内存暂存调用事件。
 */
@Component
public class MetricsCollector {

    private final List<Event> events = new CopyOnWriteArrayList<>();

    public record Event(String callerId, String api, Instant timestamp) {
    }

    public void record(String callerId, String api) {
        events.add(new Event(callerId, api, Instant.now()));
    }

    public List<Event> all() {
        return List.copyOf(events);
    }
}
```

- [ ] **Step 3: 创建埋点拦截器**

`src/main/java/com/dt/example/metrics/MetricsInterceptor.java`：

```java
package com.dt.example.metrics;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 对业务接口调用进行埋点：从请求头 X-Caller-Id 获取调用人。
 */
public class MetricsInterceptor implements HandlerInterceptor {

    public static final String CALLER_HEADER = "X-Caller-Id";

    private final MetricsCollector collector;

    public MetricsInterceptor(MetricsCollector collector) {
        this.collector = collector;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        String caller = request.getHeader(CALLER_HEADER);
        if (caller == null || caller.isBlank()) {
            caller = "anonymous";
        }
        collector.record(caller, request.getRequestURI());
    }
}
```

- [ ] **Step 4: 注册拦截器与 CORS**

`src/main/java/com/dt/example/web/WebConfig.java`：

```java
package com.dt.example.web;

import com.dt.example.metrics.MetricsCollector;
import com.dt.example.metrics.MetricsInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置：自定义埋点拦截器与跨域策略。
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final MetricsCollector collector;

    public WebConfig(MetricsCollector collector) {
        this.collector = collector;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new MetricsInterceptor(collector))
            .addPathPatterns("/api/helloWorld", "/api/hash", "/api/sort/bubble");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOriginPatterns("*")
            .allowedMethods("GET", "POST", "OPTIONS");
    }
}
```

- [ ] **Step 5: 编译验证**

Run: `mvn -q -DskipTests compile`
Expected: exit 0。

- [ ] **Step 6: Commit**

```bash
git add src/main/java/com/dt/example/metrics src/main/java/com/dt/example/web/WebConfig.java
git commit -m "feat(backend): 新增埋点人员字典、收集器与拦截器"
```

---

## Task 5: 报表查询接口（多维聚合）

**Files:**
- Create: `testDj/src/main/java/com/dt/example/metrics/ChartSeries.java`
- Create: `testDj/src/main/java/com/dt/example/metrics/MetricsQueryResponse.java`
- Create: `testDj/src/main/java/com/dt/example/metrics/MetricsService.java`
- Create: `testDj/src/main/java/com/dt/example/web/MetricsController.java`
- Create: `testDj/src/main/java/com/dt/example/web/GlobalExceptionHandler.java`
- Test: `testDj/src/test/java/com/dt/example/metrics/MetricsServiceTest.java`

**Interfaces:**
- Consumes: `MetricsCollector`、`PersonDictionary`、`Result`。
- Produces:
  - `MetricsService.query(String dimension, String chartType): MetricsQueryResponse`
  - `GET /api/metrics/query`、`GET /api/metrics/persons`
  - 维度枚举：`personType`/`personLevel`/`personDepartment`；图表枚举：`line`/`pie`/`bar`。

- [ ] **Step 1: 写失败测试**

`src/test/java/com/dt/example/metrics/MetricsServiceTest.java`：

```java
package com.dt.example.metrics;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MetricsService 测试")
class MetricsServiceTest {

    private final MetricsCollector collector = new MetricsCollector();
    private final PersonDictionary dictionary = new PersonDictionary();
    private final MetricsService service = new MetricsService(collector, dictionary);

    @Test
    @DisplayName("pie 应按部门聚合并保留类别")
    void pie_byDepartment() {
        collector.record("u001", "/api/helloWorld");
        collector.record("u002", "/api/hash");
        collector.record("u003", "/api/sort/bubble");

        MetricsQueryResponse resp = service.query("personDepartment", "pie");

        assertThat(resp.total()).isEqualTo(3);
        assertThat(resp.categories()).contains("研发部", "产品部");
        assertThat(resp.series()).hasSize(1);
    }

    @Test
    @DisplayName("bar 应按接口拆分系列")
    void bar_byType() {
        collector.record("u001", "/api/helloWorld");
        collector.record("u003", "/api/helloWorld");

        MetricsQueryResponse resp = service.query("personType", "bar");

        assertThat(resp.series()).hasSize(3);
        assertThat(resp.series().get(0).name()).isEqualTo("helloworld");
    }

    @Test
    @DisplayName("line 应按时间桶聚合")
    void line_byTime() {
        collector.record("u001", "/api/helloWorld");
        MetricsQueryResponse resp = service.query("time", "line");
        assertThat(resp.series()).hasSize(1);
        assertThat(resp.total()).isEqualTo(1);
    }

    @Test
    @DisplayName("未知维度应抛 IllegalArgumentException")
    void unknown_dimension() {
        org.junit.jupiter.api.Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> service.query("badDim", "pie"));
    }
}
```

- [ ] **Step 2: 运行测试确认失败**

Run: `mvn -q -Dtest=MetricsServiceTest test`
Expected: FAIL（类不存在）。

- [ ] **Step 3: 实现 DTO 与聚合服务**

`src/main/java/com/dt/example/metrics/ChartSeries.java`：

```java
package com.dt.example.metrics;

import java.util.List;

public record ChartSeries(String name, List<Double> values) {
}
```

`src/main/java/com/dt/example/metrics/MetricsQueryResponse.java`：

```java
package com.dt.example.metrics;

import java.util.List;

public record MetricsQueryResponse(
    String dimension,
    String chartType,
    List<String> categories,
    List<ChartSeries> series,
    long total) {
}
```

`src/main/java/com/dt/example/metrics/MetricsService.java`：

```java
package com.dt.example.metrics;

import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;

/**
 * 埋点聚合服务，按维度与图表形态输出报表数据。
 */
@Service
public class MetricsService {

    private static final DateTimeFormatter TIME_FORMAT =
        DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault());
    private static final List<String> API_ORDER =
        List.of("/api/helloWorld", "/api/hash", "/api/sort/bubble");

    private final MetricsCollector collector;
    private final PersonDictionary dictionary;

    public MetricsService(MetricsCollector collector, PersonDictionary dictionary) {
        this.collector = collector;
        this.dictionary = dictionary;
    }

    public MetricsQueryResponse query(String dimension, String chartType) {
        List<MetricsCollector.Event> events = collector.all();
        return switch (chartType) {
            case "pie" -> pie(dimension, events);
            case "bar" -> bar(dimension, events);
            case "line" -> line(events);
            default -> throw new IllegalArgumentException("unsupported chartType: " + chartType);
        };
    }

    private MetricsQueryResponse pie(String dimension, List<MetricsCollector.Event> events) {
        LinkedHashMap<String, Long> counts = grouped(dimension, events);
        List<String> categories = List.copyOf(counts.keySet());
        List<Double> values = categories.stream()
            .map(c -> (double) counts.get(c)).toList();
        return new MetricsQueryResponse(dimension, "pie", categories,
            List.of(new ChartSeries("调用次数", values)), events.size());
    }

    private MetricsQueryResponse bar(String dimension, List<MetricsCollector.Event> events) {
        LinkedHashMap<String, Long> counts = grouped(dimension, events);
        List<String> categories = List.copyOf(counts.keySet());
        List<ChartSeries> series = new ArrayList<>();
        for (String api : API_ORDER) {
            List<Double> values = categories.stream()
                .map(c -> (double) events.stream()
                    .filter(e -> e.api().equals(api)
                        && dimensionValue(e, dimension).equals(c))
                    .count())
                .toList();
            series.add(new ChartSeries(apiName(api), values));
        }
        return new MetricsQueryResponse(dimension, "bar", categories, series, events.size());
    }

    private MetricsQueryResponse line(List<MetricsCollector.Event> events) {
        TreeMap<String, Long> buckets = new TreeMap<>();
        for (MetricsCollector.Event e : events) {
            buckets.merge(TIME_FORMAT.format(e.timestamp()), 1L, Long::sum);
        }
        List<String> categories = List.copyOf(buckets.keySet());
        List<Double> values = categories.stream()
            .map(c -> (double) buckets.get(c)).toList();
        return new MetricsQueryResponse("time", "line", categories,
            List.of(new ChartSeries("调用次数", values)), events.size());
    }

    private LinkedHashMap<String, Long> grouped(String dimension,
                                                List<MetricsCollector.Event> events) {
        LinkedHashMap<String, Long> counts = new LinkedHashMap<>();
        for (MetricsCollector.Event e : events) {
            counts.merge(dimensionValue(e, dimension), 1L, Long::sum);
        }
        return counts;
    }

    private String dimensionValue(MetricsCollector.Event e, String dimension) {
        Person p = dictionary.find(e.callerId());
        return switch (dimension) {
            case "personType" -> p.type();
            case "personLevel" -> p.level();
            case "personDepartment" -> p.department();
            default -> throw new IllegalArgumentException("unsupported dimension: " + dimension);
        };
    }

    private String apiName(String api) {
        return switch (api) {
            case "/api/helloWorld" -> "helloworld";
            case "/api/hash" -> "hash";
            case "/api/sort/bubble" -> "bubble";
            default -> api;
        };
    }
}
```

- [ ] **Step 4: 实现 MetricsController 与全局异常处理**

`src/main/java/com/dt/example/web/MetricsController.java`：

```java
package com.dt.example.web;

import com.dt.example.common.Result;
import com.dt.example.metrics.MetricsQueryResponse;
import com.dt.example.metrics.MetricsService;
import com.dt.example.metrics.Person;
import com.dt.example.metrics.PersonDictionary;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 埋点报表与人员字典接口。
 */
@RestController
@RequestMapping("/api/metrics")
public class MetricsController {

    private final MetricsService metricsService;
    private final PersonDictionary dictionary;

    public MetricsController(MetricsService metricsService, PersonDictionary dictionary) {
        this.metricsService = metricsService;
        this.dictionary = dictionary;
    }

    @GetMapping("/query")
    public Result<MetricsQueryResponse> query(
        @RequestParam(defaultValue = "personType") String dimension,
        @RequestParam(defaultValue = "pie") String chartType) {
        return Result.ok(metricsService.query(dimension, chartType));
    }

    @GetMapping("/persons")
    public Result<List<Person>> persons() {
        return Result.ok(dictionary.all());
    }
}
```

`src/main/java/com/dt/example/web/GlobalExceptionHandler.java`：

```java
package com.dt.example.web;

import com.dt.example.common.Result;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理，统一输出 Result 结构。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleBadRequest(IllegalArgumentException e) {
        return Result.error(400, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleServerError(Exception e) {
        return Result.error(500, e.getMessage());
    }
}
```

- [ ] **Step 5: 运行测试确认通过**

Run: `mvn -q -Dtest=MetricsServiceTest test`
Expected: PASS。

- [ ] **Step 6: 全量测试**

Run: `mvn -q test`
Expected: 全部通过，BUILD SUCCESS。

- [ ] **Step 7: Commit**

```bash
git add src/main/java/com/dt/example/metrics src/main/java/com/dt/example/web/MetricsController.java src/main/java/com/dt/example/web/GlobalExceptionHandler.java src/test/java/com/dt/example/metrics
git commit -m "feat(backend): 新增多维埋点报表查询接口"
```

---

## Task 6: 导出接口（JSON / CSV / XLSX）

**Files:**
- Create: `testDj/src/main/java/com/dt/example/export/ExportService.java`
- Create: `testDj/src/main/java/com/dt/example/web/ExportController.java`
- Test: `testDj/src/test/java/com/dt/example/export/ExportServiceTest.java`

**Interfaces:**
- Consumes: `Result`（间接），`ObjectMapper`（Spring 注入）。
- Produces: `POST /api/export`；`ExportService.export(String format, String page, List<Map<String,Object>> rows): ExportFile`（`ExportFile(byte[] content, String filename, String contentType)`）。

- [ ] **Step 1: 写失败测试**

`src/test/java/com/dt/example/export/ExportServiceTest.java`：

```java
package com.dt.example.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ExportService 测试")
class ExportServiceTest {

    private final ExportService service = new ExportService(new ObjectMapper());
    private final List<Map<String, Object>> rows =
        List.of(Map.of("key", "greeting", "value", "Hello, World!"));

    @Test
    @DisplayName("JSON 导出应返回 JSON 字节")
    void json() throws Exception {
        ExportService.ExportFile file = service.export("json", "helloworld", rows);
        assertThat(file.filename()).isEqualTo("helloworld.json");
        assertThat(new String(file.content(), StandardCharsets.UTF_8))
            .contains("\"key\"");
    }

    @Test
    @DisplayName("CSV 导出应包含表头")
    void csv() throws Exception {
        ExportService.ExportFile file = service.export("csv", "helloworld", rows);
        assertThat(file.filename()).isEqualTo("helloworld.csv");
        assertThat(new String(file.content(), StandardCharsets.UTF_8))
            .contains("key,value");
    }

    @Test
    @DisplayName("XLSX 导出应产生非空内容")
    void xlsx() throws Exception {
        ExportService.ExportFile file = service.export("xlsx", "helloworld", rows);
        assertThat(file.filename()).isEqualTo("helloworld.xlsx");
        assertThat(file.content()).isNotEmpty();
    }

    @Test
    @DisplayName("不支持的格式应抛异常")
    void unsupported() {
        assertThatThrownBy(() -> service.export("pdf", "helloworld", rows))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
```

- [ ] **Step 2: 运行测试确认失败**

Run: `mvn -q -Dtest=ExportServiceTest test`
Expected: FAIL（类不存在）。

- [ ] **Step 3: 实现 ExportService**

`src/main/java/com/dt/example/export/ExportService.java`：

```java
package com.dt.example.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 导出服务：JSON / CSV / XLSX 三种格式。
 */
@Service
public class ExportService {

    private final ObjectMapper objectMapper;

    public ExportService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public record ExportFile(byte[] content, String filename, String contentType) {
    }

    public ExportFile export(String format, String page, List<Map<String, Object>> rows)
        throws Exception {
        return switch (format.toLowerCase(Locale.ROOT)) {
            case "json" -> new ExportFile(
                objectMapper.writeValueAsBytes(rows == null ? List.of() : rows),
                name(page, "json"), "application/json;charset=UTF-8");
            case "csv" -> new ExportFile(toCsv(rows), name(page, "csv"),
                "text/csv;charset=UTF-8");
            case "xlsx" -> new ExportFile(toXlsx(rows), name(page, "xlsx"),
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            default -> throw new IllegalArgumentException("unsupported format: " + format);
        };
    }

    private String name(String page, String ext) {
        return (page == null || page.isBlank() ? "export" : page) + "." + ext;
    }

    private LinkedHashSet<String> headers(List<Map<String, Object>> rows) {
        LinkedHashSet<String> headers = new LinkedHashSet<>();
        if (rows == null) {
            return headers;
        }
        for (Map<String, Object> row : rows) {
            if (row != null) {
                headers.addAll(row.keySet());
            }
        }
        return headers;
    }

    private byte[] toCsv(List<Map<String, Object>> rows) {
        LinkedHashSet<String> headers = headers(rows);
        StringBuilder sb = new StringBuilder("\uFEFF"); // BOM 让 Excel 正确识别 UTF-8
        sb.append(String.join(",", headers)).append("\n");
        if (rows != null) {
            for (Map<String, Object> row : rows) {
                List<String> cells = new ArrayList<>();
                for (String h : headers) {
                    Object v = row == null ? null : row.get(h);
                    cells.add(escape(v == null ? "" : String.valueOf(v)));
                }
                sb.append(String.join(",", cells)).append("\n");
            }
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String escape(String s) {
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }

    private byte[] toXlsx(List<Map<String, Object>> rows) throws Exception {
        LinkedHashSet<String> headers = headers(rows);
        try (Workbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet("result");
            Row header = sheet.createRow(0);
            int c = 0;
            for (String h : headers) {
                header.createCell(c++).setCellValue(h);
            }
            int r = 1;
            if (rows != null) {
                for (Map<String, Object> row : rows) {
                    Row xr = sheet.createRow(r++);
                    c = 0;
                    for (String h : headers) {
                        Object v = row == null ? null : row.get(h);
                        Cell cell = xr.createCell(c++);
                        if (v instanceof Number n) {
                            cell.setCellValue(n.doubleValue());
                        } else {
                            cell.setCellValue(v == null ? "" : String.valueOf(v));
                        }
                    }
                }
            }
            wb.write(out);
            return out.toByteArray();
        }
    }
}
```

- [ ] **Step 4: 实现 ExportController**

`src/main/java/com/dt/example/web/ExportController.java`：

```java
package com.dt.example.web;

import com.dt.example.export.ExportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 导出接口：接收前端页面展示结果，转换为指定格式文件返回。
 */
@RestController
@RequestMapping("/api")
public class ExportController {

    private final ExportService exportService;

    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    @PostMapping("/export")
    public ResponseEntity<byte[]> export(@RequestBody ExportRequest request) throws Exception {
        ExportService.ExportFile file = exportService.export(
            request.format(), request.page(), request.rows());
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.filename() + "\"")
            .contentType(MediaType.parseMediaType(file.contentType()))
            .body(file.content());
    }

    public record ExportRequest(String format, String page, List<Map<String, Object>> rows) {
    }
}
```

- [ ] **Step 5: 运行测试确认通过**

Run: `mvn -q -Dtest=ExportServiceTest test`
Expected: PASS。

- [ ] **Step 6: 全量测试**

Run: `mvn -q test`
Expected: 全部通过。

- [ ] **Step 7: Commit**

```bash
git add src/main/java/com/dt/example/export src/main/java/com/dt/example/web/ExportController.java src/test/java/com/dt/example/export
git commit -m "feat(backend): 新增 JSON/CSV/XLSX 导出接口"
```

---

# Part B —— [ranxitest] 前端

## Task 7: 前端工程初始化

**Files:**
- Create: `ranxitest/package.json`
- Create: `ranxitest/vite.config.js`
- Create: `ranxitest/index.html`
- Create: `ranxitest/src/main.js`
- Create: `ranxitest/src/style.css`
- Create: `ranxitest/src/App.vue`

**Interfaces:**
- Produces: dev 端口 5173；`/api` 代理到 `http://localhost:8080`；根组件渲染 `ThreeTabs`。

- [ ] **Step 1: 创建 package.json**

```json
{
  "name": "ranxitest-frontend",
  "private": true,
  "version": "0.0.1",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "vite build",
    "preview": "vite preview"
  },
  "dependencies": {
    "axios": "^1.7.2",
    "echarts": "^5.5.0",
    "vue": "^3.4.29"
  },
  "devDependencies": {
    "@vitejs/plugin-vue": "^5.0.5",
    "vite": "^5.3.1"
  }
}
```

- [ ] **Step 2: 创建 vite.config.js**

```js
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

- [ ] **Step 3: 创建 index.html**

```html
<!doctype html>
<html lang="zh-CN">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>接口演示与调用报表</title>
  </head>
  <body>
    <div id="app"></div>
    <script type="module" src="/src/main.js"></script>
  </body>
</html>
```

- [ ] **Step 4: 创建 main.js 与 style.css**

`src/main.js`：

```js
import { createApp } from 'vue'
import App from './App.vue'
import './style.css'

createApp(App).mount('#app')
```

`src/style.css`：

```css
* { box-sizing: border-box; }
body {
  margin: 0;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', sans-serif;
  background: #f5f6f8;
  color: #1f2329;
}
.page { max-width: 960px; margin: 0 auto; padding: 24px; }
h1 { font-size: 22px; }
.tabs { display: flex; gap: 8px; margin: 16px 0; }
.tabs button {
  padding: 8px 16px; border: 1px solid #d9d9d9; background: #fff; cursor: pointer;
  border-radius: 6px;
}
.tabs button.active { background: #1677ff; color: #fff; border-color: #1677ff; }
.panel {
  background: #fff; border: 1px solid #eee; border-radius: 8px;
  padding: 16px; margin-bottom: 16px;
}
.panel input, select {
  padding: 6px 8px; border: 1px solid #d9d9d9; border-radius: 6px; margin-right: 8px;
}
.panel button, .export-bar button, .metrics-bar button {
  padding: 6px 14px; border: 1px solid #1677ff; background: #1677ff; color: #fff;
  border-radius: 6px; cursor: pointer;
}
.result { margin-top: 12px; }
.export-bar, .metrics-bar, .caller-bar, .chart-wrap {
  background: #fff; border: 1px solid #eee; border-radius: 8px; padding: 16px; margin-bottom: 16px;
}
.metrics-bar { display: flex; align-items: center; gap: 8px; }
```

- [ ] **Step 5: 创建 App.vue**

```vue
<script setup>
import ThreeTabs from './views/ThreeTabs.vue'
</script>

<template>
  <ThreeTabs />
</template>
```

- [ ] **Step 6: 安装依赖并演示构建**

Run: `cd /root/.agentix/agentic-dev/runs/DEV-d885ee0d-78e2-11f1-8f3f-75954cb1c56f-02a853e4-5eda-4c6d-b754-838828eb0c80/worktree/ranxitest-AI-task-AUTO-root-b547e38a-26e5-42a6-b69d-4741f6b33b54 && npm install`
Expected: 安装成功无 error。

- [ ] **Step 7: Commit**

```bash
git add package.json package-lock.json vite.config.js index.html src
git commit -m "chore(frontend): 初始化 Vue3 + Vite 工程"
```

---

## Task 8: API 封装与三 tab 结果页

**Files:**
- Create: `ranxitest/src/api/index.js`
- Create: `ranxitest/src/views/ThreeTabs.vue`

**Interfaces:**
- Consumes: 后端 `Result<T>`、`X-Caller-Id` 请求头、`/api/metrics/persons`。
- Produces:
  - `callHelloWorld(callerId)`、`callHash(text, callerId)`、`callBubbleSort(numbers, callerId)`、`exportResult(payload)`、`queryMetrics(dimension, chartType)`、`listPersons()`

- [ ] **Step 1: 创建 API 封装**

`src/api/index.js`：

```js
import axios from 'axios'

const api = axios.create({ baseURL: '/api', timeout: 10000 })

export function callHelloWorld(callerId) {
  return api.get('/helloWorld', { headers: { 'X-Caller-Id': callerId } })
}

export function callHash(text, callerId) {
  return api.post('/hash', { text }, { headers: { 'X-Caller-Id': callerId } })
}

export function callBubbleSort(numbers, callerId) {
  return api.post('/sort/bubble', { numbers }, { headers: { 'X-Caller-Id': callerId } })
}

export function exportResult(payload) {
  return api.post('/export', payload, { responseType: 'blob' })
}

export function queryMetrics(dimension, chartType) {
  return api.get('/metrics/query', { params: { dimension, chartType } })
}

export function listPersons() {
  return api.get('/metrics/persons')
}
```

- [ ] **Step 2: 创建三 tab 页面**

`src/views/ThreeTabs.vue`：

```vue
<script setup>
import { ref, onMounted } from 'vue'
import {
  callHelloWorld, callHash, callBubbleSort,
  exportResult, listPersons, queryMetrics
} from '../api'
import MetricsCharts from '../components/MetricsCharts.vue'

const activeTab = ref('helloworld')
const callerId = ref('u001')
const persons = ref([])

const helloResult = ref('')
const hashInput = ref('abc')
const hashResult = ref('')
const sortInput = ref('5, 3, 8, 1, 9')
const sortResult = ref('')

const format = ref('json')
const metrics = ref({ dimension: 'personType', chartType: 'pie' })
const metricsData = ref(null)

const TABS = [
  { key: 'helloworld', label: 'HelloWorld' },
  { key: 'hash', label: '哈希算法(MD5)' },
  { key: 'bubble', label: '冒泡排序' }
]

async function runHello() {
  const { data } = await callHelloWorld(callerId.value)
  helloResult.value = data.data
}

async function runHash() {
  const { data } = await callHash(hashInput.value, callerId.value)
  hashResult.value = data.data.hash
}

async function runBubble() {
  const nums = sortInput.value.split(',')
    .map(s => parseInt(s.trim(), 10))
    .filter(n => !Number.isNaN(n))
  const { data } = await callBubbleSort(nums, callerId.value)
  sortResult.value = data.data.sorted.join(', ')
}

function currentRows() {
  if (activeTab.value === 'helloworld') {
    return [{ key: 'greeting', value: helloResult.value }]
  }
  if (activeTab.value === 'hash') {
    return [
      { key: 'input', value: hashInput.value },
      { key: 'md5', value: hashResult.value }
    ]
  }
  return [
    { key: 'input', value: sortInput.value },
    { key: 'sorted', value: sortResult.value }
  ]
}

async function doExport() {
  const { data } = await exportResult({
    format: format.value,
    page: activeTab.value,
    rows: currentRows()
  })
  const url = URL.createObjectURL(data)
  const a = document.createElement('a')
  a.href = url
  a.download = `${activeTab.value}.${format.value}`
  a.click()
  URL.revokeObjectURL(url)
}

async function loadMetrics() {
  const { data } = await queryMetrics(metrics.value.dimension, metrics.value.chartType)
  metricsData.value = data.data
}

onMounted(async () => {
  const { data } = await listPersons()
  persons.value = data.data
  await loadMetrics()
})
</script>

<template>
  <div class="page">
    <h1>接口演示与调用报表</h1>

    <div class="caller-bar">
      调用人：
      <select v-model="callerId">
        <option v-for="p in persons" :key="p.id" :value="p.id">
          {{ p.name }}（{{ p.type }} / {{ p.level }} / {{ p.department }}）
        </option>
      </select>
    </div>

    <div class="tabs">
      <button
        v-for="t in TABS"
        :key="t.key"
        :class="{ active: activeTab === t.key }"
        @click="activeTab = t.key"
      >
        {{ t.label }}
      </button>
    </div>

    <div v-if="activeTab === 'helloworld'" class="panel">
      <button @click="runHello">执行 HelloWorld</button>
      <div class="result">结果：{{ helloResult }}</div>
    </div>

    <div v-else-if="activeTab === 'hash'" class="panel">
      <input v-model="hashInput" placeholder="输入文本" />
      <button @click="runHash">计算 MD5</button>
      <div class="result">MD5：{{ hashResult }}</div>
    </div>

    <div v-else class="panel">
      <input v-model="sortInput" placeholder="逗号分隔整数，如 5,3,8,1" />
      <button @click="runBubble">冒泡排序</button>
      <div class="result">排序结果：{{ sortResult }}</div>
    </div>

    <div class="export-bar">
      导出格式：
      <select v-model="format">
        <option value="json">JSON</option>
        <option value="csv">CSV</option>
        <option value="xlsx">Excel(.xlsx)</option>
      </select>
      <button @click="doExport">导出当前页结果</button>
    </div>

    <div class="metrics-bar">
      维度：
      <select v-model="metrics.dimension">
        <option value="personType">人员类型</option>
        <option value="personLevel">人员层级</option>
        <option value="personDepartment">人员部门</option>
      </select>
      图表：
      <select v-model="metrics.chartType">
        <option value="line">折线图</option>
        <option value="pie">饼图</option>
        <option value="bar">柱状图</option>
      </select>
      <button @click="loadMetrics">刷新报表</button>
    </div>

    <div class="chart-wrap">
      <MetricsCharts :data="metricsData" :chart-type="metrics.chartType" />
    </div>
  </div>
</template>
```

- [ ] **Step 3: Commit**

```bash
git add src
git commit -m "feat(frontend): 新增三 tab 结果页与 API 封装"
```

---

## Task 9: 报表图表组件（折线/饼/柱状）

**Files:**
- Create: `ranxitest/src/components/MetricsCharts.vue`

**Interfaces:**
- Consumes: `MetricsQueryResponse`（props: `data`, `chartType`）。
- Produces: 渲染 ECharts 折线/饼/柱状图。

- [ ] **Step 1: 创建 MetricsCharts.vue**

```vue
<script setup>
import { ref, watch, onMounted, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts'

const props = defineProps({
  data: { type: Object, default: null },
  chartType: { type: String, default: 'pie' }
})

const el = ref(null)
let chart = null

function optionFor(data, chartType) {
  if (!data) return {}
  if (chartType === 'pie') {
    const values = data.series?.[0]?.values ?? []
    const items = (data.categories ?? []).map((c, i) => ({
      name: c, value: values[i] ?? 0
    }))
    return {
      title: { text: '调用占比' },
      tooltip: { trigger: 'item' },
      series: [{ type: 'pie', radius: '60%', data: items }]
    }
  }
  return {
    title: { text: chartType === 'line' ? '调用趋势' : '调用对比' },
    tooltip: { trigger: 'axis' },
    legend: { data: (data.series ?? []).map(s => s.name) },
    xAxis: { type: 'category', data: data.categories ?? [] },
    yAxis: { type: 'value', minInterval: 1 },
    series: (data.series ?? []).map(s => ({
      name: s.name,
      type: chartType,
      smooth: true,
      data: s.values ?? []
    }))
  }
}

function render() {
  if (!el.value) return
  if (!chart) chart = echarts.init(el.value)
  chart.setOption(optionFor(props.data, props.chartType))
}

function onResize() {
  chart?.resize()
}

watch([() => props.data, () => props.chartType], render)

onMounted(() => {
  render()
  window.addEventListener('resize', onResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', onResize)
  chart?.dispose()
})
</script>

<template>
  <div ref="el" class="chart"></div>
</template>

<style scoped>
.chart { width: 100%; height: 380px; }
</style>
```

- [ ] **Step 2: 构建验证**

Run: `cd /root/.agentix/agentic-dev/runs/DEV-d885ee0d-78e2-11f1-8f3f-75954cb1c56f-02a853e4-5eda-4c6d-b754-838828eb0c80/worktree/ranxitest-AI-task-AUTO-root-b547e38a-26e5-42a6-b69d-4741f6b33b54 && npm run build`
Expected: 构建成功（`dist/` 生成）。

- [ ] **Step 3: Commit**

```bash
git add src
git commit -m "feat(frontend): 新增折线/饼/柱状报表图表组件"
```

---

## Task 10: 跨仓联调与验收

**Files:** 无新增（仅验证）。

- [ ] **Step 1: 后端启动并冒烟**

Run: `mvn -q spring-boot:run`（后台运行，端口 8080）

冒烟（另一终端）：
```bash
curl -s http://localhost:8080/api/helloWorld | head
curl -s -X POST http://localhost:8080/api/hash \
  -H 'Content-Type: application/json' -H 'X-Caller-Id: u001' \
  -d '{"text":"abc"}'
curl -s -X POST http://localhost:8080/api/sort/bubble \
  -H 'Content-Type: application/json' \
  -d '{"numbers":[5,3,8,1]}'
curl -s 'http://localhost:8080/api/metrics/query?dimension=personType&chartType=pie'
```
Expected: 分别返回 `Hello, World!`、MD5 值、升序数组、饼图聚合 JSON。

- [ ] **Step 2: 前端联调**

Run: `npm run dev`（后台运行，端口 5173）

浏览器访问 `http://localhost:5173`，验证：
1. 三个 tab 能切换并各自执行展示结果。
2. 顶部选择调用人后调用接口，刷新报表能看到人次增长。
3. 切换维度（类型/层级/部门）与图表（折线/饼/柱状）均正常渲染。
4. 导出按钮在三种格式下均能下载文件，CSV/JSON 可读、XLSX 可用 Excel 打开。

- [ ] **Step 3: 收尾提交（如联调产生微调）**

```bash
git add -A
git commit -m "chore: 跨仓联调收尾"
```

---

## Self-review

**Spec coverage:**
- 三个接口 helloworld/哈希/冒泡排序 → Task 2、Task 3（后端）+ Task 8（前端调用）。✅
- 前端一个页面三 tab 展示执行结果 → Task 8。✅
- 导出按钮 + 后台导出接口（三格式）→ Task 6（后端）+ Task 8（前端按钮）。✅
- 埋点（调用次数 + 调用人）→ Task 4。✅
- 页面内可视化报表（人员类型/层级/部门维度，折线/饼/柱状）→ Task 5（后端聚合）+ Task 9（前端图表）。✅

**Placeholder scan:** 无 TBD/TODO/“类似 Task N”；所有代码步骤均给出完整实现。✅

**Type consistency:**
- `Result.ok/error`、`HashService.md5Hex`、`BubbleSortService.sort` 签名在 Task 2/3/5/6 中一致使用。✅
- `MetricsQueryResponse{ categories, series, total }` 字段在 Task 5（后端）与 Task 9（前端 `data.series/categories`）一致。✅
- `Person{ id,name,type,level,department }` 在 Task 4 后端与 Task 8 前端下拉渲染字段一致。✅
- 维度取值 `personType/personLevel/personDepartment`、图表取值 `line/pie/bar` 前后端一一对应。✅
- 导出请求 `{format,page,rows}` 与后端 `ExportRequest` record 完全一致。✅

**跨仓对齐点检查结论：**
- 路径：前端 axios `baseURL=/api` + Vite 代理 `/api -> localhost:8080`，与后端 `@RequestMapping("/api")` 对齐。✅
- 响应：前端直接读 `data.data`，对应后端 `Result.data`。✅
- 调用人：前端 header `X-Caller-Id`，后端拦截器同名读取。✅
- 埋点范围仅 3 个业务接口，导出/报表/人员字典访问不计数（符合「获取调用次数」的语义）。✅