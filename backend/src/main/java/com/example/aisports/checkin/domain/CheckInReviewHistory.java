package com.example.aisports.checkin.domain;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "check_in_review_history")
public class CheckInReviewHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long checkInId;
    private Long reviewerId;
    private Boolean oldAbnormal;
    private String oldAbnormalReason;
    private String oldTeacherReviewNote;
    private Boolean newAbnormal;
    private String newAbnormalReason;
    private String newTeacherReviewNote;
    private String editReason;
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public Long getCheckInId() { return checkInId; }
    public void setCheckInId(Long checkInId) { this.checkInId = checkInId; }
    public Long getReviewerId() { return reviewerId; }
    public void setReviewerId(Long reviewerId) { this.reviewerId = reviewerId; }
    public Boolean getOldAbnormal() { return oldAbnormal; }
    public void setOldAbnormal(Boolean oldAbnormal) { this.oldAbnormal = oldAbnormal; }
    public String getOldAbnormalReason() { return oldAbnormalReason; }
    public void setOldAbnormalReason(String oldAbnormalReason) { this.oldAbnormalReason = oldAbnormalReason; }
    public String getOldTeacherReviewNote() { return oldTeacherReviewNote; }
    public void setOldTeacherReviewNote(String oldTeacherReviewNote) { this.oldTeacherReviewNote = oldTeacherReviewNote; }
    public Boolean getNewAbnormal() { return newAbnormal; }
    public void setNewAbnormal(Boolean newAbnormal) { this.newAbnormal = newAbnormal; }
    public String getNewAbnormalReason() { return newAbnormalReason; }
    public void setNewAbnormalReason(String newAbnormalReason) { this.newAbnormalReason = newAbnormalReason; }
    public String getNewTeacherReviewNote() { return newTeacherReviewNote; }
    public void setNewTeacherReviewNote(String newTeacherReviewNote) { this.newTeacherReviewNote = newTeacherReviewNote; }
    public String getEditReason() { return editReason; }
    public void setEditReason(String editReason) { this.editReason = editReason; }
    public Instant getCreatedAt() { return createdAt; }
}
