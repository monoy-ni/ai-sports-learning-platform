package com.example.aisports.checkin.repository;

import com.example.aisports.checkin.domain.CheckIn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface CheckInRepository extends JpaRepository<CheckIn, Long> {
    boolean existsByStudentIdAndCheckInDate(Long studentId, LocalDate checkInDate);

    List<CheckIn> findByStudentIdOrderByCheckInDateDesc(Long studentId);

    List<CheckIn> findByStudentIdInAndCheckInDateBetweenOrderByStudentIdAscCheckInDateAsc(
        Collection<Long> studentIds, LocalDate start, LocalDate end);

    List<CheckIn> findByStudentIdInAndAbnormalTrueOrderByCheckInDateDesc(Collection<Long> studentIds);

    long countByStudentIdInAndAbnormalTrue(Collection<Long> studentIds);
}
