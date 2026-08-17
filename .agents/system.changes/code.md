# 成本统计报表系统 — 编码实现文档

> **文档版本**: v1.0
> **日期**: 2025-08-17
> **阶段**: 编码实现
> **关联仓库**: [testDj] 前端主仓库 / [testDJnew] 后端服务仓库
> **上游文档**: `.agents/specs/dima.md` (需求) → `.agents/specs/20260817-开发一个成本统计报表...md` (计划) → `.agents/system.changes/design.md` (设计)

---

## 1. 编码总览

### 1.1 实现范围

基于实施计划，共 **13 个任务**，按仓库划分：

| 仓库 | 任务数 | 范围 |
|------|--------|------|
| testDJnew (后端) | 8 | 项目骨架 → 数据模型 → 迁移脚本 → Repository → DTO → Controller → Service → 导出 |
| testDj (前端) | 5 | 项目骨架 → API 层 + 类型 → Dashboard 页面 → 分析页面 → (导出已集成) |

### 1.2 关键契约

| 契约项 | 值 | 来源 |
|--------|-----|------|
| API 基础路径 | `/api/cost/*` | design.md §4.1 |
| 金额单位 | 元，小数2位，BigDecimal(15,2) | design.md §1.3 |
| 日期格式 | `YYYY-MM` 字符串 | design.md §1.3 |
| 角色枚举 | `DEV` / `TEST` / `PM` / `OPS` | design.md §1.3 |
| 成本类型 | `LABOR` / `PROJECT` / `ALL` | design.md §1.3 |
| 分页 | `page`(1-based) / `pageSize` / `total` | design.md §1.3 |
| 错误码 | `COST_{MODULE}_{SEQ}` | design.md §5.1 |
| 通用出参 | `{ result, msg, data }` | design.md §5.1 |

---

## 2. 后端编码 (testDJnew)

### 2.1 Task 1: 项目初始化与基础架构

**文件清单**:

| 操作 | 文件路径 | 说明 |
|------|----------|------|
| CREATE | `pom.xml` | Spring Boot 3.2.0 + JPA + Redis + POI + Quartz + Flyway |
| CREATE | `src/main/java/com/example/costreport/CostReportApplication.java` | 主启动类 |
| CREATE | `src/main/resources/application.yml` | PostgreSQL + Redis + Jackson 配置 |
| CREATE | `src/main/java/com/example/costreport/config/WebConfig.java` | CORS 配置，允许 localhost:3000 |

**关键实现要点**:
- `pom.xml` 依赖: `spring-boot-starter-web`, `spring-boot-starter-data-jpa`, `spring-boot-starter-data-redis`, `spring-boot-starter-validation`, `postgresql`, `poi-ooxml:5.2.5`, `spring-boot-starter-quartz`, `flyway-core` (Task 3 追加)
- `application.yml`: `ddl-auto: validate` (由 Flyway 管理 schema)
- `WebConfig`: `/api/**` 允许跨域，methods: GET/POST/PUT/DELETE/OPTIONS

**编译验证**:
```bash
cd testDJnew-main && mvn compile -q
```

---

### 2.2 Task 2: 数据模型 — JPA 实体

**文件清单**:

| 操作 | 文件路径 | 实体 |
|------|----------|------|
| CREATE | `src/main/java/com/example/costreport/model/CostType.java` | 枚举: LABOR, PROJECT |
| CREATE | `src/main/java/com/example/costreport/model/EmployeeRole.java` | 枚举: DEV, TEST, PM, OPS |
| CREATE | `src/main/java/com/example/costreport/model/Department.java` | 部门 (id, name, parentId, businessLineId, status) |
| CREATE | `src/main/java/com/example/costreport/model/Project.java` | 项目 (id, name, departmentId, businessLineId, budgetAmount, startDate, endDate, status) |
| CREATE | `src/main/java/com/example/costreport/model/BusinessLine.java` | 业务线 (id, name, description, status) |
| CREATE | `src/main/java/com/example/costreport/model/Employee.java` | 员工 (id, name, departmentId, businessLineId, role, costRate, status) |
| CREATE | `src/main/java/com/example/costreport/model/CostRecord.java` | 成本记录 (id, type, amount, departmentId, projectId, employeeId, businessLineId, role, periodYear, periodMonth, periodQuarter, description) |

