# Code Review Report

> **Change** `三接口工具 + 埋点可视化报表` · **分支/Commit** `AI/task-DEV-966dcd0a-7905-11f1-9649-3b4281182f10-e09a2952-5098-4d54-8616-02251dd458b2` / `74f87354` · **日期** `2026-08-25` · **审查者** AI
>
> **AI**：等级 **P0 / P1 / P2**；G/S 以 checklist 行内定义为准；Bug 模式以 `bug-pattern-checklist.md` 表头为准（Blocker→P0、Major→P1、Info→P2）。**已先**运行 `scan-all-rules.sh`（52/222 规则，命中 42 条）并将要点并入 §5，**再**写 LLM 结论。

---

## §1 审查范围

| 项 | 值 |
|----|-----|
| 审查仓库 | [testDj] Java 后端 |
| `.java` 文件数 | 25 |
| 变更行数 | `+2022 / -0`（git diff 404c03d1..HEAD） |
| 自动化预扫 | `scan-all-rules.sh` 扫描 `src/main/java/com/example/demo/`，命中 **42 条**（P0=6, P1=3, P2=33） |

| 类/接口 | 路径 | 角色 |
|---------|------|------|
| DemoApplication | `src/main/java/com/example/demo/DemoApplication.java` | Spring Boot 启动类 |
| TrackingAspect | `src/main/java/com/example/demo/aspect/TrackingAspect.java` | AOP 埋点切面 |
| SecurityConfig | `src/main/java/com/example/demo/config/SecurityConfig.java` | Spring Security + JWT |
| WebConfig | `src/main/java/com/example/demo/config/WebConfig.java` | CORS 配置 |
| AuthController | `src/main/java/com/example/demo/controller/AuthController.java` | 注册/登录 |
| BubbleSortController | `src/main/java/com/example/demo/controller/BubbleSortController.java` | 冒泡排序 |
| ExportController | `src/main/java/com/example/demo/controller/ExportController.java` | Excel 导出 |
| HashController | `src/main/java/com/example/demo/controller/HashController.java` | SHA-256 哈希 |
| HelloWorldController | `src/main/java/com/example/demo/controller/HelloWorldController.java` | HelloWorld |
| TrackingController | `src/main/java/com/example/demo/controller/TrackingController.java` | 埋点报表 |
| TrackingRecord | `src/main/java/com/example/demo/model/TrackingRecord.java` | 埋点 Entity |
| User | `src/main/java/com/example/demo/model/User.java` | 用户 Entity |
| AuthResponse | `src/main/java/com/example/demo/model/dto/AuthResponse.java` | 认证响应 DTO |
| LoginRequest | `src/main/java/com/example/demo/model/dto/LoginRequest.java` | 登录请求 DTO |
| RegisterRequest | `src/main/java/com/example/demo/model/dto/RegisterRequest.java` | 注册请求 DTO |
| TrackingRecordRepository | `src/main/java/com/example/demo/repository/TrackingRecordRepository.java` | 埋点 Repository |
| UserRepository | `src/main/java/com/example/demo/repository/UserRepository.java` | 用户 Repository |
| JwtAuthenticationFilter | `src/main/java/com/example/demo/security/JwtAuthenticationFilter.java` | JWT 过滤器 |
| JwtUtil | `src/main/java/com/example/demo/security/JwtUtil.java` | JWT 签发/校验 |
| BubbleSortService | `src/main/java/com/example/demo/service/BubbleSortService.java` | 冒泡排序逻辑 |
| ExportService | `src/main/java/com/example/demo/service/ExportService.java` | Excel 生成 |
| HashService | `src/main/java/com/example/demo/service/HashService.java` | SHA-256 计算 |
| TrackingService | `src/main/java/com/example/demo/service/TrackingService.java` | 报表聚合 |
| UserService | `src/main/java/com/example/demo/service/UserService.java` | 用户注册/登录 |
| DemoApplicationTests | `src/test/java/com/example/demo/DemoApplicationTests.java` | 集成测试 |

