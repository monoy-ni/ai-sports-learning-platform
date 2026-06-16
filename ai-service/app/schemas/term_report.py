from app.schemas.common import FlexibleRequest


class TermReportRequest(FlexibleRequest):
    termId: str = "2026-Spring"
    teacherNote: str = ""
    checkInSummary: str | None = None
    campusRunScore: float | None = None
    healthChangeSummary: str | None = None

