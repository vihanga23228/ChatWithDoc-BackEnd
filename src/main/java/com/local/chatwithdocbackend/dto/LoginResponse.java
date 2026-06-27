package com.local.chatwithdocbackend.dto;

import com.local.chatwithdocbackend.entity.Role;

public class LoginResponse {

    private String token;
    private long expiresIn;
    private String email;
    private Role role;

    public LoginResponse() {
    }

    public LoginResponse(String token, long expiresIn, String email, Role role) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.email = email;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
