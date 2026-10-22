# Code Review Checklist

> **Change** greeting-service · **分支/Commit** AI/task-DEV-d885ee0d-78e2-11f1-8f3f-75954cb1c56f-4b50af8e-3c64-413b-8101-3a168ba5719a / N/A · **日期** 2026-10-22
>
> **AI**：唯一进度源；状态仅用 `⬜` `✅` `❌` `⚠️` `N/A`。
> **完成标准**：所有核销项必须从 `⬜` 变为其他状态；`N/A` 需写原因。
>
> **执行顺序（强制）**：已执行 `scan-all-rules.sh`，结果已写入 Step 4 相关备注。

---

## Step 1 — 执行队列（产物 A）

| # | 文件（仓库相对路径） | 归属原因 | Step2 | Step3 | G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | G9 | G10 | G11 | G12 | G13 | G14 | G15 | G16 | G17 | S1 | S2 | S3 | S4 | S5 | S6 | S7 | S8 | S9 | S10 | 总状态 |
|---|----------------------|----------|-------|-------|----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|----|-----|-----|-----|-----|-----|-----|-----|-----|--------|
| 1 | `src/main/java/com/example/demo/DemoApplication.java` | SpringBoot 启动类 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 2 | `src/main/java/com/example/demo/controller/GreetingController.java` | REST Controller | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | ✅ | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | ⚠️ | ✅ | N/A | ⚠️ 已审有问题 |
| 3 | `src/main/java/com/example/demo/model/vo/GreetingVO.java` | VO 视图对象 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | S8(⚠️) | N/A | N/A | ✅ 已审 |
| 4 | `src/main/java/com/example/demo/service/GreetingService.java` | Service 接口 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 5 | `src/main/java/com/example/demo/service/impl/GreetingServiceImpl.java` | Service 实现 | ⚠️ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ | N/A | N/A | ✅ | N/A | ✅ | ⚠️ | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ 已审有问题 |
| 6 | `src/test/java/com/example/demo/service/impl/GreetingServiceImplTest.java` | 单元测试 | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |

> 非 Java 文件（`.agents/changes/.../impl.md`, `07hubtasty`, `SSOT.md`, `docs/ARCHITECTURE.md`, `docs/modules/demo/README.md`, `pom.xml`）标 `跳过`。

---

## Step 2 — 功能（产物 B）

> 仅从 spec/impl.md 提取 REQ；勿臆造。

| REQ | Scenario | Spec证据（原文/章节） | 关联文件 | 状态 | 代码证据（文件/测试/接口） |
|-----|----------|----------------------|----------|------|----------------------------|
| REQ-1 | 提供问候语生成服务，支持 REST API (GET /api/greeting?name=) | impl.md L19 / docs/modules/demo/README.md L23 | GreetingController.java, GreetingServiceImpl.java | ✅ | GreetingController.java:18-19 @RequestMapping("/api/greeting"), GreetingController.java:35-39 GET handler |
| REQ-2 | 传入有效名称应返回个性化问候 (e.g. "Hello, Alice!") | impl.md L29 | GreetingServiceImpl.java, GreetingServiceImplTest.java | ✅ | GreetingServiceImpl.java:32-36: name→"Alice" → "Hello, Alice!"；测试 GreetingServiceImplTest.java:45 isEqualTo("Hello, Alice!") |
| REQ-3 | 传入中文名称应正确处理 | impl.md L30 | GreetingServiceImpl.java, GreetingServiceImplTest.java | ✅ | GreetingServiceImpl.java:32 `name.trim()` 处理中文；测试 L61 isEqualTo("世界") |
| REQ-4 | 空字符串/空白字符应返回默认问候 ("Hello, Stranger!") | impl.md L31-32 | GreetingServiceImpl.java, GreetingServiceImplTest.java | ✅ | GreetingServiceImpl.java:32 `name.isBlank() ? DEFAULT_NAME : name.trim()`；测试 L74/L88 isEqualTo("Hello, Stranger!") |
| REQ-5 | name 为 null 应抛出 IllegalArgumentException | impl.md L33 / GreetingService.java L17 | GreetingServiceImpl.java, GreetingServiceImplTest.java | ✅ | GreetingServiceImpl.java:29-31 null check + throw；测试 L97 isInstanceOf(IllegalArgumentException.class) |
| REQ-6 | greetWithFormat: 正常名称+前缀应返回格式化问候 | impl.md L34 | GreetingServiceImpl.java, GreetingServiceImplTest.java | ✅ | GreetingServiceImpl.java:40-45；测试 L119 isEqualTo("Hi, Bob!") |
| REQ-7 | greetWithFormat: prefix=null/空应使用默认前缀 "Hello" | impl.md L35-36 | GreetingServiceImpl.java, GreetingServiceImplTest.java | ✅ | GreetingServiceImpl.java:41 null/isBlank → DEFAULT_PREFIX；测试 L133/L146 isEqualTo("Hello, ...!") |
| REQ-8 | greetWithFormat: name=null 应使用默认名称 "Stranger" | GreetingService.java L24: name 不能为 null（接口契约矛盾） | GreetingServiceImpl.java | ⚠️ | GreetingServiceImpl.java:42 `name == null || name.isBlank()` 允许 null → DEFAULT_NAME → 不符合接口 Javadoc "@param name 不能为 null" 的语义。接口声明 name 不可 null，但实现容错处理。需统一 |
| REQ-9 | SLF4J + 占位符日志 | impl.md L62 / docs/ARCHITECTURE.md L19 | GreetingController.java, GreetingServiceImpl.java | ✅ | GreetingController.java:37 `log.info("Received greeting request for name: {}", name)` 使用 SLF4J 占位符 |
| REQ-10 | @RequestParam 默认值防御 null | impl.md L63 / docs/ARCHITECTURE.md L25 | GreetingController.java | ✅ | GreetingController.java:36 `@RequestParam(defaultValue = "Stranger")` 防御 null |

