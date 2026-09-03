# Code Review Report

> **Change** 待办事项模块 - 新增待办事项 · **分支/Commit** `AI/task-DEV-*` / `未指定` · **日期** 2026-09-03 · **审查者** AI
>
> **AI**：等级 **P0 / P1 / P2**；G/S 以 checklist 行内定义为准；Bug 模式以 `bug-pattern-checklist.md` 表头为准（Blocker→P0、Major→P1、Info→P2）。**已运行** `scan-all-rules.sh` 并将要点并入 §5。问题须含 `path:line` 或清单 ID。**每个 ❌/⚠️ 问题在 §7 后附 `.java` 问题片段**。

---

## 1. 审查范围

| 项 | 值 |
|----|-----|
| `.java` 文件数 | 11 |
| 变更行数 | `+~300 / -0` |

| 类/接口 | 路径 | 角色 |
|---------|------|--------------|
| `TodoController` | `src/main/java/com/example/todo/todo/controller/TodoController.java` | REST 控制器 |
| `TodoService` | `src/main/java/com/example/todo/todo/service/TodoService.java` | 业务服务接口 |
| `TodoServiceImpl` | `src/main/java/com/example/todo/todo/service/impl/TodoServiceImpl.java` | 业务服务实现 |
| `TodoItemDO` | `src/main/java/com/example/todo/todo/dao/entity/TodoItemDO.java` | 数据实体 |
| `TodoItemMapper` | `src/main/java/com/example/todo/todo/dao/mapper/TodoItemMapper.java` | Mapper 接口 |
| `CreateTodoRequest` | `src/main/java/com/example/todo/todo/model/dto/CreateTodoRequest.java` | 请求 DTO |
| `BusinessException` | `src/main/java/com/example/todo/common/exception/BusinessException.java` | 业务异常 |
| `GlobalExceptionHandler` | `src/main/java/com/example/todo/common/exception/GlobalExceptionHandler.java` | 全局异常处理器 |
| `ApiResponse` | `src/main/java/com/example/todo/common/response/ApiResponse.java` | 通用响应体 |
| `TodoApplication` | `src/main/java/com/example/todo/TodoApplication.java` | 应用启动类 |
| `TodoServiceImplTest` | `src/test/java/com/example/todo/todo/service/impl/TodoServiceImplTest.java` | 单元测试 |

---

## 2. 问题计数

| P0 | P1 | P2 |
|----|----|-----|
| 1 | 2 | 1 |

---

## 3. Step 2 — 功能（REQ）

### REQ-1: 新增待办事项接口

**来源**：design.md §5.1.2 W01 — `POST /api/todo/create`

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 接口路径正确暴露 | ✅ | design.md §5.1.2 — URI: POST /api/todo/create | `TodoController.java:36` — `@PostMapping("/create")` + `@RequestMapping("/api/todo")` | 路径正确 |
| 事项名称非空校验 | ✅ | design.md §5.1.3.1 R01 — 名称不能为空，返回 TODO_001 | `TodoServiceImpl.java:32-33` — `if (title == null \|\| title.trim().isEmpty())` | Service 层防御性校验返回 TODO_001 |
| 名称长度不超过100字符 | ✅ | design.md §5.1.3.1 R02 — 长度限制100字符，返回 TODO_002 | `TodoServiceImpl.java:35-36` — `if (title.length() > 100)` | 正确 |
| 描述可选，长度不超过500字符 | ✅ | design.md §5.1.3.1 R03 — 描述长度限制500字符，返回 TODO_003 | `TodoServiceImpl.java:39-40` — `if (description != null && description.length() > 500)` | 正确 |
| 创建时状态默认为待办(0) | ✅ | design.md §5.1.1.1 — status 默认值 0 | `TodoServiceImpl.java:51` — `todoItem.setStatus(0)` | 符合设计 |
| 创建时记录当前用户ID | ⚠️ | design.md §5.1.2 — "创建时自动记录当前用户ID" | `TodoServiceImpl.java:46` — `String userId = "SYSTEM"` | 当前为固定值，注释说明后续接入统一登录态 |
| 返回新建待办事项ID | ✅ | design.md §5.1.2 — 出参 data.id 为新建事项ID | `TodoController.java:40-41` — `Map.of("id", id)` | 返回结构正确 |

### REQ-2: 错误码定义

**来源**：design.md §5.1.2 — 错误码表

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| TODO_001 名称为空 | ✅ | design.md §5.1.2 — 错误码 TODO_001 | `TodoServiceImpl.java:33` | 正确 |
| TODO_002 名称超长 | ✅ | design.md §5.1.2 — 错误码 TODO_002 | `TodoServiceImpl.java:36` | 正确 |
| TODO_003 描述超长 | ✅ | design.md §5.1.2 — 错误码 TODO_003 | `TodoServiceImpl.java:40` | 正确 |

