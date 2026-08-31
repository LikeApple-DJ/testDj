# Code Review Checklist

> **Change** `待办事项新增（F01）` · **分支/Commit** `AI/task-DEV-966dcd0a-7905-11f1-9649-3b4281182f10-253ec98f-d966-4471-8cc3-752ad05e9a17` / `66c0f229` · **日期** `2026-08-31`

> **AI**：唯一进度源；状态仅用 `⬜` `✅` `❌` `⚠️` `N/A`。

---

## A. 执行队列（Step 1 产物 A）

| # | 文件（仓库相对路径） | 归属原因 | 状态 |
|---|---|---|---|
| 1 | `src/main/java/com/antdigital/todo/TodoApplication.java` | 启动类 | ✅ 已审 |
| 2 | `src/main/java/com/antdigital/todo/common/ApiResponse.java` | 通用出参 | ✅ 已审 |
| 3 | `src/main/java/com/antdigital/todo/common/BizException.java` | 业务异常 | ✅ 已审 |
| 4 | `src/main/java/com/antdigital/todo/common/ErrorCode.java` | 错误码 | ✅ 已审 |
| 5 | `src/main/java/com/antdigital/todo/common/GlobalExceptionHandler.java` | 全局异常 | ✅ 已审 |
| 6 | `src/main/java/com/antdigital/todo/common/UserContext.java` | 登录态上下文 | ✅ 已审 |
| 7 | `src/main/java/com/antdigital/todo/config/LoginInterceptor.java` | 登录拦截器 | ✅ 已审 |
| 8 | `src/main/java/com/antdigital/todo/config/WebMvcConfig.java` | WebMvc 配置 | ✅ 已审 |
| 9 | `src/main/java/com/antdigital/todo/controller/TodoController.java` | 控制器 W01 | ✅ 已审 |
| 10 | `src/main/java/com/antdigital/todo/dao/mapper/TodoMapper.java` | Mapper 接口 | ✅ 已审 |
| 11 | `src/main/java/com/antdigital/todo/enums/IsDeleted.java` | 枚举 | ✅ 已审 |
| 12 | `src/main/java/com/antdigital/todo/enums/TodoStatus.java` | 枚举 | ✅ 已审 |
| 13 | `src/main/java/com/antdigital/todo/model/dto/TodoCreateRequest.java` | 入参 DTO | ✅ 已审 |
| 14 | `src/main/java/com/antdigital/todo/model/dto/TodoCreateResponse.java` | 出参 DTO | ✅ 已审 |
| 15 | `src/main/java/com/antdigital/todo/model/entity/TodoDO.java` | DO | ✅ 已审 |
| 16 | `src/main/java/com/antdigital/todo/service/TodoService.java` | 服务接口 | ✅ 已审 |
| 17 | `src/main/java/com/antdigital/todo/service/impl/TodoServiceImpl.java` | 服务实现 | ⚠️ 已审有问题 |
| 18 | `src/main/resources/application.yml` | 主配置 | ⚠️ 已审有问题 |
| 19 | `src/main/resources/db/schema.sql` | DDL | ⚠️ 已审有问题 |
| 20 | `src/main/resources/mapper/TodoMapper.xml` | MyBatis 映射 | ✅ 已审 |
| 21 | `src/test/java/com/antdigital/todo/service/impl/TodoServiceImplTest.java` | 单元测试 | ⚠️ 已审有问题 |
| 22 | `src/test/resources/application.yml` | 测试配置 | ✅ 已审 |
| 23 | `pom.xml` | 依赖/构建 | ✅ 已审 |

> 跳过项：无。Java 文件 17 个，均纳入审查。

---

## B. 功能性检查（Step 2 产物 B）

| REQ | 功能点（spec 证据） | 关联文件 | 结论 |
|---|---|---|---|
| F01 | 新增待办事项（design §1/§4.1 W01：POST /api/todo/create） | TodoController.java:36-40 | ✅ |
| R01 | name 非空，长度1-128（§5.1.3.1） | TodoCreateRequest.java:20-21 / TodoServiceImpl.java:52-57 | ✅ |
| R02 | description 长度0-1024（§5.1.3.1） | TodoCreateRequest.java:25 / TodoServiceImpl.java:60-62 | ✅ |
| R03 | 同租户未删除范围内 name 唯一（§5.1.3.1） | TodoServiceImpl.java:65-68 / TodoMapper.xml:20-27 / schema.sql:17 | ⚠️ 预校验正确；唯一索引范围与 R03 不一致（见 §D-2） |
| R04 | 必须登录态，creator 取登录用户（§5.1.3.1） | LoginInterceptor.java:34-49 / TodoServiceImpl.java:45-49 | ✅ |
| R05 | status 默认0、is_deleted 默认0，服务端强制覆盖（§5.1.3.1） | TodoServiceImpl.java:75-77 | ✅ |
| ERR | 错误码 TODO_001~005/900（§5.1.2） | ErrorCode.java:14-29 / GlobalExceptionHandler.java | ✅ |
| OUT | 通用出参 {code,msg,data}（§5.1） | ApiResponse.java:42-55 | ✅ |
| CONC | 唯一索引兜底并发穿透（§5.1.3.1） | schema.sql:17 / TodoServiceImpl.java:83-88 | ✅ |

