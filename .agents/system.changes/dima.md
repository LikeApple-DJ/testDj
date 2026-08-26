# 需求澄清 — 跨仓设计分析

> 阶段: 需求澄清 (loop-1) | 技能: brainstorming | 日期: 2025-07-17

---

## 1. 跨仓依赖与现状摘要

| 仓库 | 当前状态 | 推测角色 |
|------|---------|---------|
| `[testDj]` | 仅含 `07hubtasty`（"hello world"），无项目骨架 | ✅ **后端** (Spring Boot) |
| `[testDJnew]` | 仅含 `README.md`（"# testDJnew"），空仓库 | ✅ **前端** (React) |

**结论**: ✅ 已确认。技术栈：Java Spring Boot 后端 + React 前端 + ECharts 图表库。testDj=后端，testDJnew=前端。

---

## 2. 需求分解

### 2.1 后端接口（3 个核心 API）

| 接口 | 功能 | 待澄清 |
|------|------|--------|
| `/helloworld` | 返回 "Hello World" 字符串 | 实现简单，无歧义 |
| `/hash` | 对输入执行哈希算法 | ✅ 支持 MD5 / SHA-1 / SHA-256，输入为文本字符串，通过 `algorithm` 参数选择 |
| `/bubblesort` | 对输入数组执行冒泡排序 | ✅ 输入 JSON 整数数组 `{"array": [3,1,4,1,5]}`，返回排序结果与步骤 |

### 2.2 前端页面（3 Tab + 导出）

| 功能 | 描述 | 待澄清 |
|------|------|--------|
| Tab 1: HelloWorld | 展示 `/helloworld` 接口结果 | — |
| Tab 2: Hash | 展示 `/hash` 接口结果 | ✅ 文本输入框 + 算法下拉选择（MD5/SHA-1/SHA-256） |
| Tab 3: BubbleSort | 展示 `/bubblesort` 接口结果 | ✅ JSON 数组文本输入框（如 `[3,1,4,1,5]`） |
| 导出按钮 | 每个 Tab 有导出按钮 | ✅ Excel (.xlsx)，后端生成文件下载 |
| 导出接口 | 后端提供导出 API | `/api/export?tab=helloworld|hash|bubblesort`，返回 `.xlsx` 流 |

### 2.3 埋点与可视化报表

| 功能 | 描述 | 待澄清 |
|------|------|--------|
| 后端埋点 | 记录每次接口调用的「调用人 + 调用次数」 | ✅ JWT Token 解析用户名，写入 `invocation_log` 表 |
| 前端可视化报表 | 在当前页面展示调用情况 | ✅ ECharts（折线图、饼图、柱状图） |
| 维度拆分 | 人员类型、人员层级、人员部门 | ✅ 数据库 `user_profile` 表（type/level/department 字段） |
| 图表类型 | 折线图、饼图、柱状图 | ✅ 三种图表均需实现，支持维度切换 |

---

## 3. ✅ 已确认决策

| 决策点 | 确认结果 |
|--------|---------|
| 技术栈 | **Java Spring Boot** (后端) + **React** (前端) + **ECharts** (图表) |
| 仓库角色 | `[testDj]` = 后端，`[testDJnew]` = 前端 |
| 哈希算法 | 支持多种：MD5 / SHA-1 / SHA-256，通过 `algorithm` 参数选择 |
| 导出格式 | Excel (.xlsx)，后端生成文件流下载 |
| 身份识别 | JWT Token 解析 + 数据库 `user_profile` 查询维度数据 |

---

## 4. 确认方案：Java Spring Boot + React + ECharts

### 4.1 后端 `[testDj]` — Spring Boot 项目结构

