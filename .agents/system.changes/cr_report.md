# Code Review Report

> **Change** Demo Tools（前后端协同：HelloWorld / 哈希 / 冒泡排序 / 导出 / 埋点报表） · **分支/Commit** `AI/task-DEV-966dcd0a-7905-11f1-9649-3b4281182f10-0d66a3a1-0730-4823-a9f4-c44ea5442b7b` · **日期** 2026-08-25 · **审查者** AI

---

## 1. 审查范围

| 项 | 值 |
|----|-----|
| 仓库 | testDj-main（Java 后端）、testDJnew-main（React 前端） |
| `.java` 文件数 | 16（main: 12 / test: 4） |
| 变更行数 | 新增约 1300+ 行 |

| 类/接口 | 路径 | 角色 |
|---------|------|------|
| `HelloController` | `[testDj] src/main/java/com/testdj/demo/hello/HelloController.java` | HelloWorld 接口 |
| `HashController` / `HashService` | `[testDj] src/main/java/com/testdj/demo/hash/*.java` | 哈希算法接口 |
| `BubbleSortController` / `BubbleSortService` | `[testDj] src/main/java/com/testdj/demo/sort/*.java` | 冒泡排序接口 |
| `ExportController` / `ExportService` | `[testDj] src/main/java/com/testdj/demo/export/*.java` | 导出接口 |
| `MetricsController` / `MetricService` / `MetricsInterceptor` / `MetricRepository` / `MetricEvent` | `[testDj] src/main/java/com/testdj/demo/metrics/*.java` | 埋点与报表 |
| `ApiResponse` / `BusinessException` / `GlobalExceptionHandler` | `[testDj] src/main/java/com/testdj/demo/{common,exception}/*.java` | 统一响应与异常 |
| `WebConfig` | `[testDj] src/main/java/com/testdj/demo/config/WebConfig.java` | 拦截器、CORS |
| `client.ts` / `useMetrics.ts` / `DemoPage.tsx` / `ReportPanel.tsx` | `[testDJnew] src/api/client.ts`、`src/hooks/useMetrics.ts`、`src/components/*.tsx` | 前端 API、页面、报表 |

---

## 2. 问题计数

| P0 | P1 | P2 |
|----|----|-----|
| 5  | 3  | 10  |

---

## 3. Step 2 — 功能（REQ）

### REQ-1：HelloWorld 接口

| Scenario | 结果 | Spec 证据 | 代码证据 | 说明 |
|----------|------|-----------|----------|------|
| `GET /api/v1/demo/hello` 返回固定问候语 | ✅ | design.md §5.2.2 | `[testDj] HelloController.java:12-15` | 实现与契约一致 |

### REQ-2：哈希算法接口

| Scenario | 结果 | Spec 证据 | 代码证据 | 说明 |
|----------|------|-----------|----------|------|
| 默认 SHA-256，content 空返回错误码 | ️ | design.md §5.3.3 要求 `HASH_001`/`HASH_002` | `[testDj] HashService.java:17-18` | 校验存在，但返回通用 400，未使用业务错误码 |

### REQ-3：冒泡排序接口

| Scenario | 结果 | Spec 证据 | 代码证据 | 说明 |
|----------|------|-----------|----------|------|
| numbers 为空返回 `SORT_001` | ⚠️ | design.md §5.4.2 | `[testDj] BubbleSortService.java:14-16` | 校验存在，返回通用 400，未使用 `SORT_001` |
| 升序/降序/去重 | ✅ | design.md §5.4.2 | `[testDj] BubbleSortService.java:20-32` | 实现正确 |

### REQ-4：导出接口

| Scenario | 结果 | Spec 证据 | 代码证据 | 说明 |
|----------|------|-----------|----------|------|
| 根据当前 Tab 导出真实结果 | ❌ | design.md §5.5.2 “根据当前 Tab 导出 CSV 或 Excel 文件” | `[testDj] ExportService.java:27-42` | `buildRows` 写死 mock 数据，未导出真实调用结果 |
| 支持 csv/excel | ✅ | design.md §5.5.2 | `[testDj] ExportService.java:18-25` | 格式支持正确 |

### REQ-5：埋点 / 报表接口

