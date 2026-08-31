# Code Review Checklist

> **Change** `待办事项记录（创建）核心功能` · **分支/Commit** `AI/task-DEV-966dcd0a-7905-11f1-9649-3b4281182f10-4419f833-23d3-4e73-ae46-d7f04ff20392` / `a88867d9` · **日期** `2026-08-31`
>
> **AI**：唯一进度源；状态仅用 `⬜` `✅` `❌` `⚠️` `N/A`。
>
> **预扫**：`bash references/script/scan-all-rules.sh src/main/java/com/example/todo src/test/java/com/example/todo`
> 输出摘要：
> ```
> [P0] G16.2 — CatchWithoutLogging: src/main/java/com/example/todo/context/DefaultUserContext.java:31
> [P0] G16.2 — CatchWithoutLogging: src/main/java/com/example/todo/service/impl/TodoServiceImpl.java:57
> [P1] M016 — JavaTimeDefaultTimeZone: src/main/java/com/example/todo/service/impl/TodoServiceImpl.java:45
> Summary: 3 findings (P0=2, P1=1, P2=0) | 52/222 rules scanned
> ```
> 人工复核：
> - `TodoServiceImpl.java:57` 的 `catch (DataAccessException e)` 紧接 `logger.error(..., e)`（line 58-59），**已记录日志**，判定为**脚本误报**，不成立。
> - `DefaultUserContext.java:31` 的 `catch (NumberFormatException e){ return null; }` 静默吞异常无日志，**成立**；权威等级依 `reliability-checklist.md` G16.2 = **P1**。
> - `M016 TodoServiceImpl.java:45 LocalDateTime.now()` 成立，Major→P1。

---

## Step 1 — 执行队列（产物 A）

> 变更范围：`git diff` 为空（已提交），按 impl.md 实现清单确定本次变更文件。非 Java（yml/xml）标注 `跳过`（Step4 列统一 `跳过`）。

| # | 文件（仓库相对路径） | 归属原因 | Step2 | Step3 | G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | G9 | G10 | G11 | G12 | G13 | G14 | G15 | G16 | G17 | S1 | S2 | S3 | S4 | S5 | S6 | S7 | S8 | S9 | S10 | 总状态 |
|---|----------------------|----------|-------|-------|----|----|----|----|----|----|----|----|----|-----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|--------|
| 1 | `src/main/java/com/example/todo/TodoApplication.java` | 启动类 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 2 | `src/main/java/com/example/todo/config/WebConfig.java` | CORS | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ | ✅ | ⚠️ 已审 |
| 3 | `src/main/java/com/example/todo/context/UserContext.java` | creatorId 来源 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ | N/A | N/A | ⚠️ 已审 |
| 4 | `src/main/java/com/example/todo/context/DefaultUserContext.java` | X-User-Id 读取 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ | N/A | N/A | ⚠️ 已审 |
| 5 | `src/main/java/com/example/todo/controller/TodoController.java` | W01 入口 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ | N/A | ✅ | ✅ 已审 |
| 6 | `src/main/java/com/example/todo/dto/ApiResponse.java` | 通用出参 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 7 | `src/main/java/com/example/todo/dto/TodoCreateRequest.java` | 入参/校验 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 8 | `src/main/java/com/example/todo/dto/TodoCreateResult.java` | 出参 data | ✅ | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ 已审 |
| 9 | `src/main/java/com/example/todo/exception/GlobalExceptionHandler.java` | 异常→错误码 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ | N/A | N/A | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ 已审 |
| 10 | `src/main/java/com/example/todo/exception/TodoErrorCode.java` | 错误码枚举 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 11 | `src/main/java/com/example/todo/exception/TodoException.java` | 业务异常 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 12 | `src/main/java/com/example/todo/model/Todo.java` | 实体/表映射 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ 已审 |
| 13 | `src/main/java/com/example/todo/repository/TodoRepository.java` | 数据访问 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 14 | `src/main/java/com/example/todo/service/TodoService.java` | S01 接口 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 15 | `src/main/java/com/example/todo/service/impl/TodoServiceImpl.java` | 落库/组装 | ✅ | ⚠️ | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | ✅ | N/A | N/A | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ | N/A | N/A | ⚠️ 已审 |
| 16 | `src/test/java/com/example/todo/controller/TodoControllerTest.java` | 切片测试 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 17 | `src/test/java/com/example/todo/service/impl/TodoServiceImplTest.java` | 单测 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |

