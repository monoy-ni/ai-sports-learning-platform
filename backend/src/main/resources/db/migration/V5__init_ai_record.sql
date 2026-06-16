CREATE TABLE IF NOT EXISTS ai_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_id VARCHAR(80) NOT NULL,
    agent_type VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL,
    input_summary JSON,
    output_result JSON,
    image_url VARCHAR(500),
    failure_reason VARCHAR(1000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_ai_records_task_id (task_id)
);

