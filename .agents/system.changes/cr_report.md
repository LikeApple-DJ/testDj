# Code Review Report

> **Change** `多接口演示与分析系统` · **分支** `AI/task-DEV-f88b571b-71c9-11f1-b645-2557175a1efa-389437b7-4acc-415b-` / `main` · **日期** `2025-08-13` · **审查者** AI
>
> **AI**：等级 **P0 / P1 / P2**；G/S 以 checklist 行内定义为准；Bug 模式以 `bug-pattern-checklist.md` 表头为准（Blocker→P0、Major→P1、Info→P2）。**须先**运行 `scan-all-rules.sh` 并将要点并入 §5，**再**写 LLM 结论。问题须含 `path:line` 或清单 ID。

---

## 1. 审查范围

| 项 | 值 |
|----|-----|
| `.java` 文件数 | `22`（含 4 个测试文件） |
| 变更行数 | `+5057 / -0`（全仓库新增，含前端） |

| 类/接口 | 路径 | 角色 |
|---------|------|------|
| DemoApplication | `src/main/java/com/example/demo/DemoApplication.java` | Spring Boot 启动类 |
| HelloController | `src/main/java/com/example/demo/controller/HelloController.java` | HelloWorld REST 接口 |
| HashController | `src/main/java/com/example/demo/controller/HashController.java` | 哈希算法 REST 接口 |
| BubbleSortController | `src/main/java/com/example/demo/controller/BubbleSortController.java` | 冒泡排序 REST 接口 |
| ExportController | `src/main/java/com/example/demo/controller/ExportController.java` | CSV 导出 REST 接口 |
| StatisticsController | `src/main/java/com/example/demo/controller/StatisticsController.java` | 统计查询 REST 接口 |
| HelloService | `src/main/java/com/example/demo/service/HelloService.java` | HelloWorld 业务逻辑 |
| HashService | `src/main/java/com/example/demo/service/HashService.java` | 哈希算法业务逻辑 |
| BubbleSortService | `src/main/java/com/example/demo/service/BubbleSortService.java` | 冒泡排序业务逻辑 |
| ExportService | `src/main/java/com/example/demo/service/ExportService.java` | CSV 导出业务逻辑 |
| StatisticsService | `src/main/java/com/example/demo/service/StatisticsService.java` | 统计查询业务逻辑 |
| ApiCallLogAspect | `src/main/java/com/example/demo/aspect/ApiCallLogAspect.java` | AOP 埋点切面 |
| ApiCallLog | `src/main/java/com/example/demo/entity/ApiCallLog.java` | JPA 实体 |
| ApiCallLogRepository | `src/main/java/com/example/demo/repository/ApiCallLogRepository.java` | JPA Repository |
| HelloRequest/Response | `src/main/java/com/example/demo/dto/Hello*.java` | DTO |
| HashRequest/Response | `src/main/java/com/example/demo/dto/Hash*.java` | DTO |
| SortRequest/Response | `src/main/java/com/example/demo/dto/Sort*.java` | DTO |
| StatisticsResponse | `src/main/java/com/example/demo/dto/StatisticsResponse.java` | DTO |
| CorsConfig | `src/main/java/com/example/demo/config/CorsConfig.java` | CORS 配置 |
| 测试文件 (4) | `src/test/java/com/example/demo/**/*.java` | 单元测试 |

---

## 2. 问题计数

| P0 | P1 | P2 |
|----|----|-----|
| 2 | 5 | 6 |

---

## 3. Step 2 — 功能（REQ）

> REQ 来源：`.agents/system.changes/design.md` §5（功能模块设计）及 `.agents/specs/dima.md` §3（接口契约）

### REQ-1: HelloWorld 接口（F01, W01）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| POST /api/demo/hello 路径正确 | ✅ | design.md §5.1.2 W01: `POST /api/demo/hello` | `HelloController.java:18` `@PostMapping("/hello")` | 路径匹配 |
| 入参 name 非空校验 | ❌ | design.md R01: "name 不能为空或空白字符串 → 返回 BIZ_001" | `HelloController.java:19` 无校验逻辑；`HelloRequest.java` 无 `@NotNull`/`@NotBlank` | **P0** — name=null 时 HelloService 拼接 "Hello, null!" 不报错但结果错误 |
| 入参 name 长度 ≤100 校验 | ❌ | design.md R02: "name 长度不超过 100 字符 → 返回 BIZ_001" | `HelloController.java:19` 无长度校验 | **P0** — 超长 name 无限制 |
| 出参 message + timestamp | ✅ | design.md W01 出参表 | `HelloResponse.java:6-7` 含 message + timestamp | 字段匹配 |

