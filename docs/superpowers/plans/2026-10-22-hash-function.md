# FNV-1a Hash Function 实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 实现一个 FNV-1a 哈希函数，支持字符串与字节输入的 32 位与 64 位哈希值计算。

**Architecture:** 单模块实现，包含 `hash_fnv1a` 和 `hash_fnv1a_64` 两个公开函数。核心逻辑为逐字节迭代异或乘法，无外部依赖。模块同时提供独立运行入口，支持命令行输入。

**Tech Stack:** Python 3.8+，标准库 `struct` 模块（可选，用于 64 位溢出模拟），`pytest` 用于测试。

## Global Constraints

- Python 版本不低于 3.8
- 零外部依赖（除开发期 `pytest` 外）
- 遵循 FNV-1a 标准算法：`hash = (hash XOR byte) * FNV_prime`
- 32 位：offset_basis = 0x811c9dc5, prime = 0x01000193
- 64 位：offset_basis = 0xcbf29ce484222325, prime = 0x00000100000001b3
- 输入统一编码为 UTF-8 字节序列

---

## 文件结构

| 文件 | 职责 |
|------|------|
| `src/hashfn.py` | FNV-1a 哈希函数实现（32/64 位） |
| `tests/test_hashfn.py` | 单元测试，覆盖空字符串、已知向量、边界条件 |
| `src/__init__.py` | 空文件，使 `src` 成为 Python 包 |
| `tests/__init__.py` | 空文件，使 `tests` 成为 Python 包 |

---

### Task 1: 编写测试用例

**Files:**
- Create: `src/__init__.py`
- Create: `tests/__init__.py`
- Create: `tests/test_hashfn.py`

**Interfaces:**
- Produces: 导入 `src.hashfn.hash_fnv1a` 和 `src.hashfn.hash_fnv1a_64`，两者接受 `str` 返回 `int`

- [ ] **Step 1: 创建包结构文件**

```bash
mkdir -p src tests
touch src/__init__.py tests/__init__.py
```

- [ ] **Step 2: 编写测试文件 `tests/test_hashfn.py`**

```python
import pytest
from src.hashfn import hash_fnv1a, hash_fnv1a_64


class TestHashFnv1a32:
    """32-bit FNV-1a hash tests."""

    def test_empty_string(self):
        """空字符串返回 offset_basis (0x811c9dc5)。"""
        assert hash_fnv1a("") == 0x811c9dc5

    def test_known_vector_a(self):
        """已知向量: "a" -> 0xe40c292c"""
        assert hash_fnv1a("a") == 0xe40c292c

    def test_known_vector_foobar(self):
        """已知向量: "foobar" -> 0xbf9cf968"""
        assert hash_fnv1a("foobar") == 0xbf9cf968

    def test_chinese_text(self):
        """中文输入正确处理（UTF-8 编码后哈希）。"""
        assert hash_fnv1a("你好") == 0x187c3ff2

    def test_unicode_emoji(self):
        """多字节 emoji 输入正确处理。"""
        result = hash_fnv1a("🚀")
        assert isinstance(result, int)
        assert 0 <= result < 2 ** 32


class TestHashFnv1a64:
    """64-bit FNV-1a hash tests."""

    def test_empty_string(self):
        """空字符串返回 64 位 offset_basis。"""
        assert hash_fnv1a_64("") == 0xcbf29ce484222325

    def test_known_vector_a(self):
        """已知向量: "a" -> 0xaf63dc4c8601ec8c"""
        assert hash_fnv1a_64("a") == 0xaf63dc4c8601ec8c

    def test_known_vector_foobar(self):
        """已知向量: "foobar" -> 0x85944171f73967e8"""
        assert hash_fnv1a_64("foobar") == 0x85944171f73967e8

    def test_64bit_range(self):
        """64 位哈希结果在 [0, 2^64) 范围内。"""
        result = hash_fnv1a_64("hello world")
        assert isinstance(result, int)
        assert 0 <= result < 2 ** 64


class TestEdgeCases:
    """边界与一致性测试。"""

    def test_deterministic(self):
        """相同输入产生相同输出。"""
        for _ in range(100):
            assert hash_fnv1a("test") == hash_fnv1a("test")

    def test_avalanche(self):
        """微小输入变化导致输出显著不同。"""
        h1 = hash_fnv1a("abc")
        h2 = hash_fnv1a("abd")
        # 至少有一位不同
        assert h1 != h2

    def test_long_input(self):
        """长输入不引发异常。"""
        result = hash_fnv1a("x" * 10000)
        assert isinstance(result, int)
        assert 0 <= result < 2 ** 32

    def test_non_ascii_bytes_consistency(self):
        """相同 Unicode 字符串的 32/64 位结果各自确定。"""
        s = "café–résumé"
        assert hash_fnv1a(s) == hash_fnv1a(s)
        assert hash_fnv1a_64(s) == hash_fnv1a_64(s)
```

