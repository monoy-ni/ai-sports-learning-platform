package com.example.aisports.teacher.dto;

import com.example.aisports.student.dto.DashboardMetric;

import java.util.List;

public record TeacherDashboardResponse(
    List<DashboardMetric> metrics,
    List<RiskStudent> riskStudents
) {
    public record RiskStudent(Long id, String name, String reason, String status) {
    }
}

