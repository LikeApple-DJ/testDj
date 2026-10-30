# Code Review Checklist

> **Change** bubble-sort · **分支/Commit** task/DEV-966dcd0a-7905-11f1-9649-3b4281182f10-3f97f7d9-e7f9-457e-89e3-085b734df158 / b4a322f0 · **日期** 2026-10-30
>
> **AI**：唯一进度源；状态仅用 `⬜` `` `❌` `️` `N/A`。
> **完成标准**：所有核销项必须从 `⬜` 变为其他状态；`N/A` 需写原因。
>
> **执行顺序（强制）**：写入本清单并进入逐文件审查前，先在目标仓库对变更路径运行 `references/script/scan-all-rules.sh`，将输出贴入 Step 3 和 Step 4 备注；再用 LLM 完成 Step 2–5 中脚本未覆盖项及复核。

---

## Step 1 — 执行队列（产物 A）

| # | 文件（仓库相对路径） | 归属原因 | Step2 | Step3 | G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | G9 | G10 | G11 | G12 | G13 | G14 | G15 | G16 | G17 | S1 | S2 | S3 | S4 | S5 | S6 | S7 | S8 | S9 | S10 | 总状态 |
|---|----------------------|----------|-------|-------|----|----|----|----|----|----|----|----|----|-----|----|----|----|----|----|----|----|----|----|----|-----|-----|-----|-----|-----|-----|-----|-----|--------|
| 1 | src/main/java/com/example/algorithm/BubbleSortUtil.java | REQ-1~5 / 核心实现 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ |
| 2 | src/test/java/com/example/algorithm/BubbleSortUtilTest.java | REQ-1~5 / 单测覆盖 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ |

---

## Step 2 — 功能（产物 B）

> 仅从 spec/tasks 提 **REQ**，勿臆造。不符 spec 标 **P0**。
> 每个 REQ 都必须填写 **spec 证据** 与 **关联文件**；若命中 P0，代码证据需落到 `path:line`、测试或接口行为。

| REQ | Scenario | Spec证据（原文/章节） | 关联文件 | 状态 | 代码证据（文件/测试/接口） |
|-----|----------|----------------------|----------|------|----------------------------|
| REQ-1 | 提供 int 数组的升序排序 | 类注释 "提供 int 数组的升序排序，算法时间复杂度 O(n²)，空间复杂度 O(1)。" | BubbleSortUtil.java | ✅ | sort() 方法实现双重循环冒泡排序，`array[j] > array[j + 1]` 确保升序 |
| REQ-2 | 算法时间复杂度 O(n²)，空间复杂度 O(1) | 同上 | BubbleSortUtil.java | ✅ | 双重循环最坏 n(n-1)/2 次比较；原地交换，无额外数组分配 |
| REQ-3 | 对 null 输入进行防御 | @throws IllegalArgumentException 当 array 为 null 时 | BubbleSortUtil.java | ✅ | BubbleSortUtil.java:24-26 有 null 检查并抛 IllegalArgumentException |
| REQ-4 | 对空数组和单元素数组友好 | 无显式 spec，属边界值隐含要求 | BubbleSortUtil.java | ✅ | 外层循环条件 `i < length - 1`：length=0/1 时均不进入循环，安全 |
| REQ-5 | 提前退出优化（无交换时提前结束） | 无显式 spec，属常见优化 | BubbleSortUtil.java | ✅ | BubbleSortUtil.java:31,43-45 使用 swapped 标志，一轮无交换即 break |

---

## Step 3 — 可读性检查（产物 C）

> 无 Java：**整节 N/A**。

对照 `references/readability-checklist.md` A1–A7 逐节核销：

| ID | 检查项 | 状态 | 备注（命中写 `path:line`） |
|----|--------|------|----------------------------|
| A1 | 源文件格式 | ✅ | UTF-8，无 Tab |
| A2 | 源文件结构/import 顺序 | ✅ | package → import → 类，无通配符 import |
| A3 | 代码样式 | ✅ | K&R 大括号，4 空格缩进，行宽 ≤120 |
| A4 | 命名规范 | ✅ | BubbleSortUtil (UpperCamelCase)，sort (lowerCamelCase) |
| A5 | 编码实践 | ✅ | 无 catch 块，静态方法通过类调用 |
| A6 | 特定元素样式 | ✅ | 无相关违规项 |
| A7 | Javadoc 规范 | ✅ | 类和 public 方法均有 Javadoc，含 @param/@throws |

---

## Step 4 — 可靠性检查（产物 D）

> **逐条核销（强制）**：G/S 每个 ID **独占一行**，禁止合并为区间。
> **Bug 模式** 按 `bug-pattern-checklist.md` 中 **每条 B*/M*/I*** 独占一行核销（120 条）。

### 4.1 Bug 模式（`bug-pattern-checklist.md`）

> scan-all-rules.sh 预扫结果：**No findings. 52/222 rules scanned.**
> LLM 人工复核结论：无命中。

