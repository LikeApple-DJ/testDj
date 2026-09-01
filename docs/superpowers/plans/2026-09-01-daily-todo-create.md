# 日常待办事项（创建）实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 构建一个极简的日常待办事项记录系统，支持新增待办事项（名称 + 描述）。

**Architecture:** Spring Boot 3 单体后端 + H2 内存数据库 + 单页 HTML 前端。POST /api/todos 接收 JSON 请求体，持久化至 H2 数据库，前端提供表单提交页面。

**Tech Stack:** Java 17, Spring Boot 3.2.0, Spring Data JPA, H2 Database, Maven, HTML5 + Vanilla JS

---

## Global Constraints

- Java 17+
- Spring Boot 3.2.0
- H2 内存数据库（开发/测试阶段，无需外部依赖）
- 金额/数字字段统一使用 Java 标准类型，无特殊精度要求
- 日期格式：ISO 8601（`yyyy-MM-dd'T'HH:mm:ss`）
- 通用出参结构：`{ "result": "OK", "msg": "SUCCESS", "data": {} }`
- 端口：8080
- 前端零构建工具依赖，纯静态 HTML

---

## Task 1: 项目骨架与依赖配置

**Files:**
- Create: `pom.xml`
- Create: `src/main/java/com/example/todo/TodoApplication.java`
- Create: `src/main/resources/application.yml`

**Interfaces:**
- Consumes: 无
- Produces: `TodoApplication` 主启动类，端口 8080，H2 控制台 `/h2-console`

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
        <version>3.2.0</version>
        <relativePath/>
    </parent>
    <groupId>com.example</groupId>
    <artifactId>todo</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>todo</name>
    <description>Daily Todo Application</description>
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
            <artifactId>spring-boot-starter-validation</artifactId>
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

- [ ] **Step 2: 创建 TodoApplication.java**

```java
package com.example.todo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TodoApplication {
    public static void main(String[] args) {
        SpringApplication.run(TodoApplication.class, args);
    }
}
```

- [ ] **Step 3: 创建 application.yml**

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:h2:mem:tododb
    driver-class-name: org.h2.Driver
    username: sa
    password:
  h2:
    console:
      enabled: true
      path: /h2-console
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: update
    show-sql: true
```

- [ ] **Step 4: 编译验证**

Run: `mvn compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 5: Commit**

```bash
git add pom.xml src/main/java/com/example/todo/TodoApplication.java src/main/resources/application.yml
git commit -m "feat: init Spring Boot project skeleton with H2"
```

---

## Task 2: 数据模型与 Repository

**Files:**
- Create: `src/main/java/com/example/todo/model/TodoItem.java`
- Create: `src/main/java/com/example/todo/repository/TodoItemRepository.java`

**Interfaces:**
- Consumes: 无（独立实体）
- Produces: `TodoItem` 实体（id: Long, name: String, description: String, createdAt: LocalDateTime），`TodoItemRepository` 继承 `JpaRepository<TodoItem, Long>`

- [ ] **Step 1: 创建 TodoItem 实体**

```java
package com.example.todo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "todo_item")
public class TodoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public TodoItem() {}

    public TodoItem(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
```

- [ ] **Step 2: 创建 TodoItemRepository**

```java
package com.example.todo.repository;

import com.example.todo.model.TodoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoItemRepository extends JpaRepository<TodoItem, Long> {
}
```

- [ ] **Step 3: 编译验证**

Run: `mvn compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add src/main/java/com/example/todo/model/TodoItem.java src/main/java/com/example/todo/repository/TodoItemRepository.java
git commit -m "feat: add TodoItem entity and JPA repository"
```

---

## Task 3: Service + Controller + DTO

**Files:**
- Create: `src/main/java/com/example/todo/dto/CreateTodoRequest.java`
- Create: `src/main/java/com/example/todo/dto/ApiResponse.java`
- Create: `src/main/java/com/example/todo/service/TodoService.java`
- Create: `src/main/java/com/example/todo/controller/TodoController.java`

**Interfaces:**
- Consumes: `TodoItemRepository` (Task 2)
- Produces: `POST /api/todos` 接收 `CreateTodoRequest`(name, description)，返回 `ApiResponse<TodoItem>`；`TodoService.create(name, description)` → `TodoItem`

