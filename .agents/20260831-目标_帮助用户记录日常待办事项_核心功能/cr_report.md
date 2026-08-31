# Code Review Report

> **Change** `待办事项记录-核心功能（新增待办事项）` · **分支/Commit** `AI/task-DEV-966dcd0a-7905-11f1-9649-3b4281182f10-7f12c115-1b28-4216-9ebf-adee88e8e9f8` / `HEAD` · **日期** `2026-08-31` · **审查者** AI
>
> 审查依据：`.agents/20260831-目标_帮助用户记录日常待办事项_核心功能/design.md`
> 自动化预扫：已执行 `references/script/scan-all-rules.sh src/main/java/com/antdigital/todo src/test/java/com/antdigital/todo`，输出并入 §5。

---

## 1. 审查范围

| 项 | 值 |
|----|-----|
| `.java` 文件数 | 14 |
| 变更行数 | `+1341 / -0`（22 文件，其中 14 个 .java） |

| 类/接口 | 路径 | 角色 |
|---------|------|--------------|
| TodoApplication | `src/main/java/com/antdigital/todo/TodoApplication.java` | 启动类 |
| TodoConstants | `src/main/java/com/antdigital/todo/common/constant/TodoConstants.java` | 常量 |
| BusinessException | `src/main/java/com/antdigital/todo/common/exception/BusinessException.java` | 业务异常 |
| GlobalExceptionHandler | `src/main/java/com/antdigital/todo/common/exception/GlobalExceptionHandler.java` | 全局异常处理 |
| ApiResponse | `src/main/java/com/antdigital/todo/common/model/ApiResponse.java` | 通用出参 |
| TodoController | `src/main/java/com/antdigital/todo/todo/controller/TodoController.java` | 控制器 |
| TodoErrorCodeEnum | `src/main/java/com/antdigital/todo/todo/enums/TodoErrorCodeEnum.java` | 错误码枚举 |
| TodoCreateRequest | `src/main/java/com/antdigital/todo/todo/model/dto/TodoCreateRequest.java` | 请求 DTO |
| TodoCreateResult | `src/main/java/com/antdigital/todo/todo/model/dto/TodoCreateResult.java` | 结果 DTO |
| TodoDO | `src/main/java/com/antdigital/todo/todo/model/entity/TodoDO.java` | JPA 实体 |
| TodoRepository | `src/main/java/com/antdigital/todo/todo/repository/TodoRepository.java` | 数据访问 |
| TodoService | `src/main/java/com/antdigital/todo/todo/service/TodoService.java` | 服务接口 |
| TodoServiceImpl | `src/main/java/com/antdigital/todo/todo/service/impl/TodoServiceImpl.java` | 服务实现 |
| TodoServiceImplTest | `src/test/java/com/antdigital/todo/todo/service/impl/TodoServiceImplTest.java` | 单元测试 |

---

## 2. 问题计数

| P0 | P1 | P2 |
|----|----|-----|
| 1 | 1 | 4 |

---

## 3. Step 2 — 功能（REQ）

> REQ 来源：design.md §1.2（FR-TODO-01）、§1.5（F01）、§5.2.3（W01 接口/错误码）、§5.2.4.1（R01/R02/R03）。

