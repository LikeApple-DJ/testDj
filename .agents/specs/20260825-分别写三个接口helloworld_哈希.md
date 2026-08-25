# 需求澄清与接口设计规格书

> 产物编号：20260825-分别写三个接口helloworld_哈希
> 阶段：需求澄清 / 技能：brainstorming

---

## 1. 跨仓依赖与现状摘要

### 1.1 仓库现状

| repo_id | 物理路径 | 现状 |
|---------|----------|------|
| testDj-main | `/root/.agentix/agentic-dev/runs/DEV-966dcd0a-7905-11f1-9649-3b4281182f10-0d66a3a1-0730-4823-a9f4-c44ea5442b7b/worktree/testDj-main` | 仅含 `.git` 与占位文件 `07hubtasty`，无现有业务代码与依赖结构 |
| testDJnew-main | `/root/.agentix/agentic-dev/runs/DEV-966dcd0a-7905-11f1-9649-3b4281182f10-0d66a3a1-0730-4823-a9f4-c44ea5442b7b/worktree/testDJnew-main` | 仅含 `README.md`，为空仓库 |

### 1.2 仓间依赖（已确认）

- `testDj-main`：后端 API 仓库
- `testDJnew-main`：前端页面仓库
- 跨仓对齐点：三个核心接口、导出接口、埋点/报表接口的 URL、请求/响应结构、鉴权、错误码必须一致。

---

## 2. 需求澄清

### 2.1 用户原始需求

1. 后端提供三个接口：
   - `helloworld`：返回问候信息
   - 哈希算法：对输入内容做哈希计算
   - 冒泡排序：对输入数组做冒泡排序
2. 前端新增一个页面，包含三个 Tab，分别展示三个接口的执行结果。
3. 前端页面新增“导出”按钮，后台提供导出接口，支持导出当前/全部 Tab 的展示结果。
4. 后端做埋点，记录：
   - 接口调用次数
   - 调用人信息（人员类型、人员层级、人员部门等）
5. 前端在当前页面可视化展示报表，支持：
   - 折线图
   - 饼图
   - 柱状图
   - 维度切换：人员类型、人员层级、人员部门等

### 2.2 已确认假设

- 接口协议：HTTP JSON REST
- 鉴权/用户身份：后端从 JWT/Session 解析 `userId`、`userType`、`userLevel`、`userDept`
- 哈希算法：支持 MD5、SHA-256、SM3，默认 SHA-256
- 冒泡排序：对整数数组升序排序，去重/不去重可配置
- 导出格式：CSV、Excel 均支持
- 可视化库：ECharts / AntV（默认 ECharts）
- 后端默认技术栈：Java（Spring Boot）
- 前端默认技术栈：React

---

## 3. 功能规划

### 3.1 后端接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| HelloWorld | GET | `/api/v1/demo/hello` | 返回问候语 |
| 哈希算法 | POST | `/api/v1/demo/hash` | 输入内容与算法，返回哈希值 |
| 冒泡排序 | POST | `/api/v1/demo/sort/bubble` | 输入整数数组，返回排序结果 |
| 导出 | POST | `/api/v1/demo/export` | 导出指定 Tab 或全部结果 |
| 埋点上报 | POST | `/api/v1/demo/metrics/track` | 由前端或网关调用，记录调用行为 |
| 报表数据 | GET | `/api/v1/demo/metrics/report` | 按维度聚合的调用统计数据 |

### 3.2 前端页面

- 路由：`/demo-tools`
- 布局：顶部操作区（导出按钮）、中部 Tab 切换、下部报表区
- Tab：
  - HelloWorld
  - Hash 计算
  - 冒泡排序
- 报表区：
  - 维度选择器：人员类型 / 人员层级 / 人员部门
  - 图表切换：折线图 / 饼图 / 柱状图
  - 数据表格：展示具体数值

---

## 4. 接口契约（初稿）

### 4.1 HelloWorld

```
GET /api/v1/demo/hello

Response 200:
{
  "code": 0,
  "data": "Hello, World!",
  "message": "ok"
}
```

### 4.2 哈希算法

```
POST /api/v1/demo/hash
Content-Type: application/json

Request:
{
  "algorithm": "SHA-256",
  "content": "待哈希字符串"
}

Response 200:
{
  "code": 0,
  "data": {
    "algorithm": "SHA-256",
    "original": "待哈希字符串",
    "hash": "..."
  },
  "message": "ok"
}
```

### 4.3 冒泡排序

```
POST /api/v1/demo/sort/bubble
Content-Type: application/json

Request:
{
  "numbers": [3, 1, 4, 1, 5, 9, 2, 6],
  "ascending": true,
  "unique": false
}

Response 200:
{
  "code": 0,
  "data": {
    "input": [3, 1, 4, 1, 5, 9, 2, 6],
    "output": [1, 1, 2, 3, 4, 5, 6, 9]
  },
  "message": "ok"
}
```

### 4.4 导出

```
POST /api/v1/demo/export
Content-Type: application/json

Request:
{
  "tab": "all" | "hello" | "hash" | "bubble",
  "format": "csv" | "excel"
}

Response 200:
Content-Type: application/octet-stream
Content-Disposition: attachment; filename="demo-export.csv"
```

### 4.5 埋点数据模型

```
{
  "traceId": "唯一请求ID",
  "userId": "调用人ID",
  "userType": "人员类型",
  "userLevel": "人员层级",
  "userDept": "人员部门",
  "api": "hello|hash|bubble|export",
  "timestamp": 171...
}
```

### 4.6 报表数据

```
GET /api/v1/demo/metrics/report?dimension=userType&startDate=...&endDate=...

Response 200:
{
  "code": 0,
  "data": [
    { "dimension": "正式员工", "count": 120 },
    { "dimension": "实习生", "count": 30 }
  ],
  "message": "ok"
}
```

---

## 5. 仓间对齐点

1. **接口前缀**：统一为 `/api/v1/demo`
2. **统一响应结构**：`{ code, data, message }`
3. **用户上下文透传**：后端从 JWT/Session 解析 `userId`、`userType`、`userLevel`、`userDept`，前端无需额外请求头；字段名需前后端对齐
4. **导出文件名与 MIME 类型**：CSV 与 Excel 统一命名规则
5. **埋点字段**：`userType`、`userLevel`、`userDept` 必须同时存在于前后端与报表维度中
6. **图表数据结构**：报表接口返回 `{ dimension, count }[]`，前端 ECharts 统一解析

---

## 6. 已确认决策

| 议题 | 决策 |
|------|------|
| 仓库职责切分 | `testDj-main` 后端 API，`testDJnew-main` 前端页面 |
| 哈希算法范围 | 支持 MD5、SHA-256、SM3，默认 SHA-256 |
| 导出格式 | CSV + Excel |
| 用户身份来源 | 后端从 JWT/Session 解析 |
| 默认技术栈 | 后端 Java（Spring Boot），前端 React |

## 7. 下一步工作

- 后端（testDj-main）：搭建 Spring Boot 工程，实现 HelloWorld、Hash、Bubble Sort、Export、Metrics 接口与埋点存储
- 前端（testDJnew-main）：搭建 React 工程，实现 Demo Tools 页面、Tab 切换、导出按钮、报表可视化（折线图/饼图/柱状图）
- 跨仓联调：统一接口契约、CORS、字段命名、错误码
