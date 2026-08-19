# Code Review Report

> **Change** `分别写三个接口helloworld_哈希` · **分支/Commit** `AI/task-DEV-9d10e310-7901-11f1-8a9f-59ecae612580-cbc6700a-7c5c-4d3d-b5e2-3930c18d1f39` / `N/A` · **日期** `2025-08-19` · **审查者** AI
>
> **AI**：等级 **P0 / P1 / P2**；G/S 以 checklist 行内定义为准；Bug 模式以 `bug-pattern-checklist.md` 表头为准（Blocker→P0、Major→P1、Info→P2）。**须先**运行 `scan-all-rules.sh` 并将要点并入 §5，**再**写 LLM 结论。

---

## 1. 审查范围

| 项 | 值 |
|----|-----|
| `.java` 文件数 | `22` |
| 变更行数 | `+2100 / -0`（全部新增） |

| 类/接口 | 路径 | 角色 |
|---------|------|------|
| `DemoApplication` | `src/main/java/com/dtcode/demo/DemoApplication.java` | Spring Boot 启动类 |
| `DemoController` | `src/main/java/com/dtcode/demo/demo/api/controller/DemoController.java` | 演示接口控制器 |
| `DemoService` | `src/main/java/com/dtcode/demo/demo/service/DemoService.java` | 演示服务接口 |
| `DemoServiceImpl` | `src/main/java/com/dtcode/demo/demo/service/impl/DemoServiceImpl.java` | 演示服务实现 |
| `AnalyticsController` | `src/main/java/com/dtcode/demo/analytics/api/controller/AnalyticsController.java` | 调用分析控制器 |
| `AnalyticsService` | `src/main/java/com/dtcode/demo/analytics/service/AnalyticsService.java` | 调用分析服务接口 |
| `AnalyticsServiceImpl` | `src/main/java/com/dtcode/demo/analytics/service/impl/AnalyticsServiceImpl.java` | 调用分析服务实现 |
| `ExportController` | `src/main/java/com/dtcode/demo/export/api/controller/ExportController.java` | 导出控制器 |
| `ExportService` | `src/main/java/com/dtcode/demo/export/service/ExportService.java` | 导出服务接口 |
| `ExportServiceImpl` | `src/main/java/com/dtcode/demo/export/service/impl/ExportServiceImpl.java` | 导出服务实现 |
| `ApiCallLogDO` | `src/main/java/com/dtcode/demo/analytics/dao/entity/ApiCallLogDO.java` | 调用日志实体 |
| `ApiCallLogMapper` | `src/main/java/com/dtcode/demo/analytics/dao/mapper/ApiCallLogMapper.java` | 调用日志 Mapper |
| `HelloWorldRequest` | `src/main/java/com/dtcode/demo/demo/model/dto/HelloWorldRequest.java` | HelloWorld 请求 DTO |
| `HelloWorldDTO` | `src/main/java/com/dtcode/demo/demo/model/dto/HelloWorldDTO.java` | HelloWorld 响应 DTO |
| `HashRequest` | `src/main/java/com/dtcode/demo/demo/model/dto/HashRequest.java` | 哈希请求 DTO |
| `HashDTO` | `src/main/java/com/dtcode/demo/demo/model/dto/HashDTO.java` | 哈希响应 DTO |
| `BubbleSortRequest` | `src/main/java/com/dtcode/demo/demo/model/dto/BubbleSortRequest.java` | 冒泡排序请求 DTO |
| `BubbleSortDTO` | `src/main/java/com/dtcode/demo/demo/model/dto/BubbleSortDTO.java` | 冒泡排序响应 DTO |
| `CallSummaryDTO` | `src/main/java/com/dtcode/demo/analytics/model/dto/CallSummaryDTO.java` | 调用汇总 DTO |
| `TrendDTO` | `src/main/java/com/dtcode/demo/analytics/model/dto/TrendDTO.java` | 调用趋势 DTO |
| `DistributionDTO` | `src/main/java/com/dtcode/demo/analytics/model/dto/DistributionDTO.java` | 调用分布 DTO |
| `ApiResponse` | `src/main/java/com/dtcode/demo/common/model/ApiResponse.java` | 统一响应封装 |
| `BusinessException` | `src/main/java/com/dtcode/demo/common/exception/BusinessException.java` | 业务异常 |
| `GlobalExceptionHandler` | `src/main/java/com/dtcode/demo/common/exception/GlobalExceptionHandler.java` | 全局异常处理 |
| `AsyncConfig` | `src/main/java/com/dtcode/demo/common/config/AsyncConfig.java` | 异步线程池配置 |
| `ApiNameEnum` | `src/main/java/com/dtcode/demo/common/constant/ApiNameEnum.java` | 接口名称枚举 |
| `ResponseStatusEnum` | `src/main/java/com/dtcode/demo/common/constant/ResponseStatusEnum.java` | 响应状态枚举 |
| `DemoServiceImplTest` | `src/test/java/com/dtcode/demo/demo/service/impl/DemoServiceImplTest.java` | 单元测试 |

