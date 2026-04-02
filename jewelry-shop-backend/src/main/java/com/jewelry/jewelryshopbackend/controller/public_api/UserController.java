package com.jewelry.jewelryshopbackend.controller.public_api;

import com.jewelry.jewelryshopbackend.payload.ApiResponse;
import com.jewelry.jewelryshopbackend.service.user.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserQueryService userQueryService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<String>> userProfile() {
        return ResponseEntity.ok(ApiResponse.success(userQueryService.getCurrentUserProfileMessage()));
    }
}
