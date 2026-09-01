# Code Review Report

> **Change** `todo-impl` · **分支/Commit** `AI/task-DEV-966dcd0a-...` / `e8dc443e` · **日期** `2026-09-01` · **审查者** AI
>
> **AI**：等级 **P0 / P1 / P2**；G/S 以 checklist 行内定义为准；Bug 模式以 `bug-pattern-checklist.md` 表头为准（Blocker→P0、Major→P1、Info→P2）。已先运行 `scan-all-rules.sh`（无命中）再写 LLM 结论。问题含 `path:line` 或清单 ID。每个 ❌/⚠️ 问题附 `.java` 片段（§7.1）。

---

## 1. 审查范围

| 项 | 值 |
|----|-----|
| `.java` 文件数 | `8` |
| 变更行数 | `+429 / -0`（提交 `e8dc443e`，12 文件，含文档/DDL/测试） |

| 类/接口 | 路径 | 角色 |
|---------|------|------|
| `Todo` | `src/main/java/com/org/module/entity/Todo.java` | 实体，映射 `todo` 表 |
| `TodoDTO` | `src/main/java/com/org/module/dto/TodoDTO.java` | 入参 DTO，含校验 |
| `TodoVO` | `src/main/java/com/org/module/dto/TodoVO.java` | 出参 VO |
| `TodoMapper` | `src/main/java/com/org/module/mapper/TodoMapper.java` | MP `BaseMapper` |
| `TodoService` | `src/main/java/com/org/module/service/TodoService.java` | 业务接口 |
| `TodoServiceImpl` | `src/main/java/com/org/module/service/impl/TodoServiceImpl.java` | 业务实现 |
| `TodoController` | `src/main/java/com/org/module/controller/TodoController.java` | REST 入口 `POST /api/todos` |
| `TodoServiceImplTest` | `src/test/java/com/org/module/service/impl/TodoServiceImplTest.java` | 单元测试 |

---

## 2. 问题计数

| P0 | P1 | P2 |
|----|----|-----|
| 0 | 0 | 2 |

---

## 3. Step 2 — 功能（REQ）

### REQ-1: 新增待办事项（事项名称 + 描述）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| Given 名称+描述 / When 新增 / Then 落库返回展示对象 | ✅ | T1「核心功能：新增待办事项。任务信息：事项名称和描述」 | `TodoController.java:27-29` / `TodoServiceImpl.java:28-40` / 测试 `should_returnTodoVO_when_requestIsValid` | 接口契约一致：`POST /api/todos` → `Result<TodoVO>` |

### REQ-2: 名称必填、描述可空

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| Given 名称为空 / Then 拒绝 | ✅ | impl.md「title(*) 必填」 | `TodoDTO.java:12-13`(`@NotBlank`+`@Size`)；`TodoController.java:27`(`@Valid`) | 校验在 DTO 层，Controller 触发 |
| Given 描述为空 / Then 正常创建 | ✅ | impl.md「description 可选」 | `TodoDTO.java:16`；测试 `should_saveTodo_when_descriptionIsNull` | `@Size` 对 null 通过 |

### REQ-3: 最小闭环（仅创建）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 仅提供创建接口 | ✅ | T1「最小闭环：仅创建」 | `TodoController.java:26`(`@PostMapping` 唯一端点) | 无 update/delete/list，符合最小闭环 |

### REQ-4: 创建后状态为待处理

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 新建事项 status=0 | ✅ | README「status:0 待处理」 | `TodoServiceImpl.java:24,32`(`TODO_STATUS_PENDING=0`)；测试断言 `isZero()` | 常量化，避免魔法值 |

---

## 4. Step 3 — 可读性检查

| 结果 | 说明（违规写 Ax.x 与 `path:行`） |
|------|--------------------------------|
| ⚠️ | **P2** `A7.1` — `TodoController.java:26` public 方法 `create` 缺 Javadoc。其余文件命名/缩进/K&R/import 顺序/常量风格均合规，scan 脚本与 LLM 复核一致 |

---

## 5. Step 4 — 可靠性检查

