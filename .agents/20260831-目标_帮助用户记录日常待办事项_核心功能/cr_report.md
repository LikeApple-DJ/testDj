# 待办事项模块 代码评审报告

> **文档元信息**
>
> | 项目 | 内容 |
> |------|------|
> | 评审范围 | 待办事项模块「新增待办事项」最小闭环（F01/W01） |
> | 采用技能 | dtazziboot-java-code-review（code-review 多车道评审） |
> | 评审日期 | 2026-08-31 |
> | 系分依据 | `.agents/20260831-目标_帮助用户记录日常待办事项_核心功能/design.md` |
> | 编码依据 | `.agents/20260831-目标_帮助用户记录日常待办事项_核心功能/impl.md` |
> | 评审目标 | 静态代码审查 + 契约/设计/裁剪/根因/验证 五车道评审 |

---

## 1. 通览 (Overview)

### 1.1 需求与最小闭环

- **目标**：帮助内部用户记录日常待办事项。
- **核心功能**：新增待办事项（事项名称 + 描述）。
- **最小闭环**：仅创建——前端录入 → 后端校验 → 持久化 → 返回主键 id。
- **排除范围**：查询/编辑/完成/删除/状态机/幂等/SSO 均不在本期。

### 2.2 仓库现状与依赖关系

- 技术栈：Spring Boot 3.2.5 + Java 17 + MyBatis-Plus 3.5.6 + MySQL 8（pom.xml 实测）。
- 包结构：扁平分层 `com.org.module.{controller,service,service/impl,mapper,entity,dto,exception,config}`，与既有 Employee/Department/TransferRecord 模块一致。
- 复用既有能力：`Result<T>`（code/msg/data，成功 code=200）、`BusinessException`、`GlobalExceptionHandler`、`MybatisPlusConfig`（分页 + 乐观锁拦截器）、DB 脚本 `V1__init_schema.sql`。
- 本次新增/修改：
  - 新增 `TodoItem` / `TodoCreateRequest` / `TodoVO` / `TodoMapper` / `TodoService` / `TodoServiceImpl` / `TodoController` / `V2__add_todo_item.sql` / `docs/modules/todo/README.md` / `TodoServiceImplTest`。
  - 修改 `GlobalExceptionHandler`：追加 `MethodArgumentNotValidException` / `BindException` / `ConstraintViolationException` 校验异常兜底（返回 400 + 首条字段错误 message）。

### 2.3 评审画像（Project Profile）

- **State**：`NOT_CHECKED`
- **Source**：仓库根无 `REVIEW.md`；任务硬性约束限定产物仅 `cr_report.md` 与 `run_context.json`，故未写入 `REVIEW.md`。
- **Notes**：项目文件可检视并已实测（pom.xml、V1__init_schema.sql、Employee/Department 实体、Result、BusinessException、MybatisPlusConfig）；以工程既有约定作为项目级评审基准，应用通用五车道评审。

---

## 2. 规划 (Planning)：评审车道与执行口径

依据 `references/lane-index.md`，默认车道 `align / design / trim / cause / verify` 均具备最小上下文（diff 文件、设计文档、测试、调用链可追溯），全部运行。`cause` 车道因本次为纯新增功能（非 bug 修复）按需收敛。

评审基准（来自工程既有约定）：

| 基准项 | 既有约定 | 来源 |
|--------|----------|------|
| 通用出参 | `Result<T>{code,msg,data}`，成功 code=200 | `Result.java` |
| 时间列命名 | `created_at/updated_at`（DB）↔ `createdAt/updatedAt`（实体） | `Employee.java:26-27`、`V1__init_schema.sql:8-9,26-27,43` |
| 逻辑删除 | `is_deleted` + `@TableLogic` | `Employee.java:24-25`、`V1:25` |
| 异常兜底 | `BusinessException→400`、乐观锁→409、系统异常→500 | `GlobalExceptionHandler.java`（既有） |
| 主键回填 | `@TableId(IdType.AUTO)`，insert 后实体 id 回填 | `Employee.java:14-15` |

---

## 3. 执行 (Execution)：车道评审结论

### Lane verdict table

