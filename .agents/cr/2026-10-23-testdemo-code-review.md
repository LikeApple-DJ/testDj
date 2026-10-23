# Code Review Report

> **Change** testdemo · **分支/Commit** `AI/task-DEV-d885ee0d...` / `e5d793c7` · **日期** `2026-10-23` · **审查者** AI
>
> **AI**：等级 **P0 / P1 / P2**；G/S 以 checklist 行内定义为准；Bug 模式以 `bug-pattern-checklist.md` 表头为准（Blocker→P0、Major→P1、Info→P2）。**已先**运行 `scan-all-rules.sh` 并将要点并入 §5，**再**写 LLM 结论。问题须含 `path:line` 或清单 ID。

---

## 1. 审查范围

| 项 | 值 |
|----|-----|
| `.java` 文件数 | 9 |
| 变更行数 | `+839 / -0` |

| 类/接口 | 路径 | 角色 |
|---------|------|------|
| TestdemoApplication | `src/main/java/com/dtazziboot/testdemo/TestdemoApplication.java` | Spring Boot 启动类 |
| CalculatorConstants | `src/main/java/com/dtazziboot/testdemo/common/constant/CalculatorConstants.java` | 业务常量 |
| BusinessException | `src/main/java/com/dtazziboot/testdemo/common/exception/BusinessException.java` | 业务异常 |
| CalculatorRequest | `src/main/java/com/dtazziboot/testdemo/model/dto/CalculatorRequest.java` | 请求 DTO |
| CalculatorVO | `src/main/java/com/dtazziboot/testdemo/model/dto/CalculatorVO.java` | 响应 VO |
| CalculatorDO | `src/main/java/com/dtazziboot/testdemo/model/entity/CalculatorDO.java` | 数据对象 |
| CalculatorService | `src/main/java/com/dtazziboot/testdemo/service/CalculatorService.java` | 服务接口 |
| CalculatorServiceImpl | `src/main/java/com/dtazziboot/testdemo/service/impl/CalculatorServiceImpl.java` | 服务实现 |
| CalculatorServiceImplTest | `src/test/java/com/dtazziboot/testdemo/service/impl/CalculatorServiceImplTest.java` | 单元测试 |

---

## 2. 问题计数

| P0 | P1 | P2 |
|----|----|-----|
| 0 | 2 | 2 |

---

## 3. Step 2 — 功能（REQ）

> Spec 来源：`docs/ARCHITECTURE.md`、`docs/modules/testdemo/README.md`、`.agents/coding-impl.md`

### REQ-1: 四则运算（ADD / SUBTRACT / MULTIPLY / DIVIDE）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 加法 | ✅ | ARCHITECTURE.md L10 "加/减/乘/除四则运算" | CalculatorServiceImpl.java:38-39 | `operandA.add(operandB)` |
| 减法 | ✅ | 同上 | CalculatorServiceImpl.java:41-42 | `operandA.subtract(operandB)` |
| 乘法 | ✅ | 同上 | CalculatorServiceImpl.java:44-45 | `operandA.multiply(operandB)` |
| 除法（含除零保护） | ✅ | 同上 | CalculatorServiceImpl.java:47-53 | `divide(operandB, scale, HALF_UP)` + 除零检查 |

### REQ-2: 参数校验（null / 空字符串 / 非法操作类型）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| operation 为 null | ✅ | ARCHITECTURE.md L10 "参数校验" | CalculatorServiceImpl.java:27-28 | B0001 + "操作类型不能为空" |
| operation 为空字符串 | ✅ | 同上 | CalculatorServiceImpl.java:27-28 | trim().isEmpty() 覆盖 |
| operandA / operandB 为 null | ✅ | 同上 | CalculatorServiceImpl.java:30-31 | B0002 + "操作数不能为空" |
| 非法 operation | ✅ | 同上 | CalculatorServiceImpl.java:54-55 | default 分支 B0004 |
| 除数为零 | ✅ | 同上 | CalculatorServiceImpl.java:48-49 | B0003 + "除数为零" |

