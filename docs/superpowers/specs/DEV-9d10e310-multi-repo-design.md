# 跨仓多模块设计文档

## 1. 概述

### 1.1 项目背景
实现三个后端接口（HelloWorld、哈希算法、冒泡排序），前端以 Tab 页展示执行结果，支持导出和埋点可视化报表。

### 1.2 仓库分配

| 仓库 | 角色 | 技术栈 | 构建工具 |
|------|------|--------|---------|
| **testDj** | 后端 API 服务 | Spring Boot 3 + Java 17 + H2 | Maven |
| **ykstest** | 前端 SPA | Vue 3 + Element Plus + ECharts + Axios | Vite / npm |

### 1.3 架构图

```
┌─────────────────────────────────────────────────────┐
│                  用户浏览器                           │
│  ┌───────────────────────────────────────────────┐  │
│  │  ykstest (Vue 3 + Element Plus)               │  │
│  │  ┌──────┐ ┌──────┐ ┌──────┐ ┌──────────────┐ │  │
│  │  │Hello │ │ Hash │ │ Sort │ │ 📊 Dashboard │ │  │
│  │  │ Tab  │ │ Tab  │ │ Tab  │ │ 饼/柱/折线图  │ │  │
│  │  └──────┘ └──────┘ └──────┘ └──────────────┘ │  │
│  │  [导出按钮]                                     │  │
│  └───────────────────────┬───────────────────────┘  │
│                          │ HTTP REST API             │
└──────────────────────────┼───────────────────────────┘
                           │
┌──────────────────────────┼───────────────────────────┐
│  testDj (Spring Boot 3)  │                           │
│  ┌───────────────────────▼───────────────────────┐  │
│  │  Controller Layer                             │  │
│  │  /api/hello  /api/hash  /api/sort             │  │
│  │  /api/export  /api/tracking/stats             │  │
│  └───────────────┬───────────────────────────────┘  │
│                  │                                   │
│  ┌───────────────▼───────────────────────────────┐  │
│  │  Service Layer                                │  │
│  │  HelloService  HashService  SortService       │  │
│  │  ExportService  TrackingService               │  │
│  └───────────────┬───────────────────────────────┘  │
│                  │                                   │
│  ┌───────────────▼───────────────────────────────┐  │
│  │  Repository / H2 Database                     │  │
│  │  tracking_record 表 (埋点数据)                 │  │
│  └───────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────┘
```

---

## 2. 后端 API 设计（testDj 仓库）

### 2.1 基础信息

- **基础路径**: `http://localhost:8080`
- **统一响应格式**:

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

### 2.2 接口列表

#### 2.2.1 HelloWorld 接口

| 项目 | 内容 |
|------|------|
| 路径 | `GET /api/hello` |
| 请求参数 | `name` (query, 可选, 默认"World") |
| 响应示例 | `{ "code": 200, "data": { "greeting": "Hello, World!" } }` |
| 埋点 | 记录调用人、调用时间、接口名 |

#### 2.2.2 哈希算法接口

| 项目 | 内容 |
|------|------|
| 路径 | `POST /api/hash` |
| 请求体 | `{ "input": "待哈希字符串", "algorithm": "MD5|SHA-256|SHA-512" }` |
| 响应示例 | `{ "code": 200, "data": { "algorithm": "SHA-256", "input": "hello", "output": "2cf24dba5fb0a30e..." } }` |
| 埋点 | 记录调用人、调用时间、接口名、算法类型 |

#### 2.2.3 冒泡排序接口

| 项目 | 内容 |
|------|------|
| 路径 | `POST /api/sort` |
| 请求体 | `{ "numbers": [3, 1, 4, 1, 5, 9, 2, 6], "order": "asc|desc" }` |
| 响应示例 | `{ "code": 200, "data": { "original": [...], "sorted": [...], "swapCount": 12 } }` |
| 埋点 | 记录调用人、调用时间、接口名、数组长度 |