### REQ-1: 新增待办事项（名称必填、描述选填、落库返回 ID）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| POST /api/todos 路由 | ✅ | §4.1 W01「POST /api/todos」 | `TodoController.java:20,35` | `@RequestMapping("/api/todos")` + `@PostMapping` |
| name 必填、非空白（R01） | ❌ | §5.2.4.1 R01「name 必填，去除首尾空白后非空 → TODO_0001」 | `TodoCreateRequest.java:17` `@NotBlank` + `GlobalExceptionHandler.java:45-53` | **见 P0-1**：name 为空白时由 `@NotBlank` 触发 `MethodArgumentNotValidException`，被 `handleValidationException` 统一返回 `TODO_0004「请求参数格式错误」`，而非 spec 规定的 `TODO_0001`。Service 层 `validate()` 的 `TODO_0001` 分支（`TodoServiceImpl.java:71-74`）经 API 不可达。 |
| name 长度 ≤ 100（R02） | ❌ | §5.2.4.1 R02「name 长度 ≤ 100 → TODO_0002」 | `TodoCreateRequest.java:18` `@Size(max=100)` + `GlobalExceptionHandler.java:45-53` | name 超长时返回 `TODO_0004` 而非 `TODO_0002`；Service `validate()` 的 `TODO_0002` 分支（`TodoServiceImpl.java:75-78`）经 API 不可达。 |
| description 长度 ≤ 500（R03） | ❌ | §5.2.4.1 R03「description 长度 ≤ 500 → TODO_0003」 | `TodoCreateRequest.java:22` `@Size(max=500)` + `GlobalExceptionHandler.java:45-53` | description 超长时返回 `TODO_0004` 而非 `TODO_0003`；Service `validate()` 的 `TODO_0003` 分支（`TodoServiceImpl.java:80-83`）经 API 不可达。 |
| description 选填、可空 | ✅ | §5.2.3 入参「description 否」 | `TodoServiceImpl.java:44` + `TodoDO.java:35` | description 为 null 时存 NULL |
| tenant_id 注入默认 default | ✅ | §5.2.4.1「注入 tenant_id 默认 default」/ A04 | `TodoServiceImpl.java:45` | 硬编码 `DEFAULT_TENANT_ID`，符合 A04 单租户假设 |
| 返回 data.id | ✅ | §5.2.3 出参「data.id Long」 | `TodoController.java:38` + `TodoServiceImpl.java:58` | `TodoCreateResult(saved.getId())` |
| 通用出参 {result,msg,data} | ✅ | §5.1 通用出参结构 | `ApiResponse.java:31-53` | success/error 结构正确 |
| DB 异常返回 TODO_0004 | ✅ | §5.2.4.1 异常场景「数据库失败 → TODO_0004」 | `TodoServiceImpl.java:51-54` | catch DataAccessException → log + 抛 SYSTEM_ERROR |
| 错误码取值 TODO_0001-0004 | ✅ | §5.1/§5.2.3 错误码表 | `TodoErrorCodeEnum.java:12-21` | 四个码定义与 spec 一致 |

**REQ-1 结论**：核心创建链路功能正常，但 **错误码契约不一致（R01/R02/R03 对应的 TODO_0001/0002/0003 经 API 不可达）**，详见 P0-1。

---

## 4. Step 3 — 可读性检查

> 对照 `readability-checklist.md` A1–A7 逐节扫描 14 个 .java 文件。

| 结果 | 说明（违规写 Ax.x 与 `path:行`） |
|------|--------------------------------|
| ✅ | A1 源文件格式：文件名=类名、UTF-8、统一空格缩进无 Tab，全部符合。 |
| ✅ | A2 源文件结构：package→import→单一顶层类；无 `import *` 通配符；import 按完整名有序（见 `GlobalExceptionHandler.java:3-13`、`TodoServiceImpl.java:3-16`）。 |
| ✅ | A3 代码样式：K&R 大括号、4 空格缩进、行宽 ≤ 120、类成员间空行，均符合。 |
| ✅ | A4 命名：包名全小写、类 UpperCamelCase、方法 lowerCamelCase、常量 UPPER_SNAKE_CASE（`TodoConstants.java:12-18`），符合。 |
| ✅ | A5 编码实践：重写方法均加 `@Override`（`TodoServiceImpl.java:35`）；无空 catch；无 `finalize()` 重写。 |
| ✅ | A6 特定元素：数组类型前置；无 switch fall-through；注解每行一个；无 long 字面量小写 `l`。 |
| ✅ | A7 Javadoc：public 类与 public/protected 方法均有 Javadoc 且含 `@author/@date`，块标记顺序正确（`TodoService.java:14-20`）。 |

---

## 5. Step 4 — 可靠性检查

**自动化预扫输出（scan-all-rules.sh，52/222 条规则）：**
```
[P0] G16.2 — CatchWithoutLogging: src/main/java/com/antdigital/todo/todo/service/impl/TodoServiceImpl.java:51
[P1] M016 — JavaTimeDefaultTimeZone: src/main/java/com/antdigital/todo/todo/model/entity/TodoDO.java:52
[P1] M016 — JavaTimeDefaultTimeZone: src/main/java/com/antdigital/todo/todo/model/entity/TodoDO.java:59
=== Summary: 3 findings (P0=1, P1=2, P2=0) | 52/222 rules scanned ===
```

