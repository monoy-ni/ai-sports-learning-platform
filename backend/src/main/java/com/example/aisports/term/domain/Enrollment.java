package com.example.aisports.term.domain;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "enrollments")
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long studentId;
    private Long classId;
    private Long termId;
    private Instant enrolledAt;

    @PrePersist
    void prePersist() {
        enrolledAt = Instant.now();
    }

    public Long getId() { return id; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public Long getClassId() { return classId; }
    public void setClassId(Long classId) { this.classId = classId; }
    public Long getTermId() { return termId; }
    public void setTermId(Long termId) { this.termId = termId; }
}
