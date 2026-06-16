# 接口调用规范

前端只调用 Spring Boot：

- 基础路径：`/api`
- 鉴权：`Authorization: Bearer <jwt>`
- 统一响应结构：`{ code, message, data, traceId }`

前端代码不能直接调用 `/internal/ai/v1/**`。AI 服务接口是 Spring Boot 和 FastAPI 之间的内部服务契约。
