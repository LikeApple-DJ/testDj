# 成本统计报表系统 — 设计规格文档

> 创建日期: 2025-01-20  
> 状态: 草案  
> 技术栈: Vue3 + Spring Boot + MySQL

---

## 1. 项目概述

### 1.1 目标

开发一个企业级成本统计报表系统，用于统计和展示企业各项成本支出情况，支持多维度数据分析和报表导出。

### 1.2 核心功能

- **Dashboard 看板**：直观展示成本关键指标和趋势
- **成本统计分析**：按部门、项目、业务线、人员、时间等维度统计
- **人力成本管理**：开发、测试、产品、运维人员成本统计
- **项目成本管理**：预算、实际消耗、预算占比、预计超支
- **报表导出**：支持 Excel 格式导出
- **基础数据管理**：部门、项目、人员、业务线的 CRUD

### 1.3 仓库分工

| 仓库 | 角色 | 技术栈 |
|------|------|--------|
| testDj | 前端 | Vue3 + Element Plus + ECharts |
| testDJnew | 后端 | Spring Boot + MyBatis-Plus + MySQL |

---

## 2. 系统架构

### 2.1 整体架构

```
┌──────────────────────────────────────────────────────────┐
│                    testDj (前端 Vue3)                      │
│  ┌──────────┐  ┌──────────────┐  ┌────────────────────┐  │
│  │ Dashboard │  │ 成本统计分析  │  │ 数据管理(基础数据)  │  │
│  │  看板页   │  │    报表页     │  │  部门/项目/人员     │  │
│  └──────────┘  └──────────────┘  └────────────────────┘  │
│  Element Plus + ECharts + Axios + Pinia + Vue Router      │
├──────────────────────────────────────────────────────────┤
│                    REST API (JSON)                         │
│                    CORS 跨域配置                           │
├──────────────────────────────────────────────────────────┤
│                  testDJnew (后端 Spring Boot)               │
│  ┌────────────────────────────────────────────────────┐  │
│  │ Controller → Service → Repository (MyBatis-Plus)    │  │
│  │ 统计聚合引擎 │ 导出服务(EasyExcel) │ 统一异常处理    │  │
│  └────────────────────────────────────────────────────┘  │
│                    MySQL 8.x                              │
└──────────────────────────────────────────────────────────┘
```

### 2.2 架构选型理由

采用经典单体分层架构（Controller → Service → Repository），理由：
- 当前需求为单一报表系统，无需微服务拆分
- 开发效率高，团队易于协作
- 后续可按需演进为模块化或微服务

---

## 3. 数据库设计

### 3.1 ER 关系

```
sys_department (1) ──── (N) sys_employee
sys_department (1) ──── (N) biz_project
biz_business_line (1) ──── (N) biz_project
sys_employee (1) ──── (N) cost_labor
biz_project (1) ──── (N) cost_labor
biz_project (1) ──── (N) cost_project
```

### 3.2 表结构定义

#### sys_department（部门表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 主键 |
| name | VARCHAR(100) | 部门名称 |
| parent_id | BIGINT | 上级部门ID（0=顶级） |
| level | INT | 层级 |
| sort_order | INT | 排序号 |
| status | TINYINT | 状态（1=启用, 0=禁用） |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

#### sys_employee（人员表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 主键 |
| name | VARCHAR(50) | 姓名 |
| employee_no | VARCHAR(30) | 工号 |
| department_id | BIGINT FK | 所属部门 |
| role | VARCHAR(20) | 角色（DEVELOPER/TESTER/PRODUCT/OPS） |
| salary | DECIMAL(12,2) | 月薪 |
| entry_date | DATE | 入职日期 |
| status | TINYINT | 状态 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

#### biz_business_line（业务线表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 主键 |
| name | VARCHAR(100) | 业务线名称 |
| description | VARCHAR(500) | 描述 |
| status | TINYINT | 状态 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

