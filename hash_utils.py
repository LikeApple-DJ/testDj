"""
字符串哈希工具模块 —— 用于散列存储/哈希表场景

基于 Python 标准库实现，提供多种哈希方式：
  1. builtin_hash  — Python 内置 hash()，快速但进程间不持久
  2. fnv_hash      — FNV-1a 算法（纯 Python 实现，可持久化）
  3. md5_hash      — hashlib.md5 摘要转整数
  4. sha256_hash   — hashlib.sha256 摘要转整数
"""

import hashlib


def builtin_hash(s: str) -> int:
    """Python 内置 hash，适合单进程哈希表使用。
    注意：不同进程/重启后值可能不同。
    """
    return hash(s)


def fnv_hash(s: str, bits: int = 64) -> int:
    """FNV-1a 哈希 —— 简单、快速、分布均匀，结果可跨进程持久。

    参数:
        s: 输入字符串
        bits: 输出位数，支持 32 或 64
    返回:
        非负整数哈希值
    """
    if bits == 32:
        prime = 0x01000193
        offset = 0x811C9DC5
        mask = 0xFFFFFFFF
    else:  # 64
        prime = 0x100000001B3
        offset = 0xCBF29CE484222325
        mask = 0xFFFFFFFFFFFFFFFF

    h = offset
    for byte in s.encode("utf-8"):
        h ^= byte
        h = (h * prime) & mask
    return h


def md5_hash(s: str) -> int:
    """MD5 摘要转整数，适合一般散列存储。"""
    return int(hashlib.md5(s.encode("utf-8")).hexdigest(), 16)


def sha256_hash(s: str) -> int:
    """SHA-256 摘要转整数，碰撞概率极低。"""
    return int(hashlib.sha256(s.encode("utf-8")).hexdigest(), 16)


def hash_to_bucket(s: str, num_buckets: int, method: str = "fnv") -> int:
    """将字符串哈希到 [0, num_buckets) 的桶中 —— 直接用于哈希表散列存储。

    参数:
        s: 输入字符串
        num_buckets: 桶的数量
        method: 哈希方法，可选 "builtin" / "fnv" / "md5" / "sha256"
    返回:
        桶索引（0 到 num_buckets-1）
    """
    if num_buckets <= 0:
        raise ValueError("num_buckets 必须大于 0")

    methods = {
        "builtin": builtin_hash,
        "fnv": fnv_hash,
        "md5": md5_hash,
        "sha256": sha256_hash,
    }
    if method not in methods:
        raise ValueError(f"不支持的哈希方法: {method}，可选: {list(methods.keys())}")

    h = methods[method](s)
    return h % num_buckets


# ---------- 使用示例 ----------
if __name__ == "__main__":
    test_strings = ["hello", "world", "hello world", "hash_utils", "test"]

    print("=" * 60)
    print("字符串哈希演示")
    print("=" * 60)

    for s in test_strings:
        print(f"\n字符串: {s!r}")
        print(f"  builtin: {builtin_hash(s)}")
        print(f"  fnv-64:  {fnv_hash(s)}")
        print(f"  md5:     {md5_hash(s)}")
        print(f"  sha256:  {sha256_hash(s)}")

    print("\n" + "=" * 60)
    print("哈希表散列演示（4 个桶）")
    print("=" * 60)
    for s in test_strings:
        bucket = hash_to_bucket(s, 4, method="fnv")
        print(f"  {s!r:15s} -> 桶 {bucket}")