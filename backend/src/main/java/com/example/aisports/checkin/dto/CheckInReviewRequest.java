package com.example.aisports.checkin.dto;

import jakarta.validation.constraints.NotBlank;

public record CheckInReviewRequest(
    Boolean abnormal,
    String abnormalReason,
    @NotBlank String teacherReviewNote
) {
}

