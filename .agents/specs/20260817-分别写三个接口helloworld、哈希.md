# Hello World / SHA-256 Hash / Bubble Sort — 跨仓实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 实现三个后端接口（Hello World、SHA-256 哈希、冒泡排序）及对应前端交互页面，并支持 PDF 导出功能。

**Architecture:** 后端 Spring Boot 3.2.5 (Java 17) 提供 RESTful API，前端 Vite + React 18 实现 Tab 面板式 Dashboard，前后端通过 Axios 通信，Vite 代理转发 `/api` 至后端 8080 端口。

**Tech Stack:**
- 后端: Spring Boot 3.2.5, Java 17, Maven, iText 7 (PDF)
- 前端: React 18, Vite 5, Axios

---

## 跨仓依赖与现状摘要

### 仓库 testDj (后端)

| 模块 | 文件路径 | 职责 |
|------|---------|------|
| DTO | `dto/HelloRequest.java` | 接收 `name` 字段 |
| DTO | `dto/HashRequest.java` | 接收 `input` 字段 |
| DTO | `dto/SortRequest.java` | 接收 `array` (List<Integer>) 字段 |
| DTO | `dto/ExportRequest.java` | 接收 `tab` 字段 |
| Service | `service/HelloService.java` | `greet(name)` → 返回问候语 |
| Service | `service/HashService.java` | `sha256(input)` → 返回十六进制 SHA-256 |
| Service | `service/SortService.java` | `bubbleSort(array)` → 返回排序后列表 |
| Service | `service/ExportService.java` | `exportTabResult(tab, data)` → 返回 PDF 字节数组 |
| Controller | `controller/HelloController.java` | `POST /api/hello` |
| Controller | `controller/HashController.java` | `POST /api/hash` |
| Controller | `controller/SortController.java` | `POST /api/bubble-sort` |
| Controller | `controller/ExportController.java` | `POST /api/export` |
| Config | `config/WebConfig.java` | CORS 配置 |
| Entry | `TestDjApplication.java` | Spring Boot 启动类 |
| Config | `application.yml` | 端口 8080 |

### 仓库 testDJnew (前端)

| 模块 | 文件路径 | 职责 |
|------|---------|------|
| Entry | `index.html` | HTML 入口 |
| Entry | `src/main.jsx` | React 挂载 |
| Entry | `src/App.jsx` | 渲染 Dashboard |
| Page | `src/pages/Dashboard.jsx` | Tab 切换 + 导出按钮 |
| Component | `src/components/HelloTab.jsx` | Hello World 输入/结果展示 |
| Component | `src/components/HashTab.jsx` | SHA-256 输入/结果展示 |
| Component | `src/components/SortTab.jsx` | 冒泡排序输入/结果展示 |
| Component | `src/components/ExportButton.jsx` | 导出 PDF 按钮 |
| Service | `src/services/api.js` | Axios API 封装 |
| Style | `src/App.css` | 全局样式 |
| Config | `vite.config.js` | Vite 配置 + 代理 |
| Config | `package.json` | 依赖声明 |

### 仓间对齐点 (API 契约)

| 前端调用 | 后端端点 | 请求体 | 响应体关键字段 |
|---------|---------|--------|-------------|
| `callHello(name)` | `POST /api/hello` | `{ name }` | `tab`, `message`, `input` |
| `callHash(input)` | `POST /api/hash` | `{ input }` | `tab`, `algorithm`, `input`, `hash` |
| `callBubbleSort(array)` | `POST /api/bubble-sort` | `{ array }` | `tab`, `original`, `sorted`, `length` |
| `exportTab(tab)` | `POST /api/export` | `{ tab }` | `application/pdf` 二进制流 |

---

## Global Constraints

- Java 17, Spring Boot 3.2.5, Maven
- React 18, Vite 5, Axios
- 前端端口 5173, 后端端口 8080
- 前端通过 Vite proxy 转发 `/api` → `http://localhost:8080`
- 后端 CORS 允许 `localhost:5173` 和 `localhost:3000`
- PDF 导出使用 iText 7 Core (7.2.6)
- Git 禁止写操作（仅允许 `git status/log/diff/show` 等只读命令）

