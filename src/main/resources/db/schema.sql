-- 接口调用埋点流水表
CREATE TABLE IF NOT EXISTS api_invoke_log (
    id            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    api_code      VARCHAR(32)  NOT NULL COMMENT '接口编码（HELLO/HASH/BUBBLE_SORT/EXPORT/REPORT_*）',
    api_name      VARCHAR(64)  NOT NULL COMMENT '接口名称',
    invoker_id    VARCHAR(64)  NOT NULL COMMENT '调用人ID',
    invoker_name  VARCHAR(64)  NULL COMMENT '调用人姓名',
    person_type   VARCHAR(32)  NOT NULL COMMENT '人员类型（冗余）',
    person_level  VARCHAR(32)  NOT NULL COMMENT '人员层级（冗余）',
    person_dept   VARCHAR(64)  NOT NULL COMMENT '人员部门（冗余）',
    invoke_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '调用时间',
    duration_ms   INT          NULL COMMENT '调用耗时(ms)',
    result_code   VARCHAR(16)  NULL COMMENT '结果码',
    ip            VARCHAR(64)  NULL COMMENT '调用来源IP',
    gmt_create    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    gmt_modified  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (id),
    KEY idx_api_invoke_log_api_time (api_code, invoke_time),
    KEY idx_api_invoke_log_person_type (person_type, invoke_time),
    KEY idx_api_invoke_log_person_level (person_level, invoke_time),
    KEY idx_api_invoke_log_person_dept (person_dept, invoke_time),
    KEY idx_api_invoke_log_invoker (invoker_id, invoke_time)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '接口调用埋点流水表';