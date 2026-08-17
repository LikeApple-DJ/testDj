# Code Review Report

> **Change** 分别写三个接口helloworld、哈希算法以及冒泡排序；前端新增页面与导出功能 · **分支/Commit** `AI/task-DEV-9d10e310-7901-11f1-8a9f-59ecae612580-70d0a07e-96cb-4ec9-aa0d-a0b7609bc035` · **日期** 2026-08-17 · **审查者** AI
>
> **AI**：等级 **P0 / P1 / P2**；G/S 以 checklist 行内定义为准；Bug 模式以 `bug-pattern-checklist.md` 表头为准（Blocker→P0、Major→P1、Info→P2）。已运行 `scan-all-rules.sh` 并将要点并入 §5。问题含 `path:line` 及清单 ID。每个 ❌/⚠️ 问题在 §7 后附 `.java` 问题片段。

---

## 1. 审查范围

| 项 | 值 |
|----|-----|
| `.java` 文件数 | 14 |
| 变更行数 | `+432 / -0`（新增全部文件，无删除） |

| 类/接口 | 路径 | 角色 |
|---------|------|------|
| TestDjApplication | `src/main/java/com/testdj/TestDjApplication.java` | Spring Boot 启动类 |
| WebConfig | `src/main/java/com/testdj/config/WebConfig.java` | CORS 跨域配置 |
| ExportController | `src/main/java/com/testdj/controller/ExportController.java` | 导出 PDF 控制器 |
| HashController | `src/main/java/com/testdj/controller/HashController.java` | SHA-256 哈希控制器 |
| HelloController | `src/main/java/com/testdj/controller/HelloController.java` | Hello World 控制器 |
| SortController | `src/main/java/com/testdj/controller/SortController.java` | 冒泡排序控制器 |
| ExportRequest | `src/main/java/com/testdj/dto/ExportRequest.java` | 导出请求 DTO |
| HashRequest | `src/main/java/com/testdj/dto/HashRequest.java` | 哈希请求 DTO |
| HelloRequest | `src/main/java/com/testdj/dto/HelloRequest.java` | Hello 请求 DTO |
| SortRequest | `src/main/java/com/testdj/dto/SortRequest.java` | 排序请求 DTO |
| ExportService | `src/main/java/com/testdj/service/ExportService.java` | PDF 导出服务 |
| HashService | `src/main/java/com/testdj/service/HashService.java` | SHA-256 哈希计算服务 |
| HelloService | `src/main/java/com/testdj/service/HelloService.java` | 问候语生成服务 |
| SortService | `src/main/java/com/testdj/service/SortService.java` | 冒泡排序算法服务 |

---

## 2. 问题计数

| P0 | P1 | P2 |
|----|----|-----|
| 1 | 2 | 5 |

---

## 3. Step 2 — 功能（REQ）

### REQ-1: Hello World 接口（F01）
> Spec 来源：设计文档 §5.1 W01，实施计划 Task 1

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| `POST /api/hello` 返回问候语 | ✅ | 设计文档 §5.1.2 W01: "接收用户名，返回问候语" | `HelloController.java:19-25` | 返回 `{tab, message, input}`，格式正确 |
| name 为空时默认 "World" | ✅ | 设计文档 §5.1.2: "若 name 为 null 或空白，默认使用 'World'" | `HelloService.java:9-11` | 在 Service 层处理空值默认逻辑 |
| 响应格式含 tab 字段 | ✅ | 设计文档 §5.1.2: 出参含 tab/message/input | `HelloController.java:21-25` | Map.of 构建正确 |

### REQ-2: SHA-256 哈希接口（F02）
> Spec 来源：设计文档 §5.2 W02，实施计划 Task 2

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| `POST /api/hash` 返回哈希值 | ✅ | 设计文档 §5.2.2 W02: "返回 SHA-256 哈希值（十六进制）" | `HashController.java:19-27` | 返回 `{tab, algorithm, input, hash}` |
| 空输入返回空字符串哈希 | ✅ | 设计文档 §5.2.2: "input 为 null 时当作空字符串处理" | `HashController.java:20` | null 转为空串 |
| 输出为小写十六进制 | ✅ | 设计文档 §5.2.2: "输出为小写十六进制字符串" | `HashService.java:16-22` | 使用 `Integer.toHexString(0xff & b)` 小写输出 |

