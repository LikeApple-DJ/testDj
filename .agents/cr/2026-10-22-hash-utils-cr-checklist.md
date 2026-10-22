# Code Review Checklist

> **Change** `hash-utils` · **分支/Commit** `AI/task-DEV-…` / `0613e399` · **日期** `2026-10-22`
>
> **AI**：唯一进度源；状态仅用 `⬜` `✅` `❌` `⚠️` `N/A`。
> **完成标准**：所有核销项必须从 `⬜` 变为其他状态；`N/A` 需写原因。
>
> **执行顺序（强制）**：已先在目标仓库对变更路径运行 `scan-all-rules.sh`（输出并入 Step 3/4 备注），再由 LLM 完成 Step 2–5 补扫与复核。

---

## Step 1 — 执行队列（产物 A）

变更范围（`git diff HEAD~1 HEAD`）：3 文件，其中 Java 2 个，pom.xml 1 个（非 Java，跳过）。

| # | 文件（仓库相对路径） | 归属原因 | Step2 | Step3 | Step4（G+S+B） | 总状态 |
|---|----------------------|----------|-------|-------|----------------|--------|
| 1 | src/main/java/com/example/hash/HashUtils.java | 哈希函数实现主体 | ✅ | ✅ | ⚠️（G 全 N/A；S9.3 弱算法 P1；B/M/I 全 N/A） | ✅ 已审有问题 |
| 2 | src/test/java/com/example/hash/HashUtilsTest.java | 单元测试（REQ-3 正确性证据） | ❌（REQ-3 断言值错误） | ✅ | ✅（G11 已覆盖；B/M/I 全 N/A） | ❌ 已审有问题 |
| 3 | pom.xml | 构建依赖配置 | 跳过（非 Java） | 跳过 | 跳过（`S1.1` 预扫命中为 Maven `${...}` 属性插值，误报） | 跳过 |

- 守卫通过：存在 `.java` 文件，继续审查。
- 收口核对：上述 Step4 各细项结论与下方 §4 逐条 ID 表一致。

---

## Step 2 — 功能（产物 B）

> 需求原文极简：「写个哈希函数」；功能点从 change 内实现与测试共同提炼，未臆造范围外功能。

| REQ | Scenario | Spec证据（原文/章节） | 关联文件 | 状态 | 代码证据（文件/测试/接口） |
|-----|----------|----------------------|----------|------|----------------------------|
| REQ-1 提供哈希计算函数 | Given 任意非 null 字符串；When 调用 md5/sha1/sha256/hash；Then 返回小写十六进制字符串 | `<requirement_section>：写个哈希函数`；`HashUtils.java` 类注释「支持的算法包括 MD5、SHA-1、SHA-256」 | HashUtils.java | ✅ | `HashUtils.java:38/49/60/72`；`bytesToHex` `:94-104` |
| REQ-2 异常入参防护 | Given null 输入 / null 或空算法 / 不支持的算法；Then 抛 `IllegalArgumentException` | `HashUtils.java` 各公开方法 `@throws` 注释 | HashUtils.java | ✅ | `HashUtils.java:73-84`；测试 `HashUtilsTest.java:53/84/115/168/177/186` |
| REQ-3 单测正确且可回归 | Given 标准样例向量；When 运行单测；Then 断言与真实哈希一致且全部通过 | `HashUtilsTest.java` 各 `@Test`/`@DisplayName` | HashUtilsTest.java | ❌ | `HashUtilsTest.java:48` 期望 `a78f4501e95bae1e4e49a5ef2a969026`；实现按 UTF-8 编码（`HashUtils.java:87`），独立双引擎算得真实值为 `65396ee4aad0b4f17aacd1c6112ee364`，两者不等 → 该用例必然失败 |

---

## Step 3 — 可读性检查（产物 C）

> 对照 `readability-checklist.md` A1–A7 逐节核销。脚本预扫命中 0 条 A* 规则。

| ID | 检查项 | 状态 | 备注（命中写 `path:line`） |
|----|--------|------|----------------------------|
| A1 | 源文件格式（文件名/UTF-8/禁 Tab） | ✅ | 无违规；缩进为 4 空格 |
| A2 | 源文件结构/import 顺序 | ✅ | 无通配符 import；静态/非静态 import 分组且组间空行（`HashUtilsTest.java:3-11`） |
| A3 | 代码样式 | ✅ | 行宽 max=109（≤120）；K&R 大括号规范；无 `if(` 粘连 |
| A4 | 命名规范 | ✅ | 常量 UPPER_SNAKE_CASE（`HashUtils.java:19-25`）；类/方法命名规范 |
| A5 | 编码实践 | ✅ | 无空 catch；静态方法均以类名调用 |
| A6 | 特定元素样式 | ✅ | 修饰符顺序正确；无 fall-through |
| A7 | Javadoc 规范 | ✅ | public 类与方法均有 Javadoc，`@param/@return/@throws` 顺序正确 |

---

## Step 4 — 可靠性检查（产物 D）

> 预扫结果（`scan-all-rules.sh`，ripgrep 引擎，52/222 规则）：
> - `[P0] G16.2` `HashUtils.java:83` —— **误报**：为该处 `catch (NoSuchAlgorithmException e)` 后**重新抛出**带 cause 的 `IllegalArgumentException`，非吞异常/无日志。
> - `[P0] S1.1` ×5（`pom.xml:28/36/44/56/57`）—— **误报**：命中 Maven 属性插值 `${junit-jupiter.version}` 等，非 MyBatis `${}` SQL 拼接，且 pom.xml 非 Java。
> - 脚本无其他命中，以下 ID 由 LLM 补扫。

