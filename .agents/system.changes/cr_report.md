# Code Review Report

> **Change** Demo Tools 后端（HelloWorld / 哈希 / 冒泡排序 / 导出 / 埋点报表） · **分支/Commit** `AI/task-DEV-966dcd0a-7905-11f1-9649-3b4281182f10-0d66a3a1-0730-4823-a9f4-c44ea5442b7b` · **日期** 2026-08-25 · **审查者** AI

---

## 1. 审查范围

| 项 | 值 |
|----|-----|
| 仓库 | testDj-main（Java 后端） |
| `.java` 文件数 | 18（main: 13 / test: 5） |
| 变更阶段 | 问题修复（stage: test） |

| 类/接口 | 路径 | 角色 |
|---------|------|------|
| `HelloController` | `src/main/java/com/testdj/demo/hello/HelloController.java` | HelloWorld 接口 |
| `HashController` / `HashService` | `src/main/java/com/testdj/demo/hash/*.java` | 哈希算法接口 |
| `BubbleSortController` / `BubbleSortService` | `src/main/java/com/testdj/demo/sort/*.java` | 冒泡排序接口 |
| `ExportController` / `ExportService` / `ExportRequest` | `src/main/java/com/testdj/demo/export/*.java` | 导出接口 |
| `MetricsController` / `MetricService` / `MetricsInterceptor` / `MetricRepository` / `MetricEvent` / `Dimension` / `ReportItem` | `src/main/java/com/testdj/demo/metrics/*.java` | 埋点与报表 |
| `ApiResponse` / `ErrorCode` / `BusinessException` / `GlobalExceptionHandler` | `src/main/java/com/testdj/demo/{common,exception}/*.java` | 统一响应、错误码、异常处理 |
| `AsyncConfig` / `WebConfig` | `src/main/java/com/testdj/demo/config/*.java` | 异步线程池、拦截器、CORS |
| `*Test` | `src/test/java/com/testdj/demo/**/*.java` | 单元/接口测试 |

---

## 2. 问题计数

| P0 | P1 | P2 |
|----|----|-----|
| 1  | 2  | 4   |

---

## 3. Step 2 — 功能（REQ）

### REQ-1：HelloWorld 接口

| Scenario | 结果 | Spec 证据 | 代码证据 | 说明 |
|----------|------|-----------|----------|------|
| `GET /api/v1/demo/hello` 返回固定问候语 | ✅ | 需求：分别写三个接口 helloworld | `HelloController.java:12-15` | 实现与契约一致 |

### REQ-2：哈希算法接口

| Scenario | 结果 | Spec 证据 | 代码证据 | 说明 |
|----------|------|-----------|----------|------|
| 默认 SHA-256，content 空返回业务错误码 | ✅ | 需求：哈希算法接口 | `HashService.java:19-23` | 已使用 `HASH_CONTENT_EMPTY` 业务错误码 |
| 不支持算法返回业务错误码 | ✅ | 需求：哈希算法接口 | `HashService.java:32-35` | 已使用 `HASH_UNSUPPORTED_ALGORITHM` 业务错误码 |

### REQ-3：冒泡排序接口

| Scenario | 结果 | Spec 证据 | 代码证据 | 说明 |
|----------|------|-----------|----------|------|
| numbers 为空返回 `SORT_001` | ✅ | 需求：冒泡排序接口 | `BubbleSortService.java:15-17` | 已使用业务错误码 |
| 升序/降序/去重 | ✅ | 需求：冒泡排序接口 | `BubbleSortService.java:21-34` | 实现正确 |

### REQ-4：导出接口

| Scenario | 结果 | Spec 证据 | 代码证据 | 说明 |
|----------|------|-----------|----------|------|
| 根据当前 Tab 导出真实结果 | ✅ | 需求：新增导出按钮，后台提供导出接口 | `ExportService.java:49-93` | 已接入真实 Hello/Hash/Bubble 结果 |
| 支持 csv/excel | ⚠️ | 需求：支持导出各个页面的展示结果 | `ExportService.java:41-46` | 格式支持正确，但 `format` 未校验空值 |
| 导出请求参数校验 | ❌ | 需求：导出接口 | `ExportController.java:24`、`ExportService.java:39-51` | `format`、`tab` 未校验空值，直接调用会 NPE |

### REQ-5：埋点 / 报表接口

| Scenario | 结果 | Spec 证据 | 代码证据 | 说明 |
|----------|------|-----------|----------|------|
| 后端按人员类型/层级/部门聚合 | ✅ | 需求：后端再做个埋点...折线图以及饼图和柱状图 | `MetricRepository.java:12-28` | JPA 查询实现正确 |
| 埋点不阻塞业务接口 | ✅ | 需求：埋点写入不阻塞业务 | `MetricService.java:18-21`、`AsyncConfig.java:14-23` | 已使用 `@Async("metricsExecutor")` + 有界线程池 |
| 时间范围校验 | ✅ | 需求：报表查看调用情况 | `MetricsController.java:29-32` | 已校验 `startDate <= endDate` |

