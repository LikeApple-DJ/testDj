# Code Review Report

> **Change** 算法演示与埋点报表系统 · **分支** `AI/task-DEV-9d10e310-...` · **日期** 2025-08-18 · **审查者** AI
>
> **等级 P0 / P1 / P2**；G/S 以 checklist 行内定义为准；Bug 模式 Blocker→P0、Major→P1、Info→P2。

---

## §1 审查范围

| 仓库 | 变更文件数 | Java 文件数 | 审查方式 |
|------|-----------|------------|---------|
| testDj (后端) | 32 | 21 | 自动化脚本 + LLM 逐文件 |
| testDJnew (前端) | 21 | 0 (TSX/TS) | 静态审查（非 Java，技能仅覆盖 Java） |

**审查依据**：设计文档 `design.md`、编码报告 `code.md`、需求规格 `${system.dima}.md`

---

## §2 功能性检查 (REQ)

| REQ | 功能点 | 状态 | 证据 |
|-----|--------|------|------|
| F01 | HelloWorld 接口 GET /api/helloworld | ✅ | HelloWorldController.java:16-22，返回 `{message, timestamp}` |
| F02 | 哈希算法接口 POST /api/hash (MD5/SHA256) | ✅ | HashController.java:20-33 + HashService.java:13-26 |
| F03 | 冒泡排序接口 POST /api/bubblesort | ✅ | BubbleSortController.java:22-38 + BubbleSortService.java:15-51 |
| F04 | Excel 导出接口 POST /api/export | ⚠️ | ExportController.java:21-43 — 功能正确，但错误响应未使用 ApiResult 封装（见 §3 P0-1） |
| F05 | 埋点拦截器自动记录 | ✅ | MetricsInterceptor.java:23-58 + WebConfig.java:18-20 |
| F06 | 埋点报表查询 GET /api/metrics | ⚠️ | MetricsController.java:21-35 — 日期解析异常未捕获（见 §3 P0-2） |
| F07 | 三 Tab 展示页面 | ✅ | 前端实现（非 Java 审查范围） |
| F08 | 导出按钮 | ✅ | 前端实现（非 Java 审查范围） |
| F09 | 可视化报表 | ✅ | 前端实现（非 Java 审查范围） |
| F10 | 天气接口 GET /api/weather | ✅ | WeatherController.java:19-29 + WeatherService.java:22-45（新增功能，非原始 spec） |

---

## §3 发现汇总

### 严重性分布

| 等级 | 数量 | 说明 |
|------|------|------|
| **P0 (Blocker)** | **14** | 阻塞合并 — 必须修复 |
| **P1 (Major)** | **8** | 推荐修复 — 合并前应修复 |
| **P2 (Info)** | **11** | 参考 — 可选改进 |
| **合计** | **33** | |

---

## §4 P0 — 阻塞项 (Blocker)

### P0-1: ExportController 错误响应未使用统一 ApiResult 封装
- **文件**: `controller/ExportController.java:38-41`
- **规则**: 接口契约违反 (design.md §4.1 — 所有接口返回 `{code, message, data}`)
- **证据**: design.md 第36行明确要求「统一响应格式：所有接口返回 `{code, message, data}`」
- **代码**: 异常路径使用 `java.util.Map.of("code", 400, "message", ...)` 而非 `ApiResult.error(400, ...)`
- **影响**: 前端统一解析器 `ApiResult<T>` 无法正确反序列化，导出失败时前端白屏
- **修复**: 将 `Map.of(...)` 替换为 `ApiResult.error(400, e.getMessage())`

### P0-2: MetricsController 日期解析异常未捕获
- **文件**: `controller/MetricsController.java:27-28`
- **规则**: G11.3 — 入参空值/空集合无防御性校验
- **证据**: `LocalDate.parse(startDate)` 在格式非法时抛出 `DateTimeParseException`（非 `IllegalArgumentException` 子类），未被 catch 捕获
- **影响**: 用户传入非法日期格式（如 `2025/08/18`）时返回 500 而非 400
- **修复**: 增加 `catch (DateTimeParseException e)` 分支，返回 400 + 明确错误提示