### REQ-3: 自定义 BusinessException + errorCode

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| errorCode 可获取 | ✅ | ARCHITECTURE.md L41 "自定义 BusinessException" | BusinessException.java:8,15-16 | `getErrorCode()` 暴露 |
| 错误码体系 B0001-B0004 | ✅ | README.md L25 | CalculatorServiceImpl.java:28,31,49,55 | 四个场景各有独立错误码 |

### REQ-4: BigDecimal 精度计算

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 使用 BigDecimal | ✅ | ARCHITECTURE.md L41 "禁止 System.out / e.printStackTrace" + 数科编码规范 | 全链路 BigDecimal | 无 float/double 参与运算 |
| 除法指定精度 | ✅ | CalculatorConstants.DEFAULT_SCALE=4 | CalculatorServiceImpl.java:51-52 | divide(operandB, 4, HALF_UP) |

### REQ-5: SLF4J 占位符日志

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| SLF4J Logger | ✅ | ARCHITECTURE.md L40 "使用 SLF4J 占位符日志" | CalculatorServiceImpl.java:22 | LoggerFactory.getLogger |
| debug 级别判断 | ✅ | 同上 | CalculatorServiceImpl.java:58-60 | `if (logger.isDebugEnabled())` |
| info 占位符 | ✅ | 同上 | CalculatorServiceImpl.java:71-72 | `logger.info("... {}", ...)` |

### REQ-6: 接口与实现分离（ServiceImpl 后缀）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 接口定义 | ✅ | ARCHITECTURE.md L39 "接口与实现分离" | CalculatorService.java | 独立接口 |
| 实现类后缀 | ✅ | 同上 | CalculatorServiceImpl.java L20 | `implements CalculatorService` |

### REQ-7: DO→VO 转换 + stripTrailingZeros

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| from(CalculatorDO) | ✅ | README.md L14 "含 DO→VO 转换" | CalculatorVO.java:53-64 | 静态工厂方法 |
| stripTrailingZeros | ✅ | README.md L14 | CalculatorVO.java:57,59,62 | 三个字段均处理 |

### REQ-8: 常量类（操作类型 + 精度）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| final class + 私有构造 | ✅ | README.md L16 "操作类型 & 精度常量" | CalculatorConstants.java:6,8 | `public final class` + `private` 构造 |
| 常量定义 | ✅ | 同上 | CalculatorConstants.java:13-19 | DEFAULT_SCALE + 4 个 OP_* |

### REQ-9: 单元测试覆盖

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 13 个测试方法 | ✅ | coding-impl.md L60 "测试方法数: 13" | CalculatorServiceImplTest.java | 全部标注 @Test |
| AAA 模式 | ✅ | coding-impl.md L99 "AAA 模式" | 全测试类 | Arrange/Act/Assert 注释 |
| 覆盖场景: 正常/异常/边界 | ✅ | coding-impl.md L62 | 测试类覆盖全部场景 | Happy Path + Exception + Boundary |

---

## 4. Step 3 — 可读性检查

> 若无 Java 则 N/A。对照 `readability-checklist.md` A1–A7 逐文件核销。

| ID | 检查项 | 状态 | 备注（命中写 `path:line`） |
|----|--------|------|---------------------------|
| A1 | 源文件格式（文件名、UTF-8、无Tab） | ✅ | 所有 .java 文件符合 |
| A2 | 源文件结构/import 顺序 | ✅ | 无 `import *`，静态/非静态分组正确 |
| A3 | 代码样式（K&R、缩进4空格、行宽≤120） | ✅ | 全文件符合 |
| A4 | 命名规范 | ✅ | 包名全小写、类名大驼峰、方法小驼峰、常量全大写 |
| A5 | 编码实践（@Override、非空catch、静态方法调用） | ✅ | CalculatorServiceImpl L24 含 @Override |
| A6 | 特定元素样式（数组、switch、修饰符、long字面量） | ✅ | switch 有 default 分支 |
| A7 | Javadoc 规范 | ⚠️ | 详见下文 |

**A7 详情**：

- **P2** `A7.1` — `CalculatorServiceImpl.java:20` — 类级别 Javadoc 过于简略（仅「计算器服务实现」），建议补充职责说明、异常定义、线程安全性等关键信息。
- **P2** `A7.2` — `CalculatorService.java:11-18` — `calculate` 方法的 Javadoc 缺少 `@throws` 标签；方法会抛出 `BusinessException`，建议补充异常说明。
- 其余各类均有基本类级 Javadoc，getter/setter 按 A7.3 合理省略，通过。

