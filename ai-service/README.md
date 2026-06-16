# AI 服务

FastAPI 内部 AI 服务，负责承载三类非流式结构化 Agent 任务：

- `DAILY_ANALYSIS`：每日运动分析和运动卡片任务
- `PLAN_GENERATION`：学期运动计划生成
- `TERM_REPORT`：结课报告草稿生成

当前实现为 mock Agent，便于先完成前端和 Spring Boot 联调。

## 常用命令

```powershell
python -m venv .venv
.\.venv\Scripts\python -m pip install -e ".[dev]"
.\.venv\Scripts\python -m uvicorn app.main:app --reload --port 8000
.\.venv\Scripts\python -m pytest
```

