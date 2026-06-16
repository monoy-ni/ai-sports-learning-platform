package com.example.aisports.auth.service;

import com.example.aisports.auth.domain.Role;
import com.example.aisports.auth.dto.CurrentUserResponse;
import com.example.aisports.auth.dto.LoginRequest;
import com.example.aisports.auth.dto.LoginResponse;
import com.example.aisports.common.exception.BusinessException;
import com.example.aisports.common.security.JwtService;
import com.example.aisports.common.security.UserPrincipal;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {
    private final JwtService jwtService;
    private final Map<String, DemoAccount> demoAccounts = Map.of(
        "student001", new DemoAccount(1L, "student001", "张同学", Role.STUDENT, "password123"),
        "teacher001", new DemoAccount(2L, "teacher001", "王老师", Role.TEACHER, "password123")
    );

    public AuthService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        DemoAccount account = demoAccounts.get(request.username());
        if (account == null || !account.password().equals(request.password())) {
            throw new BusinessException("AUTH_INVALID_CREDENTIALS", "用户名或密码错误");
        }
        UserPrincipal principal = account.toPrincipal();
        return new LoginResponse(jwtService.createToken(principal), CurrentUserResponse.from(principal));
    }

    private record DemoAccount(Long id, String username, String displayName, Role role, String password) {
        UserPrincipal toPrincipal() {
            return new UserPrincipal(id, username, displayName, role);
        }
    }
}