| Lane | Verdict | Notes |
|---|---|---|
| Align | APPROVE_WITH_COMMENTS | 设计 4.3 内部接口签名 `boolean` 与实现 `Long` 存在文档漂移；错误码 TODO_001/002 为文档分类未进入响应（与既有项目模式一致） |
| Design | APPROVE_WITH_COMMENTS | 时间列命名 `gmt_create/gmt_modified` 偏离工程既有 `created_at/updated_at` 约定；`gmt_modified` 未配 `ON UPDATE` |
| Trim | APPROVE_WITH_COMMENTS | `TodoController.log` 死代码；`TodoVO` `@NoArgsConstructor` 未使用 |
| Cause | NOT_RUN | 本期为纯新增最小闭环，无 bug 修复/失败闭环声明，无根因对照需求 |
| Verify | APPROVE_WITH_COMMENTS | 校验异常兜底新增正确；缺 `HttpMessageNotReadableException` 兜底（缺失/不可解析请求体落 500）；测试仅覆盖 Service 层，无 Controller 校验路径测试 |

### Blocking findings

无 CRITICAL / HIGH 阻塞问题。

### Advisory findings

#### [WARNING] [DESIGN] [CONVENTION_DRIFT] src/main/java/com/org/module/entity/TodoItem.java:36
- **Finding**：时间字段 `gmtCreate/gmtModified` 与工程既有实体（Employee/Department/TransferRecord 均为 `createdAt/updatedAt`→`created_at/updated_at`）不一致，造成同一数据库内两套时间列命名。
- **Evidence**：
  - `Employee.java:26-27` → `createdAt/updatedAt`
  - `V1__init_schema.sql:8-9,26-27,43` → `created_at/updated_at`
  - `TodoItem.java:36-39` → `gmtCreate/gmtModified`
  - `V2__add_todo_item.sql:8-9` → `gmt_create/gmt_modified`
- **Recommendation**：统一为既有工程约定 `createdAt/updatedAt`→`created_at/updated_at`；或在模块文档显式记录该分歧与取舍，避免后续维护混淆。功能不受影响（MyBatis-Plus 默认 camelCase↔underscore 映射 `gmtCreate↔gmt_create` 成立）。

#### [WARNING] [VERIFY] [UNHANDLED_EXCEPTION] src/main/java/com/org/module/exception/GlobalExceptionHandler.java:57
- **Finding**：请求体缺失或 JSON 不可解析时抛 `HttpMessageNotReadableException`，未纳入新增的校验异常处理，落入通用 `Exception` 处理返回 500，与设计「参数校验失败返回 400」意图不符。
- **Evidence**：`GlobalExceptionHandler` 仅处理 `MethodArgumentNotValidException`/`BindException`/`ConstraintViolationException`（line 22-50）；`HttpMessageNotReadableException` 无专属 handler，走到 line 57-61 返回 `code=500`。
- **Recommendation**：追加 `@ExceptionHandler(HttpMessageNotReadableException.class)` 返回 400 + 「请求体格式错误」提示。

#### [INFO] [TRIM] [DEAD_CODE] src/main/java/com/org/module/controller/TodoController.java:23
- **Finding**：`log` 字段声明后从未被引用，为死代码。
- **Evidence**：`TodoController.java:23` 声明 `private static final Logger log`，类内无任何 `log.xxx(...)` 调用（Service 侧 `TodoServiceImpl.java:35` 才是真正记日志处）。
- **Recommendation**：删除该字段及其 import（`org.slf4j.Logger`、`org.slf4j.LoggerFactory`）。

#### [INFO] [TRIM] [UNUSED_CONSTRUCTOR] src/main/java/com/org/module/dto/TodoVO.java:11
- **Finding**：`@NoArgsConstructor` 从未使用——`TodoController.java:45` 仅以 `new TodoVO(id)`（`@AllArgsConstructor`）构造。
- **Recommendation**：移除 `@NoArgsConstructor` 及对应 import，缩小不必要的公共构造面。

#### [INFO] [ALIGN] [CONTRACT_DRIFT] src/main/java/com/org/module/service/TodoService.java:19
- **Finding**：设计 4.3 内部接口签名声明 `boolean createTodo(TodoCreateRequest request)`，实现返回 `Long`（承载主键 id）。impl.md 已记录该决策并覆盖设计歧义，但 design.md 未同步。
- **Evidence**：`design.md:205` → `boolean createTodo(TodoCreateRequest request)`；`TodoService.java:19` → `Long createTodo(...)`；`TodoServiceImpl.java:25,36` 返回 `item.getId()`。
- **Recommendation**：回写 design.md 4.3 签名为 `Long createTodo(TodoCreateRequest request, String creator)`，消除文档与代码分歧（实现方向正确：`boolean` 无法承载出参 `data.id`）。

