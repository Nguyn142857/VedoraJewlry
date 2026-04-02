package com.jewelry.jewelryshopbackend.service.user;

import com.jewelry.jewelryshopbackend.dto.response.user.UserProfileResponse;

public interface UserQueryService {

    UserProfileResponse getCurrentUserProfile();

    String getAdminDashboardMessage();
}
