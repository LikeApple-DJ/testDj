# Code Review Checklist

> **Change** `待办事项新增（最小闭环）` · **分支/Commit** `AI/task-DEV-966dcd0a` / `c96fc5be` · **日期** `2026-09-01`
>
> **AI**：唯一进度源；状态仅用 `⬜` `✅` `❌` `⚠️` `N/A`。

---

## Step 1：文件列表与执行队列（产物 A）

| # | 文件路径 | 归属原因 | 状态 |
|---|----------|----------|------|
| 1 | src/main/java/com/aiwork/todo/TodoApplication.java | 启动类 | ✅ 已审 |
| 2 | src/main/java/com/aiwork/todo/common/constant/TodoConstants.java | 常量定义 | ✅ 已审 |
| 3 | src/main/java/com/aiwork/todo/common/exception/BizException.java | 异常载体 | ✅ 已审 |
| 4 | src/main/java/com/aiwork/todo/common/exception/GlobalExceptionHandler.java | 异常兜底 | ✅ 已审 |
| 5 | src/main/java/com/aiwork/todo/common/exception/TodoErrorCodeEnum.java | 错误码枚举 | ✅ 已审 |
| 6 | src/main/java/com/aiwork/todo/common/result/Result.java | 通用出参 | ✅ 已审 |
| 7 | src/main/java/com/aiwork/todo/controller/TodoItemController.java | 接口入口 O01 | ✅ 已审 |
| 8 | src/main/java/com/aiwork/todo/dao/mapper/TodoItemMapper.java | 数据访问 | ✅ 已审 |
| 9 | src/main/java/com/aiwork/todo/model/dto/CreateTodoRequest.java | 入参 DTO | ✅ 已审 |
| 10 | src/main/java/com/aiwork/todo/model/dto/CreateTodoResult.java | 出参 DTO | ✅ 已审 |
| 11 | src/main/java/com/aiwork/todo/model/entity/TodoItemDO.java | 数据对象 | ✅ 已审 |
| 12 | src/main/java/com/aiwork/todo/model/enums/TodoStatusEnum.java | 状态枚举 | ✅ 已审 |
| 13 | src/main/java/com/aiwork/todo/service/TodoItemService.java | 服务接口 S01 | ✅ 已审 |
| 14 | src/main/java/com/aiwork/todo/service/impl/TodoItemServiceImpl.java | 业务规则 R01-R05 | ⚠️ 已审有问题 |
| 15 | src/main/resources/mapper/TodoItemMapper.xml | SQL 映射 | ✅ 已审 |
| 16 | src/main/resources/sql/schema.sql | 表 DDL | ✅ 已审 |

> 待审 `⬜` 数：0（全部完成）。

---

## Step 2：功能性检查（产物 B）

> REQ 来源：`.agents/20260831-目标_帮助用户记录日常待办事项_核心功能/design.md` §5.2.2 / 需求功能清单。

| REQ | spec 证据 | 关联文件 | 结论 |
|-----|-----------|----------|------|
| F01 新增待办事项（POST /openapi/todo/items，落库返回 ID） | design §4.2 O01 / §5.2.3.1 时序图 | TodoItemController.java:44-51 / TodoItemServiceImpl.java:73-90 | ✅ |
| F02 入参校验（名称必填 1-100；描述选填 ≤1000） | design §5.2.2 入参表 / §5.2.3.1 R01-R03 | TodoItemServiceImpl.java:42-60 | ✅ |
| F03 创建结果返回（含生成的事项 ID） | design §5.2.2 出参表 / F03 | TodoItemServiceImpl.java:90 / Result.java:56-62 | ✅ |
| R01 名称非空 → TODO_001 | design §5.2.3.1 R01 | TodoItemServiceImpl.java:42-44 | ✅ |
| R02 名称 ≤100 → TODO_002 | design R02 | TodoItemServiceImpl.java:47-49 | ✅ |
| R03 描述 ≤1000 → TODO_003 | design R03 | TodoItemServiceImpl.java:58-60 | ✅ |
| R04 状态默认 PENDING | design R04 | TodoItemServiceImpl.java:67 | ✅ |
| R05 creator/tenant_id 缺失按默认值不阻断 | design R05 / A01-A02 | TodoItemServiceImpl.java:64,68 | ✅ |
| 错误码 TODO_001/002/003/999 | design §5.2.2 错误码表 | TodoErrorCodeEnum.java:14-23 | ✅ |
| 通用出参 result/msg/data | design §5.1 全局约定 | Result.java:14-78 | ✅ |

