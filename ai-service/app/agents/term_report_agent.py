from app.agents.base import BaseAgent
from app.schemas.ai_task import AiTaskResponse
from app.schemas.common import AgentType
from app.schemas.term_report import TermReportRequest


class TermReportAgent(BaseAgent[TermReportRequest]):
    def run(self, request: TermReportRequest) -> AiTaskResponse:
        result = {
            "termId": request.termId,
            "healthOverview": "本学期基础健康数据整体稳定。",
            "participation": "运动参与度良好，能完成主要打卡任务。",
            "planExecution": "学期计划执行情况中等偏好。",
            "campusRunAnalysis": "校园跑成绩具备提升趋势。",
            "riskAndWeakness": "需继续关注疲劳反馈和异常数据。",
            "overallEvaluation": "建议保持规律运动，逐步提升跑步专项能力。",
            "teacherReviewSlot": request.teacherNote or "待教师补充审核意见。",
            "medicalDisclaimer": "本报告为 AI 草稿，不替代教师最终评价或医学诊断。",
        }
        return AiTaskResponse(agentType=AgentType.TERM_REPORT, inputVersion=request.inputVersion, result=result)

