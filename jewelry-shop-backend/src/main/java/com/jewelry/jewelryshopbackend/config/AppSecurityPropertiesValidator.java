package com.jewelry.jewelryshopbackend.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppSecurityPropertiesValidator {

    private static final int MIN_JWT_SECRET_LENGTH = 32;

    private final Environment environment;

    @PostConstruct
    public void validate() {
        String jwtSecret = environment.getProperty("app.jwt.secret", "");
        if (jwtSecret == null || jwtSecret.isBlank()) {
            throw new IllegalStateException("JWT_SECRET must be configured and must not be blank");
        }

        if (jwtSecret.length() < MIN_JWT_SECRET_LENGTH) {
            throw new IllegalStateException("JWT_SECRET must be at least 32 characters long");
        }
    }
}
