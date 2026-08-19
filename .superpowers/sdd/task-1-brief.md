### Task 1: 数据库初始化 — 建表与索引

**目标**: 创建 `department`、`employee`、`employee_transfer_record` 三张表及索引。

**文件:**
- Create: `src/main/resources/db/V1__init_schema.sql`

**接口契约:**
- Produces: 数据库 schema，供后续所有 Task 依赖。

- [ ] **Step 1: 编写建表 SQL**

```sql
-- 部门表
CREATE TABLE IF NOT EXISTS department (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    name        VARCHAR(100) NOT NULL COMMENT '部门名称',
    parent_id   BIGINT DEFAULT NULL COMMENT '父部门ID',
    path        VARCHAR(255) DEFAULT NULL COMMENT '路径 1-2-5',
    sort_order  INT DEFAULT 0 COMMENT '排序权重',
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_parent (parent_id),
    INDEX idx_path (path),
    FOREIGN KEY (parent_id) REFERENCES department(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- 员工表
CREATE TABLE IF NOT EXISTS employee (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    name          VARCHAR(100) NOT NULL COMMENT '姓名',
    employee_no   VARCHAR(50) NOT NULL UNIQUE COMMENT '工号',
    phone         VARCHAR(20) NOT NULL UNIQUE COMMENT '手机号',
    dept_id       BIGINT NOT NULL COMMENT '所属部门',
    position      VARCHAR(100) COMMENT '职位',
    status        TINYINT DEFAULT 1 COMMENT '1=在职, 0=离职',
    version       BIGINT DEFAULT 0 COMMENT '乐观锁版本号',
    is_deleted    TINYINT DEFAULT 0 COMMENT '逻辑删除 0=否 1=是',
    created_at    DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_dept (dept_id),
    INDEX idx_status (status),
    FOREIGN KEY (dept_id) REFERENCES department(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工表';

-- 员工调动记录表
CREATE TABLE IF NOT EXISTS employee_transfer_record (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    employee_id   BIGINT NOT NULL COMMENT '员工ID',
    old_dept_id   BIGINT COMMENT '原部门',
    new_dept_id   BIGINT NOT NULL COMMENT '目标部门',
    old_position  VARCHAR(100) COMMENT '原职位',
    new_position  VARCHAR(100) COMMENT '新职位',
    reason        VARCHAR(500) COMMENT '调动原因',
    operator_id   BIGINT COMMENT '操作人ID',
    created_at    DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_emp (employee_id),
    FOREIGN KEY (employee_id) REFERENCES employee(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工调动记录表';
```

- [ ] **Step 2: 本地执行验证**

Run: `mysql -u root -p org_db < src/main/resources/db/V1__init_schema.sql`

Expected: 三张表创建成功，`SHOW TABLES;` 显示 `department`, `employee`, `employee_transfer_record`。

- [ ] **Step 3: Commit**

```bash
git add src/main/resources/db/V1__init_schema.sql
git commit -m "feat(db): init department, employee, transfer_record schema"
```

---

