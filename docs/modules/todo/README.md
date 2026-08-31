# 待办事项模块（todo）

## 模块职责

待办事项的创建与持久化，提供"新增待办事项"能力（POST /api/todo/create）。

## 关键类

| 类 | 层级 | 说明 |
|----|------|------|
| `TodoController` | Controller | POST /api/todo/create 入口，@Valid 参数校验 |
| `TodoService` / `TodoServiceImpl` | Service | 业务规则 R01-R05，唯一性校验，默认值注入 |
| `TodoMapper` | DAO | MyBatis 映射，insert + selectByTenantAndName |
| `TodoDO` | Entity | biz_todo 表数据对象 |
| `TodoCreateRequest` | DTO | 入参：name(必填 1-128) + description(选填 0-1024) |
| `TodoCreateResponse` | DTO | 出参：id（新建待办事项ID） |
| `TodoStatus` | Enum | 状态枚举：0-待处理/1-进行中/2-已完成 |
| `IsDeleted` | Enum | 逻辑删除：0-未删除/1-已删除 |
| `LoginInterceptor` | Config | 登录态校验，注入 UserContext |
| `GlobalExceptionHandler` | Common | 统一异常到 ApiResponse |

## 依赖关系

- 依赖 MySQL（biz_todo 表）
- 依赖 UserContext（ThreadLocal 登录态）

## API 接口列表

| 编号 | 方法 | 路径 | 说明 |
|------|------|------|------|
| W01 | POST | /api/todo/create | 新增待办事项，返回 data.id |
