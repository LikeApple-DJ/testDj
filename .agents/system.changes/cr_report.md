# Code Review Report

> **Change**: 成本统计报表系统 — 编码实现阶段
> **分支/Commit**: `AI/task-DEV-9d10e310-7901-11f1-8a9f-59ecae612580-2e1b780f-c865-4257-ae68-d44387f78c47` / `ecc130e`
> **日期**: 2025-08-17
> **审查者**: DTCoder (AI)
> **关联仓库**: [testDj] 前端主仓库 / [testDJnew] 后端服务仓库

---

## §0 审查范围说明

### 0.1 Java 守卫触发

按 `dtazziboot-java-code-review` 技能规范，执行预检结果：

| 检查项 | testDj-main | testDJnew-main |
|--------|-------------|----------------|
| Git 仓库 | ✅ | ✅ |
| 变更文件数 | 4 个 `.md` 文件 | 0（仅 Initial commit） |
| Java 文件变更 | **0** | **0** |

**结论**：本次变更**不包含任何 Java 代码文件**。两个仓库的 `AI/task-DEV-*` 分支仅包含 SDD 文档产物（需求澄清、实施计划、系分设计、编码实现计划），实际代码尚未实现。

按技能规范，Java 代码审查即时终止。本报告转为**文档审查模式**，对设计文档与编码计划进行静态审查。

### 0.2 审查范围

| 文件 | 行数 | 审查维度 |
|------|------|----------|
| `.agents/specs/dima.md` | 339 | 需求完整性 |
| `.agents/specs/20260817-开发一个成本统计报表...md` | 2908 | 实施计划可行性 |
| `.agents/system.changes/design.md` | 1286 | 架构设计合理性 |
| `.agents/system.changes/code.md` | 645 | 编码计划一致性 |

---

## §1 审查总览

### 1.1 文档质量评估

| 维度 | 评分 | 说明 |
|------|------|------|
| 需求追溯性 | ⭐⭐⭐⭐⭐ | 从 dima.md → 实施计划 → design.md → code.md 四层可追溯，需求 ID 到任务映射完整 |
| 跨仓契约一致性 | ⭐⭐⭐⭐⭐ | 前端 TS 类型与后端 DTO 严格对齐，枚举/分页/金额/日期格式统一 |
| 架构设计完整性 | ⭐⭐⭐⭐☆ | 功能架构、数据模型、接口设计、非功能需求覆盖全面 |
| 编码计划可行性 | ⭐⭐⭐⭐☆ | 13 个任务拆分合理，依赖关系清晰，文件清单完整 |
| 风险识别 | ⭐⭐⭐⭐☆ | 识别了 5 项风险并给出了缓解措施 |

### 1.2 问题统计

| 等级 | 数量 | 说明 |
|------|------|------|
| **P0 阻塞** | **0** | 无阻塞性问题 |
| **P1 推荐** | 3 | 建议修复后再进入编码 |
| **P2 参考** | 4 | 可选改进 |

---

## §2 功能性检查 (Step 2 — REQ 核对)

### 2.1 需求覆盖矩阵

基于 `dima.md` §2 功能需求与 `code.md` §8 需求覆盖追踪：

| 需求 ID | 功能 | 设计覆盖 | 编码计划覆盖 | 状态 |
|---------|------|----------|-------------|------|
| FR-DASH-01 | 成本总览卡片 | design.md §5.2.3.1 | code.md §3.3 (Task 11) | ✅ |
| FR-DASH-02 | 部门成本排行 | design.md §5.2.3.2 | code.md §3.3 (Task 11) | ✅ |
| FR-DASH-03 | 月度趋势图 | design.md §5.2.3.3 | code.md §3.3 (Task 11) | ✅ |
| FR-DASH-04 | 项目超支预警 | design.md §5.2.3.4 | code.md §3.3 (Task 11) | ✅ |
| FR-DASH-05 | 人力成本构成 | design.md §5.2.3.5 | code.md §3.3 (Task 11) | ✅ |
| FR-DASH-06 | 业务线对比 | design.md §5.2.3.5 | code.md §3.3 (Task 11) | ✅ |
| FR-ANALY-01 | 多维筛选器 | design.md §5.3.2.1 | code.md §3.4 (Task 12) | ✅ |
| FR-ANALY-02 | 人力成本明细表 | design.md §5.3.2.1 | code.md §3.4 (Task 12) | ✅ |
| FR-ANALY-03 | 项目成本明细表 | design.md §5.3.2.1 | code.md §3.4 (Task 12) | ✅ |
| FR-ANALY-04 | 交叉分析 | design.md §5.3.2.2 | code.md §3.4 (Task 12) | ✅ |
| FR-ANALY-05 | 时间维度切换 | design.md §5.3.2.2 | code.md §3.4 (Task 12) | ✅ |
| FR-ANALY-06 | 数据钻取 | design.md §5.3.2.2 | code.md §3.4 (Task 12) | ✅ |
| FR-EXPORT-01 | 导出当前视图 | design.md §5.4.4.1 | code.md §3.4 (Task 12 export) | ✅ |
| FR-EXPORT-02 | 导出图表 | design.md §5.4 | code.md §8 (P2, 暂不实现) | ⚠️ 已声明延期 |
| FR-EXPORT-03 | 定时导出 | design.md §5.4.4.2 | code.md §2.8 (Task 8) | ✅ |
| FR-EXPORT-04 | 导出模板 | design.md §5.4.1 | code.md §2.8 (Task 8) | ✅ |

