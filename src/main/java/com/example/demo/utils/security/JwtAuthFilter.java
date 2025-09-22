package com.example.demo.utils.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
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

public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwt;

    public JwtAuthFilter(JwtService jwt) {
        this.jwt = jwt;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {
            String header = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);
                Jws<io.jsonwebtoken.Claims> jws = jwt.parse(token);
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
