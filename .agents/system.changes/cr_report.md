# Code Review Report

> **Change** 成本统计报表系统（第二轮复审：问题修复验证） · **日期** 2026-08-18 · **审查者** AI (DTCoder)
>
> **审查范围**：[testDj] 后端 Java 代码（22 个 .java 文件） — 重点验证第一轮 6 个 P0 阻塞问题的修复情况
>
> **等级**：P0 阻塞 / P1 推荐 / P2 参考

---

## §1 审查概述

本轮为第二轮复审，聚焦第一轮 CR 中发现的 6 个 P0 阻塞问题是否已通过「问题修复」阶段（commit `7bddbcf`）正确解决。修复涉及 6 个文件：

| 文件 | 涉及 P0 问题 |
|------|-------------|
| `CorsConfig.java` | P1-1 CORS 凭证与通配符冲突 |
| `GlobalExceptionHandler.java` | P1-2 异常信息泄露 |
| `DashboardServiceImpl.java` | P0-4 趋势仅年周期 + P2-2 硬编码项目名 |
| `ExportServiceImpl.java` | P0-5 导出未应用筛选 + P0-6 catch 无日志 |
| `LaborCostServiceImpl.java` | P0-1 维度筛选未生效 |
| `ProjectCostServiceImpl.java` | P0-2 维度筛选未生效 + P0-3 Item 字段缺失 |

**自动化预扫**：重新运行 `scan-all-rules.sh`，命中 8 项（1 P0 误报, 1 P1 已缓解, 6 P2）。

**复审结论**：**6 个 P0 阻塞问题全部修复**，**blocker_count = 0**。3 个 P1 中 2 个已修复，7 个 P2 中 2 个已修复。代码质量显著提升，可合并。

---

## §2 审查范围

### 2.1 后端 Java 文件清单 (testDj)

| # | 文件 | 状态 | 说明 |
|---|------|------|------|
| 1 | `CostApplication.java` | ✅ 已审 | 无变更 |
| 2 | `common/Result.java` | ✅ 已审 | 无变更 |
| 3 | `common/DimensionVO.java` | ✅ 已审 | 无变更 |
| 4 | `config/CorsConfig.java` | ✅ 已审（已修复） | P1-1 已修复 |
| 5 | `config/GlobalExceptionHandler.java` | ✅ 已审（已修复） | P1-2 已修复 |
| 6 | `controller/DashboardController.java` | ✅ 已审 | 无变更 |
| 7 | `controller/DimensionController.java` | ✅ 已审 | 无变更，显式导入 |
| 8 | `controller/ExportController.java` | ✅ 已审 | 无变更 |
| 9 | `controller/LaborCostController.java` | ✅ 已审 | 无变更 |
| 10 | `controller/ProjectCostController.java` | ✅ 已审 | 无变更 |
| 11 | `dto/DashboardVO.java` | ✅ 已审 | 无变更 |
| 12 | `dto/LaborCostQueryDTO.java` | ✅ 已审 | 无变更 |
| 13 | `dto/LaborCostVO.java` | ✅ 已审 | 无变更 |
| 14 | `dto/ProjectCostQueryDTO.java` | ✅ 已审 | 无变更 |
| 15 | `dto/ProjectCostVO.java` | ✅ 已审 | 无变更 |
| 16 | `entity/*.java` (6 个实体) | ✅ 已审 | 无变更 |
| 17 | `mapper/*.java` (6 个 Mapper) | ✅ 已审 | 无变更 |
| 18 | `service/impl/DashboardServiceImpl.java` | ✅ 已审（已修复） | P0-4 + P2-2 已修复 |
| 19 | `service/impl/DimensionServiceImpl.java` | ✅ 已审 | 无变更 |
| 20 | `service/impl/ExportServiceImpl.java` | ✅ 已审（已修复） | P0-5 + P0-6 已修复 |
| 21 | `service/impl/LaborCostServiceImpl.java` | ✅ 已审（已修复） | P0-1 已修复 |
| 22 | `service/impl/ProjectCostServiceImpl.java` | ✅ 已审（已修复） | P0-2 + P0-3 已修复 |

---

## §3 功能性检查 (Step 2) — P0 修复验证

对照 spec 功能点 F01–F12 逐项复核修复后的代码：

