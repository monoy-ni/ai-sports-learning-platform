from abc import ABC, abstractmethod
from typing import Generic, TypeVar

from app.schemas.ai_task import AiTaskResponse

RequestT = TypeVar("RequestT")


class BaseAgent(ABC, Generic[RequestT]):
    @abstractmethod
    def run(self, request: RequestT) -> AiTaskResponse:
        raise NotImplementedError

