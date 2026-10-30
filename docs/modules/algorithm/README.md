# algorithm 模块

## 模块职责

提供冒泡排序（Bubble Sort）算法的原地排序实现。

## 关键类

| 类 | 类型 | 说明 |
|----|------|------|
| `BubbleSortUtil` | 工具类 | int 数组冒泡升序排序，含提前终止优化 |
| `BubbleSortUtilTest` | 测试类 | 10 个测试用例，覆盖正常/边界/异常/原地验证 |

## 依赖关系

无外部模块依赖，纯 JDK 实现。

## API 接口

### `BubbleSortUtil.sort(int[] array)`

- **参数**：`array` — 待排序 int 数组，不可为 null
- **返回**：void（原地排序）
- **异常**：`IllegalArgumentException` — 当 array 为 null
- **复杂度**：时间 O(n²)，空间 O(1)；已有序时通过 swapped 标志提前退出