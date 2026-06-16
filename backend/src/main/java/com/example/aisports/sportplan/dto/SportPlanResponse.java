package com.example.aisports.sportplan.dto;

import com.example.aisports.airecord.dto.AiTaskResponse;

public record SportPlanResponse(Long planId, AiTaskResponse aiTask) {
}