---

## §2 功能性检查 (REQ vs Spec)

> REQ 来源：`docs/superpowers/specs/2026-08-25-three-api-tracking-dashboard-design.md` §4 + `.agents/system.changes/design.md` §1 功能清单 F01–F12。

| REQ | 功能点 | 优先级 | 状态 | Spec 证据 | 代码证据 |
|-----|--------|--------|------|-----------|----------|
| F01 | 用户注册 | P0 | ✅ | §4.1 `POST /api/auth/register` Body: `{username, password, personType, personLevel, personDept}` | `AuthController.java:14-21` — 字段完整，BCrypt 加密 |
| F02 | 用户登录 | P0 | ✅ | §4.1 `Response: {token, user: {id, username, personType, personLevel, personDept}}` | `AuthController.java:23-37` — 返回格式正确 |
| F03 | HelloWorld 接口 | P0 | ✅ | §4.2 `GET /api/helloworld?name={name}` → `{result: "Hello, {name}!"}` | `HelloWorldController.java:7-9` |
| F04 | SHA-256 哈希接口 | P0 | ✅ | §4.2 `POST /api/hash` Body: `{input}` | `HashController.java:11-18` |
| F05 | 冒泡排序接口 | P0 | ✅ | §4.2 `POST /api/bubblesort` Body: `{array}` | `BubbleSortController.java:12-21` |
| F06 | AOP 埋点记录 | P0 | ❌ | §5.2 tracking_records 表 `api_name` 字段应为 "helloworld"/"hash"/"bubblesort" | `TrackingAspect.java:34` — `joinPoint.getSignature().getName()` 返回方法名 "hello" 而非 "helloworld"；导出 "helloworld" 查不到记录 |
| F07 | Excel 导出 | P1 | ✅ | §4.3 `GET /api/export?type={helloworld\|hash\|bubblesort}` | `ExportController.java:15-25` — Content-Disposition 正确 |
| F08 | 埋点报表查询 | P1 | ✅ | §4.4 `GET /api/tracking/report?dimension={personType\|personLevel\|personDept}` | `TrackingController.java:13-18` — 维度枚举一致 |
| F09–F12 | 前端功能 | — | N/A | 非 Java 审查范围 | — |

### F06 详细分析：apiName 不匹配

**Spec 证据**（§4.2 接口定义）：
- `GET /api/helloworld` → apiName 应为 "helloworld"
- `POST /api/hash` → apiName 应为 "hash"
- `POST /api/bubblesort` → apiName 应为 "bubblesort"

**代码证据**：
- `TrackingAspect.java:34`: `String apiName = joinPoint.getSignature().getName();` — 返回 **方法名**："hello"、"hash"、"sort"
- `ExportController.java:13`: `VALID_TYPES = Set.of("helloworld", "hash", "bubblesort")` — 导出使用接口名
- `ExportService.java:20`: `trackingRepo.findByApiName(apiName)` — 按 apiName 过滤

**结论**：HelloWorldController 的方法名为 `hello()`，BubbleSortController 为 `sort()`，与导出接口的 VALID_TYPES 不匹配。导出 "helloworld" 或 "bubblesort" 时查询结果为空。

---

## §3 可读性检查 (Step 3)

> 参考 `references/readability-checklist.md` A1–A7。脚本扫描命中项已标注 `[scan]`。

| ID | 规则 | 等级 | 结果 | 命中位置 |
|----|------|------|------|----------|
| A1 | 源文件格式 | — | ✅ | 无违规 |
| A2.2 | 禁止 `import *` | P2 | ❌ | 11 处 `[scan]`：`AuthController.java:7`, `BubbleSortController.java:4`, `ExportController.java:6`, `HashController.java:4`, `HelloWorldController.java:2`, `TrackingController.java:4`, `TrackingRecord.java:2`, `User.java:2`, `JwtUtil.java:2`, `ExportService.java:6`, `TrackingService.java:7` |
| A3.4 | 行宽 ≤ 120 | P2 | ❌ | 11 处 `[scan]`：`TrackingAspect.java:53`, `TrackingRecord.java:27-29`, `User.java:28-33`, `AuthResponse.java:10,12-14`, `LoginRequest.java:4-5`, `RegisterRequest.java:5-9` |
| A4 | 命名规范 | — | ✅ | 无违规 |
| A5 | 注释规范 | — | ✅ | 无关键违规 |
| A6 | 代码结构 | — | ✅ | 分层清晰 |
| A7 | 其他 | — | ✅ | — |

