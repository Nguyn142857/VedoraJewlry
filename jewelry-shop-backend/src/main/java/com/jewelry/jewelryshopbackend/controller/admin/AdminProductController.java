package com.jewelry.jewelryshopbackend.controller.admin;

import com.jewelry.jewelryshopbackend.dto.request.product.ProductCreateRequest;
import com.jewelry.jewelryshopbackend.dto.request.product.ProductUpdateRequest;
import com.jewelry.jewelryshopbackend.dto.response.product.ProductResponse;
import com.jewelry.jewelryshopbackend.payload.ApiResponse;
import com.jewelry.jewelryshopbackend.payload.PageResponse;
import com.jewelry.jewelryshopbackend.service.product.ProductCommandService;
import com.jewelry.jewelryshopbackend.service.product.ProductQueryService;
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
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductController {

    private final ProductCommandService productCommandService;
    private final ProductQueryService productQueryService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody ProductCreateRequest request
    ) {
        ProductResponse response = productCommandService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Product created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateRequest request
    ) {
        ProductResponse response = productCommandService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Product updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productCommandService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Product deleted successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getById(@PathVariable Long id) {
        ProductResponse response = productQueryService.getByIdForAdmin(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProductResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean status
    ) {
        PageResponse<ProductResponse> response = productQueryService.getAllForAdmin(
                page, size, sortBy, sortDir, q, categoryId, status
        );
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
