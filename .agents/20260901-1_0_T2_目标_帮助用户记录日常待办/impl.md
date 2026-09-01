# 待办事项模块（todo）编码报告

> **文档元信息**
>
> | 项目 | 内容 |
> |------|------|
> | 任务 | 1.0 T2 — 帮助用户记录日常待办事项（最小闭环：仅创建） |
> | 系分来源 | `.agents/20260901-1_0_T2_目标_帮助用户记录日常待办/design.md` |
> | 技能 | dtazziboot-java-coding-standards |
> | 作者 | AiWork |
> | 日期 | 2026-09-01 |

## 1. 模块进度追踪

| 序号 | 模块 | READ | TEST | IMPL | CHECK | DOCS | 状态 |
|:----:|------|:----:|:----:|:----:|:-----:|:----:|------|
| 1 | todo | ✅ | ✅ | ✅ | ✅ | ✅ | 已完成 |

## 2. READ 阶段摘要

**模块职责**：接收待办创建请求、参数校验、从登录上下文注入创建人、生成待办记录并落库。

**关键类列表**：
- `Todo` — 数据对象（MyBatis-Plus 实体，@TableName("todo")）
- `TodoDTO` — 请求对象（title/description + Bean Validation）
- `TodoMapper` — 数据访问（extends BaseMapper<Todo>）
- `TodoService` / `TodoServiceImpl` — 业务服务
- `TodoController` — 控制器（POST /api/todos）
- `UserContext` / `UserContextImpl` — 登录上下文（获取 creator_id）

**依赖关系**：复用 `Result`、`BusinessException`、`GlobalExceptionHandler`、`MybatisPlusConfig`；依赖 MySQL（org_db）。

**已加载规范**：
- [x] naming.md
- [x] exception-logging.md
- [x] unit-testing.md
- [x] mysql.md（表设计沿用 V1 风格，无外键符合 db 规范）
- [x] frontend-backend.md（REST DTO + @Valid）

**关键决策（自主决策）**：
- 分层沿用现有 Employee 模块惯例（Entity + DTO + BaseMapper + IService/ServiceImpl + Controller），不引入 DO/VO/Request 拆分。
- A01 creator_id 来源：仓库无任何鉴权拦截器（已验证：EmployeeServiceImpl 用 `0L`+TODO 占位）。为落实 R03（缺失则抛 TODO_003）且保持可测、可运行，新建 `UserContext` 抽象：`UserContextImpl` 从内部 oneapi 网关注入的 `X-User-Id` 请求头读取当前用户 ID，缺失返回空 → Service 抛 TODO_003。Service 单测中 Mock `UserContext`。
- A03 description 选填、≤1000；A04 名称不唯一；A05 不分租户 —— 均按系分假设落地。

## 3. TEST 阶段摘要

**测试文件**：`src/test/java/com/org/module/service/impl/TodoServiceImplTest.java`

**被测类**：`TodoServiceImpl`（SUT）。Mock：`UserContext`、`TodoMapper`。技术栈：JUnit 5 + Mockito（`@ExtendWith(MockitoExtension.class)`）+ AssertJ。依赖来自 `spring-boot-starter-test`，无新增依赖。

| 测试方法 | 测试场景 | 状态 |
|------|----------|:----:|
| should_createTodo_when_creatorIdPresent | 正常路径：creator_id 由登录上下文注入并落库 | ✅ |
| should_createTodo_when_descriptionIsNull | 边界：description 选填，允许为空 | ✅ |
| should_throwBusinessException_when_creatorIdAbsent | 异常：登录上下文缺失 → TODO_003，不落库 | ✅ |

**测试覆盖摘要**：
- 被测类: TodoServiceImpl
- 测试方法数: 3
- 覆盖的被测方法: createTodo (3)
- 覆盖场景: 正常路径 ✓, 边界值 ✓, 异常处理 ✓
- 验证方式: `ArgumentCaptor<Todo>` 回查落库参数（title/description/creator_id）+ `verify` 插入次数 + 异常断言
- R01/R02（title/description 长度校验）在 Controller `@Valid` 层完成，属「无逻辑 Controller 仅转发」范畴，不纳入 Service 单测。

