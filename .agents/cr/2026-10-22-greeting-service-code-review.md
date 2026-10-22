# Code Review Report

> **Change** greeting-service · **分支/Commit** AI/task-DEV-d885ee0d-78e2-11f1-8f3f-75954cb1c56f-4b50af8e-3c64-413b-8101-3a168ba5719a / N/A · **日期** 2026-10-22 · **审查者** AI
>
> **AI**：等级 **P0 / P1 / P2**；G/S 以 checklist 行内定义为准；Bug 模式以 `bug-pattern-checklist.md` 表头为准（Blocker→P0、Major→P1、Info→P2）。**已执行** `scan-all-rules.sh`，命中要点并入 §5。问题须含 `path:line` 或清单 ID。

---

## 1. 审查范围

| 项 | 值 |
|----|-----|
| `.java` 文件数 | 6 |
| 变更行数 | `+572 / -1` |

| 类/接口 | 路径 | 角色 |
|---------|------|------|
| DemoApplication | `src/main/java/.../DemoApplication.java` | Spring Boot 启动类 |
| GreetingController | `src/main/java/.../controller/GreetingController.java` | REST API 端点 |
| GreetingVO | `src/main/java/.../model/vo/GreetingVO.java` | 视图对象 |
| GreetingService | `src/main/java/.../service/GreetingService.java` | 服务接口 |
| GreetingServiceImpl | `src/main/java/.../service/impl/GreetingServiceImpl.java` | 服务实现 |
| GreetingServiceImplTest | `src/test/java/.../GreetingServiceImplTest.java` | 单元测试 |

---

## 2. 问题计数

| P0 | P1 | P2 |
|----|----|-----|
| 0 | 3 | 1 |

---

## 3. Step 2 — 功能（REQ）

### REQ-1: 提供问候语生成 REST API

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| GET /api/greeting?name= | ✅ | impl.md L19 / docs/modules/demo/README.md L23 | GreetingController.java:18-19,35-39 | API 路径和 GET 方法正确 |

### REQ-2: 传入有效名称 → 个性化问候

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| name="Alice" → "Hello, Alice!" | ✅ | impl.md L29 | GreetingServiceImpl.java:32-36; 测试 L45 | 功能正确 |

### REQ-3: 中文名称正确处理

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| name="世界" → "Hello, 世界!" | ✅ | impl.md L30 | GreetingServiceImpl.java:32; 测试 L61 | `name.trim()` 正确处理中文 |

### REQ-4: 空字符串/空白字符 → 默认问候

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| name="" / "   " → "Hello, Stranger!" | ✅ | impl.md L31-32 | GreetingServiceImpl.java:32; 测试 L74/L88 | `name.isBlank()` 正确兜底 |

### REQ-5: name=null → 抛出异常

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| greet(null) → IllegalArgumentException | ✅ | impl.md L33 / GreetingService.java L17 | GreetingServiceImpl.java:29-31; 测试 L97 | 异常类型和消息正确 |

### REQ-6: greetWithFormat 正常路径

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| name="Bob", prefix="Hi" → "Hi, Bob!" | ✅ | impl.md L34 | GreetingServiceImpl.java:40-45; 测试 L119 | 格式正确 |

### REQ-7: greetWithFormat prefix=null/空 → 默认前缀

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| prefix=null / "" → "Hello, ...!" | ✅ | impl.md L35-36 | GreetingServiceImpl.java:41; 测试 L133/L146 | 默认前缀逻辑正确 |

### REQ-8: greetWithFormat name=null → 默认值（⚠️ 契约矛盾）

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| name=null → "Hello, Stranger!" | ⚠️ | GreetingService.java L24: `@param name 不能为 null` | GreetingServiceImpl.java:42 | **接口声明 name 不可为 null，但实现容错为默认值。接口与实现矛盾，需统一** |

