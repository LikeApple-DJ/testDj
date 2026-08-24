CREATE TABLE IF NOT EXISTS tracking_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    api_name VARCHAR(255) NOT NULL,
    caller_name VARCHAR(255),
    caller_type VARCHAR(255),
    caller_level VARCHAR(255),
    caller_dept VARCHAR(255),
    extra_info VARCHAR(1000),
    call_time TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_tracking_api_name ON tracking_record(api_name);
CREATE INDEX IF NOT EXISTS idx_tracking_caller_name ON tracking_record(caller_name);
CREATE INDEX IF NOT EXISTS idx_tracking_call_time ON tracking_record(call_time);