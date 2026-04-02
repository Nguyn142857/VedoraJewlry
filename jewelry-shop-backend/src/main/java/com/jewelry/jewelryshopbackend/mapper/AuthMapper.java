package com.jewelry.jewelryshopbackend.mapper;

import com.jewelry.jewelryshopbackend.dto.response.auth.AuthResponse;
import com.jewelry.jewelryshopbackend.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuthMapper {

    public AuthResponse toAuthResponse(User user, String token) {
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
