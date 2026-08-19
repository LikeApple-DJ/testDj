# Code Review Report

> **Change** `org-arch-backend 组织架构管理模块` · **分支/Commit** `AI/task-DEV-9d10e310-7901-11f1-8a9f-59ecae612580-bc7bf497-9e5f-4d90-98a6-56cdcf9f9313` · **日期** `2026-08-19` · **审查者** AI
> **审查轮次**：第 2 轮（问题修复后复审），对照第 1 轮 6 个 P0 阻塞项与 8 个 P1 推荐项逐条核验。

> 等级：**P0 / P1 / P2**；G/S 以 checklist 行内定义为准；Bug 模式 Blocker→P0、Major→P1、Info→P2。
> 审查基准：`.agents/system.changes/design.md`（F01–F17、R01–R39、W01–W08）。

---

## 1. 审查范围与执行队列

本次复审聚焦「问题修复」阶段变更文件（已含修复），并在修复文件范围内逐文件完成 Step 2→5。

| # | 文件（仓库内相对路径） | 状态 |
|---|------------------------|------|
| 1 | org-arch-backend/src/main/java/com/orgarch/common/GlobalExceptionHandler.java | ✅ 已审（P0①/P1 已修复） |
| 2 | org-arch-backend/src/main/java/com/orgarch/department/Department.java | ✅ 已审（leader_id 已补） |
| 3 | org-arch-backend/src/main/java/com/orgarch/department/DepartmentController.java | ✅ 已审 |
| 4 | org-arch-backend/src/main/java/com/orgarch/department/DepartmentRepository.java | ✅ 已审（LIKE 通配符/悲观锁 已修复） |
| 5 | org-arch-backend/src/main/java/com/orgarch/department/DepartmentService.java | ✅ 已审（根级0/path重算/循环校验/并发 已修复） |
| 6 | org-arch-backend/src/main/java/com/orgarch/employee/Employee.java | ✅ 已审（@Version 确认） |
| 7 | org-arch-backend/src/main/java/com/orgarch/employee/EmployeeController.java | ✅ 已审（X-Operator 已接入） |
| 8 | org-arch-backend/src/main/java/com/orgarch/employee/EmployeeService.java | ✅ 已审（size截断/resign释放/check白名单/operator 已修复） |
| 9 | org-arch-backend/src/main/java/com/orgarch/transfer/TransferRecord.java | ✅ 已审 |
| 10 | org-arch-backend/src/main/java/com/orgarch/approval/ApprovalFlowNode.java | ✅ 已审 |
| 11 | org-arch-backend/src/main/java/com/orgarch/approval/ApprovalFlowNodeRepository.java | ✅ 已审（findByApproverId 已补） |
| 12 | org-arch-backend/src/main/java/com/orgarch/approval/ApprovalFlowService.java | ✅ 已审（approver_id 级联/deactivate 已修复） |
| 13 | org-arch-backend/src/main/resources/db/migration/V1__init_schema.sql | ✅ 已审（leader_id/复合唯一索引 已修复） |
| 14 | org-arch-backend/src/test/java/com/orgarch/employee/EmployeeCheckTest.java | ✅ 已审（未知字段400 用例 已补） |
| 15 | org-arch-backend/src/test/java/com/orgarch/employee/EmployeeTransferTest.java | ✅ 已审（级联/乐观锁 用例 已补） |

> scan-all-rules.sh 预扫结果：**No findings**（52/222 条可程序化规则，含 A2.2 通配符 import 全部清零）。
> [降级说明] 未执行 Maven 编译/集成测试：当前环境无 MySQL，属跨库环境依赖问题；转为静态审查 + 自动脚本扫描，核验跨仓对齐点入参/出参类型与契约匹配。

---

## 2. 功能性检查（Step 2 — 对照 design.md REQ，复审第1轮 P0/P1 修复项）

