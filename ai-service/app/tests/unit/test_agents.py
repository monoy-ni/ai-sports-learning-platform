from app.agents.registry import agent_registry
from app.schemas.common import AgentType
from app.schemas.daily_analysis import DailyAnalysisRequest
from app.schemas.sport_plan import PlanGenerationRequest
from app.schemas.term_report import TermReportRequest


def test_daily_analysis_returns_structured_result():
    result = agent_registry.daily_analysis.run(DailyAnalysisRequest(studentId=1, weather="RAINY", fatigueLevel=6))

    assert result.agentType == AgentType.DAILY_ANALYSIS
    assert result.status == "SUCCESS"
    assert "riskReminder" in result.result
    assert result.result["imageStatus"] == "MOCK_SUCCESS"


def test_plan_generation_returns_weekly_plan():
    result = agent_registry.plan_generation.run(PlanGenerationRequest(studentId=1))

    assert result.agentType == AgentType.PLAN_GENERATION
    assert len(result.result["weeklyPlans"]) >= 1


def test_term_report_returns_draft_sections():
    result = agent_registry.term_report.run(TermReportRequest(studentId=1))

    assert result.agentType == AgentType.TERM_REPORT
    assert "overallEvaluation" in result.result

