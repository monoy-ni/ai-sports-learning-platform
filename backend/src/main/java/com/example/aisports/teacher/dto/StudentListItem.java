package com.example.aisports.teacher.dto;

import java.math.BigDecimal;

public record StudentListItem(
    Long id,
    String name,
    String studentNumber,
    String className,
    BigDecimal bmi,
    String bmiCategory,
    BigDecimal campusRunScore,
    String checkInStatus,
    String reportStatus,
    int riskFlagCount
) {
}
