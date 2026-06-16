CREATE TABLE IF NOT EXISTS campus_run_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    score DECIMAL(6,2) NOT NULL,
    data_source VARCHAR(32) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_campus_run_student (student_id)
);