**关键约束**:
- 所有实体使用 `@Entity` + `@Table` 注解，字段名 snake_case 映射
- `CostRecord` 表含 4 个索引: `idx_cost_record_type`, `idx_cost_record_dept`, `idx_cost_record_project`, `idx_cost_record_period`
- 金额字段: `@Column(precision = 15, scale = 2)`
- 枚举字段: `@Enumerated(EnumType.STRING)`
- 时间戳: `created_at` 字段 `updatable = false`, 默认 `LocalDateTime.now()`

---

### 2.3 Task 3: 数据库迁移脚本

**文件清单**:

| 操作 | 文件路径 | 说明 |
|------|----------|------|
| CREATE | `src/main/resources/db/migration/V1__init_schema.sql` | 5 张表 DDL + 6 个索引 |
| MODIFY | `pom.xml` | 追加 `flyway-core` 依赖 |

**表结构**:
- `department` — 部门表，含 `parent_id` 自关联
- `business_line` — 业务线表
- `project` — 项目表，含 `budget_amount NUMERIC(15,2)`
- `employee` — 员工表，含 `role VARCHAR(10)`, `cost_rate NUMERIC(10,2)`
- `cost_record` — 成本记录表，核心聚合查询目标

**索引**:
- `idx_cost_record_type` (type)
- `idx_cost_record_dept` (department_id)
- `idx_cost_record_project` (project_id)
- `idx_cost_record_period` (period_year, period_month)
- `idx_cost_record_business_line` (business_line_id)
- `idx_cost_record_employee` (employee_id)

---

### 2.4 Task 4: Repository 层

**文件清单**:

| 操作 | 文件路径 | 职责 |
|------|----------|------|
| CREATE | `src/main/java/com/example/costreport/repository/CostRecordRepository.java` | 核心聚合查询 (7 个 JPQL 方法) |
| CREATE | `src/main/java/com/example/costreport/repository/DepartmentRepository.java` | 部门 CRUD + 按 status/parentId 查询 |
| CREATE | `src/main/java/com/example/costreport/repository/ProjectRepository.java` | 项目 CRUD + 按 departmentId/businessLineId 查询 |
| CREATE | `src/main/java/com/example/costreport/repository/EmployeeRepository.java` | 员工 CRUD + 按 departmentId/role 查询 |
| CREATE | `src/main/java/com/example/costreport/repository/BusinessLineRepository.java` | 业务线 CRUD + 按 status 查询 |

**CostRecordRepository 聚合查询方法**:

| 方法 | JPQL 要点 | 返回 |
|------|----------|------|
| `sumTotalCost(year, month)` | SUM(amount) WHERE periodYear=:year AND (:month IS NULL OR periodMonth=:month) | BigDecimal |
| `sumByType(year, month, type)` | 同上 + type 过滤 | BigDecimal |
| `sumTotalCostPrevious(prevYear, prevMonth)` | 环比计算用 | BigDecimal |
| `departmentRanking(year, month, type)` | JOIN Department, GROUP BY dept, ORDER BY SUM DESC | List\<Object[]\> |
| `monthlyTrend(year, startMonth, prevYear, prevStartMonth)` | 跨年12个月，按 LABOR/PROJECT 分别 SUM | List\<Object[]\> |
| `overBudgetProjects(year, month)` | LEFT JOIN Project, HAVING SUM(actual) > budget | List\<Object[]\> |
| `laborComposition(year, month)` | WHERE type='LABOR', GROUP BY role | List\<Object[]\> |
| `businessLineComparison(year, month)` | JOIN BusinessLine, GROUP BY bizLine | List\<Object[]\> |

---

### 2.5 Task 5: DTO 层

**文件清单**:

| 操作 | 文件路径 | 说明 |
|------|----------|------|
| CREATE | `src/main/java/com/example/costreport/dto/DashboardSummaryResponse.java` | Dashboard 汇总出参 |
| CREATE | `src/main/java/com/example/costreport/dto/AnalysisQueryRequest.java` | 分析查询入参 |
| CREATE | `src/main/java/com/example/costreport/dto/AnalysisQueryResponse.java` | 分析查询出参 |
| CREATE | `src/main/java/com/example/costreport/dto/ExportRequest.java` | 导出请求 |
| CREATE | `src/main/java/com/example/costreport/dto/ApiResponse.java` | 通用响应包装 `{ result, msg, data }` |

**DTO 字段对齐 (与前端 types/cost.ts 对照)**:

