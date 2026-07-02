package com.example.aisports.termreport.repository;

import com.example.aisports.termreport.domain.TermReportEditHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TermReportEditHistoryRepository extends JpaRepository<TermReportEditHistory, Long> {
    List<TermReportEditHistory> findByReportIdOrderByCreatedAtDesc(Long reportId);
}
