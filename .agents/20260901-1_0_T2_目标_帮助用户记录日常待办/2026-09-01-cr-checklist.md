# Code Review Checklist

> **Change** `1.0 T2 — 帮助用户记录日常待办事项（仅创建）` · **分支/Commit** `AI/task-DEV-966dcd0a` / `cc2f99a0` · **日期** `2026-09-01`
>
> **AI**：唯一进度源；状态仅用 `⬜` `✅` `❌` `⚠️` `N/A`。
> **完成标准**：所有核销项必须从 `⬜` 变为其他状态；`N/A` 需写原因。

---

## Step 1 — 执行队列（产物 A）

| # | 文件（仓库相对路径） | 归属原因 | Step2 | Step3 | 总状态 |
|---|----------------------|----------|-------|-------|--------|
| 1 | `src/main/java/com/org/module/controller/TodoController.java` | REQ-1/W01 | ✅ | ✅ | ✅ 已审 |
| 2 | `src/main/java/com/org/module/service/impl/TodoServiceImpl.java` | REQ-1/S01/R03 | ✅ | ✅ | ⚠️ 已审有问题 |
| 3 | `src/main/java/com/org/module/service/TodoService.java` | REQ-1/S01 | ✅ | ✅ | ✅ 已审 |
| 4 | `src/main/java/com/org/module/entity/Todo.java` | REQ-1/表结构 | ✅ | ✅ | ✅ 已审 |
| 5 | `src/main/java/com/org/module/dto/TodoDTO.java` | REQ-1/R01/R02 | ✅ | ✅ | ✅ 已审 |
| 6 | `src/main/java/com/org/module/mapper/TodoMapper.java` | REQ-1 | ✅ | ✅ | ✅ 已审 |
| 7 | `src/main/java/com/org/module/context/UserContext.java` | REQ-1/R03 | ✅ | ✅ | ✅ 已审 |
| 8 | `src/main/java/com/org/module/context/UserContextImpl.java` | REQ-1/R03 | ✅ | ⚠️ | ⚠️ 已审有问题 |
| 9 | `src/main/java/com/org/module/exception/GlobalExceptionHandler.java` | REQ-1/异常处理 | ✅ | ⚠️ | ⚠️ 已审有问题 |
| 10 | `src/test/java/com/org/module/service/impl/TodoServiceImplTest.java` | REQ-1/G11 | ✅ | ✅ | ✅ 已审 |

非 Java 文件（跳过）：`src/main/resources/db/V2__todo_table.sql`（SQL，非 Java，脚本预扫已覆盖）、`docs/modules/todo/README.md`、`.agents/.../impl.md`、`.agents/.../design.md`。

---

## Step 2 — 功能（产物 B）

| REQ | Scenario | Spec证据（原文/章节） | 关联文件 | 状态 | 代码证据（文件/测试/接口） |
|-----|----------|----------------------|----------|------|----------------------------|
| REQ-1 | Given 内部用户已登录；When POST /api/todos {title, description}；Then 落库一条待办记录并返回 Result.ok() | design §4.1 W01「POST /api/todos」、§5.2.3.1 F01 时序图 | `TodoController.java:35-40` `TodoServiceImpl.java:32-49` | ✅ | Controller 调用 Service.createTodo → save(todo) → 返回 Result.ok()；时序与 design 一致 |
| REQ-2 | Given title 为空或超 200；When 创建；Then 返回 TODO_001 提示 | design §5.2.2 R01「事项名称 title 非空且长度 1-200」、§5.2.1.1 表结构 title varchar(200) NOT NULL | `TodoDTO.java:14-16` `GlobalExceptionHandler.java:23-32` | ⚠️ | `@NotBlank` + `@Size(max=200)` 校验到位；但错误码 TODO_001 未在响应 code 字段中返回（仅返回 400 + message 文本） |
| REQ-3 | Given description 超 1000；When 创建；Then 返回 TODO_002 提示 | design §5.2.2 R02「description 长度 ≤1000」 | `TodoDTO.java:19` | ⚠️ | `@Size(max=1000)` 校验到位；错误码 TODO_002 同 REQ-2 未返回 |
| REQ-4 | Given 登录上下文缺失；When 创建；Then 抛 BusinessException(TODO_003) 不落库 | design §5.2.2 R03、§5.2.3.1 R03 | `TodoServiceImpl.java:36-38` `UserContextImpl.java:21-36` `TodoServiceImplTest.java:82-93` | ⚠️ | Service 从 UserContext 获取 creatorId，缺失抛 BusinessException("TODO_003", ...)；但 GlobalExceptionHandler.handleBusiness:20 丢弃 getCode()，响应仅返回 400 + message |
| REQ-5 | Given creator_id 由登录上下文注入而非前端传入；When 创建；Then todo.creator_id = 登录用户 ID | design §6.4.2.1「creator_id 由登录上下文注入而非前端传入，本身即防止越权创建」 | `TodoServiceImpl.java:36-43` `UserContextImpl.java:27` | ✅ | creator_id 从 UserContext 读取，TodoDTO 无 creatorId 字段，前端无法传入 |

