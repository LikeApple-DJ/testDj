# Code Review Report

> **Change** 分别写三个接口helloworld、哈希算法以及冒泡排序；前端新增一个页面，有三个tab分别展示不同的执行结果 新增导出按钮，后台提供导出接口，支持导出各个页面的展示结果 · **分支/Commit** `AI/task-DEV-9d10e310-7901-11f1-8a9f-59ecae612580-70d0a07e-96cb-4ec9-aa0d-a0b7609bc035` / `HEAD` · **日期** `2026-08-17` · **审查者** AI
>
> **AI**：等级 **P0 / P1 / P2**；G/S 以 checklist 行内定义为准；Bug 模式以 `bug-pattern-checklist.md` 表头为准（Blocker→P0、Major→P1、Info→P2）。已运行 `scan-all-rules.sh` 并将要点并入 §5。问题含 `path:line` 及清单 ID。每个 ❌/⚠️ 问题在 §7 后附 `.java` 问题片段。

---

## 1. 审查范围

| 项 | 值 |
|----|-----|
| `.java` 文件数 | 14 |
| 变更行数 | `+432 / -0`（新增文件，估算） |

| 类/接口 | 路径 | 角色 |
|---------|------|------|
| TestDjApplication | `src/main/java/com/testdj/TestDjApplication.java` | Spring Boot 启动入口 |
| WebConfig | `src/main/java/com/testdj/config/WebConfig.java` | CORS 跨域配置 |
| HelloController | `src/main/java/com/testdj/controller/HelloController.java` | Hello World REST 控制器 |
| HashController | `src/main/java/com/testdj/controller/HashController.java` | SHA-256 哈希 REST 控制器 |
| SortController | `src/main/java/com/testdj/controller/SortController.java` | 冒泡排序 REST 控制器 |
| ExportController | `src/main/java/com/testdj/controller/ExportController.java` | PDF 导出 REST 控制器 |
| HelloRequest | `src/main/java/com/testdj/dto/HelloRequest.java` | Hello 请求 DTO |
| HashRequest | `src/main/java/com/testdj/dto/HashRequest.java` | 哈希请求 DTO |
| SortRequest | `src/main/java/com/testdj/dto/SortRequest.java` | 排序请求 DTO |
| ExportRequest | `src/main/java/com/testdj/dto/ExportRequest.java` | 导出请求 DTO（含 resultData 扩展字段） |
| HelloService | `src/main/java/com/testdj/service/HelloService.java` | Hello 业务逻辑 |
| HashService | `src/main/java/com/testdj/service/HashService.java` | SHA-256 哈希计算逻辑 |
| SortService | `src/main/java/com/testdj/service/SortService.java` | 冒泡排序算法逻辑 |
| ExportService | `src/main/java/com/testdj/service/ExportService.java` | PDF 生成逻辑 |

---

## 2. 问题计数

| P0 | P1 | P2 |
|----|----|-----|
| 0 | 2 | 1 |

---

## 3. Step 2 — 功能（REQ）

### REQ-1: Hello World 接口（F01）
> Spec 来源：设计文档 §5.1 W01，实施计划 Task 1

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| `POST /api/hello` 返回问候语 | ✅ | 设计文档 §5.1.2 W01: "接收用户名，返回问候语" | `HelloController.java:22-28` | 返回 `{tab, message, input}`，格式正确 |
| name 为空时默认 "World" | ✅ | 设计文档 §5.1.2: "若 name 为 null 或空白，默认使用 'World'" | `HelloService.java:9-11` | 在 Service 层处理空值默认逻辑 |
| 响应格式含 tab 字段 | ✅ | 设计文档 §5.1.2: 出参含 tab/message/input | `HelloController.java:24-27` | Map.of 构建正确 |

### REQ-2: SHA-256 哈希接口（F02）
> Spec 来源：设计文档 §5.2 W02，实施计划 Task 2

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| `POST /api/hash` 返回哈希值 | ✅ | 设计文档 §5.2.2 W02: "返回 SHA-256 哈希值（十六进制）" | `HashController.java:22-31` | 返回 `{tab, algorithm, input, hash}` |
| 空输入返回空字符串哈希 | ✅ | 设计文档 §5.2.2: "input 为 null 时当作空字符串处理" | `HashController.java:23` | null 转为空串 |
| 输出为小写十六进制 | ✅ | 设计文档 §5.2.2: "输出为小写十六进制字符串" | `HashService.java:20-27` | 使用 `Integer.toHexString(0xff & b)` 小写输出 |

