# Code Review Report

> **Change** 成本统计报表系统 · **日期** 2026-08-18 · **审查者** AI (DTCoder)
>
> **审查范围**：[testDj] 后端 Java 代码 (20 个 .java 文件) + [testDJnew] 前端 TypeScript/TSX 代码 (5 个 .ts/.tsx 文件)
>
> **等级**：P0 阻塞 / P1 推荐 / P2 参考

---

## §1 审查概述

本次审查覆盖成本统计报表系统的完整实现——后端 Spring Boot 3.x + MyBatis-Plus（testDj）和前端 React 18 + Ant Design 5（testDJnew）。系统包含 5 个 REST API 接口、维度筛选、人力/项目成本统计、Dashboard 汇总和 Excel 导出功能。

**自动化预扫**：运行 `scan-all-rules.sh` 覆盖 52/222 条规则，命中 9 项（1 P0, 1 P1, 7 P2）。

**审查结论**：发现 **6 个 P0 阻塞问题**、**3 个 P1 推荐修复项**、**7 个 P2 参考改进项**。核心问题集中在：维度筛选条件未在 Service 层传递到数据库查询、项目成本 Item 缺少关键字段、Dashboard 趋势数据仅支持年度周期、导出未应用筛选条件。

---

## §2 审查范围

### 2.1 后端 Java 文件清单 (testDj)

| # | 文件 | 状态 |
|---|------|------|
| 1 | `CostApplication.java` | ✅ 已审 |
| 2 | `common/Result.java` | ✅ 已审 |
| 3 | `common/DimensionVO.java` | ✅ 已审 |
| 4 | `config/CorsConfig.java` | ⚠️ 已审有问题 |
| 5 | `config/GlobalExceptionHandler.java` | ⚠️ 已审有问题 |
| 6 | `controller/DashboardController.java` | ✅ 已审 |
| 7 | `controller/DimensionController.java` | ✅ 已审 |
| 8 | `controller/ExportController.java` | ✅ 已审 |
| 9 | `controller/LaborCostController.java` | ✅ 已审 |
| 10 | `controller/ProjectCostController.java` | ✅ 已审 |
| 11 | `dto/DashboardVO.java` | ✅ 已审 |
| 12 | `dto/LaborCostQueryDTO.java` | ✅ 已审 |
| 13 | `dto/LaborCostVO.java` | ✅ 已审 |
| 14 | `dto/ProjectCostQueryDTO.java` | ✅ 已审 |
| 15 | `dto/ProjectCostVO.java` | ✅ 已审 |
| 16 | `entity/*.java` (6 个实体) | ✅ 已审 |
| 17 | `mapper/*.java` (6 个 Mapper) | ✅ 已审 |
| 18 | `service/impl/DashboardServiceImpl.java` | ⚠️ 已审有问题 |
| 19 | `service/impl/DimensionServiceImpl.java` | ✅ 已审 |
| 20 | `service/impl/ExportServiceImpl.java` | ⚠️ 已审有问题 |
| 21 | `service/impl/LaborCostServiceImpl.java` | ⚠️ 已审有问题 |
| 22 | `service/impl/ProjectCostServiceImpl.java` | ⚠️ 已审有问题 |

### 2.2 前端 TypeScript 文件清单 (testDJnew)

| # | 文件 | 状态 |
|---|------|------|
| 1 | `src/types/cost.ts` | ✅ 已审 |
| 2 | `src/api/cost.ts` | ✅ 已审 |
| 3 | `src/api/request.ts` | ✅ 已审 |
| 4 | `src/pages/Dashboard.tsx` | ✅ 已审 |
| 5 | `src/pages/CostReport.tsx` | ✅ 已审 |

---

## §3 功能性检查 (Step 2)

对照 spec 功能点 F01–F12 和设计文档 design.md 逐项核验：

