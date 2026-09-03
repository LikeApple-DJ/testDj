# ARCHITECTURE.md

## 模块边界与依赖方向

todo-app

| 模块 | 职责 | 不得包含 |
|------|------|----------|
| todo-common | 通用响应体、业务异常、全局异常处理 | 业务逻辑、DB 依赖 |
| todo-todo | 待办事项 CRUD、业务校验、REST 接口 | 跨模块依赖 |

## 配置分层

application.yml < application-{profile}.yml < 环境变量 < 启动参数

敏感配置（DB 密码、API Key）必须通过环境变量或密钥管理服务注入，禁止写入配置文件。

## 错误处理

所有业务异常继承 `BusinessException`，包含 `errorCode` 和 `message`。
通过 `GlobalExceptionHandler` 统一转换为 HTTP 响应。
禁止吞掉异常。

## 安全约束

- 凭证不得硬编码
- 日志中禁止输出用户手机号、身份证号等 PII 字段
- 所有用户输入必须进行后端参数校验（@Valid + 防御性校验）
- SQL 必须使用参数化查询（#{} 占位符）

## 测试策略

- Service 层：单元测试 + Mockito，覆盖率 ≥ 80%
- API 层：集成测试，MockMvc

## AI 编码约束

- 改动前先读系分方案
- 不跨模块边界修改
- 数据库 Schema 变更必须同步更新表结构文档