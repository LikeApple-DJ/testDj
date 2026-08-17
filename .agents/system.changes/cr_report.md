# 代码评审报告 (Code Review Report)

> **评审阶段:** loop-2 (代码评审)
> **评审范围:** testDj-main (后端) + testDJnew-main (前端)
> **需求:** 三接口 (HelloWorld / SHA-256 Hash / Bubble Sort) + 前端三Tab页 + 导出Excel
> **评审日期:** 2025-01

---

## 一、评审概要

### 本次变更规模
| 维度 | 数据 |
|------|------|
| 涉及仓库 | 2 (testDj-main, testDJnew-main) |
| 新增文件 | 17 个 |
| 后端 | 11 个文件 (pom.xml, DemoApplication.java, application.yml, 3 Controller, 5 DTO, 1 Service) |
| 前端 | 6 个文件 (index.html, package.json, vite.config.js, main.js, App.vue, DemoPage.vue, 3 Panel 组件, api/index.js) |

### 需求覆盖检查
| 需求 | 实现状态 | 说明 |
|------|---------|------|
| HelloWorld 接口 (GET /api/hello) | ✅ 已实现 | HelloController.java |
| SHA-256 哈希接口 (POST /api/hash/sha256) | ✅ 已实现 | HashController.java + HashRequest/Response |
| 冒泡排序接口 (POST /api/sort/bubble) | ✅ 已实现 | SortController.java + SortRequest/Response/Step |
| 前端三个 Tab | ✅ 已实现 | DemoPage.vue + HelloPanel/HashPanel/SortPanel |
| 导出按钮 + 导出接口 | ⚠️ 部分实现 | 存在 blocking 问题 (见下文) |

---

## 二、缺陷与建议

### 🔴 [blocking] 必须修复 - 1 个

---

#### 🔴 B1: DemoPage.vue 中 ref 位置错误导致导出数据无法获取子组件实例

**文件:** `[testDJnew] src/components/DemoPage.vue` (第 6-13 行)

**问题描述:**
```vue
<el-tab-pane label="Hello World" name="hello">
  <HelloPanel ref="helloRef" />       <!-- ❌ ref 应放在 HelloPanel 上 -->
</el-tab-pane>
<el-tab-pane label="SHA-256 哈希" name="hash">
  <HashPanel ref="hashRef" />         <!-- ❌ ref 应放在 HashPanel 上 -->
</el-tab-pane>
<el-tab-pane label="冒泡排序" name="sort">
  <SortPanel ref="sortRef" />         <!-- ❌ ref 应放在 SortPanel 上 -->
</el-tab-pane>
```

`ref` 放置在 `<el-tab-pane>` 上时，获取的是 Element Plus 的 `ElTabPane` 组件实例，**而非**内部的 `HelloPanel`/`HashPanel`/`SortPanel` 组件实例。因此：

- `hashRef.value?.data` → `undefined`（因为 ElTabPane 实例没有 data 属性）
- `sortRef.value?.data` → `undefined`

导致导出 hash/sort 类型时，**无法获取当前用户操作的实时数据**，只能回退到后端 `ExportController` 中硬编码的示例数据。

**影响:** 用户在当前 Tab 中操作的数据无法被导出，导出的只是固定示例数据，功能存在严重缺陷。

**修复建议:**
```vue
<el-tab-pane label="Hello World" name="hello">
  <HelloPanel ref="helloRef" />
</el-tab-pane>
<el-tab-pane label="SHA-256 哈希" name="hash">
  <HashPanel ref="hashRef" />
</el-tab-pane>
<el-tab-pane label="冒泡排序" name="sort">
  <SortPanel ref="sortRef" />
</el-tab-pane>
```

将 `ref` 从 `<el-tab-pane>` 移动到内部的子组件标签上即可。

---

### 🟡 [important] 建议修复 - 2 个

---

#### 🟡 I1: HashController 缺少全局异常处理，参数校验失败返回 500

**文件:** `[testDj] src/main/java/com/example/demo/controller/HashController.java` (第 18-19 行)

**问题描述:**
```java
if (request.getInput() == null || request.getInput().isBlank()) {
    throw new IllegalArgumentException("input 不能为空");
}
```

`IllegalArgumentException` 是运行时异常，没有对应的 `@ExceptionHandler` 或全局 `@ControllerAdvice`，Spring Boot 默认返回 `HTTP 500 Internal Server Error`。语义上应返回 `HTTP 400 Bad Request`。

**影响:** 客户端输入空字符串时收到 500 错误，体验不佳且不符合 RESTful 规范。

**修复建议:**
- 方式一：创建 `@ControllerAdvice` 全局异常处理器，将 `IllegalArgumentException` 映射为 400
- 方式二：在 Controller 中捕获并返回 `ResponseEntity.badRequest().body(...)`

---

#### 🟡 I2: ExportService 方法签名抛出 `throws Exception` 过于宽泛

**文件:** `[testDj] src/main/java/com/example/demo/service/ExportService.java` (第 13, 39, 63 行)

**问题描述:**
```java
public byte[] generateHelloExcel() throws Exception { ... }
public byte[] generateHashExcel(String input, String hash) throws Exception { ... }
public byte[] generateSortExcel(int[] original, int[] sorted, int swaps) throws Exception { ... }
```