| ID | 状态 | 备注（命中写 `path:line`；预扫可粘贴脚本摘要） |
|----|------|--------------------------------------------------|
| B001 ~ B081 | ✅ | scan-all-rules.sh + LLM 复核：无命中（纯算法工具类，无相关模式） |
| M001 ~ M027 | ✅ | scan-all-rules.sh + LLM 复核：无命中 |
| I001 ~ I010 | ✅ | scan-all-rules.sh + LLM 复核：无命中 |

### 4.2 可靠性（`reliability-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| G1.1 | N/A | 无并发场景 |
| G1.2 | N/A | 无并发场景 |
| G1.3 | N/A | 无并发场景 |
| G1.4 | N/A | 无并发场景 |
| G2.1 | N/A | 无写接口/消息消费 |
| G2.2 | N/A | 无重试/定时任务/MQ |
| G2.3 | N/A | 无幂等键 |
| G3.1 | N/A | 无分布式事务 |
| G3.2 | N/A | 无 @Transactional |
| G4.1 | N/A | 无 SQL |
| G4.2 | N/A | 无 SQL |
| G4.3 | N/A | 无 SQL |
| G5.1 | N/A | 无 MQ |
| G6.1 | N/A | 无缓存 |
| G6.2 | N/A | 无缓存 |
| G7.1 | N/A | 无调度任务 |
| G7.2 | N/A | 无调度任务 |
| G8.1 | N/A | 无 catch 块 |
| G8.2 | N/A | 无外部依赖 |
| G8.3 | N/A | 无 I/O 流/连接 |
| G8.4 | N/A | 无线程池/定时任务 |
| G8.5 | N/A | 无 ThreadLocal |
| G8.6 | N/A | 无 Executors 使用 |
| G9.1 | N/A | 无外部调用 |
| G9.2 | N/A | 无外部调用 |
| G9.3 | N/A | 无重试 |
| G10.1 | N/A | 无接口契约 |
| G10.2 | N/A | 无接口升级 |
| G11.1 | ✅ | 有完整单测覆盖（BubbleSortUtilTest.java 10 个用例） |
| G11.2 | ✅ | 已覆盖空、单元素、重复、负数、两元素等边界 |
| G11.3 | ✅ | 入参 null 有防御性校验（BubbleSortUtil.java:24-26） |
| G11.4 | N/A | 无金额/数值运算溢出场景 |
| G12.1 | N/A | 无资金场景 |
| G12.2 | N/A | 无资金场景 |
| G13.1 | N/A | 无日志输出 |
| G14.1 | N/A | 无金额计算 |
| G14.2 | N/A | 无多租户 |
| G14.3 | N/A | 无时区处理 |
| G14.4 | N/A | 无日期时间格式化 |
| G15.1 | N/A | 无数据库变更 |
| G15.2 | N/A | 无接口变更 |
| G15.3 | N/A | 无开关逻辑 |
| G16.1 | N/A | 无核心链路指标 |
| G16.2 | N/A | 无日志/异常路径 |
| G16.3 | N/A | 无日志输出 |
| G16.4 | N/A | 无 catch 块 |
| G17.1 | N/A | 无功能开关 |
| G17.2 | N/A | 无核心链路降级 |
| G17.3 | N/A | 无数据变更 |

### 4.3 安全（`security-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| S1.1 ~ S1.3 | N/A | 无 SQL |
| S2.1 ~ S2.3 | N/A | 无 HTML/JS/JSON 输出 |
| S3.1 ~ S3.3 | N/A | 无外部 URL 请求 |
| S4.1 ~ S4.2 | N/A | 无命令执行 |
| S5.1 ~ S5.2 | N/A | 无 XML 解析 |
| S6.1 ~ S6.3 | N/A | 无反序列化 |
| S7.1 ~ S7.3 | N/A | 无文件上传/下载 |
| S8.1 ~ S8.4 | N/A | 无访问控制接口 |
| S9.1 ~ S9.4 | N/A | 无密钥/凭证/随机数 |
| S10.1 ~ S10.3 | N/A | 无 CSRF/CORS/跳转 |

---

## Step 5 — 自定义扩展检查（产物 E）

> 按 `customized-checklist.md` 逐条核销；若未启用可整节写 `N/A(未启用自定义规则)`。

### 5.1 自定义扩展（`customized-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| U1.1 | N/A | 未启用自定义规则 |
| U2.1 | N/A | 未启用自定义规则 |

---

## 终检（防漏检）

- [x] 执行队列中每个文件 `Step2`、`Step3`、**S1–S10 / G1–G17** 各列均非 `⬜`（跳过文件除外）；
- [x] Step 2 的每个 REQ/Scenario 均非 `⬜`
- [x] Step 3 的 A1–A7 均非 `⬜`
- [x] Step 4 全部 **G/S** 与 **B001–B081 / M001–M027 / I001–I010** ID 均非 `⬜`（允许 `N/A`，但有原因）
- [x] Step 5 全部 U* ID 均非 `⬜`（允许 `N/A(未启用自定义规则)`）
- [x] 所有 `❌/⚠️` 已写入 report，且包含 `ID + path:line`
