package com.example.aisports.campusrun.repository;

import com.example.aisports.campusrun.domain.CampusRunRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface CampusRunRecordRepository extends JpaRepository<CampusRunRecord, Long> {
    List<CampusRunRecord> findByStudentIdIn(Collection<Long> studentIds);
    List<CampusRunRecord> findByStudentIdOrderByCreatedAtDesc(Long studentId);
}
