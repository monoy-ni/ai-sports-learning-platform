package com.example.aisports.campusrun.repository;

import com.example.aisports.campusrun.domain.CampusRunScoreRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CampusRunScoreRuleRepository extends JpaRepository<CampusRunScoreRule, Long> {
    Optional<CampusRunScoreRule> findByTermIdAndIsActiveTrue(Long termId);
}
