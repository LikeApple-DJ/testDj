"""Task 1 测试：Todo 模型、JSONL 存储、CLI add 子命令。

全部使用真实文件 IO（tempfile 隔离），不 mock。
"""

from __future__ import annotations

import contextlib
import io
import json
import os
import subprocess
import sys
import tempfile
import unittest
from datetime import datetime
from pathlib import Path

from todoapp import cli
from todoapp.model import Todo
from todoapp.storage import create_todo, load_todos, save_todo


class TestTodoModel(unittest.TestCase):
    def test_construct_with_name_and_description(self) -> None:
        todo = Todo(name="买菜", description="周末去超市")
        self.assertEqual(todo.name, "买菜")
        self.assertEqual(todo.description, "周末去超市")
        # created_at 为非空 ISO 字符串
        self.assertIsInstance(todo.created_at, str)
        self.assertTrue(todo.created_at.strip())
        datetime.fromisoformat(todo.created_at)

    def test_blank_name_raises_value_error(self) -> None:
        with self.assertRaises(ValueError):
            Todo(name="   ")
        with self.assertRaises(ValueError):
            Todo(name="")

    def test_empty_description_allowed(self) -> None:
        todo = Todo(name="x", description="")
        self.assertEqual(todo.name, "x")
        self.assertEqual(todo.description, "")


class TestStorage(unittest.TestCase):
    def setUp(self) -> None:
        self._tmp = tempfile.TemporaryDirectory()
        self.addCleanup(self._tmp.cleanup)
        self.path = str(Path(self._tmp.name) / "nested" / "todos.jsonl")

    def test_save_then_load_roundtrip_and_append(self) -> None:
        first = Todo(name="买菜", description="周末去超市")
        save_todo(first, path=self.path)  # 父目录应自动创建
        self.assertTrue(Path(self.path).is_file())

        second = Todo(name="写报告", description="")
        save_todo(second, path=self.path)

        loaded = load_todos(path=self.path)
        self.assertEqual(len(loaded), 2)  # 追加而非覆盖
        self.assertEqual(loaded[0].name, "买菜")
        self.assertEqual(loaded[0].description, "周末去超市")
        self.assertEqual(loaded[0].created_at, first.created_at)
        self.assertEqual(loaded[1].name, "写报告")
        self.assertEqual(loaded[1].description, "")
        self.assertEqual(loaded[1].created_at, second.created_at)

        # 文件内容确为 JSONL：一行一个合法 JSON
        lines = Path(self.path).read_text(encoding="utf-8").splitlines()
        self.assertEqual(len(lines), 2)
        for line in lines:
            json.loads(line)

    def test_load_empty_file_returns_empty_list(self) -> None:
        Path(self.path).parent.mkdir(parents=True)
        Path(self.path).touch()
        self.assertEqual(load_todos(path=self.path), [])

    def test_create_todo_returns_saved_todo(self) -> None:
        todo = create_todo("写报告", "周五前", path=self.path)
        self.assertEqual(todo.name, "写报告")
        self.assertEqual(todo.description, "周五前")

        loaded = load_todos(path=self.path)
        self.assertEqual(len(loaded), 1)
        self.assertEqual(loaded[0].name, todo.name)
        self.assertEqual(loaded[0].description, todo.description)
        self.assertEqual(loaded[0].created_at, todo.created_at)


class TestCli(unittest.TestCase):
    def setUp(self) -> None:
        self._tmp = tempfile.TemporaryDirectory()
        self.addCleanup(self._tmp.cleanup)
        # 在隔离临时目录中运行 CLI，使默认存储路径 todos.jsonl 落在其中
        self._cwd = os.getcwd()
        os.chdir(self._tmp.name)
        self.addCleanup(os.chdir, self._cwd)

    def _run_cli(self, argv: list[str]) -> tuple[int, str, str]:
        out, err = io.StringIO(), io.StringIO()
        with contextlib.redirect_stdout(out), contextlib.redirect_stderr(err):
            code = cli.main(argv)
        return code, out.getvalue(), err.getvalue()

    def test_add_success(self) -> None:
        code, out, _ = self._run_cli(["add", "晨会", "9点开始"])
        self.assertEqual(code, 0)
        self.assertIn("晨会", out)
        # 成功提示须同时包含名称与存储位置
        self.assertIn("todos.jsonl", out)

        loaded = load_todos(path="todos.jsonl")
        self.assertEqual(len(loaded), 1)
        self.assertEqual(loaded[0].name, "晨会")
        self.assertEqual(loaded[0].description, "9点开始")

    def test_add_with_empty_name_fails(self) -> None:
        code, out, err = self._run_cli(["add", ""])
        self.assertNotEqual(code, 0)
        self.assertIn("名称不能为空", out + err)
        # 非法输入不得落盘
        self.assertFalse(Path("todos.jsonl").exists())


class TestCliSubprocess(unittest.TestCase):
    """真实 CLI 子进程测试：经 `python -m todoapp` 入口（覆盖 __main__.py）。"""

    def test_add_via_real_subprocess(self) -> None:
        repo_root = Path(__file__).resolve().parents[1]
        with tempfile.TemporaryDirectory() as tmp:
            env = dict(os.environ)
            # 让子进程在任意 cwd 下都能 import todoapp
            env["PYTHONPATH"] = str(repo_root) + os.pathsep + env.get("PYTHONPATH", "")
            result = subprocess.run(
                [sys.executable, "-m", "todoapp", "add", "晨会", "9点开始"],
                cwd=tmp,
                capture_output=True,
                text=True,
                env=env,
            )
            self.assertEqual(result.returncode, 0, msg=result.stderr)
            # stdout 须同时包含名称与存储位置
            self.assertIn("晨会", result.stdout)
            self.assertIn("todos.jsonl", result.stdout)

            # JSONL 行落盘到 <tmp>/todos.jsonl
            jsonl = Path(tmp) / "todos.jsonl"
            self.assertTrue(jsonl.is_file())
            lines = jsonl.read_text(encoding="utf-8").splitlines()
            self.assertEqual(len(lines), 1)
            row = json.loads(lines[0])
            self.assertEqual(row["name"], "晨会")
            self.assertEqual(row["description"], "9点开始")
            # 紧凑 JSON：无 ": " 分隔符
            self.assertNotIn('": ', lines[0])


if __name__ == "__main__":
    unittest.main()
