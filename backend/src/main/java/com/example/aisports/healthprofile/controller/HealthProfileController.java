package com.example.aisports.healthprofile.controller;

import com.example.aisports.common.response.ApiResponse;
import com.example.aisports.common.security.UserPrincipal;
import com.example.aisports.healthprofile.dto.HealthProfileRequest;
import com.example.aisports.healthprofile.dto.HealthProfileResponse;
import com.example.aisports.healthprofile.service.HealthProfileService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student/health-profile")
public class HealthProfileController {
    private final HealthProfileService service;

    public HealthProfileController(HealthProfileService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<HealthProfileResponse> current(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok(service.current(principal));
    }

    @PostMapping
    public ApiResponse<HealthProfileResponse> create(@AuthenticationPrincipal UserPrincipal principal, @Valid @RequestBody HealthProfileRequest request) {
        return ApiResponse.ok(service.save(principal, request));
    }

    @PutMapping
    public ApiResponse<HealthProfileResponse> update(@AuthenticationPrincipal UserPrincipal principal, @Valid @RequestBody HealthProfileRequest request) {
        return ApiResponse.ok(service.save(principal, request));
    }
}

