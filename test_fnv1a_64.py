import unittest

from fnv1a_64 import fnv1a_64


class TestFnv1a64(unittest.TestCase):
    def test_empty_bytes(self):
        self.assertEqual(fnv1a_64(b""), 0xcbf29ce484222325)

    def test_known_vector_a(self):
        self.assertEqual(fnv1a_64(b"a"), 0xaf63dc4c8601ec8c)

    def test_known_vector_foobar(self):
        self.assertEqual(fnv1a_64(b"foobar"), 0x85944171f73967e8)

    def test_str_equals_bytes(self):
        s = "中文"
        self.assertEqual(fnv1a_64(s), fnv1a_64(s.encode("utf-8")))

    def test_non_ascii_known_vector(self):
        # "中文" 的 UTF-8 字节序列的 FNV-1a 64 位结果
        self.assertEqual(fnv1a_64("中文"), 0x514dcc99d8cadec5)

    def test_range(self):
        for v in (b"", b"a", b"foobar", "中文"):
            h = fnv1a_64(v)
            self.assertGreaterEqual(h, 0)
            self.assertLess(h, 2 ** 64)

    def test_rejects_int(self):
        with self.assertRaises(TypeError):
            fnv1a_64(123)

    def test_rejects_none(self):
        with self.assertRaises(TypeError):
            fnv1a_64(None)

    def test_rejects_list(self):
        with self.assertRaises(TypeError):
            fnv1a_64([])


if __name__ == "__main__":
    unittest.main()