### REQ-9: SLF4J + 占位符日志

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 日志使用 SLF4J | ✅ | impl.md L62 / docs/ARCHITECTURE.md L19 | Controller L37, ServiceImpl L34 | 占位符参数化正确 |

### REQ-10: @RequestParam 默认值防御 null

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| Controller 入参防御 | ✅ | impl.md L63 / docs/ARCHITECTURE.md L25 | GreetingController.java:36 `defaultValue="Stranger"` | 正确防御 |

---

## 4. Step 3 — 可读性检查

| 域 | 结果 | 说明（违规写 Ax.x 与 `path:行`） |
|----|------|--------------------------------|
| A1 源文件格式 | ✅ | 文件名=类名.java, UTF-8 编码 |
| A2 源文件结构/import | ✅ | 无 `import *`，静态/非静态分组正确 |
| A3 代码样式 | ✅ | K&R 大括号，4 空格缩进，行宽合规 |
| A4 命名规范 | ✅ | 包名/类名/方法/常量/测试类命名合规 |
| A5 编码实践 | ✅ | @Override 已加，无空 catch，无 finalize() |
| A6 特定元素样式 | ✅ | 修饰符顺序合规，数组声明 Java 风格 |
| A7 Javadoc 规范 | ✅ | public 类/方法有 Javadoc，getter/Override 可省略 |

**结论**: 所有 6 个 Java 文件可读性良好，无违规项。

---

## 5. Step 4 — 可靠性检查

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| 可靠性（G1-G17） | `reliability-checklist.md` | ⚠️ | P1 | G8.1 接口契约矛盾（REQ-8）；G14.4 `LocalDateTime.now()` 未指定 ZoneId |
| 安全（S1-S10） | `security-checklist.md` | ⚠️ | P1 | S8.1 GreetingController 无鉴权保护（demo 可接受） |
| Bug 模式（B/M/I） | `bug-pattern-checklist.md` | ⚠️ | P1 | M016 `[P1] JavaTimeDefaultTimeZone` GreetingServiceImpl.java:36,45 — `scan-all-rules.sh` 预扫命中 |

### 预扫结果

`scan-all-rules.sh` 扫描 52/222 条可程序化规则：
- **M016** `[P1]` ×2: `GreetingServiceImpl.java:36`, `GreetingServiceImpl.java:45` — `LocalDateTime.now()` 未显式指定 ZoneId

### 关键发现

1. **P1 — M016 / G14.4**: `GreetingServiceImpl.java:36,45` — `LocalDateTime.now()` 使用系统默认时区，在跨区域部署时可能产生不一致的 timestamp。建议改为 `LocalDateTime.now(ZoneId.of("UTC"))` 或使用 `Instant.now()`。

2. **P1 — G8.1**: `GreetingServiceImpl.java:42` — `greetWithFormat` 方法对 `name=null` 静默容错（返回 "Stranger"），但与接口 `GreetingService.java:24` 的 Javadoc 声明矛盾（`@param name 不能为 null`）。应统一：要么接口声明允许 null 并文档化容错行为，要么 greetWithFormat 也抛出 IllegalArgumentException。

3. **P2 — S8.1**: `GreetingController.java:35-39` — `GET /api/greeting` 无鉴权保护。demo 项目场景可接受，但若后续上线需接入认证。

---

## 6. Step 5 — 自定义扩展检查

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| U1.1 Controller 校验注解 | `customized-checklist.md` | ⚠️ | P1 | GreetingController.java:36 无 `@Valid` 等统一校验注解（demo 简单场景可接受） |
| U1.2-U2.3 | `customized-checklist.md` | N/A | — | 未启用自定义规则 |

---

## 7. 结论

- **合并建议**：修复后合并
- **P0**：无
- **P1**：3 项
  1. `G14.4 / M016` — `LocalDateTime.now()` 应显式指定 ZoneId（`GreetingServiceImpl.java:36,45`）
  2. `G8.1` — `greetWithFormat` 的 name=null 容错行为与接口契约矛盾，需统一（`GreetingServiceImpl.java:42` vs `GreetingService.java:24`）
  3. `U1.1` — Controller 入参缺少 `@Valid` 统一校验注解（`GreetingController.java:36`）
