# ARCHITECTURE.md

## 模块边界与依赖方向

demo (单体应用，含 controller / service / model)

| 模块 | 职责 | 不得包含 |
|------|------|----------|
| demo | 问候服务，提供 REST API 和业务逻辑 | 无跨模块依赖（单体项目） |

## 配置分层

application.yml < application-{profile}.yml < 环境变量 < 启动参数

敏感配置必须通过环境变量注入，禁止写入配置文件。

## 错误处理

业务层通过 `IllegalArgumentException` 处理参数校验异常，由 Spring Boot 默认错误处理机制转换为 HTTP 400 响应。日志使用 SLF4J + 占位符。

## 安全约束

- 凭证不得硬编码
- 日志中禁止输出 PII 字段
- Controller 输入参数使用 `@RequestParam` 默认值防御 null

## 测试策略

- Service 层：单元测试 + 直接实例化（无外部依赖），AAA 模式
- 测试框架：JUnit 5 + AssertJ

## AI 编码约束

- 遵循 dtazziboot-java-coding-standards
- 命名遵循大驼峰/小驼峰/全大写下划线规范
- 测试先于实现 (TDD)