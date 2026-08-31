# 待办事项模块 (todo)

> **功能**: F01 新增待办事项
> **接口**: W01 `POST /api/todo`

## 1. 模块职责

接收新增待办请求、参数校验、持久化、返回创建结果（事项ID与创建时间）。构成"仅创建"最小闭环。

## 2. 关键类

| 类 | 类型 | 职责 |
|----|------|------|
| `Todo` | JPA 实体 | 映射 todo 表，含 name/description/creator_id/tenant_id/gmt_create/gmt_modified |
| `TodoRepository` | JpaRepository | 待办事项数据访问 |
| `TodoCreateRequest` | DTO | 入参：name(必填,1~200) / description(选填,0~2000) |
| `TodoCreateResult` | DTO | 出参 data：id/name/description/creatorId/gmtCreate |
| `ApiResponse<T>` | DTO | 通用响应包装 `{ result, msg, data }` |
| `TodoService` / `TodoServiceImpl` | Service | 业务编排：设置 creatorId/tenantId → 落库 → 组装结果 |
| `TodoController` | Controller | POST /api/todo 入口，`@Valid` 参数校验 |
| `UserContext` / `DefaultUserContext` | 组件 | 读取登录态用户ID（请求头 X-User-Id） |
| `TodoErrorCode` | 枚举 | 错误码 TODO_0001~TODO_0099 |
| `TodoException` | 异常 | 业务异常载体 |
| `GlobalExceptionHandler` | ControllerAdvice | 参数校验/业务/系统异常统一映射为 ApiResponse |

## 3. 依赖关系

- 依赖 PostgreSQL（生产）/ H2（测试）。
- 依赖全局登录拦截器（本期假设，由 UserContext 读取 creator_id）。
- 无第三方系统集成。

## 4. API 接口列表

| 编号 | 方法 | 路径 | 说明 |
|------|------|------|------|
| W01 | POST | `/api/todo` | 新增待办事项 |

### 4.1 W01 详细契约

- **入参**: `{ name: String(必填,1~200), description: String(选填,0~2000) }`
- **出参**: `{ result:"OK", msg:"SUCCESS", data:{ id, name, description, creatorId, gmtCreate } }`
- **错误码**:

| 错误码 | 说明 |
|--------|------|
| TODO_0001 | name 参数缺失或为空 |
| TODO_0002 | name 长度超过 200 字符 |
| TODO_0003 | description 长度超过 2000 字符 |
| TODO_0004 | 系统内部错误（落库失败） |
| TODO_0099 | 服务维护中（特性开关） |

## 5. 表结构（todo）

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| id | bigint | PK, 自增 | 系统主键 |
| name | varchar(200) | NOT NULL | 事项名称 |
| description | varchar(2000) | NULL | 事项描述 |
| creator_id | bigint | NULL | 创建人ID |
| tenant_id | bigint | NOT NULL | 租户ID（默认0） |
| gmt_create | datetime | NOT NULL | 创建时间 |
| gmt_modified | datetime | NOT NULL | 修改时间 |

索引：`idx_todo_creator(creator_id)`、`idx_todo_tenant(tenant_id)`（JPA `@Index` 声明，预留后续查询）。
