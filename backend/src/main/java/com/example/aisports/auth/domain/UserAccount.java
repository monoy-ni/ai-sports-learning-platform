package com.example.aisports.auth.domain;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "user_accounts")
public class UserAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String passwordHash;
    private String displayName;

    @Enumerated(EnumType.STRING)
    private Role role;

    private Boolean enabled;
    private Instant createdAt;
    private Instant updatedAt;

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getDisplayName() { return displayName; }
    public Role getRole() { return role; }
    public Boolean getEnabled() { return enabled; }
}
