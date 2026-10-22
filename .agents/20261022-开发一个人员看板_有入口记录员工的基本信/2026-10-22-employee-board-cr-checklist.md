# Code Review Checklist — 2026-10-22-employee-board

## Step 1 执行队列（30 个 .java，全部 ✅ 已审）

- ✅ employee/EmployeeController.java（⚠️ mask 恒 false）
- ✅ employee/EmployeeServiceImpl.java（⚠️ recentImport=null P0；B022 误报）
- ✅ employee/{EmployeeService,EmployeeDO,EmployeeDTO,EmployeeCreateReq,EmployeeUpdateReq,EmployeeQueryReq,EmployeeSummaryDTO}.java
- ✅ employee/mapper/EmployeeMapper.java（S1.1 ✅ 全 #{} 预编译）
- ✅ importing/ImportController.java / ImportServiceImpl.java（✅ 表头/1000行/去重/overwrite/逐行明细；B022 误报）
- ✅ importing/{ImportService,ImportTaskDO,ImportDetailDO,ImportTaskDTO}.java / mapper/ImportTaskMapper.java
- ✅ budget/BudgetController.java / BudgetServiceImpl.java（⚠️ G11.3 budgetPeriod null 防御 P1；B022/I004 P2）
- ✅ budget/{BudgetService,BudgetDO,BudgetDTO,BudgetSaveReq}.java / mapper/BudgetMapper.java
- ✅ whitelist/WhitelistController.java / WhitelistServiceImpl.java（✅ WL_004 最后一人保护；⚠️ revive 无日志 P2）
- ✅ whitelist/{WhitelistService,WhitelistDO}.java / mapper/WhitelistMapper.java
- ✅ common/AuthInterceptor.java（❌ G8.5 ThreadLocal 残留 P0）
- ✅ common/{UserContext,WebConfig,Result,PageResult,BizException,GlobalExceptionHandler}.java
- ✅ EmployeeBoardApplication.java
- ✅ src/test/java/.../EmployeeServiceImplTest / BudgetServiceImplTest / WhitelistServiceImplTest（断言存在，G11.1 ✅）

非 Java：application.yml（❌ S9.1 口令硬编码 P0）、schema.sql（✅ 全新增表，含 uk 唯一索引）、pom.xml（✅）。

## Step 2 功能核对
详见 cr_report.md §3：REQ-1 ✅ / REQ-2 ❌(recentImport) ⚠️(打码) / REQ-3 ✅(MD5 防重 P2 缺) / REQ-4 ✅ / REQ-5 ✅(缓存 P2 缺)

## Step 3 可读性
⚠️ 全限定类名内联（EmployeeServiceImpl:186、ImportController:29-31）P2；其余 A1-A7 ✅。

## Step 4 可靠性/安全/Bug
- scan-all-rules.sh 原始 13 命中：B022×3 → 局部变量误报降 P2；I004 → P2；G16.2×9 → 8 条误报（重抛/已记录），WhitelistServiceImpl:55 真实 P2。
- LLM 补扫：G8.5 P0（AuthInterceptor）、S9.1 P0（application.yml）、G11.3 P1（BudgetServiceImpl:43）、G11.4 ✅、S1/S7/S8 ✅。

## Step 5 自定义扩展
N/A(未启用自定义规则)

## 收口
- 队列 ⬜ 待审 = 0 ✅
- 报告问题计数：P0=3 / P1=2 / P2=5；blocker_count=3 已写入 run_context.json
