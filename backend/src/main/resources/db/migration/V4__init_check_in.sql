CREATE TABLE IF NOT EXISTS check_ins (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    check_in_date DATE NOT NULL,
    weather VARCHAR(32) NOT NULL,
    exercise_type VARCHAR(32) NOT NULL,
    duration_minutes INT NOT NULL,
    distance_km DECIMAL(7,2),
    pace_minutes_per_km DECIMAL(5,2),
    fatigue_level INT NOT NULL,
    feeling VARCHAR(1000),
    completed_plan BOOLEAN NOT NULL DEFAULT FALSE,
    campus_run_score DECIMAL(6,2),
    data_source VARCHAR(32) NOT NULL,
    abnormal BOOLEAN NOT NULL DEFAULT FALSE,
    abnormal_reason VARCHAR(255),
    teacher_review_note VARCHAR(1000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_student_checkin_date (student_id, check_in_date)
);

