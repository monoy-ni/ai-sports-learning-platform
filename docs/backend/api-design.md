# 后端接口设计

前端接口统一以 `/api/**` 开头。

主要接口分组：

- `/api/auth/**`
- `/api/student/**`
- `/api/teacher/**`

后端使用内部服务 token 调用 `/internal/ai/v1/**` AI 服务接口。浏览器客户端不能直接调用 AI 接口。
