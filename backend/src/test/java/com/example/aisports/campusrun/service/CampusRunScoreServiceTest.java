package com.example.aisports.campusrun.service;

import com.example.aisports.campusrun.domain.CampusRunRecord;
import com.example.aisports.campusrun.domain.CampusRunScoreRule;
import com.example.aisports.campusrun.dto.CampusRunScoreResponse;
import com.example.aisports.campusrun.repository.CampusRunRecordRepository;
import com.example.aisports.campusrun.repository.CampusRunScoreRuleRepository;
import com.example.aisports.checkin.domain.DataSource;
import com.example.aisports.term.domain.Term;
import com.example.aisports.term.repository.TermRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CampusRunScoreServiceTest {

    private CampusRunRecordRepository recordRepository;
    private CampusRunScoreRuleRepository ruleRepository;
    private TermRepository termRepository;
    private CampusRunScoreService service;

    @BeforeEach
    void setUp() {
        recordRepository = mock(CampusRunRecordRepository.class);
        ruleRepository = mock(CampusRunScoreRuleRepository.class);
        termRepository = mock(TermRepository.class);
        service = new CampusRunScoreService(recordRepository, ruleRepository, termRepository, new ObjectMapper());
    }

    private Term term() {
        Term t = new Term();
        t.setTermCode("2026-Spring");
        t.setStartDate(LocalDate.now().minusDays(30));
        t.setEndDate(LocalDate.now().plusDays(30));
        return t;
    }

    private CampusRunRecord rec(double score, DataSource ds) {
        CampusRunRecord r = new CampusRunRecord();
        java.lang.reflect.Field scoreField;
        try {
            var f = CampusRunRecord.class.getDeclaredField("score");
            f.setAccessible(true);
            f.set(r, BigDecimal.valueOf(score));
            var dsField = CampusRunRecord.class.getDeclaredField("dataSource");
            dsField.setAccessible(true);
            dsField.set(r, ds);
            var idField = CampusRunRecord.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(r, 1L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return r;
    }

    @Test
    void noRecordsReturnsZeroManual() {
        when(termRepository.findByIsCurrentTrue()).thenReturn(Optional.of(term()));
        when(ruleRepository.findByTermIdAndIsActiveTrue(anyLong())).thenReturn(Optional.empty());
        when(recordRepository.findByStudentIdOrderByCreatedAtDesc(anyLong())).thenReturn(List.of());
        CampusRunScoreResponse resp = service.currentScore(1L, null);
        assertThat(resp.score()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(resp.dataSource()).isEqualTo("MANUAL");
    }

    @Test
    void averagesRecordsAndClamps() {
        when(termRepository.findById(anyLong())).thenReturn(Optional.of(term()));
        when(ruleRepository.findByTermIdAndIsActiveTrue(anyLong())).thenReturn(Optional.empty());
        when(recordRepository.findByStudentIdOrderByCreatedAtDesc(anyLong())).thenReturn(List.of(
            rec(80, DataSource.MANUAL), rec(90, DataSource.API_SYNC)));
        CampusRunScoreResponse resp = service.currentScore(1L, 1L);
        assertThat(resp.score()).isEqualByComparingTo(new BigDecimal("85.00"));
        assertThat(resp.dataSource()).isEqualTo("MANUAL"); // 列表首条
    }
}
