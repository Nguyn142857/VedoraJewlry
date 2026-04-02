package com.jewelry.jewelryshopbackend.controller.public_api;

import com.jewelry.jewelryshopbackend.dto.response.productimage.ProductImageResponse;
import com.jewelry.jewelryshopbackend.dto.response.productimage.ProductImageSummaryResponse;
import com.jewelry.jewelryshopbackend.payload.ApiResponse;
import com.jewelry.jewelryshopbackend.payload.PageResponse;
import com.jewelry.jewelryshopbackend.service.productimage.ProductImageQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products/{productSlug}/images")
@RequiredArgsConstructor
public class ProductImageController {

    private final ProductImageQueryService productImageQueryService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProductImageSummaryResponse>>> getAllByProductSlug(
            @PathVariable String productSlug,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Boolean isMain
    ) {
        PageResponse<ProductImageSummaryResponse> response = productImageQueryService.getAllByProductSlugForPublic(
                productSlug, page, size, sortBy, sortDir, q, isMain
        );
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{imageId}")
    public ResponseEntity<ApiResponse<ProductImageResponse>> getById(
            @PathVariable String productSlug,
            @PathVariable Long imageId
    ) {
        ProductImageResponse response = productImageQueryService.getByIdForPublic(productSlug, imageId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
