# Code Review Report

> **Change** `1.0 T2 — 帮助用户记录日常待办事项（仅创建）` · **分支/Commit** `AI/task-DEV-966dcd0a` / `cc2f99a0` · **日期** `2026-09-01` · **审查者** AI

---

## 1. 审查范围

| 项 | 值 |
|----|-----|
| `.java` 文件数 | `10` |
| 变更行数 | `+534 / -0`（纯新增，GlobalExceptionHandler 为 +15 修改） |

| 类/接口 | 路径 | 角色 |
|---------|------|------|
| `TodoController` | `src/main/java/com/org/module/controller/TodoController.java` | REST 控制器，POST /api/todos |
| `TodoService` | `src/main/java/com/org/module/service/TodoService.java` | 服务接口 |
| `TodoServiceImpl` | `src/main/java/com/org/module/service/impl/TodoServiceImpl.java` | 服务实现，含 createTodo 业务逻辑 |
| `TodoDTO` | `src/main/java/com/org/module/dto/TodoDTO.java` | 请求 DTO，含 Bean Validation |
| `Todo` | `src/main/java/com/org/module/entity/Todo.java` | MyBatis-Plus 实体 |
| `TodoMapper` | `src/main/java/com/org/module/mapper/TodoMapper.java` | 数据访问接口 |
| `UserContext` | `src/main/java/com/org/module/context/UserContext.java` | 登录上下文抽象 |
| `UserContextImpl` | `src/main/java/com/org/module/context/UserContextImpl.java` | 登录上下文实现，读取 X-User-Id 头 |
| `GlobalExceptionHandler` | `src/main/java/com/org/module/exception/GlobalExceptionHandler.java` | 全局异常处理器（修改：新增 MethodArgumentNotValidException 处理） |
| `TodoServiceImplTest` | `src/test/java/com/org/module/service/impl/TodoServiceImplTest.java` | 单元测试 |

**预扫工具**：`scan-all-rules.sh`（52/222 条规则自动化扫描），结果 1 项命中。

---

## 2. 问题计数

| P0 | P1 | P2 |
|----|----|-----|
| 0 | 3 | 2 |

---

## 3. Step 2 — 功能（REQ）

### REQ-1: 新增待办事项（F01/W01/S01）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 内部用户 POST /api/todos {title, description}，系统创建并落库 | ✅ | design §4.1 W01「POST /api/todos」、§5.2.3.1 F01 时序图 | `TodoController.java:35-40` → `TodoServiceImpl.java:32-49` → `save(todo)` | 时序与 design 一致：Controller→Service→Mapper→DB |
| creator_id 由登录上下文注入，前端不传 | ✅ | design §6.4.2.1「creator_id 由登录上下文注入而非前端传入」 | `TodoServiceImpl.java:36-43`，TodoDTO 无 creatorId 字段 | 防止越权创建 |
| title 非空、长度 1-200 | ⚠️ | design §5.2.2 R01 | `TodoDTO.java:14-16` @NotBlank+@Size(max=200) | 校验到位，但错误码 TODO_001 未在响应中返回（见 P2-1） |
| description 选填、长度 ≤1000 | ⚠️ | design §5.2.2 R02 | `TodoDTO.java:19` @Size(max=1000) | 校验到位，错误码 TODO_002 未在响应中返回 |
| 登录上下文缺失抛 TODO_003 不落库 | ⚠️ | design §5.2.2 R03 | `TodoServiceImpl.java:36-38`，`TodoServiceImplTest.java:82-93` | 抛出正确，但 TODO_003 错误码被 handler 丢弃（见 P2-1） |

---

## 4. Step 3 — 可读性检查

| 结果 | 说明（违规写 Ax.x 与 `path:行`） |
|------|--------------------------------|
| ⚠️ | A7.1 — `GlobalExceptionHandler.java:24` 新增 public 方法 `handleValidation` 缺少 Javadoc（与既有方法风格一致，但违反 A7.1 public 成员须有 Javadoc 规范）。其余 A1-A6 均通过：文件名=类名、UTF-8、无 Tab、K&R 大括号、4 空格缩进、行宽 ≤120、命名规范、@Override 齐全。 |

---

## 5. Step 4 — 可靠性检查

