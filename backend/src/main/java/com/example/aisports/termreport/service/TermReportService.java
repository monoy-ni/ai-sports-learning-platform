package com.example.aisports.termreport.service;

import com.example.aisports.airecord.client.AiServiceClient;
import com.example.aisports.airecord.dto.AiTaskResponse;
import com.example.aisports.airecord.service.AiRecordService;
import com.example.aisports.auth.domain.UserAccount;
import com.example.aisports.auth.repository.UserAccountRepository;
import com.example.aisports.campusrun.domain.CampusRunRecord;
import com.example.aisports.campusrun.domain.CampusRunScoreRule;
import com.example.aisports.campusrun.repository.CampusRunRecordRepository;
import com.example.aisports.campusrun.repository.CampusRunScoreRuleRepository;
import com.example.aisports.campusrun.service.CampusRunScoreService;
import com.example.aisports.checkin.domain.CheckIn;
import com.example.aisports.checkin.repository.CheckInRepository;
import com.example.aisports.common.exception.BusinessException;
import com.example.aisports.common.security.UserPrincipal;
import com.example.aisports.common.validation.BmiCategoryUtil;
import com.example.aisports.healthprofile.domain.HealthProfile;
import com.example.aisports.healthprofile.repository.HealthProfileRepository;
import com.example.aisports.sportplan.domain.SportPlan;
import com.example.aisports.sportplan.repository.SportPlanRepository;
import com.example.aisports.teacher.service.TeacherDataService;
import com.example.aisports.term.domain.Term;
import com.example.aisports.termreport.domain.ReportStatus;
import com.example.aisports.termreport.domain.TermReport;
import com.example.aisports.termreport.domain.TermReportEditHistory;
import com.example.aisports.termreport.dto.BatchGenerateRequest;
import com.example.aisports.termreport.dto.GenerateReportRequest;
import com.example.aisports.termreport.dto.TermReportEditHistoryItem;
import com.example.aisports.termreport.dto.TermReportResponse;
import com.example.aisports.termreport.dto.TermReportSummary;
import com.example.aisports.termreport.dto.UpdateReportRequest;
import com.example.aisports.termreport.repository.TermReportEditHistoryRepository;
import com.example.aisports.termreport.repository.TermReportRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class TermReportService {
    private final TermReportRepository repository;
    private final TermReportEditHistoryRepository historyRepository;
    private final AiServiceClient aiServiceClient;
    private final AiRecordService aiRecordService;
    private final TeacherDataService dataService;
    private final CheckInRepository checkInRepository;
    private final HealthProfileRepository profileRepository;
    private final SportPlanRepository sportPlanRepository;
    private final CampusRunRecordRepository campusRunRepository;
    private final CampusRunScoreRuleRepository ruleRepository;
    private final CampusRunScoreService campusRunScoreService;
    private final UserAccountRepository userRepository;
    private final ObjectMapper objectMapper;

    public TermReportService(TermReportRepository repository,
                             TermReportEditHistoryRepository historyRepository,
                             AiServiceClient aiServiceClient,
                             AiRecordService aiRecordService,
                             TeacherDataService dataService,
                             CheckInRepository checkInRepository,
                             HealthProfileRepository profileRepository,
                             SportPlanRepository sportPlanRepository,
                             CampusRunRecordRepository campusRunRepository,
                             CampusRunScoreRuleRepository ruleRepository,
                             CampusRunScoreService campusRunScoreService,
                             UserAccountRepository userRepository,
                             ObjectMapper objectMapper) {
        this.repository = repository;
        this.historyRepository = historyRepository;
        this.aiServiceClient = aiServiceClient;
        this.aiRecordService = aiRecordService;
        this.dataService = dataService;
        this.checkInRepository = checkInRepository;
        this.profileRepository = profileRepository;
        this.sportPlanRepository = sportPlanRepository;
        this.campusRunRepository = campusRunRepository;
        this.ruleRepository = ruleRepository;
        this.campusRunScoreService = campusRunScoreService;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public TermReportResponse generate(GenerateReportRequest request, UserPrincipal editor) {
        Term term = dataService.resolveTerm(request.termId());
        Map<String, Object> payload = buildPayload(request.studentId(), term, request.teacherNote());
        AiTaskResponse aiTask = aiServiceClient.generateTermReport(payload);
        aiRecordService.save(payload, aiTask);

        String aiDraft = toJson(aiTask.result());
        String finalContent = renderFinalContent(aiTask.result());
        // 复用已有草稿, 否则新建
        TermReport report = repository.findByStudentIdAndTermId(request.studentId(), term.getTermCode())
            .orElseGet(TermReport::new);
        boolean isNew = report.getId() == null;
        if (isNew) {
            report.setStudentId(request.studentId());
            report.setTermId(term.getTermCode());
        }
        report.setStatus(ReportStatus.DRAFT);
        report.setAiDraft(aiDraft);
        report.setFinalContent(finalContent);
        repository.save(report);

        recordHistory(report.getId(), editor, null, finalContent, null, null, "GENERATE");
        return toResponse(report);
    }

    @Transactional
    public List<TermReportResponse> generateBatch(BatchGenerateRequest request, UserPrincipal editor) {
        List<TermReportResponse> results = new ArrayList<>();
        for (Long sid : request.studentIds()) {
            GenerateReportRequest single = new GenerateReportRequest(sid, request.termId(), request.teacherNote());
            results.add(generate(single, editor));
        }
        return results;
    }

    @Transactional(readOnly = true)
    public TermReportResponse get(Long reportId) {
        TermReport report = load(reportId);
        return toResponse(report);
    }

    @Transactional(readOnly = true)
    public List<TermReportSummary> list(Long teacherId, Long termId, Long classId, String status) {
        Term term = dataService.resolveTerm(termId);
        List<Long> studentIds = dataService.resolveStudentIds(teacherId, classId, term.getId());
        if (studentIds.isEmpty()) return List.of();
        Map<Long, String> names = dataService.loadUsers(studentIds).entrySet().stream()
            .collect(java.util.stream.Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getDisplayName()));
        List<TermReport> reports = repository.findByStudentIdInAndTermId(studentIds, term.getTermCode());
        return reports.stream()
            .filter(r -> status == null || status.isBlank() || r.getStatus().name().equalsIgnoreCase(status))
            .map(r -> TermReportSummary.from(r, names.getOrDefault(r.getStudentId(), "")))
            .toList();
    }

    @Transactional
    public TermReportResponse update(Long reportId, UpdateReportRequest request, UserPrincipal editor) {
        TermReport report = load(reportId);
        String oldContent = report.getFinalContent();
        String oldNote = report.getTeacherReviewNote();
        report.setFinalContent(request.finalContent());
        report.setTeacherReviewNote(request.teacherReviewNote());
        repository.save(report);
        // aiDraft 不动 (保留 AI 原文)
        recordHistory(reportId, editor, oldContent, request.finalContent(), oldNote, request.teacherReviewNote(), "EDIT");
        return toResponse(report);
    }

    @Transactional
    public TermReportResponse approve(Long reportId, UserPrincipal editor) {
        TermReport report = load(reportId);
        String oldContent = report.getFinalContent();
        String oldNote = report.getTeacherReviewNote();
        report.setStatus(ReportStatus.APPROVED);
        repository.save(report);
        recordHistory(reportId, editor, oldContent, oldContent, oldNote, oldNote, "APPROVE");
        return toResponse(report);
    }

    @Transactional(readOnly = true)
    public List<TermReportEditHistoryItem> editHistory(Long reportId) {
        return historyRepository.findByReportIdOrderByCreatedAtDesc(reportId).stream()
            .map(TermReportEditHistoryItem::from).toList();
    }

    // ---- 聚合 payload (转发给 3 号 AI) ----

    private Map<String, Object> buildPayload(Long studentId, Term term, String teacherNote) {
        UserAccount user = userRepository.findById(studentId).orElse(null);
        HealthProfile profile = profileRepository.findByStudentId(studentId).orElse(null);
        SportPlan plan = sportPlanRepository.findFirstByStudentIdAndActiveTrueOrderByGeneratedAtDesc(studentId).orElse(null);
        LocalDate today = LocalDate.now();
        LocalDate rangeEnd = term.getEndDate().isBefore(today) ? term.getEndDate() : today;
        List<CheckIn> checkIns = checkInRepository
            .findByStudentIdInAndCheckInDateBetweenOrderByStudentIdAscCheckInDateAsc(
                List.of(studentId), term.getStartDate(), rangeEnd);
        List<CampusRunRecord> runRecords = campusRunRepository.findByStudentIdOrderByCreatedAtDesc(studentId);
        CampusRunScoreRule rule = ruleRepository.findByTermIdAndIsActiveTrue(term.getId()).orElse(null);
        BigDecimal score = campusRunScoreService.compute(term, rule, runRecords).score();

        long total = checkIns.size();
        long abnormal = checkIns.stream().filter(c -> Boolean.TRUE.equals(c.getAbnormal())).count();
        long completed = checkIns.stream().filter(c -> Boolean.TRUE.equals(c.getCompletedPlan())).count();
        double avgDur = checkIns.stream().filter(c -> c.getDurationMinutes() != null)
            .mapToInt(CheckIn::getDurationMinutes).average().orElse(0);

        String checkInSummary = String.format("总打卡 %d 次, 平均时长 %.0f 分钟, 异常 %d 条", total, avgDur, abnormal);
        String healthChangeSummary = profile == null ? "无健康档案"
            : String.format("BMI %.1f (%s), 肺活量 %d, 疾病备注: %s",
                profile.getBmi(), BmiCategoryUtil.classify(profile.getBmi()).getLabel(),
                profile.getVitalCapacity(),
                profile.getDiseaseStatus() == null ? "无" : profile.getDiseaseStatus().name());

        Map<String, Object> planCompletion = new LinkedHashMap<>();
        planCompletion.put("activePlan", plan != null);
        planCompletion.put("weeklyFrequency", profile == null ? null : profile.getWeeklyFrequency());
        planCompletion.put("completedPlanRatio", total == 0 ? 0 : Math.round(completed * 100.0 / total) / 100.0);

        List<Map<String, Object>> abnormalRecords = checkIns.stream()
            .filter(c -> Boolean.TRUE.equals(c.getAbnormal()))
            .map(c -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("date", c.getCheckInDate().toString());
                m.put("reason", c.getAbnormalReason());
                m.put("exerciseType", c.getExerciseType() == null ? null : c.getExerciseType().name());
                return m;
            }).toList();

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("studentId", studentId);
        payload.put("studentName", user == null ? null : user.getDisplayName());
        payload.put("termId", term.getTermCode());
        payload.put("teacherNote", teacherNote == null ? "" : teacherNote);
        payload.put("inputVersion", "v0.1.0");
        payload.put("checkInSummary", checkInSummary);
        payload.put("campusRunScore", score.doubleValue());
        payload.put("healthChangeSummary", healthChangeSummary);
        payload.put("planCompletion", planCompletion);
        payload.put("abnormalRecords", abnormalRecords);
        payload.put("bmiCategory", profile == null ? null : BmiCategoryUtil.classify(profile.getBmi()).name());
        return payload;
    }

    private String renderFinalContent(Map<String, Object> result) {
        if (result == null || result.isEmpty()) return "AI 报告草稿待教师审核";
        StringBuilder sb = new StringBuilder();
        section(sb, "基础健康概况", result.get("healthOverview"));
        section(sb, "运动参与情况", result.get("participation"));
        section(sb, "计划执行情况", result.get("planExecution"));
        section(sb, "校园跑成绩分析", result.get("campusRunAnalysis"));
        section(sb, "健康数据变化", result.get("healthChange"));
        section(sb, "风险与不足", result.get("riskAndWeakness"));
        section(sb, "综合评价", result.get("overallEvaluation"));
        section(sb, "后续运动建议", result.get("followUpSuggestion"));
        section(sb, "教师审核意见", result.get("teacherReviewSlot"));
        return sb.toString();
    }

    private void section(StringBuilder sb, String title, Object body) {
        if (body == null) return;
        sb.append("【").append(title).append("】").append(body).append("\n");
    }

    private void recordHistory(Long reportId, UserPrincipal editor,
                               String oldContent, String newContent,
                               String oldNote, String newNote, String action) {
        TermReportEditHistory h = new TermReportEditHistory();
        h.setReportId(reportId);
        h.setEditorId(editor.id());
        h.setOldFinalContent(oldContent);
        h.setNewFinalContent(newContent);
        h.setOldTeacherReviewNote(oldNote);
        h.setNewTeacherReviewNote(newNote);
        h.setAction(action);
        historyRepository.save(h);
    }

    private TermReport load(Long reportId) {
        return repository.findById(reportId)
            .orElseThrow(() -> new BusinessException("REPORT_NOT_FOUND", "报告不存在"));
    }

    private TermReportResponse toResponse(TermReport r) {
        return new TermReportResponse(r.getId(), r.getStudentId(), r.getStatus(),
            r.getAiDraft(), r.getFinalContent(), r.getTeacherReviewNote());
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            return "{}";
        }
    }
}
