-- 教师端 MVP 种子数据 (相对 CURDATE() 生成, 保证任意运行日期都有近期数据)

-- 学期: 围绕当前日期的宽窗口, is_current=TRUE
INSERT INTO terms (term_code, name, start_date, end_date, is_current)
SELECT '2026-Spring', '2026春季学期', CURDATE() - INTERVAL 120 DAY, CURDATE() + INTERVAL 30 DAY, TRUE
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM terms WHERE term_code = '2026-Spring');

-- 12 名 seed 学生 (V1 已有 student001/teacher001)
INSERT INTO user_accounts (username, password_hash, display_name, role)
VALUES
    ('student002', '{demo}password123', '李明',   'STUDENT'),
    ('student003', '{demo}password123', '王芳',   'STUDENT'),
    ('student004', '{demo}password123', '赵强',   'STUDENT'),
    ('student005', '{demo}password123', '孙静',   'STUDENT'),
    ('student006', '{demo}password123', '周伟',   'STUDENT'),
    ('student007', '{demo}password123', '吴敏',   'STUDENT'),
    ('student008', '{demo}password123', '郑磊',   'STUDENT'),
    ('student009', '{demo}password123', '陈雪',   'STUDENT'),
    ('student010', '{demo}password123', '褚杰',   'STUDENT'),
    ('student011', '{demo}password123', '卫婷',   'STUDENT'),
    ('student012', '{demo}password123', '蒋勇',   'STUDENT'),
    ('student013', '{demo}password123', '沈丽',   'STUDENT')
ON DUPLICATE KEY UPDATE display_name = VALUES(display_name);

-- 3 个班级, 任课教师 teacher001
INSERT INTO classes (class_name, course_name, term_id, teacher_id)
SELECT c.class_name, c.course_name, t.id, u.id
FROM (
    SELECT '计算机2401' AS class_name, '大学体育' AS course_name UNION ALL
    SELECT '计算机2402', '大学体育' UNION ALL
    SELECT '软件2401',   '大学体育'
) c
CROSS JOIN terms t
CROSS JOIN user_accounts u
WHERE t.term_code = '2026-Spring' AND u.username = 'teacher001'
  AND NOT EXISTS (
      SELECT 1 FROM classes x
      WHERE x.class_name = c.class_name AND x.term_id = t.id
  );

-- 选课: 每班 4 人
INSERT INTO enrollments (student_id, class_id, term_id)
SELECT u.id, c.id, t.id
FROM user_accounts u
JOIN classes c ON c.class_name = (
    CASE u.username
        WHEN 'student002' THEN '计算机2401' WHEN 'student003' THEN '计算机2401'
        WHEN 'student004' THEN '计算机2401' WHEN 'student005' THEN '计算机2401'
        WHEN 'student006' THEN '计算机2402' WHEN 'student007' THEN '计算机2402'
        WHEN 'student008' THEN '计算机2402' WHEN 'student009' THEN '计算机2402'
        WHEN 'student010' THEN '软件2401'   WHEN 'student011' THEN '软件2401'
        WHEN 'student012' THEN '软件2401'   WHEN 'student013' THEN '软件2401'
    END)
JOIN terms t ON t.term_code = '2026-Spring'
WHERE u.username LIKE 'student0%' AND u.username <> 'student001'
  AND NOT EXISTS (
      SELECT 1 FROM enrollments e WHERE e.student_id = u.id AND e.class_id = c.id
  );

