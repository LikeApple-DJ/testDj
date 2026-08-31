# Code Review Report

> **Change** `待办事项记录（创建）核心功能` · **分支/Commit** `AI/task-DEV-966dcd0a-7905-11f1-9649-3b4281182f10-4419f833-23d3-4e73-ae46-d7f04ff20392` / `a88867d9` · **日期** `2026-08-31` · **审查者** AI
>
> 技能：`dtazziboot-java-code-review`（SDD 范式）。**先**运行 `scan-all-rules.sh`（52/222 条可程序化规则），**再**由 LLM 完成 Step 2–5 中脚本未覆盖项及误报复核。

---

## 1. 审查范围

| 项 | 值 |
|----|-----|
| `.java` 文件数 | 17 |
| 变更行数 | `git diff` 为空（变更已提交于 `a88867d9`），按 impl.md 实现清单确定范围；新增文件，未统计 +/- |

| 类/接口 | 路径 | 角色（可选） |
|---------|------|--------------|
| TodoApplication | `src/main/java/com/example/todo/TodoApplication.java` | 启动类 |
| WebConfig | `src/main/java/com/example/todo/config/WebConfig.java` | CORS 配置 |
| UserContext | `src/main/java/com/example/todo/context/UserContext.java` | 用户上下文接口 |
| DefaultUserContext | `src/main/java/com/example/todo/context/DefaultUserContext.java` | X-User-Id 读取实现 |
| TodoController | `src/main/java/com/example/todo/controller/TodoController.java` | W01 POST /api/todo 入口 |
| ApiResponse | `src/main/java/com/example/todo/dto/ApiResponse.java` | 通用出参 {result,msg,data} |
| TodoCreateRequest | `src/main/java/com/example/todo/dto/TodoCreateRequest.java` | 入参 + 校验注解 |
| TodoCreateResult | `src/main/java/com/example/todo/dto/TodoCreateResult.java` | 出参 data |
| GlobalExceptionHandler | `src/main/java/com/example/todo/exception/GlobalExceptionHandler.java` | 异常→错误码映射 |
| TodoErrorCode | `src/main/java/com/example/todo/exception/TodoErrorCode.java` | 错误码枚举 |
| TodoException | `src/main/java/com/example/todo/exception/TodoException.java` | 业务异常 |
| Todo | `src/main/java/com/example/todo/model/Todo.java` | JPA 实体/表映射 |
| TodoRepository | `src/main/java/com/example/todo/repository/TodoRepository.java` | 数据访问层 |
| TodoService | `src/main/java/com/example/todo/service/TodoService.java` | S01 服务接口 |
| TodoServiceImpl | `src/main/java/com/example/todo/service/impl/TodoServiceImpl.java` | 落库/组装实现 |
| TodoControllerTest | `src/test/java/com/example/todo/controller/TodoControllerTest.java` | 切片测试 |
| TodoServiceImplTest | `src/test/java/com/example/todo/service/impl/TodoServiceImplTest.java` | 单元测试 |

非 Java：`pom.xml`、`application.yml`、`application-test.yml`（Step3–5 逐文件 Java 规则跳过；`application.yml` 在 §5 安全/可靠性维度单独核销）。

---

## 2. 问题计数

| P0 | P1 | P2 |
|----|----|-----|
| 1 | 5 | 6 |

> **blocker_count (P0) = 1**

---

## 3. Step 2 — 功能（REQ）

### REQ-1: 新增待办事项（F01/W01）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 合法 name+description 提交 → 落库返回 id+name+description+creatorId+gmtCreate | ✅ | design §5.2.2 W01、§5.2.3.1 F01 | `TodoController.java:34-37`、`TodoServiceImpl.java:44-65`、测试 `should_returnCreateResult_when_requestIsValid` | 契约对齐 |

### REQ-2/3/4: 参数校验 R01/R02/R03

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| name 空白→TODO_0001；name>200→TODO_0002；description>2000→TODO_0003 | ✅ | design §5.2.3.1 R01/R02/R03 | `TodoCreateRequest.java:16-21`、`GlobalExceptionHandler.java:99-118`、`TodoControllerTest` 三个用例 | 错误码映射精确 |

