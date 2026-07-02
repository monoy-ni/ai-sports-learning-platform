package com.example.aisports.teacher.controller;

import com.example.aisports.common.response.ApiResponse;
import com.example.aisports.common.security.UserPrincipal;
import com.example.aisports.teacher.dto.StudentDetailResponse;
import com.example.aisports.teacher.dto.StudentListItem;
import com.example.aisports.teacher.dto.StudentListQuery;
import com.example.aisports.teacher.dto.StudentNoteRequest;
import com.example.aisports.teacher.dto.StudentNoteResponse;
import com.example.aisports.teacher.service.TeacherStudentService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/teacher/students")
public class TeacherStudentController {
    private final TeacherStudentService service;

    public TeacherStudentController(TeacherStudentService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<List<StudentListItem>> list(
        @AuthenticationPrincipal UserPrincipal principal,
        @RequestParam(required = false) Long classId,
        @RequestParam(required = false) Long termId,
        @RequestParam(required = false) String q,
        @RequestParam(required = false) String bmiCategory,
        @RequestParam(required = false) BigDecimal scoreMin,
        @RequestParam(required = false) BigDecimal scoreMax,
        @RequestParam(required = false) Boolean healthRisk,
        @RequestParam(required = false) String checkInStatus,
        @RequestParam(required = false) String reportStatus,
        @RequestParam(required = false) String sort,
        @RequestParam(required = false) String dir) {
        StudentListQuery query = new StudentListQuery(classId, termId, q, bmiCategory,
            scoreMin, scoreMax, healthRisk, checkInStatus, reportStatus, sort, dir);
        return ApiResponse.ok(service.list(principal.id(), query));
    }

    @GetMapping("/{studentId}")
    public ApiResponse<StudentDetailResponse> detail(
        @PathVariable Long studentId,
        @RequestParam(required = false) Long termId) {
        return ApiResponse.ok(service.detail(studentId, termId));
    }

    @GetMapping("/{studentId}/note")
    public ApiResponse<StudentNoteResponse> getNote(
        @PathVariable Long studentId,
        @RequestParam(required = false) Long termId) {
        return ApiResponse.ok(service.getNote(studentId, termId));
    }

    @PutMapping("/{studentId}/note")
    public ApiResponse<StudentNoteResponse> updateNote(
        @AuthenticationPrincipal UserPrincipal principal,
        @PathVariable Long studentId,
        @Valid @RequestBody StudentNoteRequest request) {
        return ApiResponse.ok(service.updateNote(principal.id(), studentId, request));
    }
}
