# 待办事项记录（创建）— 编码实现文档

> **文档版本**: v1.0
> **日期**: 2026-08-31
> **阶段**: 编码实现
> **关联系分**: `.agents/20260831-目标_帮助用户记录日常待办事项_核心功能/design.md`
> **技能**: dtazziboot-java-coding-standards
> **技术栈**: Spring Boot 3.2.0 + Spring Data JPA + PostgreSQL（JDK 17 / Maven）

---

## 1. 模块进度追踪

| 序号 | 模块 | READ | TEST | IMPL | CHECK | DOCS | 状态 |
|:----:|------|:----:|:----:|:----:|:-----:|:----:|------|
| 1 | todo（新增待办事项） | ✅ | ✅ | ✅ | ✅ | ✅ | 已完成 |

---

## 2. 各阶段产出摘要

### 2.1 READ
- 读取系分方案，确认 F01/W01/S01 契约、表结构、错误码、业务规则 R01~R04。
- 加载规范：naming.md、exception-logging.md、unit-testing.md、project-structure.md、frontend-backend.md。
- 关键契约：`POST /api/todo`、通用出参 `{ result, msg, data }`、错误码 `TODO_0001~0004`。

### 2.2 TEST（TDD，先生成单测）
- 被测类 SUT：`TodoServiceImpl`、`TodoController`。
- 框架：JUnit 5 + Mockito（`@ExtendWith(MockitoExtension.class)`，strict stubs）+ AssertJ + MockMvc。

**测试文件**：`src/test/java/com/example/todo/service/impl/TodoServiceImplTest.java`

| 方法 | 测试场景 | 状态 |
|------|----------|:----:|
| should_returnCreateResult_when_requestIsValid | 正常路径：返回结果字段映射正确 | ✅ |
| should_persistTodoWithCorrectFields_when_requestIsValid | 落库实参校验（name/description/creatorId/tenantId/时间） | ✅ |
| should_setCreatorIdNull_when_userContextReturnsNull | 边界：未登录 creatorId 为 null | ✅ |
| should_throwSystemError_when_repositoryThrowsDataAccessException | 异常：落库失败 → TODO_0004 | ✅ |

**测试文件**：`src/test/java/com/example/todo/controller/TodoControllerTest.java`

| 方法 | 测试场景 | 状态 |
|------|----------|:----:|
| should_returnOk_when_requestIsValid | 正常路径 200 + result=OK | ✅ |
| should_returnNameEmpty_when_nameIsBlank | 参数校验：name 空白 → TODO_0001 | ✅ |
| should_returnNameTooLong_when_nameExceeds200 | 参数校验：name 超长 → TODO_0002 | ✅ |
| should_returnDescriptionTooLong_when_descriptionExceeds2000 | 参数校验：desc 超长 → TODO_0003 | ✅ |

**测试覆盖摘要**:
- 被测类: TodoServiceImpl (4)、TodoController (4)
- 测试方法数: 8
- 覆盖场景: 正常路径 ✓、参数校验 ✓、异常处理 ✓、边界值 ✓、副作用 verify ✓

### 2.3 IMPL
**已实现文件**：
- `pom.xml`
- `src/main/resources/application.yml`
- `src/test/resources/application-test.yml`
- `src/main/java/com/example/todo/TodoApplication.java`
- `src/main/java/com/example/todo/config/WebConfig.java`
- `src/main/java/com/example/todo/model/Todo.java`
- `src/main/java/com/example/todo/repository/TodoRepository.java`
- `src/main/java/com/example/todo/dto/ApiResponse.java`
- `src/main/java/com/example/todo/dto/TodoCreateRequest.java`
- `src/main/java/com/example/todo/dto/TodoCreateResult.java`
- `src/main/java/com/example/todo/exception/TodoErrorCode.java`
- `src/main/java/com/example/todo/exception/TodoException.java`
- `src/main/java/com/example/todo/exception/GlobalExceptionHandler.java`
- `src/main/java/com/example/todo/context/UserContext.java`
- `src/main/java/com/example/todo/context/DefaultUserContext.java`
- `src/main/java/com/example/todo/service/TodoService.java`
- `src/main/java/com/example/todo/service/impl/TodoServiceImpl.java`
- `src/main/java/com/example/todo/controller/TodoController.java`