### 4.1 Bug 模式（`bug-pattern-checklist.md`，120 条）

> 本次变更为无状态工具类（哈希计算）+ 纯单元测试：不涉及集合/线程池/日期时间/SQL/IO/反射/序列化/事务/锁/枚举 switch 等模式。
> 经 LLM 逐条核对，**B001–B081、M001–M027、I001–I010 全部为 `N/A`（代码未触及该模式）**；无命中项需写 `path:line`。
> 专项说明：`HashUtilsTest.java` 使用 `@Nested` 非 static 内部类（JUnit 5 合法）、`@ParameterizedTest + @NullAndEmptySource` 用于 String 参数（用法正确）、断言均为 AssertJ（含消息断言，优于 `@Test(expected=)`，故 I001 亦不适用）。

### 4.2 可靠性（`reliability-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| G1.1–G1.4（并发控制） | N/A | 无共享可变状态、无锁/事务并发写 |
| G2.1–G2.3（幂等拦截） | N/A | 无写接口/消息消费 |
| G3.1–G3.2（事务控制） | N/A | 无数据库/事务 |
| G4.1–G4.3（SQL与索引） | N/A | 无 SQL；G4.4 模板遗留，清单无此 ID |
| G5.1（消息MQ） | N/A | 无 MQ |
| G6.1–G6.2（缓存） | N/A | 无缓存 |
| G7.1–G7.2（调度任务） | N/A | 无调度 |
| G8.1–G8.6（防御编程） | N/A | 异常已包装重抛并保留 cause（`HashUtils.java:83-84`），非吞异常；无 IO/线程池/ThreadLocal |
| G9.1–G9.3（网络调用） | ✅ | 无外部调用；`MessageDigest` 为本地 JDK 调用 |
| G10.1–G10.2（接口契约） | N/A | G10.3 模板遗留，清单无此 ID |
| G11.1–G11.4（开发自测） | ✅ | 有单测且有断言；空/中文/null/幂等/长度边界已覆盖；入参 null 已防御（`HashUtils.java:73-78`）；无金额运算 |
| G12.1–G12.2（资损防控） | N/A | 无资金场景 |
| G13.1（监控核对） | N/A | 无日志输出 |
| G14.1–G14.4（国际/多租户/时区） | N/A | 无金额/租户/时区 |
| G15.1–G15.3（可灰度） | N/A | 无 DB DDL/接口契约变更 |
| G16.1–G16.4（可监控） | N/A | 无核心链路埋点需求；异常路径已以异常传播（可追溯），非空 catch |
| G17.1–G17.3（可应急） | N/A | 无开关/发布/数据变更 |
| G18.1–G18.3 | N/A | 清单无 G18 节（模板遗留），以 reliability-checklist.md 为准 |

### 4.3 安全（`security-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| S1.1–S1.3（SQL注入） | N/A | 无 SQL；预扫 S1.1 命中为 pom.xml Maven 插值误报 |
| S2.1–S2.3（XSS） | N/A | 无 Web/模板输出 |
| S3.1–S3.3（SSRF） | N/A | 无外部 URL 请求 |
| S4.1–S4.2（命令执行） | N/A | 无命令执行 |
| S5.1–S5.2（XXE） | N/A | 无 XML 解析 |
| S6.1–S6.3（反序列化） | N/A | 无反序列化 |
| S7.1–S7.3（上传/下载） | N/A | 无文件处理 |
| S8.1–S8.4（访问控制） | N/A | 无接口/资源 |
| S9.1 | N/A | 无硬编码密钥 |
| S9.2 | N/A | 无敏感信息日志 |
| **S9.3（传输/存储加密、安全算法）** | ⚠️ | **P1** 公开 API 提供 MD5/SHA-1（`HashUtils.java:19/21/38/49`）。MD5/SHA-1 已被密码学判定不安全（碰撞攻击），若用于密码、签名、完整性校验等安全场景不满足要求（详见 report） |
| S9.4 | ✅ | 无随机数 |
| S10.1–S10.3 | N/A | 无 Web 增删改/跳转 |

---

## Step 5 — 自定义扩展检查（产物 E）

> `customized-checklist.md` 仅含 1 条示例项（U1.1），无团队私有规则。

| ID | 状态 | 备注 |
|----|------|------|
| U1.1（示例项） | N/A | 未启用自定义规则；且无 Controller 场景 |
| U1.2 / U1.3 / U2.1–U2.3 | N/A | 清单未定义（未启用自定义规则） |

---

## 终检（防漏检）

- [x] 执行队列中每个 Java 文件 Step2、Step3、S1–S10/G1–G17 各列均非 `⬜`（pom.xml 跳过）
- [x] Step 2 的每个 REQ/Scenario 均非 `⬜`
- [x] Step 3 的 A1–A7 均非 `⬜`
- [x] Step 4 全部 G/S 与 B001–B081 / M001–M027 / I001–I010 ID 均非 `⬜`（G/S 逐级核销，B/M/I 逐条 N/A 并给出原因）
- [x] Step 5 全部 U* 标 `N/A(未启用自定义规则)`
- [x] 所有 `❌/⚠️` 已写入 report，且包含 `ID + path:line`（S9.3；REQ-3 断言错误）