| 后端 DTO 字段 | 前端 TS 类型 | 对齐 |
|---------------|-------------|------|
| `DashboardSummaryResponse.totalCost` (BigDecimal) | `DashboardSummary.totalCost` (number) | ✅ |
| `DashboardSummaryResponse.totalCostChange` (BigDecimal) | `DashboardSummary.totalCostChange` (number) | ✅ |
| `DepartmentRankingItem.rank` (int) | `DepartmentRankingItem.rank` (number) | ✅ |
| `MonthlyTrendItem.month` (String, "YYYY-MM") | `MonthlyTrendItem.month` (string) | ✅ |
| `LaborCompositionItem.role` (EmployeeRole enum) | `LaborCompositionItem.role` (EmployeeRole) | ✅ |
| `AnalysisQueryRequest.page` (int, 1-based) | `AnalysisQueryRequest.page` (number) | ✅ |
| `AnalysisQueryResponse.aggregations.grandTotal` | `AnalysisQueryResponse.aggregations.grandTotal` | ✅ |

---

### 2.6 Task 6: Dashboard Controller

**文件清单**:

| 操作 | 文件路径 | 说明 |
|------|----------|------|
| CREATE | `src/main/java/com/example/costreport/controller/DashboardController.java` | W01 接口 |

**接口**: `GET /api/cost/dashboard/summary?year={year}&month={month}`

**入参校验**:
- `year`: @RequestParam, 必填, 范围 2000-2099
- `month`: @RequestParam(required=false), 范围 1-12

**错误码**:
- `COST_DASH_0001`: year 参数缺失或无效
- `COST_DASH_0002`: 系统内部错误

**业务流程**:
1. Controller 校验参数 → 调用 DashboardService.getSummary(year, month)
2. Service 先查 Redis 缓存 (key=`dashboard:summary:{year}:{month}`)
3. 缓存未命中 → 依次调用 Repository 的 6 个聚合方法
4. 计算环比: `(totalCost - prevTotalCost) / prevTotalCost`
5. 计算预算执行率: `actualSum / totalBudget`
6. 写入 Redis, TTL 1h
7. 组装 `DashboardSummaryResponse` 返回

**Redis 降级**: Redis 不可用时直查数据库，日志告警。

---

### 2.7 Task 7: Analysis Controller

**文件清单**:

| 操作 | 文件路径 | 说明 |
|------|----------|------|
| CREATE | `src/main/java/com/example/costreport/controller/AnalysisController.java` | W02, W10 接口 |

**接口**:

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| W02 | POST | `/api/cost/analysis/query` | 多维筛选 + 分页排序 |
| W10 | GET | `/api/cost/analysis/drill-down` | 汇总→明细钻取 |

**W02 入参校验**:
- `page`: 必填, ≥1
- `pageSize`: 必填, 1-100
- `dimensions`: 可选, 不支持的维度静默忽略
- `filters.costType`: LABOR/PROJECT/ALL

**错误码**:
- `COST_ANALY_0100`: page 或 pageSize 参数缺失/无效
- `COST_ANALY_0101`: 查询参数组合无效
- `COST_ANALY_0102`: 查询超时 (statement_timeout=5s)

**动态查询构建**:
- 根据 `dimensions` 动态拼接 GROUP BY 子句
- 根据 `filters` 动态拼接 WHERE 条件
- 支持排序: `sortField` + `sortOrder` (asc/desc)
- 分页: LIMIT + OFFSET
- 聚合: 查询结果集外独立计算 `aggregations`

---

### 2.8 Task 8: Export Controller + Service

**文件清单**:

| 操作 | 文件路径 | 说明 |
|------|----------|------|
| CREATE | `src/main/java/com/example/costreport/controller/ExportController.java` | W03, W04 接口 |
| CREATE | `src/main/java/com/example/costreport/service/ExportService.java` | Excel/CSV 生成 |
| CREATE | `src/main/java/com/example/costreport/model/ExportTask.java` | 导出任务实体 |
| CREATE | `src/main/java/com/example/costreport/model/ExportTemplate.java` | 导出模板实体 |

**接口**:

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| W03 | POST | `/api/cost/export` | 同步导出, 返回二进制流 |
| W04 | POST | `/api/cost/export/schedule` | 创建定时导出任务 |

**W03 业务规则**:
- 单次导出 ≤ 5 万行 (超限: `COST_EXPORT_0201`)
- 导出超时 60s (`COST_EXPORT_0202`)
- 无数据时导出空 Excel (含表头)
- Content-Type: `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`