### REQ-3: 冒泡排序接口（F03）
> Spec 来源：设计文档 §5.3 W03，实施计划 Task 3

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| `POST /api/bubble-sort` 返回排序结果 | ✅ | 设计文档 §5.3.2 W03: "返回冒泡排序后的结果" | `SortController.java:20-28` | 返回 `{tab, original, sorted, length}` |
| 升序排列 | ✅ | 设计文档 §5.3.2: "使用冒泡排序算法，升序排列" | `SortService.java:13-21` | 标准冒泡排序实现 |
| 操作时复制原数组 | ✅ | 设计文档 §5.3.2: "操作时复制原数组，不修改原始输入" | `SortService.java:11` | `new ArrayList<>(array)` 复制 |
| **array 为 null 时 NPE** | ⚠️ | 设计文档 §5.3.3: R03 "输入为 null 时抛出 NullPointerException" | `SortController.java:21-22` → `SortService.java:11` | 按设计文档有意为之，但 REST API 应返回 400 而非 500 |

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
| `POST /api/export` 返回 PDF | ✅ | 设计文档 §5.4.2: "生成对应结果的 PDF 文件并返回" | `ExportController.java:20-35` | 返回 `application/pdf` 二进制流 |
| 支持三种 Tab 导出 | ✅ | 设计文档 §5.4.2: "支持 'hello'、'hash'、'sort' 三种 Tab" | `ExportController.java:38-58` | switch 覆盖三种类型 |
| **导出实际页面展示结果** | ❌ **P0** | 原始需求: "支持导出各个页面的展示结果" | `ExportController.java:25` `buildSampleData()` 使用硬编码模拟数据 | **功能缺陷**：`ExportController.buildSampleData()` 返回硬编码模拟数据而非用户实时交互结果。前端仅发送 `tab` 参数，未发送实际结果数据到后端。导出 PDF 内容为预设示例，不包含用户输入和真实计算结果。 |

---

## 4. Step 3 — 可读性检查

| 结果 | 说明（违规写 Ax.x 与 `path:行`） |
|------|--------------------------------|
| ✅ | **A1.1** 所有文件名与顶层类名一致，大小写正确 |
| ✅ | **A1.2** 编码 UTF-8 |
| ✅ | **A1.3** 仅使用 ASCII 空格和换行符 |
| ❌ | **A2.2** 通配符导入：`ExportController.java:9`, `HashController.java:7`, `HelloController.java:7`, `SortController.java:7` — 使用 `import ...*` 违反禁止通配符导入规则 |
| ✅ | **A2.3** 静态/非静态 import 分组正确 |
| ✅ | **A2.4** import 排序大部分正确 |
| ✅ | **A3.1** K&R 大括号风格正确 |
| ✅ | **A3.3** 缩进 4 空格 |
| ❌ | **A3.4** 行宽超限：`ExportService.java:29` — 行宽 120+ 字符 |
| ✅ | **A3.7** 关键字与括号间空格正确 |
| ✅ | **A4.1–A4.7** 命名规范全部符合（包名全小写，类名 UpperCamelCase，方法名 lowerCamelCase） |
| ✅ | **A5.1** `@Override` 使用正确 |
| ⚠️ | **A6.2** `ExportController.java:38` switch 表达式有 `default` 分支，符合规范 |
| ⚠️ | **A7.1** public 类/方法缺少 Javadoc（所有文件均为新增，无 Javadoc 注释）。鉴于方法名自解释且无复杂逻辑，可接受 |

---

## 5. Step 4 — 可靠性检查

| 域 | 参考 | 结果 | 等级 | 说明（列命中 ID 或「已扫无命中」） |
|----|------|------|------|-------------------------------------|
| 可靠性 | `reliability-checklist.md` G1–G17 | ⚠️ | P1 | **G16.2** — `HashService.java:24` 捕获 `NoSuchAlgorithmException` 后仅包装为 `RuntimeException`，未输出日志，异常路径可观测性不足（清单等级 P1） |
| 安全 | `security-checklist.md` S1–S10 | ✅ | N/A | 本变更不涉及 SQL 操作、XSS 输出、URL 请求、命令执行、文件操作、认证授权等场景，无数据库操作 |
| Bug 模式 | `bug-pattern-checklist.md` B/M/I（120） | ⚠️ | P1 | **M016** — `ExportService.java:29` 使用 `LocalDateTime.now()` 未指定时区，依赖系统默认时区，可能导致时区不一致问题（Major→P1）。预扫结果：`scan-all-rules.sh` 已覆盖并检出此条 |

### 预扫扫描结果汇总（`scan-all-rules.sh`）