**预扫复核：**
- `TodoServiceImpl.java:51` 的 G16.2 (CatchWithoutLogging) 为 **脚本误报**：该 catch 块在 `TodoServiceImpl.java:52` 已执行 `logger.error(..., ex)` 记录异常与堆栈并重抛 `BusinessException`，非空 catch、非吞异常，复核为 ✅。
- `TodoDO.java:52,59` 的 M016 (JavaTimeDefaultTimeZone) 为 **真实命中**，见 P1-1。

| 域 | 参考 | 结果 | 等级 | 说明（列命中 ID 或「已扫无命中」） |
|----|------|------|------|-------------------------------------|
| 可靠性 | `reliability-checklist.md` G1–G17 | ⚠️ | P1 | G8.3/G8.1/G16 已扫无命中；G14.3 命中（见 P1-1，与 M016 同源）；G1/G2/G3/G5/G6/G7/G9/G12/G15 N/A（本期无并发/MQ/缓存/调度/RPC/资损/灰度兼容问题，新表新建）；G17 可应急：开关 `todo.create.enabled` 已预留于 `application.yml:27-29`，但**无代码读取该开关**（P2-3）。 |
| 安全 | `security-checklist.md` S1–S10 | ⚠️ | P1 | S1.1 N/A（JPA 参数化，无拼接 SQL）；S8.1 鉴权 N/A（设计明确排除登录，假设外部统一鉴权，A03 待确认），但设计 §6.4.2 声明的登录态拦截器未实现（P2-4，参考）；其余 S2–S7、S9–S10 与本期无关 N/A。 |
| Bug 模式 | `bug-pattern-checklist.md` B/M/I（120） | ⚠️ | P1 | 预扫 M016 命中（TodoDO.java:52,59）；B* 与其余 M*/I* 已扫无命中；实体 `equals/hashCode` 基于 id（`TodoDO.java:111-125`）属常见 JPA 模式，未触发 Blocker。 |

---

## 6. Step 5 — 自定义扩展检查

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| 自定义扩展 | `customized-checklist.md` U* | N/A | — | N/A(未启用自定义规则)：`customized-checklist.md` 仅有示例项 U1.1（Controller 入参使用统一校验注解 `@Valid`），实际代码 `TodoController.java:36` 已使用 `@Valid`，符合示例项，无命中。 |

---

## 7. 结论

- **合并建议**：修复后合并
- **P0**：
  1. **错误码契约不一致**：`TodoCreateRequest` 的 `@NotBlank`/`@Size` 在 Controller 层触发 `MethodArgumentNotValidException`，被 `GlobalExceptionHandler.handleValidationException` 统一返回 `TODO_0004「请求参数格式错误」`，导致 spec §5.2.3 定义的 `TODO_0001`（name 空白）/`TODO_0002`（name 超长）/`TODO_0003`（description 超长）经 POST /api/todos 不可达，Service 层 `validate()` 的对应分支成为 API 不可达代码。错误码契约与 spec 不符。
- **P1/P2**：
  1. **P1** `M016`/`G14.3`：`TodoDO.java:52,59` 使用 `LocalDateTime.now()` 取系统默认时区，未显式指定时区/UTC，跨区部署或时区漂移时时间不一致；建议 `LocalDateTime.now(ZoneOffset.UTC)` 或 `Instant`。
  2. **P2**：设计 §1.3 约束「事务隔离级别 ReadCommitted」未在 `application.yml` 配置 `default-transaction-isolation`，MySQL InnoDB 默认为 REPEATABLE READ。
  3. **P2**：设计 §6.3 限流「单用户 5 req/s」未实现（可由网关/SLB 承担，建议确认落地位置）。
  4. **P2**：设计 §7.3 应急开关 `todo.create.enabled` 已配置但无代码读取，创建入口未真正受开关控制。
- **一句话**：核心创建链路与表结构、出参、单测质量良好，主要风险是错误码契约被 `@Valid` 提前拦截破坏（P0）与时间写入默认时区（P1），修复后可合并。

---

## 7.1 问题片段（必填）

### P0-1 · 错误码契约不一致（TODO_0001/0002/0003 经 API 不可达）