- [ ] **Step 1: 创建 CreateTodoRequest DTO**

```java
package com.example.todo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateTodoRequest {

    @NotBlank(message = "事项名称不能为空")
    @Size(max = 200, message = "事项名称最多200个字符")
    private String name;

    @Size(max = 2000, message = "描述最多2000个字符")
    private String description;

    public CreateTodoRequest() {}

    public CreateTodoRequest(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
```

- [ ] **Step 2: 创建 ApiResponse 通用响应包装**

```java
package com.example.todo.dto;

public class ApiResponse<T> {

    private String result;
    private String msg;
    private T data;

    private ApiResponse(String result, String msg, T data) {
        this.result = result;
        this.msg = msg;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("OK", "SUCCESS", data);
    }

    public static <T> ApiResponse<T> error(String msg) {
        return new ApiResponse<>("ERROR", msg, null);
    }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }

    public String getMsg() { return msg; }
    public void setMsg(String msg) { this.msg = msg; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}
```

- [ ] **Step 3: 创建 TodoService**

```java
package com.example.todo.service;

import com.example.todo.model.TodoItem;
import com.example.todo.repository.TodoItemRepository;
import org.springframework.stereotype.Service;

@Service
public class TodoService {

    private final TodoItemRepository repository;

    public TodoService(TodoItemRepository repository) {
        this.repository = repository;
    }

    public TodoItem create(String name, String description) {
        TodoItem item = new TodoItem(name, description);
        return repository.save(item);
    }
}
```

- [ ] **Step 4: 创建 TodoController**

```java
package com.example.todo.controller;

import com.example.todo.dto.ApiResponse;
import com.example.todo.dto.CreateTodoRequest;
import com.example.todo.model.TodoItem;
import com.example.todo.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoService service;

    public TodoController(TodoService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<TodoItem> create(@Valid @RequestBody CreateTodoRequest request) {
        TodoItem item = service.create(request.getName(), request.getDescription());
        return ApiResponse.success(item);
    }
}
```

- [ ] **Step 5: 编译验证**

Run: `mvn compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 6: 启动服务并验证 API**

Run: `mvn spring-boot:run` (在后台启动)
Run: `curl -X POST http://localhost:8080/api/todos -H 'Content-Type: application/json' -d '{"name":"测试待办","description":"这是一个测试事项"}'`
Expected: HTTP 201, JSON 包含 `result: "OK"`, `data.id` 不为空

- [ ] **Step 7: Commit**

```bash
git add src/main/java/com/example/todo/dto/CreateTodoRequest.java src/main/java/com/example/todo/dto/ApiResponse.java src/main/java/com/example/todo/service/TodoService.java src/main/java/com/example/todo/controller/TodoController.java
git commit -m "feat: add create todo API endpoint"
```

---

## Task 4: 前端表单页

**Files:**
- Create: `src/main/resources/static/index.html`

**Interfaces:**
- Consumes: `POST /api/todos` (Task 3)
- Produces: 单页 HTML 表单，输入名称和描述，提交后显示创建结果

- [ ] **Step 1: 创建 index.html 前端表单页**

