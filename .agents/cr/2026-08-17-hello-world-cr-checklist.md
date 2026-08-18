# Code Review Checklist

> **Change** hello-world · **分支/Commit** AI/task-DEV-9d10e310-7901-11f1-8a9f-59ecae612580-0b823fae-c0c6-45c3-b6bd-b321d4d5948e / HEAD · **日期** 2026-08-17
>
> **AI**：唯一进度源；状态仅用 `⬜` `✅` `❌` `⚠️` `N/A`。
> **完成标准**：所有核销项必须从 `⬜` 变为其他状态；`N/A` 需写原因。
>
> **执行顺序（强制）**：已运行 `scan-all-rules.sh`（52/222 条规则扫描，无命中），输出已并入 Step 4 备注。

---

## Step 1 — 执行队列（产物 A）

> **Step4 列语义**：每个 **Sn / Gn** 表示「**本文件**在 Step4 审查中，对 `reliability-checklist.md` 第 **G*n*** 节、`security-checklist.md` 第 **S*n*** 节的扫描结论」。**Bug 模式（B/M/I）** 不在本表分列，在下方 **§4.1** 按清单 ID 核销。

| # | 文件（仓库相对路径） | 归属原因 | Step2 | Step3 | G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | G9 | G10 | G11 | G12 | G13 | G14 | G15 | G16 | G17 | S1 | S2 | S3 | S4 | S5 | S6 | S7 | S8 | S9 | S10 | 总状态 |
|---|----------------------|----------|-------|-------|----|----|----|----|----|----|----|----|----|-----|-----|-----|-----|-----|-----|-----|-----|----|----|----|----|----|----|----|----|----|-----|--------|
| 1 | pom.xml | 项目配置 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | 跳过 | ✅ 跳过(非Java) |
| 2 | src/main/java/com/dt/example/hello/HelloWorld.java | 主代码实现 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 3 | src/test/java/com/dt/example/hello/HelloWorldTest.java | 单元测试 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |

---

## Step 2 — 功能（产物 B）

> 仅从 spec/tasks 提 **REQ**，勿臆造。不符 spec 标 **P0**。

| REQ | Scenario | Spec证据（原文/章节） | 关联文件 | 状态 | 代码证据（文件/测试/接口） |
|-----|----------|----------------------|----------|------|----------------------------|
| REQ-1 | 生成一个 hello world 输出 | 需求：「生成一个hello world」 | HelloWorld.java | ✅ | `HelloWorld.java:19-21` greet() 返回 "Hello, World!"；`HelloWorld.java:41-44` main() 输出到 stdout |
| REQ-2 | 支持个性化问候 | 需求隐含：需支持带名称的问候（从现有代码实现推断） | HelloWorld.java | ✅ | `HelloWorld.java:29-34` greet(String name) 返回 "Hello, {name}!"；`HelloWorldTest.java:38-49` 测试验证 |
| REQ-3 | null 安全处理 | greet(String) 的 Javadoc 声明 null 时回退默认问候 | HelloWorld.java | ✅ | `HelloWorld.java:30-31` null 检查返回 DEFAULT_GREETING；`HelloWorldTest.java:70-81` 测试验证 |

---

## Step 3 — 可读性检查（产物 C）

> 对照 `references/readability-checklist.md` A1–A7 逐节核销：