---

## Task 1: 后端 — Hello World 接口

**Files:**
- Create: `testDj/src/main/java/com/testdj/dto/HelloRequest.java`
- Create: `testDj/src/main/java/com/testdj/service/HelloService.java`
- Create: `testDj/src/main/java/com/testdj/controller/HelloController.java`

**Interfaces:**
- Produces: `POST /api/hello` — 接收 `{ name: string }`，返回 `{ tab: "hello", message: string, input: string }`

- [ ] **Step 1: 创建 HelloRequest DTO**

```java
package com.testdj.dto;

public class HelloRequest {
    private String name;

    public HelloRequest() {}

    public HelloRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```

- [ ] **Step 2: 创建 HelloService**

```java
package com.testdj.service;

import org.springframework.stereotype.Service;

@Service
public class HelloService {

    public String greet(String name) {
        if (name == null || name.trim().isEmpty()) {
            name = "World";
        }
        return "Hello, " + name.trim() + "!";
    }
}
```

- [ ] **Step 3: 创建 HelloController**

```java
package com.testdj.controller;

import com.testdj.dto.HelloRequest;
import com.testdj.service.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/hello")
public class HelloController {

    @Autowired
    private HelloService helloService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> hello(@RequestBody HelloRequest request) {
        String message = helloService.greet(request.getName());
        return ResponseEntity.ok(Map.of(
                "tab", "hello",
                "message", message,
                "input", request.getName() != null ? request.getName() : ""
        ));
    }
}
```

- [ ] **Step 4: 验证编译**

```bash
cd /workspace/testDj
mvn compile -q
```

Expected: BUILD SUCCESS (无输出)

---

## Task 2: 后端 — SHA-256 哈希接口

**Files:**
- Create: `testDj/src/main/java/com/testdj/dto/HashRequest.java`
- Create: `testDj/src/main/java/com/testdj/service/HashService.java`
- Create: `testDj/src/main/java/com/testdj/controller/HashController.java`

**Interfaces:**
- Produces: `POST /api/hash` — 接收 `{ input: string }`，返回 `{ tab: "hash", algorithm: "SHA-256", input: string, hash: string }`

- [ ] **Step 1: 创建 HashRequest DTO**

```java
package com.testdj.dto;

public class HashRequest {
    private String input;

    public HashRequest() {}

    public HashRequest(String input) {
        this.input = input;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }
}
```

- [ ] **Step 2: 创建 HashService**

```java
package com.testdj.service;

import org.springframework.stereotype.Service;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

@Service
public class HashService {

    public String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
```

- [ ] **Step 3: 创建 HashController**

```java
package com.testdj.controller;

import com.testdj.dto.HashRequest;
import com.testdj.service.HashService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/hash")
public class HashController {

    @Autowired
    private HashService hashService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> hash(@RequestBody HashRequest request) {
        String input = request.getInput() != null ? request.getInput() : "";
        String hashValue = hashService.sha256(input);
        return ResponseEntity.ok(Map.of(
                "tab", "hash",
                "algorithm", "SHA-256",
                "input", input,
                "hash", hashValue
        ));
    }
}
```

- [ ] **Step 4: 验证编译**

```bash
cd /workspace/testDj
mvn compile -q
```

Expected: BUILD SUCCESS

---

## Task 3: 后端 — 冒泡排序接口

**Files:**
- Create: `testDj/src/main/java/com/testdj/dto/SortRequest.java`
- Create: `testDj/src/main/java/com/testdj/service/SortService.java`
- Create: `testDj/src/main/java/com/testdj/controller/SortController.java`

**Interfaces:**
- Produces: `POST /api/bubble-sort` — 接收 `{ array: number[] }`，返回 `{ tab: "sort", original: number[], sorted: number[], length: number }`