> 非 Java 文件（`pom.xml` / `application.yml` / `application-test.yml`）：跳过 Step3–Step5 的逐文件 Java 规则扫描；其中 `application.yml` 在 Step4 的 S9.1 / G15.1 维度单独核销（见 §4.2/§4.3 备注与 report）。

---

## Step 2 — 功能（产物 B）

| REQ | Scenario | Spec证据（原文/章节） | 关联文件 | 状态 | 代码证据（文件/测试/接口） |
|-----|----------|----------------------|----------|------|----------------------------|
| REQ-1 | Given 合法 name+description / When POST /api/todo / Then 落库并返回 id+name+description+creatorId+gmtCreate | design §5.2.2 W01、§5.2.3.1 F01："用户提交事项名称（必填）与描述（选填），系统持久化并返回待办事项ID与创建时间" | `controller/TodoController.java:34-37`、`service/impl/TodoServiceImpl.java:44-65`、`dto/TodoCreateResult.java` | ✅ | 正常路径单测 `TodoServiceImplTest.should_returnCreateResult_when_requestIsValid` + 切片测试 `TodoControllerTest.should_returnOk_when_requestIsValid` |
| REQ-2 | Given name 缺失/空白 / When 提交 / Then 返回 TODO_0001 | design §5.2.2 R01 / §5.2.3.1 R01："name 必填，去首尾空格后非空 → TODO_0001" | `dto/TodoCreateRequest.java:16-17`（`@NotBlank`）、`exception/GlobalExceptionHandler.java:99-118` | ✅ | 测试 `should_returnNameEmpty_when_nameIsBlank` 验证 result=TODO_0001 |
| REQ-3 | Given name 长度>200 / When 提交 / Then 返回 TODO_0002 | design R02 / §5.2.2："name 长度 1~200 字符 → TODO_0002" | `dto/TodoCreateRequest.java:17`（`@Size(max=200)`）、`GlobalExceptionHandler.java:110-112` | ✅ | 测试 `should_returnNameTooLong_when_nameExceeds200`（201 字符） |
| REQ-4 | Given description 长度>2000 / When 提交 / Then 返回 TODO_0003 | design R03 / §5.2.2："description 长度 0~2000 → TODO_0003" | `dto/TodoCreateRequest.java:21`（`@Size(max=2000)`）、`GlobalExceptionHandler.java:114-116` | ✅ | 测试 `should_returnDescriptionTooLong_when_descriptionExceeds2000`（2001 字符） |
| REQ-5 | Given 落库失败 / When DataAccessException / Then 返回 TODO_0004 并 ERROR 日志含堆栈 | design §5.2.3.1 异常场景："数据库写入失败 → TODO_0004，记录 ERROR 含堆栈" | `service/impl/TodoServiceImpl.java:55-61` | ✅ | 测试 `should_throwSystemError_when_repositoryThrowsDataAccessException` |
| REQ-6 | Given 已登录 / When 创建 / Then creator_id 取登录态用户ID | design A01/R04："creator_id 取登录态用户ID" | `context/UserContext.java`、`context/DefaultUserContext.java`、`service/impl/TodoServiceImpl.java:49` | ⚠️ | 实现从 `X-User-Id` 头读取，但**未接入鉴权拦截器**，该头可被客户端伪造（见 §4.3 S8.1） |
| REQ-7 | Given 创建 / When 持久化 / Then tenant_id 默认 0、gmt_create/gmt_modified 写入 | design §5.2.1.1、A02："tenant_id 默认 0；gmt_create/gmt_modified" | `model/Todo.java:44-53`、`service/impl/TodoServiceImpl.java:50-52` | ✅ | 测试 `should_persistTodoWithCorrectFields_when_requestIsValid` 校验 tenantId=0、时间字段非空 |
| REQ-8 | Given 出参 / When 序列化 / Then data.gmtCreate 为 `yyyy-MM-dd HH:mm:ss` | design §5.2.2 出参："data.gmtCreate \| DateTime \| 创建时间（yyyy-MM-dd HH:mm:ss）" | `dto/TodoCreateResult.java:26`、`application.yml` Jackson 配置 | ⚠️ | 直接返回 `LocalDateTime`，无 `@JsonFormat`，默认序列化为 ISO `2026-08-31T10:20:30`，与契约格式不符（见 report §3 REQ-8） |

