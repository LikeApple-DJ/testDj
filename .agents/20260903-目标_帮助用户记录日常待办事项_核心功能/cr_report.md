# 代码评审报告 - 待办事项模块

> **评审日期**: 2026-09-03
> **系分方案**: `.agents/20260903-目标_帮助用户记录日常待办事项_核心功能/design.md`
> **实现报告**: `.agents/20260903-目标_帮助用户记录日常待办事项_核心功能/impl-report.md`
> **评审技能**: code-review (Align / Design / Trim / Cause / Verify)

---

## Project Profile

| 项目 | 内容 |
|------|------|
| State | `FIXED` |
| Source | 仓库根目录无 `REVIEW.md` 文件 |
| Notes | 按产物约束跳过 REVIEW.md 创建，使用通用评审通道 |

---

## Lane Verdict Table

| Lane | Verdict | Notes |
|------|---------|-------|
| `align` | `REJECT` | 2 项合同漂移：Service 方法签名与设计不一致；空白标题错误码与设计不一致 |
| `design` | `APPROVE_WITH_COMMENTS` | 架构合理，边界清晰，但 user_id 归属层有歧义 |
| `trim` | `APPROVE_WITH_COMMENTS` | 存在 1 处死代码 (selectById) |
| `cause` | `NOT_RUN` | 本次为新增代码，非缺陷修复 |
| `verify` | `APPROVE_WITH_COMMENTS` | 测试覆盖充分，但缺少异常类型扩展覆盖 |

---

## Blocking Findings

### [HIGH] [align] [CLAIM-DRIFT] Service 方法签名与系分设计不一致

**位置**: `src/main/java/com/example/todo/todo/service/TodoService.java:17`
**系分位置**: `.agents/.../design.md:173` (S01)

**问题描述**:
系分设计文档中内部接口表定义 Service 方法签名为 `createTodo(CreateTodoRequest)`，但实际实现为 `createTodo(CreateTodoRequest, String userId)`。系分文档的时序图显示 "Svc->>Svc: 设置默认值（status=待办, user_id）"，暗示 user_id 在 Service 内部解析，但实际实现将 user_id 从 Controller 传入。

**证据**:
- Design.md 第 173 行: `S01 | 创建待办事项 | TodoService | createTodo(CreateTodoRequest)`
- TodoService.java 第 17 行: `Long createTodo(CreateTodoRequest request, String userId);`
- Design.md 第 294 行 (时序图): `Svc->>Svc: 设置默认值（status=待办, user_id）`

**建议**:
- 方案 A：更新系分文档，将方法签名修正为 `createTodo(CreateTodoRequest, String userId)`，明确 user_id 由 Controller 层解析
- 方案 B：将 user_id 解析逻辑移入 Service 层，保持与设计文档一致

---

### [HIGH] [align] [API-CONTRACT] @Valid 校验返回错误码 A0001 而非系分约定的 TODO_001

**位置**: `src/main/java/com/example/todo/todo/controller/TodoController.java:38`
**系分位置**: `.agents/.../design.md:248` (TODO_001)

**问题描述**:
系分设计约定事项名称为空时返回错误码 `TODO_001`。但 Controller 上使用了 `@Valid @RequestBody CreateTodoRequest`，其中 `@NotBlank` 校验失败时会抛出 `MethodArgumentNotValidException`，被 `GlobalExceptionHandler` 捕获后返回错误码 `A0001`（通用参数校验错误码），而非系分约定的 `TODO_001`。

这意味着经由 REST API 请求时，空白标题的响应为 `{code: "A0001", msg: "title: 事项名称不能为空"}`，而非系分设计的 `{code: "TODO_001", msg: "事项名称不能为空"}`。

**证据**:
- CreateTodoRequest.java 第 11 行: `@NotBlank(message = "事项名称不能为空")`
- GlobalExceptionHandler.java 第 41 行: `return ApiResponse.error("A0001", message);`
- Design.md 第 248 行: `TODO_001 | 事项名称不能为空`
- Design.md 第 307 行: `R01 | 事项名称不能为空 | 创建时 | 返回错误码 TODO_001`

**建议**:
- 方案 A：移除 `CreateTodoRequest` 上的 `@NotBlank` 注解，完全依赖 Service 层的防御性校验返回 TODO_001
- 方案 B：在 Controller 中捕获 `MethodArgumentNotValidException` 并转换为 `BusinessException(TODO_001, ...)` 抛出

