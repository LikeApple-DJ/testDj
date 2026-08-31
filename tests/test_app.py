import json
import os

import pytest

from app import app, load_todos


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


def test_list_newest_first(client, tmp_path):
    client.post("/todos", data={"name": "第一", "description": ""}, follow_redirects=True)
    client.post("/todos", data={"name": "第二", "description": ""}, follow_redirects=True)
    body = client.get("/").data.decode("utf-8")
    assert body.index("第二") < body.index("第一")


def test_empty_state_message(client, tmp_path):
    body = client.get("/").data.decode("utf-8")
    assert "暂无待办事项" in body
