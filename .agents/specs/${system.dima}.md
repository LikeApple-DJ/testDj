# 设计规格说明：算法演示与调用埋点可视化平台

> 阶段：需求澄清（brainstorming）
> 仓库：`testDj`（后端）、`testDJnew`（前端）
> 日期：2025-08-13
> 状态：草案（自主决策，待评审）

---

## 0. 文档说明

本 spec 由 brainstorming 技能在"全流水线模式"下产出。由于禁止阻塞式提问，所有未澄清的歧义点以**显式假设（Assumption）**形式标注，评审时可逐条确认或推翻。本阶段产物仅为设计文档，**不修改任何代码文件**。

---

## 1. 需求理解

### 1.1 功能清单

| 编号 | 需求 | 归属仓库 |
|------|------|----------|
| F1 | 后端提供三个接口：helloworld、哈希算法、冒泡排序 | testDj（后端） |
| F2 | 前端新增页面，三个 Tab 分别展示三个接口执行结果 | testDJnew（前端） |
| F3 | 导出按钮 + 后端导出接口，支持导出各页面展示结果 | testDj + testDJnew |
| F4 | 后端埋点：记录调用次数与调用人 | testDj |
| F5 | 前端在当前页面可视化埋点报表（折线图/饼图/柱状图，按人员类型/层级/部门等维度） | testDJnew |

### 1.2 跨仓职责划分

- **testDj（后端）**：算法接口、导出接口、埋点采集与查询接口、人员维度元数据。
- **testDJnew（前端）**：页面与 Tab、接口调用、导出触发、报表可视化。

> 假设 A1：`testDj` 承担后端，`testDJnew` 承担前端。依据：`testDj` 已含 "hello world" 占位，更接近后端起步；`testDJnew` 命名含 "new" 暗示前端新仓。若实际相反，仅需互换仓库映射，接口契约不变。

---

## 2. 技术选型（自主决策）

两仓库均为空仓库，无既有约束。基于企业级"人员类型/层级/部门"埋点场景的常见选型：

### 2.1 后端（testDj）

- **语言/框架**：Java 17 + Spring Boot 3.x
- **数据存储**：H2（开发期内存）/ MySQL 8（生产）——埋点表
- **导出格式**：Excel（.xlsx，Apache POI）+ CSV 兜底
- **埋点采集**：AOP 切面 + 注解 `@TrackInvoke`，非侵入业务代码

> 假设 A2：后端采用 Spring Boot。若团队偏好 Node/Python，接口契约（REST + JSON）保持不变，仅实现层替换。

### 2.2 前端（testDJnew）

- **框架**：React 18 + TypeScript + Vite
- **UI 组件**：Ant Design 5（Tab、Button、Table）
- **图表库**：Apache ECharts 5（折线图/饼图/柱状图统一支持）
- **HTTP**：Axios

> 假设 A3：前端采用 React + ECharts。若团队偏好 Vue，组件结构可平移。

---

## 3. 架构设计

### 3.1 总体数据流

```
[前端 testDJnew 页面]
   │
   ├── Tab1/2/3 调用 ──> POST /api/algo/{helloworld|hash|bubble} ──> [后端 testDj]
   │                          │（AOP 埋点：记录接口、调用人、时间、入参摘要、耗时）
   │                          ▼
   │                     [埋点表 invoke_log]
   │
   ├── 导出按钮 ──────> GET /api/export?type={helloworld|hash|bubble} ──> 返回 xlsx/csv
   │
   └── 报表区 ────────> GET /api/metrics/summary?dimension=...&range=... ──> 聚合数据
                            ▼
                      折线图（调用次数趋势）/ 饼图（人员维度占比）/ 柱状图（部门对比）
```

### 3.2 模块边界（后端 testDj）

| 模块 | 职责 | 对外接口 |
|------|------|----------|
| algo | 三个算法执行 | 3 个 REST 接口 |
| export | 结果导出 | 1 个 REST 接口 |
| tracking | 埋点采集（AOP）+ 聚合查询 | 采集为切面；查询 1 个 REST 接口 |
| person | 人员维度元数据（类型/层级/部门） | 内部服务，供 tracking 聚合 |

