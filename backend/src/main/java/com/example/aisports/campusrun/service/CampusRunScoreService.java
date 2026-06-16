package com.example.aisports.campusrun.service;

import com.example.aisports.campusrun.dto.CampusRunScoreResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CampusRunScoreService {
    public CampusRunScoreResponse currentScore(Long studentId) {
        return new CampusRunScoreResponse(BigDecimal.valueOf(82), "MANUAL");
    }
}

