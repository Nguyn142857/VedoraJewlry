package com.jewelry.jewelryshopbackend.controller.admin;

import com.jewelry.jewelryshopbackend.dto.request.productimage.ProductImageCreateRequest;
import com.jewelry.jewelryshopbackend.dto.request.productimage.ProductImageUpdateRequest;
import com.jewelry.jewelryshopbackend.dto.response.productimage.ProductImageResponse;
import com.jewelry.jewelryshopbackend.payload.ApiResponse;
import com.jewelry.jewelryshopbackend.payload.PageResponse;
import com.jewelry.jewelryshopbackend.service.productimage.ProductImageCommandService;
import com.jewelry.jewelryshopbackend.service.productimage.ProductImageQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/product-images")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductImageController {

    private final ProductImageCommandService productImageCommandService;
    private final ProductImageQueryService productImageQueryService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductImageResponse>> create(
            @Valid @RequestBody ProductImageCreateRequest request
    ) {
        ProductImageResponse response = productImageCommandService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Product image created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductImageResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductImageUpdateRequest request
    ) {
        ProductImageResponse response = productImageCommandService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Product image updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        productImageCommandService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Product image deleted successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductImageResponse>> getById(@PathVariable Long id) {
        ProductImageResponse response = productImageQueryService.getByIdForAdmin(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProductImageResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Boolean isMain
    ) {
        PageResponse<ProductImageResponse> response =
                productImageQueryService.getAllForAdmin(page, size, sortBy, sortDir, q, productId, isMain);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
