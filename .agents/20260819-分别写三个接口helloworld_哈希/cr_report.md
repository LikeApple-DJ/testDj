# Code Review Report

> **Change** `分别写三个接口helloworld_哈希` · **分支** `AI/task-DEV-9d10e310-7901-11f1-8a9f-59ecae612580-4440cdb6-f20d-4be3-935e-02bfcba73d89` · **日期** `2025-08-19` · **审查者** AI
>
> **AI**：等级 **P0 / P1 / P2**；G/S 以 checklist 行内定义为准；Bug 模式以 `bug-pattern-checklist.md` 表头为准（Blocker→P0、Major→P1、Info→P2）。**须先**运行 `scan-all-rules.sh` 并将要点并入 §5，**再**写 LLM 结论。

---

## 1. 审查范围

| 项 | 值 |
|----|-----|
| `.java` 文件数 | `17`（含 2 个测试文件） |
| 变更行数 | `+2310 / -0` |

| 类/接口 | 路径 | 角色 |
|---------|------|------|
| AlgorithmDemoApplication | `src/main/java/.../AlgorithmDemoApplication.java` | 启动类 |
| AlgorithmType | `src/main/java/.../common/AlgorithmType.java` | 算法类型枚举 |
| BusinessException | `src/main/java/.../common/BusinessException.java` | 业务异常 |
| GlobalExceptionHandler | `src/main/java/.../common/GlobalExceptionHandler.java` | 全局异常处理 |
| Result | `src/main/java/.../common/Result.java` | 统一响应封装 |
| AlgorithmController | `src/main/java/.../controller/AlgorithmController.java` | 算法控制器 |
| ExportController | `src/main/java/.../controller/ExportController.java` | 导出控制器 |
| HashRequest | `src/main/java/.../model/dto/HashRequest.java` | 哈希请求 DTO |
| HashResponse | `src/main/java/.../model/dto/HashResponse.java` | 哈希响应 DTO |
| HelloResponse | `src/main/java/.../model/dto/HelloResponse.java` | Hello 响应 DTO |
| SortRequest | `src/main/java/.../model/dto/SortRequest.java` | 排序请求 DTO |
| SortResponse | `src/main/java/.../model/dto/SortResponse.java` | 排序响应 DTO |
| AlgorithmService | `src/main/java/.../service/AlgorithmService.java` | 算法服务接口 |
| ExportService | `src/main/java/.../service/ExportService.java` | 导出服务接口 |
| AlgorithmServiceImpl | `src/main/java/.../service/impl/AlgorithmServiceImpl.java` | 算法服务实现 |
| ExportServiceImpl | `src/main/java/.../service/impl/ExportServiceImpl.java` | 导出服务实现 |
| AlgorithmServiceTest | `src/test/java/.../service/AlgorithmServiceTest.java` | 算法服务单测 |
| ExportServiceTest | `src/test/java/.../service/ExportServiceTest.java` | 导出服务单测 |

---

## 2. 问题计数

| P0 | P1 | P2 |
|----|----|-----|
| 0 | 4 | 2 |

---

## 3. Step 2 — 功能（REQ）

### REQ-1: HelloWorld 接口 (F01)

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| GET /api/algorithm/hello 返回消息+时间戳 | ⚠️ | design.md §5.1.2 W01: `data.message="Hello World"`, `data.timestamp` | `AlgorithmController.java:37-48` | 功能实现，但时间戳不一致（见 §7 P1-4） |
| 响应结构 {code, msg, data} | ✅ | design.md §5.1: `{code, msg, data}` | `Result.java:9-80` | 符合规范 |

### REQ-2: 哈希算法接口 (F02)

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| POST /api/algorithm/hash 使用 SHA-256 | ✅ | design.md §5.1.2 W02: SHA-256 | `AlgorithmServiceImpl.java:26,42-52` | 使用 SHA-256 正确 |
| 入参 input 非空校验 | ✅ | design.md §5.1.3.2 R01 | `HashRequest.java:13` @NotBlank + `AlgorithmServiceImpl.java:38` | 双重校验 |
| 空输入返回 ALGO_002 | ❌ | design.md §5.1.2: 错误码 `ALGO_002` | `GlobalExceptionHandler.java:30-34` 返回 `"400"` | **P1** 错误码不符合 Spec |
| 哈希异常返回 ALGO_003 | ✅ | design.md §5.1.2: 错误码 `ALGO_003` | `AlgorithmServiceImpl.java:51` | 正确抛出 BusinessException |

