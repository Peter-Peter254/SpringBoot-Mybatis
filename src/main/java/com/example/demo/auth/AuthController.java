package com.example.demo.auth;

import com.example.demo.security.JwtService;
import com.example.demo.user.User;
import com.example.demo.user.UserMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserMapper users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;
    private final long expMinutes;

    public AuthController(UserMapper users, PasswordEncoder encoder, JwtService jwt,
                          @org.springframework.beans.factory.annotation.Value("${app.jwt.expiration-minutes}") long expMinutes) {
        this.users = users; this.encoder = encoder; this.jwt = jwt; this.expMinutes = expMinutes;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthDtos.LoginRequest req) {
        User u = users.findByEmail(req.email);
        if (u == null || !encoder.matches(req.password, u.getPasswordHash())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
        String token = jwt.generateToken(u.getId(), u.getOrgId(), u.getEmail(), u.getRole());
        long expiresIn = expMinutes * 60;
        return ResponseEntity.ok(new AuthDtos.LoginResponse(token, expiresIn, u.getId(), u.getOrgId(), u.getEmail(), u.getRole()));
    }
}
