from app.agents.base import BaseAgent
from app.schemas.ai_task import AiTaskResponse
from app.schemas.common import AgentType
from app.schemas.sport_plan import PlanGenerationRequest


class PlanGenerationAgent(BaseAgent[PlanGenerationRequest]):
    def run(self, request: PlanGenerationRequest) -> AiTaskResponse:
        result = {
            "termId": request.termId,
            "weeklyPlans": [
                {
                    "week": 1,
                    "goal": "建立跑步习惯和心肺适应",
                    "sessions": 3,
                    "runningType": "普通跑",
                    "paceRange": "6'50\"-7'30\"/km",
                    "durationMinutes": "20-25",
                    "safetyReminder": "运动前热身 8 分钟，结束后拉伸。",
                },
                {
                    "week": 2,
                    "goal": "提升稳定配速能力",
                    "sessions": 3,
                    "runningType": "节奏跑 + 普通跑",
                    "paceRange": "6'30\"-7'10\"/km",
                    "durationMinutes": "25-30",
                    "safetyReminder": "疲劳高时降低强度。",
                },
                {
                    "week": 3,
                    "goal": "引入 Léger/折返跑能力训练",
                    "sessions": 4,
                    "runningType": "间歇跑 + 折返跑",
                    "paceRange": "按阶段递进",
                    "durationMinutes": "30-35",
                    "safetyReminder": "关注膝踝不适，不追求一次到位。",
                },
            ],
            "rainyDayAlternative": ["原地高抬腿", "开合跳", "核心训练", "动态拉伸", "低冲击有氧"],
            "medicalDisclaimer": "AI 建议不能替代医学诊断，存在疾病或明显风险时请咨询医生或教师。",
        }
        return AiTaskResponse(agentType=AgentType.PLAN_GENERATION, inputVersion=request.inputVersion, result=result)

