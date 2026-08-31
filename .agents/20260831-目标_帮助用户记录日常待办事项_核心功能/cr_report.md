# Code Review Report

> **Change** `todo-create` · **分支/Commit** `AI/task-DEV-966dcd0a` / `465cc61b` · **日期** `2026-08-31` · **审查者** AI
>
> **AI**：等级 **P0 / P1 / P2**；G/S 以 checklist 行内定义为准；Bug 模式以 `bug-pattern-checklist.md` 表头为准（Blocker→P0、Major→P1、Info→P2）。**已先**运行 `scan-all-rules.sh` 并将要点并入 §5，**再**写 LLM 结论。

---

## 1. 审查范围

| 项 | 值 |
|----|-----|
| `.java` 文件数 | `13` |
| 变更行数 | `+1356 / -0` |

| 类/接口 | 路径 | 角色 |
|---------|------|------|
| `TodoApplication` | `src/main/java/com/antdigital/todo/TodoApplication.java` | 启动类 |
| `TodoConstants` | `src/main/java/com/antdigital/todo/common/constant/TodoConstants.java` | 常量集中管理 |
| `BusinessException` | `src/main/java/com/antdigital/todo/common/exception/BusinessException.java` | 业务异常 |
| `GlobalExceptionHandler` | `src/main/java/com/antdigital/todo/common/exception/GlobalExceptionHandler.java` | 全局异常处理 |
| `ApiResponse` | `src/main/java/com/antdigital/todo/common/response/ApiResponse.java` | 统一响应体 |
| `TodoItemController` | `src/main/java/com/antdigital/todo/controller/TodoItemController.java` | 控制器 |
| `TodoItemMapper` | `src/main/java/com/antdigital/todo/dao/mapper/TodoItemMapper.java` | Mapper接口 |
| `TodoItemCreateRequest` | `src/main/java/com/antdigital/todo/model/dto/TodoItemCreateRequest.java` | 请求DTO |
| `TodoItemDO` | `src/main/java/com/antdigital/todo/model/entity/TodoItemDO.java` | 数据对象 |
| `TodoItemVO` | `src/main/java/com/antdigital/todo/model/vo/TodoItemVO.java` | 响应VO |
| `TodoItemService` | `src/main/java/com/antdigital/todo/service/TodoItemService.java` | 服务接口 |
| `TodoItemServiceImpl` | `src/main/java/com/antdigital/todo/service/impl/TodoItemServiceImpl.java` | 服务实现 |
| `TodoItemServiceImplTest` | `src/test/java/com/antdigital/todo/service/impl/TodoItemServiceImplTest.java` | 单元测试 |

---

## 2. 问题计数

| P0 | P1 | P2 |
|----|----|-----|
| 1 | 1 | 4 |

---

## 3. Step 2 — 功能（REQ）

### REQ-3/REQ-4/REQ-8: `参数校验错误码与spec不符`

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| title为空时返回TODO_001 | ❌ | R01: "事项名称（title）不可为空…返回 TODO_001"（§5.1.3.1业务规则） | `GlobalExceptionHandler.java:36` PARAM_ERROR_CODE="TODO_999"; `:67` 返回error(PARAM_ERROR_CODE, detail) | @Valid触发MethodArgumentNotValidException→GlobalExceptionHandler统一返回TODO_999，spec要求的TODO_001/002/003在正常HTTP流程下不可达 |
| title超过100字符时返回TODO_002 | ❌ | R02: "事项名称长度1~100字符…返回 TODO_002"（§5.1.3.1业务规则） | 同上：`TodoItemServiceImpl.java:116-120`有TODO_002但不可达 | Service层validateCreateRequest含正确错误码，但@Valid在Controller层先拦截 |
| description超过500字符时返回TODO_003 | ❌ | R03: "事项描述长度不超过500字符…返回 TODO_003"（§5.1.3.1业务规则） | 同上：`TodoItemServiceImpl.java:123-127`有TODO_003但不可达 | 同根因：PARAM_ERROR_CODE="TODO_999"覆盖了所有校验错误 |
| 正常创建返回id | ✅ | F01: "录入事项名称+描述并持久化"（§1需求） | `TodoItemServiceImpl.java:53-95` insert+回填id | ✅ |
| 统一响应体{code,msg,data} | ✅ | §5.1.2出参 | `ApiResponse.java:29-35` | ✅ |
| DB写入失败返回TODO_999 | ✅ | §5.1.3.1异常场景 | `TodoItemServiceImpl.java:78-84` | ✅ |
| 请求体格式错误返回400 | ⚠️ | §5.1.3.1异常场景："返回 400 + TODO_999" | `GlobalExceptionHandler.java:76-81` handleException(Exception)→500 | 非JSON(HttpMessageNotReadableException)被兜底Exception处理器捕获返回500而非400 |

