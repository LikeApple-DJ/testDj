# Code Review Report

> **Change** testdemo · **分支/Commit** `AI/task-DEV-d885ee0d-...` / initial · **日期** `2026-10-23` · **审查者** AI
>
> **AI**：等级 **P0 / P1 / P2**；G/S 以 checklist 行内定义为准；Bug 模式以 `bug-pattern-checklist.md` 表头为准（Blocker→P0、Major→P1、Info→P2）。**已先**运行 `scan-all-rules.sh` 并入 §5。问题含 `path:line` 或清单 ID。

---

## 1. 审查范围

| 项 | 值 |
|----|-----|
| `.java` 文件数 | 9 |
| 变更行数 | 全新模块，全量约 550 行 Java 代码 |

| 类/接口 | 路径 | 角色 |
|---------|------|------|
| TestdemoApplication | src/main/java/com/dtazziboot/testdemo/TestdemoApplication.java | Spring Boot 启动类 |
| CalculatorConstants | src/main/java/com/dtazziboot/testdemo/common/constant/CalculatorConstants.java | 业务常量（操作类型/精度） |
| BusinessException | src/main/java/com/dtazziboot/testdemo/common/exception/BusinessException.java | 自定义业务异常 |
| CalculatorRequest | src/main/java/com/dtazziboot/testdemo/model/dto/CalculatorRequest.java | 请求 DTO |
| CalculatorVO | src/main/java/com/dtazziboot/testdemo/model/dto/CalculatorVO.java | 响应 VO（含 DO→VO 转换） |
| CalculatorDO | src/main/java/com/dtazziboot/testdemo/model/entity/CalculatorDO.java | 数据对象 |
| CalculatorService | src/main/java/com/dtazziboot/testdemo/service/CalculatorService.java | 服务接口 |
| CalculatorServiceImpl | src/main/java/com/dtazziboot/testdemo/service/impl/CalculatorServiceImpl.java | 核心计算逻辑实现 |
| CalculatorServiceImplTest | src/test/java/com/dtazziboot/testdemo/service/impl/CalculatorServiceImplTest.java | 单元测试（13 用例） |

---

## 2. 问题计数

| P0 | P1 | P2 |
|----|----|-----|
| 0 | 1 | 2 |

---

## 3. Step 2 — 功能（REQ）

### REQ-1: 提供加/减/乘/除四则运算

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| ADD / SUBTRACT / MULTIPLY / DIVIDE 四种操作正确计算 | ✅ | coding-impl.md L15 | CalculatorServiceImpl.java:37-56 switch 全覆盖；单测 13 方法验证 | 每种操作均有 case 分支与 break |

### REQ-2: 参数校验

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| operation null/blank 拒绝 | ✅ | coding-impl.md L15 | CalculatorServiceImpl.java:27-29 | BusinessException B0001 |
| operand null 拒绝 | ✅ | coding-impl.md L15 | CalculatorServiceImpl.java:30-32 | BusinessException B0002 |
| 无效操作类型拒绝 | ✅ | coding-impl.md L15 | CalculatorServiceImpl.java:54-55 | default 分支 B0004 |

### REQ-3: 业务异常处理

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 除零异常 | ✅ | coding-impl.md L15 | CalculatorServiceImpl.java:48-49 | B0003 + 单测 should_throwException_when_divideByZero |

### REQ-4: 自定义 BusinessException

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| errorCode + message 结构 | ✅ | ARCHITECTURE.md L41 | BusinessException.java:6-17 | 继承 RuntimeException，final errorCode |

### REQ-5: CalculatorVO DO→VO 转换

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| stripTrailingZeros 正确应用 | ✅ | coding-impl.md L19 | CalculatorVO.java:57,59,61-62 | null 安全处理 |

### REQ-6: CalculatorRequest @NotBlank 校验

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| operation 字段非空约束 | ✅ | coding-impl.md L20 | CalculatorRequest.java:10 | jakarta.validation @NotBlank |

### REQ-7: 单测覆盖（13 方法 AAA）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 13 个测试方法 | ✅ | coding-impl.md L58-62 | CalculatorServiceImplTest.java:31-214 | Happy Path / 边界 / 异常 / 大数全覆盖 |

---

## 4. Step 3 — 可读性检查

| 结果 | 说明（违规写 Ax.x 与 `path:行`） |
|------|--------------------------------|
| ⚠️ | **P2 A7.1** `BusinessException.java:10` — public 构造器 `BusinessException(String, String)` 缺少 Javadoc |
| ⚠️ | **P2** `CalculatorVO.java:61` — `setScale(4, RoundingMode.HALF_UP)` 硬编码精度值 4，应引用 `CalculatorConstants.DEFAULT_SCALE` 以保持一致性和可维护性 |
| ✅ | 其余 A1–A7 项全部通过：无 `import *`、K&R 大括号、4 空格缩进、行宽 ≤120、命名规范正确 |

