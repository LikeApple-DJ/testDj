# demo 模块

## 职责

核心业务逻辑，提供问候语生成服务，包含 REST API 暴露和业务编排。

## 关键类

| 类 | 职责 |
|----|------|
| GreetingService / GreetingServiceImpl | 问候语生成核心业务逻辑 |
| GreetingController | REST API 端点，接收请求并委托 Service |
| GreetingVO | 问候语视图对象，用于 API 响应 |

## 依赖

- 无外部模块依赖（单体 Spring Boot 应用）

## API 接口列表

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/greeting?name={name} | 获取问候语，name 可选默认为 "Stranger" |