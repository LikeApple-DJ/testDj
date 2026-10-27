# 代码评审报告：三个接口 + 导出 + 埋点报表

> - 任务 ID：DEV-d885ee0d-78e2-11f1-8f3f-75954cb1c56f-02a853e4-5eda-4c6d-b754-838828eb0c80
> - 评审节点：loop-2 / 代码评审（只读，采用 code-review-skill）
> - 评审对象：testDj（后端 Spring Boot 3）+ ranxitest（前端 Vue 3 + Vite + ECharts）
> - 评审日期：2026-10-27
> - 评审方式：全文静态审查（未执行 mvn/npm 构建，避免在 worktree 生成构建产物）

## 0. 评审结论

**决策：🔄 Request Changes**

- 🔴 Blocker：**1** 个
- 🟡 Important：**6** 个
- 🟢 Nit：**4** 个

必须先修复 Blocker（后端存在两个 `public static void main` 入口，阻断 Spring Boot 打包与启动冒烟），方可进入验收。

---

## 1. 需求与现状通览

需求覆盖：三个后端接口（helloworld / MD5 哈希 / 冒泡排序）→ 前端单页三 tab 展示 → 导出（JSON/CSV/XLSX）→ 埋点（调用人 + 调用次数）→ 页面内多维可视化报表（人员类型/层级/部门 × 折线/饼/柱状）。

现状：两仓均已由「编码实现」节点产出完整代码。
- [testDj] Spring Boot 3.2.5 工程已落地：`Application`、`Result<T>`、`HashService`、`BubbleSortService`、`PersonDictionary`、`MetricsCollector`、`MetricsInterceptor`、`MetricsService`、`ExportService` 及三个 Controller + 全局异常处理 + 6 个测试类。
- [ranxitest] Vue3 + Vite + ECharts + axios 工程已落地：`ThreeTabs.vue`、`MetricsCharts.vue`、`api/index.js`、`vite.config.js`（`/api` 代理到 `localhost:8080`）等 8 个文件。

需求功能点（F01~F09）与系分/实施计划逐项静态对照均已有对应实现，无功能缺失。

---

## 2. 🔴 Blocker（必须修复）

### B1. 多入口 main class：Spring Boot 打包 / 启动将失败 [testDj]

- 位置：
  - `[testDj] src/main/java/com/dt/example/Application.java:12` → `public static void main`
  - `[testDj] src/main/java/com/dt/example/hello/HelloWorld.java:41` → `public static void main`
- 问题：遗留的 `hello/HelloWorld.java` 及其配套 `HelloWorldTest.java`（历史 hello-world 示例）未清理，导致编译产物中存在**两个 main 类**。`spring-boot-maven-plugin` 的 `repackage` 与 `spring-boot:run` 在检测到多个 main 入口时会失败（`Unable to find a single main class from the following candidates [...]`），使计划 Task 10 的 `mvn spring-boot:run` 冒烟联调无法通过，阻断最终验收。
- 建议（选一）：
  1. 删除遗留的 `src/main/java/com/dt/example/hello/HelloWorld.java` 与 `src/test/java/com/dt/example/hello/HelloWorldTest.java`（其功能已被 `BusinessController` 的 `/api/helloWorld` 取代）；并在 `pom.xml` 的 `spring-boot-maven-plugin` 中显式指定 `<mainClass>com.dt.example.Application</mainClass>` 兜底。
  2. 或至少移除 `HelloWorld.main(...)` 方法并保留为纯领域类。
- 契约影响：无跨仓影响；仅后端构建/启动被阻断。

---

## 3. 🟡 Important（应修复）

### I1. 导出接口参数 null 防御缺失：format 为 null 时 NPE → 500 [testDj]
- 位置：`[testDj] src/main/java/com/dt/example/export/ExportService.java:36`（`format.toLowerCase(Locale.ROOT)`）
- 问题：`ExportRequest.format` 为 null 时抛 NPE，被 `GlobalExceptionHandler` 的 `Exception` 分支转为 500，与设计 EXPORT_400 约定不符；`page`/`rows` 为 null 时部分分支也已兜底（rows），但 format 未兜底。
- 建议：在 `ExportService.export` 入口对 `format` 做 `null/blank` 校验，`page` 缺省时回退 `"export"`。

### I2. 500 分支直接透传异常消息，泄漏内部细节（与设计矛盾）[testDj]
- 位置：`[testDj] src/main/java/com/dt/example/web/GlobalExceptionHandler.java:23-24`
- 问题：`return Result.error(500, e.getMessage())` 会把 POI/MD5 等内部异常消息（含可为 null）直接返回客户端，与 design 6.1「不暴露堆栈细节」矛盾；对调用方价值低且不利安全审计。
- 建议：500 固定返回通用文案（如 `"internal error"`），原始异常仅记录服务端日志；400 分支可保留语义化消息。

### I3. 前端所有异步调用无错误处理 [ranxitest]
- 位置：`[ranxitest] src/views/ThreeTabs.vue` 中 `runHello/runHash/runBubble/doExport/loadMetrics/onMounted`
- 问题：后端返回 4xx/5xx（如 hash `text` 为空→400、网络超时）时无 `try/catch`，产生 unhandled rejection，UI 无任何提示；design 5.6.2 已声明「前端按需提示（后续补充 toast）」但实现未兑现。导出失败时用户无反馈。
- 建议：为每个调用封装统一错误提示（alert/toast），并处理 `blob` 下载失败分支。

