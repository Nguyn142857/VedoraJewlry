package com.jewelry.jewelryshopbackend.service.impl;

import com.jewelry.jewelryshopbackend.dto.request.auth.LoginRequest;
import com.jewelry.jewelryshopbackend.dto.request.auth.RegisterRequest;
import com.jewelry.jewelryshopbackend.dto.response.auth.AuthResponse;
import com.jewelry.jewelryshopbackend.entity.Role;
import com.jewelry.jewelryshopbackend.entity.User;
import com.jewelry.jewelryshopbackend.entity.UserRole;
import com.jewelry.jewelryshopbackend.enums.UserStatus;
import com.jewelry.jewelryshopbackend.exception.BadRequestException;
import com.jewelry.jewelryshopbackend.repository.RoleRepository;
import com.jewelry.jewelryshopbackend.repository.UserRepository;
import com.jewelry.jewelryshopbackend.repository.UserRoleRepository;
import com.jewelry.jewelryshopbackend.security.CustomUserDetails;
import com.jewelry.jewelryshopbackend.security.JwtService;
import com.jewelry.jewelryshopbackend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .status(UserStatus.ACTIVE)
                .build();

        User savedUser = userRepository.save(user);

        Role defaultRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new BadRequestException("Default role ROLE_USER not found"));

        UserRole userRole = UserRole.builder()
                .user(savedUser)
                .role(defaultRole)
                .build();

        userRoleRepository.save(userRole);

        User userWithRoles = userRepository.findByEmail(savedUser.getEmail())
                .orElseThrow(() -> new BadRequestException("User not found after registration"));

        String token = jwtService.generateToken(new CustomUserDetails(userWithRoles));

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(userWithRoles.getId())
                .fullName(userWithRoles.getFullName())
                .email(userWithRoles.getEmail())
                .roles(extractRoleNames(userWithRoles))
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        String token = jwtService.generateToken(new CustomUserDetails(user));

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .roles(extractRoleNames(user))
                .build();
    }

    private List<String> extractRoleNames(User user) {
        return user.getUserRoles().stream()
                .map(userRole -> userRole.getRole().getName())
                .distinct()
                .toList();
    }
}