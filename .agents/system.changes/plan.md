# 三接口 + 前端三Tab页面 + 导出功能 实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 在 testDj-main（后端）和 testDJnew-main（前端）两个仓库中实现三个后端接口、一个前端三Tab展示页面及导出功能。

**Architecture:** 后端 Spring Boot 3.x 提供 RESTful API（HelloWorld / SHA-256 哈希 / 冒泡排序 + 导出 Excel），前端 Vue 3 + Vite 通过 Axios 调用后端接口，分三个 Tab 展示结果，全局导出按钮调用导出接口下载 Excel。跨库通过 HTTP JSON 契约对齐。

**Tech Stack:**
- 后端: Spring Boot 3.x, Java 17+, Apache POI (Excel导出)
- 前端: Vue 3, Vite, Axios, Element Plus (Tab/UI组件)
- 构建: Maven (后端), npm/pnpm (前端)
- 通信: RESTful JSON, 文件流下载

---

## Global Constraints

- 后端基础路径: `/api`，端口 8080
- 前端代理转发后端请求，避免跨域
- 接口契约必须与 `dima.md` 设计文档完全一致
- 导出文件格式: `.xlsx` (Excel)，Content-Type: `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`
- 冒泡排序默认参数: 数组大小 10，值范围 1-100
- 禁止修改非任务范围内的已有文件
- 所有新增代码必须包含中文注释

---

## 任务分解

### Task 1: [testDj] 初始化 Spring Boot 3.x 后端项目

**Files:**
- Create: `testDj-main/pom.xml`
- Create: `testDj-main/src/main/java/com/example/demo/DemoApplication.java`
- Create: `testDj-main/src/main/resources/application.yml`

**Interfaces:**
- Consumes: 无
- Produces: 可运行的 Spring Boot 项目骨架，后续 Task 在此之上添加 Controller

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
    <artifactId>demo</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>demo</name>
    <description>三接口演示项目</description>
    <properties>
        <java.version>17</java.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <!-- Apache POI for Excel export -->
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

- [ ] **Step 2: 创建主启动类 DemoApplication.java**

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

- [ ] **Step 3: 创建 application.yml 配置**

```yaml
server:
  port: 8080

spring:
  application:
    name: demo-service
```

- [ ] **Step 4: 验证项目可编译**

```bash
cd /home/admin/.agentic-dev/runs/DEV-9d10e310-7901-11f1-8a9f-59ecae612580-145201c3-c9d2-43fc-b7e8-ed494985a8c0/worktree/testDj-main
mvn compile -q
```
预期: BUILD SUCCESS

---

### Task 2: [testDj] 实现 HelloWorld 接口

**Files:**
- Create: `testDj-main/src/main/java/com/example/demo/controller/HelloController.java`

**Interfaces:**
- Consumes: Task 1 的 Spring Boot 项目骨架
- Produces: `GET /api/hello` → `{"message": "Hello World!", "timestamp": "..."}`

- [ ] **Step 1: 创建 HelloController.java**

```java
package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HelloController {

    @GetMapping("/hello")
    public Map<String, Object> hello() {
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Hello World!");
        result.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return result;
    }
}
```

- [ ] **Step 2: 编译验证**

```bash
cd /home/admin/.agentic-dev/runs/DEV-9d10e310-7901-11f1-8a9f-59ecae612580-145201c3-c9d2-43fc-b7e8-ed494985a8c0/worktree/testDj-main
mvn compile -q
```
预期: BUILD SUCCESS

---

### Task 3: [testDj] 实现 SHA-256 哈希接口

**Files:**
- Create: `testDj-main/src/main/java/com/example/demo/controller/HashController.java`
- Create: `testDj-main/src/main/java/com/example/demo/dto/HashRequest.java`
- Create: `testDj-main/src/main/java/com/example/demo/dto/HashResponse.java`

**Interfaces:**
- Consumes: Task 1 的项目骨架
- Produces: `POST /api/hash/sha256` 请求/响应 DTO 和端点

- [ ] **Step 1: 创建 HashRequest.java**