```
testDj-main/
├── pom.xml                          # Maven 依赖 (Spring Boot Web, JWT, Apache POI, H2)
├── src/main/java/com/example/demo/
│   ├── DemoApplication.java         # 启动类
│   ├── config/
│   │   ├── SecurityConfig.java      # Spring Security + JWT 过滤器配置
│   │   └── WebConfig.java           # CORS 跨域配置
│   ├── controller/
│   │   ├── AlgorithmController.java # /api/helloworld, /api/hash, /api/bubblesort
│   │   ├── ExportController.java    # /api/export
│   │   └── StatsController.java     # /api/stats (埋点查询，多维度)
│   ├── service/
│   │   ├── AlgorithmService.java
│   │   ├── ExportService.java
│   │   └── StatsService.java
│   ├── model/
│   │   ├── InvocationLog.java       # 埋点实体 (id, username, api, timestamp)
│   │   └── UserProfile.java         # 用户维度实体 (username, type, level, department)
│   ├── repository/
│   │   ├── InvocationLogRepository.java
│   │   └── UserProfileRepository.java
│   ├── security/
│   │   ├── JwtTokenFilter.java      # JWT 解析过滤器
│   │   └── JwtTokenProvider.java    # JWT 生成/验证
│   └── dto/
│       ├── HashRequest.java
│       ├── SortRequest.java
│       └── StatsResponse.java
└── src/main/resources/
    ├── application.yml
    └── data.sql                     # 初始化用户维度数据
```

### 4.2 前端 `[testDJnew]` — React 项目结构

```
testDJnew-main/
├── package.json
├── public/index.html
├── src/
│   ├── App.jsx                      # 路由 + 布局
│   ├── index.jsx
│   ├── pages/
│   │   ├── DashboardPage.jsx        # 主页面：3 Tab + 导出按钮
│   │   └── ReportPage.jsx           # 报表页面：埋点可视化
│   ├── components/
│   │   ├── HelloWorldTab.jsx        # Tab 1
│   │   ├── HashTab.jsx              # Tab 2
│   │   ├── BubbleSortTab.jsx        # Tab 3
│   │   ├── ExportButton.jsx         # 导出按钮组件
│   │   ├── LineChart.jsx            # ECharts 折线图
│   │   ├── PieChart.jsx             # ECharts 饼图
│   │   ├── BarChart.jsx             # ECharts 柱状图
│   │   └── DimensionSelector.jsx    # 维度切换控件
│   ├── services/
│   │   └── api.js                   # axios 封装，统一请求
│   └── utils/
│       └── auth.js                  # JWT Token 管理
└── ...
```

### 4.3 仓间接口契约

| 接口 | 方法 | 路径 | 请求体 | 响应 |
|------|------|------|--------|------|
| HelloWorld | GET | `/api/helloworld` | — | `{"message":"Hello World","timestamp":"..."}` |
| Hash | POST | `/api/hash` | `{"input":"text","algorithm":"SHA-256"}` | `{"algorithm":"SHA-256","input":"text","hash":"..."}` |
| BubbleSort | POST | `/api/bubblesort` | `{"array":[3,1,4,1,5]}` | `{"original":[3,1,4,1,5],"sorted":[1,1,3,4,5],"steps":[...]}` |
| Export | GET | `/api/export?tab=helloworld` | — | `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet` 流 |
| Stats | GET | `/api/stats?dimension=type&chart=line` | — | `{"dimension":"type","data":[{"key":"正式员工","count":42},...]}` |

> ⚠️ **仓间对齐点**: 以上接口契约为 `[testDj]` 与 `[testDJnew]` 的唯一交互边界，前后端须严格对齐请求/响应格式。

---

## 5. 待办

- [x] 用户确认技术栈 → Java Spring Boot + React + ECharts
- [x] 用户确认仓库角色分配 → testDj=后端, testDJnew=前端
- [x] 用户确认哈希算法选择 → MD5/SHA-1/SHA-256
- [x] 用户确认导出格式 → Excel (.xlsx)
- [x] 用户确认调用人身份来源 → JWT Token + 数据库
- [ ] 进入下一阶段：详细设计（writing-plans）→ 按上述项目结构初始化两仓代码