| REQ ID | 功能点 | 关联文件 | 第一轮结论 | 第二轮结论 | 证据 |
|--------|--------|----------|-----------|-----------|------|
| F01 | 维度查询接口 | `DimensionController.java` | ✅ 满足 | ✅ 满足 | 无变更 |
| F02 | 人力成本统计（按角色） | `LaborCostServiceImpl.java` | ❌ P0 | ✅ 已修复 | `applyDimensionFilter()` 方法（L91-122）正确解析 department/project/businessLine 名称→ID 映射，personnelId 直接过滤。未匹配到名称时使用 `-1L` 确保返回空集 |
| F03 | 项目成本统计 | `ProjectCostServiceImpl.java` | ❌ P0 | ✅ 已修复 | `applyDimensionFilter()`（L102-130）+ `loadProjectMap/loadDepartmentMap/loadBusinessLineMap`（L132-151）批量加载真实名称，Item 正确设置 department/businessLine（L71-72） |
| F04 | Dashboard 汇总概览 | `DashboardServiceImpl.java` | ❌ P0 | ✅ 已修复 | `buildTrend()`（L104-164）使用 Java 14+ 增强 switch（`->` 无穿透），支持 month→12月/quarter→4季度/year→3年；`generateLast12Months/4Quarters/3Years` 生成正确标签 |
| F05 | 按时间维度筛选 | 多个 ServiceImpl | ✅ 满足 | ✅ 满足 | 所有 `applyPeriodFilter` 均含 default 分支抛出 `IllegalArgumentException` |
| F06 | 按部门维度筛选 | `LaborCostServiceImpl.java`, `ProjectCostServiceImpl.java` | ❌ P0 | ✅ 已修复 | 见 F02/F03 |
| F07 | 按项目维度筛选 | 同上 | ❌ P0 | ✅ 已修复 | 见 F02/F03 |
| F08 | 按业务线维度筛选 | 同上 | ❌ P0 | ✅ 已修复 | 见 F02/F03 |
| F09 | 按人员维度筛选 | `LaborCostServiceImpl.java` | ❌ P0 | ✅ 已修复 | `query.getPersonnelId() != null` → `wrapper.eq(LaborCost::getPersonnelId, ...)`（L119-121） |
| F10 | 报表导出（Excel） | `ExportServiceImpl.java` | ❌ P0 | ✅ 已修复 | `applyLaborDimensionFilter`（L153-189）+ `applyProjectDimensionFilter`（L191-220）完整应用所有维度筛选 |
| F11 | 成本趋势分析 | `DashboardServiceImpl.java` | ❌ P0 | ✅ 已修复 | 见 F04 |
| F12 | 超支项目 Top N | `DashboardServiceImpl.java` | ⚠️ P1 | ✅ 已修复 | 使用 `projectMapper.selectBatchIds()` 查询真实项目名（L75-79），`proj.getName()` 替代硬编码（L92） |

### 跨仓接口契约对齐

| 契约 | 后端 (testDj) | 前端 (testDJnew) | 对齐 |
|------|--------------|-------------------|------|
| GET /api/cost/dimensions | `DimensionController` | `getDimensions()` | ✅ |
| POST /api/cost/labor-stats | `LaborCostController` | `queryLaborStats()` | ✅ |
| POST /api/cost/project-stats | `ProjectCostController` | `queryProjectStats()` | ✅ |
| GET /api/cost/dashboard | `DashboardController` | `getDashboard()` | ✅ |
| POST /api/cost/export | `ExportController` | `exportReport()` | ✅ |
| 统一响应格式 | `Result<T>` (code: int) | `ApiResult<T>` (code: number) | ✅ |
| 项目成本 Item 字段 | `department`, `businessLine` 已赋值 | 前端类型定义含对应字段 | ✅ |

---

## §4 可读性检查 (Step 3)

### 4.1 脚本扫描结果

| ID | 检查项 | 命中文件 | 等级 | 说明 |
|----|--------|----------|------|------|
| A2.2 | 通配符导入 | 5 个文件 | P2 | `DashboardController.java:7`, `ExportController.java:6`, `LaborCostController.java:8`, `ProjectCostController.java:8` — `import org.springframework.web.bind.annotation.*`；`ExportServiceImpl.java:18` — `import org.apache.poi.ss.usermodel.*`。DimensionController 已使用显式导入，建议统一 |
| A3.4 | 行宽超限 | `ExportServiceImpl.java:56` | P2 | `createLaborSheet(workbook, periodType, periodValue, department, project, businessLine, role, personnelId)` 行过长 |