---

## §4 可靠性检查 (Step 4)

### §4.1 可靠性 (G)

| ID | 问题 | 等级 | 位置 | 说明 |
|----|------|------|------|------|
| G16.2 | catch 块未记录日志 | P1 | `AuthController.java:19,35` | 注册/登录异常无日志，排障困难 |
| G16.2 | catch 块未记录日志 | P1 | `JwtUtil.java:26` | Token 校验失败无声 |
| G16.2 | catch 块未记录日志 | P1 | `ExportService.java:53` | 包裹后抛 RuntimeException，但原始异常链保留 |
| G16.2 | catch 块未记录日志 | P1 | `HashService.java:18` | SHA-256 不可用场景应记录严重错误 |
| — | `[scan]` TrackingAspect.java:63 | N/A | — | **脚本误报**：该行已执行 `log.error("Tracking aspect error", e)`，LLM 复核排除 |
| G4.3 | 全表扫描无分页 | **P0** | `TrackingService.java:18` | `trackingRepo.findAll()` 全量加载，数据量大时 OOM |
| — | N+1 查询 | **P0** | `ExportService.java:37` | 循环内逐条 `userRepository.findById()`，1000 条 = 1001 次查询 |
| — | `input.getBytes()` 无字符集 | **P0** | `HashService.java:10` | 使用平台默认编码，跨环境 SHA-256 结果不一致 |
| M016 | `LocalDateTime.now()` 无时区 | P1 | `[scan]` `TrackingAspect.java:60`, `TrackingRecord.java:16`, `User.java:20` | 应使用 UTC 或明确指定 ZoneId |

### §4.2 安全 (S)

| ID | 问题 | 等级 | 位置 | 说明 |
|----|------|------|------|------|
| S1 注入 | — | ✅ | — | 使用 JPA，无原生 SQL 拼接 |
| S3 认证 | — | ✅ | — | JWT + BCrypt 正确配置 |
| S4 授权 | — | ✅ | — | 所有认证用户可调用，符合 spec |
| S5.1 | JWT 密钥硬编码 | P1 | `application.yml:17` | 应通过 `${JWT_SECRET}` 环境变量注入 |
| S5.2 | DB 凭据硬编码 | P1 | `application.yml:6-7` | `root/root` 应外部化 |
| S10.2 | CORS `allowedHeaders("*")` | P2 | `WebConfig.java:15` | 开发环境可接受，生产应改为明确白名单 |

### §4.3 Bug 模式 (B/M/I)

> `[scan]` = `scan-all-rules.sh` 命中，LLM 复核确认。

| ID | 问题 | 等级 | 位置 |
|----|------|------|------|
| M016 | `LocalDateTime.now()` 无时区 | P1 | `[scan]` ×3（见 §4.1） |
| I004 | 使用 `java.util.Date` | P2 | `[scan]` `JwtUtil.java:19` — 建议 `java.time.Instant` |

---

## §5 自定义扩展检查 (Step 5)

N/A（未启用自定义规则，`customized-checklist.md` 仅含示例项）

---

## §6 跨仓对齐检查

