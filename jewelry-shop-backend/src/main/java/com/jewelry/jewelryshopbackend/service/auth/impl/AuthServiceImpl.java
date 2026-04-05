package com.jewelry.jewelryshopbackend.service.auth.impl;

import com.jewelry.jewelryshopbackend.dto.request.auth.LoginRequest;
import com.jewelry.jewelryshopbackend.dto.request.auth.GoogleLoginRequest;
import com.jewelry.jewelryshopbackend.dto.request.auth.RegisterRequest;
import com.jewelry.jewelryshopbackend.dto.response.auth.AuthResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jewelry.jewelryshopbackend.entity.Role;
import com.jewelry.jewelryshopbackend.entity.User;
import com.jewelry.jewelryshopbackend.entity.UserRole;
import com.jewelry.jewelryshopbackend.enums.UserStatus;
import com.jewelry.jewelryshopbackend.exception.BadRequestException;
import com.jewelry.jewelryshopbackend.exception.UnauthorizedException;
import com.jewelry.jewelryshopbackend.repository.RoleRepository;
import com.jewelry.jewelryshopbackend.repository.UserRepository;
import com.jewelry.jewelryshopbackend.security.CustomUserDetails;
import com.jewelry.jewelryshopbackend.security.JwtService;
import com.jewelry.jewelryshopbackend.service.auth.AuthInputSanitizer;
import com.jewelry.jewelryshopbackend.service.auth.AuthLoginAttemptService;
import com.jewelry.jewelryshopbackend.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final String DEFAULT_USER_ROLE = "ROLE_USER";
    private static final String GOOGLE_TOKENINFO_URL = "https://oauth2.googleapis.com/tokeninfo";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuthInputSanitizer authInputSanitizer;
    private final AuthLoginAttemptService authLoginAttemptService;
    private final RestClient restClient = RestClient.builder().baseUrl(GOOGLE_TOKENINFO_URL).build();

    @Value("${app.google.client-id:}")
    private String googleClientId;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = authInputSanitizer.normalizeEmail(request.getEmail());
        String fullName = authInputSanitizer.normalizeFullName(request.getFullName());

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new BadRequestException("Email already exists");
        }

        Role role = roleRepository.findByName(DEFAULT_USER_ROLE)
                .orElseGet(() -> roleRepository.save(
                        Role.builder()
                                .name(DEFAULT_USER_ROLE)
                                .description("Default customer role")
                                .build()
                ));

        User user = User.builder()
                .fullName(fullName)
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .status(UserStatus.ACTIVE)
                .build();

        UserRole userRole = UserRole.builder()
                .user(user)
                .role(role)
                .build();
        user.getUserRoles().add(userRole);

        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(new CustomUserDetails(savedUser));

        return buildAuthResponse(savedUser, token);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String email = authInputSanitizer.normalizeEmail(request.getEmail());

        authLoginAttemptService.ensureAllowed(email);
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.getPassword())
            );
        } catch (BadCredentialsException ex) {
            authLoginAttemptService.recordFailure(email);
            throw new UnauthorizedException("Invalid credentials");
        } catch (DisabledException ex) {
            authLoginAttemptService.recordFailure(email);
            throw new UnauthorizedException("Account is disabled");
        }

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (user.getStatus() != UserStatus.ACTIVE) {
            authLoginAttemptService.recordFailure(email);
            throw new UnauthorizedException("Account is not active");
        }

        authLoginAttemptService.recordSuccess(email);

        String token = jwtService.generateToken(new CustomUserDetails(user));
        return buildAuthResponse(user, token);
    }

    @Override
    @Transactional
    public AuthResponse googleLogin(GoogleLoginRequest request) {
        if (!StringUtils.hasText(googleClientId)) {
            throw new BadRequestException("Google login is not configured");
        }

        GoogleTokenInfoResponse tokenInfo = verifyGoogleIdToken(request.getIdToken());
        String email = authInputSanitizer.normalizeEmail(tokenInfo.email);

        if (!Boolean.parseBoolean(tokenInfo.emailVerified)) {
            throw new UnauthorizedException("Google account email is not verified");
        }

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseGet(() -> createGoogleUser(email, tokenInfo.name));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new UnauthorizedException("Account is not active");
        }

        String token = jwtService.generateToken(new CustomUserDetails(user));
        return buildAuthResponse(user, token);
    }

    private AuthResponse buildAuthResponse(User user, String token) {
        List<String> roles = user.getUserRoles().stream()
                .map(UserRole::getRole)
                .filter(role -> role != null && role.getName() != null)
                .map(Role::getName)
                .distinct()
                .sorted(Comparator.naturalOrder())
                .toList();

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .roles(roles)
                .build();
    }

    private GoogleTokenInfoResponse verifyGoogleIdToken(String idToken) {
        try {
            GoogleTokenInfoResponse tokenInfo = restClient.get()
                    .uri(uriBuilder -> uriBuilder.queryParam("id_token", idToken).build())
                    .retrieve()
                    .body(GoogleTokenInfoResponse.class);

            if (tokenInfo == null || !StringUtils.hasText(tokenInfo.aud) || !googleClientId.equals(tokenInfo.aud)) {
                throw new UnauthorizedException("Invalid Google token audience");
            }

            if (!StringUtils.hasText(tokenInfo.email)) {
                throw new UnauthorizedException("Google account email is missing");
            }

            return tokenInfo;
        } catch (RestClientException ex) {
            throw new UnauthorizedException("Google token verification failed");
        }
    }

    private User createGoogleUser(String email, String fullName) {
        Role role = roleRepository.findByName(DEFAULT_USER_ROLE)
                .orElseGet(() -> roleRepository.save(
                        Role.builder()
                                .name(DEFAULT_USER_ROLE)
                                .description("Default customer role")
                                .build()
                ));

        String normalizedFullName = authInputSanitizer.normalizeFullName(
                StringUtils.hasText(fullName) ? fullName : email.substring(0, email.indexOf('@'))
        );

        User user = User.builder()
                .fullName(normalizedFullName)
                .email(email)
                .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                .status(UserStatus.ACTIVE)
                .build();

        UserRole userRole = UserRole.builder()
                .user(user)
                .role(role)
                .build();
        user.getUserRoles().add(userRole);

        return userRepository.save(user);
    }

    private static class GoogleTokenInfoResponse {
        public String aud;
        public String email;
        @JsonProperty("email_verified")
        public String emailVerified;
        public String name;
    }
}
