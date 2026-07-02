# 教师端 MVP 实现说明

## 目标
把混在 demo 里的教师端分离成可交付 MVP：独立前端应用 + 共享 Spring Boot 后端 + 后端补真实查询，AI 结课报告保持后端转发到现有 FastAPI（不写 Python）。

## 形态
- `frontend-teacher/`：独立 React 应用（端口 5174），仅含登录 + 教师路由，复用 `shared/`（httpClient、components、styles、providers）。
- `backend/`：共享 Spring Boot，教师相关接口集中在 `/api/teacher/**`，`SecurityConfig` 已要求 `TEACHER`/`ADMIN` 角色。
- `ai-service/`：无改动，后端 `AiServiceClient.generateTermReport` 转发到 `/internal/ai/v1/term-report`。

## 数据库（Flyway V8/V9）
- V8 新表：`terms`、`classes`、`enrollments`、`campus_run_score_rules`、`check_in_review_history`、`term_report_edit_history`、`student_teacher_notes`。
- V9 种子：1 学期、3 班级、12 学生、健康档案/打卡/校园跑/规则/报告/AI 记录，覆盖异常/连续缺卡/低量/突增等场景。

## 鉴权
`AuthService` 改为读取 `user_accounts` 表，支持 `{demo}` 前缀字面密码与 BCrypt。teacher001 与 12 名 seed 学生均可登录。

## 功能映射（规格 3.1–3.4）
- **3.1 教师看板**：`GET /api/teacher/dashboard?classId=&termId=`，9 项指标 + 运动记录看板 + BMI/肺活量分布 + 4 类风险学生列表，全部真实聚合。
- **3.2 学生详情**：`GET /api/teacher/students/{id}`，档案/计划/打卡/校园跑分数/健康趋势/AI 建议+形象/风险标记/可编辑教师备注。
- **3.2 异常审核留痕**：`PATCH /api/teacher/check-ins/{id}/review` 写 `check_in_review_history`（old/new + reviewer），原始运动数据不动；`GET /review-history` 查记录。
- **3.3 校园跑分数**：`CampusRunScoreService` 按 `campus_run_score_rules.rules_json`（AVERAGE/LATEST/BEST + 频次奖励 + 缺周扣分）计算；规则可配置 `GET/PUT /api/teacher/campus-run-rules`，AI 只解释不裁定。
- **3.4 结课报告**：`POST /api/teacher/reports/generate`（+ `/generate-batch`）聚合打卡/校园跑/健康/计划/异常，转发 3 号 AI；`aiDraft` 保留 AI 原文永不改；`PUT` 编辑写 `term_report_edit_history`；`POST /approve` 转 APPROVED；`GET /edit-history` 查修改记录。

## 验证
后端：`mvn test`（含 `TeacherRepositoryTest` @DataJpaTest 验证实体映射与 @Query；`BmiCategoryUtilTest`；`CampusRunScoreServiceTest`）。
前端：`cd frontend-teacher && npm run build`（tsc + vite 通过）。
端到端：启动 docker mysql + ai-service uvicorn + `mvn spring-boot:run`（Flyway 自动跑 V8/V9）+ `frontend-teacher npm run dev`，登录 teacher001/password123 走查看板→列表→详情→异常审核→报告生成/编辑/审核。
