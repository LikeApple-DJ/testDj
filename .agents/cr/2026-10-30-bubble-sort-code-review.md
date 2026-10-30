# Code Review Report

> **Change** `bubble-sort` · **分支/Commit** `task/DEV-966dcd0a` / `996d6d44` · **日期** `2026-10-30` · **审查者** AI
>
> **AI**：等级 **P0 / P1 / P2**；G/S 以 checklist 行内定义为准；Bug 模式以 `bug-pattern-checklist.md` 表头为准（Blocker→P0、Major→P1、Info→P2）。已运行 `scan-all-rules.sh`（0 findings）。

---

## 1. 审查范围

| 项 | 值 |
|----|-----|
| `.java` 文件数 | 2 |
| 变更行数 | `+247 / -0`（新增文件） |

| 类/接口 | 路径 | 角色 |
|---------|------|------|
| `BubbleSort` | `src/main/java/com/example/sort/BubbleSort.java` | 冒泡排序工具类（final，纯静态方法） |
| `BubbleSortTest` | `src/test/java/com/example/sort/BubbleSortTest.java` | 单元测试（JUnit 5 + AssertJ） |

---

## 2. 问题计数

| P0 | P1 | P2 |
|----|----|-----|
| 0 | 0 | 3 |

---

## 3. Step 2 — 功能（REQ）

### REQ-1: 冒泡排序工具类（int[] + 泛型 T[]）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| final class + 私有构造 | ✅ | impl.md §IMPL: "final class，私有构造（工具类规范）" | BubbleSort.java:27,29-30 | — |
| sort(int[]) 正序排序 | ✅ | impl.md §IMPL | BubbleSort.java:39-55 | 冒泡逻辑 + 提前终止 |
| sort(T[], Comparator) 泛型排序 | ✅ | impl.md §IMPL | BubbleSort.java:65-82 | 双重空校验 + Comparator 驱动 |

### REQ-5: 提前终止优化

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| swapped=false → break | ✅ | impl.md §IMPL: "提前终止优化" | BubbleSort.java:44,48,51-53 (int[]) + :71,75,78-80 (T[]) | 两处实现一致 |

### REQ-7: 测试覆盖

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 12 测试场景全部通过 | ✅ | impl.md §TEST: "12 tests run, 0 failures" | surefire-reports 确认 12/0/0 | 覆盖正常/边界/异常路径 |

---

## 4. Step 3 — 可读性检查

| 结果 | 说明（违规写 Ax.x 与 `path:行`） |
|------|--------------------------------|
| ⚠️ | 见下方明细（3 项 P2） |

- **P2** `A1.3` — 文件末尾缺少换行符：`src/test/java/com/example/sort/BubbleSortTest.java:152`
- **P2** `A2.4` — import 未按 ASCII 字典序排列：`org.junit.jupiter.api.Test` 应在 `java.util.Comparator` 之后：`src/test/java/com/example/sort/BubbleSortTest.java:3-5`
- **P2** `A2.3` — 非静态 import 组内存在多余空行：`src/test/java/com/example/sort/BubbleSortTest.java:4`

> 其余 A1–A7 全部通过：K&R 大括号、4空格缩进、命名规范、Javadoc 完整均合规。

---

## 5. Step 4 — 可靠性检查

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| 可靠性 | `reliability-checklist.md` G1–G17 | ✅ | — | G11（开发自测）全部通过：12 测试含断言、覆盖边界、null 校验完整；其余 G1–G10/G12–G17 均与此纯算法工具类无关 |
| 安全 | `security-checklist.md` S1–S10 | N/A | — | 纯 JDK 算法工具类，无 Web/SQL/文件/网络/序列化等攻击面 |
| Bug 模式 | `bug-pattern-checklist.md` B/M/I（120） | ✅ | — | `scan-all-rules.sh` 预扫 0 findings；LLM 逐条复核无命中 |

---

## 6. Step 5 — 自定义扩展检查

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| 自定义扩展 | `customized-checklist.md` U* | N/A | — | 未启用自定义规则 |

---

## 7. 结论

- **合并建议**：**通过**
- **P0**：无
- **P1**：无
- **P2**：3 项（import 排序、多余空行、文件末尾换行符 — 均仅影响代码风格，无功能/可靠性影响）
- **一句话**：冒泡排序实现规范、测试覆盖充分，仅有 3 个 P2 级别的代码风格小问题，可直接合并。

---

## 7.1 问题片段（必填）

### P2 `A2.3` / `A2.4` — import 排序不规范

`src/test/java/com/example/sort/BubbleSortTest.java:3-8`

```java
L3|import org.junit.jupiter.api.Test;
L4|
L5|import java.util.Comparator;
L6|
L7|import static org.assertj.core.api.Assertions.assertThat;
L8|import static org.assertj.core.api.Assertions.assertThatNullPointerException;
```

**建议修复**：`java.util` 包应在 `org.junit` 之前；删除两个非静态 import 间的多余空行。

```java
import java.util.Comparator;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;
```

### P2 `A1.3` — 文件末尾缺少换行符

`src/test/java/com/example/sort/BubbleSortTest.java:152`

文件最后一行 `}` 之后缺少换行符（`\n`）。

---

## 8. 修复任务列表

### P2（可选）

- [ ] **P2** `src/test/java/com/example/sort/BubbleSortTest.java:3-8` — 修复 import 顺序：将 `java.util.Comparator` 移到 `org.junit.jupiter.api.Test` 之前，删除两个非静态 import 间的多余空行
- [ ] **P2** `src/test/java/com/example/sort/BubbleSortTest.java:152` — 文件末尾添加换行符