### REQ-2: 哈希算法接口（F02, W02）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| POST /api/demo/hash 路径正确 | ✅ | design.md §5.1.2 W02 | `HashController.java:18` | 路径匹配 |
| 入参 input 非空校验 | ❌ | design.md R03: "input 不能为空 → 返回 BIZ_002" | `HashController.java:19` 无校验；`HashRequest.java` 无注解 | **P0** — input=null 时 HashService:16 NPE |
| 入参 algorithm 枚举校验 | ❌ | design.md R04: "algorithm 必须为 MD5/SHA-1/SHA-256 之一 → 返回 BIZ_003" | `HashService.java:24-30` normalizeAlgorithm default 分支直接返回原值 | **P0** — 非法算法不会返回 BIZ_003，而是传入 MessageDigest 后可能抛 NoSuchAlgorithmException → 500 |
| 出参 input + algorithm + hash | ✅ | design.md W02 出参表 | `HashResponse.java:4-6` | 字段匹配 |

### REQ-3: 冒泡排序接口（F03, W03）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| POST /api/demo/bubble-sort 路径正确 | ✅ | design.md §5.1.2 W03 | `BubbleSortController.java:18` | 路径匹配 |
| 入参 array 非空校验 | ❌ | design.md R05: "array 不能为空 → 返回 BIZ_004" | `BubbleSortController.java:19` 无校验；`SortRequest.java` 无注解 | **P0** — array=null 时 BubbleSortService:13 NPE |
| 入参 array 长度 ≤10000 校验 | ❌ | design.md R06: "array 长度不超过 10000 → 返回 BIZ_004" | `BubbleSortController.java:19` 无长度校验 | **P0** — 超大数组导致 O(n²) 长时间阻塞 |
| 出参 original + sorted + steps | ✅ | design.md W03 出参表 | `SortResponse.java:6-8` | 字段匹配 |

### REQ-4: 导出接口（F05, W04）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| GET /api/demo/export 路径正确 | ✅ | design.md §5.3.2 W04 | `ExportController.java:19` | 路径匹配 |
| type 参数校验 (hello/hash/bubble-sort) | ❌ | design.md R10: "type 必须为 hello/hash/bubble-sort → 返回 EXPORT_001" | `ExportController.java:21` 无校验，`ExportService.java:32` default 分支静默处理 | **P1** — 非法 type 不报错，返回通用 CSV |
| CSV UTF-8 BOM | ✅ | design.md R12: "CSV 内容使用 UTF-8 编码 + BOM" | `ExportService.java:29` `sb.append('\ufeff')` | BOM 已添加 |
| 导出条数限制 10000 | ✅ | design.md R11: "单次导出最多 10000 条" | `ExportService.java:23-25` | 已实现截断 |

### REQ-5: 统计查询接口（F07, W05）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| GET /api/demo/statistics 路径正确 | ✅ | design.md §5.4.2 W05 | `StatisticsController.java:17` | 路径匹配 |
| dimension 参数校验 | ❌ | design.md R13: "dimension 必须为 userType/userLevel/userDept → 返回 STAT_001" | `StatisticsController.java:18` 无校验；`StatisticsService.java:35-36` 抛 IllegalArgumentException | **P1** — 非法 dimension 返回 500 而非 STAT_001 |
| period 时间过滤 | ✅ | design.md R14-R16 | `StatisticsService.java:51-60` | 7d/30d/all 逻辑正确 |
| NULL 维度值归入"未知" | ✅ | design.md R17 | `StatisticsService.java:42` | `row[0] != null ? ... : "未知"` |

### REQ-6: AOP 埋点（F06）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 切点覆盖三个业务 Controller | ✅ | design.md §5.2.2 切点定义 | `ApiCallLogAspect.java:33-35` | 三个 Controller 均已覆盖 |
| 从 Header 提取用户信息 | ✅ | design.md R07 | `ApiCallLogAspect.java:48-57` | X-User-Id 默认 "anonymous" |
| 埋点失败不影响主流程 | ✅ | design.md R08 | `ApiCallLogAspect.java:79-82` | 外层 catch + log.error |
| 序列化失败字段置 null | ✅ | design.md R09 | `ApiCallLogAspect.java:62-76` | 内层 catch + log.warn |

### REQ-7: 数据模型

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| api_call_log 表结构 | ✅ | design.md §5.2.1.1 | `schema.sql:1-13` | 字段完整匹配 |
| 索引创建 | ✅ | design.md §5.2.1.1 IDX | `schema.sql:15-17` | 三个索引均已创建 |

