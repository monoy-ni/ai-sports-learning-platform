package com.example.aisports.common.security;

import com.example.aisports.auth.domain.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey key;
    private final long expirationSeconds;

    public JwtService(
        @Value("${app.security.jwt-secret}") String secret,
        @Value("${app.security.jwt-expiration-seconds:86400}") long expirationSeconds
    ) {
        this.key = Keys.hmacShaKeyFor(padSecret(secret).getBytes(StandardCharsets.UTF_8));
        this.expirationSeconds = expirationSeconds;
    }

    public String createToken(UserPrincipal principal) {
        Instant now = Instant.now();
        return Jwts.builder()
            .subject(principal.username())
            .claim("id", principal.id())
            .claim("displayName", principal.displayName())
            .claim("role", principal.role().name())
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusSeconds(expirationSeconds)))
            .signWith(key)
            .compact();
    }

    public UserPrincipal parse(String token) {
        Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        Long id = claims.get("id", Number.class).longValue();
        String displayName = claims.get("displayName", String.class);
        Role role = Role.valueOf(claims.get("role", String.class));
        return new UserPrincipal(id, claims.getSubject(), displayName, role);
    }

    private String padSecret(String secret) {
        if (secret.length() >= 32) {
            return secret;
        }
        return (secret + "00000000000000000000000000000000").substring(0, 32);
    }
}

