# 需求澄清文档（DIMA）

- 任务 ID：DEV-d885ee0d-78e2-11f1-8f3f-75954cb1c56f-02a853e4-5eda-4c6d-b754-838828eb0c80
- 阶段/节点：loop-1 / 需求澄清
- 采用技能：brainstorming
- 生成日期：2026-10-23

## 1. 需求原文

> 分别写三个接口 helloworld、哈希算法以及冒泡排序；前端新增一个页面，有三个 tab 分别展示不同的执行结果；新增导出按钮，后台提供导出接口，支持导出各个页面的展示结果；后端再做个埋点，获取调用次数和调用人，前端在当前页面上可视化出来一个报表查看调用情况（根据不同的维度：人员类型、人员层级、人员部门等），折线图以及饼图和柱状图不同展示形式。

## 2. 需求理解与功能拆解

### 2.1 后端（初步指派到 testDj 仓库）
1. `helloWorld` 接口：返回问候语。
2. 哈希算法接口：对入参做哈希计算并返回结果。
3. 冒泡排序接口：对入参数组排序并返回结果。
4. 导出接口：导出各页面展示结果（格式待确认）。
5. 埋点：记录接口调用次数与调用人。
6. 报表查询接口：按「人员类型 / 人员层级 / 人员部门」维度聚合，支持折线/饼图/柱状图所需数据。

### 2.2 前端（初步指派到 ranxitest 仓库）
1. 新增一个页面，含三个 tab：helloworld / 哈希算法 / 冒泡排序，各 tab 展示对应执行结果。
2. 导出按钮，触发表格/结果导出。
3. 当前页面内可视化埋点报表：折线图、饼图、柱状图，维度可切换（人员类型/层级/部门）。

## 3. 跨仓依赖与现状摘要

### [testDj]
- 路径：`/root/.agentix/.../worktree/testDj-AI-task-DEV-9d10e310-...-45c3`
- 现状：`pom.xml`（group `com.dt.example`，artifact `hello-world`）+ 单文件 `src/main/java/com/dt/example/hello/HelloWorld.java` + `HelloWorldTest.java`。
- 技术栈：Java 21 + Maven，仅 JUnit5/AssertJ 测试依赖；**无任何 Web 框架依赖**（无 Spring Boot）。
- 已有历史：hello world 示例，含代码评审记录 `.agents/cr/*`。

### [ranxitest]
- 路径：`/root/.agentix/.../worktree/ranxitest-AI-task-AUTO-root-b547e38a-...b54`
- 现状：仅 `README.md`（GitLab 模板）+ `.agents/cr/68c9593c.md`，**无源码、无前端工程**。
- 结论：基线为空仓库，需从零搭建前端工程。

## 4. 各仓改动规划（草案，待澄清后定稿）

### [testDj] 后端
- 引入 Web 能力（技术方案待确认，见 Q1）。
- 新增接口：`/api/helloWorld`、`/api/hash`、`/api/sort/bubble`、`/api/export`、埋点上报/查询 `/api/metrics/**`。
- 定义统一响应结构 `Result<T>`（code/message/data）。
- 埋点数据模型与存储（方案待确认，见 Q4）。

### [ranxitest] 前端
- 从零初始化前端工程（框架待确认，见 Q2）。
- 三 tab 结果展示页 + 导出按钮。
- 埋点报表区：折线图 / 饼图 / 柱状图组件，维度切换。

## 5. 仓间对齐点（接口契约）

| 契约项 | 待确认内容 |
| --- | --- |
| 接口输入输出结构 | Q3 |
| 埋点数据模型（调用人/人员类型/层级/部门） | Q4 |
| 导出文件格式 | Q5 |
| 报表聚合口径（计数/去重/时间段） | 随 Q4 一并确认 |
| 跨仓接口路径与响应格式 | 由后端统一 `Result<T>`，前端对齐 |

## 6. 澄清问题（已确认）

- **Q1 后端技术栈**：✅ 引入 Spring Boot 3（`spring-boot-starter-web`），以标准 REST 提供 HTTP 服务。
- **Q2 前端技术栈**：✅ Vue 3 + Vite（配 ECharts 绘制图表）。
- **Q3 接口契约**：✅ 哈希算法用 MD5；冒泡排序入参为整数数组；helloWorld 固定输出，不传 name 入参。
- **Q4 埋点数据来源**：✅ 调用人标识由请求传入，人员「类型/层级/部门」由后端人员字典维护；埋点进程内内存暂存。
- **Q5 导出格式**：✅ 同时支持 JSON、Excel(.xlsx)、CSV 三种格式（按请求参数/Header 指定）。

## 7. 确认结论（最终方案基线）

1. testDj = 后端，ranxitest = 前端。
2. 后端 Java 21 + Spring Boot 3；前端 Vue 3 + Vite + ECharts。
3. 哈希算法 MD5（十六进制输出）；冒泡排序入参为整数数组；helloWorld 固定无参。
4. 埋点调用人与组织维度由后端 `人员字典` 维护，接口通过请求参数/Header 识别调用人标识。
5. 埋点数据以进程内内存集合暂存（demo 级），预留后续持久化替换点。
6. 导出支持 JSON / Excel(.xlsx) / CSV 三种格式。
7. 图表形态：折线图=按时间趋势；饼图=各维度占比；柱状图=各维度人次/次数对比。

## 8. 待进入下阶段的设计要点

- [testDj] 在 `pom.xml` 引入 Spring Boot 3 parent + `spring-boot-starter-web`；新增 `Result<T>` 统一响应、四个业务/导出接口、埋点服务与人员字典、报表聚合接口。
- [ranxitest] 从零初始化 Vue3+Vite 工程；一页三 tab（helloworld/MD5/冒泡排序）+ 导出按钮（JSON/xlsx/CSV 选择）+ 埋点报表区（折线/饼/柱，维度：人员类型/层级/部门）。
- 仓间契约以 `Result<T>（code/message/data）` 统一，前端按后端接口对齐。