-- 健康档案 (BMI 分布: 偏瘦2/正常6/超重3/肥胖1, 肺活量 2400-4500, 2 例疾病)
INSERT INTO health_profiles (student_id, gender, age, height_cm, weight_kg, bmi, vital_capacity, disease_status, disease_note, sport_goal, weekly_frequency, body_type)
SELECT u.id, v.gender, v.age, v.height_cm, v.weight_kg, v.bmi, v.vital_capacity, v.disease_status, v.disease_note, v.sport_goal, v.weekly_frequency, v.body_type
FROM user_accounts u
JOIN (
    SELECT 'student002' AS un, 'M' AS gender, 20 AS age, 175.00 AS height_cm, 68.00 AS weight_kg, 22.20 AS bmi, 4200 AS vital_capacity, 'NONE' AS disease_status, NULL AS disease_note, '提升耐力' AS sport_goal, 3 AS weekly_frequency, '匀称' AS body_type UNION ALL
    SELECT 'student003',      'F', 19, 162.00, 48.00, 18.29, 2600, 'NONE',       NULL,          '减脂塑形', 3, '偏瘦' UNION ALL
    SELECT 'student004',      'M', 21, 180.00, 95.00, 29.32, 3800, 'HAS_DISEASE','哮喘,避免剧烈长跑','维持体能', 2, '肥胖' UNION ALL
    SELECT 'student005',      'F', 20, 165.00, 58.00, 21.30, 3500, 'NONE',       NULL,          '提升配速', 4, '匀称' UNION ALL
    SELECT 'student006',      'M', 20, 178.00, 82.00, 25.91, 4100, 'NONE',       NULL,          '减重',     3, '超重' UNION ALL
    SELECT 'student007',      'F', 19, 160.00, 45.00, 17.58, 2400, 'UNKNOWN',    '偶有心悸',    '低强度运动',2, '偏瘦' UNION ALL
    SELECT 'student008',      'M', 21, 176.00, 71.00, 22.92, 4400, 'NONE',       NULL,          '备战校运', 5, '匀称' UNION ALL
    SELECT 'student009',      'F', 20, 168.00, 78.00, 27.64, 3300, 'NONE',       NULL,          '减脂',     3, '超重' UNION ALL
    SELECT 'student010',      'M', 20, 172.00, 65.00, 21.97, 4000, 'NONE',       NULL,          '提升耐力', 4, '匀称' UNION ALL
    SELECT 'student011',      'F', 19, 163.00, 55.00, 20.71, 3700, 'NONE',       NULL,          '保持健康', 3, '匀称' UNION ALL
    SELECT 'student012',      'M', 21, 179.00, 88.00, 27.50, 3900, 'NONE',       NULL,          '减重',     3, '超重' UNION ALL
    SELECT 'student013',      'F', 20, 166.00, 56.00, 20.33, 3600, 'NONE',       NULL,          '提升配速', 4, '匀称'
) v ON u.username = v.un
ON DUPLICATE KEY UPDATE
    gender=v.gender, age=v.age, height_cm=v.height_cm, weight_kg=v.weight_kg, bmi=v.bmi,
    vital_capacity=v.vital_capacity, disease_status=v.disease_status, disease_note=v.disease_note,
    sport_goal=v.sport_goal, weekly_frequency=v.weekly_frequency, body_type=v.body_type;

-- 运动计划 (3 条 active)
INSERT INTO sport_plans (student_id, term_id, generation_status, weekly_plan, active)
SELECT u.id, '2026-Spring', 'SUCCESS',
    JSON_OBJECT('goal','提升耐力','weeklyFrequency',3,'sessions',
        JSON_ARRAY(JSON_OBJECT('type','RUNNING','paceMin',6,'paceMax',7,'durationMin',30,'distanceKm',5))), TRUE
FROM user_accounts u
WHERE u.username IN ('student002','student005','student008')
  AND NOT EXISTS (SELECT 1 FROM sport_plans p WHERE p.student_id = u.id AND p.term_id='2026-Spring');

