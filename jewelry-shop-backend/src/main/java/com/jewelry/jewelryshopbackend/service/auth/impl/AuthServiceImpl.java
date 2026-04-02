package com.jewelry.jewelryshopbackend.service.auth.impl;

import com.jewelry.jewelryshopbackend.dto.request.auth.LoginRequest;
import com.jewelry.jewelryshopbackend.dto.request.auth.RegisterRequest;
import com.jewelry.jewelryshopbackend.dto.response.auth.AuthResponse;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final String DEFAULT_USER_ROLE = "ROLE_USER";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuthInputSanitizer authInputSanitizer;
    private final AuthLoginAttemptService authLoginAttemptService;

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
}
