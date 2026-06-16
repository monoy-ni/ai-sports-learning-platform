package com.example.aisports.teacher.controller;

import com.example.aisports.checkin.dto.CheckInListItem;
import com.example.aisports.checkin.dto.CheckInReviewRequest;
import com.example.aisports.checkin.service.CheckInService;
import com.example.aisports.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teacher/check-ins")
public class TeacherCheckInController {
    private final CheckInService service;

    public TeacherCheckInController(CheckInService service) {
        this.service = service;
    }

    @PatchMapping("/{checkInId}/review")
    public ApiResponse<CheckInListItem> review(@PathVariable Long checkInId, @Valid @RequestBody CheckInReviewRequest request) {
        return ApiResponse.ok(service.review(checkInId, request));
    }
}