---

## 4. Step 3 — 可读性检查

> 参考 `dtazziboot-java-code-review/references/readability-checklist.md` A1–A7。

| 结果 | 说明 |
|------|------|
| ⚠️ A2.2 | `src/test/java/com/testdj/demo/*Test.java` 中存在 `import static ...*` 通配符静态导入 |
| ⚠️ A3.4 | `MetricRepository.java:12,18,24`、`MetricsInterceptor.java:23` 部分 JPQL/构造行超过 120 字符 |
| ✅ 其他 | 命名、缩进、空格、结构等基本合规 |

---

## 5. Step 4 — 可靠性检查

### 5.1 自动化预扫结果

> 预扫命令：
> ```bash
> bash /root/.agentix/skills/managed/dtazziboot-java-code-review/references/script/scan-all-rules.sh \
>   /root/.agentix/agentic-dev/runs/DEV-966dcd0a-7905-11f1-9649-3b4281182f10-0d66a3a1-0730-4823-a9f4-c44ea5442b7b/worktree/testDj-main/src/main/java/com/testdj/demo \
>   /root/.agentix/agentic-dev/runs/DEV-966dcd0a-7905-11f1-9649-3b4281182f10-0d66a3a1-0730-4823-a9f4-c44ea5442b7b/worktree/testDj-main/src/test/java/com/testdj/demo
> > ```

| 结果 | 等级 | 规则 ID | 定位 | 说明 |
|------|------|---------|------|------|
| ⚠️ | P0 | G16.2 | `ExportService.java:116` | 脚本命中，但经人工复核：catch 块内下一行已记录 `LOGGER.error`，属于误报 |
| ⚠️ | P0 | G16.2 | `HashService.java:32` | 脚本命中，但经人工复核：catch 块内下一行已记录 `LOGGER.error`，属于误报 |

### 5.2 可靠性（reliability-checklist.md G1–G17）

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| 异常日志 | G16.2 | ✅ | — | `HashService.java:33`、`ExportService.java:117` 已记录异常日志；脚本误报已复核 |
| 异步埋点 | G5/G8 | ✅ | — | `MetricService.track` 已加 `@Async("metricsExecutor")`，线程池有界 |
| 线程池配置 | G8.6 | ✅ | — | `AsyncConfig` 使用 `ThreadPoolTaskExecutor` 并显式设置 core/max/queue |
| 输入空值防御 | G11.3 | ❌ | P0 | `ExportController` / `ExportService` 对 `format`、`tab` 未做非空校验 |
| 资源释放 | G8.3 | ✅ | — | `ExportService.toExcel` 使用 try-with-resources 关闭 Workbook 和 ByteArrayOutputStream |

### 5.3 安全（security-checklist.md S1–S10）

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| SQL 注入 | S1.1 | ✅ | — | JPA 参数化查询，无 `${}` 拼接 |
| 弱哈希算法 | S9.3 | ⚠️ | P1 | `HashService.java:19` 允许调用方指定任意算法（如 `MD5`），应增加算法白名单或默认仅允许 SHA-256/SHA-384/SHA-512 |
| CORS | S10.2 | ✅ | — | `WebConfig.java:26` 仅允许 `http://localhost:5173`，未使用通配符 |
| 命令执行 | S4.1 | ✅ | — | 无外部命令拼接 |
| 异常信息泄露 | S9.2/S8 | ✅ | — | `GlobalExceptionHandler` 已改为返回通用错误码，不泄露内部异常消息 |

### 5.4 Bug 模式（bug-pattern-checklist.md B/M/I）

| 结果 | 等级 | 规则 ID | 定位 | 说明 |
|------|------|---------|------|------|
| ✅ | — | — | — | 未命中 Blocker/Major Bug 模式 |
| ⚠️ | P2 | M007 | `GlobalExceptionHandler.java:15-18` | `handleBusiness` 未记录业务异常日志，问题排查可观测性不足 |

---

## 6. Step 5 — 自定义扩展检查

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| 自定义扩展 | `customized-checklist.md` U* | N/A | — | 未启用自定义规则 |

---

## 7. 结论

- **合并建议**：修复后合并
- **P0**：
  1. 导出接口对 `format`、`tab` 未做空值校验，缺失必填字段时直接抛出 `NullPointerException`，导致导出功能不可用。
- **P1/P2**：
  1. `HashService` 未限制哈希算法白名单，存在使用 MD5 等弱哈希算法的安全隐患。
  2. `ExportService.toCsv` 未对字段中的逗号、引号进行转义，可能导出格式错误的 CSV。
  3. `GlobalExceptionHandler.handleBusiness` 未记录业务异常日志，可观测性不足。
  4. 测试类存在通配符静态导入；`MetricRepository` 部分 JPQL 行宽超过 120 字符。
