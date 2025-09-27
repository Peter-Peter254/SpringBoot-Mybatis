package com.example.demo.utils.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwt;

    public JwtAuthFilter(JwtService jwt) {
        this.jwt = jwt;
    }

    // ---- tiny JSON helper kept in this class so no extra file ----
    private static final ObjectMapper MAPPER = new ObjectMapper();
    public static void writeJson(HttpServletResponse res, int status, String error, String message) throws IOException {
        res.setStatus(status);
        res.setContentType("application/json");
        MAPPER.writeValue(res.getOutputStream(), Map.of(
                "status", status,
                "error",  error,
                "message", message
        ));
    }
    // --------------------------------------------------------------

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {
            String header = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);
                try {
                    Jws<Claims> jws = jwt.parse(token); // may throw JwtException
                    Claims c = jws.getPayload();

                    Long userId = c.get("uid", Number.class).longValue();
                    Long orgId  = c.get("org", Number.class).longValue();
                    String email = c.getSubject();
                    String role  = c.get("role", String.class);

                    // Put in thread-local tenant context
                    TenantContext.setUserId(userId);
                    TenantContext.setOrgId(orgId);

                    // Build Authentication with role
                    var auth = new JwtUserAuthentication(email, List.of(new SimpleGrantedAuthority("ROLE_" + role)));
                    auth.setAuthenticated(true);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } catch (JwtException e) {
                    // Token present but invalid/expired → 401 JSON
                    writeJson(response, HttpServletResponse.SC_UNAUTHORIZED, "invalid_token", "Invalid or expired token");
                    return; // stop chain
                }
            }

            filterChain.doFilter(request, response);
        } finally {
            // Always clear after the request
            TenantContext.clear();
        }
    }

    // Minimal Authentication principal for this example
    static class JwtUserAuthentication extends AbstractAuthenticationToken {
        private final String principal;
        public JwtUserAuthentication(String email, List<SimpleGrantedAuthority> auths) {
            super(auths);
            this.principal = email;
        }
        @Override public Object getCredentials() { return ""; }
        @Override public Object getPrincipal() { return principal; }
    }
}
