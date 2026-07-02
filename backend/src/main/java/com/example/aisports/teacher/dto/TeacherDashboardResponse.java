package com.example.aisports.teacher.dto;

import com.example.aisports.student.dto.DashboardMetric;

import java.util.List;

public record TeacherDashboardResponse(
    List<DashboardMetric> metrics,
    ExerciseDashboard exercise,
    BmiDistribution bmiDistribution,
    VitalCapacityDistribution vitalCapacity,
    List<RiskStudent> consecutiveMissStudents,
    List<RiskStudent> lowVolumeStudents,
    List<RiskStudent> abnormalDataStudents,
    List<RiskStudent> healthRiskStudents
) {
    public record RiskStudent(Long id, String name, String reason, String status) {
    }

    public record ExerciseDashboard(
        long totalCheckIns,
        double avgDurationMinutes,
        double avgDistanceKm,
        double avgPaceMinutesPerKm,
        double planCompletionRate
    ) {
    }

    public record BmiDistribution(long underweight, long normal, long overweight, long obese) {
    }

    public record VitalCapacityDistribution(long excellent, long pass, long fail) {
    }
}