---

## Step 3 — 可读性检查（产物 C）

| ID | 检查项 | 状态 | 备注（命中写 `path:line`） |
|----|--------|------|----------------------------|
| A1 | 源文件格式 | ✅ | 文件名=类名，UTF-8，无 Tab |
| A2 | 源文件结构/import 顺序 | ✅ | package→import→class，无通配符 import，静态/非静态分组正确 |
| A3 | 代码样式 | ✅ | K&R 大括号，4 空格缩进，行宽 ≤120 |
| A4 | 命名规范 | ✅ | 包名全小写，类名 UpperCamelCase，方法 lowerCamelCase，常量 UPPER_SNAKE_CASE（`CODE_NO_LOGIN_USER`、`USER_ID_HEADER`、`CREATOR_ID`） |
| A5 | 编码实践 | ✅ | @Override 均添加（`UserContextImpl.getCurrentUserId`、`TodoServiceImpl.createTodo`），无非空 catch |
| A6 | 特定元素样式 | ✅ | 无数组/switch/修饰符顺序问题，注解使用规范 |
| A7 | Javadoc 规范 | ⚠️ | `GlobalExceptionHandler.java:24` 新增 public 方法 `handleValidation` 缺少 Javadoc（与既有方法一致，但违反 A7.1）；其余 public 方法均有 Javadoc |

---

## Step 4 — 可靠性检查（产物 D）

### 预扫结果（scan-all-rules.sh）

```
=== Step 4 Rule Scan (B/M/I + A/S/G) ===
Engine: ripgrep

[P0] G16.2 — CatchWithoutLogging: src/main/java/com/org/module/context/UserContextImpl.java:33

=== Summary: 1 findings (P0=1, P1=0, P2=0) | 52/222 rules scanned ===
```

### 4.1 Bug 模式（bug-pattern-checklist.md）

> 已对照 120 条规则逐条核销；下表仅列出与本次变更相关的检查项，其余标 N/A。

| ID | 状态 | 备注 |
|----|------|------|
| B001-B081（81 条 Blocker） | ✅ | 逐条核销：无字面量 parse/of 误用、无数组比较/填充/toString 误用、无包装类型 == 比较、无 Calendar 误用、无集合类型不兼容、无死异常/死线程、无双括号初始化、无 equals(null)/equals(自身)、无浮点 == 比较、无 String.format 占位符不匹配、无 InfiniteRecursion、无 JDBC 连接泄漏、无 JUnit3/4 混用、无锁包装类型、无死循环、无精度丢失 compare、无 Math.round 误用、无日期格式误用、无 Boolean.getBoolean 误用、无 MissingFail（测试用 assertThatThrownBy 替代）、无 Mockito 误用（when().thenReturn() 正确、verify(mock).method() 正确）、无 Arrays.asList add 误用、无集合自修改、无 NCopies 误用、无 NullTernary 拆箱、无过时 Base64/ClassLoader/XML 类、无 Optional == 比较、无 Pojo 自赋值、无 Math.random() 强转、无 Random 取余负数、无变量自赋值、无 compareTo/equals 自比较、无 size()>=0、无 Stream.toString()、无 StringBuilder(char) 初始化、无 substring(0)、无 SuspiciousForLoop、无 @Transactional 非公开方法、无 TryFailThrowable、无 TruthSelfEquals、无 @Mock 显式赋值、无单测无断言、无 UnusedCollectionModifiedInPlace |
| M001-M027（27 条 Major） | ✅ | 逐条核销：无连续同条件判断、无 BadInstanceof、无包装类构造器（valueOf 使用正确）、无 printStackTrace、无内部类可 static、无编译期布尔常量、无空 catch（UserContextImpl catch 有 return 语句，非空但缺日志→G16.2）、equals/hashCode 未重写（Todo 使用 Lombok @Data 自动生成，一致）、无 equals 不兼容类型、无位运算恒 0、无 FallThrough、无 finally return/throw、无 FloatCast、无 GetClassOnEnum、无字段隐藏、无默认时区方法（使用 LocalDateTime 由 DB DEFAULT）、无 JUnit4 无 @Test、无 LockNotBeforeTry、无 enum switch 缺 default、无 MissingOverride、无 NonOverridingEquals、无 Optional.of(null)、无 Object.toString()、无 Optional.get() 未检查、无 ProtectedMembersInFinalClass、无 static @Mock、无非 static ThreadLocal |
| I001-I010（10 条 Info） | ✅ | 逐条核销：测试用 assertThatThrownBy + hasMessageContaining 对异常消息断言（I001 ✅）、无 @DoNotMock/@AutoValue 类被 Mock、无 java.util.Date（使用 LocalDateTime）、无 JUnit3/4 混用、无 @Before/@After 缺失、无 DataProvider 缺失、单测方法可识别、无需容器启动 |