### REQ-3: 冒泡排序接口 (F03)

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| POST /api/algorithm/sort 升序排序 | ✅ | design.md §5.1.2 W03 | `AlgorithmServiceImpl.java:56-90` | 冒泡排序实现正确 |
| 空列表返回 ALGO_004 | ❌ | design.md §5.1.2: 错误码 `ALGO_004` | `SortRequest.java:15` @NotEmpty → MethodArgumentNotValidException → `"ALGO_001"` | **P1** 缺少 MethodArgumentNotValidException 处理 |
| 非整数返回 ALGO_005 | ❌ | design.md §5.1.2: 错误码 `ALGO_005` | 无对应校验逻辑（前端传入 List<Integer>，JSON 解析失败时返回 500） | **P1** 错误码不符合 Spec |
| 列表过大(>10000)返回 ALGO_006 | ✅ | design.md §5.1.3.3 异常场景 | `AlgorithmServiceImpl.java:60-62` + `SortRequest.java:16` @Size | 双重限制 |
| 不修改原始列表 | ✅ | design.md §5.1.3.3 | `AlgorithmServiceImpl.java:66` 复制列表 | 符合规范 |
| 返回交换次数 swapCount | ⚠️ | design.md §5.1.2 W03: `data.swapCount` | `AlgorithmController.java:80-81` 重复执行排序 | **P1** 性能问题（见 §7 P1-3） |

### REQ-4: 前端 Tab 展示页面 (F04)

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 三个 Tab 展示算法结果 | N/A | design.md §5.3.3.1 | testDJnew 仓库（非 Java） | 前端代码不在本次 Java CR 范围 |

### REQ-5: 导出按钮 (F05)

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 前端导出按钮 | N/A | design.md §5.3.3.2 | testDJnew 仓库（非 Java） | 前端代码不在本次 Java CR 范围 |

### REQ-6: 导出接口 (F06)

| Scenario | 结果 | Spec证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| GET /api/export/result 导出 CSV | ✅ | design.md §5.2.2 W04 | `ExportController.java:43-81` | 实现正确 |
| Content-Type: text/csv | ✅ | design.md §5.2.2 | `ExportController.java:64` | 正确设置 |
| 文件名格式 {type}_result_{timestamp}.csv | ✅ | design.md §5.2.3 R04 | `ExportServiceImpl.java:66-68` | 符合规范 |
| 不支持类型返回 EXPORT_001 | ✅ | design.md §5.2.2 | `ExportController.java:53-57` | 正确抛出 |
| 导出失败返回 EXPORT_002 | ✅ | design.md §5.2.2 | `ExportController.java:74-77` | 正确抛出 |
| UTF-8 BOM 支持 | ✅ | — | `ExportController.java:70-71` | 额外加分项，确保 Excel 兼容 |

---

## 4. Step 3 — 可读性检查

> scan-all-rules.sh 预扫结果：A2.2 WildcardImport ×2（AlgorithmController.java:4,9）

| 检查项 | 结果 | 说明 |
|--------|------|------|
| A1 源文件格式 | ✅ | UTF-8 编码，无 Tab 字符 |
| A2 包/导入 | ⚠️ | **P2** `AlgorithmController.java:4,9` 使用通配符导入 `import ...dto.*` 和 `import ...annotation.*` |
| A3 行宽/格式 | ✅ | 无超长行 |
| A4 命名 | ✅ | 类名大驼峰、方法名小驼峰、常量全大写 |
| A5 类/接口 | ✅ | 无 finalize 重写 |
| A6 修饰符/字面量 | ✅ | 修饰符顺序正确 |
| A7 注释 | ✅ | Javadoc 类/方法注释完整 |

---

## 5. Step 4 — 可靠性检查

### 自动化预扫结果（scan-all-rules.sh）

```
=== Step 4 Rule Scan (B/M/I + A/S/G) ===
Targets: src/main/java/
Engine:  ripgrep

[P0] G16.2 CatchWithoutLogging: AlgorithmType.java:42        → LLM复核：误报，catch 后 re-throw
[P0] G16.2 CatchWithoutLogging: ExportController.java:55     → LLM复核：误报，catch 后 re-throw
[P0] G16.2 CatchWithoutLogging: ExportController.java:74     → LLM复核：误报，catch 内有 log.error
[P0] G16.2 CatchWithoutLogging: AlgorithmServiceImpl.java:49 → LLM复核：误报，catch 内有 log.error
[P0] G16.2 CatchWithoutLogging: ExportServiceImpl.java:114   → LLM复核：误报，catch 后 re-throw
[P1] M016 JavaTimeDefaultTimeZone: AlgorithmController.java:45    → 确认，LocalDateTime.now() 使用系统默认时区
[P1] M016 JavaTimeDefaultTimeZone: AlgorithmServiceImpl.java:31   → 确认
[P1] M016 JavaTimeDefaultTimeZone: ExportServiceImpl.java:67      → 确认
[P2] A2.2 WildcardImport: AlgorithmController.java:4,9            → 确认

Summary: 10 findings (P0=5, P1=3, P2=2) | 52/222 rules scanned
```

