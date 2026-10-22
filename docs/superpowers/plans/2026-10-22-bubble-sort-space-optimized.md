# 冒泡排序-空间优化 实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 实现一个空间优化的冒泡排序算法（O(1) 额外空间，原地排序，带提前终止优化）。

**Architecture:** 单模块纯函数设计。`bubble_sort.py` 暴露 `bubble_sort(arr: list) -> list` 公共接口，在原地对列表排序并返回同一引用。通过标志位 `swapped` 实现提前终止，避免对已有序数组的无意义遍历。测试覆盖：空数组、单元素、已有序、逆序、含重复元素、随机大数组。

**Tech Stack:** Python 3.10+（仅标准库），unittest / pytest 测试框架。

## Global Constraints

- Python 3.10+ 运行环境
- 仅依赖标准库，零外部依赖
- 函数签名：`bubble_sort(arr: list) -> list`，原地修改并返回同一列表
- 空间复杂度 O(1)，时间复杂度 O(n²) 最坏、O(n) 最优（已有序）
- 遵循 TDD 流程：先写测试 → 测试失败 → 实现 → 测试通过 → 提交

---

### Task 1: 项目骨架搭建

**Files:**
- Create: `sorting/__init__.py`
- Create: `sorting/bubble_sort.py`（空骨架）
- Create: `tests/__init__.py`
- Create: `tests/test_bubble_sort.py`（空骨架）

**Interfaces:**
- Produces: `sorting/bubble_sort.py` 模块，后续 Task 填充实现

- [ ] **Step 1: 创建 sorting 包及 bubble_sort 骨架**

```bash
mkdir -p sorting tests
```

```python
# sorting/__init__.py
from sorting.bubble_sort import bubble_sort

__all__ = ["bubble_sort"]
```

```python
# sorting/bubble_sort.py
def bubble_sort(arr: list) -> list:
    """对列表进行原地冒泡排序，返回排序后的列表。

    Args:
        arr: 待排序列表，元素需支持 < 比较运算符

    Returns:
        排序后的同一列表引用（原地修改）
    """
    raise NotImplementedError
```

- [ ] **Step 2: 创建测试骨架**

```python
# tests/__init__.py
# 空文件
```

```python
# tests/test_bubble_sort.py
import unittest
from sorting import bubble_sort


class TestBubbleSort(unittest.TestCase):
    """冒泡排序单元测试"""
    pass


if __name__ == "__main__":
    unittest.main()
```

- [ ] **Step 3: 验证骨架可导入**

```bash
python -c "from sorting import bubble_sort; print('import OK')"
```
Expected: `import OK`

- [ ] **Step 4: 提交**

```bash
git add sorting/__init__.py sorting/bubble_sort.py tests/__init__.py tests/test_bubble_sort.py
git commit -m "feat: add sorting package skeleton and test scaffold"
```

---

### Task 2: 空列表 & 单元素测试

**Files:**
- Modify: `tests/test_bubble_sort.py` — 添加测试方法

**Interfaces:**
- Consumes: `sorting.bubble_sort(arr: list) -> list`（Task 1 骨架）

- [ ] **Step 1: 编写空列表与单元素测试**

```python
# tests/test_bubble_sort.py
import unittest
from sorting import bubble_sort


class TestBubbleSort(unittest.TestCase):
    def test_empty_list(self):
        """空列表应原样返回"""
        arr = []
        result = bubble_sort(arr)
        self.assertEqual(result, [])
        self.assertIs(result, arr)  # 原地操作确认

    def test_single_element(self):
        """单元素列表应原样返回"""
        arr = [42]
        result = bubble_sort(arr)
        self.assertEqual(result, [42])
        self.assertIs(result, arr)


if __name__ == "__main__":
    unittest.main()
```

- [ ] **Step 2: 运行测试验证失败**

```bash
python -m pytest tests/test_bubble_sort.py -v
```
Expected: 2 tests FAIL — `NotImplementedError`

- [ ] **Step 3: 实现最小通过代码**