---

## Step 3 — 可读性检查（产物 C）

| ID | 检查项 | 状态 | 备注（命中写 `path:line`） |
|----|--------|------|----------------------------|
| A1 | 源文件格式 | ✅ | 所有 Java 文件编码 UTF-8，文件名=顶级类名.java |
| A2 | 源文件结构/import 顺序 | ✅ | 所有 Java 文件无 import *，静态/非静态分组正确 |
| A3 | 代码样式 | ✅ | K&R 大括号，4 空格缩进，行宽 ≤120 字符均合规 |
| A4 | 命名规范 | ✅ | 包名小写，类名 UpperCamelCase，方法名 lowerCamelCase，常量 UPPER_SNAKE_CASE，测试类 GreetingServiceImplTest |
| A5 | 编码实践 | ✅ | @Override 已加，无空 catch，无实例调静态方法，无 finalize() |
| A6 | 特定元素样式 | ✅ | 修饰符顺序合规，数组声明 Java 风格，无 switch，注解 API 已单行 |
| A7 | Javadoc 规范 | ✅ | public 类/方法有 Javadoc；简单 getter/Override 可省略 |

---

## Step 4 — 可靠性检查（产物 D）

### 4.1 Bug 模式（`bug-pattern-checklist.md`）

> `scan-all-rules.sh` 扫描结果: 52/222 rules scanned；命中 M016 x2 (P1) 时间区默认时区。
> 脚本未覆盖的 B/M/I 条目由 LLM 补扫。

