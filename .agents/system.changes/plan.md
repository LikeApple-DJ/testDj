# 实施计划 - 多仓库协同开发

## 1. 概述

基于需求澄清（dima.md）的决策，分两仓库实施：

| 仓库 | 角色 | 技术栈 |
|------|------|--------|
| testDj | 后端 | Java Spring Boot + Spring Data JPA + H2 |
| ranxitest | 前端 | Vue3 + Element Plus + ECharts + Axios |

---

## 2. 仓库分工与迭代计划

### 2.1 testDj（后端）- 实施顺序

| 迭代 | 内容 | 产出 |
|------|------|------|
| **S1** | Spring Boot 项目骨架 + 基础配置 | `pom.xml`, `application.yml`, 启动类 |
| **S2** | 三个业务接口 (hello/hash/bubble-sort) | Controller + Service |
| **S3** | 埋点数据库 + TrackingService | Entity, Repository, Service |
| **S4** | 导出接口 (CSV) | ExportController |
| **S5** | 统计查询接口 (多维度聚合) | StatisticsController |

### 2.2 ranxitest（前端）- 实施顺序

| 迭代 | 内容 | 产出 |
|------|------|------|
| **F1** | Vue3 项目骨架 + Element Plus 集成 | 项目初始化 |
| **F2** | 三个 Tab 页面 + 各接口调用 | Tab 组件 + API 调用 |
| **F3** | 导出按钮 + 下载功能 | Export 组件 |
| **F4** | 埋点统计报表 (ECharts) | 折线图/饼图/柱状图 |

---

## 3. API 接口契约（跨仓对齐点）

### 3.1 HelloWorld 接口

```
GET /api/hello
Response 200:
{
  "message": "Hello World!",
  "timestamp": "2026-08-25T12:00:00"
}
```

### 3.2 哈希算法接口

```
POST /api/hash
Content-Type: application/json
Request:
{
  "input": "待哈希字符串"
}
Response 200:
{
  "algorithm": "SHA-256",
  "input": "待哈希字符串",
  "output": "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"
}
```

### 3.3 冒泡排序接口

```
POST /api/bubble-sort
Content-Type: application/json
Request:
{
  "array": [3, 1, 4, 1, 5, 9]
}
Response 200:
{
  "original": [3, 1, 4, 1, 5, 9],
  "sorted": [1, 1, 3, 4, 5, 9],
  "steps": 15
}
```

### 3.4 导出接口

```
GET /api/export?type=hello|hash|bubble
Response 200:
Content-Type: text/csv
Content-Disposition: attachment; filename=export_<type>.csv

[CSV 内容]
```

### 3.5 埋点统计接口

```
POST /api/track
Content-Type: application/json
Request:
{
  "apiName": "hello",
  "caller": "张三",
  "department": "技术部",
  "level": "高级",
  "type": "内部"
}
Response 200:
{
  "code": 0,
  "message": "success"
}
```

```
GET /api/statistics?dimension=department|level|type&startTime=2026-01-01&endTime=2026-12-31
Response 200:
{
  "dimension": "department",
  "data": [
    {"name": "技术部", "count": 120},
    {"name": "产品部", "count": 80},
    {"name": "市场部", "count": 45}
  ]
}
```

```
GET /api/statistics/trend?startTime=2026-01-01&endTime=2026-12-31
Response 200:
{
  "data": [
    {"date": "2026-08-01", "count": 15},
    {"date": "2026-08-02", "count": 22},
    ...
  ]
}
```

---

## 4. testDj 后端 - 详细文件结构

```
testDj-main/
├── pom.xml                          # Maven 项目配置
├── src/main/java/com/testdj/
│   ├── Application.java             # 启动类
│   ├── config/
│   │   └── WebConfig.java           # CORS 配置
│   ├── controller/
│   │   ├── HelloController.java     # GET /api/hello
│   │   ├── HashController.java      # POST /api/hash
│   │   ├── BubbleSortController.java# POST /api/bubble-sort
│   │   ├── ExportController.java    # GET /api/export
│   │   └── StatisticsController.java# GET /api/statistics, POST /api/track
│   ├── service/
│   │   ├── HelloService.java        # HelloWorld 业务逻辑
│   │   ├── HashService.java         # SHA-256 哈希
│   │   ├── BubbleSortService.java   # 冒泡排序
│   │   ├── TrackingService.java     # 埋点记录服务
│   │   ├── ExportService.java       # CSV 导出服务
│   │   └── StatisticsService.java   # 统计查询服务
│   ├── entity/
│   │   └── ApiCallLog.java          # 调用记录实体
│   ├── repository/
│   │   └── ApiCallLogRepository.java# JPA Repository
│   └── dto/
│       ├── HashRequest.java         # 哈希请求 DTO
│       ├── BubbleSortRequest.java   # 冒泡排序请求 DTO
│       ├── TrackRequest.java        # 埋点请求 DTO
│       └── StatisticsResponse.java  # 统计响应 DTO
└── src/main/resources/
    ├── application.yml              # 应用配置
    └── schema.sql                   # 表结构初始化
```

