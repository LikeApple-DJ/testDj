"""待办事项的 JSONL 持久化存储。"""

from __future__ import annotations

import json
from dataclasses import asdict
from pathlib import Path

from todoapp.model import Todo

DEFAULT_PATH = "todos.jsonl"


def save_todo(todo: Todo, path: str = DEFAULT_PATH) -> None:
    """将一条待办以 JSONL 形式追加写入 path（父目录自动创建）。"""
    target = Path(path)
    target.parent.mkdir(parents=True, exist_ok=True)
    line = json.dumps(asdict(todo), ensure_ascii=False, separators=(",", ":"))
    with target.open("a", encoding="utf-8") as f:
        f.write(line + "\n")


def load_todos(path: str = DEFAULT_PATH) -> list[Todo]:
    """读取并解析 path 中的全部待办；文件不存在时返回空列表。"""
    target = Path(path)
    if not target.exists():
        return []
    todos: list[Todo] = []
    with target.open("r", encoding="utf-8") as f:
        for line in f:
            line = line.strip()
            if not line:
                continue
            data = json.loads(line)
            todos.append(
                Todo(
                    name=data["name"],
                    description=data.get("description", ""),
                    created_at=data.get("created_at", ""),
                )
            )
    return todos


def create_todo(
    name: str, description: str = "", path: str = DEFAULT_PATH
) -> Todo:
    """组合「构造 + 校验 + 持久化」，返回已保存的 Todo。"""
    todo = Todo(name=name, description=description)
    save_todo(todo, path=path)
    return todo
