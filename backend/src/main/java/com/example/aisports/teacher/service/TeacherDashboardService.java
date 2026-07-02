package com.example.aisports.teacher.service;

import com.example.aisports.campusrun.domain.CampusRunRecord;
import com.example.aisports.campusrun.repository.CampusRunRecordRepository;
import com.example.aisports.checkin.domain.CheckIn;
import com.example.aisports.common.validation.BmiCategory;
import com.example.aisports.common.validation.BmiCategoryUtil;
import com.example.aisports.healthprofile.domain.HealthProfile;
import com.example.aisports.sportplan.repository.SportPlanRepository;
import com.example.aisports.student.dto.DashboardMetric;
import com.example.aisports.teacher.dto.TeacherDashboardResponse;
import com.example.aisports.term.domain.Term;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TeacherDashboardService {
    private static final int MISS_DAYS_THRESHOLD = 3;
    private static final int LOW_VOLUME_THRESHOLD_MIN = 15;
    private static final int VITAL_CAPACITY_EXCELLENT = 3500;
    private static final int VITAL_CAPACITY_PASS = 2600;

    private final TeacherDataService data;
    private final SportPlanRepository sportPlanRepository;
    private final CampusRunRecordRepository campusRunRepository;

    public TeacherDashboardService(TeacherDataService data,
                                   SportPlanRepository sportPlanRepository,
                                   CampusRunRecordRepository campusRunRepository) {
        this.data = data;
        this.sportPlanRepository = sportPlanRepository;
        this.campusRunRepository = campusRunRepository;
    }

    public TeacherDashboardResponse dashboard(Long teacherId, Long classId, Long termId) {
        Term term = data.resolveTerm(termId);
        List<Long> studentIds = data.resolveStudentIds(teacherId, classId, term.getId());
        if (studentIds.isEmpty()) {
            return emptyResponse();
        }

        Map<Long, HealthProfile> profiles = data.loadProfiles(studentIds);
        LocalDate termStart = term.getStartDate();
        LocalDate termEnd = term.getEndDate();
        LocalDate today = LocalDate.now();
        LocalDate rangeEnd = termEnd.isBefore(today) ? termEnd : today;
        List<CheckIn> checkIns = data.loadCheckIns(studentIds, termStart, rangeEnd);
        List<CampusRunRecord> runRecords = campusRunRepository.findByStudentIdIn(studentIds);

        // 按学生分组打卡 (已按 studentId, date 升序)
        Map<Long, List<CheckIn>> checkInsByStudent = checkIns.stream()
            .collect(Collectors.groupingBy(CheckIn::getStudentId));
        Map<Long, List<CampusRunRecord>> runsByStudent = runRecords.stream()
            .collect(Collectors.groupingBy(CampusRunRecord::getStudentId));

        long archived = profiles.size();
        long planGenerated = sportPlanRepository
            .findByStudentIdInAndTermIdAndGenerationStatus(studentIds, term.getTermCode(), "SUCCESS").size();
        long activeRecently = countActiveRecently(checkInsByStudent, today);
        BigDecimal avgRunScore = averageRunScore(runRecords);
        long abnormalCount = checkIns.stream().filter(c -> Boolean.TRUE.equals(c.getAbnormal())).count();

        // 风险列表
        List<TeacherDashboardResponse.RiskStudent> consecutiveMiss = new ArrayList<>();
        List<TeacherDashboardResponse.RiskStudent> lowVolume = new ArrayList<>();
        List<TeacherDashboardResponse.RiskStudent> abnormalData = new ArrayList<>();
        List<TeacherDashboardResponse.RiskStudent> healthRisk = new ArrayList<>();
        Set<Long> abnormalStudents = new HashSet<>();

        Map<Long, String> nameMap = data.loadUsers(studentIds).entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getDisplayName()));

        long bmiUnder = 0, bmiNormal = 0, bmiOver = 0, bmiObese = 0;
        long vcExcellent = 0, vcPass = 0, vcFail = 0;

        for (Long sid : studentIds) {
            String name = nameMap.getOrDefault(sid, "学生" + sid);
            List<CheckIn> cis = checkInsByStudent.getOrDefault(sid, List.of());
            // 异常数据学生
            boolean hasAbnormal = cis.stream().anyMatch(c -> Boolean.TRUE.equals(c.getAbnormal()));
            if (hasAbnormal) {
                abnormalStudents.add(sid);
                abnormalData.add(new TeacherDashboardResponse.RiskStudent(sid, name, "存在异常打卡记录", "待核验"));
            }
            // 连续缺卡
            int missDays = consecutiveMissDays(cis, today);
            if (missDays >= MISS_DAYS_THRESHOLD) {
                consecutiveMiss.add(new TeacherDashboardResponse.RiskStudent(sid, name, "连续缺卡 " + missDays + " 天", "待关注"));
            }
            // 运动量过低 (近14天均时长)
            double avgDur = avgDurationRecent(cis, today, 14);
            if (!cis.isEmpty() && avgDur < LOW_VOLUME_THRESHOLD_MIN) {
                lowVolume.add(new TeacherDashboardResponse.RiskStudent(sid, name,
                    "近14天平均运动 " + String.format("%.0f", avgDur) + " 分钟", "运动量过低"));
            }
            // BMI / 肺活量分布 + 健康风险
            HealthProfile p = profiles.get(sid);
            if (p != null) {
                BmiCategory cat = BmiCategoryUtil.classify(p.getBmi());
                switch (cat) {
                    case UNDERWEIGHT -> bmiUnder++;
                    case NORMAL -> bmiNormal++;
                    case OVERWEIGHT -> bmiOver++;
                    case OBESE -> bmiObese++;
                }
                int vc = p.getVitalCapacity() == null ? 0 : p.getVitalCapacity();
                if (vc >= VITAL_CAPACITY_EXCELLENT) vcExcellent++;
                else if (vc >= VITAL_CAPACITY_PASS) vcPass++;
                else vcFail++;
                boolean disease = p.getDiseaseStatus() != null && p.getDiseaseStatus() != com.example.aisports.healthprofile.domain.DiseaseStatus.NONE;
                if (disease || cat == BmiCategory.OBESE) {
                    healthRisk.add(new TeacherDashboardResponse.RiskStudent(sid, name,
                        disease ? "健康疾病备注: " + (p.getDiseaseNote() == null ? "" : p.getDiseaseNote()) : "BMI 肥胖",
                        "健康风险"));
                }
            }
        }

        // 运动记录看板
        long totalCheckIns = checkIns.size();
        double avgDuration = checkIns.stream().filter(c -> c.getDurationMinutes() != null)
            .mapToInt(CheckIn::getDurationMinutes).average().orElse(0);
        double avgDistance = checkIns.stream().filter(c -> c.getDistanceKm() != null)
            .mapToDouble(c -> c.getDistanceKm().doubleValue()).average().orElse(0);
        double avgPace = checkIns.stream().filter(c -> c.getPaceMinutesPerKm() != null)
            .mapToDouble(c -> c.getPaceMinutesPerKm().doubleValue()).average().orElse(0);
        double planCompletion = checkIns.isEmpty() ? 0 :
            (double) checkIns.stream().filter(c -> Boolean.TRUE.equals(c.getCompletedPlan())).count() / checkIns.size();

        List<DashboardMetric> metrics = List.of(
            new DashboardMetric("学生总数", String.valueOf(studentIds.size()), classId == null ? "全部班级" : "当前班级", "info"),
            new DashboardMetric("已建档", String.valueOf(archived), "完成率 " + pct(archived, studentIds.size()), "success"),
            new DashboardMetric("计划生成", String.valueOf(planGenerated), "本学期已生成", "info"),
            new DashboardMetric("打卡活跃度", String.valueOf(activeRecently), "近7天打卡人数", "success"),
            new DashboardMetric("校园跑平均分", fmtScore(avgRunScore), "本学期", "neutral"),
            new DashboardMetric("健康风险学生", String.valueOf(healthRisk.size()), "疾病/BMI肥胖", "warning"),
            new DashboardMetric("异常数据", String.valueOf(abnormalCount), "待人工核验", "warning")
        );

        return new TeacherDashboardResponse(
            metrics,
            new TeacherDashboardResponse.ExerciseDashboard(
                totalCheckIns, round1(avgDuration), round2(avgDistance), round2(avgPace), round2(planCompletion * 100) / 100.0),
            new TeacherDashboardResponse.BmiDistribution(bmiUnder, bmiNormal, bmiOver, bmiObese),
            new TeacherDashboardResponse.VitalCapacityDistribution(vcExcellent, vcPass, vcFail),
            consecutiveMiss, lowVolume, abnormalData, healthRisk
        );
    }

    private long countActiveRecently(Map<Long, List<CheckIn>> byStudent, LocalDate today) {
        LocalDate since = today.minusDays(7);
        return byStudent.values().stream()
            .filter(list -> list.stream().anyMatch(c -> !c.getCheckInDate().isBefore(since)))
            .count();
    }

    /** 自最近一次打卡到今天的天数; 无打卡返回一个大数. */
    private int consecutiveMissDays(List<CheckIn> cis, LocalDate today) {
        if (cis.isEmpty()) return 999;
        LocalDate last = cis.get(cis.size() - 1).getCheckInDate();
        return (int) java.time.temporal.ChronoUnit.DAYS.between(last, today);
    }

    private double avgDurationRecent(List<CheckIn> cis, LocalDate today, int days) {
        LocalDate since = today.minusDays(days);
        return cis.stream()
            .filter(c -> !c.getCheckInDate().isBefore(since))
            .filter(c -> c.getDurationMinutes() != null)
            .mapToInt(CheckIn::getDurationMinutes)
            .average().orElse(0);
    }

    private BigDecimal averageRunScore(List<CampusRunRecord> records) {
        if (records.isEmpty()) return BigDecimal.ZERO;
        double avg = records.stream().filter(r -> r.getScore() != null)
            .mapToDouble(r -> r.getScore().doubleValue()).average().orElse(0);
        return BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP);
    }

    private static String pct(long part, long total) {
        if (total == 0) return "0%";
        return String.format("%.1f%%", part * 100.0 / total);
    }

    private static String fmtScore(BigDecimal s) {
        return s == null ? "0.0" : s.setScale(1, RoundingMode.HALF_UP).toPlainString();
    }

    private static double round1(double v) { return Math.round(v * 10) / 10.0; }
    private static double round2(double v) { return Math.round(v * 100) / 100.0; }

    private TeacherDashboardResponse emptyResponse() {
        return new TeacherDashboardResponse(
            List.of(new DashboardMetric("学生总数", "0", "无学生数据", "neutral")),
            new TeacherDashboardResponse.ExerciseDashboard(0, 0, 0, 0, 0),
            new TeacherDashboardResponse.BmiDistribution(0, 0, 0, 0),
            new TeacherDashboardResponse.VitalCapacityDistribution(0, 0, 0),
            List.of(), List.of(), List.of(), List.of()
        );
    }
}
