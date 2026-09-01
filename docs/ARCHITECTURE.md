# 待办事项应用架构文档

## 1. 概述

待办事项记录工具（内部用户），最小闭环为「新增待办事项」。单体应用，技术栈：Spring Boot 3.2 + MyBatis + MySQL。

## 2. 分层架构

遵循 MVC 分层（上层依赖下层，禁止反向依赖）：

```
开放接口层（Controller）→ Service 层 → DAO 层（Mapper）→ MySQL
```

| 层 | 职责 | 关键类 |
|----|------|--------|
| Controller | 接口路由、功能开关、转发 | TodoItemController |
| Service | 业务规则校验、数据组装、落库 | TodoItemService / TodoItemServiceImpl |
| DAO | 数据访问 | TodoItemMapper |
| 公共层 | 通用响应、异常、常量、错误码 | Result / GlobalExceptionHandler / TodoConstants / BizException / TodoErrorCodeEnum |

## 3. 模块清单

| 模块 | 职责 | 依赖 |
|------|------|------|
| todo（待办事项） | 接收新增请求、校验、持久化、返回创建结果 | MySQL 数据库 |

## 4. 包结构

```
com.aiwork.todo
├── TodoApplication               # 启动类
├── controller                    # Web 层
├── service / impl                # Service 层
├── dao.mapper                    # DAO 层
├── model
│   ├── entity                    # 数据对象 DO
│   ├── dto                       # 请求/结果对象
│   └── enums                     # 枚举
└── common
    ├── constant                  # 常量
    ├── exception                 # 异常/错误码/全局处理
    └── result                    # 通用出参
```

## 5. 接口

| 编号 | 方法 | 路径 | 说明 |
|------|------|------|------|
| O01 | POST | /openapi/todo/items | 新增待办事项 |

## 6. 约定

- 响应结构：`{ "result": "OK"/错误码, "msg": "提示信息", "data": {} }`
- 错误码格式：`{MODULE}_{SEQ}`，模块编码 `TODO`（TODO_001 ~ TODO_999）
- 应急开关：`todo.create.enabled`
