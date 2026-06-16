package com.example.aisports.healthprofile.dto;

import com.example.aisports.healthprofile.domain.DiseaseStatus;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record HealthProfileRequest(
    @NotBlank String gender,
    @Min(5) @Max(100) Integer age,
    @DecimalMin("80.0") @DecimalMax("230.0") BigDecimal heightCm,
    @DecimalMin("20.0") @DecimalMax("200.0") BigDecimal weightKg,
    @Min(500) @Max(9000) Integer vitalCapacity,
    @NotNull DiseaseStatus diseaseStatus,
    String diseaseNote,
    @NotBlank String sportGoal,
    @Min(0) @Max(14) Integer weeklyFrequency,
    @NotBlank String bodyType
) {
}

