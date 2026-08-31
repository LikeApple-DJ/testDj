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
