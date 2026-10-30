# algorithm 项目架构

## 模块边界

本项目为算法工具库，提供常用排序算法的纯 Java 实现。

## 模块列表

| 模块 | 包路径 | 职责 |
|------|--------|------|
| algorithm | com.example.algorithm | 排序算法 |

## 分层约束

- **Util 层**：静态工具类，无状态，无外部依赖
- **Test 层**：JUnit 5 + AssertJ，覆盖正常/边界/异常路径

## 技术栈

- Java 8
- JUnit Jupiter 5.10.2
- AssertJ 3.25.3
- Maven 3.8+