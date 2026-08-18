# 需求澄清规格文档 — 多仓库协同开发

> **文档版本**: v1.0  
> **生成日期**: 2025-07-14  
> **关联仓库**: testDj (后端) / testDJnew (前端)  
> **需求节点**: 需求澄清  
> **技能**: brainstorming

---

## 1. 需求概述

### 1.1 业务目标

构建一个包含算法演示、结果导出、调用统计可视化的全栈示例系统。

### 1.2 功能清单

| 编号 | 功能 | 归属仓库 | 优先级 |
|------|------|----------|--------|
| F1 | HelloWorld 接口 | testDj | P0 |
| F2 | 哈希算法接口 (MD5/SHA256) | testDj | P0 |
| F3 | 冒泡排序接口 (含步骤) | testDj | P0 |
| F4 | 导出接口 (Excel) | testDj | P0 |
| F5 | 埋点拦截器 (自动记录调用) | testDj | P0 |
| F6 | 埋点查询报表接口 | testDj | P0 |
| F7 | 三 Tab 展示页面 | testDJnew | P0 |
| F8 | 导出按钮 (调用后端导出) | testDJnew | P0 |
| F9 | 可视化报表 (折线/饼图/柱状图) | testDJnew | P0 |

### 1.3 非功能需求

- 埋点记录需包含：调用人、调用时间、接口路径、人员类型、人员层级、人员部门
- 导出格式为 Excel (.xlsx)
- 前端图表支持三种维度切换：人员类型、人员层级、人员部门
- 接口响应时间 < 500ms (P95)

---

## 2. 技术架构

### 2.1 技术选型

| 层级 | 技术 | 版本 | 选型理由 |
|------|------|------|----------|
| 后端框架 | Spring Boot | 3.2.x | 企业级标准，自动配置 |
| 后端语言 | Java | 17 | LTS 版本，生态成熟 |
| 构建工具 | Maven | 3.9.x | 依赖管理简单 |
| 前端框架 | React | 18.x | 生态丰富，组件化 |
| 前端语言 | TypeScript | 5.x | 类型安全 |
| 前端构建 | Vite | 5.x | 快速 HMR |
| UI 组件库 | Ant Design | 5.x | 企业级，Tab/Button 开箱即用 |
| 图表库 | ECharts | 5.x | 折线/饼图/柱状图原生支持 |
| 数据存储 | H2 | 2.x | 内存模式，零配置 |
| ORM | Spring Data JPA | - | 自动建表 |
| 导出 | Apache POI | 5.x | Excel 生成 |
| 包管理 (前端) | pnpm | 8.x | 快速、节省磁盘 |

### 2.2 架构图 (逻辑)

```
┌─────────────────────────────────────────────────────────┐
│  testDJnew (前端 React)                                  │
│  ┌──────────┐  ┌──────────┐  ┌──────────────────────┐  │
│  │ Tab:     │  │ Tab:     │  │ Tab:                 │  │
│  │ Hello    │  │ Hash     │  │ BubbleSort           │  │
│  │ World    │  │          │  │                      │  │
│  └──────────┘  └──────────┘  └──────────────────────┘  │
│  ┌──────────────────────────────────────────────────┐   │
│  │ 报表区域 (ECharts)                                │   │
│  │ [折线图] [饼图] [柱状图] — 维度切换下拉框         │   │
│  └──────────────────────────────────────────────────┘   │
│  ┌──────────┐                                           │
│  │ 导出按钮  │ ────── POST /api/export ──────────┐      │
│  └──────────┘                                     │      │
└───────────────────────────────────────────────────┼──────┘
                                                    │
┌───────────────────────────────────────────────────┼──────┐
│  testDj (后端 Spring Boot)                        │      │
│  ┌──────────────────────────────────────────────────────┐│
│  │  MetricsInterceptor (埋点拦截器)                      ││
│  │  记录每次 /api/* 调用 → H2 数据库                     ││
│  └──────────────────────────────────────────────────────┘│
│  ┌─────────────┐  ┌─────────────┐  ┌──────────────┐     │
│  │ HelloWorld  │  │ HashController│ │ BubbleSort   │     │
│  │ Controller  │  │               │ │ Controller   │     │
│  └──────┬──────┘  └──────┬───────┘  └──────┬───────┘     │
│         │                │                  │            │
│  ┌──────┴──────┐  ┌──────┴───────┐  ┌──────┴───────┐     │
│  │ ExportService│  │ HashService  │  │ SortService  │     │
│  └─────────────┘  └──────────────┘  └──────────────┘     │
│  ┌──────────────────────────────────────────────────┐    │
│  │ MetricsService / MetricsRepository (JPA)          │    │
│  └──────────────────────────────────────────────────┘    │
└──────────────────────────────────────────────────────────┘
```

