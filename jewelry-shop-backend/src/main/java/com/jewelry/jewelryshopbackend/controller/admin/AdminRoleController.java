package com.jewelry.jewelryshopbackend.controller.admin;

import com.jewelry.jewelryshopbackend.payload.ApiResponse;
import com.jewelry.jewelryshopbackend.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/roles")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminRoleController {

    private final RoleRepository roleRepository;

    @GetMapping
    public ApiResponse<List<String>> getAllRoles() {
        List<String> roles = roleRepository.findAll()
                .stream()
                .map(role -> role.getName())
                .distinct()
                .sorted()
                .toList();

        return ApiResponse.success(roles);
    }
}