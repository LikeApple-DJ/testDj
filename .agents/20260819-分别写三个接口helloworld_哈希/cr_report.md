# Code Review Report

> **Change**：算法演示模块（HelloWorld / 哈希 / 冒泡排序 / 导出）  
> **分支/Commit**：`AI/task-DEV-9d10e310-7901-11f1-8a9f-59ecae612580-e864d0bb-6563-4dfe-a55f-511b07cddf40`  
> **日期**：2026-08-19  
> **审查者**：AI  

---

## 1. 审查范围

| 项 | 值 |
|----|-----|
| 生产环境 `.java` 文件数 | 26 |
| 测试 `.java` 文件数 | 4 |
| 变更行数 | 本次为新增模块，全部代码均为新增 |

| 类/接口 | 路径 | 角色 |
|---------|------|------|
| `AlgoDemoApplication` | `src/main/java/com/example/algodemo/AlgoDemoApplication.java` | 启动类 |
| `HelloWorldController` | `src/main/java/com/example/algodemo/api/controller/HelloWorldController.java` | HelloWorld 接口 |
| `HashController` | `src/main/java/com/example/algodemo/api/controller/HashController.java` | 哈希接口 |
| `SortController` | `src/main/java/com/example/algodemo/api/controller/SortController.java` | 冒泡排序接口 |
| `ExportController` | `src/main/java/com/example/algodemo/api/controller/ExportController.java` | 导出接口 |
| `HelloWorldServiceImpl` | `src/main/java/com/example/algodemo/service/impl/HelloWorldServiceImpl.java` | HelloWorld 服务实现 |
| `HashServiceImpl` | `src/main/java/com/example/algodemo/service/impl/HashServiceImpl.java` | 哈希服务实现 |
| `SortServiceImpl` | `src/main/java/com/example/algodemo/service/impl/SortServiceImpl.java` | 排序服务实现 |
| `ExportServiceImpl` | `src/main/java/com/example/algodemo/service/impl/ExportServiceImpl.java` | 导出服务实现 |
| `GlobalExceptionHandler` | `src/main/java/com/example/algodemo/common/exception/GlobalExceptionHandler.java` | 全局异常处理 |
| `index.html` | `src/main/resources/static/index.html` | 前端演示页面 |

---

## 2. 问题计数

| P0 | P1 | P2 |
|----|----|-----|
| 0 | 3 | 1 |

---

## 3. Step 2 — 功能（REQ）

### REQ-1：HelloWorld 接口

| Scenario | 结果 | Spec 证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 支持 `GET /api/hello` 与 `/openapi/hello` | ✅ | design.md §5.3.1 | `HelloWorldController.java:25` | 双路径映射正确 |
| `name` 缺省为 `"World"` | ✅ | design.md §5.3.1 | `HelloWorldServiceImpl.java:17-22` | null / 空白均回退 |
| 返回统一响应体 `{code, msg, data}` | ✅ | design.md §5. §通用出参 | `ApiResponse.java:25-27` | 符合 |

### REQ-2：哈希算法接口

| Scenario | 结果 | Spec 证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 支持 `POST /api/hash` 与 `/openapi/hash` | ✅ | design.md §5.3.2 | `HashController.java:23` | 双路径映射正确 |
| 支持 MD5 / SHA-256 | ✅ | design.md §5.2 / §5.3.2 | `HashAlgorithmEnum.java:13-14` | 枚举定义正确 |
| `content` 为空返回 `ALG_001` | ✅ | design.md §5.3.2 业务规则 | `HashServiceImpl.java:23-25` | 校验正确 |
| 不支持算法返回 `ALG_002` | ✅ | design.md §5.3.2 业务规则 | `HashAlgorithmEnum.java:32-43` | 校验正确 |

### REQ-3：冒泡排序接口

