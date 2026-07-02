package com.example.aisports.termreport.dto;

import jakarta.validation.constraints.NotNull;

public record GenerateReportRequest(@NotNull Long studentId, Long termId, String teacherNote) {
}
