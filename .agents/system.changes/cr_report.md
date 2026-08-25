# Code Review Report

> **Change** `三接口工具 + 埋点可视化报表` · **分支** `AI/task-DEV-966dcd0a-7905-11f1-9649-3b4281182f10-e09a2952-5098-4d54-8616-02251dd458b2` · **日期** `2026-08-25` · **审查者** AI

---

## §1 审查范围

| 仓库 | 文件数 | 新增行 |
|------|--------|--------|
| [testDj] Java 后端 | 25 个 Java 文件 + pom.xml + application.yml + 测试 | 4,295 |
| [testDJnew] React 前端 | 14 个 JSX/JS 文件 | 558 |

**审查策略**：自动化预扫 `scan-all-rules.sh`（52/222 规则）→ LLM 逐文件补全 Step 2–5。

---

## §2 功能性检查 (REQ vs Spec)

> 对照 design.md 功能清单 F01–F12 逐项核对。

| REQ | 功能点 | 优先级 | 状态 | 证据 |
|-----|--------|--------|------|------|
| F01 | 用户注册 | P0 | ❌ 部分不符 | 缺少密码长度≥6 校验(R02) 和用户名非空校验(R03)；错误码返回 `400` 而非 `AUTH_001` |
| F02 | 用户登录 | P0 | ❌ 部分不符 | 错误码返回 `400` 而非 `AUTH_002` |
| F03 | HelloWorld 接口 | P0 | ✅ | HelloWorldController.java:8 — `GET /api/helloworld?name={name}` |
| F04 | SHA-256 哈希接口 | P0 | ✅ | HashController.java:12 — `POST /api/hash` |
| F05 | 冒泡排序接口 | P0 | ✅ | BubbleSortController.java:13 — `POST /api/bubblesort` |
| F06 | AOP 埋点记录 | P0 | ✅ | TrackingAspect.java:27-30 — 拦截三个业务 Controller |
| F07 | Excel 导出 | P1 | ✅ | ExportController.java:16 — `GET /api/export?type=` |
| F08 | 埋点报表查询 | P1 | ✅ | TrackingController.java:14 — `GET /api/tracking/report?dimension=` |
| F09 | 前端三 Tab 工具页 | P0 | ✅ | [testDJnew] DashboardPage.jsx + 三个 Tab 组件 |
| F10 | 前端导出按钮 | P1 | ✅ | [testDJnew] ExportButton.jsx |
| F11 | 前端可视化报表 | P1 | ✅ | [testDJnew] TrackingDashboard.jsx (ECharts) |
| F12 | JWT 认证拦截 | P0 | ✅ | JwtAuthenticationFilter.java + SecurityConfig.java |

---

## §3 可读性检查 (Step 3)

| ID | 规则 | 等级 | 命中文件 | 说明 |
|----|------|------|----------|------|
| A2.2 | 禁止 `import *` | P2 | 11 个文件 | 全部使用通配符导入 `org.springframework.web.bind.annotation.*` 等 |
| A3.4 | 行宽 ≤ 120 | P2 | 9 个文件 20 处 | getter/setter 同行压缩导致超宽 |
| A3.6 | 类成员间空行 | P2 | User.java:27-33, TrackingRecord.java:24-29, AuthResponse.java:9-14 | getter/setter 连续无空行分隔 |
| A7.1 | public 类/方法需 Javadoc | P2 | 全部 Controller/Service/Entity | 所有 public 类和方法均无 Javadoc |

---

## §4 可靠性检查 (Step 4)

### §4.1 可靠性 (G)

| ID | 问题 | 等级 | 位置 |
|----|------|------|------|
| G4.3 | 无分页大列表查询 | P1 | TrackingService.java:18 — `trackingRepo.findAll()` 全量加载 |
| G16.2 | catch 块未记录日志 | P0 | AuthController.java:19, AuthController.java:35, JwtUtil.java:26, ExportService.java:53, HashService.java:18 |