### 4.2 LLM 补充审查

| 项目 | 文件 | 等级 | 说明 |
|------|------|------|------|
| 注释质量 | `LaborCostServiceImpl.java:88-90`, `ProjectCostServiceImpl.java:99-101` | ✅ | 新增 Javadoc 注释说明维度筛选逻辑 |
| 方法命名 | `DashboardServiceImpl.java` | P2 | `buildTrend` / `generateLast12Months` 等命名清晰，但 `applyPeriod` 与 `applyPeriodFilter` 命名不一致（DashboardServiceImpl 用 `applyPeriod`，其他 ServiceImpl 用 `applyPeriodFilter`） |
| 魔法数字 | `DashboardServiceImpl.java:191,207` | P2 | `for (int i = 11; i >= 0; i--)` 和 `for (int i = 3; i >= 0; i--)` 中的 11/3 建议提取为常量 `TREND_MONTH_COUNT` / `TREND_QUARTER_COUNT` |

---

## §5 可靠性/安全/缺陷检查 (Step 4)

### 5.1 脚本扫描结果 (scan-all-rules.sh, 第二轮)

| 等级 | 规则 ID | 名称 | 位置 | 复核结论 |
|------|---------|------|------|----------|
| ~~P0~~ | G16.2 | CatchWithoutLogging | `ExportServiceImpl.java:69` | **误报** — `log.error("导出失败", e)` 位于 L70，脚本正则仅匹配同行 `catch` 后的 `log.`，人工确认已正确记录日志 |
| ~~P1~~ | S10.2 | CorsWildcard | `CorsConfig.java:15` | **已缓解** — `addAllowedOriginPattern("*")` 仍为通配符，但 `setAllowCredentials(false)` 已设置，不再违反 CORS 规范。脚本仅检测通配符模式，无法感知 credentials 配置 |
| P2 | A2.2 | WildcardImport | 5 个文件 | 见 §4 |
| P2 | A3.4 | LineWidthExceeded | `ExportServiceImpl.java:56` | 见 §4 |

### 5.2 LLM 补充审查

#### 可靠性 (G)

| ID | 规则 | 命中 | 位置 | 等级 | 说明 |
|----|------|------|------|------|------|
| G1 | 并发控制 | N/A | - | - | 纯查询接口，无并发写入风险 |
| G2 | 资源释放 | ✅ | `ExportServiceImpl.java:54-68` | ✅ | try-with-resources 正确关闭 Workbook 和 OutputStream |
| G4 | 边界条件 | ✅ | 多处 | ✅ | 所有 `divide` 前均有 `compareTo(BigDecimal.ZERO) == 0` 检查；维度筛选未匹配到名称时使用 `-1L` 兜底；所有 switch 均有 default 抛出 `IllegalArgumentException` |
| G5 | 超时/重试 | ❌ | 无 | P2 | 未配置数据库连接超时/查询超时（与第一轮一致） |

#### 安全 (S)

| ID | 规则 | 命中 | 位置 | 等级 | 说明 |
|----|------|------|------|------|------|
| S1 | SQL 注入 | N/A | - | - | MyBatis-Plus LambdaQueryWrapper，无 SQL 拼接 |
| S9.1 | 硬编码凭证 | ⚠️ | `application.yml:7-8` | P2 | 数据库密码 `root` 仍硬编码（与第一轮一致，未在本次修复范围） |
| S10.2 | CORS 通配符 | ✅ | `CorsConfig.java:15` | P2 | 通配符 + `allowCredentials(false)` 可接受，但生产环境建议限定具体域名 |
| S10 | 异常信息泄露 | ✅ | `GlobalExceptionHandler.java:14-15` | ✅ | 已修复：返回通用消息"系统繁忙，请稍后重试"，详细异常记录到 `log.error` |

#### Bug 模式 (B/M/I)