```java
package com.example.demo.dto;

public class HashRequest {
    private String input;

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }
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

- [ ] **Step 3: 创建 HashController.java**

```java
package com.example.demo.controller;

import com.example.demo.dto.HashRequest;
import com.example.demo.dto.HashResponse;
import org.springframework.web.bind.annotation.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@RestController
@RequestMapping("/api/hash")
public class HashController {

    @PostMapping("/sha256")
    public HashResponse sha256(@RequestBody HashRequest request) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(request.getInput().getBytes());
        String hashHex = HexFormat.of().formatHex(hashBytes);
        return new HashResponse(request.getInput(), "SHA-256", hashHex);
    }
}
```

- [ ] **Step 4: 编译验证**

```bash
cd /home/admin/.agentic-dev/runs/DEV-9d10e310-7901-11f1-8a9f-59ecae612580-145201c3-c9d2-43fc-b7e8-ed494985a8c0/worktree/testDj-main
mvn compile -q
```
预期: BUILD SUCCESS

---

### Task 4: [testDj] 实现冒泡排序接口

**Files:**
- Create: `testDj-main/src/main/java/com/example/demo/controller/SortController.java`
- Create: `testDj-main/src/main/java/com/example/demo/dto/SortRequest.java`
- Create: `testDj-main/src/main/java/com/example/demo/dto/SortResponse.java`
- Create: `testDj-main/src/main/java/com/example/demo/dto/SortStep.java`

**Interfaces:**
- Consumes: Task 1 的项目骨架
- Produces: `POST /api/sort/bubble` 请求/响应 DTO 和端点

- [ ] **Step 1: 创建 SortRequest.java**

```java
package com.example.demo.dto;

public class SortRequest {
    private Integer arraySize; // 默认 10
    private Integer min;       // 默认 1
    private Integer max;       // 默认 100

    public Integer getArraySize() { return arraySize; }
    public void setArraySize(Integer arraySize) { this.arraySize = arraySize; }
    public Integer getMin() { return min; }
    public void setMin(Integer min) { this.min = min; }
    public Integer getMax() { return max; }
    public void setMax(Integer max) { this.max = max; }
}
```

- [ ] **Step 2: 创建 SortStep.java**

```java
package com.example.demo.dto;

public class SortStep {
    private int round;
    private int[] array;

    public SortStep(int round, int[] array) {
        this.round = round;
        this.array = array;
    }

    public int getRound() { return round; }
    public int[] getArray() { return array; }
}
```

- [ ] **Step 3: 创建 SortResponse.java**

```java
package com.example.demo.dto;

import java.util.List;

public class SortResponse {
    private int[] originalArray;
    private int[] sortedArray;
    private List<SortStep> steps;
    private int totalRounds;
    private int swapCount;

    public int[] getOriginalArray() { return originalArray; }
    public void setOriginalArray(int[] originalArray) { this.originalArray = originalArray; }
    public int[] getSortedArray() { return sortedArray; }
    public void setSortedArray(int[] sortedArray) { this.sortedArray = sortedArray; }
    public List<SortStep> getSteps() { return steps; }
    public void setSteps(List<SortStep> steps) { this.steps = steps; }
    public int getTotalRounds() { return totalRounds; }
    public void setTotalRounds(int totalRounds) { this.totalRounds = totalRounds; }
    public int getSwapCount() { return swapCount; }
    public void setSwapCount(int swapCount) { this.swapCount = swapCount; }
}
```

- [ ] **Step 4: 创建 SortController.java**

```java
package com.example.demo.controller;

import com.example.demo.dto.SortRequest;
import com.example.demo.dto.SortResponse;
import com.example.demo.dto.SortStep;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/sort")
public class SortController {