-- 打卡记录 (CURDATE 相对, 含异常/连续缺卡/低量/突增). 唯一键 (student_id, check_in_date) 保证可重入
INSERT INTO check_ins (student_id, check_in_date, weather, exercise_type, duration_minutes, distance_km, pace_minutes_per_km, fatigue_level, feeling, completed_plan, campus_run_score, data_source, abnormal, abnormal_reason, teacher_review_note)
SELECT u.id, d.check_in_date, d.weather, d.exercise_type, d.duration_minutes, d.distance_km, d.pace_minutes_per_km, d.fatigue_level, d.feeling, d.completed_plan, d.campus_run_score, d.data_source, d.abnormal, d.abnormal_reason, NULL
FROM user_accounts u
JOIN (
    SELECT 'student002' AS un, CURDATE() - INTERVAL 1 DAY AS check_in_date, 'SUNNY' AS weather, 'RUNNING' AS exercise_type, 30 AS duration_minutes, 5.0 AS distance_km, 6.0 AS pace_minutes_per_km, 4 AS fatigue_level, '良好' AS feeling, TRUE AS completed_plan, 82 AS campus_run_score, 'MANUAL' AS data_source, FALSE AS abnormal, NULL AS abnormal_reason UNION ALL
    SELECT 'student002',      CURDATE() - INTERVAL 3 DAY,  'CLOUDY',            'RUNNING',            32, 5.2, 6.1, 5, '稍累', TRUE,  NULL, 'MANUAL', FALSE, NULL UNION ALL
    SELECT 'student002',      CURDATE() - INTERVAL 5 DAY,  'SUNNY',             'RUNNING',            28, 4.8, 6.1, 4, 'ok',  TRUE,  NULL, 'MANUAL', FALSE, NULL UNION ALL
    SELECT 'student003',      CURDATE() - INTERVAL 2 DAY,  'SUNNY',             'RUNNING',            10, 1.5, 6.7, 3, '一般', FALSE, 70, 'MANUAL', FALSE, NULL UNION ALL
    SELECT 'student003',      CURDATE() - INTERVAL 4 DAY,  'RAINY',             'INDOOR_BODYWEIGHT',  8,  NULL, NULL,3, 'ok',  FALSE, NULL, 'MANUAL', FALSE, NULL UNION ALL
    SELECT 'student003',      CURDATE() - INTERVAL 6 DAY,  'SUNNY',             'RUNNING',            10, 1.4, 7.1, 2, '轻松', FALSE, NULL, 'MANUAL', FALSE, NULL UNION ALL
    SELECT 'student004',      CURDATE() - INTERVAL 1 DAY,  'CLOUDY',            'RUNNING',            25, 4.0, 6.2, 4, 'ok',  TRUE,  65, 'MANUAL', FALSE, NULL UNION ALL
    SELECT 'student005',      CURDATE() - INTERVAL 1 DAY,  'SUNNY',             'RUNNING',            40, 6.5, 6.2, 5, '累',   TRUE,  90, 'MANUAL', FALSE, NULL UNION ALL
    SELECT 'student005',      CURDATE() - INTERVAL 2 DAY,  'SUNNY',             'RUNNING',            120,8.0, 15.0,7, '过度', TRUE, NULL, 'MANUAL', TRUE, '配速异常偏低/时长突增' UNION ALL
    SELECT 'student006',      CURDATE() - INTERVAL 1 DAY,  'SUNNY',             'RUNNING',            35, 5.5, 6.4, 5, 'ok',  TRUE,  78, 'MANUAL', FALSE, NULL UNION ALL
    SELECT 'student007',      CURDATE() - INTERVAL 12 DAY, 'SUNNY',             'RUNNING',            20, 3.0, 6.7, 3, 'ok',  TRUE,  60, 'MANUAL', FALSE, NULL UNION ALL
    SELECT 'student008',      CURDATE() - INTERVAL 1 DAY,  'SUNNY',             'RUNNING',            45, 7.0, 6.4, 6, '累',   TRUE,  92, 'API_SYNC', FALSE, NULL UNION ALL
    SELECT 'student008',      CURDATE() - INTERVAL 3 DAY,  'CLOUDY',            'RUNNING',            40, 6.5, 6.1, 5, 'ok',  TRUE,  NULL, 'MANUAL', FALSE, NULL UNION ALL
    SELECT 'student009',      CURDATE() - INTERVAL 2 DAY,  'SUNNY',             'RUNNING',            20, 3.0, 6.7, 3, 'ok',  FALSE, 68, 'MANUAL', FALSE, NULL UNION ALL
    SELECT 'student009',      CURDATE() - INTERVAL 5 DAY,  'SUNNY',             'RUNNING',            18, 2.7, 6.7, 3, 'ok',  FALSE, NULL, 'MANUAL', FALSE, NULL UNION ALL
    SELECT 'student010',      CURDATE() - INTERVAL 1 DAY,  'SUNNY',             'RUNNING',            30, 5.0, 6.0, 4, 'ok',  TRUE,  85, 'MANUAL', FALSE, NULL UNION ALL
    SELECT 'student010',      CURDATE() - INTERVAL 9 DAY,  'SUNNY',             'RUNNING',            15, 1.5, 1.8, 4, '异常', FALSE, NULL, 'MANUAL', TRUE, '配速过低' UNION ALL
    SELECT 'student011',      CURDATE() - INTERVAL 1 DAY,  'RAINY',             'INDOOR_BODYWEIGHT', 25,  NULL, NULL,4, 'ok',  TRUE,  NULL, 'MANUAL', FALSE, NULL UNION ALL
    SELECT 'student012',      CURDATE() - INTERVAL 1 DAY,  'SUNNY',             'RUNNING',            28, 4.5, 6.2, 4, 'ok',  TRUE,  72, 'MANUAL', FALSE, NULL UNION ALL
    SELECT 'student013',      CURDATE() - INTERVAL 2 DAY,  'SUNNY',             'RUNNING',            32, 5.2, 6.1, 4, 'ok',  TRUE,  88, 'API_SYNC', FALSE, NULL
) d ON u.username = d.un
ON DUPLICATE KEY UPDATE
    weather=VALUES(weather), exercise_type=VALUES(exercise_type), duration_minutes=VALUES(duration_minutes),
    distance_km=VALUES(distance_km), pace_minutes_per_km=VALUES(pace_minutes_per_km), fatigue_level=VALUES(fatigue_level),
    feeling=VALUES(feeling), completed_plan=VALUES(completed_plan), campus_run_score=VALUES(campus_run_score),
    data_source=VALUES(data_source), abnormal=VALUES(abnormal), abnormal_reason=VALUES(abnormal_reason);