| Scenario | 结果 | Spec 证据 | 代码证据 | 说明 |
|----------|------|-----------|----------|------|
| 后端按人员类型/层级/部门聚合 | ✅ | design.md §5.6.3 | `[testDj] MetricRepository.java:12-19` | JPA 查询实现正确 |
| 前端维度参数与后端枚举对齐 | ❌ | design.md §8.1 维度 `userType`/`userLevel`/`userDept` | `[testDJnew] useMetrics.ts:18`、`[testDj] Dimension.java:4-6`、`MetricsController.java:24` | 前端传 `dimension.toUpperCase()` 得到 `USERTYPE` 等，后端 Spring 枚举名 `USER_TYPE`，默认无法转换 |
| MetricEvent 表结构含 gmt_create/gmt_modified | ❌ | design.md §5.6.1 | `[testDj] MetricEvent.java:11-55` | 缺少 `gmt_create`、`gmt_modified` 字段 |
| 埋点不阻塞业务接口 | ⚠️ | design.md §5.6.4 “埋点写入失败不阻塞业务接口” | `[testDj] MetricsInterceptor.java:32` | 同步写库，DB 异常会直接阻塞业务请求 |

---

## 4. Step 3 — 可读性检查

> 参考 `dtazziboot-java-code-review/references/readability-checklist.md` A1–A7。

| 结果 | 说明 |
|------|------|
| ❌ A2.2 | `[testDj] ExportControllerTest.java:12`、`HashServiceTest.java:6`、`HelloControllerTest.java:10`、`MetricsControllerTest.java:15`、`BubbleSortServiceTest.java:8` 存在通配符导入 |
| ❌ A3.4 | `[testDj] MetricEvent.java:28`、`MetricRepository.java:12,15,18`、`MetricsInterceptor.java:23` 行宽超过 120 字符 |
| ✅ 其他 | 命名、缩进、空格等基本合规 |

---

## 5. Step 4 — 可靠性检查

### 5.1 可靠性（reliability-checklist.md G1–G17）

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| 异常日志 | G16.2 CatchWithoutLogging | ❌ | P0 | `[testDj] HashService.java:27`、`ExportService.java:65` 捕获异常后直接转 BusinessException，未记录原始异常日志 |
| 埋点阻塞 | G5 可靠性 / 设计 §5.6.4 | ⚠️ | P1 | `[testDj] MetricsInterceptor.java:32` 同步写库可能阻塞业务 |
| 时间范围校验 | G 边界条件 | ️ | P2 | `[testDj] MetricsController.java:23-27` 未校验 `startDate <= endDate` |

### 5.2 安全（security-checklist.md S1–S10）

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| 异常信息泄露 | S8/S10 信息泄露 | ⚠️ | P1 | `[testDj] GlobalExceptionHandler.java:16` 将 `e.getMessage()` 直接返回客户端 |
| CORS 配置 | S10.2 CorsWildcard | ✅ | — | 仅允许 `http://localhost:5173`，未使用 `*` |
| SQL 注入 | S1.1 | ✅ | — | 使用 JPA 参数化查询 |
| 弱哈希 | S9.3 WeakCryptoAlgorithm | ⚠️ | P1 | `[testDj] HashService.java:14,20` 允许调用方传入 `MD5` 等弱算法（需求默认支持，但无安全提示） |

### 5.3 Bug 模式（bug-pattern-checklist.md B/M/I）

> 预扫命令：`bash .../scan-all-rules.sh <testDj-main/src/main/java> <testDj-main/src/test/java> <testDj-main/pom.xml>`

| 结果 | 等级 | 规则 ID | 定位 | 说明 |
|------|------|---------|------|------|
| ❌ | P0 | G16.2 | `[testDj] HashService.java:27` | catch 未记录日志 |
| ❌ | P0 | G16.2 | `[testDj] ExportService.java:65` | catch 未记录日志 |
| ❌ | P2 | A2.2 | `[testDj] *Test.java` | 通配符导入 |
| ❌ | P2 | A3.4 | `[testDj] MetricEvent.java:28` 等 | 行宽超限 |

---

## 6. Step 5 — 自定义扩展检查

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| 自定义扩展 | `customized-checklist.md` | N/A | — | 未启用自定义规则 |

