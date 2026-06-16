package com.example.aisports.campusrun.repository;

import com.example.aisports.campusrun.domain.CampusRunRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampusRunRecordRepository extends JpaRepository<CampusRunRecord, Long> {
}

