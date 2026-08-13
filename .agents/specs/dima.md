# 需求澄清规格文档 — 多接口演示与分析系统

> **阶段**: 需求澄清（Brainstorming）  
> **日期**: 2025-08-13  
> **涉及仓库**: testDj（后端）、testDJnew（前端）  
> **状态**: 待评审

---

## 1. 通览 (Overview)

### 1.1 需求理解

本次需求构建一个**多接口演示 + 数据可视化分析**系统，涵盖四大功能域：

| # | 功能域 | 描述 |
|---|--------|------|
| F1 | 三个业务接口 | HelloWorld、哈希算法、冒泡排序 |
| F2 | 前端多Tab展示页 | 三个Tab分别展示各接口执行结果 |
| F3 | 导出功能 | 前端导出按钮 + 后端导出接口，支持各页面结果导出 |
| F4 | 埋点与可视化报表 | 后端记录调用次数/调用人，前端多维度可视化（折线图/饼图/柱状图） |

### 1.2 仓库现状

- **testDj**（后端仓库）：当前仅含 `07hubtasty` 文件（内容 "hello world"），无框架/无构建配置，属于**全新项目**。
- **testDJnew**（前端仓库）：当前仅含 `README.md`（"# testDJnew"），属于**全新项目**。

> **关键决策点**：两个仓库均为空仓库，需从零搭建技术栈。

---

## 2. 需求澄清问题与假设 (Clarification & Assumptions)

### 2.1 已明确的约束

| 编号 | 约束 | 来源 |
|------|------|------|
| C1 | 后端仓库 = testDj | 任务上下文 |
| C2 | 前端仓库 = testDJnew | 任务上下文 |
| C3 | 三个接口：HelloWorld、哈希算法、冒泡排序 | 需求描述 |
| C4 | 前端三Tab页面 | 需求描述 |
| C5 | 导出功能（前端按钮 + 后端接口） | 需求描述 |
| C6 | 埋点：调用次数 + 调用人 | 需求描述 |
| C7 | 可视化维度：人员类型、人员层级、人员部门 | 需求描述 |
| C8 | 图表类型：折线图、饼图、柱状图 | 需求描述 |

### 2.2 待澄清问题（附默认假设）

| # | 问题 | 默认假设（用于推进设计） |
|---|------|--------------------------|
| Q1 | 后端技术栈选择？ | **Java + Spring Boot**（企业级标准，与蚂蚁数科技术栈匹配） |
| Q2 | 前端技术栈选择？ | **React + Ant Design + ECharts**（Ant Design 生态成熟，ECharts 支持折线/饼/柱状图） |
| Q3 | 三个接口的输入输出规范？ | 见 §3.1 接口契约 |
| Q4 | "调用人"如何获取？ | 通过请求 Header 中的用户标识（如 `X-User-Id`）或登录态 Session 获取 |
| Q5 | "人员类型/层级/部门"数据来源？ | 假设存在用户信息服务，或通过接口入参传入用户属性；MVP 阶段由前端传入用户维度信息 |
| Q6 | 导出格式？ | **CSV**（通用、轻量）；可扩展支持 Excel |
| Q7 | 数据存储方案？ | **H2 内嵌数据库**（MVP 阶段零配置）；可平滑迁移至 MySQL |
| Q8 | 接口是否需要认证/鉴权？ | MVP 阶段不做鉴权，通过 Header 传递用户信息用于埋点 |

---

## 3. 设计方案 (Design)

### 3.1 接口契约设计

#### F1-1: HelloWorld 接口

```
POST /api/demo/hello
Request:  { "name": "string" }
Response: { "message": "Hello, {name}!", "timestamp": "2025-08-13T10:00:00" }
```

#### F1-2: 哈希算法接口

```
POST /api/demo/hash
Request:  { "input": "string", "algorithm": "MD5|SHA-1|SHA-256" }
Response: { "input": "string", "algorithm": "SHA-256", "hash": "e3b0c44298fc1c14..." }
```

#### F1-3: 冒泡排序接口

```
POST /api/demo/bubble-sort
Request:  { "array": [5, 3, 8, 1, 9] }
Response: { "original": [5,3,8,1,9], "sorted": [1,3,5,8,9], "steps": 6 }
```

#### F3: 导出接口

