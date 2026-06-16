package com.example.aisports.auth.dto;

public record LoginResponse(String token, CurrentUserResponse user) {
}

