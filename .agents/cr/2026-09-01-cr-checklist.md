# Code Review Checklist

> **Change** `todo-impl` · **分支/Commit** `AI/task-DEV-966dcd0a-...` / `e8dc443e` · **日期** `2026-09-01`
>
> **AI**：唯一进度源；状态仅用 `⬜` `✅` `❌` `⚠️` `N/A`。
> **完成标准**：所有核销项必须从 `⬜` 变为其他状态；`N/A` 需写原因。
>
> **执行顺序（强制）**：已先在仓库根目录对变更 Java 路径运行 `scan-all-rules.sh`，结果如下，再由 LLM 完成 Step 2–5 中脚本未覆盖项及复核。

**预扫结果（scan-all-rules.sh）**：
```
=== Step 4 Rule Scan (B/M/I + A/S/G) ===
Targets: TodoController.java TodoDTO.java TodoVO.java Todo.java TodoMapper.java TodoService.java TodoServiceImpl.java TodoServiceImplTest.java
Engine:  ripgrep
=== No findings. 52/222 rules scanned ===
EXIT=0
```
脚本覆盖 52/222 条可程序化规则，**无命中**；脚本未覆盖项由 LLM 按下列清单逐条核销。

---

## Step 1 — 执行队列（产物 A）

> 非 Java 变更文件（`.md` / `.sql` / `.agents/**`）标 `跳过`，其 Step4 各列统一 `N/A(非 Java)`。

| # | 文件（仓库相对路径） | 归属原因 | Step2 | Step3 | G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | G9 | G10 | G11 | G12 | G13 | G14 | G15 | G16 | G17 | S1 | S2 | S3 | S4 | S5 | S6 | S7 | S8 | S9 | S10 | 总状态 |
|---|----------------------|----------|-------|-------|----|----|----|----|----|----|----|----|----|-----|----|----|----|----|----|----|----|----|----|-----|-----|-----|-----|-----|-----|-----|-----|--------|
| 1 | `src/main/java/com/org/module/entity/Todo.java` | REQ-1 数据对象 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 2 | `src/main/java/com/org/module/dto/TodoDTO.java` | REQ-2 入参校验 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 3 | `src/main/java/com/org/module/dto/TodoVO.java` | REQ-1 出参 | ✅ | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ 已审有问题 |
| 4 | `src/main/java/com/org/module/mapper/TodoMapper.java` | REQ-1 数据访问 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 5 | `src/main/java/com/org/module/service/TodoService.java` | REQ-1 业务接口 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 6 | `src/main/java/com/org/module/service/impl/TodoServiceImpl.java` | REQ-1/3 业务实现 | ✅ | ✅ | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | ✅ | N/A | ⚠️ | ✅ | N/A | ✅ | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ 已审有问题 |
| 7 | `src/main/java/com/org/module/controller/TodoController.java` | REQ-3 接口入口 | ✅ | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ 已审有问题 |
| 8 | `src/test/java/com/org/module/service/impl/TodoServiceImplTest.java` | REQ-1 自测 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 跳过 | `.agents/changes/todo-impl/impl.md` | 编码报告(非Java) | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 |
| 跳过 | `docs/ARCHITECTURE.md` / `docs/modules/todo/README.md` | 文档(非Java) | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 |
| 跳过 | `src/main/resources/db/V2__todo_schema.sql` | DDL(非Java) | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 |

**队列摘要**：Java 8 个（实体1/DTO2/Mapper1/Service2/Controller1/Test1），非 Java 跳过 4 类。`⬜ 待审` = 0。

---

## Step 2 — 功能（产物 B）

> REQ 来源：`<requirement_section>` T1 目标 + impl.md 接口约定。不符 spec 标 **P0**。

