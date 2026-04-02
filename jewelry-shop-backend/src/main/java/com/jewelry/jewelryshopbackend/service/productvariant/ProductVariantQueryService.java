package com.jewelry.jewelryshopbackend.service.productvariant;

import com.jewelry.jewelryshopbackend.dto.response.productvariant.ProductVariantResponse;
import com.jewelry.jewelryshopbackend.dto.response.productvariant.ProductVariantSummaryResponse;
import com.jewelry.jewelryshopbackend.payload.PageResponse;

import java.math.BigDecimal;

public interface ProductVariantQueryService {

    ProductVariantResponse getByIdForAdmin(Long id);

    PageResponse<ProductVariantResponse> getAllForAdmin(
            int page,
            int size,
            String sortBy,
            String sortDir,
            String q,
            Long productId,
            Boolean status,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Integer minStock,
            Integer maxStock
    );

    PageResponse<ProductVariantSummaryResponse> getAllActiveByProductSlug(
            String productSlug,
            int page,
            int size,
            String sortBy,
            String sortDir,
            String q,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Integer minStock
    );

    ProductVariantResponse getByIdForPublic(String productSlug, Long variantId);
}