| 域 | 参考 | 结果 | 等级 | 说明（列命中 ID 或「已扫无命中」） |
|----|------|------|------|-------------------------------------|
| 可靠性 | `reliability-checklist.md` G1–G17 | ⚠️ | P2 | 命中 `G10.1(降级P2)` `TodoServiceImpl.java:39,48` — VO.createdAt 创建响应为 null；其余 ✅/N/A，事务边界 G3.2 合理 |
| 安全 | `security-checklist.md` S1–S10 | ✅/N/A | — | 无自定义 SQL（S1 预编译由 MP 保证）；S8.2 增删改用 POST；S9.2 日志无敏感信息；鉴权/CORS 为全仓基线（非本次引入） |
| Bug 模式 | `bug-pattern-checklist.md` B/M/I（120） | N/A | — | 预扫 `scan-all-rules.sh` 无命中；LLM 复核：简单 POJO + 单条 insert，无 NPE/资源/并发/序列化命中 |

---

## 6. Step 5 — 自定义扩展检查

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| 自定义扩展 | `customized-checklist.md` U* | ✅/N/A | — | U1.1(示例项) 已满足（Controller 入参用 `@Valid`）；U2 业务红线为空，`N/A(未启用自定义规则)` |

---

## 7. 结论

- **合并建议**：**通过（可选改进）**
- **P0**：无
- **P1/P2**：
  1. **P2** `G10.1(降级)` `TodoServiceImpl.java:39,48` — 创建响应 `createdAt` 为 null，与 README 示例不一致（MP 不回填 DB 默认时间戳，项目无 MetaObjectHandler）；数据已正确落库，建议创建后回查或引入 MetaObjectHandler 自动填充。
  2. **P2** `A7.1` `TodoController.java:26` — public 方法 `create` 缺 Javadoc。
- **一句话**：最小闭环实现清晰、与既有分层约定一致，无阻塞/安全隐患；仅有创建响应时间戳未回填与一处 Javadoc 缺失两项可选改进。

---

## 7.1 问题片段（必填）

### P2 · G10.1(降级) · `src/main/java/com/org/module/service/impl/TodoServiceImpl.java:28-48` — VO.createdAt 创建响应为 null

`save(todo)` 仅回填主键 id，不回填 DB 默认生成的 `created_at`/`updated_at`；项目无 `MetaObjectHandler`，故 `toVO` 返回的 `createdAt` 恒为 null，与 README 响应示例（`"createdAt": "2026-09-01T10:00:00"`）不一致。

```java
L28|    public TodoVO createTodo(TodoDTO dto) {
L29|        Todo todo = new Todo();
L30|        todo.setTitle(dto.getTitle());
L31|        todo.setDescription(dto.getDescription());
L32|        todo.setStatus(TODO_STATUS_PENDING);
L33|        boolean saved = save(todo);              // 仅回填 todo.id，createdAt 仍为 null
L34|        if (!saved || todo.getId() == null) {
L35|            log.error("待办事项创建失败, title: {}", dto.getTitle());
L36|            throw new BusinessException("B0001", "待办事项创建失败");
L37|        }
L38|        log.info("待办事项创建成功, id: {}", todo.getId());
L39|        return toVO(todo);                        // toVO 取 todo.getCreatedAt() → null
L40|    }
L42|    private TodoVO toVO(Todo todo) {
L43|        TodoVO vo = new TodoVO();
L44|        vo.setId(todo.getId());
L45|        vo.setTitle(todo.getTitle());
L46|        vo.setDescription(todo.getDescription());
L47|        vo.setStatus(todo.getStatus());
L48|        vo.setCreatedAt(todo.getCreatedAt());      // null 被透传给响应
L49|        return vo;
L50|    }
```

### P2 · A7.1 · `src/main/java/com/org/module/controller/TodoController.java:26-30` — public 方法 create 缺 Javadoc

```java
L26|    @PostMapping
L27|    public Result<TodoVO> create(@RequestBody @Valid TodoDTO dto) {  // 无 Javadoc
L28|        TodoVO vo = todoService.createTodo(dto);
L29|        return Result.ok(vo);
L30|    }
```

---

## 8. 修复任务列表

> 无 P0/P1 待办；以下为可选 P2 改进项。完成后可改为 `- [x]` 或删除。

### P0

- 无待修复项。

### P1

- 无待修复项。

### P2（可选）

- [ ] **P2** `src/main/java/com/org/module/service/impl/TodoServiceImpl.java:39` — 创建成功后 `getById(todo.getId())` 回查以回填 `createdAt`/`updatedAt`，或为 `Todo` 时间字段加 `@TableField(fill = INSERT/UPDATE)` 并实现 `MetaObjectHandler`，使 `TodoVO.createdAt` 在创建响应中非空（对齐 README 示例）。
- [ ] **P2** `src/main/java/com/org/module/controller/TodoController.java:26` — 为 public 方法 `create` 补充 Javadoc（`@param` / `@return`），满足 A7.1。