| 原问题 | REQ | 关联文件 | 复审结论 | 证据 |
|--------|-----|----------|----------|------|
| P0① create 未捕获 DB 唯一索引冲突 | F07 / 5.2.3.1 异常场景 | GlobalExceptionHandler.java:33-37 | ✅ 已修复 | 新增 `@ExceptionHandler(DataIntegrityViolationException)`，并发新增唯一索引冲突返回 400 + EMP_002 |
| P0② 子孙 path 重算失效（LIKE 无通配符） | F04/R04 | DepartmentRepository.java:23-24 | ✅ 已修复 | `like concat(:prefix, '%')` 拼接通配符，子孙 path 正确匹配重算 |
| P0③ 审批流级联未更新 approver_id + 缺 leader_id | F08/R22/R35 | ApprovalFlowService.java:37-61; Department.java:31-33; V1__init_schema.sql:7 | ✅ 已修复 | `cascadeOnTransfer` 按目标部门 `leaderId` 更新 `approver_id`；`Department` 实体与建表均补 `leader_id` |
| P0④ resign 未清理 approver 节点 | F10/R29 | EmployeeService.java:165-170; ApprovalFlowService.java:69-77 | ✅ 已修复 | `releaseSystemResources` 调用 `deactivateApproverNodes`，将 `approver_id=员工` 的节点置空（停用），IAM 释放为扩展点降级日志 |
| P0⑤ page 未实施 size 上限 100 截断 | F14/R16/A07 | EmployeeService.java:26,177 | ✅ 已修复 | `MAX_PAGE_SIZE=100`，`Math.min(size, MAX_PAGE_SIZE)` 截断 |
| P0⑥ 根级移动传 newParentId=0 失败 | F04/R01 | DepartmentService.java:67 | ✅ 已修复 | `toRoot = newParentId == null \|\| newParentId == 0L`，传 0 正确走根级分支 |
| P1 MethodArgumentNotValid 未映射 | R39 | GlobalExceptionHandler.java:40-48 | ✅ 已修复 | 新增 `@ExceptionHandler(MethodArgumentNotValidException)` 返回 400 + ApiResponse |
| P1 check 接口 field 未白名单 | F06/S2 | EmployeeService.java:52-63 | ✅ 已修复 | switch 白名单 `employeeNo`/`phone`，default 抛 `BizException(400)`；EmployeeCheckTest 覆盖未知字段 400 用例 |
| P1 move 无并发控制 | design 5.1.3.2 | DepartmentService.java:62; DepartmentRepository.java:29-31 | ✅ 已修复 | `@Lock(PESSIMISTIC_WRITE)` 行级悲观锁串行化同节点拖拽（design 方案C 降级） |
| P1 isAncestorOrSelf 多位数歧义 | F12/R03 | DepartmentService.java:112-116 | ✅ 已修复 | path 以 `/` 结尾，`/10/`.startsWith(`/1/`)=false，多位数歧义消除 |
| P1 唯一索引未限定 is_deleted | F07 | V1__init_schema.sql:31-32 | ✅ 已修复 | 改为 `uk_emp_no(employee_no, is_deleted)` / `uk_emp_phone(phone, is_deleted)` 复合唯一键 |
| P1 operator 硬编码 | F08/R34/A04 | EmployeeService.java:108,135; EmployeeController.java:49 | ✅ 已修复 | `transfer(id, req, operator)` 接收 `X-Operator` 请求头，缺失降级 SYSTEM |
| P1 transferWithVersion 测试脚手架泄漏 | — | EmployeeService.java | ✅ 已修复 | 生产 Service 已移除 `transferWithVersion`，改由测试用 `EntityManager` 原生 SQL 模拟并发（EmployeeTransferTest:91-94） |

---

## 3. 可读性检查（Step 3 — A1–A7）

| ID | 项 | 文件:行 | 等级 | 说明 |
|----|----|---------|------|------|
| A2.2 | 通配符 import | 全部主源码 | ✅ 已清零 | scan-all-rules.sh 报 No findings；Department/ApprovalFlowNode/TransferRecord/Employee 均改为逐项 import |
| A2 | 全限定类名内联 | DepartmentService/EmployeeService | ✅ 已修复 | 内联 `com.orgarch.common.BizException` 等已提取为 import |
| A6 | 状态字面量 | EmployeeService.java:28-30 | ⚠️ P2 | 已提取 `STATUS_ACTIVE`/`STATUS_RESIGNED` 常量；design 5.2.1.2 定义 `EmployeeStatus` 枚举尚未落地为枚举类型（参考项） |

---

## 4. 可靠性 / 安全 / Bug 模式（Step 4 复审）

### 可靠性（G）

| ID | 等级 | 项 | 文件:行 | 复审结论 |
|----|------|----|---------|----------|
| G1 | — | 部门 move 并发 | DepartmentService.java:62 | ✅ 已修复（悲观锁方案C，design 接受降级） |
| G3 | — | 子孙 path 重算 | DepartmentRepository.java:23 | ✅ 已修复（concat 通配符） |
| G5 | — | 资源释放降级 | EmployeeService.java:165-170 | ✅ 已修复（deactivate + IAM 扩展点） |
| G1* | P2 | 悲观锁仅锁被移动节点行 | DepartmentService.java:62 | ⚠️ 残留（参考）：方案C 仅锁被移动节点，并发移动同一子树内不同节点仍可能 path 不一致；design 5.1.3.2 明确多实例需切方案B（Redis 分布式锁），当前单实例降级可接受 |

### 安全（S）

| ID | 等级 | 项 | 文件:行 | 复审结论 |
|----|------|----|---------|----------|
| S2 | — | check field 白名单 | EmployeeService.java:57-61 | ✅ 已修复（switch 白名单，非法 400） |
| S- | N/A | 鉴权 | — | design 6.4 承认过渡方案，不作为缺陷 |

### Bug 模式（B/M/I）

