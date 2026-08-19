CREATE TABLE department (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(100) NOT NULL,
    parent_id   BIGINT       NULL,
    path        VARCHAR(512) NOT NULL DEFAULT '/',
    sort_order  INT          NOT NULL DEFAULT 0,
    leader_id   BIGINT       NULL,
    is_deleted  TINYINT      NOT NULL DEFAULT 0,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_dept_parent (parent_id),
    KEY idx_dept_sort (parent_id, sort_order),
    KEY idx_dept_path (path)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE employee (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    name         VARCHAR(50)  NOT NULL,
    employee_no  VARCHAR(32)  NOT NULL,
    phone        VARCHAR(20)  NOT NULL,
    dept_id      BIGINT       NOT NULL,
    position     VARCHAR(50)  NULL,
    status       VARCHAR(16)  NOT NULL DEFAULT 'ACTIVE',
    is_deleted   TINYINT      NOT NULL DEFAULT 0,
    resign_date  DATE         NULL,
    version      INT          NOT NULL DEFAULT 0,
    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_emp_no (employee_no, is_deleted),
    UNIQUE KEY uk_emp_phone (phone, is_deleted),
    KEY idx_emp_dept (dept_id),
    KEY idx_emp_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE transfer_record (
    id             BIGINT       NOT NULL AUTO_INCREMENT,
    employee_id    BIGINT       NOT NULL,
    from_dept_id   BIGINT       NOT NULL,
    to_dept_id     BIGINT       NOT NULL,
    old_position   VARCHAR(50)  NULL,
    new_position   VARCHAR(50)  NULL,
    reason         VARCHAR(255) NULL,
    operator       VARCHAR(50)  NULL,
    created_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_transfer_emp (employee_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE approval_flow_node (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    employee_id   BIGINT       NOT NULL,
    dept_id       BIGINT       NOT NULL,
    approver_id   BIGINT       NULL,
    scene         VARCHAR(32)  NOT NULL DEFAULT 'LEAVE',
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_flow_emp (employee_id),
    KEY idx_flow_approver (approver_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
