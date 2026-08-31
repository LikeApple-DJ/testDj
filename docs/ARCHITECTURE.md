# 待办事项应用 架构文档

## 1. 概述

| 项目 | 内容 |
|------|------|
| 应用名称 | todo-app |
| 技术栈 | Spring Boot 3.2 + Spring Data JPA + MySQL(InnoDB) |
| JDK 版本 | JDK 21 LTS |
| 包根路径 | com.antdigital.todo |

## 2. 分层架构

应用遵循 MVC 分层架构：

```
com.antdigital.todo
├── common              # 公共层
│   ├── constant        # 常量
│   ├── exception       # 异常类
│   └── model           # 通用模型
└── todo                # 待办模块
    ├── controller       # Web 层 - 控制器
    ├── service          # Service 层 - 业务服务
    │   └── impl
    ├── repository       # DAO 层 - 数据访问
    ├── model            # 数据模型
    │   ├── entity       # 实体（DO）
    │   └── dto          # 数据传输对象
    └── enums            # 枚举
```

### 分层调用关系

```
Controller → Service → Repository → MySQL
```

- 上层依赖下层，下层不依赖上层，无循环依赖。

## 3. 模块列表

| 模块 | 职责 | 关键类 |
|------|------|--------|
| 待办模块 (todo) | 待办事项新增的参数校验、业务规则校验、持久化与结果返回 | TodoController, TodoService, TodoRepository, TodoDO |

## 4. 全局约定

- **通用出参**：`{result, msg, data}`，成功 result="OK"/msg="SUCCESS"
- **错误码格式**：`TODO_{SEQ}`，范围 0001-0099
- **全局异常处理**：`GlobalExceptionHandler` 统一捕获 BusinessException、参数校验异常、未知异常

## 5. 数据存储

| 表名 | 说明 | 引擎 |
|------|------|------|
| todo | 待办事项记录 | InnoDB, utf8mb4 |

## 6. API 接口

| 编号 | 方法 | 路径 | 说明 |
|------|------|------|------|
| W01 | POST | /api/todos | 新增待办事项 |
