package com.example.aisports.termreport.controller;

import com.example.aisports.common.response.ApiResponse;
import com.example.aisports.common.security.UserPrincipal;
import com.example.aisports.termreport.dto.BatchGenerateRequest;
import com.example.aisports.termreport.dto.GenerateReportRequest;
import com.example.aisports.termreport.dto.TermReportEditHistoryItem;
import com.example.aisports.termreport.dto.TermReportResponse;
import com.example.aisports.termreport.dto.TermReportSummary;
import com.example.aisports.termreport.dto.UpdateReportRequest;
import com.example.aisports.termreport.service.TermReportService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teacher/reports")
public class TermReportController {
    private final TermReportService service;

    public TermReportController(TermReportService service) {
        this.service = service;
    }

    @PostMapping("/generate")
    public ApiResponse<TermReportResponse> generate(
        @AuthenticationPrincipal UserPrincipal principal,
        @Valid @RequestBody GenerateReportRequest request) {
        return ApiResponse.ok(service.generate(request, principal));
    }

    @PostMapping("/generate-batch")
    public ApiResponse<List<TermReportResponse>> generateBatch(
        @AuthenticationPrincipal UserPrincipal principal,
        @Valid @RequestBody BatchGenerateRequest request) {
        return ApiResponse.ok(service.generateBatch(request, principal));
    }

    @GetMapping
    public ApiResponse<List<TermReportSummary>> list(
        @AuthenticationPrincipal UserPrincipal principal,
        @RequestParam(required = false) Long termId,
        @RequestParam(required = false) Long classId,
        @RequestParam(required = false) String status) {
        return ApiResponse.ok(service.list(principal.id(), termId, classId, status));
    }

    @GetMapping("/{reportId}")
    public ApiResponse<TermReportResponse> get(@PathVariable Long reportId) {
        return ApiResponse.ok(service.get(reportId));
    }

    @PutMapping("/{reportId}")
    public ApiResponse<TermReportResponse> update(
        @AuthenticationPrincipal UserPrincipal principal,
        @PathVariable Long reportId,
        @Valid @RequestBody UpdateReportRequest request) {
        return ApiResponse.ok(service.update(reportId, request, principal));
    }

    @PostMapping("/{reportId}/approve")
    public ApiResponse<TermReportResponse> approve(
        @AuthenticationPrincipal UserPrincipal principal,
        @PathVariable Long reportId) {
        return ApiResponse.ok(service.approve(reportId, principal));
    }

    @GetMapping("/{reportId}/edit-history")
    public ApiResponse<List<TermReportEditHistoryItem>> editHistory(@PathVariable Long reportId) {
        return ApiResponse.ok(service.editHistory(reportId));
    }
}