### 4.2 可靠性（reliability-checklist.md）

| ID | 状态 | 备注 |
|----|------|------|
| G1.1 | N/A | 创建为独立行 INSERT，无先读后写 |
| G1.2 | N/A | 无已加锁后未二次校验 |
| G1.3 | N/A | 无乐观锁重试场景 |
| G1.4 | N/A | 无多资源加锁 |
| G2.1 | N/A | design 明确：名称不要求唯一，允许重复，无需幂等键（§5.2.3.1 并发控制） |
| G2.2 | N/A | 无重试/定时任务/MQ 重投 |
| G2.3 | N/A | 无幂等键上游约定 |
| G3.1 | N/A | 无分布式事务 |
| G3.2 | ✅ | `@Transactional` 范围仅含 save(todo) DB 操作，无外部 I/O（`TodoServiceImpl.java:33`） |
| G4.1 | N/A | 无复杂业务分支堆在 SQL（MyBatis-Plus BaseMapper 无自定义 SQL） |
| G4.2 | N/A | 无 WHERE 函数/隐式转换（无自定义查询） |
| G4.3 | N/A | 无大列表查询 |
| G5.1 | N/A | 无 MQ 消费 |
| G6.1 | N/A | 无缓存 |
| G6.2 | N/A | 无缓存与 DB 双写 |
| G7.1 | N/A | 无调度任务 |
| G7.2 | N/A | 无调度任务 |
| G8.1 | ⚠️ | `UserContextImpl.java:33` catch NumberFormatException 后仅返回 Optional.empty()，无日志记录（与 G16.2 同一问题） |
| G8.2 | N/A | 无核心链路强依赖非核心 |
| G8.3 | N/A | 无 I/O 流/连接需释放 |
| G8.4 | N/A | 无线程池/定时任务关闭 |
| G8.5 | N/A | 无 ThreadLocal |
| G8.6 | N/A | 无 Executors 创建线程池 |
| G9.1 | N/A | 仅 DB 调用（MyBatis-Plus），无 HTTP/RPC/Redis 外部调用 |
| G9.2 | N/A | 仅 DB 调用，超时由数据源配置保障 |
| G9.3 | N/A | 无重试场景 |
| G10.1 | N/A | 无同一字段 null 双义 |
| G10.2 | N/A | 全新接口，无契约变更 |
| G11.1 | ✅ | 新逻辑有 3 个单测，含断言（`TodoServiceImplTest.java`） |
| G11.2 | ✅ | 覆盖正常路径、description=null 边界、creator 缺失异常 |
| G11.3 | ✅ | 入参由 @Valid 校验（title @NotBlank, description @Size），createTodo 内对 creatorId 缺失有防御 |
| G11.4 | N/A | 无数值运算/金额 |
| G12.1 | N/A | 无资金场景 |
| G12.2 | N/A | 无资金止血需求 |
| G13.1 | ✅ | 成功打 INFO（`TodoServiceImpl.java:47`），系统异常打 ERROR（`GlobalExceptionHandler.java:41`），级别正确 |
| G14.1 | N/A | 无金额 |
| G14.2 | N/A | design A05 明确不分租户 |
| G14.3 | N/A | 时间由 DB DATETIME + CURRENT_TIMESTAMP 管理，MyBatis-Plus 实体用 LocalDateTime |
| G14.4 | N/A | 无 SimpleDateFormat/DateTimeFormatter 使用 |
| G15.1 | ✅ | 新建表 CREATE TABLE IF NOT EXISTS，向前兼容（`V2__todo_table.sql:2`） |
| G15.2 | N/A | 全新接口，无新旧共存 |
| G15.3 | N/A | 无不兼容逻辑 |
| G16.1 | N/A | design §7.1 描述了埋点设计，但本次为最小闭环，无显式埋点实现（design 为变更三板斧规划，不阻塞） |
| G16.2 | ⚠️ | `UserContextImpl.java:33` catch NumberFormatException 无日志输出（预扫确认） |
| G16.3 | ✅ | 成功 INFO、系统异常 ERROR，级别正确 |
| G16.4 | ⚠️ | `UserContextImpl.java:33` catch 后返回 Optional.empty() 且无记录，虽非完全空 catch 但缺日志（与 G16.2 同一问题） |
| G17.1 | ⚠️ | design §7.2/7.3 推荐接口级功能开关（方案A），代码未实现任何开关；应急关闭需重新部署 |
| G17.2 | N/A | 本功能为独立创建链路，无核心链路依赖需降级 |
| G17.3 | ✅ | 新建表可 DDL 回滚，design §7.3 已说明回滚策略 |
| G18.1 | N/A | 安全补强——无密钥管理场景 |
| G18.2 | N/A | 无敏感数据加密 |
| G18.3 | N/A | 无安全日志审计 |