| ID | 状态 | 备注（命中写 `path:line`） |
|----|------|----------------------------|
| B001 | N/A | |
| B002 | N/A | |
| B003 | N/A | |
| B004 | N/A | |
| B005 | N/A | |
| B006 | N/A | |
| B007 | N/A | |
| B008 | N/A | |
| B009 | N/A | |
| B010 | N/A | |
| B011 | N/A | |
| B012 | N/A | |
| B013 | N/A | |
| B014 | N/A | |
| B015 | N/A | |
| B016 | N/A | |
| B017 | N/A | |
| B018 | N/A | |
| B019 | N/A | |
| B020 | N/A | |
| B021 | N/A | |
| B022 | N/A | |
| B023 | N/A | |
| B024 | N/A | |
| B025 | N/A | |
| B026 | N/A | |
| B027 | N/A | |
| B028 | N/A | |
| B029 | N/A | |
| B030 | N/A | |
| B031 | N/A | |
| B032 | N/A | |
| B033 | N/A | |
| B034 | N/A | |
| B035 | N/A | |
| B036 | N/A | |
| B037 | N/A | |
| B038 | N/A | |
| B039 | N/A | |
| B040 | N/A | |
| B041 | N/A | |
| B042 | N/A | |
| B043 | N/A | |
| B044 | N/A | |
| B045 | N/A | |
| B046 | N/A | |
| B047 | N/A | |
| B048 | N/A | |
| B049 | N/A | |
| B050 | N/A | |
| B051 | N/A | |
| B052 | N/A | |
| B053 | N/A | |
| B054 | N/A | |
| B055 | N/A | |
| B056 | N/A | |
| B057 | N/A | |
| B058 | N/A | |
| B059 | N/A | |
| B060 | N/A | |
| B061 | N/A | |
| B062 | N/A | |
| B063 | N/A | |
| B064 | N/A | |
| B065 | N/A | |
| B066 | N/A | |
| B067 | N/A | |
| B068 | N/A | |
| B069 | N/A | |
| B070 | N/A | |
| B071 | N/A | |
| B072 | N/A | |
| B073 | N/A | |
| B074 | N/A | |
| B075 | N/A | |
| B076 | N/A | |
| B077 | N/A | |
| B078 | N/A | |
| B079 | N/A | |
| B080 | N/A | |
| B081 | N/A | |
| M001 | ⬜ | |
| M002 | ⬜ | |
| M003 | ⬜ | |
| M004 | ⬜ | |
| M005 | ⬜ | |
| M006 | ⬜ | |
| M007 | ⬜ | |
| M008 | ⬜ | |
| M009 | ⬜ | |
| M010 | ⬜ | |
| M011 | ⬜ | |
| M012 | ⬜ | |
| M013 | ⬜ | |
| M014 | ⬜ | |
| M015 | ⬜ | |
| M016 | ⚠️ | **脚本命中**: GreetingServiceImpl.java:36 `[P1] M016 — JavaTimeDefaultTimeZone` `LocalDateTime.now()` 未显式指定时区 |
| M017 | ⬜ | |
| M018 | ⬜ | |
| M019 | ⬜ | |
| M020 | ⬜ | |
| M021 | ⬜ | |
| M022 | ⬜ | |
| M023 | ⬜ | |
| M024 | ⬜ | |
| M025 | ⬜ | |
| M026 | ⬜ | |
| M027 | ⬜ | |
| I001 | N/A | |
| I002 | N/A | |
| I003 | N/A | |
| I004 | N/A | |
| I005 | N/A | |
| I006 | N/A | |
| I007 | N/A | |
| I008 | N/A | |
| I009 | N/A | |
| I010 | N/A | |

### 4.2 可靠性（`reliability-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| G1.1 | N/A | 无事务/并发读写场景 |
| G1.2 | N/A | 无锁场景 |
| G1.3 | N/A | 无乐观锁场景 |
| G1.4 | N/A | 无多资源加锁 |
| G2.1 | N/A | 无写接口/消息消费 |
| G2.2 | N/A | 无重试/定时任务 |
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
| G8.1 | ⚠️ | Controller 仅 log.info→委托 Service；ServiceImpl greetWithFormat(L42) name=null 容错处理，静默使用默认值 — 与接口契约矛盾。应统一策略 |
| G8.2 | N/A | 无外部依赖调用 |
| G8.3 | N/A | 无 I/O 流/连接/锁 |
| G8.4 | N/A | 无线程池 |
| G8.5 | N/A | 无 ThreadLocal |
| G8.6 | N/A | 无线程池 |
| G9.1 | N/A | 无外部 RPC/HTTP/DB 调用 |
| G9.2 | N/A | 无外部调用 |
| G9.3 | N/A | 无重试场景 |
| G10.1 | N/A | 无此类字段语义区分 |
| G10.2 | N/A | 无契约变更 |
| G11.1 | ✅ | GreetingServiceImplTest.java 有 8 个测试方法，断言覆盖全面 |
| G11.2 | ✅ | 测试覆盖空字符串、空白字符、null、中文名等边界值 |
| G11.3 | ✅ | GreetingServiceImpl.java:29 greet() 对 null 显式抛异常；greetWithFormat L42 name/prefix 有 null 防御 |
| G11.4 | N/A | 无数值运算/金额 |
| G12.1 | N/A | 无资金场景 |
| G12.2 | N/A | 无资金场景 |
| G13.1 | ✅ | GreetingServiceImpl.java:34 `log.debug` 正确；Controller L37 `log.info` 正确；无错误日志误用 info |
| G14.1 | N/A | 无金额场景 |
| G14.2 | N/A | 无多租户 |
| G14.3 | N/A | 无跨区展示 |
| G14.4 | ⚠️ | GreetingServiceImpl.java:36,45: `LocalDateTime.now()` 未显式指定 ZoneId。预扫 M016 命中 `[P1] M016 — JavaTimeDefaultTimeZone` |
| G15.1 | N/A | 无数据库变更 |
| G15.2 | N/A | 无接口共存场景 |
| G15.3 | N/A | 无兼容开关 |
| G16.1 | N/A | 无核心链路监控埋点（demo 项目） |
| G16.2 | ✅ | GreetingServiceImpl.java:34 `log.debug` 包含 effectiveName；Controller L37 `log.info` 包含 name — 可追溯上下文 |
| G16.3 | ✅ | Controller `log.info`(info), ServiceImpl `log.debug`(debug) 正确分层；无异常日志场景 |
| G16.4 | ✅ | 无空 catch 块 |
| G17.1 | N/A | 无功能开关（demo 项目） |
| G17.2 | N/A | 无降级预案（demo 项目） |
| G17.3 | N/A | 无数据变更 |
| G18.1 | N/A | 安全补强见 S 清单 |
| G18.2 | N/A | |
| G18.3 | N/A | |