**ExportTask 状态机**:
```
PENDING → PROCESSING → COMPLETED
                    → FAILED → PENDING (重试)
```

**定时导出** (Task 13 可选):
- Quartz `@Scheduled(cron = "0 0 2 1 * ?")` — 每月1号凌晨2点
- 生成文件到 `/tmp/cost-exports/`

---

### 2.9 Master Data Controller

**文件清单**:

| 操作 | 文件路径 | 说明 |
|------|----------|------|
| CREATE | `src/main/java/com/example/costreport/controller/MasterDataController.java` | W05-W09 接口 |

**接口**:

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| W05 | GET | `/api/cost/master/departments` | 部门树形列表 |
| W06 | GET | `/api/cost/master/projects?departmentId=` | 项目列表 |
| W07 | GET | `/api/cost/master/business-lines` | 业务线列表 |
| W08 | GET | `/api/cost/master/employees?departmentId=&role=` | 人员列表 |
| W09 | POST | `/api/cost/master/projects/import` | CSV 批量导入预算 |

---

## 3. 前端编码 (testDj)

### 3.1 Task 9: 前端项目初始化

**文件清单**:

| 操作 | 文件路径 | 说明 |
|------|----------|------|
| CREATE | `package.json` | React 18 + TypeScript + Ant Design 5 + ECharts + React Query |
| CREATE | `tsconfig.json` | TypeScript 严格模式配置 |
| CREATE | `src/main.tsx` | 入口 |
| CREATE | `src/App.tsx` | 路由配置 |

**依赖清单**:
- `react`, `react-dom` (18.x)
- `antd` (5.x), `@ant-design/icons`, `@ant-design/charts`
- `@tanstack/react-query`
- `axios`
- `react-router-dom` (6.x)
- `dayjs`
- `exceljs`, `file-saver`
- `typescript`, `@types/react`, `@types/react-dom`

**路由配置**:
| 路由 | 组件 | 说明 |
|------|------|------|
| `/dashboard/cost` | `CostDashboard` | Dashboard 看板 |
| `/analysis/cost` | `CostAnalysis` | 成本分析页 |
| `/analysis/cost/:id` | `CostDetail` | 成本明细钻取页 |

---

### 3.2 Task 10: API 层 + 类型定义

**文件清单**:

| 操作 | 文件路径 | 说明 |
|------|----------|------|
| CREATE | `src/types/cost.ts` | 全部 TypeScript 类型定义 |
| CREATE | `src/api/costApi.ts` | axios 封装，3 个 API 函数 |

**类型定义 (src/types/cost.ts)**:

```typescript
// 枚举
type CostType = 'LABOR' | 'PROJECT' | 'ALL';
type EmployeeRole = 'DEV' | 'TEST' | 'PM' | 'OPS';

// 角色标签映射
const ROLE_LABELS: Record<EmployeeRole, string> = {
  DEV: '开发', TEST: '测试', PM: '产品', OPS: '运维',
};

// 接口类型 (与后端 DTO 严格对齐)
interface DashboardSummary { ... }
interface AnalysisQueryRequest { ... }
interface AnalysisQueryResponse { ... }
interface ExportRequest { ... }
// ... 共 13 个 interface
```

**API 函数 (src/api/costApi.ts)**:
- `fetchDashboardSummary(year, month?)` → GET `/api/cost/dashboard/summary`
- `queryAnalysis(request)` → POST `/api/cost/analysis/query`
- `exportReport(request)` → POST `/api/cost/export` (responseType: blob)

**axios 配置**: baseURL=`/api/cost`, timeout=30000

---

### 3.3 Task 11: Dashboard 看板页面

**文件清单**:

| 操作 | 文件路径 | 组件 | 对应需求 |
|------|----------|------|----------|
| MODIFY | `src/pages/CostDashboard.tsx` | 页面容器 | FR-DASH-01~06 |
| CREATE | `src/components/CostOverviewCards.tsx` | 总览卡片 (4 个 Statistic) | FR-DASH-01 |
| CREATE | `src/components/DepartmentRankingChart.tsx` | 部门成本排行柱状图 | FR-DASH-02 |
| CREATE | `src/components/MonthlyTrendChart.tsx` | 月度趋势折线图 | FR-DASH-03 |
| CREATE | `src/components/OverBudgetTable.tsx` | 项目超支预警表格 | FR-DASH-04 |
| CREATE | `src/components/LaborCompositionChart.tsx` | 人力成本构成饼图 | FR-DASH-05 |
| CREATE | `src/components/BusinessLineChart.tsx` | 业务线对比柱状图 | FR-DASH-06 |

