# 待办事项模块（todo）

## 模块职责

为内部用户提供日常待办事项的记录能力，当前最小闭环支持「新增待办事项」。

## 关键类说明

| 类 | 层 | 说明 |
|----|----|------|
| `Todo` | entity | 待办事项数据对象，映射 `todo` 表，含乐观锁与逻辑删除字段 |
| `TodoDTO` | dto | 新增请求入参，含 `title`(必填)、`description`(可选) 校验 |
| `TodoVO` | dto | 创建后展示对象，返回主键与状态 |
| `TodoMapper` | mapper | MyBatis-Plus `BaseMapper` 数据访问 |
| `TodoService` | service | 业务接口，`createTodo` |
| `TodoServiceImpl` | service/impl | 业务实现，事务内落库并返回 VO |
| `TodoController` | controller | `POST /api/todos` 创建接口 |

## 依赖关系

- 依赖 MyBatis-Plus `IService` / `ServiceImpl` 与全局 `MybatisPlusConfig`（分页 + 乐观锁拦截器）。
- 复用全局 `Result<T>`、`BusinessException`、`GlobalExceptionHandler`。
- 无跨业务模块依赖。

## API 接口列表

| 方法 | 路径 | 入参 | 出参 |
|------|------|------|------|
| POST | `/api/todos` | `TodoDTO`(title, description) | `Result<TodoVO>` |

### 请求示例

```json
POST /api/todos
{
  "title": "完成周报",
  "description": "本周工作总结与下周计划"
}
```

### 响应示例

```json
{
  "code": 200,
  "msg": "SUCCESS",
  "data": {
    "id": 1,
    "title": "完成周报",
    "description": "本周工作总结与下周计划",
    "status": 0,
    "createdAt": "2026-09-01T10:00:00"
  }
}
```

## 数据模型

表 `todo`：id、title(200)、description(1000)、status(0待处理/1已完成)、version、is_deleted、created_at、updated_at。
DDL：`src/main/resources/db/V2__todo_schema.sql`。
