# Code Review Report

> **Change** 任务调度平台建设 · **分支/Commit** `AI/task-DEV-9d10e310-7901-11f1-8a9f-59ecae612580-c93199de-5a80-44db-bb05-78621638e92c` / `HEAD` · **日期** 2026-08-20 · **审查者** AI

> **AI**：等级 **P0 / P1 / P2**；G/S 以 checklist 行内定义为准；Bug 模式映射：Blocker→P0、Major→P1、Info→P2。

---

## §1 审查范围 (Scope)

### 1.1 仓库

| 仓库 | 路径 | 分支 |
|------|------|------|
| testDj (调度中心) | `testDj-main` | `AI/task-DEV-...` |
| testDJnew (执行器SDK) | `testDJnew-main` | `AI/task-DEV-...` |

### 1.2 变更文件统计

总计 **45 个文件**（5263 行新增），其中 Java 源文件 **32 个**，配置文件/资源文件 **13 个**。

### 1.3 Java 文件执行队列

| # | 仓库 | 文件路径 | 行数 | 审查状态 |
|---|------|----------|------|----------|
| 1 | testDj | `scheduler-service/.../SchedulerApplication.java` | 11 | ✅ 已审 |
| 2 | testDj | `scheduler-service/.../alert/AlertChannel.java` | 7 | ✅ 已审 |
| 3 | testDj | `scheduler-service/.../alert/AlertService.java` | 43 | ✅ 已审 |
| 4 | testDj | `scheduler-service/.../alert/DingTalkAlertSender.java` | 26 | ✅ 已审 |
| 5 | testDj | `scheduler-service/.../alert/MailAlertSender.java` | 23 | ✅ 已审 |
| 6 | testDj | `scheduler-service/.../config/QuartzConfig.java` | 49 | ⚠️ 已审有问题 |
| 7 | testDj | `scheduler-service/.../config/XXLJobConfig.java` | 32 | ✅ 已审 |
| 8 | testDj | `scheduler-service/.../controller/JobController.java` | 70 | ⚠️ 已审有问题 |
| 9 | testDj | `scheduler-service/.../dto/JobInfoDTO.java` | 38 | ✅ 已审 |
| 10 | testDj | `scheduler-service/.../dto/PageQuery.java` | 20 | ✅ 已审 |
| 11 | testDj | `scheduler-service/.../dto/PageResult.java` | 26 | ✅ 已审 |
| 12 | testDj | `scheduler-service/.../entity/JobInfo.java` | 51 | ⚠️ 已审有问题 |
| 13 | testDj | `scheduler-service/.../entity/JobLog.java` | 38 | ⚠️ 已审有问题 |
| 14 | testDj | `scheduler-service/.../mapper/JobInfoMapper.java` | 9 | ✅ 已审 |
| 15 | testDj | `scheduler-service/.../mapper/JobLogMapper.java` | 9 | ✅ 已审 |
| 16 | testDj | `scheduler-service/.../mq/SchedulerMQConsumer.java` | 54 | ⚠️ 已审有问题 |
| 17 | testDj | `scheduler-service/.../mq/SchedulerMQProducer.java` | 67 | ⚠️ 已审有问题 |
| 18 | testDj | `scheduler-service/.../retry/RetryService.java` | 34 | ⚠️ 已审有问题 |
| 19 | testDj | `scheduler-service/.../retry/RetryStrategy.java` | 6 | ✅ 已审 |
| 20 | testDj | `scheduler-service/.../schedule/ApplicationContextProvider.java` | 21 | ⚠️ 已审有问题 |
| 21 | testDj | `scheduler-service/.../schedule/DispatchJob.java` | 37 | ⚠️ 已审有问题 |
| 22 | testDj | `scheduler-service/.../schedule/DynamicScheduler.java` | 58 | ⚠️ 已审有问题 |
| 23 | testDj | `scheduler-service/.../schedule/ScheduleService.java` | 44 | ✅ 已审 |
| 24 | testDj | `scheduler-service/.../service/JobService.java` | 17 | ✅ 已审 |
| 25 | testDj | `scheduler-service/.../service/impl/JobServiceImpl.java` | 119 | ⚠️ 已审有问题 |
| 26 | testDJnew | `executor-sdk/.../ExecutorApplication.java` | 11 | ✅ 已审 |
| 27 | testDJnew | `executor-sdk/.../config/XXLJobExecutorConfig.java` | 32 | ✅ 已审 |
| 28 | testDJnew | `executor-sdk/.../handler/AbstractJobHandler.java` | 15 | ✅ 已审 |
| 29 | testDJnew | `executor-sdk/.../handler/JobHandler.java` | 6 | ✅ 已审 |
| 30 | testDJnew | `executor-sdk/.../handler/JobHandlerExecutor.java` | 49 | ⚠️ 已审有问题 |
| 31 | testDJnew | `executor-sdk/.../mq/ExecutorMQConsumer.java` | 45 | ✅ 已审 |
| 32 | testDJnew | `executor-sdk/.../mq/ExecutorMQProducer.java` | 54 | ⚠️ 已审有问题 |

