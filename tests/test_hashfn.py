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
        assert hash_fnv1a("你好") == 0x86938da3

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
