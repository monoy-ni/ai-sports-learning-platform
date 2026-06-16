package com.example.aisports.healthprofile.service;

import com.example.aisports.common.security.UserPrincipal;
import com.example.aisports.common.validation.BmiCalculator;
import com.example.aisports.healthprofile.domain.HealthProfile;
import com.example.aisports.healthprofile.dto.HealthProfileRequest;
import com.example.aisports.healthprofile.dto.HealthProfileResponse;
import com.example.aisports.healthprofile.repository.HealthProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HealthProfileService {
    private final HealthProfileRepository repository;

    public HealthProfileService(HealthProfileRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public HealthProfileResponse save(UserPrincipal principal, HealthProfileRequest request) {
        HealthProfile profile = repository.findByStudentId(principal.id()).orElseGet(HealthProfile::new);
        profile.setStudentId(principal.id());
        profile.setGender(request.gender());
        profile.setAge(request.age());
        profile.setHeightCm(request.heightCm());
        profile.setWeightKg(request.weightKg());
        profile.setBmi(BmiCalculator.calculate(request.heightCm().doubleValue(), request.weightKg().doubleValue()));
        profile.setVitalCapacity(request.vitalCapacity());
        profile.setDiseaseStatus(request.diseaseStatus());
        profile.setDiseaseNote(request.diseaseNote());
        profile.setSportGoal(request.sportGoal());
        profile.setWeeklyFrequency(request.weeklyFrequency());
        profile.setBodyType(request.bodyType());
        return HealthProfileResponse.from(repository.save(profile));
    }

    @Transactional(readOnly = true)
    public HealthProfileResponse current(UserPrincipal principal) {
        return repository.findByStudentId(principal.id()).map(HealthProfileResponse::from).orElse(null);
    }
}

