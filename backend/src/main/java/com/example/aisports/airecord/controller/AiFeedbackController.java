package com.example.aisports.airecord.controller;

import com.example.aisports.airecord.dto.AiFeedbackResponse;
import com.example.aisports.airecord.service.AiFeedbackService;
import com.example.aisports.common.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/student/ai-feedback")
public class AiFeedbackController {
    private final AiFeedbackService service;

    public AiFeedbackController(AiFeedbackService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<AiFeedbackResponse> latest() {
        return ApiResponse.ok(service.latest());
    }
}

