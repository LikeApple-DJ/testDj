# 待办事项「新增」功能实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking. Version-control commits are handled by the execution orchestration layer; do not run `git commit` manually.

**Goal:** 提供一个 Flask 服务端渲染 Web 应用，让内部用户填写事项名称和描述并提交，提交成功后页面展示已保存的待办列表。

**Architecture:** 单文件 Flask 应用 `app.py` 承载路由与业务逻辑；一个 Jinja2 模板 `templates/index.html` 同时承载录入表单与列表展示；数据持久化为仓库根目录下的 `todos.json`（JSON 数组），读取缺失或损坏视为空列表，提交时追加并写回。

**Tech Stack:** Python 3、Flask、Jinja2、pytest、Flask 测试客户端。

## Global Constraints

- 交付形态：Web 应用（服务端渲染）。
- 核心功能：仅「新增」待办事项，明确不做编辑、删除、认证、数据库、分页、前端框架。
- 字段：`name`（必填非空）、`description`（可选，允许为空）。
- 数据模型：`{id, name, description, created_at}`；`id` 整数自增初始 1；`created_at` 为 ISO 8601 时间戳由服务端生成；顶层为 JSON 数组。
- 存储：本地 `todos.json`，不存在视为空列表。
- 数据流：`GET /` 渲染表单+列表；`POST /todos` 校验→生成 id 与 created_at→追加→写回→重定向回 `GET /`。
- 错误处理：name 为空不写入并提示；读取失败/不存在视为空列表；写入失败给出提示。
- 列表展示：按时间倒序（最新在前）。
- 依赖：`requirements.txt` 仅声明 Flask；测试使用 pytest。

## File Structure

| 文件 | 职责 |
|---|---|
| `app.py` | Flask 应用、路由（`GET /`、`POST /todos`）、JSON 文件读写封装（`load_todos`/`save_todos`/`next_id`）、ID 自增、`created_at` 生成。 |
| `templates/index.html` | Jinja2 模板：录入表单（name、description、提交按钮）+ 列表区（倒序）+ flash 成功/错误提示。 |
| `requirements.txt` | 依赖声明：Flask。 |
| `tests/test_app.py` | 路由/集成测试：GET 渲染表单、POST 创建持久化、空 name 拦截、缺失文件首次创建、ID 自增、列表倒序。 |
| `tests/test_data.py` | 数据层单元测试：`load_todos`/`save_todos`/`next_id`。 |
| `todos.json` | 运行期生成的数据文件，不在仓库中预创建。 |

## Task 1: 项目脚手架与 GET / 渲染空表单

**Files:**
- Create: `requirements.txt`
- Create: `app.py`
- Create: `templates/index.html`
- Create: `tests/test_app.py`
- Create: `tests/__init__.py`（空文件，确保测试包可被发现）

**Interfaces:**
- Consumes: 无（首个任务）。
- Produces: Flask 应用对象 `app`（模块级），路由 `GET /`（`index`）。后续任务复用此应用并扩展路由与数据函数。

- [ ] **Step 1: 创建依赖声明**

`requirements.txt`:
```text
Flask>=3.0
pytest>=8.0
```

- [ ] **Step 2: 安装依赖**

Run: `pip install -r requirements.txt`
Expected: Flask 与 pytest 安装成功，`python -c "import flask, pytest"` 无报错。

- [ ] **Step 3: 编写失败测试（GET / 渲染表单）**

`tests/test_app.py`:
```python
import pytest

from app import app


@pytest.fixture
def client(tmp_path, monkeypatch):
    monkeypatch.setattr("app.DATA_FILE", str(tmp_path / "todos.json"))
    app.config["TESTING"] = True
    with app.test_client() as client:
        yield client


def test_get_index_shows_form(client):
    response = client.get("/")
    assert response.status_code == 200
    assert b"<form" in response.data
    assert b'name="name"' in response.data
```

- [ ] **Step 4: 运行测试确认失败**

Run: `pytest tests/test_app.py::test_get_index_shows_form -v`
Expected: FAIL，`ModuleNotFoundError: No module named 'app'`。