```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>日常待办事项</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body {
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
            background: #f5f5f5;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
        }
        .container {
            background: #fff;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            padding: 32px;
            width: 100%;
            max-width: 480px;
        }
        h1 {
            font-size: 24px;
            color: #333;
            margin-bottom: 24px;
            text-align: center;
        }
        .form-group {
            margin-bottom: 16px;
        }
        label {
            display: block;
            font-size: 14px;
            color: #666;
            margin-bottom: 6px;
        }
        input, textarea {
            width: 100%;
            padding: 10px 12px;
            border: 1px solid #d9d9d9;
            border-radius: 6px;
            font-size: 14px;
            transition: border-color 0.2s;
        }
        input:focus, textarea:focus {
            outline: none;
            border-color: #1677ff;
            box-shadow: 0 0 0 2px rgba(22,119,255,0.1);
        }
        textarea {
            resize: vertical;
            min-height: 100px;
        }
        button {
            width: 100%;
            padding: 10px;
            background: #1677ff;
            color: #fff;
            border: none;
            border-radius: 6px;
            font-size: 16px;
            cursor: pointer;
            transition: background 0.2s;
        }
        button:hover { background: #4096ff; }
        button:disabled { background: #a0c4ff; cursor: not-allowed; }
        .message {
            margin-top: 16px;
            padding: 12px;
            border-radius: 6px;
            font-size: 14px;
            display: none;
        }
        .message.success {
            display: block;
            background: #f6ffed;
            border: 1px solid #b7eb8f;
            color: #52c41a;
        }
        .message.error {
            display: block;
            background: #fff2f0;
            border: 1px solid #ffccc7;
            color: #ff4d4f;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>📝 新增待办事项</h1>
        <form id="todoForm">
            <div class="form-group">
                <label for="name">事项名称 *</label>
                <input type="text" id="name" name="name"
                       placeholder="请输入事项名称" maxlength="200" required>
            </div>
            <div class="form-group">
                <label for="description">描述</label>
                <textarea id="description" name="description"
                          placeholder="请输入事项描述（选填）" maxlength="2000"></textarea>
            </div>
            <button type="submit" id="submitBtn">创建待办事项</button>
        </form>
        <div id="message" class="message"></div>
    </div>

    <script>
        const form = document.getElementById('todoForm');
        const messageEl = document.getElementById('message');
        const submitBtn = document.getElementById('submitBtn');

        function showMessage(type, text) {
            messageEl.className = 'message ' + type;
            messageEl.textContent = text;
        }

        form.addEventListener('submit', async (e) => {
            e.preventDefault();
            submitBtn.disabled = true;
            submitBtn.textContent = '提交中...';
            messageEl.className = 'message';

            const name = document.getElementById('name').value.trim();
            const description = document.getElementById('description').value.trim();

            try {
                const response = await fetch('/api/todos', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ name, description })
                });

                const result = await response.json();

                if (response.ok && result.result === 'OK') {
                    showMessage('success',
                        '✅ 创建成功！事项名称：' + result.data.name +
                        '（ID: ' + result.data.id + '）');
                    form.reset();
                } else {
                    showMessage('error', '❌ 创建失败：' + (result.msg || '未知错误'));
                }
            } catch (err) {
                showMessage('error', '❌ 网络错误：' + err.message);
            } finally {
                submitBtn.disabled = false;
                submitBtn.textContent = '创建待办事项';
            }
        });
    </script>
</body>
</html>
```

- [ ] **Step 2: 重新编译并启动验证**

Run: `mvn compile -q`
Expected: BUILD SUCCESS

Run: `mvn spring-boot:run` (后台启动)
Run: `curl -s http://localhost:8080/ | head -5`
Expected: 返回 index.html 内容

- [ ] **Step 3: 端到端验证**

Run: `curl -X POST http://localhost:8080/api/todos -H 'Content-Type: application/json' -d '{"name":"完成周报","description":"周五前提交本周工作总结"}'`
Expected: HTTP 201，响应含 `result: "OK"`，`data.name: "完成周报"`

- [ ] **Step 4: Commit**

```bash
git add src/main/resources/static/index.html
git commit -m "feat: add todo creation frontend page"
```

---

## Self-Review

**1. Spec coverage:**
| 需求 | 对应任务 | 覆盖 |
|------|----------|------|
| 新增待办事项 | Task 3 (POST /api/todos) + Task 4 (前端表单) | ✅ |
| 事项名称 | Task 2 (TodoItem.name) + Task 3 (CreateTodoRequest.name) | ✅ |
| 事项描述 | Task 2 (TodoItem.description) + Task 3 (CreateTodoRequest.description) | ✅ |
| 内部用户 | Task 4 (index.html 内网可访问) | ✅ |
| 最小闭环：仅创建 | 全部任务仅实现 POST 创建，无查询/更新/删除 | ✅ |

**2. Placeholder scan:** 无 TBD/TODO，所有代码步骤均包含完整实现。

**3. Type consistency:**
- `TodoItem.name` (String) ↔ `CreateTodoRequest.name` (String) ↔ 前端 `name` 字段 → 一致 ✅
- `TodoItem.description` (String) ↔ `CreateTodoRequest.description` (String) ↔ 前端 `description` 字段 → 一致 ✅
- `ApiResponse<T>` 泛型在 Controller 返回 `ApiResponse<TodoItem>`，前端解析 `result.data` → 一致 ✅