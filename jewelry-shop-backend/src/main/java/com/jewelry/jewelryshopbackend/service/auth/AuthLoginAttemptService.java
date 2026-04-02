package com.jewelry.jewelryshopbackend.service.auth;

import com.jewelry.jewelryshopbackend.entity.AuthLoginAttempt;
import com.jewelry.jewelryshopbackend.exception.UnauthorizedException;
import com.jewelry.jewelryshopbackend.repository.AuthLoginAttemptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class AuthLoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final Duration ATTEMPT_WINDOW = Duration.ofMinutes(15);
    private static final Duration LOCK_DURATION = Duration.ofMinutes(15);

    private final AuthLoginAttemptRepository authLoginAttemptRepository;

    @Transactional
    public void ensureAllowed(String principal) {
        if (principal == null || principal.isBlank()) {
            return;
        }

        AuthLoginAttempt attempt = authLoginAttemptRepository.findById(principal).orElse(null);
        if (attempt == null) {
            return;
        }

        Instant now = Instant.now();
        if (attempt.getLockedUntil() != null && now.isBefore(attempt.getLockedUntil())) {
            throw new UnauthorizedException("Too many login attempts. Please try again later.");
        }

        if (attempt.getLockedUntil() != null && !now.isBefore(attempt.getLockedUntil())) {
            authLoginAttemptRepository.deleteById(principal);
        }
    }

    @Transactional
    public void recordFailure(String principal) {
        if (principal == null || principal.isBlank()) {
            return;
        }

        AuthLoginAttempt attempt = authLoginAttemptRepository.findByPrincipalForUpdate(principal)
                .orElseGet(() -> {
                    AuthLoginAttempt created = new AuthLoginAttempt();
                    created.setPrincipal(principal);
                    created.setFailures(0);
                    return created;
                });

        Instant now = Instant.now();

        if (attempt.getFirstFailureAt() == null || now.isAfter(attempt.getFirstFailureAt().plus(ATTEMPT_WINDOW))) {
            attempt.setFirstFailureAt(now);
            attempt.setFailures(1);
        } else {
            attempt.setFailures(attempt.getFailures() + 1);
        }

        if (attempt.getFailures() >= MAX_ATTEMPTS) {
            attempt.setLockedUntil(now.plus(LOCK_DURATION));
        }

        attempt.setUpdatedAt(now);
        authLoginAttemptRepository.save(attempt);
    }

    @Transactional
    public void recordSuccess(String principal) {
        if (principal == null || principal.isBlank()) {
            return;
        }
        authLoginAttemptRepository.deleteById(principal);
    }
}