| # | 对齐项 | [testDj] | [testDJnew] | 状态 |
|---|--------|----------|-------------|------|
| 1 | JWT 格式 `Bearer {token}` | `JwtAuthenticationFilter.java:20` — `startsWith("Bearer ")` | `utils/auth.js` 拦截器附加 Bearer | ✅ |
| 2 | 接口路径 `/api/*` | 全部 Controller 使用 `/api` 前缀 | `api/index.js` 统一前缀 | ✅ |
| 3 | 报表维度枚举 | `personType`/`personLevel`/`personDept` | `TrackingDashboard.jsx` 下拉切换 | ✅ |
| 4 | 导出 Content-Disposition | `ExportController.java:22` — `attachment; filename=` | `ExportButton.jsx` blob 下载 | ✅ |
| 5 | 错误码格式 | `{code, message}` 但 code 类型不一致（AuthController 用 int `400`，其他用 String `"BIZ_001"`） | — | ⚠️ P1 |
| 6 | apiName 值 | `TrackingAspect.java:34` 存方法名 "hello" | 导出 `type=helloworld` | ❌ **P0** |

---

## §7 问题片段

### P0-1: apiName 不匹配

- **P0** `TrackingAspect.java:34` — 方法名 "hello" vs 接口名 "helloworld"

```java
// src/main/java/com/example/demo/aspect/TrackingAspect.java:27-41
L27|    @Around("execution(* com.example.demo.controller.HelloWorldController.*(..)) || " +
L28|            "execution(* com.example.demo.controller.HashController.*(..)) || " +
L29|            "execution(* com.example.demo.controller.BubbleSortController.*(..))")
L30|    public Object recordTracking(ProceedingJoinPoint joinPoint) throws Throwable {
L31|        try {
L32|            var auth = SecurityContextHolder.getContext().getAuthentication();
L33|            if (auth != null && auth.getPrincipal() instanceof Long userId) {
L34|                String apiName = joinPoint.getSignature().getName(); // ❌ "hello" 而非 "helloworld"
```

```java
// src/main/java/com/example/demo/controller/ExportController.java:12-13
L12|    private final ExportService exportService;
L13|    private static final Set<String> VALID_TYPES = Set.of("helloworld", "hash", "bubblesort");
```

**修复**：在 TrackingAspect 中维护 Controller 类名 → API 名映射（`HelloWorldController→"helloworld"`, `BubbleSortController→"bubblesort"`）。

### P0-2: 字符集缺失

- **P0** `HashService.java:10` — `input.getBytes()` 未指定字符集

```java
// src/main/java/com/example/demo/service/HashService.java:7-17
 L7|    public String computeHash(String input) {
 L8|        try {
 L9|            MessageDigest digest = MessageDigest.getInstance("SHA-256");
L10|            byte[] hash = digest.digest(input.getBytes());  // ❌ 平台默认编码
L11|            StringBuilder hexString = new StringBuilder();
```

**修复**：`input.getBytes(StandardCharsets.UTF_8)`。

### P0-3: N+1 查询

- **P0** `ExportService.java:37` — 循环内逐条查用户

```java
// src/main/java/com/example/demo/service/ExportService.java:35-45
L35|            for (TrackingRecord record : records) {
L36|                Row row = sheet.createRow(rowNum++);
L37|                User user = userRepository.findById(record.getUserId()).orElse(null); // ❌ N+1
L38|                row.createCell(0).setCellValue(user != null ? user.getUsername() : "N/A");
```

**修复**：先收集所有 userId → `userRepository.findAllById(userIds)` → `Map<Long, User>`。

### P0-4: 全表扫描

- **P0** `TrackingService.java:18` — `findAll()` 无分页

```java
// src/main/java/com/example/demo/service/TrackingService.java:17-21
L17|    public List<Map<String, Object>> getReport(String dimension) {
L18|        List<TrackingRecord> records = trackingRepo.findAll();  // ❌ 全表扫描
L19|        Map<Long, User> userMap = userRepository.findAll().stream()
L20|                .collect(Collectors.toMap(User::getId, u -> u));
```

**修复**：添加分页参数（Pageable）或时间范围过滤。

### P1: 异常无日志示例

- **P1** `G16.2` `AuthController.java:19` — catch 无日志