---

## C. 可读性检查（Step 3 产物 C，A1–A7）

| 项 | 检查内容 | 结论 |
|---|---|---|
| A1 源文件格式 | 编码 UTF-8、无 BOM | ✅ |
| A2 命名 | 类大驼峰、方法小驼峰、常量全大写 | ✅ |
| A3.4 行宽 | 超过 120 字符 | ❌ P2（LoginInterceptor.java:35,53；TodoServiceImplTest.java:118,135,177） |
| A4 注释 | 公共方法 Javadoc 完整 | ✅ |
| A5 方法/类规模 | 无超大类/方法 | ✅ |
| A6 魔法值 | 抽常量（NAME_MAX_LENGTH/DESCRIPTION_MAX_LENGTH） | ✅ |
| A7 POJO | toString 覆写、布尔属性不加 is 前缀 | ✅ |

---

## D. 可靠性检查（Step 4 产物 D）

### D.1 自动化预扫（scan-all-rules.sh，52/222 条）

```text
[P0] G16.2 — CatchWithoutLogging: TodoServiceImpl.java:85
[P1] M016 — JavaTimeDefaultTimeZone: TodoServiceImpl.java:78
[P2] A3.4 — LineWidthExceeded: LoginInterceptor.java:35
[P2] A3.4 — LineWidthExceeded: LoginInterceptor.java:53
[P2] A3.4 — LineWidthExceeded: TodoServiceImplTest.java:118
[P2] A3.4 — LineWidthExceeded: TodoServiceImplTest.java:135
[P2] A3.4 — LineWidthExceeded: TodoServiceImplTest.java:177
Summary: 7 findings (P0=1, P1=1, P2=5)
```

> **误报复核**：`G16.2 CatchWithoutLogging`（TodoServiceImpl.java:85）为**脚本误报**——catch 块第 86 行已 `logger.warn(...)` 记录日志，人工复核确认不构成问题，**不计入 blocker**。

### D.2 LLM 核对（G/S/B/M/I，脚本未覆盖项）

| ID | 等级 | 简述 | 定位 |
|---|---|---|---|
| M016 | P1 | `LocalDateTime.now()` 使用系统默认时区；且应用层显式 set gmtCreate/gmtModified 覆盖 DB `DEFAULT CURRENT_TIMESTAMP`，多实例/JVM-DB 时区不一致风险 | TodoServiceImpl.java:78-80 |
| G-idx | P1 | 唯一索引 `uk_biz_todo_tenant_name(tenant_id,name)` 未含 `is_deleted`，与 R03"未删除范围内唯一"不一致：软删后同名无法重建（本期删除未实现，属 latent 风险） | schema.sql:17 |
| G-toggle | P1 | 变更三板斧「可灰度/可应急」未实现：design §7.2/§7.3 要求接口开关灰度，代码无 feature toggle | TodoController.java / WebMvcConfig.java |
| B-val | P1 | 空字符串 name 同时触发 `@NotBlank` 与 `@Size(min=1)`；GlobalExceptionHandler 用 `getCode().contains("Size")` 启发式映射可能误返回 TODO_002（名称超长）而非 TODO_001 | TodoCreateRequest.java:21 / GlobalExceptionHandler.java:50-55 |
| S-trust | P1 | LoginInterceptor 直接信任 `X-Tenant-Id`/`X-User-Id` 请求头为租户/用户身份；design 假设上游网关注入，但代码未强制剥离客户端伪造头，存在租户越权隐患 | LoginInterceptor.java:36-48 |
| G-metrics | P2 | 监控埋点仅日志，无 QPS/耗时/错误率 metrics（§6.5/§7.1 要求） | TodoServiceImpl.java |
| S-secret | P2 | application.yml 明文 root/root 数据库口令（dev 环境，建议外部化） | application.yml:11-12 |
| G-dup | P2 | GlobalExceptionHandler 的 `DuplicateKeyException` 处理器对 create 路径冗余（Service 已捕获转换）；建议保留作全局兜底并注释说明 | GlobalExceptionHandler.java:72-76 |
| G-deser | P2 | 参数反序列化失败（HttpMessageNotReadableException）未单独处理，落入通用 Exception 返回 HTTP 500；建议 400 + TODO_900 | GlobalExceptionHandler.java:86-91 |

---

## E. 自定义扩展检查（Step 5 产物 E）

| 项 | 结论 |
|---|---|
| U1~U2 | N/A（未启用自定义规则，customized-checklist.md 仅含示例项 U1.1） |

---

## 收口

- 执行队列 `⬜ 待审` = 0（跳过项除外）。✅
- 审查范围文件数 23（含 Java 17、配置/资源/测试 6），已审 23。✅
- **blocker_count（P0，人工复核后）= 0**（G16.2 经复核确认为脚本误报）。
