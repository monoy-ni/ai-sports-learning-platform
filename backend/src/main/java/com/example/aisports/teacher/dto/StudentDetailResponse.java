package com.example.aisports.teacher.dto;

import java.util.List;

public record StudentDetailResponse(Long id, String name, List<String> riskFlags, List<String> recentRecords) {
}

