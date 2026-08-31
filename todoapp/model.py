"""待办事项数据模型。"""

from __future__ import annotations

from dataclasses import dataclass, field
from datetime import datetime, timezone


@dataclass
class Todo:
    """一条待办事项。

    name: 名称（去空白后必须非空）。
    description: 描述，可为空串。
    created_at: 创建时间，ISO 8601 字符串（UTC，精确到秒）。
    """

    name: str
    description: str = ""
    created_at: str = field(
        default_factory=lambda: datetime.now(timezone.utc)
        .replace(microsecond=0)
        .isoformat()
    )

    def __post_init__(self) -> None:
        if not self.name.strip():
            raise ValueError("名称不能为空")
