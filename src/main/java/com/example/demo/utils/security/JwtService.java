package com.example.demo.utils.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * JWT helper for issuing and verifying HS256 tokens using JJWT 0.12.x.
 *
 * Expected properties:
 *   app.jwt.secret               = <at least 32 bytes; raw or base64>
 *   app.jwt.issuer               = demo-api
 *   app.jwt.expiration-minutes   = 60
 */
@Component
public class JwtService {

    private final SecretKey key;          // HS256 requires a SecretKey
    private final String issuer;
    private final long expirationMinutes;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.issuer}") String issuer,
            @Value("${app.jwt.expiration-minutes}") long expirationMinutes
    ) {
        this.key = toHmacKey(secret);
        this.issuer = Objects.requireNonNullElse(issuer, "demo-api");
        this.expirationMinutes = expirationMinutes > 0 ? expirationMinutes : 60;
    }

    /**
     * Generate a JWT with standard and custom claims.
     * subject = email/username
     * custom  = uid (Long), org (Long), role (String)
     */
    public String generateToken(Long userId, Long orgId, String email, String role) {
        Instant now = Instant.now();
        return Jwts.builder()
                .issuer(issuer)
                .subject(email)
                .claims(Map.of(
                        "uid", userId,
                        "org", orgId,
                        "role", role
                ))
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expirationMinutes * 60)))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    /** Parse and verify a token (signature + exp). Throws JwtException on failure. */
    public Jws<Claims> parse(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith(key)
                .requireIssuer(issuer)
                .build()
                .parseSignedClaims(token);
    }

    /** Convenience: true if token is structurally valid, signed by us, and not expired. */
    public boolean isValid(String token) {
        try {
            parse(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // ===== Claim helpers =====

    public String getSubject(String token) {
        return parse(token).getPayload().getSubject(); // typically email/username
    }

    public Long getUserId(String token) {
        Object v = parse(token).getPayload().get("uid");
        return toLong(v);
    }

    public Long getOrgId(String token) {
        Object v = parse(token).getPayload().get("org");
        return toLong(v);
    }

    public String getRole(String token) {
        Object v = parse(token).getPayload().get("role");
        return v == null ? null : String.valueOf(v);
    }

    public boolean isExpired(String token) {
        Date exp = parse(token).getPayload().getExpiration();
        return exp != null && exp.toInstant().isBefore(Instant.now());
    }

    // ===== Internals =====

    /**
     * Accept either a raw text secret (UTF-8) or a Base64 string.
     * Ensures the key is >= 32 bytes for HS256, throwing if too short.
     */
    private static SecretKey toHmacKey(String raw) {
        byte[] bytes;
        try {
            // Try Base64 first (if user provided a Base64 secret)
            bytes = Decoders.BASE64.decode(raw);
            if (bytes.length == 0) throw new IllegalArgumentException("Empty base64 secret");
        } catch (RuntimeException ignore) { // catch DecodingException and others
            // Fallback: treat as plain UTF-8 text (UUIDs, hex strings, etc.)
            bytes = raw.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        }
        if (bytes.length < 32) { // HS256 requires >= 256 bits
            throw new IllegalArgumentException(
                    "app.jwt.secret must be at least 32 bytes (256 bits) for HS256. Provided length: " + bytes.length
            );
        }
        return Keys.hmacShaKeyFor(bytes);
    }

    private static Long toLong(Object v) {
        if (v == null) return null;
        if (v instanceof Number n) return n.longValue();
        try {
            return Long.parseLong(String.valueOf(v));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
