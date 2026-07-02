package com.example.aisports.termreport.repository;

import com.example.aisports.termreport.domain.TermReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TermReportRepository extends JpaRepository<TermReport, Long> {
    Optional<TermReport> findByStudentIdAndTermId(Long studentId, String termId);
    List<TermReport> findByStudentIdInAndTermId(Collection<Long> studentIds, String termId);
}
