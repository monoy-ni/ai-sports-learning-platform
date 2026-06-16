package com.example.aisports.checkin.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "check_ins", uniqueConstraints = @UniqueConstraint(name = "uk_student_checkin_date", columnNames = {"studentId", "checkInDate"}))
public class CheckIn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long studentId;
    private LocalDate checkInDate;

    @Enumerated(EnumType.STRING)
    private WeatherStatus weather;

    @Enumerated(EnumType.STRING)
    private ExerciseType exerciseType;

    private Integer durationMinutes;
    private BigDecimal distanceKm;
    private BigDecimal paceMinutesPerKm;
    private Integer fatigueLevel;
    private String feeling;
    private Boolean completedPlan;
    private BigDecimal campusRunScore;

    @Enumerated(EnumType.STRING)
    private DataSource dataSource;

    private Boolean abnormal;
    private String abnormalReason;
    private String teacherReviewNote;
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

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public WeatherStatus getWeather() {
        return weather;
    }

    public void setWeather(WeatherStatus weather) {
        this.weather = weather;
    }

    public ExerciseType getExerciseType() {
        return exerciseType;
    }

    public void setExerciseType(ExerciseType exerciseType) {
        this.exerciseType = exerciseType;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public BigDecimal getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(BigDecimal distanceKm) {
        this.distanceKm = distanceKm;
    }

    public BigDecimal getPaceMinutesPerKm() {
        return paceMinutesPerKm;
    }

    public void setPaceMinutesPerKm(BigDecimal paceMinutesPerKm) {
        this.paceMinutesPerKm = paceMinutesPerKm;
    }

    public Integer getFatigueLevel() {
        return fatigueLevel;
    }

    public void setFatigueLevel(Integer fatigueLevel) {
        this.fatigueLevel = fatigueLevel;
    }

    public String getFeeling() {
        return feeling;
    }

    public void setFeeling(String feeling) {
        this.feeling = feeling;
    }

    public Boolean getCompletedPlan() {
        return completedPlan;
    }

    public void setCompletedPlan(Boolean completedPlan) {
        this.completedPlan = completedPlan;
    }

    public BigDecimal getCampusRunScore() {
        return campusRunScore;
    }

    public void setCampusRunScore(BigDecimal campusRunScore) {
        this.campusRunScore = campusRunScore;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Boolean getAbnormal() {
        return abnormal;
    }

    public void setAbnormal(Boolean abnormal) {
        this.abnormal = abnormal;
    }

    public String getAbnormalReason() {
        return abnormalReason;
    }

    public void setAbnormalReason(String abnormalReason) {
        this.abnormalReason = abnormalReason;
    }

    public String getTeacherReviewNote() {
        return teacherReviewNote;
    }

    public void setTeacherReviewNote(String teacherReviewNote) {
        this.teacherReviewNote = teacherReviewNote;
    }
}