**技术实现要点**:

| 组件 | 图表库 | 关键配置 |
|------|--------|----------|
| CostOverviewCards | Ant Design Statistic | 4 卡片: 总成本/环比/人力/项目，`prefix="¥"` |
| DepartmentRankingChart | @ant-design/charts Column | xField=name, yField=cost, label position=top |
| MonthlyTrendChart | @ant-design/charts Line | seriesField=type, smooth=true, y轴格式化万 |
| OverBudgetTable | Ant Design Table | 超支金额红色高亮, 超支比例 Tag error |
| LaborCompositionChart | @ant-design/charts Pie | angleField=value, colorField=type, radius=0.8 |
| BusinessLineChart | @ant-design/charts Column | color=#5B8FF9 |

**状态管理**: React Query `useQuery` 管理 Dashboard 数据，key=`['dashboardSummary', year, month]`

**时间筛选**: DatePicker (picker="month")，默认当前月，支持切换年月

**响应式布局**: `Row` + `Col` 栅格系统，xs=24 / lg=12 自适应

---

### 3.4 Task 12: 成本分析页面

**文件清单**:

| 操作 | 文件路径 | 组件 | 对应需求 |
|------|----------|------|----------|
| MODIFY | `src/pages/CostAnalysis.tsx` | 页面容器 | FR-ANALY-01~06 |
| CREATE | `src/components/MultiDimensionFilter.tsx` | 多维筛选器 | FR-ANALY-01 |
| CREATE | `src/components/CostDetailTable.tsx` | 成本明细表 | FR-ANALY-02/03 |

**MultiDimensionFilter 筛选器**:
- 部门 (Select multiple, 待从 API 加载)
- 项目 (Select multiple)
- 业务线 (Select multiple)
- 成本类型 (Select: ALL/LABOR/PROJECT)
- 月份 (DatePicker picker="month")
- 年份 (DatePicker picker="year")
- 查询/重置按钮

**CostDetailTable 明细表**:
- 列: 部门 | 项目 | 业务线 | 月份 | 开发成本 | 测试成本 | 产品成本 | 运维成本 | 项目预算 | 实际消耗 | 预算占比 | 总成本
- 预算占比: Tag 组件，ratio>1 红色 error，否则绿色 success
- 分页: `pageSize` 可切换, `showTotal`
- 横向滚动: `scroll={{ x: 1400 }}`

**导出功能**:
- 按钮: `导出 Excel` (DownloadOutlined icon)
- 调用 `exportReport({ filters, format: 'EXCEL' })` → 返回 Blob → `file-saver` 保存
- 文件名: `成本报表_YYYY-MM-DD.xlsx`

**时间维度切换** (FR-ANALY-05):
- 通过 DatePicker picker 切换 month/year/quarter
- 切换后自动重新查询

---

## 4. 跨仓对齐检查

### 4.1 API 契约对齐

| 检查项 | testDj (前端) | testDJnew (后端) | 状态 |
|--------|--------------|-------------------|------|
| API 基础路径 | `axios.baseURL = '/api/cost'` | `@RequestMapping("/api/cost")` | ✅ |
| Dashboard 接口 | `GET /dashboard/summary?year=&month=` | `@GetMapping("/dashboard/summary")` | ✅ |
| Analysis 接口 | `POST /analysis/query` | `@PostMapping("/analysis/query")` | ✅ |
| Export 接口 | `POST /export` (responseType:blob) | `@PostMapping("/export")` → byte[] | ✅ |
| 通用响应结构 | `axios` 自动解包 `.data` | `ApiResponse { result, msg, data }` | ✅ |

### 4.2 数据类型对齐

