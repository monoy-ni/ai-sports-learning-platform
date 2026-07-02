package com.example.aisports.teacher.controller;

import com.example.aisports.common.response.ApiResponse;
import com.example.aisports.common.security.UserPrincipal;
import com.example.aisports.teacher.dto.SchoolClassResponse;
import com.example.aisports.teacher.dto.TermResponse;
import com.example.aisports.teacher.dto.TeacherDashboardResponse;
import com.example.aisports.teacher.service.TeacherDashboardService;
import com.example.aisports.teacher.service.TeacherDataService;
import com.example.aisports.term.domain.SchoolClass;
import com.example.aisports.term.domain.Term;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/teacher")
public class TeacherDashboardController {
    private final TeacherDashboardService service;
    private final TeacherDataService dataService;

    public TeacherDashboardController(TeacherDashboardService service, TeacherDataService dataService) {
        this.service = service;
        this.dataService = dataService;
    }

    @GetMapping("/dashboard")
    public ApiResponse<TeacherDashboardResponse> dashboard(
        @AuthenticationPrincipal UserPrincipal principal,
        @RequestParam(required = false) Long classId,
        @RequestParam(required = false) Long termId) {
        return ApiResponse.ok(service.dashboard(principal.id(), classId, termId));
    }

    @GetMapping("/terms")
    public ApiResponse<List<TermResponse>> terms() {
        List<TermResponse> terms = dataService.allTerms().stream()
            .map(TermResponse::from)
            .toList();
        return ApiResponse.ok(terms);
    }

    @GetMapping("/classes")
    public ApiResponse<List<SchoolClassResponse>> classes(
        @AuthenticationPrincipal UserPrincipal principal,
        @RequestParam(required = false) Long termId) {
        Long resolvedTermId = termId != null ? termId : dataService.resolveTerm(null).getId();
        List<SchoolClass> classes = dataService.classesOfTeacher(principal.id()).stream()
            .filter(c -> c.getTermId().equals(resolvedTermId))
            .toList();
        return ApiResponse.ok(classes.stream().map(SchoolClassResponse::from).toList());
    }
}
