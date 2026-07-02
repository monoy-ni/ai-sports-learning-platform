package com.example.aisports.teacher.dto;

import com.example.aisports.checkin.domain.CheckInReviewHistory;

import java.time.Instant;

public record CheckInReviewHistoryItem(
    Long id,
    Long reviewerId,
    Boolean oldAbnormal,
    String oldAbnormalReason,
    String oldTeacherReviewNote,
    Boolean newAbnormal,
    String newAbnormalReason,
    String newTeacherReviewNote,
    String editReason,
    Instant createdAt
) {
    public static CheckInReviewHistoryItem from(CheckInReviewHistory h) {
        return new CheckInReviewHistoryItem(h.getId(), h.getReviewerId(), h.getOldAbnormal(),
            h.getOldAbnormalReason(), h.getOldTeacherReviewNote(), h.getNewAbnormal(),
            h.getNewAbnormalReason(), h.getNewTeacherReviewNote(), h.getEditReason(), h.getCreatedAt());
    }
}
