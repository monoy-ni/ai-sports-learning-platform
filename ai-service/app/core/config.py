from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    model_config = SettingsConfigDict(env_file=".env", extra="ignore")

    internal_ai_service_token: str = "local-internal-ai-token"
    ai_model_name: str = "mock-deep-agent"
    ai_image_provider: str = "mock"
    cors_origins: list[str] = ["http://localhost:5173", "http://localhost:8080"]


settings = Settings()

