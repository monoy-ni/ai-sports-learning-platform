CREATE TABLE IF NOT EXISTS term_reports (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    term_id VARCHAR(64) NOT NULL,
    ai_draft JSON,
    final_content TEXT,
    teacher_review_note VARCHAR(1000),
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_term_reports_student_term (student_id, term_id)
);