### P0-3: BubbleSortController 缺少 order 参数空值校验
- **文件**: `controller/BubbleSortController.java:32`
- **规则**: G11.3 — 入参空值无防御性校验
- **证据**: `request.getOrder()` 为 null 时传入 `bubbleSortService.sort()`，在 `order.equalsIgnoreCase()` 处抛出 NPE
- **影响**: 用户不传 order 参数时返回 500
- **修复**: 在 Controller 层增加 `order == null` 校验，返回 400

### P0-4: HashController 缺少 algorithm 参数空值校验
- **文件**: `controller/HashController.java:26`
- **规则**: G11.3 — 入参空值无防御性校验
- **证据**: `request.getAlgorithm().toUpperCase()` 在 algorithm 为 null 时抛出 NPE
- **影响**: 用户不传 algorithm 参数时返回 500
- **修复**: 在 Controller 层增加 `algorithm == null` 校验，返回 400

### P0-5 ~ P0-14: 异常捕获无日志 (G16.2 × 10)
- **规则**: G16.2 — 异常路径有日志输出且包含可追溯上下文
- **影响**: 线上问题无法排查，异常信息丢失

| # | 文件 | 行号 | 异常类型 |
|---|------|------|---------|
| P0-5 | `controller/BubbleSortController.java` | 34 | `IllegalArgumentException` catch 无日志 |
| P0-6 | `controller/ExportController.java` | 36 | `IllegalArgumentException` catch 无日志 |
| P0-7 | `controller/ExportController.java` | 39 | `Exception` catch 无日志 |
| P0-8 | `controller/HashController.java` | 29 | `IllegalArgumentException` catch 无日志 |
| P0-9 | `controller/MetricsController.java` | 31 | `IllegalArgumentException` catch 无日志 |
| P0-10 | `controller/WeatherController.java` | 25 | `IllegalArgumentException` catch 无日志 |
| P0-11 | `interceptor/MetricsInterceptor.java` | 51 | `Exception` catch 使用 `System.err.println`（非正式日志） |
| P0-12 | `service/ExportService.java` | 33 | `IllegalArgumentException` catch 仅 rethrow 无日志 |
| P0-13 | `service/ExportService.java` | 35 | `Exception` catch 包装为 RuntimeException 无日志 |
| P0-14 | `service/HashService.java` | 23 | `NoSuchAlgorithmException` catch 包装为 RuntimeException 无日志 |

**统一修复方案**: 在所有 catch 块中添加 `log.error("...", e)` 或 `log.warn("...", e)`，并引入 SLF4J Logger。

---

## §5 P1 — 推荐项 (Major)

### P1-1~P1-4: 默认时区依赖 (M016 × 4)
- **规则**: M016 — `LocalDateTime.now()` 使用默认 JVM 时区，跨区部署时行为不一致
- **影响**: 容器时区非 UTC+8 时，callTime 和 gmtCreate 时间偏移

| # | 文件 | 行号 | 代码 |
|---|------|------|------|
| P1-1 | `controller/ExportController.java` | 25 | `LocalDateTime.now()` |
| P1-2 | `entity/MetricsRecord.java` | 52 | `callTime = LocalDateTime.now()` |
| P1-3 | `entity/MetricsRecord.java` | 53 | `gmtCreate = LocalDateTime.now()` |
| P1-4 | `interceptor/MetricsInterceptor.java` | 44 | `record.setCallTime(LocalDateTime.now())` |

**修复**: 统一使用 `LocalDateTime.now(ZoneOffset.UTC)` 或注入 `Clock`。

### P1-5: MetricsInterceptor 使用 ForkJoinPool 公共线程池
- **文件**: `interceptor/MetricsInterceptor.java:48`
- **规则**: G8.6 — 使用默认无界队列线程池承载业务流量
- **证据**: `CompletableFuture.runAsync()` 使用 `ForkJoinPool.commonPool()`，高并发下可能阻塞业务线程
- **修复**: 注入自定义 `ThreadPoolTaskExecutor` 或使用 `@Async` + 自定义线程池

### P1-6: MetricsInterceptor 使用 System.err 代替日志框架
- **文件**: `interceptor/MetricsInterceptor.java:53`
- **规则**: G16.3 — 日志级别正确
- **证据**: `System.err.println()` 不经过日志框架，无法被日志收集系统采集
- **修复**: 引入 `Logger`，使用 `log.error("Metrics save failed", e)`

