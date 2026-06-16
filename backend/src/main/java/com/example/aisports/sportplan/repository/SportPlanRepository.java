package com.example.aisports.sportplan.repository;

import com.example.aisports.sportplan.domain.SportPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SportPlanRepository extends JpaRepository<SportPlan, Long> {
    boolean existsByStudentIdAndTermIdAndGenerationStatus(Long studentId, String termId, String generationStatus);

    Optional<SportPlan> findFirstByStudentIdAndActiveTrueOrderByGeneratedAtDesc(Long studentId);
}