| 规则 ID | 等级 | 文件 | 行号 | 说明 |
|---------|------|------|------|------|
| G16.2 | P1 (清单) | `HashService.java` | 24 | CatchWithoutLogging — 捕获异常未记录日志 |
| M016 | P1 | `ExportService.java` | 29 | JavaTimeDefaultTimeZone — 未指定时区 |
| A2.2 | P2 | `ExportController.java` | 9 | 通配符导入 |
| A2.2 | P2 | `HashController.java` | 7 | 通配符导入 |
| A2.2 | P2 | `HelloController.java` | 7 | 通配符导入 |
| A2.2 | P2 | `SortController.java` | 7 | 通配符导入 |
| A3.4 | P2 | `ExportService.java` | 29 | 行宽超限 |

### LLM 补充扫描（脚本未覆盖项）

| 规则 ID | 等级 | 文件 | 行号 | 说明 |
|---------|------|------|------|------|
| G5.1 | N/A | — | — | 无资源（文件/网络/DB）需释放 |
| G2.1 | N/A | — | — | 无写接口/消息消费，无需幂等 |
| G1.1–G1.4 | N/A | — | — | 无并发/事务场景 |
| S1.1–S4.2 | N/A | — | — | 无 SQL/命令执行/SSRF/XXE 场景 |
| B012 (NullPointer) | ⚠️ | `SortController.java:21-22` → `SortService.java:11` | 11 | `new ArrayList<>(null)` 当 `request.getArray()` 为 null 时触发 NPE。设计文档虽标注此行为，但 REST API 应返回 400 而非 500 |

---

## 6. Step 5 — 自定义扩展检查

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| 自定义扩展 | `customized-checklist.md` U* | N/A | — | 未启用自定义规则（文件仅为模板，无实际启用的规则条目） |

---

## 7. 结论

- **合并建议**：修复后合并
- **P0**：
  1. **导出功能使用硬编码模拟数据** — ExportController.buildSampleData() 返回预设示例数据，不包含用户实际交互结果，违反需求"支持导出各个页面的展示结果"
- **P1**：
  1. **HashService 异常未记录日志** — G16.2，捕获 NoSuchAlgorithmException 后无日志输出
  2. **ExportService 未指定时区** — M016，LocalDateTime.now() 依赖系统默认时区
- **P2**：
  1. 四个 Controller 使用通配符导入（A2.2）
  2. ExportService 第 29 行超长（A3.4）
- **一句话**：代码结构清晰、接口契约对齐良好，但导出功能存在 **P0 功能性缺陷**（导出数据为硬编码而非实际结果），且异常处理可观测性需增强。

---

## 7.1 问题片段（必填）

### P0 — 导出功能使用硬编码模拟数据

- **P0** `F05` `ExportController.java:25` — 导出功能使用 `buildSampleData()` 返回硬编码模拟数据，未导出用户实际交互结果。需求要求"支持导出各个页面的展示结果"。
  片段范围：`ExportController.java:20-58`

```java
L20|    @PostMapping
L21|    public ResponseEntity<byte[]> export(@RequestBody ExportRequest request) {
L22|        String tab = request.getTab() != null ? request.getTab() : "hello";
L23|
L24|        // Build sample result data based on tab type
L25|        Map<String, Object> resultData = buildSampleData(tab);  // ← 硬编码模拟数据
L26|        byte[] pdfBytes = exportService.exportTabResult(tab, resultData);
L27|
L28|        HttpHeaders headers = new HttpHeaders();
L29|        headers.setContentType(MediaType.APPLICATION_PDF);
L30|        headers.setContentDispositionFormData("attachment", tab + "_result.pdf");
L31|        headers.setContentLength(pdfBytes.length);
L32|
L33|        return ResponseEntity.ok()
L34|                .headers(headers)
L35|                .body(pdfBytes);
L36|    }
L37|
L38|    private Map<String, Object> buildSampleData(String tab) {
L39|        return switch (tab) {
L40|            case "hello" -> Map.of(                     // ← 硬编码示例数据
L41|                    "Tab", "Hello World",
L42|                    "Input", "World",
L43|                    "Message", "Hello, World!"
L44|            );
L45|            case "hash" -> Map.of(
L46|                    "Tab", "SHA-256 Hash",
L47|                    "Input", "hello",                    // ← 硬编码
L48|                    "Algorithm", "SHA-256",
L49|                    "Hash Result", "2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824"
L50|            );
L51|            case "sort" -> Map.of(
L52|                    "Tab", "Bubble Sort",
L53|                    "Original Array", "[3, 1, 4, 1, 5]",  // ← 硬编码
L54|                    "Sorted Array", "[1, 1, 3, 4, 5]",
L55|                    "Length", "5"
L56|            );
L57|            default -> Map.of("Tab", "Unknown", "Info", "No data available");
L58|        };
L59|    }
```

