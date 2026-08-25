CREATE TABLE IF NOT EXISTS api_call_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    api_name VARCHAR(100) NOT NULL,
    caller VARCHAR(100) NOT NULL,
    department VARCHAR(100) NOT NULL,
    level VARCHAR(50) NOT NULL,
    type VARCHAR(100) NOT NULL,
    call_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_api_name ON api_call_log(api_name);
CREATE INDEX IF NOT EXISTS idx_department ON api_call_log(department);
CREATE INDEX IF NOT EXISTS idx_level ON api_call_log(level);
CREATE INDEX IF NOT EXISTS idx_type ON api_call_log(type);
CREATE INDEX IF NOT EXISTS idx_call_time ON api_call_log(call_time);