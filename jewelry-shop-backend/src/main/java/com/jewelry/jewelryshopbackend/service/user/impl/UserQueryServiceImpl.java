package com.jewelry.jewelryshopbackend.service.user.impl;

import com.jewelry.jewelryshopbackend.dto.response.user.UserProfileResponse;
import com.jewelry.jewelryshopbackend.entity.User;
import com.jewelry.jewelryshopbackend.mapper.UserMapper;
import com.jewelry.jewelryshopbackend.service.user.CurrentUserService;
import com.jewelry.jewelryshopbackend.service.user.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryServiceImpl implements UserQueryService {

    private final CurrentUserService currentUserService;
    private final UserMapper userMapper;

    @Override
    public UserProfileResponse getCurrentUserProfile() {
        User user = currentUserService.requireCurrentUser();
        return userMapper.toProfileResponse(user);
    }

    @Override
    public String getAdminDashboardMessage() {
        return "Only ADMIN can access this endpoint";
    }
}
