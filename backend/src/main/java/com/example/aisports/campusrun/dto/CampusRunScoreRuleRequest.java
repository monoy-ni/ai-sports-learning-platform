package com.example.aisports.campusrun.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CampusRunScoreRuleRequest(
    @NotNull Long termId,
    @NotBlank String ruleName,
    @NotBlank String rulesJson
) {
}
