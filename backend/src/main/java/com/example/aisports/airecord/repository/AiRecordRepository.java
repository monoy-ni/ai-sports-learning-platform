package com.example.aisports.airecord.repository;

import com.example.aisports.airecord.domain.AiRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiRecordRepository extends JpaRepository<AiRecord, Long> {
}

