package com.example.aisports.termreport.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateReportRequest(@NotBlank String finalContent, String teacherReviewNote) {
}

