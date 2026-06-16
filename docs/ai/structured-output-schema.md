# 结构化输出规范

每个 AI 任务返回：

- `taskId`
- `agentType`
- `status`
- `modelName`
- `inputVersion`
- `generatedAt`
- `result`
- `failureReason`

`result` 按 Agent 类型变化，但必须是可 JSON 序列化的数据。