| Scenario | 结果 | Spec 证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 支持 `POST /api/sort/bubble` 与 `/openapi/sort/bubble` | ✅ | design.md §5.3.3 | `SortController.java:23` | 双路径映射正确 |
| 数组为空返回 `ALG_003` | ✅ | design.md §5.3.3 | `SortServiceImpl.java:23-25` | 校验正确 |
| 非法 `order` 返回 `ALG_001` | ✅ | design.md §5.3.3 | `SortServiceImpl.java:45-48` | 校验正确 |
| 默认升序、支持降序 | ✅ | design.md §5.3.3 | `SortServiceImpl.java:41-48` | 实现正确 |
| 数组长度上限 10000 | ✅ | design.md §6.3 | `SortServiceImpl.java:26-28` | 符合非功能性约束 |

### REQ-4：结果导出接口

| Scenario | 结果 | Spec 证据 | 代码证据 | 说明 |
|----------|------|----------|----------|------|
| 支持 `POST /api/export` 与 `/openapi/export` | ✅ | design.md §5.3.4 | `ExportController.java:28` | 双路径映射正确 |
| 支持类型 `hello / hash / bubbleSort` | ✅ | design.md §5.3.4 | `ExportTypeEnum.java:11-15` | 枚举定义正确 |
| 支持格式 CSV / JSON | ✅ | design.md §5.3.4 | `ExportFormatEnum.java:11-14` | 枚举定义正确 |
| 非法 `type` 返回 `ALG_001` | ✅ | design.md §5.3.4 | `ExportTypeEnum.java:27-37` | 校验正确 |
| 非法 `format` 返回 `ALG_004` | ✅ | design.md §5.3.4 | `ExportFormatEnum.java:16-27` | 校验正确 |
| 提供 `/api/export/download` 下载 | ✅ | design.md §5.3.4 | `ExportController.java:34-46` | 直接返回文件流 |
| 前端页面新增导出按钮 | ✅ | design.md §4 / 核心功能 | `index.html:52-69` | 已新增 |

---

## 4. Step 3 — 可读性检查

| 检查项 | 结果 | 说明 |
|--------|------|------|
| A1 源文件格式 | ✅ | 无 Tab、无乱码 |
| A2 导入规范 | ✅ | 无通配符导入 |
| A3 命名与注释 | ✅ | 类/方法/变量命名规范，注释完整 |
| A4 行宽 | ✅ | 无超过 120 列的代码行 |
| A5 关键字间距 | ✅ | 符合规范 |
| A6 修饰符顺序 | ✅ | 符合规范 |
| A7 常量/字面量 | ✅ | 无小写 `l` 等风险 |

---

## 5. Step 4 — 可靠性检查

### 5.1 可靠性（G）

| ID | 检查项 | 结果 | 等级 | 说明 |
|----|--------|------|------|------|
| G16.2 | 异常路径有日志输出 | ❌ | P1 | `HashServiceImpl.java:36-38` 与 `ExportServiceImpl.java:97-99` 捕获异常后仅转抛 `BusinessException`，未记录日志，可观测性不足。按清单实际等级为 **P1**。 |
| 其它 G 项 | 超时/重试/限流/事务/并发/边界 | ✅ | — | 本模块为纯内存计算、无状态、无持久化，相关项不适用；边界条件已处理 |

### 5.2 安全（S）

| ID | 检查项 | 结果 | 等级 | 说明 |
|----|--------|------|------|------|
| S9.3 | 使用安全算法 | ⚠️ | P1 | 设计支持 `MD5`（弱哈希算法）。`HashAlgorithmEnum.java:13` 与 `HashServiceImpl.java:28` 允许使用 MD5；建议文档标注“仅用于演示”，生产场景建议仅保留 SHA-256。 |
| S1 / S2 / S4 / S6 / S7 / S8 / S10 | SQL 注入、XSS、命令执行、反序列化、文件路径穿越、访问控制、CSRF/CORS | ✅ | — | 本模块无相关场景 |

### 5.3 Bug 模式（B/M/I）

> 已执行 `scan-all-rules.sh` 对 `src/main/java/com/example/algodemo`、`src/test/java/com/example/algodemo`、`pom.xml` 进行扫描，脚本命中 2 条 `G16.2`（脚本内部标为 P0，但与 `reliability-checklist.md` 核对后实际为 P1）。
> 除此之外，未再发现其它 Blocker/Major/Info 级别的 Bug 模式命中项。

---

## 6. Step 5 — 自定义扩展检查