---

## 5. Step 4 — 可靠性检查

### 自动化预扫结果（scan-all-rules.sh）

```
[P1] M016 — JavaTimeDefaultTimeZone: CalculatorServiceImpl.java:68
[P1] M016 — JavaTimeDefaultTimeZone: CalculatorServiceImpl.java:69
```

### 5.1 可靠性（`reliability-checklist.md` G1–G17）

| ID | 状态 | 备注 |
|----|------|------|
| G1.1 | N/A | 无事务/并发场景，纯计算服务无锁操作 |
| G1.2 | N/A | 同上 |
| G1.3 | N/A | 无乐观锁 |
| G1.4 | N/A | 无多资源加锁 |
| G2.1 | N/A | 无写接口/消息消费 |
| G2.2 | N/A | 无重试/MQ |
| G2.3 | N/A | 无幂等键 |
| G3.1 | N/A | 无分布式事务 |
| G3.2 | N/A | 无 @Transactional |
| G4.1 | N/A | 无 SQL/MyBatis |
| G4.2 | N/A | 无 SQL |
| G4.3 | N/A | 无数据库查询 |
| G5.1 | N/A | 无 MQ |
| G6.1 | N/A | 无缓存 |
| G6.2 | N/A | 无缓存双写 |
| G7.1 | N/A | 无调度任务 |
| G7.2 | N/A | 无调度任务 |
| G8.1 | ✅ | 所有异常路径均抛 BusinessException + 日志 |
| G8.2 | N/A | 无外部依赖 |
| G8.3 | ✅ | 无 I/O 流、连接、锁等需释放资源 |
| G8.4 | N/A | 无线程池 |
| G8.5 | N/A | 无 ThreadLocal |
| G8.6 | N/A | 无线程池创建 |
| G9.1 | N/A | 无外部 RPC/HTTP 调用 |
| G9.2 | N/A | 无外部调用 |
| G9.3 | N/A | 无重试逻辑 |
| G10.1 | ✅ | null 语义清晰（表示未提供参数），已有前置校验保护 |
| G10.2 | N/A | 无接口版本变更 |
| G11.1 | ✅ | 13 个单测方法，含断言 |
| G11.2 | ✅ | 覆盖 null、blank、空字符串、除零、大数边界 |
| G11.3 | ✅ | 入参 null 有 `BusinessException` 防御性校验 |
| G11.4 | ✅ | 全程 BigDecimal，除零有保护，精度指定 |
| G12.1 | N/A | 非资金场景 |
| G12.2 | N/A | 非资金场景 |
| G13.1 | ✅ | debug 用 debug 级别，info 用于正常完成日志，error 未使用（异常通过 throw 传播） |
| G14.1 | ✅ | 使用 BigDecimal |
| G14.2 | N/A | 无多租户 |
| G14.3 | ⚠️ | 见下方 M016 详情 |
| G14.4 | ⚠️ | 见下方 M016 详情 |
| G15.1 | N/A | 无 DDL 变更 |
| G15.2 | N/A | 新模块，无旧接口 |
| G15.3 | N/A | 无开关逻辑 |
| G16.1 | N/A | 无监控埋点（演示项目） |
| G16.2 | ✅ | 异常路径有日志 + errorCode 追溯；正常路径有 info 日志 |
| G16.3 | ✅ | debug 级别包装、info 用于正常业务 |
| G16.4 | ✅ | 无空 catch、无 printStackTrace |
| G17.1 | N/A | 演示项目 |
| G17.2 | N/A | 演示项目 |
| G17.3 | N/A | 无数据变更 |

### 5.2 Bug 模式（`bug-pattern-checklist.md` B/M/I）

> 预扫命中 M016（2 处），其余由 LLM 逐条核对。

