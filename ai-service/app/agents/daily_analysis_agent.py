from app.agents.base import BaseAgent
from app.agents.subagents.image_card_agent import ImageCardAgent
from app.schemas.ai_task import AiTaskResponse
from app.schemas.common import AgentType
from app.schemas.daily_analysis import DailyAnalysisRequest


class DailyAnalysisAgent(BaseAgent[DailyAnalysisRequest]):
    def __init__(self, image_card_agent: ImageCardAgent):
        self.image_card_agent = image_card_agent

    def run(self, request: DailyAnalysisRequest) -> AiTaskResponse:
        image_result = self.image_card_agent.create_prompt(request.studentId, request.weather, request.fatigueLevel)
        rainy = request.weather == "RAINY"
        result = {
            "summary": "本次训练已记录，整体完成度稳定。",
            "planMatch": "雨天已切换为室内无器械方案。" if rainy else "与本周跑步计划基本匹配。",
            "stretchingAdvice": ["小腿后侧拉伸 30 秒", "髂腰肌拉伸 30 秒", "臀部放松 30 秒"],
            "recoveryAdvice": "补水并保证睡眠，疲劳较高时降低下一次训练强度。",
            "riskReminder": "该建议不能替代医学诊断；如有不适请咨询教师或医生。",
            "nextSuggestion": "下一次训练保持中等强度，优先完成热身和拉伸。",
            **image_result,
        }
        return AiTaskResponse(agentType=AgentType.DAILY_ANALYSIS, inputVersion=request.inputVersion, result=result)

