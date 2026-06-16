# 数据库设计

所有业务表包含：

- `id`
- `created_at`
- `updated_at`

重要记录还需要状态字段和审计字段。校园跑数据从第一版开始保留 `data_source`，后续可以从手动录入平滑切换到接口同步。

数据库迁移文件放在 `backend/src/main/resources/db/migration`。
