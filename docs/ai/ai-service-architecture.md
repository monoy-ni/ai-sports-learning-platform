# AI 服务架构

## 技术栈

- Python 3.12
- FastAPI
- Pydantic
- Deep Agent adapter modules

## 边界

AI 服务不负责业务持久化。它从 Spring Boot 接收完整请求载荷，返回结构化任务结果。是否保存、如何保存由 Spring Boot 决定。
