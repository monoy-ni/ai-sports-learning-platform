from app.schemas.ai_task import AiTaskResponse


class InMemoryTaskStore:
    def __init__(self) -> None:
        self._tasks: dict[str, AiTaskResponse] = {}

    def save(self, task: AiTaskResponse) -> None:
        self._tasks[task.taskId] = task

    def get(self, task_id: str) -> AiTaskResponse | None:
        return self._tasks.get(task_id)


task_store = InMemoryTaskStore()

