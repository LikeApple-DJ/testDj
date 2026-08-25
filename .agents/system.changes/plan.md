# 实施计划 — 全栈多接口演示应用

## 1. 项目概述

从零搭建一个包含**三个后端算法接口**、**前端可视化展示**及**调用埋点统计看板**的全栈演示应用。

| 仓库 | 角色 | 技术栈 |
|------|------|--------|
| **testDj-main** | 后端服务 | Java 17 / Spring Boot 3.x / Maven / Spring Data JPA / H2 |
| **testDJnew-main** | 前端应用 | Vue 3 / Vite / Element Plus / ECharts / Axios |

---

## 2. 仓库 A：testDj-main（后端服务）

### 2.1 项目初始化

生成 Maven 项目骨架（`pom.xml` + 标准目录结构）：

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
│   │   ├── StatsController.java
│   │   └── AuthController.java           # 登录认证接口
│   ├── model/
│   │   ├── CallRecord.java
│   │   └── UserInfo.java                 # 用户信息实体
│   ├── repository/
│   │   └── CallRecordRepository.java
│   ├── service/
│   │   ├── AlgorithmService.java
│   │   ├── ExportService.java
│   │   ├── StatsService.java
│   │   ├── AuthService.java              # 登录认证服务
│   │   └── PersonService.java            # 对接外部人员系统（模拟）
│   ├── interceptor/
│   │   └── CallTrackingInterceptor.java
│   └── config/
│       ├── WebConfig.java                # 拦截器注册 + CORS
│       └── AuthInterceptor.java          # Token 认证拦截器
├── src/main/resources/
│   ├── application.yml
│   └── data.sql                          # 模拟埋点数据（初始化用）
└── src/main/java/com/example/demo/config/
    └── WebConfig.java                    # 拦截器注册 + CORS