---

## 4. Step 3 — 可读性检查

> 对照 `readability-checklist.md` A1–A7

| 检查项 | 结果 | 说明 |
|--------|------|------|
| A1 源文件格式（编码/换行） | ✅ | UTF-8 编码，统一换行符 |
| A2 import 规范 | ⚠️ P2 | `A2.2` 通配符 import：`HelloController.java:6`、`HashController.java:6`、`BubbleSortController.java:6`、`ExportController.java:7`、`StatisticsController.java:5`、`ApiCallLog.java:3` 使用 `import xxx.*` |
| A3 大括号/缩进 | ✅ | 统一 4 空格缩进，K&R 风格 |
| A4 命名规范 | ✅ | 类名 PascalCase，方法/变量 camelCase，常量 UPPER_SNAKE |
| A5 注释/JavaDoc | ⚠️ P2 | 公共 API 缺少 JavaDoc 注释；但 MVP 阶段可接受 |
| A6 方法长度/复杂度 | ✅ | 所有方法长度合理，最复杂的 ExportService.exportToCsv 96 行但为重复模式 |
| A7 魔法数字/硬编码 | ⚠️ P2 | `ExportService.java:23` 硬编码 `10000`；`BubbleSortService.java` 无数组长度上限常量 |

---

## 5. Step 4 — 可靠性检查

### 自动化预扫结果（scan-all-rules.sh）

```
=== Summary: 17 findings (P0=4, P1=7, P2=6) | 52/222 rules scanned ===

[P0] G16.2 — CatchWithoutLogging: ApiCallLogAspect.java:67  → 复核：误报，L68 有 log.warn
[P0] G16.2 — CatchWithoutLogging: ApiCallLogAspect.java:74  → 复核：误报，L75 有 log.warn
[P0] G16.2 — CatchWithoutLogging: ApiCallLogAspect.java:79  → 复核：误报，L80 有 log.error
[P0] G16.2 — CatchWithoutLogging: HashService.java:19       → 复核：确认，catch 后 throw 未记录日志
[P1] M016 — JavaTimeDefaultTimeZone: 6 处 LocalDateTime.now() → 确认 P2（MVP 可接受）
[P1] S10.2 — CorsWildcard: CorsConfig.java:15               → 确认 P1
[P2] A2.2 — WildcardImport: 6 处                            → 确认 P2
```

### 可靠性（军规）

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| G1 并发控制 | G1 | N/A | - | 各接口无共享可变状态，独立 INSERT |
| G2 超时/重试/限流 | G2 | ❌ | P1 | 冒泡排序无执行超时控制，10000 长度逆序数组可能阻塞数秒 |
| G3 资源释放 | G3 | ✅ | - | 无手动资源打开，JPA 管理连接 |
| G4 事务边界 | G4 | ✅ | - | 单表 INSERT，Spring 默认事务 |
| G5 幂等 | G5 | N/A | - | 埋点为追加写入，无幂等需求 |
| G6 边界条件 | G6 | ❌ | P0 | 所有 Controller 缺少入参边界校验（null/空/超长） |
| G14 数据库 | G14 | ✅ | - | H2 内存模式，schema.sql 建表 |
| G16 异常处理 | G16 | ❌ | P1 | HashService:19 catch 后 throw 未记录日志；无全局异常处理器 |
| G17 灰度/监控/应急 | G17 | ✅ | - | AOP 埋点可视为监控手段 |

### 安全

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| S1 SQL 注入 | S1 | ✅ | - | 使用 JPA @Query 参数绑定，无拼接 SQL |
| S2 XSS | S2 | N/A | - | 后端返回 JSON，前端负责转义 |
| S3 密钥泄露 | S3 | ✅ | - | 无硬编码密钥 |
| S5 文件上传 | S5 | N/A | - | 无文件上传功能 |
| S7 敏感日志 | S7 | ✅ | - | 日志中未记录敏感信息 |
| S10 CORS | S10 | ❌ | P1 | `CorsConfig.java:15` `addAllowedOriginPattern("*")` + `setAllowCredentials(true)` — 生产环境应限制具体域名 |
| S10.3 H2 Console | S10 | ⚠️ P1 | `application.yml:12` H2 Console 启用无鉴权，结合 CORS wildcard 可被远程访问 |

### Bug 模式（B/M/I）

