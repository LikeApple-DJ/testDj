# Code Review Report

> **Change** `org-arch-backend 组织架构管理模块` · **分支/Commit** `AI/task-DEV-9d10e310-7901-11f1-8a9f-59ecae612580-bc7bf497-9e5f-4d90-98a6-56cdcf9f9313` · **日期** `2026-08-19` · **审查者** AI

> 等级：**P0 / P1 / P2**；G/S 以 checklist 行内定义为准；Bug 模式 Blocker→P0、Major→P1、Info→P2。
> 审查基准：`.agents/system.changes/design.md`（F01–F17、R01–R39、W01–W08）。

---

## 1. 审查范围与执行队列

| # | 文件（仓库内相对路径） | 状态 |
|---|------------------------|------|
| 1 | org-arch-backend/src/main/java/com/orgarch/common/ApiResponse.java | ✅ 已审 |
| 2 | org-arch-backend/src/main/java/com/orgarch/common/BizException.java | ✅ 已审 |
| 3 | org-arch-backend/src/main/java/com/orgarch/common/GlobalExceptionHandler.java | ⚠️ 已审有问题 |
| 4 | org-arch-backend/src/main/java/com/orgarch/department/Department.java | ⚠️ 已审有问题 |
| 5 | org-arch-backend/src/main/java/com/orgarch/department/DepartmentApplication.java | ✅ 已审 |
| 6 | org-arch-backend/src/main/java/com/orgarch/department/DepartmentController.java | ✅ 已审 |
| 7 | org-arch-backend/src/main/java/com/orgarch/department/DepartmentRepository.java | ⚠️ 已审有问题 |
| 8 | org-arch-backend/src/main/java/com/orgarch/department/DepartmentService.java | ⚠️ 已审有问题 |
| 9 | org-arch-backend/src/main/java/com/orgarch/department/DepartmentTreeVo.java | ✅ 已审 |
| 10 | org-arch-backend/src/main/java/com/orgarch/department/MoveRequest.java | ✅ 已审 |
| 11 | org-arch-backend/src/main/java/com/orgarch/employee/Employee.java | ✅ 已审 |
| 12 | org-arch-backend/src/main/java/com/orgarch/employee/EmployeeController.java | ✅ 已审 |
| 13 | org-arch-backend/src/main/java/com/orgarch/employee/EmployeeCreateRequest.java | ✅ 已审 |
| 14 | org-arch-backend/src/main/java/com/orgarch/employee/EmployeePageVo.java | ✅ 已审 |
| 15 | org-arch-backend/src/main/java/com/orgarch/employee/EmployeeRepository.java | ✅ 已审 |
| 16 | org-arch-backend/src/main/java/com/orgarch/employee/EmployeeService.java | ⚠️ 已审有问题 |
| 17 | org-arch-backend/src/main/java/com/orgarch/employee/EmployeeVo.java | ✅ 已审 |
| 18 | org-arch-backend/src/main/java/com/orgarch/employee/ResignRequest.java | ✅ 已审 |
| 19 | org-arch-backend/src/main/java/com/orgarch/employee/TransferRequest.java | ✅ 已审 |
| 20 | org-arch-backend/src/main/java/com/orgarch/transfer/TransferRecord.java | ✅ 已审 |
| 21 | org-arch-backend/src/main/java/com/orgarch/transfer/TransferRecordRepository.java | ✅ 已审 |
| 22 | org-arch-backend/src/main/java/com/orgarch/approval/ApprovalFlowNode.java | ✅ 已审 |
| 23 | org-arch-backend/src/main/java/com/orgarch/approval/ApprovalFlowNodeRepository.java | ✅ 已审 |
| 24 | org-arch-backend/src/main/java/com/orgarch/approval/ApprovalFlowService.java | ⚠️ 已审有问题 |
| 25 | org-arch-backend/src/main/resources/db/migration/V1__init_schema.sql | ⚠️ 已审有问题 |

