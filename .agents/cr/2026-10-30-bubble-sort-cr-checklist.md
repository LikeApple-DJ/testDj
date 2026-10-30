# Code Review Checklist

> **Change** `bubble-sort` · **分支/Commit** `task/DEV-966dcd0a` / `996d6d44` · **日期** `2026-10-30`
>
> **AI**：唯一进度源；状态仅用 `⬜` `✅` `❌` `⚠️` `N/A`。
> **完成标准**：所有核销项必须从 `⬜` 变为其他状态；`N/A` 需写原因。

---

## Step 1 — 执行队列（产物 A）

| # | 文件（仓库相对路径） | 归属原因 | Step2 | Step3 | G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | G9 | G10 | G11 | G12 | G13 | G14 | G15 | G16 | G17 | S1 | S2 | S3 | S4 | S5 | S6 | S7 | S8 | S9 | S10 | 总状态 |
|---|----------------------|----------|-------|-------|----|----|----|----|----|----|----|----|----|-----|----|----|----|----|----|----|----|----|----|----|----|----|-----|-----|-----|-----|-----|--------|
| 1 | `src/main/java/com/example/sort/BubbleSort.java` | REQ-1~6 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 2 | `src/test/java/com/example/sort/BubbleSortTest.java` | REQ-7 | ✅ | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ 已审有问题 |

---

## Step 2 — 功能（产物 B）

| REQ | Scenario | Spec证据（原文/章节） | 关联文件 | 状态 | 代码证据（文件/测试/接口） |
|-----|----------|----------------------|----------|------|----------------------------|
| REQ-1 | 提供冒泡排序纯工具类，支持 int[] 和泛型 T[] | impl.md §IMPL: "final class，私有构造" | BubbleSort.java | ✅ | BubbleSort.java:27 (final class), :29-30 (private constructor), :39-55 (sort int[]), :65-82 (sort T[]) |
| REQ-2 | final class + 私有构造 + throw IllegalStateException | impl.md §IMPL: "final class，私有构造（工具类规范）" | BubbleSort.java | ✅ | BubbleSort.java:27 (:final), :29-30 (private constructor + throw) |
| REQ-3 | sort(int[]) — 基本类型冒泡排序 + Objects.requireNonNull | impl.md §IMPL: "sort(int[] array) — 基本类型冒泡排序，Objects.requireNonNull 空校验" | BubbleSort.java | ✅ | BubbleSort.java:40 (:requireNonNull), :43-54 (bubble sort loop) |
| REQ-4 | <T> sort(T[], Comparator) — 泛型冒泡排序 + 双重空校验 | impl.md §IMPL: "<T> sort(T[], Comparator) — 泛型冒泡排序，双重空校验" | BubbleSort.java | ✅ | BubbleSort.java:66-67 (:requireNonNull ×2), :70-81 (generic bubble sort) |
| REQ-5 | 提前终止优化 — swapped=false 时 break | impl.md §IMPL: "提前终止优化：某轮无交换则已有序，break 退出" | BubbleSort.java | ✅ | BubbleSort.java:44,48,51-53 (int[]), :71,75,78-80 (T[]) — swapped flag + break |
| REQ-6 | 时间复杂度 O(n²)，最优 O(n)；空间 O(1) | impl.md §IMPL: "时间复杂度 O(n²)，最优 O(n)；空间 O(1)" | BubbleSort.java | ✅ | BubbleSort.java:9-10 (Javadoc documents complexity); swap in-place confirms O(1) space |
| REQ-7 | 12 测试场景全部通过 | impl.md §TEST: "测试方法数：12 ... 12 tests run, 0 failures, 0 errors" | BubbleSortTest.java | ✅ | BubbleSortTest.java:19-151 (12 @Test methods); surefire report confirms 12/0/0 |

---

## Step 3 — 可读性检查（产物 C）

| ID | 检查项 | 状态 | 备注（命中写 `path:line`） |
|----|--------|------|----------------------------|
| A1 | 源文件格式 | ⚠️ | A1.3 — BubbleSortTest.java:152 缺少文件末尾换行符（P2） |
| A2 | 源文件结构/import 顺序 | ⚠️ | A2.4 — BubbleSortTest.java:3,5: `org.junit.jupiter.api.Test` 在 `java.util.Comparator` 之前，违反字典序（P2）；A2.3 — BubbleSortTest.java:4 多余空行分隔了两个同组非静态 import（P2） |
| A3 | 代码样式 | ✅ | K&R 大括号、4空格缩进、行宽均合规 |
| A4 | 命名规范 | ✅ | 包名全小写、类名 UpperCamelCase、方法名 lowerCamelCase、测试类 BubbleSortTest |
| A5 | 编码实践 | ✅ | 无 @Override 场景、无 catch 块、静态方法用类名调用 |
| A6 | 特定元素样式 | ✅ | 数组类型声明为 Java 风格 `T[]`、无 switch、修饰符顺序正确 |
| A7 | Javadoc 规范 | ✅ | public 类和方法均有 Javadoc、@param/@throws/@since 齐全 |