```
GET /api/demo/export?type=hello|hash|bubble-sort&format=csv
Response: Content-Type: text/csv, Content-Disposition: attachment; filename="..."
```

#### F4: 埋点统计查询接口

```
GET /api/demo/statistics?dimension=userType|userLevel|userDept&period=7d|30d|all
Response: {
  "dimension": "userDept",
  "data": [
    { "label": "技术部", "count": 120 },
    { "label": "产品部", "count": 45 }
  ],
  "total": 165
}
```

#### F4: 埋点记录（AOP 自动拦截，无需独立接口）

通过 AOP 切面自动拦截 `/api/demo/hello`、`/api/demo/hash`、`/api/demo/bubble-sort` 三个接口，记录：
- 接口名称
- 调用人标识（从 Header 获取）
- 调用时间
- 用户维度信息（类型/层级/部门，从 Header 或参数获取）

### 3.2 数据模型

```sql
-- 调用记录表
CREATE TABLE api_call_log (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    api_name    VARCHAR(50)  NOT NULL,    -- 'hello' | 'hash' | 'bubble-sort'
    user_id     VARCHAR(100) NOT NULL,    -- 调用人标识
    user_type   VARCHAR(50),              -- 人员类型（如：正式/外包/实习）
    user_level  VARCHAR(50),              -- 人员层级（如：P5/P6/P7）
    user_dept   VARCHAR(100),             -- 人员部门
    call_time   TIMESTAMP    NOT NULL,    -- 调用时间
    request_body TEXT,                     -- 请求体（可选，用于导出）
    response_body TEXT                      -- 响应体（可选，用于导出）
);
```

### 3.3 系统架构

```
┌─────────────────────────────────────────────────────────┐
│                    testDJnew (前端)                       │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌─────────┐ │
│  │ Tab1     │  │ Tab2     │  │ Tab3     │  │ 报表页  │ │
│  │ HelloWorld│  │ Hash     │  │ BubbleSort│ │(图表)   │ │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘  └────┬────┘ │
│       │              │              │              │      │
│       └──────────────┴──────────────┴──────────────┘      │
│                          │ HTTP                          │
│                    [导出按钮] [报表入口]                     │
└──────────────────────────┬──────────────────────────────┘
                           │
┌──────────────────────────┴──────────────────────────────┐
│                    testDj (后端)                          │
│  ┌─────────────────────────────────────────────────┐    │
│  │              Spring Boot Application             │    │
│  │  ┌──────────┐  ┌──────────┐  ┌──────────────┐  │    │
│  │  │HelloCtrl │  │HashCtrl  │  │SortCtrl      │  │    │
│  │  └────┬─────┘  └────┬─────┘  └──────┬───────┘  │    │
│  │       └──────────────┴───────────────┘           │    │
│  │                      │                           │    │
│  │              ┌───────┴───────┐                   │    │
│  │              │  AOP 埋点切面  │                   │    │
│  │              └───────┬───────┘                   │    │
│  │                      │                           │    │
│  │  ┌──────────┐  ┌─────┴────┐  ┌──────────────┐  │    │
│  │  │ExportCtrl│  │StatsCtrl │  │ CallLogRepo  │  │    │
│  │  └──────────┘  └──────────┘  └──────┬───────┘  │    │
│  │                                      │          │    │
│  │                              ┌───────┴───────┐  │    │
│  │                              │  H2 Database  │  │    │
│  │                              └───────────────┘  │    │
│  └─────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────┘
```

### 3.4 前端页面设计

#### 主页面结构

```
┌──────────────────────────────────────────────────────┐
│  [Logo] 多接口演示系统          [导出▼] [查看报表]     │
├──────────────────────────────────────────────────────┤
│  [ HelloWorld ]  [ 哈希算法 ]  [ 冒泡排序 ]           │
├──────────────────────────────────────────────────────┤
│                                                      │
│   ┌─ 输入区域 ─────────────────────────────────┐    │
│   │  (根据当前Tab动态切换输入表单)               │    │
│   └────────────────────────────────────────────┘    │
│   [ 执行 ]                                          │
│                                                      │
│   ┌─ 结果展示区域 ─────────────────────────────┐    │
│   │  (展示接口返回结果)                          │    │
│   └────────────────────────────────────────────┘    │
│                                                      │
│   ┌─ 历史记录表格 ─────────────────────────────┐    │
│   │  时间 | 输入 | 输出 | 操作                   │    │
│   └────────────────────────────────────────────┘    │
│                                                      │
└──────────────────────────────────────────────────────┘
```