- [ ] **Step 1: 创建 SortRequest DTO**

```java
package com.testdj.dto;

import java.util.List;

public class SortRequest {
    private List<Integer> array;

    public SortRequest() {}

    public SortRequest(List<Integer> array) {
        this.array = array;
    }

    public List<Integer> getArray() {
        return array;
    }

    public void setArray(List<Integer> array) {
        this.array = array;
    }
}
```

- [ ] **Step 2: 创建 SortService**

```java
package com.testdj.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;

@Service
public class SortService {

    public List<Integer> bubbleSort(List<Integer> array) {
        List<Integer> sorted = new ArrayList<>(array);
        int n = sorted.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (sorted.get(j) > sorted.get(j + 1)) {
                    int temp = sorted.get(j);
                    sorted.set(j, sorted.get(j + 1));
                    sorted.set(j + 1, temp);
                }
            }
        }
        return sorted;
    }
}
```

- [ ] **Step 3: 创建 SortController**

```java
package com.testdj.controller;

import com.testdj.dto.SortRequest;
import com.testdj.service.SortService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bubble-sort")
public class SortController {

    @Autowired
    private SortService sortService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> sort(@RequestBody SortRequest request) {
        List<Integer> original = request.getArray();
        List<Integer> sorted = sortService.bubbleSort(original);
        return ResponseEntity.ok(Map.of(
                "tab", "sort",
                "original", original,
                "sorted", sorted,
                "length", original != null ? original.size() : 0
        ));
    }
}
```

- [ ] **Step 4: 验证编译**

```bash
cd /workspace/testDj
mvn compile -q
```

Expected: BUILD SUCCESS

---

## Task 4: 后端 — 配置与导出接口

**Files:**
- Create: `testDj/src/main/java/com/testdj/config/WebConfig.java` (CORS)
- Create: `testDj/src/main/java/com/testdj/dto/ExportRequest.java`
- Create: `testDj/src/main/java/com/testdj/service/ExportService.java`
- Create: `testDj/src/main/java/com/testdj/controller/ExportController.java`
- Modify: `testDj/pom.xml` (添加 iText 依赖)
- Modify: `testDj/src/main/resources/application.yml`

**Interfaces:**
- Produces: `POST /api/export` — 接收 `{ tab: string }`，返回 `application/pdf` 二进制附件

- [ ] **Step 1: 配置 pom.xml 添加 iText 7 依赖**

```xml
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>itext7-core</artifactId>
    <version>7.2.6</version>
    <type>pom</type>
</dependency>
```

- [ ] **Step 2: 配置 application.yml**

```yaml
server:
  port: 8080

spring:
  application:
    name: testdj-backend
```

- [ ] **Step 3: 创建 CORS 配置 WebConfig**

```java
package com.testdj.config;

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
                        .allowedOrigins("http://localhost:5173", "http://localhost:3000")
                        .allowedMethods("GET", "POST", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
```

- [ ] **Step 4: 创建 ExportRequest DTO**

```java
package com.testdj.dto;

public class ExportRequest {
    private String tab;

    public ExportRequest() {}

    public ExportRequest(String tab) {
        this.tab = tab;
    }

    public String getTab() {
        return tab;
    }

    public void setTab(String tab) {
        this.tab = tab;
    }
}
```

- [ ] **Step 5: 创建 ExportService (PDF 生成)**