#### biz_project（项目表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 主键 |
| name | VARCHAR(200) | 项目名称 |
| department_id | BIGINT FK | 所属部门 |
| business_line_id | BIGINT FK | 所属业务线 |
| budget | DECIMAL(14,2) | 项目预算 |
| start_date | DATE | 开始日期 |
| end_date | DATE | 结束日期 |
| status | TINYINT | 状态（0=未开始, 1=进行中, 2=已完成, 3=已取消） |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

#### cost_labor（人力成本表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 主键 |
| employee_id | BIGINT FK | 人员ID |
| project_id | BIGINT FK | 项目ID（可为空） |
| year_month | VARCHAR(7) | 月份（格式：2025-01） |
| work_hours | DECIMAL(6,2) | 工时（小时） |
| labor_cost | DECIMAL(12,2) | 人力成本 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

#### cost_project（项目成本表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 主键 |
| project_id | BIGINT FK | 项目ID |
| year_month | VARCHAR(7) | 月份 |
| budget_amount | DECIMAL(14,2) | 当月预算分配 |
| actual_cost | DECIMAL(14,2) | 当月实际消耗 |
| cost_type | VARCHAR(30) | 成本类型（LABOR/EQUIPMENT/TRAVEL/OTHER） |
| remark | VARCHAR(500) | 备注 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

---

## 4. 前端设计

### 4.1 技术选型

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue 3 | 3.x | 前端框架 |
| Vite | latest | 构建工具 |
| Element Plus | latest | UI 组件库 |
| ECharts | 5.x | 图表库 |
| Axios | latest | HTTP 客户端 |
| Vue Router | 4.x | 路由管理 |
| Pinia | latest | 状态管理 |

### 4.2 页面结构

#### 4.2.1 Dashboard 看板页 (`/dashboard`)

**布局：**
- 顶部：4 个汇总卡片（总成本、本月成本、活跃项目数、在职人员数）
- 中部左：月度成本趋势折线图（支持切换月/季度/年度）
- 中部右：部门成本占比饼图
- 下部左：项目预算 vs 实际消耗柱状图（Top 10）
- 下部右：人力成本分布堆叠柱状图（开发/测试/产品/运维）
- 底部：预计超支项目预警列表（红色高亮超支项）

**交互：**
- 时间范围选择器（默认当前年度）
- 图表联动筛选
- 卡片点击跳转对应详情页

#### 4.2.2 成本统计分析页 (`/cost-analysis`)

**筛选区域：**
- 部门下拉（支持多选）
- 项目下拉（支持多选）
- 业务线下拉
- 人员搜索
- 时间范围：月份选择器 / 季度选择器 / 年度选择器
- 查询 / 重置按钮

**Tab 切换：**
- **人力成本 Tab**：表格展示人员、角色、部门、月度成本、累计成本；支持排序
- **项目成本 Tab**：表格展示项目名、预算金额、实际消耗、预算占比(%)、预计超支金额；超支行红色高亮

**操作：**
- 导出 Excel 按钮
- 表格分页

#### 4.2.3 数据管理页 (`/management/*`)

- `/management/departments` — 部门管理（树形表格 + CRUD 弹窗）
- `/management/projects` — 项目管理（表格 + CRUD 弹窗，关联部门/业务线）
- `/management/employees` — 人员管理（表格 + CRUD 弹窗，关联部门/角色）
- `/management/business-lines` — 业务线管理（表格 + CRUD 弹窗）
- `/management/cost-entry` — 成本数据录入（按月批量录入人力成本和项目成本）

### 4.3 项目目录结构

