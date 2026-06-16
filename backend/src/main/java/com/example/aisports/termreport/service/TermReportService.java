package com.example.aisports.termreport.service;

import com.example.aisports.airecord.client.AiServiceClient;
import com.example.aisports.airecord.dto.AiTaskResponse;
import com.example.aisports.airecord.service.AiRecordService;
import com.example.aisports.termreport.domain.ReportStatus;
import com.example.aisports.termreport.domain.TermReport;
import com.example.aisports.termreport.dto.GenerateReportRequest;
import com.example.aisports.termreport.dto.TermReportResponse;
import com.example.aisports.termreport.dto.UpdateReportRequest;
import com.example.aisports.termreport.repository.TermReportRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class TermReportService {
    private final TermReportRepository repository;
    private final AiServiceClient aiServiceClient;
    private final AiRecordService aiRecordService;
    private final ObjectMapper objectMapper;

    public TermReportService(TermReportRepository repository, AiServiceClient aiServiceClient, AiRecordService aiRecordService, ObjectMapper objectMapper) {
        this.repository = repository;
        this.aiServiceClient = aiServiceClient;
        this.aiRecordService = aiRecordService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public TermReportResponse generate(GenerateReportRequest request) {
        Map<String, Object> payload = Map.of(
            "studentId", request.studentId(),
            "termId", "2026-Spring",
            "teacherNote", request.teacherNote() == null ? "" : request.teacherNote(),
            "inputVersion", "v0.1.0"
        );
        AiTaskResponse aiTask = aiServiceClient.generateTermReport(payload);
        aiRecordService.save(payload, aiTask);
        TermReport report = new TermReport();
        report.setStudentId(request.studentId());
        report.setTermId("2026-Spring");
        report.setStatus(ReportStatus.DRAFT);
        report.setAiDraft(toJson(aiTask.result()));
        report.setFinalContent("AI 报告草稿待教师审核");
        TermReport saved = repository.save(report);
        return new TermReportResponse(saved.getId(), saved.getStatus(), aiTask, saved.getFinalContent());
    }

    @Transactional(readOnly = true)
    public TermReportResponse get(Long reportId) {
        TermReport report = repository.findById(reportId)
            .orElseThrow(() -> new IllegalArgumentException("报告不存在"));
        return new TermReportResponse(report.getId(), report.getStatus(), null, report.getFinalContent());
    }

    @Transactional
    public TermReportResponse update(Long reportId, UpdateReportRequest request) {
        TermReport report = repository.findById(reportId)
            .orElseThrow(() -> new IllegalArgumentException("报告不存在"));
        report.setFinalContent(request.finalContent());
        report.setTeacherReviewNote(request.teacherReviewNote());
        TermReport saved = repository.save(report);
        return new TermReportResponse(saved.getId(), saved.getStatus(), null, saved.getFinalContent());
    }

    @Transactional
    public TermReportResponse approve(Long reportId) {
        TermReport report = repository.findById(reportId)
            .orElseThrow(() -> new IllegalArgumentException("报告不存在"));
        report.setStatus(ReportStatus.APPROVED);
        TermReport saved = repository.save(report);
        return new TermReportResponse(saved.getId(), saved.getStatus(), null, saved.getFinalContent());
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            return "{}";
        }
    }
}