-- 校园跑记录 (每生 2-4 条, 55-95 分, MANUAL/API_SYNC 混)
INSERT INTO campus_run_records (student_id, score, data_source)
SELECT u.id, r.score, r.data_source
FROM user_accounts u
JOIN (
    SELECT 'student002' AS un, 82.0 AS score, 'MANUAL' AS data_source UNION ALL
    SELECT 'student002',      85.0,           'MANUAL' UNION ALL
    SELECT 'student004',      60.0,           'MANUAL' UNION ALL
    SELECT 'student004',      58.0,           'MANUAL' UNION ALL
    SELECT 'student005',      90.0,           'API_SYNC' UNION ALL
    SELECT 'student005',      92.0,           'API_SYNC' UNION ALL
    SELECT 'student006',      78.0,           'MANUAL' UNION ALL
    SELECT 'student007',      55.0,           'MANUAL' UNION ALL
    SELECT 'student008',      92.0,           'API_SYNC' UNION ALL
    SELECT 'student008',      94.0,           'API_SYNC' UNION ALL
    SELECT 'student009',      68.0,           'MANUAL' UNION ALL
    SELECT 'student010',      85.0,           'MANUAL' UNION ALL
    SELECT 'student012',      72.0,           'MANUAL' UNION ALL
    SELECT 'student013',      88.0,           'API_SYNC'
) r ON u.username = r.un
WHERE NOT EXISTS (
    SELECT 1 FROM campus_run_records c WHERE c.student_id = u.id AND c.score = r.score
);

-- 校园跑评分规则 (1 条 active)
INSERT INTO campus_run_score_rules (term_id, rule_name, rules_json, is_active)
SELECT t.id, '默认评分规则',
    JSON_OBJECT('maxScore',100,'passingScore',60,'aggregation','AVERAGE','deductPerMissedWeek',5,'frequencyBonus',JSON_OBJECT('thresholdCount',2,'bonus',5)),
    TRUE
FROM terms t
WHERE t.term_code = '2026-Spring'
  AND NOT EXISTS (SELECT 1 FROM campus_run_score_rules r WHERE r.term_id = t.id AND r.is_active = TRUE);

-- 1 条 DRAFT 结课报告 (含 AI 原文)
INSERT INTO term_reports (student_id, term_id, ai_draft, final_content, teacher_review_note, status)
SELECT u.id, '2026-Spring',
    JSON_OBJECT('healthOverview','基础健康数据整体稳定。','participation','运动参与度良好。','planExecution','计划执行中等偏好。','campusRunAnalysis','校园跑成绩有提升趋势。','riskAndWeakness','需关注疲劳反馈。','overallEvaluation','建议保持规律运动。','teacherReviewSlot','待教师补充。','medicalDisclaimer','AI 草稿, 不替代教师评价。'),
    'AI 报告草稿待教师审核', NULL, 'DRAFT'
FROM user_accounts u
WHERE u.username = 'student002'
  AND NOT EXISTS (SELECT 1 FROM term_reports tr WHERE tr.student_id = u.id AND tr.term_id = '2026-Spring');

-- 1 条 AI 每日分析记录 (供详情页展示建议+形象)
INSERT INTO ai_records (task_id, agent_type, status, input_summary, output_result, image_url, failure_reason)
SELECT 'seed-daily-student002', 'DAILY_ANALYSIS', 'SUCCESS',
    JSON_OBJECT('studentId',(SELECT id FROM user_accounts WHERE username='student002'),'inputVersion','v0.1.0'),
    JSON_OBJECT('suggestions', JSON_ARRAY('运动后注意拉伸股四头肌','补充水分, 控制配速'),'imageStatus','SUCCESS','summary','今日跑步表现良好, 配速稳定。'),
    'https://placehold.co/600x400/176b4d/ffffff.png?text=Daily+Card', NULL
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM ai_records WHERE task_id = 'seed-daily-student002');