| REQ ID | 功能点 | 关联文件 | 结论 | 证据 |
|--------|--------|----------|------|------|
| F01 | 维度查询接口 | `DimensionController.java`, `DimensionServiceImpl.java` | ✅ 满足 | GET /api/cost/dimensions 返回 departments/projects/businessLines/personnel |
| F02 | 人力成本统计（按角色） | `LaborCostController.java`, `LaborCostServiceImpl.java` | ❌ P0 — 维度筛选未生效 | DTO 定义 department/project/businessLine/personnelId 字段，但 Service 仅应用 periodType/periodValue/role 过滤（`LaborCostServiceImpl.java:26-31`） |
| F03 | 项目成本统计（预算/实际/占比/超支） | `ProjectCostController.java`, `ProjectCostServiceImpl.java` | ❌ P0 — 维度筛选未生效 + Item 字段缺失 | DTO 定义 department/project/businessLine 字段，Service 仅应用 periodType/periodValue（`ProjectCostServiceImpl.java:26-28`）；Item 未设置 department/businessLine（`ProjectCostServiceImpl.java:38-46`）；projectName 为硬编码 `"Project-"+id` 而非真实名称 |
| F04 | Dashboard 汇总概览 | `DashboardController.java`, `DashboardServiceImpl.java` | ❌ P0 — 趋势数据仅 year 周期 | spec 要求 month→12月/quarter→4季度/year→3年趋势，代码仅 `"year".equals(periodType)` 时计算趋势（`DashboardServiceImpl.java:66`） |
| F05 | 按时间维度筛选（月/季/年） | 多个 ServiceImpl | ✅ 满足 | `applyPeriodFilter` 方法正确按 month/quarter/year 过滤 |
| F06 | 按部门维度筛选 | `LaborCostServiceImpl.java`, `ProjectCostServiceImpl.java` | ❌ P0 — 未生效 | 见 F02/F03 |
| F07 | 按项目维度筛选 | 同上 | ❌ P0 — 未生效 | 见 F02/F03 |
| F08 | 按业务线维度筛选 | 同上 | ❌ P0 — 未生效 | 见 F02/F03 |
| F09 | 按人员维度筛选 | `LaborCostServiceImpl.java` | ❌ P0 — 未生效 | 见 F02 |
| F10 | 报表导出（Excel） | `ExportController.java`, `ExportServiceImpl.java` | ❌ P0 — 导出未应用筛选 | `export()` 接收筛选参数但 `createLaborSheet/createProjectSheet` 仅按时间过滤（`ExportServiceImpl.java:55-105`） |
| F11 | 成本趋势分析 | `DashboardServiceImpl.java` | ❌ P0 — 仅 year 周期 | 见 F04 |
| F12 | 超支项目 Top N | `DashboardServiceImpl.java` | ⚠️ P1 — 使用 projectId 硬编码名称 | `"Project-"+e.getKey()`（`DashboardServiceImpl.java:97`），非真实项目名 |

### 跨仓接口契约对齐

| 契约 | 后端 (testDj) | 前端 (testDJnew) | 对齐 |
|------|--------------|-------------------|------|
| GET /api/cost/dimensions | `DimensionController` | `getDimensions()` → `request.get('/cost/dimensions')` | ✅ |
| POST /api/cost/labor-stats | `LaborCostController` | `queryLaborStats()` → `request.post('/cost/labor-stats', params)` | ✅ |
| POST /api/cost/project-stats | `ProjectCostController` | `queryProjectStats()` → `request.post('/cost/project-stats', params)` | ✅ |
| GET /api/cost/dashboard | `DashboardController` | `getDashboard()` → `request.get('/cost/dashboard', { params })` | ✅ |
| POST /api/cost/export | `ExportController` | `exportReport()` → `axios.post('/api/cost/export', ...)` | ✅ |
| 统一响应格式 `{code,data,message}` | `Result<T>` (code: int) | `ApiResult<T>` (code: number) | ✅ |
| 类型映射 BigDecimal→number | Jackson 序列化 | TypeScript number | ✅ |

---

## §4 可读性检查 (Step 3)

| ID | 检查项 | 命中文件 | 等级 | 说明 |
|----|--------|----------|------|------|
| A1.3 | Tab字符 | 无 | ✅ | 未发现 |
| A2.2 | 通配符导入 | 7 个文件 | P2 | `DashboardController.java:7`, `ExportController.java:6`, `LaborCostController.java:8`, `ProjectCostController.java:8`, `DashboardServiceImpl.java:15`, `ExportServiceImpl.java:11`, `LaborCostServiceImpl.java:15` — 使用 `import org.springframework.web.bind.annotation.*` 和 `import java.util.*` |
| A3.4 | 行宽超限 | 无 | ✅ | 未发现 |
| A3.7 | 关键字空格 | 无 | ✅ | 未发现 |
| A4.1 | 包名大写 | 无 | ✅ | 未发现 |
| A5.4 | finalize 覆盖 | 无 | ✅ | 未发现 |
| A6.3 | 修饰符顺序 | 无 | ✅ | 未发现 |
| A6.5 | 小写 long 字面量 | 无 | ✅ | 未发现 |

**LLM 补充审查：**

| 项目 | 文件 | 等级 | 说明 |
|------|------|------|------|
| 代码注释 | `LaborCostServiceImpl.java`, `ProjectCostServiceImpl.java` | P2 | 维度筛选逻辑缺少注释说明为何 DTO 字段未被使用 |
| 魔法字符串 | `DashboardServiceImpl.java:97` | P2 | `"Project-"` 硬编码前缀，建议使用常量或从 Project 表查询 |
| 方法命名 | `ExportServiceImpl.java:108-122` | P2 | `applyLaborPeriod` / `applyProjectPeriod` 命名清晰，但缺少对无效 periodType 的 default 分支处理 |

