package com.example.aisports.auth.service;

import com.example.aisports.auth.domain.UserAccount;
import com.example.aisports.auth.dto.CurrentUserResponse;
import com.example.aisports.auth.dto.LoginRequest;
import com.example.aisports.auth.dto.LoginResponse;
import com.example.aisports.auth.repository.UserAccountRepository;
import com.example.aisports.common.exception.BusinessException;
import com.example.aisports.common.security.JwtService;
import com.example.aisports.common.security.UserPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private static final String DEMO_PREFIX = "{demo}";

    private final JwtService jwtService;
    private final UserAccountRepository userRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(JwtService jwtService, UserAccountRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    public LoginResponse login(LoginRequest request) {
        UserAccount account = userRepository.findByUsername(request.username())
            .orElseThrow(() -> new BusinessException("AUTH_INVALID_CREDENTIALS", "用户名或密码错误"));
        if (Boolean.FALSE.equals(account.getEnabled())) {
            throw new BusinessException("AUTH_ACCOUNT_DISABLED", "账号已禁用");
        }
        if (!matches(request.password(), account.getPasswordHash())) {
            throw new BusinessException("AUTH_INVALID_CREDENTIALS", "用户名或密码错误");
        }
        UserPrincipal principal = new UserPrincipal(
            account.getId(),
            account.getUsername(),
            account.getDisplayName(),
            account.getRole()
        );
        return new LoginResponse(jwtService.createToken(principal), CurrentUserResponse.from(principal));
    }

    private boolean matches(String rawPassword, String storedHash) {
        if (storedHash == null) {
            return false;
        }
        if (storedHash.startsWith(DEMO_PREFIX)) {
            return storedHash.substring(DEMO_PREFIX.length()).equals(rawPassword);
        }
        return passwordEncoder.matches(rawPassword, storedHash);
    }
}
