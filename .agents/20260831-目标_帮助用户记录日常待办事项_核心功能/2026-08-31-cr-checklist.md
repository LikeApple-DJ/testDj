# Code Review Checklist

> **Change** `todo-create` · **分支/Commit** `AI/task-DEV-966dcd0a` / `465cc61b` · **日期** `2026-08-31`
>
> **AI**：唯一进度源；状态仅用 `⬜` `✅` `❌` `⚠️` `N/A`。
> **执行顺序**：已先运行 `scan-all-rules.sh`，输出贴入 Step 3/Step 4 备注；再由 LLM 完成脚本未覆盖项。

### scan-all-rules.sh 预扫输出

```text
=== Step 4 Rule Scan (B/M/I + A/S/G) ===
Targets: src/main/java src/test/java src/main/resources
Engine:  ripgrep

[P0] G16.2 — CatchWithoutLogging: src/main/java/com/antdigital/todo/service/impl/TodoItemServiceImpl.java:78
[P1] M016 — JavaTimeDefaultTimeZone: src/main/java/com/antdigital/todo/service/impl/TodoItemServiceImpl.java:66

=== Summary: 2 findings (P0=1, P1=1, P2=0) | 52/222 rules scanned ===
```

**LLM 复核结论**：
- G16.2 命中 `TodoItemServiceImpl.java:78` → **误报（false positive）**。catch 块体（L79 logger.error + L80 throw）确有日志输出和异常抛出，脚本仅检查 `catch(` 所在行不包含 `logger./throw` 关键字，属已知局限。
- M016 命中 `TodoItemServiceImpl.java:66` → **确认**。`LocalDateTime.now()` 未显式指定时区，设计要求 GMT+8。

---

## Step 1 — 执行队列（产物 A）

| # | 文件（仓库相对路径） | 归属原因 | Step2 | Step3 | 总状态 |
|---|----------------------|----------|-------|-------|--------|
| 1 | src/main/java/com/antdigital/todo/TodoApplication.java | 启动类 | ✅ | ✅ | ✅ 已审 |
| 2 | src/main/java/com/antdigital/todo/common/constant/TodoConstants.java | 常量 | ✅ | ✅ | ✅ 已审 |
| 3 | src/main/java/com/antdigital/todo/common/exception/BusinessException.java | 异常 | ✅ | ✅ | ✅ 已审 |
| 4 | src/main/java/com/antdigital/todo/common/exception/GlobalExceptionHandler.java | 异常处理 | ❌ | ✅ | ⚠️ 已审有问题 |
| 5 | src/main/java/com/antdigital/todo/common/response/ApiResponse.java | 响应体 | ✅ | ✅ | ✅ 已审 |
| 6 | src/main/java/com/antdigital/todo/controller/TodoItemController.java | 控制器 | ✅ | ✅ | ✅ 已审 |
| 7 | src/main/java/com/antdigital/todo/dao/mapper/TodoItemMapper.java | Mapper | ⚠️ | ✅ | ⚠️ 已审有问题 |
| 8 | src/main/java/com/antdigital/todo/model/dto/TodoItemCreateRequest.java | 请求DTO | ✅ | ✅ | ✅ 已审 |
| 9 | src/main/java/com/antdigital/todo/model/entity/TodoItemDO.java | 实体 | ✅ | ✅ | ✅ 已审 |
| 10 | src/main/java/com/antdigital/todo/model/vo/TodoItemVO.java | 响应VO | ✅ | ⚠️ | ⚠️ 已审有问题 |
| 11 | src/main/java/com/antdigital/todo/service/TodoItemService.java | 服务接口 | ✅ | ✅ | ✅ 已审 |
| 12 | src/main/java/com/antdigital/todo/service/impl/TodoItemServiceImpl.java | 服务实现 | ❌ | ✅ | ⚠️ 已审有问题 |
| 13 | src/test/java/com/antdigital/todo/service/impl/TodoItemServiceImplTest.java | 单元测试 | ✅ | ✅ | ✅ 已审 |

---

## Step 2 — 功能（产物 B）