#### 报表页面

```
┌──────────────────────────────────────────────────────┐
│  [返回主页]   调用统计报表                              │
├──────────────────────────────────────────────────────┤
│  维度选择: [人员类型▼] [人员层级▼] [人员部门▼]          │
│  时间范围: [最近7天▼]                                  │
├──────────────────────────────────────────────────────┤
│                                                      │
│  ┌─ 折线图 ──────────┐  ┌─ 饼图 ──────────────┐    │
│  │  调用趋势(按时间)   │  │  各维度占比          │    │
│  │  📈                │  │  🥧                 │    │
│  └────────────────────┘  └─────────────────────┘    │
│                                                      │
│  ┌─ 柱状图 ────────────────────────────────────┐    │
│  │  各接口调用次数对比                            │    │
│  │  📊                                         │    │
│  └─────────────────────────────────────────────┘    │
│                                                      │
└──────────────────────────────────────────────────────┘
```

### 3.5 跨仓接口契约对齐

| 前端调用 | 后端接口 | 方法 | 数据流向 |
|----------|----------|------|----------|
| Tab1 执行按钮 | `/api/demo/hello` | POST | 前端→后端→前端渲染 |
| Tab2 执行按钮 | `/api/demo/hash` | POST | 前端→后端→前端渲染 |
| Tab3 执行按钮 | `/api/demo/bubble-sort` | POST | 前端→后端→前端渲染 |
| 导出按钮 | `/api/demo/export` | GET | 后端→文件下载 |
| 报表页 | `/api/demo/statistics` | GET | 后端→前端图表渲染 |

**Header 约定**（所有请求携带）：
```
X-User-Id: <用户ID>
X-User-Type: <人员类型>
X-User-Level: <人员层级>
X-User-Dept: <人员部门>
```

---

## 4. 方案对比与选型 (Approach Trade-offs)

### 4.1 后端技术栈

| 方案 | 优点 | 缺点 | 推荐 |
|------|------|------|------|
| **A: Java + Spring Boot** | 企业标准、AOP 天然支持埋点、生态完善 | 启动稍重 | ✅ **推荐** |
| B: Node.js + Express | 轻量、前后端同语言 | AOP 需手动实现、企业级特性弱 | |
| C: Python + FastAPI | 开发快、算法实现简洁 | 与蚂蚁技术栈匹配度低 | |

### 4.2 前端图表库

| 方案 | 优点 | 缺点 | 推荐 |
|------|------|------|------|
| **A: ECharts** | 折线/饼/柱状图全支持、中文文档完善、性能好 | 包体积较大 | ✅ **推荐** |
| B: Chart.js | 轻量、简单 | 中文支持弱、复杂图表能力有限 | |
| C: AntV | 蚂蚁出品、设计语言统一 | 学习成本略高 | 备选 |

### 4.3 埋点实现方式

| 方案 | 优点 | 缺点 | 推荐 |
|------|------|------|------|
| **A: AOP 切面自动拦截** | 零侵入、统一管理、不遗漏 | 灵活性略低 | ✅ **推荐** |
| B: 每个接口手动埋点 | 灵活可控 | 代码冗余、易遗漏 | |
| C: Filter/Interceptor | 统一入口 | 无法获取响应体 | |

---

## 5. 技术栈确认 (Tech Stack)

### 5.1 testDj（后端）

| 组件 | 选型 | 版本 |
|------|------|------|
| 语言 | Java | 17+ |
| 框架 | Spring Boot | 3.x |
| 构建 | Maven | 3.9+ |
| 数据库 | H2 (内嵌) | 2.x |
| ORM | Spring Data JPA | - |
| AOP | Spring AOP | - |
| API 文档 | SpringDoc OpenAPI | 2.x |

### 5.2 testDJnew（前端）

| 组件 | 选型 | 版本 |
|------|------|------|
| 框架 | React | 18.x |
| UI 库 | Ant Design | 5.x |
| 图表 | ECharts (via echarts-for-react) | 5.x |
| 构建 | Vite | 5.x |
| HTTP | Axios | 1.x |
| 语言 | TypeScript | 5.x |

---

## 6. 目录结构规划

### 6.1 testDj（后端）

