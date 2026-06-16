package com.example.aisports.airecord.domain;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "ai_records")
public class AiRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String taskId;

    @Enumerated(EnumType.STRING)
    private AiAgentType agentType;

    @Enumerated(EnumType.STRING)
    private AiTaskStatus status;

    @Column(columnDefinition = "json")
    private String inputSummary;

    @Column(columnDefinition = "json")
    private String outputResult;

    private String imageUrl;
    private String failureReason;
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public AiAgentType getAgentType() {
        return agentType;
    }

    public void setAgentType(AiAgentType agentType) {
        this.agentType = agentType;
    }

    public AiTaskStatus getStatus() {
        return status;
    }

    public void setStatus(AiTaskStatus status) {
        this.status = status;
    }

    public String getInputSummary() {
        return inputSummary;
    }

    public void setInputSummary(String inputSummary) {
        this.inputSummary = inputSummary;
    }

    public String getOutputResult() {
        return outputResult;
    }

    public void setOutputResult(String outputResult) {
        this.outputResult = outputResult;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}

