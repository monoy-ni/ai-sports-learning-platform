package com.example.aisports.campusrun.domain;

import com.example.aisports.checkin.domain.DataSource;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "campus_run_records")
public class CampusRunRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long studentId;
    private BigDecimal score;
    @Enumerated(EnumType.STRING)
    private DataSource dataSource;
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public Long getStudentId() { return studentId; }
    public BigDecimal getScore() { return score; }
    public DataSource getDataSource() { return dataSource; }
    public Instant getCreatedAt() { return createdAt; }
}

