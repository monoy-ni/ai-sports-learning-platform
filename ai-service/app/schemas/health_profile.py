from pydantic import BaseModel


class HealthProfileInput(BaseModel):
    gender: str | None = None
    age: int | None = None
    heightCm: float | None = None
    weightKg: float | None = None
    bmi: float | None = None
    vitalCapacity: int | None = None
    diseaseStatus: str | None = None
    diseaseNote: str | None = None
    sportGoal: str | None = None
    weeklyFrequency: int | None = None
    bodyType: str | None = None