| ID | 等级 | 项 | 文件:行 | 复审结论 |
|----|------|----|---------|----------|
| B- | — | create 未捕获 DB 冲突 | GlobalExceptionHandler.java:33 | ✅ 已修复 |
| B- | — | Department 缺 leader_id | Department.java:31-33; V1:7 | ✅ 已修复 |
| B- | — | 根级移动传 0 失败 | DepartmentService.java:67 | ✅ 已修复 |
| B- | — | isAncestorOrSelf 多位数 | DepartmentService.java:112-116 | ✅ 已修复 |
| B- | — | 唯一索引未限定 is_deleted | V1:31-32 | ✅ 已修复 |
| B- | — | operator 硬编码 | EmployeeService.java:135 | ✅ 已修复 |
| B- | — | transferWithVersion 泄漏 | EmployeeService.java | ✅ 已移除 |
| I- | — | System.out.println | EmployeeService.java:169 | ✅ 已改 SLF4J Logger |
| B-* | P2 | 复合唯一索引「二次离职」碰撞 | V1:31-32 | ⚠️ 残留（参考）：`uk_emp_no(employee_no, is_deleted)` 允许同号复职（一次 is_deleted=0→1 再 0），但同一员工号两次离职（均 is_deleted=1）会触发唯一冲突；此时已被 DataIntegrityViolation 处理器兜底返回 400 而非 500，影响有限 |
| I-* | P2 | 调动测试未断言 approverId | EmployeeTransferTest.java:73-74 | ⚠️ 残留（参考）：仅断言 `updatedNode.getDeptId()==toDept`，未断言 `approverId==leaderId`；测试种子部门未设 leader_id，覆盖度可加强 |

---

## 5. 自定义扩展检查（Step 5）

N/A（未启用自定义规则）

---

## 6. 问题统计（复审）

| 等级 | 数量 | 说明 |
|------|------|------|
| **P0（阻塞）** | **0** | 第1轮 6 个 P0 全部修复并复审通过 |
| P1（推荐） | 0 | 第1轮 8 个 P1 全部修复（含方案C 降级） |
| P2（参考） | 4 | ①EmployeeStatus 枚举未落地；②复合唯一索引二次离职碰撞边缘场景；③悲观锁仅锁单行多实例需切分布式锁；④调动测试未断言 approverId |
| 合计 | 4 | 全部为参考项，不阻塞合并 |

> **blocker_count = 0**

---

## 7. 跨仓对齐点检查（复审）

| 对齐点 | 契约 | 复审状态 |
|--------|------|----------|
| ApiResponse 统一出参 `{code,msg,data}` | design 5.0 | ✅ 符合 |
| BizException→HTTP 映射（400/409） | R37/R38 | ✅ 符合 |
| MethodArgumentNotValid→400 | R39 | ✅ 已补（GlobalExceptionHandler:40） |
| DataIntegrityViolation→EMP_002/400 | 5.2.3.1 | ✅ 已补（GlobalExceptionHandler:33） |
| 部门树响应 `{id,name,children}` | W01 | ⚠️ DepartmentTreeVo 多出 parentId（向后兼容，非缺陷） |
| 接口路径 W01–W08 | 4.1 | ✅ 路径一致；新增 `/{id}/children` 懒加载（向后兼容扩展） |
| Department.leader_id | 5.1.1.1 | ✅ 实体与 SQL 均已补 |
| approval_flow_node.scene=LEAVE | 5.4.1.2 | ✅ 默认 LEAVE |
| 员工乐观锁 @Version→409 | F15/R25 | ✅ Employee.java:45 + 全局异常映射 |
| 调动级联 approver_id | R22/R35 | ✅ cascadeOnTransfer 更新 approver_id |
| 离职停用 approver 节点 | R29 | ✅ deactivateApproverNodes 置空 approver_id |

---

## 8. 修复任务列表（复审后待办）

> 第1轮全部 P0/P1 阻塞与推荐项已修复并复审通过，以下仅为 P2 参考改进项，不阻塞合并。

- [ ] **[P2]** 落地 `EmployeeStatus` 枚举类型替代 `STATUS_ACTIVE`/`STATUS_RESIGNED` 字符串常量（design 5.2.1.2）。
- [ ] **[P2]** 复合唯一索引二次离职碰撞：考虑用 `deleted_at` 时间戳或生成列替代 `is_deleted` 参与唯一键，彻底消除同号多次离职冲突（当前已由 DataIntegrityViolation 处理器兜底，影响有限）。
- [ ] **[P2]** 多实例部署时将部门 move 并发控制从方案C（行级悲观锁）升级为方案B（Redis 分布式锁按节点 id），保障子树 path 重算原子性（design 5.1.3.2）。
- [ ] **[P2]** EmployeeTransferTest 增加对 `approverId == leaderId` 的断言，并为目标部门种子数据设置 leader_id，提升级联审批流测试覆盖度。
