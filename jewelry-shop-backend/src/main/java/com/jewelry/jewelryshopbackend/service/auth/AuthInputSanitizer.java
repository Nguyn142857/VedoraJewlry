package com.jewelry.jewelryshopbackend.service.auth;

import com.jewelry.jewelryshopbackend.exception.BadRequestException;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class AuthInputSanitizer {

    public String normalizeEmail(String email) {
        String normalized = email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
        if (normalized.isBlank()) {
            throw new BadRequestException("Email is required");
        }
        return normalized;
    }

    public String normalizeFullName(String fullName) {
        String normalized = fullName == null ? "" : fullName.trim().replaceAll("\\s+", " ");
        if (normalized.isBlank()) {
            throw new BadRequestException("Full name is required");
        }
        return normalized;
    }
}