> scan-all-rules.sh 预扫结果：7 条 P2（A2.2 通配符 import），覆盖 52/222 条可程序化规则；脚本未覆盖项由 LLM 逐文件补扫。

---

## 2. 功能性检查（Step 2 — 对照 design.md REQ）

| REQ | 设计要求（design 原文摘录） | 关联文件 | 结论 | 证据 |
|-----|------------------------------|----------|------|------|
| F03/R09-R10 | 组装时过滤 is_deleted=1，按 sort_order 升序 | DepartmentService.java:22-38 | ✅ | findAllActive() 已过滤 is_deleted=0 并按 sortOrder 排序 |
| F04/R01 | newParentId=0 或 null 表示根级 | DepartmentService.java:52-75 | ❌ P0 | 代码仅处理 null（:63），传 0 会走 findById(0) 抛"目标父部门不存在"；design 5.1.3.2 R01：「newParentId=0/null 表示根级」 |
| F04/R02 | newParentId 不能等于 id | DepartmentService.java:57-59 | ✅ | 已校验 |
| F12/R03 | newParentId 不能是 id 的子孙 | DepartmentService.java:70-72,95-98 | ⚠️ P1 | 逻辑存在但 startsWith 判定有多位数歧义（见 §4） |
| F04/R04 | 重算 path 含所有子孙 | DepartmentService.java:84-91 | ❌ P0 | findByPathPrefix(oldPath) 的 LIKE 无通配符退化为精确匹配，子孙 path 不被重算（见 §4） |
| F04/R05 | 全程事务 | DepartmentService.java:51 | ✅ | @Transactional |
| F13/R06 | 部门下有员工禁删，msg 含数量 | DepartmentService.java:106-109 | ✅ | countByDeptIdAndIsDeleted，msg 含数量 |
| F13/R07 | 存在子部门禁删 | DepartmentService.java:110-112 | ✅ | countByParentIdAndIsDeleted |
| F13/R08 | 逻辑删除 | DepartmentService.java:113-114 | ✅ | is_deleted=1 |
| F06/W04 | 实时校验 employeeNo/phone | EmployeeService.java:28-36 | ⚠️ P1 | 不支持字段抛 IllegalArgumentException 未被捕获 → 500（见 §4） |
| F07/R11-R12 | 工号/手机号唯一校验 | EmployeeService.java:46-51 | ✅ | 应用层 existsBy 校验 |
| F07 | DB 唯一索引冲突捕获返回 EMP_002 | EmployeeService.java:39-62, GlobalExceptionHandler.java | ❌ P0 | design 5.2.3.1 异常场景明确"捕获 DataIntegrityViolationException，返回 EMP_002"；create() 与全局异常处理均未捕获该异常 → 500 |
| F07/R13 | 校验部门合法性 | EmployeeService.java:41-43 | ✅ | findById + is_deleted=0 过滤 |
| F08/R19 | 校验员工存在且 ACTIVE | EmployeeService.java:69-73 | ✅ | findByIdAndIsDeleted + status 校验 |
| F08/R20 | 校验目标部门存在 | EmployeeService.java:66-68 | ✅ | |
| F08/R21 | 更新 dept_id/position | EmployeeService.java:77-79 | ✅ | |
| F08/R22 | 级联按目标部门 leader_id 更新 approver_id | ApprovalFlowService.java:18-24 | ❌ P0 | 仅 setDeptId(newDeptId)，未更新 approver_id；design R22/R35「按目标部门 leader_id 更新 approver_id」未实现 |
| F08/R23 | 写入 transfer_record | EmployeeService.java:83-91 | ✅ | |
| F08/R24 | 全程事务 | EmployeeService.java:64 | ✅ | @Transactional |
| F08/R25 | 乐观锁 version，冲突 409 | Employee.java:39-41, GlobalExceptionHandler.java:19-23 | ✅ | @Version + 全局异常映射 409 |
| F10/R26-R27 | 校验存在、非已离职 | EmployeeService.java:113-117 | ✅ | |
| F10/R28 | status=RESIGNED, is_deleted=1, resign_date | EmployeeService.java:118-121 | ✅ | |
| F10/R29 | 释放许可 + 清理 approver 节点 | EmployeeService.java:122-130 | ❌ P0 | releaseSystemResources 仅 System.out.println，未清理 approval_flow_node 中 approver_id=员工 的记录；design R29「清理/停用该员工作为 approver 的 approval_flow_node 记录」未实现 |
| F10/R31 | 乐观锁 | EmployeeService.java:121 | ✅ | @Version |
| F10/R32 | 全程事务 | EmployeeService.java:111 | ✅ | @Transactional |
| F14/R16 | size 超过 100 强制截断 | EmployeeService.java:133-142 | ❌ P0 | page() 直接 PageRequest.of(page-1, size)，无 size<=100 截断；design R16/A07 与 PRD 异常3 明确要求 |
| F14/R18 | deptId/status 过滤 | EmployeeRepository.java:21-30 | ✅ | queryPage 动态条件 |
| F15 | 并发调动乐观锁 409 | Employee.java + GlobalExceptionHandler | ✅ | @Version 生效 |

