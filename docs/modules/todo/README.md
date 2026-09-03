# todo 模块

## 职责

管理待办事项的创建，提供新增待办事项的 REST API 接口。

## 关键类

| 类 | 职责 |
|----|------|
| TodoItemDO | 待办事项数据实体，映射 todo_item 表 |
| TodoItemMapper | MyBatis 数据访问层，提供 insert/selectById 操作 |
| TodoService | 待办事项业务服务接口 |
| TodoServiceImpl | 待办事项业务服务实现，包含参数校验、实体构建、数据库写入 |
| TodoController | REST 控制器，暴露 POST /api/todo/create 接口 |
| CreateTodoRequest | 创建待办事项请求 DTO，包含 title/description 字段及校验注解 |

## 依赖

- 向下依赖：todo-common（ApiResponse、BusinessException）
- 依赖中间件：MySQL（数据持久化）

## API 接口列表

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/todo/create | 新增待办事项 |

### 错误码

| 错误码 | 说明 |
|--------|------|
| TODO_001 | 事项名称不能为空 |
| TODO_002 | 事项名称长度超过限制（100字符） |
| TODO_003 | 事项描述长度超过限制（500字符） |