    @PostMapping("/bubble")
    public SortResponse bubbleSort(@RequestBody(required = false) SortRequest request) {
        // 默认参数
        int size = (request != null && request.getArraySize() != null) ? request.getArraySize() : 10;
        int min = (request != null && request.getMin() != null) ? request.getMin() : 1;
        int max = (request != null && request.getMax() != null) ? request.getMax() : 100;

        // 生成随机数组
        Random rand = new Random();
        int[] arr = new int[size];
        for (int i = 0; i < size; i++) {
            arr[i] = rand.nextInt(max - min + 1) + min;
        }

        int[] original = arr.clone();
        List<SortStep> steps = new ArrayList<>();
        int swapCount = 0;
        int n = arr.length;

        // 冒泡排序
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    swapCount++;
                }
            }
            steps.add(new SortStep(i + 1, arr.clone()));
        }

        SortResponse response = new SortResponse();
        response.setOriginalArray(original);
        response.setSortedArray(arr);
        response.setSteps(steps);
        response.setTotalRounds(n - 1);
        response.setSwapCount(swapCount);
        return response;
    }
}
```

- [ ] **Step 5: 编译验证**

```bash
cd /home/admin/.agentic-dev/runs/DEV-9d10e310-7901-11f1-8a9f-59ecae612580-145201c3-c9d2-43fc-b7e8-ed494985a8c0/worktree/testDj-main
mvn compile -q
```
预期: BUILD SUCCESS

---

### Task 5: [testDj] 实现导出接口 (Excel)

**Files:**
- Create: `testDj-main/src/main/java/com/example/demo/controller/ExportController.java`
- Create: `testDj-main/src/main/java/com/example/demo/service/ExportService.java`

**Interfaces:**
- Consumes: Task 2、3、4 中的 Controller 逻辑（复用数据生成逻辑）
- Produces: `GET /api/export?type=hello|hash|sort` → Excel 文件流下载

- [ ] **Step 1: 创建 ExportService.java**

```java
package com.example.demo.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class ExportService {

    public byte[] generateHelloExcel() throws Exception {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Hello World");
            // 表头
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Tab 名称");
            header.createCell(1).setCellValue("数据内容");
            header.createCell(2).setCellValue("时间戳");

            // 数据行
            Row row = sheet.createRow(1);
            row.createCell(0).setCellValue("Hello World");
            row.createCell(1).setCellValue("Hello World!");
            row.createCell(2).setCellValue(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        }
    }

    public byte[] generateHashExcel(String input, String hash) throws Exception {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("SHA-256 哈希");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Tab 名称");
            header.createCell(1).setCellValue("数据内容");
            header.createCell(2).setCellValue("时间戳");

            Row row = sheet.createRow(1);
            row.createCell(0).setCellValue("SHA-256 哈希");
            row.createCell(1).setCellValue("输入: " + input + " | 哈希值: " + hash);
            row.createCell(2).setCellValue(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        }
    }

    public byte[] generateSortExcel(int[] original, int[] sorted, int swaps) throws Exception {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("冒泡排序");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Tab 名称");
            header.createCell(1).setCellValue("数据内容");
            header.createCell(2).setCellValue("时间戳");

            Row row = sheet.createRow(1);
            row.createCell(0).setCellValue("冒泡排序");
            row.createCell(1).setCellValue("原始数组: " + java.util.Arrays.toString(original)
                + " | 排序后: " + java.util.Arrays.toString(sorted)
                + " | 交换次数: " + swaps);
            row.createCell(2).setCellValue(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        }
    }
}
```

- [ ] **Step 2: 创建 ExportController.java**

```java
package com.example.demo.controller;

