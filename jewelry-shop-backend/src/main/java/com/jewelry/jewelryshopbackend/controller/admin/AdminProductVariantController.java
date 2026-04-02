package com.jewelry.jewelryshopbackend.controller.admin;

import com.jewelry.jewelryshopbackend.dto.request.productvariant.ProductVariantCreateRequest;
import com.jewelry.jewelryshopbackend.dto.request.productvariant.ProductVariantStatusUpdateRequest;
import com.jewelry.jewelryshopbackend.dto.request.productvariant.ProductVariantUpdateRequest;
import com.jewelry.jewelryshopbackend.dto.response.productvariant.ProductVariantResponse;
import com.jewelry.jewelryshopbackend.payload.ApiResponse;
import com.jewelry.jewelryshopbackend.payload.PageResponse;
import com.jewelry.jewelryshopbackend.service.productvariant.ProductVariantCommandService;
import com.jewelry.jewelryshopbackend.service.productvariant.ProductVariantQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/admin/product-variants")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductVariantController {

    private final ProductVariantCommandService productVariantCommandService;
    private final ProductVariantQueryService productVariantQueryService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductVariantResponse>> create(
            @Valid @RequestBody ProductVariantCreateRequest request
    ) {
        ProductVariantResponse response = productVariantCommandService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Product variant created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductVariantResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductVariantUpdateRequest request
    ) {
        ProductVariantResponse response = productVariantCommandService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Product variant updated successfully"));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ProductVariantResponse>> changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody ProductVariantStatusUpdateRequest request
    ) {
        ProductVariantResponse response = productVariantCommandService.changeStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Product variant status updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        productVariantCommandService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Product variant deleted successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductVariantResponse>> getById(@PathVariable Long id) {
        ProductVariantResponse response = productVariantQueryService.getByIdForAdmin(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProductVariantResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Boolean status,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer minStock,
            @RequestParam(required = false) Integer maxStock
    ) {
        PageResponse<ProductVariantResponse> response =
                productVariantQueryService.getAllForAdmin(
                        page, size, sortBy, sortDir, q, productId, status, minPrice, maxPrice, minStock, maxStock
                );
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