| 规则 | 位置 | 等级 | 说明 |
|------|------|------|------|
| 除零保护 | 多处 | ✅ | 所有除法操作前均有零值检查 |
| 精度丢失 | `ExportServiceImpl.java:95,120-121,125-126` | P2 | `BigDecimal.doubleValue()` 写入 Excel 单元格存在精度丢失风险（与第一轮一致，未修复） |
| 空指针风险 | `ExportServiceImpl.java:45-46` | P2 | `params.get("periodType")` / `params.get("periodValue")` 可能返回 null，但 `applyLaborPeriod`/`applyProjectPeriod` 已有 `StringUtils.hasText` 守卫 |

---

## §6 自定义扩展检查 (Step 5)

**N/A（未启用自定义规则）** — `references/customized-checklist.md` 为空/示例项，无团队特定规则。

---

## §7 跨仓对齐点检查

| 检查项 | 后端 (testDj) | 前端 (testDJnew) | 结论 |
|--------|--------------|-------------------|------|
| 维度接口 | `GET /api/cost/dimensions` | `request.get('/cost/dimensions')` | ✅ |
| 人力成本接口 | `POST /api/cost/labor-stats` | `request.post('/cost/labor-stats', ...)` | ✅ |
| 项目成本接口 | `POST /api/cost/project-stats` | `request.post('/cost/project-stats', ...)` | ✅ |
| Dashboard 接口 | `GET /api/cost/dashboard` | `request.get('/cost/dashboard', ...)` | ✅ |
| 导出接口 | `POST /api/cost/export` | `axios.post('/api/cost/export', ...)` | ✅ |
| 响应格式 | `Result<T> { code, data, message }` | `ApiResult<T> { code, data, message }` | ✅ |
| 项目成本 Item | `department`, `businessLine` 已赋值 | 前端类型定义含对应字段 | ✅ |
| 导出 Content-Type | `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet` | `responseType: 'blob'` | ✅ |
| CORS 配置 | `addAllowedOriginPattern("*")` + `allowCredentials(false)` | Vite proxy `/api` → `localhost:8080` | ✅ 可接受 |

---

## §8 修复任务列表

### P0 阻塞（必须修复才能合并）

**无待修复项。** 第一轮 6 个 P0 问题全部修复。

### P1 推荐（合并前应修复）

- [ ] **P1-1** `ExportController.java:18` + `ExportServiceImpl.java:43` — 将 `Map<String, Object>` 参数替换为类型化 DTO（如 `ExportQueryDTO`），增强编译时类型安全和可维护性。当前 `params.get("periodType")` 等字符串键存在拼写错误风险

### P2 参考（可选改进）

- [ ] **P2-1** 5 个文件 — 将通配符导入替换为显式导入（`import org.springframework.web.bind.annotation.*` → 具体注解类；`import org.apache.poi.ss.usermodel.*` → 具体类）
- [ ] **P2-2** `application.yml:7-8` — 数据库密码使用环境变量 `${DB_PASSWORD}` 替代硬编码
- [ ] **P2-3** `ExportServiceImpl.java:95,120-121,125-126` — Excel 金额单元格使用 `setCellValue(BigDecimal)` 或格式化字符串，避免 double 精度丢失
- [ ] **P2-4** `DashboardServiceImpl.java:191,207` — 将魔法数字 `11` / `3` 提取为常量
- [ ] **P2-5** `DashboardServiceImpl.java:166` — 方法命名 `applyPeriod` 建议统一为 `applyPeriodFilter` 与其他 ServiceImpl 保持一致

---

## §9 审查指标

| 指标 | 第一轮 | 第二轮 | 变化 |
|------|--------|--------|------|
| 审查文件总数 | 27 | 22（仅 Java） | — |
| P0 阻塞问题 | 6 | **0** | ✅ -6 |
| P1 推荐修复 | 3 | 1 | ✅ -2（P1-1/P1-2 已修复，P1-3 未修复） |
| P2 参考改进 | 7 | 5 | ✅ -2（P2-2/P2-5 已修复） |
| 自动化预扫命中 | 9 | 8 | — |
| 跨仓接口对齐 | 5/5 ✅ | 5/5 ✅ | — |
| 功能点覆盖 | 6/12 P0 | 12/12 ✅ | ✅ 全部满足 |

---

*报告生成时间: 2026-08-18 | 审查工具: dtazziboot-java-code-review v1.1.0 + scan-all-rules.sh | 复审轮次: 2*
