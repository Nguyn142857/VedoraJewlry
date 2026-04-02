package com.jewelry.jewelryshopbackend.service.user.impl;

import com.jewelry.jewelryshopbackend.service.user.UserQueryService;
import org.springframework.stereotype.Service;

@Service
public class UserQueryServiceImpl implements UserQueryService {

    @Override
    public String getCurrentUserProfileMessage() {
        return "USER or ADMIN can access this endpoint";
    }

    @Override
    public String getAdminDashboardMessage() {
        return "Only ADMIN can access this endpoint";
    }
}
