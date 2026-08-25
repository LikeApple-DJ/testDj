# 代码审查报告

> 审查阶段：loop-2（第二轮审查，含 BUG 修复验证）  
> 审查时间：2025-04-15  
> 审查范围：testDj-main（后端）+ testDJnew-main（前端）  
> 审查人：DTCoder (Code Review Skill)

---

## 一、审查概要

| 仓库 | 审查文件数 | Blockers | 建议项 | 备注 |
|------|-----------|---------|-------|------|
| testDj-main（后端） | 20 | 2 | 4 | 含 pom.xml、Java 源文件、配置文件 |
| testDJnew-main（前端） | 15 | 0 | 2 | 含 Vue 组件、JS 配置文件 |
| **合计** | **35** | **2** | **6** | — |

---

## 二、Blocker 修复验证

### 上一轮 B1: HashController 缺少输入参数校验 → ⚠️ 部分修复

**状态**: 已新增 `input == null \|\| input.isBlank()` 校验和 `ClassCastException` 捕获，但仍有遗漏。

**当前代码** (`src/main/java/com/example/demo/controller/HashController.java`):
- ✅ 已修复：`input` 空值校验
- ✅ 已修复：`ClassCastException` 捕获
- ❌ **仍有问题**：`AlgorithmService.hash()` 在传入 **不支持的算法**（如 `"INVALID"`）时抛出 `IllegalArgumentException`，但 Controller 仅捕获 `ClassCastException`，未捕获 `IllegalArgumentException`。导致客户端收到 HTTP 500。

### 上一轮 B2: SortController 缺少输入参数校验 → ✅ 已修复

**状态**: 当前代码已包含 `arrayObj == null || !(arrayObj instanceof List)` 前置校验和通用 `Exception` 捕获，逻辑正确。

### 上一轮 B3: StatsService 的 LocalDateTime.parse 无异常处理 → ✅ 已修复

**状态**: 当前代码已包含 `DateTimeParseException` 的 try-catch 处理，并抛出 `IllegalArgumentException` 供 Controller 层处理。

---

## 三、当前 Blockers（必须修复）

### Blocker 1：[testDj] HashController 未捕获算法参数异常

**文件**: `src/main/java/com/example/demo/controller/HashController.java`

**问题**: `HashController.hash()` 中，当用户传入不支持的算法类型（如 `"INVALID"`）时，`AlgorithmService.hash()` 抛出 `IllegalArgumentException("Unsupported algorithm: INVALID")`，但 Controller 的 catch 块仅捕获 `ClassCastException`，未处理 `IllegalArgumentException`。导致客户端收到 HTTP 500 而非正确的 400 Bad Request。

**影响**: 前端传入无效算法时得到 500 服务器错误，无法区分错误类型。

**修复建议**: 增加 `IllegalArgumentException` 的 catch 处理：

```java
} catch (ClassCastException e) {
    return ResponseEntity.badRequest()
            .body(Map.of("error", "Invalid input format", "status", 400));
} catch (IllegalArgumentException e) {
    return ResponseEntity.badRequest()
            .body(Map.of("error", e.getMessage(), "status", 400));
}
```

---

### Blocker 2：[testDj] ExportController Content-Disposition 头格式错误

**文件**: `src/main/java/com/example/demo/controller/ExportController.java`（第 30 行）

**问题**: 使用 `headers.setContentDispositionFormData("attachment", filename)` 设置下载响应头。该方法生成的是 `Content-Disposition: form-data; name="attachment"; filename="..."` 格式，这是 multipart/form-data 的专用格式，不适用于文件下载场景。正确的文件下载响应头应为 `Content-Disposition: attachment; filename="..."`。

**影响**: 前端 `ExportButton.vue` 从 `content-disposition` 头解析文件名时可能失败，导致导出的 CSV 文件名不正确。

**修复建议**: 改用 `ContentDisposition.attachment()` 构建正确的响应头：

```java
import org.springframework.http.ContentDisposition;

headers.setContentDisposition(
    ContentDisposition.attachment().filename(filename).build()
);
```

---

## 四、Non-Blocker 问题（建议修复）

### 建议 1：[testDj] PersonService 与数据库数据源重复

