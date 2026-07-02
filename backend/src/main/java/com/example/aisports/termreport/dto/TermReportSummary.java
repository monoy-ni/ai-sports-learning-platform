package com.example.aisports.termreport.dto;

import com.example.aisports.termreport.domain.ReportStatus;
import com.example.aisports.termreport.domain.TermReport;

import java.time.Instant;

public record TermReportSummary(
    Long reportId,
    Long studentId,
    String studentName,
    ReportStatus status,
    Instant updatedAt
) {
    public static TermReportSummary from(TermReport r, String studentName) {
        return new TermReportSummary(r.getId(), r.getStudentId(), studentName, r.getStatus(), r.getUpdatedAt());
    }
}