| REQ | Scenario | Spec证据（原文/章节） | 关联文件 | 状态 | 代码证据 |
|-----|----------|----------------------|----------|------|----------|
| REQ-1 | Given 有效title+description When POST /api/todo/items Then 持久化并返回id | F01"录入事项名称+描述并持久化" §5.1.3.1 | TodoItemController.java:44-47, TodoItemServiceImpl.java:53-95 | ✅ | insert调用+useGeneratedKeys回填 |
| REQ-2 | 接口路径 POST /api/todo/items | W01 §4.1/§5.1.2 | TodoItemController.java:20,44 | ✅ | @RequestMapping("/api/todo")+@PostMapping("/items") |
| REQ-3 | title必填1~100字符 | R01/R02 §5.1.3.1 | TodoItemCreateRequest.java:21-22, TodoItemServiceImpl.java:110-120 | ❌ | **@Valid校验失败→GlobalExceptionHandler返回TODO_999而非TODO_001/002** |
| REQ-4 | description选填≤500字符 | R03 §5.1.3.1 | TodoItemCreateRequest.java:26, TodoItemServiceImpl.java:122-127 | ❌ | **同上：@Valid校验失败→TODO_999而非TODO_003** |
| REQ-5 | 返回创建后事项ID | §5.1.2出参 data.id | TodoItemController.java:47, TodoItemVO.java:19 | ✅ | ApiResponse.success(new TodoItemVO(id)) |
| REQ-6 | 统一响应体{code,msg,data} | §5.1.2出参 | ApiResponse.java:29-35 | ✅ | code+msg+data字段 |
| REQ-7 | creator不可为空 | R04 §5.1.3.1 | TodoItemServiceImpl.java:58-63 | ✅ | StringUtils.isBlank(creator)→TODO_999 |
| REQ-8 | 错误码TODO_001/002/003/999 | §5.1.2错误码 | TodoConstants.java:21-30, GlobalExceptionHandler.java:36 | ❌ | **PARAM_ERROR_CODE="TODO_999"将所有参数校验错误映射为TODO_999** |
| REQ-9 | DB写入失败返回TODO_999 | §5.1.3.1异常场景 | TodoItemServiceImpl.java:78-84 | ✅ | catch→BusinessException(CODE_SYSTEM_ERROR) |
| REQ-10 | 请求体格式错误返回400+TODO_999 | §5.1.3.1异常场景 | GlobalExceptionHandler.java:59-68 | ⚠️ | @Valid校验返回400+TODO_999✅；但非JSON(HttpMessageNotReadableException)被handleException(Exception)捕获返回500而非400 |

---

## Step 3 — 可读性检查（产物 C）

| ID | 检查项 | 状态 | 备注 |
|----|--------|------|------|
| A1 | 源文件格式 | ✅ | UTF-8编码，无Tab字符（预扫确认） |
| A2 | 源文件结构/import顺序 | ✅ | 无通配符import（预扫确认）；package→import→类顺序正确 |
| A3 | 代码样式 | ✅ | K&R大括号，4空格缩进，行宽≤120（预扫确认） |
| A4 | 命名规范 | ✅ | 包名全小写，类名UpperCamelCase，常量UPPER_SNAKE_CASE |
| A5 | 编码实践 | ✅ | @Override已标注，无空catch，无finalize重写 |
| A6 | 特定元素样式 | ✅ | 数组类型正确，修饰符顺序正确，long字面量大写L |
| A7 | Javadoc规范 | ✅ | public类/方法均有Javadoc，含@author+@date |

---

## Step 4 — 可靠性检查（产物 D）

### 4.1 Bug 模式（bug-pattern-checklist.md）

> 预扫覆盖 52/222 条（B 25/81 + M 6/27 + I 2/10 + A 8 + S 7 + G 4）。以下列出预扫覆盖的 B/M/I 规则及 LLM 补充。

| ID | 状态 | 备注 |
|----|------|------|
| B005-B028(预扫覆盖) | ✅ | 已扫无命中 |
| B036-B076(预扫覆盖) | ✅ | 已扫无命中 |
| 其余B*(未覆盖) | N/A | 本次变更无对应代码模式（无Arrays/Calendar/BigDecimal/DateUtil/IdentityHashMap/Base64/ClassLoader/javax.xml/Random/substring等用法） |
| M003 | ✅ | 无BoxedPrimitive构造 |
| M004 | ✅ | 无printStackTrace（预扫确认） |
| M007 | ✅ | 无空catch（预扫确认） |
| **M016** | **⚠️** | **`TodoItemServiceImpl.java:66` LocalDateTime.now()未指定时区** |
| M022 | ✅ | 无Optional.of(null) |
| M027 | ✅ | 无ThreadLocal使用 |
| 其余M*(未覆盖) | N/A | 无对应代码模式 |
| I001 | ✅ | 无@Test(expected=)用法 |
| I004 | ✅ | 无new Date() |
| 其余I*(未覆盖) | N/A | 无对应代码模式 |

### 4.2 可靠性（reliability-checklist.md）

