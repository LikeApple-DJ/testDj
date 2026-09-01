# 待办事项模块（todo）

## 模块职责

为内部用户提供「新增待办事项」能力（1.0 T2 最小闭环：仅创建）。接收事项名称与描述，从登录上下文注入创建人，落库生成待办记录。

## 关键类

| 类 | 说明 |
|----|------|
| `entity.Todo` | MyBatis-Plus 实体，@TableName("todo")，含 @Version/@TableLogic |
| `dto.TodoDTO` | 创建请求 DTO，title 必填(≤200)、description 选填(≤1000) |
| `mapper.TodoMapper` | extends BaseMapper<Todo> |
| `service.TodoService` / `impl.TodoServiceImpl` | 创建业务逻辑，注入 creator_id |
| `controller.TodoController` | POST /api/todos |
| `context.UserContext` / `context.UserContextImpl` | 登录上下文，从 X-User-Id 头获取当前用户 ID |

## 依赖关系

- 复用 `Result`、`BusinessException`、`GlobalExceptionHandler`、`MybatisPlusConfig`
- 依赖 MySQL org_db，无外部系统集成

## API 接口列表

| 方法 | 路径 | 说明 | 入参 | 出参 |
|------|------|------|------|------|
| POST | /api/todos | 新增待办事项 | title(必填), description(选填) | Result<Void>{code,msg,data} |

错误码：TODO_003 未获取到登录用户信息；R01/R02 校验失败返回 400 + 字段提示。

## 数据表

见 `src/main/resources/db/V2__todo_table.sql`（表 `todo`，索引 idx_todo_creator，无外键）。