### REQ-3: 冒泡排序接口（F03）
> Spec 来源：设计文档 §5.3 W03，实施计划 Task 3

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| `POST /api/bubble-sort` 返回排序结果 | ✅ | 设计文档 §5.3.2 W03: "返回冒泡排序后的结果" | `SortController.java:23-31` | 返回 `{tab, original, sorted, length}` |
| 升序排列 | ✅ | 设计文档 §5.3.2: "使用冒泡排序算法，升序排列" | `SortService.java:13-21` | 标准冒泡排序实现 |
| 操作时复制原数组 | ✅ | 设计文档 §5.3.2: "操作时复制原数组，不修改原始输入" | `SortService.java:11` | `new ArrayList<>(array)` 复制 |

### REQ-4: 前端 Tab 页面（F04）
> Spec 来源：设计文档 §1 "三个 Tab 展示不同执行结果"

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 三个 Tab 可切换 | ✅ | 设计文档 §1: "三个 Tab 展示不同执行结果" | 前端 `Dashboard.jsx` | Hello/Hash/Sort 三个 Tab，前端组件已验证 |
| 各 Tab 展示对应结果 | ✅ | 设计文档: 前端组件对应各接口 | 前端 `HelloTab.jsx`, `HashTab.jsx`, `SortTab.jsx` | 各组件独立管理输入和结果展示 |

### REQ-5: 导出按钮及导出接口（F05）
> Spec 来源：设计文档 §5.4 W04，实施计划 Task 4，原始需求 "支持导出各个页面的展示结果"

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| `POST /api/export` 返回 PDF | ✅ | 设计文档 §5.4.2: "生成对应结果的 PDF 文件并返回" | `ExportController.java:24-43` | 返回 `application/pdf` 二进制流 |
| 支持三种 Tab 导出 | ✅ | 设计文档 §5.4.2: "支持 'hello'、'hash'、'sort' 三种 Tab" | `ExportService.java:56-61` | switch 覆盖三种类型 |
| **导出实际页面展示结果** | ✅ | 需求: "支持导出各个页面的展示结果" | `ExportController.java:26` `request.getResultData()` | 接收前端传入的实时结果数据，非硬编码 |
| **ExportRequest 扩展字段** | ⚠️ | Spec 仅定义 `tab` 字段 | `ExportRequest.java:7` 新增 `resultData` 字段 | 向后兼容扩展，新增字段不影响原有 `{tab}` 请求 |

### REQ-6: API 契约一致性（跨仓对齐点）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 前端 callHello(name) → POST /api/hello → {tab,message,input} | ✅ | Spec §跨仓对齐点: 前端调用 vs 后端端点 | `HelloController.java:15,22-28` | 契约一致 |
| 前端 callHash(input) → POST /api/hash → {tab,algorithm,input,hash} | ✅ | Spec §跨仓对齐点 | `HashController.java:15,22-31` | 契约一致 |
| 前端 callBubbleSort(array) → POST /api/bubble-sort → {tab,original,sorted,length} | ✅ | Spec §跨仓对齐点 | `SortController.java:16,23-31` | 契约一致 |
| 前端 exportTab(tab) → POST /api/export → PDF binary | ✅ | Spec §跨仓对齐点 | `ExportController.java:17,24-43` | 契约一致 |

---

## 4. Step 3 — 可读性检查

| 结果 | 说明（违规写 Ax.x 与 `path:行`） |
|------|--------------------------------|
| ✅ | **A1 源文件格式** — 所有文件 UTF-8 编码，行尾 LF，无 BOM |
| ✅ | **A2 源文件结构/import 顺序** — import 分组合理，先 org.springframework 再 java.util 等 |
| ✅ | **A3 代码样式** — 缩进 4 空格一致，K&R 大括号风格，关键字与括号间空格正确 |
| ✅ | **A4 命名规范** — 类名 PascalCase（HelloController），方法名 camelCase（greet/sha256/bubbleSort），字段名 camelCase，包名全小写 |
| ⚠️ | **A5 编码实践** — `SortService.java:11` `new ArrayList<>(array)` 当 array 为 null 时抛出 NPE。虽 spec 设计文档 R03 说明接受 NPE 行为，但 REST API 层应做防御性校验 |
| ✅ | **A6 特定元素样式** — 枚举、注解、switch 表达式使用正确 |
| ⚠️ | **A7 Javadoc 规范** — 所有 14 个 Java 文件缺少 Javadoc 类注释和方法注释。鉴于方法名自解释且无复杂业务逻辑，等级为 P2 |

---

## 5. Step 4 — 可靠性检查