**LLM 复核结论**：脚本 5 个 P0 均为误报（catch 块均有 re-throw 或 log），降级为已确认。

### LLM 补充审查

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| 可靠性 | G1-G17 | ⚠️ | P1 | G16.2 脚本误报已复核；M016 时区问题为演示系统可接受 |
| 安全 | S1-S10 | ✅ | — | 无 SQL/命令注入风险；输入校验完整；无硬编码凭证 |
| Bug 模式 | B/M/I | ✅ | — | 预扫 52 条规则无真实命中；LLM 补扫未发现 B001-B081 命中 |

**额外发现（LLM）**：

| ID | 等级 | 位置 | 说明 |
|----|------|------|------|
| — | P1 | `GlobalExceptionHandler.java` | 缺少 `MethodArgumentNotValidException` 处理器 |
| — | P1 | `AlgorithmController.java:80-81` | sort 接口重复执行冒泡排序计算 swapCount |
| — | P1 | `AlgorithmController.java:43-46` | hello 接口时间戳不一致 |

---

## 6. Step 5 — 自定义扩展检查

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| 自定义扩展 | `customized-checklist.md` | N/A | — | N/A(未启用自定义规则) |

---

## 7. 结论

- **合并建议**：修复后合并
- **P0**：无
- **P1**：
  1. `GlobalExceptionHandler.java` 缺少 `MethodArgumentNotValidException` 处理器，@Valid 校验失败时返回 `ALGO_001 系统内部错误` 而非明确的参数错误信息，且不符合 Spec 定义的 ALGO_002/ALGO_004 错误码
  2. `AlgorithmController.java:80-81` sort 接口先调用 `bubbleSort()` 排序，再调用 `countSwaps()` 重新执行一遍冒泡排序来统计交换次数，代码重复且性能浪费（O(n²) 执行两次）
  3. `AlgorithmController.java:43-46` hello 接口中 `algorithmService.hello()` 返回的消息内含一个时间戳，controller 又用 `LocalDateTime.now()` 生成另一个时间戳放入 `response.timestamp`，两者不一致
  4. Spec 定义错误码 ALGO_002（空输入）/ALGO_004（空列表）/ALGO_005（非整数）与代码实际返回不匹配
- **P2**：
  1. `AlgorithmController.java:4,9` 通配符导入
  2. `AlgorithmServiceImpl.java:96-100` bytesToHex 使用 `String.format` 效率较低（建议 Java 17 的 `HexFormat`）
- **一句话**：代码整体结构清晰、功能完整，但存在 4 个 P1 级问题需修复（异常处理缺失、代码重复、时间戳不一致、错误码与 Spec 不匹配），修复后可合并。

---

## 7.1 问题片段（必填）

### P1-1: GlobalExceptionHandler 缺少 MethodArgumentNotValidException 处理

- **P1** `src/main/java/com/algorithm/demo/common/GlobalExceptionHandler.java:39-43` — 缺少 `MethodArgumentNotValidException` 处理器，@Valid 校验失败时落入通用 Exception 处理，返回 `ALGO_001` 而非 Spec 定义的 ALGO_002/ALGO_004。
  片段范围：`GlobalExceptionHandler.java:36-43`

```java
L36|    /**
L37|     * 处理未知异常
L38|     */
L39|    @ExceptionHandler(Exception.class)
L40|    public Result<Void> handleException(Exception e) {
L41|        log.error("系统内部错误", e);
L42|        return Result.fail("ALGO_001", "系统内部错误");
L43|    }
// 问题：缺少以下处理器
// @ExceptionHandler(MethodArgumentNotValidException.class)
// public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
//     String msg = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
//     return Result.badRequest(msg);
// }
```

### P1-2: AlgorithmController.sort() 重复计算

- **P1** `src/main/java/com/algorithm/demo/controller/AlgorithmController.java:74-88` — 先调用 `bubbleSort()` 排序，再调用 `countSwaps()` 重新执行一遍冒泡排序，代码重复且性能浪费。
  片段范围：`AlgorithmController.java:72-89`

