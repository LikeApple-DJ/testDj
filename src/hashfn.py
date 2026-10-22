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