| 规则 ID | 结果 | 等级 | 说明 |
|---------|------|------|------|
| M016 JavaTimeDefaultTimeZone | ⚠️ | P2 | 6 处 `LocalDateTime.now()` 使用系统默认时区；MVP 可接受，生产建议指定时区 |
| B003 NPE 风险 | ❌ | P1 | `HashService.java:16` input=null 时 `input.getBytes()` NPE；`BubbleSortService.java:13` input=null 时 `new ArrayList<>(null)` NPE |
| B009 异常捕获后抛出 | ⚠️ | P1 | `HashService.java:19-20` catch NoSuchAlgorithmException 后 throw IllegalArgumentException 未记录日志 |

---

## 6. Step 5 — 自定义扩展检查

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| 自定义扩展 | `customized-checklist.md` U* | N/A | - | N/A(未启用自定义规则) |

---

## 7. 结论

- **合并建议**：**修复后合并**
- **P0**：
  1. 所有 Controller 缺少入参校验（`@Valid` + DTO 注解或手动校验），违反 design.md R01-R06/R10/R13 共 8 条业务规则，null 入参导致 NPE，超长/超大入参无防护
  2. 缺少全局异常处理器（`@ControllerAdvice`），`IllegalArgumentException` 等异常返回 HTTP 500 而非设计文档定义的 400 + 错误码
- **P1**：
  1. H2 Console 暴露无鉴权 + CORS wildcard — 安全风险
  2. HashService NPE 风险（input/algorithm 为 null）
  3. ExportService 非法 type 静默处理而非返回 EXPORT_001
  4. StatisticsService 非法 dimension 返回 500 而非 STAT_001
  5. CorsConfig `allowCredentials(true)` + `originPattern("*")` 组合不安全
- **P2**：
  1. 6 处通配符 import
  2. 6 处 `LocalDateTime.now()` 未指定时区
  3. 硬编码魔法数字 10000
- **一句话**：核心业务逻辑实现正确，AOP 埋点设计合理，但**入参校验全面缺失**和**异常处理缺失**导致接口不符合设计文档定义的错误码契约，需修复后方可合并。

---

## 7.1 问题片段（必填）

### P0-1: 缺少入参校验 — HelloController

- **P0** `REQ-1/REQ-2/REQ-3` `src/main/java/com/example/demo/controller/HelloController.java:19` — 无入参校验，name=null/空/超长均不拦截。

片段范围：`HelloController.java:18-21`

```java
L18|    @PostMapping("/hello")
L19|    public HelloResponse hello(@RequestBody HelloRequest request) {
L20|        return helloService.sayHello(request.getName());
L21|    }
// 问题：无 @Valid，无 null/blank/length 校验
// 应增加：if (request.getName() == null || request.getName().isBlank()) throw ...
// 或使用 @NotBlank + @Size(max=100) 注解 + @Valid
```

### P0-1: 缺少入参校验 — BubbleSortController

- **P0** `REQ-3` `src/main/java/com/example/demo/controller/BubbleSortController.java:19` — 无数组长度校验，超大数组导致 O(n²) 阻塞。

片段范围：`BubbleSortController.java:18-21`

```java
L18|    @PostMapping("/bubble-sort")
L19|    public SortResponse sort(@RequestBody SortRequest request) {
L20|        return bubbleSortService.bubbleSort(request.getArray());
L21|    }
// 问题：无 null 检查、无 size > 10000 检查
// design.md R05/R06 要求返回 BIZ_004
```

### P0-2: 缺少全局异常处理器

- **P0** `design.md §5 错误码格式` — 无 `@ControllerAdvice` 类，`IllegalArgumentException` 返回 HTTP 500。

片段范围：全仓库缺失 — 应新增 `GlobalExceptionHandler.java`

```java
// 缺失文件：src/main/java/com/example/demo/config/GlobalExceptionHandler.java
// 需要实现：
// @RestControllerAdvice
// public class GlobalExceptionHandler {
//     @ExceptionHandler(IllegalArgumentException.class)
//     public ResponseEntity<Map<String,String>> handleIllegalArg(IllegalArgumentException e) {
//         return ResponseEntity.badRequest().body(Map.of("code","BIZ_001","msg",e.getMessage()));
//     }
// }
```

### P1-1: H2 Console 暴露 + CORS wildcard

- **P1** `S10/S10.3` `src/main/java/com/example/demo/config/CorsConfig.java:15` + `src/main/resources/application.yml:12`

片段范围：`CorsConfig.java:14-18`