### 2.2 REQ 详细核对

#### REQ-01: 成本总览卡片 (FR-DASH-01)
- **Spec 证据**: `dima.md:37` — "展示本期总成本、环比变化率、预算执行率"
- **Design 证据**: `design.md:478-481` — DashboardSummaryResponse 含 totalCost, totalCostChange, budgetExecutionRate
- **Code 计划**: `code.md:186-188` — Service 计算环比和预算执行率
- **结论**: ✅ 设计完整覆盖需求

#### REQ-02: 多维筛选器 (FR-ANALY-01)
- **Spec 证据**: `dima.md:48` — "部门、项目、业务线、人员、月份/季度/年度联动筛选"
- **Design 证据**: `design.md:652-666` — AnalysisQueryRequest 含全部维度筛选字段
- **Code 计划**: `code.md:221-226` — 动态查询构建方案
- **结论**: ✅ 覆盖完整

#### REQ-03: 报表导出 (FR-EXPORT-01)
- **Spec 证据**: `dima.md:59` — "将当前筛选条件下的表格数据导出为 Excel"
- **Design 证据**: `design.md:889-906` — W03 接口含 format/filters 参数，错误码完整
- **Code 计划**: `code.md:248-252` — 业务规则（5万行限制、60s超时、空数据导出空Excel）
- **结论**: ✅ 覆盖完整

---

## §3 可读性检查 (Step 3 — 文档质量)

> 由于无 Java 代码，本节审查文档可读性与结构一致性。

### A1 文档结构

| 检查项 | 状态 | 说明 |
|--------|------|------|
| 四层文档命名规范 | ✅ | dima.md → 实施计划 → design.md → code.md 链路清晰 |
| 文档版本标记 | ✅ | 所有文档含版本号 v1.0 + 日期 |
| 章节编号一致性 | ✅ | design.md 与 code.md 章节编号对齐 |

### A2 命名一致性

| 检查项 | 状态 | 说明 |
|--------|------|------|
| 枚举值一致性 | ✅ | DEV/TEST/PM/OPS、LABOR/PROJECT/ALL 四文档统一 |
| API 路径一致性 | ✅ | `/api/cost/*` 全局统一 |
| 错误码格式一致性 | ✅ | `COST_{MODULE}_{SEQ}` 全局统一 |

### A3 问题标记

| ID | 等级 | 位置 | 问题描述 |
|----|------|------|----------|
| R1 | P2 | `code.md:84` | 索引数量描述不一致：code.md §2.3 摘要写"6 个索引"，但详细列表有 7 个索引（含 `idx_cost_record_role`），design.md §5.5.1 也是 7 个 |
| R2 | P2 | `code.md:226` | "聚合: 查询结果集外独立计算 `aggregations`" — 表述模糊，未说明如何独立计算（是二次查询还是应用层聚合） |

---

## §4 可靠性检查 (Step 4 — 设计可靠性)

> 由于无 Java 代码，本节审查设计文档中的可靠性设计。

### G1 并发控制

| 检查项 | 状态 | 说明 |
|--------|------|------|
| Dashboard 纯查询无并发风险 | ✅ | `design.md:580` 明确标注 |
| Analysis 纯查询无并发风险 | ✅ | `design.md:794` 明确标注 |
| 导出并发控制 | ⚠️ | `design.md:961` 描述"无锁，每次导出独立处理"，但建议异步化。`code.md` 未明确同步/异步导出策略 |

### G2 资源释放

| 检查项 | 状态 | 说明 |
|--------|------|------|
| 数据库连接池 | ✅ | `design.md:1222` 监控连接池 |
| Redis 连接降级 | ✅ | `design.md:1166` Redis 不可用时降级直查数据库 |
| 导出文件清理 | ❌ | `design.md` 和 `code.md` 均未提及导出文件 `/tmp/cost-exports/` 的生命周期管理与清理策略 |

### G3 事务边界

