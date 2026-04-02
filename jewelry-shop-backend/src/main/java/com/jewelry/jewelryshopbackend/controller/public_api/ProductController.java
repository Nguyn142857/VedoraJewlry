package com.jewelry.jewelryshopbackend.controller.public_api;

import com.jewelry.jewelryshopbackend.dto.response.product.ProductResponse;
import com.jewelry.jewelryshopbackend.dto.response.product.ProductSummaryResponse;
import com.jewelry.jewelryshopbackend.payload.ApiResponse;
import com.jewelry.jewelryshopbackend.payload.PageResponse;
import com.jewelry.jewelryshopbackend.service.product.ProductQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductQueryService productQueryService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProductSummaryResponse>>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice
    ) {
        PageResponse<ProductSummaryResponse> response = productQueryService.getAllActive(
                page, size, sortBy, sortDir, q, categoryId, minPrice, maxPrice
        );
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ApiResponse<ProductResponse>> getBySlug(@PathVariable String slug) {
        ProductResponse response = productQueryService.getBySlugForPublic(slug);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
