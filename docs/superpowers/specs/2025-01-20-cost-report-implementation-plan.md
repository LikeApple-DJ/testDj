# 成本统计报表系统 — 实现规划

> 创建日期: 2025-01-20  
> 基于: 2025-01-20-cost-report-design.md  
> 状态: 已规划

---

## 1. 任务总览

共 **6 个里程碑**，**18 个开发任务**，预估总工时 **10 个工作日**。

```
M1 后端基础框架 ──→ M2 前端基础框架 ──→ M3 成本录入
       │                  │                  │
       ▼                  ▼                  ▼
M4 统计聚合+Dashboard ──→ M5 成本分析+导出 ──→ M6 联调测试
```

---

## 2. 里程碑与任务拆解

### M1：后端基础框架 + 数据库（预估 2 天）

| # | 任务 | 仓库 | 产出 | 依赖 |
|---|------|------|------|------|
| 1.1 | Spring Boot 项目初始化 | testDJnew | pom.xml, 主启动类, application.yml | 无 |
| 1.2 | 数据库建表脚本 | testDJnew | db/schema.sql | 无 |
| 1.3 | 公共模块（Result/PageResult/异常处理） | testDJnew | common/*.java | 1.1 |
| 1.4 | 部门 CRUD（Entity/Mapper/Service/Controller） | testDJnew | 部门全链路 | 1.1, 1.2 |
| 1.5 | 业务线 CRUD | testDJnew | 业务线全链路 | 1.1, 1.2 |
| 1.6 | 人员 CRUD（关联部门） | testDJnew | 人员全链路 | 1.4 |
| 1.7 | 项目 CRUD（关联部门+业务线） | testDJnew | 项目全链路 | 1.4, 1.5 |

**验收标准：**
- 项目可启动，Swagger 文档可访问
- 所有 CRUD 接口可通过 Postman 测试
- 数据库表结构与设计文档一致

---

### M2：前端基础框架 + 布局 + 基础数据管理（预估 2 天）

| # | 任务 | 仓库 | 产出 | 依赖 |
|---|------|------|------|------|
| 2.1 | Vue3 + Vite 项目初始化 | testDj | package.json, vite.config.js | 无 |
| 2.2 | 主布局（侧边栏导航 + 顶栏） | testDj | MainLayout.vue | 2.1 |
| 2.3 | 路由配置 | testDj | router/index.js | 2.1 |
| 2.4 | Axios 封装 + 请求拦截 | testDj | utils/request.js | 2.1 |
| 2.5 | 部门管理页面（树形表格 + CRUD 弹窗） | testDj | DepartmentView.vue | 2.2, 2.3 |
| 2.6 | 业务线管理页面 | testDj | BusinessLineView.vue | 2.2, 2.3 |
| 2.7 | 人员管理页面 | testDj | EmployeeView.vue | 2.5 |
| 2.8 | 项目管理页面 | testDj | ProjectView.vue | 2.5, 2.6 |

**验收标准：**
- 前端可启动，侧边栏导航正常
- 所有基础数据 CRUD 页面可正常增删改查
- 与后端接口联调通过

---

### M3：成本数据录入（预估 1 天）

| # | 任务 | 仓库 | 产出 | 依赖 |
|---|------|------|------|------|
| 3.1 | 人力成本录入接口（支持批量） | testDJnew | CostEntryController/Service | M1 |
| 3.2 | 项目成本录入接口（支持批量） | testDJnew | CostEntryController/Service | M1 |
| 3.3 | 成本录入页面（前端） | testDj | CostEntryView.vue | M2 |

**验收标准：**
- 可按月批量录入人力成本和项目成本
- 录入数据可查询验证
- 前端录入页面交互流畅

---

### M4：统计聚合 + Dashboard 看板（预估 2 天）

| # | 任务 | 仓库 | 产出 | 依赖 |
|---|------|------|------|------|
| 4.1 | Dashboard 汇总接口 | testDJnew | DashboardService/Controller | M3 |
| 4.2 | 成本趋势接口（月/季/年） | testDJnew | DashboardService | M3 |
| 4.3 | 部门成本占比接口 | testDJnew | DashboardService | M3 |
| 4.4 | 项目预算对比接口 | testDJnew | DashboardService | M3 |
| 4.5 | 人力成本分布接口 | testDJnew | DashboardService | M3 |
| 4.6 | 超支预警接口 | testDJnew | DashboardService | M3 |
| 4.7 | Dashboard 前端页面 | testDj | DashboardView.vue + 图表组件 | M2, 4.1~4.6 |

**验收标准：**
- Dashboard 所有图表正确渲染
- 时间范围切换正常
- 超支项目红色高亮
- 数据与录入数据一致

---

### M5：成本统计分析 + 报表导出（预估 2 天）

| # | 任务 | 仓库 | 产出 | 依赖 |
|---|------|------|------|------|
| 5.1 | 人力成本统计接口（多维度筛选+分页） | testDJnew | CostStatsService/Controller | M3 |
| 5.2 | 项目成本统计接口（多维度筛选+分页） | testDJnew | CostStatsService/Controller | M3 |
| 5.3 | Excel 导出服务 | testDJnew | ExportService (EasyExcel) | 5.1, 5.2 |
| 5.4 | 成本统计分析前端页面 | testDj | CostAnalysisView.vue | M2, 5.1, 5.2 |
| 5.5 | 导出功能前端集成 | testDj | CostAnalysisView.vue | 5.3, 5.4 |

**验收标准：**
- 筛选条件（部门/项目/业务线/人员/时间）正常工作
- 人力成本 Tab 和项目成本 Tab 数据正确
- 超支行红色高亮
- Excel 导出文件可正常打开，数据正确

---

### M6：联调测试 + Bug 修复（预估 1 天）

| # | 任务 | 仓库 | 产出 | 依赖 |
|---|------|------|------|------|
| 6.1 | 前后端全链路联调 | testDj + testDJnew | 联调报告 | M1~M5 |
| 6.2 | 边界场景测试 | 两仓库 | 测试用例 | 6.1 |
| 6.3 | Bug 修复与优化 | 两仓库 | 修复提交 | 6.1, 6.2 |

**验收标准：**
- 所有页面功能正常
- 无 P0/P1 级别 Bug
- 性能满足非功能性要求

---

## 3. 仓间对齐点

| 对齐点 | 前端 (testDj) | 后端 (testDJnew) | 契约 |
|--------|--------------|-----------------|------|
| API 基础路径 | `VITE_API_BASE_URL` | CORS 配置 | `/api/*` |
| 统一响应格式 | 解析 `Result<T>` | 返回 `Result<T>` | `{ code, message, data }` |
| 分页参数 | `page`, `size` | MyBatis-Plus Page | `{ records, total, page, size }` |
| 时间格式 | `YYYY-MM` / `YYYY-MM-DD` | `@JsonFormat` | ISO 8601 |
| 枚举值 | role: DEVELOPER/TESTER/PRODUCT/OPS | Java Enum | 字符串传输 |
| 文件下载 | Blob + download | `Content-Disposition` | Excel MIME |

---

## 4. 风险与应对

| 风险 | 影响 | 应对措施 |
|------|------|---------|
| 统计聚合查询性能 | Dashboard 加载慢 | 预计算汇总表 + 索引优化 |
| 大数据量导出 | OOM / 超时 | EasyExcel 流式写入 |
| 跨域问题 | 前端无法调用后端 | CorsConfig 统一配置 |
| 树形部门递归查询 | 查询效率低 | 限制层级深度 + 缓存 |

---

## 5. 推荐执行顺序

```
Week 1:
  Day 1-2: M1 (后端基础) + M2 (前端基础) [可并行]
  Day 3:   M3 (成本录入)
  Day 4-5: M4 (Dashboard)

Week 2:
  Day 1-2: M5 (统计分析+导出)
  Day 3:   M6 (联调测试)
```
