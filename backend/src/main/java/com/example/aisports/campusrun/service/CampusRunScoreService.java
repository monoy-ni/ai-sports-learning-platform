package com.example.aisports.campusrun.service;

import com.example.aisports.campusrun.domain.CampusRunRecord;
import com.example.aisports.campusrun.domain.CampusRunScoreRule;
import com.example.aisports.campusrun.dto.CampusRunScoreResponse;
import com.example.aisports.campusrun.repository.CampusRunRecordRepository;
import com.example.aisports.campusrun.repository.CampusRunScoreRuleRepository;
import com.example.aisports.checkin.domain.DataSource;
import com.example.aisports.common.exception.BusinessException;
import com.example.aisports.term.domain.Term;
import com.example.aisports.term.repository.TermRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CampusRunScoreService {
    private final CampusRunRecordRepository recordRepository;
    private final CampusRunScoreRuleRepository ruleRepository;
    private final TermRepository termRepository;
    private final ObjectMapper objectMapper;

    public CampusRunScoreService(CampusRunRecordRepository recordRepository,
                                 CampusRunScoreRuleRepository ruleRepository,
                                 TermRepository termRepository,
                                 ObjectMapper objectMapper) {
        this.recordRepository = recordRepository;
        this.ruleRepository = ruleRepository;
        this.termRepository = termRepository;
        this.objectMapper = objectMapper;
    }

    public CampusRunScoreResponse currentScore(Long studentId, Long termId) {
        Term term = termId == null
            ? termRepository.findByIsCurrentTrue().orElseThrow(() -> new BusinessException("TERM_NOT_FOUND", "未配置当前学期"))
            : termRepository.findById(termId).orElseThrow(() -> new BusinessException("TERM_NOT_FOUND", "学期不存在"));
        CampusRunScoreRule rule = ruleRepository.findByTermIdAndIsActiveTrue(term.getId()).orElse(null);
        List<CampusRunRecord> records = recordRepository.findByStudentIdOrderByCreatedAtDesc(studentId);
        return compute(term, rule, records);
    }

    public CampusRunScoreResponse compute(Term term, CampusRunScoreRule rule, List<CampusRunRecord> records) {
        if (records.isEmpty()) {
            return new CampusRunScoreResponse(BigDecimal.ZERO, DataSource.MANUAL.name());
        }
        RuleConfig cfg = parseRule(rule);
        double base = aggregate(records, cfg.aggregation);
        // 频次奖励
        if (cfg.bonusThreshold > 0 && records.size() >= cfg.bonusThreshold) {
            base += cfg.bonus;
        }
        // 缺周扣分
        long missedWeeks = missedWeeks(term, records);
        base -= missedWeeks * cfg.deductPerMissedWeek;
        // clamp
        base = Math.max(0, Math.min(cfg.maxScore, base));

        BigDecimal score = BigDecimal.valueOf(base).setScale(2, RoundingMode.HALF_UP);
        DataSource ds = records.get(0).getDataSource(); // 已按 createdAt desc
        return new CampusRunScoreResponse(score, ds == null ? DataSource.MANUAL.name() : ds.name());
    }

    private double aggregate(List<CampusRunRecord> records, String aggregation) {
        List<BigDecimal> scores = records.stream()
            .filter(r -> r.getScore() != null).map(CampusRunRecord::getScore).toList();
        if (scores.isEmpty()) return 0;
        return switch (aggregation == null ? "" : aggregation.toUpperCase()) {
            case "LATEST" -> scores.get(0).doubleValue();
            case "BEST" -> scores.stream().mapToDouble(BigDecimal::doubleValue).max().orElse(0);
            default -> scores.stream().mapToDouble(BigDecimal::doubleValue).average().orElse(0);
        };
    }

    private long missedWeeks(Term term, List<CampusRunRecord> records) {
        LocalDate today = LocalDate.now();
        LocalDate end = term.getEndDate().isBefore(today) ? term.getEndDate() : today;
        long totalWeeks = Math.max(0, ChronoUnit.WEEKS.between(term.getStartDate(), end));
        long hit = records.stream().map(r -> weekOf(term, r)).collect(Collectors.toSet()).size();
        return Math.max(0, totalWeeks - hit);
    }

    private int weekOf(Term term, CampusRunRecord r) {
        if (r.getCreatedAt() == null) return 0;
        LocalDate date = LocalDate.ofInstant(r.getCreatedAt(), java.time.ZoneId.systemDefault());
        return (int) ChronoUnit.WEEKS.between(term.getStartDate(), date);
    }

    private RuleConfig parseRule(CampusRunScoreRule rule) {
        RuleConfig cfg = new RuleConfig();
        cfg.maxScore = 100;
        cfg.passingScore = 60;
        cfg.aggregation = "AVERAGE";
        cfg.deductPerMissedWeek = 0;
        cfg.bonusThreshold = 0;
        cfg.bonus = 0;
        if (rule == null || rule.getRulesJson() == null) return cfg;
        try {
            JsonNode node = objectMapper.readTree(rule.getRulesJson());
            cfg.maxScore = node.path("maxScore").asDouble(100);
            cfg.passingScore = node.path("passingScore").asDouble(60);
            cfg.aggregation = node.path("aggregation").asText("AVERAGE");
            cfg.deductPerMissedWeek = node.path("deductPerMissedWeek").asDouble(0);
            JsonNode fb = node.path("frequencyBonus");
            if (!fb.isMissingNode()) {
                cfg.bonusThreshold = fb.path("thresholdCount").asInt(0);
                cfg.bonus = fb.path("bonus").asDouble(0);
            }
        } catch (Exception ignored) {
            // 解析失败用默认
        }
        return cfg;
    }

    private static class RuleConfig {
        double maxScore;
        double passingScore;
        String aggregation;
        double deductPerMissedWeek;
        int bonusThreshold;
        double bonus;
    }
}
