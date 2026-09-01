# 待办事项模块（todo）

## 模块职责

接收「新增待办事项」请求，执行入参校验与业务规则校验，持久化待办事项记录并返回生成的事项 ID。

## 关键类说明

| 类 | 层 | 说明 |
|----|----|------|
| TodoItemController | Controller | 对外接口 O01：POST /openapi/todo/items，含应急功能开关 |
| TodoItemService | Service 接口 | createTodoItem(CreateTodoRequest) → CreateTodoResult |
| TodoItemServiceImpl | Service 实现 | 业务规则 R01-R05 校验、数据组装、落库、异常捕获 |
| TodoItemMapper | DAO | MyBatis 映射，insert 并回写主键 |
| TodoItemDO | Entity | 对应 todo_item 表 |
| CreateTodoRequest | DTO | 入参：title / description |
| CreateTodoResult | DTO | 出参：id |
| TodoStatusEnum | Enum | PENDING / DONE / DELETED |
| TodoErrorCodeEnum | Enum | TODO_001 ~ TODO_999 |
| Result | Common | 通用出参结构 |

## 依赖关系

- 依赖 MySQL（通过 MyBatis + JDBC）。
- 无其他模块依赖（本期最小闭环，单一模块）。

## API 接口列表

| 编号 | 方法 | 路径 | 说明 |
|------|------|------|------|
| O01 | POST | /openapi/todo/items | 新增待办事项 |

## 业务规则

| 规则 | 说明 | 错误码 |
|------|------|--------|
| R01 | title 不能为空 | TODO_001 |
| R02 | title 长度 ≤ 100 | TODO_002 |
| R03 | description 长度 ≤ 1000 | TODO_003 |
| R04 | status 默认 PENDING | - |
| R05 | creator/tenant_id 上下文带入，缺失写默认值不阻断 | - |

## 数据表

`todo_item`：id / tenant_id / title / description / status / creator / gmt_create / gmt_modified。详见 `src/main/resources/sql/schema.sql`。
