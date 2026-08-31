# 待办模块 (Todo)

## 模块职责

待办事项新增的参数校验、业务规则校验、租户标识注入、持久化与结果返回。

## 关键类说明

| 类 | 层次 | 说明 |
|----|------|------|
| TodoDO | Entity | 映射 todo 表，含 id/name/description/tenant_id/gmt_create/gmt_modified |
| TodoCreateRequest | DTO | 新增请求，含 name（@NotBlank/@Size(max=100)）、description（@Size(max=500)） |
| TodoCreateResult | DTO | 新增结果，含 id |
| TodoRepository | DAO | Spring Data JPA Repository，提供 save 能力 |
| TodoService | Service | 业务接口，定义 createTodo 方法 |
| TodoServiceImpl | Service | 业务实现，校验规则 R01-R03、注入 tenant_id、持久化 |
| TodoController | Controller | POST /api/todos，@Valid 参数校验后委托 Service |
| TodoErrorCodeEnum | Enum | 错误码枚举 TODO_0001-TODO_0004 |
| TodoConstants | Constant | 常量：DEFAULT_TENANT_ID/MAX_NAME_LENGTH/MAX_DESCRIPTION_LENGTH |
| ApiResponse | Model | 通用出参 {result, msg, data} |
| BusinessException | Exception | 业务异常，承载 errorCode/errorMessage |
| GlobalExceptionHandler | Advice | 全局异常处理器 |

## 依赖关系

- 依赖 MySQL（通过 Spring Data JPA）
- 无其他模块依赖（本期单一模块）

## API 接口列表

| 编号 | 方法 | 路径 | 说明 |
|------|------|------|------|
| W01 | POST | /api/todos | 新增待办事项，返回 {result:OK, data:{id}} |

### W01 新增待办事项

- **入参**：name（必填，≤100字符）、description（选填，≤500字符）
- **出参**：`{result:"OK", msg:"SUCCESS", data:{id:Long}}`
- **错误码**：
  - TODO_0001 — name 缺失或为空白
  - TODO_0002 — name 长度超过 100 字符
  - TODO_0003 — description 长度超过 500 字符
  - TODO_0004 — 系统内部错误

## 业务规则

| 规则 | 描述 | 校验时机 | 不满足处理 |
|------|------|----------|------------|
| R01 | name 去除首尾空白后非空 | 创建时 | TODO_0001 |
| R02 | name 长度 ≤ 100 字符 | 创建时 | TODO_0002 |
| R03 | description 长度 ≤ 500 字符 | 创建时 | TODO_0003 |
