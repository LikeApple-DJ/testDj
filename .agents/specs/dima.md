# 组织架构管理模块 — 需求规格说明书

> 文档编号: ORG-2025-001  
> 版本: v1.0  
> 阶段: 需求澄清 / 设计确认  
> 最后更新: 2025 年 6 月  

---

## 一、项目概述

### 1.1 背景

随着团队规模扩大，人员与部门关系变得复杂。现需开发一套**组织架构管理模块**，支持部门的树形结构搭建，以及员工在部门间的调动、离职等生命周期管理，并为其他业务系统（如审批、权限）提供准确的人员数据源。

### 1.2 目标

- 支持部门树形结构的创建、查询、拖拽调整
- 支持员工信息的全生命周期管理（新增、调动、离职）
- 为审批、权限等下游业务系统提供准确、实时的组织架构数据
- 支持多角色权限控制（超管/HR、部门主管）

### 1.3 核心角色与权限

| 角色 | 权限范围 |
|------|---------|
| **超管 / HR** | 拥有最高权限，可管理部门及所有人员信息 |
| **部门主管** | 仅可查看本部门及下属部门的人员；可编辑本部门人员部分信息 |

---

## 二、需求分析

### 2.1 需求 1：部门树形结构的加载与交互

#### 2.1.1 前端交互

- 左侧展示部门树，**默认展开第一级**，点击节点加载该部门下的人员列表
- 支持**懒加载**：点击节点展开按钮时，才去请求该节点的子部门
- 支持**拖拽调整部门层级**（如将“前端组”拖拽到“研发二部”下）

#### 2.1.2 后端处理

- 查询数据库，按 `parent_id` 组装树形结构返回
- 处理拖拽产生的父节点变更事务

#### 2.1.3 交互协议

**获取部门树：**

```http
GET /api/departments/tree
```

**响应体：**

```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "name": "研发部",
      "children": [
        {
          "id": 2,
          "name": "前端组",
          "children": []
        }
      ]
    }
  ]
}
```

**拖拽更新父节点：**

```http
PUT /api/departments/{id}/move
```

**请求体：**

```json
{
  "newParentId": 5
}
```

---

### 2.2 需求 2：员工新增（唯一性校验与防重）

#### 2.2.1 前端交互

- 右侧表单录入员工信息（姓名、工号、手机号、所属部门、职位）
- **实时校验**：输入工号/手机号后，光标移开时实时请求后端检查是否重复
- 若重复，输入框标红提示

#### 2.2.2 后端处理

- **数据库唯一索引保底**：`employee_no` 和 `phone` 字段需建立唯一索引
- **应用层二次校验**：接收请求时再次校验工号和手机号全局唯一
- **部门 ID 合法性校验**：校验所属部门 ID 是否合法存在

#### 2.2.3 交互协议

**实时校验接口：**

```http
GET /api/employees/check?field=employeeNo&value=10086
```

**响应体：**

```json
{
  "code": 200,
  "data": {
    "isExist": false
  }
}
```

**新增提交接口：**

```http
POST /api/employees
```

**请求体：**

```json
{
  "name": "张三",
  "employeeNo": "10086",
  "deptId": 2,
  "phone": "13800138000"
}
```

---

### 2.3 需求 3：人员调动（级联更新与快照）

#### 2.3.1 前端交互

- 在员工详情页点击“调动”，选择目标部门和新职位
- 弹出警告提示：“调动后，该员工相关的审批流/权限将发生变化，确认调动？”

#### 2.3.2 后端处理

- **核心逻辑**：更新员工的 `dept_id`
- **级联处理**：触发更新该员工相关的默认审批流节点（如原来前端组的请假审批人是前端主管，调动到后端组后需变更为后端主管）
- **记录留痕**：在调动记录表中写入一条历史（谁从哪调到哪）

#### 2.3.3 交互协议

```http
POST /api/employees/{id}/transfer
```

**请求体：**

```json
{
  "newDeptId": 3,
  "newPosition": "Java开发",
  "reason": "业务调整"
}
```

**响应体：**

```json
{
  "code": 200,
  "msg": "调动成功"
}
```

---

### 2.4 需求 4：员工离职（逻辑删除与状态隔离）

#### 2.4.1 前端交互

- 点击“办理离职”，选择离职日期
- 列表页支持筛选状态（在职/离职）
- 离职人员显示灰色标签，不可编辑

#### 2.4.2 后端处理