---

## 3. 后端接口契约 (testDj)

### 3.1 基础路径

所有接口基础路径: `/api`

### 3.2 接口详细定义

#### 3.2.1 HelloWorld

```
GET /api/helloworld

Response 200:
{
  "code": 0,
  "message": "success",
  "data": {
    "message": "Hello World",
    "timestamp": "2025-07-14T10:00:00Z"
  }
}
```

#### 3.2.2 哈希算法

```
POST /api/hash
Content-Type: application/json

Request:
{
  "input": "hello",
  "algorithm": "MD5"          // 枚举: MD5 | SHA256
}

Response 200:
{
  "code": 0,
  "message": "success",
  "data": {
    "input": "hello",
    "algorithm": "MD5",
    "hash": "5d41402abc4b2a76b9719d911017c592"
  }
}

Error 400:
{
  "code": 400,
  "message": "不支持的算法: XXX，仅支持 MD5 / SHA256",
  "data": null
}
```

#### 3.2.3 冒泡排序

```
POST /api/bubblesort
Content-Type: application/json

Request:
{
  "array": [5, 3, 8, 1, 2],
  "order": "asc"              // 枚举: asc | desc
}

Response 200:
{
  "code": 0,
  "message": "success",
  "data": {
    "original": [5, 3, 8, 1, 2],
    "sorted": [1, 2, 3, 5, 8],
    "steps": [
      {"round": 1, "array": [3, 5, 1, 2, 8]},
      {"round": 2, "array": [3, 1, 2, 5, 8]},
      {"round": 3, "array": [1, 2, 3, 5, 8]},
      {"round": 4, "array": [1, 2, 3, 5, 8]}
    ],
    "comparisons": 10
  }
}
```

#### 3.2.4 导出接口

```
POST /api/export
Content-Type: application/json

Request:
{
  "type": "bubblesort",       // 枚举: helloworld | hash | bubblesort
  "data": { ... }             // 与对应接口返回的 data 结构一致
}

Response 200:
Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
Content-Disposition: attachment; filename="export-{type}-{timestamp}.xlsx"

Body: 二进制 Excel 文件
```

#### 3.2.5 埋点查询接口

```
GET /api/metrics?dimension=personType&startDate=2025-01-01&endDate=2025-07-14

参数:
  dimension   - 维度: personType | level | department
  startDate   - 起始日期 (可选)
  endDate     - 结束日期 (可选)

Response 200:
{
  "code": 0,
  "message": "success",
  "data": {
    "dimension": "personType",
    "items": [
      {
        "label": "正式员工",
        "count": 150,
        "subItems": [
          { "label": "/api/helloworld", "count": 50 },
          { "label": "/api/hash", "count": 60 },
          { "label": "/api/bubblesort", "count": 40 }
        ]
      },
      {
        "label": "外包人员",
        "count": 80,
        "subItems": [...]
      }
    ],
    "totalCalls": 230
  }
}
```

### 3.3 统一响应封装

所有接口返回统一结构：

```json
{
  "code": 0,           // 0=成功, 非0=业务错误
  "message": "string",
  "data": {} | null
}
```

### 3.4 埋点数据模型 (H2)