| ID | 状态 | 备注 |
|----|------|------|
| B001 | ✅ | 无 AlwaysThrows 模式 |
| B002 | ✅ | 无数组 equals 比较 |
| B003 | ✅ | 无 Arrays.fill |
| B004 | ✅ | 无数组 toString |
| B005 | ✅ | 无 Arrays.asList 装箱 |
| B006 | ✅ | 测试中 assertEquals 未使用（使用 AssertJ） |
| B007 | ✅ | 无 catch Throwable |
| B008 | ✅ | 无 Executors |
| B009 | ✅ | 无移位运算 |
| B010 | ✅ | BigDecimal 使用字符串构造：`new BigDecimal("10.5")` ✅ |
| B011 | ✅ | 无包装类型 == 比较 |
| B012 | ✅ | 无 Calendar |
| B013 | ✅ | 无 Calendar |
| B014 | ✅ | 无集合泛型不兼容 |
| B015 | ✅ | 无 Collection.toArray |
| B016 | ✅ | 无 Comparable |
| B017 | ✅ | 无 this == null |
| B018 | ✅ | 无三目类型提升 |
| B019 | ✅ | 无 Money API |
| B020 | ✅ | 无编译期常量溢出 |
| B021 | ✅ | 无 Jedis |
| B022 | ✅ | 无 SimpleDateFormat |
| B023 | ✅ | 无 DeadException |
| B024 | ✅ | 无 DeadThread |
| B025 | ✅ | 无双括号初始化 |
| B026 | ✅ | 无 equals(null) |
| B027 | ✅ | 无 equals 错误比较 |
| B028 | ✅ | 无 DateUtil |
| B029 | ✅ | 无 setter 赋值错误 |
| B030 | ✅ | 无浮点 == 比较 |
| B031 | ✅ | String.format 未使用 |
| B032 | ✅ | 无注解 getClass |
| B033 | ✅ | 无 Unsafe |
| B034 | ✅ | 无 Hashtable |
| B035 | ✅ | 无 IdentityBinaryExpression |
| B036 | ✅ | 无 IdentityHashMap |
| B037 | ✅ | 无 varargs 条件 |
| B038 | ✅ | 无无限递归 |
| B039 | ✅ | 无 IndexOfChar 参数颠倒 |
| B040 | ✅ | 无 isInstance 不兼容类型 |
| B041 | ✅ | 无 JDBC |
| B042 | ✅ | JUnit 5，非 JUnit 3 |
| B043 | ✅ | 无内部类 @Test |
| B044 | ✅ | JUnit 5 纯 Jupiter |
| B045 | ✅ | 无包装类型加锁 |
| B046 | ✅ | 无死循环 |
| B047 | ✅ | 无 LossyPrimitiveCompare |
| B048 | ✅ | 无 Math.round 传入整型 |
| B049 | ✅ | 无日期格式 DD |
| B050 | ✅ | 无 12/24 小时制混用 |
| B051 | ✅ | 无 Boolean.getBoolean |
| B052 | ✅ | 无 YYYY 格式 |
| B053 | ✅ | assertThatThrownBy 替代 try-fail 模式 ✅ |
| B054 | ✅ | 无 EqualsTester |
| B055 | ✅ | 无 Mockito |
| B056 | ✅ | 无 Arrays.asList 修改 |
| B057 | ✅ | 无增强 for 循环修改集合 |
| B058 | ✅ | 无集合自操作 |
| B059 | ✅ | 无 nCopies |
| B060 | ✅ | 无 NullTernary 拆箱 |
| B061 | ✅ | 无 sun.misc BASE64 |
| B062 | ✅ | 无 ClassLoaderCast |
| B063 | ✅ | 无 javax.xml |
| B064 | ✅ | 无 Optional == |
| B065 | ✅ | 无 POJO 自赋值 |
| B066 | ✅ | 无 Math.random 强转 |
| B067 | ✅ | 无 Random.nextInt % |
| B068 | ✅ | 无自赋值 |
| B069 | ✅ | 无 compareTo 自比较 |
| B070 | ✅ | 无 equals 自比较 |
| B071 | ✅ | 无 size >= 0 |
| B072 | ✅ | 无 Stream.toString |
| B073 | ✅ | 无 StringBuilder(char) |
| B074 | ✅ | 无 substring(0) |
| B075 | ✅ | 无可疑 for 循环 |
| B076 | ✅ | 无 @Transactional |
| B077 | ✅ | 无 catch Throwable 吞 AssertionError |
| B078 | ✅ | 无 Truth self equals |
| B079 | ✅ | 无 @Mock |
| B080 | ✅ | 单测含 assertThat 断言 ✅ |
| B081 | ✅ | 无 UnusedCollectionModifiedInPlace |
| M001 | ✅ | 无重复条件判断 |
| M002 | ✅ | 无 BadInstanceof |
| M003 | ✅ | 无包装类 new |
| M004 | ✅ | 无 printStackTrace |
| M005 | ✅ | 无内部类 |
| M006 | ✅ | 无编译期常量布尔 |
| M007 | ✅ | 无空 catch |
| M008 | ✅ | CalculatorDO 未重写 equals/hashCode（无此需求） |
| M009 | ✅ | 无不相容类型 equals |
| M010 | ✅ | 无位运算 |
| M011 | ✅ | switch 每个 case 有 break ✅ |
| M012 | ✅ | 无 finally return |
| M013 | ✅ | 无类型转换优先级 |
| M014 | ✅ | 无枚举 |
| M015 | ✅ | 无父子类字段隐藏 |
| **M016** | **⚠️** | **`CalculatorServiceImpl.java:68-69` — `LocalDateTime.now()` 未指定时区（脚本命中 x2）** |
| M017 | ✅ | 所有测试方法有 @Test ✅ |
| M018 | ✅ | 无 lock |
| M019 | ✅ | 无枚举 switch |
| M020 | ✅ | calculate() 有 @Override ✅ |
| M021 | ✅ | 无错误 equals 签名 |
| M022 | ✅ | 无 Optional.of(null) |
| M023 | ✅ | CalculatorDO 已重写 toString ✅ |
| M024 | ✅ | 无 Optional.get 误用 |
| M025 | ✅ | 无 final class protected |
| M026 | ✅ | 无 @Mock |
| M027 | ✅ | 无 ThreadLocal |
| I001 | ✅ | 测试对异常消息做了 hasMessageContaining 断言 ✅ |
| I002 | ✅ | 无 @DoNotMock |
| I003 | ✅ | 无 @AutoValue |
| I004 | ✅ | 使用 LocalDateTime（非 java.util.Date） ✅ |
| I005 | ✅ | JUnit 5 |
| I006 | ✅ | 使用 @BeforeEach ✅ |
| I007 | ✅ | 无 tearDown |
| I008 | ✅ | 无 dataProvider |
| I009 | N/A | 统计用 |
| I010 | ✅ | 纯 JUnit 5，无 Spring 容器 |