---

## 3. 可读性检查（Step 3 — A1–A7）

| ID | 项 | 文件:行 | 等级 | 说明 |
|----|----|---------|------|------|
| A2.2 | 通配符 import | ApprovalFlowNode.java:3; Department.java:3; DepartmentController.java:4; DepartmentService.java:6; Employee.java:3; EmployeeController.java:4; TransferRecord.java:3 | P2 | scan-all-rules.sh 已报 7 处 `import jakarta.persistence.*` / `import java.util.*` |
| A2 | 全限定类名代替 import | DepartmentService.java:13,16,44,55,69,71; EmployeeService.java:13-20,66,83,100,104,134-138 | P2 | 内联 `com.orgarch.common.BizException`、`org.springframework.data.domain.Pageable` 等全限定名，影响可读性，应提取为 import |
| A6 | 魔法字符串/枚举未落地 | EmployeeService.java:59,71,115,118; DepartmentService.java | P2 | "ACTIVE"/"RESIGNED" 散落多处，design 5.2.1.2 定义 EmployeeStatus 枚举但未落地为枚举类型 |

---

## 4. 可靠性 / 安全 / Bug 模式（Step 4）

### 可靠性（G）

| ID | 等级 | 项 | 文件:行 | 说明 |
|----|------|----|---------|------|
| G1 | P1 | 部门 move 无并发控制 | DepartmentService.java:52-92 | design 5.1.3.2 明确推荐分布式锁（方案B）/悲观锁（方案C），实现无任何锁，并发拖拽重叠子树将导致 path 不一致与子孙更新丢失 |
| G3 | P0 | 子孙 path 重算失效 | DepartmentService.java:84; DepartmentRepository.java:16-17 | `findByPathPrefix` 查询 `d.path like :prefix`，传入 oldPath（如 `/1/2/`）不含 `%`，LIKE 无通配符退化为精确等值匹配，仅匹配目标节点自身；子孙（path 形如 `/1/2/5/`）不被查出，path 不被重算 → 树结构 path 不一致 |
| G5 | P1 | 资源释放降级未实现 | EmployeeService.java:126-130 | design 5.2.3.4 规定离职需清理 approver 节点 + 预留 IAM 扩展点；releaseSystemResources 仅为 `System.out.println`，approval_flow_node 中 approver_id=该员工的记录未停用/清理 |

### 安全（S）

| ID | 等级 | 项 | 文件:行 | 说明 |
|----|------|----|---------|------|
| S2 | P1 | check 接口未校验 field 白名单 | EmployeeService.java:30-34 | 不支持字段抛 IllegalArgumentException，未被 GlobalExceptionHandler 捕获 → HTTP 500，应返回 400；且 field/value 未做长度/格式约束 |
| S- | N/A | 鉴权 | — | design 6.4 承认当前为过渡方案（无独立鉴权后端），本次审查不作为缺陷 |

