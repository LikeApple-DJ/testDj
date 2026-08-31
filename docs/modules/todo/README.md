# todo 模块 — 待办事项

> 版本: v1.0 | 日期: 2026-08-31 | 作者: AiWork

## 1. 模块职责

待办事项的创建与持久化。最小闭环仅支持新增（POST /api/todo/items）。

## 2. 关键类说明

| 类 | 层级 | 说明 |
|----|------|------|
| `TodoItemController` | Web | 接收 HTTP 请求，参数校验，转发 Service |
| `TodoItemService` | Service | 业务服务接口 |
| `TodoItemServiceImpl` | Service | 业务逻辑实现：校验、组装、持久化 |
| `TodoItemMapper` | DAO | MyBatis 数据访问接口 |
| `TodoItemDO` | Model | 数据对象，对应 `todo_item` 表 |
| `TodoItemCreateRequest` | Model | 请求 DTO（title + description） |
| `TodoItemVO` | Model | 响应 VO（id） |

## 3. 依赖关系

- MySQL（JDBC）：待办事项持久化
- 无外部系统集成

## 4. API 接口列表

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| W01 | POST | /api/todo/items | 新增待办事项，返回事项ID |

## 5. 数据表

**todo_item**

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | BIGINT | PK, 自增 | 主键 |
| title | VARCHAR(100) | NOT NULL | 事项名称 |
| description | VARCHAR(500) | NULL | 事项描述 |
| creator | VARCHAR(64) | NOT NULL | 创建人标识 |
| gmt_create | DATETIME | NOT NULL | 创建时间 |
| gmt_modified | DATETIME | NOT NULL | 修改时间 |

索引：PK(id), idx_todo_item_creator(creator)
