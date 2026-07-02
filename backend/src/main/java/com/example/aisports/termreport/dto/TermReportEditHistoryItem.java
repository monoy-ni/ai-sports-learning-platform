package com.example.aisports.termreport.dto;

import com.example.aisports.termreport.domain.TermReportEditHistory;

import java.time.Instant;

public record TermReportEditHistoryItem(
    Long id,
    Long editorId,
    String oldFinalContent,
    String newFinalContent,
    String oldTeacherReviewNote,
    String newTeacherReviewNote,
    String action,
    Instant createdAt
) {
    public static TermReportEditHistoryItem from(TermReportEditHistory h) {
        return new TermReportEditHistoryItem(h.getId(), h.getEditorId(), h.getOldFinalContent(),
            h.getNewFinalContent(), h.getOldTeacherReviewNote(), h.getNewTeacherReviewNote(),
            h.getAction(), h.getCreatedAt());
    }
}
