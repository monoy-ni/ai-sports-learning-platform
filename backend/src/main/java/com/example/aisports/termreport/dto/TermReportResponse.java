package com.example.aisports.termreport.dto;

import com.example.aisports.termreport.domain.ReportStatus;

public record TermReportResponse(Long reportId, ReportStatus status, Object aiTask, String finalContent) {
}
