from datetime import datetime, timezone
from typing import Any
from uuid import uuid4

from pydantic import BaseModel, Field

from app.core.config import settings
from app.schemas.common import AgentType, AiTaskStatus


class AiTaskResponse(BaseModel):
    taskId: str = Field(default_factory=lambda: f"ai_{uuid4().hex}")
    agentType: AgentType
    status: AiTaskStatus = AiTaskStatus.SUCCESS
    modelName: str = settings.ai_model_name
    inputVersion: str = "v0.1.0"
    generatedAt: datetime = Field(default_factory=lambda: datetime.now(timezone.utc))
    result: dict[str, Any]
    failureReason: str | None = None

