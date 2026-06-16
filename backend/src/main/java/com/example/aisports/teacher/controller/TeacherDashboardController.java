package com.example.aisports.teacher.controller;

import com.example.aisports.common.response.ApiResponse;
import com.example.aisports.teacher.dto.TeacherDashboardResponse;
import com.example.aisports.teacher.service.TeacherDashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/teacher")
public class TeacherDashboardController {
    private final TeacherDashboardService service;

    public TeacherDashboardController(TeacherDashboardService service) {
        this.service = service;
    }

    @GetMapping("/dashboard")
    public ApiResponse<TeacherDashboardResponse> dashboard() {
        return ApiResponse.ok(service.dashboard());
    }
}

