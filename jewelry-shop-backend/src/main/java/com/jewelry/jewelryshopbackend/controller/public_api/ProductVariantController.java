package com.jewelry.jewelryshopbackend.controller.public_api;

import com.jewelry.jewelryshopbackend.dto.response.productvariant.ProductVariantResponse;
import com.jewelry.jewelryshopbackend.dto.response.productvariant.ProductVariantSummaryResponse;
import com.jewelry.jewelryshopbackend.payload.ApiResponse;
import com.jewelry.jewelryshopbackend.payload.PageResponse;
import com.jewelry.jewelryshopbackend.service.productvariant.ProductVariantQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/products/{productSlug}/variants")
@RequiredArgsConstructor
public class ProductVariantController {

    private final ProductVariantQueryService productVariantQueryService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProductVariantSummaryResponse>>> getAllActiveByProductSlug(
            @PathVariable String productSlug,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer minStock
    ) {
        PageResponse<ProductVariantSummaryResponse> response = productVariantQueryService.getAllActiveByProductSlug(
                productSlug, page, size, sortBy, sortDir, q, minPrice, maxPrice, minStock
        );
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{variantId}")
    public ResponseEntity<ApiResponse<ProductVariantResponse>> getByIdForPublic(
            @PathVariable String productSlug,
            @PathVariable Long variantId
    ) {
        ProductVariantResponse response = productVariantQueryService.getByIdForPublic(productSlug, variantId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
