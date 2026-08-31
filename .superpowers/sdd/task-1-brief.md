## Task 1: 待办事项创建功能（模型 + 存储 + CLI + 测试）

**Files:**
- Create: `todoapp/__init__.py`
- Create: `todoapp/__main__.py`
- Create: `todoapp/model.py`
- Create: `todoapp/storage.py`
- Create: `todoapp/cli.py`
- Create: `tests/__init__.py`
- Create: `tests/test_todo.py`

**Interfaces:**

- `todoapp.model.Todo`：dataclass，字段 `name: str`、`description: str`、`created_at: str`；`__post_init__` 校验 `name` 去空白后非空。
- `todoapp.storage.save_todo(todo: Todo, path: str = "todos.jsonl") -> None`：以 JSONL 追加一行；父目录自动创建。
- `todoapp.storage.load_todos(path: str = "todos.jsonl") -> list[Todo]`：读取并解析全部待办，供测试与核验（非 CLI 对外功能，但最小闭环验证需要）。
- `todoapp.storage.create_todo(name: str, description: str = "", path: str = "todos.jsonl") -> Todo`：组合「构造 + 校验 + 持久化」的便捷函数，返回已保存的 Todo。
- `todoapp.cli.main(argv: list[str] | None = None) -> int`：argparse 解析 `add` 子命令，位置参数 `name`、`description`（description 可选，默认空串）；调用 `create_todo` 并打印确认；返回退出码。
- `todoapp.__main__`：`from todoapp.cli import main; raise SystemExit(main())`，使 `python -m todoapp` 可用。

**TDD 要求：** 严格 TDD——先写失败测试，再实现，再通过。

- [ ] **Step 1: 搭建包骨架与测试框架**
  创建空 `todoapp/__init__.py`、`tests/__init__.py`，确认 `python -m unittest discover` 可运行（0 测试时不报错）。

- [ ] **Step 2: 写失败测试（RED）**
  在 `tests/test_todo.py` 用 `unittest` 编写覆盖以下行为的测试（真实文件 IO，用 `tempfile` 隔离，不 mock）：
  1. `Todo(name="买菜", description="周末去超市")` 构造成功，字段正确，`created_at` 为非空 ISO 字符串。
  2. `Todo(name="   ")` 抛出 `ValueError`（空白名称非法）。
  3. `Todo(name="x", description="")` 允许空描述。
  4. `save_todo` 后 `load_todos` 能读回同名同描述的待办，且 `created_at` 一致；多次 `save_todo` 追加不覆盖既有记录。
  5. `create_todo("写报告", "周五前", path=tmp)` 返回的 Todo 与 `load_todos` 读回内容一致。
  6. `cli.main(["add", "晨会", "9点开始"])` 返回 0，stdout 含 `"晨会"`，且对应存储文件中出现该待办。
  7. `cli.main(["add", ""])` 返回非 0（名称非法），stdout/stderr 提示名称不能为空。
  运行 `python -m unittest discover` 应全部失败（实现尚未写）。

- [ ] **Step 3: 实现模型与存储（GREEN）**
  实现 `model.py`、`storage.py` 使数据层测试通过。

- [ ] **Step 4: 实现 CLI（GREEN）**
  实现 `cli.py`、`__main__.py` 使 CLI 测试通过。

- [ ] **Step 5: 全量验证**
  运行 `python -m unittest discover -v`，全部测试通过且输出无告警/噪声。
  运行真实 CLI 冒烟：`python -m todoapp add "测试事项" "这是描述"`，确认 stdout 打印确认信息、`todos.jsonl` 出现一行 JSON。
  冒烟产生的 `todos.jsonl` 在验证后删除（测试产物清理），交付物不含它。

- [ ] **Step 6: 自审**
  检查：仅创建闭环（无多余功能）、零第三方依赖、错误处理（文件 IO、非法名称）、命名清晰、测试验证真实行为。

---

## Self-Review

1. **Spec coverage:** 需求「新增待办事项（名称+描述），最小闭环仅创建」→ Task 1 完整覆盖（模型/存储/CLI/测试），无遗漏。
2. **Placeholder scan:** 无 TBD/TODO/占位符，所有步骤含具体命令与预期。
3. **Type consistency:** 单任务计划，接口签名一致，无跨任务类型冲突。
4. **YAGNI:** 严格限定仅创建，未引入 list/update/delete。