### P1-7: ExportService 空 catch-rethrow 无意义
- **文件**: `service/ExportService.java:33-34`
- **规则**: G8.1 — catch 吞异常或仅打 log
- **证据**: `catch (IllegalArgumentException e) { throw e; }` 无任何附加操作，可删除
- **修复**: 删除该 catch 块，让 try-with-resources 正常关闭资源

### P1-8: 缺少可应急开关
- **文件**: `config/WebConfig.java` / `application.yml`
- **规则**: G17.1 — 功能开关支持紧急关闭
- **证据**: design.md §7.3 建议 `metrics.enabled=true/false`，但 application.yml 未实现
- **修复**: 在 application.yml 添加 `metrics.enabled: true`，在 WebConfig 中读取该配置

---

## §6 P2 — 参考项 (Info)

### P2-1~P2-9: 通配符 Import (A2.2 × 9)
- **规则**: A2.2 — 禁止 `import *`（通配符引入）
- **影响**: 代码可读性下降，无法从 import 快速了解依赖

| # | 文件 | 行号 | 通配符 import |
|---|------|------|--------------|
| P2-1 | `controller/BubbleSortController.java` | 8 | `org.springframework.web.bind.annotation.*` |
| P2-2 | `controller/ExportController.java` | 5 | `org.springframework.http.*` |
| P2-3 | `controller/ExportController.java` | 6 | `org.springframework.web.bind.annotation.*` |
| P2-4 | `controller/HashController.java` | 8 | `org.springframework.web.bind.annotation.*` |
| P2-5 | `controller/MetricsController.java` | 7 | `org.springframework.web.bind.annotation.*` |
| P2-6 | `controller/WeatherController.java` | 7 | `org.springframework.web.bind.annotation.*` |
| P2-7 | `entity/MetricsRecord.java` | 3 | `jakarta.persistence.*` |
| P2-8 | `service/ExportService.java` | 3 | `org.apache.poi.ss.usermodel.*` |
| P2-9 | `service/MetricsService.java` | 11 | `java.util.*` |

**修复**: 展开为显式 import。IDE 设置 `Code Style → Java → Imports → Class count to use import with '*': 999`。

### P2-10: 缺少单元测试
- **规则**: G11.1 — 新逻辑无单测
- **证据**: `pom.xml` 包含 `spring-boot-starter-test` 但无任何 `*Test.java` 文件
- **修复**: 至少为 HashService、BubbleSortService、MetricsService 添加单元测试

### P2-11: 缺少 Javadoc
- **规则**: A7.1 — public 类、public 成员必须有 Javadoc
- **证据**: 所有 Controller、Service、DTO 类均无 Javadoc
- **修复**: 为 public API 添加基本 Javadoc

---

## §7 安全检查

| ID | 检查项 | 结果 | 说明 |
|----|--------|------|------|
| S1 | SQL 注入 | ✅ 通过 | JPA @Query 使用命名参数 `:start` / `:end`，无拼接 |
| S2 | XSS | N/A | 后端为 REST API，JSON 响应由前端框架处理 |
| S3 | SSRF | N/A | 无外部 URL 请求 |
| S4 | 命令执行 | N/A | 无系统命令调用 |
| S5 | XXE | N/A | 无 XML 解析 |
| S6 | 反序列化 | N/A | 仅使用 Spring Boot 默认 Jackson 反序列化 DTO |
| S7 | 文件上传/下载 | ⚠️ | 导出接口无认证（设计明确排除），filename 未做路径穿越过滤 |
| S8 | 访问控制 | ⚠️ | 设计明确排除认证，但生产环境必须接入 |
| S9 | 数据安全 | ✅ | 无硬编码密钥，H2 密码为空（演示环境可接受） |
| S10 | CSRF/CORS | N/A | 演示环境，未配置 CORS |

---

## §8 修复任务列表

### 必须修复 (P0) — 14 项