| ID | 检查项 | 状态 | 备注（命中写 `path:line`） |
|----|--------|------|----------------------------|
| A1 | 源文件格式 | ✅ | HelloWorld.java 文件名=类名；UTF-8 编码；无 Tab 字符 |
| A2 | 源文件结构/import 顺序 | ✅ | 无 import 语句；package→class 标准顺序；重载 greet() 连续放置 |
| A3 | 代码样式 | ✅ | K&R 大括号；4 空格缩进；行宽≤120；类成员间空行；关键字与(间空格；运算符两侧空格 |
| A4 | 命名规范 | ✅ | 包名全小写；类名 UpperCamelCase；方法 lowerCamelCase；常量 UPPER_SNAKE_CASE；测试类 HelloWorldTest |
| A5 | 编码实践 | ✅ | 无重写方法；无 catch 块；静态方法 main 通过类实例调用 greet()（非静态方法，合理） |
| A6 | 特定元素样式 | ✅ | `String[] args` 正确；无 switch；修饰符顺序正确；无 long 字面量 |
| A7 | Javadoc 规范 | ✅ | public 类和方法均有 Javadoc；@param→@return 顺序正确；含 @author/@date |

---

## Step 4 — 可靠性检查（产物 D）

> **逐条核销（强制）**：G/S 每个 ID **独占一行**。**Bug 模式** 按 `bug-pattern-checklist.md` 中每条核销。
> **预扫结果**：`scan-all-rules.sh` 对 `src/main/java/com/dt/example/hello/` 和 `src/test/java/com/dt/example/hello/` 执行，52/222 条规则扫描，**无命中**。

### 4.1 Bug 模式（`bug-pattern-checklist.md`）

> 预扫无命中。以下由 LLM 逐条核销。

| ID | 状态 | 备注（命中写 `path:line`；预扫可粘贴脚本摘要） |
|----|------|--------------------------------------------------|
| B001 | N/A | 无 LocalDateTime.parse 等字面量调用 |
| B002 | N/A | 无数组 equals 比较 |
| B003 | N/A | 无 Arrays.fill |
| B004 | N/A | 无数组 toString |
| B005 | N/A | 无 Arrays.asList 基本类型数组 |
| B006 | N/A | 使用 assertThat 而非 assertEquals |
| B007 | N/A | 无 catch Throwable |
| B008 | N/A | 无 Executors 线程池 |
| B009 | N/A | 无移位运算 |
| B010 | N/A | 无 BigDecimal |
| B011 | N/A | 无包装类型 == 比较 |
| B012 | N/A | 无 Calendar |
| B013 | N/A | 无 Calendar |
| B014 | N/A | 无集合操作 |
| B015 | N/A | 无 Collection.toArray |
| B016 | N/A | 无 Comparable 实现 |
| B017 | N/A | 无 `this == null` 判断 |
| B018 | N/A | 无三目运算符数值类型混用 |
| B019 | N/A | 无 Money 类 |
| B020 | N/A | 无编译期常量乘法溢出 |
| B021 | N/A | 无 Jedis |
| B022 | N/A | 无 SimpleDateFormat |
| B023 | N/A | 无异常实例创建未抛出 |
| B024 | N/A | 无 Thread 创建未 start |
| B025 | N/A | 无双括号初始化 |
| B026 | N/A | 无 equals(null) |
| B027 | N/A | 无 equals 方法实现 |
| B028 | N/A | 无 DateUtil |
| B029 | N/A | 无 Pojo setter |
| B030 | N/A | 无浮点数 == 比较 |
| B031 | N/A | 无 String.format |
| B032 | N/A | 无注解 getClass |
| B033 | N/A | 无 Unsafe |
| B034 | N/A | 无 Hashtable |
| B035 | N/A | 无自反二元运算 |
| B036 | N/A | 无 IdentityHashMap |
| B037 | N/A | 无可变参数条件表达式 |
| B038 | N/A | 无递归调用 |
| B039 | N/A | 无 String.indexOf |
| B040 | N/A | 无 Class.isInstance |
| B041 | N/A | 无 JDBC |
| B042 | N/A | 使用 JUnit 5 |
| B043 | N/A | 使用 JUnit 5 |
| B044 | N/A | 使用 JUnit 5 |
| B045 | N/A | 无包装类型加锁 |
| B046 | N/A | 无循环 |
| B047 | N/A | 无数值 compare |
| B048 | N/A | 无 Math.round |
| B049 | N/A | 无日期格式 |
| B050 | N/A | 无日期格式 |
| B051 | N/A | 无 Boolean.getBoolean |
| B052 | N/A | 无日期格式 |
| B053 | N/A | 无期望异常测试 |
| B054 | N/A | 无 EqualsTester |
| B055 | N/A | 无 Mockito |
| B056 | N/A | 无 Arrays.asList 修改 |
| B057 | N/A | 无增强 for 循环修改集合 |
| B058 | N/A | 无集合自操作 |
| B059 | N/A | 无 Collections.nCopies |
| B060 | N/A | 无三目运算符拆箱 |
| B061 | N/A | 无 BASE64Encoder |
| B062 | N/A | 无 ClassLoader |
| B063 | N/A | 无 javax.xml |
| B064 | N/A | 无 Optional |
| B065 | N/A | 无 Pojo 自赋值 |
| B066 | N/A | 无 Math.random |
| B067 | N/A | 无 Random.nextInt |
| B068 | N/A | 无自赋值 |
| B069 | N/A | 无 compareTo |
| B070 | N/A | 无 equals 自比较 |
| B071 | N/A | 无 size() >= 0 |
| B072 | N/A | 无 Stream.toString |
| B073 | N/A | 无 StringBuilder(char) |
| B074 | N/A | 无 substring(0) |
| B075 | N/A | 无 for 循环 |
| B076 | N/A | 无 @Transactional |
| B077 | N/A | 无 catch Throwable |
| B078 | N/A | 无 assertThat(x).isEqualTo(x) |
| B079 | N/A | 无 @Mock |
| B080 | ✅ | HelloWorldTest.java 所有测试方法均含 assertThat 断言 |
| B081 | N/A | 无集合原地修改 |
| M001 | N/A | 无重复条件判断 |
| M002 | N/A | 无 instanceof |
| M003 | N/A | 无包装类构造器 |
| M004 | N/A | 无 printStackTrace |
| M005 | N/A | 无内部类 |
| M006 | N/A | 无编译期常量布尔表达式 |
| M007 | N/A | 无 catch 块 |
| M008 | N/A | 无 equals/hashCode 重写 |
| M009 | N/A | 无 equals 不兼容类型 |
| M010 | N/A | 无位运算 |
| M011 | N/A | 无 switch |
| M012 | N/A | 无 finally |
| M013 | N/A | 无浮点类型转换 |
| M014 | N/A | 无枚举 |
| M015 | N/A | 无继承 |
| M016 | N/A | 无时间 API |
| M017 | N/A | 使用 JUnit 5 @Test |
| M018 | N/A | 无显式锁 |
| M019 | N/A | 无枚举 switch |
| M020 | N/A | 无重写方法 |
| M021 | N/A | 无 equals 方法 |
| M022 | N/A | 无 Optional |
| M023 | N/A | 无 toString 调用 |
| M024 | N/A | 无 Optional |
| M025 | N/A | 无 final 类 |
| M026 | N/A | 无 @Mock |
| M027 | N/A | 无 ThreadLocal |
| I001 | N/A | 无期望异常测试 |
| I002 | N/A | 无 @DoNotMock |
| I003 | N/A | 无 @AutoValue |
| I004 | N/A | 无 java.util.Date |
| I005 | N/A | 使用 JUnit 5 |
| I006 | N/A | 使用 JUnit 5 |
| I007 | N/A | 使用 JUnit 5 |
| I008 | N/A | 无 dataProvider |
| I009 | N/A | 统计用 |
| I010 | N/A | 无 Spring 容器 |

### 4.2 可靠性（`reliability-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| G1.1 | N/A | 无事务/并发场景 |
| G1.2 | N/A | 无锁操作 |
| G1.3 | N/A | 无乐观锁 |
| G1.4 | N/A | 无多资源加锁 |
| G2.1 | N/A | 无写接口 |
| G2.2 | N/A | 无重试/定时任务 |
| G2.3 | N/A | 无幂等键 |
| G3.1 | N/A | 无分布式事务 |
| G3.2 | N/A | 无 @Transactional |
| G4.1 | N/A | 无 SQL |
| G4.2 | N/A | 无 SQL |
| G4.3 | N/A | 无 SQL 分页 |
| G5.1 | N/A | 无 MQ |
| G6.1 | N/A | 无缓存 |
| G6.2 | N/A | 无缓存双写 |
| G7.1 | N/A | 无调度任务 |
| G7.2 | N/A | 无调度任务 |
| G8.1 | N/A | 无异常处理路径 |
| G8.2 | N/A | 无外部依赖 |
| G8.3 | N/A | 无 I/O 资源 |
| G8.4 | N/A | 无线程池 |
| G8.5 | N/A | 无 ThreadLocal |
| G8.6 | N/A | 无 Executors |
| G9.1 | N/A | 无外部调用 |
| G9.2 | N/A | 无外部调用 |
| G9.3 | N/A | 无重试 |
| G10.1 | N/A | 无外部接口 |
| G10.2 | N/A | 无外部接口 |
| G11.1 | ✅ | HelloWorldTest.java 含 4 个测试方法，均有断言 |
| G11.2 | ✅ | 覆盖正常路径、null、空字符串边界 |
| G11.3 | ✅ | greet(String) 对 null 有防御性校验（HelloWorld.java:30-31） |
| G11.4 | N/A | 无数值运算 |
| G12.1 | N/A | 无资金场景 |
| G12.2 | N/A | 无资金场景 |
| G13.1 | N/A | 无日志输出 |
| G14.1 | N/A | 无金额 |
| G14.2 | N/A | 无多租户 |
| G14.3 | N/A | 无时区处理 |
| G14.4 | N/A | 无日期格式化 |
| G15.1 | N/A | 无 DB 变更 |
| G15.2 | N/A | 无接口 |
| G15.3 | N/A | 无开关 |
| G16.1 | N/A | 非核心链路 |
| G16.2 | N/A | 无异常处理 |
| G16.3 | N/A | 无日志 |
| G16.4 | N/A | 无 catch 块 |
| G17.1 | N/A | 无功能开关 |
| G17.2 | N/A | 无降级场景 |
| G17.3 | N/A | 无数据变更 |
| G18.1 | N/A | 无安全补强场景 |
| G18.2 | N/A | 无安全补强场景 |
| G18.3 | N/A | 无安全补强场景 |

### 4.3 安全（`security-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| S1.1 | N/A | 无 SQL |
| S1.2 | N/A | 无动态 SQL |
| S1.3 | N/A | 无 like 查询 |
| S2.1 | N/A | 无 Web 输出 |
| S2.2 | N/A | 无富文本 |
| S2.3 | N/A | 无模板引擎 |
| S3.1 | N/A | 无外部 URL 请求 |
| S3.2 | N/A | 无 HTTP 调用 |
| S3.3 | N/A | 无 HTTP 调用 |
| S4.1 | N/A | 无系统命令 |
| S4.2 | N/A | 无文件操作 |
| S5.1 | N/A | 无 XML 解析 |
| S5.2 | N/A | 无 XPath |
| S6.1 | N/A | 无反序列化 |
| S6.2 | N/A | 无 JSON 反序列化 |
| S6.3 | N/A | 无 transient |
| S7.1 | N/A | 无文件上传 |
| S7.2 | N/A | 无文件路径 |
| S7.3 | N/A | 无文件存储 |
| S8.1 | N/A | 无 Web 接口 |
| S8.2 | N/A | 无 HTTP 方法 |
| S8.3 | N/A | 无数据 ID |
| S8.4 | N/A | 无 Cookie |
| S9.1 | N/A | 无密钥/凭证 |
| S9.2 | N/A | 无日志 |
| S9.3 | N/A | 无网络传输 |
| S9.4 | N/A | 无随机数 |
| S10.1 | N/A | 无 Web 接口 |
| S10.2 | N/A | 无 CORS |
| S10.3 | N/A | 无 URL 跳转 |

---

## Step 5 — 自定义扩展检查（产物 E）

> 按 `customized-checklist.md` 逐条核销；若未启用可整节写 `N/A(未启用自定义规则)`。

### 5.1 自定义扩展（`customized-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| U1.1 | N/A | 示例项，非 Controller 代码 |
| U1.2 | N/A | 未定义 |
| U1.3 | N/A | 未定义 |
| U2.1 | N/A | 未定义 |
| U2.2 | N/A | 未定义 |
| U2.3 | N/A | 未定义 |

**整节结论**：N/A(未启用自定义规则，仅含示例项 U1.1)

---

## 终检（防漏检）

- [x] 执行队列中每个文件 `Step2`、`Step3`、**S1–S10 / G1–G17** 各列均非 `⬜`（跳过文件除外）；
- [x] Step 2 的每个 REQ/Scenario 均非 `⬜`
- [x] Step 3 的 A1–A7 均非 `⬜`
- [x] Step 4 全部 **G/S** 与 **B001–B081 / M001–M027 / I001–I010** ID 均非 `⬜`
- [x] Step 5 全部 U* ID 均非 `⬜`
- [x] 所有 `❌/⚠️` 已写入 report（本次无命中）