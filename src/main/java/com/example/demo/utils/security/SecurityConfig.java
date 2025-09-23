package com.example.demo.utils.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
public class SecurityConfig {

    @Bean
    public AuthenticationEntryPoint restAuthenticationEntryPoint() {
        return (req, res, ex) ->
                JwtAuthFilter.writeJson(res, HttpServletResponse.SC_UNAUTHORIZED,
                        "unauthorized", "Authentication is required");
    }

    @Bean
    public AccessDeniedHandler restAccessDeniedHandler() {
        return (req, res, ex) ->
                JwtAuthFilter.writeJson(res, HttpServletResponse.SC_FORBIDDEN,
                        "forbidden", "You do not have permission to access this resource");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtService jwt) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(restAuthenticationEntryPoint())
                        .accessDeniedHandler(restAccessDeniedHandler())
                )
                .addFilterBefore(new JwtAuthFilter(jwt),
                        org.springframework.security.web.authentication.AnonymousAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
}
