package com.example.aisports.sportplan.service;

import com.example.aisports.airecord.client.AiServiceClient;
import com.example.aisports.airecord.dto.AiTaskResponse;
import com.example.aisports.airecord.service.AiRecordService;
import com.example.aisports.common.exception.BusinessException;
import com.example.aisports.common.security.UserPrincipal;
import com.example.aisports.sportplan.domain.SportPlan;
import com.example.aisports.sportplan.dto.CurrentSportPlanResponse;
import com.example.aisports.sportplan.dto.SportPlanResponse;
import com.example.aisports.sportplan.repository.SportPlanRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class SportPlanService {
    private static final String CURRENT_TERM = "2026-Spring";
    private final SportPlanRepository repository;
    private final AiServiceClient aiServiceClient;
    private final AiRecordService aiRecordService;
    private final ObjectMapper objectMapper;

    public SportPlanService(SportPlanRepository repository, AiServiceClient aiServiceClient, AiRecordService aiRecordService, ObjectMapper objectMapper) {
        this.repository = repository;
        this.aiServiceClient = aiServiceClient;
        this.aiRecordService = aiRecordService;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public SportPlanResponse generate(UserPrincipal principal) {
        if (repository.existsByStudentIdAndTermIdAndGenerationStatus(principal.id(), CURRENT_TERM, "SUCCESS")) {
            throw new BusinessException("PLAN_ALREADY_GENERATED", "本学期运动计划已成功生成，需教师重置后才能重新生成");
        }
        Map<String, Object> payload = Map.of(
            "studentId", principal.id(),
            "termId", CURRENT_TERM,
            "inputVersion", "v0.1.0"
        );
        AiTaskResponse aiTask = aiServiceClient.generatePlan(payload);
        aiRecordService.save(payload, aiTask);

        SportPlan plan = new SportPlan();
        plan.setStudentId(principal.id());
        plan.setTermId(CURRENT_TERM);
        plan.setGenerationStatus(aiTask.status().name());
        plan.setWeeklyPlan(toJson(aiTask.result()));
        SportPlan saved = repository.save(plan);
        return new SportPlanResponse(saved.getId(), aiTask);
    }

    @Transactional(readOnly = true)
    public CurrentSportPlanResponse current(UserPrincipal principal) {
        return repository.findFirstByStudentIdAndActiveTrueOrderByGeneratedAtDesc(principal.id())
            .map(CurrentSportPlanResponse::from)
            .orElse(null);
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            return "{}";
        }
    }
}