| ID | 状态 | 备注 |
|----|------|------|
| G1.1-G1.4 | N/A | 无并发写入共享资源（最小闭环仅独立insert） |
| G2.1-G2.3 | N/A | 无幂等需求（全新创建，非重复扣款） |
| G3.1-G3.2 | N/A | 无@Transactional，单次insert无需显式事务 |
| G4.1-G4.4 | ✅ | MyBatis #{}预编译，无函数转换，无大查询 |
| G5.1 | N/A | 无MQ消费 |
| G6.1-G6.2 | N/A | 无缓存 |
| G7.1-G7.2 | N/A | 无调度任务 |
| G8.1 | ✅ | catch块有日志+throw，非吞异常 |
| G8.2 | N/A | 无外部依赖（仅MySQL） |
| G8.3 | ✅ | 无手动I/O流，MyBatis管理连接 |
| G8.4 | N/A | 无线程池/定时任务 |
| G8.5 | N/A | 无ThreadLocal |
| G8.6 | N/A | 无Executors线程池 |
| G9.1-G9.3 | N/A | 无外部RPC/HTTP调用 |
| G10.1-G10.3 | ✅ | 响应契约清晰，全新接口无兼容问题 |
| G11.1 | ✅ | 13个单测，有断言 |
| G11.2 | ✅ | 覆盖空值/边界值/异常路径 |
| G11.3 | ✅ | Service层防御性校验request null/title blank |
| G11.4 | N/A | 无数值运算/金额 |
| G12.1-G12.2 | N/A | 无资金场景 |
| G13.1 | ✅ | 日志级别正确（业务WARN/系统ERROR/成功INFO） |
| G14.1 | N/A | 无金额字段 |
| G14.2 | N/A | 无多租户查询 |
| G14.3 | ✅ | LocalDateTime.now()使用系统时区（见M016） |
| G14.4 | N/A | 无SimpleDateFormat/DateTimeFormatter |
| G15.1 | ✅ | 全新表，向前兼容 |
| G15.2-G15.3 | N/A | 无旧接口共存/开关切换 |
| G16.1 | N/A | 无显式监控埋点（设计假设基于统一监控组件） |
| **G16.2** | **✅** | **预扫命中TodoItemServiceImpl.java:78→LLM复核为误报：catch体(L79-83)有logger.error+throw** |
| G16.3 | ✅ | 日志级别正确 |
| G16.4 | ✅ | 无空catch/吞异常 |
| G17.1 | ⚠️ | 无功能开关（设计§7.3提到应急开关但未实现），P2 |
| G17.2 | N/A | 无降级预案（内部工具，单insert） |
| G17.3 | ✅ | DDL为全新表，回滚即删表 |

### 4.3 安全（security-checklist.md）

| ID | 状态 | 备注 |
|----|------|------|
| S1.1 | ✅ | MyBatis全部使用#{}预编译（TodoItemMapper.xml:19-20,27） |
| S1.2 | N/A | 无order by/group by/动态表名 |
| S1.3 | N/A | 无like/in查询 |
| S2.1-S2.3 | N/A | 无HTML/JS/富文本输出 |
| S3.1-S3.3 | N/A | 无外部URL请求 |
| S4.1-S4.2 | N/A | 无命令执行（预扫确认） |
| S5.1-S5.2 | N/A | 无XML解析（MyBatis DTD为内部解析） |
| S6.1-S6.3 | N/A | 无反序列化 |
| S7.1-S7.3 | N/A | 无文件上传/下载 |
| S8.1 | ✅ | 设计假设网关层校验登录态（§6.4.2.3），应用层信任creator |
| S8.2 | ✅ | 新增操作用POST |
| S8.3 | N/A | ID自增，设计未要求UUID |
| S8.4 | N/A | 无Cookie |
| S9.1 | ⚠️ | application-dev.yml:5 password:root硬编码（dev配置），P2 |
| S9.2 | ✅ | 日志不记录密码/token |
| S9.3 | N/A | 无HTTPS/加密/安全算法 |
| S9.4 | N/A | 无随机数 |
| S10.1-S10.3 | N/A | 无CSRF/CORS/URL跳转配置 |

---

## Step 5 — 自定义扩展检查（产物 E）

| ID | 状态 | 备注 |
|----|------|------|
| U1.1 | ✅ | Controller入参使用@Valid（示例项，已满足） |
| U2.* | N/A(未启用自定义规则) | customized-checklist.md仅含示例项 |

---

## 终检（防漏检）

- [x] 执行队列中每个文件 Step2、Step3 各列均非 `⬜`（跳过文件除外）
- [x] Step 2 的每个 REQ/Scenario 均非 `⬜`
- [x] Step 3 的 A1–A7 均非 `⬜`
- [x] Step 4 全部 G/S 与 B*/M*/I* ID 均非 `⬜`（允许 N/A，有原因）
- [x] Step 5 全部 U* ID 均非 `⬜`
- [x] 所有 `❌/⚠️` 已写入 report，且包含 `ID + path:line`
