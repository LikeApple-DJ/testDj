# 三接口工具 + 埋点可视化报表 — 设计文档

> 日期: 2026-08-25
> 状态: 需求澄清阶段
> 关联仓库: [testDj] 后端 / [testDJnew] 前端

---

## 1. 需求概述

分别实现三个后端接口（HelloWorld、SHA-256 哈希、冒泡排序）；前端新增一个页面，含三个 Tab 展示各接口执行结果；提供导出按钮，后端支持 Excel 导出；后端 AOP 埋点记录调用次数和调用人；前端在同一页面展示可视化报表（折线图、饼图、柱状图），按人员类型/层级/部门维度分析。

---

## 2. 仓库职责

| 仓库 | 角色 | 技术栈 | 核心产物 |
|------|------|--------|----------|
| `[testDj]` | 后端服务 | Spring Boot 3 + MySQL + JWT + Apache POI | REST API、AOP 埋点、Excel 导出 |
| `[testDJnew]` | 前端应用 | React 18 + ECharts + Axios + React Router | 登录页、3-Tab 工具页、可视化报表 |

---

## 3. 技术决策

| 决策项 | 选择 | 理由 |
|--------|------|------|
| 后端框架 | Spring Boot 3 | 用户指定 |
| 前端框架 | React 18 | 用户指定 |
| 数据库 | MySQL | 用户指定 |
| 认证方案 | JWT 自建 | 无现有 SSO，自建可后续替换 |
| 图表库 | ECharts | 用户指定 |
| 导出格式 | Excel .xlsx (Apache POI) | 用户指定 |
| 哈希算法 | SHA-256 | 用户指定 |
| 埋点方式 | Spring AOP 切面 | 业务代码零侵入 |
| 用户维度 | 注册时自行填写 | 用户指定，无需管理后台 |

---

## 4. 后端 API 契约

### 4.1 认证模块

```
POST /api/auth/register
  Body: { username, password, personType, personLevel, personDept }
  Response: { id, username, token }

POST /api/auth/login
  Body: { username, password }
  Response: { token, user: { id, username, personType, personLevel, personDept } }
```

### 4.2 业务接口（均需 Authorization: Bearer <token>）

```
GET /api/helloworld?name={name}
  Response: { result: "Hello, {name}!" }

POST /api/hash
  Body: { input: "string" }
  Response: { algorithm: "SHA-256", input: "string", hash: "hex..." }

POST /api/bubblesort
  Body: { array: [5, 3, 8, 1, 2] }
  Response: { original: [...], sorted: [...] }
```

### 4.3 导出接口

```
GET /api/export?type={helloworld|hash|bubblesort}
  Response: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet (Excel 二进制流)
```

### 4.4 埋点报表接口

```
GET /api/tracking/report?dimension={personType|personLevel|personDept}
  Response: [
    { label: "技术岗", callCount: 42, details: [...] },
    { label: "管理岗", callCount: 18, details: [...] }
  ]
```

---

## 5. 数据库设计

### 5.1 users 表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK AUTO_INCREMENT | 主键 |
| username | VARCHAR(50) UNIQUE | 用户名 |
| password | VARCHAR(255) | BCrypt 加密 |
| person_type | VARCHAR(50) | 人员类型（如：技术岗/管理岗/运营岗） |
| person_level | VARCHAR(50) | 人员层级（如：初级/中级/高级/专家） |
| person_dept | VARCHAR(100) | 人员部门（如：研发部/产品部/运维部） |
| created_at | DATETIME | 注册时间 |

### 5.2 tracking_records 表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK AUTO_INCREMENT | 主键 |
| user_id | BIGINT FK → users.id | 调用人 |
| api_name | VARCHAR(50) | 接口名（helloworld/hash/bubblesort） |
| params_json | TEXT | 请求参数 JSON |
| call_time | DATETIME | 调用时间 |
| ip_address | VARCHAR(45) | 客户端 IP |

---

## 6. 后端模块结构