| 类型 | 后端 Java | 前端 TypeScript | 状态 |
|------|-----------|-----------------|------|
| 金额 | `BigDecimal` (precision=15,scale=2) | `number` (`.toFixed(2)` 展示) | ✅ |
| 日期 | `String` ("YYYY-MM") | `string` (dayjs 格式化) | ✅ |
| 角色枚举 | `EmployeeRole` enum: DEV/TEST/PM/OPS | `type EmployeeRole = 'DEV'\|'TEST'\|'PM'\|'OPS'` | ✅ |
| 成本类型 | `CostType` enum: LABOR/PROJECT | `type CostType = 'LABOR'\|'PROJECT'\|'ALL'` | ✅ |
| 分页 | `int page` (1-based), `int pageSize`, `long total` | `page: number`, `pageSize: number`, `total: number` | ✅ |
| 导出格式 | `String format`: "EXCEL"/"CSV" | `format: 'EXCEL' \| 'CSV'` | ✅ |

### 4.3 错误码对齐

| 场景 | 后端错误码 | 前端处理 | 状态 |
|------|-----------|----------|------|
| 参数无效 | `COST_DASH_0001`, `COST_ANALY_0100` | `error.message` 展示 | ✅ |
| 系统错误 | `COST_DASH_0002` | 重试或提示 | ✅ |
| 导出超限 | `COST_EXPORT_0201` | 提示缩小范围 | ✅ |
| 导出超时 | `COST_EXPORT_0202` | 提示使用定时导出 | ✅ |

### 4.4 路由对齐

| 路由 | 前端实现 | 后端无路由概念 | 状态 |
|------|----------|---------------|------|
| `/dashboard/cost` | `CostDashboard` 组件 | N/A | ✅ |
| `/analysis/cost` | `CostAnalysis` 组件 | N/A | ✅ |
| `/analysis/cost/:id` | `CostDetail` 钻取组件 | N/A | ✅ |

---

## 5. 文件完整清单

### 5.1 testDJnew (后端) — 共 28 个文件

```
testDJnew/
├── pom.xml
├── src/main/java/com/example/costreport/
│   ├── CostReportApplication.java
│   ├── config/
│   │   └── WebConfig.java
│   ├── model/
│   │   ├── CostType.java
│   │   ├── EmployeeRole.java
│   │   ├── Department.java
│   │   ├── Project.java
│   │   ├── BusinessLine.java
│   │   ├── Employee.java
│   │   ├── CostRecord.java
│   │   ├── ExportTask.java
│   │   └── ExportTemplate.java
│   ├── repository/
│   │   ├── CostRecordRepository.java
│   │   ├── DepartmentRepository.java
│   │   ├── ProjectRepository.java
│   │   ├── EmployeeRepository.java
│   │   └── BusinessLineRepository.java
│   ├── dto/
│   │   ├── ApiResponse.java
│   │   ├── DashboardSummaryResponse.java
│   │   ├── AnalysisQueryRequest.java
│   │   ├── AnalysisQueryResponse.java
│   │   └── ExportRequest.java
│   ├── service/
│   │   ├── DashboardService.java
│   │   ├── AnalysisService.java
│   │   └── ExportService.java
│   ├── controller/
│   │   ├── DashboardController.java
│   │   ├── AnalysisController.java
│   │   ├── ExportController.java
│   │   └── MasterDataController.java
│   └── job/
│       └── ScheduledExportJob.java
└── src/main/resources/
    ├── application.yml
    └── db/migration/
        └── V1__init_schema.sql
```

### 5.2 testDj (前端) — 共 14 个文件

```
testDj/
├── package.json
├── tsconfig.json
├── src/
│   ├── main.tsx
│   ├── App.tsx
│   ├── types/
│   │   └── cost.ts
│   ├── api/
│   │   └── costApi.ts
│   ├── pages/
│   │   ├── CostDashboard.tsx
│   │   ├── CostAnalysis.tsx
│   │   └── CostDetail.tsx
│   └── components/
│       ├── CostOverviewCards.tsx
│       ├── DepartmentRankingChart.tsx
│       ├── MonthlyTrendChart.tsx
│       ├── OverBudgetTable.tsx
│       ├── LaborCompositionChart.tsx
│       ├── BusinessLineChart.tsx
│       ├── MultiDimensionFilter.tsx
│       └── CostDetailTable.tsx
```

---

## 6. 实施顺序与依赖

```
testDJnew 后端链 (串行):
Task 1 (骨架) → Task 2 (实体) → Task 3 (迁移) → Task 4 (Repo)
    → Task 5 (DTO) → Task 6 (Dashboard Ctrl+Svc)
    → Task 7 (Analysis Ctrl+Svc) → Task 8 (Export Ctrl+Svc)

testDj 前端链 (串行):
Task 9 (骨架) → Task 10 (API+类型) → Task 11 (Dashboard 页)
    → Task 12 (分析页)

后端链和前端链可并行推进。
Task 10 与 Task 5 需对齐 DTO/类型定义。
```