- [ ] **Step 3: 运行测试验证全部失败**

```bash
python -m pytest tests/test_hashfn.py -v
```

Expected: 全部 12 个测试 FAIL，报 `ModuleNotFoundError: No module named 'src.hashfn'`

---

### Task 2: 实现 FNV-1a 哈希函数

**Files:**
- Create: `src/hashfn.py`

**Interfaces:**
- Produces: `hash_fnv1a(s: str) -> int` — 32 位 FNV-1a 哈希
- Produces: `hash_fnv1a_64(s: str) -> int` — 64 位 FNV-1a 哈希

- [ ] **Step 1: 编写 `src/hashfn.py`**

```python
"""
FNV-1a 哈希函数实现。

支持 32 位和 64 位 FNV-1a 哈希，输入字符串按 UTF-8 编码为字节序列后计算。
"""

_FNV32_OFFSET_BASIS = 0x811c9dc5
_FNV32_PRIME = 0x01000193
_FNV32_MOD = 2 ** 32

_FNV64_OFFSET_BASIS = 0xcbf29ce484222325
_FNV64_PRIME = 0x00000100000001b3
_FNV64_MOD = 2 ** 64


def hash_fnv1a(data: str) -> int:
    """计算输入字符串的 32 位 FNV-1a 哈希值。

    Args:
        data: 待哈希的字符串。

    Returns:
        [0, 2^32) 范围内的整数哈希值。

    Examples:
        >>> hash_fnv1a("")
        2166136261
        >>> hash_fnv1a("a")
        3826002220
    """
    hash_value = _FNV32_OFFSET_BASIS
    for byte in data.encode("utf-8"):
        hash_value ^= byte
        hash_value = (hash_value * _FNV32_PRIME) % _FNV32_MOD
    return hash_value


def hash_fnv1a_64(data: str) -> int:
    """计算输入字符串的 64 位 FNV-1a 哈希值。

    Args:
        data: 待哈希的字符串。

    Returns:
        [0, 2^64) 范围内的整数哈希值。

    Examples:
        >>> hash_fnv1a_64("")
        14695981039346656037
    """
    hash_value = _FNV64_OFFSET_BASIS
    for byte in data.encode("utf-8"):
        hash_value ^= byte
        hash_value = (hash_value * _FNV64_PRIME) % _FNV64_MOD
    return hash_value


if __name__ == "__main__":
    import sys

    if len(sys.argv) > 1:
        text = " ".join(sys.argv[1:])
        h32 = hash_fnv1a(text)
        h64 = hash_fnv1a_64(text)
        print(f"Input:   {text}")
        print(f"FNV-1a (32-bit): {h32:08x} ({h32})")
        print(f"FNV-1a (64-bit): {h64:016x} ({h64})")
    else:
        print("Usage: python -m src.hashfn <text to hash>")
```

- [ ] **Step 2: 运行测试**

```bash
python -m pytest tests/test_hashfn.py -v
```

Expected: 全部 12 个测试 PASS

- [ ] **Step 3: 运行 doctest**

```bash
python -m doctest src/hashfn.py -v
```

Expected: doctest PASS

- [ ] **Step 4: 命令行手动验证**

```bash
python src/hashfn.py "hello"
```

Expected: 输出 32 位和 64 位哈希值的 hex 与十进制表示

---

### Task 3: 最终验证与提交

- [ ] **Step 1: 运行完整测试套件**

```bash
python -m pytest tests/ -v --tb=short
```

Expected: 12 passed

- [ ] **Step 2: 提交**

```bash
git add src/ tests/ docs/superpowers/plans/2026-10-22-hash-function.md
git commit -m "feat: add FNV-1a hash function (32-bit and 64-bit)"
```