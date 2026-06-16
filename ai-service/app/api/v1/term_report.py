from fastapi import APIRouter, Depends

from app.agents.registry import agent_registry
from app.core.security import verify_internal_token
from app.schemas.ai_task import AiTaskResponse
from app.schemas.term_report import TermReportRequest
from app.services.ai_task_service import task_store

router = APIRouter(dependencies=[Depends(verify_internal_token)])


@router.post("/term-report", response_model=AiTaskResponse)
def term_report(request: TermReportRequest) -> AiTaskResponse:
    response = agent_registry.term_report.run(request)
    task_store.save(response)
    return response

