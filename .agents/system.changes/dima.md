# 需求澄清与方案设计 — Brainstorming 输出

## 1. 项目概述

基于 **Java Spring Boot + Vue.js (Element UI)** 技术栈，从零搭建一个包含三个后端接口、前端可视化展示及调用埋点分析的全栈演示应用。

---

## 2. 仓库分配（推断）

| 仓库 | 角色 | 技术栈 |
|------|------|--------|
| **testDj-main** | 后端服务 | Java 17 / Spring Boot 3.x / Maven |
| **testDJnew-main** | 前端应用 | Vue 3 / Vite / Element Plus |

---

## 3. 需求拆解清单

### 3.1 后端接口（3 个）

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| HelloWorld | GET/POST | `/api/hello` | 返回 "Hello World" 及时间戳 |
| 哈希算法 | POST | `/api/hash` | 接收字符串，返回指定哈希值（如 SHA-256） |
| 冒泡排序 | POST | `/api/sort/bubble` | 接收整数数组，返回排序后结果 |

**待确认项：**
- 哈希算法具体类型（SHA-256 / MD5 等）→ 暂定 **SHA-256**
- 输入输出格式 → 暂定 JSON

### 3.2 导出接口

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 导出 | GET | `/api/export?tab={tabName}` | 导出指定 tab 的执行结果 |

**待确认项：**
- 导出格式（CSV / Excel）→ 暂定 **CSV**
- 导出文件名规则

### 3.3 埋点与统计

**埋点数据模型：**

| 字段 | 类型 | 说明 |
|------|------|------|
| callerId | String | 调用人标识 |
| callerType | String | 人员类型（如：正式/外包/实习生） |
| callerLevel | String | 人员层级（如：P5/P6/P7/P8） |
| callerDept | String | 人员部门（如：技术部/产品部/运营部） |
| apiName | String | 调用的接口名 |
| callTime | LocalDateTime | 调用时间 |
| responseTime | Long | 响应耗时(ms) |

**统计接口：**

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 调用统计 | GET | `/api/stats/calls?dimension={dim}&start=&end=` | 按维度统计调用次数 |
| 调用人详情 | GET | `/api/stats/callers` | 获取调用人列表 |

### 3.4 前端页面

**页面结构：**
- 顶部：三个 Tab（HelloWorld / 哈希算法 / 冒泡排序）
- Tab 内容区：对应接口的调用输入 + 结果展示
- 工具栏：导出按钮（当前 Tab 内容）
- 底部：统计看板区域

**统计看板（调用可视化）：**
- 折线图：按时间维度的调用趋势
- 饼图：按人员类型/部门的调用占比
- 柱状图：按人员层级的调用量对比

**待确认项：**
- 埋点数据是真实采集还是模拟数据
- 人员维度数据的来源

---

## 4. 技术方案对比

### 方案 A（推荐）：单体 Spring Boot + Vue SPA

| 层 | 方案 | 优势 |
|----|------|------|
| 后端 | Spring Boot 3.x + Spring Data JPA + H2 内存库 | 零配置数据库，快速启动 |
| 前端 | Vue 3 + Vite + Element Plus + ECharts | 图表丰富，组件成熟 |
| 构建 | Maven 多模块（前后端分离） | 清晰分层 |

### 方案 B：Spring Boot + MyBatis + MySQL

| 层 | 方案 | 优势 |
|----|------|------|
| 后端 | Spring Boot 3.x + MyBatis + MySQL | 适合复杂查询场景 |
| 前端 | 同上 | — |
| 劣势 | 需要额外安装 MySQL | 环境搭建成本高 |

### 方案 C：Spring Boot 内嵌 + 静态资源

| 层 | 方案 | 优势 |
|----|------|------|
| 后端 | Spring Boot 打包前端静态资源 | 单 jar 部署 |
| 前端 | 构建产物置于后端 resources/static | 部署简单 |
| 劣势 | 前后端耦合，开发调试不便 |

---

## 5. 推荐方案：方案 A

- 后端：**Spring Boot 3.x + Spring Data JPA + H2 内存数据库**
- 前端：**Vue 3 + Vite + Element Plus + ECharts**
- 埋点数据：前端模拟生成（含人员维度字段），测试用

---

## 6. 数据流架构

```
[Vue 前端] 
  ├─ Tab 操作 → POST /api/hello|hash|sort/bubble → [后端接口] → 返回结果
  ├─ 导出按钮 → GET /api/export?tab=xxx → [后端] → CSV 文件下载
  └─ 统计看板 → GET /api/stats/calls?dimension=xxx → [后端] → 返回聚合数据

[后端拦截器]
  └─ 每次接口调用 → 记录埋点 → 存储到 H2 数据库
```

---

## 7. 未澄清待办（需人工确认）

| # | 事项 | 优先级 |
|---|------|--------|
| 1 | 哈希算法具体类型（SHA-256 / MD5 / 其他） | 中 |
| 2 | 导出格式（CSV / Excel / PDF） | 低 |
| 3 | 埋点人员维度数据是模拟数据还是对接真实系统 | 中 |
| 4 | 是否需要在后端做登录认证获取调用人信息 | 低 |