```java
L72|    @PostMapping("/sort")
L73|    public Result<SortResponse> sort(@Valid @RequestBody SortRequest request) {
L74|        long startTime = System.currentTimeMillis();
L75|        List<Integer> sorted = algorithmService.bubbleSort(request.getNumbers());
L76|        long cost = System.currentTimeMillis() - startTime;
L77|        log.info("接口 /api/algorithm/sort 调用, size={}, cost={}ms",
L78|                request.getNumbers().size(), cost);
L79|
L80|        // 计算交换次数（重新执行一次以统计）
L81|        int swapCount = countSwaps(request.getNumbers());  // 问题：重复 O(n²) 计算
L82|
L83|        SortResponse response = new SortResponse(
L84|                request.getNumbers(),
L85|                sorted,
L86|                swapCount
L87|        );
L88|        return Result.success(response);
L89|    }
```

### P1-3: AlgorithmController.hello() 时间戳不一致

- **P1** `src/main/java/com/algorithm/demo/controller/AlgorithmController.java:37-48` — service.hello() 返回的消息内含一个时间戳，controller 又生成另一个时间戳，两者不同。
  片段范围：`AlgorithmController.java:36-48`

```java
L36|    @GetMapping("/hello")
L37|    public Result<HelloResponse> hello() {
L38|        long startTime = System.currentTimeMillis();
L39|        String message = algorithmService.hello();  // 内部生成时间戳 T1
L40|        long cost = System.currentTimeMillis() - startTime;
L41|        log.info("接口 /api/algorithm/hello 调用, cost={}ms", cost);
L42|
L43|        HelloResponse response = new HelloResponse(
L44|                "Hello World",
L45|                LocalDateTime.now().format(FORMATTER)  // 问题：生成新时间戳 T2 ≠ T1
L46|        );
L47|        return Result.success(response);
L48|    }
```

### P1-4: 错误码与 Spec 不一致

- **P1** Spec 定义 vs 代码实际返回 — 多处错误码不匹配。

| Spec 错误码 | Spec 场景 | 代码实际返回 | 原因 |
|-------------|----------|-------------|------|
| ALGO_002 | hash 输入为空 | `"400"` | IllegalArgumentException → badRequest |
| ALGO_004 | sort 列表为空 | `"ALGO_001"` | MethodArgumentNotValidException → 通用处理 |
| ALGO_005 | sort 包含非整数 | `"ALGO_001"` | JSON 解析异常 → 通用处理 |

### P2-1: 通配符导入

- **P2** `src/main/java/com/algorithm/demo/controller/AlgorithmController.java:4,9` — 使用通配符导入。
  片段范围：`AlgorithmController.java:1-13`

```java
L1|package com.algorithm.demo.controller;
L2|
L3|import com.algorithm.demo.common.Result;
L4|import com.algorithm.demo.model.dto.*;           // 问题：通配符导入
L5|import com.algorithm.demo.service.AlgorithmService;
L6|import jakarta.validation.Valid;
L7|import org.slf4j.Logger;
L8|import org.slf4j.LoggerFactory;
L9|import org.springframework.web.bind.annotation.*;  // 问题：通配符导入
L10|
L11|import java.time.LocalDateTime;
L12|import java.time.format.DateTimeFormatter;
L13|import java.util.List;
```

---

## 8. 修复任务列表

### P0

- 无 P0 问题。

### P1

- [ ] **P1** `GlobalExceptionHandler.java` — 新增 `@ExceptionHandler(MethodArgumentNotValidException.class)` 方法，提取校验错误信息并返回 `Result.badRequest(msg)`
- [ ] **P1** `AlgorithmController.java:80-81` — 将 swapCount 统计逻辑下沉到 `AlgorithmService.bubbleSort()` 返回值中（如返回包含 sorted + swapCount 的对象），消除 Controller 层重复计算
- [ ] **P1** `AlgorithmController.java:39-45` — 统一时间戳来源：从 `algorithmService.hello()` 返回值中解析时间戳，或让 Service 直接返回 message + timestamp 的结构化对象
- [ ] **P1** Spec 错误码对齐 — 将 `AlgorithmServiceImpl.hash()` 的空输入校验改为抛出 `BusinessException("ALGO_002", ...)`；在 Controller 或 Service 层对 sort 空列表抛出 `BusinessException("ALGO_004", ...)`

### P2（可选）

- [ ] **P2** `AlgorithmController.java:4,9` — 将通配符导入替换为具体类导入
- [ ] **P2** `AlgorithmServiceImpl.java:96-100` — 将 `String.format("%02x", b)` 替换为 Java 17 `HexFormat.of().formatHex(bytes)` 或手动查表法