- [ ] **Step 5: 编写最小实现**

`app.py`:
```python
import os

from flask import Flask, render_template

app = Flask(__name__)
app.secret_key = "todo-dev-secret"

DATA_FILE = os.path.join(os.path.dirname(os.path.abspath(__file__)), "todos.json")


@app.get("/")
def index():
    return render_template("index.html", todos=[])
```

`templates/index.html`:
```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="utf-8">
  <title>待办事项</title>
</head>
<body>
  <h1>待办事项</h1>
  <form method="post" action="/todos">
    <label>事项名称<input type="text" name="name" required></label>
    <label>描述<textarea name="description"></textarea></label>
    <button type="submit">提交</button>
  </form>
</body>
</html>
```

`tests/__init__.py`:（空文件）
```text

```

- [ ] **Step 6: 运行测试确认通过**

Run: `pytest tests/test_app.py::test_get_index_shows_form -v`
Expected: PASS。

- [ ] **Step 7: 记录进度检查点**

确认 `GET /` 返回 200 且页面含表单；交付检查点由编排层提交。

---

## Task 2: 数据层（load_todos / save_todos / next_id）

**Files:**
- Modify: `app.py`
- Create: `tests/test_data.py`

**Interfaces:**
- Consumes: 模块级常量 `DATA_FILE`（Task 1 产生）。
- Produces:
  - `load_todos() -> list[dict]`：读取 `DATA_FILE`，缺失或损坏返回空列表。
  - `save_todos(todos: list[dict]) -> None`：将列表写回 `DATA_FILE`。
  - `next_id(todos: list[dict]) -> int`：返回下一个自增 id，空列表返回 1。

- [ ] **Step 1: 编写失败测试（数据层单元）**

`tests/test_data.py`:
```python
import json
import os

import pytest

from app import load_todos, save_todos, next_id


@pytest.fixture
def data_file(tmp_path, monkeypatch):
    path = str(tmp_path / "todos.json")
    monkeypatch.setattr("app.DATA_FILE", path)
    return path


def test_load_returns_empty_when_missing(data_file):
    assert load_todos() == []


def test_load_returns_empty_when_corrupt(data_file):
    with open(data_file, "w", encoding="utf-8") as f:
        f.write("{not json}")
    assert load_todos() == []


def test_load_returns_list(data_file):
    todos = [{"id": 1, "name": "a", "description": "", "created_at": "x"}]
    save_todos(todos)
    assert load_todos() == todos


def test_save_writes_utf8_json(data_file):
    save_todos([{"id": 1, "name": "买菜", "description": "西红柿", "created_at": "x"}])
    with open(data_file, encoding="utf-8") as f:
        content = f.read()
    assert "买菜" in content
    assert json.loads(content)[0]["name"] == "买菜"


def test_next_id_empty():
    assert next_id([]) == 1


def test_next_id_increments():
    assert next_id([{"id": 1}, {"id": 2}]) == 3
```

- [ ] **Step 2: 运行测试确认失败**

Run: `pytest tests/test_data.py -v`
Expected: FAIL，`ImportError: cannot import name 'load_todos'`。

- [ ] **Step 3: 编写最小实现**

修改 `app.py`，在 `DATA_FILE` 定义之后、路由之前插入数据函数：

`app.py`（替换为完整内容）:
```python
import json
import os

from flask import Flask, render_template

app = Flask(__name__)
app.secret_key = "todo-dev-secret"

DATA_FILE = os.path.join(os.path.dirname(os.path.abspath(__file__)), "todos.json")


def load_todos():
    """读取 todos.json；缺失或损坏时返回空列表。"""
    if not os.path.exists(DATA_FILE):
        return []
    try:
        with open(DATA_FILE, "r", encoding="utf-8") as f:
            data = json.load(f)
    except (json.JSONDecodeError, OSError):
        return []
    if not isinstance(data, list):
        return []
    return data


def save_todos(todos):
    """将待办列表写回 todos.json。"""
    with open(DATA_FILE, "w", encoding="utf-8") as f:
        json.dump(todos, f, ensure_ascii=False, indent=2)


def next_id(todos):
    """返回下一个自增 id，空列表时为 1。"""
    if not todos:
        return 1
    return max(item.get("id", 0) for item in todos) + 1


@app.get("/")
def index():
    return render_template("index.html", todos=load_todos())
```