- **P2**：1 项
  1. `S8.1` — API 无鉴权保护（`GreetingController.java:35-39`）
- **一句话**：代码质量整体良好，8 个测试用例覆盖全面；存在接口契约不一致和时间区未指定的轻度问题，均为低风险可快速修复。

---

## 7.1 问题片段（必填）

### P1 — `G14.4 / M016` — `GreetingServiceImpl.java:36,45` — LocalDateTime.now() 未指定 ZoneId

片段范围：`src/main/java/com/example/demo/service/impl/GreetingServiceImpl.java:33-37`

```java
L33|        String message = DEFAULT_PREFIX + ", " + effectiveName + "!";
L34|        log.debug("Generated greeting for name: {}", effectiveName);
L35|
L36|        return new GreetingVO(message, effectiveName, LocalDateTime.now());
L37|    }
```

同样问题在 L45：
```java
L43|        String message = effectivePrefix + ", " + effectiveName + "!";
L44|
L45|        return new GreetingVO(message, effectiveName, LocalDateTime.now());
```

**修复建议**: 改为 `LocalDateTime.now(ZoneOffset.UTC)` 或 `Instant.now()`。

---

### P1 — `G8.1` — `GreetingServiceImpl.java:42` — 接口契约矛盾

片段范围：`src/main/java/com/example/demo/service/GreetingService.java:23-28`

```java
L23|     * @param name   用户名，不能为 null
L24|     * @param prefix 问候前缀，为 null 或空时使用默认值 "Hello"
...
L28|    GreetingVO greetWithFormat(String name, String prefix);
```

片段范围：`src/main/java/com/example/demo/service/impl/GreetingServiceImpl.java:40-43`

```java
L40|    public GreetingVO greetWithFormat(String name, String prefix) {
L41|        String effectivePrefix = (prefix == null || prefix.isBlank()) ? DEFAULT_PREFIX : prefix.trim();
L42|        String effectiveName = (name == null || name.isBlank()) ? DEFAULT_NAME : name.trim();
L43|        String message = effectivePrefix + ", " + effectiveName + "!";
```

**问题**: interface 声明 `name 不能为 null`，但实现用 `name == null` 容错处理。需统一策略。

---

### P1 — `U1.1` — `GreetingController.java:36` — 缺少 @Valid 校验

片段范围：`src/main/java/com/example/demo/controller/GreetingController.java:35-39`

```java
L35|    @GetMapping
L36|    public GreetingVO greet(@RequestParam(defaultValue = "Stranger") String name) {
L37|        log.info("Received greeting request for name: {}", name);
L38|        return greetingService.greet(name);
L39|    }
```

---

### P2 — `S8.1` — `GreetingController.java:35-39` — 无鉴权保护

（同上片段）

---

## 8. 修复任务列表

### P1

- [ ] **P1** `G14.4 / M016` `src/main/java/com/example/demo/service/impl/GreetingServiceImpl.java:36,45` — 将 `LocalDateTime.now()` 改为 `LocalDateTime.now(ZoneOffset.UTC)`
- [ ] **P1** `G8.1` `src/main/java/com/example/demo/service/GreetingService.java:24` 和 `GreetingServiceImpl.java:42` — 统一 name=null 行为：接口声明允许 null 并文档化容错，或实现层也抛出 IllegalArgumentException
- [ ] **P1** `U1.1` `src/main/java/com/example/demo/controller/GreetingController.java:36` — 评估是否需要引入 `@Valid` 校验体系（demo 项目可延后）

### P2

- [ ] **P2** `S8.1` `src/main/java/com/example/demo/controller/GreetingController.java:35-39` — 若后续上线，接入认证鉴权机制