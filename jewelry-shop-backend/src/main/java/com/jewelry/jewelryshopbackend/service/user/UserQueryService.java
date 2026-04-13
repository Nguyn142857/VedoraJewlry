package com.jewelry.jewelryshopbackend.service.user;

import com.jewelry.jewelryshopbackend.dto.response.admin.AdminAccountResponse;
import com.jewelry.jewelryshopbackend.dto.response.admin.AdminRevenueStatsResponse;
import com.jewelry.jewelryshopbackend.dto.response.user.UserProfileResponse;
import com.jewelry.jewelryshopbackend.payload.PageResponse;

public interface UserQueryService {

    UserProfileResponse getCurrentUserProfile();

    String getAdminDashboardMessage();

    PageResponse<AdminAccountResponse> getAllForAdmin(
            int page,
            int size,
            String sortBy,
            String sortDir,
            String q,
            String status
    );

    AdminAccountResponse getByIdForAdmin(Long id);

    AdminRevenueStatsResponse getRevenueStats(int days);
}