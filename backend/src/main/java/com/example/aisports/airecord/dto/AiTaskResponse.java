package com.example.aisports.airecord.dto;

import com.example.aisports.airecord.domain.AiAgentType;
import com.example.aisports.airecord.domain.AiTaskStatus;

import java.time.Instant;
import java.util.Map;

public record AiTaskResponse(
    String taskId,
    AiAgentType agentType,
    AiTaskStatus status,
    String modelName,
    String inputVersion,
    Instant generatedAt,
    Map<String, Object> result,
    String failureReason
) {
}