| 域 | 参考 | 结果 | 等级 | 说明 |
|----|------|------|------|------|
| 自定义扩展 | `customized-checklist.md` U* | N/A | — | 当前仅有示例项，未启用项目自定义规则 |

---

## 7. 结论

- **合并建议**：`修复后合并`（P1 问题建议修复后再合入，无 P0 阻塞项）。
- **P0**：无。
- **P1**：
  1. `G16.2` `HashServiceImpl.java:36-38` — 捕获 `NoSuchAlgorithmException` 后未记录日志。
  2. `G16.2` `ExportServiceImpl.java:97-99` — 捕获 `JsonProcessingException` 后未记录日志。
  3. `S9.3` `HashAlgorithmEnum.java:13` / `HashServiceImpl.java:28` — 支持弱哈希算法 MD5，存在安全隐患，建议仅保留 SHA-256 或增加风险提示。
- **P2**：
  1. `ExportServiceImpl.java:111` — `hello` 类型 CSV 导出未对 `greeting` 内容进行 CSV 转义，若问候语含逗号/引号会生成格式错误文件。
- **一句话**：本次实现与系分设计基本一致，接口映射、错误码、导出功能均符合要求；主要风险点在于两处异常未打日志以及演示场景下仍开放了 MD5 弱哈希算法，建议修复 P1 项后再合并。

---

## 7.1 问题片段

### P1 G16.2 — `HashServiceImpl.java:36-38` 捕获异常未记录日志

片段范围：`src/main/java/com/example/algodemo/service/impl/HashServiceImpl.java:36-38`

```java
L36|        } catch (NoSuchAlgorithmException e) {
L37|            throw new BusinessException(AlgorithmErrorCode.ALG_002);
L38|        }
```

> 问题：捕获异常后直接转抛业务异常，未打印日志，排障时无法追踪原始异常。

### P1 G16.2 — `ExportServiceImpl.java:97-99` 捕获异常未记录日志

片段范围：`src/main/java/com/example/algodemo/service/impl/ExportServiceImpl.java:97-99`

```java
L97|                } catch (JsonProcessingException e) {
L98|                    throw new BusinessException(AlgorithmErrorCode.ALG_004);
L99|                }
```

> 问题：JSON 序列化异常被吞掉，未记录原始异常堆栈，不利于定位导出内容问题。

### P1 S9.3 — 支持 MD5 弱哈希算法

片段范围：`src/main/java/com/example/algodemo/common/constant/HashAlgorithmEnum.java:11-14`

```java
L11|public enum HashAlgorithmEnum {
L12|
L13|    MD5("MD5"),
L14|    SHA256("SHA-256");
```

> 问题：MD5 已被证明存在碰撞风险，属于弱哈希算法；演示场景建议保留 SHA-256 或明确标注仅用于教学演示。

### P2 — `hello` 类型 CSV 导出未转义

片段范围：`src/main/java/com/example/algodemo/service/impl/ExportServiceImpl.java:111`

```java
L111|            case HELLO -> "type,result\nhello," + map.getOrDefault("greeting", "") + "\n";
```

> 问题：未使用已有的 `escapeCsv` 方法对 `greeting` 进行转义，若结果包含逗号或双引号，CSV 格式将损坏。

---

## 8. 修复任务列表

### P1

- [ ] **P1** `src/main/java/com/example/algodemo/service/impl/HashServiceImpl.java:36-38` — 在 `catch (NoSuchAlgorithmException e)` 块中记录 `logger.warn` 或 `logger.error` 后再转抛 `BusinessException`。
- [ ] **P1** `src/main/java/com/example/algodemo/service/impl/ExportServiceImpl.java:97-99` — 在 `catch (JsonProcessingException e)` 块中记录日志后再转抛 `BusinessException`。
- [ ] **P1** `src/main/java/com/example/algodemo/common/constant/HashAlgorithmEnum.java:13` — 移除 MD5 枚举值，或增加注释/文档明确 MD5 仅用于演示、不推荐生产使用。

### P2

- [ ] **P2** `src/main/java/com/example/algodemo/service/impl/ExportServiceImpl.java:111` — 使用 `escapeCsv` 方法对 `greeting` 进行 CSV 转义，避免逗号/引号破坏格式。
