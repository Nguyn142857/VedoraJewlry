package com.jewelry.jewelryshopbackend.controller.public_api;


import com.jewelry.jewelryshopbackend.dto.response.category.CategoryResponse;
import com.jewelry.jewelryshopbackend.dto.response.category.CategorySummaryResponse;
import com.jewelry.jewelryshopbackend.payload.ApiResponse;
import com.jewelry.jewelryshopbackend.payload.PageResponse;
import com.jewelry.jewelryshopbackend.service.category.CategoryQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryQueryService categoryQueryService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<CategorySummaryResponse>>> getAllActive(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String q
    ) {
        PageResponse<CategorySummaryResponse> response = categoryQueryService.getAllActive(page, size, sortBy, sortDir, q);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getBySlug(@PathVariable String slug) {
        CategoryResponse response = categoryQueryService.getBySlugForPublic(slug);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