**文件**: `src/main/java/com/example/demo/service/PersonService.java`

**问题**: `PersonService` 维护了一份独立的静态 Mock 数据（`MOCK_PERSON_DB`），但 `UserInfo` 数据库表中已存储了相同的人员维度信息。`CallTrackingInterceptor` 调用 `PersonService.getPersonInfo()` 获取人员维度时，数据来自 Mock 而非数据库。若数据库与 Mock 数据不一致，会导致埋点记录与登录返回的用户信息不一致。

**建议**: `PersonService` 改为查询 `UserInfoRepository`，或将 Mock 数据与数据库初始化数据严格同步。

---

### 建议 2：[testDj] 统计图表首次加载无 Loading 状态

**文件**: `src/components/StatsChart.vue`

**问题**: 在 `onMounted` 时通过 `loadData()` 加载统计数据，加载过程中无 Loading 指示器。若网络延迟较大，用户可能感觉页面无响应。

**建议**: 增加 `v-loading` 加载指示器。

---

### 建议 3：[testDj] application.yml 中 ddl-auto=create 说明

**文件**: `src/main/resources/application.yml`

**问题**: `spring.jpa.hibernate.ddl-auto: create` 每次启动重建表结构，H2 内存数据库本身重启即丢失数据。Demo 阶段可接受，但建议添加注释说明。

**建议**: 添加注释说明当前为 Demo 配置。

---

### 建议 4：[testDj] 密码明文存储

**文件**: `src/main/resources/data.sql`

**问题**: 用户密码以明文形式存储（如 `admin123`、`pass123`），Demo 场景可接受但生产环境存在安全风险。

**建议**: Demo 场景可保留，生产环境应使用 BCrypt 等密码编码器。

---

### 建议 5：[testDJnew] PieChart 维度切换状态管理

**文件**: `src/components/PieChart.vue` / `src/components/StatsChart.vue`

**问题**: `PieChart` 内部 `dimension` 状态与父组件 `pieDim` 状态分别维护，可能存在不同步风险。

**建议**: 将 dimension 作为 prop 从父组件传入，确保单一数据源。

---

### 建议 6：[testDJnew] 前端 token 过期后无校验机制

**文件**: `src/store/auth.js`（第 6-7 行）

**问题**: 页面刷新时从 localStorage 恢复 token 和 user，但不会验证 token 是否仍然有效。

**建议**: 在 App.vue 的 onMounted 中增加一次 token 有效性验证。

---

## 五、跨仓接口契约对齐检查

| 对齐点 | 后端 (testDj) | 前端 (testDJnew) | 状态 |
|--------|--------------|-----------------|------|
| **基础路径** | `/api/*` | Axios baseURL: `/api` | ✅ 匹配 |
| **认证方式** | `Authorization: Bearer <token>` | 请求拦截器自动附加 Token | ✅ 匹配 |
| **登录接口** | `POST /api/auth/login` → `{token, user}` | `authApi.login()` | ✅ 匹配 |
| **Hello** | `GET /api/hello` → `{message, timestamp}` | `helloApi.call()` → `GET /hello` | ✅ 匹配 |
| **Hash** | `POST /api/hash` → `{input, algorithm, hash}` | `hashApi.call()` → `POST /hash` | ✅ 匹配 |
| **Sort** | `POST /api/sort/bubble` → `{original, sorted, duration}` | `sortApi.call()` → `POST /sort/bubble` | ✅ 匹配 |
| **Export** | `GET /api/export?tab=xxx` → CSV | `exportApi.download()` → `GET /export?tab=xxx` | ⚠️ Content-Disposition 头格式有 Blocker |
| **Stats** | `GET /api/stats/calls?dimension=xxx` → `{dimension, data}` | `statsApi.getCalls()` | ✅ 匹配 |
| **CORS** | 允许 `localhost:5173` | Vite 代理到 `localhost:8080` | ✅ 双重保障 |
| **错误格式** | `{error, status}` | 响应拦截器处理 401 | ✅ 匹配 |
| **数据格式** | JSON / CSV | Axios JSON / Blob | ✅ 匹配 |

---

## 六、汇总

### 6.1 代码变更清单