#### [INFO] [VERIFY] [TEST_GAP] src/test/java/com/org/module/service/impl/TodoServiceImplTest.java
- **Finding**：impl.md 覆盖摘要称「参数校验（经 Controller `@Valid`）✓」，但实际无 Controller 层/校验路径测试，仅 Service 层 3 个单测（正常/creator 缺失/description 为空）。
- **Evidence**：`TodoServiceImplTest.java` 共 3 个 `@Test`，均针对 `TodoServiceImpl.createTodo`；无 `@WebMvcTest`/`MockMvc` 对 `@Valid` 校验的覆盖。
- **Recommendation**：`@Valid` 为框架行为，Service 单测聚焦业务逻辑可接受；但应修正 impl.md 第 3 节覆盖摘要表述，不夸大「校验已测」。

#### [INFO] [DESIGN] [NO_ON_UPDATE] src/main/resources/db/V2__add_todo_item.sql:9
- **Finding**：`gmt_modified` 未配 `ON UPDATE CURRENT_TIMESTAMP`，与既有表（`V1__init_schema.sql:9,27` `updated_at ... ON UPDATE CURRENT_TIMESTAMP`）依赖 DB 自动维护的做法不同；设计称「修改时间，代码逻辑维护」。当前仅创建路径无影响，但未来更新路径须确保代码侧设值。
- **Recommendation**：与既有约定统一（加 `ON UPDATE CURRENT_TIMESTAMP`）或在代码侧统一填充 `gmtModified`，避免后续遗漏。

#### [INFO] [DESIGN] [ERROR_CODE_NOT_EMIT]
- **Finding**：文档定义错误码 `TODO_001/TODO_002`，但实现经 `GlobalExceptionHandler` 返回 `code=400 + 校验 message`，`TODO_001/002` 从未出现在响应中；与既有 `BusinessException.code` 同样未上报的项目模式一致，属文档分类而非接口契约。
- **Evidence**：`design.md:281-284` 错误码表；`GlobalExceptionHandler.java:29,39,49` 返回 `Result.fail(400, message)`；`README.md:34-39` 注明「返回 code 400」。
- **Recommendation**：明确错误码仅作文档分类（README 已注明），或若需对外暴露则在 `Result` 中承载业务错误码。

### Skipped lanes and reasons

- **Cause**：NOT_RUN。本期为全新增最小闭环功能，无 bug 修复/失败模式闭环声明，无根因对照对象；该车道最小上下文不满足。

### Suggested next actions

1. 修复 2 项 WARNING（时间列命名统一或显式记录分歧；补 `HttpMessageNotReadableException`→400 兜底）。
2. 清理 2 项 TRIM 死代码（`TodoController.log`、`TodoVO` 无参构造）。
3. 回写 `design.md` 4.3 接口签名为 `Long`，消除文档漂移。
4. 在具备 JDK/Maven 环境时执行 `mvn test -Dtest=TodoServiceImplTest` 与 `mvn compile` 补验（当前运行环境无 JDK/Maven，与 impl.md 第 5 节 L2 一致）。
5. 视后续迭代需要补 `@WebMvcTest` 对 `@Valid` 校验路径的 Controller 层测试。

---

## 4. 汇总 (Summary)

- **VERDICT**：`APPROVE_WITH_COMMENTS`
- **blocker_count（CRITICAL + HIGH）**：`0`
- **Advisory findings**：8 项（WARNING × 2，INFO × 6）
- **评审依据**：design.md / impl.md / 全部变更源码 / 既有工程约定（pom.xml、V1 schema、Employee 实体、Result、BusinessException、MybatisPlusConfig）。
- **产物落盘**：
  - `.agents/20260831-目标_帮助用户记录日常待办事项_核心功能/cr_report.md`（本文件）
  - `.agents/changes/task-DEV-966dcd0a-7905-11f1-9649-3b4281182f10-4b424540-07b7-4ef9-b949-efb43c4002cc/run_context.json`（`blocker_count=0`）

> 静态评审结论：本次最小闭环新增功能链路完整、分层与既有工程一致、复用 `Result/GlobalExceptionHandler` 规范、参数校验与异常兜底基本到位、单测覆盖 Service 正常与边界路径；未发现阻塞级缺陷。建议按上述 advisory 项迭代收敛命名一致性、异常兜底完备性与死代码清理。