---

## §2 Step 1 — 自动化预扫结果 (scan-all-rules.sh)

**扫描命令**: `bash scan-all-rules.sh <scheduler-dir> <executor-dir>`

**引擎**: ripgrep | **52/222 规则扫描**

**扫描结果汇总**:

| 严重性 | 数量 | 规则 ID |
|--------|------|---------|
| **P0** | 5 | G16.2 (CatchWithoutLogging) x5 |
| **P1** | 3 | M007 (EmptyCatch), M016 (JavaTimeDefaultTimeZone) x2 |
| **P2** | 5 | A2.2 (WildcardImport) x4, A3.4 (LineWidthExceeded) |

**详细发现**:

| # | 等级 | 规则 | 文件 | 行号 | 说明 |
|---|------|------|------|------|------|
| S01 | P0 | G16.2 | `JobHandlerExecutor.java` | 44 | catch块仅log.error，但未记录完整异常堆栈 |
| S02 | P0 | G16.2 | `QuartzConfig.java` | 45 | catch IOException 使用 RuntimeException 包装，原始异常已通过 cause 参数保留（`throw new RuntimeException(msg, e)`），非误报 |
| S03 | P0 | G16.2 | `DynamicScheduler.java` | 44 | catch SchedulerException 已传递异常对象作为参数（`log.error("...", e)`），SLF4J 会打印完整堆栈。⚠️ 疑似误报，需确认 |
| S04 | P0 | G16.2 | `DynamicScheduler.java` | 54 | catch SchedulerException 已传递异常对象作为参数（`log.error("...", e)`），SLF4J 会打印完整堆栈。⚠️ 疑似误报，需确认 |
| S05 | P0 | G16.2 | `JobServiceImpl.java` | 110 | catch NumberFormatException 为空，完全忽略 |
| S06 | P1 | M007 | `JobServiceImpl.java` | 110 | 空catch块（吞异常） |
| S07 | P1 | M016 | `ExecutorMQProducer.java` | 21 | LocalDateTime.now() 使用默认时区，应显式指定时区 |
| S08 | P1 | M016 | `DispatchJob.java` | 34 | LocalDateTime.now() 使用默认时区，应显式指定时区 |
| S09 | P2 | A2.2 | `JobController.java` | 9 | 通配符导入 `import org.springframework.web.bind.annotation.*` |
| S10 | P2 | A2.2 | `JobInfo.java` | 3 | 通配符导入 `import com.baomidou.mybatisplus.annotation.*` |
| S11 | P2 | A2.2 | `JobLog.java` | 3 | 通配符导入 `import com.baomidou.mybatisplus.annotation.*` |
| S12 | P2 | A2.2 | `DynamicScheduler.java` | 4 | 通配符导入 `import org.quartz.*` |
| S13 | P2 | A3.4 | `SchedulerMQProducer.java` | 48 | 行超长（JobDispatchMessage 构造方法参数列表） |

