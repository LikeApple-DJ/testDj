# 待办事项模块 模块文档

> 模块代号：`TODO` ｜ 对应表：`todo_item` ｜ 对应系分：`.agents/20260831-目标_帮助用户记录日常待办事项_核心功能/design.md`

## 1. 模块职责

支持内部用户新增待办事项，记录事项名称与描述，创建即落库并返回主键。本期为最小可用闭环（仅创建），不引入状态/查询/编辑/删除等能力。

## 2. 关键类说明

| 类 | 所在包 | 职责 |
|----|--------|------|
| `TodoItem` | `com.org.module.entity` | 数据对象，映射 `todo_item` 表 |
| `TodoCreateRequest` | `com.org.module.dto` | 新增请求体（name + description），含 Bean Validation 注解 |
| `TodoVO` | `com.org.module.dto` | 新增响应体（id） |
| `TodoMapper` | `com.org.module.mapper` | 数据访问，继承 MyBatis-Plus `BaseMapper` |
| `TodoService` | `com.org.module.service` | 业务服务接口 |
| `TodoServiceImpl` | `com.org.module.service.impl` | 业务服务实现，组装实体并落库 |
| `TodoController` | `com.org.module.controller` | REST 控制器，`POST /api/todo` |

## 3. 依赖关系

- 复用工程既有 `Result<T>`、`BusinessException`、`GlobalExceptionHandler`（已扩展校验异常兜底）。
- 持久层：MyBatis-Plus `BaseMapper`，单表插入，无 XML。
- 参数校验：`spring-boot-starter-validation`（`@Valid` + `@NotBlank`/`@Size`）。
- 无外部系统/第三方服务集成。

## 4. API 接口列表

| 编号 | 接口名称 | 方法 | 路径 | 说明 |
|------|----------|------|------|------|
| W01 | 新增待办事项 | POST | `/api/todo` | 入参 name/description；请求头 `X-User-Id` 作为 creator；返回 `{code,msg,data:{id}}` |

### 错误码（内部分类）

| 错误码 | 说明 | 返回 code |
|--------|------|-----------|
| TODO_001 | 事项名称不能为空或长度超限（1–128） | 400 |
| TODO_002 | 事项描述长度超限（>1024） | 400 |