---

## 2. 问题计数

| P0 | P1 | P2 |
|----|----|-----|
| 2 | 5 | 3 |

---

## 3. Step 2 — 功能（REQ）

### REQ-F01: HelloWorld 接口

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| POST /api/demo/helloworld 返回问候语 | ✅ | design §5.1.2 W01 | `DemoController.java:34-42`, `DemoServiceImpl.java:47-54` | 正常返回 "Hello, {name}!" |
| name 为 null/空时使用默认值 "World" | ✅ | design §5.1.3.1 R01 | `DemoServiceImpl.java:48` | `(name == null ‖ name.trim().isEmpty()) ? DEFAULT_NAME` |
| 返回含 timestamp | ✅ | design §5.1.2 W01 出参 | `DemoServiceImpl.java:50` | 格式 yyyy-MM-dd HH:mm:ss |

### REQ-F02: 哈希算法接口

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| POST /api/demo/hash 返回 SHA-256 哈希值 | ✅ | design §5.1.2 W02, A02 | `DemoServiceImpl.java:61-64` | MessageDigest SHA-256 + bytesToHex |
| input 为空时返回 DEMO_002 | ✅ | design §5.1.3.2 R02 | `DemoServiceImpl.java:58-59` | BusinessException("DEMO_002") |
| SHA-256 不可用时返回 DEMO_003 | ✅ | design §5.1.3.2 异常场景 | `DemoServiceImpl.java:69-72` | catch NoSuchAlgorithmException |

### REQ-F03: 冒泡排序接口

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| POST /api/demo/bubble-sort 返回升序结果 | ✅ | design §5.1.2 W03 | `DemoServiceImpl.java:87-102` | 经典冒泡 + swapped 优化 |
| 返回 original + sorted | ✅ | design §5.1.2 W03 出参 | `DemoServiceImpl.java:84-85, 105` | original 使用 unmodifiableList 保护 |
| numbers 为空时返回 DEMO_004 | ✅ | design §5.1.3.3 R04 | `DemoServiceImpl.java:77-78` | |
| 数组超过 10000 返回 DEMO_005 | ✅ | design §5.1.3.3 R05 | `DemoServiceImpl.java:80-81` | |

### REQ-F05/F06: 导出接口

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| GET /api/export/helloworld 导出 CSV | ✅ | design §5.2.2 W04 | `ExportController.java:31-36` | Content-Type text/csv, attachment |
| GET /api/export/hash 导出 CSV | ✅ | design §5.2.2 W05 | `ExportController.java:38-43` | |
| GET /api/export/bubble-sort 导出 CSV | ✅ | design §5.2.2 W06 | `ExportController.java:45-50` | |
| 无数据时返回 EXPORT_001 | ✅ | design §5.2.3.1 异常场景 | `ExportServiceImpl.java:37-38` | BusinessException("EXPORT_001") |
| 文件名含接口名+时间戳 | ✅ | design §5.2.3.1 R07 | `ExportController.java:34,41,48` | 格式 `{type}_yyyyMMdd_HHmmss.csv` |

### REQ-F07: 接口调用埋点

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 异步记录埋点不阻塞业务 | ✅ | design §5.3.3.1 R08 | `AnalyticsServiceImpl.java:48-49` | @Async("asyncExecutor") |
| 埋点失败不影响业务 | ✅ | design §5.3.3.1 R09 | `AnalyticsServiceImpl.java:62-64` | catch Exception + warn log |
| 调用人信息从请求上下文获取 | ❌ | design §5.3.3.1 R10, §4.3 S04 | `AnalyticsServiceImpl.java:53-57` | caller_id 始终为 "UNKNOWN"，所有维度字段为空；接口签名仅 3 参数，未传递 caller 信息 |
| 埋点写入调用次数和调用人 | ❌ | 需求原文 "获取调用次数和调用人" | `AnalyticsServiceImpl.java:53` | callerId 硬编码 "UNKNOWN"，多维度统计（caller_type/level/dept）数据源永远为空 |