> 功能性不符（P0）：0 条。

---

## Step 3：可读性检查（产物 C）

> 对照 `references/readability-checklist.md` A1–A7。

| 编号 | 检查项 | 结论 | 备注 |
|------|--------|------|------|
| A1 | 源文件格式（编码/换行/末尾） | ✅ | UTF-8、LF、单顶层类 |
| A2 | 命名规范（类大驼峰/方法小驼峰/常量全大写/DO-DTO-Impl 后缀） | ✅ | — |
| A3 | 包名小写、接口与实现分离 | ✅ | service/impl 分离 |
| A4 | Javadoc/注释（类/方法 @author @date、枚举字段注释） | ✅ | — |
| A5 | 无魔法值，常量归类、private 构造 | ✅ | TodoConstants 私有构造抛异常 |
| A6 | 日志使用 SLF4J 占位符 | ✅ | logger.warn/error 均占位符 |
| A7 | 方法长度/圈复杂度 | ⚠️ P2 | TodoItemServiceImpl.createTodoItem 92 行单方法，catch(BizException){throw e;} 冗余可简化 |

---

## Step 4：可靠性 / 安全 / Bug 模式（产物 D）

### 自动化预扫结果（scan-all-rules.sh，52/222 条）

| 脚本命中 | 等级 | 复核结论 |
|----------|------|----------|
| G16.2 CatchWithoutLogging @ TodoItemServiceImpl.java:81 | P0 | ❌ 误报：该 catch 块含 `logger.error(...,e)` |
| G16.2 CatchWithoutLogging @ TodoItemServiceImpl.java:84 | P0 | ❌ 误报：该 catch 块含 `logger.error(...,e)` |
| G16.2 CatchWithoutLogging @ TodoItemServiceImpl.java:79 | P0 | ⚠️ 误报/可接受：`catch(BizException e){throw e;}` 为业务异常重抛，由 GlobalExceptionHandler.handleBizException 统一 warn 记录 |
| S1.1 MyBatisSqlInjection @ TodoItemController.java:31 | P0 | ❌ 误报：命中 `@Value("${todo.create.enabled...}")` Spring 占位符；实际 mapper XML 全用 `#{}` 参数化 |

### LLM 补扫（脚本未覆盖项）

| 等级 | ID | 简述 | 位置 |
|------|----|------|------|
| P2 | G — 事务边界 | 单条 INSERT 无需显式事务，auto-commit 满足最小闭环；spec §并发控制已声明无并发风险 | TodoItemServiceImpl.java:73 |
| P2 | G — 资源释放 | 无外部资源需手动释放（JDBC 由 Spring 管理） | — |
| P1 | G — 监控/日志 | `logger.warn("...creator missing...")` 在未实现登录拦截器时**每次创建**触发告警，产生噪音、淹没真实告警；建议加 `logger.isDebugEnabled` 或降级为 info，或接入拦截器后再 warn | TodoItemServiceImpl.java:71 |
| P2 | S — 输入校验 | 仅长度校验，未做内容净化（XSS）；本期无渲染页面，存储不直接回显，风险低；后续接入展示层需补充 | TodoItemServiceImpl.java:39-60 |
| P2 | S — 密钥泄露 | application.yml 明文硬编码 DB 账密 root/root；内部工具 dev 配置可接受，生产应外置（环境变量/配置中心） | application.yml:9-10 |
| P2 | B/M/I — 冗余 catch | `catch(BizException e){throw e;}` 可删除，BizException 会自然向上传播 | TodoItemServiceImpl.java:79-80 |

---

## Step 5：自定义扩展检查（产物 E）

> `references/customized-checklist.md` 为空/示例项 → `N/A(未启用自定义规则)`。

---

## 跨文件最终勾选

- Step 2 功能性：✅ 全部满足（0 P0）
- Step 3 可读性：1× P2（冗余 catch / 方法略长）
- Step 4 可靠性+安全+Bug：脚本 4×P0 全为误报；LLM 补扫 1×P1（告警噪音）+ 4×P2
- Step 5 自定义：N/A