**编译验证**：⚠️ 环境受限（详见 CHECK L2）

### 2.4 CHECK

#### L1 静态检查

| 检查项 | 规范要求 | 符合情况 |
|--------|----------|:--------:|
| 命名规范 | 类名大驼峰、方法名小驼峰、常量全大写 | ✅ |
| 前后端契约 | API 路径 /api/todo、响应 `{ result, msg, data }` | ✅ |
| 异常日志 | SLF4J + 占位符、自定义异常 TodoException、ERROR 含堆栈 | ✅ |
| 安全规范 | JPA 参数化绑定、输入校验 @Valid、@NotBlank/@Size | ✅ |
| 表设计 | 表名小写、必备字段 gmt_create/gmt_modified、@Index 预留 | ✅ |
| 单元测试 | 测试类存在、AAA 模式、AssertJ 断言、副作用 verify | ✅ |
| 注释 | 类/方法 Javadoc（`/** */`） | ✅ |
| OOP | POJO 覆写 toString、equals 基于 id | ✅ |

#### L2 动态验证

| 验证项 | 状态 | 说明 |
|--------|:----:|------|
| 环境探测 (`mvn -version`/`java -version`) | ⚠️ | 运行环境未安装 JDK/Maven/Gradle，无法本地编译 |
| 编译验证 | ⚠️ | 跳过（原因：环境无 JDK/Maven） |
| 单测验证 | ⚠️ | 跳过（原因：环境无 JDK/Maven） |

#### 待人工验证

```bash
mvn compile -DskipTests
mvn test -Dtest=TodoServiceImplTest,TodoControllerTest
```

**静态审查结论**：逐类核对 import、类型、分支与契约映射，未发现编译级问题；strict-stubs 已规避（各用例内联 stub）。剩余风险为运行时类路径依赖（Spring Boot 3.2 starter 全量提供），建议在含 JDK17+Maven 环境执行上述命令确认。

### 2.5 DOCS
- 架构文档：新建 `docs/ARCHITECTURE.md`
- 模块文档：新建 `docs/modules/todo/README.md`
- 编码报告：已写入本文件

---

## 3. 设计契约对齐

| 契约项 | 设计值 | 实现 | 状态 |
|--------|--------|------|:----:|
| 接口 | POST /api/todo | TodoController `@PostMapping("/todo")` + `@RequestMapping("/api")` | ✅ |
| 通用出参 | { result, msg, data } | ApiResponse | ✅ |
| name 校验 | 必填 1~200 | @NotBlank @Size(max=200) → TODO_0001/0002 | ✅ |
| description 校验 | 0~2000 | @Size(max=2000) → TODO_0003 | ✅ |
| 落库失败 | TODO_0004 | catch DataAccessException → TodoException(SYSTEM_ERROR) | ✅ |
| creator_id | 登录态 | UserContext（X-User-Id） | ✅ |
| tenant_id | 预留默认0 | DEFAULT_TENANT_ID=0L | ✅ |
| 时间字段 | gmt_create/gmt_modified | 实体字段 + 服务端 LocalDateTime.now() | ✅ |
| 事务 | 失败回滚 | @Transactional(rollbackFor=Exception.class) | ✅ |
| 表结构 | todo 表 + 2 索引 | Todo 实体 @Table + @Index | ✅ |

---

## 4. 需求追溯

| 需求点 | 对应实现 | 状态 |
|--------|----------|:----:|
| 新增待办事项 | TodoController + TodoServiceImpl | ✅ |
| 任务信息：名称+描述 | TodoCreateRequest.name/description | ✅ |
| 落库返回 ID+创建时间 | TodoCreateResult.id/gmtCreate | ✅ |
| 最小闭环：仅创建 | 无查询/编辑/删除 | ✅ |
| 目标用户：内部用户 | UserContext + 预留登录拦截器 | ✅ |
