# AI 体育学习平台

这是 AI 体育学习平台一期的 Monorepo 工程骨架。系统采用 `React -> Spring Boot -> FastAPI -> Deep Agent` 分层，围绕学生建档、AI 运动计划、每日运动打卡、AI 反馈、生图状态、教师看板和结课报告形成业务闭环。

## 项目结构

```text
frontend/    React + TypeScript + Vite 前端应用，包含学生端和教师端
backend/     Spring Boot 业务后端，负责 JWT/RBAC、MySQL、AI 服务调用
ai-service/  FastAPI 内部 AI 服务，包含三类 Agent 的 mock 适配
docs/        前端、后端、AI、接口、ADR、版本和计划文档
infra/       Dockerfile、MySQL、Nginx 等基础设施配置
scripts/     本地开发辅助脚本
```

## 服务边界

- 前端只调用 Spring Boot 暴露的 `/api/**` 接口。
- Spring Boot 负责登录鉴权、角色权限、业务规则、事务、MySQL 持久化和教师审核流程。
- Spring Boot 通过 `/internal/ai/v1/**` 调用 FastAPI 内部 AI 服务。
- FastAPI 只负责结构化 AI 任务执行，不直接访问 MySQL。
- 所有 AI 输出均为非流式，并包含任务状态、模型信息、输入版本和失败原因。

## 默认端口

| Service | Port | URL |
| --- | --- | --- |
| 前端 | 5173 | `http://localhost:5173` |
| 后端 | 8080 | `http://localhost:8080` |
| AI 服务 | 8000 | `http://localhost:8000` |
| MySQL | 3306 | `localhost:3306` |

## 本地开发

```powershell
Copy-Item .env.example .env
docker compose up -d mysql

cd ai-service
python -m venv .venv
.\.venv\Scripts\python -m pip install -e ".[dev]"
.\.venv\Scripts\python -m uvicorn app.main:app --reload --port 8000

cd ..\backend
mvn spring-boot:run

cd ..\frontend
npm install
npm run dev
```

后端内置本地开发演示账号：

| 角色 | 用户名 | 密码 |
| --- | --- | --- |
| 学生 | `student001` | `password123` |
| 教师 | `teacher001` | `password123` |

## 验证

```powershell
.\scripts\check-all.ps1
```

该脚本会在依赖已安装的前提下运行前端类型检查/构建、后端测试和 AI 服务测试。