| 仓库 | 文件路径 | 行数 | 质量评估 |
|------|---------|------|---------|
| testDj | pom.xml | 71 | ✅ 依赖完整 |
| testDj | DemoApplication.java | 11 | ✅ 标准启动类 |
| testDj | model/CallRecord.java | 72 | ✅ 实体定义完整 |
| testDj | model/UserInfo.java | 59 | ✅ 实体定义完整 |
| testDj | repository/CallRecordRepository.java | 54 | ✅ 统计查询完善 |
| testDj | repository/UserInfoRepository.java | 13 | ✅ 基础查询 |
| testDj | service/AlgorithmService.java | 52 | ✅ 算法实现正确 |
| testDj | service/AuthService.java | 79 | ⚠️ JWT 密钥硬编码（Demo 可接受） |
| testDj | service/ExportService.java | 51 | ✅ CSV 导出正确 |
| testDj | service/PersonService.java | 55 | ⚠️ Mock 数据独立于数据库 |
| testDj | service/StatsService.java | 86 | ✅ 统计聚合正确 |
| testDj | controller/HelloController.java | 27 | ✅ 简洁正确 |
| testDj | controller/HashController.java | 40 | ❌ **Blocker 1**: 未捕获 IllegalArgumentException |
| testDj | controller/SortController.java | 47 | ✅ 已修复，校验完整 |
| testDj | controller/AuthController.java | 29 | ✅ 登录接口正确 |
| testDj | controller/ExportController.java | 35 | ❌ **Blocker 2**: Content-Disposition 头错误 |
| testDj | controller/StatsController.java | 35 | ✅ 统计接口正确 |
| testDj | config/AuthInterceptor.java | 48 | ✅ Token 校验正确 |
| testDj | config/WebConfig.java | 38 | ✅ 拦截器 + CORS 正确 |
| testDj | interceptor/CallTrackingInterceptor.java | 85 | ✅ 埋点逻辑完整 |
| testDj | application.yml | 26 | ✅ 配置完整 |
| testDj | data.sql | 66 | ✅ 初始数据覆盖多维度 |

| testDJnew | 全部 15 个文件 | ~780 | ✅ 整体质量良好 |

### 6.2 跨仓对齐点检查结论

| 检查项 | 结论 |
|--------|------|
| 接口路径一致性 | ✅ 全部对齐 |
| 请求/响应格式 | ✅ 全部对齐 |
| 认证 Token 传递 | ✅ 前后端一致 |
| 统计维度枚举 | ✅ 后端 `type`/`level`/`dept`/`time` 与前端一致 |
| 导出格式 | ⚠️ 格式一致但 Content-Disposition 头有 Bug（Blocker 2） |
| 错误处理 | ✅ 前端 401 拦截器与后端一致 |
| 人员维度数据 | ⚠️ PersonService Mock 与数据库独立（建议项） |

### 6.3 质量总结

**整体评价：代码质量良好，功能完整度较高。**

- 后端架构清晰，分层合理（Controller → Service → Repository）
- 前端组件化设计良好，ECharts 图表配置正确
- 跨仓接口契约基本对齐
- **2 个 Blocker 问题**：HashController 异常处理遗漏 + ExportController 响应头格式错误
- **6 个建议项**：主要涉及数据源一致性、用户体验优化

---

## 七、审查结论

| 维度 | 评分 | 说明 |
|------|------|------|
| 功能完整性 | ⭐⭐⭐⭐☆ | 三个接口 + 导出 + 埋点统计 + 登录认证，需求覆盖完整 |
| 代码质量 | ⭐⭐⭐⭐☆ | 代码规范、结构清晰，少量异常处理遗漏 |
| 跨仓对齐 | ⭐⭐⭐⭐⭐ | 接口契约全部对齐 |
| 安全性 | ⭐⭐⭐⭐☆ | Token 认证正确，密码明文存储（Demo 可接受） |
| 可维护性 | ⭐⭐⭐⭐☆ | 代码注释充分，分层清晰 |

**Blocker 数量: 2**（HashController 异常处理 + ExportController Content-Disposition 头）

**修复建议**: 修复 2 个 Blocker 后进入下一阶段。