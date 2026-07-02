# [OPEN] backend-start-fail

## 背景
- 现象：执行 Spring Boot 后端启动命令时，Maven 输出 `Process terminated with exit code: 1`
- 当前结论：这是外层错误包装，根因尚未确认，需要查看更早的启动日志

## 初始假设
- 假设 1：后端启动时连接数据库失败，例如 MySQL 地址、端口、账号或库名配置不对
- 假设 2：应用端口被占用，Spring Boot 内嵌 Web 服务器启动失败
- 假设 3：环境变量或配置文件缺失，导致 Bean 创建失败
- 假设 4：项目依赖或初始化 SQL 状态异常，触发启动阶段异常
- 假设 5：Docker 中的 MySQL 已启动，但应用仍连接到了错误端口

## 下一步
- 在 `backend` 目录重新运行启动命令并收集完整错误栈
- 根据第一处 `Caused by` 或 `APPLICATION FAILED TO START` 定位根因

## 运行证据
- `mvn spring-boot:run -e` 复现成功，第一处关键报错为：`Access denied for user 'ai_sports'@'localhost' (using password: YES)`
- `backend/src/main/resources/application.yml` 默认连接：`jdbc:mysql://localhost:3306/ai_sports`
- `docker-compose.yml` 当前 MySQL 映射：`3307:3306`
- `docker compose config` 显示目标配置确实是发布宿主机 `3307 -> 容器 3306`
- 但当前运行中的 `ai-sports-mysql` 是旧容器，`docker inspect .NetworkSettings.Ports` 返回 `{"3306/tcp":[]}`，说明没有对宿主机发布端口
- 同时 `docker exec ai-sports-mysql mysql -uai_sports -pai_sports_password -D ai_sports -e "SELECT 1"` 成功，证明容器内账号密码本身是正确的
- 结论：本地直接运行后端时，应用实际连到的是宿主机上另一个 `localhost:3306` MySQL，而不是当前 Docker 容器

## 假设结论
- 假设 1：成立，但更准确地说是“应用连接到了错误目标数据库实例”
- 假设 2：排除，Tomcat 已成功初始化到 `8080`
- 假设 3：暂不支持，当前首个致命错误不是配置缺失而是数据库鉴权失败
- 假设 4：暂不支持，Flyway 尚未成功连库，未进入迁移执行阶段
- 假设 5：成立，旧容器未重建，`3307` 映射未生效，导致本地后端仍连错 MySQL

## 建议修复方向
- 方案 A：重建 MySQL 容器，让 `3307:3306` 真正生效，再用 `localhost:3307` 启动本地后端
- 方案 B：若要继续用 `3306`，先停止宿主机原有 MySQL，再重建 Docker 容器并显式发布 `3306:3306`
- 方案 C：同步修正 README 中 MySQL 端口说明，并补充“修改 compose 端口后需要重建容器”

## 新证据（2026-07-02）
- Docker 当前已正常运行，`docker version` 正常，`ai-sports-mysql` 为 healthy，`localhost:3307` 可连通
- 当前 `mvn spring-boot:run` 的新失败原因不是连接问题，而是 Flyway 校验失败：`Detected failed migration to version 9 (teacher seed)`
- 通过查询 `flyway_schema_history`，确认 `V9__teacher_seed.sql` 的记录为 `success = 0`
- 逐段手动验证后，定位到 `V9__teacher_seed.sql` 中 `health_profiles` 种子数据派生表只给前两列起了别名，却在外层引用了 `v.age`、`v.height_cm` 等列
- 复现实锤报错：`ERROR 1054 (42S22): Unknown column 'v.age' in 'field list'`
- 在修复 `health_profiles` 段后再次全量迁移，进一步定位到 `check_ins` 段存在同类问题：只给前四列起了别名，却在外层引用 `d.duration_minutes`、`d.distance_km` 等列
- 第二个实锤报错：`ERROR 1054 (42S22): Unknown column 'd.duration_minutes' in 'field list'`

## 根因结论
- 根因已确认：`V9__teacher_seed.sql` 的 `health_profiles` 段存在列别名缺失，导致 Flyway 在版本 9 失败
- Docker、MySQL 端口、账号密码都不是当前这次失败的根因