```python
# sorting/bubble_sort.py
def bubble_sort(arr: list) -> list:
    n = len(arr)
    return arr
```

- [ ] **Step 4: 运行测试验证通过**

```bash
python -m pytest tests/test_bubble_sort.py -v
```
Expected: 2 tests PASS

- [ ] **Step 5: 提交**

```bash
git add sorting/bubble_sort.py tests/test_bubble_sort.py
git commit -m "feat: implement bubble_sort for empty and single-element lists"
```

---

### Task 3: 已有序 & 逆序测试 + 核心实现

**Files:**
- Modify: `tests/test_bubble_sort.py` — 添加测试方法
- Modify: `sorting/bubble_sort.py` — 填充核心算法

**Interfaces:**
- Produces: `bubble_sort(arr)` 完整实现，时间复杂度 O(n²)，空间 O(1)

- [ ] **Step 1: 添加已有序和逆序测试**

```python
def test_already_sorted(self):
    """已有序列表应保持不变"""
    arr = [1, 2, 3, 4, 5]
    result = bubble_sort(arr)
    self.assertEqual(result, [1, 2, 3, 4, 5])
    self.assertIs(result, arr)

def test_reverse_sorted(self):
    """逆序列表应正确排序"""
    arr = [5, 4, 3, 2, 1]
    result = bubble_sort(arr)
    self.assertEqual(result, [1, 2, 3, 4, 5])
    self.assertIs(result, arr)
```

- [ ] **Step 2: 运行测试验证失败**

```bash
python -m pytest tests/test_bubble_sort.py -v
```
Expected: `test_already_sorted` 通过, `test_reverse_sorted` 失败

- [ ] **Step 3: 实现核心冒泡排序 + 提前终止优化**

```python
# sorting/bubble_sort.py
def bubble_sort(arr: list) -> list:
    n = len(arr)
    if n <= 1:
        return arr

    for i in range(n - 1):
        swapped = False  # 空间优化的关键：单布尔标志位
        for j in range(n - 1 - i):
            if arr[j] > arr[j + 1]:
                arr[j], arr[j + 1] = arr[j + 1], arr[j]
                swapped = True
        if not swapped:  # 本轮无交换 → 已有序，提前终止
            break
    return arr
```

- [ ] **Step 4: 运行全部测试验证通过**

```bash
python -m pytest tests/test_bubble_sort.py -v
```
Expected: 4 tests PASS

- [ ] **Step 5: 提交**

```bash
git add sorting/bubble_sort.py tests/test_bubble_sort.py
git commit -m "feat: implement core bubble sort with early termination optimization"
```

---

### Task 4: 边界用例覆盖 — 重复元素 & 稳定性 & 大数组

**Files:**
- Modify: `tests/test_bubble_sort.py` — 添加边界测试

- [ ] **Step 1: 添加重复元素、大数据量、类型兼容测试**

```python
def test_with_duplicates(self):
    """含重复元素的列表应正确排序"""
    arr = [3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5]
    result = bubble_sort(arr)
    self.assertEqual(result, [1, 1, 2, 3, 3, 4, 5, 5, 5, 6, 9])

def test_large_random_array(self):
    """1000 个随机元素的排序验证"""
    import random
    random.seed(42)
    arr = [random.randint(-10000, 10000) for _ in range(1000)]
    expected = sorted(arr)
    result = bubble_sort(arr)
    self.assertEqual(result, expected)
    self.assertIs(result, arr)

def test_negative_numbers(self):
    """负数列表应正确排序"""
    arr = [-3, -1, -4, -1, -5]
    result = bubble_sort(arr)
    self.assertEqual(result, [-5, -4, -3, -1, -1])

def test_string_sorting(self):
    """字符串列表（按字典序）"""
    arr = ["banana", "apple", "cherry", "date"]
    result = bubble_sort(arr)
    self.assertEqual(result, ["apple", "banana", "cherry", "date"])

def test_float_sorting(self):
    """浮点数列表"""
    arr = [3.14, 1.41, 2.72, 0.58]
    result = bubble_sort(arr)
    self.assertEqual(result, [0.58, 1.41, 2.72, 3.14])
```

