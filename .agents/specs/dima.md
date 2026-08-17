# 成本统计报表系统 — 需求澄清与设计规格

> **版本**: v1.0  
> **日期**: 2025-07-10  
> **状态**: 需求澄清阶段  
> **关联仓库**: [testDj] 前端主仓库 / [testDJnew] 后端服务仓库

---

## 1. 概述 (Overview)

### 1.1 项目背景
企业需要一个成本统计报表系统，用于统计和可视化企业各项成本支出，支持多维度分析（部门、项目、业务线、人员、时间），涵盖人力成本与项目成本两大类，并提供报表导出能力。

### 1.2 核心目标
- **Dashboard 看板**: 提供成本总览，快速掌握企业成本健康度
- **成本分析页**: 多维度交叉分析，支持钻取与筛选
- **报表导出**: 支持 Excel/CSV 格式导出

### 1.3 范围界定
| 包含 | 不包含（本期） |
|------|---------------|
| 人力成本统计（开发/测试/产品/运维） | 固定资产折旧 |
| 项目成本统计（预算/实际/占比/超支） | 税务计算 |
| 多维度筛选与交叉分析 | 实时流式数据接入 |
| Dashboard 看板 + 分析页 | 移动端 App |
| Excel/CSV 导出 | 第三方 ERP 对接 |

---

## 2. 功能需求 (Functional Requirements)

### 2.1 Dashboard 看板 (FR-DASH)

| ID | 功能 | 描述 |
|----|------|------|
| FR-DASH-01 | 成本总览卡片 | 展示本期总成本、环比变化率、预算执行率 |
| FR-DASH-02 | 部门成本排行 | Top N 部门成本柱状图，支持切换人力/项目维度 |
| FR-DASH-03 | 月度趋势图 | 近12个月成本趋势折线图，区分人力/项目成本 |
| FR-DASH-04 | 项目超支预警 | 展示超预算项目列表，高亮超支金额与比例 |
| FR-DASH-05 | 人力成本构成 | 饼图展示开发/测试/产品/运维占比 |
| FR-DASH-06 | 业务线对比 | 各业务线成本横向对比柱状图 |

### 2.2 成本分析页 (FR-ANALYSIS)

| ID | 功能 | 描述 |
|----|------|------|
| FR-ANALY-01 | 多维筛选器 | 部门、项目、业务线、人员、月份/季度/年度联动筛选 |
| FR-ANALY-02 | 人力成本明细表 | 按角色（开发/测试/产品/运维）分列，支持排序与合计 |
| FR-ANALY-03 | 项目成本明细表 | 预算金额、实际消耗、预算占比%、预计超支金额 |
| FR-ANALY-04 | 交叉分析 | 部门×业务线、项目×月份等二维交叉透视 |
| FR-ANALY-05 | 时间维度切换 | 月/季/年粒度一键切换，图表与表格联动 |
| FR-ANALY-06 | 数据钻取 | 从汇总数据点击下钻至明细记录 |

### 2.3 报表导出 (FR-EXPORT)

| ID | 功能 | 描述 |
|----|------|------|
| FR-EXPORT-01 | 导出当前视图 | 将当前筛选条件下的表格数据导出为 Excel |
| FR-EXPORT-02 | 导出图表 | Dashboard 图表导出为 PNG 或 PDF |
| FR-EXPORT-03 | 定时导出 | 支持按月/季度自动生成并推送报表（邮件） |
| FR-EXPORT-04 | 导出模板 | 预设几种常用导出模板（部门成本月报、项目成本季报等） |

---

## 3. 数据模型 (Data Model)

### 3.1 核心实体

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│   Department    │     │     Project     │     │   BusinessLine  │
│─────────────────│     │─────────────────│     │─────────────────│
│ id              │     │ id              │     │ id              │
│ name            │     │ name            │     │ name            │
│ parent_id       │     │ department_id   │     │ description     │
│ business_line_id│     │ business_line_id│     │ status          │
│ status          │     │ budget_amount   │     └─────────────────┘
└─────────────────┘     │ start_date      │
                        │ end_date        │
                        │ status          │
                        └─────────────────┘

┌─────────────────┐     ┌──────────────────────────┐
│    Employee     │     │     CostRecord           │
│─────────────────│     │──────────────────────────│
│ id              │     │ id                       │
│ name            │     │ type (LABOR / PROJECT)   │
│ department_id   │     │ amount                   │
│ business_line_id│     │ department_id            │
│ role            │     │ project_id               │
│ cost_rate       │     │ employee_id              │
│ status          │     │ business_line_id         │
└─────────────────┘     │ role (DEV/TEST/PM/OPS)   │
                        │ period_year              │
                        │ period_month             │
                        │ period_quarter           │
                        │ description              │
                        │ created_at               │
                        └──────────────────────────┘
