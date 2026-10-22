# Code Review Report

> **Change** `20261022-开发一个人员看板_有入口记录员工的基本信` · **分支** `AI/task-DEV-d885ee0d-78e2-11f1-8f3f-75954cb1c56f-1b6b013d-047e-4918-9ba2-c4448230068b` · **日期** `2026-10-22` · **审查者** AI
>
> **Spec 依据**：`[testDj] .agents/20261022-开发一个人员看板_有入口记录员工的基本信/design.md`（F01–F06 / W01–W14 / R01–R06）。
> **执行方式**：先运行 `scan-all-rules.sh`（命中 13 条，已逐条人工复核，误报已降级），再由 LLM 按 Step 2→3→4→5 逐文件审查。

---

## 1. 审查范围

| 项 | 值 |
|----|-----|
| `.java` 文件数 | `30`（main 27 + test 3） |
| 变更行数 | `+4071 / -0`（全新功能） |

| 类/接口 | 路径 | 角色 |
|---------|------|------|
| EmployeeController/Service(Impl)/DO/DTO/Req/SummaryDTO/Mapper | `src/main/java/com/example/employeeboard/employee/**` | 员工 CRUD + 看板汇总（W01-W06） |
| ImportController/Service(Impl)/TaskDO/DetailDO/TaskDTO/Mapper | `src/main/java/com/example/employeeboard/importing/**` | 批量导入（W07-W09） |
| BudgetController/Service(Impl)/DO/DTO/SaveReq/Mapper | `src/main/java/com/example/employeeboard/budget/**` | 成本预算（W10-W11） |
| WhitelistController/Service(Impl)/DO/Mapper | `src/main/java/com/example/employeeboard/whitelist/**` | 白名单（W12-W14 + 访问校验） |
| AuthInterceptor/UserContext/WebConfig/Result/PageResult/BizException/GlobalExceptionHandler | `src/main/java/com/example/employeeboard/common/**` | 登录态+白名单拦截、通用结构 |
| EmployeeBoardApplication / application.yml / schema.sql / pom.xml | `src/main/**` | 启动与配置 |
| EmployeeServiceImplTest / BudgetServiceImplTest / WhitelistServiceImplTest | `src/test/java/com/example/employeeboard/**` | 单测 |

---

## 2. 问题计数

| P0 | P1 | P2 |
|----|----|----|
| 3 | 2 | 5 |

---

## 3. Step 2 — 功能（REQ）

### REQ-1: F01/F02 员工录入与增删改查（design.md §5.1.3.1，R01-R05）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 新增员工，工号唯一（R02） | ✅ | "工号在租户内唯一…返回 EMP_001" | `employee/EmployeeServiceImpl.java:45-63` 先查 + DuplicateKeyException 兜底 | 与"并发创建同工号由唯一索引兜底"一致 |
| 必填/格式校验（R01/R03） | ✅ | "必填字段非空且长度合法…EMP_002" | `employee/EmployeeCreateReq.java` @NotBlank/@Size；`EmployeeServiceImpl.java:177-184` 邮箱/手机号正则 | |
| 更新前存在性校验（R04） | ✅ | "更新/删除前员工必须存在…EMP_003" | `EmployeeServiceImpl.java:70-73,107-109` | |
| 软删除 | ✅ | "软删除 is_deleted=1" | `employee/mapper/EmployeeMapper.java:33-35` | |
| 状态机流转（ACTIVE↔INACTIVE） | ✅ | "EMP_004 状态流转非法" | `EmployeeServiceImpl.java:75-78` | |

