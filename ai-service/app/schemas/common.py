from enum import StrEnum
from pydantic import BaseModel, ConfigDict


class AgentType(StrEnum):
    DAILY_ANALYSIS = "DAILY_ANALYSIS"
    PLAN_GENERATION = "PLAN_GENERATION"
    TERM_REPORT = "TERM_REPORT"
    IMAGE_CARD = "IMAGE_CARD"


class AiTaskStatus(StrEnum):
    SUCCESS = "SUCCESS"
    FAILED = "FAILED"
    RUNNING = "RUNNING"
    PENDING_RETRY = "PENDING_RETRY"


class FlexibleRequest(BaseModel):
    model_config = ConfigDict(extra="allow")

    studentId: int | None = None
    inputVersion: str = "v0.1.0"