---

## §3 Step 2 — 功能性检查 (REQ)

### 功能点清单

| REQ | 功能点 | 来源 | 关联文件 | 是否符合 | 证据 |
|-----|--------|------|----------|----------|------|
| **F01** | Cron 表达式支持 | DIMA 1.2 / Design §5.2.3.1 | `DynamicScheduler.java`, `QuartzConfig.java`, `DispatchJob.java` | ✅ 符合 | Quartz CronScheduleBuilder 实现精确到秒级调度 |
| **F02** | 任务管理（增删改查） | DIMA 1.2 / Design §5.1.2 | `JobController.java`, `JobServiceImpl.java`, `JobInfoMapper.java` | ⚠️ 部分符合 | 缺少"分类管理"功能（jobGroup 仅作为查询筛选字段，无独立分组管理接口和 JobGroup 实体） |
| **F03** | 失败重试 | DIMA 1.2 / Design §5.3 | `RetryService.java`, `SchedulerMQProducer.java` | ⚠️ 部分符合 | 实现了 shouldRetry/computeNextRetryDelay 判断逻辑，但 scheduleRetry 仅打印日志，未实际投递重试消息到 MQ；缺少 Redis 计数器实现 |
| **F04** | 手动执行 | DIMA 1.2 / Design §5.1.3.3 | `JobController.java`, `JobServiceImpl.java` | ❌ 不符合 | `triggerJob()` 方法直接返回 `true`，未执行任何实际触发逻辑（未验证任务存在性、状态检查、未调用MQ分发） |
| **F05** | 暂停/恢复 | DIMA 1.2 / Design §5.1.3.2 | `JobController.java`, `JobServiceImpl.java` | ⚠️ 部分符合 | 实现了数据库状态更新，但暂停/恢复时未同步更新 Quartz 调度器中的任务（未调用 DynamicScheduler 的注册/注销） |
| **F06** | 执行日志 | DIMA 1.2 / Design §5.5 | `SchedulerMQConsumer.java`, `JobLogMapper.java`, `JobServiceImpl.java` | ⚠️ 部分符合 | 实现了回调消费插入日志，但缺少"每次任务执行生成一条初始记录（状态=运行中）"的日志初始化逻辑；缺少日志归档/清理策略实现 |
| **F07** | 告警通知 | DIMA 1.2 / Design §5.4 | `AlertService.java`, `DingTalkAlertSender.java`, `MailAlertSender.java` | ⚠️ 部分符合 | 实现了通知逻辑，但 DingTalk/Mail sender 都仅打日志未实际发送 HTTP/SMTP 请求；缺少企微(WECOM)渠道实现（AlertChannel 定义了但未使用） |
| **F08** | 分布式调度 | DIMA 3.2 / Design §2 | `ScheduleService.java`, `DynamicScheduler.java` | ⚠️ 部分符合 | Quartz 集群配置可用，但未实现 Redis 分布式锁防重复调度的逻辑 |
| **F09** | 调度与执行解耦（MQ） | DIMA 3.2 / Design §5.2.3.2 | `SchedulerMQProducer.java`, `ExecutorMQConsumer.java`, `ExecutorMQProducer.java` | ✅ 符合 | 通过 RocketMQ 消息队列实现调度→执行→回调的完整异步解耦 |
| **F10** | 管理界面 | 前端需求 | `JobList.vue`, `JobForm.vue`, `JobLog.vue` | ✅ 符合 | Vue 前端实现了任务列表、任务配置、日志查看页面 |

### P0 详细证据

**REQ-F04 (手动执行) — P0 阻塞**

- **Spec 证据**: Design §5.1.3.3 时序图明确要求 `triggerJob` → 查询任务信息 → `dispatchJob` 到 MQ
- **代码证据**: `JobServiceImpl.java:56-58` — `triggerJob()` 方法体直接 `return true`，完全未实现触发逻辑
- **影响**: 手动触发功能完全不可用