- **P0** `REQ-1/R01-R03` `src/main/java/com/antdigital/todo/todo/model/dto/TodoCreateRequest.java:17-23` + `src/main/java/com/antdigital/todo/common/exception/GlobalExceptionHandler.java:45-53` — DTO 校验注解在 Controller 层触发 `MethodArgumentNotValidException`，被统一映射为 `TODO_0004`，覆盖了 spec 定义的业务错误码。
  片段范围：`src/main/java/com/antdigital/todo/todo/model/dto/TodoCreateRequest.java:16-23`

```java
L16|    /** 事项名称，必填，≤100 字符，不可全空白 */
L17|    @NotBlank(message = "事项名称不可为空")
L18|    @Size(max = 100, message = "事项名称不可超过100字符")
L19|    private String name;
L20|
L21|    /** 事项描述，选填，≤500 字符 */
L22|    @Size(max = 500, message = "事项描述不可超过500字符")
L23|    private String description;
```

  关联处理器片段：`src/main/java/com/antdigital/todo/common/exception/GlobalExceptionHandler.java:45-53`

```java
L45|    @ExceptionHandler(MethodArgumentNotValidException.class)
L46|    @ResponseStatus(HttpStatus.OK)
L47|    public ApiResponse<Void> handleValidationException(MethodArgumentNotValidException ex) {
L48|        String detail = ex.getBindingResult().getFieldErrors().stream()
L49|                .map(fieldError -> fieldError.getField() + " " + fieldError.getDefaultMessage())
L50|                .collect(Collectors.joining("; "));
L51|        logger.warn("参数校验失败: {}", detail);
L52|        return ApiResponse.error(TodoErrorCodeEnum.SYSTEM_ERROR.getErrorCode(), "请求参数格式错误");
L53|    }
```

  > 说明：当 name 为空白/超长、description 超长时，`@Valid` 先于 Service 生效，返回 `TODO_0004`；而 `TodoServiceImpl.validate()`（`TodoServiceImpl.java:69-84`）抛出的 `TODO_0001/0002/0003` 仅在直接调用 Service 时可达，经 API 不可达。

### P1-1 · 时间写入使用默认时区（M016 / G14.3）

- **P1** `M016` `src/main/java/com/antdigital/todo/todo/model/entity/TodoDO.java:52` 及 `:59` — `LocalDateTime.now()` 依赖 JVM 默认时区，未显式指定 UTC。
  片段范围：`src/main/java/com/antdigital/todo/todo/model/entity/TodoDO.java:50-60`

```java
L50|    @PrePersist
L51|    void prePersist() {
L52|        LocalDateTime now = LocalDateTime.now();
L53|        this.gmtCreate = now;
L54|        this.gmtModified = now;
L55|    }
L56|
L57|    @PreUpdate
L58|    void preUpdate() {
L59|        this.gmtModified = LocalDateTime.now();
L60|    }
```

---

## 8. 修复任务列表

### P0

- [ ] **P0** `src/main/java/com/antdigital/todo/todo/model/dto/TodoCreateRequest.java:17-23` + `src/main/java/com/antdigital/todo/common/exception/GlobalExceptionHandler.java:45-53` — 使 spec 错误码 TODO_0001/0002/0003 经 API 可达：移除 DTO 上与 Service `validate()` 重复的 `@NotBlank`/`@Size` 约束（改为在 Service 抛业务异常），或在 `handleValidationException` 中按字段错误映射回 TODO_0001/0002/0003，保证客户端收到 spec 定义的错误码。

### P1

- [ ] **P1** `src/main/java/com/antdigital/todo/todo/model/entity/TodoDO.java:52,59` — 将 `LocalDateTime.now()` 改为 `LocalDateTime.now(ZoneOffset.UTC)`（或改用 `Instant`），显式指定时区，满足 G14.3「应用 Instant/UTC 存库」。

### P2（可选）

- [ ] **P2** `src/main/resources/application.yml` — 显式配置事务隔离级别为 READ_COMMITTED，对齐设计 §1.3 约束（`spring.datasource.hikari.transaction-isolation` 或连接参数 `transactionIsolation=READ_COMMITTED`）。
- [ ] **P2** `src/main/resources/application.yml:27-29` — 读取并生效应急开关 `todo.create.enabled`（在 Controller/Service 判断关闭时返回 TODO_0004 或禁用入口），落实设计 §7.3 可应急。
- [ ] **P2** 限流（设计 §6.3「单用户 5 req/s」）— 确认落地位置（网关/SLB 或 Sentinel），若需后端实现则补充。
