package com.example.aisports.termreport.dto;

import com.example.aisports.termreport.domain.ReportStatus;

public record TermReportResponse(
    Long reportId,
    Long studentId,
    ReportStatus status,
    String aiDraft,
    String finalContent,
    String teacherReviewNote
) {
}