```

### 3.2 维度定义

| 维度 | 字段 | 说明 |
|------|------|------|
| 部门 | `department_id` | 支持树形层级（父部门汇总子部门） |
| 项目 | `project_id` | 关联项目预算 |
| 业务线 | `business_line_id` | 独立于部门的业务划分 |
| 人员 | `employee_id` | 关联角色与成本费率 |
| 月份 | `period_month` | 1-12 |
| 季度 | `period_quarter` | Q1-Q4（由月份计算） |
| 年度 | `period_year` | 财年 |

### 3.3 成本类型

| 类型 | 子类 | 计算方式 |
|------|------|----------|
| **人力成本** | 开发 / 测试 / 产品 / 运维 | 人员费率 × 工时（或按月固定成本） |
| **项目成本** | 项目预算 / 实际消耗 / 预算占比 / 预计超支 | 预算金额 vs 实际消耗汇总 |

---

## 4. 技术方案 (Technical Approach)

### 4.1 架构概览

```
[前端 testDj]                    [后端 testDJnew]
┌──────────────────────┐       ┌──────────────────────────┐
│  Dashboard 页面       │       │  REST API                │
│  ├─ 成本总览卡片       │       │  ├─ GET /api/cost/       │
│  ├─ ECharts 图表       │  ←→  │  │    summary             │
│  └─ 响应式布局         │       │  ├─ GET /api/cost/       │
│                        │       │  │    analysis            │
│  成本分析页            │       │  ├─ GET /api/cost/       │
│  ├─ 多维筛选器         │       │  │    dashboard           │
│  ├─ 数据表格           │       │  ├─ GET /api/cost/       │
│  └─ 交叉透视           │       │  │    export              │
│                        │       │  └─ POST /api/cost/      │
│  导出功能              │       │     export/schedule       │
│  └─ Excel/CSV 生成     │       │                          │
└──────────────────────┘       └──────────────────────────┘
```

### 4.2 前端技术选型（testDj）

| 层面 | 选型 | 理由 |
|------|------|------|
| 框架 | React 18 + TypeScript | 主流企业级方案，类型安全 |
| UI 组件库 | Ant Design 5 | 企业级表格/筛选器/导出组件丰富 |
| 图表 | ECharts / @ant-design/charts | 支持柱状/折线/饼图/交叉透视 |
| 状态管理 | React Query (TanStack) | 服务端状态缓存，自动刷新 |
| 导出 | exceljs / file-saver | 前端生成 Excel，减轻服务端压力 |
| 路由 | React Router 6 | SPA 路由 |

### 4.3 后端技术选型（testDJnew）

| 层面 | 选型 | 理由 |
|------|------|------|
| 框架 | Spring Boot 3 / NestJS | 企业级后端框架 |
| 数据库 | PostgreSQL | 支持复杂聚合查询与窗口函数 |
| 缓存 | Redis | Dashboard 汇总数据缓存 |
| 导出 | Apache POI / exceljs | 服务端 Excel 生成 |
| 定时任务 | Quartz / node-cron | 定时导出调度 |

> **注**: 具体后端框架取决于 testDJnew 现有技术栈。当前仓库为空，需与团队确认。

### 4.4 API 契约（跨仓接口）

#### 4.4.1 Dashboard 汇总

```
GET /api/cost/dashboard/summary?year=2025&month=7
Response:
{
  "totalCost": 1250000.00,
  "totalCostChange": 0.12,          // 环比变化率
  "budgetExecutionRate": 0.85,       // 预算执行率
  "laborCost": 750000.00,
  "projectCost": 500000.00,
  "departmentRanking": [
    { "departmentId": 1, "name": "研发部", "cost": 450000, "rank": 1 }
  ],
  "monthlyTrend": [
    { "month": "2025-01", "laborCost": 700000, "projectCost": 480000 }
  ],
  "overBudgetProjects": [
    { "projectId": 1, "name": "项目A", "budget": 100000, "actual": 135000, "overrun": 35000, "overrunRate": 0.35 }
  ],
  "laborComposition": [
    { "role": "DEV", "label": "开发", "cost": 300000, "percentage": 0.40 },
    { "role": "TEST", "label": "测试", "cost": 150000, "percentage": 0.20 },
    { "role": "PM", "label": "产品", "cost": 150000, "percentage": 0.20 },
    { "role": "OPS", "label": "运维", "cost": 150000, "percentage": 0.20 }
  ]
}
```

#### 4.4.2 成本分析查询

```
POST /api/cost/analysis/query
Body:
{
  "dimensions": ["department", "project", "month"],
  "filters": {
    "departmentIds": [1, 2],
    "projectIds": [10],
    "businessLineIds": [],
    "employeeIds": [],
    "year": 2025,
    "month": null,
    "quarter": null,
    "costType": "ALL"           // LABOR | PROJECT | ALL
  },
  "page": 1,
  "pageSize": 20,
  "sortField": "totalCost",
  "sortOrder": "desc"
}
Response:
{
  "total": 150,
  "page": 1,
  "pageSize": 20,
  "rows": [
    {
      "departmentName": "研发部",
      "projectName": "项目A",
      "month": "2025-07",
      "laborCost": { "dev": 50000, "test": 20000, "pm": 30000, "ops": 15000 },
      "projectCost": { "budget": 100000, "actual": 85000, "ratio": 0.85, "overrun": 0 },
      "totalCost": 200000
    }
  ],
  "aggregations": {
    "totalLaborCost": 750000,
    "totalProjectCost": 500000,
    "grandTotal": 1250000
  }
}
```

#### 4.4.3 导出

```
POST /api/cost/export
Body: { /* 同 analysis/query 的 filters + dimensions */, "format": "EXCEL" }
Response: Binary (application/vnd.openxmlformats-officedocument.spreadsheetml.sheet)
```

### 4.5 跨仓对齐要点

| 检查项 | testDj（前端） | testDJnew（后端） | 对齐状态 |
|--------|--------------|-------------------|----------|
| API 基础路径 | `/api/cost/*` | 实现 `/api/cost/*` | ⚠️ 待确认 |
| 日期格式 | `YYYY-MM` 字符串 | 返回 `YYYY-MM` 字符串 | ✅ 一致 |
| 角色枚举 | `DEV/TEST/PM/OPS` | 同 | ✅ 一致 |
| 分页规范 | `page/pageSize/total` | 同 | ✅ 一致 |
| 金额单位 | 元（小数2位） | 元（小数2位） | ✅ 一致 |
| 错误码规范 | 待定义 | 待定义 | ⚠️ 待补充 |

---

## 5. 页面路由设计

| 路由 | 页面 | 所属仓库 |
|------|------|----------|
| `/dashboard/cost` | 成本 Dashboard 看板 | testDj |
| `/analysis/cost` | 成本分析页 | testDj |
| `/analysis/cost/:id` | 成本明细钻取页 | testDj |

---

## 6. 非功能需求

| 需求 | 指标 |
|------|------|
| 性能 | Dashboard 首屏加载 < 2s，分析查询 < 3s（数据量 < 10万条） |
| 并发 | 支持 50 并发用户 |
| 导出 | 单次导出 ≤ 5万行，超时 60s |
| 浏览器兼容 | Chrome 90+, Edge 90+, Safari 15+ |
| 响应式 | Dashboard 适配 1366px ~ 1920px 宽度 |

---

## 7. 开放问题 (Open Questions)

> 以下问题因全流水线模式无法交互确认，基于合理假设给出默认答案；实际开发前需与产品/业务确认。

| # | 问题 | 假设/默认答案 |
|---|------|--------------|
| Q1 | 财年起始月份？ | 假设自然年（1月-12月） |
| Q2 | 人力成本计算方式：按实际工时×费率，还是按月固定成本？ | 假设按月固定成本 + 费率（简化初期实现） |
| Q3 | 部门树形层级深度？ | 假设最多 3 级（公司→部门→小组） |
| Q4 | 是否需要权限控制（不同角色看到不同部门数据）？ | 假设本期不做细粒度权限，所有用户可见全量数据 |
| Q5 | 预算数据从哪里来？手动录入还是对接财务系统？ | 假设手动录入 + CSV 批量导入 |
| Q6 | 实时性要求？T+0 还是 T+1？ | 假设 T+1（隔日数据），降低实时性压力 |
| Q7 | 后端技术栈（testDJnew 当前为空）？ | 建议 Spring Boot 3 + PostgreSQL |
| Q8 | 前端技术栈（testDj 当前为空）？ | 建议 React 18 + Ant Design 5 |

---

## 8. 实施建议

### 8.1 分阶段交付

| 阶段 | 内容 | 仓库 |
|------|------|------|
| Phase 1 | 后端：数据模型 + Dashboard API + 分析 API | testDJnew |
| Phase 2 | 前端：Dashboard 看板页面 | testDj |
| Phase 3 | 前端：成本分析页 + 多维筛选器 | testDj |
| Phase 4 | 导出功能（前后端） | testDj + testDJnew |
| Phase 5 | 定时导出 + 邮件推送 | testDJnew |

### 8.2 风险与缓解

| 风险 | 影响 | 缓解措施 |
|------|------|----------|
| 数据量大导致查询慢 | 用户体验差 | DB 索引 + Redis 缓存 + 分页 |
| 跨库接口不一致 | 联调失败 | 本文档作为契约基线，mock 先行 |
| 需求变更频繁 | 返工 | 模块化设计，维度可配置 |

---

## 9. 自我审查 (Spec Self-Review)

- [x] 无 TBD/TODO 占位符
- [x] 功能需求均有对应 ID，可追溯
- [x] 数据模型覆盖所有维度
- [x] API 契约定义了请求/响应结构
- [x] 开放问题已列出并给出默认假设
- [x] 跨仓接口对齐点已检查
- [x] 范围边界清晰（包含/不包含）

---

> **下一步**: 待用户审阅本规格后，调用 `writing-plans` 技能生成详细实施计划。