```
testDj-main/
├── public/
├── src/
│   ├── api/                    # API 接口定义
│   │   ├── dashboard.js
│   │   ├── cost.js
│   │   ├── department.js
│   │   ├── project.js
│   │   ├── employee.js
│   │   └── businessLine.js
│   ├── views/
│   │   ├── dashboard/
│   │   │   └── DashboardView.vue
│   │   ├── cost/
│   │   │   └── CostAnalysisView.vue
│   │   └── management/
│   │       ├── DepartmentView.vue
│   │       ├── ProjectView.vue
│   │       ├── EmployeeView.vue
│   │       ├── BusinessLineView.vue
│   │       └── CostEntryView.vue
│   ├── components/
│   │   ├── charts/
│   │   │   ├── TrendChart.vue
│   │   │   ├── PieChart.vue
│   │   │   ├── BarChart.vue
│   │   │   └── StackedBarChart.vue
│   │   ├── SummaryCard.vue
│   │   └── FilterBar.vue
│   ├── router/
│   │   └── index.js
│   ├── stores/
│   │   ├── dashboard.js
│   │   └── cost.js
│   ├── utils/
│   │   ├── request.js          # Axios 封装
│   │   └── format.js           # 格式化工具
│   ├── layouts/
│   │   └── MainLayout.vue      # 主布局（侧边栏+顶栏）
│   ├── App.vue
│   └── main.js
├── index.html
├── package.json
├── vite.config.js
└── .env.development
```

---

## 5. 后端设计

### 5.1 技术选型

| 技术 | 版本 | 用途 |
|------|------|------|
| Spring Boot | 3.x | 后端框架 |
| MyBatis-Plus | latest | ORM 框架 |
| MySQL | 8.x | 数据库 |
| EasyExcel | latest | Excel 导出 |
| SpringDoc | latest | API 文档 |
| Lombok | latest | 代码简化 |
| MapStruct | latest | 对象映射 |

### 5.2 API 接口设计

#### Dashboard 接口

```
GET /api/dashboard/summary
  Response: { totalCost, monthCost, projectCount, employeeCount, totalCostYoY, monthCostMoM }

GET /api/dashboard/trend?period=month|quarter|year&year=2025
  Response: { labels: [], datasets: [{ name, data: [] }] }

GET /api/dashboard/department-ratio?year=2025
  Response: [{ departmentName, cost, percentage }]

GET /api/dashboard/project-budget?year=2025&limit=10
  Response: [{ projectName, budget, actualCost, ratio }]

GET /api/dashboard/labor-distribution?year=2025
  Response: { months: [], roles: [{ role, data: [] }] }

GET /api/dashboard/overbudget?year=2025
  Response: [{ projectName, budget, actualCost, overAmount, overRatio }]
```

#### 成本统计接口

```
GET /api/cost/labor-stats?departmentIds=&projectIds=&yearMonth=&page=&size=
  Response: PageResult<{ employeeName, role, departmentName, monthCost, totalCost }>

GET /api/cost/project-stats?departmentIds=&businessLineId=&yearMonth=&page=&size=
  Response: PageResult<{ projectName, budget, actualCost, budgetRatio, overAmount }>

GET /api/cost/export?type=labor|project&filters...
  Response: Excel file download
```

#### 基础数据 CRUD 接口

```
# 部门
GET    /api/departments          # 列表（树形）
POST   /api/departments          # 新增
PUT    /api/departments/{id}     # 修改
DELETE /api/departments/{id}     # 删除

# 项目
GET    /api/projects?page=&size=&keyword=   # 分页列表
POST   /api/projects             # 新增
PUT    /api/projects/{id}        # 修改
DELETE /api/projects/{id}        # 删除

# 人员
GET    /api/employees?page=&size=&keyword=  # 分页列表
POST   /api/employees            # 新增
PUT    /api/employees/{id}       # 修改
DELETE /api/employees/{id}       # 删除

# 业务线
GET    /api/business-lines       # 列表
POST   /api/business-lines       # 新增
PUT    /api/business-lines/{id}  # 修改
DELETE /api/business-lines/{id}  # 删除

# 成本录入
POST   /api/cost/labor           # 人力成本录入（支持批量）
POST   /api/cost/project         # 项目成本录入（支持批量）
GET    /api/cost/labor?yearMonth=&employeeId=  # 查询人力成本
GET    /api/cost/project?yearMonth=&projectId= # 查询项目成本
```

### 5.3 项目目录结构