> **自动化预扫误报说明**：`TrackingAspect.java:63` 的 `catch(Exception e)` 实际已执行 `log.error(...)`，脚本标记为误报，已排除。

### §4.2 安全 (S)

| ID | 问题 | 等级 | 位置 |
|----|------|------|------|
| S9.1 | JWT 密钥硬编码 | **P0** | application.yml:17 — `jwt.secret: dGhpcyBpcyBhIHZlcnkgbG9uZyBzZWNyZXQga2V5IGZvciBqd3QgdG9rZW4gZ2VuZXJhdGlvbg==` |
| S10.2 | CORS allowedHeaders 使用 `*` | P1 | WebConfig.java:15 — `allowedHeaders("*")` 应改为明确白名单 |

### §4.3 Bug 模式 (B/M/I)

| ID | 问题 | 等级 | 位置 |
|----|------|------|------|
| M016 | `LocalDateTime.now()` 无时区 | P1 | TrackingAspect.java:60, TrackingRecord.java:16, User.java:20 |
| I004 | 使用 `java.util.Date` | P2 | JwtUtil.java:19 — 建议使用 `java.time.Instant` |

---

## §5 自定义扩展检查 (Step 5)

N/A（未启用自定义规则）

---

## §6 LLM 深度审查发现

### §6.1 P0 — 阻塞问题

| # | 问题 | 位置 | Spec 证据 | 代码证据 |
|---|------|------|-----------|----------|
| **P0-1** | 注册缺少密码长度校验 | UserService.java:18-31 | design.md R02: "密码长度 >= 6，返回 AUTH_003" | `register()` 方法无任何 `password.length()` 检查 |
| **P0-2** | 注册缺少用户名非空校验 | UserService.java:18-31 | design.md R03: "用户名不能为空，返回 AUTH_004" | `register()` 方法无 `username` 空值检查 |
| **P0-3** | 错误码格式与 Spec 不符 | AuthController.java:19-20, 35-36 | design.md §5.1.2: 错误码 `AUTH_001`/`AUTH_002` | 代码返回 `"code": 400`（HTTP 状态码），而非 Spec 定义的 `AUTH_001` 等业务错误码 |
| **P0-4** | JWT 密钥硬编码在配置文件 | application.yml:17 | security-checklist.md S9.1: "密钥/凭证不硬编码" | `jwt.secret` 明文 Base64 字符串直接写在 yml 中 |
| **P0-5** | `input.getBytes()` 未指定字符集 | HashService.java:10 | 通用最佳实践 | `input.getBytes()` 使用平台默认编码，跨环境结果不一致 |
| **P0-6** | catch 块吞异常无日志 (×5) | 见 §4.1 G16.2 | 可靠性军规 G16.2 | 5 处 catch 块既未记录日志也未重新抛出 |

### §6.2 P1 — 推荐修复

| # | 问题 | 位置 | 说明 |
|---|------|------|------|
| **P1-1** | N+1 查询 | ExportService.java:37 | 循环内逐条 `userRepository.findById()`，应改为批量查询 `findAllById()` |
| **P1-2** | 全量查询无分页 | TrackingService.java:18 | `trackingRepo.findAll()` 可能随数据增长导致 OOM |
| **P1-3** | `LocalDateTime.now()` 无时区 | 3 处 | 应使用 `ZonedDateTime` 或明确指定 `ZoneId` |
| **P1-4** | `instanceof Long` 耦合脆弱 | TrackingAspect.java:33 | principal 类型硬编码为 `Long`，如果 SecurityConfig 变更认证方式会静默失效 |
| **P1-5** | `@RequestBody` 缺少 `@Valid` | AuthController.java:15,24 | RegisterRequest/LoginRequest 缺少 `@NotNull`/`@NotBlank` 校验注解 |

### §6.3 P2 — 参考改进

