package com.example.aisports.airecord.repository;

import com.example.aisports.airecord.domain.AiAgentType;
import com.example.aisports.airecord.domain.AiRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AiRecordRepository extends JpaRepository<AiRecord, Long> {

    // input_summary 形如 {"studentId":N,...}; 用 LIKE 粗筛后服务层再精确解析
    List<AiRecord> findByAgentTypeAndInputSummaryContainingOrderByCreatedAtDesc(AiAgentType agentType, String fragment);
}