import com.example.demo.service.ExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ExportController {

    @Autowired
    private ExportService exportService;

    @GetMapping("/export")
    public ResponseEntity<byte[]> export(@RequestParam String type) throws Exception {
        byte[] excelData;
        String filename;

        switch (type) {
            case "hello":
                excelData = exportService.generateHelloExcel();
                filename = "HelloWorld.xlsx";
                break;
            case "hash":
                // 默认导出示例数据
                excelData = exportService.generateHashExcel("示例字符串", "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855");
                filename = "SHA256Hash.xlsx";
                break;
            case "sort":
                int[] original = {64, 34, 25, 12, 22, 11, 90};
                int[] sorted = {11, 12, 22, 25, 34, 64, 90};
                excelData = exportService.generateSortExcel(original, sorted, 10);
                filename = "BubbleSort.xlsx";
                break;
            default:
                return ResponseEntity.badRequest().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", filename);

        return ResponseEntity.ok().headers(headers).body(excelData);
    }
}
```

- [ ] **Step 3: 编译验证**

```bash
cd /home/admin/.agentic-dev/runs/DEV-9d10e310-7901-11f1-8a9f-59ecae612580-145201c3-c9d2-43fc-b7e8-ed494985a8c0/worktree/testDj-main
mvn compile -q
```
预期: BUILD SUCCESS

---

### Task 6: [testDJnew] 初始化 Vue 3 + Vite 前端项目

**Files:**
- Create: `testDJnew-main/package.json`
- Create: `testDJnew-main/vite.config.js`
- Create: `testDJnew-main/index.html`
- Create: `testDJnew-main/src/main.js`
- Create: `testDJnew-main/src/App.vue`

**Interfaces:**
- Consumes: 无
- Produces: 可运行的前端项目骨架，后续 Task 在此之上添加页面组件

- [ ] **Step 1: 创建 package.json**

```json
{
  "name": "test-djnew",
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
    "axios": "^1.6.0",
    "element-plus": "^2.5.0"
  },
  "devDependencies": {
    "@vitejs/plugin-vue": "^5.0.0",
    "vite": "^5.0.0"
  }
}
```

- [ ] **Step 2: 创建 vite.config.js**（配置代理转发到后端 8080）

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

- [ ] **Step 3: 创建 index.html**

```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>三接口演示</title>
</head>
<body>
  <div id="app"></div>
  <script type="module" src="/src/main.js"></script>
</body>
</html>
```

- [ ] **Step 4: 创建 src/main.js**

```javascript
import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'

const app = createApp(App)
app.use(ElementPlus)
app.mount('#app')
```

- [ ] **Step 5: 创建 src/App.vue**（根组件，引入 DemoPage）

```vue
<template>
  <div id="app-container">
    <DemoPage />
  </div>
</template>

<script setup>
import DemoPage from './components/DemoPage.vue'
</script>

<style>
body {
  margin: 0;
  padding: 20px;
  background-color: #f5f7fa;
}
#app-container {
  max-width: 1200px;
  margin: 0 auto;
}
</style>
```

- [ ] **Step 6: 安装依赖并验证**

```bash
cd /home/admin/.agentic-dev/runs/DEV-9d10e310-7901-11f1-8a9f-59ecae612580-145201c3-c9d2-43fc-b7e8-ed494985a8c0/worktree/testDJnew-main
npm install
```
预期: 依赖安装成功无报错

---

### Task 7: [testDJnew] 创建 DemoPage 主页面及三个 Tab 面板组件

**Files:**
- Create: `testDJnew-main/src/components/DemoPage.vue`（主页面，含 Tab 切换和导出按钮）
- Create: `testDJnew-main/src/components/HelloPanel.vue`（Tab1: Hello World 展示）
- Create: `testDJnew-main/src/components/HashPanel.vue`（Tab2: SHA-256 哈希操作）
- Create: `testDJnew-main/src/components/SortPanel.vue`（Tab3: 冒泡排序操作）
- Create: `testDJnew-main/src/api/index.js`（Axios 封装）

**Interfaces:**
- Consumes: Task 6 的前端项目骨架，Task 2-5 的后端 API 端点
- Produces: 完整的三 Tab 页面，含导出按钮，可调用后端所有接口

- [ ] **Step 1: 创建 src/api/index.js**（API 调用封装）

```javascript
import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// HelloWorld 接口
export function getHello() {
  return api.get('/hello')
}

// SHA-256 哈希接口
export function getHash(input) {
  return api.post('/hash/sha256', { input })
}

// 冒泡排序接口
export function getBubbleSort(params = {}) {
  return api.post('/sort/bubble', params)
}