### REQ-F08: 调用统计查询接口

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| GET /api/analytics/summary 按维度汇总 | ✅ | design §5.3.2 W07 | `AnalyticsServiceImpl.java:76-94` | 维度校验 + GROUP BY |
| GET /api/analytics/trend 按时间粒度 | ✅ | design §5.3.2 W08 | `AnalyticsServiceImpl.java:105-129` | DATE_FORMAT + GROUP BY |
| GET /api/analytics/distribution 分布百分比 | ✅ | design §5.3.2 W09 | `AnalyticsServiceImpl.java:140-164` | 百分比保留一位小数 |
| 日期范围默认近 7 天 | ✅ | design §5.3.3.2 R11 | `AnalyticsServiceImpl.java:182-187` | DEFAULT_DATE_RANGE_DAYS=7 |
| 维度校验 | ✅ | design §5.3.3.2 R12 | `AnalyticsServiceImpl.java:166-169` | VALID_DIMENSIONS 白名单 |
| 结果按 callCount 降序 | ✅ | design §5.3.3.2 R13 | `ApiCallLogMapper.xml:41` | ORDER BY callCount DESC |

### REQ: 导出缓存设计

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 各用户独立会话缓存 | ❌ | design §5.2.3.1 并发控制 "各用户独立会话缓存" | `DemoServiceImpl.java:40` | 全局 ConcurrentHashMap 单例共享，多用户并发时导出结果为最后执行者的数据 |

---

## 4. Step 3 — 可读性检查

| ID | 结果 | 说明 |
|----|------|------|
| A1 源文件格式 | ✅ | UTF-8 编码，文件名与类名一致 |
| A2 源文件结构 | ✅ | 无 `import *`，import 分组正确 |
| A3 代码样式 | ⚠️ | `A3.4` HashDTO.java:59 行宽超 120 字符（toString 方法） |
| A4 命名规范 | ✅ | 包名全小写、类名 UpperCamelCase、方法名 lowerCamelCase、常量 UPPER_SNAKE_CASE |
| A5 编码实践 | ✅ | @Override 正确标注，无空 catch |
| A6 特定元素样式 | ✅ | 修饰符顺序正确，long 字面量无使用场景 |
| A7 Javadoc | ✅ | public 类有 Javadoc，getter 属自解释可省略 |

---

## 5. Step 4 — 可靠性检查

### 预扫结果（scan-all-rules.sh）

```
[P0] B022 — DateFormatThreadSafety: AnalyticsServiceImpl.java:173  → 复核：误报（SimpleDateFormat 为方法局部变量，线程安全）
[P0] G16.2 — CatchWithoutLogging: AnalyticsServiceImpl.java:62     → 复核：误报（catch 内有 logger.warn）
[P0] G16.2 — CatchWithoutLogging: AnalyticsServiceImpl.java:191    → 复核：部分命中（catch 后 throw 但未链入原始 cause）
[P0] G16.2 — CatchWithoutLogging: DemoServiceImpl.java:69          → 复核：误报（catch 内有 logger.error）
[P0] G16.2 — CatchWithoutLogging: ExportServiceImpl.java:48,69,90  → 复核：误报（catch 内有 logger.error）
[P1] M016 — JavaTimeDefaultTimeZone: DemoServiceImpl.java:50,65,104 → 确认命中
[P1] M016 — JavaTimeDefaultTimeZone: ExportController.java:34,41,48 → 确认命中
[P2] A3.4 — LineWidthExceeded: HashDTO.java:59                      → 确认命中
[P2] I004 — JavaUtilDate: AnalyticsServiceImpl.java:178             → 确认命中
```

