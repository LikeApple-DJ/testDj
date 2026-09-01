# Code Review Report

> **Change** `待办事项新增（最小闭环）` · **分支/Commit** `AI/task-DEV-966dcd0a` / `c96fc5be` · **日期** `2026-09-01` · **审查者** AI
>
> **技能**：dtazziboot-java-code-review（SDD 范式）
> **系分依据**：`.agents/20260831-目标_帮助用户记录日常待办事项_核心功能/design.md`
> **清单驱动源**：`.agents/20260831-目标_帮助用户记录日常待办事项_核心功能/2026-09-01-cr-checklist.md`

> **等级**：P0 阻塞 / P1 推荐 / P2 参考。Bug 模式 Blocker→P0、Major→P1、Info→P2。

---

## §1 审查范围

本次审查覆盖编码实现提交 `c96fc5be`（父 `903614aa`）的全部 Java 源文件 + MyBatis XML + DDL，共 16 个文件：

| # | 文件 |
|---|------|
| 1 | src/main/java/com/aiwork/todo/TodoApplication.java |
| 2 | src/main/java/com/aiwork/todo/common/constant/TodoConstants.java |
| 3 | src/main/java/com/aiwork/todo/common/exception/BizException.java |
| 4 | src/main/java/com/aiwork/todo/common/exception/GlobalExceptionHandler.java |
| 5 | src/main/java/com/aiwork/todo/common/exception/TodoErrorCodeEnum.java |
| 6 | src/main/java/com/aiwork/todo/common/result/Result.java |
| 7 | src/main/java/com/aiwork/todo/controller/TodoItemController.java |
| 8 | src/main/java/com/aiwork/todo/dao/mapper/TodoItemMapper.java |
| 9 | src/main/java/com/aiwork/todo/model/dto/CreateTodoRequest.java |
| 10 | src/main/java/com/aiwork/todo/model/dto/CreateTodoResult.java |
| 11 | src/main/java/com/aiwork/todo/model/entity/TodoItemDO.java |
| 12 | src/main/java/com/aiwork/todo/model/enums/TodoStatusEnum.java |
| 13 | src/main/java/com/aiwork/todo/service/TodoItemService.java |
| 14 | src/main/java/com/aiwork/todo/service/impl/TodoItemServiceImpl.java |
| 15 | src/main/resources/mapper/TodoItemMapper.xml |
| 16 | src/main/resources/sql/schema.sql |

自动化预扫：`scan-all-rules.sh`（52/222 条规则）已执行，原始输出并入 §4。

---

## §2 功能性检查（Step 2）

对照 design.md §5.2.2 / §5.2.3.1 的功能点与业务规则 R01–R05：

| REQ | spec 证据 | 代码证据 | 结论 |
|-----|-----------|----------|------|
| F01 新增待办事项（POST /openapi/todo/items → 落库 → 返回 ID） | §4.2 O01、§5.2.3.1 时序图 | `TodoItemController.java:44-51`、`TodoItemServiceImpl.java:73-90` | ✅ 满足 |
| F02 入参校验（名称必填 1-100；描述选填 ≤1000） | §5.2.2 入参表、R01-R03 | `TodoItemServiceImpl.java:42-60` | ✅ 满足 |
| F03 创建结果返回（含生成的事项 ID） | §5.2.2 出参表、F03 | `TodoItemServiceImpl.java:90`、`Result.java:56-62` | ✅ 满足 |
| R01 名称非空 → TODO_001 | §5.2.3.1 R01 | `TodoItemServiceImpl.java:42-44` | ✅ 满足 |
| R02 名称 ≤100 → TODO_002 | R02 | `TodoItemServiceImpl.java:47-49` | ✅ 满足 |
| R03 描述 ≤1000 → TODO_003 | R03 | `TodoItemServiceImpl.java:58-60` | ✅ 满足 |
| R04 状态默认 PENDING | R04 | `TodoItemServiceImpl.java:67` | ✅ 满足 |
| R05 creator/tenant_id 缺失按默认值不阻断 | R05、A01-A02 | `TodoItemServiceImpl.java:64,68` | ✅ 满足 |
| 错误码 TODO_001/002/003/999 契约 | §5.2.2 错误码表 | `TodoErrorCodeEnum.java:14-23` | ✅ 满足 |
| 通用出参 result/msg/data | §5.1 全局约定 | `Result.java:14-78` | ✅ 满足 |
| mapper 主键回写 | §5.2.3.1「返回主键 id」 | `TodoItemMapper.xml:19-22`（useGeneratedKeys/keyProperty） | ✅ 满足 |
| 功能开关 todo.create.enabled | §7.3 可应急 | `TodoItemController.java:31-49`、`application.yml:18-21` | ✅ 满足 |