---

## 4. Step 3 — 可读性检查

| 结果 | 说明（违规写 Ax.x 与 `path:行`） |
|------|--------------------------------|
| ✅ | A1-A7 全部通过。预扫确认：无Tab字符(A1.3)、无通配符import(A2.2)、行宽≤120(A3.4)、关键字空格正确(A3.7)、包名全小写(A4.1)、无finalize重写(A5.4)、修饰符顺序正确(A6.3)、long字面量大写L(A6.5)。全部public类/方法含Javadoc(A7.1)。 |

---

## 5. Step 4 — 可靠性检查

| 域 | 参考 | 结果 | 等级 | 说明（列命中 ID 或「已扫无命中」） |
|----|------|------|------|-------------------------------------|
| 可靠性 | `reliability-checklist.md` G1–G17 | ⚠️ | P2 | G16.2预扫命中→LLM复核为**误报**（catch体L79-83有logger.error+throw）；G17.1无功能开关(P2) |
| 安全 | `security-checklist.md` S1–S10 | ⚠️ | P2 | S9.1 application-dev.yml:5 password:root硬编码（dev配置，P2）；S1.1 SQL全部#{}预编译✅ |
| Bug模式 | `bug-pattern-checklist.md` B/M/I | ⚠️ | P1 | **M016** `TodoItemServiceImpl.java:66` LocalDateTime.now()未显式指定时区（预扫命中，LLM确认）；其余B/M/I已扫无命中 |

### scan-all-rules.sh 预扫完整输出

```text
=== Step 4 Rule Scan (B/M/I + A/S/G) ===
Targets: src/main/java src/test/java src/main/resources
Engine:  ripgrep

[P0] G16.2 — CatchWithoutLogging: src/main/java/com/antdigital/todo/service/impl/TodoItemServiceImpl.java:78
[P1] M016 — JavaTimeDefaultTimeZone: src/main/java/com/antdigital/todo/service/impl/TodoItemServiceImpl.java:66

=== Summary: 2 findings (P0=1, P1=1, P2=0) | 52/222 rules scanned ===
```

**LLM 复核**：
- G16.2（P0→降级为误报）：`TodoItemServiceImpl.java:78` 的 `catch (Exception e)` 块体在 L79 执行 `logger.error(...)` 并在 L80 `throw new BusinessException(...)`，确有日志和异常抛出。脚本仅检查 `catch(` 所在行不含 `logger./throw` 关键字，属已知局限。**降级为 ✅ 误报**。
- M016（P1，确认）：`TodoItemServiceImpl.java:66` `LocalDateTime.now()` 依赖 JVM 默认时区，设计要求 GMT+8，若 JVM 运行在 UTC 则时间偏移 8 小时。

---

## 6. Step 5 — 自定义扩展检查

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| 自定义扩展 | `customized-checklist.md` U* | N/A | — | N/A(未启用自定义规则) — customized-checklist.md 仅含示例项 U1.1，Controller已使用@Valid满足 |

---

## 7. 结论

- **合并建议**：修复后合并
- **P0**：
  1. 参数校验错误码与spec不符 — `GlobalExceptionHandler.java:36` 将所有 @Valid 校验失败映射为 TODO_999，spec 要求 title空→TODO_001、title超长→TODO_002、description超长→TODO_003（§5.1.3.1 R01/R02/R03）。Service 层 `validateCreateRequest()` 含正确错误码但被 @Valid 拦截不可达。
- **P1/P2**：
  1. **P1** M016 — `TodoItemServiceImpl.java:66` `LocalDateTime.now()` 未显式指定时区，依赖JVM默认时区，应指定 `ZoneId.of("Asia/Shanghai")`。
  2. **P2** `TodoItemMapper.java:30` `selectById` 为查询方法，设计排除范围明确不含查询（§1排除范围），属未使用死代码。
  3. **P2** `TodoItemVO.java:30` 注释"MyBatis 需要无参构造"有误导性——MyBatis 不使用 VO，无参构造实为 Jackson 序列化所需。
  4. **P2** `application-dev.yml:5` DB密码 `password: root` 硬编码，dev配置可接受但建议使用环境变量。
  5. **P2** `GlobalExceptionHandler.java:76-81` `handleException(Exception.class)` 兜底捕获 `HttpMessageNotReadableException`（非JSON请求体）返回500，spec §5.1.3.1要求返回400。
