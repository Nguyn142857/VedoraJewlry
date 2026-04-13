package com.jewelry.jewelryshopbackend.service.user;

import com.jewelry.jewelryshopbackend.dto.request.admin.AdminAccountUpsertRequest;
import com.jewelry.jewelryshopbackend.dto.request.user.UserProfileUpdateRequest;
import com.jewelry.jewelryshopbackend.dto.response.admin.AdminAccountResponse;
import com.jewelry.jewelryshopbackend.dto.response.user.UserProfileResponse;

public interface UserCommandService {

    UserProfileResponse updateMyProfile(UserProfileUpdateRequest request);

    AdminAccountResponse createForAdmin(AdminAccountUpsertRequest request);

    AdminAccountResponse updateForAdmin(Long id, AdminAccountUpsertRequest request);

    void deleteForAdmin(Long id);
}