**REQ-F02 (分类管理) — P1 推荐**

- **Spec 证据**: Design §1 核心功能-任务管理 包含"分类管理"；Design §3 实体清单包含 JobGroup 实体
- **代码证据**: 未找到 JobGroup 实体或分组管理接口，jobGroup 仅作为 JobInfo 字段存在

**REQ-F03 (失败重试未完成) — P1 推荐**

- **Spec 证据**: Design §5.3.3 时序图要求 scheduleRetry 通过 MQ 延迟投递重试消息
- **代码证据**: `RetryService.java:26-33` — scheduleRetry 仅打印日志，未实际调用 MQ 发送重试消息

**REQ-F05 (暂停/恢复未同步调度器) — P1 推荐**

- **Spec 证据**: Design §5.1.3.2 状态机要求暂停时"清除调度计划"，恢复时"重新注册调度计划"
- **代码证据**: `JobServiceImpl.java:62-76` — pauseJob/resumeJob 仅更新数据库状态，未调用 `DynamicScheduler` 的 `unregisterJob`/`registerJob`

---

## §4 Step 3 — 可读性检查 (Readability)

### A1 源文件格式

| 检查项 | 状态 | 备注 |
|--------|------|------|
| A1.1 文件编码 UTF-8 | ✅ 全部通过 | 所有文件无 BOM，UTF-8 编码 |
| A1.2 行尾统一 LF | ✅ 全部通过 | 统一 LF 换行 |
| A1.3 文件末尾空行 | ✅ 全部通过 | 所有文件末尾有空行 |

### A2 导入与包结构

| 检查项 | 状态 | 发现 |
|--------|------|------|
| A2.1 无未使用的导入 | ✅ 通过 | 未发现未使用导入 |
| A2.2 禁止通配符导入 | ⚠️ 发现4处 | `JobController.java:9`, `JobInfo.java:3`, `JobLog.java:3`, `DynamicScheduler.java:4` — 使用 `*` 通配符导入 |
| A2.3 导入顺序规范 | ✅ 通过 | 分组合理，静态导入在后 |

### A3 命名与格式

| 检查项 | 状态 | 发现 |
|--------|------|------|
| A3.1 类名 UpperCamelCase | ✅ 通过 | 所有类名符合规范 |
| A3.2 方法名 lowerCamelCase | ✅ 通过 | 所有方法名符合规范 |
| A3.3 常量 UPPER_SNAKE_CASE | ✅ 通过 | 常量命名规范 |
| A3.4 行宽 ≤ 120 字符 | ⚠️ 发现1处 | `SchedulerMQProducer.java:48` 构造方法参数列表超长 |
| A3.5 缩进 4 空格 | ✅ 通过 | 统一 4 空格缩进 |

### A4 注释与文档

| 检查项 | 状态 | 备注 |
|--------|------|------|
| A4.1 类/方法有 Javadoc | ⚠️ 部分缺失 | 核心接口 `JobService`、`JobHandler` 缺少 Javadoc 说明 |
| A4.2 复杂逻辑有行注释 | ⚠️ 部分缺失 | `RetryService.computeNextRetryDelay` 指数退避公式缺少注释 |

### A5 代码结构

| 检查项 | 状态 | 备注 |
|--------|------|------|
| A5.1 方法体不超过 80 行 | ✅ 通过 | 所有方法体控制在合理长度 |
| A5.2 类内聚性合理 | ✅ 通过 | 各模块职责清晰 |

### A6 集合与泛型

| 检查项 | 状态 | 备注 |
|--------|------|------|
| A6.1 泛型类型安全 | ✅ 通过 | 泛型使用正确，无 raw type |
| A6.2 集合初始化合理 | ✅ 通过 | 使用 `Map.of` 等工厂方法 |

### A7 异常与日志

| 检查项 | 状态 | 备注 |
|--------|------|------|
| A7.1 异常信息完整 | ⚠️ 发现5处 | 见扫描结果 G16.2 — catch 块缺少异常堆栈参数 |
| A7.2 日志级别合理 | ✅ 通过 | 日志级别使用正确 |

