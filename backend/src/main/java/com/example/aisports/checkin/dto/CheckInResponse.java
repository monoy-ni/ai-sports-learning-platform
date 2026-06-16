package com.example.aisports.checkin.dto;

import com.example.aisports.airecord.dto.AiTaskResponse;
import com.example.aisports.checkin.domain.CheckIn;

import java.time.LocalDate;

public record CheckInResponse(
    Long id,
    LocalDate date,
    boolean abnormal,
    String abnormalReason,
    AiTaskResponse aiTask
) {
    public static CheckInResponse from(CheckIn checkIn, AiTaskResponse aiTask) {
        return new CheckInResponse(
            checkIn.getId(),
            checkIn.getCheckInDate(),
            Boolean.TRUE.equals(checkIn.getAbnormal()),
            checkIn.getAbnormalReason(),
            aiTask
        );
    }
}

