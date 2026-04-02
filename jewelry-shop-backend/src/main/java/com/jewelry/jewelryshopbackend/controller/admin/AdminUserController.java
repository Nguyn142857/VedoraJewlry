package com.jewelry.jewelryshopbackend.controller.admin;

import com.jewelry.jewelryshopbackend.payload.ApiResponse;
import com.jewelry.jewelryshopbackend.service.user.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserQueryService userQueryService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<String>> adminDashboard() {
        return ResponseEntity.ok(ApiResponse.success(userQueryService.getAdminDashboardMessage()));
    }
}
