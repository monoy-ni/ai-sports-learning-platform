from app.schemas.check_in import CheckInInput
from app.schemas.common import FlexibleRequest
from app.schemas.health_profile import HealthProfileInput


class DailyAnalysisRequest(FlexibleRequest):
    profile: HealthProfileInput | None = None
    checkIn: CheckInInput | None = None
    recentSummary: str | None = None
    currentPlan: dict | None = None
    weather: str | None = None
    fatigueLevel: int | None = None

