# Code Review Report

> **Change** `组织架构管理模块（testDj）` · **分支/Commit** `AI/task-DEV-9d10e310-7901-11f1-8a9f-59ecae612580-7d70fc66-fce6-46f6-b975-497bb80d5b7c` / `main` · **日期** `2025-08-17` · **审查者** AI

> **AI**：等级 **P0 / P1 / P2**；G/S 以 checklist 行内定义为准；Bug 模式以 `bug-pattern-checklist.md` 表头为准（Blocker→P0、Major→P1、Info→P2）。**须先**运行 `scan-all-rules.sh` 并将要点并入 §5，**再**写 LLM 结论。问题须含 `path:line` 或清单 ID：可读性 `A3.4`，安全 `S1.1`，可靠性 `G16.2`，Bug 模式 `B012` / `M005` 等。**每个 ❌/⚠️ 问题在 §7 后必须附 `.java` 问题片段**（见 §7.1）。

---

## 1. 审查范围

| 项 | 值 |
|----|-----|
| `.java` 文件数 | 13 |
| 变更行数 | 约 +500 行 |

| 类/接口 | 路径 | 角色（可选） |
|---------|------|--------------|
| CorsConfig | `src/main/java/com/org/module/config/CorsConfig.java` | 跨域配置 |
| DepartmentController | `src/main/java/com/org/module/controller/DepartmentController.java` | 部门树/移动/删除接口 |
| EmployeeController | `src/main/java/com/org/module/controller/EmployeeController.java` | 员工作命周期接口 |
| EmployeeDTO | `src/main/java/com/org/module/dto/EmployeeDTO.java` | 新增员工入参 |
| ResignDTO | `src/main/java/com/org/module/dto/ResignDTO.java` | 员工离职入参 |
| TransferDTO | `src/main/java/com/org/module/dto/TransferDTO.java` | 员工调动入参 |
| Department | `src/main/java/com/org/module/entity/Department.java` | 部门实体 |
| Employee | `src/main/java/com/org/module/entity/Employee.java` | 员工实体（含乐观锁） |
| BusinessException | `src/main/java/com/org/module/exception/BusinessException.java` | 业务异常 |
| GlobalExceptionHandler | `src/main/java/com/org/module/exception/GlobalExceptionHandler.java` | 全局异常处理 |
| DepartmentServiceImpl | `src/main/java/com/org/module/service/impl/DepartmentServiceImpl.java` | 部门业务实现 |
| EmployeeServiceImpl | `src/main/java/com/org/module/service/impl/EmployeeServiceImpl.java` | 员工作命周期实现 |
| TransferServiceImpl | `src/main/java/com/org/module/service/impl/TransferServiceImpl.java` | 调动记录服务 |

---

## 2. 问题计数

| P0 | P1 | P2 |
|----|----|-----|
| 0 | 3 | 3 |

---

## 3. Step 2 — 功能（REQ）

### REQ-1: 部门树形结构查询（F01）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| GET /api/departments/tree 返回嵌套 JSON | ✅ | 需求1："查询数据库，按 parent_id 组装树形结构返回" | `DepartmentServiceImpl.java:19-36` | `buildTree` 递归组装，实现正确 |

### REQ-2: 部门层级拖拽调整（F02）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| move 接口校验循环引用 | ⚠️ | 需求1/边界场景："newParentId 不能是被移动节点的自身或其子孙节点，否则返回 400" | `DepartmentServiceImpl.java:38-53` | `isDescendant` 依赖 `path` 字段，若 `path` 为 `null` 则校验被绕过（见 §5）。 |

### REQ-3: 员工新增唯一性校验（F03）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 新增前二次校验工号/手机号 | ✅ | 需求2："接收请求时再次校验工号和手机号全局唯一" | `EmployeeServiceImpl.java:50-56` | 应用层二次校验已实现 |
| 校验所属部门ID是否合法存在 | ✅ | 需求2："校验所属部门ID是否合法存在" | `EmployeeServiceImpl.java:57-59` | 调用 `departmentService.getById` 校验 |
| 并发新增相同工号 | ⚠️ | 需求2："数据库唯一索引保底" | `EmployeeServiceImpl.java:50-67` | 存在 TOCTOU 竞态，并发时可能抛 500（见 §5） |