```
testDj-main/
└── src/main/java/com/example/demo/
    ├── DemoApplication.java
    ├── config/
    │   ├── SecurityConfig.java          # Spring Security + JWT Filter
    │   └── WebConfig.java               # CORS
    ├── controller/
    │   ├── AuthController.java          # 注册/登录
    │   ├── HelloWorldController.java
    │   ├── HashController.java
    │   ├── BubbleSortController.java
    │   ├── ExportController.java
    │   └── TrackingController.java
    ├── service/
    │   ├── UserService.java
    │   ├── HashService.java
    │   ├── BubbleSortService.java
    │   ├── ExportService.java
    │   └── TrackingService.java
    ├── model/
    │   ├── User.java                    # JPA Entity
    │   ├── TrackingRecord.java          # JPA Entity
    │   └── dto/                         # 请求/响应 DTO
    ├── repository/
    │   ├── UserRepository.java
    │   └── TrackingRecordRepository.java
    ├── security/
    │   ├── JwtUtil.java                 # JWT 签发/校验
    │   └── JwtAuthenticationFilter.java # 过滤器
    └── aspect/
        └── TrackingAspect.java          # AOP 埋点切面
```

---

## 7. 前端模块结构

```
testDJnew-main/
└── src/
    ├── App.jsx
    ├── pages/
    │   ├── LoginPage.jsx                # 登录/注册
    │   └── DashboardPage.jsx            # 主页面（Tab + 报表）
    ├── components/
    │   ├── HelloWorldTab.jsx
    │   ├── HashTab.jsx
    │   ├── BubbleSortTab.jsx
    │   ├── ExportButton.jsx
    │   └── TrackingDashboard.jsx        # ECharts 报表区
    ├── api/
    │   └── index.js                     # Axios 封装 + 拦截器
    ├── hooks/
    │   └── useAuth.js                   # JWT 状态管理
    └── utils/
        └── auth.js                      # Token 存储
```

---

## 8. 前端页面布局

```
┌─────────────────────────────────────────────────┐
│  Header: 用户名 | 退出                           │
├─────────────────────────────────────────────────┤
│  ┌─ Tab: HelloWorld ─┬─ Tab: 哈希 ─┬─ Tab: 排序 ─┐  │
│  │                                                   │
│  │  [输入框] [执行按钮]          [导出Excel]         │
│  │  ┌─────────────────────────────┐                  │
│  │  │       结果展示区              │                  │
│  │  └─────────────────────────────┘                  │
│  │                                                   │
│  ├───────────────────────────────────────────────────┤
│  │  埋点调用报表                                      │
│  │  [维度切换: 人员类型 ▼ | 人员层级 | 人员部门]      │
│  │  ┌──────────────┐ ┌──────────────┐ ┌───────────┐ │
│  │  │   折线图      │ │   饼图       │ │  柱状图    │ │
│  │  │  (趋势)      │ │  (占比)      │ │  (对比)    │ │
│  │  └──────────────┘ └──────────────┘ └───────────┘ │
└─────────────────────────────────────────────────────┘
```

---

## 9. 仓间对齐清单

| # | 对齐项 | testDj | testDJnew | 风险 |
|---|--------|--------|-----------|------|
| 1 | JWT 格式 | `Bearer {token}`，token 含 userId/username | 登录后存 localStorage，请求拦截器自动附加 | 低 |
| 2 | 接口路径 | 统一 `/api/*` 前缀 | 前端代理或 CORS 配置 | 低 |
| 3 | 报表维度枚举 | `personType` / `personLevel` / `personDept` | 前端维度切换按钮与后端参数一致 | 低 |
| 4 | 导出文件 | `Content-Disposition: attachment; filename=xxx.xlsx` | 前端以 blob 方式下载 | 中（需联调验证） |
| 5 | 错误码 | 统一 `{ code, message }` 格式 | 前端统一拦截展示 | 低 |

---

## 10. 自审清单

- [x] 需求覆盖：3 接口 + 前端 Tab + 导出 + 埋点 + 可视化报表，全部覆盖
- [x] 接口契约：每个接口的路径、方法、请求/响应体已明确
- [x] 数据库表：users 和 tracking_records 已定义
- [x] 仓间依赖：JWT、API 路径、报表维度、导出格式已对齐
- [x] 未决项：无
- [ ] 约束确认：`<outputs_content>` 为空，当前阶段仅产出本文档，不涉及代码修改