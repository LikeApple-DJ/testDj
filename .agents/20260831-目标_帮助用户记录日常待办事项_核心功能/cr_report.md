# Code Review Report

> **Change** `待办事项新增（F01）` · **分支/Commit** `AI/task-DEV-966dcd0a-7905-11f1-9649-3b4281182f10-253ec98f-d966-4471-8cc3-752ad05e9a17` / `66c0f229` · **日期** `2026-08-31` · **审查者** AI

> **等级**：P0（阻塞）/ P1（推荐）/ P2（参考）。G/S 以 checklist 行内定义为准；Bug 模式映射 Blocker→P0、Major→P1、Info→P2。
> **驱动源**：`.agents/20260831-目标_帮助用户记录日常待办事项_核心功能/2026-08-31-cr-checklist.md`

---

## §1 审查范围

本次审查覆盖「编码实现」阶段提交（`66c0f229`）相对系分提交（`8b86088e`）的全部 26 个变更文件，其中 Java 文件 17 个（已全部逐文件完成 Step 2→3→4→5），配置/资源/测试文件 9 个。变更对应系分：`.agents/20260831-目标_帮助用户记录日常待办事项_核心功能/design.md`；编码报告：`.agents/system.changes/code.md`。

完整执行队列见 checklist §A，共 23 项（跳过 0，待审 0）。

---

## §2 功能性检查（Step 2）

以 design.md §1/§4.1/§5.1.2/§5.1.3.1 为 spec 来源，逐条核对：

| 功能点 | spec 证据 | 关联文件 | 结论 |
|---|---|---|---|
| F01 新增待办事项 | §1「核心功能：新增待办事项」；§4.1 W01 `POST /api/todo/create` | `TodoController.java:36-40` | ✅ 路径/方法/出参一致 |
| R01 name 非空 1-128 | §5.1.3.1 业务规则表「name 非空，长度1-128」；§5.1.2 入参表 | `TodoCreateRequest.java:20-21`、`TodoServiceImpl.java:52-57` | ✅ Controller @Valid + Service 兜底 |
| R02 description 0-1024 | §5.1.3.1「description 长度0-1024」 | `TodoCreateRequest.java:25`、`TodoServiceImpl.java:60-62` | ✅ |
| R03 同租户未删除范围内 name 唯一 | §5.1.3.1「同租户未删除范围内 name 唯一」 | `TodoServiceImpl.java:65-68`、`TodoMapper.xml:20-27`、`schema.sql:17` | ⚠️ 预校验过滤 `is_deleted=0` 与 R03 一致；但唯一索引范围不符（见 §4） |
| R04 登录态 + creator 取登录用户 | §5.1.3.1「必须登录态，creator 取登录用户」；§6.4.2.3 | `LoginInterceptor.java:34-49`、`TodoServiceImpl.java:45-49` | ✅ tenant_id/creator 由服务端注入 |
| R05 status/is_deleted 默认值覆盖 | §5.1.3.1「status 默认0、is_deleted 默认0，服务端强制覆盖入参」 | `TodoServiceImpl.java:75-77` | ✅ 强制 PENDING/NOT_DELETED |
| 错误码 TODO_001~005/900 | §5.1.2 错误码表 | `ErrorCode.java:14-29`、`GlobalExceptionHandler.java` | ✅ 枚举与映射齐全 |
| 通用出参 {code,msg,data} | §5.1 全局约定 | `ApiResponse.java:42-55` | ✅ |
| 并发兜底 | §5.1.3.1「唯一索引兜底」 | `schema.sql:17`、`TodoServiceImpl.java:83-88` | ✅ 预校验 + DuplicateKeyException 捕获 |

**功能性结论**：F01 最小闭环与 R01/R02/R04/R05 完全满足；R03 预校验逻辑正确，唯一索引范围与 R03 字面存在 latent 不一致（见 §4-2，非本期可触发，未判 P0）。

---

## §3 可读性检查（Step 3，A1–A7）

总体良好：命名规范、魔法值已抽常量（`NAME_MAX_LENGTH`/`DESCRIPTION_MAX_LENGTH`）、POJO 覆写 `toString()`、布尔属性不加 `is` 前缀（`is_deleted→deleted`）、Javadoc 完整、包路径与目录一致、MyBatis namespace 与接口全限定名匹配、SQL 全 `#{}` 参数化无 `SELECT *`。

命中项：

| 等级 | 项 | 定位 |
|---|---|---|
| P2 | A3.4 行宽超 120 字符 | `LoginInterceptor.java:35`、`LoginInterceptor.java:53`、`TodoServiceImplTest.java:118`、`TodoServiceImplTest.java:135`、`TodoServiceImplTest.java:177` |

---

