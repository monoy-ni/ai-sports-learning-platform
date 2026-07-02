-- 教师端 MVP 所需关联表与留痕表

-- 学期
CREATE TABLE IF NOT EXISTS terms (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    term_code    VARCHAR(64)  NOT NULL UNIQUE,
    name         VARCHAR(128) NOT NULL,
    start_date   DATE         NOT NULL,
    end_date     DATE         NOT NULL,
    is_current   BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 班级 (课程 + 学期 + 任课教师)
CREATE TABLE IF NOT EXISTS classes (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    class_name   VARCHAR(64)  NOT NULL,
    course_name  VARCHAR(128) NOT NULL,
    term_id      BIGINT       NOT NULL,
    teacher_id   BIGINT       NOT NULL,
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_class_term (class_name, term_id),
    INDEX idx_classes_teacher (teacher_id),
    CONSTRAINT fk_classes_term    FOREIGN KEY (term_id)    REFERENCES terms(id),
    CONSTRAINT fk_classes_teacher FOREIGN KEY (teacher_id) REFERENCES user_accounts(id)
);

-- 选课 / 班级-学生关联 (term_id 冗余便于查询)
CREATE TABLE IF NOT EXISTS enrollments (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id   BIGINT       NOT NULL,
    class_id     BIGINT       NOT NULL,
    term_id      BIGINT       NOT NULL,
    enrolled_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_enroll_student_class (student_id, class_id),
    INDEX idx_enroll_class (class_id),
    INDEX idx_enroll_student_term (student_id, term_id),
    CONSTRAINT fk_enroll_class   FOREIGN KEY (class_id)   REFERENCES classes(id),
    CONSTRAINT fk_enroll_student FOREIGN KEY (student_id) REFERENCES user_accounts(id),
    CONSTRAINT fk_enroll_term    FOREIGN KEY (term_id)    REFERENCES terms(id)
);

-- 校园跑评分规则 (可配置, 不写死 AI)
CREATE TABLE IF NOT EXISTS campus_run_score_rules (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    term_id      BIGINT       NOT NULL,
    rule_name    VARCHAR(128) NOT NULL,
    rules_json   JSON         NOT NULL,
    is_active    BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_rule_term_active (term_id, is_active),
    CONSTRAINT fk_rule_term FOREIGN KEY (term_id) REFERENCES terms(id)
);

-- 打卡审核留痕 (保留修改痕迹: old/new, reviewer, 时间)
CREATE TABLE IF NOT EXISTS check_in_review_history (
    id                      BIGINT PRIMARY KEY AUTO_INCREMENT,
    check_in_id             BIGINT       NOT NULL,
    reviewer_id             BIGINT       NOT NULL,
    old_abnormal            BOOLEAN,
    old_abnormal_reason     VARCHAR(255),
    old_teacher_review_note VARCHAR(1000),
    new_abnormal            BOOLEAN      NOT NULL,
    new_abnormal_reason     VARCHAR(255),
    new_teacher_review_note VARCHAR(1000),
    edit_reason             VARCHAR(500),
    created_at              TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_review_hist_checkin (check_in_id),
    CONSTRAINT fk_review_hist_checkin FOREIGN KEY (check_in_id) REFERENCES check_ins(id)
);

-- 报告编辑留痕 (AI 原文保存在 term_reports.ai_draft, 此表只记 finalContent 的人工修改)
CREATE TABLE IF NOT EXISTS term_report_edit_history (
    id                       BIGINT PRIMARY KEY AUTO_INCREMENT,
    report_id                BIGINT       NOT NULL,
    editor_id                BIGINT       NOT NULL,
    old_final_content        MEDIUMTEXT,
    new_final_content        MEDIUMTEXT,
    old_teacher_review_note  VARCHAR(1000),
    new_teacher_review_note  VARCHAR(1000),
    action                   VARCHAR(32)  NOT NULL,
    created_at               TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_edit_hist_report (report_id),
    CONSTRAINT fk_edit_hist_report FOREIGN KEY (report_id) REFERENCES term_reports(id)
);

-- 学生级教师备注 (详情页可编辑)
CREATE TABLE IF NOT EXISTS student_teacher_notes (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id   BIGINT       NOT NULL,
    teacher_id   BIGINT       NOT NULL,
    term_id      BIGINT       NOT NULL,
    content      TEXT,
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_note_student_term (student_id, term_id),
    CONSTRAINT fk_note_student FOREIGN KEY (student_id) REFERENCES user_accounts(id),
    CONSTRAINT fk_note_term    FOREIGN KEY (term_id)    REFERENCES terms(id)
);
