package com.replate.dto.response;

import com.replate.domain.enums.UserRole;

public class AuthResponseDTO {

    private String accessToken;
    private String tokenType = "Bearer";
    private long expiresInMs;
    private Long userId;
    private String fullName;
    private String email;
    private UserRole role;
    private String city;

    public AuthResponseDTO() {}

    public AuthResponseDTO(String accessToken, long expiresInMs, Long userId, String fullName, String email, UserRole role, String city) {
        this.accessToken = accessToken;
        this.tokenType = "Bearer";
        this.expiresInMs = expiresInMs;
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.city = city;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public long getExpiresInMs() {
        return expiresInMs;
    }

    public void setExpiresInMs(long expiresInMs) {
        this.expiresInMs = expiresInMs;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}