---

## 7. 结论

- **合并建议**：阻止合并（须先修复 P0）
- **P0**：
  1. 跨仓报表维度参数不匹配，导致 `/metrics/report` 调用失败（前端 `USERTYPE`，后端 `USER_TYPE`）。
  2. 导出接口未导出真实结果，仅返回 mock 数据。
  3. 业务错误码体系未落地（HASH_xxx、SORT_xxx、EXPORT_xxx、METRICS_xxx 均缺失）。
  4. `HashService.java:27` 与 `ExportService.java:65` 捕获异常后未记录日志（scan P0）。
- **P1/P2**：埋点同步阻塞、异常信息泄露、MetricEvent 字段缺失、前端类型重复与 `any` 断言等。
- **一句话**：当前实现完成了基础接口骨架，但存在 5 个 P0 级别问题，其中跨仓契约不一致和导出 mock 数据会导致功能不可用，必须修复后方可合并。

---

## 7.1 问题片段

### P0 — 跨仓报表维度参数不匹配

**`[testDJnew] src/hooks/useMetrics.ts:18`** — 将 `userType` 转为 `USERTYPE`，与后端枚举 `USER_TYPE` 不一致。

```typescript
L16|    const start = new Date();
L17|    start.setDate(start.getDate() - 7);
L18|    fetchReport(dimension.toUpperCase(), start.toISOString(), end.toISOString())
L19|      .then(setData)
L20|      .catch(() => setData([]));
```

**`[testDj] src/main/java/com/testdj/demo/metrics/Dimension.java:4-6`** — 后端枚举名。

```java
L4|public enum Dimension {
L5|    USER_TYPE,
L6|    USER_LEVEL,
L7|    USER_DEPT
L8|}
```

---

### P0 — 导出接口返回 mock 数据

**`[testDj] src/main/java/com/testdj/demo/export/ExportService.java:27-42`** — 写死示例数据，未接入真实业务结果。

```java
L27|    private List<String[]> buildRows(String tab) {
L28|        return switch (tab) {
L29|            case "hello" -> List.of(new String[]{"Hello, World!"});
L30|            case "hash" -> List.of(
L31|                    new String[]{"algorithm", "original", "hash"},
L32|                    new String[]{"SHA-256", "demo", "hashValue"});
L33|            case "bubble" -> List.of(
L34|                    new String[]{"input", "output"},
L35|                    new String[]{"[3,1,4]", "[1,3,4]"});
L36|            case "all" -> List.of(
L37|                    new String[]{"tab", "result"},
L38|                    new String[]{"hello", "Hello, World!"},
L39|                    new String[]{"hash", "hashValue"},
L40|                    new String[]{"bubble", "[1,3,4]"});
L41|            default -> throw new BusinessException(400, "unknown tab: " + tab);
L42|        };
L43|    }
```

---

### P0 — 业务错误码缺失

**`[testDj] src/main/java/com/testdj/demo/hash/HashService.java:17-18,28`** — 使用通用 400 状态码，未使用 `HASH_001`/`HASH_002`。

```java
L16|        if (content == null || content.isEmpty()) {
L17|            throw new BusinessException(400, "content must not be empty");
L18|        }
L28|            throw new BusinessException(400, "unsupported algorithm: " + algorithm);
```

---

### P0 — catch 未记录日志（scan G16.2）

**`[testDj] src/main/java/com/testdj/demo/hash/HashService.java:19-29`**

```java
L19|        try {
L20|            MessageDigest digest = MessageDigest.getInstance(algorithm);
L21|            byte[] hash = digest.digest(content.getBytes(StandardCharsets.UTF_8));
L22|            StringBuilder hex = new StringBuilder();
L23|            for (byte b : hash) {
L24|                hex.append(String.format("%02x", b));
L25|            }
L26|            return new HashResponse(algorithm, content, hex.toString());
L27|        } catch (NoSuchAlgorithmException e) {
L28|            throw new BusinessException(400, "unsupported algorithm: " + algorithm);
L29|        }
```

**`[testDj] src/main/java/com/testdj/demo/export/ExportService.java:53-68`**