### Bug 模式（B/M/I）

| ID | 等级 | 项 | 文件:行 | 说明 |
|----|------|----|---------|------|
| B- | P0 | create 未捕获 DB 唯一索引冲突 | EmployeeService.java:39-62; GlobalExceptionHandler.java | 并发新增相同工号/手机号时应用层校验双双通过，DB 唯一索引抛 DataIntegrityViolationException，全局异常处理无对应 @ExceptionHandler → HTTP 500；design 5.2.3.1 异常场景要求捕获并返回 EMP_002（400） |
| B- | P0 | Department 缺 leader_id 字段 | Department.java; V1__init_schema.sql:1-14 | design 5.1.1.1 明确 `leader_id bigint NULL`，实体与建表均缺失 → R22/R35 审批流级联（按目标部门 leader_id 更新 approver_id）无数据支撑，功能不可实现 |
| B- | P0 | 根级移动传 0 失败 | DepartmentService.java:63 | design R01「newParentId=0/null 表示根级」，代码仅 `if (newParentId == null)`，传 0（PRD/交互协议常用）走 else 分支 findById(0) 抛异常 |
| B- | P1 | isAncestorOrSelf 多位数歧义 | DepartmentService.java:95-98 | path 格式 `/1/` 与 `/10/`：`"/10/".startsWith("/1/")` 为 true，误判 dept10 为 dept1 的子孙，合法移动被拒 |
| B- | P1 | 参数校验异常未映射 | GlobalExceptionHandler.java | design R39 要求 MethodArgumentNotValid → HTTP 400（ApiResponse 体），全局处理无 @ExceptionHandler(MethodArgumentNotValidException) → 返回 Spring 默认错误体，响应格式不一致 |
| B- | P1 | employee 唯一索引未限定 is_deleted | V1__init_schema.sql:30-31 | `uk_emp_no`/`uk_emp_phone` 覆盖含已删除行，与逻辑删除语义不一致；且应用层校验 is_deleted=0 通过但 DB 索引冲突 → 前述 P0 的触发路径 |
| B- | P1 | operator 硬编码 | EmployeeService.java:90 | design R34/A04 规定 operator 从 X-Operator 头传入，代码硬编码 "SYSTEM" |
| B- | P1 | 测试脚手架泄漏生产源码 | EmployeeService.java:99-109 | transferWithVersion 手动构造 ObjectOptimisticLockingFailureException，无 Controller 暴露，仅测试使用，污染生产 Service |
| I- | P2 | System.out.println 替代日志 | EmployeeService.java:129 | 应使用 SLF4J Logger |
| I- | P2 | resign 查询方式不一致 | EmployeeService.java:113 | 使用 findById 而非 findByIdAndIsDeleted，与 transfer() 不一致 |

---

## 5. 自定义扩展检查（Step 5）

N/A（未启用自定义规则）

---

## 6. 问题统计

| 等级 | 数量 |
|------|------|
| **P0（阻塞）** | **6** |
| P1（推荐） | 8 |
| P2（参考） | 10 |
| 合计 | 24 |

> P0 明细：①create 未捕获 DataIntegrityViolationException；②子孙 path 重算失效（LIKE 无通配符）；③审批流级联未更新 approver_id + Department 缺 leader_id；④resign 未清理 approver 节点；⑤page 未实施 size 上限 100 截断；⑥根级移动传 newParentId=0 失败。

---

## 7. 跨仓对齐点检查