---

## 4. Step 3 — 可读性检查

| 结果 | 说明（违规写 Ax.x 与 `path:行`） |
|------|--------------------------------|
| ✅ | **A1 源文件格式** — 文件名=类名，UTF-8编码，无Tab |
| ✅ | **A2 源文件结构** — 无 `import *`，静态/非静态分组有序 |
| ✅ | **A3 代码样式** — K&R 大括号，4空格缩进，行宽 ≤ 120，运算符空格正确 |
| ✅ | **A4 命名规范** — 包名全小写，类名 UpperCamelCase，方法/字段 lowerCamelCase，测试类 xxxTest |
| ✅ | **A5 编码实践** — `@Override` 齐全，无空 catch 块 |
| ✅ | **A6 特定元素样式** — 修饰符顺序正确，long 字面量使用大写 L |
| ✅ | **A7 Javadoc** — 各 public 类均有 Javadoc，getter/setter 按惯例省略 |

---

## 5. Step 4 — 可靠性检查

| 域 | 参考 | 结果 | 等级 | 说明（列命中 ID 或「已扫无命中」） |
|----|------|------|------|-------------------------------------|
| 可靠性 | `reliability-checklist.md` G1–G17 | ✅ | — | G1 并发: N/A(单表写入); G2 幂等: N/A(仅创建); G3 事务: N/A; G4 SQL: ✅ 使用 `#{}` 参数化查询; G5 MQ: N/A; G6 缓存: N/A; G7 调度: N/A; G8 防御: ✅; G9 网络: N/A; G10 接口: ✅; G11 自测: ✅ 测试覆盖7场景; G12 资损: N/A; G13 监控: ✅; G14 时区: ⚠️ 见 M016; G15 灰度: ✅; G16 监控: ✅; G17 应急: N/A |
| 安全 | `security-checklist.md` S1–S10 | ❌ | P0 | **S9.1 — 密钥/凭证硬编码**：`application.yml:10` 数据库密码 `root` 硬编码 |
| Bug 模式 | `bug-pattern-checklist.md` B/M/I（120） | ❌ | P1 | 预扫发现 **M016 — JavaTimeDefaultTimeZone**：`TodoServiceImpl.java:52` `LocalDateTime.now()` 使用系统默认时区 |

### 预扫结果（scan-all-rules.sh）

```
[P1] M016 — JavaTimeDefaultTimeZone: src/main/java/com/example/todo/todo/service/impl/TodoServiceImpl.java:52
```

### LLM 补充检查

**P0** 安全问题：
- **S9.1** `src/main/resources/application.yml:10` — 数据库密码 `root` 硬编码在配置文件中，违反项目架构文档 `docs/ARCHITECTURE.md` 中"敏感配置必须通过环境变量或密钥管理服务注入，禁止写入配置文件"的约束。

**P1** 可靠性问题：
- **M016** `src/main/java/com/example/todo/todo/service/impl/TodoServiceImpl.java:52` — `LocalDateTime.now()` 使用系统默认时区，未显式指定。对于跨时区部署场景可能导致时间不一致。

**P1** 功能问题（LLM补充）：
- Controller 缺少 `@Valid` 注解：`TodoController.java:37` — `@RequestBody CreateTodoRequest request` 未加 `@Valid`，导致 DTO 上的 `@NotBlank` / `@Size` 校验注解不生效。虽然 Service 层有重复的防御性校验兜底，但 DTO 层校验形同虚设，建议补充 `@Valid` 以利用 Spring 的声明式校验机制。注意：增加 `@Valid` 后需处理 `MethodArgumentNotValidException` 返回的错误码为 `A0001` 而非 `TODO_001` 的问题（见下文 §7.1 问题片段）。

**剩余 B/M/I 规则核销**（已由 LLM 逐条核对，未覆盖项无命中）：
- B001–B081: 无命中（无非受检parse调用、无数组比较、无装箱类型==比较、无`@Transactional`非public方法等）
- M001–M027: 除 M016 外无命中（无非static内部类、无空catch、无`@Override`缺失等）
- I001–I010: 无命中（测试均使用assertj+assertThat，非`@Test(expected=...)`、无JUnit3混用等）

---

## 6. Step 5 — 自定义扩展检查

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| 自定义扩展 | `customized-checklist.md` U* | N/A | — | N/A(未启用自定义规则，文件仅含示例项) |

---

## 7. 结论

- **合并建议**：修复后合并
- **P0**：
  1. **S9.1** `application.yml:10` — 数据库密码硬编码，需改为环境变量注入
- **P1**：
  1. **`@Valid` 缺失** `TodoController.java:37` — Controller 入参缺少 `@Valid` 注解，DTO 校验注解无效
  2. **M016** `TodoServiceImpl.java:52` — `LocalDateTime.now()` 未显式指定时区