### REQ-5: 落库失败→TODO_0004 + ERROR 日志

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| DataAccessException → TODO_0004 + ERROR 含堆栈 | ✅ | design §5.2.3.1 异常场景 | `TodoServiceImpl.java:55-61`、测试 `should_throwSystemError_when_repositoryThrowsDataAccessException` | — |

### REQ-6: creator_id 取登录态

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| creatorId 从登录态获取 | ⚠️ | design A01/R04 | `DefaultUserContext.java:20-34`、`TodoServiceImpl.java:49` | 实现读取 `X-User-Id` 头，但**未接入鉴权拦截器**，该头可伪造（见 §5 S8.1） |

### REQ-7: tenant_id 默认 0、时间字段

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| tenantId=0、gmt_create/gmt_modified 写入 | ✅ | design §5.2.1.1、A02 | `Todo.java:44-53`、`TodoServiceImpl.java:50-52`、测试 `should_persistTodoWithCorrectFields_when_requestIsValid` | — |

### REQ-8: 出参 gmtCreate 格式

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| data.gmtCreate 为 `yyyy-MM-dd HH:mm:ss` | ⚠️ | design §5.2.2 出参："data.gmtCreate \| DateTime \| 创建时间（yyyy-MM-dd HH:mm:ss）" | `TodoCreateResult.java:26`、`application.yml:19-20` Jackson | 直接返回 `LocalDateTime` 无 `@JsonFormat`，默认序列化为 ISO `2026-08-31T10:20:30`，与契约示例 `2026-08-31 10:20:30` 不符。测试未断言日期格式。**P1** |

---

## 4. Step 3 — 可读性检查

| 结果 | 说明（违规写 Ax.x 与 `path:行`） |
|------|--------------------------------|
| ⚠️ | `A2.4`：`service/impl/TodoServiceImpl.java:11-17` 中 `java.time.LocalDateTime` 排在 `org.springframework.*` 之后，非严格 ASCII 字典序（应为 com → java → org）。P2。整体命名/样式/Javadoc 规范良好。 |

---

## 5. Step 4 — 可靠性检查

> 预扫 `scan-all-rules.sh` 输出 3 条：
> - `G16.2 CatchWithoutLogging: DefaultUserContext.java:31` → 复核**成立**（P1）
> - `G16.2 CatchWithoutLogging: TodoServiceImpl.java:57` → 复核**误报**（line 58-59 已 `logger.error(...,e)`）
> - `M016 JavaTimeDefaultTimeZone: TodoServiceImpl.java:45` → 复核**成立**（Major→P1）

| 域 | 参考 | 结果 | 等级 | 说明（列命中 ID 或「已扫无命中」） |
|----|------|------|------|-------------------------------------|
| 可靠性 | `reliability-checklist.md` G1–G17 | ⚠️ | P0–P2 | 命中 `G16.2`（DefaultUserContext.java:31，P1）、`G14.4`（时间未显式时区，P1）、`G16.3`/`G13.1`（SYSTEM_ERROR 路径 WARN，P2）、`G15.1`（ddl-auto:update，P2）、`G17.1`（应急开关未实现，P1）、`G11.2`（desc 空串边界未测，P2）；其余 G 类 N/A 或 ✅ |
| 安全 | `security-checklist.md` S1–S10 | ❌ | P0–P1 | 命中 `S8.1`（未鉴权 + creatorId 可伪造，**P0**）、`S9.1`（DB 口令硬编码，P1）、`S10.2`（CORS allowedHeaders `*` + credentials，P2）；SQL 注入/反序列化/XSS 等无命中 |
| Bug 模式 | `bug-pattern-checklist.md` B/M/I（120） | ⚠️ | P0–P2 | 预扫无 B* 命中；命中 `M016`（LocalDateTime.now() 默认时区，P1）、`I` 类 JPA 实体 equals 基于 id 的 null-id 风险（P2）；其余逐条 N/A |

---

## 6. Step 5 — 自定义扩展检查

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| 自定义扩展 | `customized-checklist.md` U* | N/A | — | `N/A(未启用自定义规则)`；仅示例项 U1.1 顺带核销（Controller 入参已用 `@Valid`，符合） |

---

## 7. 结论