---

## Advisory Findings

### [WARNING] [align] [DOC-DRIFT] 系分文档中 Controller 接口路径未包含上下文路径

**位置**: `.agents/.../design.md:163` / `TodoController.java:20`

**问题描述**:
Controller 的 `@RequestMapping("/api/todo")` 与 `application.yml` 中的 `server.servlet.context-path`（未配置，默认为空）一致，但系分文档中定义的接口路径为 `/api/todo/create`，实际为 `POST /api/todo/create`。路径一致，无实质问题，但系分文档中接口编号 W01 的表头参数与设计文档其他地方描述一致，无需修改。

**结论**: 非实质问题，供参考。

---

### [WARNING] [trim] [DEAD-CODE] selectById 方法在当前最小闭环范围内未被使用

**位置**: `src/main/java/com/example/todo/todo/dao/mapper/TodoItemMapper.java:27`
**对应 XML**: `src/main/resources/mapper/TodoItemMapper.xml:25-30`

**问题描述**:
`TodoItemMapper` 中定义了 `selectById(@Param("id") Long id)` 方法，以及对应的 XML 映射。当前最小闭环仅包含"创建"功能，`selectById` 方法未被任何调用方使用。虽然为后续查询功能预留了拓展点，但违反了最小闭环"不引入不必要的代码"的原则。

**建议**:
- 当前阶段可保留（作为合理的未来扩展预留），但建议添加注释说明其为未来扩展预留
- 或删除，待需要查询功能时再添加

---

### [WARNING] [verify] [TEST-GAP] 缺少数据库异常类型的测试覆盖

**位置**: `src/test/java/.../TodoServiceImplTest.java:156-169`

**问题描述**:
测试 `should_throwException_when_insertFails` 仅覆盖了 `insert` 返回 0 的场景。当数据库连接异常、主键冲突或 `DataAccessException` 抛出时，ServiceImpl 中未捕获（`insert` 调用未包裹 try-catch），异常会传播到 `GlobalExceptionHandler` 的 `handleException` 方法，返回 `B0001`。

**建议**: 增加测试覆盖 `insert` 抛出 `DataAccessException`（或 `RuntimeException`）的场景，验证全局异常处理是否能正确返回 B0001。

---

### [INFO] [design] [OWNER-DRIFT] user_id 硬编码为 "SYSTEM" 与系分设计假设 A01 一致但未体现"自动记录"

**位置**: `src/main/java/com/example/todo/todo/controller/TodoController.java:41`

**问题描述**:
Controller 中固定使用 `userId = "SYSTEM"`，注释说明"暂未接入统一登录态"。系分设计假设 A01 说明"假设通过统一登录态获取用户ID"，当前实现与假设一致，但"自动记录"的设计意图未体现。

**建议**: 当前阶段可接受，后续接入统一登录态后替换为从 SecurityContext 获取。

---

### [INFO] [verify] 双重校验（@Valid + Service 层校验）为防御性设计

**位置**: `CreateTodoRequest.java:11-12` / `TodoServiceImpl.java:32-41`

**问题描述**:
Controller 层通过 `@Valid` 进行声明式校验，Service 层又进行了防御性校验。这是典型的"防御性编程"模式，但如前所述，@Valid 的校验结果错误码（A0001）与系分设计不符。

**建议**: 保持双重校验机制，但需对齐错误码。

---

## Skipped Lanes and Reasons

| Lane | Reason |
|------|--------|
| `cause` | 本次评审为新增代码，非缺陷修复，无"根因闭合"场景可评审 |

---

## Suggested Next Actions

1. **修复 Blocking 问题**（优先级高）：
   - 对齐 Service 方法签名与设计文档（或更新设计文档）
   - 对齐空白标题的错误码为 TODO_001（移除 DTO 上的 @NotBlank 或增加 Controller 层转换逻辑）

2. **处理 Advisory 问题**：
   - 评估 `selectById` 是否保留，若保留建议添加注释
   - 补充数据库异常类型的测试覆盖

3. **文档对齐**：更新系分文档中 Service 接口方法签名（若选择方案 A）

---

## VERDICT: REJECT

存在 2 项 HIGH 阻塞性发现，需修复后方可合并。