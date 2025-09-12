package com.example.demo.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AuthDtos {
    public static class LoginRequest {
        @NotBlank @Email public String email;
        @NotBlank public String password;
    }
    public static class LoginResponse {
        public String token; public long expiresInSeconds;
        public Long userId; public Long orgId; public String email; public String role; public String tokenType = "Bearer";
        public LoginResponse(String token, long exp, Long uid, Long oid, String email, String role) {
            this.token = token; this.expiresInSeconds = exp; this.userId = uid; this.orgId = oid; this.email = email; this.role = role;
        }
    }
}
