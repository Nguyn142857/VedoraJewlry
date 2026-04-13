package com.jewelry.jewelryshopbackend.controller.admin;

import com.jewelry.jewelryshopbackend.dto.request.admin.AdminAccountUpsertRequest;
import com.jewelry.jewelryshopbackend.dto.response.admin.AdminAccountResponse;
import com.jewelry.jewelryshopbackend.payload.ApiResponse;
import com.jewelry.jewelryshopbackend.payload.PageResponse;
import com.jewelry.jewelryshopbackend.service.user.UserCommandService;
import com.jewelry.jewelryshopbackend.service.user.UserQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/accounts")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminAccountController {

    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<AdminAccountResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String status
    ) {
        PageResponse<AdminAccountResponse> response =
                userQueryService.getAllForAdmin(page, size, sortBy, sortDir, q, status);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminAccountResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(userQueryService.getByIdForAdmin(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AdminAccountResponse>> create(
            @Valid @RequestBody AdminAccountUpsertRequest request
    ) {
        AdminAccountResponse response = userCommandService.createForAdmin(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Account created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AdminAccountResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody AdminAccountUpsertRequest request
    ) {
        AdminAccountResponse response = userCommandService.updateForAdmin(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Account updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        userCommandService.deleteForAdmin(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Account deleted successfully"));
    }
}