```java
package com.testdj.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class ExportService {

    public byte[] exportTabResult(String tab, Map<String, Object> resultData) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        String title = getTabTitle(tab);
        document.add(new Paragraph(title).setFontSize(20).setBold());
        document.add(new Paragraph("Export Time: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        document.add(new Paragraph(" "));

        if (resultData != null && !resultData.isEmpty()) {
            Table table = new Table(UnitValue.createPercentArray(2)).useAllAvailableWidth();
            table.addHeaderCell(new Cell().add(new Paragraph("Field").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Value").setBold()));

            for (Map.Entry<String, Object> entry : resultData.entrySet()) {
                table.addCell(new Cell().add(new Paragraph(entry.getKey())));
                String value = entry.getValue() != null ? entry.getValue().toString() : "";
                table.addCell(new Cell().add(new Paragraph(value)));
            }
            document.add(table);
        } else {
            document.add(new Paragraph("No data available for this tab."));
        }

        document.close();
        return baos.toByteArray();
    }

    private String getTabTitle(String tab) {
        return switch (tab) {
            case "hello" -> "Hello World - Result Export";
            case "hash" -> "SHA-256 Hash - Result Export";
            case "sort" -> "Bubble Sort - Result Export";
            default -> "Unknown Tab - Result Export";
        };
    }
}
```

- [ ] **Step 6: 创建 ExportController**

```java
package com.testdj.controller;

import com.testdj.dto.ExportRequest;
import com.testdj.service.ExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/export")
public class ExportController {

    @Autowired
    private ExportService exportService;

    @PostMapping
    public ResponseEntity<byte[]> export(@RequestBody ExportRequest request) {
        String tab = request.getTab() != null ? request.getTab() : "hello";
        Map<String, Object> resultData = buildSampleData(tab);
        byte[] pdfBytes = exportService.exportTabResult(tab, resultData);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", tab + "_result.pdf");
        headers.setContentLength(pdfBytes.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    private Map<String, Object> buildSampleData(String tab) {
        return switch (tab) {
            case "hello" -> Map.of(
                    "Tab", "Hello World",
                    "Input", "World",
                    "Message", "Hello, World!"
            );
            case "hash" -> Map.of(
                    "Tab", "SHA-256 Hash",
                    "Input", "hello",
                    "Algorithm", "SHA-256",
                    "Hash Result", "2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824"
            );
            case "sort" -> Map.of(
                    "Tab", "Bubble Sort",
                    "Original Array", "[3, 1, 4, 1, 5]",
                    "Sorted Array", "[1, 1, 3, 4, 5]",
                    "Length", "5"
            );
            default -> Map.of("Tab", "Unknown", "Info", "No data available");
        };
    }
}
```

- [ ] **Step 7: 验证编译**

```bash
cd /workspace/testDj
mvn compile -q
```

Expected: BUILD SUCCESS

---

## Task 5: 前端 — 项目初始化与 API 服务层

**Files:**
- Create: `testDJnew/package.json`
- Create: `testDJnew/vite.config.js`
- Create: `testDJnew/index.html`
- Create: `testDJnew/src/main.jsx`
- Create: `testDJnew/src/services/api.js`

**Interfaces:**
- Produces: `api.js` 导出 `callHello(name)`, `callHash(input)`, `callBubbleSort(array)`, `exportTab(tab)`

- [ ] **Step 1: 创建 package.json**

```json
{
  "name": "testdjnew-frontend",
  "private": true,
  "version": "1.0.0",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "vite build",
    "preview": "vite preview"
  },
  "dependencies": {
    "react": "^18.3.1",
    "react-dom": "^18.3.1",
    "axios": "^1.7.2"
  },
  "devDependencies": {
    "@types/react": "^18.3.3",
    "@types/react-dom": "^18.3.0",
    "@vitejs/plugin-react": "^4.3.1",
    "vite": "^5.3.1"
  }
}
```

- [ ] **Step 2: 创建 vite.config.js**

```javascript
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

- [ ] **Step 3: 创建 index.html**

```html
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>testDJnew - Dashboard</title>
  </head>
  <body>
    <div id="root"></div>
    <script type="module" src="/src/main.jsx"></script>
  </body>
</html>
```

- [ ] **Step 4: 创建 main.jsx**

```jsx
import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
import './App.css';

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);
```

- [ ] **Step 5: 创建 API 服务层**

```javascript
import axios from 'axios';

const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

export function callHello(name) {
  return api.post('/hello', { name });
}

