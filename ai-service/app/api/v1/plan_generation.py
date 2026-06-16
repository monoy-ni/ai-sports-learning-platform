from fastapi import APIRouter, Depends, HTTPException

from app.agents.registry import agent_registry
from app.core.security import verify_internal_token
from app.schemas.ai_task import AiTaskResponse
from app.schemas.sport_plan import PlanGenerationRequest
from app.services.ai_task_service import task_store

router = APIRouter(dependencies=[Depends(verify_internal_token)])


@router.post("/plan-generation", response_model=AiTaskResponse)
def plan_generation(request: PlanGenerationRequest) -> AiTaskResponse:
    response = agent_registry.plan_generation.run(request)
    task_store.save(response)
    return response


@router.get("/tasks/{task_id}", response_model=AiTaskResponse)
def get_task(task_id: str) -> AiTaskResponse:
    task = task_store.get(task_id)
    if task is None:
        raise HTTPException(status_code=404, detail="AI task not found")
    return task


@router.post("/tasks/{task_id}/retry", response_model=AiTaskResponse)
def retry_task(task_id: str) -> AiTaskResponse:
    task = task_store.get(task_id)
    if task is None:
        raise HTTPException(status_code=404, detail="AI task not found")
    retried = task.model_copy(update={"status": "PENDING_RETRY", "failureReason": None})
    task_store.save(retried)
    return retried