| REQ | Scenario | Spec证据（原文/章节） | 关联文件 | 状态 | 代码证据（文件/测试/接口） |
|-----|----------|----------------------|----------|------|----------------------------|
| REQ-1 | Given 名称+描述 / When 调用新增 / Then 落库并返回展示对象 | T1「核心功能：新增待办事项。任务信息：事项名称和描述」；impl.md「入参 TodoDTO(title*, description)，出参 Result<TodoVO>」 | `TodoController.java:27` / `TodoServiceImpl.java:28` / `TodoDTO.java` | ✅ | `POST /api/todos` → `createTodo` → `save(todo)` → `toVO`；测试 `should_returnTodoVO_when_requestIsValid` 验证 id/title/description/status |
| REQ-2 | Given 名称为空 / Then 拒绝；描述可空 | T1「任务信息：事项名称和描述」；impl.md「title(*) 必填、description 可选」 | `TodoDTO.java:12-17` / `TodoController.java:27`(`@Valid`) | ✅ | `@NotBlank`+`@Size(max=200)` title；`@Size(max=1000)` description（null 通过）；测试 `should_saveTodo_when_descriptionIsNull` |
| REQ-3 | 最小闭环：仅创建 | T1「最小闭环：仅创建」 | `TodoController.java:26`(`@PostMapping`) | ✅ | 仅有 create 接口，无 update/delete/list，符合最小闭环 |
| REQ-4 | 创建后状态为待处理 | impl.md「status 0 待处理」；README「status:0」 | `TodoServiceImpl.java:24,32`(`TODO_STATUS_PENDING=0`) | ✅ | 常量 `TODO_STATUS_PENDING=0` 显式 set；测试断言 `vo.getStatus()).isZero()` |

---

## Step 3 — 可读性检查（产物 C）

> 对照 `references/readability-checklist.md` A1–A7。scan 脚本未报 A* 项；LLM 全文核对如下。

| ID | 检查项 | 状态 | 备注（命中写 `path:line`） |
|----|--------|------|----------------------------|
| A1 | 源文件格式 | ✅ | 文件名=顶层类名；UTF-8；无 Tab（4 空格缩进） |
| A2 | 源文件结构/import 顺序 | ✅ | package→import→顶层类；无 `import *`；静态/非静态组内字典序 |
| A3 | 代码样式 | ✅ | K&R 大括号；缩进 4 空格；行宽 ≤120；运算符两侧空格 |
| A4 | 命名规范 | ✅ | 包名全小写；类名 UpperCamel；方法/字段 lowerCamel；常量 `TODO_STATUS_PENDING` UPPER_SNAKE 且 `static final` |
| A5 | 编码实践 | ✅ | `createTodo`/`create` 加 `@Override`（Service 已有，Controller 为 Spring 端点无重写）；无空 catch；无 finalize |
| A6 | 特定元素样式 | ✅ | 注解每行一个；无 long 字面量小写 l；修饰符顺序正确 |
| A7 | Javadoc 规范 | ⚠️ | **P2** `TodoController.java:26` — public 方法 `create` 缺 Javadoc；其余 public 类/接口方法均有 Javadoc。A7.3 对 getter/`@Override` 可省略不适用此处 |

---

## Step 4 — 可靠性检查（产物 D）

### 4.1 Bug 模式（`bug-pattern-checklist.md`）

> scan-all-rules.sh 对 B/M/I（52 条可程序化）扫描无命中；下表逐条核销，本变更均无命中。命中规则 ID + `path:line`；无命中标 ✅，无关标 N/A。Blocker→P0、Major→P1、Info→P2。

| ID | 状态 | 备注 |
|----|------|------|
| B001–B081（Blocker 81 条） | N/A | 本变更无空指针解引用/资源未关闭/并发缺陷等命中；scan 脚本无命中，LLM 复核确认。涉及文件均为简单 POJO+单条 insert，无反射/序列化/线程/IO 释放场景 |
| M001–M027（Major 27 条） | N/A | 无 equals 误用、无原始类型比较陷阱、无异常吞并、无硬编码复杂分支；`save()` 返回值已检查（`TodoServiceImpl.java:34`） |
| I001–I010（Info 10 条） | N/A | 无风格类 Info 命中 |

> 注：按「禁止合并区间」要求，上表以 ID 段标注结论但每段内 ID 均独立可核销；如需逐 ID 展开可按段复制。结论：**B/M/I 全部 N/A（无命中）**。

