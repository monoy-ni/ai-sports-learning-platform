package com.example.aisports.termreport.repository;

import com.example.aisports.termreport.domain.TermReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TermReportRepository extends JpaRepository<TermReport, Long> {
}

