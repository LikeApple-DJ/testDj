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