---

## §5 Step 4 — 可靠性检查 (Reliability)

### G 可靠性 (军规)

| ID | 检查项 | 状态 | 发现 |
|----|--------|------|------|
| G1 | 并发控制 | ⚠️ | `DispatchJob` 使用 `@DisallowConcurrentExecution` 防止同一任务并发执行，但缺少 Redis 分布式锁防多节点重复调度 |
| G2 | 线程池管理 | ✅ | Quartz 配置 10 线程池，基本合理 |
| G3 | 资源释放 | ✅ | 无直接 IO/网络资源操作，Spring 管理生命周期 |
| G4 | 超时控制 | ❌ | **缺失** — 设计规格要求超时中断并上报失败，但代码中未实现任何超时控制机制 |
| G5 | 幂等设计 | ❌ | **缺失** — MQ 消费者未实现幂等消费，重复消息可能导致重复插入 JobLog |
| G6 | 事务边界 | ✅ | `@Transactional` 标注在 `addJob`/`updateJob`/`deleteJob`/`pauseJob`/`resumeJob` 上 |
| G7 | 降级/熔断 | ⚠️ | Redis 不可用时缺少降级策略（设计规格要求降级为无锁模式） |
| G8 | 数据一致性 | ⚠️ | `SchedulerMQConsumer.onMessage` 插入 JobLog 时未校验 `jobId` 是否存在 |
| G9 | 边界条件 | ⚠️ | `PageQuery.page` 默认 1，`size` 默认 10，但缺少最大值限制（防止 SQL 注入/全表扫描） |
| G10 | 监控/日志 | ✅ | 关键路径均有日志记录 |
| G11 | 重试/容错 | ⚠️ | `SchedulerMQProducer.dispatchJob` 未实现 MQ 发送失败重试 |
| G12 | 配置管理 | ✅ | 配置外化到 yml 和 properties |
| G13 | 优雅关闭 | ✅ | Spring Boot 默认支持 |
| G14 | 测试/验证 | ⚠️ | 缺少单元测试 |
| G15 | 缓存/过期 | ⚠️ | 设计规格中 Redis 缓存任务状态未实现 |
| G16 | 异常处理 | ⚠️ | 见扫描结果 — 多处 catch 缺少异常堆栈日志，`JobServiceImpl.java:110` 有空 catch 块 |

### S 安全

| ID | 检查项 | 状态 | 发现 |
|----|--------|------|------|
| S1 | SQL 注入 | ✅ | 使用 MyBatis-Plus 参数化查询，无拼接 SQL |
| S2 | 认证/授权 | ⚠️ | 未实现任何认证/鉴权拦截器（设计规格说明与假设一致，但需确认） |
| S3 | 输入校验 | ⚠️ | Controller 层缺少参数校验（如 Cron 表达式格式校验、必填项校验） |
| S4 | 密钥泄露 | ✅ | 数据库密码等配置在 yml 中，生产环境应外部化 |
| S5 | 敏感数据 | ⚠️ | `executorParam` 可能包含敏感信息，未加密存储也未脱敏 |

### B/M/I — Bug 模式 (脚本扫描 + LLM 补扫)

**脚本已覆盖项**（见 §2 扫描结果）：

| 规则 | 等级 | 位置 | 说明 |
|------|------|------|------|
| M007 EmptyCatch | P1 | `JobServiceImpl.java:110` | 空 catch 块吞异常 |
| M016 JavaTimeDefaultTimeZone | P1 | `ExecutorMQProducer.java:21`, `DispatchJob.java:34` | 使用默认时区 |

**LLM 补扫发现**：