---

### P1 — HashService 异常未记录日志

- **P1** `G16.2` `HashService.java:24` — 捕获 `NoSuchAlgorithmException` 后仅包装为 `RuntimeException` 抛出，未使用日志框架记录异常上下文，排障可观测性不足。
  片段范围：`HashService.java:11-27`

```java
L11|    public String sha256(String input) {
L12|        try {
L13|            MessageDigest digest = MessageDigest.getInstance("SHA-256");
L14|            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
L15|            ...
L23|            return hexString.toString();
L24|        } catch (NoSuchAlgorithmException e) {
L25|            throw new RuntimeException("SHA-256 algorithm not available", e);  // 无日志记录
L26|        }
L27|    }
```

---

### P1 — ExportService 未指定时区

- **P1** `M016` `ExportService.java:29` — 使用 `LocalDateTime.now()` 未指定时区，依赖系统默认时区，可能导致不同部署环境的导出时间不一致。
  片段范围：`ExportService.java:27-30`

```java
L27|        String title = getTabTitle(tab);
L28|        document.add(new Paragraph(title).setFontSize(20).setBold());
L29|        document.add(new Paragraph("Export Time: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
L30|        document.add(new Paragraph(" "));
```

---

### P2 — 通配符导入（4 个文件）

- **P2** `A2.2` `ExportController.java:9` — 通配符导入 `import java.util.Map.*;`（实际应为 `import java.util.Map;`）
  片段范围：`ExportController.java:9-11`

```java
L9| import java.util.Map.*;         // ← 通配符导入
L10|
L11| import java.util.Map;
```

- **P2** `A2.2` `HashController.java:7` — 通配符导入
  片段范围：`HashController.java:7-9`

```java
L7| import org.springframework.web.bind.annotation.*;  // ← 通配符导入
L8|
L9| import java.util.Map;
```

- **P2** `A2.2` `HelloController.java:7` — 通配符导入
  片段范围：`HelloController.java:7-9`

```java
L7| import org.springframework.web.bind.annotation.*;  // ← 通配符导入
L8|
L9| import java.util.Map;
```

- **P2** `A2.2` `SortController.java:7` — 通配符导入
  片段范围：`SortController.java:7-11`

```java
L7| import org.springframework.web.bind.annotation.*;  // ← 通配符导入
L8|
L9| import java.util.List;
L10| import java.util.Map;
```

---

### P2 — 行宽超限

- **P2** `A3.4` `ExportService.java:29` — 行宽超过 120 字符限制
  片段范围：`ExportService.java:28-30`

```java
L28|        document.add(new Paragraph(title).setFontSize(20).setBold());
L29|        document.add(new Paragraph("Export Time: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));  // 超长
L30|        document.add(new Paragraph(" "));
```

---

## 8. 修复任务列表

### P0

- [ ] **P0** `ExportController.java:25` — 重构导出接口，使其接收前端传递的实际结果数据（而非硬编码示例数据），支持导出用户真实交互的页面展示结果。前端需同时改造，将当前 Tab 的请求结果数据随 `tab` 一起发送到后端。

### P1

- [ ] **P1** `HashService.java:24` — 在 catch 块中增加日志记录（如 `log.error("SHA-256 algorithm not available", e)`），提升异常可观测性
- [ ] **P1** `ExportService.java:29` — 将 `LocalDateTime.now()` 改为 `LocalDateTime.now(ZoneId.of("Asia/Shanghai"))` 或 `ZoneId.systemDefault()` 显式声明时区

### P2（可选）

- [ ] **P2** `ExportController.java:9` — 将通配符导入 `import java.util.Map.*;` 替换为具体导入 `import java.util.Map;`
- [ ] **P2** `HashController.java:7` — 替换通配符导入为具体类型导入
- [ ] **P2** `HelloController.java:7` — 替换通配符导入为具体类型导入
- [ ] **P2** `SortController.java:7` — 替换通配符导入为具体类型导入
- [ ] **P2** `ExportService.java:29` — 将超长行拆分为多行，控制在 120 字符以内