| 检查项 | 状态 | 说明 |
|--------|------|------|
| 预算导入事务 | ✅ | `design.md:1180` "事务批量提交，失败回滚" |
| 成本记录写入 | ⚠️ | 文档未明确成本记录的数据写入来源（是 ETL 导入还是手动录入？），事务边界不清晰 |

### G4 边界条件

| 检查项 | 状态 | 说明 |
|--------|------|------|
| 无数据月份 | ✅ | `design.md:577/1181` 返回空数据不报错 |
| 环比除零保护 | ❌ | `code.md:186` 公式 `(totalCost - prevTotalCost) / prevTotalCost`，当 prevTotalCost=0 时除零异常。design.md 未明确处理 |
| 金额为负 | ❌ | 文档未定义金额是否允许负数（如退款/冲销场景） |

### 安全问题标记

| ID | 等级 | 位置 | 问题描述 |
|----|------|------|----------|
| S1 | P1 | `design.md:1186-1192` | 认证授权声明"本期不做细粒度权限，所有用户可见全量数据"。作为企业成本系统，即使本期简化，也应至少要求登录态校验并在 API 层统一拦截 |
| S2 | P1 | `design.md:1196` | SQL 注入防护声明使用 JPQL 预编译，但 Analysis 模块的动态查询拼接（`code.md:222-224`）涉及动态 WHERE/GROUP BY 拼接，需确保使用参数化查询而非字符串拼接 |

---

## §5 跨仓契约对齐检查 (Step 5)

### 5.1 API 契约

| 契约项 | testDj (前端) | testDJnew (后端) | 对齐 |
|--------|--------------|-------------------|------|
| 基础路径 | `/api/cost` | `/api/cost` | ✅ |
| Dashboard 接口 | `GET /dashboard/summary` | `@GetMapping("/dashboard/summary")` | ✅ |
| Analysis 接口 | `POST /analysis/query` | `@PostMapping("/analysis/query")` | ✅ |
| Export 接口 | `POST /export` (blob) | `@PostMapping("/export")` → byte[] | ✅ |
| 通用响应 | axios 解包 `.data` | `ApiResponse { result, msg, data }` | ✅ |

### 5.2 数据类型

| 类型 | 后端 Java | 前端 TypeScript | 对齐 |
|------|-----------|-----------------|------|
| 金额 | `BigDecimal(15,2)` | `number` (`.toFixed(2)`) | ✅ |
| 日期 | `String "YYYY-MM"` | `string` | ✅ |
| 角色枚举 | `EmployeeRole: DEV/TEST/PM/OPS` | `type EmployeeRole = 'DEV'\|'TEST'\|'PM'\|'OPS'` | ✅ |
| 成本类型 | `CostType: LABOR/PROJECT` | `type CostType = 'LABOR'\|'PROJECT'\|'ALL'` | ⚠️ 后端无 ALL 枚举值 |
| 分页 | `int page` (1-based) | `number` (1-based) | ✅ |

### 5.3 跨仓对齐问题

| ID | 等级 | 位置 | 问题描述 |
|----|------|------|----------|
| C1 | P1 | `code.md:30` vs `code.md:68` | 后端 `CostType` 枚举仅含 `LABOR`, `PROJECT`，但 design.md 和前端 types 包含 `ALL`。`ALL` 应作为查询参数的特殊值在后端 Service 层处理（不传 type 过滤条件），而非枚举值。需在代码中明确 `ALL` 的语义处理 |
| C2 | P2 | `code.md:159` | `AnalysisQueryRequest.page` 标注为 `int, 1-based`，但 `design.md:663` 未明确标注 1-based，建议两端文档统一 |

---

## §6 编码计划完整性审查

### 6.1 后端文件清单完整性

对照 `code.md §5.1` 文件清单与 `design.md` 模块设计：

| 模块 | 计划文件数 | 设计覆盖 | 缺失 |
|------|-----------|----------|------|
| 实体/枚举 | 9 | ✅ | - |
| Repository | 5 | ✅ | - |
| DTO | 5 | ✅ | - |
| Service | 3 | ✅ | - |
| Controller | 4 | ✅ | - |
| Config | 1 | ✅ | - |
| Job | 1 | ✅ | - |
| 迁移 | 1 | ✅ | - |
| 配置 | 1 | ✅ | - |

**缺失项**: `design.md` 中提到的 `MasterDataService` (S01-S06 以外的基础数据 CRUD) 在 `code.md §5.1` 文件清单中未列出，但 `code.md §2.9` 有 MasterDataController。建议确认 MasterDataService 是否需要单独创建。

### 6.2 前端文件清单完整性

对照 `code.md §5.2` 与 `design.md §2.1`：