- **一句话**：核心功能链路完整、单测覆盖充分，但参数校验错误码与设计规格存在P0级不符，需修复 GlobalExceptionHandler 的错误码映射后方可合并。

---

## 7.1 问题片段（必填）

### P0 — 参数校验错误码与spec不符

- **P0** `REQ-3/4/8` `src/main/java/com/antdigital/todo/common/exception/GlobalExceptionHandler.java:36,67` — `PARAM_ERROR_CODE="TODO_999"` 将所有 @Valid 校验失败统一返回 TODO_999，spec 要求 TODO_001/002/003。Service 层有正确错误码但被 @Valid 拦截不可达。
  片段范围：`src/main/java/com/antdigital/todo/common/exception/GlobalExceptionHandler.java:35-68`

```java
L35|     /** 参数校验错误码 */
L36|     private static final String PARAM_ERROR_CODE = "TODO_999";  // 问题：应为TODO_001/002/003按场景区分
L37|
L38|     /** 参数校验提示 */
L39|     private static final String PARAM_ERROR_MSG = "请求参数格式错误";
L40|
L54|     @ExceptionHandler(MethodArgumentNotValidException.class)
L55|     @ResponseStatus(HttpStatus.BAD_REQUEST)
L56|     public ApiResponse<Void> handleValidationException(MethodArgumentNotValidException e) {
L57|         String detail = e.getBindingResult().getFieldErrors().stream()
L58|                 .map(FieldError::getDefaultMessage)
L59|                 .collect(Collectors.joining("; "));
L60|         logger.warn("参数校验失败, detail: {}", detail);
L61|         return ApiResponse.error(PARAM_ERROR_CODE, detail);  // 问题：统一返回TODO_999
L62|     }
```

Service层不可达的正确错误码（对照）：
  片段范围：`src/main/java/com/antdigital/todo/service/impl/TodoItemServiceImpl.java:109-127`

```java
L109|        String title = request.getTitle();
L110|        if (StringUtils.isBlank(title)) {
L111|            throw new BusinessException(
L112|                    TodoConstants.CODE_TITLE_EMPTY,      // TODO_001 — 被@Valid拦截不可达
L113|                    TodoConstants.MSG_TITLE_EMPTY);
L114|        }
L115|
L116|        if (title.length() > TodoConstants.TITLE_MAX_LENGTH) {
L117|            throw new BusinessException(
L118|                    TodoConstants.CODE_TITLE_TOO_LONG,   // TODO_002 — 被@Valid拦截不可达
L119|                    TodoConstants.MSG_TITLE_TOO_LONG);
L120|        }
L121|
L122|        String description = request.getDescription();
L123|        if (description != null && description.length() > TodoConstants.DESCRIPTION_MAX_LENGTH) {
L124|            throw new BusinessException(
L125|                    TodoConstants.CODE_DESCRIPTION_TOO_LONG, // TODO_003 — 被@Valid拦截不可达
L126|                    TodoConstants.MSG_DESCRIPTION_TOO_LONG);
L127|        }
```

### P1 — LocalDateTime.now() 未指定时区

- **P1** `M016` `src/main/java/com/antdigital/todo/service/impl/TodoItemServiceImpl.java:66` — `LocalDateTime.now()` 依赖 JVM 默认时区，设计要求 GMT+8。
  片段范围：`src/main/java/com/antdigital/todo/service/impl/TodoItemServiceImpl.java:65-73`

```java
L65|        // 组装数据对象
L66|        LocalDateTime now = LocalDateTime.now();  // 问题：未显式指定时区
L67|        TodoItemDO todoItem = new TodoItemDO();
L68|        todoItem.setTitle(request.getTitle());
L69|        todoItem.setDescription(request.getDescription());
L70|        todoItem.setCreator(creator);
L71|        todoItem.setGmtCreate(now);
L72|        todoItem.setGmtModified(now);
```

