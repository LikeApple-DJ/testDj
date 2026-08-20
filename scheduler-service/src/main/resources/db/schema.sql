CREATE TABLE IF NOT EXISTS job_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_name VARCHAR(128) NOT NULL COMMENT '任务名称',
    job_desc VARCHAR(512) COMMENT '任务描述',
    job_group VARCHAR(64) COMMENT '任务分组',
    cron_expression VARCHAR(64) NOT NULL COMMENT 'Cron 表达式',
    executor_handler VARCHAR(128) NOT NULL COMMENT '执行器处理器标识',
    executor_param TEXT COMMENT '执行参数（JSON）',
    max_retry_times INT DEFAULT 3 COMMENT '最大重试次数',
    retry_interval INT DEFAULT 60 COMMENT '重试间隔（秒）',
    alert_email VARCHAR(256) COMMENT '告警邮箱',
    status TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_group (job_group),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务信息表';

CREATE TABLE IF NOT EXISTS job_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    job_id BIGINT NOT NULL COMMENT '任务 ID',
    trigger_time DATETIME COMMENT '触发时间',
    finish_time DATETIME COMMENT '完成时间',
    executor_address VARCHAR(128) COMMENT '执行器地址',
    status TINYINT DEFAULT 0 COMMENT '状态 0-运行中 1-成功 2-失败 3-超时',
    result TEXT COMMENT '执行结果',
    retry_times INT DEFAULT 0 COMMENT '已重试次数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_job_id (job_id),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='执行记录表';