| 域 | 参考 | 结果 | 等级 | 说明（列命中 ID 或「已扫无命中」） |
|----|------|------|------|-------------------------------------|
| 可靠性 | `reliability-checklist.md` G1–G17 | ⚠️ | P1 | **G8.7** — `SortController.java:24` 缺少 null 校验，`request.getArray()` 可能为 null 导致 NPE；**G16.4** — `ExportController.java:24-43` 缺少对 PDF 生成过程的 try-catch 异常处理 |
| 安全 | `security-checklist.md` S1–S10 | ✅ | — | 已扫无命中：无 SQL 操作、无文件上传、无认证、无敏感数据；CORS 配置正确（S10.1） |
| Bug 模式 | `bug-pattern-checklist.md` B/M/I（120） | ✅ | — | 预扫 `scan-all-rules.sh` 检出 1 条 **G16.2** `HashService.java:28`，经复核为 **误报**（代码 L29 已调用 `log.error()`）。LLM 补充扫描 B001–B081 / M001–M027 / I001–I010 均未发现违规 |

### 预扫扫描结果汇总（`scan-all-rules.sh`）

| 规则 ID | 等级 | 文件 | 行号 | 说明 | 复核结论 |
|---------|------|------|------|------|---------|
| G16.2 | P1 (清单) | `HashService.java` | 28 | CatchWithoutLogging — 捕获异常未记录日志 | **误报**：代码 L29 有 `log.error("SHA-256 algorithm not available", e)`，日志记录完整 |

### 可靠性明细（LLM 补充扫描）

| ID | 状态 | 说明 |
|----|------|------|
| G1.1–G1.4 并发控制 | N/A | 无状态计算，无共享数据写入 |
| G2.1–G2.3 事务 | N/A | 无数据库操作 |
| G3.1–G3.2 资源释放 | ✅ | ByteArrayOutputStream 自动回收，iText Document.close() 正确关闭 |
| G4.1–G4.4 日志 | ✅ | 规范使用 SLF4J，异常含堆栈（HashService.java:29） |
| G5.1 超时 | ✅ | 前端 Axios 设置 10s 超时；后端无同步等待 |
| G6.1–G6.2 重试/幂等 | N/A | 非幂等场景（每次导出生成新 PDF） |
| G7.1–G7.2 限流 | N/A | 演示应用，未配置限流 |
| G8.1–G8.7 参数校验 | ⚠️ | **P1** — `SortController.java:24` `request.getArray()` 可能为 null 直接传入 Service，导致 NPE；`ExportController.java:25` tab 参数未校验是否在允许值范围内 |
| G9.1–G9.3 枚举/常量 | ✅ | switch 覆盖所有已知 case + default |
| G10.1–G10.3 序列化 | ✅ | Jackson 默认配置，DTO 有正确 getter/setter |
| G11.1–G11.4 日期时间 | ✅ | 使用 `java.time.LocalDateTime` + `ZoneId.of("Asia/Shanghai")` |
| G12.1–G12.2 集合处理 | ✅ | 使用防御性拷贝（SortService.java:11） |
| G13.1 流处理 | ✅ | 无复杂流操作 |
| G14.1–G14.4 灰度/监控 | N/A | 演示应用，未配置 |
| G15.1–G15.3 应急 | N/A | 演示应用，未配置 |
| G16.1–G16.4 异常处理 | ⚠️ | **P1** — `ExportController.java:24-43` 未对 PDF 生成过程中的潜在异常（如 iText 库异常）做 try-catch 处理，异常直接上抛给 Spring 默认错误处理，返回 500 但无友好错误信息 |
| G17.1–G17.3 安全补强 | N/A | 无敏感数据 |

### 安全明细（`security-checklist.md` S1–S10）

| ID | 状态 | 说明 |
|----|------|------|
| S1.1–S1.3 SQL 注入 | N/A | 无数据库操作 |
| S2.1–S2.3 敏感数据 | N/A | 用户输入文本无敏感信息要求 |
| S3.1–S3.3 文件上传 | N/A | 无文件上传操作 |
| S4.1–S4.2 命令执行 | N/A | 无命令执行 |
| S5.1–S5.2 SSRF | N/A | 无外部 URL 请求 |
| S6.1–S6.3 XXE | N/A | 无 XML 解析 |
| S7.1–S7.3 认证/授权 | N/A | 公开接口，无需认证 |
| S8.1–S8.4 会话管理 | N/A | 无状态服务 |
| S9.1–S9.4 日志脱敏 | N/A | 无敏感日志输出 |
| **S10.1–S10.3 CORS** | ✅ | CORS 配置精确锁定 `localhost:5173` 和 `localhost:3000`，方法限制为 GET/POST/OPTIONS |

