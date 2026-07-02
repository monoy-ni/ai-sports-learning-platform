# 前端工程

AI 体育学习平台的 React + TypeScript + Vite 前端工程。

## 常用命令

```powershell
npm install
npm run dev
npm run typecheck
npm run build
```

## 架构说明

- `src/pages`：路由级页面。
- `src/app`：应用 Provider、路由和布局。
- `src/shared`：API client、复用组件、Hooks、工具函数和样式。
- `src/features`：按业务能力预留的功能模块，后续复杂流程放在这里。