---

## Step 4 — 可靠性检查（产物 D）

### 4.1 Bug 模式（`bug-pattern-checklist.md`）

> 预扫：`scan-all-rules.sh` 无命中（52/222 rules scanned, 0 findings）。LLM 补充核对如下：

| ID | 状态 | 备注 |
|----|------|------|
| B001–B081 | ✅ | scan-all-rules.sh 无命中；LLM 复核：无字面量 parse、无数组/集合误用、无死循环/死代码、无线程/锁问题、无日期/时区误用、无 Mockito/Spring 注解误用 |
| M001–M027 | ✅ | scan-all-rules.sh 无命中；LLM 复核：无冗余判断、无 catch 吞异常、无 equals/hashCode 遗漏、无需声明的内部类 |
| I001–I010 | ✅ | scan-all-rules.sh 无命中；LLM 复核：无 Date 使用、无 JUnit 版本混用、无 Spring 容器依赖 |

### 4.2 可靠性（`reliability-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| G1.1–G1.4 | N/A | 无并发/加锁场景 |
| G2.1–G2.3 | N/A | 无写接口/消息消费 |
| G3.1–G3.2 | N/A | 无事务/数据库操作 |
| G4.1–G4.3 | N/A | 无 SQL/数据库查询 |
| G5.1 | N/A | 无消息队列 |
| G6.1–G6.2 | N/A | 无缓存 |
| G7.1–G7.2 | N/A | 无调度任务 |
| G8.1–G8.6 | N/A | 无外部I/O/线程池/ThreadLocal |
| G9.1–G9.3 | N/A | 无网络调用 |
| G10.1–G10.2 | N/A | 无对外接口契约 |
| G11.1 | ✅ | 12 个测试方法均有断言 |
| G11.2 | ✅ | 覆盖边界：空数组、单元素、已有序、逆序、重复元素、null 入参 |
| G11.3 | ✅ | `Objects.requireNonNull` 对 null 入参做防御性校验 |
| G11.4 | N/A | 无数值运算/金额场景 |
| G12.1–G12.2 | N/A | 无资金相关场景 |
| G13.1 | N/A | 无日志输出 |
| G14.1–G14.4 | N/A | 无金额/多租户/时区场景 |
| G15.1–G15.3 | N/A | 无表结构变更/接口演进 |
| G16.1–G16.4 | N/A | 无监控埋点/日志 |
| G17.1–G17.3 | N/A | 无应急开关/降级需求 |

### 4.3 安全（`security-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| S1.1–S1.3 | N/A | 无 SQL 操作 |
| S2.1–S2.3 | N/A | 无 Web 输出 |
| S3.1–S3.3 | N/A | 无外部 URL 请求 |
| S4.1–S4.2 | N/A | 无命令执行 |
| S5.1–S5.2 | N/A | 无 XML 解析 |
| S6.1–S6.3 | N/A | 无反序列化 |
| S7.1–S7.3 | N/A | 无文件上传/下载 |
| S8.1–S8.4 | N/A | 无 Web 接口 |
| S9.1–S9.4 | N/A | 无密钥/凭证/日志/加密 |
| S10.1–S10.3 | N/A | 无 CSRF/CORS/跳转 |

---

## Step 5 — 自定义扩展检查（产物 E）

### 5.1 自定义扩展（`customized-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| U1.1 | N/A | 示例项 — Controller 校验注解，不适用；自定义规则未启用 |
| U2.1–U2.3 | N/A | 未启用自定义规则 |

> **结论**：自定义扩展规则未启用，整体标注为 `N/A(未启用自定义规则)`。

---

## 终检（防漏检）

- [x] 执行队列中每个文件 `Step2`、`Step3`、**S1–S10 / G1–G17** 各列均非 `⬜`（跳过文件除外）
- [x] Step 2 的每个 REQ/Scenario 均非 `⬜`
- [x] Step 3 的 A1–A7 均非 `⬜`
- [x] Step 4 全部 **G/S** 与 **B001–B081 / M001–M027 / I001–I010** ID 均非 `⬜`
- [x] Step 5 全部 U* ID 均非 `⬜`
- [x] 所有 `❌/⚠️` 已写入 report，且包含 `ID + path:line`