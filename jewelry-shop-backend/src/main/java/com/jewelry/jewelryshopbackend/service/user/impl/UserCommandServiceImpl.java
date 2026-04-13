package com.jewelry.jewelryshopbackend.service.user.impl;

import com.jewelry.jewelryshopbackend.dto.request.admin.AdminAccountUpsertRequest;
import com.jewelry.jewelryshopbackend.dto.request.user.UserProfileUpdateRequest;
import com.jewelry.jewelryshopbackend.dto.response.admin.AdminAccountResponse;
import com.jewelry.jewelryshopbackend.dto.response.user.UserProfileResponse;
import com.jewelry.jewelryshopbackend.entity.Role;
import com.jewelry.jewelryshopbackend.entity.User;
import com.jewelry.jewelryshopbackend.entity.UserRole;
import com.jewelry.jewelryshopbackend.enums.UserStatus;
import com.jewelry.jewelryshopbackend.exception.BadRequestException;
import com.jewelry.jewelryshopbackend.exception.ResourceNotFoundException;
import com.jewelry.jewelryshopbackend.mapper.UserMapper;
import com.jewelry.jewelryshopbackend.repository.RoleRepository;
import com.jewelry.jewelryshopbackend.repository.UserRepository;
import com.jewelry.jewelryshopbackend.service.user.CurrentUserService;
import com.jewelry.jewelryshopbackend.service.user.UserCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserCommandServiceImpl implements UserCommandService {

    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserProfileResponse updateMyProfile(UserProfileUpdateRequest request) {
        User user = currentUserService.requireCurrentUser();

        user.setFullName(normalizeRequiredText(request.getFullName()));
        user.setPhone(normalizeOptionalText(request.getPhone()));
        user.setAddress(normalizeOptionalText(request.getAddress()));

        User savedUser = userRepository.save(user);
        return userMapper.toProfileResponse(savedUser);
    }

    @Override
    public AdminAccountResponse createForAdmin(AdminAccountUpsertRequest request) {
        String email = normalizeEmail(request.getEmail());
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new BadRequestException("Email already exists");
        }
        if (!StringUtils.hasText(request.getPassword())) {
            throw new BadRequestException("Password is required");
        }

        User user = User.builder()
                .fullName(normalizeRequiredText(request.getFullName()))
                .email(email)
                .password(passwordEncoder.encode(request.getPassword().trim()))
                .phone(normalizeOptionalText(request.getPhone()))
                .address(normalizeOptionalText(request.getAddress()))
                .status(request.isStatus() ? UserStatus.ACTIVE : UserStatus.INACTIVE)
                .build();

        assignRoles(user, request.getRoles());

        User savedUser = userRepository.saveAndFlush(user);
        return toAdminAccountResponse(savedUser);
    }

    @Override
    public AdminAccountResponse updateForAdmin(Long id, AdminAccountUpsertRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        String email = normalizeEmail(request.getEmail());
        if (!user.getEmail().equalsIgnoreCase(email)
                && userRepository.existsByEmailIgnoreCase(email)) {
            throw new BadRequestException("Email already exists");
        }

        user.setFullName(normalizeRequiredText(request.getFullName()));
        user.setEmail(email);
        user.setPhone(normalizeOptionalText(request.getPhone()));
        user.setAddress(normalizeOptionalText(request.getAddress()));
        user.setStatus(request.isStatus() ? UserStatus.ACTIVE : UserStatus.INACTIVE);

        if (StringUtils.hasText(request.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getPassword().trim()));
        }

        // Xóa role cũ bằng orphanRemoval + flush trước khi thêm role mới
        user.getUserRoles().clear();
        userRepository.saveAndFlush(user);

        assignRoles(user, request.getRoles());

        User savedUser = userRepository.saveAndFlush(user);
        return toAdminAccountResponse(savedUser);
    }

    @Override
    public void deleteForAdmin(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        user.getUserRoles().clear();
        userRepository.saveAndFlush(user);
        userRepository.delete(user);
    }

    private void assignRoles(User user, List<String> roleNames) {
        if (roleNames == null) {
            throw new BadRequestException("At least one role is required");
        }

        List<String> normalizedRoles = roleNames.stream()
                .filter(StringUtils::hasText)
                .map(String::trim)
                .map(String::toUpperCase)
                .map(name -> name.startsWith("ROLE_") ? name : "ROLE_" + name)
                .distinct()
                .sorted(Comparator.naturalOrder())
                .toList();

        if (normalizedRoles.isEmpty()) {
            throw new BadRequestException("At least one role is required");
        }

        for (String roleName : normalizedRoles) {
            Role role = roleRepository.findByName(roleName)
                    .orElseGet(() -> roleRepository.save(
                            Role.builder()
                                    .name(roleName)
                                    .description("Created by admin")
                                    .build()
                    ));

            user.getUserRoles().add(
                    UserRole.builder()
                            .user(user)
                            .role(role)
                            .assignedBy(currentUserService.requireCurrentUser())
                            .build()
            );
        }
    }

    private AdminAccountResponse toAdminAccountResponse(User user) {
        List<String> roles = user.getUserRoles().stream()
                .map(UserRole::getRole)
                .filter(role -> role != null && role.getName() != null)
                .map(Role::getName)
                .distinct()
                .sorted()
                .toList();

        return AdminAccountResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .status(user.getStatus() == UserStatus.ACTIVE)
                .roles(roles)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    private String normalizeRequiredText(String value) {
        return value == null ? "" : value.trim().replaceAll("\\s+", " ");
    }

    private String normalizeOptionalText(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim().replaceAll("\\s+", " ");
        return normalized.isBlank() ? null : normalized;
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }
}