| # | 问题 | 位置 |
|---|------|------|
| P2-1 | 11 个文件使用通配符 import | 全部 Controller + 部分 Service/Model |
| P2-2 | 20 处行宽超过 120 字符 | getter/setter 密集的 Entity 和 DTO |
| P2-3 | 无 Javadoc 文档 | 全部 public 类和方法 |
| P2-4 | `java.util.Date` 遗留 API | JwtUtil.java:19 |

---

## §7 跨仓对齐检查

| # | 对齐项 | [testDj] | [testDJnew] | 状态 |
|---|--------|----------|-------------|------|
| 1 | JWT 格式 `Bearer {token}` | JwtAuthenticationFilter.java:20 — `startsWith("Bearer ")` | utils/auth.js — 拦截器附加 Bearer Token | ✅ |
| 2 | 接口路径 `/api/*` | 全部 Controller 使用 `/api` 前缀 | api/index.js — 统一 `/api` 前缀 | ✅ |
| 3 | 报表维度枚举 | `personType`/`personLevel`/`personDept` | TrackingDashboard.jsx — 维度切换按钮 | ✅ |
| 4 | 导出文件 Content-Disposition | ExportController.java:22 — `attachment; filename=` | ExportButton.jsx — blob 下载 | ✅ |
| 5 | 错误码格式 | 返回 `{code, message}` 但 code 为 400 而非业务码 | — | ⚠️ 参见 P0-3 |

---

## §8 修复任务列表

### P0 — 必须修复（阻塞合并）

- [ ] **P0-1**: UserService.register() 添加密码长度 ≥6 校验，不满足抛出 RuntimeException("密码长度不能少于6位")
- [ ] **P0-2**: UserService.register() 添加 username 非空校验
- [ ] **P0-3**: AuthController 统一错误码格式，register 返回 `AUTH_001`/`AUTH_003`/`AUTH_004`，login 返回 `AUTH_002`
- [ ] **P0-4**: 将 JWT secret 移到环境变量或配置中心，application.yml 中使用 `${JWT_SECRET}` 占位符
- [ ] **P0-5**: HashService.java:10 改为 `input.getBytes(StandardCharsets.UTF_8)`
- [ ] **P0-6**: AuthController(×2)、JwtUtil、ExportService、HashService 的 catch 块添加日志记录

### P1 — 合并前应修复

- [ ] **P1-1**: ExportService 批量查询用户替代 N+1 模式
- [ ] **P1-2**: TrackingService.getReport() 添加分页或限制查询条数
- [ ] **P1-3**: 统一使用 `ZonedDateTime.now(ZoneOffset.UTC)` 替代 `LocalDateTime.now()`
- [ ] **P1-4**: TrackingAspect principal 类型检查改为更健壮的方式
- [ ] **P1-5**: 为 RegisterRequest/LoginRequest 添加 `@NotBlank` 校验注解，Controller 添加 `@Valid`

### P2 — 可选改进

- [ ] 替换所有 `import *` 为显式导入
- [ ] 拆分超长行（getter/setter 分多行或使用 Lombok）
- [ ] 为 public API 添加 Javadoc

---

## §9 统计摘要

| 等级 | 数量 | 说明 |
|------|------|------|
| **P0 (Blocker)** | **9** | 6 功能/安全阻断 + 3 脚本扫描 |
| **P1 (Major)** | **8** | 3 脚本扫描 + 5 LLM 深度审查 |
| **P2 (Minor)** | **35** | 33 脚本扫描 + 2 可读性 |
| **总计** | **52** | |

---

> **审查结论**：代码整体完成了 12 项功能需求中的 10 项，架构分层清晰，跨仓接口契约对齐良好。但存在 **9 个 P0 阻断问题**，主要集中在：错误码格式与 Spec 不符、关键输入校验缺失、JWT 密钥硬编码。建议在修复 P0 问题后方可合并。