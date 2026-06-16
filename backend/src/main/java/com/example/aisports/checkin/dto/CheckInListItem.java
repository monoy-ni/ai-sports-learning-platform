package com.example.aisports.checkin.dto;

import com.example.aisports.checkin.domain.CheckIn;

import java.time.LocalDate;

public record CheckInListItem(Long id, LocalDate date, String exerciseType, Integer durationMinutes, boolean abnormal, String abnormalReason) {
    public static CheckInListItem from(CheckIn checkIn) {
        return new CheckInListItem(
            checkIn.getId(),
            checkIn.getCheckInDate(),
            checkIn.getExerciseType().name(),
            checkIn.getDurationMinutes(),
            Boolean.TRUE.equals(checkIn.getAbnormal()),
            checkIn.getAbnormalReason()
        );
    }
}