| 域 | 参考 | 结果 | 等级 | 说明（列命中 ID 或「已扫无命中」） |
|----|------|------|------|-------------------------------------|
| 可靠性 | `reliability-checklist.md` G1–G17 | ⚠️ | P1 | G16.2/G8.1 — `UserContextImpl.java:33` catch NumberFormatException 无日志（预扫确认）；G17.1 — design 推荐接口级应急开关未实现 |
| 安全 | `security-checklist.md` S1–S10 | ⚠️ | P1 | S8.1 — 接口无鉴权拦截器，X-User-Id 头由客户端控制，design A02「待确认」 |
| Bug 模式 | `bug-pattern-checklist.md` B/M/I（120） | ✅ | — | 预扫：`scan-all-rules.sh` 无 B/M/I 命中；LLM 逐条核销 81 Blocker + 27 Major + 10 Info 均无命中 |

### 预扫输出

```
[P0] G16.2 — CatchWithoutLogging: src/main/java/com/org/module/context/UserContextImpl.java:33
=== Summary: 1 findings (P0=1, P1=0, P2=0) | 52/222 rules scanned ===
```

> 注：脚本将 G16.2 标为 P0（基于 catch-without-log 的通用启发式），但按 reliability-checklist.md 原始定义，G16.2 等级为 P1（异常路径有日志输出且包含可追溯上下文）。G16.4（空 catch/关键路径吞异常，P0）不完全匹配——此处 catch 有 return 语句（非空），且非资金/关键路径。最终等级取 P1。

---

## 6. Step 5 — 自定义扩展检查

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| 自定义扩展 | `customized-checklist.md` U* | ✅ | — | U1.1（@Valid）通过；其余为示例项或空节，N/A(未启用自定义规则) |

---

## 7. 结论

- **合并建议**：修复后合并
- **P0**：无
- **P1**：
  1. `G16.2` `UserContextImpl.java:33` — catch `NumberFormatException` 后仅返回 `Optional.empty()`，无日志记录。当网关注入的 `X-User-Id` 头格式异常时，异常被静默吞掉，运维无法从日志诊断网关配置问题。
  2. `S8.1` `TodoController.java:35` / `UserContextImpl.java:27` — 接口无鉴权拦截器，`X-User-Id` 请求头直接从客户端读取。design 假设 A02（复用现有登录态校验）标注"待确认"，impl.md 确认仓库无任何鉴权拦截器。若应用未部署在受信网关之后，任意用户可伪造 `X-User-Id` 冒充他人创建待办。
  3. `G17.1` `TodoController.java` — design §7.2/7.3 推荐接口级功能开关（方案A）用于应急关闭，但代码未实现任何开关机制。异常时只能通过重新部署回滚，无法秒级止血。
- **P2**：
  1. `GlobalExceptionHandler.java:20` — `handleBusiness` 返回 `Result.fail(400, e.getMessage())`，丢弃了 `BusinessException.getCode()`（"TODO_003"）。design §5.1 定义的错误码 TODO_001/002/003 未在 API 响应中体现。
  2. `A7.1` `GlobalExceptionHandler.java:24` — 新增 public 方法 `handleValidation` 缺少 Javadoc。
- **一句话**：功能实现与系分设计基本一致，核心创建链路正确、测试覆盖合理；主要风险在于安全鉴权假设未验证和异常可观测性不足，建议合并前修复 P1 项。

---

## 7.1 问题片段（必填）

### P1-1: G16.2 — catch 异常未记录日志

- **P1** `G16.2` `src/main/java/com/org/module/context/UserContextImpl.java:33` — 捕获 `NumberFormatException` 后直接返回 `Optional.empty()`，无日志记录。当 `X-User-Id` 头格式非法（如 `"abc"`）时，异常被静默吞掉，运维无法从日志发现网关注入头异常。

片段范围：`src/main/java/com/org/module/context/UserContextImpl.java:30-36`

```java
L30|        try {
L31|            return Optional.of(Long.parseLong(header.trim()));
L32|        } catch (NumberFormatException e) {
L33|            return Optional.empty(); // 问题：吞异常且无日志
L34|        }
L35|    }
L36|}
```

### P1-2: S8.1 — 接口无鉴权，X-User-Id 可伪造

- **P1** `S8.1` `src/main/java/com/org/module/context/UserContextImpl.java:27` / `src/main/java/com/org/module/controller/TodoController.java:36` — 接口未接入鉴权拦截器，`X-User-Id` 请求头由客户端控制。design A02 标注"待确认"，impl.md 确认仓库无任何鉴权拦截器（EmployeeServiceImpl 用 0L 占位）。若应用直接暴露（未经受信网关），任意用户可伪造 `X-User-Id` 冒充他人。