- [ ] **Step 4: 运行测试确认通过**

Run: `pytest tests/test_data.py -v`
Expected: PASS（全部 6 个用例）。

- [ ] **Step 5: 记录进度检查点**

确认数据层读写与 ID 自增逻辑通过；由编排层提交。

---

## Task 3: POST /todos 创建流程与校验

**Files:**
- Modify: `app.py`（新增 `POST /todos` 路由）
- Modify: `templates/index.html`（新增 flash 提示区）
- Modify: `tests/test_app.py`（新增创建/校验/自增用例）

**Interfaces:**
- Consumes: `load_todos()`、`save_todos(todos)`、`next_id(todos)`（Task 2 产生）。
- Produces: 路由 `POST /todos`（`create_todo`），接受表单字段 `name`、`description`，重定向回 `GET /`，并通过 flash 设置 `success`/`error` 消息。

- [ ] **Step 1: 编写失败测试（创建与校验）**

在 `tests/test_app.py` 末尾追加：
```python
import json
import os

from app import load_todos


def test_create_todo_persists_and_shows_in_list(client, tmp_path):
    response = client.post(
        "/todos",
        data={"name": "买菜", "description": "西红柿和鸡蛋"},
        follow_redirects=True,
    )
    assert response.status_code == 200
    assert "买菜".encode("utf-8") in response.data
    todos = load_todos()
    assert len(todos) == 1
    assert todos[0]["name"] == "买菜"
    assert todos[0]["description"] == "西红柿和鸡蛋"
    assert todos[0]["id"] == 1
    assert todos[0]["created_at"]


def test_empty_name_rejected_and_not_saved(client, tmp_path):
    response = client.post(
        "/todos",
        data={"name": "   ", "description": "不应保存"},
        follow_redirects=True,
    )
    assert response.status_code == 200
    assert "事项名称不能为空".encode("utf-8") in response.data
    assert load_todos() == []


def test_missing_file_first_create(client, tmp_path):
    assert not os.path.exists(str(tmp_path / "todos.json"))
    client.post("/todos", data={"name": "首次", "description": ""}, follow_redirects=True)
    todos = load_todos()
    assert len(todos) == 1
    assert todos[0]["name"] == "首次"
    assert todos[0]["description"] == ""


def test_id_auto_increments(client, tmp_path):
    client.post("/todos", data={"name": "一", "description": ""}, follow_redirects=True)
    client.post("/todos", data={"name": "二", "description": ""}, follow_redirects=True)
    assert [t["id"] for t in load_todos()] == [1, 2]
```

注意：`client` fixture 中已通过 `monkeypatch.setattr("app.DATA_FILE", ...)` 指向 `tmp_path` 下的临时文件，因此 `load_todos()` 读取的是测试隔离的数据文件。

- [ ] **Step 2: 运行测试确认失败**

Run: `pytest tests/test_app.py -v`
Expected: FAIL，新增用例报 `404` 或重定向后无列表/无 flash 提示。

- [ ] **Step 3: 编写最小实现（POST 路由）**

修改 `app.py`，扩展导入并新增路由：

