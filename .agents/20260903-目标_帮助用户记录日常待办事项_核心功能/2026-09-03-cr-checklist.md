# Code Review Checklist

> **Change** 待办事项管理系统-新增待办事项 · **分支/Commit** `AI/task-DEV-966dcd0a-...` / `HEAD` · **日期** `2026-09-03`
>
> **AI**：唯一进度源；状态仅用 `⬜` `✅` `❌` `⚠️` `N/A`。

---

## 执行队列

| # | 文件 | 状态 | 备注 |
|---|------|------|------|
| 1 | `src/main/java/com/example/todo/todo/controller/TodoController.java` | ✅ 已审 | 新增 `@Valid` 注解 |
| 2 | `src/main/java/com/example/todo/todo/service/impl/TodoServiceImpl.java` | ✅ 已审 | 时区显式指定 Asia/Shanghai |
| 3 | `src/main/resources/application.yml` | ✅ 已审 | 密码外置化 |

| 非 Java 文件 | 状态 | 备注 |
|-------------|------|------|
| 4 | `src/main/resources/application.yml` | ✅ 已审 | YAML 配置变更 |

---

## Step 2 — 功能性检查（REQ）

### REQ-1: 新增待办事项（F01 / W01）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 名称非空校验 | ✅ | design.md §5.1.2 W01: title必填 | `TodoController.java:38` `@Valid` + `CreateTodoRequest.java:11` `@NotBlank` | 双重校验：Controller 层 `@Valid` + Service 层防御性检查 |
| 名称长度≤100字符 | ✅ | design.md §5.1.2 W01: 长度1~100字符 | `CreateTodoRequest.java:12` `@Size(max=100)` + `TodoServiceImpl.java:36` | 双重校验 |
| 描述可选，长度≤500字符 | ✅ | design.md §5.1.2 W01: 描述可选，≤500字符 | `CreateTodoRequest.java:15` `@Size(max=500)` + `TodoServiceImpl.java:40` | 双重校验 |
| 创建成功返回ID | ✅ | design.md §5.1.2 W01: data.id = 新建ID | `TodoController.java:42` `Map.of("id", id)` | 返回格式正确 |
| 状态默认待办(0) | ✅ | design.md §5.1.1.1: status默认0 | `TodoServiceImpl.java:52` `setStatus(0)` | 符合设计 |
| 创建时记录时间 | ✅ | design.md §5.1.1.1: gmt_create/gmt_modified | `TodoServiceImpl.java:53-55` | 显式 Asia/Shanghai 时区 |
| 错误码 TODO_001/002/003 | ✅ | design.md §5.1.2 W01: 错误码表 | `TodoServiceImpl.java:34,37,41` | 业务异常抛出正确错误码 |

---

## Step 3 — 可读性检查（A1–A7）

| 结果 | 说明（违规写 Ax.x 与 `path:行`） |
|------|--------------------------------|
| ✅ A1.1 | 文件名与类名一致 |
| ✅ A1.2 | 编码 UTF-8 |
| ✅ A1.3 | 仅 ASCII 空格 |
| ✅ A2.1 | package → import → class 顺序正确 |
| ✅ A2.2 | 无 `import *` |
| ✅ A2.3 | import 分组正确 |
| ✅ A2.4 | 字典序排列 |
| ✅ A3.1 | K&R 大括号 |
| ✅ A3.3 | 4 空格缩进 |
| ✅ A3.4 | 行宽 ≤ 120 字符 |
| ✅ A3.7 | 关键字与括号间空格 |
| ✅ A4.1-A4.7 | 命名规范合规 |
| ✅ A5.1 | `@Override` 存在 |
| ✅ A7.1 | public 类/方法有 Javadoc |

---

## Step 4 — 可靠性检查

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| 可靠性 | `reliability-checklist.md` G1–G17 | ✅ | — | 预扫无命中；LLM 审查确认无并发/事务/幂等/SQL/消息/缓存/调度/防御/网络/资损/监控/G15-G17 相关问题 |
| 安全 | `security-checklist.md` S1–S10 | ✅ | — | 预扫无命中；S9.1 原硬编码密码已改为 `${DB_PASSWORD:root}` 环境变量方式，修复完成 |
| Bug 模式 | `bug-pattern-checklist.md` B/M/I（120） | ✅ | — | 预扫 `scan-all-rules.sh` 52条规则无命中；LLM 审查未发现缺陷模式 |

---

## Step 5 — 自定义扩展检查

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| 自定义扩展 | `customized-checklist.md` U* | N/A | — | 未启用自定义规则 |

---

## 进度跟踪

- ✅ 执行队列所有文件已审
- ✅ Step 2 功能点核对完成
- ✅ Step 3 可读性检查完成
- ✅ Step 4 可靠性检查完成（预扫 + LLM）
- ✅ Step 5 自定义扩展检查完成