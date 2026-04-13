package com.jewelry.jewelryshopbackend.dto.response.admin;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class AdminAccountResponse {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private boolean status;
    private List<String> roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}