- **合并建议**：**修复后合并**（存在 1 个 P0 阻塞项）
- **P0**：
  1. `S8.1` `src/main/java/com/example/todo/context/DefaultUserContext.java:25` + `src/main/java/com/example/todo/controller/TodoController.java:34` — `POST /api/todo` 未接入鉴权拦截器，`creatorId` 取自客户端可控的 `X-User-Id` 请求头，可被任意伪造。design §6.4.2/A05 假设由"全局拦截器统一校验"，但本期代码未实现该拦截器，上线前必须接入鉴权并确保 `X-User-Id` 由可信登录态写入、不被前端直传。
- **P1/P2**：
  1. `S9.1` `application.yml:9-10` — DB 账号口令 `todo/todo` 硬编码，建议改为环境变量/配置中心注入。
  2. `REQ-8` `dto/TodoCreateResult.java:26` — gmtCreate 未加 `@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")`，响应格式与契约不符。
  3. `G16.2` `context/DefaultUserContext.java:29-33` — `catch (NumberFormatException e){ return null; }` 静默吞异常，建议至少 `logger.debug` 记录非法 header。
  4. `M016`/`G14.4` `service/impl/TodoServiceImpl.java:45` — `LocalDateTime.now()` 依赖系统默认时区，建议显式指定 `ZoneId`（单区内可接受，但需明确）。
  5. `G17.1` — design §7.3 设计的应急开关 `todo.create.enabled` 未实现，无法紧急关闭入口。
  6. `G13.1`/`G16.3` `exception/GlobalExceptionHandler.java:75-77` — `handleBiz` 对 `SYSTEM_ERROR`（落库失败）打 WARN，与"系统异常 ERROR"不一致且与 service 的 ERROR 重复。
  7. `A2.4` `service/impl/TodoServiceImpl.java:11-17` — import 顺序非严格 ASCII 字典序。
  8. `G15.1` `application.yml:13` — `ddl-auto: update` 生产不建议，建议 `validate` + 显式迁移脚本。
  9. `S10.2` `config/WebConfig.java:18` — CORS `allowedHeaders("*")` + `allowCredentials(true)`，生产应收敛 header 白名单。
  10. Bug/I 类 `model/Todo.java:124-139` — JPA 实体 `equals/hashCode` 仅基于 `id`，未持久化实体 null-id 相等风险（低风险）。
  11. `G11.2` — 未覆盖 description=空字符串落库边界用例。
- **一句话**：功能与契约整体对齐、测试覆盖核心路径，但存在 1 个鉴权缺失的安全阻塞项与若干可靠性/契约格式隐患，修复 P0 后可合并。

---

## 7.1 问题片段（必填）

### P0 — `S8.1` 鉴权缺失 + creatorId 可伪造

- **P0** `S8.1` `src/main/java/com/example/todo/context/DefaultUserContext.java:20-34`（+ `controller/TodoController.java:34-37`）— 接口未接入鉴权拦截器，`creatorId` 直接取自客户端可控的 `X-User-Id` 头，无任何校验，可被任意伪造。
  片段范围：`src/main/java/com/example/todo/context/DefaultUserContext.java:20-34`

```java
L20|    @Override
L21|    public Long getCurrentUserId() {
L22|        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
L23|        if (!(attributes instanceof ServletRequestAttributes servletAttributes)) {
L24|            return null;
L25|        }
L26|        String header = servletAttributes.getRequest().getHeader(USER_ID_HEADER);
L27|        if (header == null || header.isBlank()) {
L28|            return null;
L29|        }
L30|        try {
L31|            return Long.valueOf(header.trim());
L32|        } catch (NumberFormatException e) {
L33|            return null; // 问题：客户端可控的 X-User-Id 被直接信任为 creatorId，且无鉴权拦截器校验登录态
L34|        }
L35|    }
```

### P1 — `S9.1` DB 口令硬编码

- **P1** `S9.1` `src/main/resources/application.yml:7-11` — 数据库账号口令 `todo/todo` 硬编码于配置文件，未从环境变量/配置中心获取。
  片段范围：`src/main/resources/application.yml:7-11`

```yaml
L7 |    datasource:
L8 |      url: jdbc:postgresql://localhost:5432/todo_db
L9 |      username: todo
L10|      password: todo
L11|      driver-class-name: org.postgresql.Driver
```

