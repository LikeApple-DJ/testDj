# Code Review Checklist

> **Change** testdemo · **分支/Commit** `AI/task-DEV-d885ee0d-...` / initial · **日期** `2026-10-23`
>
> **AI**：唯一进度源；状态仅用 `⬜` `✅` `❌` `⚠️` `N/A`。
> **完成标准**：所有核销项必须从 `⬜` 变为其他状态；`N/A` 需写原因。

---

## Step 1 — 执行队列（产物 A）

| # | 文件（仓库相对路径） | 归属原因 | Step2 | Step3 | G1 | G2 | G3 | G4 | G5 | G6 | G7 | G8 | G9 | G10 | G11 | G12 | G13 | G14 | G15 | G16 | G17 | S1 | S2 | S3 | S4 | S5 | S6 | S7 | S8 | S9 | S10 | 总状态 |
|---|----------------------|----------|-------|-------|----|----|----|----|----|----|----|----|----|-----|----|----|----|----|----|----|----|----|-----|-----|-----|-----|-----|-----|-----|-----|-----|--------|
| 1 | src/main/java/com/dtazziboot/testdemo/TestdemoApplication.java | REQ-1（启动类） | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 2 | src/main/java/com/dtazziboot/testdemo/common/constant/CalculatorConstants.java | REQ-1（操作常量） | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 3 | src/main/java/com/dtazziboot/testdemo/common/exception/BusinessException.java | REQ-3/4（异常） | ✅ | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ 已审有问题 |
| 4 | src/main/java/com/dtazziboot/testdemo/model/dto/CalculatorRequest.java | REQ-6（请求校验） | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 5 | src/main/java/com/dtazziboot/testdemo/model/dto/CalculatorVO.java | REQ-5（VO转换） | ✅ | ⚠️ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ 已审有问题 |
| 6 | src/main/java/com/dtazziboot/testdemo/model/entity/CalculatorDO.java | REQ-1（数据对象） | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 7 | src/main/java/com/dtazziboot/testdemo/service/CalculatorService.java | REQ-1（服务接口） | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |
| 8 | src/main/java/com/dtazziboot/testdemo/service/impl/CalculatorServiceImpl.java | REQ-1/2/3（核心实现） | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ⚠️ 已审有问题 |
| 9 | src/test/java/com/dtazziboot/testdemo/service/impl/CalculatorServiceImplTest.java | REQ-7（单测） | ✅ | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | N/A | ✅ 已审 |

非 Java 文件（跳过）：
| # | 文件 | 说明 |
|---|------|------|
| S1 | .agents/coding-impl.md | 非 Java，跳过 |
| S2 | .gitignore | 非 Java，跳过 |
| S3 | SSOT.md | 非 Java，跳过 |
| S4 | docs/ARCHITECTURE.md | 非 Java，跳过 |
| S5 | docs/modules/testdemo/README.md | 非 Java，跳过 |
| S6 | pom.xml | 非 Java，跳过 |
| S7 | src/main/resources/application.yml | 非 Java，跳过 |

---

## Step 2 — 功能（产物 B）

| REQ | Scenario | Spec证据（原文/章节） | 关联文件 | 状态 | 代码证据（文件/测试/接口） |
|-----|----------|----------------------|----------|------|----------------------------|
| REQ-1 | 提供加/减/乘/除四则运算 | coding-impl.md L15 "提供计算器四则运算服务（加/减/乘/除）" | CalculatorServiceImpl.java | ✅ | `:37-56` switch 覆盖四种操作；单测 13 个方法覆盖全部路径 |
| REQ-2 | 参数校验（null/空/无效操作） | coding-impl.md L15 "包含参数校验" | CalculatorServiceImpl.java | ✅ | `:27-32` null/blank 检查；`:54-55` default 分支抛异常 |
| REQ-3 | 业务异常处理（BusinessException + errorCode） | coding-impl.md L15 "业务异常处理"; ARCHITECTURE.md L41 "自定义 BusinessException 替代裸 RuntimeException" | CalculatorServiceImpl.java, BusinessException.java | ✅ | `:28,31,49,55` 统一抛出 BusinessException；单测验证异常类型与消息 |
| REQ-4 | 自定义 BusinessException（errorCode + message） | ARCHITECTURE.md L41 | BusinessException.java | ✅ | `:6-17` 继承 RuntimeException，含 errorCode 字段 |
| REQ-5 | CalculatorVO DO→VO 转换 + stripTrailingZeros | coding-impl.md L19 "CalculatorVO — 视图对象（含 DO→VO 转换、stripTrailingZeros）" | CalculatorVO.java | ✅ | `:53-64` from() 方法；`:57,59,61-62` stripTrailingZeros |
| REQ-6 | CalculatorRequest @NotBlank 校验 operation | coding-impl.md L20 "CalculatorRequest — 请求 DTO（@NotBlank 校验 operation）" | CalculatorRequest.java | ✅ | `:10` @NotBlank 注解 |
| REQ-7 | 单测 13 方法 AAA 模式 | coding-impl.md L58-62 | CalculatorServiceImplTest.java | ✅ | 13 个 @Test 方法，均含 Arrange/Act/Assert 注释分隔 |