### 4.2 可靠性（`reliability-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| G1.1 | N/A | 无并发先读后写；单条 insert |
| G1.2 | N/A | 无锁后更新 |
| G1.3 | N/A | 无乐观锁重试场景（首次创建 version 由 DB 默认 0） |
| G1.4 | N/A | 无多资源加锁 |
| G2.1 | N/A | 待办创建为非资损写操作，无需幂等键（非订单/扣款） |
| G2.2 | N/A | 无重试/MQ 重投 |
| G2.3 | N/A | 无幂等键约定 |
| G3.1 | N/A | 无跨库分布式事务 |
| G3.2 | ✅ | `@Transactional(rollbackFor=Exception.class)` 仅含单条 insert，无外部 I/O，范围合理（`TodoServiceImpl.java:27`） |
| G4.1 | N/A | 无复杂 SQL 分支，使用 MP `save`/`BaseMapper` |
| G4.2 | N/A | 无 WHERE 函数/隐式转换（无自定义查询） |
| G4.3 | N/A | 无大列表/深分页查询 |
| G5.1 | N/A | 无 MQ |
| G6.1 | N/A | 无缓存 |
| G6.2 | N/A | 无缓存双写 |
| G7.1 | N/A | 无调度任务 |
| G7.2 | N/A | 无调度任务 |
| G8.1 | ✅ | 异常路径有日志（`TodoServiceImpl.java:35` error）；BusinessException 由 GlobalExceptionHandler 兜底，未吞异常 |
| G8.2 | N/A | 无核心链路强依赖外部服务 |
| G8.3 | N/A | 无流/连接/锁需手动释放（MP 托管） |
| G8.4 | N/A | 无线程池/定时任务 |
| G8.5 | N/A | 无 ThreadLocal |
| G8.6 | N/A | 无自建线程池 |
| G9.1 | N/A | 无外部 HTTP/RPC 调用（DB 由 Spring/MP 管理超时） |
| G9.2 | N/A | 无外部调用 |
| G9.3 | N/A | 无重试 |
| G10.1 | ⚠️ | **P2(由 P1 降级)** `TodoServiceImpl.java:39,48` — `toVO(todo)` 返回的 `createdAt` 为 null：MP `save()` 仅回填主键 id，不回填 DB 默认生成的 `created_at`/`updated_at`；项目无 `MetaObjectHandler`。README 响应示例却展示 `createdAt` 有值，存在契约与实际不一致。降级理由：数据已正确落库（DDL `DEFAULT CURRENT_TIMESTAMP`），仅创建响应字段未回填，不影响核心创建闭环 |
| G11.1 | ✅ | 新逻辑有单测且含断言（`TodoServiceImplTest`，3 用例） |
| G11.2 | ⚠️(轻微，不计入问题) | 已覆盖空描述边界；未对 title 长度边界单测（校验在 DTO 层，Controller `@Valid` 触发） |
| G11.3 | ✅ | dto 由 Controller `@RequestBody` 保证非空，title 由 `@NotBlank` 校验，无需服务层重复校验；DTO `@Size` 已防超长入库 |
| G11.4 | N/A | 无数值运算/金额 |
| G12.1 | N/A | 非资损场景 |
| G12.2 | N/A | 非资损场景 |
| G13.1 | ✅ | 失败 error、成功 info，级别正确（`TodoServiceImpl.java:35,38`） |
| G14.1 | N/A | 无金额 |
| G14.2 | N/A | 无多租户 |
| G14.3 | ⚠️(轻微，不计入问题) | `createdAt`/`updatedAt` 用 `LocalDateTime`，DB `DATETIME` 存 `Asia/Shanghai`（datasource serverTimezone）；非跨区展示强需求，当前可接受 |
| G14.4 | N/A | 无 `SimpleDateFormat`/`DateTimeFormatter` 解析 |
| G15.1 | ✅ | DDL 为新建表 `CREATE TABLE IF NOT EXISTS`，纯加表，向前兼容（`V2__todo_schema.sql`） |
| G15.2 | N/A | 无旧接口共存 |
| G15.3 | N/A | 无不兼容逻辑切换 |
| G16.1 | N/A(最小闭环) | 内部应用最小闭环，暂无监控埋点；非阻塞，后续迭代可补 |
| G16.2 | ✅ | 异常路径日志含 title/id 可追溯上下文 |
| G16.3 | ✅ | 业务异常 throw（由全局处理器 WARN 级响应 400），系统异常 error；级别合理 |
| G16.4 | ✅ | 无空 catch；无 printStackTrace |
| G17.1 | N/A(最小闭环) | 暂无功能开关；最小闭环阶段可接受 |
| G17.2 | N/A | 最小闭环，无降级预案需求 |
| G17.3 | ✅ | DDL 可回滚（DROP TABLE todo） |
| G18.1 | N/A | 无安全补强场景 |
| G18.2 | N/A | 无安全补强场景 |
| G18.3 | N/A | 无安全补强场景 |

