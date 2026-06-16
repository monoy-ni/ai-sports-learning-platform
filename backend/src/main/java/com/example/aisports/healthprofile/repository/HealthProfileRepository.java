package com.example.aisports.healthprofile.repository;

import com.example.aisports.healthprofile.domain.HealthProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HealthProfileRepository extends JpaRepository<HealthProfile, Long> {
    Optional<HealthProfile> findByStudentId(Long studentId);
}

