package com.example.aisports.checkin.controller;

import com.example.aisports.checkin.dto.CheckInRequest;
import com.example.aisports.checkin.dto.CheckInResponse;
import com.example.aisports.checkin.dto.CheckInListItem;
import com.example.aisports.checkin.service.CheckInService;
import com.example.aisports.common.response.ApiResponse;
import com.example.aisports.common.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student/check-ins")
public class CheckInController {
    private final CheckInService service;

    public CheckInController(CheckInService service) {
        this.service = service;
    }

    @PostMapping
    public ApiResponse<CheckInResponse> create(@AuthenticationPrincipal UserPrincipal principal, @Valid @RequestBody CheckInRequest request) {
        return ApiResponse.ok(service.create(principal, request));
    }

    @GetMapping
    public ApiResponse<java.util.List<CheckInListItem>> list(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok(service.list(principal));
    }
}
