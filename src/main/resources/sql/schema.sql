-- 接口调用日志表
CREATE TABLE IF NOT EXISTS api_call_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    api_name VARCHAR(64) NOT NULL COMMENT '接口标识：helloworld/hash/bubble-sort',
    caller_id VARCHAR(64) NOT NULL DEFAULT 'UNKNOWN' COMMENT '调用人ID',
    caller_name VARCHAR(128) NOT NULL DEFAULT '' COMMENT '调用人姓名',
    caller_type VARCHAR(32) NOT NULL DEFAULT '' COMMENT '人员类型：REGULAR/CONTRACTOR/INTERN',
    caller_level VARCHAR(32) NOT NULL DEFAULT '' COMMENT '人员层级：P5/P6/P7等',
    caller_dept VARCHAR(128) NOT NULL DEFAULT '' COMMENT '人员部门',
    request_params TEXT COMMENT '请求参数快照（JSON）',
    response_status VARCHAR(16) NOT NULL DEFAULT 'SUCCESS' COMMENT '调用结果状态：SUCCESS/FAIL',
    call_duration_ms INT NOT NULL DEFAULT 0 COMMENT '调用耗时（毫秒）',
    gmt_create DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '调用时间',
    gmt_modified DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    INDEX idx_api_call_log_api_name (api_name),
    INDEX idx_api_call_log_caller_id (caller_id),
    INDEX idx_api_call_log_caller_dept (caller_dept),
    INDEX idx_api_call_log_gmt_create (gmt_create),
    INDEX idx_api_call_log_caller_type (caller_type),
    INDEX idx_api_call_log_caller_level (caller_level)
) COMMENT='接口调用日志表';