### REQ-2: F03 人员看板列表与汇总（design.md §5.1.2 W04/W06）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 分页查询，默认20/最大100 | ✅ | "默认20条/页"（§1 约束） | `employee/EmployeeQueryReq.java` @Min(1)/@Max(100) | |
| 汇总：总人数/部门数/预算合计 | ✅ | W06 出参定义 | `EmployeeServiceImpl.java:145-151` | |
| 汇总：recentImport 最近导入批次摘要 | ❌ P0 | W06 出参："data.recentImport 最近一次导入批次摘要（批次号/成功数/失败数）" | `EmployeeServiceImpl.java:150` `dto.setRecentImport(null)` 恒为 null | 功能字段未实现 |
| 列表手机号默认打码 | ⚠️ P1 | §6.4.3.2 "列表接口手机号默认中间四位打码"；W04 "非白名单用户返回中间四位打码" | `employee/EmployeeController.java:51` `mask = !isAllowed(current)`，而拦截器已保证白名单 → mask 恒 false，永不打码 | 与设计"默认打码"不一致 |

### REQ-3: F04 批量导入（design.md §5.2.3.1，R01-R06）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 模板下载（W07，9列表头） | ✅ | W07 列定义 | `importing/ImportController.java:28-42` | 表头与 `ImportServiceImpl.TEMPLATE_HEADER` 一致 |
| 表头校验（R01） | ✅ | "表头必须与模板一致…IMP_003" | `importing/ImportServiceImpl.java:181-190` | |
| 单批 ≤1000 行（R03） | ✅ | "IMP_002" | `ImportServiceImpl.java:72-76`（lastRow 0-based，1000 数据行→lastRow=1000 放行，1001 行拒绝，边界正确） | |
| 文件内工号去重（R06） | ✅ | "以首行为准，后续行 FAIL" | `ImportServiceImpl.java:134-136` HashSet | |
| overwrite 策略（R04） | ✅ | "overwrite=false 跳过；true 更新" | `EmployeeServiceImpl.java:155-175` upsertFromImport | |
| 含预算金额必须含周期（R05） | ✅ | "该行 FAIL" | `ImportServiceImpl.java:149-157` | |
| 逐行失败记录明细、整批不回滚 | ✅ | "逐行独立事务…记录失败明细" | `ImportServiceImpl.java:126-179` | |
| 同文件 MD5 30s 防重复提交 | ⚠️ P2 | §5.2.3.1 并发控制"同文件 MD5 30s 内重复上传提示" | 未实现 | 设计约定项缺失 |
| 上传文件 ≤5MB | ✅ | W08 "≤5MB" | `src/main/resources/application.yml` multipart max-file-size 5MB | |

### REQ-4: F05 成本预算（design.md §5.3.3.1，R01-R03）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 员工存在校验（BUD_001） | ✅ | R01 | `budget/BudgetServiceImpl.java:40-42` | |
| 金额 decimal(18,2)、非负（R02） | ✅ | R02 | `BudgetServiceImpl.java:46-49` + `BudgetSaveReq` @DecimalMin；金额全程 BigDecimal，无 float/double | 满足 §1 约束"金额禁止 float/double" |
| 同员工同周期覆盖更新（R03） | ✅ | "uk_emp_budget_emp_period 兜底 upsert" | `BudgetServiceImpl.java:57` `budgetMapper.upsert` | |

