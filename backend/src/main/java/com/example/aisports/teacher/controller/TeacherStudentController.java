package com.example.aisports.teacher.controller;

import com.example.aisports.common.response.ApiResponse;
import com.example.aisports.teacher.dto.StudentDetailResponse;
import com.example.aisports.teacher.dto.StudentListItem;
import com.example.aisports.teacher.service.TeacherStudentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teacher/students")
public class TeacherStudentController {
    private final TeacherStudentService service;

    public TeacherStudentController(TeacherStudentService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<List<StudentListItem>> list() {
        return ApiResponse.ok(service.list());
    }

    @GetMapping("/{studentId}")
    public ApiResponse<StudentDetailResponse> detail(@PathVariable Long studentId) {
        return ApiResponse.ok(service.detail(studentId));
    }
}