---

## §5 可靠性/安全/缺陷检查 (Step 4)

### 5.1 脚本扫描结果 (scan-all-rules.sh)

| 等级 | 规则 ID | 名称 | 位置 | 说明 |
|------|---------|------|------|------|
| **P0** | G16.2 | CatchWithoutLogging | `ExportServiceImpl.java:50` | `catch (Exception e) { throw new RuntimeException("导出失败", e); }` — 未记录原始异常日志 |
| **P1** | S10.2 | CorsWildcard | `CorsConfig.java:15` | `addAllowedOriginPattern("*")` + `setAllowCredentials(true)` — CORS 规范不允许凭证与通配符同时使用 |
| P2 | A2.2 | WildcardImport | 7 个文件 | 通配符导入（见 §4） |

### 5.2 LLM 补充审查

#### 可靠性 (G)

| ID | 规则 | 命中 | 位置 | 等级 | 说明 |
|----|------|------|------|------|------|
| G1 | 并发控制 | N/A | - | - | 所有接口为纯查询，无并发写入风险 |
| G2 | 资源释放 | ✅ | `ExportServiceImpl.java:35-49` | P1 | `Workbook` 使用 try-with-resources 正确关闭，`OutputStream` 也正确关闭。但 `response.getOutputStream()` 中的异常可能导致 Workbook 未正确 dispose |
| G3 | 事务边界 | N/A | - | - | 纯查询接口，无事务需求 |
| G4 | 边界条件 | ✅ | 多处 | P1 | 多处 `divide` 前检查 `compareTo(BigDecimal.ZERO) == 0`，防御性良好。但 `applyPeriodFilter` 对无效 periodType 静默忽略（无 default 分支），可能导致查询返回全量数据 |
| G5 | 超时/重试 | ❌ | 无 | P2 | 未配置数据库连接超时、查询超时或 HTTP 请求超时 |

#### 安全 (S)

| ID | 规则 | 命中 | 位置 | 等级 | 说明 |
|----|------|------|------|------|------|
| S1 | SQL 注入 | N/A | - | - | 使用 MyBatis-Plus LambdaQueryWrapper，无 SQL 拼接风险 |
| S4 | 命令执行 | N/A | - | - | 未使用 Runtime.exec 或 ProcessBuilder |
| S6 | 反序列化 | N/A | - | - | 未使用 ObjectInputStream |
| S9.1 | 硬编码凭证 | ⚠️ | `application.yml:7-8` | P2 | 数据库密码 `root` 硬编码在配置文件中，建议使用环境变量或配置中心 |
| S9.3 | 弱加密 | N/A | - | - | 未使用弱加密算法 |
| S10.2 | CORS 通配符 | ✅ | `CorsConfig.java:15` | P1 | 见脚本扫描 |
| S10 | 异常信息泄露 | ✅ | `GlobalExceptionHandler.java:12` | P1 | `e.getMessage()` 直接返回给客户端，可能泄露内部错误详情 |

#### Bug 模式 (B/M/I)

| ID | 规则 | 命中 | 位置 | 等级 | 说明 |
|----|------|------|------|------|------|
| B005–B076 | 25 条 Blocker 规则 | 无 | - | ✅ | 脚本扫描未命中 |
| M003–M027 | 6 条 Major 规则 | 无 | - | ✅ | 脚本扫描未命中 |
| I001–I004 | 2 条 Info 规则 | 无 | - | ✅ | 脚本扫描未命中 |

**LLM 补充 Bug 检视：**

| 规则 | 位置 | 等级 | 说明 |
|------|------|------|------|
| 空指针风险 | `ExportServiceImpl.java:32-33` | P1 | `params.get("periodType")` / `params.get("periodValue")` 可能返回 null，传入 `applyLaborPeriod` 后 switch 匹配不到任何分支，静默返回全量数据 |
| 除零保护 | `LaborCostServiceImpl.java:63` | ✅ | 已检查 `headcount == 0` |
| 精度丢失 | `ExportServiceImpl.java:72,95,100-101` | P2 | `BigDecimal.doubleValue()` 转换为 double 可能丢失精度，Excel 中金额建议使用 `setCellValue(BigDecimal)` 或字符串格式 |

---

## §6 自定义扩展检查 (Step 5)

**N/A（未启用自定义规则）** — `references/customized-checklist.md` 为空/示例项，无团队特定规则。

---

## §7 跨仓对齐点检查

