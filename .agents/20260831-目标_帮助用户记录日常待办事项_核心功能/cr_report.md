# 代码评审报告 — 待办事项「新增」功能

> 评审日期：2026-08-31
> 评审目标：`app.py` / `templates/index.html` / `tests/*` / `requirements.txt`（创建待办事项最小闭环）
> 依据：`docs/superpowers/specs/2026-08-31-todo-create-design.md`、`docs/superpowers/plans/2026-08-31-todo-create.md`

## 1. 通览 (Overview)

本仓库是一个单文件 Flask 服务端渲染 Web 应用，目标是让内部用户填写事项名称和描述并提交，提交后页面展示已保存的待办列表（最新在前）。核心模块：

- `app.py`：Flask 路由（`GET /`、`POST /todos`）+ JSON 文件读写封装（`load_todos`/`save_todos`/`next_id`）+ ID 自增 + `created_at` 生成。
- `templates/index.html`：Jinja2 模板，录入表单 + flash 提示区 + 倒序列表区。
- `tests/test_app.py`：集成测试（GET 渲染、POST 创建持久化、空 name 拦截、缺失文件首次创建、ID 自增、列表倒序、空状态）。
- `tests/test_data.py`：数据层单元测试（缺失/损坏返回空、读写 UTF-8、next_id 自增）。
- 存储：本地 `todos.json`（JSON 数组），运行期生成。

依赖：`Flask>=3.0`、`pytest>=8.0`（已声明于 `requirements.txt`，仓库内含 `.venv`）。

## 2. 规划 (Planning)

本次评审不修改任何源码文件，仅按 code-review 技能的五车道（Align/Design/Trim/Cause/Verify）执行静态审查 + 测试运行验证，产出本报告。产物路径基于仓库根目录写入。

涉及审查文件清单：
- `app.py:1-66`
- `templates/index.html:1-40`
- `tests/test_app.py:1-75`
- `tests/test_data.py:1-45`
- `requirements.txt`
- 仓库卫生：`.gitignore` 缺失、`.venv/` 与 `__pycache__` 被追踪

## 3. 执行 (Execution)

### 3.1 项目评审档案 (Project Review Profile)

- 状态：`CREATED_AND_USED`（仓库原本无 `REVIEW.md`，本次从项目入口文件生成）。
- 产出：仓库根 `REVIEW.md`（含项目事实 + 四条项目级评审门 G1–G4）。

### 3.2 测试运行验证

```
$ .venv/bin/pytest -q
.............
13 passed in 0.10s
```

13/13 用例通过，覆盖 GET 渲染、创建持久化、空 name 拦截、缺失文件首次创建、ID 自增、列表倒序、空状态、数据层读写/损坏/UTF-8/next_id。

### 3.3 仓库卫生检查

```
git ls-files | grep -c '^\.venv/'      → 2291
git ls-files | grep -c '__pycache__'   → 1088
.gitignore                             → 不存在
```

## 4. 汇总 — 评审结论

### Project profile

State: CREATED_AND_USED
Source: 仓库根 `REVIEW.md`（本次新建）
Notes: 原无评审档案；从 `app.py`、spec、plan 生成项目事实与门 G1–G4。

### Lane verdict table

| Lane | Verdict | Notes |
|---|---|---|
| Align | APPROVE_WITH_COMMENTS | 代码/模板/测试与 spec/plan 一致；写入失败提示为已知接受的取舍。 |
| Design | APPROVE | 单文件 Flask + JSON 文件存储，边界与抽象与最小内部范围匹配，无过度设计。 |
| Trim | APPROVE_WITH_COMMENTS | `.venv`/`__pycache__` 入库且无 `.gitignore`，需清理；运行期 `todos.json` 缺保护。 |
| Cause | NOT_RUN | 本次为新增功能，非缺陷修复，无根因闭合可审。 |
| Verify | APPROVE_WITH_COMMENTS | 13/13 测试通过且断言行为；存在写入失败路径与 created_at 格式两个轻量测试缺口。 |

### Blocking findings

无（blocker_count = 0）。

### Advisory findings

```
[WARNING] [TRIM] [DATA-EXPOSURE] .gitignore（缺失）/ .venv/:1 / __pycache__ —
  2291 个 .venv 文件与 1088 个 __pycache__ 文件被 git 追踪，且仓库无 .gitignore。
Evidence:
  git ls-files | grep -c '^\.venv/' → 2291
  git ls-files | grep -c '__pycache__' → 1088
  ls .gitignore → 不存在
Recommendation:
  新增 .gitignore（忽略 .venv/、__pycache__/、*.pyc、todos.json）；
  从版本控制移除已追踪的 .venv 与 __pycache__（git rm -r --cached）。
  这同时防止运行期数据文件 todos.json 被误入库（plan Task 4 Step 7 明确要求其不入库）。
```

```
[INFO] [TRIM] [DEAD-CODE] 07hubtasty / helloworld.py / code-review.md —
  仓库根存在先前无关任务遗留产物，与本次「待办事项」变更无关。
Evidence:
  git ls-files（非 .venv）列出 07hubtasty、helloworld.py、code-review.md
Recommendation:
  确认后清理无关遗留文件，保持仓库聚焦于当前交付。
```

```
[INFO] [ALIGN] [EVIDENCE-MISMATCH] app.py:29-30 (save_todos) —
  spec §7 要求「写入失败 → 返回错误提示」，但 save_todos 未捕获 OSError，
  写入失败会抛出异常而非 flash 提示。
Evidence:
  app.py:27-30 save_todos 直接 open(...,"w") + json.dump，无 try/except。
  plan Self-Review 明确「写入失败由 Flask 默认异常反馈（内部场景可接受）」。
Recommendation:
  内部场景已显式接受该取舍，无需阻塞；如需加固可在 create_todo 中
  包裹 save_todos 并 flash 错误。
```

```
[WARNING] [VERIFY] [TEST-GAP] tests/test_app.py —
  无写入失败路径测试（spec §7 列出该错误处理）。
Evidence:
  tests/test_app.py 与 tests/test_data.py 均无 OSError/写失败用例。
Recommendation:
  与上述 ALIGN 取舍一致；若后续加固写入错误反馈，应同步补测试。
```

```
[INFO] [VERIFY] [TEST-GAP] tests/test_app.py:37 —
  created_at 仅断言为真值，未校验 ISO 8601 格式。
Evidence:
  assert todos[0]["created_at"]  （仅真值断言）
Recommendation:
  可增加格式断言（如正则 ^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}Z$）以锁契约。
```

### Skipped lanes and reasons

- `Cause`（NOT_RUN）：本次变更为新增「创建待办」功能，非缺陷修复，无根因闭合需要审查。

### Suggested next actions

1. 新增 `.gitignore` 并 `git rm -r --cached .venv __pycache__`，避免 2000+ 无关文件入库并保护 `todos.json`。
2. （可选）在 `create_todo` 包裹 `save_todos` 捕获写入异常并 flash 错误，同步补写失败测试。
3. （可选）为 `created_at` 增加 ISO 8601 格式断言。
4. 清理 `07hubtasty`/`helloworld.py`/`code-review.md` 等无关遗留文件。

### VERDICT: APPROVE_WITH_COMMENTS

blocker_count = 0