每个模块单一职责，通过 well-defined 接口通信，可独立测试。

---

## 4. 接口契约（跨库对齐核心）

### 4.1 算法接口（F1）

#### 4.1.1 helloworld

```
POST /api/algo/helloworld
Request:  { "input": "world" }            // 可选，默认 "world"
Response: { "code":0, "data": { "message": "Hello, world!" }, "traceId":"..." }
```

#### 4.1.2 哈希算法

```
POST /api/algo/hash
Request:  { "input": "abc", "algo": "SHA-256" }   // algo 可选，默认 SHA-256；支持 MD5/SHA-1/SHA-256
Response: { "code":0, "data": { "input":"abc", "algo":"SHA-256", "digest":"ba7816bf..." }, "traceId":"..." }
```

#### 4.1.3 冒泡排序

```
POST /api/algo/bubble
Request:  { "input": [5,3,8,1,9,2] }              // 整数数组
Response: { "code":0, "data": { "input":[...], "sorted":[1,2,3,5,8,9], "swaps":7, "durationMs":0.12 }, "traceId":"..." }
```

> 契约约束：统一响应体 `{ code, data, traceId, [msg] }`；`code=0` 表示成功，非 0 表示业务错误。所有接口幂等（纯计算，无副作用）。

### 4.2 导出接口（F3）

```
GET /api/export?type={helloworld|hash|bubble}&format={xlsx|csv}
Response: 二进制流（Content-Disposition: attachment; filename=algo-<type>-<ts>.xlsx）
```

- 导出内容为该类型接口最近一次（或最近 N 次，默认最近 100 次）的执行结果快照，来源于埋点表 + 结果缓存。
- `format=csv` 作为兜底，避免 POI 依赖问题。

> 假设 A4：导出范围为"该接口最近 100 次调用结果"。若需按时间区间导出，追加 `from/to` 查询参数即可，向后兼容。

### 4.3 埋点查询接口（F5）

```
GET /api/metrics/summary?dimension={personType|personLevel|department|interface}&range={1d|7d|30d}&chart={line|pie|bar}
Response: {
  "code":0,
  "data": {
    "dimension": "department",
    "chart": "bar",
    "series": [
      { "label":"研发一部", "value": 128 },
      { "label":"研发二部", "value": 96 }
    ],
    "trend": [ {"date":"2025-08-07","value":40}, ... ]   // 仅 line 图填充
  }
}
```

> 契约约束：`series` 为通用维度聚合；`trend` 仅折线图填充。前端按 `chart` 字段选择渲染形式。

---

## 5. 埋点数据模型（F4）

### 5.1 invoke_log 表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint PK | 自增 |
| interface | varchar(32) | helloworld/hash/bubble/export/metrics |
| caller_id | varchar(64) | 调用人 ID（从请求头 `X-User-Id` 或鉴权上下文获取） |
| caller_name | varchar(64) | 调用人姓名 |
| person_type | varchar(32) | 人员类型（正式/外包/实习） |
| person_level | varchar(16) | 人员层级（L1-L6 或 P 序列） |
| department | varchar(64) | 部门 |
| input_summary | varchar(512) | 入参摘要（截断，避免敏感数据） |
| result_summary | varchar(512) | 结果摘要 |
| duration_ms | int | 耗时 |
| invoke_time | datetime | 调用时间 |
| trace_id | varchar(64) | 链路 ID |

### 5.2 采集机制

- 注解 `@TrackInvoke(interface="hash")` 标注于算法 Controller 方法。
- AOP 环绕切面捕获：入参摘要、出参摘要、耗时、异常。
- 调用人信息从 `RequestContext`（由鉴权拦截器填充）获取；无鉴权时回退到请求头 `X-User-Id`。
- 人员维度（type/level/department）由 `person` 模块按 `caller_id` 查询元数据表填充。

> 假设 A5：调用人身份通过请求头 `X-User-Id` + `X-User-Name` 传递（开发期），生产期由网关鉴权注入。人员维度元数据由独立 `person` 表维护，初始可内置种子数据。

