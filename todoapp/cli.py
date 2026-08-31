"""命令行入口：`todoapp add <name> [description]`。"""

from __future__ import annotations

import argparse
import sys

from todoapp.storage import create_todo


def build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(prog="todoapp", description="极简待办事项应用")
    subparsers = parser.add_subparsers(dest="command", required=True)
    add = subparsers.add_parser("add", help="新增一条待办事项")
    add.add_argument("name", help="待办事项名称")
    add.add_argument("description", nargs="?", default="", help="待办事项描述（可选）")
    return parser


def main(argv: list[str] | None = None) -> int:
    """解析参数并执行子命令，返回退出码。"""
    args = build_parser().parse_args(argv)
    if args.command == "add":
        try:
            todo = create_todo(name=args.name, description=args.description)
        except ValueError as exc:
            print(f"错误：{exc}", file=sys.stderr)
            return 1
        print(f"已创建待办事项：{todo.name}，已保存到 todos.jsonl")
        return 0
    return 2  # pragma: no cover - subparsers required=True 保证不会到达
