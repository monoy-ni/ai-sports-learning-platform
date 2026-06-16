from app.core.config import settings


class ModelClient:
    def model_name(self) -> str:
        return settings.ai_model_name