- **严禁物理删除**：将员工状态更新为离职（逻辑删除）
- **资源释放**：自动释放该员工占用的系统账号许可，清除系统登录权限
- **历史保留**：该员工的历史考勤、审批数据保留，但关联查询时需带上状态标识

#### 2.4.3 交互协议

```http
PUT /api/employees/{id}/resign
```

**请求体：**

```json
{
  "resignDate": "2023-11-01"
}
```

---

## 三、异常与边界交互场景

### 3.1 场景 1：树形结构的循环引用（死循环）

**场景描述：**

前端拖拽时，误将“研发部”拖成了“前端组”的子节点（父变子节点），导致树形结构死循环。

**处理方案：**

- 后端在 `move` 接口中必须校验 `newParentId` 不能是被移动节点的自身或其子孙节点
- 若违反，返回 **400 Bad Request**
- 前端将树还原到拖拽前状态

### 3.2 场景 2：部门下有人员时禁止删除

**场景描述：**

HR 尝试删除“前端组”，但组内还有 5 个人。

**处理方案：**

- 后端校验部门下是否存在员工，若存在，拒绝删除并返回错误信息
- 前端弹出提示：“该部门下存在 5 名员工，请先转移人员后再删除”

### 3.3 场景 3：大数据量渲染（前端性能）

**场景描述：**

公司有上万员工，全量加载人员列表会卡死浏览器。

**处理方案：**

- 后端必须支持分页 `page`/`size`
- 前端采用分页表格
- 切忌一次性拉取万条数据渲染

### 3.4 场景 4：并发调动冲突

**场景描述：**

HR-A 和 HR-B 同时对员工“张三”发起调动请求。

**处理方案：**

- 后端使用**乐观锁**（版本号 `version`）
- 先提交的成功
- 后提交的返回 **409 Conflict**
- 前端提示“该员工信息已被他人修改，请刷新重试”

---

## 四、数据模型设计

### 4.1 部门表（department）

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| `id` | BIGINT | PK, 自增 | 主键 |
| `name` | VARCHAR(100) | NOT NULL | 部门名称 |
| `parent_id` | BIGINT | FK → department.id | 父部门 ID，根节点为 NULL |
| `path` | VARCHAR(255) | INDEX | 路径，如 `1-2-5`，用于加速子孙节点查询 |
| `sort_order` | INT | DEFAULT 0 | 排序权重 |
| `created_at` | DATETIME | DEFAULT NOW() | 创建时间 |
| `updated_at` | DATETIME | DEFAULT NOW() | 更新时间 |

### 4.2 员工表（employee）

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| `id` | BIGINT | PK, 自增 | 主键 |
| `name` | VARCHAR(100) | NOT NULL | 姓名 |
| `employee_no` | VARCHAR(50) | UNIQUE, NOT NULL | 工号 |
| `phone` | VARCHAR(20) | UNIQUE, NOT NULL | 手机号 |
| `dept_id` | BIGINT | FK → department.id, INDEX | 所属部门 |
| `position` | VARCHAR(100) | | 职位 |
| `status` | TINYINT | DEFAULT 1 | 1=在职, 0=离职 |
| `version` | BIGINT | DEFAULT 0 | 乐观锁版本号 |
| `is_deleted` | TINYINT | DEFAULT 0 | 逻辑删除标识 |
| `created_at` | DATETIME | DEFAULT NOW() | 创建时间 |
| `updated_at` | DATETIME | DEFAULT NOW() | 更新时间 |

### 4.3 员工调动记录表（employee_transfer_record）

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| `id` | BIGINT | PK, 自增 | 主键 |
| `employee_id` | BIGINT | FK → employee.id, INDEX | 员工 ID |
| `old_dept_id` | BIGINT | FK → department.id | 原部门 |
| `new_dept_id` | BIGINT | FK → department.id | 目标部门 |
| `old_position` | VARCHAR(100) | | 原职位 |
| `new_position` | VARCHAR(100) | | 新职位 |
| `reason` | VARCHAR(500) | | 调动原因 |
| `operator_id` | BIGINT | | 操作人 ID |
| `created_at` | DATETIME | DEFAULT NOW() | 创建时间 |

---