- [ ] **P0-1**: ExportController L38-41 → 使用 `ApiResult.error()` 替代 `Map.of()`
- [ ] **P0-2**: MetricsController L27-28 → 捕获 `DateTimeParseException`，返回 400
- [ ] **P0-3**: BubbleSortController L32 → 增加 `order` 参数 null 校验
- [ ] **P0-4**: HashController L26 → 增加 `algorithm` 参数 null 校验
- [ ] **P0-5**: BubbleSortController L34 → catch 块添加 `log.error`
- [ ] **P0-6**: ExportController L36 → catch 块添加 `log.error`
- [ ] **P0-7**: ExportController L39 → catch 块添加 `log.error`
- [ ] **P0-8**: HashController L29 → catch 块添加 `log.error`
- [ ] **P0-9**: MetricsController L31 → catch 块添加 `log.error`
- [ ] **P0-10**: WeatherController L25 → catch 块添加 `log.error`
- [ ] **P0-11**: MetricsInterceptor L51-53 → 使用 SLF4J Logger 替代 `System.err`
- [ ] **P0-12**: ExportService L33-34 → 删除无意义的 catch-rethrow 或添加日志
- [ ] **P0-13**: ExportService L35-36 → catch 块添加 `log.error`
- [ ] **P0-14**: HashService L23-24 → catch 块添加 `log.error`

### 推荐修复 (P1) — 8 项

- [ ] **P1-1~P1-4**: 统一使用 `LocalDateTime.now(ZoneOffset.UTC)` 或注入 `Clock`
- [ ] **P1-5**: MetricsInterceptor → 使用自定义线程池替代 `ForkJoinPool.commonPool()`
- [ ] **P1-6**: MetricsInterceptor → 使用 Logger 替代 `System.err.println()`
- [ ] **P1-7**: ExportService L33-34 → 删除空 catch-rethrow
- [ ] **P1-8**: 添加 `metrics.enabled` 开关到 application.yml

### 可选改进 (P2) — 11 项

- [ ] **P2-1~P2-9**: 展开通配符 import 为显式 import
- [ ] **P2-10**: 添加单元测试（HashService / BubbleSortService / MetricsService）
- [ ] **P2-11**: 为 public API 添加 Javadoc

---

## §9 跨仓对齐点检查

| # | 对齐项 | 后端 (testDj) | 前端 (testDJnew) | 状态 |
|---|--------|---------------|-------------------|------|
| 1 | 统一响应 `{code, message, data}` | `ApiResult<T>` | `ApiResult<T>` | ⚠️ ExportController 错误路径违反 |
| 2 | `GET /api/helloworld` | `HelloWorldController` | `fetchHelloWorld()` | ✅ |
| 3 | `POST /api/hash` | `HashController` + `HashRequest` | `fetchHash(input, algorithm)` | ✅ |
| 4 | `POST /api/bubblesort` | `BubbleSortController` | `fetchBubbleSort(array, order)` | ✅ |
| 5 | `POST /api/export` → Blob | `ExportController` + POI | `exportExcel(type, data)` | ✅ |
| 6 | `GET /api/metrics?dimension=` | `MetricsController` | `fetchMetrics(dimension)` | ✅ |
| 7 | 埋点 `X-Caller-*` 请求头 | `MetricsInterceptor` 读取 | `callerHeaders()` 注入 | ✅ |
| 8 | 维度枚举 `personType/level/department` | `MetricsService` switch | `Dimension` 类型 | ✅ |
| 9 | 冒泡排序最大长度 100 | `MAX_LENGTH = 100` | 前端无线制（后端兜底） | ✅ |
| 10 | 导出文件名 `export-{type}-{timestamp}.xlsx` | `Content-Disposition` | 前端 `a.download` | ✅ |
| 11 | 天气接口 `GET /api/weather` | `WeatherController` | `WeatherTab.tsx` | ✅ (新增功能) |

---

> **审查结论**: ⚠️ **不建议直接合并** — 存在 14 个 P0 阻塞项，其中 4 个为功能性缺陷（统一响应违反、日期解析异常、空值 NPE），10 个为日志缺失导致线上不可观测。建议优先修复 P0-1~P0-4 后再合并，P0-5~P0-14 可批量统一修复。

> **审查时间**: 2025-08-18 | **工具**: scan-all-rules.sh + LLM 逐文件审查 | **规则覆盖**: 52/222 脚本规则 + 全量 LLM 补扫