## §4 可靠性检查（Step 4 — G/S/B/M/I）

### 4.1 自动化预扫结果（scan-all-rules.sh，52/222 条）

```text
[P0] G16.2 — CatchWithoutLogging: src/main/java/com/antdigital/todo/service/impl/TodoServiceImpl.java:85
[P1] M016 — JavaTimeDefaultTimeZone: src/main/java/com/antdigital/todo/service/impl/TodoServiceImpl.java:78
[P2] A3.4 — LineWidthExceeded: LoginInterceptor.java:35
[P2] A3.4 — LineWidthExceeded: LoginInterceptor.java:53
[P2] A3.4 — LineWidthExceeded: TodoServiceImplTest.java:118
[P2] A3.4 — LineWidthExceeded: TodoServiceImplTest.java:135
[P2] A3.4 — LineWidthExceeded: TodoServiceImplTest.java:177
Summary: 7 findings (P0=1, P1=1, P2=5)
```

**[误报复核]** `[P0] G16.2 CatchWithoutLogging`（`TodoServiceImpl.java:85`）：catch 块下一行（`:86`）已执行 `logger.warn("并发同名校验穿透, ...", tenantId, request.getName())`，满足"catch 必须记录日志"。脚本因正则匹配格式未识别，**人工复核确认为误报，不计入 blocker**。

### 4.2 LLM 核对项（脚本未覆盖）

- **[P1] M016 — JavaTime 默认时区**：`TodoServiceImpl.java:78-80` 使用 `LocalDateTime.now()` 取系统默认时区写入 `gmt_create`/`gmt_modified`。多实例部署或 JVM/DB 时区不一致时，时间戳可能偏差；且表已定义 `DEFAULT CURRENT_TIMESTAMP` / `ON UPDATE CURRENT_TIMESTAMP`，应用层显式覆盖反而引入不一致风险。**建议**：要么不在 INSERT 中写入时间字段、交给 DB 默认值；要么注入统一 `Clock`（如 `Clock.system(ZoneId.of("Asia/Shanghai"))`）。

- **[P1] 唯一索引范围与 R03 不一致（latent）**：`schema.sql:17` `uk_biz_todo_tenant_name(tenant_id, name)` 未含 `is_deleted`，而 R03 要求"同租户**未删除范围内** name 唯一"。预校验 SQL（`TodoMapper.xml:25` `AND is_deleted=0`）正确，但 DB 唯一约束会拦截含软删记录的同名重建。本期删除接口未实现，属 latent 风险；后续实现软删时需将唯一索引改为 `(tenant_id, name, is_deleted)` 或采用删除时间戳占位。**spec 证据**：design §5.1.1.1 索引定义本身即如此，属设计-实现共同缺陷。

- **[P1] 变更三板斧「可灰度/可应急」未实现**：design §7.2/§7.3 要求"接口开关灰度 + 功能开关应急"，§8 自查标记"通过"，但代码无任何 feature toggle / 配置开关控制 `/api/todo/create`。**建议**：引入 `@ConditionalOnProperty` 或配置开关在 Controller/Interceptor 层短路返回"功能不可用"。

- **[P1] 空名错误码误映射**：`TodoCreateRequest.java:21` `@Size(min=1, max=128)` 与 `@NotBlank` 对 name 重复约束；当 name 为空串 `""` 时二者同时触发，`GlobalExceptionHandler.java:50-55` 用 `fieldError.getCode().contains("Size")` 启发式判断会命中 `@Size` 分支返回 `TODO_002`（名称超 128）而非 `TODO_001`（名称为空）。**建议**：移除冗余 `min=1`（改为 `@Size(max=128)`），或按约束注解类型显式映射而非 `contains`。

- **[P1] 请求头身份信任边界（S）**：`LoginInterceptor.java:36-48` 直接将 `X-Tenant-Id`/`X-User-Id` 作为 tenant/creator 注入。design §6.4.2 假设"上游网关注入"，但代码未校验/剥离客户端伪造的同名头；若部署链路无强制网关，客户端可伪造 tenant_id 造成跨租户越权。**建议**：在网关层强制覆盖（strip + set）该两 header，或改用 Session/JWT 解析。

- **[P2] 监控埋点不完整**：仅 `logger.info/warn`（`TodoServiceImpl.java:86,90`），无 QPS/耗时/错误率 metrics 埋点（design §6.5/§7.1）。**建议**：接入 Micrometer 对 create 接口打 Timer/Counter。

- **[P2] 明文数据库口令**：`application.yml:11-12` 明文 `root/root`。dev 环境可接受，**建议**外部化（环境变量/Vault）。