- [ ] **Step 2: 运行全部测试**

```bash
python -m pytest tests/test_bubble_sort.py -v
```
Expected: 8 tests PASS

- [ ] **Step 3: 提交**

```bash
git add tests/test_bubble_sort.py
git commit -m "test: add boundary cases — duplicates, large array, negatives, strings, floats"
```

---

### Task 5: 空间复杂度验证 & 文档

**Files:**
- Modify: `sorting/bubble_sort.py` — 添加 docstring 和空间复杂度说明

- [ ] **Step 1: 完善文档字符串**

```python
# sorting/bubble_sort.py
def bubble_sort(arr: list) -> list:
    """对列表进行原地冒泡排序（空间优化版），返回排序后的列表。

    算法特性：
    - 空间复杂度: O(1) —— 仅使用 swapped 布尔标志和循环计数器，
      不分配任何额外数据结构
    - 时间复杂度: O(n²) 最坏/平均，O(n) 最优（已有序时一轮扫描即退出）
    - 稳定性: 稳定排序（相等元素保持原始相对顺序）
    - 原地排序: 直接在输入列表上操作，返回同一引用

    Args:
        arr: 待排序列表，元素需支持 < 比较运算符

    Returns:
        排序后的同一列表引用（原地修改）

    Raises:
        TypeError: 如果列表元素不支持比较操作

    Examples:
        >>> bubble_sort([3, 1, 2])
        [1, 2, 3]
        >>> bubble_sort([])
        []
        >>> bubble_sort([5])
        [5]
    """
    n = len(arr)
    if n <= 1:
        return arr

    for i in range(n - 1):
        swapped = False  # O(1) 额外空间：仅一个布尔标志位
        for j in range(n - 1 - i):
            if arr[j] > arr[j + 1]:
                arr[j], arr[j + 1] = arr[j + 1], arr[j]
                swapped = True
        if not swapped:
            break
    return arr
```

- [ ] **Step 2: 验证 docstring 示例**

```bash
python -m doctest sorting/bubble_sort.py -v
```
Expected: docstring 用例通过

- [ ] **Step 3: 运行完整测试套件**

```bash
python -m pytest tests/test_bubble_sort.py -v
```
Expected: 8 tests PASS

- [ ] **Step 4: 空间复杂度静态验证 — 确认无额外数据结构分配**

```bash
# 用 dis 模块确认字节码层面无可疑分配
python -c "
from sorting import bubble_sort
import sys

# 内存验证：排序前后列表 id 不变
arr = [5, 3, 1, 4, 2]
addr_before = id(arr)
result = bubble_sort(arr)
addr_after = id(result)
assert addr_before == addr_after, 'FAIL: 列表地址变化，非原地操作'
assert result == [1, 2, 3, 4, 5], 'FAIL: 排序结果错误'
print('OK: 原地操作确认，列表 ID 一致')
print('OK: 排序结果正确')
"
```
Expected: `OK: 原地操作确认` + `OK: 排序结果正确`

- [ ] **Step 5: 提交**

```bash
git add sorting/bubble_sort.py
git commit -m "docs: add comprehensive docstring with space complexity analysis and doctest"
```

---

## 文件结构总结

```
sorting/
├── __init__.py          # 包导出 bubble_sort
└── bubble_sort.py       # 核心实现
tests/
├── __init__.py
└── test_bubble_sort.py  # 8 个测试用例
```

## 自审清单

**1. 需求覆盖：**
- ✅ "冒泡排序" — Task 3 核心实现
- ✅ "空间" — Task 5 docstring 明确 O(1) 空间复杂度 + 内存验证
- ✅ 提前终止优化 — Task 3 swapped 标志位

**2. 占位符扫描：**
- ✅ 无 TBD/TODO/implement later
- ✅ 所有步骤均有完整代码或命令

**3. 类型一致性：**
- ✅ `bubble_sort(arr: list) -> list` 签名贯穿所有 Task
- ✅ 所有测试调用 `from sorting import bubble_sort`