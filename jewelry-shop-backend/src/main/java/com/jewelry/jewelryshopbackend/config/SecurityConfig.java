package com.jewelry.jewelryshopbackend.config;

import com.jewelry.jewelryshopbackend.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->
                                writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized", request.getRequestURI()))
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                writeErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "Forbidden", request.getRequestURI()))
                )
                .authorizeHttpRequests(auth -> auth
                        // public auth APIs
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/actuator/health").permitAll()

                        // public read APIs
                        .requestMatchers(HttpMethod.GET, "/api/categories/**", "/api/products/**").permitAll()

                        // authenticated APIs
                        .requestMatchers("/api/user/**").authenticated()

                        // admin area
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // everything else
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    private void writeErrorResponse(HttpServletResponse response, int status, String message, String path) throws java.io.IOException {
        String safeMessage = escapeJson(message);
        String safePath = escapeJson(path);
        String errorLabel = org.springframework.http.HttpStatus.valueOf(status).toString();
        String timestamp = LocalDateTime.now().toString();
        String safeError = escapeJson(errorLabel);
        String body = "{\"timestamp\":\"" + timestamp + "\""
                + ",\"status\":" + status
                + ",\"error\":\"" + safeError + "\""
                + ",\"message\":\"" + safeMessage + "\""
                + ",\"path\":\"" + safePath + "\""
                + ",\"validationErrors\":null}";
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write(body);
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
