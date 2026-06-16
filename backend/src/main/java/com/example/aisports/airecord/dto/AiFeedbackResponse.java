package com.example.aisports.airecord.dto;

import java.util.List;

public record AiFeedbackResponse(List<String> suggestions, String imageStatus, String imageUrl) {
}