### REQ-5: F06 白名单（design.md §5.4）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 增/删/分页查询（W12-W14） | ✅ | §5.4.2 | `whitelist/WhitelistServiceImpl.java`、`WhitelistController.java` | |
| 不允许移除最后一个白名单（WL_004） | ✅ | §5.4.2 业务规则 | `WhitelistServiceImpl.java:67-69` | |
| AuthFilter 拦截 /api/** 全部接口 | ✅ | §5.4.3.1 | `common/WebConfig.java`、`common/AuthInterceptor.java` | |
| 白名单查询 Caffeine 60s 缓存 | ⚠️ P2 | §5.4.3.1 "白名单查询走本地缓存（Caffeine，60s TTL）" | `WhitelistServiceImpl.java:29-34` 每次直查 DB | 低频场景可接受，标注偏差 |

---

## 4. Step 3 — 可读性检查

| 结果 | 说明 |
|------|------|
| ⚠️ | A2.4 ✅ import 有序无通配；A4 ✅ 命名规范；A3 ✅ K&R/4 空格缩进。轻微问题：`EmployeeServiceImpl.java:186` 方法返回类型写全限定名 `java.util.Date`（应 import，`A2` 风格 P2）；`ImportController.java:29-31` 大量使用全限定类名内联（P2）；`AuthInterceptor.java:31` 先 `UserContext.set` 再校验白名单，拒绝路径残留上下文（见 §5 G8.5）。 |

---

## 5. Step 4 — 可靠性检查

自动化预扫（`scan-all-rules.sh`）原始命中 13 条，人工复核结论如下：

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| 可靠性 | G1–G17 | ❌ | P0 | **G8.5** `common/AuthInterceptor.java:31-35`：`preHandle` 中先 `UserContext.setCurrentEmpNo(empNo)` 再抛 `BizException(WL_001)`；Spring 在 preHandle 抛异常时**不会回调 afterCompletion**，Tomcat 线程池场景 ThreadLocal 残留（虽下次请求会覆盖，仍违反"线程池内 ThreadLocal 必须 remove"军规）。G1/G3：写操作均为单语句 upsert/insert + 唯一索引兜底，无先读后写多步事务问题，✅。G11.4 ✅ 金额全 BigDecimal。 |
| 安全 | S1–S10 | ❌ | P0 | **S9.1** `src/main/resources/application.yml`：datasource 用户名/密码 `root/root` 硬编码在仓库内（P0，应走配置中心/环境变量）。S1.1 ✅ MyBatis 全部 `#{}` 预编译、LIKE 用 CONCAT 参数化，无 `${}`。S7.1 ✅ `.xlsx` 后缀白名单 + multipart 5MB 上限。S8.2 ✅ 增删改均 POST。S8.1 ✅ /api/** 全量白名单拦截。 |
| Bug 模式 | B/M/I（120） | ⚠️ | P2 | 脚本命中 **B022×3**（`BudgetServiceImpl.java:70`、`EmployeeServiceImpl.java:191`、`ImportServiceImpl.java:210`）：均为**方法内局部变量** `new SimpleDateFormat(...)`，无线程安全问题，**误报降级为 P2**（建议改用 `java.time`，随 I004 一并处理）。**I004** `BudgetServiceImpl.java:70` 使用 `java.util.Date`，P2。脚本命中 **G16.2×9**：逐条复核——`EmployeeServiceImpl:61/163`（DuplicateKey→BizException）、`EmployeeServiceImpl:194`、`ImportServiceImpl:213`（ParseException→BizException）、`ImportServiceImpl:97/99`（重抛/已 LOGGER.error）、`ImportServiceImpl:161/163`（行级异常已落库明细+warn 日志）均为**误报**；`WhitelistServiceImpl.java:55-57` catch DuplicateKey 后 `revive` 无日志，复核为真实小问题（P2）。**G11.3**（LLM 补扫）`BudgetServiceImpl.java:43`：`PERIOD_PATTERN.matcher(req.getBudgetPeriod())` 未做 null 防御，Controller 路径有 @NotBlank 兜底，但内部调用（import 已判空除外）/未来调用方传 null 会 NPE 而非 BUD_002（P1）。 |

---

## 6. Step 5 — 自定义扩展检查

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| 自定义扩展 | `customized-checklist.md` U* | N/A | - | N/A(未启用自定义规则)，U1.1 为示例项；实测 Controller 写接口已使用 @Valid/手工校验，符合示例精神。 |

---

## 7. 结论

- **合并建议**：修复后合并（3 个 P0 均为小改动）
- **P0**：
  1. `AuthInterceptor.java:31-35` — 白名单拒绝路径 ThreadLocal 未清理（G8.5）。
  2. `application.yml` — DB 口令 `root/root` 硬编码入库（S9.1）。
  3. `EmployeeServiceImpl.java:150` — W06 出参 `recentImport` 恒为 null，功能字段未实现。
- **P1/P2**：
  1. **P1** `BudgetServiceImpl.java:43` — `budgetPeriod` 缺 null 防御（G11.3）。
  2. **P1** `EmployeeController.java:51` — 列表手机号打码逻辑恒失效，与"默认打码"设计不一致。
  3. **P2** B022×3/I004 — 局部 SimpleDateFormat + java.util.Date，建议统一 `java.time`。
  4. **P2** `WhitelistServiceImpl.java:55-57` — revive 路径无日志。
  5. **P2** 导入同文件 MD5 防重复提交未实现（设计 §5.2.3.1 约定项）。
  6. **P2** 白名单 Caffeine 缓存未实现（直查 DB，低频可接受，需确认）。
  7. **P2** `EmployeeServiceImpl.java:186` 等方法签名使用全限定类名，风格不统一。
- **一句话**：功能覆盖度高、契约与 spec 基本对齐、金额/注入/分页等关键点处理得当；3 个 P0 修复成本低，修复后可合并。

---

## 7.1 问题片段

- **P0** `G8.5` `src/main/java/com/example/employeeboard/common/AuthInterceptor.java:29-37` — 白名单拒绝时 preHandle 抛异常，afterCompletion 不回调，ThreadLocal 残留。

```java
L29|public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
L30|    String empNo = request.getHeader(HEADER_EMP_NO);
L31|    UserContext.setCurrentEmpNo(empNo);            // 问题：先 set
L32|    if (!whitelistService.isAllowed(empNo)) {
L33|        LOGGER.warn("whitelist check rejected, empNo={}", empNo);
L34|        throw new BizException("WL_001", "操作者不在白名单"); // 抛出后 afterCompletion 不执行
L35|    }
L36|    return true;
L37|}
```

- **P0** `S9.1` `src/main/resources/application.yml:4-8` — 数据库凭证硬编码。片段：`N/A(非 Java)`，配置原文：

```yaml
  datasource:
    url: jdbc:mysql://localhost:3306/employee_board?...
    username: root
    password: root
```

- **P0** `REQ-2/W06` `src/main/java/com/example/employeeboard/employee/EmployeeServiceImpl.java:145-152` — 汇总出参 recentImport 恒 null，未查最近导入批次。

```java
L145|public EmployeeSummaryDTO summary() {
L146|    EmployeeSummaryDTO dto = new EmployeeSummaryDTO();
L147|    dto.setTotalCount(employeeMapper.countActive(DEFAULT_TENANT));
L148|    dto.setDeptCount(employeeMapper.countDepts(DEFAULT_TENANT));
L149|    dto.setBudgetTotal(budgetService.sumCurrentPeriod());
L150|    dto.setRecentImport(null);  // 问题：W06 要求"最近一次导入批次摘要"，恒为 null
L151|    return dto;
L152|}
```

- **P1** `G11.3` `src/main/java/com/example/employeeboard/budget/BudgetServiceImpl.java:39-49` — budgetPeriod 未判空即进入正则匹配。

```java
L39|public boolean saveBudget(BudgetSaveReq req) {
L40|    if (employeeMapper.selectByEmpNo(DEFAULT_TENANT, req.getEmpNo()) == null) {
L41|        throw new BizException("BUD_001", "员工不存在");
L42|    }
L43|    if (!PERIOD_PATTERN.matcher(req.getBudgetPeriod()).matches()) { // budgetPeriod 为 null 时 NPE
L44|        throw new BizException("BUD_002", "预算周期格式非法，应为 yyyy-MM 或 yyyy-Qn");
L45|    }
```

- **P1** `REQ-2` `src/main/java/com/example/employeeboard/employee/EmployeeController.java:48-53` — mask 恒为 false，手机号打码永不生效。

```java
L48|@PostMapping("/pageQuery")
L49|public Result<PageResult<EmployeeDTO>> pageQuery(@Valid @RequestBody EmployeeQueryReq req) {
L50|    // 管理端接口已要求白名单，此处按需求保留按角色脱敏扩展点；当前白名单内可见明文
L51|    boolean mask = !whitelistService.isAllowed(UserContext.getCurrentEmpNo()); // 恒 false
L52|    return Result.ok(employeeService.pageQuery(req, mask));
L53|}
```

- **P2** `B022/I004` `src/main/java/com/example/employeeboard/budget/BudgetServiceImpl.java:69-72` — 局部 SimpleDateFormat 无线程安全问题（脚本误报复核），建议改 `java.time.YearMonth`。

```java
L69|public BigDecimal sumCurrentPeriod() {
L70|    String period = new SimpleDateFormat("yyyy-MM").format(new Date()); // 建议 YearMonth.now().toString()
L71|    return budgetMapper.sumByPeriod(DEFAULT_TENANT, period);
L72|}
```

- **P2** `G16.2` `src/main/java/com/example/employeeboard/whitelist/WhitelistServiceImpl.java:53-58` — 唯一索引冲突复活路径无日志。

```java
L53|try {
L54|    whitelistMapper.insert(record);
L55|} catch (org.springframework.dao.DuplicateKeyException e) {
L56|    whitelistMapper.revive(record);  // 问题：软删复活无日志，追溯困难
L57|}
L58|LOGGER.info("whitelist added, empNo={}, operator={}", empNo, createdBy);
```

---

## 8. 修复任务列表

### P0

- [ ] **P0** `common/AuthInterceptor.java:31-34` — 调整顺序：先白名单校验通过后再 `UserContext.set`；或在 preHandle 抛异常前 `UserContext.clear()`，确保拒绝路径无 ThreadLocal 残留（G8.5）。
- [ ] **P0** `src/main/resources/application.yml` — 移除硬编码 `username: root / password: root`，改为环境变量/配置中心注入（S9.1）。
- [ ] **P0** `employee/EmployeeServiceImpl.java:150` — 实现 W06 `recentImport`：查询最近一条 emp_import_task 并填充批次号/成功数/失败数。

### P1

- [ ] **P1** `budget/BudgetServiceImpl.java:43` — `saveBudget` 入口增加 `budgetPeriod`/`empNo` null 与空白防御，返回 BUD_002 而非 NPE（G11.3）。
- [ ] **P1** `employee/EmployeeController.java:51` — 按设计 §6.4.3.2 将列表手机号改为默认打码（或引入真实角色维度），消除 mask 恒 false 的死逻辑。

### P2（可选）

- [ ] **P2** `budget/BudgetServiceImpl.java:70`、`employee/EmployeeServiceImpl.java:191`、`importing/ImportServiceImpl.java:210` — SimpleDateFormat/java.util.Date 统一替换为 `java.time`（B022/I004）。
- [ ] **P2** `whitelist/WhitelistServiceImpl.java:55-57` — revive 分支补充 warn 日志（G16.2）。
- [ ] **P2** `importing/ImportServiceImpl.java` — 补同文件 MD5 30s 防重复提交提示（设计 §5.2.3.1 约定项）。
- [ ] **P2** `whitelist/WhitelistServiceImpl.java:29-34` — 确认是否按设计引入 Caffeine 60s 缓存，或更新设计文档标注偏差。
- [ ] **P2** `employee/EmployeeServiceImpl.java:186` 等 — 消除方法签名中的全限定类名，统一 import 风格（A2）。

---

> 验证说明：本节点为只读评审，未执行构建/测试（按降级协议采用静态审查 + 脚本预扫 + 人工复核）；测试文件（3 个 Service 单测类）已纳入范围检查，断言存在性由编码节点保障。