三个方法均抛出 `throws Exception`，是宽泛的异常声明，无法区分具体异常类型（IOException、POI 异常等），且调用方必须用通用 catch 处理。

**影响:** 降低代码可维护性，异常分类不清晰。

**修复建议:** 抛出具体异常类型 (如 `IOException`)，或在方法内部捕获并转换为自定义运行时异常。

---

### 🟢 [nit] 非阻塞建议 - 3 个

---

#### 🟢 N1: 部分 DTO 类缺少中文注释

**文件:**
- `[testDj] src/main/java/com/example/demo/dto/HashRequest.java`
- `[testDj] src/main/java/com/example/demo/dto/HashResponse.java`
- `[testDj] src/main/java/com/example/demo/dto/SortResponse.java`
- `[testDj] src/main/java/com/example/demo/dto/SortStep.java`

**建议:** 为类和关键字段补充中文注释，与 `SortRequest.java` 保持一致。

---

#### 🟢 N2: 前端 exportData API 参数隐式依赖 Axios 展开

**文件:** `[testDJnew] src/api/index.js` (第 24-29 行)

**问题描述:**
```javascript
export function exportData(params) {
  return api.get('/export', {
    params: params,
    responseType: 'blob'
  })
}
```

`params` 对象被 Axios 展开为 URL 查询参数，与后端 `@RequestParam` 形成隐式耦合。缺少参数名映射层，后端参数名变更时前端不会报错但会静默失效。

**建议:** 可考虑添加参数名映射或 TypeScript 接口定义增强类型安全。

---

#### 🟢 N3: ExportService 中三个导出方法存在代码重复

**文件:** `[testDj] src/main/java/com/example/demo/service/ExportService.java`

**问题描述:** `generateHelloExcel`、`generateHashExcel`、`generateSortExcel` 三个方法结构高度相似（创建工作簿、创建表头行、写入数据行、自动列宽、输出字节数组），存在重复代码。

**建议:** 抽取公共方法 `generateExcel(String sheetName, String[] headers, String[] dataRow)` 减少重复。

---

## 三、跨仓接口契约对齐检查

| 对齐项 | 后端 (testDj-main) | 前端 (testDJnew-main) | 状态 |
|--------|-------------------|----------------------|------|
| GET /api/hello | HelloController.java | `api/index.js` → `getHello()` | ✅ 一致 |
| POST /api/hash/sha256 | HashController.java | `api/index.js` → `getHash(input)` | ✅ 一致 |
| POST /api/sort/bubble | SortController.java | `api/index.js` → `getBubbleSort(params)` | ✅ 一致 |
| GET /api/export?type= | ExportController.java | `api/index.js` → `exportData(params)` | ✅ 一致 |
| 请求体 JSON 格式 | HashRequest/SortRequest | 前端 JS 对象 | ✅ 一致 |
| 响应 JSON 格式 | 各 Response DTO | 前端直接消费 | ✅ 一致 |
| 导出 Content-Type | application/vnd.openxmlformats-officedocument.spreadsheetml.sheet | Blob 接收 | ✅ 一致 |
| 导出参数 | type/input/hash/original/sorted/swaps | params 对象展开 | ✅ 一致 |
| 端口/代理 | 8080 | Vite proxy → 8080 | ✅ 一致 |

**跨仓对齐结论:** ✅ 全部对齐，接口路径、请求/响应格式、导出参数名均一致。

---

## 四、代码质量亮点

1. **构造器注入替代 @Autowired** — ExportController 使用了构造器注入，符合 Spring 官方推荐
2. **输入校验** — HashController 做了 null/blank 校验，SortController 处理了 nullable 请求体
3. **显式指定字符集** — HashController 使用 `StandardCharsets.UTF_8`，确保跨平台哈希一致性
4. **try-with-resources** — ExportService 使用 try-with-resources 自动关闭 Workbook
5. **defineExpose 暴露组件数据** — 三个 Panel 组件均通过 `defineExpose` 暴露数据供父组件导出使用（设计思路正确，但 ref 位置有 bug）
6. **自动列宽** — ExportService 导出的 Excel 通过 `autoSizeColumn` 自动调整列宽
7. **前端 trim 处理** — HashPanel 对输入做了 trim 处理，避免多余空格导致哈希结果不一致
8. **静态 Random 复用** — SortController 使用静态 Random 实例避免每次请求创建新对象
9. **前端导出传递实时数据** — DemoPage.vue 的 handleExport 将当前组件数据作为参数传递给后端（设计正确，但 ref 绑定位置错误导致未生效）
10. **pom.xml 配置完整** — 包含 sourceEncoding 配置、Apache POI 5.2.5 依赖

---

## 五、评审结论

| 类别 | 数量 |
|------|------|
| 🔴 blocking | 1 |
| 🟡 important | 2 |
| 🟢 nit | 3 |
| **总计** | **6** |

### 最终决定: 🔄 需要修复 (Request Changes)

🔴 **B1** 必须在合并前修复——否则导出功能在 hash 和 sort 模式下无法获取用户当前数据，只能导出后端硬编码示例数据，功能存在严重缺陷。

🟡 **I1、I2** 建议修复以提升代码鲁棒性和可维护性。