| 对齐点 | 契约 | 实现状态 |
|--------|------|----------|
| ApiResponse 统一出参 `{code,msg,data}` | design 5.0 | ✅ 符合 |
| BizException→HTTP 映射（400/409） | R37/R38 | ✅ 符合 |
| MethodArgumentNotValid→400 | R39 | ❌ 缺失（P1） |
| DataIntegrityViolation→EMP_002/400 | 5.2.3.1 异常场景 | ❌ 缺失（P0） |
| 部门树响应 `{id,name,children}` | W01 | ⚠️ DepartmentTreeVo 多出 parentId 字段（向后兼容，非缺陷） |
| 接口路径 W01–W08 | 4.1 | ✅ 路径一致；新增 /{id}/children 懒加载子树接口（向后兼容扩展） |
| Department.leader_id | 5.1.1.1 | ❌ 实体与 SQL 均缺失（P0） |
| approval_flow_node.scene=LEAVE | 5.4.1.2 | ✅ 默认 LEAVE |

---

## 8. 修复任务列表

- [ ] **[P0]** `EmployeeService.create()` / `GlobalExceptionHandler`：新增 `@ExceptionHandler(DataIntegrityViolationException)`，并发新增唯一索引冲突时返回 400 + EMP_002 错误码与提示（design 5.2.3.1 异常场景）。
- [ ] **[P0]** `DepartmentRepository.findByPathPrefix`：修正 LIKE 查询，调用处传入 `oldPath + "%"` 或查询改为 `like concat(:prefix, '%')`，确保子孙 path 正确重算（R04）。
- [ ] **[P0]** `ApprovalFlowService.cascadeOnTransfer`：按目标部门 `leader_id` 更新 `approval_flow_node.approver_id`（R22/R35）；同步在 `Department` 实体与 `V1__init_schema.sql` 补建 `leader_id` 字段。
- [ ] **[P0]** `EmployeeService.resign()`：实现 `releaseSystemResources` 清理/停用 `approval_flow_node` 中 `approver_id = 员工ID` 的记录（R29），预留 IAM 扩展点。
- [ ] **[P0]** `EmployeeService.page()`：增加 `size = Math.min(size, 100)` 截断逻辑（R16/A07），防止全量拉取。
- [ ] **[P0]** `DepartmentService.move()`：兼容 `newParentId == 0` 视为根级移动（R01）。
- [ ] **[P1]** `GlobalExceptionHandler`：新增 `@ExceptionHandler(MethodArgumentNotValidException)` 返回 400 + ApiResponse（R39）。
- [ ] **[P1]** `EmployeeService.check()`：对 field 做白名单校验，非法值返回 400 而非抛 IllegalArgumentException→500。
- [ ] **[P1]** `DepartmentService.move()`：引入并发控制（悲观锁 SELECT FOR UPDATE 或分布式锁），保障子树 path 重算原子性（design 5.1.3.2）。
- [ ] **[P1]** `DepartmentService.isAncestorOrSelf`：修复 path 前缀匹配多位数歧义（如改用 `parentPath.startsWith(targetPath)` 且 targetPath 以 `/` 结尾的分段判定，或使用 path 段集合比对）。
- [ ] **[P1]** `V1__init_schema.sql`：employee 唯一索引调整为 `(employee_no, is_deleted)` 复合唯一键或删除策略对齐，消除逻辑删除与 DB 唯一约束语义冲突。
- [ ] **[P1]** `EmployeeService.transfer()`：operator 改从请求头 `X-Operator` 获取（R34/A04），不再硬编码 "SYSTEM"。
- [ ] **[P1]** `EmployeeService.transferWithVersion`：移出生产 Service 或重命名为测试辅助，避免测试脚手架污染生产源码。
- [ ] **[P2]** 7 处通配符 import 改为逐项 import（A2.2）。
- [ ] **[P2]** DepartmentService/EmployeeService 内联全限定类名提取为 import。
- [ ] **[P2]** 引入 EmployeeStatus 枚举替代 "ACTIVE"/"RESIGNED" 字面量。
- [ ] **[P2]** `releaseSystemResources` 改用 SLF4J Logger 替代 System.out.println。
- [ ] **[P2]** `resign()` 查询统一为 `findByIdAndIsDeleted(id, 0)`，与 transfer() 保持一致。
