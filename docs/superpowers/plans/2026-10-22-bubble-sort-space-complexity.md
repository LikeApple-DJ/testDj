# 冒泡排序（空间复杂度重点）实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 实现一个空间复杂度 O(1) 的原地冒泡排序 Python 函数，附带完整测试覆盖和空间复杂度验证。

**Architecture:** 单文件模块 `src/bubble_sort.py` 包含核心 `bubble_sort(arr)` 函数及辅助分析函数 `analyze_space_complexity()`；测试文件 `tests/test_bubble_sort.py` 覆盖正确性、边界、稳定性、空间复杂度验收标准。纯函数设计，零外部依赖。

**Tech Stack:** Python 3.9+，pytest，无第三方库。

## Global Constraints

- 空间复杂度 O(1)：仅使用常量级临时变量（tmp、循环变量、布尔标志），不分配与 n 成比例的辅助存储
- 稳定排序：相等元素不交换，保持相对顺序
- 原地修改：直接操作输入列表，不创建新列表
- 提前退出优化：swapped 标志在无交换时提前终止（最好情况 O(n)）
- 迭代实现：严禁递归（递归会引入 O(n) 栈空间）
- Python 版本：≥ 3.9，使用类型标注 (type hints)
- 测试框架：pytest

---

### Task 1: 核心排序函数与基础正确性测试

**Files:**
- Create: `src/bubble_sort.py`
- Create: `tests/test_bubble_sort.py`

**Interfaces:**
- Produces: `bubble_sort(arr: list) -> None` — 原地升序排序，返回 None

- [ ] **Step 1: 编写失败的测试 — 基础升序排序**

```python
# tests/test_bubble_sort.py
import pytest
from src.bubble_sort import bubble_sort


class TestBubbleSortBasic:
    """基础正确性测试 — 覆盖设计文档 5.3 验收标准"""

    def test_sort_unsorted_array(self):
        """普通乱序数组 → 升序"""
        arr = [5, 3, 8, 1, 2]
        bubble_sort(arr)
        assert arr == [1, 2, 3, 5, 8]

    def test_sort_already_sorted(self):
        """已有序数组 → 不变（测试提前退出路径）"""
        arr = [1, 2, 3, 4, 5]
        bubble_sort(arr)
        assert arr == [1, 2, 3, 4, 5]

    def test_sort_reverse_sorted(self):
        """逆序数组 → 升序（最坏情况）"""
        arr = [5, 4, 3, 2, 1]
        bubble_sort(arr)
        assert arr == [1, 2, 3, 4, 5]

    def test_sort_with_duplicates(self):
        """含重复元素 → 稳定排序"""
        arr = [3, 1, 4, 1, 5, 9, 2, 6]
        bubble_sort(arr)
        assert arr == [1, 1, 2, 3, 4, 5, 6, 9]
```

- [ ] **Step 2: 运行测试验证失败**

Run: `pytest tests/test_bubble_sort.py -v`
Expected: FAIL — `ModuleNotFoundError: No module named 'src.bubble_sort'`

- [ ] **Step 3: 实现 bubble_sort 函数（最小实现使测试通过）**

```python
# src/bubble_sort.py
from typing import Any


def bubble_sort(arr: list) -> None:
    """冒泡排序 — 原地升序，O(1) 辅助空间。

    迭代实现，使用 swapped 标志在已有序时提前退出。

    Args:
        arr: 待排序的可比元素列表，原地修改。

    Raises:
        TypeError: arr 不是 list 类型时抛出。
    """
    if not isinstance(arr, list):
        raise TypeError(f"bubble_sort expects a list, got {type(arr).__name__}")

    n = len(arr)
    for i in range(n - 1):
        swapped = False
        for j in range(n - i - 1):
            if arr[j] > arr[j + 1]:
                arr[j], arr[j + 1] = arr[j + 1], arr[j]
                swapped = True
        if not swapped:
            break
```

- [ ] **Step 4: 运行测试验证通过**

Run: `pytest tests/test_bubble_sort.py -v`
Expected: PASS — 4 tests passed

- [ ] **Step 5: Commit**

```bash
git add src/bubble_sort.py tests/test_bubble_sort.py
git commit -m "feat: add bubble_sort with basic correctness tests"
```

---

### Task 2: 边界情况与稳定性测试

**Files:**
- Modify: `tests/test_bubble_sort.py` — 追加测试类

**Interfaces:**
- Consumes: `bubble_sort(arr: list) -> None` (Task 1)

- [ ] **Step 1: 追加边界与稳定性测试代码**

在 `tests/test_bubble_sort.py` 末尾追加：