### REQ-4: 人员调动（F04）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 更新员工 dept_id | ✅ | 需求3："核心逻辑：更新员工的 dept_id" | `EmployeeServiceImpl.java:83-84` | 已实现 |
| 乐观锁并发控制 | ✅ | 边界场景4："后端使用乐观锁（版本号 version）" | `EmployeeServiceImpl.java:85-89` | `updateById` 返回行数判 0 抛 409，实现正确 |
| 调动记录留痕 | ✅ | 需求3："在调动记录表中写入一条历史" | `EmployeeServiceImpl.java:91-100` | 写入 `TransferRecord`，含 old/new 部门及职位 |
| 触发下游审批流更新 | ✅ | 需求3："级联处理：触发更新该员工相关的默认审批流节点" | `EmployeeServiceImpl.java:102-103` | 发布 `EmployeeTransferredEvent` 事件解耦 |

### REQ-5: 员工离职（F05）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 逻辑删除/状态更新 | ✅ | 需求4："将员工状态更新为离职（逻辑删除）" | `EmployeeServiceImpl.java:113-114` | `status=0`, `isDeleted=1` |
| 资源释放（系统账号许可） | ❌ | 需求4："资源释放：自动释放该员工占用的系统账号许可，清除系统登录权限" | `EmployeeServiceImpl.java:108-119` | **未实现**。仅打印 `log.info`，未调用权限系统或发布事件。 |

### REQ-6: 分页查询

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 支持分页 page/size | ✅ | 边界场景3："后端必须支持分页 page/size" | `EmployeeController.java:52-70` | 使用 MyBatis-Plus `Page`，并限制 `size <= 100` |

---

## 4. Step 3 — 可读性检查

> 无 Java：**N/A**。

| 结果 | 说明（违规写 Ax.x 与 `path:行`） |
|------|--------------------------------|
| ⚠️ | A7.1 — 新增公开类/方法普遍缺少 Javadoc。如 `DepartmentServiceImpl.getDepartmentTree`、`EmployeeServiceImpl.transferEmployee` 等均未添加方法注释，影响后续维护。 |
| ✅ | A3.4 — 行宽控制良好，未超 120 字符。 |
| ✅ | A2.2 — 无通配符引入。 |
| ✅ | A3.1 — K&R 大括号风格一致。 |

---

## 5. Step 4 — 可靠性检查

### 5.1 可靠性（G1–G17）

| 域 | 参考 | 结果 | 等级 | 说明（列命中 ID 或「已扫无命中」） |
|----|------|------|------|-------------------------------------|
| 可靠性 | `reliability-checklist.md` G1–G17 | ⚠️ | P1 | G16.2 — `GlobalExceptionHandler.handleBusiness` 未记录异常日志，排障时缺少上下文。预扫脚本误报 `EmployeeController.java:77`（该行实际存在 `log.warn`），但全局 handler 的日志缺失是真实问题。 |
| 可靠性 | `reliability-checklist.md` G1–G17 | ⚠️ | P1 | G11.3 — `EmployeeServiceImpl.createEmployee` 中先 `checkFieldExists` 再 `save`，在并发场景下存在 TOCTOU 竞态。虽然 DB 唯一索引兜底，但冲突时未捕获 `DataIntegrityViolationException`，会返回 500 而非友好 400。 |
| 可靠性 | `reliability-checklist.md` G1–G17 | ✅ | — | G14.3 — 不涉及多时区场景。 |
| 可靠性 | `reliability-checklist.md` G1–G17 | ✅ | — | G15.1 — 无 DDL 变更。 |

### 5.2 安全（S1–S10）

| 域 | 参考 | 结果 | 等级 | 说明（列命中 ID 或「已扫无命中」） |
|----|------|------|------|-------------------------------------|
| 安全 | `security-checklist.md` S1–S10 | ⚠️ | P1 | S10.2 — `CorsConfig.java:17` 配置 `allowedOriginPatterns("http://localhost:*")`，开发环境可用，但生产环境需收紧为精确白名单，防止开放 CORS。 |
| 安全 | `security-checklist.md` S1–S10 | ✅ | — | S1.1 — MyBatis-Plus Lambda 查询，无 SQL 注入风险。 |
| 安全 | `security-checklist.md` S1–S10 | ✅ | — | S9.1 — 无硬编码密钥。 |

### 5.3 Bug 模式（B/M/I）

