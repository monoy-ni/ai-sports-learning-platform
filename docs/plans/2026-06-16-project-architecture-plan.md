# 项目架构实施计划

## 目标

为 AI 体育学习平台搭建 Monorepo 架构，包含 React 前端、Spring Boot 后端、FastAPI AI 服务和完整文档体系。

## 架构

前端只调用 Spring Boot。Spring Boot 负责业务规则、持久化和权限控制。FastAPI 接收内部 AI 任务请求，在真实 Deep Agent 集成前返回结构化的非流式 mock 结果。

## 验收标准

- 文档中定义的目录结构已经存在。
- 每个服务都有最小可运行骨架。
- API 边界已经文档化。
- Docker 和开发脚本已经就位。
- 在依赖可用的前提下，基础编译和构建检查通过。
