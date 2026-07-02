package com.example.aisports.termreport.domain;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "term_reports")
public class TermReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long studentId;
    private String termId;
    @Column(columnDefinition = "json")
    private String aiDraft;
    @Column(columnDefinition = "text")
    private String finalContent;
    private String teacherReviewNote;
    @Enumerated(EnumType.STRING)
    private ReportStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        createdAt = Instant.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getTermId() {
        return termId;
    }

    public void setTermId(String termId) {
        this.termId = termId;
    }

    public String getAiDraft() {
        return aiDraft;
    }

    public void setAiDraft(String aiDraft) {
        this.aiDraft = aiDraft;
    }

    public String getFinalContent() {
        return finalContent;
    }

    public void setFinalContent(String finalContent) {
        this.finalContent = finalContent;
    }

    public String getTeacherReviewNote() {
        return teacherReviewNote;
    }

    public void setTeacherReviewNote(String teacherReviewNote) {
        this.teacherReviewNote = teacherReviewNote;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}

