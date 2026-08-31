# Task 1 报告：待办事项创建功能（模型 + 存储 + CLI + 测试）

**状态：DONE**

## 实现内容

按 brief 的接口签名逐字实现，文件结构与 brief 完全一致：

| 文件 | 内容 |
| --- | --- |
| `todoapp/__init__.py` | 包标识（docstring） |
| `todoapp/model.py` | `Todo` dataclass：`name: str`、`description: str`、`created_at: str`；`__post_init__` 校验 `name` 去空白后非空（否则 `ValueError("名称不能为空")`）；`created_at` 默认为 UTC ISO 8601 字符串（`datetime.now(timezone.utc)`） |
| `todoapp/storage.py` | `save_todo(todo, path="todos.jsonl") -> None`（JSONL 追加一行，`ensure_ascii=False` 保留中文，父目录自动创建）；`load_todos(path="todos.jsonl") -> list[Todo]`（文件不存在返回 `[]`，跳过空行）；`create_todo(name, description="", path="todos.jsonl") -> Todo`（构造 + 校验 + 持久化） |
| `todoapp/cli.py` | `main(argv: list[str] \| None = None) -> int`：argparse `add` 子命令，位置参数 `name`、`description`（`nargs="?"`，默认空串）；成功打印 `已创建待办事项：<name>` 返回 0；`ValueError` 打印 `错误：名称不能为空` 到 stderr 返回 1 |
| `todoapp/__main__.py` | `from todoapp.cli import main; raise SystemExit(main())`，支持 `python -m todoapp` |
| `tests/__init__.py` | 空文件（使 `tests` 成为包，保证 discover 稳定） |
| `tests/test_todo.py` | 8 个测试，覆盖 brief 列出的全部 7 项行为，真实文件 IO + `tempfile` 隔离，零 mock |

## TDD 证据

### Step 1 — 骨架（0 测试可运行）

```
$ python3 -m unittest discover
Ran 0 tests in 0.000s
NO TESTS RAN          # exit=5，0 测试时的正常信号，非报错
```

### Step 2 — RED（先写测试，全部失败）

```
$ python3 -m unittest discover
ImportError: Failed to import test module: tests.test_todo
ImportError: cannot import name 'cli' from 'todoapp' (todoapp/__init__.py)
Ran 1 test in 0.000s
FAILED (errors=1)
```

### Step 3/4 — GREEN（实现 model/storage/cli/__main__ 后全通过）

```
$ python3 -m unittest discover -v
test_add_success (tests.test_todo.TestCli.test_add_success) ... ok
test_add_with_empty_name_fails (tests.test_todo.TestCli.test_add_with_empty_name_fails) ... ok
test_create_todo_returns_saved_todo (tests.test_todo.TestStorage.test_create_todo_returns_saved_todo) ... ok
test_load_empty_file_returns_empty_list (tests.test_todo.TestStorage.test_load_empty_file_returns_empty_list) ... ok
test_save_then_load_roundtrip_and_append (tests.test_todo.TestStorage.test_save_then_load_roundtrip_and_append) ... ok
test_blank_name_raises_value_error (tests.test_todo.TestTodoModel.test_blank_name_raises_value_error) ... ok
test_construct_with_name_and_description (tests.test_todo.TestTodoModel.test_construct_with_name_and_description) ... ok
test_empty_description_allowed (tests.test_todo.TestTodoModel.test_empty_description_allowed) ... ok

Ran 8 tests in 0.003s
OK                      # exit=0，输出无告警/噪声
```

### Step 5 — 真实 CLI 冒烟

```
$ python3 -m todoapp add "测试事项" "这是描述"
已创建待办事项：测试事项            # stdout 确认，exit=0
$ cat todos.jsonl
{"name": "测试事项", "description": "这是描述", "created_at": "2026-08-31T09:12:19+00:00"}   # 恰好一行 JSON

$ python3 -m todoapp add ""
stderr: 错误：名称不能为空           # exit=1，未落盘
```

冒烟产生的 `todos.jsonl` 已删除（`ls: cannot access 'todos.jsonl'` 确认），删除后复跑 `python3 -m unittest discover -v` 仍为 8/8 OK。

## 自审

- **仅创建闭环**：无 list/update/delete，无多余 CLI 子命令；`load_todos` 仅按 brief 要求为测试/核验闭环存在，未暴露为 CLI 功能。
- **零第三方依赖**：仅 stdlib（dataclasses / datetime / json / pathlib / argparse / unittest / tempfile）。
- **错误处理**：空白名称 → `ValueError`（模型层）；CLI 捕获后非 0 退出 + stderr 提示，且不落盘；存储层父目录自动创建、文件缺失返回空列表、跳过空行。
- **测试真实行为**：全部真实文件 IO（tempfile 隔离），CLI 测试用 `os.chdir` 隔离默认路径，校验了 JSONL 文件内容本身是合法逐行 JSON。
- **中文处理**：JSON 写入 `ensure_ascii=False`，文件统一 UTF-8。

## 关注点 / 偏差说明

