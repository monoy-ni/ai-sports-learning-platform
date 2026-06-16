from app.schemas.ai_task import AiTaskResponse
from app.schemas.common import AgentType


class ImageCardAgent:
    def create_prompt(self, student_id: int | None, weather: str | None, fatigue_level: int | None) -> dict[str, str | None]:
        mood = "雨天室内训练" if weather == "RAINY" else "校园跑训练"
        prompt = f"为学生 {student_id or 'unknown'} 生成一张{mood}运动卡片，体现完成度、恢复建议和日期记录。"
        return {
            "imagePrompt": prompt,
            "imageStatus": "MOCK_SUCCESS",
            "imageUrl": None,
        }

    def run(self, student_id: int | None, weather: str | None, fatigue_level: int | None) -> AiTaskResponse:
        return AiTaskResponse(
            agentType=AgentType.IMAGE_CARD,
            result=self.create_prompt(student_id, weather, fatigue_level),
        )

