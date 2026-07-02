package com.example.aisports.teacher.domain;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "student_teacher_notes")
public class StudentTeacherNote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long studentId;
    private Long teacherId;
    private Long termId;
    @Column(columnDefinition = "text")
    private String content;
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

    public Long getId() { return id; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }
    public Long getTermId() { return termId; }
    public void setTermId(Long termId) { this.termId = termId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