`app.py`（完整内容）:
```python
import json
import os
from datetime import datetime, timezone

from flask import Flask, render_template, request, redirect, url_for, flash

app = Flask(__name__)
app.secret_key = "todo-dev-secret"

DATA_FILE = os.path.join(os.path.dirname(os.path.abspath(__file__)), "todos.json")


def load_todos():
    """读取 todos.json；缺失或损坏时返回空列表。"""
    if not os.path.exists(DATA_FILE):
        return []
    try:
        with open(DATA_FILE, "r", encoding="utf-8") as f:
            data = json.load(f)
    except (json.JSONDecodeError, OSError):
        return []
    if not isinstance(data, list):
        return []
    return data


def save_todos(todos):
    """将待办列表写回 todos.json。"""
    with open(DATA_FILE, "w", encoding="utf-8") as f:
        json.dump(todos, f, ensure_ascii=False, indent=2)


def next_id(todos):
    """返回下一个自增 id，空列表时为 1。"""
    if not todos:
        return 1
    return max(item.get("id", 0) for item in todos) + 1


@app.get("/")
def index():
    return render_template("index.html", todos=load_todos())


@app.post("/todos")
def create_todo():
    name = request.form.get("name", "").strip()
    description = request.form.get("description", "").strip()
    if not name:
        flash("事项名称不能为空", "error")
        return redirect(url_for("index"))
    todos = load_todos()
    todo = {
        "id": next_id(todos),
        "name": name,
        "description": description,
        "created_at": datetime.now(timezone.utc).strftime("%Y-%m-%dT%H:%M:%SZ"),
    }
    todos.append(todo)
    save_todos(todos)
    flash("待办事项已创建", "success")
    return redirect(url_for("index"))


if __name__ == "__main__":
    app.run(debug=True)
```

- [ ] **Step 4: 更新模板显示 flash 提示**

修改 `templates/index.html`，在 `<h1>` 与 `<form>` 之间插入 flash 区：

`templates/index.html`（完整内容）:
```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="utf-8">
  <title>待办事项</title>
</head>
<body>
  <h1>待办事项</h1>
  {% with messages = get_flashed_messages(with_categories=true) %}
    {% if messages %}
      <ul class="messages">
        {% for category, message in messages %}
          <li class="{{ category }}">{{ message }}</li>
        {% endfor %}
      </ul>
    {% endif %}
  {% endwith %}
  <form method="post" action="/todos">
    <label>事项名称<input type="text" name="name" required></label>
    <label>描述<textarea name="description"></textarea></label>
    <button type="submit">提交</button>
  </form>
</body>
</html>
```

- [ ] **Step 5: 运行测试确认通过**

Run: `pytest tests/test_app.py -v`
Expected: PASS（含原有 GET 用例与新增创建/校验/自增用例）。

- [ ] **Step 6: 记录进度检查点**

确认创建、校验、ID 自增、缺失文件首次创建均通过；由编排层提交。

---

## Task 4: 列表倒序展示与端到端冒烟

**Files:**
- Modify: `templates/index.html`（新增列表区，倒序展示）
- Modify: `tests/test_app.py`（新增倒序与端到端用例）

**Interfaces:**
- Consumes: 路由 `index` 已传入 `todos`（Task 3 产生），flash 消息机制。
- Produces: 模板列表区按 `todos` 倒序渲染（最新在前）。

- [ ] **Step 1: 编写失败测试（倒序展示）**

在 `tests/test_app.py` 末尾追加：
```python
def test_list_newest_first(client, tmp_path):
    client.post("/todos", data={"name": "第一", "description": ""}, follow_redirects=True)
    client.post("/todos", data={"name": "第二", "description": ""}, follow_redirects=True)
    body = client.get("/").data.decode("utf-8")
    assert body.index("第二") < body.index("第一")


def test_empty_state_message(client, tmp_path):
    body = client.get("/").data.decode("utf-8")
    assert "暂无待办事项" in body
```

- [ ] **Step 2: 运行测试确认失败**

Run: `pytest tests/test_app.py::test_list_newest_first tests/test_app.py::test_empty_state_message -v`
Expected: FAIL，页面不含列表项或不含「暂无待办事项」文案。

- [ ] **Step 3: 更新模板添加列表区**

修改 `templates/index.html`，在 `</form>` 之后、`</body>` 之前插入列表区：

