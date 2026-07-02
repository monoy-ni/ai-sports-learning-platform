package com.example.aisports.teacher.controller;

import com.example.aisports.checkin.domain.CheckIn;
import com.example.aisports.checkin.dto.CheckInListItem;
import com.example.aisports.checkin.dto.CheckInReviewRequest;
import com.example.aisports.checkin.service.CheckInService;
import com.example.aisports.common.response.ApiResponse;
import com.example.aisports.common.security.UserPrincipal;
import com.example.aisports.teacher.dto.AbnormalCheckInItem;
import com.example.aisports.teacher.dto.CheckInReviewHistoryItem;
import com.example.aisports.teacher.service.TeacherDataService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/teacher/check-ins")
public class TeacherCheckInController {
    private final CheckInService service;
    private final TeacherDataService dataService;

    public TeacherCheckInController(CheckInService service, TeacherDataService dataService) {
        this.service = service;
        this.dataService = dataService;
    }

    @GetMapping("/abnormal")
    public ApiResponse<List<AbnormalCheckInItem>> abnormal(
        @AuthenticationPrincipal UserPrincipal principal,
        @RequestParam(required = false) Long classId,
        @RequestParam(required = false) Long termId) {
        Long resolvedTermId = termId != null ? termId : dataService.resolveTerm(null).getId();
        List<Long> studentIds = dataService.resolveStudentIds(principal.id(), classId, resolvedTermId);
        List<CheckIn> checkIns = service.abnormalCheckIns(studentIds);
        Map<Long, String> names = dataService.loadUsers(studentIds).entrySet().stream()
            .collect(java.util.stream.Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getDisplayName()));
        return ApiResponse.ok(checkIns.stream()
            .map(c -> AbnormalCheckInItem.from(c, names.getOrDefault(c.getStudentId(), "")))
            .toList());
    }

    @PatchMapping("/{checkInId}/review")
    public ApiResponse<CheckInListItem> review(
        @AuthenticationPrincipal UserPrincipal principal,
        @PathVariable Long checkInId,
        @Valid @RequestBody CheckInReviewRequest request) {
        return ApiResponse.ok(service.review(principal, checkInId, request));
    }

    @GetMapping("/{checkInId}/review-history")
    public ApiResponse<List<CheckInReviewHistoryItem>> reviewHistory(@PathVariable Long checkInId) {
        return ApiResponse.ok(service.reviewHistory(checkInId).stream()
            .map(CheckInReviewHistoryItem::from).toList());
    }
}
