# ARCHITECTURE — testdemo

## 项目概述
测试计算器服务演示项目，展示数科 Java 编码规范在 Spring Boot 工程中的落地实践。

## 模块边界

| 模块 | 职责 |
|------|------|
| testdemo | 计算器核心服务：加/减/乘/除四则运算，参数校验，异常处理 |

## 分层架构

```
com.dtazziboot.testdemo
├── controller         # Web 层（控制器）
├── service            # Service 层
│   └── impl           # Service 实现
├── model
│   ├── entity         # 数据对象（DO）
│   └── dto            # 视图对象 / 请求对象
└── common
    ├── constant       # 常量
    └── exception      # 异常定义
```

## 技术栈

| 组件 | 版本 |
|------|------|
| Java | 17 |
| Spring Boot | 3.2.5 |
| JUnit | 5 (Jupiter) |
| Mockito | (Spring Boot Test 内嵌) |
| AssertJ | (Spring Boot Test 内嵌) |
| Slf4j | (Spring Boot 内嵌) |

## 约束
- 接口与实现分离（ServiceImpl 后缀）
- 使用 SLF4J 占位符日志
- 自定义 BusinessException 替代裸 RuntimeException
- 禁止 System.out / e.printStackTrace