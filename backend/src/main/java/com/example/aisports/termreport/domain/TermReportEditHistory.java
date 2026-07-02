package com.example.aisports.termreport.domain;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "term_report_edit_history")
public class TermReportEditHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long reportId;
    private Long editorId;
    @Column(columnDefinition = "mediumtext")
    private String oldFinalContent;
    @Column(columnDefinition = "mediumtext")
    private String newFinalContent;
    private String oldTeacherReviewNote;
    private String newTeacherReviewNote;
    private String action;
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public Long getReportId() { return reportId; }
    public void setReportId(Long reportId) { this.reportId = reportId; }
    public Long getEditorId() { return editorId; }
    public void setEditorId(Long editorId) { this.editorId = editorId; }
    public String getOldFinalContent() { return oldFinalContent; }
    public void setOldFinalContent(String oldFinalContent) { this.oldFinalContent = oldFinalContent; }
    public String getNewFinalContent() { return newFinalContent; }
    public void setNewFinalContent(String newFinalContent) { this.newFinalContent = newFinalContent; }
    public String getOldTeacherReviewNote() { return oldTeacherReviewNote; }
    public void setOldTeacherReviewNote(String oldTeacherReviewNote) { this.oldTeacherReviewNote = oldTeacherReviewNote; }
    public String getNewTeacherReviewNote() { return newTeacherReviewNote; }
    public void setNewTeacherReviewNote(String newTeacherReviewNote) { this.newTeacherReviewNote = newTeacherReviewNote; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public Instant getCreatedAt() { return createdAt; }
}
