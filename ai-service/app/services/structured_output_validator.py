from app.schemas.ai_task import AiTaskResponse


class StructuredOutputValidator:
    def validate(self, response: AiTaskResponse) -> AiTaskResponse:
        return response

