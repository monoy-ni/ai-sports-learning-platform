CREATE TABLE IF NOT EXISTS sport_plans (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    term_id VARCHAR(64) NOT NULL,
    generation_status VARCHAR(32) NOT NULL,
    weekly_plan JSON,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    generated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_sport_plan_student_term (student_id, term_id)
);