**功能性不符（P0）：0 条。** 所有功能点与业务规则均在代码中落地且与 spec 一致。`StringUtils.hasText` 对 null/空白均判空，名称长度边界（=100 通过、=101 拒绝）与描述边界（=1000 通过、=1001 拒绝）均有单测覆盖。

---

## §3 可读性检查（Step 3）

对照 `readability-checklist.md` A1–A7：

| 编号 | 结论 | 说明 |
|------|------|------|
| A1 源文件格式 | ✅ | UTF-8、LF、单顶层类、无 `*` 导入 |
| A2 命名规范 | ✅ | 类大驼峰、方法小驼峰、常量全大写下划线、DO/DTO/Impl 后缀齐全 |
| A3 包结构与接口实现分离 | ✅ | `service` 与 `service/impl` 分离，公共类置于 `common` |
| A4 Javadoc | ✅ | 类/方法均有 Javadoc 且含 `@author/@date`，枚举字段有注释 |
| A5 无魔法值 | ✅ | 长度/默认值/提示语均抽至 `TodoConstants`，私有构造抛 `IllegalStateException` |
| A6 SLF4J 占位符日志 | ✅ | `logger.warn/error` 全部使用 `{}` 占位符，异常对象作为最后参数 |
| A7 方法长度/复杂度 | ⚠️ P2 | `createTodoItem` 单方法 92 行略长，且 `catch(BizException e){throw e;}`（`:79-80`）为冗余 catch，可删除以简化 |

---

## §4 可靠性 / 安全 / Bug 模式（Step 4）

### §4.1 自动化预扫（scan-all-rules.sh）

原始输出：

```
[P0] G16.2 — CatchWithoutLogging: src/main/java/com/aiwork/todo/service/impl/TodoItemServiceImpl.java:79
[P0] G16.2 — CatchWithoutLogging: src/main/java/com/aiwork/todo/service/impl/TodoItemServiceImpl.java:81
[P0] G16.2 — CatchWithoutLogging: src/main/java/com/aiwork/todo/service/impl/TodoItemServiceImpl.java:84
[P0] S1.1 — MyBatisSqlInjection: src/main/java/com/aiwork/todo/controller/TodoItemController.java:31
=== Summary: 4 findings (P0=4, P1=0, P2=0) | 52/222 rules scanned ===
```

人工复核（全部为误报/可接受，不计入 P0）：

| 命中 | 复核 | 处置 |
|------|------|------|
| G16.2 @ `:81` | 误报：该 `catch (DataAccessException e)` 块**含** `logger.error("...data access error...", e)`（`:82`） | ❌ 误报 |
| G16.2 @ `:84` | 误报：该 `catch (Exception e)` 块**含** `logger.error("...system error...", e)`（`:85`） | ❌ 误报 |
| G16.2 @ `:79` | `catch (BizException e){throw e;}` 业务异常重抛；该异常最终由 `GlobalExceptionHandler.handleBizException` 统一 `logger.warn` 记录，边界已兜底 | ⚠️ 可接受（建议删除冗余 catch，见 §8） |
| S1.1 @ `TodoItemController.java:31` | 误报：正则命中 `@Value("${todo.create.enabled:true}")` 的 Spring SpEL 占位符 `${}`，非 MyBatis SQL 拼接；实际 `TodoItemMapper.xml` 全部使用 `#{}` 参数化（`:22`） | ❌ 误报 |