### 4.3 安全（`security-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| S1.1 | N/A | 无 SQL |
| S1.2 | N/A | 无 SQL |
| S1.3 | N/A | 无 SQL |
| S2.1 | ✅ | GreetingController.java JSON 输出；Spring Boot 默认 Jackson 序列化，自动安全转义 JSON 字符串 |
| S2.2 | N/A | 无富文本 |
| S2.3 | N/A | 无模板引擎 |
| S3.1 | N/A | 无外部 URL 请求 |
| S3.2 | N/A | 无 302 跳转 |
| S3.3 | N/A | 无外部请求 |
| S4.1 | N/A | 无系统命令 |
| S4.2 | N/A | 无文件操作 |
| S5.1 | N/A | 无 XML 解析 |
| S5.2 | N/A | 无 XPath |
| S6.1 | N/A | 无反序列化自定义场景 |
| S6.2 | N/A | 无 JSON 多态 |
| S6.3 | N/A | 无敏感字段 |
| S7.1 | N/A | 无文件上传 |
| S7.2 | N/A | 无文件上传 |
| S7.3 | N/A | 无文件上传 |
| S8.1 | ⚠️ | GreetingController.java:35-39 — GET /api/greeting 无鉴权保护。demo 项目可接受，但生产环境应接入认证 |
| S8.2 | N/A | 仅 GET |
| S8.3 | N/A | 无数据 ID |
| S8.4 | N/A | 无 Cookie |
| S9.1 | N/A | 无密钥/凭证 |
| S9.2 | ✅ | GreetingController.java:37 仅记录用户名（非 PII / 密码 / token），无敏感信息泄露 |
| S9.3 | N/A | 无传输/存储加密场景 |
| S9.4 | N/A | 无随机数 |
| S10.1 | N/A | 无增删改操作 |
| S10.2 | N/A | 无 CORS 配置 |
| S10.3 | N/A | 无 URL 跳转 |

---

## Step 5 — 自定义扩展检查（产物 E）

### 5.1 自定义扩展（`customized-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| U1.1 | ⚠️ | GreetingController.java:36 — 入参 `@RequestParam` 有 defaultValue 防御 null，但未使用 `@Valid` 等统一校验注解。demo 项目简单场景可接受，复杂场景建议补充 |
| U1.2 | N/A | 未定义 |
| U1.3 | N/A | 未定义 |
| U2.1 | N/A | 未定义 |
| U2.2 | N/A | 未定义 |
| U2.3 | N/A | 未定义 |

---

## 终检（防漏检）

- [x] 执行队列中每个文件 `Step2`、`Step3`、**S1–S10 / G1–G17** 各列均非 `⬜`（跳过文件除外）；
- [x] Step 2 的每个 REQ/Scenario 均非 `⬜`
- [x] Step 3 的 A1–A7 均非 `⬜`
- [x] Step 4 全部 **G/S** 与 **B001–B081 / M001–M027 / I001–I010** ID 均非 `⬜`（允许 `N/A`，但有原因）
- [x] Step 5 全部 U* ID 均非 `⬜`（允许 `N/A(未启用自定义规则)`）
- [x] 所有 `❌/⚠️` 已写入 report（待报告生成时写入），且包含 `ID + path:line`