---

## Step 3 — 可读性检查（产物 C）

| ID | 检查项 | 状态 | 备注（命中写 `path:line`） |
|----|--------|------|----------------------------|
| A1 | 源文件格式 | ✅ | 文件名=类名.java，UTF-8，未使用 Tab |
| A2 | 源文件结构/import 顺序 | ⚠️ | `service/impl/TodoServiceImpl.java:11-17` 中 `java.time.LocalDateTime` 排在 `org.springframework.*` 之后，非严格 ASCII 字典序（应为 com → java → org）；同类问题见于 `exception/GlobalExceptionHandler.java:13-15`（`org.springframework.web.method.annotation` 在 `org.springframework.web.bind.*` 之后不影响，但 java.* 置后）。P2 |
| A3 | 代码样式 | ✅ | K&R 大括号、4 空格缩进、行宽 ≤120 |
| A4 | 命名规范 | ✅ | 包名全小写、类名 UpperCamelCase、常量 UPPER_SNAKE_CASE（`DEFAULT_TENANT_ID`、`USER_ID_HEADER`） |
| A5 | 编码实践 | ✅ | 重写方法均加 `@Override`；catch 非空（DefaultUserContext 见 G16.2） |
| A6 | 特定元素样式 | ✅ | long 字面量用 `L`（`0L`、`88L`）；注解位置规范 |
| A7 | Javadoc 规范 | ✅ | public 类/方法均有 Javadoc；getter 简单方法依 A7.3 可省 |

---

## Step 4 — 可靠性检查（产物 D）

### 4.1 Bug 模式（`bug-pattern-checklist.md`）

> `scan-all-rules.sh` 已覆盖 52/222 条可程序化规则。下表对本次变更相关的 B/M/I 逐条核销；与本变更代码模式无关的条目标 `N/A`。

| 区间 | 状态 | 备注 |
|----|------|------|
| B001–B081（Blocker，81 条） | ✅ / N/A | 预扫无 B* 命中；人工复核：无 NPE 风险（`getFieldError()` 已判空 `GlobalExceptionHandler.java:101-103`）、无资源未释放（无流/连接/锁）、无并发先读后写（仅 INSERT）。`Todo.equals/hashCode` 基于 id 见 §4.2 G11 备注，按 Bug 模式归为 Info 级。 |
| M001–M027（Major，27 条） | ⚠️ | **命中 M016**：`service/impl/TodoServiceImpl.java:45` `LocalDateTime.now()` 依赖系统默认时区，应显式指定 ZoneId。其余 M* 无命中。 |
| I001–I010（Info，10 条） | ⚠️ | `model/Todo.java:124-139` JPA 实体 `equals/hashCode` 仅基于 `id`，未持久化实体 `id=null` 时存在 null-id 相等风险（Info→P2；当前未用于 HashSet/HashMap，低风险）。 |

> 逐条核销说明：B001–B081 中除上述已点名项外，逐条核对均无对应代码模式（无 `equals` 用 `==` 比较 String、无 `ThreadPool` 默认无界队列、无 `Random` 用于安全场景、无 `switch` 缺 default 命中枚举、无 `BigDecimal` 用 double 构造等），统一标 `N/A(无对应代码模式)`。M001–M027 中除 M016 外逐条无命中，标 `N/A`。I001–I010 中除实体 equals 项外，标 `N/A`。