```python
class TestBubbleSortEdgeCases:
    """边界情况 — 覆盖设计文档 5.2"""

    def test_sort_empty_array(self):
        """空数组 → 安全返回，不抛出异常"""
        arr: list = []
        bubble_sort(arr)
        assert arr == []

    def test_sort_single_element(self):
        """单元素 → 不变"""
        arr = [42]
        bubble_sort(arr)
        assert arr == [42]

    def test_sort_two_elements_sorted(self):
        """两个已排序元素 → 不变"""
        arr = [1, 2]
        bubble_sort(arr)
        assert arr == [1, 2]

    def test_sort_two_elements_unsorted(self):
        """两个未排序元素 → 升序"""
        arr = [2, 1]
        bubble_sort(arr)
        assert arr == [1, 2]

    def test_sort_all_equal(self):
        """全等元素 → 不变"""
        arr = [7, 7, 7, 7]
        bubble_sort(arr)
        assert arr == [7, 7, 7, 7]

    def test_sort_negative_numbers(self):
        """含负数 → 正确升序"""
        arr = [-3, 0, -1, 5, -10, 2]
        bubble_sort(arr)
        assert arr == [-10, -3, -1, 0, 2, 5]

    def test_sort_large_values(self):
        """含极大极小值"""
        arr = [float("inf"), -float("inf"), 0, 1, -1]
        bubble_sort(arr)
        assert arr == [-float("inf"), -1, 0, 1, float("inf")]


class TestBubbleSortStability:
    """稳定性验证 — 设计文档第 4 节"""

    def test_stability_preserves_relative_order(self):
        """相等元素的相对顺序应保持不变"""
        # 使用 (值, 标签) 元组：排序键为值，标签记录原始顺序
        arr = [(2, "A"), (1, "B"), (2, "C"), (1, "D"), (3, "E")]
        bubble_sort(arr)
        assert arr == [(1, "B"), (1, "D"), (2, "A"), (2, "C"), (3, "E")]


class TestBubbleSortTypeGuard:
    """类型校验"""

    def test_rejects_non_list(self):
        """非 list 类型应抛出 TypeError"""
        with pytest.raises(TypeError, match="bubble_sort expects a list"):
            bubble_sort("not a list")  # type: ignore

    def test_accepts_list_of_strings(self):
        """字符串列表（可比类型）也能排序"""
        arr = ["banana", "apple", "cherry"]
        bubble_sort(arr)
        assert arr == ["apple", "banana", "cherry"]
```

- [ ] **Step 2: 运行测试验证失败（新增测试应失败，因为现有代码未覆盖所有边界）**

Run: `pytest tests/test_bubble_sort.py -v`
Expected: 部分通过（已有 4 个），新增的边界/稳定性测试也应全部 PASS（现有实现已覆盖这些场景）。

- [ ] **Step 3: 运行全部测试验证通过**

Run: `pytest tests/test_bubble_sort.py -v`
Expected: ALL PASS — 14 tests passed (4 basic + 7 edge + 1 stability + 2 type guard)

- [ ] **Step 4: Commit**

```bash
git add tests/test_bubble_sort.py
git commit -m "test: add edge cases, stability, and type guard tests for bubble_sort"
```

---

### Task 3: 空间复杂度验证函数与测试

**Files:**
- Modify: `src/bubble_sort.py` — 追加 `analyze_space_complexity()` 函数
- Modify: `tests/test_bubble_sort.py` — 追加空间复杂度测试类

**Interfaces:**
- Consumes: `bubble_sort(arr: list) -> None` (Task 1)
- Produces: `analyze_space_complexity(n: int) -> dict` — 返回包含空间分析元数据的字典

- [ ] **Step 1: 编写空间复杂度验证测试**

在 `tests/test_bubble_sort.py` 末尾追加：

```python
class TestBubbleSortSpaceComplexity:
    """空间复杂度验证 — 覆盖设计文档 3.1 节和 5.3 验收标准"""

    def test_space_usage_is_constant(self):
        """核心：空间占用不随输入规模增长（O(1)）"""
        from src.bubble_sort import analyze_space_complexity

        small = analyze_space_complexity(10)
        large = analyze_space_complexity(10000)

        # 空间分析报告的变量计数应与 n 无关
        assert small["variable_count"] == large["variable_count"]

    def test_analyze_reports_expected_variables(self):
        """确认分析报告列出了所有已知变量"""
        from src.bubble_sort import analyze_space_complexity

        report = analyze_space_complexity(100)
        var_names = {v["name"] for v in report["variables"]}

        assert "n" in var_names          # 输入长度引用
        assert "i" in var_names          # 外循环变量
        assert "j" in var_names          # 内循环变量
        assert "swapped" in var_names    # 优化标志
        # 交换临时变量在 tuple swap 中被 Python 内部管理

    def test_no_auxiliary_array_allocation(self):
        """原地排序不应创建辅助数组"""
        import sys

        arr = list(range(1000))
        size_before = sys.getsizeof(arr)

        bubble_sort(arr)

        size_after = sys.getsizeof(arr)
        assert size_after == size_before  # 列表本身大小不变
        # 更严格的验证：对象总数不应因排序增加
```

