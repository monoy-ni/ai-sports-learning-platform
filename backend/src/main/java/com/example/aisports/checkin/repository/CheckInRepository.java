package com.example.aisports.checkin.repository;

import com.example.aisports.checkin.domain.CheckIn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CheckInRepository extends JpaRepository<CheckIn, Long> {
    boolean existsByStudentIdAndCheckInDate(Long studentId, LocalDate checkInDate);

    List<CheckIn> findByStudentIdOrderByCheckInDateDesc(Long studentId);
}