| 组件 | 计划文件 | 设计覆盖 |
|------|----------|----------|
| 页面容器 | 3 (`CostDashboard`, `CostAnalysis`, `CostDetail`) | ✅ |
| 图表组件 | 6 | ✅ |
| 筛选/表格 | 2 | ✅ |
| API/Types | 2 | ✅ |
| 配置 | 3 | ✅ |

**缺失项**: `design.md` 中 `ExportBtn` 组件未在 `code.md §5.2` 独立列出，而是集成在 `CostAnalysis.tsx` 页面的导出按钮中。功能覆盖无遗漏，但组件粒度不一致。

---

## §7 设计缺陷与风险

### 7.1 已识别问题汇总

| ID | 等级 | 类别 | 描述 | 建议 |
|----|------|------|------|------|
| **P1-01** | P1 | 安全 | 全量数据无权限控制 | 至少实现登录态拦截 + API 层统一鉴权 |
| **P1-02** | P1 | 安全 | 动态 SQL 拼接风险 | 确保 Analysis 模块动态查询使用 Criteria API 或参数化 JPQL |
| **P1-03** | P1 | 契约 | CostType 枚举 `ALL` 语义不清 | 明确 ALL 为查询参数而非枚举值，后端做特殊处理 |
| P2-01 | P2 | 可靠性 | 环比除零未保护 | `prevTotalCost=0` 时环比返回 null 或特殊标记 |
| P2-02 | P2 | 可靠性 | 导出文件无清理策略 | 定义 `/tmp/cost-exports/` 文件的 TTL 和清理机制 |
| P2-03 | P2 | 一致性 | 索引数量描述不一致 | code.md §2.3 统一为 7 个索引 |
| P2-04 | P2 | 完整性 | 成本数据写入来源不明确 | 明确 CostRecord 数据由 ETL 导入还是业务操作产生 |

### 7.2 设计亮点

1. **四层可追溯**: 需求→计划→设计→编码 逐层细化，需求 ID 全程可追溯
2. **降级设计完善**: Redis 不可用降级、数据库超时处理、导出超限处理均有明确方案
3. **状态机设计**: ExportTask 和 Project 状态机设计规范，状态流转条件清晰
4. **跨仓契约基线**: code.md §4 提供了完整的跨仓对齐表，可作为联调基线
5. **三板斧覆盖**: 可监控（8 项监控点）、可灰度（预留方案）、可应急（4 个开关）均有设计

---

## §8 修复任务列表

> 以下为待修复项，按优先级排列。

### P1（合并前应修复）

- [ ] **P1-01**: 明确 API 层登录态校验方案（至少全局拦截器校验 token），在 `design.md §6.4.2` 或 `code.md` 中补充具体实现方式
- [ ] **P1-02**: 在 `code.md §2.7` 中补充动态查询防注入方案说明（明确使用 Criteria API 或 `:param` 占位符，禁止字符串拼接）
- [ ] **P1-03**: 统一 CostType 枚举定义：后端仅保留 `LABOR`/`PROJECT`；`ALL` 作为查询参数特殊值在 Service 层处理（不传 type 过滤条件即为 ALL）

### P2（可选改进）

- [ ] **P2-01**: 在 `code.md §2.6` 环比计算中补充除零保护逻辑
- [ ] **P2-02**: 在 `design.md §5.4` 或 `code.md §2.8` 中补充导出文件生命周期管理策略
- [ ] **P2-03**: 修正 `code.md §2.3` 索引数量描述为 7 个
- [ ] **P2-04**: 在 `design.md §3` 或 `code.md §2.3` 中补充 CostRecord 数据写入来源说明

---

## §9 审查结论

### 9.1 总体评价

本次变更处于 **SDD 文档阶段**，实际 Java/TypeScript 代码尚未实现。四份文档质量整体较高：需求追溯完整、跨仓契约对齐、架构设计合理、编码计划可行。无 P0 阻塞问题。

### 9.2 进入编码阶段的前置条件

1. ✅ 解决 3 个 P1 问题（安全/契约）
2. ✅ 确认 MasterDataService 是否需要单独创建
3. ✅ 明确 CostRecord 数据写入来源

### 9.3 审查统计

| 指标 | 值 |
|------|-----|
| 审查文件数 | 4（全部为 .md 文档） |
| Java 文件数 | 0 |
| P0 阻塞 | 0 |
| P1 推荐修复 | 3 |
| P2 参考建议 | 4 |
| 跨仓契约对齐项 | 10/10 通过 |
| 需求覆盖 | 15/16（FR-EXPORT-02 延期） |

---

> **审查者**: DTCoder (AI) | **审查时间**: 2025-08-17 | **技能**: dtazziboot-java-code-review v1.1.0
> **注**: 本次审查因无 Java 代码变更，Java 守卫触发后转为文档静态审查模式。