---

## Step 3 — 可读性检查（产物 C）

> 脚本预扫命中：无 A* 相关命中。

| ID | 检查项 | 状态 | 备注（命中写 `path:line`） |
|----|--------|------|----------------------------|
| A1 | 源文件格式 | ✅ | 文件名=类名+.java ✓；UTF-8 ✓；无 Tab ✓ |
| A2 | 源文件结构/import 顺序 | ✅ | 无 `import *`；包→import→类顺序正确；ASCII 字典序排列正确 |
| A3 | 代码样式 | ✅ | K&R 大括号 ✓；4 空格缩进 ✓；行宽 ≤120 ✓；运算符两侧空格 ✓ |
| A4 | 命名规范 | ✅ | 包名全小写 ✓；类名 UpperCamelCase ✓；方法名 lowerCamelCase ✓；常量 UPPER_SNAKE_CASE ✓（`OP_ADD` 等）；测试类 `CalculatorServiceImplTest` ✓ |
| A5 | 编码实践 | ✅ | @Override 使用正确 ✓；无空 catch ✓；静态方法用类名调用 ✓；无 finalize() ✓ |
| A6 | 特定元素样式 | ✅ | 无 switch fall-through ✓；修饰符顺序正确 ✓；无 long 字面量 ✓ |
| A7 | Javadoc 规范 | ⚠️ | **BusinessException.java:10** — public 构造器缺少 Javadoc（A7.1，P2）；**CalculatorVO.java:61** — `setScale(4, ...)` 硬编码精度值 4，应引用 `CalculatorConstants.DEFAULT_SCALE`（P2 可维护性） |

---

## Step 4 — 可靠性检查（产物 D）

> **脚本预扫结果**：`scan-all-rules.sh` 命中 2 处 M016，已并入下方。

### 4.1 Bug 模式（`bug-pattern-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| B001–B005 | ✅ | 无 AlwaysThrows / ArrayEquals / ArraysAsList 等 |
| B006 | ✅ | 使用 AssertJ `assertThat`，无 assertEquals 参数顺序问题 |
| B007 | ✅ | 无 catch Throwable |
| B008 | ✅ | 无 Executors 创建线程池 |
| B009–B020 | ✅ | 无移位 / BigDecimal(double) / 包装类型 == / Calendar / Collection 不兼容等 |
| B021 | ✅ | 无 Jedis |
| B022–B024 | ✅ | 无 SimpleDateFormat / DeadException / DeadThread |
| B025 | ✅ | 无双括号初始化 |
| B026–B029 | ✅ | 无 equals(null) / equals 错误比较 / ErroneousDateUtil / ErroneousPojoSetter |
| B030–B040 | ✅ | 无浮点 == / FormatString / GetClassOnAnnotation / IdentityBinaryExpression 等 |
| B041–B045 | ✅ | 无 JDBC / JUnit3/4 混用 / LockOnBoxedPrimitive |
| B046–B055 | ✅ | 无 LoopCondition / LossyPrimitiveCompare / MisusedDayOfYear / MissingFail 等 |
| B056–B060 | ✅ | 无 ModificationOnArraysAsList / ModifyCollectionInEnhancedForLoop / NullTernary 等 |
| B061–B063 | ✅ | 无 sun.misc.BASE64 / URLClassLoader 强转 / javax.xml.bind |
| B064–B070 | ✅ | 无 Optional == / PojoSelfAssignment / SelfAssignment 等 |
| B071–B075 | ✅ | 无 SizeGreaterThanOrEqualsZero / StreamToString / StringBuilder(char) / SubstringOfZero / SuspiciousForLoop |
| B076 | ✅ | 无 @Transactional |
| B077–B081 | ✅ | 无 TryFailThrowable / TruthSelfEquals / UnnecessaryAssignment / UnitCaseNoAssertions / UnusedCollectionModifiedInPlace |
| M001–M002 | ✅ | 无 AlreadyChecked / BadInstanceof |
| M003 | ✅ | 无 new Long() 等弃用构造器 |
| M004 | ✅ | 无 printStackTrace |
| M005 | ✅ | 无应静态化的内部类 |
| M006–M007 | ✅ | 无 ComplexBooleanConstant / 空 catch |
| M008 | ✅ | 无自定义 equals（无 Hash 容器使用场景） |
| M009–M010 | ✅ | 无 EqualsIncompatibleType / ErroneousBitwiseExpression |
| M011 | ✅ | CalculatorServiceImpl switch 每个 case 有 break/throw ✓ |
| M012 | ✅ | 无 finally return/throw |
| M013–M015 | ✅ | 无 FloatCast / GetClassOnEnum / HidingField |
| M016 | ⚠️ | **CalculatorServiceImpl.java:68-69** — `LocalDateTime.now()` 依赖系统默认时区，应显式指定时区（脚本预扫命中，Major→P1） |
| M017 | ✅ | 所有测试方法都有 @Test |
| M018 | ✅ | 无 Lock |
| M019 | ✅ | switch 覆盖所有常量 + default ✓ |
| M020 | ✅ | @Override 使用正确 |
| M021–M027 | ✅ | 无非重写 equals / NullOptional / ObjectToString / OptionalNotPresent / ProtectedMembersInFinalClass / StaticMockMember / ThreadLocal |
| I001–I010 | ✅ | 无相关 Info 项命中 |

