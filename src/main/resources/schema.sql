-- 人员看板建表脚本（MySQL 8.0，InnoDB，utf8mb4）

CREATE TABLE IF NOT EXISTS emp_employee (
    id           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '系统自增主键',
    tenant_id    VARCHAR(32)  NOT NULL DEFAULT 'default' COMMENT '租户标识（预留）',
    emp_no       VARCHAR(32)  NOT NULL DEFAULT '' COMMENT '员工工号（业务唯一）',
    emp_name     VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '员工姓名',
    dept_name    VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '所属部门',
    position     VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '岗位',
    email        VARCHAR(128) NOT NULL DEFAULT '' COMMENT '邮箱',
    phone        VARCHAR(32)  NOT NULL DEFAULT '' COMMENT '手机号',
    hire_date    DATETIME     NULL DEFAULT NULL COMMENT '入职日期',
    status       VARCHAR(16)  NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE/INACTIVE',
    source       VARCHAR(16)  NOT NULL DEFAULT 'MANUAL' COMMENT '来源：MANUAL/IMPORT',
    is_deleted   TINYINT      NOT NULL DEFAULT 0 COMMENT '软删标记 0/1',
    gmt_create   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    gmt_modified DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY pk_emp_employee (id),
    UNIQUE KEY uk_emp_employee_no (tenant_id, emp_no),
    KEY idx_emp_employee_dept (tenant_id, dept_name)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='员工基本信息表';

CREATE TABLE IF NOT EXISTS emp_budget (
    id            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    tenant_id     VARCHAR(32)  NOT NULL DEFAULT 'default' COMMENT '租户',
    emp_no        VARCHAR(32)  NOT NULL DEFAULT '' COMMENT '员工工号',
    budget_period VARCHAR(16)  NOT NULL DEFAULT '' COMMENT '预算周期，如 2026-10 / 2026-Q4',
    amount        DECIMAL(18, 2) NOT NULL DEFAULT 0.00 COMMENT '预算金额',
    currency      VARCHAR(8)   NOT NULL DEFAULT 'CNY' COMMENT '币种',
    remark        VARCHAR(256) NOT NULL DEFAULT '' COMMENT '备注',
    gmt_create    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    gmt_modified  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY pk_emp_budget (id),
    UNIQUE KEY uk_emp_budget_emp_period (tenant_id, emp_no, budget_period)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='员工成本预算表';

CREATE TABLE IF NOT EXISTS emp_import_task (
    id            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '批次ID',
    tenant_id     VARCHAR(32)  NOT NULL DEFAULT 'default' COMMENT '租户',
    operator      VARCHAR(32)  NOT NULL DEFAULT '' COMMENT '操作人工号',
    file_name     VARCHAR(128) NOT NULL DEFAULT '' COMMENT '原始文件名',
    total_count   INT          NOT NULL DEFAULT 0 COMMENT '总行数',
    success_count INT          NOT NULL DEFAULT 0 COMMENT '成功行数',
    fail_count    INT          NOT NULL DEFAULT 0 COMMENT '失败行数',
    status        VARCHAR(16)  NOT NULL DEFAULT 'PROCESSING' COMMENT 'PROCESSING/DONE/FAILED',
    gmt_create    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    gmt_modified  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY pk_emp_import_task (id),
    KEY idx_emp_import_task_op (tenant_id, operator)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='导入批次表';

CREATE TABLE IF NOT EXISTS emp_import_detail (
    id           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    task_id      BIGINT       NOT NULL DEFAULT 0 COMMENT '批次ID',
    row_no       INT          NOT NULL DEFAULT 0 COMMENT 'Excel 行号',
    emp_no       VARCHAR(32)  NOT NULL DEFAULT '' COMMENT '行内工号',
    result       VARCHAR(8)   NOT NULL DEFAULT '' COMMENT 'SUCCESS/FAIL',
    fail_reason  VARCHAR(256) NOT NULL DEFAULT '' COMMENT '失败原因',
    gmt_create   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    gmt_modified DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY pk_emp_import_detail (id),
    KEY idx_emp_import_detail_task (task_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='导入明细表';

CREATE TABLE IF NOT EXISTS emp_whitelist (
    id           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    tenant_id    VARCHAR(32)  NOT NULL DEFAULT 'default' COMMENT '租户',
    emp_no       VARCHAR(32)  NOT NULL DEFAULT '' COMMENT '被授权用户工号',
    user_name    VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '姓名',
    remark       VARCHAR(128) NOT NULL DEFAULT '' COMMENT '备注',
    created_by   VARCHAR(32)  NOT NULL DEFAULT '' COMMENT '添加人工号',
    is_deleted   TINYINT      NOT NULL DEFAULT 0 COMMENT '软删标记',
    gmt_create   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    gmt_modified DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY pk_emp_whitelist (id),
    UNIQUE KEY uk_emp_whitelist_no (tenant_id, emp_no)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='白名单表';

-- 数据迁移：初始化首名白名单管理员（按实际工号替换）
INSERT INTO emp_whitelist (tenant_id, emp_no, user_name, remark, created_by)
SELECT 'default', 'admin', '系统管理员', '初始化管理员', 'system'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM emp_whitelist WHERE tenant_id = 'default' AND emp_no = 'admin');
