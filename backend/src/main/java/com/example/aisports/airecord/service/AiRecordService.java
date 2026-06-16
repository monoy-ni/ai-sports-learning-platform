package com.example.aisports.airecord.service;

import com.example.aisports.airecord.domain.AiRecord;
import com.example.aisports.airecord.dto.AiTaskResponse;
import com.example.aisports.airecord.repository.AiRecordRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AiRecordService {
    private final AiRecordRepository repository;
    private final ObjectMapper objectMapper;

    public AiRecordService(AiRecordRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    public AiRecord save(Map<String, Object> input, AiTaskResponse response) {
        AiRecord record = new AiRecord();
        record.setTaskId(response.taskId());
        record.setAgentType(response.agentType());
        record.setStatus(response.status());
        record.setFailureReason(response.failureReason());
        record.setInputSummary(toJson(input));
        record.setOutputResult(toJson(response.result()));
        Object imageUrl = response.result() == null ? null : response.result().get("imageUrl");
        record.setImageUrl(imageUrl == null ? null : imageUrl.toString());
        return repository.save(record);
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            return "{}";
        }
    }
}