- **一句话**：问题修复阶段已较好解决上一轮 P0 问题（业务错误码、真实导出、异常日志、异步埋点），但导出接口的入参空值防御缺失会导致 NPE，需修复后方可合并。

---

## 7.1 问题片段

### P0 — `ExportController` 未校验 `format` 空值

**定位**：`src/main/java/com/testdj/demo/export/ExportController.java:22-26`

```java
L21|    @PostMapping("/export")
L22|    public void export(@RequestBody ExportRequest request, HttpServletResponse response) throws Exception {
L23|        byte[] data = exportService.export(request);
L24|        String extension = request.format().equalsIgnoreCase("excel") ? "xlsx" : "csv";  // NPE if format == null
L25|        response.setContentType("application/octet-stream");
L26|        response.setHeader("Content-Disposition", "attachment; filename=\"demo-export.\"" + extension);
L27|    }
```

### P0 — `ExportService` 未校验 `format` / `tab` 空值

**定位**：`src/main/java/com/testdj/demo/export/ExportService.java:38-51`

```java
L38|    public byte[] export(ExportRequest request) {
L39|        String format = request.format();
L40|        List<String[]> rows = buildRows(request);
L41|        return switch (format.toLowerCase()) {          // NPE if format == null
L42|            case "csv" -> toCsv(rows);
L43|            case "excel" -> toExcel(rows);
L44|            default -> throw new BusinessException(ErrorCode.EXPORT_UNSUPPORTED_FORMAT,
L45|                    ErrorCode.EXPORT_UNSUPPORTED_FORMAT_MSG + ": " + format);
L46|        };
L47|    }
L48|
L49|    private List<String[]> buildRows(ExportRequest request) {
L50|        String tab = request.tab();
L51|        return switch (tab) {                            // NPE if tab == null
```

### P1 — 弱哈希算法未加白名单

**定位**：`src/main/java/com/testdj/demo/hash/HashService.java:18-20`

```java
L18|    public HashResponse hash(HashRequest request) {
L19|        String algorithm = request.algorithm() == null ? "SHA-256" : request.algorithm();
L20|        String content = request.content();
L21|        // 未校验 algorithm 是否为允许的算法，调用方可传入 MD5/SHA-1 等弱算法
```

### P1 — CSV 未转义

**定位**：`src/main/java/com/testdj/demo/export/ExportService.java:95-100`

```java
L95|    private byte[] toCsv(List<String[]> rows) {
L96|        StringBuilder sb = new StringBuilder();
L97|        for (String[] row : rows) {
L98|            sb.append(String.join(",", row)).append("\n");
L99|        }
L100|        return sb.toString().getBytes(StandardCharsets.UTF_8);
L101|    }
```

### P2 — 业务异常未记录日志

**定位**：`src/main/java/com/testdj/demo/exception/GlobalExceptionHandler.java:15-18`

```java
L15|    @ExceptionHandler(BusinessException.class)
L16|    public ApiResponse<Void> handleBusiness(BusinessException e) {
L17|        return ApiResponse.error(e.getCode(), e.getMessage());
L18|    }
```

---

## 8. 修复任务列表

### P0

- [ ] **P0** `src/main/java/com/testdj/demo/export/ExportController.java:24`、`ExportService.java:39-51` — 在 `ExportController` 或 `ExportService` 中对 `format`、`tab` 进行非空校验，缺失时返回 `EXPORT_UNKNOWN_TAB` / `EXPORT_UNSUPPORTED_FORMAT` 等业务错误码，避免 `NullPointerException`。

### P1

- [ ] **P1** `src/main/java/com/testdj/demo/hash/HashService.java:19` — 增加哈希算法白名单校验，仅允许 `SHA-256`、`SHA-384`、`SHA-512` 等安全算法，非法算法返回 `HASH_UNSUPPORTED_ALGORITHM`。
- [ ] **P1** `src/main/java/com/testdj/demo/export/ExportService.java:97-98` — 对 CSV 字段中的逗号、双引号、换行符进行转义或包裹处理，确保导出文件格式正确。

### P2

- [ ] **P2** `src/main/java/com/testdj/demo/exception/GlobalExceptionHandler.java:16-18` — 在 `handleBusiness` 中记录业务异常日志，便于问题排查。
- [ ] **P2** `src/test/java/com/testdj/demo/*Test.java` — 移除 `import static ...*` 通配符静态导入，改为单类型导入。
- [ ] **P2** `src/main/java/com/testdj/demo/metrics/MetricRepository.java:12-27` — 拆分超长 JPQL 行，控制行宽 ≤ 120 字符。
