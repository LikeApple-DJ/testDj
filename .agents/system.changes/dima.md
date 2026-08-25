# 需求澄清与方案设计文档

## 1. 决策记录

| 决策项 | 选择 | 说明 |
|--------|------|------|
| 技术栈 | Java Spring Boot + Vue3 | 后端 Spring Boot，前端 Vue3 |
| 仓库分工 | testDj=后端, ranxitest=前端 | 后端在 testDj 仓库，前端在 ranxitest 仓库 |
| 哈希算法 | SHA-256 | 标准安全哈希算法 |
| 导出格式 | CSV | 标准逗号分隔值 |
| 前端框架 | Vue3 | 配合 Element Plus UI 库 |

## 2. 整体架构

```
┌─────────────────────────────────┐
│   ranxitest (前端 Vue3 SPA)    │
│  ┌─Tab1: HelloWorld ─────────┐ │
│  │  Tab2: 哈希算法(SHA-256)  │ │
│  │  Tab3: 冒泡排序           │ │
│  │  [导出按钮]               │ │
│  │  [统计报表-折线/饼/柱状图] │ │
│  └────────────────────────────┘ │
└──────────────┬──────────────────┘
               │ REST API (Axios)
┌──────────────▼──────────────────┐
│   testDj (后端 Spring Boot)     │
│  ┌────────────────────────────┐ │
│  │ GET /api/hello             │ │
│  │ POST /api/hash             │ │
│  │ POST /api/bubble-sort      │ │
│  │ GET /api/export            │ │
│  │ POST /api/track            │ │
│  │ GET /api/statistics        │ │
│  └────────────────────────────┘ │
│  ┌────────────────────────────┐ │
│  │ 埋点服务 (TrackingService) │ │
│  │ 数据库 (H2/MySQL)          │ │
│  └────────────────────────────┘ │
└─────────────────────────────────┘
```

## 3. 后端接口设计 (testDj 仓库)

### 3.1 HelloWorld 接口
```
GET /api/hello
Response: { "message": "Hello World!", "timestamp": "2026-08-25T12:00:00" }
```

### 3.2 哈希算法接口
```
POST /api/hash
Request: { "input": "待哈希字符串" }
Response: { "algorithm": "SHA-256", "input": "xxx", "output": "hash值" }
```

### 3.3 冒泡排序接口
```
POST /api/bubble-sort
Request: { "array": [3, 1, 4, 1, 5, 9] }
Response: { "original": [3,1,4,1,5,9], "sorted": [1,1,3,4,5,9], "steps": 15 }
```

### 3.4 导出接口
```
GET /api/export?type=hello|hash|bubble
Response: text/csv (Content-Disposition: attachment; filename=export.csv)
```

### 3.5 埋点统计接口
```
POST /api/track
Request: { "apiName": "hello", "caller": "张三", "department": "技术部", "level": "高级", "type": "内部" }

GET /api/statistics?dimension=department|level|type
Response: 按维度聚合的统计数据
```

## 4. 前端页面设计 (ranxitest 仓库)

### 4.1 页面布局
- 顶部：3个 Tab 切换（HelloWorld / 哈希算法 / 冒泡排序）
- 中部：对应 Tab 的执行结果展示区
- 右上角：导出按钮
- 底部：统计报表区域（折线图 + 饼图 + 柱状图）

### 4.2 统计报表维度
- 人员类型（内部/外部）
- 人员层级（初级/中级/高级/专家）
- 人员部门（技术部/产品部/市场部等）

### 4.3 图表类型
- 折线图：按时间跨度展示调用趋势
- 饼图：按维度展示调用占比
- 柱状图：按维度展示调用量对比

## 5. 数据库设计

### 调用记录表 (api_call_log)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 主键 |
| api_name | VARCHAR(50) | 接口名称 |
| caller | VARCHAR(100) | 调用人 |
| department | VARCHAR(100) | 部门 |
| level | VARCHAR(50) | 层级 |
| type | VARCHAR(50) | 人员类型 |
| call_time | DATETIME | 调用时间 |

## 6. 推荐的实现方案

### 方案A（推荐）：一体化 Spring Boot + Vue3 SPA
- 后端：testDj 仓库，Spring Boot + Spring Data JPA + H2 数据库
- 前端：ranxitest 仓库，Vue3 + Element Plus + ECharts
- 优点：结构简单，开发效率高，适合演示
- 缺点：H2 不支持持久化

### 方案B：Spring Boot + MySQL + Vue3
- 后端增加 MySQL 依赖
- 优点：数据持久化，适合生产
- 缺点：需要额外配置数据库

### 方案C：Spring Boot + Vue3 分离部署
- 前后端完全分离，跨域配置
- 优点：独立部署，可扩展
- 缺点：部署复杂度增加

## 7. 仓间对齐点

| 对齐项 | 后端 (testDj) | 前端 (ranxitest) |
|--------|---------------|------------------|
| API 基础路径 | `/api/` | 代理配置 `/api` → `localhost:8080` |
| 数据格式 | JSON | Axios JSON 解析 |
| 导出文件 | 二进制流 (CSV) | Blob 下载 |
| 统计数据 | 按维度聚合 JSON | ECharts 渲染 |