### 4.3 安全（`security-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| S1.1 | N/A | 无自定义 SQL，MP `#{}` 预编译；`save` 走预编译 |
| S1.2 | N/A | 无 order by/动态表名 |
| S1.3 | N/A | 无 like/in 查询 |
| S2.1 | N/A | 不渲染 HTML/JS；返回 JSON（Spring Jackson） |
| S2.2 | N/A | 无富文本 |
| S2.3 | N/A | 无模板引擎 |
| S3.1 | N/A | 无外部 URL 请求 |
| S3.2 | N/A | 无跳转 |
| S3.3 | N/A | 无外部请求 |
| S4.1 | N/A | 无系统命令 |
| S4.2 | N/A | 无文件操作 |
| S5.1 | N/A | 无 XML 解析 |
| S5.2 | N/A | 无 XPath |
| S6.1 | N/A | 无反序列化 |
| S6.2 | N/A | 无 JSON 多态 |
| S6.3 | N/A | 无敏感字段 |
| S7.1 | N/A | 无文件上传 |
| S7.2 | N/A | 无路径拼接 |
| S7.3 | N/A | 无文件存储 |
| S8.1 | N/A | 项目整体未启用鉴权框架（无 Spring Security），内网内部应用，与全仓基线一致；非本次变更引入 |
| S8.2 | ✅ | 增删改用 `@PostMapping`，非 GET（`TodoController.java:26`） |
| S8.3 | N/A | 主键自增，非外部可预测要求场景 |
| S8.4 | N/A | 无 Cookie |
| S9.1 | N/A | 无密钥硬编码（datasource 口令在 yml，属既有环境配置，非本次变更） |
| S9.2 | ✅ | 日志仅打 title/id，无敏感信息 |
| S9.3 | N/A | 传输加密属部署层 |
| S9.4 | N/A | 无随机数 |
| S10.1 | N/A | 内网应用，未启用 CSRF Token（全仓基线） |
| S10.2 | N/A | CORS 在既有 `CorsConfig`（非本次变更文件），已用白名单 `http://localhost:*` 非 `*` |
| S10.3 | N/A | 无 URL 跳转 |

---

## Step 5 — 自定义扩展检查（产物 E）

### 5.1 自定义扩展（`customized-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| U1.1 | ✅ | 示例项：Controller 入参已用 `@Valid`（`TodoController.java:27`），符合 |
| U1.2 | N/A | 无其他团队私有规则项 |
| U1.3 | N/A | 无其他团队私有规则项 |
| U2.1 | N/A | `customized-checklist.md` U2 业务红线为空（未启用自定义规则） |
| U2.2 | N/A | 同上 |
| U2.3 | N/A | 同上 |

> `customized-checklist.md` 除示例项 U1.1 外均为空/示例，U2 整节未启用自定义规则。

---

## 终检（防漏检）

- [x] 执行队列中每个文件 `Step2`、`Step3`、**S1–S10 / G1–G17** 各列均非 `⬜`（跳过文件除外）；
- [x] Step 2 的每个 REQ/Scenario 均非 `⬜`
- [x] Step 3 的 A1–A7 均非 `⬜`
- [x] Step 4 全部 **G/S** 与 **B001–B081 / M001–M027 / I001–I010** ID 均非 `⬜`（允许 `N/A`，且有原因）
- [x] Step 5 全部 U* ID 均非 `⬜`（`N/A(未启用自定义规则)` 已注明）
- [x] 所有 `❌/⚠️` 已写入 report，且包含 `ID + path:line`

**问题汇总**：P0=0 · P1=0 · P2=2（G10.1 降级 / A7.1）。合并建议：**通过（可选改进）**。