### I4. 空请求体 / JSON 解析失败被归为 500 而非 400 [testDj]
- 位置：`BusinessController` 各 `@RequestBody` 入口依赖全局异常兜底
- 问题：`HttpMessageNotReadableException`（空 body、非法 JSON、类型不匹配）未单列处理，落入 `Exception` 分支返回 500，语义上应为客户端错误 400。
- 建议：在 `GlobalExceptionHandler` 增加对 `HttpMessageNotReadableException`/`MethodArgumentNotValidException` 的 400 分支。

### I5. 埋点计数语义未区分成功/失败 [testDj]
- 位置：`[testDj] src/main/java/com/dt/example/metrics/MetricsInterceptor.java:21-28`
- 问题：`afterCompletion` 无论请求成功或抛异常（`ex` 非 null）都调用 `record`，因此“调用次数”将失败请求也计入。与需求「获取调用次数」的常规口径（通常指成功调用）存在歧义。
- 建议：明确口径——若仅统计成功调用，应判断 `ex == null`（及响应状态码 2xx）后再记录；本项为口头径确认后改动的低风险点。

### I6. 仓库根目录遗留垃圾文件 [testDj]
- 位置：`[testDj] 07hubtasty`（内容 `hello world\n`，已提交入库）
- 问题：无扩展名、无业务含义的调试临时文件进入版本库，属仓库卫生问题，且易与真实产物混淆。
- 建议：从版本库删除。

---

## 4. 🟢 Nit（可选优化）

### N1. 折线图下「维度」选择失效，前后端语义不一致
- 前端 `line` 仍可选 personType/personLevel/personDepartment，但后端 `line` 固定按 `HH:mm` 时间桶聚合，返回 `dimension="time"`。功能正确但对用户有误导。
- 建议：前端在 `chartType='line'` 时禁用/隐藏维度下拉，或后端统一接受 time 维度值表达折线。

### N2. dimension × chartType 组合约束未前置校验
- `query("time","bar")` 会在 `dimensionValue` switch 抛异常（报 400 但语义不清晰）；`line` 分支则忽略传入 dimension。建议在 `MetricsService.query` 入口显式定义合法组合矩阵，给出确定的可读错误。

### N3. MetricsCollector 无容量/老化上限
- `CopyOnWriteArrayList` 只增不减，长时运行内存无限增长。demo 级可接受（design A08 已声明进程内暂存），生产化需切持久化 + 采样/滚动窗口。

### N4. 测试覆盖缺口
- 后端：错误路径（hash text 空、差参数、非法 format 的 HTTP 层行为）、`MetricsInterceptor` 埋点集成（`X-Caller-Id` → 事件落库）未测；`BusinessControllerTest` 用 `@Import` 拼装（新增了 `MetricsCollector`），未走完整上下文，埋点链路未被断言。
- 前端：无单元/组件测试。

---

## 5. 跨仓对齐点检查结论

| 对齐点 | testDj（后端） | ranxitest（前端） | 结论 |
|---|---|---|---|
| 统一响应 `Result<T>{code,message,data}` | 全部 Controller 返回该结构（导出除外） | 读 `data.data` | ✅ |
| 路径：`/api` 前缀 | `@RequestMapping("/api")` 各 Controller | axios `baseURL=/api` + Vite 代理 `:5173→:8080` | ✅ |
| 调用人 `X-Caller-Id` | 拦截器同名读取，缺省 anonymous | 三业务调用携带同名 header | ✅ |
| 报表契约 `MetricsQueryResponse{categories,series,total}` | 后端字段一致 | `data.series/categories` 渲染一致 | ✅ |
| 维度/图表枚举取值 | `personType/personLevel/personDepartment`；`line/pie/bar` | 下拉取值完全一致 | ✅（line 语义见 N1） |
| 导出契约 `{format,page,rows}`→字节流 | `ExportRequest` record | 前端组装同构 payload，blob 下载 | ✅（异常码见 I1） |
| 埋点范围仅 3 个业务接口 | 拦截器 path 限定 3 路径 | 导出/报表/字典不计入 | ✅ |

**跨仓结论**：对外契约（结构、字段、枚举、路径、请求头、代理）前后端一一对齐，无跨仓阻塞项；唯一阻断来自 [testDj] 单仓的构建入口问题（B1）。

---

## 6. 验证说明

- 本次为只读评审节点，**未执行** `mvn test` / `npm run build`（避免在 worktree 产生 `target/`、`node_modules`、`dist` 等构建产物污染基线），采用全文静态审查。
- B1 的「多 main class」事实由直接阅读两个入口文件确认：`HelloWorld.java:41` 与 `Application.java:12` 均含 `public static void main`。
- 剩余风险：前端依赖未安装（无 `node_modules`/`package-lock.json`），构建与运行正确性未经运行时验证；后续编码节点应补跑 `mvn package` 与 `npm run build` 复核。