# 待办事项应用 — 架构文档

> 版本: v1.0 | 日期: 2026-08-31 | 作者: AiWork

## 1. 架构概述

单体三层架构（Controller–Service–DAO），单服务，单数据库。

```
com.antdigital.todo
├── controller          # Web 层：参数校验、请求转发
├── service             # Service 层：业务逻辑
│   └── impl
├── dao
│   └── mapper          # DAO 层：MyBatis 数据访问
├── model
│   ├── entity          # 数据对象 (DO)
│   ├── dto             # 数据传输对象
│   └── vo              # 视图对象
└── common
    ├── constant        # 常量
    ├── exception       # 异常处理
    └── response        # 统一响应体
```

## 2. 模块清单

| 模块 | 职责 | 依赖 |
|------|------|------|
| todo（待办事项） | 待办事项的创建与持久化 | MySQL |

## 3. 分层约束

- Web 层 → Service 层 → DAO 层（单向依赖，禁止反向）
- 接口与实现分离，实现类放在 `impl` 包下
- 公共类（常量、异常、工具）放在 `common` 包下

## 4. 技术栈

| 层面 | 选型 |
|------|------|
| 框架 | Spring Boot 3.2.5 (Java 17) |
| 持久层 | MyBatis 3.0.3 |
| 数据库 | MySQL 8.x |
| 校验 | spring-boot-starter-validation (Jakarta Validation) |
| 测试 | JUnit 5 + Mockito + AssertJ + H2 |