### P2 — selectById 未使用死代码

- **P2** `src/main/java/com/antdigital/todo/dao/mapper/TodoItemMapper.java:30` — `selectById` 为查询方法，设计排除范围明确不含查询（§1），本期最小闭环仅创建。
  片段范围：`src/main/java/com/antdigital/todo/dao/mapper/TodoItemMapper.java:24-31`

```java
L24|     /**
L25|      * 根据ID查询待办事项
L26|      *
L27|      * @param id 主键ID
L28|      * @return 待办事项数据对象，不存在返回 null
L29|      */
L30|     TodoItemDO selectById(@Param("id") Long id);  // 问题：本期排除查询，未使用
L31| }
```

### P2 — TodoItemVO 注释误导

- **P2** `src/main/java/com/antdigital/todo/model/vo/TodoItemVO.java:30` — 注释"MyBatis 需要无参构造"有误导性，MyBatis 不使用 VO。
  片段范围：`src/main/java/com/antdigital/todo/model/vo/TodoItemVO.java:29-33`

```java
L29|
L30|     /** MyBatis 需要无参构造 */  // 问题：MyBatis不使用VO，应为Jackson序列化
L31|     public TodoItemVO() {
L32|     }
```

### P2 — DB 密码硬编码

- **P2** `S9.1` `src/main/resources/application-dev.yml:5` — dev 配置 DB 密码硬编码。
  片段范围：`src/main/resources/application-dev.yml:1-6`

```yaml
L1| spring:
L2|   datasource:
L3|     url: jdbc:mysql://localhost:3306/todo_db?...
L4|     username: root
L5|     password: root   # 问题：硬编码，建议使用环境变量
L6|     driver-class-name: com.mysql.cj.jdbc.Driver
```

### P2 — 非JSON请求体返回500而非400

- **P2** `src/main/java/com/antdigital/todo/common/exception/GlobalExceptionHandler.java:76-81` — `handleException(Exception.class)` 兜底捕获 `HttpMessageNotReadableException` 返回500，spec要求400。
  片段范围：`src/main/java/com/antdigital/todo/common/exception/GlobalExceptionHandler.java:76-81`

```java
L76|     @ExceptionHandler(Exception.class)
L77|     @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)  // 问题：非JSON应返回400
L78|     public ApiResponse<Void> handleException(Exception e) {
L79|         logger.error("系统异常, errorMessage: {}", e.getMessage(), e);
L80|         return ApiResponse.error(SYS_ERROR_CODE, SYS_ERROR_MSG);
L81|     }
```

---

## 8. 修复任务列表

### P0

- [ ] **P0** `src/main/java/com/antdigital/todo/common/exception/GlobalExceptionHandler.java:36` — 修改 `handleValidationException` 使其根据校验失败的字段名映射到 spec 定义的错误码（title空→TODO_001、title超长→TODO_002、description超长→TODO_003），或移除Controller层 `@Valid` 改由 Service 层 `validateCreateRequest()` 统一校验返回正确错误码
- [ ] **P0** `src/main/java/com/antdigital/todo/controller/TodoItemController.java:45` — 如选择移除 `@Valid` 方案，同步移除该注解使 Service 层校验生效

### P1

- [ ] **P1** `M016` `src/main/java/com/antdigital/todo/service/impl/TodoItemServiceImpl.java:66` — 将 `LocalDateTime.now()` 改为 `LocalDateTime.now(ZoneId.of("Asia/Shanghai"))` 显式指定时区，与设计 GMT+8 一致

### P2（可选）

- [ ] **P2** `src/main/java/com/antdigital/todo/dao/mapper/TodoItemMapper.java:30` — 移除 `selectById` 方法及 Mapper XML 中对应 select（本期排除查询，避免死代码）
- [ ] **P2** `src/main/java/com/antdigital/todo/model/vo/TodoItemVO.java:30` — 修正注释为"Jackson 序列化需要无参构造"或删除误导注释
- [ ] **P2** `src/main/resources/application-dev.yml:5` — 将 DB 密码改为环境变量引用 `${DB_PASSWORD:root}`
- [ ] **P2** `src/main/java/com/antdigital/todo/common/exception/GlobalExceptionHandler.java:76` — 新增 `@ExceptionHandler(HttpMessageNotReadableException.class)` 返回 400 + TODO_999，避免非JSON请求体被兜底 Exception 处理器返回500