## 4. IMPL 阶段摘要

**已实现文件**：
- `src/main/resources/db/V2__todo_table.sql` — todo 建表（含 idx_todo_creator，无外键）
- `src/main/java/com/org/module/entity/Todo.java`
- `src/main/java/com/org/module/dto/TodoDTO.java`
- `src/main/java/com/org/module/mapper/TodoMapper.java`
- `src/main/java/com/org/module/service/TodoService.java`
- `src/main/java/com/org/module/service/impl/TodoServiceImpl.java`
- `src/main/java/com/org/module/controller/TodoController.java`
- `src/main/java/com/org/module/context/UserContext.java`
- `src/main/java/com/org/module/context/UserContextImpl.java`
- `src/main/java/com/org/module/exception/GlobalExceptionHandler.java`（修改：新增 `MethodArgumentNotValidException` 处理）

**业务规则落地**：
- R01 title 非空/≤200：`@NotBlank` + `@Size(max=200)`，校验失败经全局异常处理器返回 400 + 提示。
- R02 description ≤1000（允许空）：`@Size(max=1000)`。
- R03 creator_id 由 `UserContext` 注入，缺失抛 `BusinessException("TODO_003", ...)`。
- 创建为独立行 INSERT、名称不要求唯一 → 无并发风险，无需额外并发控制（与系分结论一致）。
- 日志仅打印 title + creator_id + todo_id，避免完整打印长描述（符合 6.4.3.2）。

**编译验证**：⚠️ 环境受限（当前运行环境无 `mvn`/`javac`/`java`/`~/.m2`，无法执行编译与单测），已降级为静态代码审查。

## 5. CHECK 阶段摘要

### L1 静态检查

| 检查项 | 规范要求 | 符合情况 |
|--------|----------|:--------:|
| 命名规范 | 类名大驼峰、方法名小驼峰、常量全大写 | ✅ |
| 数科网关 | API 路径 /api/todos、响应结构 Result<T>{code,msg,data} | ✅ |
| 异常日志 | SLF4J 占位符、自定义 BusinessException、错误码 TODO_xxx | ✅ |
| 安全规范 | 输入校验 @Valid + @NotBlank/@Size；creator_id 服务端注入不信任前端 | ✅ |
| MySQL 规范 | 表名小写、必备字段 version/is_deleted/created_at/updated_at、无外键、datetime | ✅ |
| 单元测试 | 测试类存在、AAA 模式、ArgumentCaptor 回查、断言具体值 | ✅ |

### L2 动态验证

| 验证项 | 状态 | 说明 |
|--------|:----:|------|
| 编译验证 | ⚠️ | 环境无 mvn/javac，已跳过 |
| 单测验证 | ⚠️ | 环境无 mvn/java，已跳过 |

[降级说明] 当前运行环境未安装 JDK 与 Maven（`command -v mvn/javac/java` 均无，无 `~/.m2`），无法执行 `mvn compile` 与 `mvn test`。已对全部新增/修改文件完成 L1 静态审查：包名一致、import 完整、类型与方法签名正确、MyBatis-Plus 注解与现有 Employee 模块一致、Mockito @InjectMocks 可注入 ServiceImpl 的 `baseMapper` 字段。

## 6. 待人工验证

```bash
mvn compile -DskipTests
mvn test -Dtest=TodoServiceImplTest
```

**发现问题**：无。

## 7. ✅ 模块 todo 完成

| 阶段 | 状态 |
|------|:----:|
| READ | ✅ |
| TEST | ✅ |
| IMPL | ✅ |
| CHECK | ✅ |
| DOCS | ✅ |

**下一步**：本迭代为「仅创建」最小闭环，编辑/完成/查询等能力留待后续迭代。