---

## 6. Step 5 — 自定义扩展检查

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| 自定义扩展 | `customized-checklist.md` U* | N/A | — | 未启用自定义规则（文件仅为模板，无实际启用的规则条目） |

---

## 7. 结论

- **合并建议**：修复后合并
- **P0**：无
- **P1**：
  1. **G8.7** `SortController.java:24` — 缺少对 `request.getArray()` 的 null 校验，array 为 null 时直接传入 `SortService.bubbleSort(original)` 会抛出 NPE。建议添加防御性检查并在 null 时返回 400 错误响应。
  2. **G16.4** `ExportController.java:24-43` — 缺少对 PDF 生成过程的 try-catch 异常处理，异常直接上抛给 Spring 默认错误处理器。建议使用 try-catch 包装并返回含错误信息的响应。
- **P2**：
  1. **A7** 所有 14 个 Java 文件缺少 Javadoc 类/方法注释。建议为 Controller 和 Service 类添加简要 Javadoc。
- **一句话**：代码质量整体良好，功能实现与 spec 一致，跨仓接口契约对齐正确；P0 问题已在问题修复阶段全部解决，当前仅余 2 个 P1 可靠性问题和 1 个 P2 可读性建议，修复后即可合并。

---

## 7.1 问题片段（必填）

### P1 G8.7 — SortController.java:24 缺少空值校验

- **P1** `G8.7` `src/main/java/com/testdj/controller/SortController.java:24` — `request.getArray()` 可能为 null，直接传入 Service 导致 NPE。
  片段范围：`src/main/java/com/testdj/controller/SortController.java:22-31`

```java
L22|    @PostMapping
L23|    public ResponseEntity<Map<String, Object>> sort(@RequestBody SortRequest request) {
L24|        List<Integer> original = request.getArray();  // 问题：可能为 null
L25|        List<Integer> sorted = sortService.bubbleSort(original);  // 传入 null，SortService 中 new ArrayList<>(null) 抛 NPE
L26|        return ResponseEntity.ok(Map.of(
L27|                "tab", "sort",
L28|                "original", original,
L29|                "sorted", sorted,
L30|                "length", original != null ? original.size() : 0
L31|        ));
```

### P1 G16.4 — ExportController.java:24-43 缺少异常处理

- **P1** `G16.4` `src/main/java/com/testdj/controller/ExportController.java:24-43` — PDF 生成缺少 try-catch，iText 库异常直接上抛。
  片段范围：`src/main/java/com/testdj/controller/ExportController.java:24-43`

```java
L24|    public ResponseEntity<byte[]> export(@RequestBody ExportRequest request) {
L25|        String tab = request.getTab() != null ? request.getTab() : "hello";
L26|        Map<String, Object> resultData = request.getResultData();
L27|
L28|        if (resultData == null) {
L29|            resultData = Map.of();
L30|        }
L31|
L32|        byte[] pdfBytes = exportService.exportTabResult(tab, resultData);  // 潜在异常未捕获
L33|
L34|        HttpHeaders headers = new HttpHeaders();
L35|        headers.setContentType(MediaType.APPLICATION_PDF);
L36|        headers.setContentDispositionFormData("attachment", tab + "_result.pdf");
L37|        headers.setContentLength(pdfBytes.length);
L38|
L39|        return ResponseEntity.ok()
L40|                .headers(headers)
L41|                .body(pdfBytes);
L42|    }
```

### P2 A7 — 所有文件缺少 Javadoc

- **P2** `A7` — 所有 14 个 Java 文件缺少 Javadoc 类/方法注释。
  片段范围：`src/main/java/com/testdj/controller/HelloController.java:14-18`

```java
L14|@RestController
L15|@RequestMapping("/api/hello")
L16|public class HelloController {  // 建议添加 @param 描述类用途
L17|
L18|    @Autowired
L19|    private HelloService helloService;
```

---

## 8. 修复任务列表

### P0

- 无待修复项。

### P1

- [ ] **P1** `src/main/java/com/testdj/controller/SortController.java:24` — 为 `request.getArray()` 添加 null 校验，若为 null 则返回 400 错误响应或空数组。
- [ ] **P1** `src/main/java/com/testdj/controller/ExportController.java:24-43` — 为 `exportService.exportTabResult()` 调用添加 try-catch，捕获异常后返回含错误描述的响应（如 500 + JSON 错误消息）。

### P2（可选）

- [ ] **P2** 所有 Controller 和 Service 类 — 添加 Javadoc 类注释（简要说明类用途）和关键方法注释。