### 可靠性检查矩阵

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| G1 并发控制 | reliability-checklist.md | ✅ | N/A | 三个业务接口无状态，无并发写入共享资源 |
| G2 幂等拦截 | reliability-checklist.md | ✅ | N/A | 无写接口幂等需求（INSERT 天然幂等，统计查询只读） |
| G3 事务控制 | reliability-checklist.md | ✅ | N/A | 无 @Transactional，埋点 INSERT 为独立操作 |
| G4 SQL与索引 | reliability-checklist.md | ⚠️ | P1 | `${dimension}` 在 GROUP BY 中使用，已通过 VALID_DIMENSIONS 白名单校验（满足 S1.2），但缺少代码注释说明安全策略 |
| G6 缓存 | reliability-checklist.md | ❌ | P0 | `DemoServiceImpl.java:40` — resultCache 为全局单例 ConcurrentHashMap，无 TTL/无会话隔离，违反 design §5.2.3.1 "各用户独立会话缓存" |
| G8 防御编程 | reliability-checklist.md | ⚠️ | P1 | `ExportServiceImpl.java:42-46` — PrintWriter 未使用 try-with-resources（但因 ByteArrayOutputStream 为内存流，无实际资源泄漏，降级为 P2） |
| G11 开发自测 | reliability-checklist.md | ✅ | N/A | DemoServiceImplTest 覆盖正常/边界/异常路径，断言完整 |
| G14 时区 | reliability-checklist.md | ⚠️ | P1 | `DemoServiceImpl.java:50,65,104`、`ExportController.java:34,41,48` — LocalDateTime.now() 未显式指定时区（M016） |
| G16 可监控 | reliability-checklist.md | ✅ | N/A | 异常路径有日志输出，级别正确（业务 WARN、系统 ERROR） |
| G17 可应急 | reliability-checklist.md | ✅ | N/A | 演示系统，回滚无数据迁移风险 |

### 安全检查矩阵

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| S1 SQL 注入 | security-checklist.md | ✅ | N/A | `${dimension}` 为动态列名，已通过白名单校验（S1.2 满足）；其他参数均用 `#{}` 预编译 |
| S2 XSS | security-checklist.md | ✅ | N/A | 后端 REST API 返回 JSON，无 HTML 输出 |
| S8 访问控制 | security-checklist.md | ⚠️ | P1 | design §6.4.2 提到"全局统一拦截器校验登录态"，但代码中未实现任何认证/鉴权拦截器 |
| S9 数据安全 | security-checklist.md | ⚠️ | P1 | `application.yml:8` — 数据库密码 "root" 硬编码在配置文件中，应从配置中心或环境变量获取 |

### Bug 模式检查矩阵

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| B022 DateFormatThreadSafety | bug-pattern-checklist.md | ✅ | N/A | 误报：SimpleDateFormat 为方法局部变量 |
| M016 JavaTimeDefaultTimeZone | bug-pattern-checklist.md | ⚠️ | P1 | DemoServiceImpl + ExportController 共 6 处 LocalDateTime.now() 无显式时区 |
| I004 JavaUtilDate | bug-pattern-checklist.md | ⚠️ | P2 | AnalyticsServiceImpl.java:178 使用 `new Date()` 旧 API |
| 其他 B/M/I 规则 | bug-pattern-checklist.md | ✅ | N/A | 脚本扫描 52/222 条 + LLM 补扫，未发现其他命中 |

---

## 6. Step 5 — 自定义扩展检查

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| 自定义扩展 | customized-checklist.md | N/A | N/A | N/A(未启用自定义规则) |

---

## 7. 结论

- **合并建议**：修复后合并
- **P0（2项）**：
  1. 埋点方法签名与系分规格不符，caller 维度数据始终为空，多维度报表功能不可用
  2. 结果缓存为全局单例，违反"各用户独立会话缓存"设计，多用户并发时导出数据错乱
- **P1（5项）**：
  1. AnalyticsController 直接注入 Impl 而非接口，违反依赖倒置
  2. ExportServiceImpl 直接注入 DemoServiceImpl 而非接口，getCachedResult 方法不在接口定义中
  3. AnalyticsService 接口缺少 getSummary/getTrend/getDistribution 方法声明
  4. LocalDateTime.now() 未指定时区（6处）
  5. application.yml 数据库密码硬编码 "root"
- **P2（3项）**：
  1. HashDTO.java:59 行宽超 120 字符
  2. AnalyticsServiceImpl.java:178 使用 java.util.Date 旧 API
  3. AnalyticsServiceImpl.java:191-193 ParseException 未链入 BusinessException cause
