# 日常待办事项系统 — 需求澄清与设计规格

> **版本**: v1.0  
> **日期**: 2026-08-27  
> **状态**: 待审阅  

---

## 1. 需求概述

帮助内部用户记录日常待办事项。本轮最小闭环：**创建待办事项 + 列表查看**。

| 维度 | 决策 |
|------|------|
| 交付形态 | Web 全栈应用 |
| 技术栈 | React + Spring Boot + MySQL |
| 功能边界 | 创建 + 列表查看（不含编辑/删除/完成标记） |
| 用户认证 | 无需认证 |
| 仓库结构 | 单仓 Monorepo（frontend/ + backend/） |

---

## 2. 数据模型

### 2.1 数据库表 `todos`

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| `id` | BIGINT | PK, AUTO_INCREMENT | 主键 |
| `name` | VARCHAR(255) | NOT NULL | 事项名称 |
| `description` | TEXT | NULLABLE | 事项描述 |
| `created_at` | DATETIME | DEFAULT NOW() | 创建时间 |

### 2.2 实体定义

```java
@Entity
@Table(name = "todos")
public class TodoItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
```

---

## 3. API 设计

| 方法 | 路径 | 请求体 | 响应 | 说明 |
|------|------|--------|------|------|
| `POST` | `/api/todos` | `{"name":"...", "description":"..."}` | `201` + TodoItem JSON | 创建待办事项 |
| `GET` | `/api/todos` | — | `200` + `[{...}]` | 获取全部待办列表（按创建时间倒序） |

### 请求/响应示例

**POST /api/todos**
```json
// Request
{ "name": "完成周报", "description": "整理本周工作内容并提交" }

// Response 201
{ "id": 1, "name": "完成周报", "description": "整理本周工作内容并提交", "createdAt": "2026-08-27T10:30:00" }
```

**GET /api/todos**
```json
// Response 200
[
  { "id": 2, "name": "开会", "description": "项目周会", "createdAt": "2026-08-27T11:00:00" },
  { "id": 1, "name": "完成周报", "description": "整理本周工作内容并提交", "createdAt": "2026-08-27T10:30:00" }
]
```

---

## 4. 前端设计

### 4.1 页面结构

单页应用，路由 `/`：

```
┌──────────────────────────────────────┐
│           日常待办事项                 │
│                                      │
│  ┌──────────────────────────────┐    │
│  │  [事项名称________]           │    │
│  │  [描述____________]           │    │
│  │  [       新增待办        ]     │    │
│  └──────────────────────────────┘    │
│                                      │
│  ┌──────────────────────────────┐    │
│  │ 📋 完成周报                   │    │
│  │    整理本周工作内容并提交       │    │
│  │    2026-08-27 10:30          │    │
│  ├──────────────────────────────┤    │
│  │ 📋 开会                      │    │
│  │    项目周会                   │    │
│  │    2026-08-27 11:00          │    │
│  └──────────────────────────────┘    │
└──────────────────────────────────────┘
```

### 4.2 组件树

```
App
├── TodoForm        # 新增表单（name + description + 提交按钮）
└── TodoList        # 待办列表容器
    └── TodoCard[]  # 单条待办卡片
```

### 4.3 交互流程

1. 页面加载 → `GET /api/todos` → 渲染列表
2. 用户填写名称和描述 → 点击"新增待办" → `POST /api/todos`
3. 创建成功 → 自动刷新列表（重新 `GET /api/todos`）
4. 创建失败 → 显示错误提示

---

## 5. 后端分层架构

```
Controller (TodoController)
    │
    ▼
Service (TodoService)
    │
    ▼
Repository (TodoRepository → JpaRepository)
    │
    ▼
Entity (TodoItem) → MySQL (todos)
```

### 5.1 关键类

| 类 | 职责 |
|-----|------|
| `TodoController` | 接收 HTTP 请求，参数校验，返回响应 |
| `TodoService` | 业务逻辑（创建、查询） |
| `TodoRepository` | 数据访问层，继承 `JpaRepository` |
| `TodoItem` | JPA 实体映射 |

---

## 6. 项目结构

```
├── backend/
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/example/todo/
│       │   ├── TodoApplication.java
│       │   ├── controller/TodoController.java
│       │   ├── dto/CreateTodoRequest.java
│       │   ├── entity/TodoItem.java
│       │   ├── repository/TodoRepository.java
│       │   └── service/TodoService.java
│       └── main/resources/
│           └── application.yml
├── frontend/
│   ├── package.json
│   ├── public/index.html
│   └── src/
│       ├── App.jsx
│       ├── App.css
│       ├── index.js
│       ├── api/todoApi.js
│       └── components/
│           ├── TodoForm.jsx
│           ├── TodoForm.css
│           ├── TodoList.jsx
│           ├── TodoList.css
│           └── TodoCard.jsx
└── docs/superpowers/specs/
    └── 2026-08-27-todo-design.md
```

---

## 7. 非功能约束

| 约束项 | 说明 |
|--------|------|
| 认证 | 无，所有内部用户共享同一视图 |
| 前端端口 | `localhost:3000`（开发模式） |
| 后端端口 | `localhost:8080` |
| 跨域 | 后端配置 CORS 允许 `localhost:3000` |
| 数据库 | MySQL，库名 `todo_db`，表 `todos` |

---

## 8. 输入校验

| 字段 | 规则 |
|------|------|
| `name` | 必填，1-255 字符，前后端均校验 |
| `description` | 选填，最大 5000 字符 |

### 错误响应格式

```json
// 400 Bad Request
{ "error": "VALIDATION_ERROR", "message": "事项名称不能为空" }
```

---

## 9. 自审清单

- [x] 数据模型覆盖所有需求字段（name + description）
- [x] API 覆盖创建和列表查询
- [x] 前端页面覆盖新增表单和列表展示
- [x] 无认证体系，架构简洁
- [x] 单仓结构，前后端分离清晰
- [x] 输入校验规则已补充
- [x] 错误响应格式已定义
- [ ] 待用户审阅确认后进入实施计划阶段

---

## 10. 下一步

待用户审阅本规格后，调用 `writing-plans` 技能生成详细实施计划。