package com.example.aisports.sportplan.domain;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "sport_plans")
public class SportPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long studentId;
    private String termId;
    private String generationStatus;
    @Column(columnDefinition = "json")
    private String weeklyPlan;
    private boolean active;
    private Instant generatedAt;

    @PrePersist
    void prePersist() {
        generatedAt = Instant.now();
        active = true;
    }

    public Long getId() {
        return id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getTermId() {
        return termId;
    }

    public void setTermId(String termId) {
        this.termId = termId;
    }

    public String getGenerationStatus() {
        return generationStatus;
    }

    public void setGenerationStatus(String generationStatus) {
        this.generationStatus = generationStatus;
    }

    public String getWeeklyPlan() {
        return weeklyPlan;
    }

    public void setWeeklyPlan(String weeklyPlan) {
        this.weeklyPlan = weeklyPlan;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Instant getGeneratedAt() {
        return generatedAt;
    }
}

