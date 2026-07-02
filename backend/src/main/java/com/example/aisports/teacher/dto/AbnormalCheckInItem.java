package com.example.aisports.teacher.dto;

import com.example.aisports.checkin.domain.CheckIn;

import java.time.LocalDate;

public record AbnormalCheckInItem(
    Long id,
    Long studentId,
    String studentName,
    LocalDate date,
    String exerciseType,
    Integer durationMinutes,
    String abnormalReason,
    String teacherReviewNote
) {
    public static AbnormalCheckInItem from(CheckIn c, String studentName) {
        return new AbnormalCheckInItem(c.getId(), c.getStudentId(), studentName, c.getCheckInDate(),
            c.getExerciseType() == null ? null : c.getExerciseType().name(),
            c.getDurationMinutes(), c.getAbnormalReason(), c.getTeacherReviewNote());
    }
}
