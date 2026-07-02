package com.example.aisports.campusrun.controller;

import com.example.aisports.campusrun.dto.CampusRunScoreRuleRequest;
import com.example.aisports.campusrun.dto.CampusRunScoreRuleResponse;
import com.example.aisports.campusrun.service.CampusRunScoreRuleService;
import com.example.aisports.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teacher/campus-run-rules")
public class CampusRunScoreRuleController {
    private final CampusRunScoreRuleService service;

    public CampusRunScoreRuleController(CampusRunScoreRuleService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<CampusRunScoreRuleResponse> get(@RequestParam(required = false) Long termId) {
        return ApiResponse.ok(service.getActive(termId));
    }

    @PutMapping
    public ApiResponse<CampusRunScoreRuleResponse> update(@Valid @RequestBody CampusRunScoreRuleRequest request) {
        return ApiResponse.ok(service.update(request));
    }
}