> 注：`application.yml` 为非 Java 文件，按硬性约束（仅评审产出文档，不改代码）仅在此记录问题，代码片段以配置文件形式呈现。

### P1 — `REQ-8` gmtCreate 响应格式不符契约

- **P1** `REQ-8` `src/main/java/com/example/todo/dto/TodoCreateResult.java:25-26` — `gmtCreate` 为 `LocalDateTime` 且无 `@JsonFormat`，默认 Jackson 序列化为 ISO `2026-08-31T10:20:30`，与 design §5.2.2 要求的 `yyyy-MM-dd HH:mm:ss` 不符。
  片段范围：`src/main/java/com/example/todo/dto/TodoCreateResult.java:25-26`

```java
L25|    /** 创建时间 */
L26|    private LocalDateTime gmtCreate;
```

### P1 — `G16.2` 静默吞异常

- **P1** `G16.2` `src/main/java/com/example/todo/context/DefaultUserContext.java:29-34` — `catch (NumberFormatException e){ return null; }` 未记录日志，异常路径无可观测性。
  片段范围：`src/main/java/com/example/todo/context/DefaultUserContext.java:29-34`

```java
L29|        try {
L30|            return Long.valueOf(header.trim());
L31|        } catch (NumberFormatException e) {
L32|            return null; // 问题：吞异常且无日志，线上非法 X-User-Id 无法排查
L33|        }
L34|    }
```

> 预扫对 `TodoServiceImpl.java:57` 报同条 `G16.2`，经复核为**误报**：line 58-59 紧接 `logger.error("待办事项落库失败, ...", ..., e)`，已记录日志。

### P1 — `M016`/`G14.4` 时间默认时区

- **P1** `M016` `src/main/java/com/example/todo/service/impl/TodoServiceImpl.java:44-52` — `LocalDateTime.now()` 依赖系统默认时区，应显式指定 `ZoneId`。
  片段范围：`src/main/java/com/example/todo/service/impl/TodoServiceImpl.java:44-52`

```java
L44|    public TodoCreateResult createTodo(TodoCreateRequest request) {
L45|        LocalDateTime now = LocalDateTime.now(); // 问题：使用系统默认时区
L46|        Todo todo = new Todo();
L47|        todo.setName(request.getName());
L48|        todo.setDescription(request.getDescription());
L49|        todo.setCreatorId(userContext.getCurrentUserId());
L50|        todo.setTenantId(DEFAULT_TENANT_ID);
L51|        todo.setGmtCreate(now);
L52|        todo.setGmtModified(now);
```

### P2 — `G13.1`/`G16.3` SYSTEM_ERROR 日志级别错配

- **P2** `G16.3` `src/main/java/com/example/todo/exception/GlobalExceptionHandler.java:74-78` — `handleBiz` 对所有 `TodoException`（含 `SYSTEM_ERROR` 落库失败）打 WARN，系统异常应为 ERROR；service 已在 line 58 打 ERROR，存在重复且级别错配。
  片段范围：`src/main/java/com/example/todo/exception/GlobalExceptionHandler.java:74-78`

```java
L74|    @ExceptionHandler(TodoException.class)
L75|    public ResponseEntity<ApiResponse<Object>> handleBiz(TodoException ex) {
L76|        logger.warn("业务异常: code={}, msg={}", ex.getErrorCode().getCode(), ex.getMessage());
L77|        return ResponseEntity.ok(ApiResponse.fail(ex.getErrorCode().getCode(), ex.getMessage()));
L78|    }
```

### P2 — `G15.1` ddl-auto 生产风险

- **P2** `G15.1` `src/main/resources/application.yml:12-15` — `ddl-auto: update` 生产环境不建议自动建表/改表。
  片段范围：`src/main/resources/application.yml:12-15`

```yaml
L12|    jpa:
L13|      hibernate:
L14|        ddl-auto: update
L15|      open-in-view: false
```

### P2 — `S10.2` CORS header 过宽

- **P2** `S10.2` `src/main/java/com/example/todo/config/WebConfig.java:14-20` — `allowedHeaders("*")` + `allowCredentials(true)`，生产应收敛。
  片段范围：`src/main/java/com/example/todo/config/WebConfig.java:14-20`

