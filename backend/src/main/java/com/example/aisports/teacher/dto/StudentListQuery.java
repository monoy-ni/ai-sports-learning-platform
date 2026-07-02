package com.example.aisports.teacher.dto;

import java.math.BigDecimal;

public record StudentListQuery(
    Long classId,
    Long termId,
    String q,
    String bmiCategory,
    BigDecimal scoreMin,
    BigDecimal scoreMax,
    Boolean healthRisk,
    String checkInStatus,   // ACTIVE | MISSED | LOW_VOLUME | null
    String reportStatus,    // DRAFT | APPROVED | NONE | null
    String sort,            // NAME | STUDENT_NUMBER | BMI | SCORE
    String dir              // ASC | DESC
) {
}