```java
L53|    private byte[] toExcel(List<String[]> rows) {
L54|        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
L55|            Sheet sheet = workbook.createSheet("export");
L56|            int rowIdx = 0;
L57|            for (String[] row : rows) {
L58|                Row excelRow = sheet.createRow(rowIdx++);
L59|                for (int i = 0; i < row.length; i++) {
L60|                    excelRow.createCell(i).setCellValue(row[i]);
L61|                }
L62|            }
L63|            workbook.write(out);
L64|            return out.toByteArray();
L65|        } catch (IOException e) {
L66|            throw new BusinessException(500, "failed to generate excel");
L67|        }
L68|    }
```

---

### P1 — 埋点同步阻塞业务

**`[testDj] src/main/java/com/testdj/demo/metrics/MetricsInterceptor.java:20-35`**

```java
L20|    @Override
L21|    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
L22|        String uri = request.getRequestURI();
L23|        if (uri.startsWith("/api/v1/demo/hello") || uri.startsWith("/api/v1/demo/hash") || uri.startsWith("/api/v1/demo/sort/bubble")) {
L24|            MetricEvent event = new MetricEvent(...);
L25|            metricService.track(event);
L26|        }
L27|        return true;
L28|    }
```

---

### P1 — 异常信息泄露

**`[testDj] src/main/java/com/testdj/demo/exception/GlobalExceptionHandler.java:15-18`**

```java
L15|    @ExceptionHandler(Exception.class)
L16|    public ApiResponse<Void> handleException(Exception e) {
L17|        return ApiResponse.error(500, e.getMessage());
L18|    }
```

---

## 8. 修复任务列表

### P0

- [ ] **P0** `[testDJnew] src/hooks/useMetrics.ts:18` — 将维度参数从 `dimension.toUpperCase()` 改为与后端枚举 `USER_TYPE/USER_LEVEL/USER_DEPT` 对齐，或在后端增加自定义 `Converter` 兼容前端参数。
- [ ] **P0** `[testDj] src/main/java/com/testdj/demo/export/ExportService.java:27-42` — 接入真实业务数据，按 Tab 导出 HelloWorld 结果、哈希结果、排序结果或全部汇总。
- [ ] **P0** `[testDj] src/main/java/com/testdj/demo/hash/HashService.java:17-28`、`BubbleSortService.java:14-16`、`ExportService.java:18-42`、`MetricsController.java:23-27` — 统一定义并实现业务错误码（HASH_001/002、SORT_001、EXPORT_001/002/003、METRICS_001/002）。
- [ ] **P0** `[testDj] src/main/java/com/testdj/demo/hash/HashService.java:19-29` — catch `NoSuchAlgorithmException` 时记录异常日志再抛业务异常。
- [ ] **P0** `[testDj] src/main/java/com/testdj/demo/export/ExportService.java:53-68` — catch `IOException` 时记录异常日志再抛业务异常。

### P1

- [ ] **P1** `[testDj] src/main/java/com/testdj/demo/metrics/MetricsInterceptor.java:25` — 将 `metricService.track(event)` 改为异步执行，避免阻塞业务接口。
- [ ] **P1** `[testDj] src/main/java/com/testdj/demo/exception/GlobalExceptionHandler.java:16-17` — 统一返回模糊错误提示，内部异常详情仅记录服务端日志。
- [ ] **P1** `[testDj] src/main/java/com/testdj/demo/metrics/MetricEvent.java:11-55` — 按设计补充 `gmtCreate`、`gmtModified` 字段及相应 getter/setter。

### P2

- [ ] **P2** `[testDj] src/test/java/com/testdj/demo/*Test.java` — 移除通配符导入，使用单类型导入。
- [ ] **P2** `[testDj] src/main/java/com/testdj/demo/metrics/*` — 拆分超长 JPQL 与构造行，控制行宽 ≤120 字符。
- [ ] **P2** `[testDJnew] src/hooks/useMetrics.ts:4-9`、`src/types/index.ts:18-25` — 移除重复的 `ReportItem`/`Dimension` 类型定义，统一复用 `types/index.ts`。
- [ ] **P2** `[testDJnew] src/components/ReportPanel.tsx:47` — 将 `chartType` 的 `as any` 改为受约束的类型转换或联合类型字面量。