```
testDj-main/
├── pom.xml
├── src/main/java/com/example/demo/
│   ├── DemoApplication.java
│   ├── controller/
│   │   ├── HelloController.java
│   │   ├── HashController.java
│   │   ├── BubbleSortController.java
│   │   ├── ExportController.java
│   │   └── StatisticsController.java
│   ├── service/
│   │   ├── HelloService.java
│   │   ├── HashService.java
│   │   ├── BubbleSortService.java
│   │   ├── ExportService.java
│   │   └── StatisticsService.java
│   ├── aspect/
│   │   └── ApiCallLogAspect.java
│   ├── entity/
│   │   └── ApiCallLog.java
│   ├── repository/
│   │   └── ApiCallLogRepository.java
│   └── dto/
│       ├── HelloRequest.java / HelloResponse.java
│       ├── HashRequest.java / HashResponse.java
│       ├── SortRequest.java / SortResponse.java
│       └── StatisticsResponse.java
├── src/main/resources/
│   ├── application.yml
│   └── schema.sql
└── src/test/java/...
```

### 6.2 testDJnew（前端）

```
testDJnew-main/
├── package.json
├── vite.config.ts
├── tsconfig.json
├── src/
│   ├── main.tsx
│   ├── App.tsx
│   ├── pages/
│   │   ├── DemoPage.tsx          # 主页面（含三Tab）
│   │   └── ReportPage.tsx        # 报表页面
│   ├── components/
│   │   ├── tabs/
│   │   │   ├── HelloTab.tsx
│   │   │   ├── HashTab.tsx
│   │   │   └── BubbleSortTab.tsx
│   │   ├── charts/
│   │   │   ├── LineChart.tsx
│   │   │   ├── PieChart.tsx
│   │   │   └── BarChart.tsx
│   │   └── ExportButton.tsx
│   ├── services/
│   │   └── api.ts                # 统一 API 调用
│   └── types/
│       └── index.ts              # TypeScript 类型定义
└── public/
```

---

## 7. 实施计划 (Implementation Plan)

### Phase 1: 后端基础接口（testDj）
1. 初始化 Spring Boot 项目（pom.xml + 启动类）
2. 实现 HelloWorld 接口
3. 实现哈希算法接口
4. 实现冒泡排序接口

### Phase 2: 埋点与统计（testDj）
5. 创建 `api_call_log` 表 + JPA Entity
6. 实现 AOP 切面自动记录调用
7. 实现统计查询接口

### Phase 3: 导出功能（testDj）
8. 实现 CSV 导出接口

### Phase 4: 前端主页面（testDJnew）
9. 初始化 React + Vite + Ant Design 项目
10. 实现三 Tab 主页面
11. 对接三个业务接口

### Phase 5: 前端报表与导出（testDJnew）
12. 实现导出按钮功能
13. 实现报表页面（折线图 + 饼图 + 柱状图）
14. 对接统计接口

---

## 8. 风险与待决项 (Risks & Open Items)

| # | 风险/待决项 | 影响 | 缓解措施 |
|---|------------|------|----------|
| R1 | 用户维度信息（类型/层级/部门）无上游数据源 | 报表维度数据可能为空 | MVP 阶段由前端 Header 传入；后续可对接用户中心 |
| R2 | H2 数据库重启后数据丢失 | 埋点数据不持久 | 可配置 H2 文件持久化模式或迁移至 MySQL |
| R3 | 导出大数据量时性能 | 内存溢出 | 限制单次导出条数（如 10000 条），流式写入 |
| R4 | 前端跨域 | 开发联调受阻 | 后端配置 CORS 或前端 Vite proxy |

---

## 9. Spec 自检 (Self-Review)

- [x] **Placeholder 扫描**: 无 TBD/TODO，所有假设已明确标注
- [x] **内部一致性**: 接口契约与前端调用表一一对应；数据模型字段覆盖所有报表维度
- [x] **范围检查**: 聚焦于单一系统的前后端实现，可在一个实施计划内完成
- [x] **歧义检查**: 每个接口的输入输出已明确定义；埋点方式选定 AOP 方案

---

## 10. 下一步 (Next Steps)

1. ✅ 需求澄清文档完成（本文件）
2. → 用户评审本规格文档
3. → 通过后进入 **实施计划阶段**（writing-plans）
4. → 按 Phase 1-5 顺序实施