```java
L14|        registry.addMapping("/api/**")
L15|                .allowedOriginPatterns("http://localhost:*")
L16|                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
L17|                .allowedHeaders("*")
L18|                .allowCredentials(true)
L19|                .maxAge(3600);
```

### P2 — Bug/I JPA 实体 equals 基于 id

- **P2** `model/Todo.java:124-139` — `equals/hashCode` 仅基于 `id`，未持久化实体 `id=null` 时两新实体相等。
  片段范围：`src/main/java/com/example/todo/model/Todo.java:124-139`

```java
L124|    @Override
L125|    public boolean equals(Object o) {
L126|        if (this == o) return true;
L127|        if (o == null || getClass() != o.getClass()) return false;
L132|        Todo todo = (Todo) o;
L133|        return Objects.equals(id, todo.id); // 问题：未持久化实体 id=null 时所有新实体相等
L134|    }
L137|    @Override
L138|    public int hashCode() {
L139|        return Objects.hash(id);
L140|    }
```

### P2 — `A2.4` import 顺序

- **P2** `A2.4` `src/main/java/com/example/todo/service/impl/TodoServiceImpl.java:11-17` — `java.time` 置于 `org.springframework` 之后，非严格 ASCII 字典序。
  片段范围：`src/main/java/com/example/todo/service/impl/TodoServiceImpl.java:11-17`

```java
L11|import org.slf4j.Logger;
L12|import org.slf4j.LoggerFactory;
L13|import org.springframework.dao.DataAccessException;
L14|import org.springframework.stereotype.Service;
L15|import org.springframework.transaction.annotation.Transactional;
L16|
L17|import java.time.LocalDateTime; // 问题：java.* 应排在 org.* 之前
```

---

## 8. 修复任务列表

### P0

- [ ] **P0** `src/main/java/com/example/todo/context/DefaultUserContext.java:25` + `controller/TodoController.java:34` — 接入鉴权拦截器（登录态校验），确保 `X-User-Id` 仅由可信登录态写入、禁止前端直传伪造；未登录返回 401（对齐 design §6.4.2/A05）。
- [ ] **P0** `S8.1` — 在 `config` 包新增登录拦截器并注册到 `WebMvcConfigurer`，覆盖 `/api/**`。

### P1

- [ ] **P1** `src/main/resources/application.yml:9-10` — DB 账号口令改为环境变量/配置中心注入（`${DB_USERNAME}` / `${DB_PASSWORD}`）。
- [ ] **P1** `src/main/java/com/example/todo/dto/TodoCreateResult.java:26` — 为 `gmtCreate` 增加 `@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")`，并对齐 design §5.2.2 契约格式。
- [ ] **P1** `src/main/java/com/example/todo/context/DefaultUserContext.java:31` — `catch (NumberFormatException e)` 增加至少 `logger.debug` 记录非法 `X-User-Id`。
- [ ] **P1** `src/main/java/com/example/todo/service/impl/TodoServiceImpl.java:45` — `LocalDateTime.now()` 显式指定 `ZoneId`（或评估改用 `Instant`/UTC 存库）。
- [ ] **P1** `G17.1` — 实现应急开关 `todo.create.enabled`，关闭时后端返回 `TODO_0099`（对齐 design §7.3）。

### P2（可选）

- [ ] **P2** `src/main/java/com/example/todo/exception/GlobalExceptionHandler.java:76` — `handleBiz` 对 `SYSTEM_ERROR` 改打 ERROR（或避免与 service 重复记录）。
- [ ] **P2** `src/main/resources/application.yml:14` — 生产 `ddl-auto` 改为 `validate` 并使用 Flyway/Liquibase 迁移脚本。
- [ ] **P2** `src/main/java/com/example/todo/config/WebConfig.java:17` — CORS `allowedHeaders` 收敛为白名单。
- [ ] **P2** `src/main/java/com/example/todo/model/Todo.java:133` — JPA 实体 `equals/hashCode` 增加业务键或处理 null-id（如未持久化按实例相等）。
- [ ] **P2** `src/main/java/com/example/todo/service/impl/TodoServiceImpl.java:17` — 调整 import 顺序为严格 ASCII 字典序（com → java → org）。
- [ ] **P2** `src/test/java/.../TodoServiceImplTest.java` — 补充 description=空字符串落库边界用例。
