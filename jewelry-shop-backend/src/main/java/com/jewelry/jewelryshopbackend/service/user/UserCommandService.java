package com.jewelry.jewelryshopbackend.service.user;

import com.jewelry.jewelryshopbackend.dto.request.user.UserProfileUpdateRequest;
import com.jewelry.jewelryshopbackend.dto.response.user.UserProfileResponse;

public interface UserCommandService {

    UserProfileResponse updateMyProfile(UserProfileUpdateRequest request);
}
