import datetime as dt
from pydantic import BaseModel


class CheckInInput(BaseModel):
    checkInId: int | None = None
    date: dt.date | None = None
    weather: str | None = None
    exerciseType: str | None = None
    durationMinutes: int | None = None
    distanceKm: float | None = None
    paceMinutesPerKm: float | None = None
    fatigueLevel: int | None = None
    feeling: str | None = None
    completedPlan: bool | None = None
