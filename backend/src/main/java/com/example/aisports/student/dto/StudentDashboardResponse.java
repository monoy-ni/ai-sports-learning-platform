package com.example.aisports.student.dto;

import java.util.List;

public record StudentDashboardResponse(
    List<DashboardMetric> metrics,
    List<String> weeklyPlan,
    String todayAdvice
) {
}