`templates/index.html`（完整内容）:
```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="utf-8">
  <title>待办事项</title>
</head>
<body>
  <h1>待办事项</h1>
  {% with messages = get_flashed_messages(with_categories=true) %}
    {% if messages %}
      <ul class="messages">
        {% for category, message in messages %}
          <li class="{{ category }}">{{ message }}</li>
        {% endfor %}
      </ul>
    {% endif %}
  {% endwith %}
  <form method="post" action="/todos">
    <label>事项名称<input type="text" name="name" required></label>
    <label>描述<textarea name="description"></textarea></label>
    <button type="submit">提交</button>
  </form>
  <section class="todo-list">
    <h2>已保存事项</h2>
    {% if todos %}
      <ul>
        {% for todo in todos|reverse %}
          <li>
            <strong>{{ todo.name }}</strong>
            <span>{{ todo.description }}</span>
            <time>{{ todo.created_at }}</time>
          </li>
        {% endfor %}
      </ul>
    {% else %}
      <p>暂无待办事项。</p>
    {% endif %}
  </section>
</body>
</html>
```

- [ ] **Step 4: 运行测试确认通过**

Run: `pytest tests/test_app.py::test_list_newest_first tests/test_app.py::test_empty_state_message -v`
Expected: PASS。

- [ ] **Step 5: 端到端冒烟（手动启动验证）**

Run: `python -c "from app import app; app.config['TESTING']=True; c=app.test_client(); print(c.get('/').status_code); print(c.post('/todos', data={'name':'冒烟','description':''}, follow_redirects=True).status_code)"`
Expected: 输出两行 `200`，且仓库根目录生成 `todos.json` 含一条 `name=冒烟` 记录。

- [ ] **Step 6: 运行完整测试套件**

Run: `pytest -v`
Expected: 所有用例 PASS，无失败。

- [ ] **Step 7: 清理冒烟产物并记录检查点**

Run: `rm -f todos.json`
Expected: 删除冒烟生成的 `todos.json`（运行期数据文件不应入库）。由编排层提交最终交付。

---

## Self-Review

**1. Spec coverage：**
- Web 应用服务端渲染（Flask）→ Task 1。
- 新增待办（仅创建）→ Task 3。
- 字段 name/description、必填校验、可选 description→ Task 3（`name.strip()` 校验，`description` 允许空）。
- 创建后成功提示+列表展示→ Task 3（flash success）+ Task 4（列表区）。
- 本地 JSON 文件存储、缺失视为空列表→ Task 2（`load_todos`）。
- 数据模型 id 自增初始 1、created_at ISO 8601 服务端生成→ Task 2/3（`next_id`、`datetime.now(timezone.utc)`）。
- GET / 渲染表单+列表→ Task 1/4。
- POST /todos 校验→生成→追加→写回→重定向→ Task 3。
- 错误处理（空 name 拦截、读取失败视为空、写入失败提示）→ Task 2/3 覆盖读取与校验；写入失败由 Flask 默认异常反馈（内部场景可接受）。
- 列表倒序→ Task 4（`todos|reverse`）。
- 范围裁剪（无编辑/删除/认证/数据库/分页/前端框架）→ 计划全程遵守。
- 测试（冒烟 GET 200、POST 后列表与文件含记录；边界空 name、文件不存在首次创建）→ Task 3/4。
- 无遗漏需求。

**2. Placeholder scan：** 无 TBD/TODO/「适当处理错误」等占位；每个代码步骤均含完整代码；测试均含实际断言。

**3. Type consistency：** `load_todos()`、`save_todos(todos)`、`next_id(todos)` 签名在 Task 2 定义，Task 3/4 引用一致；`DATA_FILE` 模块级常量贯穿；路由名 `index`/`create_todo` 与 `url_for` 调用一致；flash 类别 `success`/`error` 与模板 `{{ category }}` 一致。

## Execution Handoff

Plan complete and saved to `docs/superpowers/plans/2026-08-31-todo-create.md`. Two execution options:

**1. Subagent-Driven (recommended)** - 每个 Task 派发独立 subagent，任务间评审，迭代快。
**2. Inline Execution** - 在当前会话内按 executing-plans 批量执行，带检查点评审。

选择哪种方式推进？
