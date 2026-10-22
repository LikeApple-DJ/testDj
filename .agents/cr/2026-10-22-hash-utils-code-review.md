# Code Review Report

> **Change** `hash-utils` · **分支/Commit** `AI/task-DEV-…` / `0613e399` · **日期** `2026-10-22` · **审查者** AI

---

## 1. 审查范围

| 项 | 值 |
|----|-----|
| `.java` 文件数 | 2 |
| 变更行数 | `+401 / -0` |

| 类/接口 | 路径 | 角色 |
|---------|------|------|
| HashUtils | src/main/java/com/example/hash/HashUtils.java | 哈希工具类（MD5/SHA-1/SHA-256，小写十六进制输出） |
| HashUtilsTest | src/test/java/com/example/hash/HashUtilsTest.java | 单元测试（JUnit 5 + AssertJ） |

> pom.xml（+68 行）为构建依赖配置，属非 Java 文件，仅做安全预扫复核（S1.1 为 Maven 属性插值误报）。

---

## 2. 问题计数

| P0 | P1 | P2 |
|----|----|-----|
| 1 | 1 | 1 |

---

## 3. Step 2 — 功能（REQ）

### REQ-1: 提供哈希计算函数

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 任意非 null 字符串 → 小写十六进制哈希 | ✅ | 需求「写个哈希函数」+ 类注释 | `HashUtils.java:38/49/60/72`，`bytesToHex :94-104` | 三算法与通用 `hash()` 实现正确，实测编码固定 UTF-8 |

### REQ-2: 异常入参防护

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| null/空/不支持算法 → `IllegalArgumentException` | ✅ | 各方法 `@throws` 注释 | `HashUtils.java:73-84`；测试 `:53/84/115/168/177/186` | 覆盖充分 |

### REQ-3: 单测正确且可回归（❌ P0）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 标准样例向量断言与真实哈希一致 | ❌ | `HashUtilsTest.java` 各用例 | `HashUtilsTest.java:48` | 中文用例期望值错误，详见 §7 |

---

## 4. Step 3 — 可读性检查

| 结果 | 说明（违规写 Ax.x 与 `path:行`） |
|------|--------------------------------|
| ✅ | A1–A7 全部通过：无 Tab、禁通配符 import、import 分组、行宽 max=109、K&R 大括号、常量 UPPER_SNAKE_CASE、Javadoc 完整规范 |

---

## 5. Step 4 — 可靠性检查

| 域 | 参考 | 结果 | 等级 | 说明（列命中 ID 或「已扫无命中」） |
|----|------|------|------|-------------------------------------|
| 可靠性 | `reliability-checklist.md` G1–G17 | ✅ | — | 无并发/事务/IO/MQ/缓存/调度/资金/金额场景；入参防御与异常传播到位，G11 单测覆盖边界值；`catch` 处为包装重抛保留 cause（预扫 G16.2 为误报） |
| 安全 | `security-checklist.md` S1–S10 | ⚠️ | P1 | S9.3：公开暴露 MD5/SHA-1 弱算法（`HashUtils.java:19/21`） |
| Bug 模式 | `bug-pattern-checklist.md` B/M/I（120） | ✅ | — | 预扫 `scan-all-rules.sh` 无真实命中（S1.1/G16.2 均已人工复核为误报）；LLM 补扫 120 条全部不适用 |

---

## 6. Step 5 — 自定义扩展检查

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| 自定义扩展 | `customized-checklist.md` U* | N/A | — | 未启用自定义规则 |

---

## 7. 结论

- **合并建议**：修复后合并（1 个 P0）
- **P0**：
  1. `HashUtilsTest.java:48` 中文 MD5 期望值错误，导致 `md5("你好世界")` 用例必然失败，破坏回归。
- **P1**：
  1. `S9.3` `HashUtils.java:19/21/38/49` MD5/SHA-1 为已被攻破的弱哈希算法，需在 Javadoc 明确限定其非安全用途。
- **P2**：
  1. `HashUtilsTest.java:159` `caseDesc` 参数声明但未在断言/命名中引用，可移除或用于 `@DisplayName`。
- **一句话**：实现本身正确、风格规范、边界与异常路径覆盖充分，唯一阻断项是中文样例向量的返回值抄写错误；另有 MD5/SHA-1 弱算法用途需警示。

---

## 7.1 问题片段（必填）

- **P0** `REQ-3` `src/test/java/com/example/hash/HashUtilsTest.java:48` — 期望值与 UTF-8 真实 MD5 不一致，用例必然失败。
  片段范围：`src/test/java/com/example/hash/HashUtilsTest.java:43-49`

```java
L43|        @Test
L44|        @DisplayName("中文输入 → 返回正确的MD5值")
L45|        void should_returnMd5Hash_when_chineseInput() {
L46|            String result = HashUtils.md5("你好世界");
L47|
L48|            assertThat(result).isEqualTo("a78f4501e95bae1e4e49a5ef2a969026"); // ❌ 真实值为 65396ee4aad0b4f17aacd1c6112ee364
L49|        }
```

- **P1** `S9.3` `src/main/java/com/example/hash/HashUtils.java:19` — MD5/SHA-1 弱算法作为公开 API 暴露。
  片段范围：`src/main/java/com/example/hash/HashUtils.java:18-25`

```java
L18|    /** MD5 算法名称 */
L19|    public static final String ALGORITHM_MD5 = "MD5";        // ⚠️ 弱算法，禁用于安全场景
L20|
L21|    /** SHA-1 算法名称 */
L22|    public static final String ALGORITHM_SHA1 = "SHA-1";     // ⚠️ 弱算法，禁用于安全场景
L23|
L24|    /** SHA-256 算法名称 */
L25|    public static final String ALGORITHM_SHA256 = "SHA-256";
```

---

## 8. 修复任务列表

### P0

- [ ] **P0** `src/test/java/com/example/hash/HashUtilsTest.java:48` — 将 `md5("你好世界")` 断言期望值由 `a78f4501e95bae1e4e49a5ef2a969026` 更正为 UTF-8 真实值 `65396ee4aad0b4f17aacd1c6112ee364`。

### P1

- [ ] **P1** `S9.3` `src/main/java/com/example/hash/HashUtils.java` — 在 MD5/SHA-1 相关常量与 `md5()`/`sha1()` 的 Javadoc 中注明「仅供非安全用途（校验和、文件指纹等），禁止用于密码/签名/完整性校验」，或引导使用 `sha256()`。

### P2（可选）

- [ ] **P2** `src/test/java/com/example/hash/HashUtilsTest.java:159` — 移除未引用的 `caseDesc` 参数，或将其用于测试命名（`@DisplayName`）。