---

## 6. 前端设计（F2/F3/F5）

### 6.1 页面结构

```
AlgoDashboardPage
├── Tabs
│   ├── Tab1: HelloWorld   ── 输入框 + 执行按钮 + 结果展示
│   ├── Tab2: Hash        ── 输入框 + 算法下拉 + 执行按钮 + 摘要展示
│   └── Tab3: BubbleSort   ── 数组输入 + 执行按钮 + 排序前后对比 + 交换次数
├── 工具栏
│   └── 导出按钮（导出当前 Tab 对应接口结果）
└── 报表区（页面下方，跨 Tab 共享）
    ├── 维度切换：人员类型 / 人员层级 / 部门 / 接口
    ├── 时间范围：近1天 / 近7天 / 近30天
    └── 图表区
        ├── 折线图（调用次数趋势）
        ├── 饼图（维度占比）
        └── 柱状图（维度对比）
```

### 6.2 组件边界

| 组件 | 职责 | 依赖 |
|------|------|------|
| AlgoTabs | 三个算法 Tab 切换与执行 | algoApi |
| ExportButton | 触发导出下载 | exportApi |
| MetricsFilter | 维度/时间范围筛选 | metricsApi |
| ChartLine/ChartPie/ChartBar | 三种图表渲染 | ECharts |

每个组件单一职责，可独立测试。

---

## 7. 错误处理

- 后端：统一全局异常处理，返回 `{ code, msg, traceId }`；算法入参校验失败返回 `code=400`。
- 前端：Axios 拦截器统一弹错；导出失败回退提示。
- 埋点采集失败**不得影响业务接口**（AOP 切面 try-catch + 异步落库）。

---

## 8. 测试策略

| 层 | 范围 | 方式 |
|----|------|------|
| 后端单元 | 三个算法纯函数 | JUnit5 参数化 |
| 后端集成 | 接口契约 + 埋点落库 | MockMvc + H2 |
| 前端单元 | 组件渲染与交互 | Vitest + Testing Library |
| 契约对齐 | 跨仓接口字段一致性 | 基于本 spec 第 4 章的契约快照 |

---

## 9. 跨仓对齐点（验收核心）

| 对齐点 | 后端（testDj） | 前端（testDJnew） | 一致性要求 |
|--------|----------------|-------------------|-----------|
| 算法接口路径 | `/api/algo/{helloworld,hash,bubble}` | 调用同路径 | 路径与 method 一致 |
| 响应体结构 | `{code,data,traceId}` | 解析同结构 | 字段名/类型一致 |
| 导出接口 | `GET /api/export?type=&format=` | 触发下载同参数 | 参数名一致 |
| 埋点查询 | `GET /api/metrics/summary?dimension=&range=&chart=` | 调用同参数 | `series`/`trend` 结构一致 |
| 调用人传递 | 读取 `X-User-Id`/`X-User-Name` | 请求头注入同字段 | 字段名一致 |

---

## 10. 显式假设汇总

| 编号 | 假设 | 影响 |
|------|------|------|
| A1 | testDj=后端，testDJnew=前端 | 仓库映射，可互换 |
| A2 | 后端 Spring Boot | 实现层，契约不变 |
| A3 | 前端 React+ECharts | 实现层，契约不变 |
| A4 | 导出范围为最近 100 次 | 可扩展为时间区间 |
| A5 | 调用人经请求头传递，人员维度独立元数据表 | 鉴权方案 |

---

## 11. 待评审开放问题

1. 人员维度（类型/层级/部门）数据源：是否对接现有 HR/组织架构系统？还是本仓内置？
2. 埋点数据保留周期：是否需要 TTL 清理？
3. 导出是否需要权限控制（仅本人可导出自己的调用记录）？
4. 报表是否需要实时刷新还是按天缓存？

> 以上问题不阻塞 spec 落盘；评审确认后可在实现阶段调整，接口契约向后兼容扩展。

---

## 12. 后续步骤

本 spec 通过评审后，进入 writing-plans 阶段产出实现计划，按 testDj → testDJnew 顺序实现，先固化接口契约再并行开发。