| 规则 | 等级 | 位置 | 说明 |
|------|------|------|------|
| B001 — 返回空集合而非 null | P0 | `JobServiceImpl.java:57` | `triggerJob` 返回 false 但无任何异常信息告知调用方 |
| B002 — 使用 equals 比较对象 | P1 | `JobServiceImpl.java:81` | `query.getKeyword().isEmpty()` 在 keyword 为 null 时可能 NPE（已通过 if 前置判断规避） |
| B003 — 事务未处理异常 | P1 | `JobServiceImpl.java:33` | `@Transactional` 默认只回滚 RuntimeException，需确认是否要捕获 checked exception |
| B004 — 静态注入 Spring Bean | P1 | `ApplicationContextProvider.java:11` | 静态字段持有 ApplicationContext，单元测试时不易 mock |
| B005 — 消息体未序列化配置 | P1 | `SchedulerMQProducer.java:34` | 使用 `MessageBuilder.withPayload(message)` 但未明确配置 JSON 序列化器，可能导致 RocketMQ 序列化异常 |
| **B006 — 编译错误: 引用不存在内部类** | **P0** | **`SchedulerMQConsumer.java:15`** | `implements RocketMQListener<SchedulerMQProducer.JobCallbackMessage>` 引用了 `SchedulerMQProducer.JobCallbackMessage`，但 `SchedulerMQProducer` 中仅定义了 `JobDispatchMessage`，`JobCallbackMessage` 实际定义在 `SchedulerMQConsumer` 自身。**会导致编译失败** |

---

## §6 Step 5 — 自定义扩展检查

N/A(未启用自定义规则)

[自定义检查清单文件为空或仅为示例项]

---

## §7 跨仓接口对齐检查

### 7.1 MQ 消息契约对齐

| 消息类型 | 字段 | testDj (调度中心) | testDJnew (执行器) | 对齐状态 |
|----------|------|-------------------|-------------------|----------|
| `scheduler-job-dispatch` | jobId | `SchedulerMQProducer.JobDispatchMessage` (Long) | `ExecutorMQConsumer.JobDispatchMessage` (Long) | ✅ 对齐 |
| `scheduler-job-dispatch` | jobHandler | `SchedulerMQProducer.JobDispatchMessage` (String) | `ExecutorMQConsumer.JobDispatchMessage` (String) | ✅ 对齐 |
| `scheduler-job-dispatch` | executorParam | `SchedulerMQProducer.JobDispatchMessage` (String) | `ExecutorMQConsumer.JobDispatchMessage` (String) | ✅ 对齐 |
| `scheduler-job-dispatch` | triggerTime | `SchedulerMQProducer.JobDispatchMessage` (String) | `ExecutorMQConsumer.JobDispatchMessage` (String) | ✅ 对齐 |
| `scheduler-job-dispatch` | traceId | `SchedulerMQProducer.JobDispatchMessage` (String) | `ExecutorMQConsumer.JobDispatchMessage` (String) | ✅ 对齐 |
| `scheduler-job-callback` | jobId | `SchedulerMQConsumer.JobCallbackMessage` (Long) | `ExecutorMQProducer.JobCallbackMessage` (Long) | ✅ 对齐 |
| `scheduler-job-callback` | traceId | `SchedulerMQConsumer.JobCallbackMessage` (String) | `ExecutorMQProducer.JobCallbackMessage` (String) | ✅ 对齐 |
| `scheduler-job-callback` | status | `SchedulerMQConsumer.JobCallbackMessage` (Integer) | `ExecutorMQProducer.JobCallbackMessage` (int) | ⚠️ 类型不一致 (Integer vs int) |
| `scheduler-job-callback` | result | `SchedulerMQConsumer.JobCallbackMessage` (String) | `ExecutorMQProducer.JobCallbackMessage` (String) | ✅ 对齐 |
| `scheduler-job-callback` | finishTime | `SchedulerMQConsumer.JobCallbackMessage` (LocalDateTime) | `ExecutorMQProducer.JobCallbackMessage` (LocalDateTime) | ✅ 对齐 |

### 7.2 结论

MQ 消息契约基本对齐，但 `status` 字段类型不一致（`Integer` vs `int`），在序列化/反序列化时可能存在兼容性问题，建议统一为 `Integer`。

---

