package com.jewelry.jewelryshopbackend.mapper;

import com.jewelry.jewelryshopbackend.dto.response.user.UserProfileResponse;
import com.jewelry.jewelryshopbackend.entity.Role;
import com.jewelry.jewelryshopbackend.entity.User;
import com.jewelry.jewelryshopbackend.entity.UserRole;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class UserMapper {

    public UserProfileResponse toProfileResponse(User user) {
        List<String> roles = user.getUserRoles().stream()
                .map(UserRole::getRole)
                .filter(role -> role != null && role.getName() != null)
                .map(Role::getName)
                .distinct()
                .sorted(Comparator.naturalOrder())
                .toList();

        return UserProfileResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .status(user.getStatus().name())
                .roles(roles)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
