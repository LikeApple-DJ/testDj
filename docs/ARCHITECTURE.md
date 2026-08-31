# 待办事项应用架构文档

> 对应系分设计：`.agents/20260831-目标_帮助用户记录日常待办事项_核心功能/design.md`

## 1. 架构概述

单体 Spring Boot 应用，采用 MVC 分层架构（Controller → Service → Mapper），技术栈：Spring Boot 3.2 + MyBatis 3.0 + MySQL。

## 2. 模块清单

| 模块 | 职责 | 依赖 |
|------|------|------|
| 待办事项模块（todo） | 待办事项的创建与持久化 | MySQL（biz_todo 表） |

## 3. 分层约束

- **交互层（Controller）**：接收 HTTP 请求，参数校验（@Valid），委托 Service 处理。
- **核心服务层（Service）**：业务规则校验（R01-R05）、唯一性预校验、默认值注入。
- **数据访问层（Mapper）**：MyBatis 映射，SQL 参数化（#{}），resultMap 定义。
- **横切层（config + common）**：登录态拦截器、全局异常处理、通用响应包装。

## 4. 关键设计决策

- **租户隔离**：tenant_id 由登录态拦截器注入 UserContext，禁止客户端传入。
- **并发控制**：同租户事项名称唯一由 DB 唯一索引 `uk_biz_todo_tenant_name` 兜底，捕获 `DuplicateKeyException` 转为 `TODO_004`。
- **状态机**：本期仅实现「新建→待处理」入口，status 默认 0，预留状态流转字段。
