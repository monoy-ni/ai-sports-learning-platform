from fastapi import APIRouter

from app.api.v1 import daily_analysis, health, plan_generation, term_report

api_router = APIRouter()
api_router.include_router(health.router)
api_router.include_router(daily_analysis.router, prefix="/internal/ai/v1", tags=["daily-analysis"])
api_router.include_router(plan_generation.router, prefix="/internal/ai/v1", tags=["plan-generation"])
api_router.include_router(term_report.router, prefix="/internal/ai/v1", tags=["term-report"])

