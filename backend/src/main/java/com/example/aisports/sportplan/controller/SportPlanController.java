package com.example.aisports.sportplan.controller;

import com.example.aisports.common.response.ApiResponse;
import com.example.aisports.common.security.UserPrincipal;
import com.example.aisports.sportplan.dto.CurrentSportPlanResponse;
import com.example.aisports.sportplan.dto.SportPlanResponse;
import com.example.aisports.sportplan.service.SportPlanService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/student/sport-plans")
public class SportPlanController {
    private final SportPlanService service;

    public SportPlanController(SportPlanService service) {
        this.service = service;
    }

    @PostMapping("/generate")
    public ApiResponse<SportPlanResponse> generate(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok(service.generate(principal));
    }

    @GetMapping("/current")
    public ApiResponse<CurrentSportPlanResponse> current(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok(service.current(principal));
    }
}