```sql
CREATE TABLE metrics_record (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  caller_name   VARCHAR(100)   NOT NULL,   -- 调用人
  caller_type   VARCHAR(50)    NOT NULL,   -- 人员类型: 正式员工/外包/实习生
  caller_level  VARCHAR(50)    NOT NULL,   -- 人员层级: P6/P7/P8/P9
  caller_dept   VARCHAR(100)   NOT NULL,   -- 人员部门: 技术部/产品部/运营部
  api_path      VARCHAR(200)   NOT NULL,   -- 接口路径
  api_method    VARCHAR(10)    NOT NULL,   -- HTTP 方法
  call_time     TIMESTAMP      NOT NULL,   -- 调用时间
  client_ip     VARCHAR(50),               -- 客户端 IP
  user_agent    VARCHAR(500)               -- UA
);
```

### 3.5 埋点拦截器设计

- 使用 Spring `HandlerInterceptor` 拦截所有 `/api/**` 请求
- 从请求头 `X-Caller-Name`、`X-Caller-Type`、`X-Caller-Level`、`X-Caller-Dept` 提取调用人信息
- 若请求头缺失，使用默认值：`anonymous / 未知 / 未知 / 未知`
- 异步写入 H2，不阻塞业务响应

---

## 4. 前端页面设计 (testDJnew)

### 4.1 路由

新增路由：`/dashboard`

### 4.2 页面布局

```
┌──────────────────────────────────────────────────────┐
│  算法演示平台                                         │
├──────────────────────────────────────────────────────┤
│  [HelloWorld] [哈希算法] [冒泡排序]   ← Tabs          │
├──────────────────────────────────────────────────────┤
│                                                      │
│  输入区域 (Tab 内)                                    │
│  ┌──────────────────────────────────────────────┐    │
│  │ hash: [  输入文本  ] [算法: MD5 ▼] [执行]    │    │
│  │ bubblesort: [  数组  ] [排序: asc ▼] [执行]  │    │
│  └──────────────────────────────────────────────┘    │
│                                                      │
│  结果展示区域                                         │
│  ┌──────────────────────────────────────────────┐    │
│  │ {JSON 结果}                                   │    │
│  └──────────────────────────────────────────────┘    │
│                                                      │
│  [📥 导出 Excel]   ← 导出按钮                        │
│                                                      │
├──────────────────────────────────────────────────────┤
│  调用统计报表                                         │
│  ┌──────────────────────────────────────────────┐    │
│  │ 维度: [人员类型 ▼]  图表: [柱状图 ▼]         │    │
│  ├──────────────────────────────────────────────┤    │
│  │                                              │    │
│  │         ECharts 图表渲染区                    │    │
│  │                                              │    │
│  └──────────────────────────────────────────────┘    │
│  [折线图] [饼图] [柱状图]  ← 图表类型切换按钮        │
└──────────────────────────────────────────────────────┘
```

### 4.3 组件树

```
DashboardPage
├── AlgorithmTabs
│   ├── HelloWorldTab        (仅展示结果，无输入)
│   ├── HashTab              (输入框 + 算法选择 + 执行按钮)
│   └── BubbleSortTab        (数组输入 + 排序方向 + 执行按钮)
├── ExportButton             (导出当前 Tab 结果)
└── MetricsPanel
    ├── DimensionSelector    (维度下拉)
    ├── ChartTypeSelector    (图表类型切换)
    └── MetricsChart          (ECharts 实例)
```

### 4.4 状态管理

使用 React `useState` + `useEffect`，无需引入 Redux：

```typescript
interface DashboardState {
  activeTab: 'helloworld' | 'hash' | 'bubblesort';
  tabResults: Record<string, ApiResult | null>;
  loading: boolean;
  metricsDimension: 'personType' | 'level' | 'department';
  chartType: 'line' | 'pie' | 'bar';
  metricsData: MetricsData | null;
}
```

### 4.5 API 调用封装