- **一句话**：核心业务逻辑（三接口计算、导出CSV、异步埋点框架）实现正确，但埋点数据采集链路存在功能缺陷（caller 维度字段永远为空），需修复后方可支撑多维度报表需求。

---

## 7.1 问题片段（必填）

### P0-1: 埋点方法签名与系分规格不符，caller 维度数据永远为空

- **P0** `G6+功能不符` `src/main/java/com/dtcode/demo/analytics/service/impl/AnalyticsServiceImpl.java:47-65` — recordCallAsync 方法仅接收 apiName/requestParams/durationMs 三个参数，未传递 callerId/callerName/callerType/callerLevel/callerDept，导致所有维度字段为空。系分 §4.3 S04 定义的签名为 `recordCall(String apiName, String callerId, String callerName, String callerType, String callerLevel, String callerDept)`。

  片段范围：`AnalyticsServiceImpl.java:47-65`

```java
L47|    @Override
L48|    @Async("asyncExecutor")
L49|    public void recordCallAsync(String apiName, String requestParams, long durationMs) {
L50|        try {
L51|            ApiCallLogDO logDO = new ApiCallLogDO();
L52|            logDO.setApiName(apiName);
L53|            logDO.setCallerId("UNKNOWN");       // 问题：硬编码，永远不获取真实调用人
L54|            logDO.setCallerName("");            // 问题：空字符串，维度统计无数据
L55|            logDO.setCallerType("");            // 问题：人员类型为空
L56|            logDO.setCallerLevel("");           // 问题：人员层级为空
L57|            logDO.setCallerDept("");            // 问题：人员部门为空
L58|            logDO.setRequestParams(requestParams);
L59|            logDO.setResponseStatus(ResponseStatusEnum.SUCCESS.getCode());
L60|            logDO.setCallDurationMs((int) durationMs);
L61|            apiCallLogMapper.insert(logDO);
L62|        } catch (Exception e) {
L63|            logger.warn("异步埋点记录失败: apiName={}, error={}", apiName, e.getMessage());
L64|        }
L65|    }
```

同时 Controller 层调用时也未传递 caller 信息：

  片段范围：`DemoController.java:34-42`

```java
L34|    @PostMapping("/helloworld")
L35|    public ApiResponse<HelloWorldDTO> helloWorld(@RequestBody(required = false) HelloWorldRequest request) {
L36|        String name = (request != null) ? request.getName() : null;
L37|        long startTime = System.currentTimeMillis();
L38|        HelloWorldDTO result = demoService.helloWorld(name);
L39|        long duration = System.currentTimeMillis() - startTime;
L40|        analyticsService.recordCallAsync("helloworld", name, duration); // 问题：未传递 caller 信息
L41|        return ApiResponse.success(result);
L42|    }
```

### P0-2: 结果缓存为全局单例，违反会话隔离设计

- **P0** `G6` `src/main/java/com/dtcode/demo/demo/service/impl/DemoServiceImpl.java:40` — resultCache 为 @Service 单例的实例字段，所有用户共享同一缓存。design §5.2.3.1 并发控制明确要求"各用户独立会话缓存"。多用户并发时，User A 的导出结果会被 User B 的执行结果覆盖。

  片段范围：`DemoServiceImpl.java:38-53`

```java
L38|    /**
L39|     * 最近执行结果缓存（按接口类型缓存，用于导出）
L40|     */
L41|    private final ConcurrentHashMap<String, Object> resultCache = new ConcurrentHashMap<>();
L42|    // 问题：全局单例缓存，无 session/userId 维度隔离
L43|
L44|    public static final String CACHE_KEY_HELLOWORLD = "helloworld";
L45|    public static final String CACHE_KEY_HASH = "hash";
L46|    public static final String CACHE_KEY_BUBBLE_SORT = "bubble-sort";
L47|
L48|    @Override
L49|    public HelloWorldDTO helloWorld(String name) {
L50|        String actualName = (name == null || name.trim().isEmpty()) ? DEFAULT_NAME : name.trim();
L51|        String result = "Hello, " + actualName + "!";
L52|        String timestamp = LocalDateTime.now().format(FORMATTER);
L53|        HelloWorldDTO dto = new HelloWorldDTO(result, timestamp);
L54|        resultCache.put(CACHE_KEY_HELLOWORLD, dto); // 问题：所有用户共享同一 key，后者覆盖前者
L55|        return dto;
L56|    }
```