### 4.3 安全（security-checklist.md）

| ID | 状态 | 备注 |
|----|------|------|
| S1.1 | ✅ | MyBatis-Plus BaseMapper 使用预编译，无 ${} 拼接 |
| S1.2 | N/A | 无 order by/group by/动态表名字段名 |
| S1.3 | N/A | 无 like/in 查询 |
| S2.1 | N/A | 无 HTML/JS 输出（REST API 返回 JSON） |
| S2.2 | N/A | 无富文本 |
| S2.3 | N/A | 无模板引擎 |
| S3.1 | N/A | 无外部 URL 请求 |
| S3.2 | N/A | 无 302 跳转 |
| S3.3 | N/A | 无外部 HTTP 调用 |
| S4.1 | N/A | 无系统命令拼接 |
| S4.2 | N/A | 无文件/图片操作 |
| S5.1 | N/A | 无 XML 解析 |
| S5.2 | N/A | 无 XPath |
| S6.1 | N/A | 无反序列化 |
| S6.2 | N/A | 无 JSON 多态反序列化 |
| S6.3 | N/A | 无敏感字段 transient |
| S7.1 | N/A | 无文件上传/下载 |
| S7.2 | N/A | 无路径穿越风险 |
| S7.3 | N/A | 无文件重命名 |
| S8.1 | ⚠️ | `TodoController.java:35` / `UserContextImpl.java:27` — 接口无鉴权拦截器，X-User-Id 头由客户端控制；design A02 标注"待确认"，impl.md 确认仓库无鉴权拦截器。若应用未部署在受信网关后，存在用户伪造 creator_id 的风险 |
| S8.2 | ✅ | 使用 POST 创建，非 GET |
| S8.3 | N/A | id 为自增主键，不涉及 UUID/加密 |
| S8.4 | N/A | 无 Cookie 操作 |
| S9.1 | N/A | 无密钥硬编码 |
| S9.2 | ✅ | 日志仅打印 title + creator_id + todo_id，不打印完整 description（`TodoController.java:38` `TodoServiceImpl.java:47-48`） |
| S9.3 | N/A | 无传输/存储加密场景（HTTPS 由网关保障） |
| S9.4 | N/A | 无随机数 |
| S10.1 | N/A | 无 CSRF Token（内部 API 经网关） |
| S10.2 | N/A | 无 CORS 配置变更 |
| S10.3 | N/A | 无 URL 跳转 |

---

## Step 5 — 自定义扩展检查（产物 E）

| ID | 状态 | 备注 |
|----|------|------|
| U1.1 | ✅ | Controller 入参使用 @Valid（`TodoController.java:36`） |
| U1.2 | N/A(未启用自定义规则) | customized-checklist.md 仅含示例项 |
| U1.3 | N/A(未启用自定义规则) | 同上 |
| U2.1-U2.3 | N/A(未启用自定义规则) | U2 节为空 |

---

## 终检（防漏检）

- [x] 执行队列中每个文件 `Step2`、`Step3` 各列均非 `⬜`（跳过文件除外）
- [x] Step 2 的每个 REQ/Scenario 均非 `⬜`
- [x] Step 3 的 A1–A7 均非 `⬜`
- [x] Step 4 全部 G/S 与 B001–B081 / M001–M027 / I001–I010 ID 均非 `⬜`（允许 N/A，有原因）
- [x] Step 5 全部 U* ID 均非 `⬜`（允许 N/A）
- [x] 所有 `❌/⚠️` 已写入 report，且包含 `ID + path:line`
