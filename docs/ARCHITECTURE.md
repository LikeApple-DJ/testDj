# 待办事项记录应用 — 架构文档

> **版本**: v1.0
> **关联系分**: `.agents/20260831-目标_帮助用户记录日常待办事项_核心功能/design.md`

## 1. 技术栈

| 层 | 选型 |
|------|------|
| Web 层 | Spring Boot 3.2.0 + Spring MVC (REST) |
| 数据访问层 | Spring Data JPA (Hibernate) |
| 数据库 | PostgreSQL（生产）/ H2（测试） |
| 参数校验 | Jakarta Bean Validation (`spring-boot-starter-validation`) |
| 构建 | Maven，JDK 17 |
| 测试 | JUnit 5 + Mockito + AssertJ + MockMvc |

## 2. 分层架构

```
controller  →  service  →  repository  →  PostgreSQL
    ↕             ↕
   dto         context(UserContext)
   exception(GlobalExceptionHandler)
```

遵循 MVC 分层：Controller 负责协议适配与参数校验入口；Service 负责业务编排、落库与结果组装；Repository 负责持久化。

## 3. 模块清单

| 模块 | 职责 | 文档 |
|------|------|------|
| todo | 待办事项新增（F01） | [modules/todo/README.md](modules/todo/README.md) |

## 4. 全局约定

| 约定 | 值 |
|------|-----|
| API 基础路径 | `/api` |
| 通用出参 | `{ result, msg, data }`（`ApiResponse<T>`） |
| 错误码格式 | `TODO_{SEQ}`（如 `TODO_0001`） |
| 时间字段 | `gmt_create` / `gmt_modified`（`LocalDateTime`） |
| 租户隔离 | 预留 `tenant_id`，当前单租户默认 0 |
| 登录态 | 由全局拦截器校验，creator_id 经 `UserContext`（请求头 `X-User-Id`）读取 |

## 5. 跨模块约束

- 仅创建闭环，无查询/编辑/删除；后续扩展预留 `creator_id` / `tenant_id` 索引。
- SQL 注入防护：使用 Spring Data JPA 参数化绑定，禁止字符串拼接 SQL。
- 事务：创建采用 `@Transactional(rollbackFor = Exception.class)`，落库失败回滚并返回 `TODO_0004`。
