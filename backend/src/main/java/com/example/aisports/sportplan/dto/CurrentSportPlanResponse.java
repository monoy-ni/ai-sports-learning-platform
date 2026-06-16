package com.example.aisports.sportplan.dto;

import com.example.aisports.sportplan.domain.SportPlan;

public record CurrentSportPlanResponse(Long planId, String termId, String generationStatus, String weeklyPlan) {
    public static CurrentSportPlanResponse from(SportPlan plan) {
        return new CurrentSportPlanResponse(plan.getId(), plan.getTermId(), plan.getGenerationStatus(), plan.getWeeklyPlan());
    }
}