### 5.3 安全（`security-checklist.md` S1–S10）

| ID | 状态 | 备注 |
|----|------|------|
| S1.1 | N/A | 无 SQL，无 MyBatis |
| S1.2 | N/A | 无动态 SQL |
| S1.3 | N/A | 无 like/in 查询 |
| S2.1 | N/A | 无 Web 输出（纯后端计算服务） |
| S2.2 | N/A | 无富文本 |
| S2.3 | N/A | 无模板引擎 |
| S3.1 | N/A | 无外部 URL 请求 |
| S3.2 | N/A | 无 302 跳转 |
| S3.3 | N/A | 无外部调用 |
| S4.1 | N/A | 无系统命令 |
| S4.2 | N/A | 无文件/图片操作 |
| S5.1 | N/A | 无 XML 解析 |
| S5.2 | N/A | 无 XPath |
| S6.1 | N/A | 无反序列化 |
| S6.2 | N/A | 无 JSON 多态 |
| S6.3 | N/A | 无 transient 需求 |
| S7.1 | N/A | 无文件上传 |
| S7.2 | N/A | 无文件路径 |
| S7.3 | N/A | 无文件操作 |
| S8.1 | N/A | 纯计算服务，无鉴权上下文 |
| S8.2 | N/A | 无 REST 端点 |
| S8.3 | N/A | 无数据 ID 暴露 |
| S8.4 | N/A | 无 Cookie |
| S9.1 | ✅ | 无硬编码密钥/凭证 |
| S9.2 | ✅ | 日志输出为操作类型和数值，无敏感信息 |
| S9.3 | N/A | 演示项目，无传输加密需求 |
| S9.4 | N/A | 无随机数 |

---

