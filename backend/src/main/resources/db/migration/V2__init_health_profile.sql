CREATE TABLE IF NOT EXISTS health_profiles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL UNIQUE,
    gender VARCHAR(20) NOT NULL,
    age INT NOT NULL,
    height_cm DECIMAL(6,2) NOT NULL,
    weight_kg DECIMAL(6,2) NOT NULL,
    bmi DECIMAL(5,2) NOT NULL,
    vital_capacity INT NOT NULL,
    disease_status VARCHAR(32) NOT NULL,
    disease_note VARCHAR(500),
    sport_goal VARCHAR(255) NOT NULL,
    weekly_frequency INT NOT NULL,
    body_type VARCHAR(64) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

