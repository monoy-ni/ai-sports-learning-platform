package com.example.aisports.teacher.dto;

import com.example.aisports.campusrun.dto.CampusRunScoreResponse;
import com.example.aisports.checkin.dto.CheckInListItem;
import com.example.aisports.healthprofile.dto.HealthProfileResponse;
import com.example.aisports.sportplan.dto.CurrentSportPlanResponse;

import java.math.BigDecimal;
import java.util.List;

public record StudentDetailResponse(
    Long id,
    String name,
    String studentNumber,
    String className,
    HealthProfileResponse healthProfile,
    CurrentSportPlanResponse sportPlan,
    List<CheckInListItem> recentCheckIns,
    CampusRunScoreResponse campusRunScore,
    HealthTrend healthTrend,
    AiDailySuggestion aiDailySuggestion,
    List<RiskFlag> riskFlags,
    String teacherNote
) {
    public record RiskFlag(String type, String label, String severity) {
    }

    public record HealthTrend(List<TrendPoint> points) {
        public record TrendPoint(String date, Integer durationMinutes, BigDecimal distanceKm) {
        }
    }

    public record AiDailySuggestion(String summary, List<String> suggestions, String imageStatus, String imageUrl) {
    }
}
