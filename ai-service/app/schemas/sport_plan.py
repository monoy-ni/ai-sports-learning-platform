from app.schemas.common import FlexibleRequest
from app.schemas.health_profile import HealthProfileInput


class PlanGenerationRequest(FlexibleRequest):
    termId: str = "2026-Spring"
    profile: HealthProfileInput | None = None
    sportGoal: str | None = None
    weeklyFrequency: int | None = None