## 五、API 汇总

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 获取部门树 | GET | `/api/departments/tree` | 返回完整树形结构 |
| 移动部门 | PUT | `/api/departments/{id}/move` | 拖拽更新父节点 |
| 检查员工字段 | GET | `/api/employees/check` | 实时校验工号/手机号唯一性 |
| 新增员工 | POST | `/api/employees` | 创建员工 |
| 调动员工 | POST | `/api/employees/{id}/transfer` | 级联更新 + 快照 |
| 员工离职 | PUT | `/api/employees/{id}/resign` | 逻辑删除 + 资源释放 |
| 分页查询员工 | GET | `/api/employees?page=&size=&deptId=&status=` | 分页 + 筛选 |

---

## 六、技术实现建议

### 6.1 数据库设计要点

- **部门表**：设计 `parent_id` 字段，增加 `path` 字段（如 `1-2-5`）用于加速子孙节点的查询
- **员工表**：与部门表是**多对一**关系，`dept_id` 需加索引
- **必须使用逻辑删除**：`is_deleted` 或 `status` 字段

### 6.2 前端状态管理

- 部门树和人员列表属于高频复用数据，建议放入全局状态管理（如 **Redux / Pinia**），避免重复请求
- 拖拽交互可使用成熟的库（如 **react-beautiful-dnd** 或 **ztree**）

### 6.3 后端关键逻辑

- **防循环引用**：在 `move` 接口中递归校验 `newParentId` 不是被移动节点的子孙
- **并发控制**：员工表增加 `version` 字段，调动时使用乐观锁
- **级联审批流更新**：调动时触发审批流节点重新计算（可基于事件驱动或事务内同步更新）

---

## 七、风险与应对

| 风险 | 影响 | 应对策略 |
|------|------|---------|
| 树形结构循环引用导致查询死循环 | 高 | move 接口递归校验 + 400 返回 |
| 大数据量分页查询慢 | 中 | dept_id + status 联合索引，必要时分库分表 |
| 并发调动冲突 | 中 | 乐观锁（version 字段）+ 409 冲突提示 |
| 物理删除导致历史数据丢失 | 高 | 强制逻辑删除（is_deleted），禁止 DELETE 物理删除 |

---

## 八、验收标准

- [ ] 部门树支持懒加载 + 拖拽调整层级
- [ ] 员工新增时工号/手机号全局唯一，重复时实时提示
- [ ] 人员调动后，审批流节点正确级联更新，且生成调动记录
- [ ] 员工离职后状态变为“已离职”，历史数据保留，不可再编辑
- [ ] move 接口能拦截循环引用，返回 400
- [ ] 调动接口带乐观锁，并发冲突返回 409
- [ ] 人员列表支持分页 + 状态筛选

---

## 九、附录

### 9.1 数据库建表 SQL（示例）

```sql
-- 部门表
CREATE TABLE department (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(100) NOT NULL,
    parent_id   BIGINT DEFAULT NULL,
    path        VARCHAR(255) DEFAULT NULL,
    sort_order  INT DEFAULT 0,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_parent (parent_id),
    INDEX idx_path (path),
    FOREIGN KEY (parent_id) REFERENCES department(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 员工表
CREATE TABLE employee (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    name          VARCHAR(100) NOT NULL,
    employee_no   VARCHAR(50) NOT NULL UNIQUE,
    phone         VARCHAR(20) NOT NULL UNIQUE,
    dept_id       BIGINT NOT NULL,
    position      VARCHAR(100),
    status        TINYINT DEFAULT 1 COMMENT '1=在职, 0=离职',
    version       BIGINT DEFAULT 0,
    is_deleted    TINYINT DEFAULT 0,
    created_at    DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_dept (dept_id),
    INDEX idx_status (status),
    FOREIGN KEY (dept_id) REFERENCES department(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 调动记录表
CREATE TABLE employee_transfer_record (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    employee_id   BIGINT NOT NULL,
    old_dept_id   BIGINT,
    new_dept_id   BIGINT NOT NULL,
    old_position  VARCHAR(100),
    new_position  VARCHAR(100),
    reason        VARCHAR(500),
    operator_id   BIGINT,
    created_at    DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_emp (employee_id),
    FOREIGN KEY (employee_id) REFERENCES employee(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### 9.2 术语表

| 术语 | 说明 |
|------|------|
| 懒加载 | 点击节点展开时才请求子节点数据 |
| 逻辑删除 | 不真正删除数据，而是通过 `is_deleted` 字段标记 |
| 乐观锁 | 通过版本号 `version` 控制并发，先提交者胜 |
| path 字段 | 部门路径字符串，如 `1-2-5`，用于快速查询子孙节点 |