```
testDJnew-main/
├── src/main/java/com/cost/
│   ├── CostApplication.java
│   ├── controller/
│   │   ├── DashboardController.java
│   │   ├── CostStatsController.java
│   │   ├── DepartmentController.java
│   │   ├── ProjectController.java
│   │   ├── EmployeeController.java
│   │   ├── BusinessLineController.java
│   │   └── CostEntryController.java
│   ├── service/
│   │   ├── DashboardService.java
│   │   ├── CostStatsService.java
│   │   ├── DepartmentService.java
│   │   ├── ProjectService.java
│   │   ├── EmployeeService.java
│   │   ├── BusinessLineService.java
│   │   ├── CostEntryService.java
│   │   └── ExportService.java
│   ├── mapper/
│   │   ├── DepartmentMapper.java
│   │   ├── EmployeeMapper.java
│   │   ├── ProjectMapper.java
│   │   ├── BusinessLineMapper.java
│   │   ├── CostLaborMapper.java
│   │   └── CostProjectMapper.java
│   ├── entity/
│   │   ├── Department.java
│   │   ├── Employee.java
│   │   ├── Project.java
│   │   ├── BusinessLine.java
│   │   ├── CostLabor.java
│   │   └── CostProject.java
│   ├── dto/
│   │   ├── request/            # 请求参数
│   │   └── response/           # 响应对象
│   ├── vo/
│   │   ├── DashboardSummaryVO.java
│   │   ├── CostTrendVO.java
│   │   ├── LaborStatsVO.java
│   │   └── ProjectStatsVO.java
│   ├── config/
│   │   ├── CorsConfig.java
│   │   ├── MyBatisPlusConfig.java
│   │   └── SwaggerConfig.java
│   └── common/
│       ├── Result.java          # 统一响应
│       ├── PageResult.java      # 分页响应
│       └── GlobalExceptionHandler.java
├── src/main/resources/
│   ├── mapper/                  # MyBatis XML
│   ├── db/
│   │   └── schema.sql           # 建表脚本
│   ├── application.yml
│   └── application-dev.yml
└── pom.xml
```

---

## 6. 统计计算逻辑

### 6.1 人力成本统计

- **月度人力成本** = 该员工当月 salary（按工时比例折算）
- **部门人力成本** = SUM(部门下所有员工月度成本)
- **角色维度统计** = 按 DEVELOPER/TESTER/PRODUCT/OPS 分组汇总

### 6.2 项目成本统计

- **实际消耗** = SUM(cost_project.actual_cost) + SUM(cost_labor.labor_cost)（该项目关联）
- **预算占比** = (实际消耗 / 项目预算) × 100%
- **预计超支金额** = MAX(0, 实际消耗 - 项目预算)
- **月度消耗趋势** = 按月汇总 actual_cost

### 6.3 Dashboard 汇总

- **总成本** = SUM(所有 cost_labor.labor_cost) + SUM(所有 cost_project.actual_cost)
- **本月成本** = 当月数据汇总
- **同比/环比** = 与去年同期/上月对比计算增长率

---

## 7. 非功能性要求

### 7.1 性能

- 列表查询响应时间 < 500ms
- Dashboard 聚合查询 < 1s
- 导出 10000 行数据 < 5s

### 7.2 安全

- 接口统一鉴权（预留 JWT Token 机制）
- SQL 注入防护（MyBatis-Plus 参数化查询）
- XSS 防护（前端输入过滤）

### 7.3 兼容性

- 浏览器：Chrome 90+, Firefox 88+, Edge 90+
- 分辨率：最低 1280×720，推荐 1920×1080

---

## 8. 开发里程碑

| 阶段 | 内容 | 预估 |
|------|------|------|
| M1 | 后端基础框架 + 数据库建表 + 基础数据 CRUD | 2天 |
| M2 | 前端基础框架 + 布局 + 基础数据管理页 | 2天 |
| M3 | 成本录入接口 + 前端录入页面 | 1天 |
| M4 | 统计聚合接口 + Dashboard 看板页 | 2天 |
| M5 | 成本统计分析页 + 报表导出 | 2天 |
| M6 | 联调测试 + Bug 修复 | 1天 |
