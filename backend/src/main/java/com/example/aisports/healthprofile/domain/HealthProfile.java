package com.example.aisports.healthprofile.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "health_profiles")
public class HealthProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long studentId;
    private String gender;
    private Integer age;
    private BigDecimal heightCm;
    private BigDecimal weightKg;
    private BigDecimal bmi;
    private Integer vitalCapacity;

    @Enumerated(EnumType.STRING)
    private DiseaseStatus diseaseStatus;

    private String diseaseNote;
    private String sportGoal;
    private Integer weeklyFrequency;
    private String bodyType;
    private Instant createdAt;
    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        createdAt = Instant.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public BigDecimal getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(BigDecimal heightCm) {
        this.heightCm = heightCm;
    }

    public BigDecimal getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(BigDecimal weightKg) {
        this.weightKg = weightKg;
    }

    public BigDecimal getBmi() {
        return bmi;
    }

    public void setBmi(BigDecimal bmi) {
        this.bmi = bmi;
    }

    public Integer getVitalCapacity() {
        return vitalCapacity;
    }

    public void setVitalCapacity(Integer vitalCapacity) {
        this.vitalCapacity = vitalCapacity;
    }

    public DiseaseStatus getDiseaseStatus() {
        return diseaseStatus;
    }

    public void setDiseaseStatus(DiseaseStatus diseaseStatus) {
        this.diseaseStatus = diseaseStatus;
    }

    public String getDiseaseNote() {
        return diseaseNote;
    }

    public void setDiseaseNote(String diseaseNote) {
        this.diseaseNote = diseaseNote;
    }

    public String getSportGoal() {
        return sportGoal;
    }

    public void setSportGoal(String sportGoal) {
        this.sportGoal = sportGoal;
    }

    public Integer getWeeklyFrequency() {
        return weeklyFrequency;
    }

    public void setWeeklyFrequency(Integer weeklyFrequency) {
        this.weeklyFrequency = weeklyFrequency;
    }

    public String getBodyType() {
        return bodyType;
    }

    public void setBodyType(String bodyType) {
        this.bodyType = bodyType;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}