1. **`python` 不在 PATH 上**（仅有 `python3`，3.12.3），故验证命令实际为 `python3 -m unittest discover` / `python3 -m todoapp ...`，与 brief 的 `python ...` 等价。未为创建 `python` 别名而改动系统。
2. **`created_at` 精度**：截断到秒（`replace(microsecond=0)`），格式为带 UTC 偏移的 ISO 8601（如 `2026-08-31T09:12:19+00:00`），可被 `datetime.fromisoformat` 解析。
3. **CLI 默认路径**：`cli.main` 按 brief 签名不含 path 参数，固定写工作目录下 `todos.jsonl`；测试中通过 `os.chdir` 到临时目录隔离。若后续任务需要可配置路径，可在 `create_todo` 已有的 `path` 参数上扩展。
4. 按环境约束未执行任何 git 写操作，全部新文件以 untracked 状态留在工作树中。

## Fix Round 1

针对评审发现的三项问题（Critical ×1、Important ×1、Minor ×1）完成修复。未执行任何 git 写操作。

### 变更明细

1. **Critical — 成功提示补充存储位置**：`todoapp/cli.py:29`
   - 旧：`print(f"已创建待办事项：{todo.name}")`
   - 新：`print(f"已创建待办事项：{todo.name}，已保存到 todos.jsonl")`
   - 依据：`cli.main` 按 brief 签名固定使用默认路径 `todos.jsonl`，故提示中直接给出该路径，满足「stdout 确认含 name 与存储位置」。
   - 同步扩展 `tests/test_todo.py` 的 `TestCli.test_add_success`，新增 `assertIn("todos.jsonl", out)`。

2. **Important — 新增真实 CLI 子进程测试**：`tests/test_todo.py`（新增 `TestCliSubprocess.test_add_via_real_subprocess`，并新增 `import subprocess` / `import sys`）
   - 通过 `subprocess.run([sys.executable, "-m", "todoapp", "add", "晨会", "9点开始"], cwd=<临时目录>, capture_output=True, text=True, env=...)` 走真实 `python -m todoapp` 入口，覆盖此前零覆盖的 `todoapp/__main__.py`。
   - 断言：退出码 0；stdout 含「晨会」与「todos.jsonl」；`<tmp>/todos.jsonl` 存在且恰有一行 JSONL，解析后 name/description 正确；行内无 `": "`（顺带验证紧凑 JSON）。
   - 子进程以 `cwd=tmp` 运行并注入 `PYTHONPATH=<仓库根>`，不扰动测试进程自身 cwd。

3. **Minor — 紧凑 JSON**：`todoapp/storage.py:18`
   - 旧：`json.dumps(asdict(todo), ensure_ascii=False)`
   - 新：`json.dumps(asdict(todo), ensure_ascii=False, separators=(",", ":"))`
   - 修复后行示例：`{"name":"测试事项","description":"这是描述","created_at":"2026-08-31T09:19:36+00:00"}`。
   - 按指示，cli.py 未捕获 OSError 的 Minor 项本轮仅记录、不修复（deferred）。

### 重跑测试

命令：`python3 -W error -m unittest discover -v`（加 `-W error` 以确保无任何警告，输出纯净）

结果：**9/9 全部通过，OK，无警告**。

```
test_add_success (tests.test_todo.TestCli.test_add_success) ... ok
test_add_with_empty_name_fails (tests.test_todo.TestCli.test_add_with_empty_name_fails) ... ok
test_add_via_real_subprocess (tests.test_todo.TestCliSubprocess.test_add_via_real_subprocess) ... ok
test_create_todo_returns_saved_todo (tests.test_todo.TestStorage.test_create_todo_returns_saved_todo) ... ok
test_load_empty_file_returns_empty_list (tests.test_todo.TestStorage.test_load_empty_file_returns_empty_list) ... ok
test_save_then_load_roundtrip_and_append (tests.test_todo.TestStorage.test_save_then_load_roundtrip_and_append) ... ok
test_blank_name_raises_value_error (tests.test_todo.TestTodoModel.test_blank_name_raises_value_error) ... ok
test_construct_with_name_and_description (tests.test_todo.TestTodoModel.test_construct_with_name_and_description) ... ok
test_empty_description_allowed (tests.test_todo.TestTodoModel.test_empty_description_allowed) ... ok

----------------------------------------------------------------------
Ran 9 tests in 0.039s

OK
```

### 真实 CLI 冒烟

命令：`python3 -m todoapp add "测试事项" "这是描述"`

stdout：`已创建待办事项：测试事项，已保存到 todos.jsonl`（exit 0）——**存储位置已出现在成功提示中**，与 name 同时满足约束。

落盘验证：`todos.jsonl` 内容为紧凑 JSONL：`{"name":"测试事项","description":"这是描述","created_at":"2026-08-31T09:19:36+00:00"}`。

**产物清理**：冒烟产生的 `todos.jsonl` 已 `rm` 删除，`ls todos.jsonl` 确认 "No such file or directory"。

### 遗留事项

- Minor（deferred，record-only）：`todoapp/cli.py` 对磁盘写入的 `OSError`（如磁盘满、权限不足）未捕获，会以 traceback 退出。按本轮指示暂不修复。