### P1-1: AnalyticsController 直接注入实现类

- **P1** `G10+可读性` `src/main/java/com/dtcode/demo/analytics/api/controller/AnalyticsController.java:22-26` — Controller 直接依赖 AnalyticsServiceImpl 而非 AnalyticsService 接口。getSummary/getTrend/getDistribution 方法仅在 Impl 类上定义，不在接口中，违反依赖倒置原则。

  片段范围：`AnalyticsController.java:20-27`

```java
L20|public class AnalyticsController {
L21|
L22|    private final AnalyticsServiceImpl analyticsServiceImpl;  // 问题：依赖实现类
L23|
L24|    public AnalyticsController(AnalyticsServiceImpl analyticsServiceImpl) {
L25|        this.analyticsServiceImpl = analyticsServiceImpl;
L26|    }
```

### P1-2: ExportServiceImpl 直接注入 DemoServiceImpl

- **P1** `G10+可读性` `src/main/java/com/dtcode/demo/export/service/impl/ExportServiceImpl.java:28-32` — 直接依赖 DemoServiceImpl 而非 DemoService 接口。getCachedResult() 方法仅在 Impl 类上定义。

  片段范围：`ExportServiceImpl.java:28-32`

```java
L28|    private final DemoServiceImpl demoServiceImpl;  // 问题：依赖实现类
L29|
L30|    public ExportServiceImpl(DemoServiceImpl demoServiceImpl) {
L31|        this.demoServiceImpl = demoServiceImpl;
L32|    }
```

### P1-3: AnalyticsService 接口缺少查询方法声明

- **P1** `G10` `src/main/java/com/dtcode/demo/analytics/service/AnalyticsService.java:8-18` — 接口仅声明 recordCallAsync，缺少 getSummary/getTrend/getDistribution 方法定义，导致 Controller 被迫依赖 Impl。

  片段范围：`AnalyticsService.java:8-18`

```java
L8|public interface AnalyticsService {
L9|
L10|    /**
L11|     * 异步记录接口调用埋点
L12|     *
L13|     * @param apiName      接口名称
L14|     * @param requestParams 请求参数快照
L15|     * @param durationMs   调用耗时（毫秒）
L16|     */
L17|    void recordCallAsync(String apiName, String requestParams, long durationMs);
L18|    // 问题：缺少 getSummary/getTrend/getDistribution 方法声明
```

### P1-4: LocalDateTime.now() 未指定时区

- **P1** `M016` `src/main/java/com/dtcode/demo/demo/service/impl/DemoServiceImpl.java:50,65,104` + `ExportController.java:34,41,48` — 6 处 LocalDateTime.now() 使用系统默认时区，未显式指定。若部署环境时区与预期不一致将导致时间戳偏差。

  片段范围：`DemoServiceImpl.java:49-51`

```java
L49|        String result = "Hello, " + actualName + "!";
L50|        String timestamp = LocalDateTime.now().format(FORMATTER); // 问题：未指定时区
L51|        HelloWorldDTO dto = new HelloWorldDTO(result, timestamp);
```

### P1-5: 数据库密码硬编码

- **P1** `S9.1` `src/main/resources/application.yml:8` — 数据库密码 "root" 硬编码在配置文件中，应从配置中心或环境变量获取。

  片段范围：`application.yml:5-9`

```yaml
L5|  datasource:
L6|    url: jdbc:mysql://localhost:3306/demo_platform?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
L7|    username: root
L8|    password: root    # 问题：硬编码密码
L9|    driver-class-name: com.mysql.cj.jdbc.Driver
```

### P2-1: 行宽超限

- **P2** `A3.4` `src/main/java/com/dtcode/demo/demo/model/dto/HashDTO.java:59` — toString() 方法行宽超 120 字符。

  片段范围：`HashDTO.java:57-61`

```java
L57|    @Override
L58|    public String toString() {
L59|        return "HashDTO{input='" + input + "', algorithm='" + algorithm + "', hashValue='" + hashValue + "', timestamp='" + timestamp + "'}";
L60|        // 问题：行宽超过 120 字符
L61|    }
```