#### 2.2.4 导出接口

| 项目 | 内容 |
|------|------|
| 路径 | `GET /api/export` |
| 请求参数 | `tab` (必选: hello / hash / sort), `callerName` (必选) |
| 响应 | 返回 CSV 文件下载 (`Content-Type: text/csv`) |
| 导出内容 | 每个 Tab 当前执行结果明细 |

#### 2.2.5 埋点统计接口

| 项目 | 内容 |
|------|------|
| 路径 | `GET /api/tracking/stats` |
| 请求参数 | `dimension` (必选: callerType / callerLevel / callerDept / time), `startDate`, `endDate` (可选) |
| 响应示例 | `{ "code": 200, "data": { "labels": [...], "values": [...] } }` |
| 用途 | 前端折线图/饼图/柱状图数据源 |

#### 2.2.6 埋点调用记录接口（供前端查看明细）

| 项目 | 内容 |
|------|------|
| 路径 | `GET /api/tracking/records` |
| 请求参数 | `page`, `size` (分页) |
| 响应示例 | `{ "code": 200, "data": { "records": [...], "total": 100 } }` |

---

## 3. 数据库设计（H2 内存数据库）

### 3.1 tracking_record 表

```sql
CREATE TABLE tracking_record (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    api_name    VARCHAR(50)   NOT NULL,       -- 接口名: hello/hash/sort
    caller_name VARCHAR(100)  NOT NULL,       -- 调用人
    caller_type VARCHAR(50),                  -- 人员类型: 正式/实习/外包
    caller_level VARCHAR(50),                 -- 人员层级: 初级/中级/高级/专家
    caller_dept  VARCHAR(100),                -- 人员部门: 研发/产品/测试/运维
    extra_info  VARCHAR(500),                 -- 额外信息（算法类型、数组长度等）
    call_time   TIMESTAMP DEFAULT CURRENT_TIMESTAMP  -- 调用时间
);
```

### 3.2 索引

- `idx_api_name` ON `api_name`
- `idx_call_time` ON `call_time`
- `idx_caller_type` ON `caller_type`
- `idx_caller_dept` ON `caller_dept`

---

## 4. 前端设计（ykstest 仓库）

### 4.1 页面结构

```
App.vue
└── MainPage.vue
    ├── ApiTabs.vue (Tab 切换组件)
    │   ├── HelloTab.vue     — 输入 name，调用 /api/hello，展示结果
    │   ├── HashTab.vue      — 输入文本+选择算法，调用 /api/hash，展示结果
    │   └── SortTab.vue      — 输入数字数组+选择排序方式，调用 /api/sort，展示结果
    ├── ExportButton.vue     — 导出按钮，调用 /api/export
    └── Dashboard.vue        — 埋点可视化报表
        ├── LineChart.vue    — 折线图（按时间维度展示调用趋势）
        ├── PieChart.vue     — 饼图（按人员类型/人员层级维度展示占比）
        └── BarChart.vue     — 柱状图（按人员部门维度展示调用量）
```

### 4.2 组件数据流

```
用户输入 → 调用 API → 后端返回结果 → 展示在 Tab 内
                                          ↓
                                   埋点自动记录
                                          ↓
Dashboard 组件 ← 轮询/定时调用 /api/tracking/stats ← 后端统计
```

### 4.3 可视化图表配置

| 图表类型 | 维度 | 说明 |
|---------|------|------|
| 📈 折线图 | `time` | X 轴=时间，Y 轴=调用次数，展示调用趋势 |
| 🥧 饼图 | `callerType` 或 `callerLevel` | 各人员类型/层级占比 |
| 📊 柱状图 | `callerDept` | 各部门调用量对比 |

### 4.4 前端路由

- `#/` 或 `/` → MainPage

### 4.5 依赖库

```json
{
  "dependencies": {
    "vue": "^3.4",
    "element-plus": "^2.9",
    "axios": "^1.7",
    "echarts": "^5.5",
    "vue-echarts": "^7.0"
  }
}
```