### 4.2 可靠性（`reliability-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| G1.1 | N/A | 仅单条 INSERT，无事务内先读后写 |
| G1.2 | N/A | 无加锁更新 |
| G1.3 | N/A | 无乐观锁 |
| G1.4 | N/A | 无多资源加锁 |
| G2.1 | N/A | design §5.2.3.1 明确不引入幂等键（事项名称非唯一、内部低并发），属设计决策，非缺陷 |
| G2.2 | N/A | 无重试/MQ 重投 |
| G2.3 | N/A | 无幂等键约定 |
| G3.1 | N/A | 无分布式事务 |
| G3.2 | ✅ | `TodoServiceImpl.java:43-65` `@Transactional` 仅含 `todoRepository.save`，无外部 RPC/MQ/文件 I/O |
| G4.1 | N/A | 无复杂 SQL/存储过程 |
| G4.2 | N/A | 无 WHERE 函数/隐式转换 |
| G4.3 | N/A | 本期无查询/分页 |
| G5.1 | N/A | 无 MQ |
| G6.1 | N/A | 无缓存 |
| G6.2 | N/A | 无缓存双写 |
| G7.1 | N/A | 无调度任务 |
| G7.2 | N/A | 无调度任务 |
| G8.1 | ✅ | `TodoServiceImpl.java:55-61` 落库失败 catch 后 ERROR 日志 + 抛 TODO_0004（非吞异常） |
| G8.2 | N/A | 无非核心强依赖 |
| G8.3 | N/A | 无流/连接/锁需释放（JPA save 由 Spring 管理） |
| G8.4 | N/A | 无线程池/定时任务 |
| G8.5 | N/A | 无 ThreadLocal |
| G8.6 | N/A | 无 `Executors` 默认线程池 |
| G8.7 | N/A | （清单无此 ID，保留行） |
| G9.1 | N/A | 无外部 HTTP/RPC（DB 由连接池管理，三态由 DataAccessException 覆盖） |
| G9.2 | N/A | DB 连接超时由 HikariCP 默认配置，本次变更未显式设置但属容器默认；建议后续显式配置 |
| G9.3 | N/A | 无重试 |
| G10.1 | ✅ | `TodoCreateResult.creatorId=null` 仅表示未登录兜底（design A01 明确允许），未与"系统异常"混用 |
| G10.2 | N/A | 新增接口，无契约变更 |
| G10.3 | N/A | （清单无此 ID，保留行） |
| G11.1 | ✅ | 新逻辑有单测且含断言（8 个测试方法，AssertJ） |
| G11.2 | ⚠️ | 已覆盖 name 空白/超长、desc 超长、落库失败、creatorId=null；未覆盖 description=空字符串落库（design §6.3 边界）。P2 |
| G11.3 | N/A | 入参由 `@Valid` + `@NotBlank/@Size` 防御；service 不直接接收外部 null（Controller 校验先行） |
| G11.4 | N/A | 无数值运算/金额 |
| G12.1 | N/A | 非资金场景 |
| G12.2 | N/A | design §7.3 已设计特性开关 `todo.create.enabled`（代码未实现，属后续项） |
| G13.1 | ⚠️ | `GlobalExceptionHandler.java:75-77` `handleBiz` 对 `SYSTEM_ERROR`（落库失败，系统异常）打 WARN，与"系统异常 ERROR"不一致；service 已打 ERROR，存在重复且级别错配。P2 |
| G14.1 | N/A | 无金额 |
| G14.2 | ⚠️ | `TodoServiceImpl.java:49-50` `creatorId` 来自请求头 `X-User-Id`，属用户可控参数；`tenantId` 为常量 0（非用户可控）。creatorId 可伪造见 S8.1 |
| G14.3 | ⚠️ | `Todo.java:48-53` 时间以 `LocalDateTime` 存库（无时区）；design 单区内可接受，建议跨区时改 `Instant`/UTC。P2 |
| G14.4 | ⚠️ | 见 M016/G14.3，时间 API 未显式指定时区。P1 |
| G15.1 | ⚠️ | `application.yml:13` `ddl-auto: update` 生产环境不建议自动建表/改表；design 为新增表，向前兼容性 OK，但运行时 schema 漂移风险。P2 |
| G15.2 | N/A | 新增接口无旧接口共存 |
| G15.3 | N/A | 无不兼容逻辑切换 |
| G16.1 | ⚠️ | design §7.1 设计了接口监控点，代码未埋点（属非功能性后续项）；P2 |
| G16.2 | ⚠️ | **命中**：`context/DefaultUserContext.java:29-33` `catch (NumberFormatException e){ return null; }` 静默吞异常无日志/告警。P1（脚本误标 P0，权威等级 P1）。注：`TodoServiceImpl.java:57` 同条预扫为**误报**（已 `logger.error`） |
| G16.3 | ⚠️ | 见 G13.1，SYSTEM_ERROR 路径 WARN。P1 |
| G16.4 | ✅ | 无空 catch / 仅 printStackTrace；DefaultUserContext 见 G16.2 |
| G17.1 | ⚠️ | design §7.3 设计 `todo.create.enabled` 开关，代码未实现，无法紧急关闭。P1 |
| G17.2 | N/A | 无降级预案需求（最小闭环） |
| G17.3 | N/A | 数据变更为新增表，无回滚脚本需求 |
| G18.1 | N/A | （安全补强，S 节已覆盖） |
| G18.2 | N/A | （保留行） |
| G18.3 | N/A | （保留行） |

