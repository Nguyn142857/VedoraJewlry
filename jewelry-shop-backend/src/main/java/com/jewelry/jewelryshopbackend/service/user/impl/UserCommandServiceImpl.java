package com.jewelry.jewelryshopbackend.service.user.impl;

import com.jewelry.jewelryshopbackend.dto.request.user.UserProfileUpdateRequest;
import com.jewelry.jewelryshopbackend.dto.response.user.UserProfileResponse;
import com.jewelry.jewelryshopbackend.entity.User;
import com.jewelry.jewelryshopbackend.mapper.UserMapper;
import com.jewelry.jewelryshopbackend.repository.UserRepository;
import com.jewelry.jewelryshopbackend.service.user.CurrentUserService;
import com.jewelry.jewelryshopbackend.service.user.UserCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserCommandServiceImpl implements UserCommandService {

    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserProfileResponse updateMyProfile(UserProfileUpdateRequest request) {
        User user = currentUserService.requireCurrentUser();

        user.setFullName(normalizeRequiredText(request.getFullName()));
        user.setPhone(normalizeOptionalText(request.getPhone()));
        user.setAddress(normalizeOptionalText(request.getAddress()));

        User savedUser = userRepository.save(user);
        return userMapper.toProfileResponse(savedUser);
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
}