### 4.2 可靠性（`reliability-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| G1.1–G1.4 | N/A | 无并发/锁场景 |
| G2.1–G2.3 | N/A | 无写接口/消息消费，幂等不适用 |
| G3.1–G3.2 | N/A | 无事务注解/分布式事务 |
| G4.1–G4.3 | N/A | 无 SQL/MyBatis |
| G5.1 | N/A | 无 MQ |
| G6.1–G6.2 | N/A | 无缓存 |
| G7.1–G7.2 | N/A | 无调度任务 |
| G8.1 | N/A | 无 try-catch 吞异常 |
| G8.2 | N/A | 无外部依赖调用 |
| G8.3 | N/A | 无 I/O 流/连接 |
| G8.4 | N/A | 无线程池 |
| G8.5 | N/A | 无 ThreadLocal |
| G8.6 | N/A | 无 Executors |
| G9.1–G9.3 | N/A | 无网络调用 |
| G10.1–G10.2 | N/A | 本模块无接口契约变更场景 |
| G11.1 | ✅ | 13 个单测方法，均有断言 |
| G11.2 | ✅ | 覆盖正常/边界/异常/大数场景 |
| G11.3 | ✅ | CalculatorServiceImpl:27-32 null 检查 |
| G11.4 | ✅ | BigDecimal 运算，除法指定 scale+RoundingMode；无 float/double |
| G12.1–G12.2 | N/A | 无资金场景 |
| G13.1 | ✅ | 日志级别：debug 有 isDebugEnabled 守卫；info 正常流程；error 仅在异常时（由 BusinessException 携带） |
| G14.1 | ✅ | 使用 BigDecimal，无浮点金额 |
| G14.2 | N/A | 无多租户 |
| G14.3 | N/A | 无跨时区展示需求 |
| G14.4 | N/A | 无 SimpleDateFormat/DateTimeFormatter |
| G15.1–G15.3 | N/A | 无 DB 表变更 / 接口共存 |
| G16.1 | N/A | 无核心链路埋点需求（演示项目） |
| G16.2 | ✅ | 异常路径抛 BusinessException（含 errorCode）；正常流程 info 日志含上下文 |
| G16.3 | ✅ | info 用于正常完成；BusinessException 用于异常 |
| G16.4 | ✅ | 无空 catch / printStackTrace |
| G17.1–G17.3 | N/A | 演示项目无应急需求 |

### 4.3 安全（`security-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| S1.1–S1.3 | N/A | 无 SQL |
| S2.1–S2.3 | N/A | 无 HTML/JS 输出 |
| S3.1–S3.3 | N/A | 无外部 URL 请求 |
| S4.1–S4.2 | N/A | 无命令执行 |
| S5.1–S5.2 | N/A | 无 XML 解析 |
| S6.1–S6.3 | N/A | 无反序列化配置 |
| S7.1–S7.3 | N/A | 无文件上传/下载 |
| S8.1–S8.4 | N/A | 无 Web 接口暴露 |
| S9.1–S9.4 | N/A | 无密钥/凭证/加密 |
| S10.1–S10.3 | N/A | 无 CSRF/CORS/跳转 |

---

## Step 5 — 自定义扩展检查（产物 E）

### 5.1 自定义扩展（`customized-checklist.md`）

| ID | 状态 | 备注 |
|----|------|------|
| U1.1 | N/A | 示例项，项目未启用自定义规则 |
| U2.1–U2.3 | N/A | 业务红线未定义 |

**结论**：N/A（未启用自定义规则）

---

## 终检（防漏检）

- [x] 执行队列中每个文件 `Step2`、`Step3`、**S1–S10 / G1–G17** 各列均非 `⬜`（跳过文件除外）
- [x] Step 2 的每个 REQ/Scenario 均非 `⬜`
- [x] Step 3 的 A1–A7 均非 `⬜`
- [x] Step 4 全部 **G/S** 与 **B001–B081 / M001–M027 / I001–I010** ID 均非 `⬜`
- [x] Step 5 全部 U* ID 均非 `⬜`
- [x] 所有 `❌/⚠️` 已写入 report，且包含 `ID + path:line`