### P2-2: 使用 java.util.Date 旧 API

- **P2** `I004` `src/main/java/com/dtcode/demo/analytics/service/impl/AnalyticsServiceImpl.java:178` — 使用 `new Date()` 旧 API，建议改用 java.time 包。

  片段范围：`AnalyticsServiceImpl.java:172-180`

```java
L172|    private Date[] parseDateRange(String startDate, String endDate) {
L173|        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
L174|        try {
L175|            Date start;
L176|            Date end;
L177|            if (endDate == null || endDate.trim().isEmpty()) {
L178|                end = new Date();  // 问题：使用旧 Date API
L179|            } else {
L180|                end = sdf.parse(endDate);
```

### P2-3: ParseException 未链入 cause

- **P2** `G16.2` `src/main/java/com/dtcode/demo/analytics/service/impl/AnalyticsServiceImpl.java:191-193` — catch ParseException 后抛出 BusinessException 但未传入原始异常 cause，丢失异常链路。

  片段范围：`AnalyticsServiceImpl.java:189-194`

```java
L189|                start = sdf.parse(startDate);
L190|            }
L191|            return new Date[]{start, end};
L192|        } catch (ParseException e) {
L193|            throw new BusinessException("ANALYTICS_002", "日期格式无效，请使用 yyyy-MM-dd 格式");
L194|            // 问题：未链入原始 ParseException cause，应为 throw new BusinessException("...", "...", e)
```

---

## 8. 修复任务列表

### P0

- [ ] **P0** `src/main/java/com/dtcode/demo/analytics/service/impl/AnalyticsServiceImpl.java:49` — 扩展 recordCallAsync 方法签名，增加 callerId/callerName/callerType/callerLevel/callerDept 参数（或在 Controller 层从请求上下文解析后传入），确保埋点维度数据可采集
- [ ] **P0** `src/main/java/com/dtcode/demo/analytics/service/AnalyticsService.java:17` — 同步更新 AnalyticsService 接口的 recordCallAsync 方法签名
- [ ] **P0** `src/main/java/com/dtcode/demo/demo/api/controller/DemoController.java:40` — DemoController 三个接口调用 recordCallAsync 时传入从请求上下文解析的 caller 信息
- [ ] **P0** `src/main/java/com/dtcode/demo/demo/service/impl/DemoServiceImpl.java:40` — 将 resultCache 改为按会话/用户维度隔离（如使用 ThreadLocal + sessionId 作为 key，或改为在导出时接收前端传入的最近执行结果），满足 design §5.2.3.1 "各用户独立会话缓存"

### P1

- [ ] **P1** `src/main/java/com/dtcode/demo/analytics/service/AnalyticsService.java` — 在接口中补充 getSummary/getTrend/getDistribution 方法声明
- [ ] **P1** `src/main/java/com/dtcode/demo/analytics/api/controller/AnalyticsController.java:22` — 将注入类型从 AnalyticsServiceImpl 改为 AnalyticsService 接口
- [ ] **P1** `src/main/java/com/dtcode/demo/export/service/impl/ExportServiceImpl.java:28` — 将注入类型从 DemoServiceImpl 改为 DemoService 接口，并在 DemoService 接口中声明 getCachedResult 方法
- [ ] **P1** `src/main/java/com/dtcode/demo/demo/service/impl/DemoServiceImpl.java:50` — 所有 LocalDateTime.now() 调用增加显式时区参数，如 `LocalDateTime.now(ZoneId.of("Asia/Shanghai"))`
- [ ] **P1** `src/main/resources/application.yml:8` — 将数据库密码改为环境变量引用，如 `${DB_PASSWORD:root}`

### P2（可选）

- [ ] **P2** `src/main/java/com/dtcode/demo/demo/model/dto/HashDTO.java:59` — 将 toString() 方法拆行，控制行宽 ≤ 120 字符
- [ ] **P2** `src/main/java/com/dtcode/demo/analytics/service/impl/AnalyticsServiceImpl.java:178` — 将 java.util.Date 替换为 java.time.LocalDate/Instant
- [ ] **P2** `src/main/java/com/dtcode/demo/analytics/service/impl/AnalyticsServiceImpl.java:193` — BusinessException 构造时传入原始 ParseException 作为 cause：`throw new BusinessException("ANALYTICS_002", "...", e)`
