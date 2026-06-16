from fastapi import APIRouter, Depends

from app.agents.registry import agent_registry
from app.core.security import verify_internal_token
from app.schemas.daily_analysis import DailyAnalysisRequest
from app.schemas.ai_task import AiTaskResponse
from app.services.ai_task_service import task_store

router = APIRouter(dependencies=[Depends(verify_internal_token)])


@router.post("/daily-analysis", response_model=AiTaskResponse)
def daily_analysis(request: DailyAnalysisRequest) -> AiTaskResponse:
    response = agent_registry.daily_analysis.run(request)
    task_store.save(response)
    return response