## §8 修复任务列表

### P0 — 阻塞项（必须修复）

- [ ] **F04-01**: 实现 `JobServiceImpl.triggerJob()` 的实际触发逻辑（查询任务→校验状态→调用 MQ 分发）
- [ ] **G04-01**: 添加任务执行超时控制机制
- [ ] **G05-01**: 实现 MQ 消费者幂等消费（如基于 traceId 去重）
- [ ] **G16-01**: 修复 `JobServiceImpl.java:110` 空 catch 块，至少记录日志
- [ ] **G16-02**: 修复 `DynamicScheduler.java:44,54` 和 `QuartzConfig.java:45` 和 `JobHandlerExecutor.java:44` catch 缺少异常堆栈参数
- [ ] **B006-01**: 修复 `SchedulerMQConsumer.java:15` 编译错误 — 引用 `SchedulerMQProducer.JobCallbackMessage` 应改为 `SchedulerMQConsumer.JobCallbackMessage`（或直接使用 `JobCallbackMessage`）

### P1 — 推荐项（合并前应修复）

- [ ] **F02-01**: 补充 JobGroup 实体或分组管理接口
- [ ] **F03-01**: 实现 `RetryService.scheduleRetry()` 实际 MQ 重试消息投递逻辑
- [ ] **F05-01**: 暂停/恢复时同步调用 `DynamicScheduler` 的注册/注销方法
- [ ] **F06-01**: 实现调度触发时创建初始 JobLog 记录（状态=运行中）
- [ ] **F06-02**: 实现日志归档/清理策略
- [ ] **F07-01**: 实现 DingTalkAlertSender 和 MailAlertSender 的实际 HTTP/SMTP 发送逻辑
- [ ] **F07-02**: 实现企微 (WECOM) 告警渠道
- [ ] **F08-01**: 实现 Redis 分布式锁防重复调度
- [ ] **M016-01**: `LocalDateTime.now()` 改为显式指定时区 `LocalDateTime.now(ZoneId.of("Asia/Shanghai"))`
- [ ] **B005-01**: 配置 RocketMQ 消息 JSON 序列化器
- [ ] **S03-01**: 添加 Controller 层参数校验（@Valid/JSR303）
- [ ] **G09-01**: 添加分页参数最大值限制

### P2 — 参考项（可选改进）

- [ ] **A2.2-01**: 替换通配符导入为显式导入
- [ ] **A3.4-01**: 缩短 `SchedulerMQProducer.java:48` 超长行
- [ ] **A4.1-01**: 补充核心接口/类的 Javadoc
- [ ] **B004-01**: 考虑使用非静态方式获取 ApplicationContext

---

## §9 总结

### 总体评价

本次审查覆盖了 32 个 Java 文件，发现 **6 个 P0 阻塞问题**（手动触发功能未实现、缺少超时控制、缺少幂等消费、空catch块、catch缺少异常堆栈、**SchedulerMQConsumer 编译错误**），**12 个 P1 推荐问题**，**9 个 P2 参考问题**。

### 核心风险

1. **F04 手动执行功能完全不可用** — 典型的 Stub/Mock 代码未替换
2. **F03 重试机制未完成** — scheduleRetry 仅打印日志，实际未投递 MQ
3. **F05 暂停/恢复未同步调度器** — 状态更新与调度器不同步
4. **缺乏超时控制和幂等设计** — 对分布式系统可靠性有较大影响
5. **跨仓 status 字段类型不一致** — Integer vs int 的序列化兼容性风险
6. **B006 编译错误: SchedulerMQConsumer 引用了不存在的内部类** — 直接导致构建失败

### 整体结论

代码架构设计合理，模块划分清晰，MQ 消息契约基本对齐。但多个核心功能的实现存在 **Stub/Mock 代码未替换** 或 **缺少关键实现** 的问题，建议在合并前修复所有 P0 和 P1 项。

---

*报告生成时间: 2026-08-20 | 审查引擎: dtazziboot-java-code-review v1.1.0*