片段范围：`src/main/java/com/org/module/context/UserContextImpl.java:20-30`

```java
L20|    @Override
L21|    public Optional<Long> getCurrentUserId() {
L22|        ServletRequestAttributes attributes =
L23|                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
L24|        if (attributes == null) {
L25|            return Optional.empty();
L26|        }
L27|        String header = attributes.getRequest().getHeader(USER_ID_HEADER);
L28|        if (header == null || header.isBlank()) {
L29|            return Optional.empty();
L30|        }
```

### P1-3: G17.1 — 应急开关未实现

- **P1** `G17.1` `src/main/java/com/org/module/controller/TodoController.java` — design §7.2 推荐"方案A：接口级功能开关"用于应急关闭，§7.3 可应急明确"保留接口级功能开关：异常时关闭开关，创建接口直接返回功能不可用"。代码未实现任何开关机制，异常时只能重新部署。

片段范围：`src/main/java/com/org/module/controller/TodoController.java:35-41`

```java
L35|    @PostMapping
L36|    public Result<Void> create(@RequestBody @Valid TodoDTO dto) {
L37|        // 日志仅打印 title，避免打印长描述
L38|        log.info("新增待办事项: title={}", dto.getTitle());
L39|        todoService.createTodo(dto);
L40|        return Result.ok();
L41|    }
```

### P2-1: 错误码 TODO_xxx 未在响应中返回

- **P2** `GlobalExceptionHandler.java:20` — `handleBusiness` 使用 `Result.fail(400, e.getMessage())`，丢弃了 `BusinessException.getCode()`（值为 "TODO_003"）。design §5.1 定义错误码格式为 `{MODULE}_{SEQ}`（如 TODO_001/002/003），但 API 响应 code 字段返回 400 而非 TODO_xxx。

片段范围：`src/main/java/com/org/module/exception/GlobalExceptionHandler.java:18-21`

```java
L18|    @ExceptionHandler(BusinessException.class)
L19|    public Result<Void> handleBusiness(BusinessException e) {
L20|        return Result.fail(400, e.getMessage()); // 问题：未使用 e.getCode()
L21|    }
```

### P2-2: A7.1 — 新增 public 方法缺少 Javadoc

- **P2** `A7.1` `src/main/java/com/org/module/exception/GlobalExceptionHandler.java:24` — 新增 public 方法 `handleValidation` 无 Javadoc（与既有方法风格一致，但违反 A7.1）。

片段范围：`src/main/java/com/org/module/exception/GlobalExceptionHandler.java:23-32`

```java
L23|    @ExceptionHandler(MethodArgumentNotValidException.class)
L24|    public Result<Void> handleValidation(MethodArgumentNotValidException e) {
L25|        String msg = e.getBindingResult().getFieldErrors().stream()
L26|                .map(FieldError::getDefaultMessage)
L27|                .collect(Collectors.joining("; "));
L28|        if (msg.isBlank()) {
L29|            msg = "参数校验失败";
L30|        }
L31|        return Result.fail(400, msg);
L32|    }
```

---

## 8. 修复任务列表

### P0

- 无待修复项。

### P1

- [ ] **P1** `src/main/java/com/org/module/context/UserContextImpl.java:33` — 在 `catch (NumberFormatException e)` 块中添加 `log.warn("X-User-Id 头格式非法: {}", header, e)` 日志记录，便于运维诊断网关注入头异常
- [ ] **P1** `S8.1` `src/main/java/com/org/module/controller/TodoController.java` — 确认应用部署在受信网关之后（网关完成登录态校验后注入 X-User-Id 并剥离客户端伪造的同名头），或在应用层补充鉴权拦截器；将 design A02 从"待确认"推进为"已确认"
- [ ] **P1** `G17.1` `src/main/java/com/org/module/controller/TodoController.java` — 实现接口级功能开关（如 `@ConditionalOnProperty` 或运行时配置开关），异常时可不重启关闭创建接口

### P2（可选）

- [ ] **P2** `src/main/java/com/org/module/exception/GlobalExceptionHandler.java:20` — 在 `handleBusiness` 中将 `BusinessException.getCode()` 纳入响应（如 `Result.fail(400, e.getCode() + ": " + e.getMessage())` 或扩展 Result 支持 String code），使 TODO_003 错误码可在响应中体现
- [ ] **P2** `src/main/java/com/org/module/exception/GlobalExceptionHandler.java:24` — 为 `handleValidation` 方法补充 Javadoc
