# 算法演示模块编码报告

> 系分文档：`.agents/20260819-分别写三个接口helloworld_哈希/design.md`

## 1. 模块进度追踪

| 序号 | 模块 | READ | TEST | IMPL | CHECK | DOCS | 状态 |
|:----:|------|:----:|:----:|:----:|:-----:|:----:|------|
| 1 | HelloWorld | ✅ | ✅ | ✅ | ✅ | ✅ | 已完成 |
| 2 | 哈希算法 | ✅ | ✅ | ✅ | ✅ | ✅ | 已完成 |
| 3 | 冒泡排序 | ✅ | ✅ | ✅ | ✅ | ✅ | 已完成 |
| 4 | 结果导出 | ✅ | ✅ | ✅ | ✅ | ✅ | 已完成 |

## 2. 实现概述

基于系分设计，在仓库根目录新建了一个标准的 Maven Spring Boot 工程 `algo-demo`，提供：

- `GET /api/hello` / `GET /openapi/hello`：HelloWorld 问候接口
- `POST /api/hash` / `POST /openapi/hash`：MD5 / SHA-256 哈希接口
- `POST /api/sort/bubble` / `POST /openapi/sort/bubble`：冒泡排序接口
- `POST /api/export` / `POST /openapi/export`：结果导出接口（支持 CSV / JSON）
- 前端演示页面：`src/main/resources/static/index.html`，包含导出按钮

工程使用统一响应体 `{code, msg, data}`，全局异常处理，错误码沿用系分设计 `ALG_xxx`。

## 3. 新增文件清单

```
.
├── pom.xml
└── src
    ├── main
    │   ├── java/com/example/algodemo
    │   │   ├── AlgoDemoApplication.java
    │   │   ├── api
    │   │   │   ├── controller
    │   │   │   │   ├── HelloWorldController.java
    │   │   │   │   ├── HashController.java
    │   │   │   │   ├── SortController.java
    │   │   │   │   └── ExportController.java
    │   │   │   └── request
    │   │   │       ├── HashRequest.java
    │   │   │       ├── SortRequest.java
    │   │   │       └── ExportRequest.java
    │   │   ├── common
    │   │   │   ├── constant
    │   │   │   │   ├── HashAlgorithmEnum.java
    │   │   │   │   ├── ExportFormatEnum.java
    │   │   │   │   └── ExportTypeEnum.java
    │   │   │   ├── exception
    │   │   │   │   ├── AlgorithmErrorCode.java
    │   │   │   │   ├── BusinessException.java
    │   │   │   │   └── GlobalExceptionHandler.java
    │   │   │   └── response
    │   │   │       └── ApiResponse.java
    │   │   ├── service
    │   │   │   ├── HelloWorldService.java
    │   │   │   ├── HashService.java
    │   │   │   ├── SortService.java
    │   │   │   ├── ExportService.java
    │   │   │   ├── impl
    │   │   │   │   ├── HelloWorldServiceImpl.java
    │   │   │   │   ├── HashServiceImpl.java
    │   │   │   │   ├── SortServiceImpl.java
    │   │   │   │   └── ExportServiceImpl.java
    │   │   │   └── model
    │   │   │       ├── HashResult.java
    │   │   │       ├── SortResult.java
    │   │   │       └── ExportResult.java
    │   └── resources
    │       ├── application.yml
    │       └── static/index.html
    └── test/java/com/example/algodemo/service/impl
            ├── HelloWorldServiceImplTest.java
            ├── HashServiceImplTest.java
            ├── SortServiceImplTest.java
            └── ExportServiceImplTest.java
```

## 4. 单测覆盖摘要

| 被测类 | 测试方法数 | 覆盖场景 |
|--------|-----------|----------|
| HelloWorldServiceImpl | 3 | 正常称呼、null、空白 |
| HashServiceImpl | 4 | MD5 正确性、SHA-256 正确性、空 content、不支持的算法 |
| SortServiceImpl | 5 | 升序、降序、默认排序、空数组、非法 order |
| ExportServiceImpl | 5 | hello CSV、hash JSON、bubbleSort CSV、不支持类型、默认格式 |

测试框架：JUnit 5 + Mockito + AssertJ，符合 `unit-testing.md` 规范。

## 5. L1 静态检查

| 检查项 | 规范要求 | 符合情况 |
|--------|----------|:--------:|
| 命名规范 | 类名大驼峰、方法/变量小驼峰、常量全大写 | ✅ |
| 包结构 | 按业务模块划分，接口与实现分离 | ✅ |
| 异常日志 | SLF4J、统一错误码、全局异常处理 | ✅ |
| 前后端规约 | REST 路径、统一响应体 | ✅ |
| 单元测试 | Service 层全覆盖，遵循 AAA 模式 | ✅ |
| 安全规范 | 输入参数校验、防止 NPE | ✅ |

## 6. L2 动态验证

| 验证项 | 状态 | 说明 |
|--------|:----:|------|
| 编译验证 | ⚠️ | 当前运行环境未安装 JDK / Maven，无法执行 `mvn compile` |
| 单测验证 | ⚠️ | 同上，建议在本地执行 `mvn test` |

### 待人工验证命令

```bash
mvn compile -DskipTests
mvn test
```

## 7. 启动与使用

```bash
mvn spring-boot:run
```

- 前端页面：`http://localhost:8080`
- HelloWorld：`GET http://localhost:8080/api/hello?name=Alice`
- 哈希：`POST http://localhost:8080/api/hash`
- 冒泡排序：`POST http://localhost:8080/api/sort/bubble`
- 导出：`POST http://localhost:8080/api/export`

## 8. 后续建议

1. 在具备 JDK/Maven 的环境执行 `mvn test` 验证单测。
2. 若接入网关，可按设计配置 `/openapi/**` 路由。
3. 如需持久化导出文件，可将导出结果写入对象存储，并替换 `ExportResult` 中的 `content` 为 `downloadUrl`。
