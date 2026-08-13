CREATE TABLE IF NOT EXISTS api_call_log (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    api_name      VARCHAR(50)   NOT NULL,
    user_id       VARCHAR(100)  NOT NULL,
    user_type     VARCHAR(50),
    user_level    VARCHAR(50),
    user_dept     VARCHAR(100),
    call_time     TIMESTAMP     NOT NULL,
    request_body  TEXT,
    response_body TEXT,
    gmt_create    TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    gmt_modified  TIMESTAMP     DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_api_call_log_api_name ON api_call_log(api_name);
CREATE INDEX IF NOT EXISTS idx_api_call_log_call_time ON api_call_log(call_time);
CREATE INDEX IF NOT EXISTS idx_api_call_log_user_id ON api_call_log(user_id);