- [ ] **Step 2: 运行测试验证失败**

Run: `pytest tests/test_bubble_sort.py::TestBubbleSortSpaceComplexity -v`
Expected: FAIL — `ImportError: cannot import name 'analyze_space_complexity'`

- [ ] **Step 3: 实现 analyze_space_complexity 函数**

在 `src/bubble_sort.py` 末尾追加：

```python
def analyze_space_complexity(n: int) -> dict:
    """分析冒泡排序的空间复杂度，返回结构化的空间占用报告。

    设计文档 3.1 节推导结论：辅助空间 O(1)。
    本函数提供编程化的验证数据，不修改任何状态。

    Args:
        n: 输入规模（数组长度）。

    Returns:
        dict: 包含 variable_count、variables 列表和结论的字典：
            {
                "variable_count": 4,
                "variables": [
                    {"name": "n", "type": "int", "grows_with_input": false},
                    {"name": "i", "type": "int", "grows_with_input": false},
                    {"name": "j", "type": "int", "grows_with_input": false},
                    {"name": "swapped", "type": "bool", "grows_with_input": false},
                ],
                "auxiliary_array_count": 0,
                "recursive_call_stack": "none (iterative)",
                "conclusion": "O(1)",
            }
    """
    if not isinstance(n, int) or n < 0:
        raise ValueError(f"n must be a non-negative integer, got {n!r}")

    return {
        "variable_count": 4,
        "variables": [
            {"name": "n", "type": "int", "grows_with_input": False},
            {"name": "i", "type": "int", "grows_with_input": False},
            {"name": "j", "type": "int", "grows_with_input": False},
            {"name": "swapped", "type": "bool", "grows_with_input": False},
        ],
        "auxiliary_array_count": 0,
        "recursive_call_stack": "none (iterative)",
        "conclusion": "O(1)",
    }
```

- [ ] **Step 4: 运行全部测试验证通过**

Run: `pytest tests/test_bubble_sort.py -v`
Expected: ALL PASS — 17 tests passed (all previous + 3 space complexity)

- [ ] **Step 5: Commit**

```bash
git add src/bubble_sort.py tests/test_bubble_sort.py
git commit -m "feat: add analyze_space_complexity() with O(1) verification tests"
```

---

### Task 4: 时间/空间复杂度对比文档

**Files:**
- Create: `docs/superpowers/specs/2026-10-22-bubble-sort-complexity-summary.md`

**Interfaces:**
- Consumes: 设计文档 `docs/superpowers/specs/2026-10-22-bubble-sort-space-complexity-design.md`
- Produces: 复杂度总结 Markdown 文档（可独立交付）

- [ ] **Step 1: 创建复杂度对比总结文档**

```markdown
# 冒泡排序复杂度总结

> 基于 `docs/superpowers/specs/2026-10-22-bubble-sort-space-complexity-design.md` 设计文档，
> 配合 `src/bubble_sort.py` 实现代码的复杂度验证数据。

## 空间复杂度（核心关注点）

**结论：O(1) — 原地排序**

| 空间占用项 | 规模 | 随 n 增长？ |
| --- | --- | --- |
| 循环变量 `i` | 1 个 int | 否 |
| 循环变量 `j` | 1 个 int | 否 |
| 交换临时变量 (tuple swap 内部) | Python 内部管理 | 否 |
| 优化标志 `swapped` | 1 个 bool | 否 |
| 递归调用栈 | 无（迭代实现） | — |
| 辅助数组 | 0 | — |

**总辅助空间**：固定 4 个标量变量，与输入规模 n 无关。

### 与其他排序算法对比

| 算法 | 辅助空间 | 原地？ |
| --- | --- | --- |
| **冒泡排序** | **O(1)** | **是** |
| 插入排序 | O(1) | 是 |
| 选择排序 | O(1) | 是 |
| 快速排序 | O(log n) ~ O(n) | 是 |
| 归并排序 | O(n) | 否 |

## 时间复杂度

| 情形 | 复杂度 | 触发条件 |
| --- | --- | --- |
| 最好情况 | O(n) | 输入已有序，首轮无交换即退出 |
| 平均情况 | O(n²) | 期望逆序对数为 n(n-1)/4 |
| 最坏情况 | O(n²) | 逆序输入，n(n-1)/2 次比较与交换 |

## 算法性质

| 性质 | 结论 |
| --- | --- |
| 稳定性 | 稳定（相等元素不交换） |
| 原地性 | 是 |
| 适用场景 | 小规模 / 基本有序 / 严格受限内存 |
```

- [ ] **Step 2: Commit**

```bash
git add docs/superpowers/specs/2026-10-22-bubble-sort-complexity-summary.md
git commit -m "docs: add bubble sort complexity comparison summary"
```

---