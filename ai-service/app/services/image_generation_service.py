from app.core.config import settings


class ImageGenerationService:
    def generate(self, prompt: str) -> dict[str, str | None]:
        return {
            "provider": settings.ai_image_provider,
            "status": "MOCK_SUCCESS",
            "imageUrl": None,
            "prompt": prompt,
        }

