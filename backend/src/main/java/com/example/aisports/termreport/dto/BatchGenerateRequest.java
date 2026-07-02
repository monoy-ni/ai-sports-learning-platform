package com.example.aisports.termreport.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record BatchGenerateRequest(
    @NotEmpty List<Long> studentIds,
    Long termId,
    String teacherNote
) {
}