---

## 5. 仓间对齐点（Cross-Repo Contract）

### 5.1 API 协议

| 项目 | 约定 |
|------|------|
| 基础 URL | 后端 `localhost:8080`，前端通过 `vite.config.js` proxy 转发 |
| 响应格式 | 统一 `{ code, message, data }` |
| 错误码 | 200=成功, 400=参数错误, 500=服务端错误 |
| 日期格式 | ISO 8601: `yyyy-MM-dd HH:mm:ss` |

### 5.2 埋点调用人信息传递

前端每次调用业务 API 时，通过请求头传递调用人信息：

| Header | 说明 |
|--------|------|
| `X-Caller-Name` | 调用人姓名 |
| `X-Caller-Type` | 人员类型 |
| `X-Caller-Level` | 人员层级 |
| `X-Caller-Dept` | 人员部门 |

后端通过 `@RequestHeader` 或拦截器统一提取。

### 5.3 导出文件格式

| 项目 | 约定 |
|------|------|
| 格式 | CSV（UTF-8 with BOM） |
| 文件命名 | `export_{tab}_{yyyyMMddHHmmss}.csv` |
| 列头 | 英文首行，与 API 响应字段对齐 |

---

## 6. 目录结构

### 6.1 testDj（后端）

```
testDj-main/
├── pom.xml
├── src/main/java/com/example/demo/
│   ├── DemoApplication.java
│   ├── controller/
│   │   ├── HelloController.java
│   │   ├── HashController.java
│   │   ├── SortController.java
│   │   ├── ExportController.java
│   │   └── TrackingController.java
│   ├── service/
│   │   ├── HelloService.java
│   │   ├── HashService.java
│   │   ├── SortService.java
│   │   ├── ExportService.java
│   │   └── TrackingService.java
│   ├── entity/
│   │   └── TrackingRecord.java
│   ├── repository/
│   │   └── TrackingRepository.java
│   └── config/
│       └── WebConfig.java        (CORS 配置)
├── src/main/resources/
│   ├── application.yml
│   └── schema.sql
└── docs/superpowers/specs/
    └── DEV-9d10e310-multi-repo-design.md
```

### 6.2 ykstest（前端）

```
ykstest-main/
├── package.json
├── vite.config.js
├── index.html
├── src/
│   ├── main.js
│   ├── App.vue
│   ├── api/
│   │   └── index.js              (Axios 封装)
│   ├── views/
│   │   └── MainPage.vue          (主页面)
│   ├── components/
│   │   ├── tabs/
│   │   │   ├── HelloTab.vue
│   │   │   ├── HashTab.vue
│   │   │   └── SortTab.vue
│   │   ├── ExportButton.vue
│   │   └── dashboard/
│   │       ├── Dashboard.vue
│   │       ├── LineChart.vue
│   │       ├── PieChart.vue
│   │       └── BarChart.vue
│   └── styles/
│       └── main.css
└── README.md
```

---

## 7. 开发顺序

| 阶段 | 内容 | 仓库 |
|------|------|------|
| 1 | 初始化 Spring Boot 项目 + H2 配置 | testDj |
| 2 | 实现 HelloController + HelloService | testDj |
| 3 | 实现 HashController + HashService | testDj |
| 4 | 实现 SortController + SortService | testDj |
| 5 | 实现埋点实体、仓库、服务、拦截器 | testDj |
| 6 | 实现导出接口 ExportController | testDj |
| 7 | 实现统计接口 TrackingController | testDj |
| 8 | 初始化 Vue 3 项目 + Element Plus 配置 | ykstest |
| 9 | 实现三个 Tab 组件 + API 调用 | ykstest |
| 10 | 实现导出按钮组件 | ykstest |
| 11 | 实现 Dashboard 及三种图表组件 | ykstest |
| 12 | 集成测试 + CORS 联调 | 双仓 |