| 检查项 | 后端 (testDj) | 前端 (testDJnew) | 结论 |
|--------|--------------|-------------------|------|
| 维度接口路径 | `GET /api/cost/dimensions` | `request.get('/cost/dimensions')` → `/api/cost/dimensions` | ✅ |
| 人力成本接口路径 | `POST /api/cost/labor-stats` | `request.post('/cost/labor-stats', ...)` | ✅ |
| 项目成本接口路径 | `POST /api/cost/project-stats` | `request.post('/cost/project-stats', ...)` | ✅ |
| Dashboard 接口路径 | `GET /api/cost/dashboard` | `request.get('/cost/dashboard', ...)` | ✅ |
| 导出接口路径 | `POST /api/cost/export` | `axios.post('/api/cost/export', ...)` | ✅ |
| 响应格式 | `Result<T> { code: int, data: T, message: string }` | `ApiResult<T> { code: number, data: T, message: string }` | ✅ |
| 人力成本 VO 字段 | `LaborCostVO.summary.totalLaborCost` (BigDecimal) | `LaborCostSummary.totalLaborCost` (number) | ✅ |
| 项目成本 Item 字段 | `Item.department, Item.businessLine` 存在但未设值 | `ProjectCostItem.department, ProjectCostItem.businessLine` 定义但前端表格未展示 | ⚠️ 对齐但均为空 |
| 导出 Content-Type | `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet` | `responseType: 'blob'` | ✅ |
| CORS 配置 | `addAllowedOriginPattern("*")` | Vite proxy `/api` → `localhost:8080` | ⚠️ CORS 配置有安全风险 |

---

## §8 修复任务列表

### P0 阻塞（必须修复才能合并）

- [ ] **P0-1** `LaborCostServiceImpl.java:26-31` — 应用 DTO 中 department/project/businessLine/personnelId 筛选条件到 LambdaQueryWrapper。需 JOIN 查询 Department/Project/BusinessLine 表获取名称→ID 映射，或改为子查询
- [ ] **P0-2** `ProjectCostServiceImpl.java:26-28` — 应用 DTO 中 department/project/businessLine 筛选条件
- [ ] **P0-3** `ProjectCostServiceImpl.java:38-46` — 设置 Item 的 department 和 businessLine 字段；从 Project 表 JOIN 查询真实项目名称替代 `"Project-"+id` 硬编码
- [ ] **P0-4** `DashboardServiceImpl.java:66-83` — 实现 month/quarter 周期趋势数据：month→最近12个月、quarter→最近4季度、year→最近3年
- [ ] **P0-5** `ExportServiceImpl.java:55-105` — 在 createLaborSheet/createProjectSheet 中应用 department/project/businessLine/role 等筛选条件
- [ ] **P0-6** `ExportServiceImpl.java:50` — 在 catch 块中添加日志记录：`log.error("导出失败", e)` 后再抛出

### P1 推荐（合并前应修复）

- [ ] **P1-1** `CorsConfig.java:15` — 将 `addAllowedOriginPattern("*")` 替换为具体允许的域名列表，或在使用 `allowCredentials(true)` 时移除通配符
- [ ] **P1-2** `GlobalExceptionHandler.java:12` — 返回通用错误消息（如"系统繁忙，请稍后重试"），将原始异常消息记录到日志
- [ ] **P1-3** `ExportServiceImpl.java:30` — 将 `Map<String, Object>` 参数替换为类型化 DTO（如 `ExportQueryDTO`），增强编译时类型安全

### P2 参考（可选改进）

- [ ] **P2-1** 7 个文件 — 将通配符导入替换为显式导入（`import org.springframework.web.bind.annotation.*` → 具体注解类）
- [ ] **P2-2** `DashboardServiceImpl.java:97` — 将 `"Project-"` 前缀提取为常量或从 Project 表查询
- [ ] **P2-3** `application.yml:7-8` — 数据库密码使用环境变量 `${DB_PASSWORD}` 替代硬编码
- [ ] **P2-4** `ExportServiceImpl.java:72,95,100-101` — Excel 金额单元格使用 `setCellValue(BigDecimal)` 或格式化字符串，避免 double 精度丢失
- [ ] **P2-5** 所有 ServiceImpl — 对无效 periodType 添加 default 分支处理（返回空或抛参数异常）

---

## §9 审查指标

| 指标 | 数值 |
|------|------|
| 审查文件总数 | 27 |
| Java 文件 | 22 |
| TypeScript/TSX 文件 | 5 |
| P0 阻塞问题 | 6 |
| P1 推荐修复 | 3 |
| P2 参考改进 | 7 |
| 自动化预扫命中 | 9 (52/222 规则) |
| 跨仓接口对齐 | 5/5 路径对齐 ✅ |
| 功能点覆盖 | 12 个功能点，6 个存在 P0 问题 |

---

*报告生成时间: 2026-08-18 | 审查工具: dtazziboot-java-code-review v1.1.0 + scan-all-rules.sh*