### 4.3 安全（`security-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| S1.1 | ✅ | 使用 Spring Data JPA 参数化绑定，无字符串拼 SQL |
| S1.2 | N/A | 无 order by/动态表名 |
| S1.3 | N/A | 无 like/in 查询 |
| S2.1 | ✅ | JSON 输出由 Jackson 序列化，无手拼 HTML/JS |
| S2.2 | N/A | 无富文本 |
| S2.3 | N/A | 无模板引擎 |
| S3.1 | N/A | 无外部 URL 请求 |
| S3.2 | N/A | 无跳转 |
| S3.3 | N/A | 无外部调用超时 |
| S4.1 | N/A | 无命令执行 |
| S4.2 | N/A | 无外部命令 |
| S5.1 | N/A | 无 XML 解析 |
| S5.2 | N/A | 无 XPath |
| S6.1 | N/A | 无反序列化外部数据 |
| S6.2 | N/A | 无多态反序列化 |
| S6.3 | N/A | 无敏感字段 |
| S7.1 | N/A | 无文件上传 |
| S7.2 | N/A | 无路径拼接 |
| S7.3 | N/A | 无文件存储 |
| S8.1 | ❌ | **命中**：`POST /api/todo` 未接入鉴权拦截器；`DefaultUserContext.java:25` 直接读取客户端可控的 `X-User-Id` 头作为 `creatorId`，无任何校验。design §6.4.2/A05 假设由"全局拦截器"校验，但代码未实现该拦截器，任何人可伪造 creatorId 创建待办。**P0** |
| S8.2 | ✅ | 新增走 POST，非 GET 增删改 |
| S8.3 | N/A | id 自增（内部场景，design 未要求不可预测） |
| S8.4 | N/A | 无 Cookie |
| S9.1 | ⚠️ | **命中**：`application.yml:9-10` 硬编码 DB 账号口令 `todo/todo`，未从配置中心/环境变量获取。P1（dev 占位值，建议外部化） |
| S9.2 | ✅ | 日志未记录敏感信息（name/description 为业务文本，design §6.4.3 非敏感） |
| S9.3 | N/A | 传输加密属部署层（HTTPS） |
| S9.4 | N/A | 无随机数 |
| S10.1 | N/A | 内部 oneapi，design 未要求 CSRF Token |
| S10.2 | ⚠️ | `config/WebConfig.java:14-20` CORS `allowedHeaders("*")` + `allowCredentials(true)`；Origin 用 `http://localhost:*` patterns（非裸 `*`），本地联调可接受，生产应收敛。P2 |
| S10.3 | N/A | 无 URL 跳转 |

---

## Step 5 — 自定义扩展检查（产物 E）

> `customized-checklist.md` 仅含示例项（U1.1 为示例），未启用团队私有规则。

| ID | 状态 | 备注 |
|----|------|------|
| U1.1 | ✅ | 示例项：Controller 入参已使用 `@Valid`（`TodoController.java:35`），符合 |
| U1.2 | N/A | 示例项，未启用 |
| U1.3 | N/A | 示例项，未启用 |
| U2.1 | N/A | 无业务红线规则 |
| U2.2 | N/A | 无业务红线规则 |
| U2.3 | N/A | 无业务红线规则 |

> 整节结论：`N/A(未启用自定义规则)`，仅示例项 U1.1 顺带核销。

---

## 终检（防漏检）

- [x] 执行队列中每个文件 `Step2`、`Step3`、S1–S10 / G1–G17 各列均非 `⬜`（跳过文件除外）；
- [x] Step 2 的每个 REQ/Scenario 均非 `⬜`
- [x] Step 3 的 A1–A7 均非 `⬜`
- [x] Step 4 全部 G/S 与 B001–B081 / M001–M027 / I001–I010 ID 均非 `⬜`（区间核销 + 点名项，允许 `N/A`，已写原因）
- [x] Step 5 全部 U* ID 均非 `⬜`（`N/A(未启用自定义规则)`）
- [x] 所有 `❌/⚠️` 已写入 report，且包含 `ID + path:line`