> **预扫结果**：脚本命中 `G16.2 CatchWithoutLogging` @ `EmployeeController.java:77`；经人工复核，该行 catch 块内存在 `log.warn(..., e)`，为脚本误报。

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| Bug 模式 | `bug-pattern-checklist.md` B/M/I（120） | ✅ | — | B076 — `@Transactional` 均标注在 public 方法上，符合规范。 |
| Bug 模式 | `bug-pattern-checklist.md` B/M/I（120） | ✅ | — | M007 — 无空 catch 块。 |
| Bug 模式 | `bug-pattern-checklist.md` B/M/I（120） | ✅ | — | B010 — 无 `new BigDecimal(double)` 用法。 |
| Bug 模式 | `bug-pattern-checklist.md` B/M/I（120） | ✅ | — | B022 — 使用 `DateTimeFormatter` / `LocalDateTime`，无 `SimpleDateFormat` 静态共享。 |
| Bug 模式 | `bug-pattern-checklist.md` B/M/I（120） | ✅ | — | M004 — 无 `printStackTrace()`。 |

---

## 6. Step 5 — 自定义扩展检查

| 域 | 参考 | 结果 | 等级 | 说明（列命中 ID 或「未启用自定义规则」） |
|----|------|------|------|------------------------------------------|
| 自定义扩展 | `customized-checklist.md` U* | N/A | — | 未启用自定义规则。 |

---

## 7. 结论

- **合并建议**：修复后合并（fix & merge）
- **P0**：无
- **P1/P2**：见 §8 修复任务列表
- **一句话**：整体实现符合核心功能设计，乐观锁、分页、事件发布等关键机制已到位；但存在并发新增 TOCTOU 风险、CORS 配置偏松、以及员工离职资源释放未实现等问题，修复后可合并。

---

## 7.1 问题片段（必填）

### P1 — G16.2 异常日志缺失

`src/main/java/com/org/module/exception/GlobalExceptionHandler.java:14-17`

```java
L14|    @ExceptionHandler(BusinessException.class)
L15|    public Result<Void> handleBusiness(BusinessException e) {
L16|        return Result.fail(400, e.getMessage()); // 问题：未记录异常日志，排障缺少上下文
L17|    }
```

---

### P1 — 并发新增竞态（G11.3）

`src/main/java/com/org/module/service/impl/EmployeeServiceImpl.java:50-59`

```java
L50|    @Override
L51|    @Transactional(rollbackFor = Exception.class)
L52|    public void createEmployee(EmployeeDTO dto) {
L53|        if (checkFieldExists("employeeNo", dto.getEmployeeNo())) {
L54|            throw new com.org.module.exception.BusinessException("ORG_400", "工号已存在");
L55|        }
L56|        if (checkFieldExists("phone", dto.getPhone())) {
L57|            throw new com.org.module.exception.BusinessException("ORG_400", "手机号已存在");
L58|        }
L59|        if (departmentService.getById(dto.getDeptId()) == null) {
L60|            throw new com.org.module.exception.BusinessException("ORG_400", "部门不存在");
L61|        }
L62|        Employee emp = new Employee();
L63|        // ... set fields
L67|        save(emp);
L68|    }
```

**说明**：`checkFieldExists` 与 `save` 之间存在 TOCTOU 窗口。并发请求下均通过检查后，唯一索引会阻止实际写入，但异常未被捕获，导致客户端收到 500。

---

### P1 — CORS 配置过于宽松（S10.2）

`src/main/java/com/org/module/config/CorsConfig.java:16-17`

```java
L16|                registry.addMapping("/api/**")
L17|                        .allowedOriginPatterns("http://localhost:*")
```

**说明**：生产环境应配置为精确的白名单域名，避免使用通配符或 localhost 模式。

---

## 8. 修复任务列表

### P0

- 无待修复项。

### P1

- [ ] **P1** `GlobalExceptionHandler.java:15` — 在 `handleBusiness` 中添加 `log.warn` 或 `log.error` 记录异常信息，确保可观测性。
- [ ] **P1** `EmployeeServiceImpl.java:50-67` — 在 `createEmployee` 中捕获 `DataIntegrityViolationException`，将其转换为友好 400 错误（如 "工号/手机号已存在"）。
- [ ] **P1** `CorsConfig.java:17` — 将 `allowedOriginPatterns` 改为生产环境白名单配置，禁止通配符模式。

### P2（可选）

- [ ] **P2** 多处公开方法 — 为新增 Service/Controller 的公开方法补充 Javadoc（A7.1）。
- [ ] **P2** `DepartmentServiceImpl.java:76-78` — 在 `moveDepartment` 中补充 `updatedAt` 更新。
- [ ] **P2** `EmployeeServiceImpl.java:113-115` — 在 `resignEmployee` 中校验 `updateById` 返回值，处理乐观锁冲突场景。
