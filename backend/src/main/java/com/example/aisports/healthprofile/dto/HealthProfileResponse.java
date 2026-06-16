package com.example.aisports.healthprofile.dto;

import com.example.aisports.healthprofile.domain.DiseaseStatus;
import com.example.aisports.healthprofile.domain.HealthProfile;

import java.math.BigDecimal;

public record HealthProfileResponse(
    Long id,
    Long studentId,
    String gender,
    Integer age,
    BigDecimal heightCm,
    BigDecimal weightKg,
    BigDecimal bmi,
    Integer vitalCapacity,
    DiseaseStatus diseaseStatus,
    String diseaseNote,
    String sportGoal,
    Integer weeklyFrequency,
    String bodyType
) {
    public static HealthProfileResponse from(HealthProfile profile) {
        return new HealthProfileResponse(
            profile.getId(),
            profile.getStudentId(),
            profile.getGender(),
            profile.getAge(),
            profile.getHeightCm(),
            profile.getWeightKg(),
            profile.getBmi(),
            profile.getVitalCapacity(),
            profile.getDiseaseStatus(),
            profile.getDiseaseNote(),
            profile.getSportGoal(),
            profile.getWeeklyFrequency(),
            profile.getBodyType()
        );
    }
}