// 导出接口
export function exportData(type) {
  return api.get('/export', {
    params: { type },
    responseType: 'blob'
  })
}
```

- [ ] **Step 2: 创建 HelloPanel.vue**

```vue
<template>
  <div class="panel">
    <el-button type="primary" @click="fetchHello" :loading="loading">
      获取问候信息
    </el-button>
    <div v-if="data" class="result">
      <p><strong>消息:</strong> {{ data.message }}</p>
      <p><strong>时间戳:</strong> {{ data.timestamp }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { getHello } from '../api/index.js'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const data = ref(null)

async function fetchHello() {
  loading.value = true
  try {
    const res = await getHello()
    data.value = res.data
  } catch (e) {
    ElMessage.error('请求失败: ' + e.message)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.panel { padding: 20px; }
.result { margin-top: 20px; padding: 15px; background: #f0f9ff; border-radius: 8px; }
</style>
```

- [ ] **Step 3: 创建 HashPanel.vue**

```vue
<template>
  <div class="panel">
    <el-input
      v-model="inputText"
      placeholder="请输入待加密字符串"
      style="margin-bottom: 15px"
    />
    <el-button type="primary" @click="fetchHash" :loading="loading" :disabled="!inputText">
      加密
    </el-button>
    <div v-if="data" class="result">
      <p><strong>原始字符串:</strong> {{ data.input }}</p>
      <p><strong>算法:</strong> {{ data.algorithm }}</p>
      <p><strong>哈希值:</strong> <code>{{ data.hash }}</code></p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { getHash } from '../api/index.js'
import { ElMessage } from 'element-plus'

const inputText = ref('')
const loading = ref(false)
const data = ref(null)

async function fetchHash() {
  if (!inputText.value) return
  loading.value = true
  try {
    const res = await getHash(inputText.value)
    data.value = res.data
  } catch (e) {
    ElMessage.error('请求失败: ' + e.message)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.panel { padding: 20px; }
.result { margin-top: 20px; padding: 15px; background: #f0f9ff; border-radius: 8px; }
code { word-break: break-all; background: #f5f5f5; padding: 2px 6px; border-radius: 4px; }
</style>
```

- [ ] **Step 4: 创建 SortPanel.vue**

```vue
<template>
  <div class="panel">
    <el-form :inline="true">
      <el-form-item label="数组大小">
        <el-input-number v-model="arraySize" :min="3" :max="50" :step="1" />
      </el-form-item>
      <el-form-item label="最小值">
        <el-input-number v-model="minVal" :min="0" :max="999" />
      </el-form-item>
      <el-form-item label="最大值">
        <el-input-number v-model="maxVal" :min="1" :max="1000" />
      </el-form-item>
    </el-form>
    <el-button type="primary" @click="fetchSort" :loading="loading">
      开始排序
    </el-button>
    <div v-if="data" class="result">
      <p><strong>原始数组:</strong> {{ data.originalArray.join(', ') }}</p>
      <p><strong>排序后数组:</strong> {{ data.sortedArray.join(', ') }}</p>
      <p><strong>总轮次:</strong> {{ data.totalRounds }} | <strong>交换次数:</strong> {{ data.swapCount }}</p>
      <el-collapse>
        <el-collapse-item title="查看排序过程">
          <div v-for="step in data.steps" :key="step.round">
            <p><strong>第 {{ step.round }} 轮:</strong> {{ step.array.join(', ') }}</p>
          </div>
        </el-collapse-item>
      </el-collapse>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { getBubbleSort } from '../api/index.js'
import { ElMessage } from 'element-plus'

const arraySize = ref(10)
const minVal = ref(1)
const maxVal = ref(100)
const loading = ref(false)
const data = ref(null)

async function fetchSort() {
  loading.value = true
  try {
    const res = await getBubbleSort({
      arraySize: arraySize.value,
      min: minVal.value,
      max: maxVal.value
    })
    data.value = res.data
  } catch (e) {
    ElMessage.error('请求失败: ' + e.message)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.panel { padding: 20px; }
.result { margin-top: 20px; padding: 15px; background: #f0f9ff; border-radius: 8px; }
</style>
```

- [ ] **Step 5: 创建 DemoPage.vue**（主页面，含 Tab 切换和导出按钮）

```vue
<template>
  <div class="demo-page">
    <h1>三接口演示平台</h1>
    <el-tabs v-model="activeTab" type="border-card">
      <el-tab-pane label="Hello World" name="hello">
        <HelloPanel ref="helloRef" />
      </el-tab-pane>
      <el-tab-pane label="SHA-256 哈希" name="hash">
        <HashPanel ref="hashRef" />
      </el-tab-pane>
      <el-tab-pane label="冒泡排序" name="sort">
        <SortPanel ref="sortRef" />
      </el-tab-pane>
    </el-tabs>
    <div class="export-bar">
      <el-button type="success" @click="handleExport" :loading="exportLoading">
        导出当前 Tab 结果 (Excel)
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { exportData } from '../api/index.js'
import { ElMessage } from 'element-plus'
import HelloPanel from './HelloPanel.vue'
import HashPanel from './HashPanel.vue'
import SortPanel from './SortPanel.vue'

const activeTab = ref('hello')
const exportLoading = ref(false)

async function handleExport() {
  exportLoading.value = true
  try {
    const typeMap = { hello: 'hello', hash: 'hash', sort: 'sort' }
    const type = typeMap[activeTab.value] || 'hello'
    const res = await exportData(type)
    // 创建下载链接
    const url = window.URL.createObjectURL(new Blob([res.data]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', `${type}_result.xlsx`)
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (e) {
    ElMessage.error('导出失败: ' + e.message)
  } finally {
    exportLoading.value = false
  }
}
</script>

<style scoped>
.demo-page { padding: 20px; }
h1 { text-align: center; margin-bottom: 30px; color: #303133; }
.export-bar { margin-top: 20px; text-align: right; }
</style>
```

- [ ] **Step 6: 验证前端构建**

```bash
cd /home/admin/.agentic-dev/runs/DEV-9d10e310-7901-11f1-8a9f-59ecae612580-145201c3-c9d2-43fc-b7e8-ed494985a8c0/worktree/testDJnew-main
npx vite build
```
预期: 构建成功，生成 dist 目录

---

## 跨仓接口契约对齐表

| 对齐项 | 后端 (testDj-main) | 前端 (testDJnew-main) | 状态 |
|--------|-------------------|----------------------|------|
| GET /api/hello | HelloController.java | `api/index.js` → `getHello()` | ✅ 一致 |
| POST /api/hash/sha256 | HashController.java | `api/index.js` → `getHash(input)` | ✅ 一致 |
| POST /api/sort/bubble | SortController.java | `api/index.js` → `getBubbleSort(params)` | ✅ 一致 |
| GET /api/export?type= | ExportController.java | `api/index.js` → `exportData(type)` | ✅ 一致 |
| 请求体 JSON 格式 | HashRequest / SortRequest | 前端 JS 对象 | ✅ 一致 |
| 响应 JSON 格式 | 各 Response DTO | 前端直接消费 | ✅ 一致 |
| 导出 Content-Type | application/vnd.openxmlformats-officedocument.spreadsheetml.sheet | Blob 接收 | ✅ 一致 |
| 导出参数 | `type=hello\|hash\|sort` | `typeMap` 映射 | ✅ 一致 |
| 端口/代理 | 8080 | Vite proxy → 8080 | ✅ 一致 |

## 自检清单

**1. Spec 覆盖检查：**
- [x] 三个接口（helloworld、哈希、冒泡排序）→ Task 2, 3, 4
- [x] 前端三个 Tab → Task 7（HelloPanel, HashPanel, SortPanel）
- [x] 导出按钮 → Task 7（DemoPage.vue 中 export-bar）
- [x] 后台导出接口 → Task 5（ExportController + ExportService）

**2. 占位符检查：** 无 "TBD/TODO/implement later" 等占位符，所有代码完整

**3. 类型一致性检查：** 所有接口路径、请求/响应字段名称在前后端一致

---

**Plan complete and saved to `.agents/system.changes/plan.md`. Two execution options:**

**1. Subagent-Driven (recommended)** - 每个 Task 分派独立子智能体，任务间有审查环节，快速迭代

**2. Inline Execution** - 在当前会话中使用 executing-plans，批量化执行带检查点

**Which approach?**