---

## 5. 数据库设计

### 5.1 表结构：api_call_log

```sql
CREATE TABLE api_call_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    api_name VARCHAR(50) NOT NULL,
    caller VARCHAR(100) NOT NULL,
    department VARCHAR(100) NOT NULL,
    level VARCHAR(50) NOT NULL,
    type VARCHAR(50) NOT NULL,
    call_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 5.2 JPA Entity 映射

```java
@Entity
@Table(name = "api_call_log")
public class ApiCallLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String apiName;
    private String caller;
    private String department;
    private String level;
    private String type;
    private LocalDateTime callTime;
    // getters/setters
}
```

---

## 6. 跨仓对齐检查清单

| 检查项 | testDj (后端) | ranxitest (前端) | 状态 |
|--------|---------------|------------------|------|
| API 基础路径 | `/api/` | `/api/` 代理至 `localhost:8080` | 待对齐 |
| 请求/响应格式 | JSON (application/json) | Axios JSON 解析 | 待对齐 |
| 导出文件格式 | CSV (text/csv) | Blob 下载 (responseType: 'blob') | 待对齐 |
| 统计维度 | department/level/type | 前端传对应 dimension 参数 | 待对齐 |
| 时间格式 | ISO 8601 | `dayjs` 解析 | 待对齐 |
| 错误响应 | `{ "code": 0/1, "message": "..." }` | 统一 error handler | 待对齐 |

---

## 7. 风险与应对

| 风险 | 影响 | 应对措施 |
|------|------|----------|
| 跨域问题 | 前端无法调后端 | testDj 配置 CORS (`WebConfig.java`) |
| API 契约不一致 | 前后端联调失败 | 先定义 DTO > 后端实现 > 前端消费 |
| 图表数据格式不匹配 | 渲染异常 | 统一统计接口返回格式 |
| 时间约束 | 超时/超调用量 | 按 S1→S5 优先级依次实现，先核心后扩展 |

---

## 8. 实施步骤（执行顺序）

### Step 1: testDj S1 - Spring Boot 项目骨架
- 创建 `pom.xml`（Spring Boot 3.x + Spring Web + Spring Data JPA + H2）
- 创建 `Application.java` 启动类
- 创建 `application.yml`（端口 8080, H2 配置）
- 创建 `WebConfig.java`（CORS 允许跨域）

### Step 2: testDj S2 - 三个业务接口
- 实现 `HelloController` + `HelloService`
- 实现 `HashController` + `HashService`（SHA-256）
- 实现 `BubbleSortController` + `BubbleSortService`
- 创建对应的 DTO 类

### Step 3: testDj S3 - 埋点系统
- 创建 `ApiCallLog` 实体
- 创建 `ApiCallLogRepository`
- 实现 `TrackingService`（在三个业务接口中埋点调用）
- 实现 `POST /api/track` 接口

### Step 4: testDj S4 - 导出接口
- 实现 `ExportService`（生成 CSV）
- 实现 `ExportController`（根据 type 参数导出不同数据）

### Step 5: testDj S5 - 统计接口
- 实现 `StatisticsService`（按 dimension 聚合查询 + 时间趋势查询）
- 实现 `StatisticsController`

### Step 6: ranxitest F1-F4 - 前端实现
- Vue3 项目初始化（Element Plus + ECharts + Axios）
- 三个 Tab 组件 + API 调用
- 导出按钮 + 下载
- 统计报表 (折线图/饼图/柱状图)

---

## 9. 验证标准

| 维度 | 验收条件 |
|------|----------|
| helloworld | `GET /api/hello` 返回 `{"message":"Hello World!","timestamp":"..."}` |
| 哈希算法 | `POST /api/hash` 输入 → 返回 SHA-256 哈希值 |
| 冒泡排序 | `POST /api/bubble-sort` 返回排序结果 + 步骤数 |
| 导出 | `GET /api/export?type=hello` 下载 CSV 文件 |
| 埋点 | 每次调用接口自动记录日志到数据库 |
| 统计 | `GET /api/statistics?dimension=department` 返回聚合数据 |
| 前端 | 三个 Tab 展示 + 导出按钮 + 三张统计图表 |