- **P2**：
  1. `TodoServiceImpl.java:46` — 用户ID硬编码为"SYSTEM"，设计文档要求从统一登录态获取（已注释说明，无功能影响）
- **一句话**：代码质量整体良好，功能实现完整覆盖设计规格，测试覆盖充分（7个测试场景）。存在 1 个安全合规问题（密码硬编码，P0）和 2 个可改进点（@Valid缺失、时区显式指定），建议修复后合并。

---

## 7.1 问题片段（必填）

### P0 S9.1 — 数据库密码硬编码

**等级**：P0 · **规则**：S9.1 · **路径**：`src/main/resources/application.yml:10`

```yaml
L8|    url: jdbc:mysql://localhost:3306/todo_db?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
L9|    username: root
L10|    password: root                          # 问题：密码硬编码，违反架构文档约束
L11|    driver-class-name: com.mysql.cj.jdbc.Driver
```

**整改建议**：改为从环境变量读取，如 `${DB_PASSWORD}` 或通过密钥管理服务注入。

---

### P1 `@Valid` 缺失 — Controller 入参校验未启用

**等级**：P1 · **规则**：Controller输入校验 · **路径**：`src/main/java/com/example/todo/todo/controller/TodoController.java:37`

```java
L36|    @PostMapping("/create")
L37|    public ApiResponse<Map<String, Long>> createTodo(@RequestBody CreateTodoRequest request) {
     //                                                         ^^^^^^^ 缺少 @Valid 注解
L38|        log.info("新增待办事项请求: title={}, description={}", request.getTitle(), request.getDescription());
L39|        Long id = todoService.createTodo(request);
L40|        return ApiResponse.success(Map.of("id", id));
```

**整改建议**：将 `@RequestBody` 改为 `@Valid @RequestBody`，使 DTO 上的 `@NotBlank` / `@Size` 生效。同时需处理 `MethodArgumentNotValidException` 错误码对齐问题——若需保持 `TODO_001` 错误码，建议方案：
- 方案A：移除 DTO 上的 `@NotBlank`，完全依赖 Service 层校验返回 TODO_001
- 方案B：在 `GlobalExceptionHandler` 中针对 `title` 字段的 `@NotBlank` 校验失败转换为 `BusinessException(TODO_001, ...)`

---

### P1 M016 — JavaTimeDefaultTimeZone

**等级**：P1 · **规则**：M016 · **路径**：`src/main/java/com/example/todo/todo/service/impl/TodoServiceImpl.java:52`

```java
L50|        todoItem.setDescription(description != null ? description.trim() : null);
L51|        todoItem.setStatus(0); // 0-待办
L52|        LocalDateTime now = LocalDateTime.now();
     //                           ^^^^^^^^^^^^^^^^^ 问题：使用系统默认时区
L53|        todoItem.setGmtCreate(now);
L54|        todoItem.setGmtModified(now);
```

**整改建议**：使用 `LocalDateTime.now(ZoneId.of("Asia/Shanghai"))` 或从配置中心/环境变量读取时区。

---

### P2 用户ID硬编码（低优先级）

**等级**：P2 · **规则**：功能完善 · **路径**：`src/main/java/com/example/todo/todo/service/impl/TodoServiceImpl.java:46`

```java
L44|        // 构建实体
L45|        // 当前版本暂未接入统一登录态，使用固定用户ID
L46|        String userId = "SYSTEM";
     //                      ^^^^^^^^ 问题：用户ID硬编码，未从统一登录态获取
L47|        TodoItemDO todoItem = new TodoItemDO();
L48|        todoItem.setUserId(userId);
```

**说明**：design.md 要求在创建时自动记录当前用户ID，当前使用固定值 "SYSTEM" 作为过渡方案，有注释说明后续需接入统一登录态。无功能影响，建议在接入登录态后修复。

---

## 8. 修复任务列表

### P0

- [ ] **P0** `src/main/resources/application.yml:10` — 将数据库密码从硬编码改为环境变量注入（如 `${DB_PASSWORD}`）

### P1

- [ ] **P1** `src/main/java/com/example/todo/todo/controller/TodoController.java:37` — 添加 `@Valid` 注解启用 DTO 声明式校验，并处理错误码对齐问题
- [ ] **P1** `src/main/java/com/example/todo/todo/service/impl/TodoServiceImpl.java:52` — 将 `LocalDateTime.now()` 改为 `LocalDateTime.now(ZoneId.of("Asia/Shanghai"))` 显式指定时区

### P2（可选）

- [ ] **P2** `src/main/java/com/example/todo/todo/service/impl/TodoServiceImpl.java:46` — 接入统一登录态后，从 SecurityContext 获取当前用户ID替换 "SYSTEM" 固定值