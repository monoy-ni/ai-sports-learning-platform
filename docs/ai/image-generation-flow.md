# 生图流程

1 号 Agent 将运动卡片提示词生成委托给 `image_card_agent`。

图片生成失败不会导致打卡或每日文字分析失败。响应中保留图片状态，例如 `FAILED` 或 `PENDING_RETRY`。