### §4.2 LLM 补扫（脚本未覆盖项）

| 等级 | 类别 | ID/场景 | 简述 | 位置 |
|------|------|---------|------|------|
| P1 | 可靠性-监控日志 | G | `logger.warn("...creator missing...")` 在未实现登录拦截器时**每次创建**触发，告警噪音淹没真实告警，与「成功」语义冲突；建议降级为 `info` 或加 `isDebugEnabled`，待接入拦截器后再恢复 warn | `TodoItemServiceImpl.java:71` |
| P2 | 安全-密钥泄露 | S | `application.yml` 明文硬编码 DB 账密 `root/root`；dev 配置可接受，生产应外置 | `application.yml:9-10` |
| P2 | 安全-输入校验 | S | 仅做长度校验，未做内容净化（XSS）；本期无渲染/回显，风险低，后续接入展示层需补充 | `TodoItemServiceImpl.java:39-60` |
| P2 | Bug-冗余代码 | B | `catch(BizException e){throw e;}` 可删除，BizException 自然向上传播 | `TodoItemServiceImpl.java:79-80` |
| P2 | 可靠性-事务边界 | G | 单条 INSERT，auto-commit 满足最小闭环，无并发风险（spec 已声明） | `TodoItemServiceImpl.java:73` |
| P2 | 设计契约 | — | 功能开关关闭时返回 `TODO_999`（系统异常）码，语义与「功能已关闭」不符；msg 友好但 code 易误导调用方误判为系统故障 | `TodoItemController.java:47-48` |

### §4.3 安全专项

- **SQL 注入**：`TodoItemMapper.xml` 全部 `#{}` 参数化，无 `${}`；DDL 表名/索引命名规范。✅
- **认证/授权**：本期按 A01 不实现登录，登录态由后续拦截器注入；当前 creator 固定空串并 warn，spec 已明确为遗留项。✅（符合范围）
- **敏感数据**：无敏感字段，不涉及加密/脱敏。✅

---

## §5 Bug 模式（B/M/I）

仅命中 1 条 Info 级（映射 P2）：`catch(BizException e){throw e;}` 冗余 catch（`TodoItemServiceImpl.java:79-80`）。其余 B/M/I 规则未命中。

---

## §6 自定义扩展检查（Step 5）

`customized-checklist.md` 为空/示例项 → **N/A（未启用自定义规则）**。

---

## §7 问题汇总

| 等级 | 数量 | 说明 |
|------|------|------|
| **P0（阻塞）** | **0** | 脚本 4×P0 全为误报；功能性与 spec 完全一致 |
| P1（推荐） | 1 | creator-missing warn 告警噪音 |
| P2（参考） | 5 | 冗余 catch / 明文 DB 账密 / XSS 净化缺失 / 功能开关错误码语义 / 事务边界（无需调整） |

**结论：本次变更无阻塞问题（blocker_count = 0），可进入合并流程。** P1 建议合并前修复，P2 可选改进。

---

## §8 修复任务列表

- [ ] **[P1]** `TodoItemServiceImpl.java:71` — 将每次创建必触发的 `logger.warn("...creator missing...")` 降级为 `info`，或加 `isDebugEnabled` 守卫；待后续接入统一登录拦截器注入真实 creator 后再恢复 warn。
- [ ] **[P2]** `TodoItemServiceImpl.java:79-80` — 删除冗余 `catch (BizException e) { throw e; }`，让业务异常自然向上传播至 `GlobalExceptionHandler`。
- [ ] **[P2]** `application.yml:9-10` — DB 账密 `root/root` 外置为环境变量/配置中心（生产部署前必须处理）。
- [ ] **[P2]** `TodoItemController.java:47-48` — 功能开关关闭时勿复用 `TODO_999`（系统异常）码，建议新增专用码（如 `TODO_004 功能已关闭`）以避免调用方误判。
- [ ] **[P2]** `TodoItemServiceImpl.java:39-60` — 后续接入展示/回显层时，补充 title/description 内容净化（XSS 防护）。
- [x] *无 P0 阻塞项，无需阻断合并。*
