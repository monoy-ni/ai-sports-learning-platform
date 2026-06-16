package com.example.aisports.student.controller;

import com.example.aisports.common.response.ApiResponse;
import com.example.aisports.student.dto.StudentDashboardResponse;
import com.example.aisports.student.service.StudentDashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/student")
public class StudentDashboardController {
    private final StudentDashboardService dashboardService;

    public StudentDashboardController(StudentDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
    public ApiResponse<StudentDashboardResponse> dashboard() {
        return ApiResponse.ok(dashboardService.currentDashboard());
    }
}

