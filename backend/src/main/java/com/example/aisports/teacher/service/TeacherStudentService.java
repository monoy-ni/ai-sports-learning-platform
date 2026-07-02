package com.example.aisports.teacher.service;

import com.example.aisports.airecord.domain.AiAgentType;
import com.example.aisports.airecord.domain.AiRecord;
import com.example.aisports.airecord.repository.AiRecordRepository;
import com.example.aisports.auth.domain.UserAccount;
import com.example.aisports.campusrun.dto.CampusRunScoreResponse;
import com.example.aisports.campusrun.service.CampusRunScoreService;
import com.example.aisports.checkin.domain.CheckIn;
import com.example.aisports.checkin.dto.CheckInListItem;
import com.example.aisports.checkin.repository.CheckInRepository;
import com.example.aisports.common.exception.BusinessException;
import com.example.aisports.common.validation.BmiCategory;
import com.example.aisports.common.validation.BmiCategoryUtil;
import com.example.aisports.healthprofile.domain.DiseaseStatus;
import com.example.aisports.healthprofile.domain.HealthProfile;
import com.example.aisports.healthprofile.dto.HealthProfileResponse;
import com.example.aisports.sportplan.domain.SportPlan;
import com.example.aisports.sportplan.dto.CurrentSportPlanResponse;
import com.example.aisports.sportplan.repository.SportPlanRepository;
import com.example.aisports.teacher.domain.StudentTeacherNote;
import com.example.aisports.teacher.dto.StudentDetailResponse;
import com.example.aisports.teacher.dto.StudentListItem;
import com.example.aisports.teacher.dto.StudentListQuery;
import com.example.aisports.teacher.dto.StudentNoteRequest;
import com.example.aisports.teacher.dto.StudentNoteResponse;
import com.example.aisports.teacher.repository.StudentTeacherNoteRepository;
import com.example.aisports.termreport.domain.ReportStatus;
import com.example.aisports.termreport.domain.TermReport;
import com.example.aisports.termreport.repository.TermReportRepository;
import com.example.aisports.term.domain.Term;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TeacherStudentService {
    private static final int VITAL_CAPACITY_LOW = 2600;

    private final TeacherDataService data;
    private final CheckInRepository checkInRepository;
    private final SportPlanRepository sportPlanRepository;
    private final CampusRunScoreService campusRunScoreService;
    private final TermReportRepository termReportRepository;
    private final AiRecordRepository aiRecordRepository;
    private final StudentTeacherNoteRepository noteRepository;
    private final ObjectMapper objectMapper;

    public TeacherStudentService(TeacherDataService data,
                                 CheckInRepository checkInRepository,
                                 SportPlanRepository sportPlanRepository,
                                 CampusRunScoreService campusRunScoreService,
                                 TermReportRepository termReportRepository,
                                 AiRecordRepository aiRecordRepository,
                                 StudentTeacherNoteRepository noteRepository,
                                 ObjectMapper objectMapper) {
        this.data = data;
        this.checkInRepository = checkInRepository;
        this.sportPlanRepository = sportPlanRepository;
        this.campusRunScoreService = campusRunScoreService;
        this.termReportRepository = termReportRepository;
        this.aiRecordRepository = aiRecordRepository;
        this.noteRepository = noteRepository;
        this.objectMapper = objectMapper;
    }

    public List<StudentListItem> list(Long teacherId, StudentListQuery query) {
        Term term = data.resolveTerm(query.termId());
        List<Long> studentIds = data.resolveStudentIds(teacherId, query.classId(), term.getId());
        if (studentIds.isEmpty()) return List.of();

        Map<Long, UserAccount> users = data.loadUsers(studentIds);
        Map<Long, HealthProfile> profiles = data.loadProfiles(studentIds);
        Map<Long, String> classNames = data.loadClassNamesByStudent(studentIds, term.getId());
        LocalDate today = LocalDate.now();
        LocalDate rangeEnd = term.getEndDate().isBefore(today) ? term.getEndDate() : today;
        List<CheckIn> checkIns = data.loadCheckIns(studentIds, term.getStartDate(), rangeEnd);
        Map<Long, List<CheckIn>> cisByStudent = checkIns.stream()
            .collect(Collectors.groupingBy(CheckIn::getStudentId));
        List<TermReport> reports = termReportRepository.findByStudentIdInAndTermId(studentIds, term.getTermCode());
        Map<Long, TermReport> reportByStudent = reports.stream()
            .collect(Collectors.toMap(TermReport::getStudentId, Function.identity(), (a, b) -> a));

        List<StudentListItem> items = new ArrayList<>();
        for (Long sid : studentIds) {
            UserAccount u = users.get(sid);
            if (u == null) continue;
            HealthProfile p = profiles.get(sid);
            BigDecimal bmi = p == null ? null : p.getBmi();
            BmiCategory cat = BmiCategoryUtil.classify(bmi);
            List<CheckIn> cis = cisByStudent.getOrDefault(sid, List.of());
            BigDecimal score = avgScore(cis);
            String checkInStatus = checkInStatus(cis, today);
            String reportStatus = reportStatus(reportByStudent.get(sid));
            int riskCount = riskFlagsFor(sid, p, cis, today).size();

            items.add(new StudentListItem(
                sid, u.getDisplayName(), u.getUsername(), classNames.getOrDefault(sid, ""),
                bmi, cat.getLabel(), score, checkInStatus, reportStatus, riskCount));
        }

        return filterAndSort(items, query);
    }

    public StudentDetailResponse detail(Long studentId, Long termId) {
        Term term = data.resolveTerm(termId);
        UserAccount user = data.loadUsers(List.of(studentId)).get(studentId);
        if (user == null) throw new BusinessException("STUDENT_NOT_FOUND", "学生不存在");
        Map<Long, String> classNames = data.loadClassNamesByStudent(List.of(studentId), term.getId());
        HealthProfile profile = data.loadProfiles(List.of(studentId)).get(studentId);
        SportPlan plan = sportPlanRepository.findFirstByStudentIdAndActiveTrueOrderByGeneratedAtDesc(studentId).orElse(null);
        LocalDate today = LocalDate.now();
        LocalDate rangeEnd = term.getEndDate().isBefore(today) ? term.getEndDate() : today;
        List<CheckIn> checkIns = checkInRepository.findByStudentIdOrderByCheckInDateDesc(studentId).stream()
            .filter(c -> !c.getCheckInDate().isBefore(term.getStartDate()) && !c.getCheckInDate().isAfter(rangeEnd))
            .toList();
        CampusRunScoreResponse score = campusRunScoreService.currentScore(studentId, term.getId());
        StudentTeacherNote note = noteRepository.findByStudentIdAndTermId(studentId, term.getId()).orElse(null);

        List<StudentDetailResponse.RiskFlag> flags = riskFlagsFor(studentId, profile, checkIns, today);
        List<StudentDetailResponse.HealthTrend.TrendPoint> trend = buildTrend(checkIns, 30);
        StudentDetailResponse.AiDailySuggestion suggestion = loadDailySuggestion(studentId);

        return new StudentDetailResponse(
            studentId,
            user.getDisplayName(),
            user.getUsername(),
            classNames.getOrDefault(studentId, ""),
            profile == null ? null : HealthProfileResponse.from(profile),
            plan == null ? null : CurrentSportPlanResponse.from(plan),
            checkIns.stream().limit(15).map(CheckInListItem::from).toList(),
            score,
            new StudentDetailResponse.HealthTrend(trend),
            suggestion,
            flags,
            note == null ? "" : note.getContent()
        );
    }

    public StudentNoteResponse getNote(Long studentId, Long termId) {
        Term term = data.resolveTerm(termId);
        StudentTeacherNote note = noteRepository.findByStudentIdAndTermId(studentId, term.getId()).orElse(null);
        return new StudentNoteResponse(studentId, term.getId(), note == null ? "" : note.getContent());
    }

    public StudentNoteResponse updateNote(Long teacherId, Long studentId, StudentNoteRequest request) {
        Term term = data.resolveTerm(request.termId());
        StudentTeacherNote note = noteRepository.findByStudentIdAndTermId(studentId, term.getId())
            .orElseGet(() -> {
                StudentTeacherNote n = new StudentTeacherNote();
                n.setStudentId(studentId);
                n.setTermId(term.getId());
                return n;
            });
        note.setTeacherId(teacherId);
        note.setContent(request.content());
        noteRepository.save(note);
        return new StudentNoteResponse(studentId, term.getId(), note.getContent());
    }

    // ---- helpers ----

    private BigDecimal avgScore(List<CheckIn> cis) {
        double avg = cis.stream()
            .filter(c -> c.getCampusRunScore() != null)
            .mapToDouble(c -> c.getCampusRunScore().doubleValue()).average().orElse(0);
        return BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP);
    }

    private String checkInStatus(List<CheckIn> cis, LocalDate today) {
        if (cis.isEmpty()) return "MISSED";
        LocalDate last = cis.stream().map(CheckIn::getCheckInDate).max(Comparator.naturalOrder()).orElse(today);
        long gap = ChronoUnit.DAYS.between(last, today);
        if (gap >= 3) return "MISSED";
        LocalDate since = today.minusDays(14);
        double avgDur = cis.stream().filter(c -> !c.getCheckInDate().isBefore(since))
            .filter(c -> c.getDurationMinutes() != null).mapToInt(CheckIn::getDurationMinutes).average().orElse(0);
        if (avgDur > 0 && avgDur < 15) return "LOW_VOLUME";
        return "ACTIVE";
    }

    private String reportStatus(TermReport r) {
        if (r == null) return "NONE";
        return r.getStatus().name();
    }

    private List<StudentDetailResponse.RiskFlag> riskFlagsFor(Long studentId, HealthProfile p, List<CheckIn> cis, LocalDate today) {
        List<StudentDetailResponse.RiskFlag> flags = new ArrayList<>();
        if (p != null) {
            BmiCategory cat = BmiCategoryUtil.classify(p.getBmi());
            if (cat == BmiCategory.OBESE || cat == BmiCategory.UNDERWEIGHT) {
                flags.add(new StudentDetailResponse.RiskFlag("BMI_ABNORMAL",
                    "BMI " + p.getBmi() + " (" + cat.getLabel() + ")", cat == BmiCategory.OBESE ? "danger" : "warning"));
            }
            if (p.getVitalCapacity() != null && p.getVitalCapacity() < VITAL_CAPACITY_LOW) {
                flags.add(new StudentDetailResponse.RiskFlag("LOW_VITAL_CAPACITY",
                    "肺活量偏低 " + p.getVitalCapacity(), "warning"));
            }
            if (p.getDiseaseStatus() != null && p.getDiseaseStatus() != DiseaseStatus.NONE) {
                flags.add(new StudentDetailResponse.RiskFlag("DISEASE_NOTE",
                    "健康疾病备注: " + (p.getDiseaseNote() == null ? "" : p.getDiseaseNote()), "danger"));
            }
        }
        if (!cis.isEmpty()) {
            LocalDate last = cis.stream().map(CheckIn::getCheckInDate).max(Comparator.naturalOrder()).orElse(today);
            long gap = ChronoUnit.DAYS.between(last, today);
            if (gap >= 3) {
                flags.add(new StudentDetailResponse.RiskFlag("LONG_MISSED", "已 " + gap + " 天未打卡", "warning"));
            }
            boolean paceAbnormal = cis.stream().anyMatch(c -> c.getPaceMinutesPerKm() != null
                && c.getPaceMinutesPerKm().compareTo(BigDecimal.valueOf(2.0)) < 0);
            if (paceAbnormal) {
                flags.add(new StudentDetailResponse.RiskFlag("PACE_ABNORMAL", "存在配速异常打卡(<2.0)", "danger"));
            }
            // 运动量突增: 最近一次时长 > 历史均值的 2 倍
            List<Integer> durs = cis.stream().filter(c -> c.getDurationMinutes() != null)
                .map(CheckIn::getDurationMinutes).sorted().toList();
            if (durs.size() >= 3) {
                int lastDur = durs.get(durs.size() - 1);
                double avg = durs.stream().mapToInt(Integer::intValue).average().orElse(0);
                if (lastDur > avg * 2) {
                    flags.add(new StudentDetailResponse.RiskFlag("VOLUME_SPIKE", "运动量突增 " + lastDur + " 分钟", "warning"));
                }
            }
        }
        return flags;
    }

    private List<StudentDetailResponse.HealthTrend.TrendPoint> buildTrend(List<CheckIn> cis, int days) {
        LocalDate today = LocalDate.now();
        LocalDate since = today.minusDays(days);
        return cis.stream()
            .filter(c -> !c.getCheckInDate().isBefore(since))
            .sorted(java.util.Comparator.comparing(CheckIn::getCheckInDate))
            .map(c -> new StudentDetailResponse.HealthTrend.TrendPoint(
                c.getCheckInDate().toString(), c.getDurationMinutes(), c.getDistanceKm()))
            .toList();
    }

    private StudentDetailResponse.AiDailySuggestion loadDailySuggestion(Long studentId) {
        // input_summary 含 "studentId":N, 用 LIKE 粗筛
        List<AiRecord> records = aiRecordRepository
            .findByAgentTypeAndInputSummaryContainingOrderByCreatedAtDesc(AiAgentType.DAILY_ANALYSIS, "\"studentId\":" + studentId);
        if (records.isEmpty()) return new StudentDetailResponse.AiDailySuggestion("", List.of(), "", "");
        AiRecord rec = records.get(0);
        try {
            JsonNode node = objectMapper.readTree(rec.getOutputResult() == null ? "{}" : rec.getOutputResult());
            String summary = text(node, "summary");
            List<String> suggestions = new ArrayList<>();
            node.get("suggestions").forEach(s -> suggestions.add(s.asText()));
            String imageStatus = text(node, "imageStatus");
            return new StudentDetailResponse.AiDailySuggestion(summary, suggestions, imageStatus, rec.getImageUrl());
        } catch (Exception ex) {
            return new StudentDetailResponse.AiDailySuggestion("", List.of(), "", "");
        }
    }

    private static String text(JsonNode node, String field) {
        JsonNode n = node.get(field);
        return n == null ? "" : n.asText("");
    }

    private List<StudentListItem> filterAndSort(List<StudentListItem> items, StudentListQuery q) {
        BigDecimal scoreMin = q.scoreMin();
        BigDecimal scoreMax = q.scoreMax();
        var stream = items.stream();
        if (q.q() != null && !q.q().isBlank()) {
            String kw = q.q().trim();
            stream = stream.filter(i -> (i.name() != null && i.name().contains(kw))
                || (i.studentNumber() != null && i.studentNumber().contains(kw)));
        }
        if (q.bmiCategory() != null && !q.bmiCategory().isBlank()) {
            stream = stream.filter(i -> q.bmiCategory().equalsIgnoreCase(i.bmiCategory()));
        }
        if (scoreMin != null) {
            stream = stream.filter(i -> i.campusRunScore() != null && i.campusRunScore().compareTo(scoreMin) >= 0);
        }
        if (scoreMax != null) {
            stream = stream.filter(i -> i.campusRunScore() != null && i.campusRunScore().compareTo(scoreMax) <= 0);
        }
        if (Boolean.TRUE.equals(q.healthRisk())) {
            stream = stream.filter(i -> i.riskFlagCount() > 0);
        }
        if (q.checkInStatus() != null && !q.checkInStatus().isBlank()) {
            stream = stream.filter(i -> q.checkInStatus().equalsIgnoreCase(i.checkInStatus()));
        }
        if (q.reportStatus() != null && !q.reportStatus().isBlank()) {
            stream = stream.filter(i -> q.reportStatus().equalsIgnoreCase(i.reportStatus()));
        }
        List<StudentListItem> result = stream.collect(Collectors.toCollection(ArrayList::new));
        Comparator<StudentListItem> cmp = comparator(q.sort());
        if (cmp != null) {
            if ("DESC".equalsIgnoreCase(q.dir())) cmp = cmp.reversed();
            result.sort(cmp);
        }
        return result;
    }

    private Comparator<StudentListItem> comparator(String sort) {
        if (sort == null) return null;
        return switch (sort.toUpperCase()) {
            case "NAME" -> Comparator.comparing(i -> i.name() == null ? "" : i.name());
            case "STUDENT_NUMBER" -> Comparator.comparing(i -> i.studentNumber() == null ? "" : i.studentNumber());
            case "BMI" -> Comparator.comparing(i -> i.bmi() == null ? BigDecimal.ZERO : i.bmi(),
                Comparator.nullsFirst(Comparator.naturalOrder()));
            case "SCORE" -> Comparator.comparing(i -> i.campusRunScore() == null ? BigDecimal.ZERO : i.campusRunScore(),
                Comparator.nullsFirst(Comparator.naturalOrder()));
            default -> null;
        };
    }
}