## 6. Step 5 — 自定义扩展检查

| ID | 状态 | 备注 |
|----|------|------|
| U1.1 | N/A | `customized-checklist.md` 仅含示例项 `U1.1`，未启用实际团队自定义规则。整节标记 **N/A(未启用自定义规则)**。 |

---

## 7. 结论

- **合并建议**：修复后合并
- **P0**：无
- **P1**：
  1. **M016** `CalculatorServiceImpl.java:68-69` — `LocalDateTime.now()` 未指定时区，建议改为 `LocalDateTime.now(ZoneId.of("UTC+8"))` 或使用 `Instant.now()` 存储 UTC
- **P2**：
  1. **A7.1** `CalculatorServiceImpl.java:20` — 类 Javadoc 过于简略，建议补充职责说明和异常信息
  2. **A7.2** `CalculatorService.java:11-18` — `calculate` 方法 Javadoc 缺少 `@throws` 标签
- **一句话**：代码质量良好，符合数科编码规范，架构清晰，测试覆盖充分；仅有时区和文档完善建议。

---

## 7.1 问题片段

### P1 问题

- **P1** `M016` `src/main/java/com/dtazziboot/testdemo/service/impl/CalculatorServiceImpl.java:68-69` — `LocalDateTime.now()` 依赖系统默认时区，在跨时区部署时可能导致时间不一致；应显式指定时区或改用 `Instant`。  
  片段范围：`src/main/java/com/dtazziboot/testdemo/service/impl/CalculatorServiceImpl.java:62-70`

```java
L62|        // 构建DO（模拟持久化记录）
L63|        CalculatorDO calcDO = new CalculatorDO();
L64|        calcDO.setOperation(op);
L65|        calcDO.setOperandA(operandA);
L66|        calcDO.setOperandB(operandB);
L67|        calcDO.setResult(result);
L68|        calcDO.setGmtCreate(LocalDateTime.now());
L69|        calcDO.setGmtModified(LocalDateTime.now());
L70|
```

### P2 问题

- **P2** `A7.1` `src/main/java/com/dtazziboot/testdemo/service/impl/CalculatorServiceImpl.java:16-18` — 类 Javadoc 仅一行「计算器服务实现」，未说明异常定义、精度策略、DO 构建行为。  
  片段范围：`src/main/java/com/dtazziboot/testdemo/service/impl/CalculatorServiceImpl.java:16-20`

```java
L16|/**
L17| * 计算器服务实现
L18| */
L19|@Service
L20|public class CalculatorServiceImpl implements CalculatorService {
```

- **P2** `A7.2` `src/main/java/com/dtazziboot/testdemo/service/CalculatorService.java:11-19` — 接口方法 Javadoc 缺少 `@throws` 标签，未说明 `BusinessException` 的抛出条件。  
  片段范围：`src/main/java/com/dtazziboot/testdemo/service/CalculatorService.java:11-19`

```java
L11|    /**
L12|     * 执行计算
L13|     *
L14|     * @param operation 操作类型 ADD / SUBTRACT / MULTIPLY / DIVIDE
L15|     * @param operandA  操作数A
L16|     * @param operandB  操作数B
L17|     * @return 计算响应
L18|     */
L19|    CalculatorVO calculate(String operation, BigDecimal operandA, BigDecimal operandB);
```

---

## 8. 修复任务列表

### P1

- [ ] **P1** `src/main/java/com/dtazziboot/testdemo/service/impl/CalculatorServiceImpl.java:68-69` — `LocalDateTime.now()` 改为 `LocalDateTime.now(ZoneId.of("UTC+8"))` 或 `LocalDateTime.now(ZoneOffset.UTC)`，补充 `import java.time.ZoneId`

### P2（可选）

- [ ] **P2** `src/main/java/com/dtazziboot/testdemo/service/impl/CalculatorServiceImpl.java:17` — 补充类级 Javadoc：说明职责（参数校验→计算→DO 构建→VO 转换）、异常类型（B0001-B0004）、线程安全性（无状态，线程安全）
- [ ] **P2** `src/main/java/com/dtazziboot/testdemo/service/CalculatorService.java:11-18` — 补充 `@throws BusinessException` 标签，说明各异常码的触发条件