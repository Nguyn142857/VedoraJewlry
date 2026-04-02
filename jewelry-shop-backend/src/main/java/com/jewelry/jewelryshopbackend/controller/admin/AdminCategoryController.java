package com.jewelry.jewelryshopbackend.controller.admin;

import com.jewelry.jewelryshopbackend.dto.request.category.CategoryCreateRequest;
import com.jewelry.jewelryshopbackend.dto.request.category.CategoryStatusUpdateRequest;
import com.jewelry.jewelryshopbackend.dto.request.category.CategoryUpdateRequest;
import com.jewelry.jewelryshopbackend.dto.response.category.CategoryResponse;
import com.jewelry.jewelryshopbackend.payload.ApiResponse;
import com.jewelry.jewelryshopbackend.payload.PageResponse;
import com.jewelry.jewelryshopbackend.service.category.CategoryCommandService;
import com.jewelry.jewelryshopbackend.service.category.CategoryQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCategoryController {

    private final CategoryCommandService categoryCommandService;
    private final CategoryQueryService categoryQueryService;

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> create(
            @Valid @RequestBody CategoryCreateRequest request
    ) {
        CategoryResponse response = categoryCommandService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Category created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody CategoryUpdateRequest request
    ) {
        CategoryResponse response = categoryCommandService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Category updated successfully"));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<CategoryResponse>> changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody CategoryStatusUpdateRequest request
    ) {
        CategoryResponse response = categoryCommandService.changeStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Category status updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        categoryCommandService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Category deleted successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getById(@PathVariable Long id) {
        CategoryResponse response = categoryQueryService.getByIdForAdmin(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<CategoryResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String status
    ) {
        PageResponse<CategoryResponse> response = categoryQueryService.getAllForAdmin(page, size, sortBy, sortDir, q, status);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