---

## 5. Step 4 — 可靠性检查

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| 可靠性 | `reliability-checklist.md` G1–G17 | ✅ | — | G11（自测）全部通过；其余 G1–G10/G12–G17 与演示项目无关，标 N/A |
| 安全 | `security-checklist.md` S1–S10 | N/A | — | 无 Web 接口、无 SQL、无文件操作、无外部调用，全部标 N/A |
| Bug 模式 | `bug-pattern-checklist.md` B/M/I（120） | ⚠️ | P1 | 预扫命中 **M016** ×2：`CalculatorServiceImpl.java:68-69` — `LocalDateTime.now()` 依赖默认时区；其余 119 条均已核销，无命中 |

### M016 详情

| 行号 | 代码 | 问题 |
|------|------|------|
| 68 | `calcDO.setGmtCreate(LocalDateTime.now());` | 未显式指定时区，跨时区部署时 `gmtCreate` 含义不明确 |
| 69 | `calcDO.setGmtModified(LocalDateTime.now());` | 同上 |

> **建议修复**：改用 `LocalDateTime.now(ZoneId.of("UTC"))` 或 `ZoneId.systemDefault()` 显式声明意图。

---

## 6. Step 5 — 自定义扩展检查

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| 自定义扩展 | `customized-checklist.md` U* | N/A | — | 未启用自定义规则 |

---

## 7. 结论

- **合并建议**：通过（建议修复 P1 后再合并）
- **P0**：无
- **P1**：1 项 — `M016` CalculatorServiceImpl.java:68-69 `LocalDateTime.now()` 未显式指定时区
- **P2**：2 项 — `A7.1` BusinessException 构造器缺 Javadoc；CalculatorVO 硬编码 scale 值
- **一句话**：testdemo 模块代码质量良好，功能实现与 spec 完全对齐，单测覆盖充分；仅有时区隐式依赖和 2 个轻微可维护性问题，建议修复 P1 后合入。

---

## 7.1 问题片段（必填）

- **P1** `M016` `src/main/java/com/dtazziboot/testdemo/service/impl/CalculatorServiceImpl.java:68-69` — `LocalDateTime.now()` 未显式指定时区，跨时区部署时时间语义不明确。
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

- **P2** `A7.1` `src/main/java/com/dtazziboot/testdemo/common/exception/BusinessException.java:10` — public 构造器缺少 Javadoc。
  片段范围：`src/main/java/com/dtazziboot/testdemo/common/exception/BusinessException.java:6-13`

```java
L06| public class BusinessException extends RuntimeException {
L07|
L08|     private final String errorCode;
L09|
L10|     public BusinessException(String errorCode, String message) {
L11|         super(message);
L12|         this.errorCode = errorCode;
L13|     }
```

- **P2** `CalculatorVO.java:61` — 硬编码精度值 4，应引用 `CalculatorConstants.DEFAULT_SCALE`。
  片段范围：`src/main/java/com/dtazziboot/testdemo/model/dto/CalculatorVO.java:57-64`

```java
L57|        vo.setOperandA(calcDO.getOperandA() != null
L58|                ? calcDO.getOperandA().stripTrailingZeros().toPlainString() : null);
L59|        vo.setOperandB(calcDO.getOperandB() != null
L60|                ? calcDO.getOperandB().stripTrailingZeros().toPlainString() : null);
L61|        vo.setResult(calcDO.getResult() != null
L62|                ? calcDO.getResult().setScale(4, RoundingMode.HALF_UP)
L63|                    .stripTrailingZeros().toPlainString() : null);
L64|        return vo;
```

---

## 8. 修复任务列表

### P1

- [ ] **P1** `src/main/java/com/dtazziboot/testdemo/service/impl/CalculatorServiceImpl.java:68-69` — 将 `LocalDateTime.now()` 改为 `LocalDateTime.now(ZoneId.of("UTC"))` 或显式指定业务时区

### P2

- [ ] **P2** `src/main/java/com/dtazziboot/testdemo/common/exception/BusinessException.java:10` — 为 public 构造器添加 Javadoc（`@param errorCode` + `@param message`）
- [ ] **P2** `src/main/java/com/dtazziboot/testdemo/model/dto/CalculatorVO.java:61` — 将硬编码 `setScale(4, ...)` 改为 `setScale(CalculatorConstants.DEFAULT_SCALE, ...)`