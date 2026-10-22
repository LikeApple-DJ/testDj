_FNV_OFFSET_BASIS_64 = 14695981039346656037
_FNV_PRIME_64 = 1099511628211
_MASK_64 = 0xFFFFFFFFFFFFFFFF


def fnv1a_64(data):
    """FNV-1a 64 位通用哈希。

    支持 str 与 bytes；str 按 UTF-8 编码。返回 0~2^64-1 的无符号整数。
    非密码学哈希，不得用于安全/完整性校验。
    """
    if isinstance(data, str):
        data = data.encode("utf-8")
    elif not isinstance(data, bytes):
        raise TypeError(
            "fnv1a_64 仅支持 str 或 bytes，收到: {}".format(type(data).__name__)
        )

    h = _FNV_OFFSET_BASIS_64
    for b in data:
        h ^= b
        h = (h * _FNV_PRIME_64) & _MASK_64
    return h