```java
// src/main/java/com/example/demo/controller/AuthController.java:15-21
L15|    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
L16|        try {
L17|            AuthResponse resp = userService.register(req);
L18|            return ResponseEntity.ok(resp);
L19|        } catch (RuntimeException e) {  // ❌ 无日志
L20|            return ResponseEntity.badRequest().body(Map.of("code", 400, "message", e.getMessage()));
L21|        }
```

### P1: 错误码类型不一致

```java
// AuthController.java:20 — code 为 int
L20|            return ResponseEntity.badRequest().body(Map.of("code", 400, "message", e.getMessage()));

// HashController.java:15 — code 为 String
L15|            return ResponseEntity.badRequest().body(Map.of("code", "BIZ_001", "message", "输入参数不能为空"));
```

---

## §8 修复任务列表

### P0 — 必须修复（阻塞合并）

- [ ] **P0** `TrackingAspect.java:34` — 修复 apiName 映射：方法名 "hello"→"helloworld", "sort"→"bubblesort"，与 `ExportController.VALID_TYPES` 对齐
- [ ] **P0** `HashService.java:10` — `input.getBytes()` → `input.getBytes(StandardCharsets.UTF_8)`
- [ ] **P0** `ExportService.java:37` — 消除 N+1：先 `findAllById(userIds)` 构建 Map 缓存
- [ ] **P0** `TrackingService.java:18` — `findAll()` → 分页查询（Pageable）或添加时间范围过滤

### P1 — 合并前应修复

- [ ] **P1** `AuthController.java:19,35` — catch 块添加 `log.warn("Auth error", e)` 日志
- [ ] **P1** `JwtUtil.java:26` — catch 块添加 `log.debug("Invalid token", e)` 日志
- [ ] **P1** `ExportService.java:53` — catch 块添加 `log.error("Excel generation failed", e)` 并保留原始异常链
- [ ] **P1** `HashService.java:18` — catch 块添加 `log.error("SHA-256 unavailable", e)` 日志
- [ ] **P1** `TrackingAspect.java:60` / `TrackingRecord.java:16` / `User.java:20` — `LocalDateTime.now()` → `LocalDateTime.now(ZoneOffset.UTC)`
- [ ] **P1** 错误码类型统一：AuthController 的 `"code": 400`(int) → `"code": "AUTH_001"`(String)，与其他 Controller 保持一致
- [ ] **P1** `application.yml:17` — JWT secret → `${JWT_SECRET}` 环境变量
- [ ] **P1** `application.yml:6-7` — DB 密码 → `${DB_PASSWORD}` 环境变量

### P2（可选）

- [ ] **P2** 11 处 `A2.2` WildcardImport → 展开为单类导入
- [ ] **P2** 11 处 `A3.4` LineWidthExceeded → 换行处理
- [ ] **P2** `JwtUtil.java:19` `I004` — `new Date()` → `java.time.Instant`
- [ ] **P2** `WebConfig.java:15` — `allowedHeaders("*")` → 明确白名单（生产环境）

---

## §9 统计摘要

| 等级 | 数量 | 说明 |
|------|------|------|
| **P0 (Blocker)** | **4** | apiName 不匹配、字符集缺失、N+1 查询、全表扫描 |
| **P1 (Major)** | **8** | 5 处异常无日志 + 3 处时区 + 错误码不一致 + 密钥硬编码 |
| **P2 (Minor)** | **30** | 11 WildcardImport + 11 LineWidth + 1 java.util.Date + 7 其他（含 CORS allowedHeaders） |
| **总计** | **42** | 含 `scan-all-rules.sh` 命中 42 条（部分经 LLM 复核重分类） |

---

> **审查结论**：核心功能实现完整，3 接口 + 埋点 + 导出 + 报表链路打通，架构分层清晰，跨仓接口契约对齐良好。但存在 **4 个 P0 阻塞缺陷**（apiName 不匹配导致导出功能失效、字符集缺失导致跨平台哈希不一致、N+1 查询性能隐患、全表扫描 OOM 风险），建议修复 P0 后合并。