- **[P2] DuplicateKeyException 处理冗余**：`GlobalExceptionHandler.java:72-76` 对 `DuplicateKeyException` 的全局处理器在 create 路径永不触发（`TodoServiceImpl.java:83-88` 已捕获并转换为 `BizException`）。可保留作全局兜底，建议加注释说明。

- **[P2] 反序列化异常 HTTP 状态**：`GlobalExceptionHandler.java:86-91` 通用 `Exception` 处理器返回 HTTP 500；JSON 反序列化失败（`HttpMessageNotReadableException`）落入此分支，更宜返回 400 + `TODO_900`。建议新增 `@ExceptionHandler(HttpMessageNotReadableException.class)`。

---

## §5 安全检查（S）

| 场景 | 结论 | 说明 |
|---|---|---|
| S1 SQL 注入 | ✅ | 全部 `#{}` 参数化（`TodoMapper.xml:23,24,32-33`），无 `${}` |
| S2 认证/授权 | ⚠️ P1 | 见 §4.2「请求头身份信任边界」 |
| S3 输入校验 | ✅ | `@Valid` + Service 兜底（R01/R02） |
| S4 密钥泄露 | ⚠️ P2 | 见 §4.2「明文口令」 |
| S5 日志脱敏 | ✅ | `TodoDO.toString` 不含 description；日志仅打印 tenantId/creator/id/name |

---

## §6 Bug 模式检查（B/M/I）

脚本覆盖 B(25)/M(6)/I(2)。本次命中：

| ID | 等级 | 简述 | 定位 |
|---|---|---|---|
| M016 | P1（Major→P1） | JavaTime 默认时区 | `TodoServiceImpl.java:78` |
| G16.2 | —（误报） | catch 已记日志，复核排除 | `TodoServiceImpl.java:85` |

其余 B*/M*/I* 条目本次变更未命中。

---

## §7 自定义扩展检查（Step 5）

`customized-checklist.md` 仅含示例项 `U1.1`，未启用项目私有规则。

**结论**：`N/A（未启用自定义规则）`。

---

## §8 修复任务列表

由 §3–§7 待修复项汇总（P0=0，故无阻塞项）：

- [ ] **[P1]** 修复 JavaTime 默认时区：`TodoServiceImpl.java:78-80`，移除应用层显式写入 gmt 时间字段（交 DB 默认值）或注入统一 `Clock`。
- [ ] **[P1]** 修正唯一索引范围与 R03 一致：`schema.sql:17`，后续实现软删时改为 `uk_biz_todo_tenant_name(tenant_id, name, is_deleted)`（本期删除未实现，可记 backlog）。
- [ ] **[P1]** 落地变更三板斧可灰度/可应急：为 `/api/todo/create` 增加功能开关（`@ConditionalOnProperty` 或拦截器短路）。
- [ ] **[P1]** 修复空名错误码误映射：`TodoCreateRequest.java:21` 移除冗余 `min=1`；`GlobalExceptionHandler.java:50-55` 改为按注解类型显式映射，避免 `contains("Size")` 启发式。
- [ ] **[P1]** 收敛请求头身份信任边界：在网关层强制 strip+set `X-Tenant-Id`/`X-User-Id`，或改用 Session/JWT 解析，杜绝客户端伪造 tenant_id。
- [ ] **[P2]** 补充监控埋点：`TodoServiceImpl` 接入 Micrometer 对 create 接口打 Timer/Counter（QPS/耗时/错误率）。
- [ ] **[P2]** 外部化数据库口令：`application.yml:11-12` 改环境变量/Vault。
- [ ] **[P2]** 拆分反序列化异常处理：`GlobalExceptionHandler` 新增 `@ExceptionHandler(HttpMessageNotReadableException.class)` 返回 400 + TODO_900。
- [ ] **[P2]** 为 `GlobalExceptionHandler.java:72` 的 DuplicateKeyException 处理器补充注释说明其全局兜底语义。
- [ ] **[P2]** 行宽超限：`LoginInterceptor.java:35,53`、`TodoServiceImplTest.java:118,135,177` 拆行至 ≤120 字符。

---

## 审查结论

- **P0 / Blocker（人工复核后）= 0**（脚本报 `G16.2` 经复核为误报）。
- **P1 = 5**，**P2 = 5**。
- F01 最小闭环功能正确，R01/R02/R04/R05 全部满足；R03 预校验正确、索引范围存 latent 不一致；可靠性/安全/变更三板斧存在若干推荐项，建议合并前修复 P1。
- 本次为静态审查（环境无 JDK/Maven，无法执行 `mvn compile/test`，与编码报告一致）；单测用例 10 个覆盖正常/边界/异常/并发/默认值，逻辑分支合理，待具备环境后执行 `mvn test -Dtest=TodoServiceImplTest` 复核。