```

### 2.2 接口清单

| 接口 | 方法 | 路径 | 请求体 | 响应 |
|------|------|------|--------|------|
| 登录 | POST | `/api/auth/login` | `{ "username": "...", "password": "..." }` | `{ "token": "...", "user": { "id": "...", "name": "...", "type": "...", "level": "...", "dept": "..." } }` |
| HelloWorld | GET | `/api/hello` | — | `{ "message": "Hello World!", "timestamp": "..." }` |
| 哈希算法 | POST | `/api/hash` | `{ "input": "string", "algorithm": "SHA-256" }` 支持 `algorithm`: `SHA-256` / `MD5` 等 | `{ "input": "...", "algorithm": "...", "hash": "..." }` |
| 冒泡排序 | POST | `/api/sort/bubble` | `{ "array": [3,1,4,1,5] }` | `{ "original": [...], "sorted": [...], "duration": 123 }` |
| 导出结果 | GET | `/api/export?tab=hello\|hash\|sort` | — | CSV 文件下载 |
| 调用统计 | GET | `/api/stats/calls?dimension=type\|level\|dept&start=&end=` | — | `{ "dimension": "...", "data": [...] }` |
| 调用人列表 | GET | `/api/stats/callers` | — | `{ "callers": [...] }` |

### 2.3 数据模型：CallRecord

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long (PK, auto) | 主键 |
| callerId | String | 调用人标识 |
| callerType | String | 人员类型（正式/外包/实习生） |
| callerLevel | String | 人员层级（P5/P6/P7/P8） |
| callerDept | String | 人员部门（技术部/产品部/运营部） |
| apiName | String | 调用的接口名 |
| callTime | LocalDateTime | 调用时间 |
| responseTime | Long | 响应耗时(ms) |

### 2.4 实现步骤

1. **Step 1**：创建 `pom.xml`，引入 Spring Boot Starter Web / JPA / H2 依赖
2. **Step 2**：创建 `DemoApplication.java` 主启动类
3. **Step 3**：创建 `application.yml`，配置 H2 数据源、JPA、端口 8080、CORS
4. **Step 4**：创建 `CallRecord.java` 实体类 + `UserInfo.java` 实体类
5. **Step 5**：创建 `CallRecordRepository.java`（JPA Repository，含自定义统计查询）
6. **Step 6**：创建 `AlgorithmService.java`（含 SHA-256 / MD5 等多算法哈希、冒泡排序实现）
7. **Step 7**：创建 `HelloController.java`（`/api/hello`）
8. **Step 8**：创建 `HashController.java`（`/api/hash`，接收 `algorithm` 参数）
9. **Step 9**：创建 `SortController.java`（`/api/sort/bubble`）
10. **Step 10**：创建 `AuthService.java` + `AuthController.java`（登录认证，返回 Token + 用户信息）
11. **Step 11**：创建 `AuthInterceptor.java`（Token 验证拦截器，排除 `/api/auth/login`）
12. **Step 12**：创建 `PersonService.java`（模拟对接外部人员系统，根据 callerId 查询 type/level/dept）
13. **Step 13**：创建 `CallTrackingInterceptor.java`（拦截所有 `/api/*` 调用，从 Token 提取调用人，调用 PersonService 获取维度，记录埋点）
14. **Step 14**：创建 `WebConfig.java`（注册拦截器 + CORS 配置）
15. **Step 15**：创建 `StatsService.java` + `StatsController.java`（统计查询接口）
16. **Step 16**：创建 `ExportService.java` + `ExportController.java`（CSV 导出）
17. **Step 17**：创建 `data.sql`（插入初始用户数据用于登录）

### 2.5 跨仓接口契约

| 契约项 | 说明 |
|--------|------|
| **基础 URL** | `http://localhost:8080` |
| **CORS** | 允许 origin `http://localhost:5173`（Vite 默认端口） |
| **数据格式** | 统一 JSON（`application/json`），导出为 `text/csv` |
| **时间格式** | ISO-8601: `yyyy-MM-dd'T'HH:mm:ss` |
| **统计维度** | 枚举值：`type` / `level` / `dept`，大小写不敏感 |
| **错误响应** | `{ "error": "描述", "status": 400/500 }` |
| **认证方式** | Token 认证：登录后返回 Token，后续请求在 `Authorization: Bearer <token>` 头中传递 |
| **登录接口** | `POST /api/auth/login`，请求体 `{ "username", "password" }`，响应 `{ "token", "user": { "id", "name", "type", "level", "dept" } }` |
| **人员维度** | 后端通过 PersonService 模拟对接外部系统，根据 callerId 返回 type/level/dept |

---

## 3. 仓库 B：testDJnew-main（前端应用）

### 3.1 项目初始化

```
testDJnew-main/
├── package.json
├── vite.config.js
├── index.html
├── src/
│   ├── main.js
│   ├── App.vue
│   ├── router/
│   │   └── index.js              # Vue Router（登录页 + 主页面）
│   ├── store/
│   │   └── auth.js                # 登录状态管理（Pinia / reactive）
│   ├── api/
│   │   └── index.js              # Axios 封装，所有接口调用
│   ├── views/
│   │   ├── Login.vue             # 登录页
│   │   └── Dashboard.vue         # 主页面（Tab + 看板）
│   ├── components/
│   │   ├── HelloTab.vue          # HelloWorld Tab
│   │   ├── HashTab.vue           # 哈希算法 Tab（含算法选择器）
│   │   ├── SortTab.vue           # 冒泡排序 Tab
│   │   ├── ExportButton.vue      # 导出按钮
│   │   ├── StatsChart.vue        # 统计图表容器
│   │   ├── LineChart.vue         # 折线图（按时间趋势）
│   │   ├── PieChart.vue          # 饼图（按人员类型/部门）
│   │   └── BarChart.vue          # 柱状图（按人员层级）
│   └── utils/
│       └── export.js              # 导出文件下载逻辑
```

### 3.2 页面布局

```
┌─────────────────────────────────────────────────┐
│  [HelloWorld Tab] [哈希算法 Tab] [冒泡排序 Tab]   │  ← el-tabs
├─────────────────────────────────────────────────┤
│                                                 │
│  当前 Tab 内容区：                                │
│  ┌─ 输入控件 ──────────────────────────────┐   │
│  │  [输入框] [执行按钮]                      │   │
│  └─────────────────────────────────────────┘   │
│  ┌─ 结果展示 ──────────────────────────────┐   │
│  │  执行结果区域                             │   │
│  └─────────────────────────────────────────┘   │
│                                                 │
│  [导出当前结果] ← 导出按钮                     │
├─────────────────────────────────────────────────┤
│  📊 调用统计看板                                │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐       │
│  │ 折线图    │ │ 饼图     │ │ 柱状图   │       │
│  │ (时间趋势)│ │ (人员类型)│ │ (人员层级)│       │
│  └──────────┘ └──────────┘ └──────────┘       │
└─────────────────────────────────────────────────┘
```

### 3.3 实现步骤

1. **Step 1**：`npm create vite@latest` 初始化 Vue 3 + Vite 项目
2. **Step 2**：安装依赖：`element-plus`、`echarts`、`vue-echarts`、`axios`、`vue-router`、`pinia`
3. **Step 3**：配置 `vite.config.js`（代理 `/api` → `http://localhost:8080`）
4. **Step 4**：创建 `src/api/index.js`（Axios 实例 + 请求拦截器自动携带 Token + 响应拦截器处理 401 跳转登录页）
5. **Step 5**：创建 `src/store/auth.js`（Pinia store：token / userInfo / login / logout）
6. **Step 6**：创建 `src/router/index.js`（路由：`/login` → Login.vue，`/dashboard` → Dashboard.vue，根路径重定向）
7. **Step 7**：创建 `src/views/Login.vue`（登录表单：用户名/密码输入，调 `/api/auth/login`，成功后存 Token 跳转看板）
8. **Step 8**：创建 `src/App.vue`（el-container 布局，登录后显示导航栏含用户信息 + 退出按钮）
9. **Step 9**：创建 `src/views/Dashboard.vue`（主页面：el-tabs + 三个 Tab 内容区 + 导出按钮 + 看板区域）
10. **Step 10**：创建 `HelloTab.vue`（调用 `/api/hello`，展示结果）
11. **Step 11**：创建 `HashTab.vue`（算法选择器下拉框 + 输入字符串 → 调用 `/api/hash`，展示哈希值）
12. **Step 12**：创建 `SortTab.vue`（输入逗号分隔数字 → 调用 `/api/sort/bubble`，展示排序结果）
13. **Step 13**：创建 `ExportButton.vue`（调用 `/api/export?tab=xxx`，触发 CSV 下载）
14. **Step 14**：创建 `LineChart.vue`（折线图，调用 `/api/stats/calls?dimension=time`）
15. **Step 15**：创建 `PieChart.vue`（饼图，调用 `/api/stats/calls?dimension=type` 或 `dept`）
16. **Step 16**：创建 `BarChart.vue`（柱状图，调用 `/api/stats/calls?dimension=level`）
17. **Step 17**：创建 `StatsChart.vue`（图表容器，组合三个图表组件）
18. **Step 18**：创建 `src/utils/export.js`（文件下载工具函数）

### 3.4 跨仓接口调用关系

```
[HelloTab.vue]     → GET  /api/hello
[HashTab.vue]      → POST /api/hash         { input, algorithm }
[SortTab.vue]      → POST /api/sort/bubble  { array }
[ExportButton.vue] → GET  /api/export?tab=xxx
[LineChart.vue]    → GET  /api/stats/calls?dimension=time&start=&end=
[PieChart.vue]     → GET  /api/stats/calls?dimension=type|dept
[BarChart.vue]     → GET  /api/stats/calls?dimension=level
```

---

## 4. 跨库协同对齐点

| 对齐点 | 后端 (testDj-main) | 前端 (testDJnew-main) | 兼容性检查 |
|--------|-------------------|----------------------|-----------|
| 基础路径 | `/api/*` | 代理 `/api` → `localhost:8080` | ✅ |
| 请求格式 | `Content-Type: application/json` | Axios 默认 JSON | ✅ |
| 响应格式 | `{ "field": "value" }` | 解构 `response.data` | ✅ |
| 统计维度 | `type`/`level`/`dept` 字符串参数 | 枚举字符串传递 | ✅ |
| 导出格式 | `text/csv` 响应 + `Content-Disposition` | Blob 下载 | ✅ |
| 时间格式 | `yyyy-MM-dd'T'HH:mm:ss` | `new Date().toISOString()` | ✅ |
| 错误处理 | `{ "error": "...", "status": xxx }` | Axios interceptor 统一处理 | ✅ |
| CORS | 允许 `localhost:5173` | Vite 代理可绕过 CORS | ✅（双重保障） |
| **登录认证** | `POST /api/auth/login` → 返回 Token | Login.vue 表单 → 存 Token 到 store | ✅ |
| **Token 传递** | `Authorization: Bearer <token>` 头解析 | Axios 请求拦截器自动附加 Token | ✅ |
| **登录态校验** | `AuthInterceptor` 拦截非 `/api/auth/**` 请求 | 401 响应 → 跳转 `/login` 页 | ✅ |
| **人员维度** | `PersonService` 模拟对接外部系统，根据 callerId 返回 type/level/dept | 登录时返回的 user 含维度信息，埋点拦截器同步调用 | ✅ |

---

## 5. 执行顺序

```
Phase 1 — testDj-main 后端（空库搭建）
  ├─ 1.1 生成 pom.xml + 项目骨架
  ├─ 1.2 实体（CallRecord + UserInfo）+ Repository
  ├─ 1.3 Controller + Service（3个算法接口，哈希支持多算法）
  ├─ 1.4 AuthService + AuthController（登录认证接口）
  ├─ 1.5 AuthInterceptor（Token 验证拦截器）
  ├─ 1.6 PersonService（模拟对接外部人员系统）
  ├─ 1.7 CallTrackingInterceptor（埋点拦截器，含 Token 解析 + 人员维度查询）
  ├─ 1.8 统计接口
  ├─ 1.9 导出接口
  └─ 1.10 初始用户数据 data.sql

Phase 2 — testDJnew-main 前端（空库搭建）
  ├─ 2.1 Vite 初始化 + 依安装（含 vue-router、pinia）
  ├─ 2.2 API 层封装（Axios + Token 拦截器）
  ├─ 2.3 Pinia auth store + Router 配置
  ├─ 2.4 登录页（Login.vue）
  ├─ 2.5 App.vue + 导航栏（含用户信息、退出）
  ├─ 2.6 主页面 + Tab 布局
  ├─ 2.7 三个 Tab 组件（HashTab 含算法选择器）
  ├─ 2.8 导出按钮组件
  └─ 2.9 统计图表组件（折线图/饼图/柱状图）

Phase 3 — 联调验证
  ├─ 3.1 启动后端 → 验证接口可用
  ├─ 3.2 启动前端 → 验证跨域调用
  ├─ 3.3 验证导出功能
  └─ 3.4 验证统计看板渲染
```

---

## 6. 技术决策记录

| 决策项 | 选择 | 理由 |
|--------|------|------|
| 哈希算法 | **多算法（SHA-256 / MD5 等）** | 前端可切换，后端通过 `algorithm` 参数选择实现 |
| 导出格式 | CSV | 零依赖，浏览器原生支持 |
| 埋点数据 | **对接真实系统（PersonService 模拟）** | 登录后从 Token 获取 callerId，调用 PersonService 查询 type/level/dept |
| 调用人标识 | **登录认证 Token 获取** | 用户登录后，从 UserInfo 中提取 callerId 及维度信息 |
| 登录认证 | **Token 认证（简单 JWT 或 UUID Token）** | 无状态，前后端分离友好 |
| 人员维度数据 | **PersonService 模拟外部系统（Map 实现）** | 演示阶段无需真实对接，保留扩展接口 |
| 数据库 | H2 内存模式 | 零配置，重启即重置 |
| 前端代理 | Vite devServer.proxy | 避免 CORS 复杂配置，开发友好 |
| 图表库 | ECharts (vue-echarts) | 社区成熟，三种图表类型齐全 |
| 前端路由 | Vue Router + 路由守卫 | 未登录自动跳转登录页 |
| 状态管理 | Pinia（auth store） | 轻量、Vue3 官方推荐 |

---

## 7. 风险与应对

| 风险 | 影响 | 应对方案 |
|------|------|---------|
| 前端 Vite 代理不生效 | 跨域请求失败 | 后端同时配置 CORS 作为兜底 |
| H2 模拟数据不足 | 图表无内容 | data.sql 插入 20+ 条多维度数据 |
| 接口参数格式不一致 | 前端调用失败 | 统一 JSON Schema，前后端对齐 |
| Maven 依赖下载慢 | 构建超时 | 使用阿里云镜像仓库 |