package com.example.aisports.checkin.repository;

import com.example.aisports.checkin.domain.CheckInReviewHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CheckInReviewHistoryRepository extends JpaRepository<CheckInReviewHistory, Long> {
    List<CheckInReviewHistory> findByCheckInIdOrderByCreatedAtDesc(Long checkInId);
}
