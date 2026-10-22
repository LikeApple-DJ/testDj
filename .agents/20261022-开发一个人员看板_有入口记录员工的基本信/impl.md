# 人员看板 编码实现报告

> 基于系分 `design.md`（同目录），Skill: dtazziboot-java-coding-standards

## 模块进度追踪

| 序号 | 模块 | READ | TEST | IMPL | CHECK | DOCS | 状态 |
|------|------|------|------|------|-------|------|------|
| 1 | common/工程骨架 | ✅ | ✅ | ✅ | ✅ | ✅ | 已完成 |
| 2 | employee | ✅ | ✅ | ✅ | ✅ | ✅ | 已完成 |
| 3 | budget | ✅ | ✅ | ✅ | ✅ | ✅ | 已完成 |
| 4 | import | ✅ | ✅ | ✅ | ✅ | ✅ | 已完成 |
| 5 | whitelist | ✅ | ✅ | ✅ | ✅ | ✅ | 已完成 |

## 已实现文件清单

### 工程与资源
- `pom.xml`（Spring Boot 3.3 + MyBatis + MySQL + POI + Validation + JUnit5/Mockito）
- `src/main/resources/application.yml`、`schema.sql`（5 张表 + 初始化白名单管理员）

### common
- `common/Result.java`、`PageResult.java`、`BizException.java`、`GlobalExceptionHandler.java`
- `common/UserContext.java`、`AuthInterceptor.java`（/api/** 白名单拦截）、`WebConfig.java`

### employee（W01-W06 / S01-S06）
- `employee/EmployeeDO.java`、`EmployeeDTO.java`（含手机号脱敏 maskPhone）
- `employee/EmployeeCreateReq.java`、`EmployeeUpdateReq.java`、`EmployeeQueryReq.java`、`EmployeeSummaryDTO.java`
- `employee/mapper/EmployeeMapper.java`（#{ } 参数化，软删过滤，唯一索引兜底 DuplicateKey→EMP_001）
- `employee/EmployeeService.java`、`EmployeeServiceImpl.java`（含 upsertFromImport 供导入模块复用）
- `employee/EmployeeController.java`

### budget（W10-W11 / S09-S10）
- `budget/BudgetDO.java`、`BudgetDTO.java`、`BudgetSaveReq.java`
- `budget/mapper/BudgetMapper.java`（ON DUPLICATE KEY UPDATE upsert）
- `budget/BudgetService.java`、`BudgetServiceImpl.java`（周期 yyyy-MM/yyyy-Qn 校验、金额 BigDecimal 非负两位小数）
- `budget/BudgetController.java`

### import（W07-W09 / S07-S08）
- `importing/ImportTaskDO.java`、`ImportDetailDO.java`、`ImportTaskDTO.java`
- `importing/mapper/ImportTaskMapper.java`
- `importing/ImportService.java`、`ImportServiceImpl.java`（表头校验 IMP_003、1000 行上限 IMP_002、文件内工号去重、逐行成败明细、行内预算联动）
- `importing/ImportController.java`（模板下载/上传/批次查询）

### whitelist（W12-W14 / S11-S12）
- `whitelist/WhitelistDO.java`、`mapper/WhitelistMapper.java`
- `whitelist/WhitelistService.java`、`WhitelistServiceImpl.java`（WL_002 重复 / WL_004 最后一条保护 / 软删复活）
- `whitelist/WhitelistController.java`

### 单元测试（AAA 模式，Mockito 隔离 Mapper）
- `src/test/java/.../employee/EmployeeServiceImplTest.java`（8 用例：正常/工号重复/邮箱非法/日期非法/员工不存在/状态非法/脱敏分页/删除不存在）
- `src/test/java/.../budget/BudgetServiceImplTest.java`（5 用例：正常/员工不存在/周期非法/金额负数/季度周期）
- `src/test/java/.../whitelist/WhitelistServiceImplTest.java`（6 用例：命中/未命中/重复添加/参数非法/最后一条保护/正常移除）

## CHECK 结果

### L1 静态检查

| 检查项 | 规范要求 | 符合情况 |
|--------|----------|----------|
| 命名规范 | 类名大驼峰、方法小驼峰、常量全大写 | ✅ |
| 网关出参 | 通用 {code,msg,data}，成功 code="OK" | ✅ |
| 异常日志 | SLF4J + 占位符 + BizException 错误码（EMP/IMP/BUD/WL_XXX） | ✅ |
| 安全规范 | SQL 全部 #{ } 参数化、@Valid 入参校验、白名单拦截、手机号脱敏 | ✅ |
| MySQL 规范 | 表名小写、id/gmt_create/gmt_modified 必备字段、InnoDB+utf8mb4、唯一索引兜底 | ✅ |
| 金额规范 | BigDecimal，禁止 float/double | ✅ |
| 单元测试 | 测试类存在、AAA 模式、覆盖正常/参数/异常/边界 | ✅ |

### L2 动态验证

| 验证项 | 状态 | 说明 |
|--------|------|------|
| 编译验证 | ⚠️ | 执行环境无 java/mvn，未执行 |
| 单测验证 | ⚠️ | 同上 |

### 待人工验证

```bash
cd testDj-main
mvn compile -DskipTests
mvn test
```

## 跨仓对齐点

- 两仓库（testDj / ranxitest）均为空仓库，无既有代码与跨库契约；全部实现落于 testDj，ranxitest 无改动。
- 模块间契约按 design.md §4.3：EmployeeService.upsertFromImport 供 import 调用；BudgetService.saveBudget/queryByEmpNo/sumCurrentPeriod 供 import/employee 调用；WhitelistService.isAllowed 供 AuthInterceptor 调用，入参/出参类型与设计一致。

## 遗留与假设

- 登录态复用假设：AuthInterceptor 从请求头 `X-Emp-No` 获取工号（对接统一登录态网关时需对齐）。
- 白名单缓存（Caffeine 60s TTL）与限流未实现，预留后续迭代；当前直查 DB。
- summary 的 recentImport 暂返回 null（可接 ImportTaskMapper.selectLatestTask，Mapper 中已预留）。