```java
L14|        CorsConfiguration config = new CorsConfiguration();
L15|        config.addAllowedOriginPattern("*");   // 问题：允许任意来源
L16|        config.addAllowedHeader("*");
L17|        config.addAllowedMethod("*");
L18|        config.setAllowCredentials(true);       // 问题：凭据 + wildcard
```

片段范围：`application.yml:11-13`

```yaml
L11|    console:
L12|      enabled: true       # 问题：H2 Console 无鉴权
L13|      path: /h2-console   # 结合 CORS wildcard 可被远程访问
```

### P1-2: HashService NPE 风险

- **P1** `B003` `src/main/java/com/example/demo/service/HashService.java:12-16` — input=null 时 NPE。

片段范围：`HashService.java:12-16`

```java
L12|    public HashResponse computeHash(String input, String algorithm) {
L13|        try {
L14|            String normalizedAlgorithm = normalizeAlgorithm(algorithm); // algorithm=null → NPE
L15|            MessageDigest digest = MessageDigest.getInstance(normalizedAlgorithm);
L16|            byte[] hashBytes = digest.digest(input.getBytes()); // input=null → NPE
// 应在方法入口校验 input/algorithm 非空
```

### P1-3: ExportService 非法 type 静默处理

- **P1** `REQ-4/R10` `src/main/java/com/example/demo/service/ExportService.java:72-84` — 非法 type 走 default 分支而非报错。

片段范围：`ExportService.java:72-84`

```java
L72|            default:
L73|                sb.append("call_time,user_id,...\n"); // 问题：静默处理非法 type
L74|                for (ApiCallLog log : logs) {
// design.md R10 要求返回 EXPORT_001 错误码
// 应改为：throw new IllegalArgumentException("Unsupported type: " + type);
```

### P1-4: StatisticsService 非法 dimension 返回 500

- **P1** `REQ-5/R13` `src/main/java/com/example/demo/service/StatisticsService.java:35-36` — 抛 IllegalArgumentException 无全局处理器。

片段范围：`StatisticsService.java:35-37`

```java
L35|            default:
L36|                throw new IllegalArgumentException("Unsupported dimension: " + dimension);
L37|        }
// 问题：无 @ControllerAdvice 时返回 HTTP 500
// design.md R13 要求返回 STAT_001
```

---

## 8. 修复任务列表

### P0

- [ ] **P0** `HelloController.java:19` — 增加 name 入参校验（非空、非空白、长度 ≤100），违反时返回 BIZ_001
- [ ] **P0** `HashController.java:19` — 增加 input 非空校验（BIZ_002）和 algorithm 枚举校验（BIZ_003）
- [ ] **P0** `BubbleSortController.java:19` — 增加 array 非空校验和长度 ≤10000 校验（BIZ_004）
- [ ] **P0** 新增 `GlobalExceptionHandler.java`（`@RestControllerAdvice`） — 统一处理 `IllegalArgumentException` → HTTP 400 + 错误码（BIZ_xxx / EXPORT_xxx / STAT_xxx）
- [ ] **P0** `HelloRequest.java` / `HashRequest.java` / `SortRequest.java` — 添加 Jakarta Validation 注解（`@NotBlank`、`@Size`、`@NotNull`）并在 Controller 方法参数加 `@Valid`

### P1

- [ ] **P1** `CorsConfig.java:15` — 将 `addAllowedOriginPattern("*")` 改为配置化的具体域名白名单；或 MVP 阶段通过 `@Value` 注入
- [ ] **P1** `application.yml:12` — H2 Console 增加 `spring.h2.console.settings.web-allow-remote: false` 或通过 Spring Profile 仅在 dev 环境启用
- [ ] **P1** `HashService.java:12` — 方法入口增加 `if (input == null || input.isEmpty())` 和 `if (algorithm == null)` 校验，抛出带错误码的异常
- [ ] **P1** `ExportService.java:72` — default 分支改为抛出 `IllegalArgumentException("Unsupported export type: " + type)`
- [ ] **P1** `StatisticsService.java:35` — 确保 IllegalArgumentException 被全局异常处理器捕获并返回 STAT_001

### P2

- [ ] **P2** `HelloController.java:6` 等 6 处 — 将通配符 import `xxx.*` 改为显式 import
- [ ] **P2** `ApiCallLogAspect.java:59` 等 6 处 — `LocalDateTime.now()` 考虑指定时区 `LocalDateTime.now(ZoneId.of("Asia/Shanghai"))`
- [ ] **P2** `ExportService.java:23` — 将魔法数字 `10000` 提取为常量 `MAX_EXPORT_SIZE`