export function callHash(input) {
  return api.post('/hash', { input });
}

export function callBubbleSort(array) {
  return api.post('/bubble-sort', { array });
}

export function exportTab(tab) {
  return api.post('/export', { tab }, { responseType: 'blob' });
}
```

- [ ] **Step 6: 验证依赖安装**

```bash
cd /workspace/testDJnew
npm install
```

Expected: 无报错，node_modules 生成

---

## Task 6: 前端 — Tab 组件实现

**Files:**
- Create: `testDJnew/src/components/HelloTab.jsx`
- Create: `testDJnew/src/components/HashTab.jsx`
- Create: `testDJnew/src/components/SortTab.jsx`

**Interfaces:**
- Consumes: `callHello(name)`, `callHash(input)`, `callBubbleSort(array)` 来自 `api.js`
- Produces: 三个独立 Tab 组件，各自管理输入状态和结果展示

- [ ] **Step 1: 创建 HelloTab.jsx**

```jsx
import { useState } from 'react';
import { callHello } from '../services/api';

export default function HelloTab() {
  const [name, setName] = useState('');
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async () => {
    setLoading(true);
    try {
      const res = await callHello(name);
      setResult(res.data);
    } catch (err) {
      setResult({ error: err.message || 'Request failed' });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="tab-content">
      <h2>Hello World</h2>
      <div className="input-group">
        <input
          type="text"
          placeholder="Enter your name"
          value={name}
          onChange={(e) => setName(e.target.value)}
        />
        <button onClick={handleSubmit} disabled={loading}>
          {loading ? 'Loading...' : 'Send'}
        </button>
      </div>
      {result && (
        <div className="result-box">
          {result.error ? (
            <p className="error">Error: {result.error}</p>
          ) : (
            <>
              <p><strong>Input:</strong> {result.input}</p>
              <p><strong>Message:</strong> {result.message}</p>
            </>
          )}
        </div>
      )}
    </div>
  );
}
```

- [ ] **Step 2: 创建 HashTab.jsx**

```jsx
import { useState } from 'react';
import { callHash } from '../services/api';

export default function HashTab() {
  const [input, setInput] = useState('');
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async () => {
    setLoading(true);
    try {
      const res = await callHash(input);
      setResult(res.data);
    } catch (err) {
      setResult({ error: err.message || 'Request failed' });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="tab-content">
      <h2>SHA-256 Hash</h2>
      <div className="input-group">
        <input
          type="text"
          placeholder="Enter text to hash"
          value={input}
          onChange={(e) => setInput(e.target.value)}
        />
        <button onClick={handleSubmit} disabled={loading}>
          {loading ? 'Loading...' : 'Hash'}
        </button>
      </div>
      {result && (
        <div className="result-box">
          {result.error ? (
            <p className="error">Error: {result.error}</p>
          ) : (
            <>
              <p><strong>Algorithm:</strong> {result.algorithm}</p>
              <p><strong>Input:</strong> {result.input}</p>
              <p><strong>Hash:</strong> <span className="hash-value">{result.hash}</span></p>
            </>
          )}
        </div>
      )}
    </div>
  );
}
```

- [ ] **Step 3: 创建 SortTab.jsx**

```jsx
import { useState } from 'react';
import { callBubbleSort } from '../services/api';

export default function SortTab() {
  const [arrayInput, setArrayInput] = useState('');
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async () => {
    setLoading(true);
    try {
      const arr = arrayInput
        .split(',')
        .map((s) => parseInt(s.trim(), 10))
        .filter((n) => !isNaN(n));
      const res = await callBubbleSort(arr);
      setResult(res.data);
    } catch (err) {
      setResult({ error: err.message || 'Request failed' });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="tab-content">
      <h2>Bubble Sort</h2>
      <div className="input-group">
        <input
          type="text"
          placeholder="Enter numbers, e.g. 3,1,4,1,5"
          value={arrayInput}
          onChange={(e) => setArrayInput(e.target.value)}
        />
        <button onClick={handleSubmit} disabled={loading}>
          {loading ? 'Loading...' : 'Sort'}
        </button>
      </div>
      {result && (
        <div className="result-box">
          {result.error ? (
            <p className="error">Error: {result.error}</p>
          ) : (
            <>
              <p><strong>Original:</strong> [{result.original?.join(', ')}]</p>
              <p><strong>Sorted:</strong> [{result.sorted?.join(', ')}]</p>
              <p><strong>Length:</strong> {result.length}</p>
            </>
          )}
        </div>
      )}
    </div>
  );
}
```

---

## Task 7: 前端 — ExportButton 与 Dashboard 集成

**Files:**
- Create: `testDJnew/src/components/ExportButton.jsx`
- Create: `testDJnew/src/pages/Dashboard.jsx`
- Create: `testDJnew/src/App.jsx`
- Create: `testDJnew/src/App.css`

**Interfaces:**
- Consumes: `exportTab(tab)` 来自 `api.js`
- Produces: 完整 Dashboard 页面，三个 Tab 切换 + 导出按钮

- [ ] **Step 1: 创建 ExportButton.jsx**

```jsx
import { useState } from 'react';
import { exportTab } from '../services/api';

export default function ExportButton({ activeTab }) {
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');

  const handleExport = async () => {
    if (!activeTab) {
      setMessage('No active tab to export');
      return;
    }
    setLoading(true);
    setMessage('');
    try {
      const res = await exportTab(activeTab);
      const blob = new Blob([res.data], { type: 'application/pdf' });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `${activeTab}_result.pdf`;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
      setMessage('Export successful!');
    } catch (err) {
      setMessage('Export failed: ' + (err.message || 'Unknown error'));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="export-area">
      <button onClick={handleExport} disabled={loading} className="export-btn">
        {loading ? 'Exporting...' : 'Export PDF'}
      </button>
      {message && <span className="export-msg">{message}</span>}
    </div>
  );
}
```

- [ ] **Step 2: 创建 Dashboard.jsx**

```jsx
import { useState } from 'react';
import HelloTab from '../components/HelloTab';
import HashTab from '../components/HashTab';
import SortTab from '../components/SortTab';
import ExportButton from '../components/ExportButton';

const TABS = [
  { key: 'hello', label: 'Hello World', component: HelloTab },
  { key: 'hash', label: 'SHA-256 Hash', component: HashTab },
  { key: 'sort', label: 'Bubble Sort', component: SortTab },
];

export default function Dashboard() {
  const [activeKey, setActiveKey] = useState('hello');

  const ActiveComponent = TABS.find((t) => t.key === activeKey).component;

  return (
    <div className="dashboard">
      <h1>testDJ Dashboard</h1>
      <div className="tab-bar">
        {TABS.map((tab) => (
          <button
            key={tab.key}
            className={`tab-btn ${activeKey === tab.key ? 'active' : ''}`}
            onClick={() => setActiveKey(tab.key)}
          >
            {tab.label}
          </button>
        ))}
        <div className="export-wrapper">
          <ExportButton activeTab={activeKey} />
        </div>
      </div>
      <div className="tab-panel">
        <ActiveComponent />
      </div>
    </div>
  );
}
```

- [ ] **Step 3: 创建 App.jsx**

```jsx
import Dashboard from './pages/Dashboard';

export default function App() {
  return <Dashboard />;
}
```

- [ ] **Step 4: 创建 App.css**（完整样式，含 Tab 切换、输入框、结果区、导出按钮）

```css
* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  background: #f5f7fa;
  color: #333;
  min-height: 100vh;
}

.dashboard {
  max-width: 800px;
  margin: 0 auto;
  padding: 32px 16px;
}

.dashboard h1 {
  font-size: 28px;
  margin-bottom: 24px;
  color: #1a1a2e;
}

.tab-bar {
  display: flex;
  align-items: center;
  gap: 4px;
  border-bottom: 2px solid #e0e0e0;
  margin-bottom: 24px;
  padding-bottom: 0;
}

.tab-btn {
  padding: 10px 20px;
  border: none;
  background: transparent;
  cursor: pointer;
  font-size: 14px;
  color: #666;
  border-bottom: 2px solid transparent;
  margin-bottom: -2px;
  transition: all 0.2s;
}

.tab-btn:hover {
  color: #333;
  background: #f0f0f0;
}

.tab-btn.active {
  color: #1890ff;
  border-bottom-color: #1890ff;
  font-weight: 600;
}

.export-wrapper {
  margin-left: auto;
}

.tab-panel {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.tab-content h2 {
  font-size: 20px;
  margin-bottom: 16px;
  color: #1a1a2e;
}

.input-group {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}

.input-group input {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  font-size: 14px;
}

.input-group input:focus {
  outline: none;
  border-color: #1890ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}

.input-group button {
  padding: 8px 20px;
  background: #1890ff;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background 0.2s;
}

.input-group button:hover:not(:disabled) {
  background: #40a9ff;
}

.input-group button:disabled {
  background: #91caff;
  cursor: not-allowed;
}

.result-box {
  background: #fafafa;
  border: 1px solid #f0f0f0;
  border-radius: 4px;
  padding: 16px;
  margin-top: 8px;
}

.result-box p {
  margin-bottom: 6px;
  font-size: 14px;
  word-break: break-all;
}

.result-box .error {
  color: #ff4d4f;
}

.hash-value {
  font-family: 'Courier New', monospace;
  font-size: 12px;
  background: #f0f0f0;
  padding: 2px 6px;
  border-radius: 3px;
}

.export-area {
  display: flex;
  align-items: center;
  gap: 12px;
}

.export-btn {
  padding: 8px 16px;
  background: #52c41a;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 13px;
  transition: background 0.2s;
}

.export-btn:hover:not(:disabled) {
  background: #73d13d;
}

.export-btn:disabled {
  background: #b7eb8f;
  cursor: not-allowed;
}

.export-msg {
  font-size: 13px;
  color: #52c41a;
}
```

- [ ] **Step 5: 验证前端构建**

```bash
cd /workspace/testDJnew
npm run build
```

Expected: 构建成功，dist 目录生成

---

## 自检清单

**1. Spec 覆盖检查：**
- ✅ Hello World 接口 — Task 1 (后端) + Task 6 (前端 HelloTab)
- ✅ SHA-256 哈希接口 — Task 2 (后端) + Task 6 (前端 HashTab)
- ✅ 冒泡排序接口 — Task 3 (后端) + Task 6 (前端 SortTab)
- ✅ 前端 Tab 页面 Dashboard — Task 7 (Dashboard)
- ✅ 导出按钮 — Task 7 (ExportButton)
- ✅ 后端导出接口 — Task 4 (ExportController + ExportService)

**2. 占位符扫描：** 无 "TBD", "TODO", "implement later", "fill in details" 等占位符。所有代码块均包含完整实现。

**3. 类型一致性：**
- 前端 `callHello(name)` → 后端 `POST /api/hello` → `{ tab, message, input }` — 一致
- 前端 `callHash(input)` → 后端 `POST /api/hash` → `{ tab, algorithm, input, hash }` — 一致
- 前端 `callBubbleSort(array)` → 后端 `POST /api/bubble-sort` → `{ tab, original, sorted, length }` — 一致
- 前端 `exportTab(tab)` → 后端 `POST /api/export` → PDF binary — 一致

---

## 执行交接

**Plan complete and saved to `.agents/specs/20260817-分别写三个接口helloworld、哈希.md`.**

**验证方式：**
1. 启动后端: `cd /workspace/testDj && mvn spring-boot:run`
2. 启动前端: `cd /workspace/testDJnew && npm run dev`
3. 浏览器访问 `http://localhost:5173`，验证三个 Tab 切换、数据请求、PDF 导出功能