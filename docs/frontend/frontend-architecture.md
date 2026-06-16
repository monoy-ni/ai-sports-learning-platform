# 前端架构

## 技术栈

- Vite
- React
- TypeScript
- React Router
- 基于设计变量的原生 CSS

## 目录结构

- `src/pages`：路由级页面。
- `src/features`：业务功能模块。
- `src/shared/api`：HTTP client 和类型化接口封装。
- `src/shared/components`：复用布局、状态、表格组件。
- `src/shared/styles`：全局 CSS 和设计变量。

功能模块不直接调用 `fetch`，统一通过 `shared/api` 调用接口，保证鉴权头、错误处理和 `traceId` 处理一致。
