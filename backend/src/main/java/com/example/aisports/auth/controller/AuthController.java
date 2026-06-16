package com.example.aisports.auth.controller;

import com.example.aisports.auth.dto.CurrentUserResponse;
import com.example.aisports.auth.dto.LoginRequest;
import com.example.aisports.auth.dto.LoginResponse;
import com.example.aisports.auth.service.AuthService;
import com.example.aisports.common.response.ApiResponse;
import com.example.aisports.common.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ApiResponse<CurrentUserResponse> me(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.ok(CurrentUserResponse.from(principal));
    }
}

