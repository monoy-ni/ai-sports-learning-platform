# ADR-0001：使用 Monorepo

## 状态

已接受

## 背景

一期需要 React、Spring Boot、FastAPI 和文档一起演进，同时团队仍在验证核心产品闭环。

## 决策

使用一个仓库管理 `frontend`、`backend`、`ai-service`、`docs`、`infra` 和 `scripts`。

## 影响

正向影响：上手更简单，跨服务接口变更更容易，文档集中维护。

负向影响：后续 CI 需要按影响范围运行检查，避免每次都全量构建。
