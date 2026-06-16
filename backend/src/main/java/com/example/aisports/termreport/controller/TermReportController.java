package com.example.aisports.termreport.controller;

import com.example.aisports.common.response.ApiResponse;
import com.example.aisports.termreport.dto.GenerateReportRequest;
import com.example.aisports.termreport.dto.TermReportResponse;
import com.example.aisports.termreport.dto.UpdateReportRequest;
import com.example.aisports.termreport.service.TermReportService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teacher/reports")
public class TermReportController {
    private final TermReportService service;

    public TermReportController(TermReportService service) {
        this.service = service;
    }

    @PostMapping("/generate")
    public ApiResponse<TermReportResponse> generate(@Valid @RequestBody GenerateReportRequest request) {
        return ApiResponse.ok(service.generate(request));
    }

    @GetMapping("/{reportId}")
    public ApiResponse<TermReportResponse> get(@PathVariable Long reportId) {
        return ApiResponse.ok(service.get(reportId));
    }

    @PutMapping("/{reportId}")
    public ApiResponse<TermReportResponse> update(@PathVariable Long reportId, @Valid @RequestBody UpdateReportRequest request) {
        return ApiResponse.ok(service.update(reportId, request));
    }

    @PostMapping("/{reportId}/approve")
    public ApiResponse<TermReportResponse> approve(@PathVariable Long reportId) {
        return ApiResponse.ok(service.approve(reportId));
    }
}