---

## 7. 编译验证方案

### 7.1 后端验证

```bash
# 1. 编译
cd testDJnew-main && mvn compile -q

# 2. 单元测试 (H2 内存数据库)
mvn test -q

# 3. 启动验证 (需 PostgreSQL + Redis)
mvn spring-boot:run
# 验证: curl http://localhost:8080/api/cost/dashboard/summary?year=2025
```

### 7.2 前端验证

```bash
# 1. 类型检查
cd testDj-main && npx tsc --noEmit

# 2. 构建
npm run build

# 3. 开发服务器
npm run dev
# 访问: http://localhost:3000/dashboard/cost
```

---

## 8. 需求覆盖追踪

| 需求 ID | 功能 | 后端任务 | 前端任务 | 覆盖 |
|---------|------|----------|----------|------|
| FR-DASH-01 | 成本总览卡片 | Task 6 | Task 11 (CostOverviewCards) | ✅ |
| FR-DASH-02 | 部门成本排行 | Task 6 | Task 11 (DepartmentRankingChart) | ✅ |
| FR-DASH-03 | 月度趋势图 | Task 6 | Task 11 (MonthlyTrendChart) | ✅ |
| FR-DASH-04 | 项目超支预警 | Task 6 | Task 11 (OverBudgetTable) | ✅ |
| FR-DASH-05 | 人力成本构成 | Task 6 | Task 11 (LaborCompositionChart) | ✅ |
| FR-DASH-06 | 业务线对比 | Task 6 | Task 11 (BusinessLineChart) | ✅ |
| FR-ANALY-01 | 多维筛选器 | Task 7 | Task 12 (MultiDimensionFilter) | ✅ |
| FR-ANALY-02 | 人力成本明细表 | Task 7 | Task 12 (CostDetailTable) | ✅ |
| FR-ANALY-03 | 项目成本明细表 | Task 7 | Task 12 (CostDetailTable) | ✅ |
| FR-ANALY-04 | 交叉分析 | Task 7 (dimensions) | Task 12 | ✅ |
| FR-ANALY-05 | 时间维度切换 | Task 7 | Task 12 (DatePicker) | ✅ |
| FR-ANALY-06 | 数据钻取 | Task 7 (W10) | Task 12 | ✅ |
| FR-EXPORT-01 | 导出当前视图 | Task 8 | Task 12 (handleExport) | ✅ |
| FR-EXPORT-02 | 导出图表 | - | 本期 P2，暂不实现 | ⚠️ |
| FR-EXPORT-03 | 定时导出 | Task 13 | - | ✅ |
| FR-EXPORT-04 | 导出模板 | Task 8 (ExportTemplate) | - | ✅ |

> **注**: FR-EXPORT-02 (图表导出 PNG/PDF) 优先级 P2，本期暂不实现，后续版本补充。

---

## 9. 风险与缓解

| 风险 | 影响 | 缓解措施 |
|------|------|----------|
| 后端依赖未就绪 (PostgreSQL/Redis) | 无法启动验证 | H2 内存库做单元测试；Mock 模式开发 |
| 前端依赖安装慢/失败 | 阻塞前端开发 | 使用 yarn/pnpm 加速；锁定版本号 |
| 跨库接口不一致 | 联调失败 | 本文档 §4 作为契约基线；Mock Server 先行 |
| 大数据量查询慢 | 用户体验差 | 索引优化 + Redis 缓存 + 分页限制 |
| 定时导出邮件服务不可用 | 导出推送失败 | 降级方案：仅生成文件，邮件后续补发 |

---

## 10. 自审查清单

- [x] 所有 13 个任务均有对应文件清单和实现要点
- [x] 跨仓 API 契约对齐 (路径/类型/枚举/分页/金额)
- [x] 前后端 DTO/Type 类型严格对照
- [x] 需求 ID 到任务的可追溯矩阵
- [x] 错误码定义完整 (Dashboard/Analysis/Export/MasterData)
- [x] 状态机设计 (ExportTask, Project)
- [x] 降级方案 (Redis 不可用、数据库超时、导出超限)
- [x] 编译验证命令明确
- [x] 无 TBD/TODO 占位符 (仅 MultiDimensionFilter 中标注了外部数据依赖)
- [x] 文件完整清单覆盖两个仓库