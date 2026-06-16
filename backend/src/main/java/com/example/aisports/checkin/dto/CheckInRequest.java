package com.example.aisports.checkin.dto;

import com.example.aisports.checkin.domain.DataSource;
import com.example.aisports.checkin.domain.ExerciseType;
import com.example.aisports.checkin.domain.WeatherStatus;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CheckInRequest(
    @NotNull LocalDate date,
    @NotNull WeatherStatus weather,
    @NotNull ExerciseType exerciseType,
    @Min(1) @Max(300) Integer durationMinutes,
    @DecimalMin("0.0") @DecimalMax("100.0") BigDecimal distanceKm,
    @DecimalMin("1.0") @DecimalMax("30.0") BigDecimal paceMinutesPerKm,
    @Min(1) @Max(10) Integer fatigueLevel,
    String feeling,
    Boolean completedPlan,
    BigDecimal campusRunScore,
    DataSource dataSource
) {
}