```typescript
// api/client.ts
const BASE_URL = '/api';

async function fetchHelloWorld(): Promise<ApiResult>;
async function fetchHash(input: string, algorithm: string): Promise<ApiResult>;
async function fetchBubbleSort(array: number[], order: string): Promise<ApiResult>;
async function exportExcel(type: string, data: any): Promise<Blob>;
async function fetchMetrics(dimension: string): Promise<MetricsData>;
```

---

## 5. 跨仓对齐点检查

### 5.1 接口契约一致性

| 检查项 | testDj (后端) | testDJnew (前端) | 状态 |
|--------|---------------|-------------------|------|
| 统一响应格式 `{code, message, data}` | 所有接口统一封装 | 前端统一拦截解析 | ✅ 对齐 |
| HelloWorld GET 无参 | 已定义 | 自动调用展示 | ✅ 对齐 |
| Hash POST body `{input, algorithm}` | 已定义 | 表单字段映射 | ✅ 对齐 |
| BubbleSort POST body `{array, order}` | 已定义 | 数组输入解析 | ✅ 对齐 |
| Export POST `{type, data}` → Blob | POI 生成 Excel | `fetch` + `Blob` 下载 | ✅ 对齐 |
| Metrics GET `?dimension=` | 返回 `{dimension, items, totalCalls}` | ECharts 数据映射 | ✅ 对齐 |
| 埋点请求头 `X-Caller-*` | 拦截器读取 | 前端请求拦截器注入 | ⚠️ 需实现 |

### 5.2 开发顺序

```
Phase 1: testDj 后端骨架 + 3 个核心接口
Phase 2: testDj 埋点拦截器 + 导出接口 + 报表接口
Phase 3: testDJnew 前端页面 + Tab + API 调用
Phase 4: testDJnew 导出按钮 + 图表可视化
Phase 5: 联调测试 + 埋点验证
```

---

## 6. 待澄清项 (假设记录)

> 由于全流水线模式，以下决策已由引擎自主做出。如有偏差请在下阶段修正。

| # | 决策项 | 假设值 | 风险等级 |
|---|--------|--------|----------|
| 1 | 后端技术栈 | Spring Boot 3 + Java 17 + Maven | 低 |
| 2 | 前端技术栈 | React 18 + TypeScript + Vite | 低 |
| 3 | UI 组件库 | Ant Design 5 | 低 |
| 4 | 图表库 | ECharts 5 | 低 |
| 5 | 数据库 | H2 内存数据库 | 中 — 重启丢失数据 |
| 6 | 调用人信息来源 | 请求头 `X-Caller-*` 注入 | 中 — 需网关/前端配合 |
| 7 | 导出格式 | Excel (.xlsx) | 低 |
| 8 | 冒泡排序最大数组长度 | 100 | 低 |
| 9 | 埋点数据保留策略 | 内存存储，不自动清理 | 中 |
| 10 | 端口 | 后端 8080，前端 5173 (Vite) | 低 |

---

## 7. 风险与边界

### 7.1 已知风险

1. **H2 内存数据库**：重启后埋点数据丢失，生产环境需迁移至 MySQL/PostgreSQL
2. **调用人身份**：当前设计依赖请求头注入，实际部署需配合网关或 SSO 认证
3. **导出大文件**：冒泡排序步骤过多时 Excel 可能较大，需限制最大数组长度

### 7.2 明确不包含的内容

- 用户登录/认证系统
- 国际化 (i18n)
- 移动端适配
- 灰度发布/AB 测试
- CI/CD 流水线配置

---

## 8. 自审查结论

| 审查项 | 结果 |
|--------|------|
| 占位符扫描 | 无 TBD/TODO |
| 内部一致性 | 接口契约与前端数据模型一致 |
| 范围检查 | 功能聚焦，无过度设计 |
| 歧义检查 | 已明确所有枚举值、数据格式 |

---

> **文档状态**: ✅ 已完成  
> **下一步**: 进入 writing-plans 阶段生成详细实施计划